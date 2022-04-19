package generic.tree.reader.projectbased;

import java.util.HashMap;
import java.util.Map;

import generic.tree.VfTreeNode;
import rdb.table.data.DataTableColumnName;

/**
 * vf tree node for VfDataTree;
 * 
 * contains the full set of information read from the node and edge record data table;
 * 
 * facilitate simple tree visualization for tree trimming operation;
 * @author tanxu
 */
public class VfDataTreeNode implements VfTreeNode{
	//read from the node record data table
	private final int ID;
	private final Integer parentNodeID;
	private final int siblingOrderIndex;
	private final Double leafIndex;
	private final Integer edgeNumToRoot;
	private final Double distToRoot;
	private final Map<DataTableColumnName, String> nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap;
	
	
	////////////
	//read from the edge record data table
	private Map<DataTableColumnName, String> nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap;
	private Double distToParentNode;
	private Integer bootstrap;
	private Map<Integer, VfDataTreeNode> childrenNodeSiblingOrderIndexMap;//after all nodes on the tree are created, need to set this map for each node
	
	
	
	/**
	 * constructor
	 * 
	 * set the primary features parsed from the node data table;
	 * @param ID
	 * @param parentNodeID
	 * @param siblingOrderIndex
	 * @param nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap
	 */
	VfDataTreeNode(
			int ID, Integer parentNodeID, int siblingOrderIndex, 
			double leafIndex, int edgeNumToRoot, Double distToRoot,
			Map<DataTableColumnName, String> nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap){
		
		
		this.ID = ID;
		this.parentNodeID = parentNodeID;
		this.siblingOrderIndex = siblingOrderIndex;
		this.leafIndex = leafIndex;
		this.edgeNumToRoot = edgeNumToRoot;
		this.distToRoot = distToRoot;
		this.nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap = nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap;
		//
		this.childrenNodeSiblingOrderIndexMap = new HashMap<>();
	}
	
	
	/**
	 * @return the leafIndex
	 */
	public double getLeafIndex() {
		return leafIndex;
	}


	/**
	 * @return the edgeNumToRoot
	 */
	public int getEdgeNumToRoot() {
		return edgeNumToRoot;
	}

	/**
	 * @return the distToRoot
	 */
	public Double getDistToRoot() {
		return distToRoot;
	}


	////setter methods to set the primary features parsed from the edge data table;
	protected void setDistanceToParentNode(Double dist) {
		this.distToParentNode = dist;
	}
	
	protected void setBootstrapValueToParentNode(Integer bootstrap) {
		this.bootstrap = bootstrap;
	}
	
	protected void setNonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap(Map<DataTableColumnName, String> nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap) {
		this.nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap = nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap;
	}
	
	protected void addChildNode(VfDataTreeNode child) {
		this.childrenNodeSiblingOrderIndexMap.put(child.getSiblingOrderIndex(), child);
	}
	
	
	/**
	 * 
	 */
	@Override
	public Map<Integer, VfDataTreeNode> getChildrenNodeSiblingOrderIndexMap() {
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


	@Override
	public String toString() {
		return "VfDataTreeNode [ID=" + ID + ", parentNodeID=" + parentNodeID + ", siblingOrderIndex="
				+ siblingOrderIndex + ", leafIndex=" + leafIndex + ", edgeNumToRoot=" + edgeNumToRoot + ", distToRoot="
				+ distToRoot + ", nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap="
				+ nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap
				+ ", nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap="
				+ nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap + ", distToParentNode=" + distToParentNode
				+ ", bootstrap=" + bootstrap + ", childrenNodeSiblingOrderIndexMap=" + childrenNodeSiblingOrderIndexMap
				+ "]";
	}
	
	
	///////////////////
	
}
