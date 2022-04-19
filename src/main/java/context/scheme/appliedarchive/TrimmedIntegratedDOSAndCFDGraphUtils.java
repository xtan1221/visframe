package context.scheme.appliedarchive;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import com.google.common.base.Objects;

import context.scheme.VisScheme;
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
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroup;
import function.group.CompositionFunctionGroupID;
import function.target.CFGTarget;
import metadata.DataType;
import metadata.MetadataID;
import metadata.graph.GraphDataMetadata;
import operation.Operation;
import operation.OperationID;
import operation.vftree.VfTreeTrimmingOperationBase;
import rdb.table.data.DataTableColumnName;
import utils.Pair;

/**
 * utility class that provide a set of methods to process ...
 * 
 * @author tanxu
 *
 */
public final class TrimmedIntegratedDOSAndCFDGraphUtils {
	private final VisScheme visScheme;
	private final SimpleDirectedGraph<VCDNodeImpl, VCDEdgeImpl> visSchemeVCDGraph;
	private final SimpleDirectedGraph<VSCopy, VCCLEdge> vcclGraph;
	
	private final SimpleDirectedGraph<IntegratedCFDGraphNode, IntegratedCFDGraphEdge> trimmedIntegratedCFDGraph;
	private final SimpleDirectedGraph<IntegratedDOSGraphNode, IntegratedDOSGraphEdge> trimmedIntegratedDOSGraph;
	////////////////////////////////////
	/**
	 * 
	 * @param visScheme
	 * @param visSchemeVCDGraph
	 * @param vcclGraph
	 * @param trimmedIntegratedCFDGraph
	 * @param trimmedIntegratedDOSGraph
	 */
	public TrimmedIntegratedDOSAndCFDGraphUtils(
			VisScheme visScheme,
			SimpleDirectedGraph<VCDNodeImpl, VCDEdgeImpl> visSchemeVCDGraph,
			SimpleDirectedGraph<VSCopy, VCCLEdge> vcclGraph,
			SimpleDirectedGraph<IntegratedCFDGraphNode, IntegratedCFDGraphEdge> trimmedIntegratedCFDGraph,
			SimpleDirectedGraph<IntegratedDOSGraphNode, IntegratedDOSGraphEdge> trimmedIntegratedDOSGraph
			){
		
		this.visScheme = visScheme;
		this.visSchemeVCDGraph = visSchemeVCDGraph;
		this.vcclGraph = vcclGraph;
		this.trimmedIntegratedCFDGraph = trimmedIntegratedCFDGraph;
		this.trimmedIntegratedDOSGraph = trimmedIntegratedDOSGraph;
	}


	/**
	 * @return the visScheme
	 */
	public VisScheme getVisScheme() {
		return visScheme;
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
	
	
	/////////////////////////////////
	/**
	 * map from the CompositionFunctionGroupID to the owner record data MetadataID in applied VisScheme;
	 */
	private Map<CompositionFunctionGroupID, MetadataID> cfgIDOwnerRecordMetadataIDMapInVisScheme;
	/**
	 * @return the cfgIDOwnerRecordMetadataIDMapInVisScheme
	 */
	public Map<CompositionFunctionGroupID, MetadataID> getCfgIDOwnerRecordMetadataIDMapInVisScheme() {
		if(this.cfgIDOwnerRecordMetadataIDMapInVisScheme==null) {
			this.cfgIDOwnerRecordMetadataIDMapInVisScheme = new HashMap<>();
			
			this.visScheme.getCompositionFunctionGroupLookup().getMap().forEach((id,cfg)->{
				this.cfgIDOwnerRecordMetadataIDMapInVisScheme.put(id, cfg.getOwnerRecordDataMetadataID());
			});
		}
		
		return cfgIDOwnerRecordMetadataIDMapInVisScheme;
	}

	
	/**
	 * map from the CompositionFunctionID to the set of depended record MetadataID in applied VisScheme;
	 */
	private Map<CompositionFunctionID, Set<MetadataID>> cfIDDependedRecordMetadataIDSetMapInVisScheme;
	/**
	 * @return the cfIDDependedRecordMetadataIDSetMapInVisScheme
	 */
	public Map<CompositionFunctionID, Set<MetadataID>> getCfIDDependedRecordMetadataIDSetMapInVisScheme() {
		if(cfIDDependedRecordMetadataIDSetMapInVisScheme==null) {
			this.cfIDDependedRecordMetadataIDSetMapInVisScheme = new HashMap<>();
			
			this.visScheme.getCompositionFunctionLookup().getMap().forEach((id,cf)->{
				try {
					this.cfIDDependedRecordMetadataIDSetMapInVisScheme.put(
							id, cf.getDependedRecordMetadataIDInputColumnNameSetMap(this.visScheme).keySet());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}
		
		return cfIDDependedRecordMetadataIDSetMapInVisScheme;
	}
	
	
	/**
	 * map from the CompositionFunctionGroupID to 
	 * the map 
	 * 		from target of the CompositionFunctionGroupID
	 * 		to the CompositionFunctionID to which the target of the CompositionFunctionGroupID is assigned;
	 * 
	 * note that the information contained in this map are all extracted from the applied VisScheme
	 */
	private Map<CompositionFunctionGroupID, Map<CFGTarget<?>, CompositionFunctionID>> cfgIDTargetAssignedCFIDMapMapInVisScheme;
	
	/**
	 * @return the cfgIDTargetAssignedCFIDMapMapInVisScheme
	 */
	public Map<CompositionFunctionGroupID, Map<CFGTarget<?>, CompositionFunctionID>> getCfgIDTargetAssignedCFIDMapMapInVisScheme() {
		if(this.cfgIDTargetAssignedCFIDMapMapInVisScheme == null) {
			Map<CompositionFunctionGroupID, Map<CFGTarget<?>, CompositionFunctionID>> ret = new HashMap<>();
			
			this.visScheme.getCompositionFunctionLookup().getMap().forEach((cfid,cf)->{
				CompositionFunctionGroup hostCFG = this.visScheme.getCompositionFunctionGroupLookup().lookup(cf.getHostCompositionFunctionGroupID());
				if(!ret.containsKey(hostCFG.getID()))
					ret.put(hostCFG.getID(), new HashMap<>());
				cf.getAssignedTargetNameSet().forEach(n->{
					ret.get(cf.getHostCompositionFunctionGroupID()).put(
							hostCFG.getTargetNameMap().get(n), 
							cfid);
				});
			});
		}
		return cfgIDTargetAssignedCFIDMapMapInVisScheme;
	}


	/////////////////////////////////////
	//transient fields and utility methods to facilitate reproducing
	private transient Map<MetadataID, Map<Integer, IntegratedDOSGraphNode>> metadataIDCopyIndexIntegratedDOSGraphNodeMapMap;

	/**
	 * @return the metadataIDCopyIndexIntegratedDOSGraphNodeMapMap
	 */
	public Map<MetadataID, Map<Integer, IntegratedDOSGraphNode>> getMetadataIDCopyIndexIntegratedDOSGraphNodeMapMap() {
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
		return metadataIDCopyIndexIntegratedDOSGraphNodeMapMap;
	}
	
	/**
	 * return the IntegratedDOSGraphNode corresponding to the given MetadataID and copy index;
	 * @param metadataID
	 * @param copyIndex
	 * @return
	 */
	public IntegratedDOSGraphNode lookupIntegratedDOSGraphNode(MetadataID metadataID, int copyIndex) {
		
		return this.getMetadataIDCopyIndexIntegratedDOSGraphNodeMapMap().get(metadataID).get(copyIndex);
	}
	
	//////////////////////////

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
	 * 		to the index of VSCopy c2 of the VCDNode to which the owner record metadata of the cfgid is assigned and linked by the c1;
	 * 
	 * note that each CFG is assigned to the first VSComponent/VCDNode on whose CFD graph one or more CF of the CFG is present;
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
			
			this.getCfgIDAssignedVCDNodeMap().forEach((cfgid, cfgAssignedVCDNode)->{
				this.cfgIDCopyIndexOwnerRecordDataCopyIndexMapMap.put(cfgid, new HashMap<>());
				cfgAssignedVCDNode.getVSCopyIndexMap().forEach((copyIndex, vscopy)->{
					MetadataID ownerRecordID = this.getCfgIDOwnerRecordMetadataIDMapInVisScheme().get(cfgid);
					
					VCDNodeImpl ownerRecordIDAssignedVCDNode = this.getMetadataIDAssignedVCDNodeMap().get(ownerRecordID);
					
					if(Objects.equal(cfgAssignedVCDNode, ownerRecordIDAssignedVCDNode)) {
						//cfg and owner record data are assigned to the same VCDNode, thus the copy index are the same
						this.cfgIDCopyIndexOwnerRecordDataCopyIndexMapMap.get(cfgid).put(copyIndex, copyIndex);
						
					}else {
						VSCopy ownerRecordIDAssignedVCDNodeVSCopy = this.getVscopyDependedVCDNodeLinkedVSCopyMapMap().get(vscopy).get(ownerRecordIDAssignedVCDNode);
						
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
			this.getCfIDDependedRecordMetadataIDSetMapInVisScheme().forEach((cfid,dependedRecordDataIDSet)->{
				this.dependingCFIDDependedRecordDataIDDependingCFCopyIndexDependedRecordDataCopyIndexMapMapMap.put(cfid, new HashMap<>());
				
				VCDNodeImpl cfIDAssignedVCDNode = this.getCfgIDAssignedVCDNodeMap().get(cfid.getHostCompositionFunctionGroupID());
				
				dependedRecordDataIDSet.forEach(dependedRecordDataID->{
					this.dependingCFIDDependedRecordDataIDDependingCFCopyIndexDependedRecordDataCopyIndexMapMapMap.get(cfid).put(dependedRecordDataID, new HashMap<>());
					VCDNodeImpl dependedRecordDataIDAssignedVCDNode = this.getMetadataIDAssignedVCDNodeMap().get(dependedRecordDataID);
					
					//
					if(Objects.equal(cfIDAssignedVCDNode, dependedRecordDataIDAssignedVCDNode)) {
						//cf and depended record data are assigned to the same VCDNode, thus the copy index of depending CF and depended record data should be the same for all copies of the shared owner vcdnode;
						cfIDAssignedVCDNode.getVSCopyIndexMap().keySet().forEach(copyIndex->{
							this.dependingCFIDDependedRecordDataIDDependingCFCopyIndexDependedRecordDataCopyIndexMapMapMap
							.get(cfid).get(dependedRecordDataID).put(copyIndex, copyIndex);
						});
					}else {
						//for each copy of cfIDAssignedVCDNode, find out the linked copy of dependedRecordDataIDAssignedVCDNode
						cfIDAssignedVCDNode.getVSCopyIndexMap().forEach((copyIndex, copy)->{
							VSCopy dependedRecordDataAssignedVCDNodeVSCopy = 
									this.getVscopyDependedVCDNodeLinkedVSCopyMapMap().get(copy).get(dependedRecordDataIDAssignedVCDNode);
							
							this.dependingCFIDDependedRecordDataIDDependingCFCopyIndexDependedRecordDataCopyIndexMapMapMap
							.get(cfid).get(dependedRecordDataID).put(copyIndex, dependedRecordDataAssignedVCDNodeVSCopy.getIndex());
						});
					}
					
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

	

	
	
//	///////////////////////////////////////////////////TODO TODO
	///////////////////////////
	/**
	 * map from IntegratedDOSGraphNode on the {@link #trimmedIntegratedDOSGraph} containing a generic graph type Metadata
	 * to the pair of 
	 * 		IntegratedDOSGraphNode containing the node record data of the generic graph data
	 * 		IntegratedDOSGraphNode containing the edge record data of the generic graph data
	 * 
	 * note that it is possible that one of the two record data is not on the {@link #trimmedIntegratedDOSGraph}, thus null;
	 * but it is not possible that both of them are null;
	 */
	private Map<IntegratedDOSGraphNode, Pair<IntegratedDOSGraphNode,IntegratedDOSGraphNode>> genericGraphIntegratedDOSGraphNodeNodeAndEdgeIntegratedDOSGraphNodePairMap;
	
	
	/**
	 * build (if not yet) and return the {@link #genericGraphIntegratedDOSGraphNodeNodeAndEdgeIntegratedDOSGraphNodePairMap}
	 * @return
	 */
	public Map<IntegratedDOSGraphNode, Pair<IntegratedDOSGraphNode,IntegratedDOSGraphNode>> getGenericGraphIntegratedDOSGraphNodeNodeAndEdgeIntegratedDOSGraphNodePairMap(){
		if(this.genericGraphIntegratedDOSGraphNodeNodeAndEdgeIntegratedDOSGraphNodePairMap == null) {
			this.genericGraphIntegratedDOSGraphNodeNodeAndEdgeIntegratedDOSGraphNodePairMap = new HashMap<>();
			
			Map<IntegratedDOSGraphNode, IntegratedDOSGraphNode> genericGraphNodeRecordDataNodeMap = new HashMap<>();
			Map<IntegratedDOSGraphNode, IntegratedDOSGraphNode> genericGraphEdgeRecordDataNodeMap = new HashMap<>();
			
			this.trimmedIntegratedDOSGraph.edgeSet().forEach(e->{
				if(e.getType().equals(DOSEdgeType.COMPOSITE_DATA_COMPONENT)) {
					IntegratedDOSGraphNode composite = e.getDependedNode();
					IntegratedDOSGraphNode component = e.getDependingNode();
					
					GraphDataMetadata graph = (GraphDataMetadata)this.visScheme.getMetadataLookup().lookup(composite.getMetadataID());
					
					if(graph.getNodeRecordMetadataID().equals(component.getMetadataID())){
						genericGraphNodeRecordDataNodeMap.put(composite, component);
					}else {
						genericGraphEdgeRecordDataNodeMap.put(composite, component);
					}
					
					
				}
			});
			
			Set<IntegratedDOSGraphNode> genericGraphSet = new HashSet<>();
			genericGraphSet.addAll(genericGraphNodeRecordDataNodeMap.keySet());
			genericGraphSet.addAll(genericGraphEdgeRecordDataNodeMap.keySet());
			
			genericGraphSet.forEach(graph->{
				this.genericGraphIntegratedDOSGraphNodeNodeAndEdgeIntegratedDOSGraphNodePairMap.put(
						graph, 
						new Pair<>(
								genericGraphNodeRecordDataNodeMap.containsKey(graph)?genericGraphNodeRecordDataNodeMap.get(graph):null,
								genericGraphEdgeRecordDataNodeMap.containsKey(graph)?genericGraphEdgeRecordDataNodeMap.get(graph):null
								));
			});
			
		}
		
		return this.genericGraphIntegratedDOSGraphNodeNodeAndEdgeIntegratedDOSGraphNodePairMap;
	}
	
	
	/**
	 * map from the IntegratedDOSGraphNode containing a component Metadata on the {@link #trimmedIntegratedDOSGraph}
	 * to the IntegratedDOSGraphNode containing the owner composite Metadata on the {@link #trimmedIntegratedDOSGraph}
	 */
	private Map<IntegratedDOSGraphNode, IntegratedDOSGraphNode> componentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap;

	/**
	 * build (if not yet) and return the {@link #componentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap};
	 * @return
	 */
	public Map<IntegratedDOSGraphNode, IntegratedDOSGraphNode> getComponentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap(){
		if(this.componentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap==null) {
			this.componentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap = new HashMap<>();
			
			this.getGenericGraphIntegratedDOSGraphNodeNodeAndEdgeIntegratedDOSGraphNodePairMap().forEach((composite,pair)->{
				if(pair.getFirst()!=null)
					this.componentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap.put(pair.getFirst(), composite);
				
				if(pair.getSecond()!=null)
					this.componentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap.put(pair.getSecond(), composite);
			});
		}
		
		return this.componentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap;
	}

	
	
	/**
	 * map from the IntegratedDOSGraphNode on the {@link #trimmedIntegratedDOSGraph} containing a record type Metadata
	 * to the set of DataTableColumnName of the record data that are 
	 * 1. used as input column of one or more Operations represented by an IntegratedDOSGraphEdge incident to the IntegratedDOSGraphNode on the {@link #trimmedIntegratedDOSGraph}
	 * 		{@link Operation#getInputRecordMetadataIDInputColumnNameSetMap()}
	 * 
	 * 2. used as depended column by one or more CompositionFunction on IntegratedCFDGraphNode on the {@link #trimmedIntegratedCFDGraph};
	 * 		{@link CompositionFunction#getDependedRecordMetadataIDInputColumnNameSetMap(context.VisframeContext)}
	 */
	private Map<IntegratedDOSGraphNode, Set<DataTableColumnName>> recordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap;
	
	
	/**
	 * build (if not yet) and return {@link #recordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap}
	 * @return
	 */
	public Map<IntegratedDOSGraphNode, Set<DataTableColumnName>> getRecordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap(){
		if(this.recordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap==null) {
			this.recordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap = new HashMap<>();
			
			Set<IntegratedDOSGraphNode> recordDataNodeSet = new HashSet<>();
			//find out all IntegratedDOSGraphNode containing a record data
			this.trimmedIntegratedDOSGraph.vertexSet().forEach(integratedDOSNode->{
				if(integratedDOSNode.getMetadataID().getDataType().equals(DataType.RECORD))
					recordDataNodeSet.add(integratedDOSNode);
			});
			
			//for each record data find out all operations on the dos graph with the 
			recordDataNodeSet.forEach(recordNode->{
				//use the record data as input
				this.trimmedIntegratedDOSGraph.incomingEdgesOf(recordNode).forEach(e->{
					if(e.getType().equals(DOSEdgeType.OPERATION)) {
						Operation operation = this.visScheme.getOperationLookup().lookup(e.getOperationID());
						
						Set<DataTableColumnName> inputDataTableColNamSet = operation.getInputRecordMetadataIDInputColumnNameSetMap().get(recordNode.getMetadataID());
						
						if(!this.recordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap.containsKey(recordNode))
							this.recordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap.put(recordNode, new HashSet<>());
						
						this.recordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap.get(recordNode).addAll(inputDataTableColNamSet);
					}
				});
				
				//if the record data is a component of a composite data, check all operations use the composite data as input
				if(this.getComponentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap().containsKey(recordNode)) {
					IntegratedDOSGraphNode compositeDataNode = 
							this.getComponentMetadataIntegratedDOSGraphNodeCompositeMetadataIntegratedDOSGraphNodeMap().get(recordNode);
					
					this.trimmedIntegratedDOSGraph.incomingEdgesOf(compositeDataNode).forEach(e->{
						if(e.getType().equals(DOSEdgeType.OPERATION)) {
							Operation operation = this.visScheme.getOperationLookup().lookup(e.getOperationID());
							
							Set<DataTableColumnName> inputDataTableColNamSet = operation.getInputRecordMetadataIDInputColumnNameSetMap().get(recordNode.getMetadataID());
							
							if(!this.recordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap.containsKey(recordNode))
								this.recordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap.put(recordNode, new HashSet<>());
							
							this.recordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap.get(recordNode).addAll(inputDataTableColNamSet);
						}
					});
				}
			});
			
			
			//for each record data, find out all composition functions depends on the record data
			this.trimmedIntegratedCFDGraph.vertexSet().forEach(integratedCFNode->{
				CompositionFunction cf = this.visScheme.getCompositionFunctionLookup().lookup(integratedCFNode.getCfID());
				
				try {
					Map<MetadataID, Set<DataTableColumnName>> dependedRecordMetadataIDInputColumnNameSetMap = 
							cf.getDependedRecordMetadataIDInputColumnNameSetMap(this.visScheme);
					
					dependedRecordMetadataIDInputColumnNameSetMap.forEach((dependedRecordID, inputColNameSet)->{
						//first find out the IntegratedDOSGraphNode that contains the dependedRecordID and depended by the integratedCFNode
						IntegratedDOSGraphNode recordDataNode = 
								this.lookupIntegratedDOSGraphNode(
									dependedRecordID, 
									this.lookupDependedRecordMetadataCopyIndex(cf.getID(), integratedCFNode.getCopyIndex(), dependedRecordID));
						
						//
						if(!this.recordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap.containsKey(recordDataNode))
							this.recordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap.put(recordDataNode, new HashSet<>());
						
						this.recordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap.get(recordDataNode).addAll(inputColNameSet);
					});
					
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			});
			
			
		}
		
		return this.recordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap;
	}
	
	///////////////////////////
	/**
	 * check the VfTreeDataMetadata contained by the given IntegratedDOSGraphNode
	 * 1. if it is not used by any Operation as input on the {@link #trimmedIntegratedDOSGraph}, return false;
	 * 2. if it is used by a set of Operations as input on the {@link #trimmedIntegratedDOSGraph},
	 * 		1. if at least one of such Operations is the type that requires the input be VfTree data such as {@link VfTreeTrimmingOperationBase} types
	 * 			return true;
	 * 		2. otherwise (all such Operations are the type that only requires the input be generic graph)
	 * 			return false;
	 * 
	 * facilitate to build MetadataMapping for a Vftree data containing IntegratedDOSGraphNode;
	 * 		1. if true, the source metadata from host VisProjectDBContext must be of VfTree type as well;
	 * 		2. if false, the source metadata from host VisProjectDBContext can be of any generic graph;
	 * 
	 * @param node
	 * @return
	 */
	public boolean vfTreeContainingIntegratedDOSGraphNodeUsedByAtLeastOneOperationsRequiringVfTreeAsInput(IntegratedDOSGraphNode vftreeMetadataContainingNode){
		if(!vftreeMetadataContainingNode.getMetadataID().getDataType().equals(DataType.vfTREE))
			throw new VisframeException("given vftreeMetadataContainingNode does not contain a VfTree data!");
		
//		int incomingEdgeOfOperationType = 0;
		for(IntegratedDOSGraphEdge incomingEdge:this.trimmedIntegratedDOSGraph.incomingEdgesOf(vftreeMetadataContainingNode)){
			if(incomingEdge.getType().equals(DOSEdgeType.OPERATION)) {
//				incomingEdgeOfOperationType++;
				
				Operation operation = this.visScheme.getOperationLookup().lookup(incomingEdge.getOperationID());
				
				if(operation instanceof VfTreeTrimmingOperationBase)
					return true;
			}
		}
		
		return false;
		
//		if(incomingEdgeOfOperationType==0)
//			return false;
//		else
//			return true; //there is at least one operation use the vftree as input and all such operations are of VfTreeTrimmingOperationBase type;
		
	}
	
}
