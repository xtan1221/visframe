package rdb.table.value.type;

import context.project.rdb.VisProjectRDBConstants;
import rdb.table.value.ValueTableName;
import rdb.table.value.ValueTableSchemaID;

public class CFTargetValueTableSchemaID extends ValueTableSchemaID<CFTargetValueTableSchema> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1366546294646982819L;
	
	
	///////////////////////////////
	/**
	 * constructor
	 * @param tableName
	 */
	public CFTargetValueTableSchemaID(ValueTableName tableName) {
		super(VisProjectRDBConstants.VALUE_SCHEMA_NAME, tableName);
		// TODO Auto-generated constructor stub
	}
	
	
}
