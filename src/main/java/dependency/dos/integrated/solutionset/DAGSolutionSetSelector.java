package dependency.dos.integrated.solutionset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.SimpleDirectedGraph;

import exception.VisframeException;

/**
 * selector for a solution set on a dag;
 * 
 * one and only one node on each {@link RootPath} should be selected to form a solution set;
 * 
 * facilitate selecting a solution set from a trimmed integrated DOS graph in process of building a VisSchemeAppliedArchive;
 * 
 * @author tanxu
 *
 * @param <V>
 * @param <E>
 */
public class DAGSolutionSetSelector<V,E> {

	private final SimpleDirectedGraph<V,E> dag;
	
	//////////////////////
	private SimpleDirectedGraph<V,E> cloneDAG;
	
	private Set<RootPath<V>> rootPathSet;
	
	private Map<V, Set<RootPath<V>>> nodeAssignedRootPathSetMap;
	
	////////////////
	private Set<V> currentlySelectedNodeSet;


	private Set<RootPath<V>> rootPathSetWithOneNodeSelected;
	private Set<RootPath<V>> unselectedRootPathSet;
	
	/**
	 * constructor
	 * @param dag target DAG
	 */
	public DAGSolutionSetSelector(SimpleDirectedGraph<V,E> dag){
		if(dag==null)
			throw new IllegalArgumentException("given dag cannot be null!");
		if(dag.vertexSet().isEmpty())
			throw new IllegalArgumentException("given dag's vertex set is empty!");
		if(new CycleDetector<>(dag).detectCycles())
			throw new IllegalArgumentException("given dag contains cycles!");
		
		
		////////////////////////
		this.dag = dag;
		
		//
		this.preprocess();
		this.detectRootPaths();
		
		/////////
		this.currentlySelectedNodeSet = new HashSet<>();
		this.rootPathSetWithOneNodeSelected = new HashSet<>();
		
		this.unselectedRootPathSet = new HashSet<>();
		this.rootPathSet.forEach(rp->{
			this.unselectedRootPathSet.add(rp);
		});
	}
	
	/**
	 * 1. find out the set of edges E with target node n on dag that
	 * 		1. has two or more children nodes and 
	 * 		2. all (directly and indirectly) depended nodes of n has one single child
	 * 
	 * 2. create a cloned DAG 
	 * 
	 * 3. remove all source nodes of edges in E and all their directly and indirectly depending nodes from cloned DAG 
	 * 
	 * 4. the resulted cloned DAG will be used to detect all root path in {@link #detectRootPaths()}
	 */
	@SuppressWarnings("unchecked")
	private void preprocess() {
		
		//remove all nodes directly or indirectly depending on a node with two or more children
		//node with two or more children node and with no (directly and indirectly )depended nodes with two or more children
		
		Set<V> rootNodeSet = new HashSet<>();
		dag.vertexSet().forEach(v->{
			if(dag.outDegreeOf(v)==0)
				rootNodeSet.add(v);
		});
		
		//set of edges E with target node n that
		// 		1. has two or more children nodes and 
		// 		2. all (directly and indirectly) depended nodes of n has one single child
		Set<E> edgeSet = new HashSet<>();
		
		rootNodeSet.forEach(root->{
			V currentNode = root;
			while(this.dag.inDegreeOf(currentNode)==1) {//has single depending node
				E incomingEdge = this.dag.incomingEdgesOf(currentNode).iterator().next();
				//update
				currentNode = this.dag.getEdgeSource(incomingEdge);
			}
			
			//currentNode has two or more children/depending nodes;
			edgeSet.addAll(this.dag.incomingEdgesOf(currentNode));
		});
		
		//
		this.cloneDAG = (SimpleDirectedGraph<V, E>) this.dag.clone();
		
		//remove all source nodes of edges in E and all their directly and indirectly depending nodes from cloned DAG 
		Set<V> dependingNodeSetExcludedFromRootPaths = new HashSet<>();//set of node that are at the downstream of one or more edges in detected edgeSet thus cannot be on any root path;
		
		Set<V> newlyFoundDependingNodeSetInPreviousRound = new HashSet<>();
		
		Set<V> newlyFoundDependingNodeSet = new HashSet<>();
		edgeSet.forEach(e->{
			newlyFoundDependingNodeSet.add(this.dag.getEdgeSource(e));
		});
		
		do {
			//preprocess
			newlyFoundDependingNodeSetInPreviousRound.clear();
			newlyFoundDependingNodeSetInPreviousRound.addAll(newlyFoundDependingNodeSet);
			newlyFoundDependingNodeSet.clear();
			
			//find out all depending nodes of nodes in newlyFoundDependingNodeSetInPreviousRound
			//add them to newlyFoundDependingNodeSet if not in dependingNodeSetExcludedFromRootPaths
			
			newlyFoundDependingNodeSetInPreviousRound.forEach(v->{
				this.dag.incomingEdgesOf(v).forEach(e->{
					V dependingNode = this.dag.getEdgeSource(e);
					if(!dependingNodeSetExcludedFromRootPaths.contains(dependingNode))
						newlyFoundDependingNodeSet.add(dependingNode);
				});
			});
			
			dependingNodeSetExcludedFromRootPaths.addAll(newlyFoundDependingNodeSetInPreviousRound);
		}while(!newlyFoundDependingNodeSet.isEmpty());
		
		
		//
		this.cloneDAG.removeAllVertices(dependingNodeSetExcludedFromRootPaths);
		
	}
	/**
	 * detect all RootPaths and build the 
	 * {@link #rootPathSet} and {@link #nodeAssignedRootPathSetMap};
	 */
	private void detectRootPaths() {
		Set<V> rootNodeSet = new HashSet<>();
		this.cloneDAG.vertexSet().forEach(v->{
			if(this.cloneDAG.outDegreeOf(v)==0)
				rootNodeSet.add(v);
		});
		
		this.rootPathSet = new HashSet<>();
		this.nodeAssignedRootPathSetMap = new HashMap<>();
		rootNodeSet.forEach(root->{
			List<V> rootPathNodeList = new ArrayList<>();
			
			V currentNode = root;
			rootPathNodeList.add(currentNode);
			
			while(this.cloneDAG.inDegreeOf(currentNode)==1) {//has single depending node
				E incomingEdge = this.cloneDAG.incomingEdgesOf(currentNode).iterator().next();
				//update
				currentNode = this.cloneDAG.getEdgeSource(incomingEdge);
				rootPathNodeList.add(currentNode);
			}
			
			RootPath<V> rootPath = new RootPath<>(rootPathNodeList);
			this.rootPathSet.add(rootPath);
			
			rootPathNodeList.forEach(node->{
				if(!this.nodeAssignedRootPathSetMap.containsKey(node))
					this.nodeAssignedRootPathSetMap.put(node, new HashSet<>());
				
				this.nodeAssignedRootPathSetMap.get(node).add(rootPath);
			});
		});
	}
	

	/**
	 * @return the nodeAssignedRootPathSetMap
	 */
	public Map<V, Set<RootPath<V>>> getNodeAssignedRootPathSetMap() {
		return nodeAssignedRootPathSetMap;
	}

	//////////////////////////////
	/**
	 * select the given node in the solution set;
	 * 
	 * if the node is not on any RootPath, throw exception;
	 * 
	 * only node with all RootPaths containing it not in {@link #rootPathSetWithOneNodeSelected} can be selected!!!!
	 * 
	 * if one or more of the RootPaths the given node is on are in {@link #rootPathSetWithOneNodeSelected}, 
	 * 		throw exception;
	 * else, 
	 * 		add the node to {@link #currentlySelectedNodeSet}
	 * 		add all those RootPaths to {@link #rootPathSetWithOneNodeSelected}
	 * 		remove all those RootPaths from {@link #unselectedRootPathSet}
	 * 
	 * @param node
	 */
	public void selectNode(V node) {
		if(!this.nodeAssignedRootPathSetMap.containsKey(node))
			throw new VisframeException("given node is not on any RootPath!");
		
		
		this.nodeAssignedRootPathSetMap.get(node).forEach(rp->{
			if(this.rootPathSetWithOneNodeSelected.contains(rp))
				throw new VisframeException("at least one RootPath the given node is on has been selected!");
		});
		
		this.currentlySelectedNodeSet.add(node);
		this.rootPathSetWithOneNodeSelected.addAll(this.nodeAssignedRootPathSetMap.get(node));
		this.unselectedRootPathSet.removeAll(this.nodeAssignedRootPathSetMap.get(node));
	}
	
	/**
	 * de-select the given selected node;
	 * 
	 * 
	 * if the given node is not in {@link #currentlySelectedNodeSet}, throw exception;
	 * 
	 * remove the node from {@link #currentlySelectedNodeSet}
	 * for all RootPaths the given node is on, 
	 * 		remove them from {@link #rootPathSetWithOneNodeSelected};
	 * 		add them to {@link #unselectedRootPathSet};
	 * 
	 * @param node
	 */
	public void deselectNode(V node) {
		if(!this.currentlySelectedNodeSet.contains(node))
			throw new VisframeException("given node is not selected yet!");
		
		this.currentlySelectedNodeSet.remove(node);
		this.rootPathSetWithOneNodeSelected.removeAll(this.nodeAssignedRootPathSetMap.get(node));
		this.unselectedRootPathSet.addAll(this.nodeAssignedRootPathSetMap.get(node));
	}
	
	/**
	 * clear all selected nodes
	 * 
	 * clear {@link #currentlySelectedNodeSet}
	 * 
	 * add all RootPath in {@link #rootPathSetWithOneNodeSelected} to {@link #unselectedRootPathSet};
	 * clear {@link #rootPathSetWithOneNodeSelected};
	 */
	public void clearAllSelectedNodes() {
		this.currentlySelectedNodeSet.clear();
		this.unselectedRootPathSet.addAll(this.rootPathSetWithOneNodeSelected);
		this.rootPathSetWithOneNodeSelected.clear();
	}
	
	
	/**
	 * return whether or not the given node is represented;
	 * 
	 * specifically, return true if at least one RootPath the given node is on is in the {@link #rootPathSetWithOneNodeSelected}
	 * 
	 * otherwise, return false;
	 * @param node
	 * @return
	 */
	public boolean nodeIsRepresented(V node) {
		if(!this.nodeAssignedRootPathSetMap.containsKey(node))
			throw new VisframeException("given node is not on any RootPath!");
		
		for(RootPath<V> rp:this.nodeAssignedRootPathSetMap.get(node)){
			if(this.rootPathSetWithOneNodeSelected.contains(rp))
				return true;
		}
		
		return false;
	}
	
	//////////////////////////////////
	/**
	 * check and return if all root paths have one node been selected;
	 * 
	 * @return
	 */
	public boolean allRootPathsHaveOneNodeSelected() {
		return this.rootPathSet.size()==this.rootPathSetWithOneNodeSelected.size();
	}
	
	/**
	 * return the set of selected nodes that forms the complete solution set;
	 * 
	 * if {@link #allRootPathsHaveOneNodeSelected()} return false, throw exception;
	 * 
	 * return {@link #currentlySelectedNodeSet};
	 * @return
	 */
	public Set<V> getSelectedSolutionSet(){
		if(!this.allRootPathsHaveOneNodeSelected())
			throw new VisframeException("cannot build solution set when not all RootPaths have been selected!");
		
		return this.currentlySelectedNodeSet;
	}
	

	/**
	 * @return the rootPathSet
	 */
	public Set<RootPath<V>> getRootPathSet() {
		return rootPathSet;
	}


	/**
	 * @return the currentlySelectedNodeSet
	 */
	public Set<V> getCurrentlySelectedNodeSet() {
		return currentlySelectedNodeSet;
	}


	/**
	 * @return the rootPathSetWithOneNodeSelected
	 */
	public Set<RootPath<V>> getRootPathSetWithOneNodeSelected() {
		return rootPathSetWithOneNodeSelected;
	}


	////////////////////////
	/**
	 * =====120220-update
	 * a path on the DAG starting from a root node R (with no depended node/outgoing degree = 0) to the first descendant node D such that
	 * 1. all nodes on the path except for the last one have only one child node AND
	 * 		the last node can have multiple children nodes;
	 * 2. all nodes on the path does not have any directly or indirectly depended node with two or more children nodes;
	 * 
	 * a RootPath should contain at least one single node;
	 * a node can be shared by multiple RootPath on a DAG;
	 * 
	 * note that this is different from the definition that
	 * D is the first descendant node with two or more children nodes!!!!!!!!!!
	 * 
	 * on how to detect all RootPaths on a DAG, see
	 * {@link DAGSolutionSetSelector#preprocess()} and {@link DAGSolutionSetSelector#detectRootPaths()} methods;
	 * 
	 * @author tanxu
	 *
	 * @param <V>
	 */
	public static class RootPath<V> {
		/**
		 * the list of nodes on this RootPath;
		 * the first node is the root node, with no depended nodes (no outgoing edges) on the owner DAG;
		 * 
		 * the last node is the most recent descendant node;
		 */
		private final List<V> nodeList;
		
		/**
		 * 
		 * @param nodeList
		 */
		RootPath(List<V> nodeList){
			if(nodeList==null || nodeList.isEmpty())
				throw new IllegalArgumentException("given nodeList cannot be null or empty!");
					
			
			this.nodeList = nodeList;
		}
		
		/**
		 * @return the nodeList
		 */
		public List<V> getNodeList() {
			return nodeList;
		}
		
		public V getDescendantNode() {
			return this.getNodeList().get(this.getNodeList().size()-1);
		}
		
		public V getRootNode() {
			return this.getNodeList().get(0);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((nodeList == null) ? 0 : nodeList.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof RootPath))
				return false;
			@SuppressWarnings("unchecked")
			RootPath<V> other = (RootPath<V>) obj;
			if (nodeList == null) {
				if (other.nodeList != null)
					return false;
			} else if (!nodeList.equals(other.nodeList))
				return false;
			return true;
		}
	}
}
