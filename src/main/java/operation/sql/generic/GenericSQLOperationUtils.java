package operation.sql.generic;

import java.util.ArrayList;
import java.util.List;

import rdb.table.data.DataTableSchemaFactory;
import sql.derby.TableContentSQLStringFactory;

/**
 * see GenericSQLOperationUtils.sql for more details;
 * 
 * @author tanxu
 *
 */
public class GenericSQLOperationUtils {
	public static final String ROW_NUMBER_COL_ALIAS = "RN";
	public static final String SELECT_ELEMENT_ALIAS_PREFIX = "C";
	/**
	 * add 'ROW_NUMBER() over () as RN' to the beginning SELECT clause as the first element/term;
	 * 
	 * note that in nested SQL string, there might be multiple SELECT clause (with one at the beginning, others the subqueries);
	 * 
	 * 
	 * for example, 
	 * 		input string: SELECT A, B, C FROM ... 
	 * 		returned string: SELECT ROW_NUMBER() over () as RN, A, B, C FROM ... 
	 * 
	 * note that the elements A, B, C in SELECT clause could be 
	 * 1. single column
	 * 2. aggregate function
	 * 3. logical or numeric expression involving one or multiple columns;
	 * 
	 * also each element may be in format:
	 * expression alias_name;
	 * 
	 * for example: SELECT t1.col1 c1, t2.col5 c2, t3.col2 c3 FROM ...
	 * c1, c2 and c3 are the alias of the corresponding expression/term in SELECT clause; 
	 * 
	 * @param SQLString
	 * @return
	 */
	public static String addRowNumberCol(String SQLString) {
		if(!SQLString.toUpperCase().startsWith("SELECT"))
			throw new IllegalArgumentException("given SQLString must start with 'SELECT' keyword!");
		
		return "SELECT ROW_NUMBER() OVER () AS RN," .concat(SQLString.substring(6));
	}
	
	
	/**
	 * add alias to each expression/term in the first SELECT clause;
	 * the alias is built by {@link #SELECT_ELEMENT_ALIAS_PREFIX} and an integer index starting from 1;
	 * 
	 * 
	 * for example, 
	 * 		input string: SELECT A, B, C FROM ... 
	 * 		returned string: SELECT A C1, B C2, C C3 FROM ... 
	 * 
	 * note that this should be invoked before the {@link #addRowNumberCol(String)} method;
	 * 
	 * @param SQLString
	 * @return
	 */
	public static String addAliasToEachTermInSelectClause(String SQLString) {
		if(!SQLString.toUpperCase().startsWith("SELECT"))
			throw new IllegalArgumentException("given SQLString must start with 'SELECT' keyword!");
		
		
		String[] splitByFirstFrom = SQLString.split("(?i)FROM", 2);//split by first FROM key word; ignore case;
		
		String[] splitByComma = splitByFirstFrom[0].split(",");
		
		
		StringBuilder sb = new StringBuilder();
		boolean firstAdded = false;
		
		for(int i=0;i<splitByComma.length;i++) {
			if(firstAdded) {
				sb.append(",");
			}
			
			sb.append(splitByComma[i]).append(" AS ").append(makeSelectExpressionAlias(i+1));
			
			if(!firstAdded)
				firstAdded= true;
		}
		
		sb.append(" FROM ").append(splitByFirstFrom[1]);
		
		
		return sb.toString();
	}
	
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public static String makeSelectExpressionAlias(int index) {
		return SELECT_ELEMENT_ALIAS_PREFIX.concat(Integer.toString(index));
	}
	

	/**
	 * build and return a SQL query string in the following format:
	 * 
	 * 		SELECT C1, C2, ... //list of column alias to be put in output data table
	 * 		FROM (processedUserDefinedSQLString) T1//with user defined table/column alias replaced with real names and row number column added and alias of terms in SELECT clause added;
	 * 		WHERE RN IN //row number column of T1
	 * 		(
	 * 			SELECT MIN(RN)
	 * 			FROM (processedUserDefinedSQLString) T2//exactly the same with T1
	 * 			WHERE Ci IS NOT NULL AND Cj IS NOT NULL ... //Ci, Cj, ... are the columns corresponding to primary key columns in output data table schema
	 * 			GROUP BY Ci, Cj, ... //Ci, Cj, ... are the columns corresponding to primary key columns in output data table schema
	 * 		)
	 * 
	 * ================================================
	 * note that the given processedUserDefinedSQLString should be 
	 * 1. first replace the user defined alias ID of tables and columns with real names in the host VisProjectDBContext;
	 * 		see {@link GenericSQLQueryProcessor}
	 * 2. then add alias to each term in the SELECT clause (in cases where there are nested queries, only apply to the outmost SELECT clause!!)
	 * 		{@link #addAliasToEachTermInSelectClause(String)}
	 * 3. add row number column and alias to the SELECT clause (in cases where there are nested queries, only apply to the outmost SELECT clause!!)
	 * 		{@link #addRowNumberCol(String)}
	 * 
	 * 
	 * @param processedUserDefinedSQLString full processed;
	 * @param totalColNum total number of columns of output data table column (except for RUID column);
	 * @param primaryKeyColIndexList list of index (first one is 1) of primary key columns in the output data table schema;
	 * @return
	 */
	public static String buildSelectUniqueRowsSQLQueryString(String processedUserDefinedSQLString, int totalColNum, List<Integer> primaryKeyColIndexList) {
		StringBuilder sb = new StringBuilder();
		
		//
		List<String> outputTableColAliasNameList = new ArrayList<>();
		for(int i=1;i<=totalColNum;i++) 
			outputTableColAliasNameList.add(makeSelectExpressionAlias(i)); 
		
		//main query
		sb.append("SELECT ").append(TableContentSQLStringFactory.buildListOfColumnNameString(outputTableColAliasNameList));//this will invoke the quoteColumnName(String) method
		sb.append(" FROM (").append(processedUserDefinedSQLString).append(") T1");
		sb.append(" WHERE RN IN");
		
		//sub query
		sb.append(" (SELECT MIN(RN)");
		sb.append(" FROM (").append(processedUserDefinedSQLString).append(") T2");
		//
		sb.append(" WHERE ");
		List<String> quotedPrimaryKeyColAliasNameList = new ArrayList<>();
		primaryKeyColIndexList.forEach(i->{
			quotedPrimaryKeyColAliasNameList.add(TableContentSQLStringFactory.quoteColumnName(makeSelectExpressionAlias(i-1)));
		});
		boolean firstAdded = false;
		for(String colName: quotedPrimaryKeyColAliasNameList){
			if(firstAdded)
				sb.append(" AND ");
			
			sb.append(colName).append(" IS NOT NULL");
			
			if(!firstAdded)
				firstAdded = true;
		}
		
		//
		sb.append(" GROUP BY ");
		firstAdded = false;
		for(String name: quotedPrimaryKeyColAliasNameList){
			if(firstAdded)
				sb.append(", ");
			
			sb.append(name);
			
			if(!firstAdded)
				firstAdded = true;
		}
		sb.append(")");
		//
		
		return sb.toString();
	}
	
	
	/**
	 * 
	 * build and return a SQL string with the following format:
	 * 
	 * 		INSERT INTO outputTableFullPathString 
	 * 			(outputTableColumnNameList[0], outputTableColumnNameList[1], outputTableColumnNameList[2], ...) 
	 * 		selectFromSQLQueryString; 
	 * 
	 * =====================================
	 * note that the terms in the outmost SELECT clause of selectFromSQLQueryString must be consistent with the columns in the given outputTableColumnNameList;
	 * 
	 * the column list needs to be explicitly added because RUID column is not included in the selectFromSQLQueryString but always present in the output data table schema
	 * and it will be auto-populate with the default constraints;
	 * 		see {@link DataTableSchemaFactory#makeRUIDColumn()}
	 * 
	 * 
	 * @param outputTableFullPathString
	 * @param outputTableColumnNameStringList
	 * @param selectFromSQLQueryString
	 * @return
	 */
	public static String buildInsertIntoOutputDataTableSchemaSQLStringWithResultOfSelectFromSQLQuery(String outputTableFullPathString, List<String> outputTableColumnNameList, String selectFromSQLQueryString) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(outputTableFullPathString);
		
		sb.append(" (");
		sb.append(TableContentSQLStringFactory.buildListOfColumnNameString(outputTableColumnNameList));
		sb.append(")");
		sb.append(" ").append(selectFromSQLQueryString);
		
		return sb.toString();
	}
}	
