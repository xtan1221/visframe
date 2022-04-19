package dependency.vccl.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import dependency.vccl.utils.helper.CopyLink;
import exception.VisframeException;


/**
 * 
 * auto-addition of copy link is not implemented;
 * in fact, auto-addition is due to single assignable copy, which can be 
 * 
 * 
 * the vertex type V and edge type E should override {@link #equals(Object)} and {@link #hashCode()} methods
 * 
 * @author tanxu
 *
 * @param <V>
 * @param <E>
 */
public class DAGNodeCopyLinkAssigner<V,E> {
	private final SimpleDirectedGraph<V,E> dag;
	private final Class<E> edgeType;
	/**
	 * map from node
	 * to the map
	 * 		from the copy index
	 * 		to the NodeCopy
	 */
	private final Map<V, Map<Integer, NodeCopy<V>>> nodeCopyIndexNodeCopyMapMap;
	
	/////////////////////////
	private CLCBCCDetector2<V,E> clcbccDetector;
	
//	/**
//	 * map from node
//	 * to the map
//	 * 		from the copy index
//	 * 		to the NodeCopy
//	 */
//	private Map<V, Map<Integer, NodeCopy<V>>> nodeCopyIndexNodeCopyMapMap;
	private Map<V,Integer> nodeCopyNumberMap;
	
	/**
	 * map from depending node on the dag
	 * to the map
	 * 		from copy index of the depending node
	 * 		to the map
	 * 			from the depended node on the dag
	 * 			to the copy index of the depended node to which the depending node's copy is linked
	 */
	private Map<V, Map<Integer, Map<V,Integer>>> dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap;
	
	/**
	 * 
	 * @param dag
	 * @param edgeType
	 * @param nodeCopyNumberMap
	 */
	public DAGNodeCopyLinkAssigner(
			SimpleDirectedGraph<V,E> dag,
			Class<E> edgeType,
			Map<V, Map<Integer, NodeCopy<V>>> nodeCopyIndexNodeCopyMapMap
			){
		//
		
		this.dag = dag;
		this.edgeType = edgeType;
		this.nodeCopyIndexNodeCopyMapMap = nodeCopyIndexNodeCopyMapMap;
		
		
		//////////////////////////
		this.detectCLCBCCs();
		this.buildNodeCopyNumberMap();
		this.initializeDependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap();
	}
	
	
	private void detectCLCBCCs() {
		this.clcbccDetector = new CLCBCCDetector2<>(this);
	}
	
	/**
	 * first create NodeCopy for each node on the dag and populate the nodeCopyIndexNodeCopyMapMap;
	 * 		note that the first copy index is 1 rather than 0;
	 * 
	 * then build the key set of the dependedNodeLinkedNodeCopyMap of each NodeCopy and set it with 
	 * 		{@link NodeCopy#setDependedNodeLinkedNodeCopyMap(Map)}
	 */
	private void buildNodeCopyNumberMap() {
		//1. initialize and populate nodeCopyIndexNodeCopyMapMap;
		this.nodeCopyNumberMap = new HashMap<>();
		
		this.nodeCopyIndexNodeCopyMapMap.forEach((node,map)->{
			this.nodeCopyNumberMap.put(node, map.size());
		});
		
		//2
		this.dag.vertexSet().forEach(v->{
			for(int i=1;i<=this.nodeCopyNumberMap.get(v);i++) {
				Map<V, NodeCopy<V>> dependedNodeLinkedNodeCopyMap = new HashMap<>();
				
				this.dag.outgoingEdgesOf(v).forEach(e->{
					V dependedNode = this.dag.getEdgeTarget(e);
					
					dependedNodeLinkedNodeCopyMap.put(dependedNode, null);
				});
				this.nodeCopyIndexNodeCopyMapMap.get(v).get(i).setDependedNodeLinkedNodeCopyMap(dependedNodeLinkedNodeCopyMap);
			}
		});
	}
	
	/**
	 * initialize dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap
	 * 
	 */
	private void initializeDependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap() {
		this.dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap = new HashMap<>();
		
		this.dag.edgeSet().forEach(e->{
			V dependingNode = this.dag.getEdgeSource(e);
			V dependedNode = this.dag.getEdgeTarget(e);
			
			if(!this.dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap.containsKey(dependingNode)) {
				this.dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap.put(dependingNode, new HashMap<>());
			}
			
			for(int i=1;i<=this.nodeCopyNumberMap.get(dependingNode);i++) {
				this.dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap.get(dependingNode).put(i, new HashMap<>());
				
				this.dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap.get(dependingNode).get(i).put(dependedNode, null);
			}
		});
	}
	
	///////////////////////////////////////////
	/**
	 * find out the full set of copies of depended nodes of the node of the given NodeCopy that can be linked from it;
	 * 
	 * 1. if the owner node of the given NodeCopy is in a set of {@link CLCBCC2}s
	 * 		for each {@link CLCBCC2}, add the set returned by the {@link CLCBCC2#getDependedNodeLinkableNodeCopySetMap(NodeCopy)} to the returned set of this method
	 * 
	 * 2. for each depended node of the owner node of the given NodeCopy the edge between which is not in any {@link CLCBCC2}
	 * 		if there is no copy of the depended node is linked by the given NodeCopy
	 * 			add every NodeCopy of the depended node to the returned set;
	 * 
	 * @param nodeCopy
	 * @return
	 */
	public Map<V, Set<NodeCopy<V>>> findoutAllDependedNodeLinkableNodeCopySetMap(NodeCopy<V> nodeCopy){
		Map<V, Set<NodeCopy<V>>> ret = new HashMap<>();
		
		//1
		if(this.getClcbccDetector().getDagNodeInvolvedCLCBCSetMap().containsKey(nodeCopy.getNode())) {
			this.getClcbccDetector().getDagNodeInvolvedCLCBCSetMap().get(nodeCopy.getNode()).forEach(c->{
				ret.putAll(c.getDependedNodeLinkableNodeCopySetMap(nodeCopy));
			});
		}
		
		
		//2
		this.dag.outgoingEdgesOf(nodeCopy.getNode()).forEach(e->{
			if(!this.getClcbccDetector().getDagEdgeCLCBCCMap().containsKey(e)) {
				V dependedNode = this.dag.getEdgeTarget(e);
				
				if(nodeCopy.getDependedNodeLinkedNodeCopyMap().get(dependedNode)==null) {
					if(!ret.containsKey(dependedNode))
						ret.put(dependedNode, new HashSet<>());
					
					ret.get(dependedNode).addAll(this.nodeCopyIndexNodeCopyMapMap.get(dependedNode).values());
				}
			}
		});
		
		return ret;
	}
	
	
	
	///////////////////////////////////////////
	/**
	 * add the link between the two given node copies
	 * 
	 * note that the caller rather than this method should check if the link is assignable;
	 * 		use the method {@link #findoutAllDependedNodeLinkableNodeCopySetMap(NodeCopy)}
	 * 
	 * @param dependingNode
	 * @param dependingNodeCopyIndex
	 * @param dependedNode
	 * @param dependedNodeCopyIndex
	 */
	public void addLink(V dependingNode, int dependingNodeCopyIndex, V dependedNode, int dependedNodeCopyIndex) {
		//update dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap
		this.dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap.get(dependingNode).get(dependingNodeCopyIndex).put(dependedNode, dependedNodeCopyIndex);
		//also update involved NodeCopy.dependedNodeLinkedNodeCopyMap of the depending node's copy
		NodeCopy<V> dependingNodeCopy = this.nodeCopyIndexNodeCopyMapMap.get(dependingNode).get(dependingNodeCopyIndex);
		NodeCopy<V> dependedNodeCopy = this.nodeCopyIndexNodeCopyMapMap.get(dependedNode).get(dependedNodeCopyIndex);
		dependingNodeCopy.getDependedNodeLinkedNodeCopyMap().put(
				dependedNode, 
				dependedNodeCopy);
		
		System.out.println();
	}
	
	
	/**
	 * remove the link between the two node copies;
	 * 
	 * @param dependingNode
	 * @param dependingNodeCopyIndex
	 * @param dependedNode
	 * @param dependedNodeCopyIndex
	 */
	public void removeLink(V dependingNode, int dependingNodeCopyIndex, V dependedNode, int dependedNodeCopyIndex) {
		//update dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap
		this.dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap.get(dependingNode).get(dependingNodeCopyIndex).put(dependedNode, null);
		//also update involved NodeCopy.dependedNodeLinkedNodeCopyMap
		this.nodeCopyIndexNodeCopyMapMap.get(dependingNode).get(dependingNodeCopyIndex).getDependedNodeLinkedNodeCopyMap().put(
				dependedNode, 
				null);
	}
	
	/**
	 * clear all copy links
	 * 1. clear all linked copies of depended node in each {@link NodeCopy}
	 * 2. clear {@link #dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap}
	 */
	public void clearAllLinks() {
		this.dag.vertexSet().forEach(v->{
			for(int i=1;i<=this.nodeCopyNumberMap.get(v);i++) {
				Map<V, NodeCopy<V>> dependedNodeLinkedNodeCopyMap = new HashMap<>();
				
				this.dag.outgoingEdgesOf(v).forEach(e->{
					V dependedNode = this.dag.getEdgeTarget(e);
					
					dependedNodeLinkedNodeCopyMap.put(dependedNode, null);
				});
				this.nodeCopyIndexNodeCopyMapMap.get(v).get(i).setDependedNodeLinkedNodeCopyMap(dependedNodeLinkedNodeCopyMap);
			}
		});
		
		this.initializeDependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap();
	}
	//////////////////////////
	
	/**
	 * @return the dag
	 */
	public SimpleDirectedGraph<V, E> getDag() {
		return dag;
	}


	/**
	 * @return the edgeType
	 */
	public Class<E> getEdgeType() {
		return edgeType;
	}


	/**
	 * @return the nodeCopyNumberMap
	 */
	public Map<V, Integer> getNodeCopyNumberMap() {
		return nodeCopyNumberMap;
	}


	/**
	 * @return the clcbccDetector
	 */
	public CLCBCCDetector2<V, E> getClcbccDetector() {
		return clcbccDetector;
	}


	/**
	 * @return the nodeCopyIndexNodeCopyMapMap
	 */
	public Map<V, Map<Integer, NodeCopy<V>>> getNodeCopyIndexNodeCopyMapMap() {
		return nodeCopyIndexNodeCopyMapMap;
	}


	/**
	 * @return the dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap
	 */
	public Map<V, Map<Integer, Map<V, Integer>>> getDependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap() {
		return dependingNodeCopyIndexDependedNodeLinkedCopyIndexMapMapMap;
	}

	
	///////////////////////////////////////
	/**
	 * check and return whether all links have been created;
	 * Equivalently to check if every copy of a depending node has a link to one copy of every depended node;
	 * @return
	 */
	public boolean copyLinkGraphIsFullyBuilt() {
		for(V dependingNode:this.dag.vertexSet()){
			for(E e:this.dag.outgoingEdgesOf(dependingNode)){
				V dependedNode = this.dag.getEdgeTarget(e);
				
				for(int copyIndex:this.nodeCopyIndexNodeCopyMapMap.get(dependingNode).keySet()){
					NodeCopy<V> dependingNodeCopy = this.nodeCopyIndexNodeCopyMapMap.get(dependingNode).get(copyIndex);
					
					if(dependingNodeCopy.getDependedNodeLinkedNodeCopyMap().get(dependedNode)==null) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	
	/**
	 * build and return full copy link graph with node type being {@link NodeCopy} and edge type being {@link CopyLink};
	 * 
	 * if not all copy links are created, throw exception;
	 * @return
	 */
	public SimpleDirectedGraph<NodeCopy<V>, CopyLink<V>> buildFullCopyLinkGraph(){
		if(!copyLinkGraphIsFullyBuilt())
			throw new VisframeException("copy links are not fully created, cannot build full copy link graph!");
		
		
		SimpleDirectedGraph<NodeCopy<V>, CopyLink<V>> ret = new SimpleDirectedGraph<>(CopyLink.class);
		
		//first add all copies
		this.dag.vertexSet().forEach(node->{
			this.nodeCopyIndexNodeCopyMapMap.get(node).forEach((copyIndex, copy)->{
				ret.addVertex(copy);
			});
		});
		
		
		//add all copy links
		for(V dependingNode:this.dag.vertexSet()){
			for(E e:this.dag.outgoingEdgesOf(dependingNode)){
				V dependedNode = this.dag.getEdgeTarget(e);
				
				for(int copyIndex:this.nodeCopyIndexNodeCopyMapMap.get(dependingNode).keySet()){
					NodeCopy<V> dependingCopy = this.nodeCopyIndexNodeCopyMapMap.get(dependingNode).get(copyIndex);
					
					NodeCopy<V> dependedCopy = dependingCopy.getDependedNodeLinkedNodeCopyMap().get(dependedNode);
					
					ret.addEdge(dependingCopy, dependedCopy, new CopyLink<V>(dependingCopy, dependedCopy));
				}
			}
		}
		
		return ret;
	}
}
