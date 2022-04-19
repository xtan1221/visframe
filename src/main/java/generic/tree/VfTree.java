package generic.tree;

import java.util.LinkedHashSet;
import java.util.Map;

import metadata.graph.vftree.feature.VfTreeEdgeFeature;
import metadata.graph.vftree.feature.VfTreeNodeFeature;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;

/**
 * the base interface for VfTree that is defined in visframe;
 * 
 * @author tanxu
 * 
 */
public interface VfTree {
	/**
	 * returns the node of the tree;
	 * @return
	 */
	VfTreeNode getRootNode();
	
	/**
	 * return the full set of node on this tree
	 * @return
	 */
	default Map<Integer, ? extends VfTreeNode> getNodeIDMap(){
		return this.getRootNode().getDescendantNodeIDMap();
	}
	
	/**
	 * create and return the {@link VfTreeNodeFeature} for this VfTree which is required to make the {@link VfTreeDataMetadata};
	 * @return
	 */
	default VfTreeNodeFeature getVfTreeNodeFeature() {
//		LinkedHashSet<DataTableColumnName> IDColumnNameSet = VfTreeMandatoryNodeDataTableSchemaUtils.getIDColumnNameList();
		
		LinkedHashSet<DataTableColumnName> nonMandatoryAdditionalFeatureColumnNameSet = new LinkedHashSet<>();
//		additionalFeatureColumnNameSet.addAll(VfTreeMandatoryNodeDataTableSchemaUtils.getMandatoryAdditionalFeatureColumnNameList());
		nonMandatoryAdditionalFeatureColumnNameSet.addAll(this.getNonMandatoryAdditionalNodeFeatureColumnNameMap().keySet());
		
		
		return new VfTreeNodeFeature(nonMandatoryAdditionalFeatureColumnNameSet);
	}
	
	/**
	 * create and return the {@link VfTreeEdgeFeature} for this VfTree which is required to make the {@link VfTreeDataMetadata};
	 * @return
	 */
	default VfTreeEdgeFeature getVfTreeEdgeFeature() {
//		LinkedHashSet<DataTableColumnName> IDColumnNameSet = VfTreeMandatoryEdgeDataTableSchemaUtils.getIDColumnNameList();
		
		LinkedHashSet<DataTableColumnName> nonMandatoryAdditionalFeatureColumnNameSet = new LinkedHashSet<>();
//		additionalFeatureColumnNameSet.addAll(VfTreeMandatoryEdgeDataTableSchemaUtils.getMandatoryAdditionalFeatureColumnNameList());
		nonMandatoryAdditionalFeatureColumnNameSet.addAll(this.getNonMandatoryAdditionalEdgeFeatureColumnNameMap().keySet());
		
		
		return new VfTreeEdgeFeature(nonMandatoryAdditionalFeatureColumnNameSet);
	}
	
	
	/**
	 * return the set of non-mandatory additional attributes for the nodes of this VfTree;
	 * 
	 * for graph data's vertex data, there are 
	 * 1. ID attributes set
	 * 		ID attributes distinguishes each vertex entity from each other
	 * 2. additional attributes set;
	 * 
	 * VfTRee is a subtype of Graph data in visframe, thus it must have the consistent set of information above;
	 * 
	 * specifically, 
	 * 1. for node data of vftree, the id attributes set contains the node ID index only;
	 * 
	 * 2. all other attributes are additional attributes, among which, some are mandatory (thus there must be a corresponding column 
	 * in node record data table of every vftree data metadata) defined by visframe including 
	 * 		1. mandatory additional attributes of vftree in visframe:
	 * 				parent node id, sibling order index, dist to parent, bootstrap to parent;
	 * 		2. other additional attributes specific to each subtype of vftree format or vftree data metadata but not shared by all of vftree, which should be returned by this method;
	 * 
	 * the set of attributes above are designed based on the characteristics of the tree data visframe is supposed to deal with and also be consistent with the operations with a vftree as input; 
	 * @return
	 */
	Map<DataTableColumnName, DataTableColumn> getNonMandatoryAdditionalNodeFeatureColumnNameMap();
	
	/**
	 * return the set of non-mandatory additional attributes for the edges of this VfTree;
	 * 
	 * for graph data's edge data, there are 
	 * 1. id attributes
	 * 2. source node id attributes
	 * 3. sink node id attributes;
	 * 4. whether source/sink node id attributes are disjoint from id attributes;
	 * 5. additional attributes;
	 * 
	 * vftree is a subtype of graph data, thus it must have the consistent set of information above;
	 * 
	 * specifically, for a vftree edge data
	 * 1. the id attributes are parent and child node id
	 * 2. source node id attributes include the parent node id;
	 * 3. sink node id attributes include the child node id;
	 * 4. source/sink node id attributes are not disjoint from id attributes;
	 * 5. additional attributes
	 * 		1. visframe defined mandatory additional attributes for vftree edge data
	 * 		2. other additional attributes specific to each subtype of vftree format or vftree data metadata but not shared by all of vftree, which should be returned by this method;
	 * 
	 * the set of attributes above are designed based on the characteristics of the tree data visframe is supposed to deal with and also be consistent with the operations with a vftree as input; 
	 * @return
	 */
	Map<DataTableColumnName, DataTableColumn> getNonMandatoryAdditionalEdgeFeatureColumnNameMap();
}
