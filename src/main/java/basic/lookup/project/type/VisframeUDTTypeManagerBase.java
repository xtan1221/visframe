package basic.lookup.project.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import basic.SimpleName;
import basic.lookup.HasID;
import basic.lookup.ID;
import basic.lookup.Lookup;
import basic.lookup.PrimaryKeyID;
import basic.lookup.VisframeUDT;
import basic.lookup.project.VisProjectHasIDTypeManagerBase;
import context.project.VisProjectDBContext;
import context.project.process.logtable.VfIDCollection;
import context.project.rdb.VisProjectRDBConstants;
import context.project.rdb.initialize.VisProjectDBUDTConstants;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstance;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstanceID;
import exception.VisframeException;
import rdb.table.lookup.ManagementTableColumn;
import rdb.table.lookup.ManagementTableSchema;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;
import sql.derby.TableSchemaUtils;
import visinstance.run.VisInstanceRun;
import visinstance.run.calculation.function.composition.CFTargetValueTableRun;

/**
 * base class for manager for VisframeUDT type;
 * 
 * note that 
 * 1. VisframeUDT has PrimaryKeyID type ID; 
 * 2. VisframeUDT type has an explicit management table in the MANAGEMENT schema of the host VisProjectDBContext's rdb;
 * 
 * @author tanxu
 * 
 * @param <T>
 * @param <I>
 */
public abstract class VisframeUDTTypeManagerBase<T extends VisframeUDT,I extends PrimaryKeyID<T>> extends VisProjectHasIDTypeManagerBase<T, I>  implements Lookup<T,I>{
	/**
	 * PreparedStatement to insert an entity into the management table of this manager;
	 * this PreparedStatement is shared by all entities since there is no entity specific content in the sql string
	 */
	private PreparedStatement insertEntityPreparedStatement;
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param managedType
	 */
	public VisframeUDTTypeManagerBase(VisProjectDBContext visProjectDBContext, Class<T> managedType, Class<I> IDType) {
		super(visProjectDBContext, managedType, IDType);
	}
	
	///////////////////////////////////////
	//management table schema related
	/**
	 * return the schema name for management table;
	 * @return
	 */
	protected SimpleName getManagementTableSchemeName() {
		return VisProjectRDBConstants.MANAGEMENT_SCHEMA_NAME;
	}
	
	/**
	 * return the table name for the management table;
	 * 
	 * implemented in each final subtype class
	 * @return
	 */
	protected SimpleName getManagementTableName() {
		return VisframeUDTManagementTableUtils.makeVisframeUDTManagementTableName(this.getManagedType());
	}
	
	/**
	 * return the list of primary key columns of the management table of this manager;
	 * 
	 * implemented by each final subtype of {@link VisframeUDTTypeManagerBase};
	 * 
	 * @return
	 */
	public abstract Map<SimpleName, ManagementTableColumn> getPrimaryKeyAttributeNameMap();
	
	
	/**
	 * return the column for the VisframeUDT
	 * @return
	 */
	protected ManagementTableColumn getVisframeUDTColumn() {
		return new ManagementTableColumn(
				//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
				VisframeUDTManagementTableUtils.VISFRAME_UDT_COLUMN_NAME, VisProjectDBUDTConstants.getSQLDataType(this.getManagedType()), false, false,
				//Boolean notNull, String defaultStringValue, String additionalConstraints
				true, null, null
				);
	}
	
	/**
	 * return the list of columns including primary key attributes and the UDT column;
	 * @return
	 */
	protected List<ManagementTableColumn> getBasicColumnList(){
		List<ManagementTableColumn> ret = new ArrayList<>();
		
		//////////
		//primary key attribute columns
		ret.addAll(this.getPrimaryKeyAttributeNameMap().values());
		//object UDT column
		ret.add(this.getVisframeUDTColumn());
		return ret;
	}
	
	/**
	 * make and return the list of columns related with process and defined in {@link VisframeUDTManagementProcessRelatedTableColumnFactory}
	 * 
	 * @return
	 */
	protected List<ManagementTableColumn> getProcessRelatedColumnList(){
		List<ManagementTableColumn> ret = new ArrayList<>();
		
		//first add all VisframeUDT shared columns
		ret.add(VisframeUDTManagementProcessRelatedTableColumnFactory.insertionTimeColumn);
		
		//then add all VisframeUDT types shared columns except for CFTargetValueTableRun
		if(this.getManagedType() == CFTargetValueTableRun.class) {
			//skip
			//there is no insertion process for a CFTargetValueTableRun, since it is designed to be shared by multiple VisInstanceRuns;
		}else {
			ret.add(VisframeUDTManagementProcessRelatedTableColumnFactory.isTempColumn);
			ret.add(VisframeUDTManagementProcessRelatedTableColumnFactory.insertionProcessIDColumn);
		}
		
		
		//then add process type VisframeUDT specific columns
		//including both NonReproduceableProcessType and ReproduceableProcessType
		if(this.isOfProcessType()) {
			//note that dependentProcessIDSetColumn of VisSchemeAppliedArchiveReproducedAndInsertedInstance only includes the VisSchemeBasedVisInstance based on it; but not include the ReproduceableProcessType; (changed see VisInstanceInserter)
			//inserted ReproduceableProcessType of a VisSchemeAppliedArchiveReproducedAndInsertedInstance are put in the insertedProcessIDSetColumn specific to VisSchemeAppliedArchiveReproducedAndInsertedInstance management table
			ret.add(VisframeUDTManagementProcessRelatedTableColumnFactory.dependentProcessIDSetColumn);
			//note that for ReproduceableProcessType, their baseProcessIDSetColumn does not include the VisSchemeAppliedArchiveReproducedAndInsertedInstance even if they are inserted by it;
			ret.add(VisframeUDTManagementProcessRelatedTableColumnFactory.baseProcessIDSetColumn);
		}
		
		//process type except for VisSchemeAppliedArchiveReproducedAndInsertedInstance and VisInstanceRun
		//since all inserted types of VisSchemeAppliedArchiveReproducedAndInsertedInstance must be of ReproduceableProcessType
		if(this.isOfProcessType()&&
				(this.getManagedType()!=VisSchemeAppliedArchiveReproducedAndInsertedInstance.class && this.getManagedType()!=VisInstanceRun.class))
			ret.add(VisframeUDTManagementProcessRelatedTableColumnFactory.insertedNonProcessIDSetColumn);
		
		//VisSchemeAppliedArchiveReproducedAndInsertedInstance specific
		//insertedProcessIDSetColumn
		if(this.getManagedType()==VisSchemeAppliedArchiveReproducedAndInsertedInstance.class)
			ret.add(VisframeUDTManagementProcessRelatedTableColumnFactory.insertedProcessIDSetColumn);
		
		
		//ReproduceableProcessType specific
		if(this.isOfReproduceableProcessType()) {
			ret.add(VisframeUDTManagementProcessRelatedTableColumnFactory.isReproducedColumn);
			ret.add(VisframeUDTManagementProcessRelatedTableColumnFactory.VSAAReproducedAndInsertedInstanceUIDColumn);//
		}
		
		
		//EMPLOYER_VISINSTANCERUNID_SET column for CfTargetValueTableRun only
		if(this.getManagedType() == CFTargetValueTableRun.class) {
			ret.add(VisframeUDTManagementProcessRelatedTableColumnFactory.employerVisInstanceRunIDSetColumn);
		}
		
		
		//INSERTED_CFTARGETVALUETABLERUN_ID_SET column for VisInstanceRun only
		if(this.getManagedType() == VisInstanceRun.class) {
			ret.add(VisframeUDTManagementProcessRelatedTableColumnFactory.involvedCfTargetValueTableRunIDSetColumn);
		}
		
		return ret;
	}
	
	
	
	
	/**
	 * build and return the ManagementTableSchema of the management table of the VisframeUDT type of this VisProjectUDTManagerBase
	 * 
	 * implemented in each final subtype class;
	 * @return
	 */
	public ManagementTableSchema getManagementTableSchema() {
		List<ManagementTableColumn> columnList = new ArrayList<>();
		
		
		////////basic columns
		columnList.addAll(this.getBasicColumnList());
		
		//process related columns
		columnList.addAll(this.getProcessRelatedColumnList());
		
		
		/////type specific
		columnList.addAll(this.getTypeSpecificManagementTableColumnList());
		
		
		//////////////////////
		return new ManagementTableSchema(this.getManagementTableName(),columnList);
	}
	
	
	/**
	 * return the list of ManagementTableColumns that is specific to each VisframeUDT types and not related with the process performer
	 * @return
	 */
	public abstract List<ManagementTableColumn> getTypeSpecificManagementTableColumnList();
	
	/**
	 * create the table schema of the management table in the 
	 * @throws SQLException 
	 */
	public void createManagementTableSchemaInVisProjectRDB() throws SQLException {
		TableSchemaUtils.createTableSchema(this.getVisProjectDBConnection(), this.getManagementTableSchema());
	}
	
	/**
	 * check if the management table exist in the DB of the host VisProjectDBContext;
	 * @return
	 * @throws SQLException 
	 */
	public boolean doesTableExist() throws SQLException {
		System.out.println("check management table existence:"+this.getManagementTableName().getStringValue());
		return TableSchemaUtils.doesTableExists(
				this.getVisProjectDBConnection(), this.getManagementTableSchemeName(), this.getManagementTableName());
	}
	
	
	////////////////////////////
	/**
	 * retrieve VisframeUDTManagementTableRow containing all core column information from the management table for the entity of the given id;
	 * 
	 * 1. generate the sql query to get the resultset;
	 * 		SELECT * FROM ... WHERE ...(ID equality condition)
	 * 2. if empty, return null;
	 * 3. else,
	 * 		1. retrieve the first record in the ResultSet;
	 * 		2. build the VisframeUDTManagementTableRow object;
	 * 		
	 * if multiple records in the ResultSet, throw VisframeException;
	 * @param id
	 * @return
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public VisframeUDTManagementTableRow<T,I> retrieveRow(ID<? extends HasID> id) throws SQLException{
		if(!this.isValidID(id)) {
			throw new IllegalArgumentException("give id is not of valid type");
		}
		
		String sqlString = TableContentSQLStringFactory.buildSelectAllWithConditionString(
				SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()),
				TableContentSQLStringFactory.buildIDEquityConditionString(this.getPrimaryKeyAttributeNameMap(), (I)id));//down cast to I
				
		
		Statement statement = this.getVisProjectDBConnection().createStatement(); 
		ResultSet rs = statement.executeQuery(sqlString);
		
		//
		if(!rs.next()) {//empty
			return null;
		}
		
		VisframeUDTManagementTableRow<T,I> ret = retrieveRow(rs);
		
		//
		if(rs.next()) {//multiple records found with the same ID
			throw new VisframeException("multiple records found with the same ID");
		}
		
		return ret;
	}
	
	/**
	 * extract the currently pointed row from the given ResultSet to create and return a VisframeUDTManagementTableRow;
	 * 
	 * Note that this method does not check if the cursor is pointing to a valid row or null; thus the invoker method should check it before invoking this method;
	 * 
	 * the returned VisframeUDTManagementTableRow must contain the consistent set of columns with the table schema of the VisframeUDT managed by this maanger class;
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private VisframeUDTManagementTableRow<T,I> retrieveRow(ResultSet rs) throws SQLException{
		
		T entity = (T)rs.getObject(VisframeUDTManagementTableUtils.VISFRAME_UDT_COLUMN_NAME.getStringValue());//entity
		
		//////////
		//
		Timestamp insertionTimestamp = (Timestamp) rs.getObject(VisframeUDTManagementProcessRelatedTableColumnFactory.insertionTimeColumn.getName().getStringValue());
		
		//all VisframeUDT except for CFTargetValueTableRun
		PrimaryKeyID<? extends VisframeUDT> insertionProcessID = null;
		Boolean temporary = null;
		if(this.getManagedType() == CFTargetValueTableRun.class) {
			//
		}else {
			insertionProcessID = (PrimaryKeyID<? extends VisframeUDT>)rs.getObject(VisframeUDTManagementProcessRelatedTableColumnFactory.insertionProcessIDColumn.getName().getStringValue());
			temporary = rs.getBoolean(VisframeUDTManagementProcessRelatedTableColumnFactory.isTempColumn.getName().getStringValue());
		}
		
		//all process type VisframeUDT
		VfIDCollection baseProcessIDSet = null;
		VfIDCollection dependentProcessIDSet = null;
		VfIDCollection insertedNonProcessIDSet = null;
		VfIDCollection insertedProcessIDSet = null;
		Boolean reproduced = null;
		String VSAAReproducedAndInsertedInstanceUID = null;
		
		if(this.isOfProcessType()) {
			baseProcessIDSet = (VfIDCollection)rs.getObject(VisframeUDTManagementProcessRelatedTableColumnFactory.baseProcessIDSetColumn.getName().getStringValue());
			dependentProcessIDSet = (VfIDCollection)rs.getObject(VisframeUDTManagementProcessRelatedTableColumnFactory.dependentProcessIDSetColumn.getName().getStringValue());
			
			if(this.getManagedType() != VisSchemeAppliedArchiveReproducedAndInsertedInstance.class && this.getManagedType() != VisInstanceRun.class) {
				insertedNonProcessIDSet = (VfIDCollection)rs.getObject(VisframeUDTManagementProcessRelatedTableColumnFactory.insertedNonProcessIDSetColumn.getName().getStringValue());
			}
			
			if(this.getManagedType() == VisSchemeAppliedArchiveReproducedAndInsertedInstance.class) {
				insertedProcessIDSet = (VfIDCollection)rs.getObject(VisframeUDTManagementProcessRelatedTableColumnFactory.insertedProcessIDSetColumn.getName().getStringValue());
			}
			if(this.isOfReproduceableProcessType()) {
				reproduced = rs.getBoolean(VisframeUDTManagementProcessRelatedTableColumnFactory.isReproducedColumn.getName().getStringValue());
				if(reproduced)//reproduced is true
					VSAAReproducedAndInsertedInstanceUID = rs.getString(VisframeUDTManagementProcessRelatedTableColumnFactory.VSAAReproducedAndInsertedInstanceUIDColumn.getName().getStringValue());
			}
		}
		
		//CFTargetValueTableRun specific
		VfIDCollection employerVisInstanceRunIDSet = null;
		if(this.getManagedType() == CFTargetValueTableRun.class) {
			employerVisInstanceRunIDSet = (VfIDCollection)rs.getObject(VisframeUDTManagementProcessRelatedTableColumnFactory.employerVisInstanceRunIDSetColumn.getName().getStringValue());
		}
		
		//VisInstanceRun specific
		VfIDCollection involvedCfTargetValueTableRunIDSet = null;
		if(this.getManagedType() == VisInstanceRun.class) {
			involvedCfTargetValueTableRunIDSet = (VfIDCollection)rs.getObject(VisframeUDTManagementProcessRelatedTableColumnFactory.involvedCfTargetValueTableRunIDSetColumn.getName().getStringValue());
		}
		
		
		//////////////
		//type specific
		Map<SimpleName, Object> typeSpecificAttributeNameObjectValueMap = new LinkedHashMap<>();
		for(ManagementTableColumn col:this.getTypeSpecificManagementTableColumnList()) {
			
			typeSpecificAttributeNameObjectValueMap.put(col.getName(),rs.getObject(col.getName().getStringValue().toUpperCase())); //uppercase
			
		}
		
		//////////////////
		VisframeUDTManagementTableRow<T,I> ret = new VisframeUDTManagementTableRow<>(
				this,
				
				entity,
				
				insertionTimestamp,
				
				insertionProcessID, temporary,
				
				baseProcessIDSet, dependentProcessIDSet,
				insertedNonProcessIDSet, insertedProcessIDSet, 
				
				reproduced, VSAAReproducedAndInsertedInstanceUID,
				
				employerVisInstanceRunIDSet,
				
				involvedCfTargetValueTableRunIDSet,
				
				typeSpecificAttributeNameObjectValueMap
				);
		return ret;
	}
	
	////////////////////////////////////
	
	/**
	 * retrieve the full set of entities in the management table sorted by ASCENDING insertion time ;
	 * @return
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public Map<I,T> retrieveAll() throws SQLException{
//		String sqlString = TableContentSQLStringFactory.buildSelectAllWithoutConditionString(
//				SqlStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()));
		
		List<String> orderByColumnNameList = new ArrayList<>();
		orderByColumnNameList.add(VisframeUDTManagementProcessRelatedTableColumnFactory.insertionTimeColumn.getName().getStringValue());
		List<Boolean> orderByASCList = new ArrayList<>();
		orderByASCList.add(true);//ASC sort order
		
		String sqlString = TableContentSQLStringFactory.buildSelectAllSQLString(
				SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()),
				null, 
				orderByColumnNameList, 
				orderByASCList
				);
		
		Statement statement = this.getVisProjectDBConnection().createStatement(); 
		ResultSet rs = statement.executeQuery(sqlString);
		
		Map<I,T> ret = new LinkedHashMap<>();
		
		while(rs.next()) {
			T entity = (T)rs.getObject(VisframeUDTManagementTableUtils.VISFRAME_UDT_COLUMN_NAME.getStringValue());//entity
			ret.put((I) entity.getID(), entity);
		}
		
		return ret;
	}
	
	
//	/**
//	 * retrieve all records from this management table in the form of a list of VisframeUDTManagementTableRow;
//	 * @param filterConditionString condition string to filter the retrieved records;
//	 * @return
//	 * @throws SQLException
//	 */
//	public List<VisframeUDTManagementTableRow<T,I>> retrieveAllAsList(String filterConditionString) throws SQLException{
//		List<String> orderByColumnNameList = new ArrayList<>();
//		orderByColumnNameList.add(VisframeUDTManagementProcessRelatedTableColumnFactory.insertionTimeColumn.getName().getStringValue());
//		List<Boolean> orderByASCList = new ArrayList<>();
//		orderByASCList.add(true);//ASC sort order
//		
//		String sqlString = TableContentSQLStringFactory.buildSelectAllSqlString(
//				SqlStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()),
//				filterConditionString, 
//				orderByColumnNameList, 
//				orderByASCList
//				);
//		Statement statement = this.getVisProjectDBConnection().createStatement(); 
//		ResultSet rs = statement.executeQuery(sqlString);
//		
//		List<VisframeUDTManagementTableRow<T,I>> ret = new ArrayList<>();
//		
//		while(rs.next()) {
//			ret.add(this.retrieveRow(rs));
//		}
//		
//		return ret;
//	}
	
	/**
	 * retrieve the full list of VisframeUDTManagementTableRows of entity from the management table with the given sql filter condition and the entity Predicate condition;
	 * 
	 * @param filterConditionString sql condition to query the entities; can be null if all entities should be queried
	 * @param condition filtering Predicate condition for the queried entities; can be null if all queried entities should be returned;
	 * @return
	 * @throws SQLException
	 */
	public List<VisframeUDTManagementTableRow<T,I>> retrieveAllAsList(String filterConditionString, Predicate<T> condition) throws SQLException{
		List<String> orderByColumnNameList = new ArrayList<>();
		orderByColumnNameList.add(VisframeUDTManagementProcessRelatedTableColumnFactory.insertionTimeColumn.getName().getStringValue());
		List<Boolean> orderByASCList = new ArrayList<>();
		orderByASCList.add(true);//ASC sort order
		
		String sqlString = TableContentSQLStringFactory.buildSelectAllSQLString(
				SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()),
				filterConditionString, 
				orderByColumnNameList, 
				orderByASCList
				);
		Statement statement = this.getVisProjectDBConnection().createStatement(); 
		ResultSet rs = statement.executeQuery(sqlString);
		
		List<VisframeUDTManagementTableRow<T,I>> ret = new ArrayList<>();
		
		while(rs.next()) {
			VisframeUDTManagementTableRow<T,I> row = this.retrieveRow(rs);
			if(condition!=null && !condition.test(row.getEntity())) {
				//do not add if condition is not null and the row's entity does not pass the Predicate test;
			}else {
				ret.add(this.retrieveRow(rs));
			}
				
		}
		
		return ret;
	}
	
	
	
	/**
	 * retrieve the entity with the given ID from this Lookup;
	 * 
	 * to facilitate method of Lookup interface;
	 * 
	 * @param id
	 * @return
	 * @throws SQLException 
	 */
	@Override
	public T lookup(I id) throws SQLException {
		VisframeUDTManagementTableRow<T,I> row = this.retrieveRow(id);
		if(row==null) {
			return null;
		}else {
			return row.getEntity();
		}
	}
	
	//////////////////////////////////////////
	/**
	* lookup if the VisframeUDT with the given PrimaryKeyID exist in the visProjectDBConnection;
	* 
	* 1. build the query sql 
	* 2. run the sql
	* 3. check the ResultSet;
	 * @throws SQLException 
	* 
	*/
	@Override
	public boolean checkIDExistence(I id) throws SQLException {
		return this.retrieveRow(id) != null;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * insert a new entity;
	 * 1. initialize a PreparedStatement that inserts the entity into the management table; 
	 * 		1. set the basic column values
	 * 			{@link #setBasicColumnValues(PreparedStatement, VisframeUDT)}
	 * 		2. set the process related column values;
	 * 		3. set the type specific column values
	 * 
	 * 2. execute the PreparedStatement to insert the entity into the management table of this manager class;
	 * 
	 * 3. update the involved columns of the insertion process of the entity in process log table
	 * 		1. if this type is NonProcessType and NOT of CFTargetValueTableRun type
	 * 			add the entity ID to the insertedNonProcessIDSet column of the currently running simple process
	 * 		2. if this type is ProcessType
	 * 				if of ReproduceableProcessType and reproduced by a VisSchemeAppliedArchiveReproducerAndInserter
	 * 					add the entity's ID to the insertedProcessIDSet column of the currently running VisSchemeAppliedArchiveReproducerAndInserter process;
	 * 				else
	 * 					do nothing (the process entity is inserted by itself)
	 * 
	 * invoke {@link #typeSpecificInsertionRelatedActivity(VisframeUDT)};
	 * @throws SQLException 
	 */
	@Override
	public void insert(T t) throws SQLException{
		PreparedStatement ps = this.getEmptyPreparedStatementForInsertion();
		
		//
		this.setBasicColumnValues(ps, t);
		
		this.setProcessRelatedColumnValues(ps, t);
		
		this.setTypeSpecificColumnValues(ps, t);
		
		ps.execute();
		
		//
		if(this.isOfNonProcessType() && !this.getManagedType().equals(CFTargetValueTableRun.class)) {
			//if the entity is of NonProcessType and not of CFTargetValueTableRun type
			//add to the InsertedNonProcessIDSetColumn for the currently running simple process in the PROCESS LOG table;
			this.getProcessLogTableManager().getSimpleProcessManager().addToInsertedNonProcessIDSetColumnOfCurrentlyRunningSimpleProcess(t.getID());
		}else if(this.isOfReproduceableProcessType()){//ReproduceableProcessType
			if(this.getHostVisProjectDBContext().getProcessLogTableAndProcessPerformerManager().getVSAArchiveReproducerAndInserterManager().currentlyVSAArchiveReproducerAndInserterIsRunning()) {
				//the ReproduceableProcessType entity is reproduced and inserted by a running VisSchemeAppliedArchiveReproducerAndInserter
				//add to the insertedProcessIDSet column of the currently running VisSchemeAppliedArchiveReproducerAndInserter process;
				this.getProcessLogTableManager().getVSAArchiveReproducerAndInserterManager().addToInsertedProcessIDSetColumnOfCurrentlyRunningVSAArchiveReproducerAndInserterProcess(t.getID());
			}
		}
		
		
		//
		this.typeSpecificInsertionRelatedActivity(t);
	}
	
	/**
	 * perform any type specific operations after inserting an entity into the management table;
	 * must be invoked from {@link #insert(VisframeUDT)};
	 * @param t
	 * @throws SQLException 
	 */
	protected abstract void typeSpecificInsertionRelatedActivity(T t) throws SQLException;
	
	
	/**
	 * make and return a new PreparedStatement for this manager to insert a new entity into the management table
	 * @return
	 * @throws SQLException
	 */
	protected PreparedStatement getEmptyPreparedStatementForInsertion() throws SQLException {
		if(this.insertEntityPreparedStatement==null) {
			this.insertEntityPreparedStatement = 
					this.getVisProjectDBConnection().prepareStatement(
							TableContentSQLStringFactory.buildInsertIntoPreparedStatementSqlString(
									SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()),
									this.getManagementTableSchema().getOrderedListOfColumn().size())
							);
		}
		
		this.insertEntityPreparedStatement.clearBatch();
		return this.insertEntityPreparedStatement;
	}
	
	/**
	 * set the value of primary key columns and the VisframeUDT object column in the given PreparedStatement of the given entity to be inserted
	 * @param ps
	 * @param entity
	 * @throws SQLException 
	 */
	protected void setBasicColumnValues(PreparedStatement ps, T entity) throws SQLException {
		//first set the column values in primary key set
		for(SimpleName pkColName:this.getPrimaryKeyAttributeNameMap().keySet()) {
			this.getPrimaryKeyAttributeNameMap().get(pkColName).getSqlDataType().setPreparedStatement(
					ps, 
					this.getManagementTableSchema().getColumnIndex(pkColName), 
					entity.getID().getPrimaryKeyAttributeNameStringValueMap().get(pkColName)
					);
		}
		
		//then set the value for the VisframeUDT object column
		ps.setObject(this.getManagementTableSchema().getColumnIndex(this.getVisframeUDTColumn().getName()), entity);
	}
	
	/**
	 * set the columns related with process performing only (all those defined in {@link VisframeUDTManagementProcessRelatedTableColumnFactory});
	 * must be consistent with the columns in {@link #getProcessRelatedColumnList()}
	 * 
	 * 
	 * @param ps
	 * @param processLogTableRow
	 * @throws SQLException
	 */
	private void setProcessRelatedColumnValues(PreparedStatement ps, T entity) throws SQLException {
		//first set the insertion timestamp shared by all VisframeUDT types
		ps.setTimestamp(this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.insertionTimeColumn.getName()), 
				new Timestamp(new Date().getTime()));
		
		//set columns for all VisframeUDT types except for CfTargetValueTableRun
		if(!this.getManagedType().equals(CFTargetValueTableRun.class)) {
			//isTempColumn always true
			ps.setBoolean(
					this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.isTempColumn.getName()), 
					true);
			
			//insertionProcessIDColumn,
			if(this.getManagedType().equals(VisSchemeAppliedArchiveReproducedAndInsertedInstance.class)) {
				ps.setObject(
						this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.insertionProcessIDColumn.getName()), 
						this.getProcessLogTableManager().getVSAArchiveReproducerAndInserterManager().getCurrentlyRunningVSAReproducerAndInserterProcessID());//set as the ID of the VisSchemeAppliedArchiveReproducedAndInsertedInstance
			}else {
				ps.setObject(
						this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.insertionProcessIDColumn.getName()), 
						this.getProcessLogTableManager().getSimpleProcessManager().getMostRecentRunningSimpleProcessID());//set as currently running process ID
			}
			
			
			//isReproducedColumn, VSAAReproducedAndInsertedInstanceUIDColumn
			if(this.isOfReproduceableProcessType()) {
				if(this.getProcessLogTableManager().getVSAArchiveReproducerAndInserterManager().currentlyVSAArchiveReproducerAndInserterIsRunning()) { 
					//there is a running VisSchemeAppliedArchiveReproducerAndInserter;
					//thus the ReproduceableProcessType entity is reproduced by it;
					ps.setBoolean(
							this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.isReproducedColumn.getName()), 
							true);
					ps.setObject(
							this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.VSAAReproducedAndInsertedInstanceUIDColumn.getName()), 
							Integer.toString(
									this.getProcessLogTableManager().getVSAArchiveReproducerAndInserterManager().getCurrentlyRunningVSAReproducerAndInserterProcessID().getUID())); //set to the process ID of the running VisSchemeAppliedArchiveReproducerAndInserter
					
				}else {
					//there is no running VisSchemeAppliedArchiveReproducerAndInserter
					//thus the ReproduceableProcessType entity is not reproduced;
					ps.setBoolean(
							this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.isReproducedColumn.getName()), 
							false);
					ps.setObject(
							this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.VSAAReproducedAndInsertedInstanceUIDColumn.getName()), 
							null); //set to null
				}
			}
		}
		
		
		//set ProcessType VisframeUDT management table specific columns
		//		note that for NonProcessTypes, these columns are not in their management table schema;
		//empty dependentProcessIDSetColumn for both NonReproduceableProcessType and ReproduceableProcessType;
		//baseProcessIDSetColumn for both NonReproduceableProcessType and ReproduceableProcessType
		//insertedNonProcessIDSetColumn for all process types but VisSchemeAppliedArchiveReproducedAndInsertedInstance
		//insertedProcessIDSetColumn for VisSchemeAppliedArchiveReproducedAndInsertedInstance only
		if(this.isOfProcessType()) {
			//dependentProcessIDSetColumn set for all ProcessTypes;
			ps.setObject(
					this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.dependentProcessIDSetColumn.getName()), 
					new VfIDCollection()); //empty VfIDCollection
			
			//baseProcessIDSetColumn
			ps.setObject(
					this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.baseProcessIDSetColumn.getName()), 
					new VfIDCollection()); //baseProcessIDSetColumn will be set in the postprocess step, thus here only set to empty set;
			
			//insertedNonProcessIDSetColumn, insertedProcessIDSetColumn
			if(this.getManagedType().equals(VisSchemeAppliedArchiveReproducedAndInsertedInstance.class)) {
				ps.setObject(
						this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.insertedProcessIDSetColumn.getName()), 
						new VfIDCollection());//value will be set in postprocess of the VisSchemeAppliedArchiveReproducerAndInserter
				
			}else if(this.getManagedType().equals(VisInstanceRun.class)){
				//no insertedProcessIDSetColumn nor insertedNonProcessIDSetColumn
			}else {
				//add insertedNonProcessIDSetColumn equal to the currently running simple process's insertedNonProcessIDSetColumn
				ps.setObject(
						this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.insertedNonProcessIDSetColumn.getName()), 
						new VfIDCollection());//value will be set in postprocess of the SimpleProcessPerformer
			}
		}
		
		//set VisInstanceRun management table specific column
		//involvedCfTargetValueTableRunIDSetColumn
		if(this.getManagedType() == VisInstanceRun.class) {
			ps.setObject(
					this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.involvedCfTargetValueTableRunIDSetColumn.getName()), 
					new VfIDCollection()); //the value will be set in the post process of the SimpleProcessPerformer
		}
		
		
		//set CfTargetValueTableRun management table specific column
		//insertion of a new CfTargetValueTableRun will also set the value of employerVisInstanceRunIDSetColumn to an empty VfIDCollection???????????
		if(this.getManagedType() == CFTargetValueTableRun.class) {
			ps.setObject(
					this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.employerVisInstanceRunIDSetColumn.getName()), 
					new VfIDCollection());
		}
		
	}
	
	/**
	 * set the VisframeUDT type specific column values in the given PreparedStatement for the given entity to be inserted;
	 * implemented by each final subtype manager class;
	 * @param ps
	 * @param entity
	 * @throws SQLException 
	 */
	protected abstract void setTypeSpecificColumnValues(PreparedStatement ps, T entity) throws SQLException;
	
	
	//////////////////////////////////////////////////////
	/**
	 * 1. first check if the operation is allowed;
	 * 2. if yes 
	 * 3. if no, throw UnsupportedOperationException
	 * @throws SQLException 
	 */
	public boolean getIsReproducedColumnValue(I id) throws SQLException {
		if(this.hasManagementTable() && this.isOfReproduceableProcessType()) {
//			System.out.println(this.retrieveRow(id));
			return this.retrieveRow(id).getIsReproduced();
		}else {
			throw new UnsupportedOperationException("IS_REPRODUCED COLUMN is only existing in management table for VisframeUDT that is of ReproduceableProcessType!");
		}
	}
	
	
	/**
	 * add the given dependentProcessID to the {@link VisframeUDTManagementProcessRelatedTableColumnFactory#dependentProcessIDSetColumn} of the given entityID;
	 * 1. first check if the operation is allowed;
	 * 		1. entity ID is of valid type
	 * 		2. the row is found for the given id;
	 * 		3. there is dependentProcessIDSet column in the management table;
	 * 2. if yes 
	 * 		1. retrieve the VfIDCollection for dependent process ID set of the given ID
	 * 		2. add the ID
	 * 		3. update the column value with the updated VfIDCollection object
	 * 			UPDATE table_name SET column_name = value, column_name = value, ... WHERE conditions;
	 */
	@SuppressWarnings("unchecked")
	public void addDependentProcessID(ID<?> dependedProcessEntityID, PrimaryKeyID<?> dependentProcessID) throws SQLException{
		if(!this.isValidID(dependedProcessEntityID)) {
			throw new VisframeException("given entity id is not of valid type:"+dependedProcessEntityID);
		}
		
		//first retrieve the existing dependentProcessIDSet from the management table of the given entity ID
		VisframeUDTManagementTableRow<T,I> row = this.retrieveRow((I)dependedProcessEntityID);
		
		if(row == null) {
			throw new VisframeException("ID not found in management table");
		}
		
		VfIDCollection dependentProcessIDSet = row.getDependentProcessIDSet();
		
		if(dependentProcessIDSet==null)
			throw new VisframeException("dependentProcessIDSet is not applicable for this VisframeUDT type:"+this.getManagedType());
		
		//add the new dependent process ID
		dependentProcessIDSet.addID(dependentProcessID);
		
		
		////update the dependentProcessIDSet column with the updated dependentProcessIDSet;
		List<String> columnNameList = new ArrayList<>();
		columnNameList.add(VisframeUDTManagementProcessRelatedTableColumnFactory.dependentProcessIDSetColumn.getName().getStringValue());
		
		//create the PreparedStatement
		PreparedStatement ps = 
				this.getVisProjectDBConnection().prepareStatement(
						TableContentSQLStringFactory.buildUpdateColumnsPreparedStatementSqlString(
								SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()),
								columnNameList, 
								TableContentSQLStringFactory.buildIDEquityConditionString(this.getPrimaryKeyAttributeNameMap(), (I)dependedProcessEntityID))
						);
		
		//set the updated object value
		ps.setObject(1, dependentProcessIDSet);
		
		ps.execute();
	}
	
	

	
	/**
	 * basic implementation of deleting the entity row of the given id from the management table;
	 * 
	 * for VisframeUDT types that has specific additional operations, need to override this method;
	 * 
	 * for example, for Metadata deletion, if the deleted Metadata is of record type, need to first delete the data table schema first;
	 * 
	 * DELETE FROM table_name WHERE condition;
	 * 
	 * 
	 * 
	 * for NonProcessType entity
	 * 		simple delete the entity from the management table;
	 * for ProcessType entity
	 * 		for ReproduceableProcessType entity
	 * 			
	 * 		for NonReproduceableProcessType entity
	 * 	
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void delete(ID<? extends HasID> id) throws SQLException {
		if(!this.isValidID(id)) {
			throw new IllegalArgumentException("given id is not of valid type");
		}
		
		String sqlString = TableContentSQLStringFactory.buildDeleteWithConditionSqlString(
				SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()),
				TableContentSQLStringFactory.buildIDEquityConditionString(this.getPrimaryKeyAttributeNameMap(), (I)id) //need to downcast id to I;
				);
		
		Statement statement = this.getVisProjectDBConnection().createStatement();
		statement.execute(sqlString);
	}
	
	
	////////////
	////////////////////////////////////////////////
	/**
	 * set the column value of {@link VisframeUDTManagementProcessRelatedTableColumnFactory#insertedNonProcessIDSetColumn} of the entity with the given ID with the given VfIDCollection; only valid for {@link ProcessType} entity;
	 * 
	 * applicable for all types ProcessType except for VisSchemeAppliedArchiveReproducedAndInsertedInstance and VisInstanceRun; TODO
	 * set after the process is successfully finished;
	 * 
	 * 
	 * @param id
	 * @param idSet
	 * @throws SQLException 
	 */
	public void setInsertedNonProcessIDSetColumn(I id, VfIDCollection idSet) throws SQLException {
		if(!this.isOfProcessType()) {
			throw new VisframeException("not process type");
		}
		
		if(this.getManagedType()==VisSchemeAppliedArchiveReproducedAndInsertedInstance.class || this.getManagedType()==VisInstanceRun.class)
			throw new VisframeException("insertedNonProcessIDSetColumn is not applicable for VisSchemeAppliedArchiveReproducedAndInsertedInstance and VisInstanceRun!");
		
		if(this.retrieveRow(id)==null) {
			throw new VisframeException("given id is not found in the management table");
		}
		
		//insert
		List<String> columnNameListToBeUpdated = new ArrayList<>();
		columnNameListToBeUpdated.add(VisframeUDTManagementProcessRelatedTableColumnFactory.insertedNonProcessIDSetColumn.getName().getStringValue());
		
		String psSqlString = TableContentSQLStringFactory.buildUpdateColumnsPreparedStatementSqlString(
				SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()), 
				columnNameListToBeUpdated, 
				TableContentSQLStringFactory.buildIDEquityConditionString(this.getPrimaryKeyAttributeNameMap(), id)
				);
		
		PreparedStatement ps = this.getHostVisProjectDBContext().getDBConnection().prepareStatement(psSqlString);
		
		ps.setObject(1, idSet);//
		
		ps.execute();
		
	}
	
	/**
	 * set the column value of {@link VisframeUDTManagementProcessRelatedTableColumnFactory#insertedProcessIDSetColumn} of the entity with the given ID with the given VfIDCollection; 
	 * 
	 * only valid for VisSchemeAppliedArchiveReproducedAndInsertedInstance type process;
	 * 
	 * @param id
	 * @param idSet
	 * @throws SQLException 
	 */
	public void setInsertedProcessIDSetColumn(I id, VfIDCollection idSet) throws SQLException {
		if(this.getManagedType()!=VisSchemeAppliedArchiveReproducedAndInsertedInstance.class) {
			throw new VisframeException("InsertedProcessIDSetColumn is only applicable for VisSchemeAppliedArchiveReproducedAndInsertedInstance management table!");
		}
		if(!(id instanceof VisSchemeAppliedArchiveReproducedAndInsertedInstanceID)) {
			throw new VisframeException("given id is not of VisSchemeAppliedArchiveReproducedAndInsertedInstanceID!");
		}
		
		if(this.retrieveRow(id)==null) {
			throw new VisframeException("given id is not found in the management table");
		}
		
		//insert
		List<String> columnNameListToBeUpdated = new ArrayList<>();
		columnNameListToBeUpdated.add(VisframeUDTManagementProcessRelatedTableColumnFactory.insertedProcessIDSetColumn.getName().getStringValue());
		
		String psSqlString = TableContentSQLStringFactory.buildUpdateColumnsPreparedStatementSqlString(
				SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()), 
				columnNameListToBeUpdated, 
				TableContentSQLStringFactory.buildIDEquityConditionString(this.getPrimaryKeyAttributeNameMap(), id)
				);
		
		PreparedStatement ps = this.getHostVisProjectDBContext().getDBConnection().prepareStatement(psSqlString);
		
		ps.setObject(1, idSet);//
		
		ps.execute();
		
	}
	
	/**
	 * set column value of {@link VisframeUDTManagementProcessRelatedTableColumnFactory#involvedCfTargetValueTableRunIDSetColumn} for the given id with the given set;
	 * 
	 * only applicable for VisInstanceRun; if not, throw exception;
	 * 
	 * @param id
	 * @param idSet
	 * @throws SQLException
	 */
	public void setInvolvedCFTargetValueTableRunIDSetColumn(I id, VfIDCollection idSet) throws SQLException {
		if(!this.getManagedType().equals(VisInstanceRun.class)) {
			throw new VisframeException("cannot set the INVOLVED_CFTARGETVALUETABLERUN_ID_SET column for non-VisInstanceRun type");
		}
		
		if(this.retrieveRow(id)==null) {
			throw new VisframeException("given id is not found in the management table");
		}
		
		
		//insert
		List<String> columnNameListToBeUpdated = new ArrayList<>();
		columnNameListToBeUpdated.add(VisframeUDTManagementProcessRelatedTableColumnFactory.involvedCfTargetValueTableRunIDSetColumn.getName().getStringValue());
		
		String psSqlString = TableContentSQLStringFactory.buildUpdateColumnsPreparedStatementSqlString(
				SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()), 
				columnNameListToBeUpdated, 
				TableContentSQLStringFactory.buildIDEquityConditionString(this.getPrimaryKeyAttributeNameMap(), id)
				);
		
		PreparedStatement ps = this.getHostVisProjectDBContext().getDBConnection().prepareStatement(psSqlString);
		
		ps.setObject(1, idSet);//
		
		ps.execute();
		
	}
	/**
	 * set  column value of {@link VisframeUDTManagementProcessRelatedTableColumnFactory#baseProcessIDSetColumn} 
	 * (the set of IDs of the ProcessType entities based on which the process type entity of the given ID can EXIST in the host VisProjectDBContext); 
	 * only applicable for {@link ProcessType} entity;
	 * set after the process is successfully finished;
	 * 
	 * @param id
	 * @param idSet
	 * @throws SQLException 
	 */
	public void setBaseProcessIDSetColumn(I id, VfIDCollection idSet) throws SQLException {
		if(!this.isOfProcessType()) {
			throw new VisframeException("not process type");
		}
		
		if(this.retrieveRow(id)==null) {
			throw new VisframeException("given id is not found in the management table");
		}
		
		
		//insert
		List<String> columnNameListToBeUpdated = new ArrayList<>();
		columnNameListToBeUpdated.add(VisframeUDTManagementProcessRelatedTableColumnFactory.baseProcessIDSetColumn.getName().getStringValue());
		
		String psSqlString = TableContentSQLStringFactory.buildUpdateColumnsPreparedStatementSqlString(
				SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()), 
				columnNameListToBeUpdated, 
				TableContentSQLStringFactory.buildIDEquityConditionString(this.getPrimaryKeyAttributeNameMap(), id)
				);
		
		PreparedStatement ps = this.getHostVisProjectDBContext().getDBConnection().prepareStatement(psSqlString);
		
		ps.setObject(1, idSet);//
		
		ps.execute();
		
	}
	////////////////////////////////////////////////

	/**
	 * change the value of {@link VisframeUDTManagementProcessRelatedTableColumnFactory#isTempColumn} to false for the given id;
	 * 
	 * 1. first check if the operation is allowed;
	 * 2. if yes ...
	 * 		UPDATE table_name SET column_name = value, column_name = value, ... WHERE conditions;
	 */
	@SuppressWarnings("unchecked")
	public void formalize(ID<?> entityID) throws SQLException{
		if(!this.isValidID(entityID)) {
			throw new VisframeException("given entity id is not of valid type");
		}
		
		List<String> columnNameList = new ArrayList<>();
		columnNameList.add(VisframeUDTManagementProcessRelatedTableColumnFactory.isTempColumn.getName().getStringValue());
		
		//create the PreparedStatement
		PreparedStatement ps = 
				this.getVisProjectDBConnection().prepareStatement(
						TableContentSQLStringFactory.buildUpdateColumnsPreparedStatementSqlString(
								SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()),
								columnNameList, 
								TableContentSQLStringFactory.buildIDEquityConditionString(this.getPrimaryKeyAttributeNameMap(), (I)entityID))
						);
		
		//set the updated object value
		ps.setBoolean(1, false);
		
		ps.execute();
	}
	
//	/**
//	 * change the IS_TEMP column value to false for the row of the given id in the management table of this manager;
//	 * if {@link #hasManagementTable()} returns false, throw {@link VisframeException} (UnsupportedOperationException) since there is no management table for non-VisframeUDT types
//	 * 
//	 * need to first check if the given id is of valid type with {@link #isValidID(Object)};
//	 * @param id
//	 * @throws SQLException 
//	 */
//	abstract void formalize(ID<? extends HasID> id) throws SQLException;
	
}
