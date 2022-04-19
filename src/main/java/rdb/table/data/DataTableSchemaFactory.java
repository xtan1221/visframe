package rdb.table.data;

import java.util.ArrayList;
import java.util.List;

import basic.SimpleName;
import basic.VfNotes;
import context.project.rdb.VisProjectRDBConstants;
import rdb.sqltype.SQLDataTypeFactory;

public class DataTableSchemaFactory {
	/**
	 * 
	 * @return
	 */
	static SimpleName getSchemaName() {
		return VisProjectRDBConstants.DATA_SCHEMA_NAME;
	}
	
	
	/**
	 * make DataTableSchemaID
	 * @param name
	 * @return
	 */
	public static DataTableSchemaID makeDataTableSchemaID(DataTableName name) {
		return new DataTableSchemaID(name);
	}
	
	/**
	 * factory method to make a new RUID column;
	 * note that RUID column in data table should always be 
	 * 1. non-primary key
	 * 2. unique 
	 * 3. not null;
	 * @return
	 */
	public static DataTableColumn makeRUIDColumn() {
		return new DataTableColumn(
				//DataTableColumnName name, SQLDataType sqlDataType, boolean inPrimaryKey,
				new DataTableColumnName(VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE), SQLDataTypeFactory.integerType(),false,
				//Boolean unique, Boolean notNull, String defaultStringValue,
				true,true,null,"GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)",
				//VfNotes notes
				VfNotes.makeVisframeDefinedVfNotes()
				);
	}
	
//	/**
//	 * create and return a new RUID column that is primary key;
//	 * @return
//	 */
//	public static DataTableColumn makePKRUIDColumn() {
//		return new DataTableColumn(
//				//DataTableColumnName name, SQLDataType sqlDataType, boolean inPrimaryKey,
//				new DataTableColumnName(VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE), SQLDataTypeFactory.integerType(),true,
//				//Boolean unique, Boolean notNull, String defaultStringValue,
//				true,true,null,"GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)",
//				//VfNotes notes
//				new VfNotes()
//				);
//	}
	
	
	/**
	 * factory method to make a new DataTableSchema with the given table name and list of non-RUID DataTableColumns
	 * @param tableName
	 * @param orderedListOfNonRUIDColumn
	 * @return
	 */
	public DataTableSchema makeDataTableSchema(DataTableName tableName, List<DataTableColumn> orderedListOfNonRUIDColumn) {
		List<DataTableColumn> orderedListOfColumn = new ArrayList<>();
		
		orderedListOfColumn.add(makeRUIDColumn());
		orderedListOfColumn.addAll(orderedListOfNonRUIDColumn);
		
		return new DataTableSchema(tableName, orderedListOfColumn);
	}
}
