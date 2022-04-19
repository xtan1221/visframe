package context.project.process.manager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import basic.lookup.PrimaryKeyID;
import context.project.process.logtable.ProcessLogTableAndProcessPerformerManager;
import context.project.process.logtable.ProcessLogTableRow;
import context.project.process.logtable.ProcessLogTableSchemaUtils;
import context.project.process.logtable.StatusType;
import context.project.process.logtable.VfIDCollection;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstanceID;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import exception.VisframeException;
import javafx.application.Platform;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;

/**
 * AbstractProcessManager that manages the process of reproducing and insertion of an instance of a VisSchemeAppliedArchive into host VisProjectDBContext;
 * 
 * @author tanxu
 *
 */
public class VisSchemeAppliedArchiveReproducingAndInsertionManager extends AbstractProcessManager{
	
	////////////////////////////////////
	///the most recently submitted running VisSchemeAppliedArchiveReproducerAndInserter
	/**
	 * the currently running VisSchemeApplier; all inserted reproduced Operation,
	 * CompositionFunctionGroup and CompositionFunction during the running process
	 * of a VisSchemeApplier will have its insertion process ID be the VisInstanceID
	 * of this VisSchemeApplier
	 */
	private VisSchemeAppliedArchiveReproducerAndInserter currentlyRunningVSAReproducerAndInserter;
	
	/**
	 * UID of the most recent new SimpleProcessPerformer inserted into the LOG table;
	 */
	private Integer currentlyRunningVSAReproducerAndInserterProcessUID;
	
	/**
	 * process ID
	 */
	private VisSchemeAppliedArchiveReproducedAndInsertedInstanceID currentlyRunningVSAReproducerAndInserterProcessID;
	
	
	////////////////////////////

	/**
	 * 
	 * @param processLogTableManager
	 */
	public VisSchemeAppliedArchiveReproducingAndInsertionManager(ProcessLogTableAndProcessPerformerManager processLogTableManager){
		super(processLogTableManager);
	}
	
	
	/**
	 * retrieve the current status of the process with UID equal to {@link #currentlyRunningVSAReproducerAndInserterProcessUID};
	 * @return
	 * @throws SQLException
	 */
	public StatusType getCurrentlyRunningVSAArchiveReproducerAndInserterProcessStatus() throws SQLException {
		if(this.currentlyRunningVSAReproducerAndInserterProcessUID==null) {
			throw new VisframeException("the currentlyRunningVSAReproducerAndInserterProcessUID is null!");
		}
		
		return this.getProcessLogTableManager().retrieveRow(this.currentlyRunningVSAReproducerAndInserterProcessUID).getStatus();
	}
	
	
	/**
	 * check if all VisSchemeAppliedArchiveReproducerAndInserter are done (DISCARDED, ROLLEDBACK or FINISHED) in the process log table;
	 * TODO validate
	 * @return
	 * @throws SQLException 
	 */
	public boolean allVSAArchiveReproducerAndInserterProcessesAreDone() throws SQLException {
		StringBuilder conditionStringBuilder = new StringBuilder();
		conditionStringBuilder.append(TableContentSQLStringFactory.buildColumnValueEquityCondition(
				ProcessLogTableSchemaUtils.processStatusColumn.getName().getStringValue(),
				StatusType.RUNNING.toString(),
				ProcessLogTableSchemaUtils.processStatusColumn.getSqlDataType().isOfStringType(),
				true));
		conditionStringBuilder.append(" OR ");
		conditionStringBuilder.append(TableContentSQLStringFactory.buildColumnValueEquityCondition(
				ProcessLogTableSchemaUtils.processStatusColumn.getName().getStringValue(),
				StatusType.ABORTED.toString(),
				ProcessLogTableSchemaUtils.processStatusColumn.getSqlDataType().isOfStringType(),
				true));
		conditionStringBuilder.append(" OR ");
		conditionStringBuilder.append(TableContentSQLStringFactory.buildColumnValueEquityCondition(
				ProcessLogTableSchemaUtils.processStatusColumn.getName().getStringValue(),
				StatusType.ROLLINGBACK.toString(),
				ProcessLogTableSchemaUtils.processStatusColumn.getSqlDataType().isOfStringType(),
				true));
		
		String sqlString = TableContentSQLStringFactory.buildSelectAllWithConditionString(
				SQLStringUtils.buildTableFullPathString(ProcessLogTableSchemaUtils.getTableSchema().getSchemaName(), ProcessLogTableSchemaUtils.getTableSchema().getName()),
				conditionStringBuilder.toString());
		
		Statement statement = this.getProcessLogTableManager().getHostVisProjectDBContext().getDBConnection().createStatement();
		
		ResultSet rs = statement.executeQuery(sqlString);
		
		// if empty, return true, if non-empty, return false;
		while(rs.next()) {
			ProcessLogTableRow row = ProcessLogTableSchemaUtils.retrieveRow(rs);
			if((row.getProcessID() instanceof VisSchemeAppliedArchiveReproducedAndInsertedInstanceID)) {
				if(!row.getStatus().isDone()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * set the baseProcessIDSetColumn of for the currently running VisSchemeAppliedArchiveReproducedAndInsertedInstance process;
	 * 		{@link ProcessLogTableSchemaUtils#baseProcessIDSetColumn}
	 * 
	 * if the current status of the process is not RUNNING, throw VisframeException;
	 * 
	 * @param idSet
	 * @throws SQLException
	 */
	public void setBaseProcessIDSetColumnOfCurrentlyRunningVSAArchiveReproducerAndInserterProcess(VfIDCollection idSet) throws SQLException {
		if(this.getCurrentlyRunningVSAReproducerAndInserterProcessUID()==null)
			throw new VisframeException("currentlyRunningVSAReproducerAndInserterProcessUID is null!");
		if (this.getCurrentlyRunningVSAArchiveReproducerAndInserterProcessStatus() != StatusType.RUNNING) {
			throw new VisframeException("currentlyRunningVSAReproducerAndInserterProcess is not running!");
		}
		
		List<String> columnNameListToBeUpdated = new ArrayList<>();
		columnNameListToBeUpdated.add(ProcessLogTableSchemaUtils.baseProcessIDSetColumn.getName().getStringValue());

		String psSqlString = TableContentSQLStringFactory.buildUpdateColumnsPreparedStatementSqlString(
				SQLStringUtils.buildTableFullPathString(
						ProcessLogTableSchemaUtils.getTableSchema().getSchemaName(), ProcessLogTableSchemaUtils.getTableSchema().getName()),
				columnNameListToBeUpdated,
				TableContentSQLStringFactory.buildColumnValueEquityCondition(
						ProcessLogTableSchemaUtils.UIDColumn.getName().getStringValue(),
						Integer.toString(this.currentlyRunningVSAReproducerAndInserterProcessUID), false, null));

		PreparedStatement ps = this.getProcessLogTableManager().getHostVisProjectDBContext().getDBConnection().prepareStatement(psSqlString);
		
		ps.setObject(1, idSet);//

		ps.execute();
	}
	
	/**
	 * add the given PrimaryKeyID to the insertedNonProcessIDSetColumn of the currently RUNNING VisSchemeAppliedArchiveReproducedAndInsertedInstance process;
	 * 		({@link ProcessLogTableSchemaUtils#insertedNonProcessIDSetColumn}) 
	 * 
	 * the given PrimaryKeyID must be of a ReproduceableProcessType VisframeUDT entity;
	 * 
	 * if current status of the process is not RUNNING, throw VisframeException;
	 * 
	 * @param id
	 * @throws SQLException
	 */
	public void addToInsertedProcessIDSetColumnOfCurrentlyRunningVSAArchiveReproducerAndInserterProcess(PrimaryKeyID<?> id)
			throws SQLException {
		if(this.getCurrentlyRunningVSAReproducerAndInserterProcessUID()==null)
			throw new VisframeException("currentlyRunningVSAReproducerAndInserterProcessUID is null!");
		if (this.getCurrentlyRunningVSAArchiveReproducerAndInserterProcessStatus() != StatusType.RUNNING) {
			throw new VisframeException("currentlyRunningVSAReproducerAndInserterProcess is not running!");
		}
		

		// 1. retrieve the VfIDCollection of the INSERTED_NON_PROCESS_ID_SET column of
		ProcessLogTableRow row = this.getProcessLogTableManager().retrieveRow(this.currentlyRunningVSAReproducerAndInserterProcessUID);
		if(row==null)
			throw new VisframeException("no process with currentlyRunningVSAReproducerAndInserterProcessUID found in the PROCESS LOG table");
		
		// 2. add id
		// deal with the first result
		VfIDCollection col = row.getInsertedProcessIDSet();
		col.addID(id);
		

		// 3. update the process log table
		List<String> columnNameListToBeUpdated = new ArrayList<>();
		columnNameListToBeUpdated
				.add(ProcessLogTableSchemaUtils.insertedProcessIDSetColumn.getName().getStringValue());
		String psSqlString = TableContentSQLStringFactory.buildUpdateColumnsPreparedStatementSqlString(
				SQLStringUtils.buildTableFullPathString(
						ProcessLogTableSchemaUtils.getTableSchema().getSchemaName(), ProcessLogTableSchemaUtils.getTableSchema().getName()),
				columnNameListToBeUpdated,
				TableContentSQLStringFactory.buildColumnValueEquityCondition(
						ProcessLogTableSchemaUtils.UIDColumn.getName().getStringValue(),
						Integer.toString(this.currentlyRunningVSAReproducerAndInserterProcessUID), false, null));
		
		PreparedStatement ps = this.getProcessLogTableManager().getHostVisProjectDBContext().getDBConnection().prepareStatement(psSqlString);

		ps.setObject(1, col);//
		
		ps.execute();
	}
	
	/**
	 * remove the given PrimaryKeyID from the insertedNonProcessIDSetColumn of the currently RUNNING VisSchemeAppliedArchiveReproducedAndInsertedInstance process;
	 * 		({@link ProcessLogTableSchemaUtils#insertedNonProcessIDSetColumn}) 
	 * 
	 * the given PrimaryKeyID must be of a ReproduceableProcessType VisframeUDT entity;
	 * 
	 * if current status of the process is not RUNNING, throw VisframeException;
	 * 
	 * facilitate rolling back of an inserted Operation/CFG/CF during the process of building the VisSchemeAppliedArchiveReproducedAndInsertedInstance;
	 * @param id
	 * @throws SQLException
	 */
	public void removeFromInsertedProcessIDSetColumnOfCurrentlyRunningVSAArchiveReproducerAndInserterProcess(PrimaryKeyID<?> id)
			throws SQLException {
		if(this.getCurrentlyRunningVSAReproducerAndInserterProcessUID()==null)
			throw new VisframeException("currentlyRunningVSAReproducerAndInserterProcessUID is null!");
		if (this.getCurrentlyRunningVSAArchiveReproducerAndInserterProcessStatus() != StatusType.RUNNING) {
			throw new VisframeException("currentlyRunningVSAReproducerAndInserterProcess is not running!");
		}
		

		// 1. retrieve the VfIDCollection of the INSERTED_NON_PROCESS_ID_SET column of
		ProcessLogTableRow row = this.getProcessLogTableManager().retrieveRow(this.currentlyRunningVSAReproducerAndInserterProcessUID);
		if(row==null)
			throw new VisframeException("no process with currentlyRunningVSAReproducerAndInserterProcessUID found in the PROCESS LOG table");
		
		// 2. add id
		// deal with the first result
		VfIDCollection col = row.getInsertedProcessIDSet();
		col.removeID(id);
		
		// 3. update the process log table
		List<String> columnNameListToBeUpdated = new ArrayList<>();
		columnNameListToBeUpdated
				.add(ProcessLogTableSchemaUtils.insertedProcessIDSetColumn.getName().getStringValue());
		String psSqlString = TableContentSQLStringFactory.buildUpdateColumnsPreparedStatementSqlString(
				SQLStringUtils.buildTableFullPathString(
						ProcessLogTableSchemaUtils.getTableSchema().getSchemaName(), ProcessLogTableSchemaUtils.getTableSchema().getName()),
				columnNameListToBeUpdated,
				TableContentSQLStringFactory.buildColumnValueEquityCondition(
						ProcessLogTableSchemaUtils.UIDColumn.getName().getStringValue(),
						Integer.toString(this.currentlyRunningVSAReproducerAndInserterProcessUID), false, null));
		
		PreparedStatement ps = this.getProcessLogTableManager().getHostVisProjectDBContext().getDBConnection().prepareStatement(psSqlString);

		ps.setObject(1, col);//
		
		ps.execute();
	}
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * start the given {@link VisSchemeAppliedArchiveReproducerAndInserter};
	 * 
	 * 1. check if there is a undone process 
	 * 		{@link #allSimpleProcessesAreDone()}
	 * 		{@link #allVSAArchiveReproducerAndInserterProcessesAreDone()}
	 * 		if yes, throw exception; 
	 * 
	 * 2. check if there is finished {@link VisSchemeAppliedArchiveReproducerAndInserter} process with the same id;
	 * 		if yes, throw exception;
	 * 
	 * 3. update related fields to the given {@link VisSchemeAppliedArchiveReproducerAndInserter};
	 * 
	 * 4. insert into the process log table a row with 
	 * 		1. start time
	 * 		2. new UID
	 * 		3. status = RUNNING
	 * 		4. process ID column {@link VisSchemeAppliedArchiveReproducerAndInserter#getID()}
	 * 		5. visSchemeAppliedArchiveReproducedAndInsertedInstanceColumn = null
	 * 		6. other columns accordingly
	 * 
	 * 5. retrieve and set the value of {@link #currentlyRunningVSAReproducerAndInserterProcessUID}
	 * 
	 * 6. check the constraints of the given VisSchemeAppliedArchiveReproducerAndInserter
	 * 		if not passed, discard the currently running process;
	 * =======================================================================
	 * this method is mainly to prepare for the Operation, CFG and CF to be reproduced and inserted as Simple processes;
	 * 
	 * 
	 * @throws SQLException 
	 * 
	 */
	public void startNewVSAArchiveReproducerAndInserter(VisSchemeAppliedArchiveReproducerAndInserter vsa) throws SQLException {
		// 1 check if all simple process are done and there is no current finished process with the same id of the given process
		if (!this.getProcessLogTableManager().getSimpleProcessManager().allSimpleProcessesAreDone()) {
			throw new VisframeException("cannot start new process when existing simple processes are not done");
		}
		if(!this.allVSAArchiveReproducerAndInserterProcessesAreDone()) {
			throw new VisframeException("cannot start new process when existing VSAArchiveReproducerAndInserter processes are not done");
		}
		
		//2
		if (this.getProcessLogTableManager().isProcessIDFinished(vsa.getID())) {
			throw new VisframeException("existing FINISHED VSAArchiveReproducerAndInserter process with the same ID found");
		}
		
		//3 update mostRecentRunningSimpleProcessID and mostRecentlyRunningSimpleProcessRunnableAction
		//mostRecentRunningSimpleProcessExceptionSet
		this.currentlyRunningVSAReproducerAndInserter = vsa;
		this.currentlyRunningVSAReproducerAndInserterProcessID = vsa.getID();
		
		// 4 insert into process log table as a new row
		this.insertRowIntoProcessLogTable(vsa);
		
		// 5. update the mostRecentRunningSimpleProcessUID
		ResultSet rs = this.getProcessLogTableManager().getResultSetOfAllProcessOrderedByStartTimeDESC();
		while(rs.next()) {
			ProcessLogTableRow row = ProcessLogTableSchemaUtils.retrieveRow(rs);
			if(row.getProcessID() instanceof VisSchemeAppliedArchiveReproducedAndInsertedInstanceID) {
				this.currentlyRunningVSAReproducerAndInserterProcessUID = row.getUID();
				break;
			}
		}
		
		// 6 check the constraints of the given SimpleProcessPerformer, if not passed, discard the process
		try {
			vsa.checkConstraints();
		} catch (Exception e) {
			this.discardCurrentlyRunningVSAArchiveReproducerAndInserter();
			throw e;
		}
		
		//
		//now Operation, CFG and CF can be reproduced and inserted;
	}
	

	
	
	/**
	 * insert a new row into the process log table with the given VisSchemeAppliedArchiveReproducerAndInserter
	 * 
	 * 
	 * 
	 * set value of 
	 * 1. {@link #currentlyRunningVSAReproducerAndInserterProcessUID};
	 * 2. {@link #currentlyRunningVSAReproducerAndInserterProcessID};
	 * 
	 * 
	 * @param id
	 * @throws SQLException
	 */
	private void insertRowIntoProcessLogTable(VisSchemeAppliedArchiveReproducerAndInserter VSAReproducerAndInserter) throws SQLException {
		List<String> columnNameList = new ArrayList<>(this.getInitializedInsertPreparedStatementColumnNameList());
		// add the process type entity column specific to the given
		// SimpleProcessPerformer
//		String processTypeEntityColName = this.getProcessTypeEntityColumnName(spp.getProcessEntity()).getStringValue();
//		columnNameList.add(processTypeEntityColName);
		
		//
		PreparedStatement ps = 
				this.makeInitializedInsertPreparedStatement(
					VSAReproducerAndInserter.getID(),
					columnNameList);
		
		// process entity column
//		ps.setObject(columnNameList.size(), spp.getProcessEntity());
		
		/////////
		ps.execute();
		
		
		//TODO invoke Runnables for specific types of process
		if(this.processInsertedOrStatusColumnChangeEventRunnableSet!=null)
			this.processInsertedOrStatusColumnChangeEventRunnableSet.forEach(r->{
				Platform.runLater(r);
			});
	}
	
	

	/**
	 * list of column names to be inserted into the process log table when initializing a VisSchemeAppliedArchiveReproducedAndInsertedInstance process 
	 */
	private List<String> initializedInsertPreparedStatementColumnNameList;

	/**
	 * build (if not already) and return {@link #initializedInsertPreparedStatementColumnNameList}
	 * 
	 * the list of columns whose values need to be explicitly set for a process to be inserted as a row in the process log table;
	 * 
	 * which includes all columns of the process log table except for the following
	 * 1. UIDColumn (automatically set with default value constraints)
	 * 		see {@link ProcessLogTableSchemaUtils#UIDColumn}
	 * 2. startTimeColumn (automatically set with default value constraints)
	 * 		see {@link ProcessLogTableSchemaUtils#startTimeColumn}
	 * 3. all process entity columns
	 * 
	 * see {@link ProcessLogTableSchemaUtils#getColumnList()}
	 * @return
	 */
	private List<String> getInitializedInsertPreparedStatementColumnNameList() {
		if (initializedInsertPreparedStatementColumnNameList == null) {
			this.initializedInsertPreparedStatementColumnNameList = new ArrayList<>();
			this.initializedInsertPreparedStatementColumnNameList.add(
					ProcessLogTableSchemaUtils.processIDColumn.getName().getStringValue());
			this.initializedInsertPreparedStatementColumnNameList.add(
					ProcessLogTableSchemaUtils.insertionProcessIDColumn.getName().getStringValue());
			this.initializedInsertPreparedStatementColumnNameList.add(
					ProcessLogTableSchemaUtils.isReproducedColumn.getName().getStringValue());
			this.initializedInsertPreparedStatementColumnNameList.add(
					ProcessLogTableSchemaUtils.processStatusColumn.getName().getStringValue());
			
			this.initializedInsertPreparedStatementColumnNameList.add(
					ProcessLogTableSchemaUtils.baseProcessIDSetColumn.getName().getStringValue());
			this.initializedInsertPreparedStatementColumnNameList.add(
					ProcessLogTableSchemaUtils.insertedProcessIDSetColumn.getName().getStringValue());
			this.initializedInsertPreparedStatementColumnNameList.add(
					ProcessLogTableSchemaUtils.insertedNonProcessIDSetColumn.getName().getStringValue());
			this.initializedInsertPreparedStatementColumnNameList.add(
					ProcessLogTableSchemaUtils.involvedCfTargetValueTableRunIDSetColumn.getName().getStringValue());
		}
		return this.initializedInsertPreparedStatementColumnNameList;
	}
	
	/**
	 * create and return an initialized PreparedStatement for inserting a new VisSchemeAppliedArchiveReproducedAndInsertedInstance process into the process log table for the given VisSchemeAppliedArchiveReproducedAndInsertedInstanceID
	 * 
	 * specific to VisSchemeAppliedArchiveReproducedAndInsertedInstance
	 * @param spp
	 * @return
	 * @throws SQLException
	 */
	private PreparedStatement makeInitializedInsertPreparedStatement(
			VisSchemeAppliedArchiveReproducedAndInsertedInstanceID id, List<String> initializedInsertPreparedStatementColumnNameList) throws SQLException {
		//build sql string for PreparedStatement
		String psSqlString = 
				TableContentSQLStringFactory.buildInsertIntoPreparedStatementSqlString(
						SQLStringUtils.buildTableFullPathString(ProcessLogTableSchemaUtils.getTableSchema().getSchemaName(), ProcessLogTableSchemaUtils.getTableSchema().getName()),
				initializedInsertPreparedStatementColumnNameList // list of column name to be inserted
						);
		
		PreparedStatement ps = this.getProcessLogTableManager().getHostVisProjectDBContext().getDBConnection().prepareStatement(psSqlString);

		// UIDColumn
		// CANNOT explicitly set value of UID since it has constraints "GENERATED ALWAYS
		// AS IDENTITY (START WITH 1, INCREMENT BY 1)"
		// ps.setInt(this.getTableSchema().getColumnIndex(ProcessLogTableSchemaUtils.UIDColumn.getName()),
		// this.getMostRecentProcessUID()+1);

		// no need to set startTimeColumn, with constraints "DEFAULT CURRENT_TIMESTAMP"
		
		// processIDColumn
		ps.setObject(
				initializedInsertPreparedStatementColumnNameList.indexOf(ProcessLogTableSchemaUtils.processIDColumn.getName().getStringValue()) + 1, 
				id);
		
		//insertionProcessIDColumn
		ps.setObject(
				initializedInsertPreparedStatementColumnNameList.indexOf(ProcessLogTableSchemaUtils.insertionProcessIDColumn.getName().getStringValue()) + 1, 
				id);
		
		//isReproducedColumn
		ps.setObject( //TODO cannot set null for boolean column????
				initializedInsertPreparedStatementColumnNameList.indexOf(ProcessLogTableSchemaUtils.isReproducedColumn.getName().getStringValue()) + 1,
				null);
		
		// processStatusColumn
		ps.setString(
				initializedInsertPreparedStatementColumnNameList.indexOf(ProcessLogTableSchemaUtils.processStatusColumn.getName().getStringValue()) + 1,
				StatusType.RUNNING.toString());
		
		// baseProcessIDSetColumn - value is set in the postprocess() method of VisSchemeAppliedArchiveReproducerAndInserter;
		ps.setObject(
				initializedInsertPreparedStatementColumnNameList.indexOf(ProcessLogTableSchemaUtils.baseProcessIDSetColumn.getName().getStringValue()) + 1,
				new VfIDCollection());
		
		//insertedProcessIDSetColumn
		//must be null for non-VisSchemeAppliedArchiveReproducedAndInsertedInstance type (simple) process
		ps.setObject(
				initializedInsertPreparedStatementColumnNameList.indexOf(ProcessLogTableSchemaUtils.insertedProcessIDSetColumn.getName().getStringValue()) + 1,
				new VfIDCollection());
		
		// insertedNonProcessIDSetColumn
		ps.setObject(
				initializedInsertPreparedStatementColumnNameList.indexOf(ProcessLogTableSchemaUtils.insertedNonProcessIDSetColumn.getName().getStringValue()) + 1,
				null);
		
		
		//involvedCfTargetValueTableRunIDSetColumn
		ps.setObject(initializedInsertPreparedStatementColumnNameList.indexOf(
				ProcessLogTableSchemaUtils.involvedCfTargetValueTableRunIDSetColumn.getName().getStringValue()) + 1,
				null);
			
		return ps;
	}
	
	//////////////////////////////////////////////////
	
	/**
	 * discard the {@link #currentlyRunningVSAReproducerAndInserter};
	 * 
	 * the invoker method should be aware of that 
	 * 		this method is only applicable before it getting into the stage that produce data in rdb of host VisProjectDBContext since this will not roll back the process;
	 * 
	 * specifically before the first reproduce Operation is inserted;
	 * @throws SQLException 
	 * 
	 */
	public void discardCurrentlyRunningVSAArchiveReproducerAndInserter() throws SQLException {
		if(this.getCurrentlyRunningVSAReproducerAndInserterProcessUID()==null)
			throw new VisframeException("currentlyRunningVSAReproducerAndInserterProcessUID is null!");
		if (this.getCurrentlyRunningVSAArchiveReproducerAndInserterProcessStatus() != StatusType.RUNNING) {
			throw new VisframeException("currentlyRunningVSAReproducerAndInserterProcess is not running!");
		}
		
		// 1
		this.setProcessStatusInLogTable(this.currentlyRunningVSAReproducerAndInserterProcessUID, StatusType.DISCARDED);
	}
	
	
	/**
	 * abort and roll back the {@link #currentlyRunningVSAReproducerAndInserter};
	 * 
	 * 1. if the {@link #currentlyRunningVSAReproducerAndInserter} is null, throw exception; 
	 * 2. if there is still a running process, cancel it 
	 * 3. invoke the
	 * @throws SQLException 
	 */
	public void abortAndRollbackCurrentlyRunningVSAArchiveReproducerAndInserter() throws SQLException {
		if(this.getCurrentlyRunningVSAReproducerAndInserterProcessUID()==null)
			throw new VisframeException("currentlyRunningVSAReproducerAndInserterProcessUID is null!");
		if (this.getCurrentlyRunningVSAArchiveReproducerAndInserterProcessStatus() != StatusType.RUNNING) {
			throw new VisframeException("currentlyRunningVSAReproducerAndInserterProcess is not running!");
		}
//		
//		// 2
//		if (this.mostRecentlyRunningSimpleProcessPerformerTracker != null
//				&& !this.mostRecentlyRunningSimpleProcessPerformerTracker.isDone()) {
////			this.mostRecentlyRunningSimpleProcessPerformerTracker.getFuture().cancel(true); //TODO test
//			this.getProcessLogTableManager().getExecutorService().shutdown();
//			this.getProcessLogTableManager().setExecutorService(null);
////		} else if (this.currentlyRunningVSAReproducerAndInserter != null) {
////			this.currentlyRunningVSAReproducerAndInserter.abort();
//		}else {
//			throw new VisframeException("no process is running in the ExecutorService!"); //
//		}
		
		// 3 set status to aborted
		this.setProcessStatusInLogTable(this.getCurrentlyRunningVSAReproducerAndInserterProcessUID(), StatusType.ABORTED);
		
		//4. roll back
		this.getProcessLogTableManager().rollbackProcess(this.getCurrentlyRunningVSAReproducerAndInserterProcessUID());
	}
	
	
	
	
	
	//////////////////////////////////////////
	/**
	 * @return the currentlyRunningVSAReproducerAndInserterProcessUID
	 */
	public Integer getCurrentlyRunningVSAReproducerAndInserterProcessUID() {
		return currentlyRunningVSAReproducerAndInserterProcessUID;
	}
	
	/**
	 * @return the currentlyRunningVSAReproducerAndInserterProcessID
	 */
	public VisSchemeAppliedArchiveReproducedAndInsertedInstanceID getCurrentlyRunningVSAReproducerAndInserterProcessID() {
		return currentlyRunningVSAReproducerAndInserterProcessID;
	}


	/**
	 * @return the currentlyRunningVSAReproducerAndInserter
	 */
	public VisSchemeAppliedArchiveReproducerAndInserter getCurrentlyRunningVSAReproducerAndInserter() {
		return currentlyRunningVSAReproducerAndInserter;
	}

	/**
	 * return whether there is a currently running VisSchemeAppliedArchiveReproducedAndInsertedInstance;
	 * @return
	 * @throws SQLException
	 */
	public boolean currentlyVSAArchiveReproducerAndInserterIsRunning() throws SQLException {
		return this.getCurrentlyRunningVSAReproducerAndInserterProcessUID()!=null && 
				this.getCurrentlyRunningVSAArchiveReproducerAndInserterProcessStatus().equals(StatusType.RUNNING);
	}
}
