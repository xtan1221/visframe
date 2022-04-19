package sql.derby;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import basic.SimpleName;
import basic.lookup.PrimaryKeyID;
import basic.lookup.VisframeUDT;
import rdb.table.AbstractRelationalTableSchemaID;
import rdb.table.lookup.ManagementTableColumn;
import sql.LogicOperator;

/**
 * factory class to build sql string that query, alter or delete rows in a table;
 * 
 * @author tanxu
 * 
 */
public class TableContentSQLStringFactory {
	
	/**
	 * cover the given column name string in double quotes to avoid confusion when the column name is the same with some derby sql reserved key words such as 'END';
	 * @param colName
	 * @return
	 */
	public static String quoteColumnName(String colName) {
		return "\"".concat(colName.toUpperCase()).concat("\"");
	}
	
	/**
	 * build and return a sql query for the given list of columns;
	 * @param schemaName
	 * @param tableName
	 * @param selectedColNameSet
	 * @param conditionString
	 * @return
	 */
	public static String buildSelectSQLString(String schemaName, String tableName, Collection<String> selectedColNameSet, String conditionString) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ").append(buildListOfColumnNameString(selectedColNameSet));
		sb.append(" FROM ").append(schemaName.concat(".").concat(tableName));
		if(conditionString!=null&&!conditionString.isEmpty()) {
			sb.append(" WHERE ").append(conditionString);
		}
		
		return sb.toString();
	}
	
	/**
	 * SQL query template to retrieve one single row of each group from a table;
	 * 
	 * SELECTED_COLUMNS 
	 * TARGET_TABLE 
	 * UNIQUE_COL one unique not null column of the TARGET_TABLE
	 * WHERE_CONDITION_STRING optional;
	 * GROUP_BY_COLUMNS 
	 */
	private final static String buildSelectOneFromEachGroupSQLString_TEMPLATE = 
			"select SELECTED_COLUMNS from TARGET_TABLE where UNIQUE_COL in (select min(UNIQUE_COL) from TARGET_TABLE WHERE_CONDITION_STRING group by GROUP_BY_COLUMNS)";
	
	/**
	 * build and return a sql string that group the table with the given groupByColNameList and retrieve the columns in given selectedColNameList of one random record in each group; 
	 * this method is only applicable for table with at least one column whose value is not null and unique among all the records in the table, for example, RUID column in visframe data table;
	 * 
	 * 
	 * template:
	 * 		select (SELECTED_COLUMNS) from TARGET_TABLE where UNIQUE_COL in (select min(UNIQUE_COL) from TARGET_TABLE where (CONDITION_STRING) group by (GROUP_BY_COLUMNS));
	 * 
	 * @param schemaName
	 * @param tableName
	 * @param uniqueColName value must be unique among all rows in the table 
	 * @param selectedColNameList list of column names to be selected
	 * @param groupByColNameList list of columns names to be grouped by 
	 * @param conditionString 
	 * @return
	 */
	public static String buildSelectOneFromEachGroupSQLString(String schemaName, String tableName, String uniqueColName, Collection<String> selectedColNameList, Collection<String> groupByColNameList, String conditionString) {
		
		String SELECTED_COLUMNS = buildListOfColumnNameString(selectedColNameList);
		String TARGET_TABLE = schemaName.concat(".").concat(tableName);
		String WHERE_CONDITION_STRING = conditionString==null?"":"WHERE ".concat(conditionString);
		String GROUP_BY_COLUMNS = buildListOfColumnNameString(groupByColNameList);
		
		
		String ret = buildSelectOneFromEachGroupSQLString_TEMPLATE;
		
		
		ret = ret.replace("SELECTED_COLUMNS", SELECTED_COLUMNS)
				.replace("TARGET_TABLE", TARGET_TABLE)
				.replace("UNIQUE_COL", uniqueColName.toUpperCase())
				.replace("WHERE_CONDITION_STRING", WHERE_CONDITION_STRING)
				.replace("GROUP_BY_COLUMNS", GROUP_BY_COLUMNS);
		
		
		return ret;
	}
	
	/**
	 * build and return a string of a list of unique columns;
	 * usually used in SELECT clause;
	 * 
	 * the column order in the given Collection will be kept in the returned String;
	 * @param colNameList
	 * @return
	 */
	public static String buildListOfColumnNameString(Collection<String> colNameList) {
		StringBuilder sb = new StringBuilder();
		boolean nothingAddedYet = true;
		for(String colName:colNameList) {
			if(nothingAddedYet) {
				nothingAddedYet = false;
			}else {
				sb.append(", ");
			}
			sb.append(quoteColumnName(colName));//always quote column name in case column name is the same with some reserved key words of derby sql, for example 'END'
		}
		
		return sb.toString();
	}
	
	/**
	 * build and return the condition string that values of all the columns in the given colNameSet are not null;
	 * @param colNames collection of unique column names
	 * @return
	 */
	public static String buildAllColumnValueNotNullConditionConditionString(Collection<String> colNames) {
		StringBuilder sb = new StringBuilder();
		boolean nothingAddedYet = true;
		for(String colName:colNames) {
			if(nothingAddedYet) {
				nothingAddedYet = false;
			}else {
				sb.append(" AND ");
			}
			sb.append(buildColumnValueNotNullConditionConditionString(colName));
		}
		
		return sb.toString();
	}
	/**
	 * build and return condition string that the given column's value is not null;
	 * 
	 * key word: IS NOT NULL
	 * @param colName
	 * @return
	 */
	public static String buildColumnValueNotNullConditionConditionString(String colName) {
		return quoteColumnName(colName).concat(" IS NOT NULL");
	}
	
	/**
	 * build and return a column value equity condition string;
	 * need to put string type column's value in single quote;
	 * 
	 * template to ignore case
	 * 		UPPER(NODE_LABEL) = UPPER('osa|loc_os06g46610')
	 * 
	 * 
	 * @param colName
	 * @param valueString
	 * @param ofStringType
	 * @param toIgnoreCase whether or not to ignore the case of the string value; only applicable if ofStringType is true;
	 */
	public static String buildColumnValueEquityCondition(String colName, String valueString, boolean ofStringType, Boolean toIgnoreCase) {
		if(ofStringType && toIgnoreCase==null)
			throw new IllegalArgumentException("given toIgnoreCase cannot be null when ofStringType is true!");
		
		////////////////
		StringBuilder sb = new StringBuilder();
		
		if(ofStringType) {
			if(!toIgnoreCase) {
				sb.append(quoteColumnName(colName)).append("=");
				sb.append("'").append(valueString).append("'");
			}else {
				sb.append("UPPER(").append(colName).append(")").append("="); //
				sb.append("UPPER(").append("'").append(valueString).append("'").append(")");
			}
		}else {
			sb.append(quoteColumnName(colName)).append("=");
			sb.append(valueString);
		}
		
		return sb.toString();
	}
	
//	/**
//	 * build and return a column value equity condition string;
//	 * need to put string type column's value in single quote;
//	 * @param colName
//	 * @param valueString
//	 * @param ofStringType
//	 */
//	public static String buildColumnValueEquityCondition(String colName, String valueString, boolean ofStringType, Boolean ignoreCase) {
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(quoteColumnName(colName)).append("=");
//		if(ofStringType) {
//			sb.append("'").append(valueString).append("'");
//		}else {
//			sb.append(valueString);
//		}
//		
//		return sb.toString();
//	}
	
	/**
	 * build and return a column value equity condition string for the given list(with at least 2 elements) of columns and linking logical operators;
	 * need to put string type column's value in single quote;
	 * 
	 * @param colNameList at least 2 elements
	 * @param valueStringList list of value strings for each of the columns in the colNameList with the same index; must be same size with colNameList
	 * @param ofStringTypeList list of boolean value whether each of the columns are of string type or not in the colNameList with the same index;
	 * @param toIgnoreCaseList list of boolean value whether to ignore case; value should be non-null if corresponding element in ofStringTypeList is true; must be null otherwise;
	 * @param logicOperatorList list of binary logical operators; the one at index i links the columns at index i and i+1; thus size must be 1 less than colNameList
	 * @return
	 */
	public static String buildColumnValueEquityCondition(List<String> colNameList, List<String> valueStringList, List<Boolean> ofStringTypeList, List<Boolean> toIgnoreCaseList, List<LogicOperator> logicOperatorList) {
		if(colNameList.size()<2)
			throw new IllegalArgumentException("given colNameList's size must be equal to or larger than 2!");
		
		if(colNameList.size()!=valueStringList.size())
			throw new IllegalArgumentException("given colNameList and valueStringList must have same size!");
		
		if(colNameList.size()!=ofStringTypeList.size())
			throw new IllegalArgumentException("given colNameList and ofStringTypeList must have same size!");
		
		if(colNameList.size()!=toIgnoreCaseList.size())
			throw new IllegalArgumentException("given colNameList and toIgnoreCaseList must have same size!");
		
		for(int i=0;i<ofStringTypeList.size();i++) {
			if(ofStringTypeList.get(i) && toIgnoreCaseList.get(i)==null)
				throw new IllegalArgumentException("element in toIgnoreCaseList should be non-null if corresponding element in ofStringTypeList is true!");
		}
		
		if(colNameList.size()!=logicOperatorList.size()+1)
			throw new IllegalArgumentException("given colNameList must have size equal to the size of given logicOperatorList + 1!");
		
		logicOperatorList.forEach(lo->{
			if(lo.isUnary())
				throw new IllegalArgumentException("logical operator cannot be unary!");
		});
		
		
		StringBuilder sb = new StringBuilder();
		
		
		for(int i=0;i<colNameList.size();i++) {
			
			sb.append(buildColumnValueEquityCondition(colNameList.get(i), valueStringList.get(i), ofStringTypeList.get(i), toIgnoreCaseList.get(i)));
			if(i<colNameList.size()-1) {
				//not the last one
				sb.append(" ").append(logicOperatorList.get(i)).append(" ");
			}else {//last one
				//no logical operator
			}
			
		}
		
		return sb.toString();
	}
	
	
	/**
	 * build column value equity condition string with a set of value strings linked by a type of LogicOperator;
	 * 
	 * @param colName
	 * @param valueStringSet cannot be null or empty; must be of size 1 if given operator is unary; otherwise, can have any positive number of elements including 1;
	 * @param ofStringType
	 * @param toIgnoreCase must be non-null if ofStringType is true; must be null otherwise;
	 * @param operator
	 * @return
	 */
	public static String buildColumnValueEquityCondition(String colName, Set<String> valueStringSet, boolean ofStringType, Boolean toIgnoreCase, LogicOperator operator) {
		
		if(valueStringSet==null||valueStringSet.isEmpty()) {
			throw new IllegalArgumentException("given valueStringSet cannot be null or empty!");
		}
		
		if(operator.isUnary()&&valueStringSet.size()>1) {
			throw new IllegalArgumentException("given operator is unary while there are multiple values!");
		}
		
		///
		if(operator.isUnary()) {
			return operator.buildSqlConditionString(
					buildColumnValueEquityCondition(colName, valueStringSet.iterator().next(), ofStringType, toIgnoreCase), 
					null);
		}else {
			String ret = "";
			boolean nothingAddedYet = true;
			
			for(String valueString:valueStringSet) {
				if(nothingAddedYet) {
					nothingAddedYet = false;
					ret = ret.concat(buildColumnValueEquityCondition(colName, valueString, ofStringType, toIgnoreCase));
				}else {
					ret = operator.buildSqlConditionString(
							ret, 
							buildColumnValueEquityCondition(colName, valueString, ofStringType, toIgnoreCase));
				}
			}
			
			return ret;
		}
		
	}
	
	/**
	 * build and return a sql string for equity conditions for a PrimaryKeyID object;
	 * @param primaryKeyIDNameColumnMap
	 * @param id
	 * @return
	 */
	public static String buildIDEquityConditionString(Map<SimpleName, ManagementTableColumn> primaryKeyIDNameColumnMap, PrimaryKeyID<? extends VisframeUDT> id) {
		StringBuilder sb = new StringBuilder();
		
		boolean nothingAddedYet = true;
		
		for(SimpleName primaryKeyColumnName:primaryKeyIDNameColumnMap.keySet()) {
			ManagementTableColumn column = primaryKeyIDNameColumnMap.get(primaryKeyColumnName);
			if(nothingAddedYet) {
				nothingAddedYet = false;
			}else {
				sb.append(" AND ");
			}
			
			
			//
			sb.append(
					buildColumnValueEquityCondition(
							primaryKeyColumnName.getStringValue(), 
							id.getPrimaryKeyAttributeNameStringValueMap().get(primaryKeyColumnName),
							column.getSqlDataType().isOfStringType(),
							column.getSqlDataType().isOfStringType()?id.getPrimaryKeyAttributeNameToIgnoreCaseMap().get(primaryKeyColumnName):null
							)
					);
			
//			sb.append(primaryKeyColumnName.getStringValue()).append(" = ");
//			
//			if(column.getSqlDataType().isOfStringType()) {
//				sb.append("'").append(id.getPrimaryKeyAttributeNameStringValueMap().get(primaryKeyColumnName)).append("'");
//			}else {
//				sb.append(id.getPrimaryKeyAttributeNameStringValueMap().get(primaryKeyColumnName));
//			}
			
		}
		
		return sb.toString();
		
	}
	
	/**
	 * 
	 * @param singleTargetTableFullPathString
	 * @return
	 */
	public static String buildSelectAllWithoutConditionString(String singleTargetTableFullPathString) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT * FROM ").append(singleTargetTableFullPathString);
		
		return sb.toString();
	}
	
	
	/**
	 * build and return a sql query string that select all records from a set of tables with a condition string;
	 * @param targetTableString may contain full path name of single or multiple tables
	 * @param conditionString a condition string containing one or multiple condition component linked by logical operators
	 * @return
	 */
	public static String buildSelectAllWithConditionString(String targetTableString, String conditionString) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT * FROM ").append(targetTableString).append(" WHERE ").append(conditionString);
		
		return sb.toString();
	}
	
	
	/**
	 * build and return a sql string for PreparedStatemet that insert a record into a table:
	 * INSERT INTO table VALUES (?,?,? ...);//number of ? should be equal to the given columnNum
	 * @param targetTableString
	 * @param columnNum
	 * @return
	 */
	public static String buildInsertIntoPreparedStatementSqlString(String targetTableFullPathString, int columnNum) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(targetTableFullPathString).append(" VALUES (");
		
		boolean nothingAddedYet = true;
		for(int i = 0;i<columnNum;i++) {
			if(nothingAddedYet) {
				nothingAddedYet = false;
			}else {
				sb.append(",");
			}
			
			sb.append("?");
		}
		sb.append(")");
		
		return sb.toString();
		
	}
	
	
	public static String buildInsertIntoPreparedStatementSqlString(String targetTableFullPathString, List<String> columnNameListToBeInserted) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(targetTableFullPathString).append(" (");
		
		
		//first add the column names
		boolean nothingAddedYet = true;
		for(String colName:columnNameListToBeInserted) {
			if(nothingAddedYet) {
				nothingAddedYet = false;
			}else {
				sb.append(",");
			}
			
			sb.append(quoteColumnName(colName));
		}
		
		sb.append(" ) VALUES (");
		
		//then add one ? for each column
		nothingAddedYet = true;
		for(int i = 0;i<columnNameListToBeInserted.size();i++) {
			if(nothingAddedYet) {
				nothingAddedYet = false;
			}else {
				sb.append(",");
			}
			
			sb.append("?");
		}
		
		sb.append(")");
		
		return sb.toString();
	}
	
	/**
	 * build and return a sql string for PreparedStatement to update a set of column values of a table with a condition
	 * UPDATE table_name SET column1 = value1, column2 = value2, ... WHERE condition;
	 * @param targetTableFullPathString
	 * @param columnNameListToBeUpdated
	 * @param conditionString
	 * @return
	 */
	public static String buildUpdateColumnsPreparedStatementSqlString(String targetTableFullPathString, List<String> columnNameListToBeUpdated, String conditionString) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("UPDATE ").append(targetTableFullPathString).append(" SET ");
		
		boolean nothingAddedYet = true;
		
		for(String columnName:columnNameListToBeUpdated) {
			if(nothingAddedYet) {
				nothingAddedYet = false;
			}else {
				sb.append(",");
			}
			
			sb.append(quoteColumnName(columnName)).append(" = ? ");
		}
		
		sb.append("WHERE ").append(conditionString);
		
		return sb.toString();
	}
	
	/**
	 * DELETE FROM table_name WHERE condition;
	 * @param targetTableFullPathString
	 * @param conditionString
	 * @return
	 */
	public static String buildDeleteWithConditionSqlString(String targetTableFullPathString, String conditionString) {
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ").append(targetTableFullPathString).append(" WHERE ").append(conditionString);
		return sb.toString();
	}
	
	
	/**
	 * build and return the string content for GROUP BY clause for the given list of column names;
	 * @param columnNameList
	 * @return
	 */
	public static String buildGroupByClauseContentSqlString(List<String> columnNameList) {
		StringBuilder sb = new StringBuilder();
		
		boolean nothingAddedYet = true;
		
		for(String colName:columnNameList) {
			if(nothingAddedYet) {
				nothingAddedYet = false;
			}else {
				sb.append(", ");
			}
			sb.append(quoteColumnName(colName));
		}
		return sb.toString();
	}
	
	/**
	 * build and return the string content for ORDER BY clause for the given list of column names;
	 * 
	 * if given list is empty, return empty string;
	 * 
	 * @param columnNameList
	 * @return
	 */
	public static String buildOrderByClauseContentSqlString(List<String> columnNameList, List<Boolean> orderByASCList) {
		StringBuilder sb = new StringBuilder();
		
		boolean nothingAddedYet = true;
		
		for(int i = 0;i<columnNameList.size();i++) {
			if(nothingAddedYet) {
				nothingAddedYet = false;
			}else {
				sb.append(", ");
			}
			
			sb.append(quoteColumnName(columnNameList.get(i))).append(orderByASCList.get(i)?" ASC":" DESC");
		}
		return sb.toString();
	}
	
	
	/**
	 * build and return a SQL query that retrieve a set of rows based on given range;
	 * 
	 * basic template: 
	 * 
	 * SELECT * FROM (
	 * 		SELECT 
	 * 			ROW_NUMBER() OVER () AS R, 
	 * 			T.* 
	 * 		FROM T
	 * 		) t2
	 * WHERE R <= 10; 
	 * 
	 * see https://db.apache.org/derby/docs/10.4/ref/rreffuncrownumber.html
	 * 
	 * @param targetTableFullPathString
	 * @param start start (inclusive) of row range
	 * @param end end (inclusive) of row range
	 * @param selectedColumnList columns to be selected; if null, select all;
	 * @param conditionString condition; if null, skip
	 * @param orderByColumnNameList columns for ORDER BY clause; if null, skip
	 * @param orderByASCList for orderByColumnNameList 
	 * @return
	 */
	public static String buildSelectRowByRangeSQLString(
			String targetTableFullPathString, int start, int end, List<String> selectedColumnList, 
			String conditionString, List<String> orderByColumnNameList, List<Boolean> orderByASCList) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		if(selectedColumnList==null) {
			sb.append("* FROM (");
		}else {
			sb.append(buildListOfColumnNameString(selectedColumnList)).append(" FROM (");
		}
		
		sb.append("SELECT ROW_NUMBER() OVER () AS R, ");
		if(selectedColumnList==null) {
			sb.append(targetTableFullPathString).append(".* FROM "); //all columns
		}else {
			sb.append(buildListOfColumnNameString(selectedColumnList)).append(" FROM "); //
		}
		sb.append(targetTableFullPathString);
		sb.append(") t2 ");
		
		sb.append(" WHERE");
		sb.append(" R>=").append(Integer.toString(start));
		sb.append(" AND R<=").append(Integer.toString(end));
		
		if(conditionString!=null) {
			sb.append(" AND ").append(conditionString);
		}
		
		if(orderByColumnNameList!=null) {
			sb.append(" ORDER BY ").append(buildOrderByClauseContentSqlString(orderByColumnNameList, orderByASCList));
		}
		
		return sb.toString();
	}
	
	
	
	/**
	 * build a SELECT * FROM ... string 
	 * condition string is optional, if null, skip it;
	 * columnNameList is optional, if null, skip it;
	 * 
	 * @param targetTableFullPathString
	 * @param conditionString
	 * @param orderByColumnNameList
	 * @return
	 */
	public static String buildSelectAllSQLString(String targetTableFullPathString, String conditionString, List<String> orderByColumnNameList, List<Boolean> orderByASCList) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM ").append(targetTableFullPathString);
		
		if(conditionString!=null) {
			sb.append(" WHERE ").append(conditionString);
		}
		
		if(orderByColumnNameList!=null) {
			
			sb.append(" ORDER BY ").append(buildOrderByClauseContentSqlString(orderByColumnNameList, orderByASCList));
		}
		
		return sb.toString();
	}
	
	
	
	/**
	 * copy the full set of column data from the source table column to the target table column;
	 * 
	 * note that the constraints of the target column should be obeyed, otherwise the sql cannot run;
	 * 		for example, if the target column is the only primary key column, the data in the source column should all be non-null and unique;
	 * 
	 * 
	 * template  
	 * insert into TargetSchema.TargetTable (TargetCol) select SourceCol from SourceSchema.SourceTable;
	 * 
	 * 
	 * @param sourceTableSchemaID
	 * @param sourceColumnName
	 * @param targetTableSchemaID
	 * @param targetColumnName
	 * @return
	 */
	public static String buildCopyColumnDataOfATableToColumnOfBTableSqlQueryString(
			AbstractRelationalTableSchemaID sourceTableSchemaID, String sourceColumnName,  
			AbstractRelationalTableSchemaID targetTableSchemaID, String targetColumnName) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("INSERT INTO ")
		.append(targetTableSchemaID.getSchemaName().getStringValue()).append(".").append(targetTableSchemaID.getTableName().getStringValue())//target table
		.append(" (").append(quoteColumnName(targetColumnName)).append(")") //target column
		.append(" SELECT ")
		.append(quoteColumnName(sourceColumnName))
		.append(" FROM ")
		.append(sourceTableSchemaID.getSchemaName().getStringValue()).append(".").append(sourceTableSchemaID.getTableName().getStringValue());
		
		return sb.toString();
	}
	
	
	/**
	 * build the SET clause string for the given single column;
	 * 
	 * 		UPDATE table_name
	 * 		SET column1 = value1, column2 = value2, ... //SET clause;
	 * 		WHERE condition;
	 * 
	 * @param colName
	 * @param valueString
	 * @param ofStringType
	 * @return
	 */
	public static String buildSetSingleColumnValueClauseSqlString(String colName, String valueString, Boolean ofStringType) {
		StringBuilder sb = new StringBuilder();
		sb.append("SET ");
		
		sb.append(quoteColumnName(colName)).append("=");
		
		if(ofStringType) {
			sb.append("'").append(valueString).append("'");
		}else {
			sb.append(valueString);
		}
		
		return sb.toString();
	}
	
	/**
	 * build the SET clause string for the given set of columns;
	 * 
	 * 		UPDATE table_name
	 * 		SET column1 = value1, column2 = value2, ... //SET clause;
	 * 		WHERE condition;
	 * 
	 * @param colNameList
	 * @param valueStringList
	 * @param ofStringTypeList
	 * @return
	 */
	public static String buildSetColumnValueClauseSqlString(List<String> colNameList, List<String> valueStringList, List<Boolean> ofStringTypeList) {
		StringBuilder sb = new StringBuilder();
		sb.append("SET ");
		
		
		for(int i=0;i<colNameList.size();i++) {
			if(i==0) {
				//
			}else {
				sb.append(",");
			}
			
			sb.append(quoteColumnName(colNameList.get(i).toUpperCase())).append("=");
			
			if(ofStringTypeList.get(i)) {
				sb.append("'").append(valueStringList.get(i)).append("'");
			}else {
				sb.append(valueStringList.get(i));
			}
			
		}
		
		return sb.toString();
	}
	
	
}
