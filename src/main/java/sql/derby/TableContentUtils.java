package sql.derby;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class TableContentUtils {
	
	/**
	 * retrieve and return the total row number of the given table in the given RDB connection;
	 * 
	 * @param db
	 * @param tableFullPathString
	 * @return
	 * @throws SQLException
	 */
	public static int getTotalRowNum(Connection db, String tableFullPathString) throws SQLException {
		Statement s = db.createStatement();
		ResultSet r = s.executeQuery("SELECT COUNT(*) AS rowcount FROM ".concat(tableFullPathString));
		r.next();
		int count = r.getInt("rowcount");
		r.close();
		return count;
	}
}
