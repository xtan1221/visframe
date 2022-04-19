package generic.tree.calculation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import generic.tree.VfTreeNode;
import generic.tree.VfTreeUtils;
import rdb.table.data.DataTableColumnName;


/**
 * tree node of VfCalculatorTree;
 * 
 * contains the full set of features including those based on recursion;
 * 
 * @author tanxu
 *
 */
public class VfCalculatorTreeNode implements VfTreeNode{
	private static Double LEAF_COUNTER;
	
	//////
	private final int ID;
	private final Integer parentNodeID;
	private final int siblingOrderIndex;
	private final Map<DataTableColumnName, String> nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap;
	private final Double distanceToParentNode;
	private final Integer bootstrapValueToParentNode;
	private final Map<DataTableColumnName, String> nonMandatoryAdditionalFeatureColumnNameValueStringMapOfEdgeToParent;
	
	/////
	private Map<Integer, VfCalculatorTreeNode> childrenNodeSiblingOrderIndexMap;
	private List<VfCalculatorTreeNode> childrenNodeListOrderedBySiblingOrder;
	private VfCalculatorTreeNode parentNode = null;
	
	private Double leafIndex;
	private Integer edgeNumToRoot;
	private boolean distToRootCalculated = false;
	private Double distToRoot;
	
	/**
	 * construct a VfPopulatorTreeNode by copying information from the given VfTreeNode object
	 * @param node
	 */
	protected VfCalculatorTreeNode(VfTreeNode node){
		this.ID = node.getID();
		this.parentNodeID = node.getParentNodeID();
		this.siblingOrderIndex = node.getSiblingOrderIndex();
		this.nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap = VfTreeUtils.cloneMap(node.getNonMandatoryAdditionalNodeFeatureColumnNameValueStringMap());//clone
		this.nonMandatoryAdditionalFeatureColumnNameValueStringMapOfEdgeToParent = VfTreeUtils.cloneMap(node.getNonMandatoryAdditionalFeatureColumnNameValueStringMapOfEdgeToParent());
		this.distanceToParentNode = node.getDistanceToParentNode();
		this.bootstrapValueToParentNode = node.getBootstrapValueToParentNode();
		this.childrenNodeSiblingOrderIndexMap=new HashMap<>();
	}
	
	/**
	 * build and return the list of children nodes ordered by silbing order
	 * @return
	 */
	private List<VfCalculatorTreeNode> getChildrenNodeListOrderedBySiblingOrder(){
		if(this.childrenNodeListOrderedBySiblingOrder==null) {
			this.childrenNodeListOrderedBySiblingOrder = new ArrayList<>();
			List<Integer> sortedSiblingOrderList = new ArrayList<>(this.childrenNodeSiblingOrderIndexMap.keySet());
			Collections.sort(sortedSiblingOrderList);
			
			for(int siblingOrder:sortedSiblingOrderList) {
				this.childrenNodeListOrderedBySiblingOrder.add(childrenNodeSiblingOrderIndexMap.get(siblingOrder));
			}
		}
		
		return this.childrenNodeListOrderedBySiblingOrder;
		
	}
	
	
	protected void setParentNode(VfCalculatorTreeNode node) {
		this.parentNode = node;
	}
	
	protected void addChildNode(VfCalculatorTreeNode node) {
		this.childrenNodeSiblingOrderIndexMap.put(node.getSiblingOrderIndex(), node);
	}
	
	/**
	 * returns whether this node is a leaf or not;
	 * @return
	 */
	public boolean isLeaf() {
		return this.getChildrenNodeSiblingOrderIndexMap().isEmpty();
	}
	
	/**
	 * returns the leaf index of this node;
	 * 
	 * for a leaf node, its leaf index is an unique integer assigned to it from 0,1,2, ... based on the relative position of the leaf on the tree;
	 * 
	 * for a non-leaf node, its leaf index value is the average of all of its children nodes' leafIndex;
	 * 
	 * @return
	 */
	public double getLeafIndex() {
		if(this.leafIndex==null) {//leaf index is not calculated yet for this node
			if(this.getParentNodeID() == null) {//this node is the root node; need to reset LEAF_COUNTER
				LEAF_COUNTER = 0d;
			}
			if(this.isLeaf()) {
				this.leafIndex = LEAF_COUNTER;
				LEAF_COUNTER++;
			}else {
				double childrenNodeLeafIndexSum = 0;
				for(VfCalculatorTreeNode child:this.getChildrenNodeListOrderedBySiblingOrder()) {
					childrenNodeLeafIndexSum=childrenNodeLeafIndexSum+child.getLeafIndex();//trigger the calculation of children nodes
				}
				
				this.leafIndex = childrenNodeLeafIndexSum/this.getChildrenNodeSiblingOrderIndexMap().size();
			}
		}
		
		return this.leafIndex;
	}
	

	/**
	 * returns the distance between this node and the root node;
	 * returns null if there is at least one edge on the path between the root and this node with null valued distance;
	 * @return
	 */
	public Double getDistanceToRootNode() {
		if(!this.distToRootCalculated) {
			if(this.getParentNodeID()==null) {//root node
				this.distToRoot = 0d;
			}else if(this.getDistanceToParentNode()==null||this.parentNode.getDistanceToRootNode()==null) {
				this.distToRoot = null;
			}else {
				this.distToRoot = this.getDistanceToParentNode()+this.parentNode.getDistanceToRootNode();
			}
			
			//must update indicator before calculate children nodes
			this.distToRootCalculated = true;
			
			//trigger calculation of child nodes
			for(VfCalculatorTreeNode child:this.getChildrenNodeListOrderedBySiblingOrder()) {
				child.getDistanceToRootNode();//
			}
			
			
		}
		
		return this.distToRoot;
	}
	
	
	/**
	 * returns number of edges on the path from this node to the root node;
	 * @return
	 */
	public int getEdgeNumToRoot() {
		if(this.edgeNumToRoot==null) {
			if(this.getParentNodeID()==null) {//root node
				this.edgeNumToRoot = 0;
			
			}else {
				this.edgeNumToRoot = this.parentNode.getEdgeNumToRoot()+1;
			}
			
			//trigger calculation of child nodes
			for(VfCalculatorTreeNode child:this.getChildrenNodeListOrderedBySiblingOrder()) {
				child.getEdgeNumToRoot();//
			}
			
		}
		
		return this.edgeNumToRoot;
	}


	
	///////////////////////////////////////
	
	
	@Override
	public Map<Integer, VfCalculatorTreeNode> getChildrenNodeSiblingOrderIndexMap() {
		return this.childrenNodeSiblingOrderIndexMap;
	}

	
	
	@Override
	public int getID() {
		return this.ID;
	}

	@Override
	public Integer getParentNodeID() {
		return this.parentNodeID;
	}

	@Override
	public int getSiblingOrderIndex() {
		return this.siblingOrderIndex;
	}

	@Override
	public Map<DataTableColumnName, String> getNonMandatoryAdditionalNodeFeatureColumnNameValueStringMap() {
		return this.nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap;
	}

	@Override
	public Double getDistanceToParentNode() {
		return this.distanceToParentNode;
	}

	@Override
	public Integer getBootstrapValueToParentNode() {
		return this.bootstrapValueToParentNode;
	}

	@Override
	public Map<DataTableColumnName, String> getNonMandatoryAdditionalFeatureColumnNameValueStringMapOfEdgeToParent() {
		return this.nonMandatoryAdditionalFeatureColumnNameValueStringMapOfEdgeToParent;
	}
}
