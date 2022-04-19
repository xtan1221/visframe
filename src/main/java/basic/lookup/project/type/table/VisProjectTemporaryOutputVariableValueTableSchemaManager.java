package basic.lookup.project.type.table;

import java.sql.SQLException;
import java.util.Set;

import basic.lookup.project.type.VisProjectHasIDTypeRelationalTableSchemaManagerBase;
import context.project.VisProjectDBContext;
import context.project.rdb.VisProjectRDBConstants;
import rdb.table.value.ValueTableName;
import rdb.table.value.type.TemporaryOutputVariableValueTableInitializer;
import rdb.table.value.type.TemporaryOutputVariableValueTableSchema;
import rdb.table.value.type.TemporaryOutputVariableValueTableSchemaID;
import sql.derby.TableSchemaUtils;

public class VisProjectTemporaryOutputVariableValueTableSchemaManager 
		extends VisProjectHasIDTypeRelationalTableSchemaManagerBase<TemporaryOutputVariableValueTableSchema, TemporaryOutputVariableValueTableSchemaID>{

	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	public VisProjectTemporaryOutputVariableValueTableSchemaManager(VisProjectDBContext visProjectDBContext) {
		super(visProjectDBContext, TemporaryOutputVariableValueTableSchema.class, TemporaryOutputVariableValueTableSchemaID.class);
	}

	/**
	 * delete all TemporaryOutputVariableValueTable in the CALCULATION schema;
	 * @throws SQLException 
	 */
	public void deleteAll() throws SQLException {
		Set<String> allNamesInCalculationSchema = TableSchemaUtils.getAllTableNameSet(this.getVisProjectDBConnection(), VisProjectRDBConstants.CALCULATION_SCHEMA_NAME.getStringValue());
		
		for(String name:allNamesInCalculationSchema) {
			//only drop table starts with TemporaryOutputVariableValueTableInitializer.VALUE_TABLE_NAME_PREFIX
			if(name.toUpperCase().startsWith(TemporaryOutputVariableValueTableInitializer.VALUE_TABLE_NAME_PREFIX)) {
				TableSchemaUtils.dropTable(this.getVisProjectDBConnection(), VisProjectRDBConstants.CALCULATION_SCHEMA_NAME, new ValueTableName(name));
			}
		}
		
	}
	
}
