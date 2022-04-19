package dependency.vccl.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.graph.SimpleDirectedGraph;

import dependency.SimpleDirectedGraphUtils;

/**
 * detector class for all {@link CLCBCC} in a VCD graph;
 * 
 * ========================================reasoning behind why choosing biconnected component===================
 * the ultimate goal is to find out all connected components on the VCD graph whose copy links are constrained as a whole;
 * to achieve this, need to find out all connected components(CC) of the VCD graph with the following properties 
 * 		1. the CC does not have cut vertex nor cut edge (condition 1)
 * 		2. the CC is not bipartite (condition 2)
 * 			there are at least three levels of nodes in the CC (see {@link DAGNodeLevelAssigner})
 * 
 * for condition 1, in fact, having no cut vertex is a stronger condition than having no cut edge, 
 * thus condition 1 is equivalent to 
 * 		every CC does not have cut vertex, which is a biconnected component;
 * 
 * thus can use the {@link BiconnectivityInspector#getBlocks()} to help detect the needed connected components,
 * then check each of those CCs to find out those with at least three node levels;
 * 
 * @author tanxu
 *
 */
public final class CLCBCCDetector2<V,E> {
	private final DAGNodeCopyLinkAssigner<V,E> DAGNodeCopyLinkAssigner;
	
	///////////////////
	/**
	 * the DAG on which the detection of {@link CLCBCC} is operated;
	 * it contains the same structure as the {@link #targetVCDGraph};
	 * any operation on the STRUCTURE of this graph will not affect the structure of the {@link #targetVCDGraph};
	 * 
	 * note that must use the VSComponent as the node type rather than the VCDNodeImpl to ensure data safety with the {@link #targetVCDGraph};
	 */
//	private SimpleDirectedGraph<V, E> clonedDAG;
	
	
	///////////////
	/**
	 * set of detected {@link CLCBCC}
	 */
	private Set<CLCBCC2<V,E>> detectedCLCBCCSet;
	
	/**
	 * map from each VCDEdge to the {@link CLCBCC} it is belonging to;
	 * 
	 * if a VCDEdge is not belonging to any {@link CLCBCC}, it is not included in this map;
	 * 
	 * note that a VCDEdge can only be assigned to at most one {@link CLCBCC}; cannot be shared by multiple {@link CLCBCC}s;
	 */
	private Map<E, CLCBCC2<V,E>> dagEdgeCLCBCMap;

	/**
	 * map from each VCDNode to the set of {@link CLCBCC}s on which the VCDNode is present;
	 * 
	 * if a VCDNode is not on any {@link CLCBCC}, it is not included in this map;
	 * 
	 * note that a VCDNode can be shared by multiple {@link CLCBCC}s (only if the VCDNode is a cut vertex of those {@link CLCBCC}s);
	 */
	private Map<V, Set<CLCBCC2<V,E>>> dagNodeInvolvedCLCBCSetMap;
	
	
	//////////////////////////////////////////
	/**
	 * constructor
	 * @param targetVCDGraph
	 */
	public CLCBCCDetector2(
			DAGNodeCopyLinkAssigner<V,E> DAGNodeCopyLinkAssigner){
		//
		this.DAGNodeCopyLinkAssigner = DAGNodeCopyLinkAssigner;
		
		
		////////////////////////////
//		this.cloneDAG();
		this.detectCLCBCCs();
	}
	
	
	/**
	 * detect all {@link CLCBCC}s on the target VCD graph;
	 * 
	 * 1. detect all blocks (biconnected components) with {@link BiconnectivityInspector};
	 * 
	 * 2. for each vcd subgraph induced by nodes of each of the detected block
	 * 		1. find out the maximal level of nodes in it with {@link DAGNodeLevelAssigner};
	 * 
	 * 		2. if the maximal level is 1, the vcd subgraph induced by the block is NOT a {@link CLCBCC}; 
	 * 		
	 * 		3. if the maximal level is >=2, the vcd subgraph induced by the block is a {@link CLCBCC};
	 * 
	 * 3. Initialize and populate {@link #dagEdgeCLCBCMap} and {@link #dagNodeInvolvedCLCBCSetMap};
	 */
	private void detectCLCBCCs() {
		this.detectedCLCBCCSet = new HashSet<>();
		this.dagEdgeCLCBCMap = new HashMap<>();
		this.dagNodeInvolvedCLCBCSetMap = new HashMap<>();
		///////////////
		//note that the returned blocks (subgraph) are undirected!!!!!!!
		BiconnectivityInspector<V, E> biconnectivityInspector = new BiconnectivityInspector<>(this.getDAGNodeCopyLinkAssigner().getDag());
		
		//detect all blocks in the vcd graph;
		//each block is a biconnected graph, which contains no cut vertex (thus no cut edge?!);
		//note that not every block is CLCBCC, still need to check the levels of nodes in each block
		//those blocks with only two levels are NOT CLCBCC!!!!!!!!!!!!
		Set<Graph<V, E>> blocks = biconnectivityInspector.getBlocks();
		
		for(Graph<V, E> block:blocks) {
			//build the node set in the block subgraph;
			Set<V> nodeSet = new HashSet<>();
			block.vertexSet().forEach(v->{
				nodeSet.add(v);
			});
			
			//build SimpleDirectedGraph subgraph of the block
			SimpleDirectedGraph<V, E> subgraph = 
					SimpleDirectedGraphUtils.subgraph(this.getDAGNodeCopyLinkAssigner().getDag(), nodeSet, this.DAGNodeCopyLinkAssigner.getEdgeType());
			
			//find out edge set in the block subgraph;
			Set<E> edgeSet = new HashSet<>();
			subgraph.edgeSet().forEach(e->{
				edgeSet.add(e);
			});
			
			//check if the block is a bipartite graph such that
			//1. all nodes are either root node (not depended node) or non-root node;
			//2. all non-root nodes are only depending on root nodes, there is no dependency between non-root nodes;
			//this type of block has no constraints on the copy links between the vcd nodes;
			DAGNodeLevelAssigner<V,E> levelAssigner = new DAGNodeLevelAssigner<>(subgraph);
			if(levelAssigner.getMaxLevel()>1) {//there are three or more node levels
				CLCBCC2<V,E> clcbcc = new CLCBCC2<>(this, nodeSet, edgeSet);
				
				this.detectedCLCBCCSet.add(clcbcc);
				
				//update dagEdgeCLCBCMap
				edgeSet.forEach(e->{
					this.dagEdgeCLCBCMap.put(e, clcbcc);
				});
				
				//update dagNodeInvolvedCLCBCSetMap
				nodeSet.forEach(v->{
					if(!this.dagNodeInvolvedCLCBCSetMap.containsKey(v))
						this.dagNodeInvolvedCLCBCSetMap.put(v, new HashSet<>());
					
					this.dagNodeInvolvedCLCBCSetMap.get(v).add(clcbcc);
				});
			}
			
		}
		
	}
	
	/////////////////////////

	/**
	 * @return the DAGNodeCopyLinkAssigner
	 */
	public DAGNodeCopyLinkAssigner<V, E> getDAGNodeCopyLinkAssigner() {
		return DAGNodeCopyLinkAssigner;
	}

	/**
	 * @return the clcbcSet
	 */
	public Set<CLCBCC2<V,E>> getCLCBCSet() {
		return detectedCLCBCCSet;
	}

	/**
	 * @return the vcdEdgeCLCBCMap
	 */
	public Map<E, CLCBCC2<V,E>> getDagEdgeCLCBCCMap() {
		return dagEdgeCLCBCMap;
	}

	/**
	 * @return the vcdNodeInvolvedCLCBCSetMap
	 */
	public Map<V, Set<CLCBCC2<V,E>>> getDagNodeInvolvedCLCBCSetMap() {
		return dagNodeInvolvedCLCBCSetMap;
	}
	
}
