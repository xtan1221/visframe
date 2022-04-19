package sql.derby;

import rdb.table.AbstractRelationalTableColumn;
import rdb.table.AbstractRelationalTableSchema;
import sql.SQLStringUtils;

import static sql.SQLStringUtils.*;

import basic.SimpleName;
import basic.VfNameString;

/**
 * build sql string related with table schema manipulation such as 
 * create, drop
 * 
 * note that this class build sql string with compliance to apache derby;
 * 
 * @author tanxu
 *
 */
public class TableSchemaSQLStringFactory {
	
	/**
	 * build and return a full sql string to create a table schema with the given AbstractRelationalTableSchema;
	 * 
	 * note that in derby, if a table has single column in the primary key, the column is not allowed to have UNIQUE constraint;
	 * 
	 * @param tableSchema
	 * @return
	 */
	public static String buildCreateTableSqlString(AbstractRelationalTableSchema<?> tableSchema) {
		StringBuilder sb = new StringBuilder();
		sb.append(CREATE_TABLE_HEADER)
		.append(SQLStringUtils.buildTableFullPathString(tableSchema.getSchemaName(), tableSchema.getName()))
		.append("(");
		
		
		StringBuilder primaryKeyStringBuilder = new StringBuilder();
		primaryKeyStringBuilder.append("PRIMARY KEY (");
		//
		boolean hasSingleColumnPrimaryKey = tableSchema.getPrimaryKeyColumnNameSet().size()==1;
		
		boolean nothingAddedYet = true;
		boolean noPKAddedYet = true;
		for(AbstractRelationalTableColumn column:tableSchema.getOrderedListOfColumn()) {
			if(nothingAddedYet) {
				sb.append(buildColumnSqlString(column, hasSingleColumnPrimaryKey));
				nothingAddedYet = false;
			}else {
				sb.append(COMMA).append(buildColumnSqlString(column, hasSingleColumnPrimaryKey));
			}
			
			if(column.isInPrimaryKey()) {
				if(noPKAddedYet) {
					noPKAddedYet = false;
				}else {
					primaryKeyStringBuilder.append(", ");
				}
				primaryKeyStringBuilder.append(column.getName().getStringValue().toUpperCase());
			}
		}
		
		if(nothingAddedYet) {
			throw new IllegalArgumentException("no column added to table schema");
		}
		
		if(noPKAddedYet) {
			System.out.println("WARNING: no primary key column added to table schema:"+tableSchema.getName().getStringValue());
		}else {
			primaryKeyStringBuilder.append(")");
			
			sb.append(",").append(primaryKeyStringBuilder.toString());
		}

		sb.append(")");
		
		////
//		String primaryKeyString = buildPrimaryKeySqlString(tableSchema);
		
//		if(!primaryKeyString.isEmpty()) {
//			sb.append(primaryKeyString).append(")");
//		}
//		
		
		
		
		return sb.toString();
	}
	
	
	/**
	 * build sql string that 
	 * 
	 * 
	 * 
	 * https://db.apache.org/derby/docs/10.1/ref/rrefsqlj81859.html
     * The syntax for the column-definition for a new column is the same as for a column in a CREATE TABLE statement. 
     * This means that a column constraint can be placed on the new column within the ALTER TABLE ADD COLUMN statement. 
     * However, a column with a NOT NULL constraint can be added to an existing table unless you give a default value; 
     * otherwise, an exception is thrown when the ALTER TABLE statement is executed.
     * 
     * !!!!it seems that derby does not support adding multiple columns in a single query, thus,need to add each column separately;
     * 
     * ALTER TABLE WeatherCenter ADD COLUMN BarometricPressure SMALLINT NOT NULL;//?
     * 
	 * @param schemaName
	 * @param tableName
	 * @param newColumnList
	 * @return
	 */
	public static String buildAddColumnToExistingTableSqlString(SimpleName schemaName, VfNameString tableName, AbstractRelationalTableColumn newColumn) {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ")
		.append(SQLStringUtils.buildTableFullPathString(schemaName, tableName))
		.append(" ADD COLUMN ");
		
		sb.append(buildColumnSqlString(newColumn, false)); //here hasSingleColumnPrimaryKey is always false for convenience
		
		return sb.toString();
	}
	
	/**
	 * build and return the sql string of the given AbstractRelationalTableColumn;
	 * 
	 * note that this is not put in the AbstractRelationalTableColumn class because some of the features may be sql engine specific;
	 * 
	 * @param column
	 * @return
	 */
	public static String buildColumnSqlString(AbstractRelationalTableColumn column, boolean hasSingleColumnPrimaryKey) {
		System.out.println("create column sql string for column:"+column.getName().getStringValue());
		
		StringBuilder sb = new StringBuilder();
		sb.append("\"").append(column.getName().getStringValue().toUpperCase()).append("\"").append(" "). //always contain column name in double quotes to avoid conflicting with reserved words in sql?
		append(column.getSqlDataType().getSQLString());
		
		//only add NOT NULL constraints if the column is not in primary key;
		if(column.isNotNull()!=null && column.isNotNull()) {
			if(!column.isInPrimaryKey())
				sb.append(" NOT NULL");
		}
		
		//only add UNIQUE constraints if the column is not in primary key or is not the only primary key column
		if(column.isUnique()!=null && column.isUnique()) {
			if(!column.isInPrimaryKey()||!hasSingleColumnPrimaryKey) {
				sb.append(" UNIQUE");
			}
		}
		
//		if(column.getDefaultStringValue()!=null) {
//			if(column.getSqlDataType() instanceof SQLBooleanType) {
//				sb.append(" DEFAULT ").append(column.getDefaultStringValue());
//			}else {
//				sb.append(" DEFAULT '").append(column.getDefaultStringValue()).append("'");
//			}
//		}
		
		if(column.getDefaultStringValue()!=null) {
			if(column.getSqlDataType().isOfStringType()) {//value be quoted in single quotes ''     ???
				sb.append(" DEFAULT '").append(column.getDefaultStringValue()).append("'");
			}else {//value not quoted
				sb.append(" DEFAULT ").append(column.getDefaultStringValue());
			}
		}
		
		//TODO additionalConstraints
		if(column.getAdditionalConstraints()!=null) {
			sb.append(" ").append(column.getAdditionalConstraints());
		}
		
		
		return sb.toString();
	}
	
	
	/**
	 * create a primary key string for the given tableSchema;
	 * if there is no primary key columns in the given tableSchema, return empty string;
	 * 
	 * PRIMARY KEY (ID, NAME,...)
	 * @param tableSchema
	 * @return
	 */
	static String buildPrimaryKeySqlString(AbstractRelationalTableSchema<?> tableSchema) {
		StringBuilder sb = new StringBuilder();
		sb.append(PRIMARY_KEY_HEADER).append("(");
		
		boolean nothingAddedYet = true;
		for(VfNameString colName:tableSchema.getPrimaryKeyColumnNameSet()) {
			if(nothingAddedYet) {
				sb.append(colName.getStringValue());
				nothingAddedYet = true;
			}else {
				sb.append(COMMA).append(colName.getStringValue());
			}
		}
		
		if(nothingAddedYet) {
			return "";
		}else {
			sb.append(")");
			return sb.toString();
		}
	}
	
	////////////////////////////////////
	/**
	 * build and return a sql string to drop the given table;
	 * @param schemaName
	 * @param tableName
	 * @return
	 */
	static String buildDropTableSQLString(SimpleName schemaName, VfNameString tableName) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("DROP TABLE ").append(SQLStringUtils.buildTableFullPathString(schemaName, tableName));
		
		return sb.toString();
	}
	
	
	static String buildClearTableContentSQLString(SimpleName schemaName, VfNameString tableName) {
		return "TRUNCATE TABLE ".concat(SQLStringUtils.buildTableFullPathString(schemaName, tableName));
	}
	
	/**
	 * //note that derby use a different syntax: RENAME TABLE table-Name TO new-Table-Name
        //in mysql: ALTER TABLE table_name RENAME TO new_table_name;
	 * @param schemaName
	 * @param tableName
	 * @param newTableName
	 * @return
	 */
	static String buildRenameTableSQLString(SimpleName schemaName, VfNameString tableName, VfNameString newTableName) {
		return "RENAME TABLE ".concat(SQLStringUtils.buildTableFullPathString(schemaName, tableName)).concat(" TO ").concat(SQLStringUtils.buildTableFullPathString(schemaName, newTableName));
	}
}
