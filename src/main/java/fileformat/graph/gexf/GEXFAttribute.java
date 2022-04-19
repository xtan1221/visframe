package fileformat.graph.gexf;

import basic.VfNotes;
import importer.utils.StringUtils2;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;

/**
 * an attribute in GEXF;
 * 
 * 	see the Attributes Specification section in the GEXF 1.2draft Primer;
 * 
 * note that options (it defines the available values, separated by a pipe.) of an Attribute is ignored;
 * 
 * attributes in a GEXF file can be divided into two major types:
 * 1. GEXF defined default attributes for node and edge;
 * 
 * 2. user defined attributes;
 * 
 * @author tanxu
 *
 */
public class GEXFAttribute {
	private final String title; //attribute name string, may contain non-VfNameString allowed characters
	private final String type; //attribute data type string in GEXF data file
	private final String defaultValueString; //attribute default value string, if null, no default value;
	
	/////////////////
	private DataTableColumn dataTableColumn;
	private DataTableColumnName dataTableColumnName;
	
	/**
	 * constructor
	 * @param title
	 * @param type
	 * @param defaultValueString
	 */
	GEXFAttribute(String title, String type, String defaultValueString){
		this.title = title;
		this.type = type;
		this.defaultValueString = defaultValueString;
	}
	
	/**
	 * 
	 * @return
	 */
	public DataTableColumnName getDataTableColumnName() {
		if(this.dataTableColumnName==null) {
			this.dataTableColumnName = new DataTableColumnName(StringUtils2.transformStringToVfNameString(this.getTitle()));
		}
		return this.dataTableColumnName;
	}
	
	/**
	 * make and return a {@link DataTableColumn} based on this {@link GEXFAttribute};
	 * @param inPrimaryKey
	 * @return
	 */
	public DataTableColumn toDataTableColumn(boolean inPrimaryKey) {
//		DataTableColumnName name, SQLDataType sqlDataType, boolean inPrimaryKey,
//		Boolean unique, Boolean notNull, String defaultStringValue, String additionalConstraints,
//		VfNotes notes
		if(this.dataTableColumn==null) {
			this.dataTableColumn = new DataTableColumn(
					this.getDataTableColumnName(),
					GEXFAttributeDataTypeUtils.getType(this.getType()), 
					inPrimaryKey,
					false,
					inPrimaryKey, //not null = inPrimaryKey
					this.getDefaultValueString(),
					null,
					VfNotes.makeVisframeDefinedVfNotes()
					);
		}
		return this.dataTableColumn;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the defaultValueString
	 */
	public String getDefaultValueString() {
		return defaultValueString;
	}
	
	
}
