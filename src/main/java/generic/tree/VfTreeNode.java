package generic.tree;

import java.util.HashMap;
import java.util.Map;
import rdb.table.data.DataTableColumnName;

/**
 * base interface for a node of a vf tree for core features defined in visframe;
 * 
 * features that are derivative and must be calculated via recursion are not included in this interface; see VfPopulatorTree and VfPopulatorTreeNode
 * 
 * @author tanxu
 *
 */
public interface VfTreeNode {
	////////////
	/**
	 * returns the set of children nodes of this node in the order consistent with their sibling index order;
	 * should always be initialized for any VfTreeNode of a new VfTree
	 * @return
	 */
	Map<Integer, ? extends VfTreeNode> getChildrenNodeSiblingOrderIndexMap();
	
	/**
	 * return the set of descendant nodes of this node including this one
	 * @return
	 */
	default Map<Integer, ? extends VfTreeNode> getDescendantNodeIDMap(){
		Map<Integer, VfTreeNode> ret = new HashMap<>();
		
		ret.put(this.getID(),this);
		
		if(this.getChildrenNodeSiblingOrderIndexMap().isEmpty()) {
			//
		}else {
			for(VfTreeNode child: this.getChildrenNodeSiblingOrderIndexMap().values()) {
				ret.putAll(child.getDescendantNodeIDMap());
			}
		}
	
		return ret;
	}
	
	
	/**
	 * returns the unique id of this node among all nodes on the tree;
	 * for convenience, node id of a tree is normally ranged from 0,1,2, ...
	 * @return
	 */
	int getID();
	
	/**
	 * returns the id of the parent node of this node; return null if this node is the root;
	 * @return
	 */
	Integer getParentNodeID();
	
	/**
	 * returns the sibling order index of this node among all of its sibling nodes, which is ranged in 0,1,2,...;
	 * this value determines the relative positions of all siblings nodes with the same parent node;
	 * @return
	 */
	int getSiblingOrderIndex();
	
	
	/**
	 * returns the distance between this node and its parent node;
	 * returns null if there is no known distance value between this node and its parent node;
	 * @return
	 */
	Double getDistanceToParentNode();
	
	/**
	 * returns the bootstrap value of the edge between this node and its parent node;
	 * returns null if there is no known bootstrap value between this node and its parent node;
	 * @return
	 */
	Integer getBootstrapValueToParentNode();
	
	/**
	 * return the non-mandatory ADDITIONAL node features column name and value string map in addition to the visframe defined default ones;
	 * note that all such feature columns should be put in the returned map and if a feature value is null, put null value to the map value;
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
	 * 		2. other additional attributes specific to each subtype of vftree format or vftree data metadata but not shared by all of vftree, which should be returned by getMoreAdditionalNodeFeatureColumnNameValueStringMap();
	 * @return
	 */
	Map<DataTableColumnName, String> getNonMandatoryAdditionalNodeFeatureColumnNameValueStringMap();
	
	
	/**
	 * return the non-mandatory additional features column name and value string map in addition to the visframe defined ones for the edge between this node and its parent node
	 * note that all such feature columns should be put in the returned map and if a feature value is null, put null value to the map value;
	 * 
	 * !!!!!!!!!return null if this node is root;
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
	 * 		2. vftree data specific additional attributes;
	 * 
	 * @return
	 */
	Map<DataTableColumnName, String> getNonMandatoryAdditionalFeatureColumnNameValueStringMapOfEdgeToParent();
	
	
	
	
}
