package metadata.graph.vftree;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import basic.VfNotes;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;


/**
 * factory class for the mandatory attributes for edge data for a VfTreeDataMetadata defined in visframe;
 * 
 * specifically, includes the edge id columns and the set of mandatory additional attributes;
 * @author tanxu
 * 
 */
public class VfTreeMandatoryEdgeDataTableSchemaUtils {

	//////primary key colum
	public static DataTableColumn parentNodeIDColumn() {
		return new DataTableColumn(
				new DataTableColumnName("PARENT_NODE_ID"), 
				SQLDataTypeFactory.shortIntegerType(), //[-32768,32767], max number of node on a newick tree allowed in visframe is 32767
				true, false, true, null, null,//boolean inPrimaryKey, Boolean unique, Boolean notNull, String defaultStringValue,
				VfNotes.makeVisframeDefinedVfNotes());
	}
	
	/**
	 * note that in theory, child node id should be unique;
	 * but since it is in primary key, thus still set isUnique to false!
	 * @return
	 */
	public static DataTableColumn childNodeIDColumn() {
		return new DataTableColumn(
				new DataTableColumnName("CHILD_NODE_ID"), 
				SQLDataTypeFactory.shortIntegerType(), //[-32768,32767], max number of node on a newick tree allowed in visframe is 32767
				true, false, true, null, null,//boolean inPrimaryKey, Boolean unique, Boolean notNull, String defaultStringValue,
				VfNotes.makeVisframeDefinedVfNotes());
	}
	
	////////default/mandatory additional feature columns shared by all vftree tree file formats defined by visframe
	public static DataTableColumn lengthColumn() {
		return new DataTableColumn(
				new DataTableColumnName("LENGTH"), 
				SQLDataTypeFactory.doubleType(), //[-32768,32767], max number of node on a newick tree allowed in visframe is 32767
				false, false, false, null,null,//boolean inPrimaryKey, Boolean unique, Boolean notNull, String defaultStringValue,
				VfNotes.makeVisframeDefinedVfNotes());//can be null
	}
	public static DataTableColumn bootstrapColumn() {
		return new DataTableColumn(
				new DataTableColumnName("BOOTSTRAP"), 
				SQLDataTypeFactory.shortIntegerType(), //[-32768,32767], max number of node on a newick tree allowed in visframe is 32767
				false, false, false, null,null, //boolean inPrimaryKey, Boolean unique, Boolean notNull, String defaultStringValue,
				VfNotes.makeVisframeDefinedVfNotes());//can be null
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
	 * return the set of column for the id attribute set of vftree node record data 
	 * @return
	 */
	public static LinkedHashSet<DataTableColumn> getIDColumnList(){
		LinkedHashSet<DataTableColumn> ret = new LinkedHashSet<>();
		
		ret.add(parentNodeIDColumn());
		ret.add(childNodeIDColumn());
		
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
	
	public static LinkedHashSet<DataTableColumnName> makeAdditionalFeatureColumnsNameList(LinkedHashSet<DataTableColumnName> nonMandatoryAdditionalFeatureColumnList){
		LinkedHashSet<DataTableColumnName> ret = new LinkedHashSet<>();
		ret.addAll(getMandatoryAdditionalFeatureColumnNameList());
		ret.addAll(nonMandatoryAdditionalFeatureColumnList);
		return ret;
	}
	
	/**
	 * return the set of column names for the mandatory additional attribute set of vftree node record data;
	 * @return
	 */
	public static LinkedHashSet<DataTableColumn> getMandatoryAdditionalFeatureColumnList(){
		LinkedHashSet<DataTableColumn> ret = new LinkedHashSet<>();
		
		ret.add(lengthColumn());
		ret.add(bootstrapColumn());
		
		return ret;
	}
}
