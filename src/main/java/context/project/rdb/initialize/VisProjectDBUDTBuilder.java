package context.project.rdb.initialize;

import java.io.Serializable;
import java.sql.SQLException;

import context.project.VisProjectDBFeatures;
import context.project.rdb.VisProjectRDBConstants;
import sql.derby.UDTUtils;

/**
 * create and check all UDT defined by visframe in the in the MANAGEMENT schema of the DB of a VisProjectDBContext
 * @author tanxu
 *
 */
public class VisProjectDBUDTBuilder extends AbstractVisProjectDBInitializer{

	/**
	 * constructor
	 * @param projectDBConnection
	 */
	public VisProjectDBUDTBuilder(VisProjectDBFeatures projectDBFeatures) {
		super(projectDBFeatures);
	}
	
	/**
	 * create all UDTs in the MANAGEMENT schema
	 */
	@Override
	public void initialize() throws SQLException {
		for(Class<? extends Serializable> udt:VisProjectDBUDTConstants.visframeUDTAliasNameMap.keySet()) {
			
			UDTUtils.createUDTType(
					this.getProjectDBConnection(), 
					VisProjectRDBConstants.MANAGEMENT_SCHEMA_NAME.getStringValue(), 
					VisProjectDBUDTConstants.visframeUDTAliasNameMap.get(udt), 
					udt
					);
			
		}
		
	}
	
	
	/**
	 * @throws SQLException 
	 * 
	 */
	@Override
	public boolean allExist() throws SQLException {
		for(Class<? extends Serializable> udt:VisProjectDBUDTConstants.visframeUDTAliasNameMap.keySet()) {
			if(!UDTUtils.doesUDTExist(
					this.getProjectDBConnection(), 
					VisProjectDBUDTConstants.UDT_SCHEMA_NAME, 
					VisProjectDBUDTConstants.visframeUDTAliasNameMap.get(udt), udt)) {
				return false;
			}
		}
		System.out.println("project UDT types are all found");
		return true;
		
	}
	
	
}
