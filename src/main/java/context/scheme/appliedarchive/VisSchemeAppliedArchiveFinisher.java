package context.scheme.appliedarchive;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import com.google.common.base.Objects;

import basic.VfNotes;
import context.scheme.VisScheme;
import context.scheme.appliedarchive.mapping.GenericGraphMapping;
import context.scheme.appliedarchive.mapping.MetadataMapping;
import dependency.cfd.integrated.IntegratedCFDGraphEdge;
import dependency.cfd.integrated.IntegratedCFDGraphNode;
import dependency.dos.DOSEdge.DOSEdgeType;
import dependency.dos.integrated.IntegratedDOSGraphEdge;
import dependency.dos.integrated.IntegratedDOSGraphNode;
import dependency.vccl.VCCLEdge;
import dependency.vccl.VSCopy;
import dependency.vcd.VCDEdgeImpl;
import dependency.vcd.VCDNodeImpl;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroup;
import function.group.CompositionFunctionGroupID;
import function.target.CFGTarget;
import metadata.MetadataID;

/**
 * helper class that build a VisSchemeApplierArchive object based on the essential data directly built by a {@link VisSchemeAppliedArchiveBuilder}
 * 
 * specifically, this class will build the secondary fields required to build VisSchemeApplierArchive including
 * 
 * {@link VisSchemeAppliedArchive#integratedDOSGraphNodeMappedMetadataIDMap}
 * {@link VisSchemeAppliedArchive#cfgIDOwnerRecordMetadataIDMapInVisScheme}
 * {@link VisSchemeAppliedArchive#cfIDDependedRecordMetadataIDSetMapInVisScheme}
 * {@link VisSchemeAppliedArchive#cfgIDTargetAssignedCFIDMapMapInVisScheme}
 * 
 * @author tanxu
 *
 */
public final class VisSchemeAppliedArchiveFinisher {
	private final VisScheme appliedVisScheme;
	
	////////////////////////////
	private final VfNotes notes;
	private final int visSchemeApplierArchiveUID;
	private final SimpleDirectedGraph<VCDNodeImpl, VCDEdgeImpl> visSchemeVCDGraph;
	private final SimpleDirectedGraph<VSCopy, VCCLEdge> vcclGraph;
	private final SimpleDirectedGraph<IntegratedCFDGraphNode, IntegratedCFDGraphEdge> trimmedIntegratedCFDGraph;
	private final SimpleDirectedGraph<IntegratedDOSGraphNode, IntegratedDOSGraphEdge> trimmedIntegratedDOSGraph;
	private final Map<IntegratedDOSGraphNode, MetadataMapping> selectedSolutionSetNodeMappingMap;
	///////////////
	
	/**
	 * 
	 * @param appliedVisScheme
	 * @param hostVisProjectDBContext
	 * @param VisSchemeAppliedArchiveUID
	 * @throws SQLException 
	 */
	public VisSchemeAppliedArchiveFinisher(
			VisScheme appliedVisScheme,
			
			VfNotes notes,
			int visSchemeApplierArchiveUID,
			SimpleDirectedGraph<VCDNodeImpl, VCDEdgeImpl> visSchemeVCDGraph,
			SimpleDirectedGraph<VSCopy, VCCLEdge> vcclGraph,
			SimpleDirectedGraph<IntegratedCFDGraphNode, IntegratedCFDGraphEdge> trimmedIntegratedCFDGraph,
			SimpleDirectedGraph<IntegratedDOSGraphNode, IntegratedDOSGraphEdge> trimmedIntegratedDOSGraph,
			Map<IntegratedDOSGraphNode, MetadataMapping> selectedSolutionSetNodeMappingMap
			) throws SQLException{
		//TODO validations
		
		
		this.appliedVisScheme = appliedVisScheme;
		
		this.notes =notes;
		this.visSchemeApplierArchiveUID = visSchemeApplierArchiveUID;
		this.visSchemeVCDGraph = visSchemeVCDGraph;
		this.vcclGraph = vcclGraph;
		this.trimmedIntegratedCFDGraph = trimmedIntegratedCFDGraph;
		this.trimmedIntegratedDOSGraph = trimmedIntegratedDOSGraph;
		this.selectedSolutionSetNodeMappingMap = selectedSolutionSetNodeMappingMap;
		
	}
	
	
	
	///////////////////////////////
	//facilitate build() method
	/**
	 * build and return the map from IntegratedDOSGraphNode to the MetadataID from host VisProjectDBContext mapped to the IntegratedDOSGraphNode;
	 * key set includes the following two types:
	 * 1. IntegratedDOSGraphNodes in the selected solution set in {@link #solutionSetSelectorAndMapper};
	 * 2. IntegratedDOSGraphNodes containing component Metadata of IntegratedDOSGraphNode containing the corresponding composite Metadata in the solution set
	 * 
	 * @return
	 */
	private Map<IntegratedDOSGraphNode, MetadataID> getIntegratedDOSGraphNodeMappedMetadataIDMap(){
		Map<IntegratedDOSGraphNode, MetadataID> ret = new HashMap<>();
		
		this.selectedSolutionSetNodeMappingMap.forEach((k,v)->{
			ret.put(k, v.getSourceMetadataID());
			if(k.getMetadataID().getDataType().isGenericGraph()) {
				GenericGraphMapping mapping =(GenericGraphMapping) v;
				
				//find out the node and record component record metadata's IntegratedDOSGraphNode
				this.trimmedIntegratedDOSGraph.incomingEdgesOf(k).forEach(e->{
					if(e.getType().equals(DOSEdgeType.COMPOSITE_DATA_COMPONENT)) {//check the edge type
						IntegratedDOSGraphNode componentDataNode = this.trimmedIntegratedDOSGraph.getEdgeSource(e);
						
						if(Objects.equal(componentDataNode.getMetadataID(),mapping.getTargetNodeRecordMetadataID())) {//the component is the node record data
							ret.put(componentDataNode, mapping.getSourceNodeRecordMetadataID());
						}else {
							ret.put(componentDataNode, mapping.getSourceEdgeRecordMetadataID());
						}
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * build and return map from CompositionFunctionGroupID to the owner record MetadataID in the applied VisScheme;
	 * @return
	 */
	private Map<CompositionFunctionGroupID, MetadataID> getCfgIDOwnerRecordMetadataIDMapInVisScheme(){
		Map<CompositionFunctionGroupID, MetadataID> ret = new HashMap<>();
		
		this.appliedVisScheme.getCompositionFunctionGroupLookup().getMap().forEach((id,cfg)->{
			ret.put(id, cfg.getOwnerRecordDataMetadataID());
		});
		return ret;
	}

	/**
	 * build and return the map from CompositionFunctionID to the set of depended record MetadataID in the applied VisScheme;
	 * 
	 * see doc of {@link CompositionFunction#getDependedRecordMetadataIDInputColumnNameSetMap(context.VisframeContext)} for details;
	 * @return
	 */
	private Map<CompositionFunctionID, Set<MetadataID>> getCfIDDependedRecordMetadataIDSetMapInVisScheme(){
		Map<CompositionFunctionID, Set<MetadataID>> ret = new HashMap<>();
		
		this.appliedVisScheme.getCompositionFunctionLookup().getMap().forEach((id,cf)->{
			try {
				ret.put(id, new HashSet<>(cf.getDependedRecordMetadataIDInputColumnNameSetMap(this.appliedVisScheme).keySet()));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		return ret;
	}
	
	
	/**
	 * build and return the map from CompositionFunctionGroupID
	 * to the map
	 * 		from CFGTarget
	 * 		to the assigned CompositionFunctionID
	 * 
	 * all the data in this map is extracted from the applied VisScheme;
	 * @return
	 */
	private Map<CompositionFunctionGroupID, Map<CFGTarget<?>, CompositionFunctionID>> getCfgIDTargetAssignedCFIDMapMapInVisScheme(){
		Map<CompositionFunctionGroupID, Map<CFGTarget<?>, CompositionFunctionID>> ret = new HashMap<>();
		
		this.appliedVisScheme.getCompositionFunctionLookup().getMap().forEach((cfid,cf)->{
			CompositionFunctionGroup hostCFG = this.appliedVisScheme.getCompositionFunctionGroupLookup().lookup(cf.getHostCompositionFunctionGroupID());
			if(!ret.containsKey(hostCFG.getID()))
				ret.put(hostCFG.getID(), new HashMap<>());
			cf.getAssignedTargetNameSet().forEach(n->{
				ret.get(cf.getHostCompositionFunctionGroupID()).put(
						hostCFG.getTargetNameMap().get(n), 
						cfid);
			});
		});
		
		return ret;
	}
	
	
	/**
	 * build and return a VisSchemeAppliedArchive
	 * @return
	 */
	public VisSchemeAppliedArchive build() {
		return new VisSchemeAppliedArchive(
				this.notes,
				this.visSchemeApplierArchiveUID,
				this.appliedVisScheme.getID(),
				this.visSchemeVCDGraph,
				this.vcclGraph,
				this.trimmedIntegratedCFDGraph,
				this.trimmedIntegratedDOSGraph,
				this.selectedSolutionSetNodeMappingMap,
				
				///
				this.getIntegratedDOSGraphNodeMappedMetadataIDMap(),
				this.getCfgIDOwnerRecordMetadataIDMapInVisScheme(),
				this.getCfIDDependedRecordMetadataIDSetMapInVisScheme(),
				this.getCfgIDTargetAssignedCFIDMapMapInVisScheme()
				);
	}
}
