package generic.tree.reader.filebased.newick;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import fileformat.vftree.VfTreeDataFileFormatType;
import generic.tree.VfTreeNode;
import rdb.table.data.DataTableColumnName;
import utils.Pair;
import utils.Triple;

/**
 * node type class to parse and build a node of a {@link VfTreeDataFileFormatType#SIMPLE_NEWICK_1} or {@link VfTreeDataFileFormatType#SIMPLE_NEWICK_2} format tree;
 * 
 * @author tanxu
 * 
 */
public class SimpleNewickFileTreeNode implements VfTreeNode{
	private static int NODE_COUNTER;
	
	///////////////////
	private final VfTreeDataFileFormatType formatType;
	private final String newickString;
	private final Integer parentID;
	private final int siblingOrderIndex;
	
	////////
	private int ID;//
	private Double distToParent;
	private Integer boostrapToParent;
	private String labelString;
	
	private Map<Integer, SimpleNewickFileTreeNode> childrenNodeSiblingOrderIndexMap;
	
	/**
	 * constructor
	 * @param newickString
	 * @param parentID
	 * @param siblingOrderIndex
	 */
	public SimpleNewickFileTreeNode(
			String newickString,
			Integer parentID,
			int siblingOrderIndex,
			VfTreeDataFileFormatType formatType
			) {
		this.formatType = formatType;
		
		this.newickString = newickString;
		this.parentID = parentID;
		this.siblingOrderIndex = siblingOrderIndex;
		
		
		this.parseNewickString();
	}
	
	/**
	 * parse the newick string for:
	 * 0. set node id of this node
	 * 1. distance from this node to its parent(null if absent)
	 * 2. bootstrap on the edge between this node and its parent node(null if absent)
	 * 3. string label of this node(empty string if absent)
	 * 4. extract children newick strings(if any) and use them to create children VfNewickTreeNode and assign sibling index to them and store in the childrenNodeLinkedHashSet
	 */
	private void parseNewickString() {
		if(this.parentID == null) {//this node is root node, reset the NODE_ID_COUNTER
			NODE_COUNTER = 0;
		}
		//node id
		this.ID = NODE_COUNTER;
		NODE_COUNTER++;
		
		
		//
		Triple<String,String,String> splits = SimpleNewickParserUtils.extractChildrenNodeStringNodeLabelStringAndBranchLabelString(newickString, this.formatType);
        String childrenNodesString = splits.getLeft();
        
        this.labelString = splits.getMiddle();
        String edgeLabelString = splits.getRight(); //for edge length and bootstrap
        
        Pair<Double,Integer> parsedResult = SimpleNewickParserUtils.parseEdgeLabelStringForLengthAndBootstrap(edgeLabelString, this.formatType);
        this.distToParent = parsedResult.getFirst();
        this.boostrapToParent = parsedResult.getSecond();
        
        //
        this.childrenNodeSiblingOrderIndexMap = new LinkedHashMap<>();
        if(childrenNodesString == null){//this node is a leaf
            //no children
        }else{//this node is an internal node with children nodes
            
            List<String> childrenNodeStringList = SimpleNewickParserUtils.splitNakedInternalNodeStringIntoChildrenNodeStrings(childrenNodesString);
            int siblingOrderIndex = 0;
            for(String childNodeString:childrenNodeStringList){
                this.childrenNodeSiblingOrderIndexMap.put(siblingOrderIndex, new SimpleNewickFileTreeNode(childNodeString,this.ID, siblingOrderIndex, this.formatType));
                siblingOrderIndex++;
            }
        }
	}

	
	public String getNewickString() {
		return newickString;
	}

	/////////////////////////////////!!!!
	/**
	 * return the other additional node feature column name string value map of this VfNewickTreeNode;
	 * note that for newick format, the only feature falling into this type is the node label string;
	 */
	@Override
	public Map<DataTableColumnName, String> getNonMandatoryAdditionalNodeFeatureColumnNameValueStringMap() {
		Map<DataTableColumnName, String> ret = new LinkedHashMap<>();
		ret.put(SimpleNewickFileTreeReader.newickTreeNodeLabelStringColumn().getName(), this.labelString);//
		return ret;
	}
	
	/**
	 * return the other additional edge feature column name string value map of this VfNewickTreeNode to its parent node;
	 * note that for newick format, there is no such features, thus return empty set;
	 */
	@Override
	public Map<DataTableColumnName, String> getNonMandatoryAdditionalFeatureColumnNameValueStringMapOfEdgeToParent() {
		return new LinkedHashMap<>();
	}
	
	//////////////////////////////////////
	
	@Override
	public Map<Integer, SimpleNewickFileTreeNode> getChildrenNodeSiblingOrderIndexMap() {
		return this.childrenNodeSiblingOrderIndexMap;
	}
	
	@Override
	public int getID() {
		return this.ID;
	}
	
	@Override
	public Integer getParentNodeID() {
		return this.parentID;
	}
	
	@Override
	public int getSiblingOrderIndex() {
		return this.siblingOrderIndex;
	}
	
	
	@Override
	public Double getDistanceToParentNode() {
		return this.distToParent;
	}
	
	@Override
	public Integer getBootstrapValueToParentNode() {
		return this.boostrapToParent;
	}
	

}
