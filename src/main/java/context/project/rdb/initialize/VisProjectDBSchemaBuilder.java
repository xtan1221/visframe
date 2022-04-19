package context.project.rdb.initialize;

import java.sql.SQLException;

import basic.SimpleName;
import context.project.VisProjectDBFeatures;
import context.project.rdb.VisProjectRDBConstants;
import sql.derby.DBSchemaUtils;

/**
 * builder class that initialize a new DB or check a pre-existing DB of a VisProject
 * @author tanxu
 *
 */
public class VisProjectDBSchemaBuilder extends AbstractVisProjectDBInitializer{
	
	/**
	 * constructor
	 * @param projectDBConnection
	 */
	public VisProjectDBSchemaBuilder(VisProjectDBFeatures projectDBFeatures) {
		super(projectDBFeatures);
	}
	
	
	/**
	 * return true if none of the VisProjectDBContext's schema exist in the given DB connection;
	 * 
	 * used as indicator whether a DB of a VisProjectDBContext is connected for the first time;
	 * 
	 * @return
	 * @throws SQLException 
	 */
	public boolean noneExists() throws SQLException {
		for(SimpleName schemaName:VisProjectRDBConstants.getVisProjectSchemaNameSet()) {
			if(DBSchemaUtils.doesSchemaExist(this.getProjectDBConnection(), schemaName.getStringValue())) {
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * create all Schema in the DB
	 */
	@Override
	public void initialize() throws SQLException {
		for(SimpleName schemaName:VisProjectRDBConstants.getVisProjectSchemaNameSet()) {
			DBSchemaUtils.createSchema(this.getProjectDBConnection(), schemaName.getStringValue());
		}
	}
	
	
	/**
	 * return true if all of the VisProjectDBContext's schema exist in the given DB connection;
	 * @throws SQLException 
	 */
	@Override
	public boolean allExist() throws SQLException {
		
		for(SimpleName schemaName:VisProjectRDBConstants.getVisProjectSchemaNameSet()) {
			if(!DBSchemaUtils.doesSchemaExist(this.getProjectDBConnection(), schemaName.getStringValue())) {
				return false;
			}
		}
		System.out.println("project db schema are all found");
		return true;
	}
	
	
	
}
