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
import basic.lookup.HasID;
import basic.lookup.ID;
import basic.lookup.project.type.VisframeUDTManagementProcessRelatedTableColumnFactory;
import basic.lookup.project.type.VisframeUDTTypeManagerBase;
import context.project.VisProjectDBContext;
import context.project.process.logtable.VfIDCollection;
import function.composition.CompositionFunctionID;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.sqltype.SQLStringType;
import rdb.table.lookup.ManagementTableColumn;
import sql.LogicOperator;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;
import sql.derby.TableSchemaUtils;
import visinstance.run.VisInstanceRunID;
import visinstance.run.calculation.IndependentFIVTypeIDStringValueMap;
import visinstance.run.calculation.function.composition.CFTargetValueTableRun;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunID;

/**
 * 
 * note that even if the number of employer VisInstanceRun become 0, DO NOT delete the CFTargetValueTableRun record from the management table;
 * 		1. the CFTargetValueTableRun may be employed by new VisInstanceRun later;
 * 		2. when storage space need to be released, all CFTargetValueTableRuns with 0 employer VisInstanceRun and their value tables can be removed independently;
 * 
 * this is a trade-off between computation efficiency and storage space usage efficiency;
 * 
 * 
 * ===================================
 * 
 * 
 * @author tanxu
 * 
 */
public class VisProjectCFTargetValueTableRunManager extends VisframeUDTTypeManagerBase<CFTargetValueTableRun, CFTargetValueTableRunID>{
	/**
	 * CompositionFunctionGroup name column as a non-primary key column;
	 */
	public static final ManagementTableColumn COMPOSITION_FUNCTION_GROUP_NAME_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("COMPOSITION_FUNCTION_GROUP_NAME"), new SQLStringType(50,false), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
			);
	
	/**
	 * CompositionFunction index id column as a non-primary key column;
	 */
	public static final ManagementTableColumn COMPOSITION_FUNCTION_INDEX_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("COMPOSITION_FUNCTION_INDEX"), SQLDataTypeFactory.integerType(), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
		);
	
	/**
	 * CompositionFunction index id column as a non-primary key column;
	 */
	public static final ManagementTableColumn EMPLOYER_VISINSTANCE_RUN_NUM_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("EMPLOYER_RUN_NUM"), SQLDataTypeFactory.integerType(), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
		);
	
	
	////////////////////
	private Map<SimpleName, ManagementTableColumn> primaryKeyAttributeNameMap;
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	public VisProjectCFTargetValueTableRunManager(
			VisProjectDBContext visProjectDBContext 
			) {
		super(visProjectDBContext, CFTargetValueTableRun.class, CFTargetValueTableRunID.class);
	}
	
	//////////////////////////////////////////////
	/**
	 * find and return the next available CFTargetValueTableRun UID for the given CompositionFunctionID;
	 * note that CFTargetValueTableRun UIDs of the same CompositionFunctionID should be distinct from each other
	 * @param cfID
	 * @return
	 * @throws SQLException 
	 */
	public int findNextAvailableUID() throws SQLException {
		Statement statement = this.getVisProjectDBConnection().createStatement(); 
		ResultSet rs = statement.executeQuery(this.getSelectAllSortedByRunUIDWithASCOrderSQLString());
		
		int ret = 1;
		if(!rs.next()) {//there is no rows, return 1;
			return 1;
		}else {
			int i = rs.getInt(this.getManagementTableSchema().getColumnIndex(CFTargetValueTableRunID.RUN_UID_COLUMN.getName()));
			if(i>ret) //first row's UID is larger than 1, simply return 1;
				return ret;
			else //first row must be equal to 1;
				ret++;
		}
		
		while(rs.next()) {
			int i = rs.getInt(this.getManagementTableSchema().getColumnIndex(CFTargetValueTableRunID.RUN_UID_COLUMN.getName()));
			if(i==ret) //current row's UID is 1 larger than previous one;
				ret++;
			else //current row's UID is 2 or more larger than previous one, available UID found!;
				break;
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	private String selectAllSortedByRunUIDWithASCOrderSQLString;
	/**
	 * build and return sql string that select all rows from the management table sorted by the {@link CFTargetValueTableRunID#RUN_UID_COLUMN} with ASC sorting order;
	 * facilitate {@link #findNextAvailableUID()} method
	 * @param visInstanceID
	 * @return
	 */
	private String getSelectAllSortedByRunUIDWithASCOrderSQLString() {
		if(this.selectAllSortedByRunUIDWithASCOrderSQLString==null) {
			List<String> orderByColumnNameList = new ArrayList<>();
			orderByColumnNameList.add(CFTargetValueTableRunID.RUN_UID_COLUMN.getName().getStringValue());
			List<Boolean> orderByASCList = new ArrayList<>();
			orderByASCList.add(true);
			
			this.selectAllSortedByRunUIDWithASCOrderSQLString = TableContentSQLStringFactory.buildSelectAllSQLString(
					SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()), 
					null,
					orderByColumnNameList, 
					orderByASCList
					);
		}
		return this.selectAllSortedByRunUIDWithASCOrderSQLString;
	}
	/////////////////////////////////////
	/**
	 * lookup and return the {@link CFTargetValueTableRun} with the given targetCompositionFunctionID and {@link IndependentFIVTypeIDStringValueMap} existing in the host VisProjectDBContext's rdb;
	 * 
	 * 
	 * if there is no such {@link CFTargetValueTableRun}, return null
	 * =======================
	 * in details, 
	 * 1. retrieve all CFTargetValueTableRun objects from the lookup table with the same targetCompositionFunctionID with the given CFTargetValueTableRunID 
	 * 2. compare the given CFDGraphIndependetFIVStringValueMap with each of retrieved CFTargetValueTableRuns;
	 * 3. if one with the same CFDGraphIndependetFIVStringValueMap is found, return it; else, return null;
	 * 
	 * @param id
	 * @return
	 * @throws SQLException 
	 */
	public CFTargetValueTableRun lookupRun(CompositionFunctionID targetCompositionFunctionID, IndependentFIVTypeIDStringValueMap CFDGraphIndependetFIVTypeStringValueMap) throws SQLException {
		Statement statement = this.getVisProjectDBConnection().createStatement(); 
		ResultSet rs = statement.executeQuery(this.buildSelectAllOfCFSqlString(targetCompositionFunctionID));
		
		while(rs.next()) {
			CFTargetValueTableRun run = (CFTargetValueTableRun)rs.getObject(this.getManagementTableSchema().getColumnIndex(this.getVisframeUDTColumn().getName()));
			
			if(run.getCFDGraphIndependetFIVStringValueMap().equals(CFDGraphIndependetFIVTypeStringValueMap)) {
				return run;
			}
		}
		
		return null;
	}
	
	/**
	 * build and return sql string that select all rows from the management table of the given CompositionFunctionID;
	 * facilitate {@link #lookupRun(CompositionFunctionID, IndependentFIVTypeIDStringValueMap)} method
	 * @param visInstanceID
	 * @return
	 */
	private String buildSelectAllOfCFSqlString(CompositionFunctionID targetCompositionFunctionID) {
		List<String> colNameList = new ArrayList<>();
		List<String> valueStringList = new ArrayList<>();
		List<Boolean> ofStringTypeList = new ArrayList<>();
		List<Boolean> toIgnoreCaseList = new ArrayList<>(); 
		List<LogicOperator> logicOperatorList = new ArrayList<>();
		colNameList.add(COMPOSITION_FUNCTION_GROUP_NAME_COLUMN.getName().getStringValue());
		colNameList.add(COMPOSITION_FUNCTION_INDEX_COLUMN.getName().getStringValue());
		valueStringList.add(targetCompositionFunctionID.getHostCompositionFunctionGroupID().getName().getStringValue());
		valueStringList.add(Integer.toString(targetCompositionFunctionID.getIndexID()));
		ofStringTypeList.add(true);
		ofStringTypeList.add(false);
		toIgnoreCaseList.add(true);
		toIgnoreCaseList.add(null);
		logicOperatorList.add(LogicOperator.AND);
		
		
		String ret = TableContentSQLStringFactory.buildSelectAllSQLString(
				SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()), 
				TableContentSQLStringFactory.buildColumnValueEquityCondition(colNameList, valueStringList, ofStringTypeList, toIgnoreCaseList, logicOperatorList),
				null, 
				null
				);
			
		return ret;
	}
	
	//////////////////////////////////////////
	/**
	 * add the given VisInstanceRunID to the column {@link VisframeUDTManagementProcessRelatedTableColumnFactory#employerVisInstanceRunIDSetColumn} of the row with the given CFTargetValueTableRunID;
	 * also add to the involved CFTargetValueTableRunID set of the currently running process in the log table
	 * 
	 * 1. retrieve the employerVisInstanceRunIDSetColumn value VfIDCollection
	 * 2. add the visInstanceRunID
	 * 3. update the employerVisInstanceRunIDSetColumn value
	 * 4. add to the involved CFTargetValueTableRunID set of the currently running process in the log table; 
	 * 		the running process ID should be the same with the given visInstanceRunID;
	 * @param CFTargetValueTableRunID
	 * @param visInstanceRunID
	 * @throws SQLException 
	 */
	public void addEmployerVisInstanceRunID(CFTargetValueTableRunID CFTargetValueTableRunID, VisInstanceRunID visInstanceRunID) throws SQLException {
		//get the employerVisInstanceRunIDSet
		VfIDCollection employerVisInstanceRunIDSet = (VfIDCollection) this.retrieveRow(CFTargetValueTableRunID).getEmployerVisInstanceRunIDSet();
		
		//add
		employerVisInstanceRunIDSet.addID(visInstanceRunID);
		
		//update
		this.updateEmployerVisInstanceRunIDSetColumn(CFTargetValueTableRunID, employerVisInstanceRunIDSet);
		
		//add to the involved CFTargetValueTableRunID set of the currently running process in the log table;
		if(!this.getProcessLogTableManager().getSimpleProcessManager().getMostRecentRunningSimpleProcessID().equals(visInstanceRunID)) {
			throw new IllegalArgumentException("currently running process ID is not the same with the given VisInstanceRunID!");
		}
		this.getProcessLogTableManager().getSimpleProcessManager().addToInvolvedCFTargetValueTableRunIDSetColumnOfCurrentlyRunningProcess(CFTargetValueTableRunID);
	}
	
	/**
	 * remove the given VisInstanceRunID from the column {@link VisframeUDTManagementProcessRelatedTableColumnFactory#employerVisInstanceRunIDSetColumn} of the row with the given CFTargetValueTableRunID;
	 * 
	 * if after removal, the employer VisInstanceRunID set become empty, delete the CFTargetValueTableRun from the management table (?);
	 * 
	 * @param CFTargetValueTableRunID
	 * @param visInstanceRunID
	 * @throws SQLException 
	 */
	public void removeEmployerVisInstanceRunID(CFTargetValueTableRunID CFTargetValueTableRunID, VisInstanceRunID visInstanceRunID) throws SQLException {
		//get the employerVisInstanceRunIDSet
		VfIDCollection employerVisInstanceRunIDSet = (VfIDCollection) this.retrieveRow(CFTargetValueTableRunID).getEmployerVisInstanceRunIDSet();
		//remove
		employerVisInstanceRunIDSet.removeID(visInstanceRunID);
		
		if(employerVisInstanceRunIDSet.isEmpty()) {//delete
			this.delete(CFTargetValueTableRunID);
		}else {
			//update
			this.updateEmployerVisInstanceRunIDSetColumn(CFTargetValueTableRunID, employerVisInstanceRunIDSet);
		}
	}
	
	/**
	 * update the EmployerVisInstanceRunIDSetColumn value object for the given CFTargetValueTableRunID with the given new VfIDCollection;
	 * 
	 * also update the {@link #EMPLOYER_VISINSTANCE_RUN_NUM_COLUMN} column
	 * 
	 * note that even if the number of employer VisInstanceRun become 0, DO NOT delete the CFTargetValueTableRun record from the management table;
	 * 1. the CFTargetValueTableRun may be employed by new VisInstanceRun later;
	 * 2. when storage space need to be released, all CFTargetValueTableRuns with 0 employer VisInstanceRun and their value tables can be removed independently;
	 * this is a trade-off between computation efficiency and storage space management;
	 * 
	 * @param CFTargetValueTableRunID
	 * @param newEmployerVisInstanceRunIDSet
	 * @throws SQLException
	 */
	private void updateEmployerVisInstanceRunIDSetColumn(CFTargetValueTableRunID CFTargetValueTableRunID, VfIDCollection newEmployerVisInstanceRunIDSet) throws SQLException {
		List<String> columnNameListToBeUpdated = new ArrayList<>();
		columnNameListToBeUpdated.add(VisframeUDTManagementProcessRelatedTableColumnFactory.employerVisInstanceRunIDSetColumn.getName().getStringValue());
		columnNameListToBeUpdated.add(EMPLOYER_VISINSTANCE_RUN_NUM_COLUMN.getName().getStringValue());
		
		String psSqlString = TableContentSQLStringFactory.buildUpdateColumnsPreparedStatementSqlString(
				SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()), 
				columnNameListToBeUpdated, 
				TableContentSQLStringFactory.buildIDEquityConditionString(this.getPrimaryKeyAttributeNameMap(), CFTargetValueTableRunID)
				);
		
		PreparedStatement ps = this.getVisProjectDBConnection().prepareStatement(psSqlString);
		
		int EmployerVisInstanceRunNum = newEmployerVisInstanceRunIDSet.getIDSet(VisInstanceRunID.class).size();
		ps.setObject(1, newEmployerVisInstanceRunIDSet);//
		ps.setInt(2, EmployerVisInstanceRunNum);
		
		ps.execute();
		
		//////////////////////
		
	}
	
	
	///////////////////////////////////////////////////
	/**
	 * note that CompositionFunctionID related columns are not in the primary key column set;
	 */
	@Override
	public Map<SimpleName, ManagementTableColumn> getPrimaryKeyAttributeNameMap() {
		if(this.primaryKeyAttributeNameMap == null) {
			primaryKeyAttributeNameMap = new HashMap<>();
		
//			primaryKeyAttributeNameMap.putAll(this.getHasIDTypeManagerController().getCompositionFunctionManager().getPrimaryKeyAttributeNameMap());
			primaryKeyAttributeNameMap.put(CFTargetValueTableRunID.RUN_UID_COLUMN.getName(), CFTargetValueTableRunID.RUN_UID_COLUMN);
		}
		return primaryKeyAttributeNameMap;
	}
	
	/**
	 * add the host CompositionFunctionGroup name and the CompositionFunction index as non-primary key columns;
	 */
	@Override
	public List<ManagementTableColumn> getTypeSpecificManagementTableColumnList() {
		List<ManagementTableColumn> ret = new ArrayList<>();
		
		ret.add(COMPOSITION_FUNCTION_GROUP_NAME_COLUMN);
		ret.add(COMPOSITION_FUNCTION_INDEX_COLUMN);
		ret.add(EMPLOYER_VISINSTANCE_RUN_NUM_COLUMN);
		return ret;
	}
	
	/**
	 * 
	 */
	@Override
	protected void setTypeSpecificColumnValues(PreparedStatement ps, CFTargetValueTableRun entity) throws SQLException {
		ps.setString(
				this.getManagementTableSchema().getColumnIndex(COMPOSITION_FUNCTION_GROUP_NAME_COLUMN.getName()), 
				entity.getTargetCompositionFunctionID().getHostCompositionFunctionGroupID().getName().getStringValue()
				);
		ps.setInt(
				this.getManagementTableSchema().getColumnIndex(COMPOSITION_FUNCTION_INDEX_COLUMN.getName()), 
				entity.getTargetCompositionFunctionID().getIndexID()
				);
		ps.setInt(
				this.getManagementTableSchema().getColumnIndex(EMPLOYER_VISINSTANCE_RUN_NUM_COLUMN.getName()), 
				0//the initial value is 0; updated when a employer visinstance run is addded/removed from the employerVisInstanceRunIDSetColumn
				);
	}
	
	@Override
	protected void typeSpecificInsertionRelatedActivity(CFTargetValueTableRun entity) {
		//TODO?
	}

	/**
	 * 102120-update
	 * 
	 * also delete the value table schema of the deleted {@link CFTargetValueTableRun} in the VALUE schema;
	 * 
	 * note that the value table schema of the CFTargetValueTableRun is not included in the insertedNonProcessIDSetColumn since CFTargetValueTableRun is a NonProcessType type (thus, do not have this column)
	 * also, value table schema should not be included in the insertedNonProcessIDSetColumn of the employer VisInstanceRun;
	 * 		value table schema is only specific to the CFTargetValueTableRun rather than any of the employer VisInstanceRun;
	 * 
	 * thus, when removing the CFTargetValueTableRun, also need to remove the value table schema;
	 * 
	 * in addition, for all types of table schema related with CFTargetValueTableRun
	 * 1. CFTargetValueTableSchema
	 * 2. PiecewiseFunctionIndexIDOutputIndexValueTableSchema
	 * 3. TemporaryOutputVariableValueTableSchema
	 * DO not add them to the insertedNonProcessIDSetColumn of the currently running process!
	 * rather:
	 * 1. CFTargetValueTableSchemaID should be explicitly and directly controlled by the CFTargetValueTableRun
	 * 		1. when CFTargetValueTableRun is inserted and calculated, the CFTargetValueTableSchemaID is created and inserted;
	 * 		2. when the CFTargetValueTableRun is removed, the CFTargetValueTableSchema is removed;
	 * 				this should be implemented in the {@link VisProjectCFTargetValueTableRunManager#delete()} method, 
	 * 				thus will be triggered in the rollback after crash;
			
	 * 2. for PiecewiseFunctionIndexIDOutputIndexValueTableSchemaID and TemporaryOutputVariableValueTableSchemaID,
	 * 		they should be removed once the CFTargetValueTableRun calculation is done;
	 * 		for rollback after crash, simply remove all tables in the CALCULATION schema
	 * 
	 */
	@Override
	public void delete(ID<? extends HasID> id) throws SQLException {
		
		if(!this.isValidID(id)) {
			throw new IllegalArgumentException("given id is not of valid type");
		}
		
		CFTargetValueTableRun CFTargetValueTableRun = this.lookup((CFTargetValueTableRunID)id);
		
		
		//delete the value table
		TableSchemaUtils.dropTable(this.getVisProjectDBConnection(), CFTargetValueTableRun.getTableSchemaID().getSchemaName(), CFTargetValueTableRun.getTableSchemaID().getTableName());
		
		
		//remove the CFTargetValueTableRun record from the management table
		String sqlString = TableContentSQLStringFactory.buildDeleteWithConditionSqlString(
				SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()),
				TableContentSQLStringFactory.buildIDEquityConditionString(this.getPrimaryKeyAttributeNameMap(), (CFTargetValueTableRunID)id) //need to downcast id to I;
				);
		
		Statement statement = this.getVisProjectDBConnection().createStatement();
		statement.execute(sqlString);
	}
}
