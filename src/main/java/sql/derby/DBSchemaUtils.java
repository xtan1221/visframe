package sql.derby;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



/**
 * note that the schema name in rdb are always upper case regardless of the original schema name;
 * 
 * 
 * @author tanxu
 *
 */
public class DBSchemaUtils {
	
	/**
	 * create a new schema in the rdb of the given Connection
	 * @param dbCon
	 * @param schemaName
	 * @throws SQLException 
	 */
	public static void createSchema(Connection dbCon, String schemaName) throws SQLException {
		if(doesSchemaExist(dbCon, schemaName)) {
			System.out.println("schema already exist, skip");
			return;
		}
		
		String sqlString = "create SCHEMA ".concat(schemaName);
		
		System.out.println(sqlString);
        
        
        Statement statement;
//        try {
        statement = dbCon.createStatement();
        statement.execute(sqlString);
		
	}
	
	/**
	 * template sql string for lookup a schema name in a rdb
	 * schemaName ===
	 */
	private static final String SCHEMA_EXIST_CHECK_TEMPLATE = 
			"select * from SYS.SYSSCHEMAS where SCHEMANAME = 'schemaName'";
	/**
	 * check if the given schema name is already existing in the rdb of the given Connection;
	 * 
	 * note that the schema name should be cast to upper case;
	 * 
	 * @param dbCon
	 * @param schemaName
	 * @return
	 * @throws SQLException 
	 */
	public static boolean doesSchemaExist(Connection dbCon, String schemaName) throws SQLException {
		
		String sqlQueryString = SCHEMA_EXIST_CHECK_TEMPLATE;
		
		sqlQueryString = sqlQueryString.replace("schemaName", schemaName.toUpperCase());
		
		System.out.println(sqlQueryString);
        
        Statement statement;
//        try {
        statement = dbCon.createStatement();
        ResultSet rs = statement.executeQuery(sqlQueryString);
        
        return rs.next();

	}
	
}
