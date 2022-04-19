/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sql.derby;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * note that UDT creating, and lookup is RDB implementation specific;
 * the methods used in derby may be not applicable to other sql engine;
 * 
 * 
 * a comprehensive guide to UDT in derby and java JDBC:
 * https://www.javacodegeeks.com/2013/10/java-user-defined-types-udt-in-java-db.html
 * 
 * 
 * https://db.apache.org/derby/docs/10.8/ref/rrefsqljcreatetype.html#rrefsqljcreatetype
 * @author tanxu
 */
public class UDTUtils {
	
	/**
	 * sql query string template to create a UDT type in a specific schema with an alias UDT name for a java class;
	 * schemaName===
	 * udtTypeAliasName===
	 * javaClassPathName===
	 */
	private static final String CREATE_UDT_TYPE_TEMPLATE = 
			"create TYPE schemaName.udtTypeAliasName EXTERNAL NAME 'javaClassPathName' LANGUAGE JAVA";
	
	
    /**
     * create a UDT type in the given schema of the given database with the given type alias name;
     * 
     * if the target UDT is already existing in the schema, do nothing and return;
     * 
     * 
     * @param dbConn
     * @param udtTypeAliasName
     * @param javaClass 
     * @throws SQLException 
     */
    public static void createUDTType(Connection dbConn, String schemaName, String udtTypeAliasName, Class<?> javaClass) throws SQLException{
        //
        if(!doesUDTExist(dbConn, schemaName, udtTypeAliasName, javaClass)){
            System.out.println("UDT is not defined yet, continue to define it...");
        }else{
            System.out.println("given UDT is already defined in the given db!!!!skip");
            return;//do nothing
        }
        
        //
        if(doesUDTAliasNameExist(dbConn,schemaName, udtTypeAliasName)) {
        	System.out.println("Given UDT alias name"+udtTypeAliasName+" is already taken by a different UDT in the ginve rdb Connection!");
//        	return;
        }
        
        String sqlQueryString = CREATE_UDT_TYPE_TEMPLATE;
        sqlQueryString = sqlQueryString.replace("udtTypeAliasName", udtTypeAliasName.toUpperCase());
        sqlQueryString = sqlQueryString.replace("javaClassPathName", javaClass.getName());
        sqlQueryString = sqlQueryString.replace("schemaName", schemaName.toUpperCase());
        
    	
        //
//        String sql = "CREATE TYPE ".
//        		concat(schemaName).concat(".").concat(udtTypeAliasName).
//        		concat(" EXTERNAL NAME \'").concat(javaClass.getName()). //return the path of the class in the current project?
//        		concat("\' LANGUAGE JAVA");
        System.out.println(sqlQueryString);
        
        
        Statement statement;
//        try {
            statement = dbConn.createStatement();
            statement.execute(sqlQueryString);
            
//        } catch (SQLException ex) {
//            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
//            DerbyDBUtils.printSQLException(ex);
//        }
        
    }
    
    
    /**
     * sql query string template to check if a specific UDT type is existing;
     * udtTypeAliasName == need to cast to upper case
     * javaClassPathName == keep the original case
     * schemaName == need to cast to upper case
     */
    private static final String UDT_EXISTENCE_CHECK_SQL_TEMPLATE = 
    			"select * from SYS.SYSALIASES, SYS.SYSSCHEMAS where " + 
    			    "	SYS.SYSALIASES.alias = 'udtTypeAliasName' AND " + 
    			    "	SYS.SYSALIASES.aliastype='A' AND " + 
    			    "	CAST(SYS.SYSALIASES.JAVACLASSNAME AS VARCHAR(1024)) = 'javaClassPathName' AND " + 
    			    "	SYS.SYSSCHEMAS.SCHEMANAME = 'schemaName' AND " + 
    			    "	SYS.SYSSCHEMAS.SCHEMAID = SYS.SYSALIASES.SCHEMAID";
    
    
    
    /**
     *  check if the db already has the UDT type as the given one with the name and java class;
     * @param dbConn
     * @param schemaName
     * @param udtTypeAliasName
     * @param javaClass
     * @return
     * @throws SQLException
     */
    public static boolean doesUDTExist(Connection dbConn, String schemaName, String udtTypeAliasName, Class<?> javaClass) throws SQLException{
    	String sqlQueryString = UDT_EXISTENCE_CHECK_SQL_TEMPLATE;
    	sqlQueryString = sqlQueryString.replace("udtTypeAliasName", udtTypeAliasName.toUpperCase());
    	sqlQueryString = sqlQueryString.replace("javaClassPathName", javaClass.getName());
    	sqlQueryString = sqlQueryString.replace("schemaName", schemaName.toUpperCase());
    	
    	
        System.out.println(sqlQueryString);
        Statement statement;
//        try {
            statement = dbConn.createStatement();
            ResultSet rs = statement.executeQuery(sqlQueryString);
            
            return rs.next();
//            
//            
//        } catch (SQLException ex) {
//            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
//            DerbyDBUtils.printSQLException(ex);
//            return false;
//        }
    	
    }
    
    
    /**
     * sql query string template to check if a UDT alias name in a specific schema is already taken by an existing UDT;
     * note that this does not check the java class path name, thus different from {@link #UDT_EXISTENCE_CHECK_SQL_TEMPLATE};
     * 
     * udtTypeAliasName === need to cast to upper case
     * 
     * schemaName === need to cast to upper case
     */
    private static final String UDT_ALIAS_NAME_EXISTENCE_CHECK_TEMPLATE = 
    		"select * from SYS.SYSALIASES, SYS.SYSSCHEMAS where " + 
    		"	SYS.SYSALIASES.alias = 'udtTypeAliasName' AND " + 
    		"	SYS.SYSSCHEMAS.SCHEMANAME = 'schemaName' AND " + 
    		"	SYS.SYSSCHEMAS.SCHEMAID = SYS.SYSALIASES.SCHEMAID";
    
    
    
    /**
     * check whether the given UDT alias name in the given schema is already taken by an existing UDT;
     * 
     * @param dbConn
     * @param schemaName
     * @param udtTypeAliasName
     * @return
     * @throws SQLException 
     */
    public static boolean doesUDTAliasNameExist(Connection dbConn, String schemaName, String udtTypeAliasName) throws SQLException {
    	String sqlQueryString = UDT_ALIAS_NAME_EXISTENCE_CHECK_TEMPLATE;
    	sqlQueryString = sqlQueryString.replace("udtTypeAliasName", udtTypeAliasName.toUpperCase());
    	sqlQueryString = sqlQueryString.replace("schemaName", schemaName.toUpperCase());
    	
    	System.out.println(sqlQueryString);
        Statement statement;
//        try {
            statement = dbConn.createStatement();
            ResultSet rs = statement.executeQuery(sqlQueryString);
            
            return rs.next();
            
            
            
            
    }
    /**
     * drop the UDT in the given schema with the given alias name;
     *
     * A UDT cannot be dropped if a database object is using (or referring) the UDT.For example, (a) if a table column is of type UDT, that UDT cannot be dropped, unless until the corresponding table column is dropped, 
 or (b) if a database function is referring a UDT’s class (instance) the UDT cannot be dropped, unless until the function is modified not to refer that UDT class.
     * DROP TYPE udtTypeName RESTRICT
     * @param dbConn
     * @param typeAliasName 
     * @param javaClass 
     * @throws SQLException 
     */
    public static void dropUDT(Connection dbConn, String schemaName, String typeAliasName) throws SQLException{
        if(!doesUDTAliasNameExist(dbConn, schemaName, typeAliasName)){
            System.out.println("UDT alias name does not exist, skip dropping");
            return;
        }else{
            System.out.println("dropping...");
        }
        
        String sql = "DROP TYPE ".concat(typeAliasName).concat(" RESTRICT");
        System.out.println(sql);
        
        Statement statement;
//        try {
            statement = dbConn.createStatement();
            statement.execute(sql);
            
//        } catch (SQLException ex) {
//            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
//            DerbyDBUtils.printSQLException(ex);
//        }
        
    }
    
    
    
    
    
    
    
//    /**
//     * insert a java object into a data table row with the given primary key attribute value strings;
//     * (this requires all primary key attributes be of primitive data type)
//     * @param dbConn
//     * @param tableName
//     * @param pkAttributeNameStringValueMap
//     * @param UDTAttributeName
//     * @param insertedObject
//     */
//    public static void insertObjectByPrimaryKey(Connection dbConn, VfNameString tableName, Map<VfNameString,String> pkAttributeNameStringValueMap, VfNameString UDTAttributeName, Object insertedObject){
//        if(!DerbyTableUtils.isTablePrimaryKeyAttributes(dbConn, tableName, pkAttributeNameStringValueMap.keySet())){
//            throw new IllegalArgumentException("given primary key attributes are not correct");
//        }
//        List<VfNameString> allAttributeList = DerbyTableUtils.retrieveAllAttributeNamesAsListFromTable(dbConn, tableName);
//        
//        if(!allAttributeList.contains(UDTAttributeName)){
//            throw new IllegalArgumentException("given UDTAttributeName to update values are not in the table");
//        }
//        
//        
//        String preparedStatementString = "INSERT INTO".concat(tableName.toString()).concat("VALUES (?, ?) WHERE");
//        
//        
//        
//        PreparedStatement ps;
//        try {
//            ps = dbConn.prepareStatement();
//            int udtAttributeIndex = 1;
//            ps.setObject(udtAttributeIndex, insertedObject); // UDT data
//            ps.executeUpdate();
//        } catch (SQLException ex) {
//            Logger.getLogger(DerbyDBUDTUtils.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        
//        
//        
//        
//        
//        
//    }
}
