package dependency.vccl.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import dependency.JGraphTDependencyGraphUtils;

/**
 * assign an integer level to each node in a DAG;
 * 
 * for node with no depended nodes (root node), assign level 0;
 * for non-root nodes, assign level = max{levels of depended nodes} + 1;
 * 
 * note that the vertex and edge class of the given DAG must override equals() and hashCode() methods;
 * @author tanxu
 *
 * @param <V>
 * @param <E>
 */
public final class DAGNodeLevelAssigner<V,E> {
	
	private final SimpleDirectedGraph<V,E> dag;
	
	////////////
	private Map<V, Integer> nodeLevelMap;
	
	private int maxLevel = 0;
	
	/**
	 * 
	 * @param dag
	 */
	public DAGNodeLevelAssigner(SimpleDirectedGraph<V,E> dag){
		if(JGraphTDependencyGraphUtils.containsCycle(dag))
			throw new IllegalArgumentException("given dag contains cycle!");
		
		if(dag.vertexSet().isEmpty())
			throw new IllegalArgumentException("given dag contains no vertex!");
		
		this.dag = dag;
		this.assignLevels();
	}
	
	
	private void assignLevels() {
		this.nodeLevelMap = new HashMap<>();
		this.maxLevel = 0;
		//
		Map<V, Integer> nodeUnassignedDependedNodeNumMap = new HashMap<>();
		Set<V> nodeSetReadyToAssignLevel = new HashSet<>();
		this.dag.vertexSet().forEach(v->{
			int outDegree = this.dag.outDegreeOf(v);
			nodeUnassignedDependedNodeNumMap.put(v, outDegree);
			if(outDegree==0)
				nodeSetReadyToAssignLevel.add(v);
		});
		
		while(!nodeSetReadyToAssignLevel.isEmpty()) {
			V v = nodeSetReadyToAssignLevel.iterator().next();
			nodeSetReadyToAssignLevel.remove(v);
			
			////////////
			if(this.dag.outDegreeOf(v)==0) {
				this.nodeLevelMap.put(v, 0);
			}else {
				//
				int maxLevelOfDependedNodes = 0;
				
				for(E e:this.dag.outgoingEdgesOf(v)){
					if(this.nodeLevelMap.get(this.dag.getEdgeTarget(e))>maxLevelOfDependedNodes)
						maxLevelOfDependedNodes = this.nodeLevelMap.get(this.dag.getEdgeTarget(e));
				}
				
				this.nodeLevelMap.put(v, maxLevelOfDependedNodes+1);
				if(maxLevelOfDependedNodes+1>this.maxLevel)
					this.maxLevel = maxLevelOfDependedNodes+1;
			}
			
			//update nodeUnassignedDependedNodeNumMap and nodeSetReadyToAssignLevel
			this.dag.incomingEdgesOf(v).forEach(e->{
				V dependingNode = this.dag.getEdgeSource(e);
				int unassignedDependedNodeNum = nodeUnassignedDependedNodeNumMap.get(dependingNode)-1;
				
				nodeUnassignedDependedNodeNumMap.put(dependingNode, unassignedDependedNodeNum);
				
				if(unassignedDependedNodeNum==0)
					nodeSetReadyToAssignLevel.add(dependingNode);
			});
		}
	}
	
	///////////////////////
	/**
	 * @return the nodeLevelMap
	 */
	public Map<V, Integer> getNodeLevelMap() {
		return nodeLevelMap;
	}


	/**
	 * @return the maxLevel
	 */
	public int getMaxLevel() {
		return maxLevel;
	}


}
