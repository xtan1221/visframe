package generic.tree.trim;

import java.util.HashMap;
import java.util.Map;

import generic.tree.VfTree;
import generic.tree.VfTreeNode;


/**
 * trim the input VfTree to generate an output VfTrimTree
 * input VfTree will not be modified during the process
 * @author tanxu
 *
 */
public abstract class AbstractVfTreeTrimmer {
	private final VfTree inputTree;  //input tree is cloned into clonedNodeIDMapFromInputVfTree
	
	
	/////////////////////
	//cloned nodes from input VfTree to carry out the trimming and make the output VfTrimTree
	private VfTrimTreeNode clonedRootNodeFromInputVfTree;
	
	private Map<Integer, VfTrimTreeNode> clonedNodeIDMapFromInputVfTree;

	
	/**
	 * tree resulted from the trimming of the input tree
	 */
	protected VfTrimTree outputTree; 
	
	
	/**
	 * constructor
	 * @param inputTree
	 */
	protected AbstractVfTreeTrimmer(VfTree inputTree){
		this.inputTree = inputTree;
		
//		this.cloneTreeNodes();
	}
	
	
	
	protected VfTree getInputTree() {
		return this.inputTree;
	}
	
	
	
	public VfTrimTree getOutputTree() {
		return outputTree;
	}
	

	/**
	 * create a VfTrimTreeNode for each of the node on the input VfTree;
	 * this is a pre-processing step for all types of tree trimming operations
	 */
	protected void cloneTreeNodes() {
		this.clonedNodeIDMapFromInputVfTree = new HashMap<>();
		
		for(VfTreeNode inputTreeNode:this.getInputTree().getNodeIDMap().values()){
			this.clonedNodeIDMapFromInputVfTree.put(inputTreeNode.getID(),new VfTrimTreeNode(inputTreeNode));
			//set the cloned root node
			if(inputTreeNode.getParentNodeID()==null) {
				this.clonedRootNodeFromInputVfTree = this.clonedNodeIDMapFromInputVfTree.get(inputTreeNode.getID());
			}
		}
	}

	
	/**
	 * @return the clonedRootNodeFromInputVfTree
	 */
	protected VfTrimTreeNode getClonedRootNodeFromInputVfTree() {
		return clonedRootNodeFromInputVfTree;
	}
	
	/**
	 * @return the clonedNodeIDMapFromInputVfTree
	 */
	protected Map<Integer, VfTrimTreeNode> getClonedNodeIDMapFromInputVfTree() {
		return clonedNodeIDMapFromInputVfTree;
	}
	
	////////////////////////
	/**
	 * peform the trimming operation on the cloned clonedRootNodeFromInputVfTree
	 */
	public abstract void perform();
}
