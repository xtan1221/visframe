package metadata.graph.vftree;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import basic.VfNotes;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;

/**
 * factory class for the mandatory attributes for node data for a VfTreeDataMetadata defined in visframe;
 * 
 * specifically, includes the node id column and the set of mandatory additional attributes;
 * 
 * @author tanxu
 *
 */
public class VfTreeMandatoryNodeDataTableSchemaUtils {
	//////primary key column that should be shared by all types of vftree format
	public static DataTableColumn nodeIDColumn() {
		return new DataTableColumn(
				new DataTableColumnName("NODE_ID"), 
				SQLDataTypeFactory.shortIntegerType(), //[-32768,32767], max number of node on a newick tree allowed in visframe is 32767
				true, true, true, null,null,//boolean inPrimaryKey, Boolean unique, Boolean notNull, String defaultStringValue,
				VfNotes.makeVisframeDefinedVfNotes());
	}
	
	////////default/mandatory additional feature columns shared by all {@link VfTreeDataMetadata} defined by visframe
	public static DataTableColumn nodeParentIDColumn() {
		return new DataTableColumn(
				new DataTableColumnName("PARENT_ID"), 
				SQLDataTypeFactory.shortIntegerType(), //[-32768,32767], max number of node on a newick tree allowed in visframe is 32767
				false, false, false, null,null,//boolean inPrimaryKey, Boolean unique, Boolean notNull, String defaultStringValue,
				VfNotes.makeVisframeDefinedVfNotes());//can be null for root node
	}
	public static DataTableColumn nodeSiblingIndexColumn() {
		return new DataTableColumn(
				new DataTableColumnName("SIBLING_INDEX"), 
				SQLDataTypeFactory.shortIntegerType(), //[-32768,32767], max number of node on a newick tree allowed in visframe is 32767
				false, false, true, null,null, //boolean inPrimaryKey, Boolean unique, Boolean notNull, String defaultStringValue,
				VfNotes.makeVisframeDefinedVfNotes());//cannot be null
	}
	
	public static DataTableColumn nodeIsLeafColumn() {
		return new DataTableColumn(
				new DataTableColumnName("IS_LEAF"), 
				SQLDataTypeFactory.booleanType(), //[-32768,32767], max number of node on a newick tree allowed in visframe is 32767
				false, false, true, null, null,//boolean inPrimaryKey, Boolean unique, Boolean notNull, String defaultStringValue,
				VfNotes.makeVisframeDefinedVfNotes());//cannot be null
	}
	public static DataTableColumn nodeDistanceToRootColumn() {
		return new DataTableColumn(
				new DataTableColumnName("DIST_TO_ROOT"), 
				SQLDataTypeFactory.doubleType(), //[-32768,32767], max number of node on a newick tree allowed in visframe is 32767
				false, false, false, null, null,//boolean inPrimaryKey, Boolean unique, Boolean notNull, String defaultStringValue,
				VfNotes.makeVisframeDefinedVfNotes());//can be null
	}
	
	/**
	 * not null; for root node, the value is 0
	 * @return
	 */
	public static DataTableColumn edgeNumToRootColumn() {
		return new DataTableColumn(
				new DataTableColumnName("EDGE_NUM_TO_ROOT"), 
				SQLDataTypeFactory.shortIntegerType(), //[-32768,32767], max number of node on a newick tree allowed in visframe is 32767
				false, false, true, null,null,//boolean inPrimaryKey, Boolean unique, Boolean notNull, String defaultStringValue,
				VfNotes.makeVisframeDefinedVfNotes());//cannot be null
	}
	
	/**
	 * not null; 
	 * @return
	 */
	public static DataTableColumn leafIndexColumn() {
		return new DataTableColumn(
				new DataTableColumnName("LEAF_INDEX"), 
				SQLDataTypeFactory.doubleType(), //[-32768,32767], max number of node on a newick tree allowed in visframe is 32767
				false, false, true, null,null,//boolean inPrimaryKey, Boolean unique, Boolean notNull, String defaultStringValue,
				VfNotes.makeVisframeDefinedVfNotes());//cannot be null
	}
	
	
	private static List<DataTableColumn> mandatoryAttributeColumnList;
	/**
	 * return the list of mandatory attribute columns for a vftree node record data including ID columns and mandatory additional feature columns
	 * @return
	 */
	public static List<DataTableColumn> getMandatoryAttributeColumnList(){
		if(mandatoryAttributeColumnList==null) {
			mandatoryAttributeColumnList = new ArrayList<>();
			
			getIDColumnList().forEach(c->{
				mandatoryAttributeColumnList.add(c);
			});
			
			getMandatoryAdditionalFeatureColumnList().forEach(c->{
				mandatoryAttributeColumnList.add(c);
			});
		}
		
		return mandatoryAttributeColumnList;
	}
	
	private static List<DataTableColumnName> mandatoryAttributeColumnNameList;
	/**
	 * return the list of mandatory attribute columns for a vftree node record data including ID columns and mandatory additional feature columns
	 * @return
	 */
	public static List<DataTableColumnName> getMandatoryAttributeColumnNameList(){
		if(mandatoryAttributeColumnNameList==null) {
			mandatoryAttributeColumnNameList = new ArrayList<>();
			
			for(DataTableColumn col:getMandatoryAttributeColumnList()) {
				mandatoryAttributeColumnNameList.add(col.getName());
			}
		}
		return mandatoryAttributeColumnNameList;
	}
	
	/**
	 * return the set of column names for the id attribute set of vftree node record data 
	 * @return
	 */
	public static LinkedHashSet<DataTableColumnName> getIDColumnNameList(){
		LinkedHashSet<DataTableColumnName> ret = new LinkedHashSet<>();
		
		getIDColumnList().forEach(c->{
			ret.add(c.getName());
		});
		
		return ret;
	}
	
	/**
	 * return the set of column names for the id attribute set of vftree node record data 
	 * @return
	 */
	public static LinkedHashSet<DataTableColumn> getIDColumnList(){
		LinkedHashSet<DataTableColumn> ret = new LinkedHashSet<>();
		
		ret.add(nodeIDColumn());
		
		return ret;
	}
	
	/**
	 * return the set of column names for the mandatory additional attribute set of vftree node record data;
	 * @return
	 */
	public static LinkedHashSet<DataTableColumnName> getMandatoryAdditionalFeatureColumnNameList(){
		LinkedHashSet<DataTableColumnName> ret = new LinkedHashSet<>();
		
		getMandatoryAdditionalFeatureColumnList().forEach(c->{
			ret.add(c.getName());
		});
		
		return ret;
	}
	
	/**
	 * return the set of column names for the mandatory additional attribute set of vftree node record data;
	 * @return
	 */
	public static LinkedHashSet<DataTableColumn> getMandatoryAdditionalFeatureColumnList(){
		LinkedHashSet<DataTableColumn> ret = new LinkedHashSet<>();
		
		ret.add(nodeParentIDColumn());
		ret.add(nodeSiblingIndexColumn());
		ret.add(nodeIsLeafColumn());
		ret.add(nodeDistanceToRootColumn());
		ret.add(edgeNumToRootColumn());
		ret.add(leafIndexColumn());
		
		return ret;
	}
	
	/**
	 * 
	 * @param nonMandatoryAdditionaFeatureColumnNameSet
	 * @return
	 */
	public static LinkedHashSet<DataTableColumnName> makeAdditionalFeatureColumnNameSet(LinkedHashSet<DataTableColumnName> nonMandatoryAdditionaFeatureColumnNameSet){
		LinkedHashSet<DataTableColumnName> ret = new LinkedHashSet<>();
		ret.addAll(getMandatoryAdditionalFeatureColumnNameList());
		ret.addAll(nonMandatoryAdditionaFeatureColumnNameSet);
		return ret;
	}
	
}
