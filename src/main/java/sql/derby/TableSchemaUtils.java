package sql.derby;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import basic.SimpleName;
import basic.VfNameString;
import context.project.rdb.VisProjectRDBConstants;
import rdb.table.AbstractRelationalTableColumn;
import rdb.table.AbstractRelationalTableSchema;
import rdb.table.data.DataTableName;
import rdb.table.value.ValueTableName;
import sql.SQLStringUtils;

/**
 * related with table create, drop, alter, etc
 * 
 * also table schema construction from a table in a rdb Connection;
 * 
 * not including table query
 * @author tanxu
 *
 */
public final class TableSchemaUtils {
	/**
	 * check if the given table in the given schema exists in the given rdb Connection;
	 * @param conn
	 * @param schemaName
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static boolean doesTableExists(Connection conn, SimpleName schemaName, VfNameString tableName) throws SQLException {
		System.out.println("check table exists:" + SQLStringUtils.buildTableFullPathString(schemaName, tableName));
       	
		DatabaseMetaData meta = conn.getMetaData();

		//lookup all tables that matches the given descriptions
		//note that here table and schema name are used in WHERE condition as the constant string for query, thus need to cast to uppercase!!!!!
		ResultSet result = meta.getTables(
			null, 
			schemaName.getStringValue().toUpperCase(), //a schema name pattern; must match the schema name as it is stored in the database; "" retrieves those without a schema; null means that the schema name should not be used to narrow the search
			tableName.getStringValue().toUpperCase(), 
			null
		);
		
		//if returned ResultSet is empty, the table dose not exist;
		boolean ret = result.next();
		
		return ret;
   	}
	
	
	/**
	 * 
	 * @param dbCon
	 * @param tableSchema
	 * @throws SQLException
	 */
	public static void createTableSchema(Connection dbCon, AbstractRelationalTableSchema<?> tableSchema) throws SQLException {
		if(doesTableExists(dbCon, tableSchema.getSchemaName(), tableSchema.getName())) {
			throw new IllegalArgumentException("given Table schema is already existing in the rdb of the give Connection:"+tableSchema.getName());
		}
		
		String sqlString = TableSchemaSQLStringFactory.buildCreateTableSqlString(tableSchema);
		System.out.println(sqlString);
		
		Statement statement = dbCon.createStatement();
		
		statement.executeUpdate(sqlString);
	}
	
	
	/**
     * delete the table from db;
     *	
     * @param dbConn
     * @param tableName
     * @return
	 * @throws SQLException 
     */
    public static void dropTable(Connection dbConn, SimpleName schemaName, VfNameString tableName) throws SQLException {
        if (!TableSchemaUtils.doesTableExists(dbConn, schemaName, tableName)) {
            throw new IllegalArgumentException("Table does not exist, can not delete:" + SQLStringUtils.buildTableFullPathString(schemaName, tableName));
        }
        
        String sql = TableSchemaSQLStringFactory.buildDropTableSQLString(schemaName, tableName);
        
        Statement statement = dbConn.createStatement();
        statement.executeUpdate(sql);
        
    }
    
    
    /**
     * delete all rows in the table, but keep the table
     *
     * @param dbConn
     * @param tableName
     * @return
     * @throws SQLException 
     */
    public static void clearTable(Connection dbConn, SimpleName schemaName, VfNameString tableName) throws SQLException {
        if (!TableSchemaUtils.doesTableExists(dbConn, schemaName, tableName)) {
            throw new IllegalArgumentException("Table does not exist, can not clear:" + SQLStringUtils.buildTableFullPathString(schemaName, tableName));
        }

        String sql = TableSchemaSQLStringFactory.buildClearTableContentSQLString(schemaName, tableName);

        Statement statement = dbConn.createStatement();
        statement.executeUpdate(sql);
        
    }
    /**
     * rename the table name
     * @param dbConn
     * @param schemaName
     * @param tableName
     * @param newTableName
     * @return
     * @throws SQLException 
     */
    public static void renameTable(Connection dbConn, SimpleName schemaName, VfNameString tableName, VfNameString newTableName) throws SQLException {
        if (!TableSchemaUtils.doesTableExists(dbConn, schemaName, tableName)) {
            throw new IllegalArgumentException("Table does not exist, can not rename:" + SQLStringUtils.buildTableFullPathString(schemaName, tableName));
        }
        if (TableSchemaUtils.doesTableExists(dbConn, schemaName, newTableName)) {
            throw new IllegalArgumentException("Table with the new name alreayd exist, cannot rename another table to it:" + SQLStringUtils.buildTableFullPathString(schemaName, newTableName));
        }
        
        if (tableName.equals(newTableName)) {
            throw new IllegalArgumentException("new Table name is the same with the current name:" + newTableName);
        }
        
        String sql = TableSchemaSQLStringFactory.buildRenameTableSQLString(schemaName, tableName, newTableName);
        
        Statement statement = dbConn.createStatement();
        
        statement.executeUpdate(sql);
        
    }
    
    
    /**
     * sql string to get all table names in a specific Schema in apache derby;
     * 
     * SCHEMA_NAME ==> replaced by the real schema name cast to upper case;
     * 
     */
    public static final String GET_ALL_TABLE_IN_SCHEMA_SQL_TEMPLATE = 
    		"SELECT TABLENAME FROM SYS.SYSTABLES,SYS.SYSSCHEMAS WHERE SYS.SYSTABLES.SCHEMAID = SYS.SYSSCHEMAS.SCHEMAID and SYS.SYSSCHEMAS.SCHEMANAME = 'SCHEMA_NAME'";
    
    /**
     * return the set of table names in the given schema of the given DB connection;
     * 
     * @param dbCon
     * @param schemaName
     * @return
     * @throws SQLException 
     */
    public static Set<String> getAllTableNameSet(Connection dbConn, String schemaName) throws SQLException{
    	String sqlQueryString = GET_ALL_TABLE_IN_SCHEMA_SQL_TEMPLATE;
    	sqlQueryString = sqlQueryString.replace("SCHEMA_NAME", schemaName.toUpperCase());
    	
    	System.out.println(sqlQueryString);
        Statement statement;
    	
        statement = dbConn.createStatement();
        ResultSet rs = statement.executeQuery(sqlQueryString);
    	
        Set<String> ret = new HashSet<>();
        
        while(rs.next()) {
        	String tableName = rs.getString("TABLENAME");
        	ret.add(tableName);
        }
        
        return ret;
    }
    
    
    
    
    /**
     * return the set of DataTableName for all data table in the DATA schema
     * @param dbCon
     * @param schemaName
     * @return
     * @throws SQLException 
     */
    public static Set<DataTableName> getAllDataTableNameSet(Connection dbCon) throws SQLException{
    	Set<String> tableNameSet = getAllTableNameSet(dbCon, VisProjectRDBConstants.DATA_SCHEMA_NAME.getStringValue());
    	
    	Set<DataTableName> ret = new HashSet<>();
    	for(String name:tableNameSet) {
    		ret.add(new DataTableName(name));
    	}
    	
    	return ret;
    }
    
    
    /**
     * return the set of ValueTableName for all data table in the VALUE schema
     * @param dbCon
     * @param schemaName
     * @return
     * @throws SQLException 
     */
    public static Set<ValueTableName> getAllCFTargetValueTableNameSet(Connection dbCon) throws SQLException{
    	Set<String> tableNameSet = getAllTableNameSet(dbCon, VisProjectRDBConstants.VALUE_SCHEMA_NAME.getStringValue());
    	
    	Set<ValueTableName> ret = new HashSet<>();
    	for(String name:tableNameSet) {
    		ret.add(new ValueTableName(name));
    	}
    	
    	return ret;
    }
    
    
    

    /**
     * add the given list of columns to the existing table
     * 
     * @param dbCon
     * @param table
     * @param newColumnList
     * @throws SQLException 
     */
    public static void addNewColumnsToExistingTable(Connection dbCon, SimpleName schemaName, VfNameString tableName, List<? extends AbstractRelationalTableColumn> newColumnList) throws SQLException {
    	//
    	for(AbstractRelationalTableColumn column:newColumnList) {
    		String sqlString = TableSchemaSQLStringFactory.buildAddColumnToExistingTableSqlString(schemaName, tableName, column);
        	
        	System.out.println(sqlString);
    		
    		Statement statement = dbCon.createStatement();
    		
    		statement.executeUpdate(sqlString);
    	}
    }
    
    
    
    ////////////////////////////////////////////////////////////////////////////
    ////retrieve DataTableSchema from rdb of host VisProjectDBContext is no longer needed since DataTableSchema is now a final field of RecordDataMetadata;
//    /**
//     * 
//     * @param dbConn
//     * @param schemaName
//     * @param tableName
//     * @return
//     * @throws SQLException 
//     */
//    public static DataTableSchema retrieveDataTableSchema(Connection dbConn, SimpleName schemaName, DataTableName tableName) throws SQLException {
//    	if (!DerbyTableSchemaUtils.doesTableExists(dbConn, schemaName, tableName)) {
//            throw new IllegalArgumentException("Table does not exist, can not retrieve DataTableSchema:" + SqlStringUtils.buildTableFullPathString(schemaName, tableName));
//        }
//    	
//    	if(schemaName.equals(VisProjectRDBConstants.DATA_SCHEMA_NAME)) {
//    		throw new IllegalArgumentException("given schema name is not equal to the vis project DATA_SCHEMA_NAME");
//    	}
//    	
//    	
//    	
//    	
//    	
//    	
//    	
//    	
//    	
//    }
//    
//	/**
//     * retrieve a map from name to JDBC data type integer constant for all the
//     * attributes of the given table; see
//     * https://docs.oracle.com/javase/8/docs/api/constant-values.html#java.sql.Types.VARCHAR
//     * 
//     * @param conn
//     * @param schemaName
//     * @param tableName
//     * @return
//     */
//    static List<> retrieveMapOfTableColumnNameSQLDataTypeString(Connection conn, SimpleName schemaName, VfNameString tableName) {
//        Map<String, String> ret = new LinkedHashMap<>();
//        
//        try {
//            DatabaseMetaData databaseMetaData = conn.getMetaData();
//
//            ResultSet columns = databaseMetaData.getColumns(null, schemaName.getStringValue().toUpperCase(), tableName.getStringValue().toUpperCase(), null);
//         
//            //iterate through the ResultSet;
//            while (columns.next()) {
////                System.out.println("columns");
//                String columnName = columns.getString("COLUMN_NAME");
//                //DATA_TYPE int => SQL type from java.sql.Types
//                int datatype = columns.getInt("DATA_TYPE");
//                //TYPE_NAME String => Data source dependent type name, for a UDT the type name is fully qualified
//                String typeName = columns.getString("TYPE_NAME");
//                //IS_NULLABLE String => ISO rules are used to determine the nullability for a column.
//                //YES --- if the column can include NULLs;NO --- if the column cannot include NULLs;empty string --- if the nullability for the column is unknown
//                Boolean isNullable = Boolean.parseBoolean(columns.getString("IS_NULLABLE"));
//                //COLUMN_DEF String => default value for the column, which should be interpreted as a string when the value is enclosed in single quotes (may be null)
//                String defaultStringValue = columns.getString("COLUMN_DEF");
//                
//                
//                
//                
//                //                //Printing results
//                System.out.println(columnName + "---" + datatype + "---" + columnsize + "---" + decimaldigits + "---" + isNullable + "---" + is_autoIncrment);
//
//                ret.put(columns.getString("COLUMN_NAME"), DerbyDataTypeUtils.getSQLDataTypeStringFromJDBCIntegerConstant(Integer.parseInt(columns.getString("DATA_TYPE"))));
//            }
//            
//        } catch (SQLException ex) {
//            Logger.getLogger(DerbyDBUtils.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return ret;
//    }
//    
//    /**
//     * retrieve a map from attribute name to whether the attribute can be null
//     * of the given table; see
//     * https://docs.oracle.com/javase/8/docs/api/constant-values.html#java.sql.Types.VARCHAR
//     *
//     * @param conn
//     * @param schemaName
//     * @param tableName
//     * @return
//     */
//    public static Map<VfNameString, Boolean> retrieveMapOfTableAttributeNameIsNullable(Connection conn, VfNameString schemaName, VfNameString tableName) {
//        Map<VfNameString, Boolean> ret = new LinkedHashMap<>();
//
//        try {
//            DatabaseMetaData databaseMetaData = conn.getMetaData();
//
//            ResultSet columns = databaseMetaData.getColumns(null, null, makeSchemaTableFullName(schemaName, tableName).toUpperCase(), null);
//
//            //iterate through the ResultSet;
//            while (columns.next()) {
////                System.out.println("columns");
//                String columnName = columns.getString("COLUMN_NAME");
//                String datatype = columns.getString("DATA_TYPE");
//                String columnsize = columns.getString("COLUMN_SIZE");
//                String decimaldigits = columns.getString("DECIMAL_DIGITS");
//                String isNullable = columns.getString("IS_NULLABLE");
//                String is_autoIncrment = columns.getString("IS_AUTOINCREMENT");
////                //Printing results
//                System.out.println(columnName + "---" + datatype + "---" + columnsize + "---" + decimaldigits + "---" + isNullable + "---" + is_autoIncrment);
//
//                ret.put(new VfNameString(columns.getString("COLUMN_NAME")), columns.getString("IS_NULLABLE").equalsIgnoreCase("yes"));
//            }
//
//        } catch (SQLException ex) {
//            Logger.getLogger(DerbyDBUtils.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return ret;
//    }
//	
//    /**
//     * retrieve a map from attribute name to the data type integer in jdbc;
//     * https://docs.oracle.com/javase/8/docs/api/constant-values.html#java.sql.Types.VARCHAR
//     *
//     * @param conn
//     * @param schemaName
//     * @param tableName
//     * @return
//     */
//    public static Map<VfNameString, Integer> retrieveMapOfTableAttributeNameDataTypeConstantInteger(Connection conn, VfNameString schemaName, VfNameString tableName) {
//        Map<VfNameString, Integer> ret = new LinkedHashMap<>();
//
//        try {
//            DatabaseMetaData databaseMetaData = conn.getMetaData();
//
//            ResultSet columns = databaseMetaData.getColumns(null, null, makeSchemaTableFullName(schemaName, tableName).toUpperCase(), null);
//
//            //iterate through the ResultSet;
//            while (columns.next()) {
////                System.out.println("columns");
//                String columnName = columns.getString("COLUMN_NAME");
//                String datatype = columns.getString("DATA_TYPE");
//                String columnsize = columns.getString("COLUMN_SIZE");
//                String decimaldigits = columns.getString("DECIMAL_DIGITS");
//                String isNullable = columns.getString("IS_NULLABLE");
//                String is_autoIncrment = columns.getString("IS_AUTOINCREMENT");
////                //Printing results
////                System.out.println(columnName + "---" + datatype + "---" + columnsize + "---" + decimaldigits + "---" + isNullable + "---" + is_autoIncrment);
//
//                ret.put(new VfNameString(columns.getString("COLUMN_NAME")), Integer.parseInt(columns.getString("DATA_TYPE")));
//            }
//
//        } catch (SQLException ex) {
//            Logger.getLogger(DerbyDBUtils.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return ret;
//    }
}
