package dependency.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.SimpleDirectedGraph;

/**
 * assign level index and order index for nodes on a DAG (dependency graph) 
 * 
 * Nodes with no out-going edges (no depended nodes) are assigned level 1;
 * 
 * all other nodes are assigned level equal to the largest level of all depended nodes + 1;
 * 
 * 
 * after all nodes are assigned a level, nodes at the same level will be sorted based on the given {@link #nodeOrderComparator};
 * 
 * 
 * @author tanxu
 *
 * @param <V>
 * @param <E>
 */
public class DAGNodeLevelAndOrderAssigner<V,E> {
	
	private final SimpleDirectedGraph<V,E> dag;
	
	/**
	 * comparator for ordering of nodes at the same level;
	 */
	private final Comparator<V> nodeOrderComparator; 
	
	
	////////////////////////////////
	/**
	 * map from the node to level index;
	 * level index is starting from 0, 1, 2, ...
	 */
	private Map<V, Integer> nodeLevelIndexMap;
	
	/**
	 * map from the node to the order index at its level;
	 * order index is from 0, 1, 2, ...
	 */
	private Map<V, Integer> nodeOrderIndexMap;
	
	/**
	 * 
	 */
	private Map<Integer, List<V>> levelIndexOrderedNodeListMap;
	
	
	/**
	 * 
	 * @param dag
	 */
	public DAGNodeLevelAndOrderAssigner(SimpleDirectedGraph<V,E> dag, Comparator<V> nodeOrderComparator){
		CycleDetector<V,E> cd = new CycleDetector<>(dag);
		if(cd.detectCycles())
			throw new IllegalArgumentException("Cycle found in the given DAG!");
		
		this.dag = dag;
		this.nodeOrderComparator= nodeOrderComparator;
		
		
		this.calculate();
	}
	
	
	private void calculate() {
		this.nodeLevelIndexMap = new HashMap<>();
		this.nodeOrderIndexMap = new HashMap<>();
		
		
		Map<V, Integer> nodelargestDependedNodeLevelMap = new HashMap<>();
		Map<V, Integer> nodeUnprocessedDependedNodeNumMap = new HashMap<>();
		
		Set<V> readyNodeSet = new HashSet<>();//set of node all of whose depended nodes' levels are determined; 
		
		//initialize
		this.dag.vertexSet().forEach(v->{
			int dependedNodeNum = this.dag.outDegreeOf(v);
			nodeUnprocessedDependedNodeNumMap.put(v, dependedNodeNum);
			nodelargestDependedNodeLevelMap.put(v, -1); 
			
			if(dependedNodeNum==0)
				readyNodeSet.add(v);
		});
		
		
		//calculate level index for each node;
		while(!readyNodeSet.isEmpty()) {
			V node = readyNodeSet.iterator().next();
			
			int level = nodelargestDependedNodeLevelMap.get(node)+1;
			
			this.nodeLevelIndexMap.put(node, level);
			
			for(E edge:this.dag.incomingEdgesOf(node)) {
				
				V dependingNode = this.dag.getEdgeSource(edge);
				
				//update the dependingNode's largest depended node level if it is larger than the current one in nodelargestDependedNodeLevelMap
				if(level>nodelargestDependedNodeLevelMap.get(dependingNode))
					nodelargestDependedNodeLevelMap.put(dependingNode, level);
				
				
				//update the number of unprocessed depended nodes of the dependingNode in nodeUnprocessedDependedNodeNumMap
				int updatedUnprocessedDependedNodeNum = nodeUnprocessedDependedNodeNumMap.get(dependingNode)-1;
				nodeUnprocessedDependedNodeNumMap.put(dependingNode, updatedUnprocessedDependedNodeNum);
				
				//if there is no unprocessed depended nodes, add the dependingNode to the readyNodeSet
				if(updatedUnprocessedDependedNodeNum==0)
					readyNodeSet.add(dependingNode);
				
			}
			
			
			readyNodeSet.remove(node);
		}
		
		
		/////////////////////////////////////
		//calculate order index of each level
		this.nodeOrderIndexMap = new HashMap<>();
		this.levelIndexOrderedNodeListMap = new HashMap<>();
		
//		Map<Integer, List<V>> levelIndexNodeSetMap = new HashMap<>();
		this.nodeLevelIndexMap.forEach((node,levelIndex)->{
			if(!this.levelIndexOrderedNodeListMap.containsKey(levelIndex))
				this.levelIndexOrderedNodeListMap.put(levelIndex, new ArrayList<>());
			
			this.levelIndexOrderedNodeListMap.get(levelIndex).add(node);
		});
		
		//sort nodes in each level with the nodeOrderComparator
		this.levelIndexOrderedNodeListMap.forEach((lvlIndex, nodeList)->{
			Collections.sort(nodeList, this.nodeOrderComparator);
			
			nodeList.forEach(n->{
				this.nodeOrderIndexMap.put(n, nodeList.indexOf(n));
			});
		});
		
	}
	
	
	
	
	//////////////////////////////////////////
	/**
	 * @return the dag
	 */
	public SimpleDirectedGraph<V, E> getDag() {
		return dag;
	}


	/**
	 * @return the nodeOrderComparator
	 */
	public Comparator<V> getNodeOrderComparator() {
		return nodeOrderComparator;
	}


	/**
	 * @return the nodeLevelIndexMap
	 */
	public Map<V, Integer> getNodeLevelIndexMap() {
		return nodeLevelIndexMap;
	}

	
	/**
	 * @return the nodeOrderIndexMap
	 */
	public Map<V, Integer> getNodeOrderIndexMap() {
		return nodeOrderIndexMap;
	}


	/**
	 * @return the levelIndexOrderedNodeListMap
	 */
	public Map<Integer, List<V>> getLevelIndexOrderedNodeListMap() {
		return levelIndexOrderedNodeListMap;
	}
	
	/**
	 * return the number of levels
	 * @return
	 */
	public int getNumOfLevels() {
		return this.levelIndexOrderedNodeListMap.size();
	}
	
	/**
	 * return the number of nodes of the level with the highest number of nodes
	 * @return
	 */
	public int getLargetNodeNumLevelSize() {
		int ret = 0;
		
		for(int lvl:this.levelIndexOrderedNodeListMap.keySet()){
			if(this.levelIndexOrderedNodeListMap.get(lvl).size()>ret)
				ret = this.levelIndexOrderedNodeListMap.get(lvl).size();
		}
		
		return ret;
	}
}
