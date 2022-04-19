package rdb.table.value.type;

import java.util.List;

import context.project.rdb.VisProjectRDBConstants;
import rdb.table.value.ValueTableColumn;
import rdb.table.value.ValueTableName;
import rdb.table.value.ValueTableSchema;

public class TemporaryOutputVariableValueTableSchema extends ValueTableSchema{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4087480864491054783L;

	/**
	 * constructor
	 * @param tableName
	 * @param orderedListOfColumn
	 */
	TemporaryOutputVariableValueTableSchema(
			
			ValueTableName tableName,
			List<ValueTableColumn> orderedListOfColumn) {
		super(VisProjectRDBConstants.CALCULATION_SCHEMA_NAME, tableName, orderedListOfColumn);
	}

	@Override
	public TemporaryOutputVariableValueTableSchemaID getID() {
		return new TemporaryOutputVariableValueTableSchemaID(this.getName());
	}
	
}