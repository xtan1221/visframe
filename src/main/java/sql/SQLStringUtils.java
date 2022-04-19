package sql;

import basic.SimpleName;
import basic.VfNameString;
import rdb.table.HasIDTypeRelationalTableSchema;
import rdb.table.HasIDTypeRelationalTableSchemaID;

/**
 * utilities for general sql string construction;
 * 
 * not specific to apache Derby;
 * 
 * @author tanxu
 *
 */
public final class SQLStringUtils {

	/**
	 * regular space in a sql string
	 */
	public static final String SPACE = " ";
	public static final String PATH_SEPARATOR = ".";
	public static final String COMMA = ",";
	public static final String EQUALS = "=";

	//////////////////////////////////////////////////
	// create table related
	public static final String CREATE_TABLE_HEADER = "CREATE TABLE ";
	public static final String PRIMARY_KEY_HEADER = "PRIMARY KEY ";

	// logical operator keyword
	/**
	 * AND logical operator
	 */
	public static final String AND = "AND";

	////////////////////////////////////
	// clause header string
	public static final String SELECT_CLAUSE_HEADER = "SELECT".concat(SPACE);
	public static final String FROM_CLAUSE_HEADER = "FROM".concat(SPACE);
	public static final String WHERE_CLAUSE_HEADER = "WHERE".concat(SPACE);
	public static final String GROUP_BY_CLAUSE_HEADER = "GROUP BY".concat(SPACE);
	
	/**
	 * build and return the full path name of the given table consisted of rdb
	 * schema name, table name
	 * 
	 * @param schemaID
	 * @return
	 */
	public static String buildTableFullPathString(SimpleName schemaName, VfNameString tableName) {
		return schemaName.getStringValue().concat(PATH_SEPARATOR).concat(tableName.getStringValue());
	}
	
	/**
	 * build and return the full path name of the given table consisted of rdb
	 * schema name, table name
	 * 
	 * @param schemaID
	 * @return
	 */
	public static String buildTableFullPathString(
			HasIDTypeRelationalTableSchemaID<? extends HasIDTypeRelationalTableSchema> schemaID) {
		return buildTableFullPathString(schemaID.getSchemaName(), schemaID.getTableName());
	}

	/**
	 * build and return the full path name of the given table column consisted of
	 * rdb schema name, table name and column name;
	 * 
	 * @param schemaID
	 * @param columName
	 * @return
	 */
	public static String buildTableColumnFullPathString(
			HasIDTypeRelationalTableSchemaID<? extends HasIDTypeRelationalTableSchema> schemaID,
			VfNameString columnName) {
		return buildTableColumnFullPathString(schemaID, columnName.getStringValue());
	}
	
	public static String buildTableColumnFullPathString(
			HasIDTypeRelationalTableSchemaID<? extends HasIDTypeRelationalTableSchema> tableSchemaID,
			String columnNameStringValue) {
		return buildTableFullPathString(tableSchemaID).concat(PATH_SEPARATOR).concat(columnNameStringValue);
	}

	
	
	/**
	 * add single quote to cover the given string value of a string type constant;
	 * 
	 * need to check with {@link String#replaceAll(String, String)} method and
	 * related methods;
	 * 
	 * @param stringValue
	 * @return
	 */
	public static String singleQuoteStringTypeStringValue(String stringValue) {
		StringBuilder sb = new StringBuilder();
		sb.append("'").append(stringValue).append("'");
		return sb.toString();
	}
	
	/**
	 * build and return an equity condition string with the given two
	 * comparatorStrings;
	 */
	public static String buildEquityConditionString(Object comparatorString1, Object comparatorString2) {
		StringBuilder sb = new StringBuilder();

		sb.append(comparatorString1).append(EQUALS).append(comparatorString2);
		
		return sb.toString();
	}
}
