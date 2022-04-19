package generic.tree.trim.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import generic.tree.VfTree;
import generic.tree.trim.AbstractVfTreeTrimmer;
import generic.tree.trim.VfTrimTree;
import generic.tree.trim.VfTrimTreeNode;
import rdb.table.data.DataTableColumnName;

/**
 * create a new tree that is a subtree of the input VfTree rooted on an existing non-root node of the input tree;
 * @author tanxu
 *
 */
public class VfSubTreeOnExistingNodeTrimmer extends AbstractVfTreeTrimmer {
	
	private final int newRootNodeID;
	//////////
	private Set<Integer> descendantNodeIDSet; //node id set of all descendant nodes of the new root node on the input tree;
	private Map<Integer, VfTrimTreeNode> clonedNewRootDescendantNodeIDMap;
	private VfTrimTreeNode newRootNode;
	
	/**
	 * constructor
	 * @param inputTree
	 * @param newRootNodeID
	 */
	public VfSubTreeOnExistingNodeTrimmer(VfTree inputTree,int newRootNodeID) {
		super(inputTree);
		
		if(inputTree.getNodeIDMap().get(newRootNodeID).getParentNodeID()==null) {
			throw new IllegalArgumentException("given new root node ID is the same with the current root node id;");
		}
		
		this.newRootNodeID = newRootNodeID;
		
		this.perform();
	}
	
	
	/**
	 * 1. build the set<Integer> descendantNodeIDSet for ID of all descendant nodes of the new root node on the input tree;
	 * 2. build the Map<Integer, VfTrimTreeNode> clonedNewRootDescendantNodeIDMap from Map<Integer, VfTrimTreeNode> clonedNodeIDMapFromInputVfTree with the descendantNodeIDSet
	 * 3. modify the VfTrimTreeNode of the new root node 
	 * 4. create output VfTrimTree with the clonedNewRootDescendantNodeIDMap and the modified new root node;
	 */
	@Override
	public void perform() {
		this.cloneTreeNodes();
		
		this.descendantNodeIDSet = this.getInputTree().getNodeIDMap().get(this.newRootNodeID).getDescendantNodeIDMap().keySet();
		
		this.clonedNewRootDescendantNodeIDMap = new HashMap<>();
		
		for(Integer desendantNodeID:this.descendantNodeIDSet) {
			this.clonedNewRootDescendantNodeIDMap.put(desendantNodeID, this.getClonedNodeIDMapFromInputVfTree().get(desendantNodeID));
		}
		
		this.newRootNode = this.getClonedNodeIDMapFromInputVfTree().get(this.newRootNodeID);
		
		//modify the new root node
		Map<DataTableColumnName, String> nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap = new HashMap<>();
		for(DataTableColumnName colName:this.getInputTree().getNonMandatoryAdditionalEdgeFeatureColumnNameMap().keySet()) {
			nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap.put(colName, null);
		}
		this.newRootNode.setParentNodeID(null);
		this.newRootNode.setSiblingOrderIndex(0);
		this.newRootNode.setDistanceToParentNode(0d); //null since there is no parent node?
		this.newRootNode.setBootstrapValueToParentNode(null);
		this.newRootNode.setNonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap(nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap);
		
		//
		this.outputTree = new VfTrimTree(
				this.newRootNode, this.getClonedNodeIDMapFromInputVfTree(),
				this.getInputTree().getNonMandatoryAdditionalNodeFeatureColumnNameMap(),
				this.getInputTree().getNonMandatoryAdditionalEdgeFeatureColumnNameMap());
		
		this.outputTree.buildChildrenNodeSiblingOrderIndexMap();
	}
	


	
}
