package rdb.table.value.type;

import java.util.List;

import context.project.rdb.VisProjectRDBConstants;
import rdb.table.value.ValueTableColumn;
import rdb.table.value.ValueTableName;
import rdb.table.value.ValueTableSchema;

public class CFTargetValueTableSchema extends ValueTableSchema{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6155833947193268866L;


	/**
	 * constructor
	 * @param tableName
	 * @param orderedListOfColumn
	 */
	public CFTargetValueTableSchema(
			ValueTableName tableName,
			List<ValueTableColumn> orderedListOfColumn) {
		super(VisProjectRDBConstants.VALUE_SCHEMA_NAME, tableName, orderedListOfColumn);
	}
	
	
	@Override
	public CFTargetValueTableSchemaID getID() {
		return new CFTargetValueTableSchemaID(this.getName());
	}
	
	
}
