package rdb.table.value.type;

import context.project.rdb.VisProjectRDBConstants;
import rdb.table.value.ValueTableName;
import rdb.table.value.ValueTableSchemaID;

public class TemporaryOutputVariableValueTableSchemaID extends ValueTableSchemaID<TemporaryOutputVariableValueTableSchema> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3945175370741412650L;
	
	/**
	 * constructor
	 * @param tableName
	 */
	public TemporaryOutputVariableValueTableSchemaID(ValueTableName tableName) {
		super(VisProjectRDBConstants.CALCULATION_SCHEMA_NAME, tableName);
		// TODO Auto-generated constructor stub
	}

}
