/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sql.derby;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author tanxu
 */
public class DerbyDBUtils {
    /* the default framework is embedded */
//    private static final String FRAMEWORK = "embedded";
    private static final String PROTOCOL = "jdbc:derby:";
    private static final String CREATE_DB_OPTION = ";create=true";
    private static final String SHUT_DOWN_DB_OPTION = ";shutdown=true";
    
    
    /**
     * check if the given folder with name dbName under the given directory dbDir is a folder of a database;
     * note that even if the dbName folder exists, it is not necessarily a database folder;
     * 
     * @param dbDir
     * @param dbName
     * @return 
     * @throws SQLException 
     */
    public static boolean dbExists(Path dbDir, String dbName) throws SQLException{
        Connection con;
//        try {
        con = DerbyDBUtils.getEmbeddedDBConnection(dbDir, dbName, false);
//        } catch (SQLException ex) {
//            Logger.getLogger(DerbyDBUtilsTest.class.getName()).log(Level.SEVERE, null, ex);
//            con = null;
//        }
        
        if(con == null){//not existing
            return false;
        }else{//existing, need to shutdown the connection first for later reconnection;
//            if(shutDownEmbeddedDB(dbDir, dbName)){
//                return true;
//            }else{
//                return false;
//            }
            try {
                //
                con.close();
                System.out.println("closed normally");
            } catch (SQLException ex) {
                Logger.getLogger(DerbyDBUtils.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("not shutdown normally");;
            }
            
            return true;
            
        }
    }
    
//    /**
//     * retrieve the names of all user defined tables in the given database connection;
//     * see the details in the implementation;
//     * reference: https://www.progress.com/blogs/jdbc-tutorial-extracting-database-metadata-via-jdbc-driver
//     * @param con
//     * @return 
//     */
//    public static Set<String> retrieveAllUserDefinedTableNames(Connection con){
//        Set<String> ret = new HashSet<>();
//        
//        try {
//            DatabaseMetaData databaseMetaData = con.getMetaData();
//            //databaseMetaData.getTables() method's last parameter is an array of all table types that need to be retrieved;
//            //DatabaseMetaData.getTableTypes() method return all table types, Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
//            ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[]{"TABLE"}); //TABLE type is user defined table type
//            
//            //returned result set contains information of all retrieved tables;
//            //Each table description has the following columns:
//            //
//            //TABLE_CAT String => table catalog (may be null)
//            //TABLE_SCHEM String => table schema (may be null)
//            //TABLE_NAME String => table name
//            //TABLE_TYPE String => table type. Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
//            //REMARKS String => explanatory comment on the table
//            //TYPE_CAT String => the types catalog (may be null)
//            //TYPE_SCHEM String => the types schema (may be null)
//            //TYPE_NAME String => type name (may be null)
//            //SELF_REFERENCING_COL_NAME String => name of the designated "identifier" column of a typed table (may be null)
//            //REF_GENERATION String => specifies how values in SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER", "DERIVED". (may be null)
//            
//            //iterate through the ResultSet;
//            while(resultSet.next())
//            {
////                System.out.println(resultSet.getString("TABLE_NAME"));
//                ret.add(resultSet.getString("TABLE_NAME"));
//            }
//            
//            
//            
//        } catch (SQLException ex) {
//            Logger.getLogger(DerbyDBUtils.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        return ret;
//    }
    
    
    /**
     * try to get connection to a database; if db not exist, either return null or try to create one; 
     * @param dbDir the directory of the folder where to create or get the database directory
     * @param dbName
     * @param createDBIfNotExist
     * @return the connection to the db if it exists or have been successfully created; null if not exist, and not created;
     * @throws java.sql.SQLException 
     */
    public static Connection getEmbeddedDBConnection(Path dbDir, String dbName, boolean createDBIfNotExist) throws SQLException{
        String databaseURL = PROTOCOL.concat(Paths.get(dbDir.toString(), dbName).toString());
        System.out.println(databaseURL);
        
        Connection conn;
        
        if(createDBIfNotExist){
            databaseURL = databaseURL.concat(CREATE_DB_OPTION);
            
            conn = DriverManager.getConnection(databaseURL);
            
            return conn;
        }else{//not create db if not exist, if there is no db, return a null value;
//            try{
                conn = DriverManager.getConnection(databaseURL);
//            }catch(SQLException e){
//                System.out.println("not existing;");
////                printSQLException(e);
//                return null;//return null if no db exist;
//            }
            return conn;
        }
    }
    
    
    /**
     * shut down the derby engine, thus all the databases;
     * used when app is closed and all connected dbs need to be shutdown;
     */
    public static void shutDownDerbyEngine(){
        try {
            //This throws SQException even if the database was shutdown normally, 
            //so you should catch the exception and check SQL state like following;
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("XJ015")) {//
                System.out.println("Derby shutdown normally");
            }else{
                printSQLException(ex);
            }
        }
    }
    
    
    /**
     * shutdown the db by using DriverManager.getConnection() method with ';shutdown=true' option;
     * @param dbDir
     * @param dbName
     * @return true if the db is normally shutdown
     */
    public static boolean shutDownEmbeddedDB(Path dbDir, String dbName){
//        String databaseURL = PROTOCOL.concat(makeURLStringOfNewFolderUnderParentDir(dbDir.toString(),dbName)).concat(SHUT_DOWN_DB_OPTION);
        String databaseURL = PROTOCOL.concat(Paths.get(dbDir.toString(),dbName).toString()).concat(SHUT_DOWN_DB_OPTION);
        
        System.out.println(databaseURL);
        try{
            //This throws SQException even if the database was shutdown normally, 
            //so you should catch the exception and check SQL state like following;
            DriverManager.getConnection(databaseURL);
            
        }catch(SQLException ex){
            if (ex.getSQLState().equals("XJ015")) {//
                System.out.println("Derby shutdown normally");
                return true;
            } else {
                printSQLException(ex);
                return false;
            }
        }
        
        //should never be reached
        return true;
    }
    
    
    /**
     * delete a db with the given name and directory;
     * There is no drop database command. To drop a database, delete the database directory with operating system commands. 
     * The database must not be booted when you remove a database. You can get a list of booted databases with getPropertyInfo.
     * @param dbDir
     * @param dbName
     * @return 
     */
    public static boolean deleteEmbeddedDB(File dbDir, String dbName){
    	//TODO
        return true;
    }
    
    
    /**
     * Prints details of an SQLException chain to <code>System.err</code>.
     * Details included are SQL State, Error code, Exception message.
     *
     * @param e the SQLException from which to print details.
     */
    public static void printSQLException(SQLException e)
    {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        while (e != null)
        {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            // for stack traces, refer to derby.log or uncomment this:
            //e.printStackTrace(System.err);
            e = e.getNextException();
        }
    }
    
    
    
    /**
     * 
     * @param parentDirString path string of an existing directory
     * @param folderNameString
     * @return 
     */
    public static String makeURLStringOfNewFolderUnderParentDir(String parentDirString, String folderNameString){
        File parentDir = new File(parentDirString);
        return makeURLStringOfNewFolderUnderParentDir(parentDir,folderNameString);
    }
    
    /**
     * 
     * @param parentDir an existing directory
     * @param folderNameString name of a new folder, can not contain any file system separator string
     * @return 
     */
    public static String makeURLStringOfNewFolderUnderParentDir(File parentDir, String folderNameString){
        if(!parentDir.isDirectory()){
            throw new IllegalArgumentException("given parent directory is not an existing directory");
        }
        
        if(folderNameString.contains(File.separator)){
            throw new IllegalArgumentException("given folder name is invalid");
        }
        
        System.out.println(parentDir.getAbsolutePath().concat(File.separator).concat(folderNameString));
        return parentDir.getAbsolutePath().concat(File.separator).concat(folderNameString);
        
    }
}
