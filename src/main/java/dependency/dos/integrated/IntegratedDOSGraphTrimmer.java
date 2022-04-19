package dependency.dos.integrated;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import context.scheme.VisScheme;
import dependency.cfd.integrated.IntegratedCFDGraphTrimmer;
import dependency.vccl.VSCopy;
import dependency.vcd.VCDGraph;
import dependency.vcd.VCDNodeImpl;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;
import metadata.DataType;
import metadata.MetadataID;


/**
 * class to trim an integrated DOS graph;
 * 
 * 
 * trim the integrated DOS graph by removing all leaf nodes:
 * 
 * 1. contains record metadata AND copy index not depended by any CFG and CF in the trimmed integrated CFD graph
 * 2. contains composite (generic graph metadata)
 * 		note that composite metadata containing leaf node should be removed because it is not depended by any CFG or CF!
 * 
 * until there is no such nodes left;
 * 
 * 
 * can only be performed after integrated DOS graph is built with {@link IntegratedDOSGraphBuilder} and integrated CFD graph is trimmed with {@link IntegratedCFDGraphTrimmer};
 * 
 * note that the trimming is directly on the underlying graph of the integrated DOS graph;
 * @author tanxu
 * 
 */
public class IntegratedDOSGraphTrimmer {

	/**
	 * 
	 */
	private final VisScheme visScheme;
	/**
	 * VCD graph for assignment of Metadata and CompositionFunctionGroup to VCD node;
	 */
	private final VCDGraph vcdGraph;
	/**
	 * 
	 */
	private final IntegratedCFDGraphTrimmer integratedCFDGraphTrimmer;
	/**
	 * the integrated DOS graph to be trimmed;
	 */
	private final SimpleDirectedGraph<IntegratedDOSGraphNode, IntegratedDOSGraphEdge> iDOSUnderlyingGraph;
	
	
	///////////////////////
	/**
	 * map from record MetadataID to the set of copy index so that at least one of the following is met:
	 * 1. (record metadata + copy index) is the owner record data of one or more CompositionFunctionGroup with the same copy index
	 * 2. (record metadata + copy index) is depended by one or more CompositionFunction with the same copy index;
	 */
	private Map<MetadataID, Set<Integer>> recordMetadataIDCopyIndexSetMap;
	
	
	/**
	 * constructor
	 * @param visScheme
	 * @param vcdGraph
	 * @param integratedCFDGraphTrimmer
	 * @param iDOSUnderlyingGraph
	 * @throws SQLException 
	 */
	public IntegratedDOSGraphTrimmer(
			VisScheme visScheme,
			VCDGraph vcdGraph,
			IntegratedCFDGraphTrimmer integratedCFDGraphTrimmer,
			SimpleDirectedGraph<IntegratedDOSGraphNode, IntegratedDOSGraphEdge> iDOSUnderlyingGraph
			){
		//validation  TODO
		
		this.visScheme = visScheme;
		this.vcdGraph = vcdGraph;
		this.integratedCFDGraphTrimmer = integratedCFDGraphTrimmer;
		this.iDOSUnderlyingGraph = iDOSUnderlyingGraph;
		
		this.preprocess();
		this.trim();
	}
	
	/**
	 * @throws SQLException 
	 * 
	 */
	private void preprocess() {
		//initialize and populate recordMetadataIDCopyIndexSetMap;
		this.recordMetadataIDCopyIndexSetMap = new HashMap<>();
		
		//add owner record metadata of cfgs in keptCfgIDCopyIndexSetMap
		for(CompositionFunctionGroupID cfgID:this.integratedCFDGraphTrimmer.getKeptCFGIDCopyIndexSetMap().keySet()) {
			//
			MetadataID ownerRecordDataID = this.visScheme.getCompositionFunctionGroupLookup().lookup(cfgID).getOwnerRecordDataMetadataID();
			if(!this.recordMetadataIDCopyIndexSetMap.containsKey(ownerRecordDataID))
				this.recordMetadataIDCopyIndexSetMap.put(ownerRecordDataID, new HashSet<>());
			//
			VCDNodeImpl cfgAssignedVCDNode = this.vcdGraph.getCFGIDAssignedVCDNodeMap().get(cfgID);
			VCDNodeImpl ownerRecordMetadataAssignedVCDNode = this.vcdGraph.getMetadataIDAssignedVCDNodeMap().get(ownerRecordDataID);
			
			//process each copy of cfg
			for(int copyIndex:this.integratedCFDGraphTrimmer.getKeptCFGIDCopyIndexSetMap().get(cfgID)) {
				if(ownerRecordMetadataAssignedVCDNode.equals(cfgAssignedVCDNode)) {
					//cfg and owner record data are assigned to the same VCDNode
					//simply add the copy index of the cfg to the map
					this.recordMetadataIDCopyIndexSetMap.get(ownerRecordDataID).add(copyIndex);
				}else {
					//cfg and owner record data are assigned to different VCDNodes
					//find out the copy index of the ownerRecordMetadataAssignedVCDNode to which the copy of the cfgAssignedVCDNode is linked
					VSCopy cfgAssignedVCDNodeCopy = cfgAssignedVCDNode.getVSCopyIndexMap().get(copyIndex);
					int ownerRecordMetadataAssignedVCDNodeCopyIndex = 
							cfgAssignedVCDNodeCopy.getDependedVCDNodeLinkedCopyMap().get(ownerRecordMetadataAssignedVCDNode).getIndex();
					this.recordMetadataIDCopyIndexSetMap.get(ownerRecordDataID).add(ownerRecordMetadataAssignedVCDNodeCopyIndex);
				}
			}
		}
		
		//add depended record metadata of cfs in keptCfIDCopyIndexSetMap
		for(CompositionFunctionID cfID:this.integratedCFDGraphTrimmer.getKeptCfIDCopyIndexSetMap().keySet()) {
			CompositionFunction cf = this.visScheme.getCompositionFunctionLookup().lookup(cfID);
			//process each depended record Metadata of the cf;
			//note that the owner record metadata of the host cfg of the cf is included as well; 
			//whether to skip it or not does not make difference since it has already been processed by above CFG's owner record metadata processing;
			try {
				for(MetadataID dependedRecordDataID:cf.getDependedRecordMetadataIDInputColumnNameSetMap(this.visScheme).keySet()){
					if(!this.recordMetadataIDCopyIndexSetMap.containsKey(dependedRecordDataID))
						this.recordMetadataIDCopyIndexSetMap.put(dependedRecordDataID, new HashSet<>());
					
					VCDNodeImpl cfAssignedVCDNode = this.vcdGraph.getCFIDAssignedVCDNodeMap().get(cfID);
					VCDNodeImpl dependedRecordMetadataAssignedVCDNode = this.vcdGraph.getMetadataIDAssignedVCDNodeMap().get(dependedRecordDataID);
					
					//process each copy of cf
					for(int copyIndex:this.integratedCFDGraphTrimmer.getKeptCfIDCopyIndexSetMap().get(cfID)) {
						if(dependedRecordMetadataAssignedVCDNode.equals(cfAssignedVCDNode)) {
							//cfg and owner record data are assigned to the same VCDNode
							//simply add the copy index of the cfg to the map
							this.recordMetadataIDCopyIndexSetMap.get(dependedRecordDataID).add(copyIndex);
						}else {
							//cfg and owner record data are assigned to different VCDNodes
							//find out the copy index of the ownerRecordMetadataAssignedVCDNode to which the copy of the cfgAssignedVCDNode is linked
							VSCopy cfAssignedVCDNodeCopy = cfAssignedVCDNode.getVSCopyIndexMap().get(copyIndex);
							int dependedRecordMetadataAssignedVCDNodeCopyIndex = 
									cfAssignedVCDNodeCopy.getDependedVCDNodeLinkedCopyMap().get(dependedRecordMetadataAssignedVCDNode).getIndex();
							this.recordMetadataIDCopyIndexSetMap.get(dependedRecordDataID).add(dependedRecordMetadataAssignedVCDNodeCopyIndex);
						}
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();//this should never be reached!!!!!
			}
		}
	}
	
	
	/**
	 * trim the integrated DOS graph by ITERATIVELY removing all leaf nodes:
	 * 
	 * 1. contains record metadata AND copy index not depended by any CFG and CF in the trimmed integrated CFD graph
	 * 2. contains composite (generic graph metadata)
	 * 		note that composite metadata containing leaf node should be removed because it is not depended by any CFG or CF!
	 * 
	 * until there is no such nodes left;
	 * 
	 * implementation:
	 * 		do
	 * 				find out set S of leaf nodes of the updated integrated DOS underlying graph that 
	 * 					1. contains copy of record data and  that are not included in the {@link #recordMetadataIDCopyIndexSetMap};
	 * 					2. contains composite metadata
	 * 				delete node set S from the integrated DOS underlying graph;
	 * 		until S is empty;
	 */
	private void trim() {
		boolean nodeToBeTrimmedFound = true;
		Set<IntegratedDOSGraphNode> dosNodeSetToBeTrimmed = new HashSet<>();
		
		while(nodeToBeTrimmedFound) {
			dosNodeSetToBeTrimmed.clear();
			nodeToBeTrimmedFound = false;
			for(IntegratedDOSGraphNode node:this.iDOSUnderlyingGraph.vertexSet()) {
				if(this.iDOSUnderlyingGraph.inDegreeOf(node)==0) {// no depending nodes ==> leaf
					if(node.getMetadataID().getDataType().equals(DataType.RECORD)) {//contains record metadata
						if(this.recordMetadataIDCopyIndexSetMap.containsKey(node.getMetadataID())&&this.recordMetadataIDCopyIndexSetMap.get(node.getMetadataID()).contains(node.getCopyIndex())) {//
							//is depended by at least one cfg copy (as owner record data) or cf copy (as depended record data)
							
						}else {//
							nodeToBeTrimmedFound = true;
							//add the node to dosNodeSetToBeTrimmed
							dosNodeSetToBeTrimmed.add(node);
						}
					}else {//contains composite metadata
						nodeToBeTrimmedFound = true;
						//add the node to dosNodeSetToBeTrimmed
						dosNodeSetToBeTrimmed.add(node);
					}
				}
			}
			
			this.iDOSUnderlyingGraph.removeAllVertices(dosNodeSetToBeTrimmed);
		}
	}
	
	
	
	///////////////////////////////
	/**
	 * @return the iDOSUnderlyingGraph
	 */
	public SimpleDirectedGraph<IntegratedDOSGraphNode, IntegratedDOSGraphEdge> getTrimmedIntegratedDOSUnderlyingGraph() {
		
		return iDOSUnderlyingGraph;
	}


}
