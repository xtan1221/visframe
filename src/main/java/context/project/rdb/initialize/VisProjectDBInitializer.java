package context.project.rdb.initialize;

import java.sql.Connection;
import java.sql.SQLException;

import context.project.VisProjectDBFeatures;

public interface VisProjectDBInitializer {
	
	VisProjectDBFeatures getVisProjectDBFeatures();
	
	/**
	 * 
	 * @return
	 * @throws SQLException 
	 */
	default Connection getProjectDBConnection() throws SQLException {
		return this.getVisProjectDBFeatures().getDBConnection();
	}
	
	/**
	 * create all features required by VisProjectDBContext
	 * @throws SQLException
	 */
	void initialize() throws SQLException;
	
	/**
	 * check if all the required features already exist in the DB of the host VisProjectDBContext
	 * @return
	 * @throws SQLException 
	 */
	boolean allExist() throws SQLException;
}
