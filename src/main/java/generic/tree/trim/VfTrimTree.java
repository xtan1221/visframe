package generic.tree.trim;

import java.util.Map;
import java.util.TreeMap;

import generic.tree.VfTree;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;

/**
 * VfTree result from and manipulated by a {@link AbstractVfTreeTrimmer}
 * 
 * @author tanxu
 *
 */
public class VfTrimTree implements VfTree {

	private final VfTrimTreeNode rootNode;
	private final Map<Integer, VfTrimTreeNode> nodeIDMap;

	private final Map<DataTableColumnName, DataTableColumn> nonMandatoryAdditionalNodeFeatureColumnNameMap;
	private final Map<DataTableColumnName, DataTableColumn> nonMandatoryAdditionalEdgeFeatureColumnNameMap;

	/**
	 * constructor
	 * 
	 * @param rootNode
	 * @param nodeIDMap
	 */
	public VfTrimTree(VfTrimTreeNode rootNode, Map<Integer, VfTrimTreeNode> nodeIDMap,
			Map<DataTableColumnName, DataTableColumn> nonMandatoryAdditionalNodeFeatureColumnNameMap,
			Map<DataTableColumnName, DataTableColumn> nonMandatoryAdditionalEdgeFeatureColumnNameMap) {

		this.rootNode = rootNode;
		this.nodeIDMap = nodeIDMap;
		this.nonMandatoryAdditionalNodeFeatureColumnNameMap = nonMandatoryAdditionalNodeFeatureColumnNameMap;
		this.nonMandatoryAdditionalEdgeFeatureColumnNameMap = nonMandatoryAdditionalEdgeFeatureColumnNameMap;
		
	}

	/**
	 * 
	 * build the children node sibling order index map of each non-leaf node with the current sibling orders;
	 * 
	 * note that current sibling orders of children nodes of a parent node may not be 0,1,2,3... but can be 1,2,3.. ; 0,2,3...; 0,1,3... etc
	 * 
	 * thus need to invoke {@link #recalculateChildrenSiblingOrderIndex()}
	 * 
	 */
	public void buildChildrenNodeSiblingOrderIndexMap() {
		for (Integer nodeID : this.nodeIDMap.keySet()) {
			VfTrimTreeNode node = this.nodeIDMap.get(nodeID);
			if (node.getParentNodeID() == null) {
				// skip root node
			} else {
				VfTrimTreeNode parentNode = this.nodeIDMap.get(node.getParentNodeID());
				parentNode.addChildNode(node);
			}
		}
	}
	
	/**
	 * enforce the children nodes of each node on the tree have sibling order index
	 * as 0,1,2,3,...; must be invoked if the VfTrimTree is resulted from trimming
	 * that involve sibling order changes such as reroot because it may result in
	 * sibling order index such as: 0,2,3 or 1,2,3 or 0,1,2,4, etc
	 */
	public void recalculateChildrenSiblingOrderIndex() {
		for (Integer nodeID : this.nodeIDMap.keySet()) {
			VfTrimTreeNode node = this.nodeIDMap.get(nodeID);
			
			// sort child node of the node by the current sibling order index
			// By default, all key-value pairs in TreeMap are sorted in their natural
			// ascending order
			Map<Integer, VfTrimTreeNode> sortedChildNodeMap = new TreeMap<>(node.getChildrenNodeSiblingOrderIndexMap());
			
			// assign new sibling order index to each child node
			int newIndex = 0;
			for (VfTrimTreeNode childNode : sortedChildNodeMap.values()) {//
				childNode.setSiblingOrderIndex(newIndex);
				newIndex++;
			}
			node.clearChildrenNodeSiblingOrderIndexMap();
		}
		
		//
		this.buildChildrenNodeSiblingOrderIndexMap();
	}

	@Override
	public VfTrimTreeNode getRootNode() {
		return this.rootNode;
	}

	@Override
	public Map<DataTableColumnName, DataTableColumn> getNonMandatoryAdditionalNodeFeatureColumnNameMap() {
		return nonMandatoryAdditionalNodeFeatureColumnNameMap;
	}

	@Override
	public Map<DataTableColumnName, DataTableColumn> getNonMandatoryAdditionalEdgeFeatureColumnNameMap() {
		return nonMandatoryAdditionalEdgeFeatureColumnNameMap;
	}

}
