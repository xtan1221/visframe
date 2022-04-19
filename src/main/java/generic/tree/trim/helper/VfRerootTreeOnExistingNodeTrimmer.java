package generic.tree.trim.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import generic.tree.VfTree;
import generic.tree.VfTreeNode;
import generic.tree.VfTreeUtils;
import generic.tree.trim.AbstractVfTreeTrimmer;
import generic.tree.trim.VfTrimTree;
import generic.tree.trim.VfTrimTreeNode;

/**
 * reroot the tree with an existing non-root node on the tree;
 * 
 * @author tanxu
 * 
 */
public class VfRerootTreeOnExistingNodeTrimmer extends AbstractVfTreeTrimmer {
	private final int newRootNodeID;
	
	///////////////////////
	//all nodes that are on the path between the new root node and the old root node in the input tree(including the old root but excluding the new root node);
	private Set<Integer> nodeIDSetOfAncestralNodesOfNewRootNodeInInputTree;
	
	private VfTrimTreeNode newRootNode;
	
	/**
	 * constructor
	 * @param inputTree
	 * @param newRootNodeID
	 */
	public VfRerootTreeOnExistingNodeTrimmer(VfTree inputTree,int newRootNodeID) {
		super(inputTree);

		if(inputTree.getNodeIDMap().get(newRootNodeID).getParentNodeID()==null) {
			throw new IllegalArgumentException("given new root node ID is the same with the current root node id;");
		}
		
		this.newRootNodeID = newRootNodeID;
		
		this.perform();
	}
	

	/**
	 * 1. first find out the node id set of ancestral nodes of the new root node in the input tree;
	 * 2. for each node on the input tree, create a VfTrimTreeNode accordingly;
	 * 3. invoke the output VfTrimTree’s {@link #recalculateChildrenSiblingOrderIndex()} method
	 */
	@Override
	public void perform() {
		//1
		this.buildNodeIDSetOfAncestralNodesOfNewRootNodeInInputTree();
		
		
		//2
		Map<Integer, VfTrimTreeNode > nodeIDMap = new HashMap<>();
		
		for(int nodeID:this.getInputTree().getNodeIDMap().keySet()) {
			VfTreeNode node = this.getInputTree().getNodeIDMap().get(nodeID);
			VfTrimTreeNode newNode = new VfTrimTreeNode(nodeID, node.getNonMandatoryAdditionalNodeFeatureColumnNameValueStringMap());
			
			if(node.getID()==this.newRootNodeID) {//new root node
				newNode.setParentNodeID(null);
				newNode.setSiblingOrderIndex(0);
				newNode.setDistanceToParentNode(0d);
				newNode.setBootstrapValueToParentNode(null);//
				newNode.setNonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap(null);//empty set
				this.newRootNode = newNode;
				
			}else if(node.getID()==this.getInputTree().getNodeIDMap().get(this.newRootNodeID).getParentNodeID()) {//parent node of new root in the input tree
				newNode.setParentNodeID(this.newRootNodeID);
				//set the sibling order index to child node number (the last index)
				newNode.setSiblingOrderIndex(this.getInputTree().getNodeIDMap().get(this.newRootNodeID).getChildrenNodeSiblingOrderIndexMap().size()); //!!!!!NOT size()-1
				newNode.setDistanceToParentNode(this.getInputTree().getNodeIDMap().get(this.newRootNodeID).getDistanceToParentNode());
				newNode.setBootstrapValueToParentNode(this.getInputTree().getNodeIDMap().get(this.newRootNodeID).getBootstrapValueToParentNode());
				newNode.setNonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap(
						VfTreeUtils.cloneMap(
								this.getInputTree().getNodeIDMap().get(this.newRootNodeID).getNonMandatoryAdditionalFeatureColumnNameValueStringMapOfEdgeToParent())
						);
			}else if(this.nodeIDSetOfAncestralNodesOfNewRootNodeInInputTree.contains(node.getID())) {//non-parent ancestral nodes of the new root node in the input tree
				int childNodeID = findNodeIDOfChildNodeOfAncestralNode(node.getID());//id of child node of this node that is also ancestral node of the new root node on the input tree
				newNode.setParentNodeID(childNodeID);
				newNode.setSiblingOrderIndex(this.getInputTree().getNodeIDMap().get(childNodeID).getChildrenNodeSiblingOrderIndexMap().size());//set as the last index(note here set to size() rather than size()-1 to guarantee last index, but should be reset afterwards)
				newNode.setDistanceToParentNode(this.getInputTree().getNodeIDMap().get(childNodeID).getDistanceToParentNode());
				newNode.setBootstrapValueToParentNode(this.getInputTree().getNodeIDMap().get(childNodeID).getBootstrapValueToParentNode());
				newNode.setNonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap(
						VfTreeUtils.cloneMap(
								this.getInputTree().getNodeIDMap().get(childNodeID).getNonMandatoryAdditionalFeatureColumnNameValueStringMapOfEdgeToParent())
						);
			}else if(this.nodeIDSetOfAncestralNodesOfNewRootNodeInInputTree.contains(node.getParentNodeID())) {//child node of ancestral nodes of new root node in the input tree
				//no change at this step; but the sibling order index need to be reset
				newNode.setParentNodeID(node.getParentNodeID());
				newNode.setSiblingOrderIndex(node.getSiblingOrderIndex());
				newNode.setDistanceToParentNode(node.getDistanceToParentNode());
				newNode.setBootstrapValueToParentNode(node.getBootstrapValueToParentNode());//
				newNode.setNonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap(node.getNonMandatoryAdditionalFeatureColumnNameValueStringMapOfEdgeToParent());//empty set
				
			}else {//other nodes including descendant nodes of the new root node in the input tree
				//no change
				newNode.setParentNodeID(node.getParentNodeID());
				newNode.setSiblingOrderIndex(node.getSiblingOrderIndex());
				newNode.setDistanceToParentNode(node.getDistanceToParentNode());
				newNode.setBootstrapValueToParentNode(node.getBootstrapValueToParentNode());//
				newNode.setNonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap(node.getNonMandatoryAdditionalFeatureColumnNameValueStringMapOfEdgeToParent());//empty set
			}
			
			nodeIDMap.put(newNode.getID(), newNode);
		}
		
		this.outputTree = new VfTrimTree(this.newRootNode, nodeIDMap, 
				this.getInputTree().getNonMandatoryAdditionalNodeFeatureColumnNameMap(), 
				this.getInputTree().getNonMandatoryAdditionalEdgeFeatureColumnNameMap());
		
		//3
		//this will reassign sibling order index to each node so that the sibling nodes of a node always have order index as 0,1,2,3,...
		this.outputTree.buildChildrenNodeSiblingOrderIndexMap();
		this.outputTree.recalculateChildrenSiblingOrderIndex();
	}
	
	/**
	 * find out the ID of the child node of a non-parent ancestral node of the new root node in the input tree such that the child node is also an ancestral node
	 * of the new root node in the input tree;
	 * @param ancestralNode
	 * @return
	 */
	private int findNodeIDOfChildNodeOfAncestralNode(int ancestralNodeID) {
		for(VfTreeNode childNode: this.getInputTree().getNodeIDMap().get(ancestralNodeID).getChildrenNodeSiblingOrderIndexMap().values()) {
			if(this.nodeIDSetOfAncestralNodesOfNewRootNodeInInputTree.contains(childNode.getID())) {
				return childNode.getID();
			}
		}
		
		throw new UnsupportedOperationException();
	}
	
	/**
	 * find out the node id set of all ancestral nodes of the new root node in the input tree
	 */
	protected void buildNodeIDSetOfAncestralNodesOfNewRootNodeInInputTree() {
		this.nodeIDSetOfAncestralNodesOfNewRootNodeInInputTree = new HashSet<>();
		Integer currentAncestralNodeID = this.getInputTree().getNodeIDMap().get(this.newRootNodeID).getParentNodeID();//initialize with the parent node id of the new root node in the input tree
		while(currentAncestralNodeID!=null) {
			this.nodeIDSetOfAncestralNodesOfNewRootNodeInInputTree.add(currentAncestralNodeID);
			currentAncestralNodeID = this.getInputTree().getNodeIDMap().get(currentAncestralNodeID).getParentNodeID();
		}
	}
	
}
