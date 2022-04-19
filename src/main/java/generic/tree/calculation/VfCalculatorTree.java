package generic.tree.calculation;

import java.util.HashMap;
import java.util.Map;
import generic.tree.VfTree;
import generic.tree.VfTreeNode;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;

/**
 * given an input vftree, build a new tree with all information based on recursion calculated including:
 * 
 * 1. leafIndex
 * 2. edgeNumToRoot
 * 3. distToRoot
 * 
 * this tree can facilitate tree populating and simple tree visualization for trimming related interactive selection;
 * 
 * @author tanxu
 */
public class VfCalculatorTree implements VfTree{
	private final VfTree inputTree;
	
	//////////////
	private VfCalculatorTreeNode root;
	private Map<Integer, VfCalculatorTreeNode> nodeIDMap;
	
	/**
	 * constructor
	 * @param inputTree
	 */
	public VfCalculatorTree(VfTree inputTree){
		
		this.inputTree = inputTree;
		
		/////
		this.makePopulatorTree();
		
		this.calculate();
	}
	
	
	/**
	 * create the populator tree with the given VfTree
	 */
	private void makePopulatorTree() {
		nodeIDMap = new HashMap<>();
		//create a VfPopulatorTreeNode for each node on the input tree and find out the root node;
		for(VfTreeNode inputTreeNode:this.inputTree.getNodeIDMap().values()) {
			this.nodeIDMap.put(inputTreeNode.getID(), new VfCalculatorTreeNode(inputTreeNode));
			
			if(inputTreeNode.getParentNodeID()==null) {
				this.root = this.nodeIDMap.get(inputTreeNode.getID());
			}
		}
		
		//add each node to its parent node's children node set; also set parent node
		for(VfCalculatorTreeNode node:this.nodeIDMap.values()) {
			if(node.getParentNodeID()!=null) {
				this.nodeIDMap.get(node.getParentNodeID()).addChildNode(node);
				
				node.setParentNode(this.nodeIDMap.get(node.getParentNodeID()));
			}
		}
	}
	
	/**
	 * trigger recursive calculation of leaf index, distance to root, edge number to root for each node starting from root on the tree;
	 */
	private void calculate() {
		this.root.getLeafIndex();
		this.root.getDistanceToRootNode();
		this.root.getEdgeNumToRoot();
	}
	
	
	/**
	 * whether there is at least one non-root node on the tree with null valued distance to parent node;
	 * @return
	 */
	public boolean containsNullValuedBranchLength() {
		for(VfCalculatorTreeNode node: this.getNodeIDMap().values()) {
			if(node.getParentNodeID()!=null&&node.getDistanceToParentNode()==null) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * find out and return the number of edges of longest path from root to a leaf in terms of edge number;
	 * @return
	 */
	public int getLongestEdgeNumBetweenRootAndLeaf() {
		int ret = 0;
		
		for(VfCalculatorTreeNode node: this.getNodeIDMap().values()) {
			if(node.getEdgeNumToRoot()>ret) {
				ret = node.getEdgeNumToRoot();
			}
		}
		
		return ret;
		
	}
	
	/**
	 * find out and return the distance of the longest path between root and any leaf;
	 * only applicable when there is no edge with null valued length!
	 * @return
	 */
	public double getLongestDistBetweenRootAndLeaf() {
		if(this.containsNullValuedBranchLength()) {
			throw new UnsupportedOperationException("cannot find out longest distance between root and leaf when there exists edge with null valued length!");
		}
		
		double ret = 0;
		
		for(VfCalculatorTreeNode node: this.getNodeIDMap().values()) {
			if(node.getDistanceToRootNode()>ret) {
				ret = node.getDistanceToRootNode();
			}
		}
		
		return ret;
		
	}
	
	public int getLeafNum() {
		int ret=0;
		
		for(VfCalculatorTreeNode node: this.getNodeIDMap().values()) {
			if(node.isLeaf()) {
				ret++;
			}
		}
		
		return ret;
	}
	
	
	
	//////////////////////////////////////
	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, VfCalculatorTreeNode> getNodeIDMap(){
		return (Map<Integer, VfCalculatorTreeNode>) this.getRootNode().getDescendantNodeIDMap();
	}
	
	@Override
	public VfCalculatorTreeNode getRootNode() {
		return this.root;
	}
	
	
	@Override
	public Map<DataTableColumnName, DataTableColumn> getNonMandatoryAdditionalNodeFeatureColumnNameMap() {
		return this.inputTree.getNonMandatoryAdditionalNodeFeatureColumnNameMap();
	}

	@Override
	public Map<DataTableColumnName, DataTableColumn> getNonMandatoryAdditionalEdgeFeatureColumnNameMap() {
		return this.inputTree.getNonMandatoryAdditionalEdgeFeatureColumnNameMap();
	}
	
	
	
}
