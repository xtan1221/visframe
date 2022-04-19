package dependency.cfd.integrated;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import context.scheme.VisScheme;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;

/**
 * class to trim an integrated CFD graph;
 * 
 * trim the integrated CFD graph by ITERATIVELY removing all leaf nodes of cf and copy index if one or both of the following is true;
 * 1. the cf is not owned by a core ShapeCFG
 * (removed)2. the cf is owned by a core ShapeCFG (of its assigned VSComponent) but the VSCopy is not selected to be include in the core ShapeCFG set of the resulted VisInstance 
 * until there is no such leaf nodes left;
 * 
 * can only be performed after integrated CFD graph is built with {@link IntegratedCFDGraphBuilder};
 * 
 * note that the trimming is directly on the underlying graph of the integrated CFD graph;
 * @author tanxu
 * 
 */
public class IntegratedCFDGraphTrimmer {
	/**
	 * 
	 */
	private final VisScheme visScheme;
	
	/**
	 * the integrated CFD graph to be trimmed;
	 * note that the trimming operations are directly operated on this graph;
	 */
	private final SimpleDirectedGraph<IntegratedCFDGraphNode, IntegratedCFDGraphEdge> iCFDUnderlyingGraph;
	
	/**
	 * map from the CompositionFunctionGroupID cfgID to the set of copy index such that for each copy index i,
	 * there is at least one CompositionFunction of cfgID with the copy index i is kept in the trimmed integrated CFD graph;
	 * 
	 * facilitate to trim integrated DOS graph and the step in reproducing and insertion of CFGs;
	 * 
	 */
	private Map<CompositionFunctionGroupID, Set<Integer>> keptCFGIDCopyIndexSetMap;
	
	/**
	 * map from CompositionFunctionID cfID to the set of copy index of the VCDNode/VSComponent to which the host CFGID of the cfID is assigned, such that:
	 * for each copy index i in the set, the corresponding IntegratedCFDNode (of cfID and i) is kept in the trimmed integrated CFD graph;
	 * 
	 * facilitate to trim integrated DOS graph and the step in reproducing and insertion of CFs;
	 */
	private Map<CompositionFunctionID, Set<Integer>> keptCfIDCopyIndexSetMap;
	
	
	/**
	 * constructor
	 * @param visScheme
	 * @param iCFDUnderlyingGraph
	 */
	public IntegratedCFDGraphTrimmer(
			VisScheme visScheme, 
			SimpleDirectedGraph<IntegratedCFDGraphNode, IntegratedCFDGraphEdge> iCFDUnderlyingGraph){
		//TODO validations
		
		this.visScheme = visScheme;
		this.iCFDUnderlyingGraph = iCFDUnderlyingGraph;
		
		this.trim();
		this.postprocess();
	}
	
	
	/**
	 * trim the integrated CFD graph by ITERATIVELY removing all leaf nodes of cf and copy index if one or both of the following is true;
	 * 1. the cf is not owned by a core ShapeCFG
	 * 2. the cf is owned by a core ShapeCFG (of its assigned VSComponent) but the VSCopy is not selected to be include in the core ShapeCFG set of the resulted VisInstance 
	 * until there is no such leaf nodes left;
	 */
	private void trim() {
		Set<IntegratedCFDGraphNode> trimmedNodeSet = new HashSet<>();
		boolean trimmableNodeFound = true;
		
		while(trimmableNodeFound) {
			//initialize
			trimmableNodeFound = false;
			trimmedNodeSet.clear();
			
			//check every leaf node in the iCFDUnderlyingGraph whether it should be trimmed or kept;
			for(IntegratedCFDGraphNode node:this.iCFDUnderlyingGraph.vertexSet()) {
				//leaf node
				if(this.iCFDUnderlyingGraph.inDegreeOf(node)==0) {
					CompositionFunctionGroupID ownerCFGID = node.getCfID().getHostCompositionFunctionGroupID();
					
					if(!this.visScheme.getCoreShapeCFGIDSet().contains(ownerCFGID)) {//owner cfg is not a core shapeCFG of the owner VSComponent
						//remove from the graph
						trimmedNodeSet.add(node);
						trimmableNodeFound = true;
					}
					//since all core shapeCFG's cf can be selected to be included in the core shapecfg set of the resulted VisInstance, 
					//thus do not remove any of those cf nodes
					//TODO re-check
//					else {//owner cfg is a core shapecFG of the owner VSComponent
//						VSCopy copy = node.getAssignedVCDNode().getVSCopyIndexMap().get(node.getCopyIndex());
//						if(!copy.isToBeIncludedInCoreShapeCFGSet()) {//the owner VSCopy is not selected to be included in the core ShapeCFG set
//							trimmedNodeSet.add(node);
//							trimmableNodeFound = true;
//						}
//					}
				}
			}
			
			if(!trimmedNodeSet.isEmpty())
				this.iCFDUnderlyingGraph.removeAllVertices(trimmedNodeSet);
		}
		
	}
	
	/**
	 * initialize and build {@link #keptCFGIDCopyIndexSetMap} and {@link #keptCfIDCopyIndexSetMap}
	 */
	private void postprocess() {
		this.keptCFGIDCopyIndexSetMap = new HashMap<>();
		this.keptCfIDCopyIndexSetMap = new HashMap<>();
		
		this.keptCFGIDCopyIndexSetMap = new HashMap<>();
		
		this.iCFDUnderlyingGraph.vertexSet().forEach(v->{
			CompositionFunctionGroupID cfgID = v.getCfID().getHostCompositionFunctionGroupID();
			if(!this.keptCFGIDCopyIndexSetMap.containsKey(cfgID)) {
				this.keptCFGIDCopyIndexSetMap.put(cfgID, new HashSet<>());
			}
			this.keptCFGIDCopyIndexSetMap.get(cfgID).add(v.getCopyIndex());
			//
			if(!this.keptCfIDCopyIndexSetMap.containsKey(v.getCfID())) {
				this.keptCfIDCopyIndexSetMap.put(v.getCfID(), new HashSet<>());
			}
			this.keptCfIDCopyIndexSetMap.get(v.getCfID()).add(v.getCopyIndex());
		});
	}

	///////////////////////////
	/**
	 * return the integrated CFD underlying graph that has been trimmed;
	 * @return the iCFDUnderlyingGraph
	 */
	public SimpleDirectedGraph<IntegratedCFDGraphNode, IntegratedCFDGraphEdge> getTrimmedIntegratedCFDUnderlyingGraph() {
		return iCFDUnderlyingGraph;
	}
	

	/**
	 * @return the keptCFGIDCopyIndexSetMap
	 */
	public Map<CompositionFunctionGroupID, Set<Integer>> getKeptCFGIDCopyIndexSetMap() {
		return keptCFGIDCopyIndexSetMap;
	}


	/**
	 * @return the keptCfIDCopyIndexSetMap
	 */
	public Map<CompositionFunctionID, Set<Integer>> getKeptCfIDCopyIndexSetMap() {
		return keptCfIDCopyIndexSetMap;
	}
	
	
	
}
