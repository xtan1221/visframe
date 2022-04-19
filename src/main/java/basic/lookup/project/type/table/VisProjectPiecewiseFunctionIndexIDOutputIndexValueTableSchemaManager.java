package basic.lookup.project.type.table;

import java.sql.SQLException;
import java.util.Set;

import basic.lookup.project.type.VisProjectHasIDTypeRelationalTableSchemaManagerBase;
import context.project.VisProjectDBContext;
import context.project.rdb.VisProjectRDBConstants;
import rdb.table.value.ValueTableName;
import rdb.table.value.type.PiecewiseFunctionIndexIDOutputIndexValueTableInitializer;
import rdb.table.value.type.PiecewiseFunctionIndexIDOutputIndexValueTableSchema;
import rdb.table.value.type.PiecewiseFunctionIndexIDOutputIndexValueTableSchemaID;
import sql.derby.TableSchemaUtils;

public class VisProjectPiecewiseFunctionIndexIDOutputIndexValueTableSchemaManager 
		extends VisProjectHasIDTypeRelationalTableSchemaManagerBase<PiecewiseFunctionIndexIDOutputIndexValueTableSchema, PiecewiseFunctionIndexIDOutputIndexValueTableSchemaID>{
	
	/**
	 * constructor
	 * 
	 * @param hostVisProjectDBContext
	 */
	public VisProjectPiecewiseFunctionIndexIDOutputIndexValueTableSchemaManager(
			VisProjectDBContext visProjectDBContext) {
		super(visProjectDBContext, PiecewiseFunctionIndexIDOutputIndexValueTableSchema.class, PiecewiseFunctionIndexIDOutputIndexValueTableSchemaID.class);
	}

	/**
	 * delete all PiecewiseFunctionIndexIDOutputIndexValueTable in the CALCULATION schema;
	 * @throws SQLException 
	 */
	public void deleteAll() throws SQLException {
		Set<String> allNamesInCalculationSchema = TableSchemaUtils.getAllTableNameSet(this.getVisProjectDBConnection(), VisProjectRDBConstants.CALCULATION_SCHEMA_NAME.getStringValue());
		
		for(String name:allNamesInCalculationSchema) {
			//only drop table starts with PiecewiseFunctionIndexIDOutputIndexValueTableInitializer.VALUE_TABLE_NAME_PREFIX
			if(name.toUpperCase().startsWith(PiecewiseFunctionIndexIDOutputIndexValueTableInitializer.VALUE_TABLE_NAME_PREFIX)) {
				TableSchemaUtils.dropTable(this.getVisProjectDBConnection(), VisProjectRDBConstants.CALCULATION_SCHEMA_NAME, new ValueTableName(name));
			}
		}
		
	}
	
}
