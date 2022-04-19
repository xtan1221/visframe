package generic.tree.trim;

import java.util.HashMap;
import java.util.Map;

import generic.tree.VfTreeNode;
import generic.tree.VfTreeUtils;
import rdb.table.data.DataTableColumnName;

/**
 * vf tree node for VfTrimTree whose features can be reset due to tree trimming (except for the ID and moreAdditionalNodeFeatureColumnNameValueStringMap)
 * 
 * 
 * @author tanxu
 */
public class VfTrimTreeNode implements VfTreeNode{
	private final int ID;
	private final Map<DataTableColumnName, String> nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap;
	
	//
	private Integer parentNodeID;
	private int siblingOrderIndex;
	private Map<DataTableColumnName, String> nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap;
	private Double distToParentNode;
	private Integer bootstrap;
	
	private Map<Integer, VfTrimTreeNode > childrenNodeSiblingOrderIndexMap;
	
	/**
	 * constructor
	 * @param ID
	 * @param nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap
	 */
	public VfTrimTreeNode(int ID, Map<DataTableColumnName, String> nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap){
		this.ID = ID;
		this.nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap = nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap;
		this.childrenNodeSiblingOrderIndexMap = new HashMap<>();
	}
	
	
	/**
	 * build a VfTrimTreeNode with the full set of information from the given VfTreeNode
	 * @param node
	 */
	public VfTrimTreeNode(VfTreeNode node){
		this.ID = node.getID();
		this.nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap = VfTreeUtils.cloneMap(
				node.getNonMandatoryAdditionalNodeFeatureColumnNameValueStringMap());
		
		this.parentNodeID = node.getParentNodeID();
		this.siblingOrderIndex = node.getSiblingOrderIndex();
		
		this.nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap = //for root node, this map is null
					VfTreeUtils.cloneMap(node.getNonMandatoryAdditionalFeatureColumnNameValueStringMapOfEdgeToParent());
		
		this.distToParentNode = node.getDistanceToParentNode();
		this.bootstrap = node.getBootstrapValueToParentNode();
		
		//initialized but not populated
		this.childrenNodeSiblingOrderIndexMap = new HashMap<>();
	}
	
	
	////setter methods to set other primary features 
	public void setParentNodeID(Integer parentNodeID) {
		this.parentNodeID = parentNodeID;
	}
	public void setSiblingOrderIndex(int siblingOrderIndex) {
		this.siblingOrderIndex = siblingOrderIndex;
	}
	public void setDistanceToParentNode(Double dist) {
		this.distToParentNode = dist;
	}
	public void setBootstrapValueToParentNode(Integer bootstrap) {
		this.bootstrap = bootstrap;
	}
	public void setNonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap(Map<DataTableColumnName, String> nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap) {
		this.nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap = nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap;
	}
	
	
	
	///
	void addChildNode(VfTrimTreeNode childNode) {
		this.childrenNodeSiblingOrderIndexMap.put(childNode.getSiblingOrderIndex(), childNode);
	}

	
	public void clearChildrenNodeSiblingOrderIndexMap() {
		this.childrenNodeSiblingOrderIndexMap.clear();
	}
	
	
	
	//////////////////////////////////////
	@Override
	public Map<Integer, VfTrimTreeNode> getChildrenNodeSiblingOrderIndexMap() {
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
		return this.distToParentNode;
	}

	@Override
	public Integer getBootstrapValueToParentNode() {
		return this.bootstrap;
	}

	@Override
	public Map<DataTableColumnName, String> getNonMandatoryAdditionalFeatureColumnNameValueStringMapOfEdgeToParent() {
		return this.nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap;
	}

}
