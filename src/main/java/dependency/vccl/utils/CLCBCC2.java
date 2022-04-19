package dependency.vccl.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import dependency.vccl.utils.helper.CopyLink;
import dependency.vccl.utils.helper.DependedNodeUnlinkedDependingCopySet;
import utils.Pair;

/**
 * class for a copy link constrained biconnected component (CLCBCC)
 * 		note that biconnected component also called maximal biconnected subgraph;
 * 
 * also provide key utility methods to facilitate building copy links during VisScheme applying since copy links between VCDNodes in the same MMCC are constrained as a whole;
 * 
 * =====================
 * there are two properties for a CLCBCC
 * 1. biconnected component
 * 		no cut vertex (thus no cut edge!!!!)
 * 		it is trivial to prove that 
 * 			no cut vertex ==> no cut edge, but not vice versa!!!!!!
 * 2. corresponding subgraph in the vcd graph must have at least three levels assigned by {@link DAGNodeLevelAssigner};
 * 
 * @author tanxu
 * 
 */
public final class CLCBCC2<V,E> {
	/**
	 * the target DAG graph on which this {@link CLCBCC2} is detected
	 */
	private final CLCBCCDetector2<V,E>  detector;
	/**
	 * the set of vertex of the {@link #targetDAG} that are belonging to this {@link CLCBCC2}
	 */
	private final Set<V> assignedNodeSet;
	private final Set<E> assignedEdgeSet;
	
	
	/**
	 * constructor
	 * @param vcdSubgraph
	 */
	public CLCBCC2(
			CLCBCCDetector2<V,E>  detector,
			Set<V> assignedNodeSet,
			Set<E> assignedEdgeSet
			){
		
		
		this.detector = detector;
		this.assignedNodeSet = assignedNodeSet;
		this.assignedEdgeSet = assignedEdgeSet;
	}
	
	
	/**
	 * find out index of all linkable Node copies of all depended nodes in this {@link CLCBCC2} of the given NodeCopy
	 * 
	 * @param vscopy
	 * @return
	 */
	public Map<V, Set<NodeCopy<V>>> getDependedNodeLinkableNodeCopySetMap(NodeCopy<V> dependingNodeCopy){
		Map<V, Set<NodeCopy<V>>> ret = new HashMap<>();
		V dependingNode = dependingNodeCopy.getNode();
		
		this.detector.getDAGNodeCopyLinkAssigner().getDag().outgoingEdgesOf(dependingNode).forEach(e->{
			if(this.assignedEdgeSet.contains(e)) {//the depended node is also assigned to this CLCBCC
				V dependedNode = this.detector.getDAGNodeCopyLinkAssigner().getDag().getEdgeTarget(e);
				
				if(dependingNodeCopy.getDependedNodeLinkedNodeCopyMap().get(dependedNode)!=null) {
					//nodeCopy is already linked to a copy of the depended node, skip all copies of the node
					//do nothing;
				}else {
					//there is no link from the dependingNodeCopy to any copy of the depended node
					//check every copy of the depended node;
					for(int copyIndex:this.detector.getDAGNodeCopyLinkAssigner().getNodeCopyIndexNodeCopyMapMap().get(dependedNode).keySet()) {
						NodeCopy<V> dependedNodeCopy = 
								this.detector.getDAGNodeCopyLinkAssigner().getNodeCopyIndexNodeCopyMapMap().get(dependedNode).get(copyIndex);
						
						if(checkIfLinkable2(dependingNodeCopy, dependedNodeCopy)) {
							if(!ret.containsKey(dependedNode))
								ret.put(dependedNode, new HashSet<>());
							////
							ret.get(dependedNode).add(dependedNodeCopy);
						}
					}
				}
			}else {
				//if the edge is not assigned to this CLCBCC, skip it
			}
		});
		
		return ret;
	}
	
	/**
	 * =====113020-this method has been tested and debugged!
	 * 
	 * check and return whether the two given node copies of two node assigned to this {@link CLCBCC2} can be linked;
	 * 
	 * 1. prepare
	 * 		1. build the current copy link graph with 
	 * 				{@link #buildCopyLinkGraphWithExistingLinks()}
	 * 
	 * 		2. add the copy link between the two given node copies to the copy link graph
	 * 
	 * 2. check if there exists at least one valid FULL copy link graph including all copies of all nodes assigned to this {@link CLCBCC};
	 * 
	 * 		1. build a {@link DependedNodeUnlinkedDependingCopySet} object with all pairs of depended node and depending node copy 
	 * 			so that the copy has no link to any copy of the depended node with method
	 * 				{@link #buildCurrentDependedNodeUnlinkedDependingCopySet()};	
	 * 		
	 * 		2. for each of the non-null copy link group returned by {@link DependedNodeUnlinkedDependingCopySet#nextCopylinkGroup()};
	 * 			1. create a clone of the copy link graph built in step 1
	 * 			2. add the copy link group to the cloned graph CG;
	 * 			3. for each leaf on CG that is not depended by any other copy;
	 * 				1. induce the subgraph SG on CG by iteratively adding all depended copies
	 * 					also build the set of owner node S of the added copies
	 * 				2. if there is an owner node with two copies added to SG
	 * 					the copy link group is invalid, go to the next copy link group;
	 * 				3. if after the SG is fully induced and no such owner node found
	 * 						a valid full copy link is found with existing copy links and the copy link between the two given copies;
	 * 						return TRUE
	 * 
	 * 3. after all copy link group returned by {@link DependedNodeUnlinkedDependingCopySet#nextCopylinkGroup()} are checked 
	 * 		if there is no valid full copy link graph found, the link between the two given copies is not allowed; 
	 * 		return FALSE;
	 * 
	 * @param dependingCopy
	 * @param dependedCopy
	 * @return
	 */
	private boolean checkIfLinkable2(NodeCopy<V> dependingNodeCopy, NodeCopy<V> dependedNodeCopy) {
		//1 prepare
		//1.1
		SimpleDirectedGraph<NodeCopy<V>, CopyLink<V>> currentCopyLinkGraph = this.buildCopyLinkGraphWithExistingLinks();
		//1.2
		currentCopyLinkGraph.addEdge(dependingNodeCopy, dependedNodeCopy, new CopyLink<>(dependingNodeCopy, dependedNodeCopy));
		
		//2
		//2.1
		DependedNodeUnlinkedDependingCopySet<V> dependedNodeUnlinkedDependingCopySet = this.buildCurrentDependedNodeUnlinkedDependingCopySet(new Pair<>(dependedNodeCopy.getNode(), dependingNodeCopy));
		
		Set<CopyLink<V>> nextCopyLinkGroup;
		//2.2
		outter: //outter loop that checks every copy link group 
			while((nextCopyLinkGroup=dependedNodeUnlinkedDependingCopySet.nextCopylinkGroup())!=null) {
				//2.2.1
				@SuppressWarnings("unchecked")
				SimpleDirectedGraph<NodeCopy<V>, CopyLink<V>> clone = (SimpleDirectedGraph<NodeCopy<V>, CopyLink<V>>)currentCopyLinkGraph.clone();
				
				//2.2.2
				nextCopyLinkGroup.forEach(link->{
					clone.addEdge(link.getDependingCopy(), link.getDependedCopy(), link);
				});
				
				
				//2.2.3
				Set<NodeCopy<V>> leafSet = new HashSet<>();
				clone.vertexSet().forEach(v->{
					if(clone.inDegreeOf(v)==0)
						leafSet.add(v);
				});
				
				for(NodeCopy<V> leaf:leafSet){
					//initialize
					Set<NodeCopy<V>> addedAndCheckedCopySet = new HashSet<>(); //added and depended copies checked
					Map<V,NodeCopy<V>> addedAndCheckedOwnerNodeCopyMap = new HashMap<>();//map from owner node to the copy in the addedAndCheckedCopySet
					
					//
					Set<NodeCopy<V>> newlyFoundUnCheckedCopySet = new HashSet<>();//added but depended copies not checked;
					newlyFoundUnCheckedCopySet.add(leaf);
					
					//check
					while(!newlyFoundUnCheckedCopySet.isEmpty()) {
						//the set of new found copies of current round;
						Set<NodeCopy<V>> newlyFoundUnCheckedCopySet2= new HashSet<>();
						
						for(NodeCopy<V> copy:newlyFoundUnCheckedCopySet){
							for(CopyLink<V> e:clone.outgoingEdgesOf(copy)){
								NodeCopy<V> dependedCopy = clone.getEdgeTarget(e);
								
								if(!addedAndCheckedCopySet.contains(dependedCopy)) {
									newlyFoundUnCheckedCopySet2.add(dependedCopy);
								}
							}
							
							//after all depended copies checked
							addedAndCheckedCopySet.add(copy);
							
							if(addedAndCheckedOwnerNodeCopyMap.containsKey(copy.getNode()) && 
									!addedAndCheckedOwnerNodeCopyMap.get(copy.getNode()).equals(copy)) {//owner node with another copy already added
								//for current copy link group, there is an invalid situation found; 
								//goto the next copy link group
								continue outter;
							}else {
								addedAndCheckedOwnerNodeCopyMap.put(copy.getNode(), copy);
							}
						}
						
						//update the newly found copy set
						newlyFoundUnCheckedCopySet = newlyFoundUnCheckedCopySet2;
					}
				}
				
				//this point can only be reached if for every leaf, its induced copy link graph does not contain multiple copies belonging to the same owner node
				//which implies the corresponding full copy link graph is valid;
				return true;
			}
		
		
		//this point can only be reached 
		//if none of the all possible full copy link graphs based on currently existing links and the link between the two given copies is valid;
		return false;
	}
	
	
	
	
	/**
	 * build and return a copy link graph with 
	 * 1. all copies of all nodes assigned to this {@link CLCBCC2}
	 * 2. all currently existing copy links;
	 * 
	 * @return
	 */
	private SimpleDirectedGraph<NodeCopy<V>, CopyLink<V>> buildCopyLinkGraphWithExistingLinks(){
		SimpleDirectedGraph<NodeCopy<V>, CopyLink<V>> ret = new SimpleDirectedGraph<>(CopyLink.class);
		
		//add copies of nodes assigned to this CLCBCC2
		this.getDetector().getDAGNodeCopyLinkAssigner().getNodeCopyIndexNodeCopyMapMap().forEach((node, map)->{
			if(this.assignedNodeSet.contains(node)) {
				map.forEach((copyIndex, copy)->{
					ret.addVertex(copy);
				});
			}
		});
		
		//add links between copies in the returned graph 
		this.getDetector().getDAGNodeCopyLinkAssigner().getNodeCopyIndexNodeCopyMapMap().forEach((node, map)->{
			if(this.assignedNodeSet.contains(node)) {
				map.forEach((copyIndex, copy)->{
					copy.getDependedNodeLinkedNodeCopyMap().forEach((dependedNode, dependedCopy)->{
						if(this.assignedNodeSet.contains(dependedNode) && dependedCopy!=null)
							ret.addEdge(copy, dependedCopy, new CopyLink<>(copy, dependedCopy));
					});
				});
			}
		});
		
		return ret;
	}
	
	
	/**
	 * only add nodes assigned to this CLCBCC2
	 * also does not include the given pair, which is corresponding to the two copies whose link is to be checked;
	 * 
	 * 1. find out all pairs of depended node and depending node copy so that the copy has no link to any copy of the depended node;
	 * 		
	 * 2. build and return a {@link DependedNodeUnlinkedDependingCopySet} object with the pairs and other related information;
	 * 
	 * 
	 * @return
	 */
	private DependedNodeUnlinkedDependingCopySet<V> buildCurrentDependedNodeUnlinkedDependingCopySet(Pair<V, NodeCopy<V>> dependedNodedependingNodeCopyPairToBeExcluded) {
		List<Pair<NodeCopy<V>, V>> dependingNodeUnlinkedCopyDependedNodePairList = new ArrayList<>();
		Map<V, Integer> dependedNodeCopyNumberMap = new HashMap<>();
		Map<V, Map<Integer,NodeCopy<V>>> dependedNodeCopyIndexMapMap = new HashMap<>();
		
		this.getDetector().getDAGNodeCopyLinkAssigner().getNodeCopyIndexNodeCopyMapMap().forEach((dependingNode, map)->{
			if(this.assignedNodeSet.contains(dependingNode)) {
				map.forEach((copyIndex, copy)->{
					this.getDetector().getDAGNodeCopyLinkAssigner().getDag().outgoingEdgesOf(dependingNode).forEach(e->{
						V dependedNode = this.getDetector().getDAGNodeCopyLinkAssigner().getDag().getEdgeTarget(e);
						
						if(this.assignedNodeSet.contains(dependedNode)) {
							if(copy.getDependedNodeLinkedNodeCopyMap().containsKey(dependedNode) && copy.getDependedNodeLinkedNodeCopyMap().get(dependedNode)!=null) {
								//skip if there is already a link
							}else {
								if(dependedNodedependingNodeCopyPairToBeExcluded.getFirst().equals(dependedNode) && 
										dependedNodedependingNodeCopyPairToBeExcluded.getSecond().equals(copy)) {
									//skip the given pair to be excluded;
								}else {
									dependingNodeUnlinkedCopyDependedNodePairList.add(new Pair<>(copy, dependedNode));
									if(!dependedNodeCopyNumberMap.containsKey(dependedNode)) {
										dependedNodeCopyNumberMap.put(
												dependedNode, 
												this.getDetector().getDAGNodeCopyLinkAssigner().getNodeCopyNumberMap().get(dependedNode));
										dependedNodeCopyIndexMapMap.put(
												dependedNode, 
												this.getDetector().getDAGNodeCopyLinkAssigner().getNodeCopyIndexNodeCopyMapMap().get(dependedNode));
									}
								}
							}
						}
					});
					
				});
			}
		});
		
		return new DependedNodeUnlinkedDependingCopySet<>(dependingNodeUnlinkedCopyDependedNodePairList,
				dependedNodeCopyNumberMap,
				dependedNodeCopyIndexMapMap
				);
	}
	
	
	////////////////////////////////////////
	/**
	 * @return the detector
	 */
	public CLCBCCDetector2<V, E> getDetector() {
		return detector;
	}

	/**
	 * @return the assignedNodeSet
	 */
	public Set<V> getAssignedNodeSet() {
		return assignedNodeSet;
	}

	/**
	 * @return the assignedEdgeSet
	 */
	public Set<E> getAssignedEdgeSet() {
		return assignedEdgeSet;
	}


	///////////////////////////////////
	//only need to include the assignedNodeSet and assignedEdgeSet
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignedEdgeSet == null) ? 0 : assignedEdgeSet.hashCode());
		result = prime * result + ((assignedNodeSet == null) ? 0 : assignedNodeSet.hashCode());
		return result;
	}


	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CLCBCC2))
			return false;
		CLCBCC2<V,E> other = (CLCBCC2<V,E>) obj;
		if (assignedEdgeSet == null) {
			if (other.assignedEdgeSet != null)
				return false;
		} else if (!assignedEdgeSet.equals(other.assignedEdgeSet))
			return false;
		if (assignedNodeSet == null) {
			if (other.assignedNodeSet != null)
				return false;
		} else if (!assignedNodeSet.equals(other.assignedNodeSet))
			return false;
		return true;
	}
	
}
