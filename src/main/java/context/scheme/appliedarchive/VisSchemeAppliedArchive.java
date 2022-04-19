package context.scheme.appliedarchive;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import basic.HasNotes;
import basic.VfNotes;
import basic.lookup.VisframeUDT;
import basic.process.NonReproduceableProcessType;
import context.scheme.VisSchemeID;
import context.scheme.appliedarchive.mapping.GenericGraphMapping;
import context.scheme.appliedarchive.mapping.MetadataMapping;
import context.scheme.appliedarchive.mapping.RecordMapping;
import dependency.cfd.integrated.IntegratedCFDGraphEdge;
import dependency.cfd.integrated.IntegratedCFDGraphNode;
import dependency.dos.DOSEdge.DOSEdgeType;
import dependency.dos.integrated.IntegratedDOSGraphEdge;
import dependency.dos.integrated.IntegratedDOSGraphNode;
import dependency.vccl.VCCLEdge;
import dependency.vccl.VSCopy;
import dependency.vcd.VCDEdgeImpl;
import dependency.vcd.VCDNodeImpl;
import exception.VisframeException;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;
import function.target.CFGTarget;
import metadata.MetadataID;
import operation.OperationID;
import rdb.table.data.DataTableColumnName;
import rdb.table.data.DataTableSchemaID;

/**
 * immutable class that contains the full set of information regarding how a VisScheme is applied so that 
 * all operations and CompositionFunctionGroups and CompositionFunctions are ready to be reproduced in the host VisProjectDBContext;
 * 
 * specifically, the following steps are finished and results are included:
 * 1. select a target VisScheme
 * 		the full VCD graph of the VisScheme is automatically built;
 * 2. assign copy number to each VCDNode with respect to constraints
 * 3. create links between copies of incident VCDNodes with respect to constraints;
 * 4. integrated CFD and DOS graphs are built and trimmed
 * 5. a solution set is selected from the trimmed integrated DOS graph and MetadataMapping is created for each IntegratedDOSNode in the solution set;
 * 
 * besides,
 * 1. a unique integer ID for the VisSchemeApplierArchive is generated which is different from UIDs of all existing VisSchemeApplierArchive in the host VisProjectDBContext;
 * 2. a VfNotes is created for description of the purpose and details of the VisSchemeApplierArchive;
 * 
 * note that non-leaf VCCL nodes (VSCopy) whose core ShapeCFGs to be included in the core ShapeCFG set of the VisSchemeBasedVisInstance based on this VisSchemeApplierArchive are not selected,
 * which should be done when creating the specific VisSchemeBasedVisInstance;
 * thus different VisSchemeBasedVisInstances based on the same VisSchemeApplierArchive and VisSchemeApplierArchiveReproducingInstance can have different selected set of non-leaf VSCopies;
 * 
 * 
 * @author tanxu
 * 
 */
public class VisSchemeAppliedArchive implements HasNotes, VisframeUDT, NonReproduceableProcessType{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6900614508352970063L;
	
	///////////////////////////
	private final VfNotes notes;
	/**
	 * unique integer among all VisSchemeApplierArchives of all VisScheme in the same host VisProjectDBContext;
	 */
	private final int UID;
	/**
	 * VisSchemeID of applied VisScheme;
	 */
	private final VisSchemeID appliedVisSchemeID;
	
	/**
	 * the VCD underlying graph built based on the applied VisScheme;
	 */
	private final SimpleDirectedGraph<VCDNodeImpl, VCDEdgeImpl> visSchemeVCDGraph;
	
	/**
	 * contains the copy number assigned to each VCDNode/VSComponents and the copy links between the VSCopies 
	 * but NOT contain the set of non-leaf VSCopies to be included in VisInstance, which should be specified in each VisSchemeBasedVisInstance!!
	 */
	private final SimpleDirectedGraph<VSCopy, VCCLEdge> vcclGraph;
	
	/**
	 * trimmed integrated CFD graph based on the vcclGraph
	 */
	private final SimpleDirectedGraph<IntegratedCFDGraphNode, IntegratedCFDGraphEdge> trimmedIntegratedCFDGraph;
	
	/**
	 * trimmed integrated DOS graph based on the vcclGraph
	 */
	private final SimpleDirectedGraph<IntegratedDOSGraphNode, IntegratedDOSGraphEdge> trimmedIntegratedDOSGraph;

	/**
	 * selected solution set IntegratedDOSGraphNodes and MetadataMapping;
	 * note that the IntegratedDOSGraphNodes containing component Metadata of IntegratedDOSGraphNode in the solution set is not included ??
	 */
	private final Map<IntegratedDOSGraphNode, MetadataMapping> selectedSolutionSetNodeMappingMap;
	
	//////////////////////////////////////////supporting features
	/**
	 * map from the IntegratedDOSGraphNode to the MetadataID from host VisProjectDBContext mapped to the node;
	 * 
	 * the IntegratedDOSGraphNode must be 
	 * 1. either in the key set of {@link #selectedSolutionSetNodeMappingMap}
	 * 2. or a component IntegratedDOSGraphNode of a node in the key set of {@link #selectedSolutionSetNodeMappingMap}
	 */
	private final Map<IntegratedDOSGraphNode, MetadataID> integratedDOSGraphNodeMappedMetadataIDMap;
	
	/**
	 * map from the CompositionFunctionGroupID to the owner record data MetadataID in applied VisScheme;
	 */
	private final Map<CompositionFunctionGroupID, MetadataID> cfgIDOwnerRecordMetadataIDMapInVisScheme;
	
	/**
	 * map from the CompositionFunctionID to the set of depended record MetadataID in applied VisScheme;
	 */
	private final Map<CompositionFunctionID, Set<MetadataID>> cfIDDependedRecordMetadataIDSetMapInVisScheme;
	
	/**
	 * map from the CompositionFunctionGroupID to 
	 * the map 
	 * 		from target of the CompositionFunctionGroupID
	 * 		to the CompositionFunctionID to which the target of the CompositionFunctionGroupID is assigned;
	 * 
	 * note that the information contained in this map are all extracted from the applied VisScheme
	 */
	private final Map<CompositionFunctionGroupID, Map<CFGTarget<?>, CompositionFunctionID>> CFGIDTargetAssignedCFIDMapMapInVisScheme;
	
	/**
	 * constructor
	 * @param notes
	 * @param UID
	 * @param appliedVisSchemeID
	 * @param visSchemeVCDGraph
	 * @param vcclGraph
	 * @param trimmedIntegratedCFDGraph
	 * @param trimmedIntegratedDOSGraph
	 * @param selectedSolutionSetNodeMappingMap
	 * @param componentMetadataIDCompositeMetadataIDMap
	 * @param integratedDOSGraphNodeMappedMetadataIDMap
	 * @param cfgIDOwnerRecordMetadataIDMapInVisScheme
	 * @param cfIDDependedRecordMetadataIDSetMapInVisScheme
	 * @param CFGIDTargetAssignedCFIDMapMapInVisScheme
	 */
	public VisSchemeAppliedArchive(
			VfNotes notes,
			int UID,
			VisSchemeID appliedVisSchemeID,
			SimpleDirectedGraph<VCDNodeImpl, VCDEdgeImpl> visSchemeVCDGraph,
			SimpleDirectedGraph<VSCopy, VCCLEdge> vcclGraph,
			SimpleDirectedGraph<IntegratedCFDGraphNode, IntegratedCFDGraphEdge> trimmedIntegratedCFDGraph,
			SimpleDirectedGraph<IntegratedDOSGraphNode, IntegratedDOSGraphEdge> trimmedIntegratedDOSGraph,
			Map<IntegratedDOSGraphNode, MetadataMapping> selectedSolutionSetNodeMappingMap,
			//
//			Map<MetadataID, MetadataID> componentMetadataIDCompositeMetadataIDMap,
			Map<IntegratedDOSGraphNode, MetadataID> integratedDOSGraphNodeMappedMetadataIDMap,
			Map<CompositionFunctionGroupID, MetadataID> cfgIDOwnerRecordMetadataIDMapInVisScheme,
			Map<CompositionFunctionID, Set<MetadataID>> cfIDDependedRecordMetadataIDSetMapInVisScheme,
			Map<CompositionFunctionGroupID, Map<CFGTarget<?>, CompositionFunctionID>> CFGIDTargetAssignedCFIDMapMapInVisScheme
			){
		//validations
		if(notes==null)
			throw new IllegalArgumentException("given notes cannot be null!");
		if(appliedVisSchemeID==null)
			throw new IllegalArgumentException("given appliedVisSchemeID cannot be null!");
		if(visSchemeVCDGraph==null)
			throw new IllegalArgumentException("given visSchemeVCDGraph cannot be null!");
		if(vcclGraph==null)
			throw new IllegalArgumentException("given vcclGraph cannot be null!");
		if(trimmedIntegratedCFDGraph==null)
			throw new IllegalArgumentException("given trimmedIntegratedCFDGraph cannot be null!");
		if(trimmedIntegratedDOSGraph==null)
			throw new IllegalArgumentException("given trimmedIntegratedDOSGraph cannot be null!");
		if(selectedSolutionSetNodeMappingMap==null || selectedSolutionSetNodeMappingMap.isEmpty())
			throw new IllegalArgumentException("given selectedSolutionSetNodeMappingMap cannot be null or empty!");
		
		
		///////////////////////////
		this.notes = notes;
		this.UID = UID;
		this.appliedVisSchemeID = appliedVisSchemeID;
		this.visSchemeVCDGraph = visSchemeVCDGraph;
		this.vcclGraph = vcclGraph;
		this.trimmedIntegratedCFDGraph = trimmedIntegratedCFDGraph;
		this.trimmedIntegratedDOSGraph = trimmedIntegratedDOSGraph;
		this.selectedSolutionSetNodeMappingMap = selectedSolutionSetNodeMappingMap;
		//
		this.integratedDOSGraphNodeMappedMetadataIDMap = integratedDOSGraphNodeMappedMetadataIDMap;
		this.cfgIDOwnerRecordMetadataIDMapInVisScheme = cfgIDOwnerRecordMetadataIDMapInVisScheme;
		this.cfIDDependedRecordMetadataIDSetMapInVisScheme = cfIDDependedRecordMetadataIDSetMapInVisScheme;
		this.CFGIDTargetAssignedCFIDMapMapInVisScheme = CFGIDTargetAssignedCFIDMapMapInVisScheme;
	}

	@Override
	public VisSchemeAppliedArchiveID getID() {
		return new VisSchemeAppliedArchiveID(this.getUID());
	}
	
	@Override
	public VfNotes getNotes() {
		return this.notes;
	}
	
	/////////////////////////////////////
	//transient fields and utility methods to facilitate reproducing
	private transient Map<MetadataID, Map<Integer, IntegratedDOSGraphNode>> metadataIDCopyIndexIntegratedDOSGraphNodeMapMap;
	
	/**
	 * return the IntegratedDOSGraphNode corresponding to the given MetadataID and copy index;
	 * @param metadataID
	 * @param copyIndex
	 * @return
	 */
	public IntegratedDOSGraphNode lookupIntegratedDOSGraphNode(MetadataID metadataID, int copyIndex) {
		if(this.metadataIDCopyIndexIntegratedDOSGraphNodeMapMap==null) {
			this.metadataIDCopyIndexIntegratedDOSGraphNodeMapMap = new HashMap<>();
			this.trimmedIntegratedDOSGraph.vertexSet().forEach(v->{
				MetadataID mid = v.getMetadataID();
				int ci = v.getCopyIndex();
				
				if(!this.metadataIDCopyIndexIntegratedDOSGraphNodeMapMap.containsKey(mid))
					this.metadataIDCopyIndexIntegratedDOSGraphNodeMapMap.put(mid, new HashMap<>());
				this.metadataIDCopyIndexIntegratedDOSGraphNodeMapMap.get(mid).put(ci, v);
			});
		}
		
		return this.metadataIDCopyIndexIntegratedDOSGraphNodeMapMap.get(metadataID).get(copyIndex);
	}
	
	///////////////////////////
	/**
	 * map from IntegratedDOSGraphNode n1 containing a copy of component Metadata
	 * to the IntegratedDOSGraphNode containing the copy of the composite Metadata linked from n1
	 */
	private transient Map<IntegratedDOSGraphNode, IntegratedDOSGraphNode> componentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap;
	/**
	 * @return the componentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap
	 */
	public Map<IntegratedDOSGraphNode, IntegratedDOSGraphNode> getComponentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap() {
		if(this.componentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap == null) {
			this.componentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap = new HashMap<>();
			
			this.trimmedIntegratedDOSGraph.edgeSet().forEach(e->{
				if(e.getType().equals(DOSEdgeType.COMPOSITE_DATA_COMPONENT)) {
					IntegratedDOSGraphNode componentMetadataNode = this.trimmedIntegratedDOSGraph.getEdgeSource(e);
					IntegratedDOSGraphNode compositeMetadataNode = this.trimmedIntegratedDOSGraph.getEdgeTarget(e);
					this.componentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap.put(componentMetadataNode, compositeMetadataNode);
				}
			});
		}
		return componentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap;
	}

	/**
	 * check if the given IntegratedDOSGraphNode is mapped by a Mapping in the solution set
	 * @param node
	 * @return
	 */
	public boolean integratedDOSGraphNodeIsMapped(IntegratedDOSGraphNode node) {
		if(this.selectedSolutionSetNodeMappingMap.containsKey(node)) {
			return true;
		}else {
			//check if the node contains a component Metadata whose composite Metadata is contained by a IntegratedDOSGraphNode in the solution set;
			if(this.getComponentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap().containsKey(node)) {
				if(this.getSelectedSolutionSetNodeMappingMap().containsKey(this.getComponentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap().get(node))) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	
	/**
	 * lookup the source DataTableColumnName of the given target DataTableColumnName of the given ownerRecordMetadataID and copy index;
	 * 
	 * note that the IntegratedDOSGraphNode containing the given ownerRecordMetadataID and copyIndex must 
	 * 1. either be in the solution set
	 * 2. or a component of composite data of a IntegratedDOSGraphNode in the solution set;
	 * 
	 * @param ownerRecordMetadataID
	 * @param copyIndex
	 * @param targetName
	 * @return
	 */
	public DataTableColumnName lookupSourceDataTableColumnName(MetadataID ownerRecordMetadataID, int copyIndex, DataTableColumnName targetName) {
		IntegratedDOSGraphNode node = this.lookupIntegratedDOSGraphNode(ownerRecordMetadataID, copyIndex);
		
		if(!this.integratedDOSGraphNodeIsMapped(node))
			throw new VisframeException("IntegratedDOSGraphNode containing the given ownerRecordMetadataID and copyIndex is not mapped!");
		
		
		if(this.selectedSolutionSetNodeMappingMap.containsKey(node)) {//the node is in solution set as a RecordMapping
			RecordMapping mapping = (RecordMapping)this.selectedSolutionSetNodeMappingMap.get(node);
			return mapping.getTargetSourceColumnNameMap().get(targetName);
		}else {//the node contains a component record data of a composite data of a IntegratedDOSGraphNode in the solution set;
			GenericGraphMapping mapping = (GenericGraphMapping)this.selectedSolutionSetNodeMappingMap.get(
					this.getComponentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap().get(node)
					);
			
			if(mapping.getTargetNodeRecordMetadataID().equals(ownerRecordMetadataID)) {//ownerRecordMetadataID is the node record data
				return mapping.getTargetSourceNodeRecordDataColumNameMap().get(targetName);
			}else {//edge record data
				return mapping.getTargetSourceEdgeRecordDataColumNameMap().get(targetName);
			}
		}
	}
	
	/**
	 * 
	 * @param ownerRecordMetadataID
	 * @param copyIndex
	 * @return
	 */
	public DataTableSchemaID lookupSourceRecordDataTableSchemaID(MetadataID ownerRecordMetadataID, int copyIndex) {
		IntegratedDOSGraphNode node = this.lookupIntegratedDOSGraphNode(ownerRecordMetadataID, copyIndex);
		
		if(!this.integratedDOSGraphNodeIsMapped(node))
			throw new VisframeException("IntegratedDOSGraphNode containing the given ownerRecordMetadataID and copyIndex is not mapped!");
		
		if(this.selectedSolutionSetNodeMappingMap.containsKey(node)) {//the node is in solution set as a RecordMapping
			RecordMapping mapping = (RecordMapping)this.selectedSolutionSetNodeMappingMap.get(node);
			return mapping.getSourceRecordDataTableSchemaID();
			
		}else {//the node contains a component record data of a composite data of a IntegratedDOSGraphNode in the solution set;
			GenericGraphMapping mapping = (GenericGraphMapping)this.selectedSolutionSetNodeMappingMap.get(
					this.getComponentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap().get(node)
					);
			
			if(mapping.getTargetNodeRecordMetadataID().equals(ownerRecordMetadataID)) {//ownerRecordMetadataID is the node record data
				return mapping.getSourceNodeRecordDataTableSchemaID();
			}else {//edge record data
				return mapping.getSourceEdgeRecordDataTableSchemaID();
			}
		}
	}
	
	/**
	 * map from OperationID
	 * to 
	 * 		map from copy index of the OperationID
	 * 		to 
	 * 			map from input MetadataID of the OperationID
	 * 			to the IntegratedDOSGraphNode that contains the input MetadataID of the copy index corresponding to the copy index of the OperationID;
	 */
	private transient Map<OperationID, Map<Integer, Map<MetadataID, IntegratedDOSGraphNode>>> operationIDCopyIndexInputMetadataIDIntegratedDOSGraphNodeMapMapMap;
	/**
	 * 
	 * @return
	 */
	public Map<OperationID, Map<Integer, Map<MetadataID, IntegratedDOSGraphNode>>> getOperationIDCopyIndexInputMetadataIDIntegratedDOSGraphNodeMapMapMap(){
		if(this.operationIDCopyIndexInputMetadataIDIntegratedDOSGraphNodeMapMapMap==null) {
			this.operationIDCopyIndexInputMetadataIDIntegratedDOSGraphNodeMapMapMap = new HashMap<>();
			this.trimmedIntegratedDOSGraph.edgeSet().forEach(e->{
				if(e.getType().equals(DOSEdgeType.OPERATION)) {
					OperationID oid = e.getOperationID();
					int ci = e.getCopyIndex();
					
					if(!this.operationIDCopyIndexInputMetadataIDIntegratedDOSGraphNodeMapMapMap.containsKey(oid))
						this.operationIDCopyIndexInputMetadataIDIntegratedDOSGraphNodeMapMapMap.put(oid, new HashMap<>());
					if(!this.operationIDCopyIndexInputMetadataIDIntegratedDOSGraphNodeMapMapMap.get(oid).containsKey(ci))
						this.operationIDCopyIndexInputMetadataIDIntegratedDOSGraphNodeMapMapMap.get(oid).put(ci, new HashMap<>());
					
					IntegratedDOSGraphNode inputMetadataNode = this.trimmedIntegratedDOSGraph.getEdgeTarget(e);
					this.operationIDCopyIndexInputMetadataIDIntegratedDOSGraphNodeMapMapMap.get(oid).get(ci).put(
							inputMetadataNode.getMetadataID(), inputMetadataNode);
				}
			});
			
		}
		
		return this.operationIDCopyIndexInputMetadataIDIntegratedDOSGraphNodeMapMapMap;
	}
	/**
	 * lookup and return the copy index of the input MetadataID of the OperationID with the given copy index;
	 * 
	 * 
	 * @param operationID
	 * @param operationIDCopyIndex
	 * @param inputMetadtaID
	 * @return
	 */
	public int lookupCopyIndexOfOperationInputMetadata(OperationID operationID, int operationIDCopyIndex, MetadataID inputMetadtaID) {
		return this.getOperationIDCopyIndexInputMetadataIDIntegratedDOSGraphNodeMapMapMap().get(operationID).get(operationIDCopyIndex).get(inputMetadtaID).getCopyIndex();
	}
	
	/////////////////////////////////////
	private transient Map<MetadataID, VCDNodeImpl> metadataIDAssignedVCDNodeMap;
	
	/**
	 * @return the metadataIDAssignedVCDNodeMap
	 */
	public Map<MetadataID, VCDNodeImpl> getMetadataIDAssignedVCDNodeMap() {
		if(this.metadataIDAssignedVCDNodeMap == null) {
			this.metadataIDAssignedVCDNodeMap = new HashMap<>();
			this.visSchemeVCDGraph.vertexSet().forEach(v->{
				v.getAssignedMetadataIDSet().forEach(id->{
					this.metadataIDAssignedVCDNodeMap.put(id, v);
				});
			});
		}
		return metadataIDAssignedVCDNodeMap;
	}
	
	
	private transient Map<CompositionFunctionGroupID, VCDNodeImpl> cfgIDAssignedVCDNodeMap;

	
	/**
	 * @return the cfgIDAssignedVCDNodeMap
	 */
	public Map<CompositionFunctionGroupID, VCDNodeImpl> getCfgIDAssignedVCDNodeMap() {
		if(this.cfgIDAssignedVCDNodeMap == null) {
			this.cfgIDAssignedVCDNodeMap = new HashMap<>();
			this.visSchemeVCDGraph.vertexSet().forEach(v->{
				v.getAssignedCFGIDSet().forEach(id->{
					this.cfgIDAssignedVCDNodeMap.put(id, v);
				});
			});
		}
		return cfgIDAssignedVCDNodeMap;
	}

	/**
	 * map from a depending VSCopy 
	 * to the map
	 * 		from the depended VCDNodeImpl of the VCDNode of the depending VSCopy
	 * 		to the VSCopy of the depended VCDNodeImpl to which the depending VSCopy is linked
	 */
	private transient Map<VSCopy, Map<VCDNodeImpl, VSCopy>> vscopyDependedVCDNodeLinkedVSCopyMapMap;

	
	/**
	 * @return the vscopyDependedVCDNodeLinkedVSCopyMapMap
	 */
	public Map<VSCopy, Map<VCDNodeImpl, VSCopy>> getVscopyDependedVCDNodeLinkedVSCopyMapMap() {
		if(this.vscopyDependedVCDNodeLinkedVSCopyMapMap == null) {
			this.vscopyDependedVCDNodeLinkedVSCopyMapMap = new HashMap<>();
			
			this.vcclGraph.vertexSet().forEach(v->{
				this.vscopyDependedVCDNodeLinkedVSCopyMapMap.put(v, new HashMap<>());
				this.vscopyDependedVCDNodeLinkedVSCopyMapMap.get(v).putAll(v.getDependedVCDNodeLinkedCopyMap());
			});
		}
		return vscopyDependedVCDNodeLinkedVSCopyMapMap;
	}
	
	
	/**
	 * map from cfgID
	 * to the map
	 * 		from index of VSCopy c1 of the VCDNode to which the cfgID is assigned
	 * 		to the index of VSCopy c2 of the VCDNode to which the owner record metadata of the cfgid is assigned and linked by the c1
	 */
	private transient Map<CompositionFunctionGroupID, Map<Integer, Integer>> cfgIDCopyIndexOwnerRecordDataCopyIndexMapMap;
	
	
	/**
	 * build (if not yet) and return the cfgIDCopyIndexOwnerRecordDataCopyIndexMapMap
	 * 
	 * @return the cfgIDCopyIndexOwnerRecordDataCopyIndexMapMap
	 */
	public Map<CompositionFunctionGroupID, Map<Integer, Integer>> getCfgIDCopyIndexOwnerRecordDataCopyIndexMapMap() {
		if(this.cfgIDCopyIndexOwnerRecordDataCopyIndexMapMap == null) {
			this.cfgIDCopyIndexOwnerRecordDataCopyIndexMapMap = new HashMap<>();
			
			this.getCfgIDAssignedVCDNodeMap().forEach((cfgid, vcdnode)->{
				this.cfgIDCopyIndexOwnerRecordDataCopyIndexMapMap.put(cfgid, new HashMap<>());
				vcdnode.getVSCopyIndexMap().forEach((copyIndex, vscopy)->{
					MetadataID ownerRecordID = this.getCfgIDOwnerRecordMetadataIDMapInVisScheme().get(cfgid);
					
					VCDNodeImpl ownerRecordIDAssignedVCDNode = this.getMetadataIDAssignedVCDNodeMap().get(ownerRecordID);
					
					if(vcdnode.equals(ownerRecordIDAssignedVCDNode)) {
						//the cf is assigned to the same VCDNode with the owner record data;
						//thus the copy index should be the same;
						this.cfgIDCopyIndexOwnerRecordDataCopyIndexMapMap.get(cfgid).put(copyIndex, copyIndex);
					}else {
						VSCopy ownerRecordIDAssignedVCDNodeVSCopy = 
								this.getVscopyDependedVCDNodeLinkedVSCopyMapMap().get(vscopy).get(ownerRecordIDAssignedVCDNode);
						this.cfgIDCopyIndexOwnerRecordDataCopyIndexMapMap.get(cfgid).put(copyIndex, ownerRecordIDAssignedVCDNodeVSCopy.getIndex());
					}
					
				});
			});
		}
		return cfgIDCopyIndexOwnerRecordDataCopyIndexMapMap;
	}
	
	
	/**
	 * find out and return the index of the VSCopy c1 of the VCDNode to which the owner record Metadata of the given cfgID is assigned
	 * so that the VSCopy of the VCDNode to which the given cfgID is assigned and of the given copy index is linked to c1;
	 * 
	 * @param cfgID
	 * @param cfgIDCopyIndex
	 * @return
	 */
	public int lookupCopyIndexOfOwnerRecordMetadata(CompositionFunctionGroupID cfgID, int cfgIDCopyIndex) {
		return this.getCfgIDCopyIndexOwnerRecordDataCopyIndexMapMap().get(cfgID).get(cfgIDCopyIndex);
	}
	
	
	
	//////////////////////////////////////////////////////////////
	/**
	 * map from depending CFID cf1
	 * to the map
	 * 		from the depended CFID cf2
	 * 		to the map
	 * 			from the copy index of cf1
	 * 			to the copy index of cf2
	 * 
	 * built based on {@link #trimmedIntegratedCFDGraph}
	 */
	private transient Map<CompositionFunctionID, Map<CompositionFunctionID, Map<Integer, Integer>>> dependingCFIDDependedCFIDDependingCFCopyIndexDependedCFCopyIndexMapMapMap;
	
	public Map<CompositionFunctionID, Map<CompositionFunctionID, Map<Integer, Integer>>> getDependingCFIDDependedCFIDDependingCFCopyIndexDependedCFCopyIndexMapMapMap(){
		if(this.dependingCFIDDependedCFIDDependingCFCopyIndexDependedCFCopyIndexMapMapMap==null) {
			this.dependingCFIDDependedCFIDDependingCFCopyIndexDependedCFCopyIndexMapMapMap = new HashMap<>();
			
			this.trimmedIntegratedCFDGraph.edgeSet().forEach(e->{
				IntegratedCFDGraphNode dependingNode = this.trimmedIntegratedCFDGraph.getEdgeSource(e);
				IntegratedCFDGraphNode dependedNode = this.trimmedIntegratedCFDGraph.getEdgeTarget(e);
				
				CompositionFunctionID dependingCFID = dependingNode.getCfID();
				int dependingCFCopyIndex = dependingNode.getCopyIndex();
				
				CompositionFunctionID dependedCFID = dependedNode.getCfID();
				int dependedCFCopyIndex = dependedNode.getCopyIndex();
				
				if(!this.dependingCFIDDependedCFIDDependingCFCopyIndexDependedCFCopyIndexMapMapMap.containsKey(dependingCFID))
					this.dependingCFIDDependedCFIDDependingCFCopyIndexDependedCFCopyIndexMapMapMap.put(dependingCFID, new HashMap<>());
				
				if(!this.dependingCFIDDependedCFIDDependingCFCopyIndexDependedCFCopyIndexMapMapMap.get(dependingCFID).containsKey(dependedCFID))
					this.dependingCFIDDependedCFIDDependingCFCopyIndexDependedCFCopyIndexMapMapMap.get(dependingCFID).put(dependedCFID, new HashMap<>());
				
				this.dependingCFIDDependedCFIDDependingCFCopyIndexDependedCFCopyIndexMapMapMap.get(dependingCFID).get(dependedCFID).put(dependingCFCopyIndex, dependedCFCopyIndex);
			});
		}
		
		return this.dependingCFIDDependedCFIDDependingCFCopyIndexDependedCFCopyIndexMapMapMap;
		
	}
	
	/**
	 * find out and return the index of the VSCopy c1 of the VCDNode to which the dependedCFID is assigned 
	 * so that the VSCopy of the VCDNode to which the dependingCFID is assigned and of the given dependingCFCopyIndex is linked to c1;
	 * 
	 * @param dependingCFID
	 * @param dependingCFCopyIndex
	 * @param dependedCFID
	 * @return
	 */
	public int lookupDependedCFCopyIndex(CompositionFunctionID dependingCFID, int dependingCFCopyIndex, CompositionFunctionID dependedCFID) {
		return this.getDependingCFIDDependedCFIDDependingCFCopyIndexDependedCFCopyIndexMapMapMap().get(dependedCFID).get(dependedCFID).get(dependingCFCopyIndex);
	}
	
	
	
	///////////////////////////////////////////////
	/**
	 * map from depending CFID cf
	 * to the map
	 * 		from the depended record Metadata rmd
	 * 		to the map
	 * 			from the copy index of cf
	 * 			to the copy index of rmd
	 * 
	 * built based on {@link #cfIDDependedRecordMetadataIDSetMapInVisScheme}
	 */
	private transient Map<CompositionFunctionID, Map<MetadataID, Map<Integer, Integer>>> dependingCFIDDependedRecordDataIDDependingCFCopyIndexDependedRecordDataCopyIndexMapMapMap;
	
	public Map<CompositionFunctionID, Map<MetadataID, Map<Integer, Integer>>> getDependingCFIDDependedRecordDataIDDependingCFCopyIndexDependedRecordDataCopyIndexMapMapMap(){
		if(this.dependingCFIDDependedRecordDataIDDependingCFCopyIndexDependedRecordDataCopyIndexMapMapMap == null) {
			this.dependingCFIDDependedRecordDataIDDependingCFCopyIndexDependedRecordDataCopyIndexMapMapMap = new HashMap<>();
			
			///////////
			this.cfIDDependedRecordMetadataIDSetMapInVisScheme.forEach((cfid,dependedRecordDataIDSet)->{
				this.dependingCFIDDependedRecordDataIDDependingCFCopyIndexDependedRecordDataCopyIndexMapMapMap.put(cfid, new HashMap<>());
				
				VCDNodeImpl cfIDAssignedVCDNode = this.getCfgIDAssignedVCDNodeMap().get(cfid.getHostCompositionFunctionGroupID());
				
				dependedRecordDataIDSet.forEach(dependedRecordDataID->{
					this.dependingCFIDDependedRecordDataIDDependingCFCopyIndexDependedRecordDataCopyIndexMapMapMap.get(cfid).put(dependedRecordDataID, new HashMap<>());
					VCDNodeImpl dependedRecordDataIDAssignedVCDNode = this.getMetadataIDAssignedVCDNodeMap().get(dependedRecordDataID);
					
					//for each copy of cfIDAssignedVCDNode, find out the linked copy of dependedRecordDataIDAssignedVCDNode
					cfIDAssignedVCDNode.getVSCopyIndexMap().forEach((copyIndex, copy)->{
						if(cfIDAssignedVCDNode.equals(dependedRecordDataIDAssignedVCDNode)) {
							//the VCDNode to which the CF is assigned is the same one with the VCDNode to which the depended record data is assigned;
							//thus the copy index should be the same
							this.dependingCFIDDependedRecordDataIDDependingCFCopyIndexDependedRecordDataCopyIndexMapMapMap.get(cfid).get(dependedRecordDataID).put(copyIndex, copyIndex);
						}else {
							VSCopy dependedRecordDataAssignedVCDNodeVSCopy = 
									this.getVscopyDependedVCDNodeLinkedVSCopyMapMap().get(copy).get(dependedRecordDataIDAssignedVCDNode);
							
							this.dependingCFIDDependedRecordDataIDDependingCFCopyIndexDependedRecordDataCopyIndexMapMapMap.get(cfid).get(dependedRecordDataID).put(copyIndex, dependedRecordDataAssignedVCDNodeVSCopy.getIndex());
						}
						
					});
				});
				
			});
			
		}
		
		return this.dependingCFIDDependedRecordDataIDDependingCFCopyIndexDependedRecordDataCopyIndexMapMapMap;
	}
	
	/**
	 * find out and return the index of the VSCopy c1 of the VCDNode to which the dependedRecordMetadataID is assigned 
	 * so that the VSCopy of the VCDNode to which the dependingCFID is assigned and of the given dependingCFCopyIndex is linked to c1;
	 * 
	 * @param dependingCFID
	 * @param dependingCFCopyIndex
	 * @param dependedRecordMetadataID
	 * @return
	 */
	public int lookupDependedRecordMetadataCopyIndex(CompositionFunctionID dependingCFID, int dependingCFCopyIndex, MetadataID dependedRecordMetadataID) {
		return this.getDependingCFIDDependedRecordDataIDDependingCFCopyIndexDependedRecordDataCopyIndexMapMapMap().get(dependingCFID).get(dependedRecordMetadataID).get(dependingCFCopyIndex);
	}

	
	//////////
	/**
	 * 
	 */
	private transient Map<VCDNodeImpl, Integer> vcdNodeAssignedCopyNumberMap;

	/**
	 * @return the vcdNodeAssignedCopyNumberMap
	 */
	public Map<VCDNodeImpl, Integer> getVcdNodeAssignedCopyNumberMap() {
		if(this.vcdNodeAssignedCopyNumberMap==null) {
			this.vcdNodeAssignedCopyNumberMap = new HashMap<>();
			
			this.visSchemeVCDGraph.vertexSet().forEach(v->{
				this.vcdNodeAssignedCopyNumberMap.put(v, 0);
			});
			
			this.vcclGraph.vertexSet().forEach(v->{
				this.vcdNodeAssignedCopyNumberMap.put(
						v.getOwnerVCDNode(), 
						this.vcdNodeAssignedCopyNumberMap.get(v.getOwnerVCDNode())+1);
			});
			
		}
		return vcdNodeAssignedCopyNumberMap;
	}
	
	
	//////////////////
	private transient Map<VCDNodeImpl, Map<Integer,Map<VCDNodeImpl, Integer>>> dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap;
	/**
	 * 
	 * @return
	 */
	public Map<VCDNodeImpl, Map<Integer,Map<VCDNodeImpl, Integer>>> getDependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap(){
		if(this.dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap==null) {
			this.dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap = new HashMap<>();
			
			this.vcclGraph.edgeSet().forEach(e->{
				VSCopy dependingCopy = this.vcclGraph.getEdgeSource(e);
				VSCopy dependedCopy = this.vcclGraph.getEdgeTarget(e);
				
				if(!this.dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap.containsKey(dependingCopy.getOwnerVCDNode()))
					this.dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap.put(dependingCopy.getOwnerVCDNode(), new HashMap<>());
				
				if(!this.dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap.get(dependingCopy.getOwnerVCDNode()).containsKey(dependingCopy.getIndex()))
					this.dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap.get(dependingCopy.getOwnerVCDNode()).put(dependingCopy.getIndex(), new HashMap<>());
				
				this.dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap.get(dependingCopy.getOwnerVCDNode()).get(dependingCopy.getIndex())
				.put(dependedCopy.getOwnerVCDNode(), dependedCopy.getIndex());
			});
		}
		
		return this.dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap;
	}
	
	/////////////////////////////////////
	/**
	 * @return the uID
	 */
	public int getUID() {
		return UID;
	}

	/**
	 * @return the appliedVisSchemeID
	 */
	public VisSchemeID getAppliedVisSchemeID() {
		return appliedVisSchemeID;
	}

	/**
	 * @return the visSchemeVCDGraph
	 */
	public SimpleDirectedGraph<VCDNodeImpl, VCDEdgeImpl> getVisSchemeVCDGraph() {
		return visSchemeVCDGraph;
	}

	/**
	 * @return the vcclGraph
	 */
	public SimpleDirectedGraph<VSCopy, VCCLEdge> getVcclGraph() {
		return vcclGraph;
	}

	/**
	 * @return the trimmedIntegratedCFDGraph
	 */
	public SimpleDirectedGraph<IntegratedCFDGraphNode, IntegratedCFDGraphEdge> getTrimmedIntegratedCFDGraph() {
		return trimmedIntegratedCFDGraph;
	}

	/**
	 * @return the trimmedIntegratedDOSGraph
	 */
	public SimpleDirectedGraph<IntegratedDOSGraphNode, IntegratedDOSGraphEdge> getTrimmedIntegratedDOSGraph() {
		return trimmedIntegratedDOSGraph;
	}

	/**
	 * @return the selectedSolutionSetNodeMappingMap
	 */
	public Map<IntegratedDOSGraphNode, MetadataMapping> getSelectedSolutionSetNodeMappingMap() {
		return selectedSolutionSetNodeMappingMap;
	}

	/**
	 * @return the integratedDOSGraphNodeMappedMetadataIDMap
	 */
	public Map<IntegratedDOSGraphNode, MetadataID> getIntegratedDOSGraphNodeMappedMetadataIDMap() {
		return integratedDOSGraphNodeMappedMetadataIDMap;
	}

	/**
	 * @return the cfgIDOwnerRecordMetadataIDMapInVisScheme
	 */
	public Map<CompositionFunctionGroupID, MetadataID> getCfgIDOwnerRecordMetadataIDMapInVisScheme() {
		return cfgIDOwnerRecordMetadataIDMapInVisScheme;
	}

	/**
	 * @return the cfIDDependedRecordMetadataIDSetMapInVisScheme
	 */
	public Map<CompositionFunctionID, Set<MetadataID>> getCfIDDependedRecordMetadataIDSetMapInVisScheme() {
		return cfIDDependedRecordMetadataIDSetMapInVisScheme;
	}

	/**
	 * @return the cfgIDTargetAssignedCFIDMapMapInVisScheme
	 */
	public Map<CompositionFunctionGroupID, Map<CFGTarget<?>, CompositionFunctionID>> getCFGIDTargetAssignedCFIDMapMapInVisScheme() {
		return CFGIDTargetAssignedCFIDMapMapInVisScheme;
	}

	/**
	 * @return the metadataIDCopyIndexIntegratedDOSGraphNodeMapMap
	 */
	public Map<MetadataID, Map<Integer, IntegratedDOSGraphNode>> getMetadataIDCopyIndexIntegratedDOSGraphNodeMapMap() {
		return metadataIDCopyIndexIntegratedDOSGraphNodeMapMap;
	}

	
	/////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + UID;
		result = prime * result + ((appliedVisSchemeID == null) ? 0 : appliedVisSchemeID.hashCode());
		result = prime * result + ((cfIDDependedRecordMetadataIDSetMapInVisScheme == null) ? 0
				: cfIDDependedRecordMetadataIDSetMapInVisScheme.hashCode());
		result = prime * result + ((cfgIDOwnerRecordMetadataIDMapInVisScheme == null) ? 0
				: cfgIDOwnerRecordMetadataIDMapInVisScheme.hashCode());
		result = prime * result + ((CFGIDTargetAssignedCFIDMapMapInVisScheme == null) ? 0
				: CFGIDTargetAssignedCFIDMapMapInVisScheme.hashCode());
		result = prime * result + ((integratedDOSGraphNodeMappedMetadataIDMap == null) ? 0
				: integratedDOSGraphNodeMappedMetadataIDMap.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result
				+ ((selectedSolutionSetNodeMappingMap == null) ? 0 : selectedSolutionSetNodeMappingMap.hashCode());
		result = prime * result + ((trimmedIntegratedCFDGraph == null) ? 0 : trimmedIntegratedCFDGraph.hashCode());
		result = prime * result + ((trimmedIntegratedDOSGraph == null) ? 0 : trimmedIntegratedDOSGraph.hashCode());
		result = prime * result + ((vcclGraph == null) ? 0 : vcclGraph.hashCode());
		result = prime * result + ((visSchemeVCDGraph == null) ? 0 : visSchemeVCDGraph.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VisSchemeAppliedArchive))
			return false;
		VisSchemeAppliedArchive other = (VisSchemeAppliedArchive) obj;
		if (UID != other.UID)
			return false;
		if (appliedVisSchemeID == null) {
			if (other.appliedVisSchemeID != null)
				return false;
		} else if (!appliedVisSchemeID.equals(other.appliedVisSchemeID))
			return false;
		if (cfIDDependedRecordMetadataIDSetMapInVisScheme == null) {
			if (other.cfIDDependedRecordMetadataIDSetMapInVisScheme != null)
				return false;
		} else if (!cfIDDependedRecordMetadataIDSetMapInVisScheme
				.equals(other.cfIDDependedRecordMetadataIDSetMapInVisScheme))
			return false;
		if (cfgIDOwnerRecordMetadataIDMapInVisScheme == null) {
			if (other.cfgIDOwnerRecordMetadataIDMapInVisScheme != null)
				return false;
		} else if (!cfgIDOwnerRecordMetadataIDMapInVisScheme.equals(other.cfgIDOwnerRecordMetadataIDMapInVisScheme))
			return false;
		if (CFGIDTargetAssignedCFIDMapMapInVisScheme == null) {
			if (other.CFGIDTargetAssignedCFIDMapMapInVisScheme != null)
				return false;
		} else if (!CFGIDTargetAssignedCFIDMapMapInVisScheme.equals(other.CFGIDTargetAssignedCFIDMapMapInVisScheme))
			return false;
		if (integratedDOSGraphNodeMappedMetadataIDMap == null) {
			if (other.integratedDOSGraphNodeMappedMetadataIDMap != null)
				return false;
		} else if (!integratedDOSGraphNodeMappedMetadataIDMap.equals(other.integratedDOSGraphNodeMappedMetadataIDMap))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		if (selectedSolutionSetNodeMappingMap == null) {
			if (other.selectedSolutionSetNodeMappingMap != null)
				return false;
		} else if (!selectedSolutionSetNodeMappingMap.equals(other.selectedSolutionSetNodeMappingMap))
			return false;
		if (trimmedIntegratedCFDGraph == null) {
			if (other.trimmedIntegratedCFDGraph != null)
				return false;
		} else if (!trimmedIntegratedCFDGraph.equals(other.trimmedIntegratedCFDGraph))
			return false;
		if (trimmedIntegratedDOSGraph == null) {
			if (other.trimmedIntegratedDOSGraph != null)
				return false;
		} else if (!trimmedIntegratedDOSGraph.equals(other.trimmedIntegratedDOSGraph))
			return false;
		if (vcclGraph == null) {
			if (other.vcclGraph != null)
				return false;
		} else if (!vcclGraph.equals(other.vcclGraph))
			return false;
		if (visSchemeVCDGraph == null) {
			if (other.visSchemeVCDGraph != null)
				return false;
		} else if (!visSchemeVCDGraph.equals(other.visSchemeVCDGraph))
			return false;
		return true;
	}

}
