package basic.lookup.project.type.udt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basic.SimpleName;
import basic.lookup.project.type.VisframeUDTTypeManagerBase;
import context.project.VisProjectDBContext;
import context.project.process.AbstractProcessPerformer;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.table.lookup.ManagementTableColumn;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;
import visinstance.run.layoutconfiguration.VisInstanceRunLayoutConfiguration;
import visinstance.run.layoutconfiguration.VisInstanceRunLayoutConfigurationID;

/**
 * 
 * @author tanxu
 *
 */
public class VisProjectVisInstanceRunLayoutConfigurationManager extends VisframeUDTTypeManagerBase<VisInstanceRunLayoutConfiguration, VisInstanceRunLayoutConfigurationID>{
	/**
	 * uid of the owner VisInstance of the VisInstanceRun
	 */
	public static final ManagementTableColumn VISINSTANCERUN_UID_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("VISINSTANCERUN_UID"), SQLDataTypeFactory.integerType(), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null 
			);
	////////////////////
	private Map<SimpleName, ManagementTableColumn> primaryKeyAttributeNameMap;
	
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	public VisProjectVisInstanceRunLayoutConfigurationManager(VisProjectDBContext visProjectDBContext) {
		super(visProjectDBContext, VisInstanceRunLayoutConfiguration.class, VisInstanceRunLayoutConfigurationID.class);
	}
	
	
	/////////////////////////////////////////////
	
//	/**
//	 * check if the VisInstanceRun with the given visInstanceUID and CFDGraphIndependetFIVStringValueMap is already calculated and existing in the host VisProjectDBContext's rdb or not;
//	 * 
//	 * in details, 
//	 * 1. retrieve all VisInstanceRun objects from the lookup table with the same visInstanceUID with the given visInstanceUID 
//	 * 2. compare the given CFDGraphIndependetFIVStringValueMap with each of retrieved VisInstanceRuns;
//	 * 3. if one with the same CFDGraphIndependetFIVStringValueMap is found, return true;
//	 * 		else, return false;
//	 * 
//	 * @param id
//	 * @return
//	 * @throws SQLException 
//	 */
//	public boolean isLayoutAlreadyExisting(VisInstanceID visInstanceID, IndependetFIVTypeIDStringValueMap assignedCFDGraphIndependetFIVStringValueMap) throws SQLException {
//		Statement statement = this.getVisProjectDBConnection().createStatement(); 
//		ResultSet rs = statement.executeQuery(this.buildSelectAllVisInstanceRunOfVisInstanceSqlString(visInstanceID));
//		
//		while(rs.next()) {
//			VisInstanceRun run = (VisInstanceRun)rs.getObject(this.getManagementTableSchema().getColumnIndex(this.getVisframeUDTColumn().getName()));
//			
//			if(run.getCFDGraphIndependetFIVStringValueMap().equals(assignedCFDGraphIndependetFIVStringValueMap)) {
//				return true;
//			}
//		}
//		
//		return false;
//	}
	
//	/**
//	 * build and return sql string that select all rows from the management table with the VISINSTANCE_UID column equal to the uid of the given VisInstanceID;
//	 * facilitate {@link #isVisInstanceRunAlreadyCalculated(VisInstanceID, IndependetFIVTypeIDStringValueMap)} method
//	 * @param visInstanceID
//	 * @return
//	 */
//	private String buildSelectAllVisInstanceRunOfVisInstanceSqlString(VisInstanceID visInstanceID) {
//		String ret = TableContentSQLStringFactory.buildSelectAllSqlString(
//				SqlStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()), 
//				TableContentSQLStringFactory.buildColumnValueEquityCondition(VISINSTANCE_UID_COLUMN.getName().getStringValue(), Integer.toString(visInstanceID.getUID()), false), 
//				null, 
//				null
//				);
//		
//		return ret;
//	}
	
	//////////////////////////////////////////////
	/**
	 * find and return the next available VisInstanceRun UID
	 * 
	 * note that VisInstanceRun UID should be unique among all VisInstanceRuns in the host VisProjectDBContext;
	 * 
	 * return 1 if there is
	 * @param visInstanceUID
	 * @return
	 * @throws SQLException 
	 */
	public int findNextAvailableUID() throws SQLException {
		Statement statement = this.getVisProjectDBConnection().createStatement(); 
		ResultSet rs = statement.executeQuery(this.buildSelectAllSortedByUIDWithASCOrderSqlString());
		
		int ret = 1;
		if(!rs.next()) {//there is no rows, return 1;
			return 1;
		}else {
			int i = rs.getInt(this.getManagementTableSchema().getColumnIndex(VisInstanceRunLayoutConfigurationID.UID_COLUMN.getName()));
			if(i>ret) //first row's UID is larger than 1, simply return 1;
				return ret;
			else //first row must be equal to 1;
				ret++;
		}
		
		while(rs.next()) {
			int i = rs.getInt(this.getManagementTableSchema().getColumnIndex(VisInstanceRunLayoutConfigurationID.UID_COLUMN.getName()));
			if(i==ret) //current row's UID is 1 larger than previous one;
				ret++;
			else //current row's UID is 2 or more larger than previous one, available UID found!;
				break;
		}
		
		return ret;
		
	}
	
	/**
	 * build and return the sql that query all VisInstanceRun records from the management table ordered by the run UID column with ASC order;
	 * facilitate the {@link #findNextAvailableRunUID()} method
	 * @return
	 */
	private String buildSelectAllSortedByUIDWithASCOrderSqlString() {
		List<String> orderByColumnNameList = new ArrayList<>();
		orderByColumnNameList.add(VisInstanceRunLayoutConfigurationID.UID_COLUMN.getName().getStringValue());
		List<Boolean> orderByASCList = new ArrayList<>();
		orderByASCList.add(true);
		
		String ret = TableContentSQLStringFactory.buildSelectAllSQLString(
				SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()), 
				null,//conditionString
				orderByColumnNameList, 
				orderByASCList
				);
		
		return ret;
	}
	
	
	//////////////////////////////////////////////////////
	/**
	 * primary key column set only contains the VisInstance Run UID column;
	 * 
	 */
	@Override
	public Map<SimpleName, ManagementTableColumn> getPrimaryKeyAttributeNameMap() {
		if(this.primaryKeyAttributeNameMap == null) {
			primaryKeyAttributeNameMap = new HashMap<>();
		
			primaryKeyAttributeNameMap.put(VisInstanceRunLayoutConfigurationID.UID_COLUMN.getName(), VisInstanceRunLayoutConfigurationID.UID_COLUMN);
		}
		
		return primaryKeyAttributeNameMap;
	}
	
	/**
	 * VisInstance UID is not in the VisInstanceRunID (thus not a primary key column), but is included as a non-PK column
	 */
	@Override
	public List<ManagementTableColumn> getTypeSpecificManagementTableColumnList() {
		List<ManagementTableColumn> ret = new ArrayList<>();
		ret.add(VISINSTANCERUN_UID_COLUMN);
		return ret;
	}
	
	
	@Override
	protected void setTypeSpecificColumnValues(PreparedStatement ps, VisInstanceRunLayoutConfiguration entity) throws SQLException {
		ps.setInt(
				this.getManagementTableSchema().getColumnIndex(VISINSTANCERUN_UID_COLUMN.getName()), 
				entity.getVisInstanceRunID().getRunUID()
				);
	}
	
	
	/**
	 * note that insertion of INVOLVED_CFTARGETVALUETABLERUN_ID_SET column is done by the method
	 * {@link VisframeUDTTypeManagerBase#setInvolvedCFTargetValueTableRunIDSetColumn(PrimaryKeyID, VfIDCollection)} in {@link AbstractProcessPerformer#postprocess()}
	 */
	@Override
	protected void typeSpecificInsertionRelatedActivity(VisInstanceRunLayoutConfiguration t) {
		//do nothing
	}
	
}
