package operation.sql.generic.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import exception.ThrowingErrorListener;
import listener.FullColumnNameListener;
import listener.TableNameListener;
import mysql.MySqlLexer;
import mysql.MySqlParser;
import mysql.MySqlParser.DmlStatementContext;
import utils.SQLStringParserUtils;

/**
 * for the customized SQL query string of a GenericSQLOperation
 * 
 * 1. validation of constraints 
 * 		all columns must be in form of 'TABLE_NAME.COLUMN_NAME' (unchecked by this class);
 * 		no '*' is allowed in SELECT clause (checked);
 * 		no 'AS' is allowed in SELECT clause (checked);
 * 		no alias is allowed???
 * 		no nested query allowed;
 * 		must start with SELECT ... FROM;
 * 
 * 2. extract table alias names and column alias names and the start and end position of each appearance;
 * 		1. extract table names and their start index with {@link SQLStringParserUtils#extractTableNameStringSetFromSqlQuery(String)} method;
 * 			note that this method will only recognized those table names that are in fullColumnName of form TABLE_NAME.COLUMN_NAME;
 * 			specifically, those table names should all be from the FROM clause;
 * 
 * 		2. extract fullColumnNames and their start index;
 * 				1. if fullColumnName is not in TABLE_NAME.COLUMN_NAME form (DottedFullColumnName), it must be the parameter for aggregate function COUNT; otherwise, invalid sql for this class;
 * 		
 * 		3. identify the table name and column name for each dotted column names
 * 
 * 3. for each table name
 * 
 * 4. generate a runnable sql by replacing alias names with real table and column names
 * 
 * ==================================
 * input sql string can contain lower case for table and column names;
 * 
 * but the identified table names and column names will all be in upper case!
 * 
 * 
 * @author tanxu
 *
 */
public class GenericSQLQueryProcessor {
	private final String singleLineSQLQueryString;
	
	/////////////////////////////
//	/**
//	 * 
//	 */
//	private int selectExpressionNum;
//	
//	/**
//	 * 
//	 */
//	private List<String> selectExpressionStringList;
	
	
	/**
	 * table names that are not a part of a full column name;
	 * specifically, table names in FROM clause and table name in COUNT function;
	 */
	private List<Pair<String, Pair<Integer, Integer>>> tableNameStringPosPairList;
	
	/**
	 * table name set in tableNameStringPosPairList;
	 * 
	 * this should contain all the table names in the input sql string;
	 */
	private Set<String> tableNameSet;
	
	
	/**
	 * dotted column names in format TABLE_NAME.COLUMN_NAME;
	 * 
	 */
	private List<Pair<DottedFullColumnName, Pair<Integer, Integer>>> dottedFullColumnNamePosPairList;
	
	/**
	 * map from table alias name to the set of DottedFullColumnName;
	 * 
	 * each map value set should be non-null but can be empty?;
	 * 
	 * the map is consistent with {@link #dottedFullColumnNamePosPairList};
	 * 
	 * this should contain all columns in the input sql string;
	 */
	private Map<String, Set<DottedFullColumnName>> tableNameDottedFullColumnNameSetMap;
	
	
	/**
	 * constructor
	 * @param sqlQueryString a sql query string that result in a single table view;
	 */
	public GenericSQLQueryProcessor(String sqlQueryString) throws ParseCancellationException{
		//single line sql
		this.singleLineSQLQueryString = sqlQueryString.replaceAll("\\n", " ").trim();
		
		this.basicValidate();
		
		//extract table names
		this.extractTableNames();
		//extract dotted column names
		this.extractDottedFullColumnNames();
	}
	
	/**
	 * cannot contain 'AS', '*' in select clause;
	 */
	private void basicValidate() {
		if(!SQLStringParserUtils.isQuerySpecification(this.singleLineSQLQueryString))
			throw new ParseCancellationException("given sql string is not valid!");
		
		String selectElementsString = SQLStringParserUtils.getSelectElementsString(this.singleLineSQLQueryString.toUpperCase());
		if(SQLStringParserUtils.containsSelectStarElement(selectElementsString) || selectElementsString.contains("*")) {
			throw new ParseCancellationException("cannot contain * in select clause");
		}
		
		if(SQLStringParserUtils.selectClauseContainsASKeyword(this.singleLineSQLQueryString.toUpperCase())) {
			throw new ParseCancellationException("cannot contain 'AS' in select clause");
		}
		
	}
	
	/**
	 * extract all table names
	 * 
	 * this will only recognize table names in FROM clause;
	 */
	private void extractTableNames() {
		this.tableNameSet = new LinkedHashSet<>();
		this.tableNameDottedFullColumnNameSetMap = new LinkedHashMap<>();
		
		MySqlLexer lexer = new MySqlLexer(CharStreams.fromString(this.singleLineSQLQueryString.toUpperCase()));
		MySqlParser parser = new MySqlParser(new CommonTokenStream(lexer));
		
		lexer.removeErrorListeners();
		lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
		
		parser.removeErrorListeners();
		parser.addErrorListener(ThrowingErrorListener.INSTANCE);
		
		
		DmlStatementContext dmlStatementContext = parser.dmlStatement();
		
		
		ParseTreeWalker walker = new ParseTreeWalker();
		TableNameListener listener = new TableNameListener();
		walker.walk(listener, dmlStatementContext);
		
		tableNameStringPosPairList = new ArrayList<>();
		
		listener.getIdentifiedTargetStringStartEndPosPairList().forEach(p->{
			String tableName = this.singleLineSQLQueryString.substring(p.a, p.b+1).toUpperCase();
			
			tableNameStringPosPairList.add(new Pair<>(tableName, p));
		});;
		
		
		
		tableNameStringPosPairList.forEach(e->{
			this.tableNameSet.add(e.a);
			this.tableNameDottedFullColumnNameSetMap.put(e.a, new LinkedHashSet<>());
		}); 
		
	}
	
	
	/**
	 * extract all fullColumnName structure from SELECT, WHERE, GROUP BY clauses;
	 * 
	 * note that table name in COUNT function will be recognized as full column name!
	 */
	private void extractDottedFullColumnNames() {
		MySqlLexer lexer = new MySqlLexer(CharStreams.fromString(this.singleLineSQLQueryString.toUpperCase()));
		MySqlParser parser = new MySqlParser(new CommonTokenStream(lexer));
		
		lexer.removeErrorListeners();
		lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
		
		parser.removeErrorListeners();
		parser.addErrorListener(ThrowingErrorListener.INSTANCE);
		
		
		DmlStatementContext dmlStatementContext = parser.dmlStatement();
		
		
		ParseTreeWalker walker = new ParseTreeWalker();
		FullColumnNameListener listener = new FullColumnNameListener();
		walker.walk(listener, dmlStatementContext);
		
		dottedFullColumnNamePosPairList = new ArrayList<>();
		
		
		listener.getIdentifiedTargetStringStartEndPosPairList().forEach(e->{
			String fullColName = this.singleLineSQLQueryString.substring(e.a, e.b+1).toUpperCase();
			
			if(fullColName.contains(".")) {
				String[] splits = fullColName.split("\\.");
				String tableName = splits[0];
				String colName = splits[1];
				
				if(!this.tableNameSet.contains(tableName))
					throw new ParseCancellationException("full colum name whose table name is not identified properly:"+fullColName);
				
				DottedFullColumnName dottedFullColName = new DottedFullColumnName(tableName,colName);
				
				dottedFullColumnNamePosPairList.add(new Pair<>(dottedFullColName, e));
				tableNameDottedFullColumnNameSetMap.get(tableName).add(dottedFullColName);
				
			}else {//fullColumnName not contain '.', must be table name in COUNT function
				if(!this.tableNameSet.contains(fullColName))
					throw new ParseCancellationException("fullColumnName without . is not in table name set:"+e.a);
			}
			
		});
		
		
	}
	
	///////////////////////////////
	/**
	 * return number of select element
	 * @return
	 */
	public int getSelectElementNum() {
		//TODO retrieve the terms between the left-most SELECT and FROM keywords???
		return SQLStringParserUtils.getNumOfSelectElement(this.singleLineSQLQueryString);
	}
	
	
	
	/**
	 * replace the table names and column names in the original sql string and return a new string;
	 * 
	 * @param tableNameReplacementMap map from the replaced table name to the replacing table name for those in {@link #tableNameSet}
	 * @param dottedFullColumnNameReplacementMap dotted full column name and replacing string for those in {@link #tableNameDottedFullColumnNameSetMap}
	 * @return
	 */
	public String replace(Map<String, String> tableNameReplacementMap, Map<DottedFullColumnName, String> dottedFullColumnNameReplacementMap) {
		Map<Integer, Integer> startEndIndexMap = new TreeMap<>(); //map key sorted by natural order, for integers, sorted from small to big
		Map<Integer, String> startIndexReplacedStringMap = new HashMap<>();
		
		Map<String, String> replacedStringReplacingStringMap = new HashMap<>();
		tableNameReplacementMap.forEach((k,v)->{
			replacedStringReplacingStringMap.put(k.toUpperCase(), v.toUpperCase());
		});
		
		dottedFullColumnNameReplacementMap.forEach((k,v)->{
			replacedStringReplacingStringMap.put(k.toString().toUpperCase(), v.toUpperCase());
		});
		
		
		///////////////
		this.tableNameStringPosPairList.forEach(e->{
			startEndIndexMap.put(e.b.a, e.b.b);
			startIndexReplacedStringMap.put(e.b.a, e.a.toUpperCase());
		});
		
		this.dottedFullColumnNamePosPairList.forEach(e->{
			startEndIndexMap.put(e.b.a, e.b.b);
			startIndexReplacedStringMap.put(e.b.a, e.a.toString().toUpperCase());
		});
		
		//
		Iterator<Entry<Integer,Integer>> startEndIndexMapIterator = startEndIndexMap.entrySet().iterator();
		Entry<Integer,Integer> first = startEndIndexMapIterator.next();
		//add the beginning substring before the first replaced segment
		String ret = this.singleLineSQLQueryString.substring(0, first.getKey());
		//add the first replacing segment
		ret = ret.concat(replacedStringReplacingStringMap.get(startIndexReplacedStringMap.get(first.getKey())));
		
		int previousEndIndex = first.getValue();
		while(startEndIndexMapIterator.hasNext()) {
			Entry<Integer,Integer> next = startEndIndexMapIterator.next();
			ret = ret.concat(this.singleLineSQLQueryString.substring(previousEndIndex+1, next.getKey()));
			ret = ret.concat(replacedStringReplacingStringMap.get(startIndexReplacedStringMap.get(next.getKey())));
			previousEndIndex = next.getValue();
		}
		
		//all remaining substring
		ret = ret.concat(this.singleLineSQLQueryString.substring(previousEndIndex+1));
		
		return ret;
	}
	
	/////////////////////////////////////////////////
	/**
	 * @return the singleLineSqlQueryString
	 */
	public String getSingleLineSqlQueryString() {
		return singleLineSQLQueryString;
	}

	/**
	 * @return the tableNameStringPosPairList
	 */
	public List<Pair<String, Pair<Integer, Integer>>> getTableNameStringPosPairList() {
		return tableNameStringPosPairList;
	}

	/**
	 * @return the tableNameSet
	 */
	public Set<String> getTableNameSet() {
		return tableNameSet;
	}

	/**
	 * @return the dottedFullColumnNamePosPairList
	 */
	public List<Pair<DottedFullColumnName, Pair<Integer, Integer>>> getDottedFullColumnNamePosPairList() {
		return dottedFullColumnNamePosPairList;
	}

	/**
	 * @return the tableNameDottedFullColumnNameSetMap
	 */
	public Map<String, Set<DottedFullColumnName>> getTableNameDottedFullColumnNameSetMap() {
		return tableNameDottedFullColumnNameSetMap;
	}
	
	
	
	////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((singleLineSQLQueryString == null) ? 0 : singleLineSQLQueryString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof GenericSQLQueryProcessor))
			return false;
		GenericSQLQueryProcessor other = (GenericSQLQueryProcessor) obj;
		if (singleLineSQLQueryString == null) {
			if (other.singleLineSQLQueryString != null)
				return false;
		} else if (!singleLineSQLQueryString.equals(other.singleLineSQLQueryString))
			return false;
		return true;
	}


	/////////////////////////////////
	/**
	 * full column name corresponding to the fullColumnName rule of MySqlParser in the form of TABLE_NAME.COLUMN_NAME;
	 * 
	 * @author tanxu
	 *
	 */
	public static class DottedFullColumnName{
		private final String tableName;
		private final String colName;
		
		/**
		 * constructor
		 * @param tableName
		 * @param colName
		 */
		public DottedFullColumnName(String tableName,String colName){
			this.tableName = tableName;
			this.colName = colName;
		}
		

		/**
		 * @return the tableName
		 */
		public String getTableName() {
			return tableName;
		}


		/**
		 * @return the colName
		 */
		public String getColName() {
			return colName;
		}


		@Override
		public String toString() {
			return tableName.concat(".").concat(colName);
		}


		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((colName == null) ? 0 : colName.hashCode());
			result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
			return result;
		}


		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof DottedFullColumnName))
				return false;
			DottedFullColumnName other = (DottedFullColumnName) obj;
			if (colName == null) {
				if (other.colName != null)
					return false;
			} else if (!colName.equals(other.colName))
				return false;
			if (tableName == null) {
				if (other.tableName != null)
					return false;
			} else if (!tableName.equals(other.tableName))
				return false;
			return true;
		}
		
	}
}
