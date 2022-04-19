/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sql.derby.pre;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import basic.SimpleName;
import basic.VfNameString;
import rdb.table.AbstractRelationalTableColumn;
import rdb.table.AbstractRelationalTableSchema;
import sql.derby.DerbyDBUtils;

/**
 *
 * !!!!!NOTE: all string type information including table name and attribute
 * name, once input into derby, will transform to uppercase; thus when parsing
 * the table name and attribute names retrieved from database, should treat them
 * as all uppercase strings;!!!!!!!!
 *
 * @author tanxu
 */
public class DerbyTableUtils {

    private final static String COMMA = ",";
    private final static String SPACE = " ";

    private final static String NOT_NULL = "NOT NULL";

    /**
     * retrieve a map from name to JDBC data type integer constant for all the
     * attributes of the given table; see
     * https://docs.oracle.com/javase/8/docs/api/constant-values.html#java.sql.Types.VARCHAR
     *
     * @param conn
     * @param schemaName
     * @param tableName
     * @return
     */
    public static Map<SimpleName, String> retrieveMapOfTableAttributeNameSQLDataTypeString(Connection conn, SimpleName schemaName, SimpleName tableName) {
        Map<SimpleName, String> ret = new LinkedHashMap<>();
        
        try {
            DatabaseMetaData databaseMetaData = conn.getMetaData();

            ResultSet columns = databaseMetaData.getColumns(null, null, makeSchemaTableFullName(schemaName, tableName).toUpperCase(), null);
            
            //iterate through the ResultSet;
            while (columns.next()) {
//                System.out.println("columns");
                String columnName = columns.getString("COLUMN_NAME");
                String datatype = columns.getString("DATA_TYPE");
                String columnsize = columns.getString("COLUMN_SIZE");
                String decimaldigits = columns.getString("DECIMAL_DIGITS");
                String isNullable = columns.getString("IS_NULLABLE");
                String is_autoIncrment = columns.getString("IS_AUTOINCREMENT");
//                //Printing results
                System.out.println(columnName + "---" + datatype + "---" + columnsize + "---" + decimaldigits + "---" + isNullable + "---" + is_autoIncrment);

                ret.put(new SimpleName(columns.getString("COLUMN_NAME")), DerbyDataTypeUtils.getSQLDataTypeStringFromJDBCIntegerConstant(Integer.parseInt(columns.getString("DATA_TYPE"))));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    /**
     * retrieve a map from attribute name to whether the attribute can be null
     * of the given table; see
     * https://docs.oracle.com/javase/8/docs/api/constant-values.html#java.sql.Types.VARCHAR
     *
     * @param conn
     * @param schemaName
     * @param tableName
     * @return
     */
    public static Map<SimpleName, Boolean> retrieveMapOfTableAttributeNameIsNullable(Connection conn, SimpleName schemaName, SimpleName tableName) {
        Map<SimpleName, Boolean> ret = new LinkedHashMap<>();

        try {
            DatabaseMetaData databaseMetaData = conn.getMetaData();

            ResultSet columns = databaseMetaData.getColumns(null, null, makeSchemaTableFullName(schemaName, tableName).toUpperCase(), null);

            //iterate through the ResultSet;
            while (columns.next()) {
//                System.out.println("columns");
                String columnName = columns.getString("COLUMN_NAME");
                String datatype = columns.getString("DATA_TYPE");
                String columnsize = columns.getString("COLUMN_SIZE");
                String decimaldigits = columns.getString("DECIMAL_DIGITS");
                String isNullable = columns.getString("IS_NULLABLE");
                String is_autoIncrment = columns.getString("IS_AUTOINCREMENT");
//                //Printing results
                System.out.println(columnName + "---" + datatype + "---" + columnsize + "---" + decimaldigits + "---" + isNullable + "---" + is_autoIncrment);

                ret.put(new SimpleName(columns.getString("COLUMN_NAME")), columns.getString("IS_NULLABLE").equalsIgnoreCase("yes"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    /**
     * retrieve a map from attribute name to the data type integer in jdbc;
     * https://docs.oracle.com/javase/8/docs/api/constant-values.html#java.sql.Types.VARCHAR
     *
     * @param conn
     * @param schemaName
     * @param tableName
     * @return
     */
    public static Map<SimpleName, Integer> retrieveMapOfTableAttributeNameDataTypeConstantInteger(Connection conn, SimpleName schemaName, SimpleName tableName) {
        Map<SimpleName, Integer> ret = new LinkedHashMap<>();

        try {
            DatabaseMetaData databaseMetaData = conn.getMetaData();

            ResultSet columns = databaseMetaData.getColumns(null, null, makeSchemaTableFullName(schemaName, tableName).toUpperCase(), null);

            //iterate through the ResultSet;
            while (columns.next()) {
//                System.out.println("columns");
                String columnName = columns.getString("COLUMN_NAME");
                String datatype = columns.getString("DATA_TYPE");
                String columnsize = columns.getString("COLUMN_SIZE");
                String decimaldigits = columns.getString("DECIMAL_DIGITS");
                String isNullable = columns.getString("IS_NULLABLE");
                String is_autoIncrment = columns.getString("IS_AUTOINCREMENT");
//                //Printing results
//                System.out.println(columnName + "---" + datatype + "---" + columnsize + "---" + decimaldigits + "---" + isNullable + "---" + is_autoIncrment);

                ret.put(new SimpleName(columns.getString("COLUMN_NAME")), Integer.parseInt(columns.getString("DATA_TYPE")));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    public static void main(String[] args) {

        System.out.println(Boolean.parseBoolean("TRUE"));

        Path dbDir = Paths.get("C:\\Users\\tanxu\\Desktop\\testdb");
        String dbName = "TEST_VF_PROJECT_1113";

        Connection con;
        try {

//            String dataTableName = "gff3_1_datatable68";
            con = DerbyDBUtils.getEmbeddedDBConnection(dbDir, dbName, true);
            
            retrieveMapOfTableAttributeNameSQLDataTypeString(con, new SimpleName("test"), new SimpleName("GFF3_2"));

//            Map<SimpleName,String> primaryKeyAttributeNameStringValueMap = new HashMap<>();
//            primaryKeyAttributeNameStringValueMap.put(new SimpleName("ID"), "GRMZM2G008250_T01.v6a.CDS.2");
//            
//            System.out.println(recordExistsByPrimaryKeyAttributesValue(con, new SimpleName(dataTableName), primaryKeyAttributeNameStringValueMap));
        } catch (SQLException ex) {
//            Logger.getLogger(RecordDataImporterTester.class.getName()).log(Level.SEVERE, null, ex);
        }

        DerbyDBUtils.shutDownDerbyEngine();
    }

    /**
     * return true if the record defined by the given PK attribute values
     * already exists in the table;
     *
     * @param conn
     * @param schemaName
     * @param tableName
     * @param primaryKeyAttributeNameStringValueMap
     * @return
     */
    public static boolean recordExistsByPrimaryKeyAttributesValue(Connection conn, SimpleName schemaName, SimpleName tableName, Map<SimpleName, String> primaryKeyAttributeNameStringValueMap) {
        //todo
        if (!DerbyTableUtils.isTablePrimaryKeyAttributes(conn, schemaName, tableName, primaryKeyAttributeNameStringValueMap.keySet())) {
            throw new IllegalArgumentException("given attributes are not the same with the primary key attributes");
        }

        ResultSet queryResult = DerbyTableUtils.retrieveAllRowsWithSameAttributesValueAsResultSet(conn, schemaName, tableName, primaryKeyAttributeNameStringValueMap);

        try {
            if (queryResult.next()) {//there is at least one record found?
                queryResult.close();
                return true;
            } else {
                queryResult.close();
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);

            return false;
        }

    }

    /**
     * retrieve and return the string value of the given attribute using the
     * primary key attribute name value map;s
     *
     * @param conn
     * @param schemaName
     * @param tableName
     * @param attributeName
     * @param primaryKeyAttributeNameStringValueMap
     * @return
     */
    public static String retrieveStringValueOfAttributeByPrimaryKeyAttributesValue(Connection conn, SimpleName schemaName, SimpleName tableName, SimpleName attributeName, Map<SimpleName, String> primaryKeyAttributeNameStringValueMap) {
        if (!DerbyTableUtils.isTablePrimaryKeyAttributes(conn, schemaName, tableName, primaryKeyAttributeNameStringValueMap.keySet())) {
            throw new IllegalArgumentException("given attributes are not the same with the primary key attributes");
        }

        ResultSet queryResult = DerbyTableUtils.retrieveAllRowsWithSameAttributesValueAsResultSet(conn, schemaName, tableName, primaryKeyAttributeNameStringValueMap);

        try {
            if (queryResult.next()) {//there is at least one record found?
                String ret = queryResult.getString(attributeName.toString());
                queryResult.close();
                return ret;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);
            return null;
        }

    }

    /**
     * retrieve and return the string values all attributes of the row with the
     * same primary key attribute values as given; if an attribute's value is
     * null in database, the string value will be a null object; if
     *
     * @param conn
     * @param tableName
     * @param primaryKeyAttributeNameStringValueMap
     * @return
     */
    public static Map<SimpleName, String> retrieveStringValueOfAllAttributesByPrimaryKeyAttributesValue(
                Connection conn, SimpleName schemaName, SimpleName tableName, Map<SimpleName, String> primaryKeyAttributeNameStringValueMap) {
        if (!DerbyTableUtils.isTablePrimaryKeyAttributes(conn, schemaName, tableName, primaryKeyAttributeNameStringValueMap.keySet())) {
            throw new IllegalArgumentException("given attributes are not the same with the primary key attributes");
        }

        ResultSet queryResult = DerbyTableUtils.retrieveAllRowsWithSameAttributesValueAsResultSet(conn, schemaName, tableName, primaryKeyAttributeNameStringValueMap);

        Map<SimpleName, String> ret = new HashMap<>();
        List<SimpleName> tableAttributeList = DerbyTableUtils.retrieveAllAttributeNamesAsListFromTable(conn, schemaName, tableName);

        try {
            if (queryResult.next()) {//there is at least one record found?
                for (SimpleName att : tableAttributeList) {
                    ret.put(att, queryResult.getString(att.toString()));
                }
                //
                queryResult.close();
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);
            return null;
        }

        return ret;
    }

    /**
     * retrieve all rows whose value of the given attributes are the same as the
     * given ones;
     *
     * @param conn
     * @param tableName
     * @param tableAttributeNameStringValueMap
     * @return
     */
    public static ResultSet retrieveAllRowsWithSameAttributesValueAsResultSet(
                Connection conn, SimpleName schemaName, SimpleName tableName, Map<SimpleName, String> tableAttributeNameStringValueMap) {
        if (!DerbyTableUtils.attributeNamesAreAllInTableSchema(conn, schemaName, tableName, tableAttributeNameStringValueMap.keySet())) {
            throw new IllegalArgumentException("given attributes are not all found in the data table");
        }

//        String sql = makeSQLSelectAllRowsFromSingleTableWithGivenAttributesValues(tableName, tableAttributeNameStringValueMap);
//        
//        
//        Statement statement;
        PreparedStatement ps = makePreparedStatementSelectAllRowsFromSingleTableWithGivenAttributesValues(conn, schemaName, tableName, tableAttributeNameStringValueMap);

        try {
//            statement = conn.createStatement();
//            ResultSet resultSet = statement.executeQuery(sql);
            ResultSet resultSet = ps.executeQuery();
            return resultSet;
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);

            return null;
        }

    }

    /**
     * create a PreparedStatement for a select * from TableName where (attribute
     * values are same with the given ones);
     *
     *
     * @param con
     * @param schemaName
     * @param tableName
     * @param attributeNameStringValueMap
     * @return
     */
    public static PreparedStatement makePreparedStatementSelectAllRowsFromSingleTableWithGivenAttributesValues(
                Connection con, SimpleName schemaName, SimpleName tableName, Map<SimpleName, String> attributeNameStringValueMap) {
        String sql = "SELECT * FROM ".concat(makeSchemaTableFullName(schemaName, tableName)).concat(" WHERE ");

        List<SimpleName> attNameList = new ArrayList<>();
        String whereClause = "";
        for (SimpleName att : attributeNameStringValueMap.keySet()) {
            if (!whereClause.isEmpty()) {
                whereClause = whereClause.concat(" AND ");
            }
            whereClause = whereClause.concat(att.toString()).concat("=?");

            attNameList.add(att);
        }

        sql = sql.concat(whereClause);

        PreparedStatement ret = null;

        System.out.println("***********************************************");
        for (SimpleName n : attributeNameStringValueMap.keySet()) {
            System.out.println(n.toString() + "==" + attributeNameStringValueMap.get(n));
        }
        System.out.println("**********************************************");
        Map<SimpleName, String> attributeNameDataTypeMap = DerbyTableUtils.retrieveMapOfTableAttributeNameSQLDataTypeString(con, schemaName, tableName);
        for (SimpleName n : attributeNameDataTypeMap.keySet()) {
            System.out.println(n.toString() + "==" + attributeNameDataTypeMap.get(n));
        }
        System.out.println("===============================================================================");
        try {
            ret = con.prepareStatement(sql);

            for (int i = 0; i < attNameList.size(); i++) {
                System.out.println("============================");
                System.out.println(attNameList.get(i));
                System.out.println(attributeNameDataTypeMap.get(attNameList.get(i)));
                DerbyDataTypeUtils.setPreparedStatementParameterValueForPrimitiveTypeAttribute(
                            ret, //
                            i + 1, //index
                            attributeNameDataTypeMap.get(attNameList.get(i)), //data type
                            attributeNameStringValueMap.get(attNameList.get(i)), //value
                            false);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

//    /**
//     * make a SELECT|FROM|WHERE sql from single input table, with conditions being all attribute value equals as the given ones;
//     *  SELECT * FROM table WHERE att = 'a', att1 = '2',...
//     * @param tableName
//     * @param tableAttributeNameStringValueMap
//     * @return 
//     */
//    public static String makeSQLSelectAllRowsFromSingleTableWithGivenAttributesValues(String tableName, Map<String,String> tableAttributeNameStringValueMap){
//        String sql = "SELECT * FROM ".concat(tableName.toUpperCase()).concat(" WHERE ");
//        
//        String whereClause = "";
//        
//        for(String att:tableAttributeNameStringValueMap.keySet()){
//            if(!whereClause.isEmpty()){
//                whereClause = whereClause.concat(" AND ");
//            }
//            String valueString = tableAttributeNameStringValueMap.get(att);
//            
//            
//            whereClause = whereClause.concat(att.toUpperCase()).concat(" = '").concat(valueString).concat("'"); //this is wrong for numeric data type, there should be no quotes ''
//            
//            
////            whereClause = whereClause.concat(att.toUpperCase()).concat(" = '").concat(valueString).concat("'");
//        }
//        
//        
//        sql = sql.concat(whereClause);
//        
//        return sql;
//
//    }
    /**
     * return true if the given set of attribute name is the same as the table's
     * PK set; need to first transform all the attribute name to uppercase
     * first;
     *
     * @param conn
     * @param tableName
     * @param attributeNameSet
     * @return
     */
    public static boolean isTablePrimaryKeyAttributes(Connection conn, SimpleName schemaName, SimpleName tableName, Set<SimpleName> attributeNameSet) {
        Set<SimpleName> pkAttributeSet = DerbyTableUtils.retrieveAllPrimaryKeyAttributeNamesFromTable(conn, schemaName, tableName);

        return pkAttributeSet.containsAll(attributeNameSet) && attributeNameSet.containsAll(pkAttributeSet);
    }

    /**
     * update the value of the given attributes in
     * updatedAttributeNameStringValueMap for records whose attributes in
     * matchingAttributeNameStringValueMap are the same as given;
     *
     *
     * UPDATE Customers SET ContactName = 'Alfred Schmidt', City= 'Frankfurt'
     * WHERE CustomerID = 1 AND City = 'SDFASFS'
     *
     * @param con
     * @param schemaName
     * @param tableName
     * @param updatedAttributeNameStringValueMap
     * @param matchingAttributeNameStringValueMap
     * @return true if the update is successful, false if not;
     */
    public static boolean updateAttributeValueByMatchingAttributesValue(
                Connection con, SimpleName schemaName, SimpleName tableName, 
                Map<SimpleName, String> updatedAttributeNameStringValueMap, Map<SimpleName, String> matchingAttributeNameStringValueMap) {
        //
        if(tableName==null){
            throw new IllegalArgumentException("given tableName is null");
        }
        if (!DerbyTableUtils.doesTableExists(con, schemaName, tableName)) {
            throw new IllegalArgumentException("given table name does not exit");
        }
        
        if(updatedAttributeNameStringValueMap==null||updatedAttributeNameStringValueMap.isEmpty()){
            throw new IllegalArgumentException("given updatedAttributeNameStringValueMap is null or empty");
        }
        if(matchingAttributeNameStringValueMap==null||matchingAttributeNameStringValueMap.isEmpty()){
            throw new IllegalArgumentException("given matchingAttributeNameStringValueMap is null or empty");
        }
        
        for(SimpleName updated:updatedAttributeNameStringValueMap.keySet()){
            if(matchingAttributeNameStringValueMap.containsKey(updated)){
                throw new IllegalArgumentException("attribute in updatedAttributeNameStringValueMap is also found in matchingAttributeNameStringValueMap:"+updated.toString());
            }
        }

        List<SimpleName> allAttributeList = DerbyTableUtils.retrieveAllAttributeNamesAsListFromTable(con, schemaName, tableName);
        
        if (!allAttributeList.containsAll(updatedAttributeNameStringValueMap.keySet())) {
            throw new IllegalArgumentException("given attributes in updatedAttributeNameStringValueMap are not all in the table");
        }
        if (!allAttributeList.containsAll(matchingAttributeNameStringValueMap.keySet())) {
            throw new IllegalArgumentException("given attributes in matchingAttributeNameStringValueMap are not all in the table");
        }
        
        
        ///////////////////////////////////////////////////////////
        String sql = "UPDATE ".concat(makeSchemaTableFullName(schemaName, tableName)).concat(" SET ");

        List<SimpleName> newValueAttributeNameList = new ArrayList<>();
        String setClause = "";
        for (SimpleName att : updatedAttributeNameStringValueMap.keySet()) {
            if (!setClause.isEmpty()) {
                setClause = setClause.concat(" ,");
            }

            setClause = setClause.concat(att.toString()).concat(" = ?");

            newValueAttributeNameList.add(att);
        }

        String whereClause = "";

        List<SimpleName> primaryKeyAttributeNameList = new ArrayList<>();
        for (SimpleName att : matchingAttributeNameStringValueMap.keySet()) {
            if (!whereClause.isEmpty()) {
                whereClause = whereClause.concat(" AND ");
            }

            whereClause = whereClause.concat(att.toString()).concat(" = ?");

            primaryKeyAttributeNameList.add(att);
        }

        sql = sql.concat(setClause).concat(" WHERE ").concat(whereClause);

        /////////////////////////////////////////////
        Map<SimpleName, String> attributeNameDataTypeMap = DerbyTableUtils.retrieveMapOfTableAttributeNameSQLDataTypeString(con, schemaName, tableName);
        try {
            PreparedStatement ps = con.prepareStatement(sql);

            //1 to attributeNameNewStringValueMap.size() in SET clause
            for (int i = 0; i < newValueAttributeNameList.size(); i++) {
//                System.out.println("i:"+i);
//                System.out.println("att name:"+newValueAttributeNameList.get(i));
//                System.out.println("new attribute value:"+attributeNameNewStringValueMapUpperCase.get(newValueAttributeNameList.get(i)));
                DerbyDataTypeUtils.setPreparedStatementParameterValueForPrimitiveTypeAttribute(
                            ps, //
                            i + 1, //index
                            attributeNameDataTypeMap.get(newValueAttributeNameList.get(i)), //data type
                            updatedAttributeNameStringValueMap.get(newValueAttributeNameList.get(i)), //value
                            false);
            }

            //attributeNameNewStringValueMap.size()+1 to ... in WHERE clause
            for (int i = 0; i < primaryKeyAttributeNameList.size(); i++) {
                DerbyDataTypeUtils.setPreparedStatementParameterValueForPrimitiveTypeAttribute(
                            ps, //
                            newValueAttributeNameList.size() + i + 1, //index
                            attributeNameDataTypeMap.get(primaryKeyAttributeNameList.get(i)), //data type
                            matchingAttributeNameStringValueMap.get(primaryKeyAttributeNameList.get(i)), //value
                            false);
            }

            //
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);
            
            return false;
        }

    }

    /**
     * create an INSERT SQL with the given map of SimpleAttribute objects and
     * their string values; insert into employee (name, city, phone) values
     * ('A', 'X', '123')
     *
     * @param schemaName
     * @param tableName
     * @param attributeNameStringValueMap
     * @return
     */
    public static String makeRecordInsertSQL(SimpleName schemaName, SimpleName tableName, Map<SimpleName, String> attributeNameStringValueMap) {
        String sql = "INSERT INTO ".concat(makeSchemaTableFullName(schemaName, tableName));
        String listOfAttributeName = "";
        String listOfAttributeValue = "";

        for (SimpleName att : attributeNameStringValueMap.keySet()) {
            String value = attributeNameStringValueMap.get(att);

            if (!listOfAttributeName.isEmpty()) {
                listOfAttributeName = listOfAttributeName.concat(" ,");
                listOfAttributeValue = listOfAttributeValue.concat(" ,");
            }

            listOfAttributeName = listOfAttributeName.concat(att.toString());
            listOfAttributeValue = listOfAttributeValue.concat(value);
        }

        sql = sql.concat("( ").concat(listOfAttributeName).concat(") VALUES (").concat(listOfAttributeValue).concat(")");
        return sql;

    }

    /**
     *
     * @param tableName
     * @param schemaName
     * @param conn
     * @return
     */
    public static boolean doesTableExists(Connection conn, SimpleName schemaName, VfNameString tableName) {
        System.out.println("check table exists:" + makeSchemaTableFullName(schemaName, tableName));
        try {
            DatabaseMetaData meta = conn.getMetaData();

            ResultSet result = meta.getTables(null, null, makeSchemaTableFullName(schemaName, tableName).toUpperCase(), null);

            boolean ret = result.next();
            return ret;
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            //something is wrong, need to figure out the details
            DerbyDBUtils.printSQLException(ex);
            return false;
        }
    }

    /**
     * get all the attribute names from the given table in the given database
     * connection;
     * https://www.progress.com/blogs/jdbc-tutorial-extracting-database-metadata-via-jdbc-driver
     *
     * @param dbConn
     * @param schemaName
     * @param tableName
     * @return
     */
    public static List<SimpleName> retrieveAllAttributeNamesAsListFromTable(Connection dbConn, SimpleName schemaName, SimpleName tableName) {
        if (!DerbyTableUtils.doesTableExists(dbConn, schemaName, tableName)) {
            throw new IllegalArgumentException("given table name does not exit");
        }
//        System.out.println("retrieve attribute names");
        List<SimpleName> ret = new ArrayList<>();

        try {
            DatabaseMetaData databaseMetaData = dbConn.getMetaData();

            ResultSet columns = databaseMetaData.getColumns(null, null, makeSchemaTableFullName(schemaName, tableName).toUpperCase(), null);

            //iterate through the ResultSet;
            while (columns.next()) {
//                System.out.println("columns");
                String columnName = columns.getString("COLUMN_NAME");
                String datatype = columns.getString("DATA_TYPE");
                String columnsize = columns.getString("COLUMN_SIZE");
                String decimaldigits = columns.getString("DECIMAL_DIGITS");
                String isNullable = columns.getString("IS_NULLABLE");
                String is_autoIncrment = columns.getString("IS_AUTOINCREMENT");
//                //Printing results
//                System.out.println(columnName + "---" + datatype + "---" + columnsize + "---" + decimaldigits + "---" + isNullable + "---" + is_autoIncrment);

                ret.add(new SimpleName(columns.getString("COLUMN_NAME")));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DerbyDBUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;

    }

    /**
     * retrieve all the attribute names in the primary key of the given table;
     * reference:
     * https://www.progress.com/blogs/jdbc-tutorial-extracting-database-metadata-via-jdbc-driver
     * 1.TABLE_CAT String => table catalog (may be null) 2. TABLE_SCHEM String
 => table schema (may be null) 3. TABLE_NAME String => table name 4.
     * COLUMN_NAME String => column name 5. KEY_SEQ short => sequence number
     * within primary key( a value of 1 represents the first column of the
     * primary key, a value of 2 would represent the second column within the
     * primary key). 6. PK_NAME String => primary key name (may be null)
     *
     * @param dbConn
     * @param schemaName
     * @param tableName
     * @return
     */
    public static Set<SimpleName> retrieveAllPrimaryKeyAttributeNamesFromTable(Connection dbConn, SimpleName schemaName, SimpleName tableName) {
        Set<SimpleName> ret = new HashSet<>();
        try {
            DatabaseMetaData databaseMetaData = dbConn.getMetaData();
            ResultSet PK = databaseMetaData.getPrimaryKeys(null, null, makeSchemaTableFullName(schemaName, tableName));
//            System.out.println("------------PRIMARY KEYS-------------");
            while (PK.next()) {
//                System.out.println(PK.getString("COLUMN_NAME") + "===" + PK.getString("PK_NAME"));
                ret.add(new SimpleName(PK.getString("COLUMN_NAME")));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DerbyDBUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    /**
     * get all the foreign key information of the given table; reference:
     * https://www.progress.com/blogs/jdbc-tutorial-extracting-database-metadata-via-jdbc-driver
     *
     * @param dbConn
     * @param schemaName
     * @param tableName
     */
    public static void getForeignKeyCols(Connection dbConn, SimpleName schemaName, SimpleName tableName) {
        try {
            DatabaseMetaData databaseMetaData = dbConn.getMetaData();
            //Get Foreign Keys
            ResultSet FK = databaseMetaData.getImportedKeys(null, null, makeSchemaTableFullName(schemaName, tableName).toUpperCase());
//            System.out.println("------------FOREIGN KEYS-------------");
            while (FK.next()) {
//                System.out.println(FK.getString("PKTABLE_NAME") + "---" + FK.getString("PKCOLUMN_NAME") + "===" + FK.getString("FKTABLE_NAME") + "---" + FK.getString("FKCOLUMN_NAME"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DerbyDBUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * create a table in the given dbConn; return true if a new table is
     * created, false if there is already a table exists;
     *
     * @param dbConn
     * @param tsb
     * @return
     */
    public static boolean createTableSchema(Connection dbConn, AbstractRelationalTableSchema<?> tsb) {
        System.out.println("create table:" + makeSchemaTableFullName(tsb.getSchemaName(), tsb.getName()));
        if (doesTableExists(dbConn, tsb.getSchemaName(), tsb.getName())) {
            System.out.println("already found");
            return false;
        }
//        CREATE TABLE table_name(
//            column1 datatype,
//            column2 datatype,
//            column3 datatype,
//            .....
//            columnN datatype,
//            PRIMARY KEY( one or more columns )
//         );

        String sql = "CREATE TABLE ".concat(makeSchemaTableFullName(tsb.getSchemaName(), tsb.getName())).concat(" (");
        String attributes = "";
        String primaryKeys = "";

        for (AbstractRelationalTableColumn col : tsb.getOrderedListOfColumn()) {
            String type = col.getSqlDataType().getSQLString();
            if (!attributes.isEmpty()) {
                attributes = attributes.concat(COMMA);
            }
            attributes = attributes.concat(col.getName().toString()).concat(SPACE).concat(type);
            
            if (col.isNotNull()) {
                attributes = attributes.concat(SPACE).concat(NOT_NULL);
            }

            if (col.isInPrimaryKey()) {
                if (primaryKeys.isEmpty()) {
                    primaryKeys = "PRIMARY KEY(".concat(col.getName().toString()); //
                } else {
                    primaryKeys = primaryKeys.concat(COMMA).concat(col.getName().toString());
                }
            }
        }

        sql = sql.concat(attributes);

        if (!primaryKeys.isEmpty()) {
            sql = sql.concat(COMMA).concat(primaryKeys).concat(")");
        }

        sql = sql.concat(")");

        System.out.println(sql);

        try {
            Statement statement = dbConn.createStatement();
            statement.execute(sql);
            System.out.println("created");
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);

            return false;
        }
//        return true;
    }

//    /**
//     * create a table in the given dbConn; return true if a new table is
//     * created, false if there is already a table exists;
//     *
//     * @param dbConn
//     * @param schema
//     * @return
//     */
//    public static boolean createTableSchema(Connection dbConn, TableSchema schema) {
//        System.out.println("create table:" + schema.getTableName().toString());
//        if (doesTableExists(dbConn, schema.getTableName())) {
//            System.out.println("already found");
//            return false;
//        }
////        CREATE TABLE table_name(
////            column1 datatype,
////            column2 datatype,
////            column3 datatype,
////            .....
////            columnN datatype,
////            PRIMARY KEY( one or more columns )
////         );
//
//        String sql = "CREATE TABLE ".concat(schema.getTableName().toString()).concat(" (");
//        String attributes = "";
//        String primaryKeys = "";
//
//        for (TableColumn tableCol : schema.getTableColumnList()) {
////            RDBTableColumn tableCol = schema.getTableColumnNameMap().get(colName);
//            String att = tableCol.getColName().toString();
//            String type = tableCol.getSqlType();
//            if (!attributes.isEmpty()) {
//                attributes = attributes.concat(COMMA);
//            }
//            attributes = attributes.concat(att).concat(SPACE).concat(type);
//
//            if (tableCol.isNotNull()) {
//                attributes = attributes.concat(SPACE).concat(NOT_NULL);
//            }
//
//            if (tableCol.isInPrimaryKey()) {
//                if (primaryKeys.isEmpty()) {
//                    primaryKeys = "PRIMARY KEY(".concat(att); //
//                } else {
//                    primaryKeys = primaryKeys.concat(COMMA).concat(att);
//                }
//            }
//        }
//
//        sql = sql.concat(attributes);
//
//        if (!primaryKeys.isEmpty()) {
//            sql = sql.concat(COMMA).concat(primaryKeys).concat(")");
//        }
//
//        sql = sql.concat(")");
//
//        System.out.println(sql);
//
//        try {
//            Statement statement = dbConn.createStatement();
//            statement.execute(sql);
//            System.out.println("created");
//            return true;
//        } catch (SQLException ex) {
//            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
//            DerbyDBUtils.printSQLException(ex);
//
//            return false;
//        }
////        return true;
//    }

    /**
     * insert the output of the selectSqlQueryString into the insertedTableName;
     * 1.the column type and order must be consistent 2.no validation is done
 here for the selectSqlQueryString;
     *
     * @param dbConn
     * @param schemaName
     * @param insertedTableName
     * @param selectSqlQueryString
     * @throws java.sql.SQLException
     */
    public static void insertIntoTableWithSelectQueryResult(Connection dbConn, SimpleName schemaName, SimpleName insertedTableName, String selectSqlQueryString)
                throws SQLException {
        if (!DerbyTableUtils.doesTableExists(dbConn, schemaName, insertedTableName)) {
            throw new IllegalArgumentException("output table does not exist:" + makeSchemaTableFullName(schemaName, insertedTableName));
        }

        if (selectSqlQueryString == null || selectSqlQueryString.isEmpty()) {
            throw new IllegalArgumentException("given selectSqlQueryString is null or empty");
        }
        if (!selectSqlQueryString.toLowerCase().trim().startsWith("select")) {//not a query starting with 'select'
            throw new IllegalArgumentException("given selectSqlQueryString is not starting with 'select'");
        }

        String sqlString = "INSERT INTO ".concat(makeSchemaTableFullName(schemaName, insertedTableName)).concat(" ").concat(selectSqlQueryString);

        Statement statement;
        try {
            statement = dbConn.createStatement();
            statement.execute(sqlString);
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);
            throw ex;
        }

    }

    /**
     * make the full name of the table under the schema;
     * 
     * @param schemaName
     * @param tableName
     * @return 
     */
    public static String makeSchemaTableFullName(SimpleName schemaName, VfNameString tableName){
        return schemaName.toString().concat(".").concat(tableName.toString());
    }
    
    /**
     * add attributes to the table schema whose values can be null (thus, when
     * added, the values of the new attributes will all be null until updated);
     * sql syntax: ALTER TABLE MY_TABLE ADD STAGE INT
     *
     * regarding the column values of the newly added attribute: by default,
     * either the newly added attribute CAN BE NULL, and the new column values
     * will be null; or the newly added attribute NOT NULL, and set a default
     * value for the new column;
     *
     *
     * @param dbConn
     * @param schemaName database schema name
     * @param tableName
     * @param newAttributeList
     * @return true if all attributes are successfully added, false otherwise
     */
    public static boolean addCanBeNullNewAttributesToTableSchema(Connection dbConn, SimpleName schemaName, SimpleName tableName, List<AbstractRelationalTableColumn> newAttributeList) {
        for (AbstractRelationalTableColumn sa : newAttributeList) {
            if (sa.isNotNull()) {
                throw new IllegalArgumentException("given attribute can not be null, can not be added as CAN BE NULL attribute:" + sa.getName().toString());
            }
            
            String attName = sa.getName().toString();
            String dataType = sa.getSqlDataType().getSQLString();

            String sql = "ALTER TABLE ".concat(makeSchemaTableFullName(schemaName, tableName)).concat(" ADD ").concat(attName).concat(" ").concat(dataType);
            
            Statement statement;
            try {
                statement = dbConn.createStatement();
                statement.execute(sql);
            } catch (SQLException ex) {
                Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
                DerbyDBUtils.printSQLException(ex);
            }

        }

        return true;
    }

    /**
     * return true if all the attributes in the primary key of the table are
     * contained in the given set of attribute name;
     *
     * @param dbCon
     * @param schemaName
     * @param tableName
     * @param attributeSet
     * @return
     */
    public static boolean tablePrimaryKeyIsContainedInList(Connection dbCon, SimpleName schemaName, SimpleName tableName, Set<SimpleName> attributeSet) {
        Set<SimpleName> pkSet = DerbyTableUtils.retrieveAllPrimaryKeyAttributeNamesFromTable(dbCon, schemaName, tableName);
//        System.out.println(pkSet.toString());

        return attributeSet.containsAll(pkSet);
    }

    /**
     * check if the given attribute names are all the given table's attributes
     *
     * @param dbCon
     * @param schemaName
     * @param tableName
     * @param attributeNames
     * @return
     */
    public static boolean attributeNamesAreAllInTableSchema(Connection dbCon, SimpleName schemaName, SimpleName tableName, Collection<SimpleName> attributeNames) {
        List<SimpleName> tableAtts = DerbyTableUtils.retrieveAllAttributeNamesAsListFromTable(dbCon, schemaName, tableName);

        return tableAtts.containsAll(attributeNames);

    }

    /**
     * insert the given values of attributes of primitive type that can be
     * transformed into string value of a row into the given table; syntax:
     * <br>
     * INSERT INTO table_name (column1, column2, column3, ...) VALUES ('value1',
     * 'value2', 'value3', ...);
     * </br>
     * <p>
     * note that this method will fail to insert if the given record's primary key attributes values is duplicated with an existing record;
     * </p>
     * @param dbCon
     * @param schemaName
     * @param tableName
     * @param nullValueString null value string, can be null if no null value
     * string for the input attributes
     * @param attributeNameStringValueMap
     * @return true if successfully inserted (), false if failed
     */
    public static boolean insertRowOfPrimitiveTypeAttributesIntoTable(
                Connection dbCon, SimpleName schemaName, SimpleName tableName, String nullValueString, Map<SimpleName, String> attributeNameStringValueMap) {
        Set<SimpleName> attributeSet = new HashSet<>();

        for (SimpleName att : attributeNameStringValueMap.keySet()) {
            attributeSet.add(att);
        }
        
        if (!tablePrimaryKeyIsContainedInList(dbCon, schemaName, tableName, attributeSet)) {
            throw new IllegalArgumentException("given string values are not cover the full primary key attribute set;");
        }

        String sql = "INSERT INTO ".concat(makeSchemaTableFullName(schemaName, tableName));
        String attributeNameListString = "";
        String attributeValueListString = "";

        for (SimpleName att : attributeNameStringValueMap.keySet()) {
            String value = attributeNameStringValueMap.get(att);

            if (nullValueString != null && value.equalsIgnoreCase(nullValueString)) {
                continue;
            }

            if (!attributeNameListString.isEmpty()) {
                attributeNameListString = attributeNameListString.concat(", ");
                attributeValueListString = attributeValueListString.concat(", ");
            }

            attributeNameListString = attributeNameListString.concat(att.toString());
            attributeValueListString = attributeValueListString.concat("'").concat(value).concat("'");
            
        }

        sql = sql.concat(" (").concat(attributeNameListString).concat(") VALUES (").concat(attributeValueListString).concat(")");

        try {

            Statement statement = dbCon.createStatement();
            statement.execute(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);
            return false;
        }

    }

    /**
     * make a PreparedStatement SQL string for a table whose attributes are as
     * in the given set, and no additional attributes will be added during the
     * next table populating process; the resulted string will be used together
     * with PreparedStatement to insert records in batch mode; to insert value
     * for attribute in a PreparedStatement, use
     * DerbyDataTypeUtils.setPreparedStatementParameterValue() method;
     *
     * String sql = "insert into employee (name, city, phone) values (?, ?, ?)";
     * Connection connection = new getConnection(); PreparedStatement ps =
     * connection.prepareStatement(sql);
     *
     * final int batchSize = 1000; int count = 0;
     *
     * for (Employee employee: employees) {
     *
     * ps.setString(1, employee.getName()); ps.setString(2, employee.getCity());
     * ps.setString(3, employee.getPhone()); ps.addBatch();
     *
     * if(++count % batchSize == 0) { ps.executeBatch(); } } ps.executeBatch();
     * // insert remaining records ps.close(); connection.close();
     *
     * @param schemaName database schema name that holds the table
     * @param tableName
     * @param attributeList
     * @return
     */
    public static String makeInsertPreparedStatementSQLWithGivenListOfAttributes(SimpleName schemaName, SimpleName tableName, List<SimpleName> attributeList) {
        StringBuilder sb = new StringBuilder();
        sb = sb.append("insert into ").append(makeSchemaTableFullName(schemaName, tableName)).append(" (");
        
        StringBuilder attributeNameList = new StringBuilder();
        StringBuilder valueList = new StringBuilder();

        for (SimpleName sa : attributeList) {
//            System.out.println(sa);
            if (!attributeNameList.toString().isEmpty()) {
                valueList = valueList.append(COMMA);
                attributeNameList = attributeNameList.append(COMMA);
            }
            valueList = valueList.append("?");
            attributeNameList = attributeNameList.append(sa);

        }

//        System.out.println(valueList);
//        System.out.println(attributeNameList);
        sb = sb.append(attributeNameList.toString()).append(")").append(" values (").append(valueList.toString()).append(")");

        return sb.toString();
    }

    /**
     * insert into a data table with the given attribute name value object map;
     *
     * if the attribute is of primitive type if the attribute is of UDT type;
     *
     * if the object value of an attribute is null, and the attribute can be
     * null
     *
     * @param dbCon
     * @param schemaName name of the database schema that holds the target table
     * @param tableName
     * @param attributeNameValueObjectMap
     * @return return true if successfully inserted the record, false otherwise;
     */
    public static boolean insertRowIntoTable(Connection dbCon, SimpleName schemaName, SimpleName tableName, Map<SimpleName, Object> attributeNameValueObjectMap) {
        if (!DerbyTableUtils.doesTableExists(dbCon, schemaName, tableName)) {
            throw new IllegalArgumentException("Table does not exist, can not delete:" + makeSchemaTableFullName(schemaName, tableName));
        }
        if (!DerbyTableUtils.attributeNamesAreAllInTableSchema(dbCon, schemaName, tableName, attributeNameValueObjectMap.keySet())) {
            throw new IllegalArgumentException("given attributes are not all found in the data table");
        }

        List<SimpleName> dataTableColumnNameList = DerbyTableUtils.retrieveAllAttributeNamesAsListFromTable(dbCon, schemaName, tableName);

        String preparedStatementString = DerbyTableUtils.makeInsertPreparedStatementSQLWithGivenListOfAttributes(schemaName, tableName, dataTableColumnNameList);
        
        try {
            PreparedStatement ps = dbCon.prepareStatement(preparedStatementString);

            //1 to attributeNameNewStringValueMap.size() in SET clause
            for (int i = 0; i < dataTableColumnNameList.size(); i++) {
                SimpleName attributeName = dataTableColumnNameList.get(i);
                Object ov = attributeNameValueObjectMap.get(attributeName);
                if (ov == null) {
                    if (!DerbyTableUtils.retrieveMapOfTableAttributeNameIsNullable(dbCon, schemaName, tableName).get(attributeName)) {//check if attribute is nullable
                        throw new IllegalArgumentException("given object value is null for an attribute that is not null");
                    } else {
                        ps.setNull(
                                    i + 1, //
                                    DerbyTableUtils.retrieveMapOfTableAttributeNameDataTypeConstantInteger(dbCon, schemaName, tableName).get(attributeName)//
                        );
                    }
                } else {
                    ps.setObject(i + 1, ov);
                }

            }

            //
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);
            return false;
        }

    }

    /**
     * retrieve a map from attribute name to the value object of the record
     * whose primary key attributes are the same as the given ones
     *
     * @param dbConn
     * @param schemaName
     * @param tableName
     * @param pkAttributeNameStringValueMap
     * @return
     */
    public static Map<SimpleName, Object> retrieveAttributeNameObjectValueMapWithPrimaryAttributeStringValues(
                Connection dbConn, SimpleName schemaName, SimpleName tableName, Map<SimpleName, String> pkAttributeNameStringValueMap) {
        if (!DerbyTableUtils.isTablePrimaryKeyAttributes(dbConn, schemaName, tableName, pkAttributeNameStringValueMap.keySet())) {
            throw new IllegalArgumentException("given attributes are not the same with the primary key attributes");
        }

        ResultSet queryResult = DerbyTableUtils.retrieveAllRowsWithSameAttributesValueAsResultSet(dbConn, schemaName, tableName, pkAttributeNameStringValueMap);

        Map<SimpleName, Object> ret = new HashMap<>();
        List<SimpleName> tableAttributeList = DerbyTableUtils.retrieveAllAttributeNamesAsListFromTable(dbConn, schemaName, tableName);

        try {
            if (queryResult.next()) {//there is at least one record found?
                for (SimpleName att : tableAttributeList) {
                    ret.put(att, queryResult.getObject(att.toString()));
                }
                //
                queryResult.close();
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);
            return null;
        }

        return ret;
    }

    /**
     * create the SQL string that perform ORDER BY operation using the given
     * list of attributes on the given table; SELECT * FROM Customers ORDER BY
     * Country ASC, CustomerName DESC
     *
     * @param schemaName
     * @param tableName
     * @param selectedAttributeList the attribute set in the result set;
     * @param orderedListOfSortedAttribute the ordered list of attributes in the
     * input table to be sorted
     * @param mapOfSortingAttributeByASC whether sort each attribute using ASC
     * or DESC
     * @return
     */
    public static String makeMultiAttributeSortSQLString(SimpleName schemaName, SimpleName tableName, List<SimpleName> selectedAttributeList, List<SimpleName> orderedListOfSortedAttribute, Map<SimpleName, Boolean> mapOfSortingAttributeByASC) {

        String sql = "SELECT ";

        sql = sql.concat(SQLStringUtils.createSQLListOfAttributes(selectedAttributeList));

        sql = sql.concat(" FROM ").concat(makeSchemaTableFullName(schemaName, tableName));

        sql = sql.concat(" ORDER BY ");

        String orderByClauseContentString = "";
        for (SimpleName att : orderedListOfSortedAttribute) {
            if (!orderByClauseContentString.isEmpty()) {
                orderByClauseContentString = orderByClauseContentString.concat(",");
            }

            if (mapOfSortingAttributeByASC.get(att)) {
                orderByClauseContentString = orderByClauseContentString.concat(att.toString()).concat(" ASC");
            } else {
                orderByClauseContentString = orderByClauseContentString.concat(att.toString()).concat(" DESC");
            }

        }
        return sql;
    }

    /**
     * get the number of rows of the given table
     *
     * @param dbConn
     * @param schemaName
     * @param tableName
     * @return
     */
    public static int getRowCountOfTable(Connection dbConn, SimpleName schemaName, SimpleName tableName) {
        String sql = "SELECT COUNT(*) AS rowcount FROM ".concat(makeSchemaTableFullName(schemaName, tableName));

        Statement statement;
        try {
            statement = dbConn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            int count = resultSet.getInt("rowcount");
            resultSet.close();
            return count;
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);

            return 0;
        }
    }

    /**
     *
     * @param dbConn
     * @param schemaName
     * @param tableName
     * @return
     */
    public static ResultSet getAllRecordAsResultSetFromTable(Connection dbConn, SimpleName schemaName, SimpleName tableName) {
        String sql = makeSelectAllFromTableSQL(schemaName, tableName);

        Statement statement;
        try {
            statement = dbConn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            return resultSet;
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);

            return null;
        }

    }

    /**
     * insert the result set of the SQL query into a table with the given
     * attribute list; this method assumes that the output table's attribute
     * names are the same as the given list of attributes; the list of
     * attributes in SELECT clause should be consistent with the given list of
     * attributes in terms of the order and data type (but not the attribute
     * name) the syntax is: INSERT INTO daysorder
     * (ord_date,ord_amount,advance_amount) SELECT
     * ord_date,SUM(ord_amount),SUM(advance_amount) FROM orders GROUP BY
     * ord_date
     * https://www.w3resource.com/sql/insert-statement/inserting-the-result-of-a-query-in-another-table.php
     *
     * @param dbConn
     * @param sql
     * @param schemaName
     * @param outputTableName
     * @param orderedListOfAttributesInTable
     * @return
     */
    public static boolean insertSQLResultSetIntoAnotherTable(Connection dbConn, String sql, SimpleName schemaName, SimpleName outputTableName, List<SimpleName> orderedListOfAttributesInTable) {
        if (!DerbyTableUtils.doesTableExists(dbConn, schemaName, outputTableName)) {
            throw new IllegalArgumentException("output table does not exist:" + makeSchemaTableFullName(schemaName, outputTableName));
        }

        if (orderedListOfAttributesInTable == null || orderedListOfAttributesInTable.isEmpty()) {
            throw new IllegalArgumentException("given list of attributes in output table is null or empty!");
        }

        Set<SimpleName> pkAttributeNameSet = DerbyTableUtils.retrieveAllPrimaryKeyAttributeNamesFromTable(dbConn, schemaName, outputTableName);
        for (SimpleName pkAtt : pkAttributeNameSet) {
            if (!orderedListOfAttributesInTable.contains(pkAtt)) {
                throw new IllegalArgumentException("given list of attributes does not cover all primary key attributes in the output table");
            }
        }

        List<SimpleName> outputTableAttributeNameList = DerbyTableUtils.retrieveAllAttributeNamesAsListFromTable(dbConn, schemaName, outputTableName);
        String outputAttributesString = "";

        Set<SimpleName> uniqueAttributeNameSet = new HashSet<>();

        for (SimpleName attName : orderedListOfAttributesInTable) {
            if (!outputTableAttributeNameList.contains(attName)) {
                throw new IllegalArgumentException("Given output attribute name:" + attName + " is not in the output table schema:" + makeSchemaTableFullName(schemaName, outputTableName));
            }

            if (!uniqueAttributeNameSet.contains(attName)) {
                uniqueAttributeNameSet.add(attName);
            } else {
                throw new IllegalArgumentException("duplicate attribute name found in the given list of output table attributes:" + attName);
            }

            if (!outputAttributesString.isEmpty()) {
                outputAttributesString = outputAttributesString.concat(", ");
            }
            outputAttributesString = outputAttributesString.concat(attName.toString());
        }

        String insertToTableSql = "INSERT INTO ".concat(makeSchemaTableFullName(schemaName, outputTableName)).concat(" ( ").concat(outputAttributesString).concat(" ) ").concat(sql);
//        System.out.println("insert into table sql:"+insertToTableSql);

        Statement statement;

        try {
            statement = dbConn.createStatement();

            //????
            statement.execute(insertToTableSql);

            //todo
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);

            return false;
        }
    }

    /**
     * SELECT COUNT(*) FROM TABLENAM
     *
     * @param schemaName
     * @param tableName
     * @return
     */
    public static String makeSelectRowCountOfTableSQL(SimpleName schemaName, SimpleName tableName) {
        StringBuilder sb = new StringBuilder();
        sb = sb.append("SELECT COUNT(*) AS rowcount FROM ").append(tableName.toString());

        return sb.toString();
    }
    
    /**
     * SELECT * FROM TABLENAM
     *
     * @param schemaName
     * @param tableName
     * @return
     */
    public static String makeSelectAllFromTableSQL(SimpleName schemaName, SimpleName tableName) {
        StringBuilder sb = new StringBuilder();
        sb = sb.append("SELECT * FROM ").append(makeSchemaTableFullName(schemaName, tableName));

        return sb.toString();
    }
    
    /**
     * SELECT c1, c2, c3, ...
     *
     * @param colNameList
     * @return
     */
    public static String makeSelectColumnsClause(List<SimpleName> colNameList) {
        String ret = "";

        for (SimpleName col : colNameList) {
            if (!ret.isEmpty()) {
                ret = ret.concat(", ");
            }
            ret = ret.concat(col.toString());
        }

        return "SELECT ".concat(ret).concat(" ");
    }

//    public static String makeOrderByClause(SortStrategy sortStrategy) {
//        String ret = "";
//
//        for (int i = 0; i < sortStrategy.getOrderedListOfTableColumnWrapper().size(); i++) {
//            if (!ret.isEmpty()) {
//                ret = ret.concat(", ");
//            }
//            ret = ret
//                        .concat(sortStrategy.getOrderedListOfTableColumnWrapper().get(i).getColName().toString()) //col name
//                        .concat(" ")
//                        .concat(sortStrategy.getOrderedListOfSortType().get(i).toString());//sort order type
//        }
//        
//        return "ORDER BY ".concat(ret).concat(" ");
//    }

    /**
     * INSERT INTO table2 (column1, column2, column3, ...)
     *
     * @param tableName
     * @param colList
     * @return
     */
    public static String makeInsertIntoTableColumnsClause(SimpleName schemaName, SimpleName tableName, List<SimpleName> colList) {
        String columnList = "";

        for (SimpleName col : colList) {
            if (!columnList.isEmpty()) {
                columnList = columnList.concat(", ");
            }
            columnList = columnList.concat(col.toString());
        }

        return "INSERT INTO ".concat(makeSchemaTableFullName(schemaName, tableName)).concat(" ").concat("(").concat(columnList).concat(")");
    }

    /**
     * delete the table from db;
     *
     * @param dbConn
     * @param tableName
     * @return
     */
    public static boolean dropTable(Connection dbConn, SimpleName schemaName, SimpleName tableName) {
        if (!DerbyTableUtils.doesTableExists(dbConn, schemaName, tableName)) {
            throw new IllegalArgumentException("Table does not exist, can not delete:" + makeSchemaTableFullName(schemaName, tableName));
        }

        String sql = "DROP TABLE ".concat(makeSchemaTableFullName(schemaName, tableName));

        Statement statement;

        try {
            statement = dbConn.createStatement();
            statement.executeUpdate(sql);
//            System.out.println("Table "+ tableName+" is deleted from database");
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);

            return false;
        }
    }

    /**
     * delete all rows in the table, but keep the table
     *
     * @param dbConn
     * @param tableName
     * @return
     */
    public static boolean clearTable(Connection dbConn, SimpleName schemaName, SimpleName tableName) {
        if (!DerbyTableUtils.doesTableExists(dbConn, schemaName, tableName)) {
            throw new IllegalArgumentException("Table does not exist, can not clear:" + makeSchemaTableFullName(schemaName, tableName));
        }

        String sql = "TRUNCATE TABLE ".concat(makeSchemaTableFullName(schemaName, tableName));

        Statement statement;

        try {
            statement = dbConn.createStatement();
            statement.executeUpdate(sql);
//            System.out.println("Data in Table "+ tableName+" are deleted");
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);

            return false;
        }
    }

    /**
     * rename the table name
     * @param dbConn
     * @param schemaName
     * @param tableName
     * @param newName
     * @return
     */
    public static boolean renameTable(Connection dbConn, SimpleName schemaName, SimpleName tableName, SimpleName newName) {
        if (!DerbyTableUtils.doesTableExists(dbConn, schemaName, tableName)) {
            throw new IllegalArgumentException("Table does not exist, can not rename:" + makeSchemaTableFullName(schemaName, tableName));
        }
        if (DerbyTableUtils.doesTableExists(dbConn, schemaName, newName)) {
            throw new IllegalArgumentException("Table with the new name alreayd exist, cannot rename another table to it:" + makeSchemaTableFullName(schemaName, newName));
        }

        if (tableName.equals(newName)) {
            throw new IllegalArgumentException("new Table name is the same with the current name:" + newName);
        }

        //note that derby use a different syntax: RENAME TABLE table-Name TO new-Table-Name
        //in mysql: ALTER TABLE table_name RENAME TO new_table_name;
        String sql = "RENAME TABLE ".concat(makeSchemaTableFullName(schemaName, tableName)).concat(" TO ").concat(makeSchemaTableFullName(schemaName, newName));

        Statement statement;

        try {
            statement = dbConn.createStatement();
            statement.executeUpdate(sql);
//            System.out.println("Table "+ tableName+" is renamed to: "+newName);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);

            return false;
        }
    }

    /**
     * change the primary key of the given table to the given list of attributes
     *
     * @param con
     * @param schemaName
     * @param tableName
     * @param newPKAttributeNameList
     */
    public static void alterPrimaryKey(Connection con, SimpleName schemaName, SimpleName tableName, List<SimpleName> newPKAttributeNameList) {
        if (!DerbyTableUtils.doesTableExists(con, schemaName, tableName)) {
            throw new IllegalArgumentException("Table does not exist, can not rename:" + makeSchemaTableFullName(schemaName, tableName));
        }

        if (newPKAttributeNameList == null || newPKAttributeNameList.isEmpty()) {
            throw new IllegalArgumentException("given newPKAttributeNameList is null or empty");
        }

        if (!DerbyTableUtils.attributeNamesAreAllInTableSchema(con, schemaName, tableName, newPKAttributeNameList)) {
            throw new IllegalArgumentException("at least one of the attributes in the new primary key is not found in the given table");
        }

        String newPK = "";
        for (SimpleName att : newPKAttributeNameList) {
            if (!newPK.isEmpty()) {
                newPK = newPK.concat(",");
            }
            newPK = newPK.concat(att.toString());
        }

        String dropPKSQL = "ALTER TABLE ".concat(makeSchemaTableFullName(schemaName, tableName)).concat(" DROP PRIMARY KEY");
        String addPKSQL = "ALTER TABLE ".concat(makeSchemaTableFullName(schemaName, tableName)).concat(" ADD PRIMARY KEY ( ").concat(newPK).concat(" )");

        Statement statement;

        try {
            statement = con.createStatement();
            statement.executeUpdate(dropPKSQL);
            statement.executeUpdate(addPKSQL);
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);

        }

    }

    /**
     * 
     * @param con
     * @param schemaName
     * @param tableName
     * @param attributeName 
     */
    public static void alterAttributeToNotNull(Connection con, SimpleName schemaName, SimpleName tableName, SimpleName attributeName) {
        if (!DerbyTableUtils.doesTableExists(con, schemaName, tableName)) {
            throw new IllegalArgumentException("Table does not exist, can not rename:" + tableName);
        }

        if (attributeName == null) {
            throw new IllegalArgumentException("given attributeName is null or empty");
        }

        List<SimpleName> attList = new ArrayList<>();
        attList.add(attributeName);
        if (!DerbyTableUtils.attributeNamesAreAllInTableSchema(con, schemaName, tableName, attList)) {
            throw new IllegalArgumentException("at least one of the attributes in the new primary key is not found in the given table");
        }

        String sql = "ALTER TABLE ".concat(makeSchemaTableFullName(schemaName, tableName)).concat(" ALTER COLUMN ").concat(attributeName.toString()).concat(" NOT NULL");

        Statement statement;

        try {
            statement = con.createStatement();
            statement.executeUpdate(sql);
//            statement.executeUpdate(addPKSQL);
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);

        }

    }

    /**
     * delete all the rows in the given table if their attributes values are the
     * same with the given ones;
     * 
     * @param con
     * @param schemaName
     * @param tableName
     * @param attributeNameStringValueMap
     */
    public static void deleteRowsByAttributeValues(Connection con, SimpleName schemaName, SimpleName tableName, Map<SimpleName, String> attributeNameStringValueMap) {
        if (con == null) {
            throw new IllegalArgumentException("given connection is null");
        }
        if (tableName == null) {
            throw new IllegalArgumentException("given tableName is null or empty");
        }
        if (!DerbyTableUtils.doesTableExists(con, schemaName, tableName)) {
            throw new IllegalArgumentException("Table does not exist:" + makeSchemaTableFullName(schemaName, tableName));
        }
        
        if (attributeNameStringValueMap == null || attributeNameStringValueMap.isEmpty()) {
            throw new IllegalArgumentException("given attributeNameStringValueMap is null or empty");
        }

        if (!DerbyTableUtils.attributeNamesAreAllInTableSchema(con, schemaName, tableName, attributeNameStringValueMap.keySet())) {
            throw new IllegalArgumentException("given attributeNameStringValueMap contains attribute name that is not found in the given table schema");
        }

        String sqlString = "DELETE FROM ".concat(makeSchemaTableFullName(schemaName, tableName)).concat(" WHERE ");

        ///////////////////////////////////
        List<SimpleName> orderedAttributeNameList = new ArrayList<>();
        String whereClause = "";
        
        for (SimpleName att : attributeNameStringValueMap.keySet()) {
            if (!whereClause.isEmpty()) {
                whereClause = whereClause.concat(" AND ");
            }
            whereClause = whereClause.concat(att.toString()).concat("=?");

            orderedAttributeNameList.add(att);
        }
        
        try {
            PreparedStatement ps = con.prepareStatement(sqlString.concat(whereClause));

            Map<SimpleName, String> tableAttributeNameDataTypeMap = DerbyTableUtils.retrieveMapOfTableAttributeNameSQLDataTypeString(con, schemaName, tableName);
            for (int i = 0; i < orderedAttributeNameList.size(); i++) {
                DerbyDataTypeUtils.setPreparedStatementParameterValueForPrimitiveTypeAttribute(
                            ps, //
                            i + 1, //index
                            tableAttributeNameDataTypeMap.get(orderedAttributeNameList.get(i)), //data type
                            attributeNameStringValueMap.get(orderedAttributeNameList.get(i)), //value
                            false);

            }

            ps.execute();

        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * retrieve the values of an attribute of the given table and return it
     *
     * @param con
     * @param schemaName
     * @param tableName
     * @param attributeName
     * @return
     */
    public static List<String> retrieveStringValueSetOfGivenTableAttribute(Connection con, SimpleName schemaName, SimpleName tableName, SimpleName attributeName) {
        if (con == null) {
            throw new IllegalArgumentException("given connection is null");
        }
        if (tableName == null) {
            throw new IllegalArgumentException("given tableName is null or empty");
        }
        if (!DerbyTableUtils.doesTableExists(con, schemaName, tableName)) {
            throw new IllegalArgumentException("Table does not exist:" + makeSchemaTableFullName(schemaName, tableName));
        }

        Set<SimpleName> attSet = new HashSet<>();
        attSet.add(attributeName);
        if (!DerbyTableUtils.attributeNamesAreAllInTableSchema(con, schemaName, tableName, attSet)) {
            throw new IllegalArgumentException("given attributeName is not found in the given table:" + makeSchemaTableFullName(schemaName, tableName));
        }

        String sqlString = "SELECT ".concat(attributeName.toString()).concat(" FROM ").concat(makeSchemaTableFullName(schemaName, tableName));

        Statement statement;

        List<String> ret = new ArrayList<>();
        try {

            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sqlString);

            while (rs.next()) {
                ret.add(rs.getString(attributeName.toString()));

            }

            return ret;
//            statement.executeUpdate(addPKSQL);
        } catch (SQLException ex) {
            Logger.getLogger(DerbyTableUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);
            return null;
        }
    }

    /**
     * execute the given sql string in the given db
     *
     * @param con
     * @param sqlString
     * @throws SQLException
     */
    public static void runSQLQuery(Connection con, String sqlString) throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("given connection is null");
        }
        if (sqlString == null || sqlString.isEmpty()) {
            throw new IllegalArgumentException("given sqlString is null or empty");
        }

        Statement statement = con.createStatement();

        statement.execute(sqlString);

    }

    /**
     * return a ResultSet which contains all the records of the given table
     *
     * @param con
     * @param schemaName
     * @param tableName
     * @return
     * @throws SQLException
     */
    public static ResultSet retrieveAllRecordsFromTable(Connection con, SimpleName schemaName, SimpleName tableName) throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("given connection is null");
        }
        if (tableName == null) {
            throw new IllegalArgumentException("given tableName is null");
        }

        String sqlString = makeSelectAllFromTableSQL(schemaName, tableName);

        Statement statement = con.createStatement();
        
        ResultSet ret = statement.executeQuery(sqlString);
        
        return ret;
    }
    
    /**
     * run the given sql query and output the ResultSet
     * @param con
     * @param sqlQuery
     * @return
     * @throws SQLException 
     */
    public static ResultSet retrieveSqlQueryResultSet(Connection con, String sqlQuery) throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("given connection is null");
        }
        if (sqlQuery == null||sqlQuery.isEmpty()) {
            throw new IllegalArgumentException("given sqlQuery is null or empty");
        }

//        String sqlString = makeSelectAllFromTableSQL(tableName);

        Statement statement = con.createStatement();

        ResultSet ret = statement.executeQuery(sqlQuery);

        return ret;
    }

    /**
     * select distinct c1, c2, .... from table
     *
     * @param con
     * @param tableName
     * @param colNameList
     * @return
     * @throws SQLException
     */
    public static ResultSet retrieveUniqueRecordsOfColumnsFromTable(Connection con, SimpleName schemaName, SimpleName tableName, List<SimpleName> colNameList) throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("given connection is null");
        }
        if (tableName == null) {
            throw new IllegalArgumentException("given tableName is null");
        }
        if (colNameList == null || colNameList.isEmpty()) {
            throw new IllegalArgumentException("given colNameList is null or empty");
        }

        String selectClause = "";

        for (SimpleName col : colNameList) {
            if (!selectClause.isEmpty()) {
                selectClause = selectClause.concat(", ");
            }
            selectClause = selectClause.concat(col.toString());
        }

        String sqlString = "SELECT DISTINCT ".concat(selectClause).concat(" ").concat("FROM ").concat(makeSchemaTableFullName(schemaName, tableName));

        Statement statement = con.createStatement();

        ResultSet ret = statement.executeQuery(sqlString);

        return ret;

    }
}
