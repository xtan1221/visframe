package generic.tree.trim.helper;

import java.util.HashMap;
import java.util.Map;

import generic.tree.VfTree;
import generic.tree.trim.AbstractVfTreeTrimmer;
import generic.tree.trim.VfTrimTree;
import generic.tree.trim.VfTrimTreeNode;
import rdb.table.data.DataTableColumnName;

/**
 * add a new node at the given location on the input tree;
 * 
 * this is a pre-processing step for other tree trimming operation; should not be public class
 * @author tanxu
 *
 */
public class VfTreeAddNodeTrimmer extends AbstractVfTreeTrimmer {
//	private final int parentNodeID; 
//	private final int childNodeID;
//	private final double newRootNodePos; //newRootNodePos must be (0,1); the smaller it is, the closer the new node is to the parent node
	
	private final PositionOnTree position;
	
	//////////
	private int newNodeID;
	
	/**
	 * constructor
	 * @param inputTree
	 * @param parentNodeID
	 * @param childNodeID
	 * @param newRootNodePos
	 */
	public VfTreeAddNodeTrimmer(
			VfTree inputTree,
			PositionOnTree position
			) {
		super(inputTree);
		if(position==null) {
			throw new IllegalArgumentException("given PositionOnTree cannot be null!");
		}
		
		if(position.getParentNodeID()==null||position.getPos()==null) {
			throw new IllegalArgumentException("given PositionOnTree's parent node ID and pos must be non-null!");
		}
		
		if(inputTree.getNodeIDMap().get(position.getChildNodeID()).getParentNodeID()!=position.getParentNodeID()) {
			throw new IllegalArgumentException("given parentNodeID and childNodeID is not a valid branch on the given input tree!");
		}
		
		
		this.position = position;
		
		this.perform();
	}
	
	
	/**
	 * 0. generated a new ID for the newly added node
	 * 
	 * 1. create the new node and add it to the output VfTrimTree;
	 * 2. modify the child node of the new node;
	 * 3. create the output VfTrimTree with the Map<Integer, VfTrimTreeNode> clonedNodeIDMapFromInputVfTree 
	 * 4. invoke the output VfTrimTree’s resetChildrenNodeSiblingOrderIndexMap() method
	 */
	@Override
	public void perform() {
		this.cloneTreeNodes();
		
		//0
		this.generatedNewNodeID();
		
		////non mandatory attributes values should all be null;
		Map<DataTableColumnName, String> nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap = new HashMap<>();
		for(DataTableColumnName colName:this.getInputTree().getNonMandatoryAdditionalNodeFeatureColumnNameMap().keySet()) {
			nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap.put(colName, null);
		}
		
		Map<DataTableColumnName, String> nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap = new HashMap<>();
		for(DataTableColumnName colName:this.getInputTree().getNonMandatoryAdditionalEdgeFeatureColumnNameMap().keySet()) {
			nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap.put(colName, null);
		}
		
		VfTrimTreeNode newNode = new VfTrimTreeNode(this.newNodeID, nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap);
		
		VfTrimTreeNode childNode = this.getClonedNodeIDMapFromInputVfTree().get(this.position.getChildNodeID());//the child node on the input tree of the new node
		
		//set other feature of new node
		newNode.setParentNodeID(this.position.getParentNodeID()); //
		newNode.setSiblingOrderIndex(childNode.getSiblingOrderIndex());//same with the child node 
		if(childNode.getDistanceToParentNode()!=null) {
			newNode.setDistanceToParentNode(childNode.getDistanceToParentNode()*this.position.getPos());//
		}else {
			newNode.setDistanceToParentNode(null);
		}
		newNode.setBootstrapValueToParentNode(null);//always null
		newNode.setNonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap(nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap); //always empty set
		
		//modify features of the child node of the new node
		childNode.setParentNodeID(this.newNodeID);
		childNode.setSiblingOrderIndex(0);//only child of the new node
		if(childNode.getDistanceToParentNode()!=null) {
			childNode.setDistanceToParentNode(childNode.getDistanceToParentNode()-newNode.getDistanceToParentNode());//update
		}
		childNode.setBootstrapValueToParentNode(null);//always to null
		childNode.setNonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap(nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap);//always empty - possible data lost
		
		
		//add the new node to the node id map
		this.getClonedNodeIDMapFromInputVfTree().put(newNode.getID(), newNode);
		
		//create the output tree
		this.outputTree = new VfTrimTree(
				this.getClonedRootNodeFromInputVfTree(), this.getClonedNodeIDMapFromInputVfTree(), 
				this.getInputTree().getNonMandatoryAdditionalNodeFeatureColumnNameMap(), this.getInputTree().getNonMandatoryAdditionalEdgeFeatureColumnNameMap());
		
		
		this.outputTree.buildChildrenNodeSiblingOrderIndexMap();
	}
	
	
	/**
	 * find out an integer as the id of the new node which is not taken by any of the existing node on the tree;
	 */
	private void generatedNewNodeID() {
		int i=0;
		while(this.getInputTree().getNodeIDMap().keySet().contains(i)) {
			i++;
		}
		
		this.newNodeID = i;
	}
	
	
	/**
	 * @return the newNodeID
	 */
	public int getNewNodeID() {
		return newNodeID;
	}

}
