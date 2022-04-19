package rdb.table.value.type;

import java.util.List;

import context.project.rdb.VisProjectRDBConstants;
import rdb.table.value.ValueTableColumn;
import rdb.table.value.ValueTableName;
import rdb.table.value.ValueTableSchema;

public class PiecewiseFunctionIndexIDOutputIndexValueTableSchema extends ValueTableSchema{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8278694332813414247L;

	/**
	 * constructor
	 * @param tableName
	 * @param orderedListOfColumn
	 */
	PiecewiseFunctionIndexIDOutputIndexValueTableSchema(
			ValueTableName tableName,
			List<ValueTableColumn> orderedListOfColumn) {
		super(VisProjectRDBConstants.CALCULATION_SCHEMA_NAME, tableName, orderedListOfColumn);
	}
	
	@Override
	public PiecewiseFunctionIndexIDOutputIndexValueTableSchemaID getID() {
		return new PiecewiseFunctionIndexIDOutputIndexValueTableSchemaID(this.getName());
	}
	
}
