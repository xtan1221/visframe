package context.project.process.manager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import basic.lookup.PrimaryKeyID;
import basic.lookup.VisframeUDT;
import context.project.process.SimpleProcessPerformer;
import context.project.process.logtable.ProcessLogTableAndProcessPerformerManager;
import context.project.process.logtable.ProcessLogTableRow;
import context.project.process.logtable.ProcessLogTableSchemaUtils;
import context.project.process.logtable.StatusType;
import context.project.process.logtable.VfIDCollection;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstanceID;
import exception.VisframeException;
import function.composition.CompositionFunction;
import importer.DataImporter;
import javafx.application.Platform;
import operation.Operation;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;
import visinstance.run.VisInstanceRunID;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunID;

/**
 * manager class for all simple process performer of a ProcessLogTableAndProcessPerformerManager;
 * 
 * @author tanxu
 *
 */
public class SimpleProcessManager extends AbstractProcessManager{
	
	//////////////////////////////////////////
	/**
	 * UID of the most recent new SimpleProcessPerformer inserted into the LOG table;
	 */
	private Integer mostRecentRunningSimpleProcessUID;
	

	/**
	 * process ID
	 */
	private PrimaryKeyID<?> mostRecentRunningSimpleProcessID;
	
	
	//////////////////////////
//	/**
//	 * 
//	 */
//	private Set<Throwable> mostRecentRunningSimpleProcessExceptionSet;
//	
//	/**
//	 * tracker of the most recently submitted SimpleProcessPerformer
//	 */
//	private CallableStatusTracker3<StatusType> mostRecentlyRunningSimpleProcessPerformerTracker;
	
	////////////////////////////
	
	/**
	 * 
	 * @param processLogTableManager
	 */
	public SimpleProcessManager(ProcessLogTableAndProcessPerformerManager processLogTableManager){
		super(processLogTableManager);
	}
	
	
//	/**
//	 * monitor the status of the most recently running process until one of the following occurs;
//	 * 
//	 * 1. if the process's status is FINISHED before wait time
//	 * 
//	 * 2. if the process's is still running when the wait time is reached, return {@link StatusType#RUNNING};
//	 */
//	public StatusType monitorMostRecentlyRuningProcessStatus(){
//		
//		
//		//TODO
//		return null;
//	}
	
	
	/////////////////////////////////////////////////////////////////
	//
	/**
	 * check if all simple processes status are done (DISCARDED, ROLLEDBACK or FINISHED);
	 * 
	 * find out if there is any row 
	 * 1. with process ID not instance of VisSchemeAppliedArchiveReproducedAndInsertedInstanceID and
	 * 2. with status = RUNNING or ABORTED or ROLLINGBACK;
	 * 
	 * if true, return false (not all processes are done), otherwise, return true;
	 * 
	 * @return
	 * @throws SQLException
	 */
	public boolean allSimpleProcessesAreDone() throws SQLException {
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
		
		//TODO filter out VisSchemeAppliedArchiveReproducedAndInsertedInstanceIDs
		// if empty, return true, if non-empty, return false;
		while(rs.next()) {
			ProcessLogTableRow row = ProcessLogTableSchemaUtils.retrieveRow(rs);
			if(!(row.getProcessID() instanceof VisSchemeAppliedArchiveReproducedAndInsertedInstanceID)) {
				if(!row.getStatus().isDone()) {
					return false;
				}
			}
		}
		
		return true;

	}
	
	/**
	 * retrieve the current status of the simple process with UID equal to {@link #mostRecentRunningSimpleProcessUID};
	 * @return
	 * @throws SQLException
	 */
	public StatusType getMostRecentlyRunningSimpleProcessStatus() throws SQLException {
		if(this.mostRecentRunningSimpleProcessUID==null) {
			throw new VisframeException("the mostRecentRunningSimpleProcessUID is null!");
		}
		
		return this.getProcessLogTableManager().retrieveRow(this.mostRecentRunningSimpleProcessUID).getStatus();
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * set the BASE_PROCESS_ID_SET column ({@link ProcessLogTableSchemaUtils#baseProcessIDSetColumn}) for the currently
	 * running process (after it is performed);
	 * 
	 * if {@link #mostRecentRunningSimpleProcessStatus} is not RUNNING, throw
	 * VisframeException;
	 * 
	 * @param idSet
	 * @throws SQLException
	 */
	public void setBaseProcessIDSetColumnOfCurrentlyRunningSimpleProcess(VfIDCollection idSet) throws SQLException {
		if (this.getMostRecentlyRunningSimpleProcessStatus() != StatusType.RUNNING) {
			throw new VisframeException("no process is running");
		}
		
		List<String> columnNameListToBeUpdated = new ArrayList<>();
		columnNameListToBeUpdated.add(ProcessLogTableSchemaUtils.baseProcessIDSetColumn.getName().getStringValue());

		String psSqlString = TableContentSQLStringFactory.buildUpdateColumnsPreparedStatementSqlString(
				SQLStringUtils.buildTableFullPathString(
						ProcessLogTableSchemaUtils.getTableSchema().getSchemaName(), ProcessLogTableSchemaUtils.getTableSchema().getName()),
				columnNameListToBeUpdated,
				TableContentSQLStringFactory.buildColumnValueEquityCondition(
						ProcessLogTableSchemaUtils.UIDColumn.getName().getStringValue(),
						Integer.toString(this.mostRecentRunningSimpleProcessUID), false, null));

		PreparedStatement ps = this.getProcessLogTableManager().getHostVisProjectDBContext().getDBConnection().prepareStatement(psSqlString);

		ps.setObject(1, idSet);//

		ps.execute();
	}
	
	/**
	 * add the given PrimaryKeyID to the INSERTED_NON_PROCESS_ID_SET column ({@link ProcessLogTableSchemaUtils#insertedNonProcessIDSetColumn}) of the currently RUNNING simple process;
	 * 
	 * the given PrimaryKeyID must be of a NonProcessType VisframeUDT entity;
	 * 
	 * if {@link #mostRecentRunningSimpleProcessStatus} is not RUNNING, throw VisframeException;
	 * 
	 * @param id
	 * @throws SQLException
	 */
	public void addToInsertedNonProcessIDSetColumnOfCurrentlyRunningSimpleProcess(PrimaryKeyID<?> id)
			throws SQLException {
		if (this.getMostRecentlyRunningSimpleProcessStatus() != StatusType.RUNNING) {
			throw new VisframeException("no process is running");
		}
		
		// 1. retrieve the VfIDCollection of the INSERTED_NON_PROCESS_ID_SET column of
		ProcessLogTableRow row = this.getProcessLogTableManager().retrieveRow(this.mostRecentRunningSimpleProcessUID);
		if(row==null)
			throw new VisframeException("no process with mostRecentRunningSimpleProcessUID found in the PROCESS LOG table");
		
		
		// 2. add id
		// deal with the first result
		VfIDCollection col = row.getInsertedNonProcessIDSet();
		col.addID(id);
		
		
		// 3. update the process log table
		List<String> columnNameListToBeUpdated = new ArrayList<>();
		columnNameListToBeUpdated
				.add(ProcessLogTableSchemaUtils.insertedNonProcessIDSetColumn.getName().getStringValue());
		String psSqlString = TableContentSQLStringFactory.buildUpdateColumnsPreparedStatementSqlString(
				SQLStringUtils.buildTableFullPathString(
						ProcessLogTableSchemaUtils.getTableSchema().getSchemaName(), ProcessLogTableSchemaUtils.getTableSchema().getName()),
				columnNameListToBeUpdated,
				TableContentSQLStringFactory.buildColumnValueEquityCondition(
						ProcessLogTableSchemaUtils.UIDColumn.getName().getStringValue(),
						Integer.toString(this.mostRecentRunningSimpleProcessUID), false, null));
		
		PreparedStatement ps = this.getProcessLogTableManager().getHostVisProjectDBContext().getDBConnection().prepareStatement(psSqlString);
		
		ps.setObject(1, col);//
		
		ps.execute();
	}
	
	/**
	 * add the given CFTargetValueTableRunID to the INVOLVED_CFTARGETVALUETABLERUNID_SET column({@link ProcessLogTableSchemaUtils#involvedCfTargetValueTableRunIDSetColumn}) of the currently RUNNING process;
	 * 
	 * if {@link #mostRecentRunningSimpleProcessStatus} is not RUNNING, throw VisframeException;
	 * 
	 * note that it is not checked here whether the currently running process is a VisInstanceRun or not, which should be implicitly constrained by the caller method;
	 * 
	 * @param id
	 * @throws SQLException
	 */
	public void addToInvolvedCFTargetValueTableRunIDSetColumnOfCurrentlyRunningProcess(CFTargetValueTableRunID id)
			throws SQLException {
		if (this.getMostRecentlyRunningSimpleProcessStatus() != StatusType.RUNNING) {
			throw new VisframeException("no process is running");
		}
		
		// 1. retrieve the VfIDCollection of the INSERTED_NON_PROCESS_ID_SET column of
		ProcessLogTableRow row = this.getProcessLogTableManager().retrieveRow(this.mostRecentRunningSimpleProcessUID);
		if(row==null)
			throw new VisframeException("no process with mostRecentRunningSimpleProcessUID found in the PROCESS LOG table");
				
		
		// 2. add id
		// deal with the first result
		VfIDCollection col = row.getInvolvedCFTargetValueTableRunIDSet();
		col.addID(id);
		

		// 3. update the process log table
		List<String> columnNameListToBeUpdated = new ArrayList<>();
		columnNameListToBeUpdated
				.add(ProcessLogTableSchemaUtils.involvedCfTargetValueTableRunIDSetColumn.getName().getStringValue());
		String psSqlString = TableContentSQLStringFactory.buildUpdateColumnsPreparedStatementSqlString(
				SQLStringUtils.buildTableFullPathString(
						ProcessLogTableSchemaUtils.getTableSchema().getSchemaName(), ProcessLogTableSchemaUtils.getTableSchema().getName()),
				columnNameListToBeUpdated,
				TableContentSQLStringFactory.buildColumnValueEquityCondition(
						ProcessLogTableSchemaUtils.UIDColumn.getName().getStringValue(),
						Integer.toString(this.mostRecentRunningSimpleProcessUID), false, null));

		PreparedStatement ps = this.getProcessLogTableManager().getHostVisProjectDBContext().getDBConnection().prepareStatement(psSqlString);

		ps.setObject(1, col);//

		ps.execute();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// *************************start a new simple process
	/**
	 * 112420-update
	 * perform starter steps of the given {@link SimpleProcessPerformer};
	 * 
	 * 1. perform all validations and constraints checking needed before submitting the process to the {@link ProcessLogTableAndProcessPerformerManager#getExecutorService()};
	 * 		1. check if there are any running simple prcoess
	 * 		2. check if there is any finished process with the same ID
	 * 		3. set values of fields related with the most recently running process
	 * 		4. insert into the process log table
	 * 			status will be RUNNING
	 * 		5. check the constraints 
	 * 			{@link SimpleProcessPerformer#checkConstraints()}
	 * 
	 * 2. if all validations are passed and constraints are obeyed;
	 * 			the process's status in the process log table will be RUNNING
	 * 		else, discard the process and throw exceptions;
	 * 			in this case, the process's status in the process log table will be DISCARDED
	 * 
	 * note that this method will not submit the SimpleProcessPerformer to the ExecutorService of the ProcessLogTableAndProcessPerformerManager
	 * 
	 * invoker of this method should use the try-catch clause to catch any exception throw and decide what to do next based on whether excpetion is thrown or not;
	 * 
	 * @param spp
	 * @throws SQLException
	 */
	public void startNewProcess(SimpleProcessPerformer<?, ?, ?> spp) throws SQLException {
		// 1 check if all simple process are done and there is no current finished process with the same id of the given process
		if (!this.allSimpleProcessesAreDone()) {
			throw new VisframeException("cannot start new process when existing processes are not done");
		}
		if (this.getProcessLogTableManager().isProcessIDFinished(spp.getProcessEntity().getID())) {
			throw new VisframeException("existing FINISHED process with the same ID found");
		}
		
		//2 update mostRecentRunningSimpleProcessID and mostRecentlyRunningSimpleProcessRunnableAction
		//mostRecentRunningSimpleProcessExceptionSet
		this.mostRecentRunningSimpleProcessID = spp.getID();
//		this.mostRecentRunningSimpleProcessExceptionSet = null;
		
		// 3 insert into process log table as a new row
		this.insertRowIntoProcessLogTable(spp);
		
		// update the mostRecentRunningSimpleProcessUID
		ResultSet rs = this.getProcessLogTableManager().getResultSetOfAllProcessOrderedByStartTimeDESC();
		while(rs.next()) {
			ProcessLogTableRow row = ProcessLogTableSchemaUtils.retrieveRow(rs);
			if(row.getProcessID() instanceof VisSchemeAppliedArchiveReproducedAndInsertedInstanceID) {
				//skip VisSchemeAppliedArchiveReproducedAndInsertedInstance process
			}else {
				this.mostRecentRunningSimpleProcessUID = row.getUID();
				break;//exit loop after first simple process is found
			}
		}
		
		// 4 check the constraints of the given SimpleProcessPerformer, if not passed, discard the process
		try {
			spp.checkConstraints();
//			return true;
		} catch (Exception e) {
			this.discardCurrentRunningSimpleProcess();
			throw e;
//			return false;
		}
		
		////////////////////////////////////////////////////
		//5. run the process to generated data in rdb of host VisProjectDBContext
//		normalRunWithUI(spp, maxRunWaitTimeReachedAction, exceptionThrownAction, successfullyFinishedAction);
		
		//
//		normalRunWithoutUI(spp, maxRunWaitTimeReachedAction, exceptionThrownAction, successfullyFinishedAction);

		//
//		testRunToDebugStepsDetails(spp);

	}

//	/**
//	 * submit and run and track the process performer as normal;
//	 * 
//	 * the {@link SimpleProcessPerformer#call()} method will be invoked after it is
//	 * submitted to the ExecutorService;
//	 * 
//	 * note that any other methods of the SimpleProcessPerformer such as the
//	 * {@link SimpleProcessPerformer#checkConstraints()} will not invoked;
//	 * 
//	 * 
//	 * only applicable for UI based running;
//	 * 
//	 * @param spp
//	 */
//	private void normalRunWithUI(SimpleProcessPerformer<?, ?, ?> spp, Runnable maxRunWaitTimeReachedAction, Runnable exceptionThrownAction, Runnable successfullyFinishedAction) {
////		//to see what happens inside the mostRecentlyRunningSimpleProcessPerformerTracker, comment out the next line and used the testing block below;
//		// submit the process
//		Future<StatusType> future = this.getProcessLogTableManager().getExecutorService().submit(spp);
//		this.mostRecentlyRunningSimpleProcessPerformerTracker = 
//				new CallableStatusTracker3<>(
//					this, spp, future,
//					maxRunWaitTimeReachedAction, exceptionThrownAction, successfullyFinishedAction,
//					SimpleProcessPerformer.MAX_WAIT_TIME, SimpleProcessPerformer.WAIT_TIME_UNIT,
//					SimpleProcessPerformer.MAX_WAIT_ROUND);
//		
//		this.getProcessLogTableManager().getExecutorService().submit(mostRecentlyRunningSimpleProcessPerformerTracker);
//	}
	
	
//	/**
//	 * test run with JUnit testing process performer is submitted to ExecutorService
//	 * and cannot see the details of each step (breakpoint is not working) used
//	 * after a debug testing is done and need a quick run to verify the result in
//	 * VisProjectDBContext's db before testing with UI;
//	 * 
//	 * the main feature of this method is that it will try to hold the thread for a
//	 * short amount of time to wait for the whole process to finish; if not, some
//	 * process may not finish before the Virtual machine is terminated, thus lead to
//	 * unexpected result;
//	 * 
//	 * note that with UI, since virtual machine is always running before the UI is
//	 * closed, thus no need to explicitly hold the thread for a period of time!
//	 * 
//	 * theoretically, the resulted changes of a successfully run in the host
//	 * VisProjectDBContext's db should not be rolled back but kept next time it is
//	 * connected;
//	 * 
//	 * @param spp
//	 */
//	private void normalRunWithoutUI(SimpleProcessPerformer<?, ?, ?> spp, Runnable maxRunWaitTimeReachedAction, Runnable exceptionThrownAction, Runnable successfullyFinishedAction) {
////		if not sleep for a small period time and just exit current thread, the ExecutorService that runs the process may be terminated immediately and process cannot finish;
////		thus next time the session is reconnected, the unfinished process will be rolled back;
////		thus when testing process with this method, need to activate the Thread.sleep to ensure the ExecutorService has enough time to finish the process;
//		// submit the process
//		Future<StatusType> future = this.getProcessLogTableManager().getExecutorService().submit(spp);
//		this.mostRecentlyRunningSimpleProcessPerformerTracker = new CallableStatusTracker3<>(
//				this, spp, future, 
//				maxRunWaitTimeReachedAction, exceptionThrownAction, successfullyFinishedAction,
//				SimpleProcessPerformer.MAX_WAIT_TIME, SimpleProcessPerformer.WAIT_TIME_UNIT,
//				SimpleProcessPerformer.MAX_WAIT_ROUND);
//		//
//		mostRecentlyRunningSimpleProcessPerformerTracker.run();// for testing
//		
//		try {
//			Thread.sleep(6000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

//	/**
//	 * debug each step with breakpoints;
//	 * 
//	 * theoretically, the resulted changes of a successfully run in host
//	 * VisProjectDBContext's db will be rolled back next time it is connected since
//	 * the post processing is not run, which is invoked by
//	 * {@link CallableStatusTracker2#run()}!
//	 * 
//	 * @param spp
//	 */
//	private void testRunToDebugStepsDetails(SimpleProcessPerformer<?, ?, ?> spp) {
//		// for testing that focus on details of how process is performed, but do not
//		// result in success of process running (always rolledback)
//		try {
//			spp.call();
//		} catch (SQLException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		System.out.println("testing done!");
//	}
	
	/////////////////////////////////////////////////////////////
	
	/**
	 * insert the process entity of the given SimpleProcessPerformer as a row of the process log table with STATUS = RUNNING;
	 * 
	 * then set the fields related 
	 * 1. {@link #mostRecentRunningSimpleProcessUID} to the uid of the process 
	 * 2. {@link #mostRecentRunningSimpleProcessID} to the id of the process of the SimpleProcessPerformer 
	 * 3. {@link #mostRecentRunningSimpleProcessStatus} to RUNNING
	 * 4. {@link #mostRecentRunningSimpleProcessExceptionSet} to empty
	 * 
	 * note that this method does not check if new process can be inserted or not (due to any undone process), 
	 * which should be performed by invoker method of this one;
	 * @param spp
	 * @throws SQLException
	 */
	private void insertRowIntoProcessLogTable(SimpleProcessPerformer<?,?,?> spp) throws SQLException {
		List<String> columnNameList = new ArrayList<>(this.getInitializedInsertPreparedStatementColumnNameListForSimpleProcess());
		// add the process type entity column specific to the given
		// SimpleProcessPerformer
		String processTypeEntityColName = ProcessLogTableSchemaUtils.getProcessTypeEntityColumnName(spp.getProcessEntity()).getStringValue();
		columnNameList.add(processTypeEntityColName);
		
		//set the value of columns in columnNameList except for the processTypeEntityColName
		PreparedStatement ps = 
				this.makeInitializedInsertPreparedStatement(
						spp.getProcessEntity().getID(), 
						spp.getProcessTypeManager().isOfReproduceableProcessType(),
						columnNameList);
		
		// process entity column
		ps.setObject(columnNameList.size(), spp.getProcessEntity());
		
		/////////
		ps.execute();
		
		
		//TODO invoke Runnables for specific types of process
		if(this.processInsertedOrStatusColumnChangeEventRunnableSet!=null)
			this.processInsertedOrStatusColumnChangeEventRunnableSet.forEach(r->{
				Platform.runLater(r);
			});
		
		
		Object processEntity = spp.getProcessEntity();
		
		if(processEntity instanceof CompositionFunction && this.CFTypeProcessInsertedOrStatusColumnChangeEventRunnableSet!=null)
			this.CFTypeProcessInsertedOrStatusColumnChangeEventRunnableSet.forEach(r->{
				Platform.runLater(r);
			});
		
		if((processEntity instanceof DataImporter || processEntity instanceof Operation) && this.metadataProducingTypeProcessInsertedOrStatusColumnChangeEventRunnableSet!=null)
			this.metadataProducingTypeProcessInsertedOrStatusColumnChangeEventRunnableSet.forEach(r->{
				Platform.runLater(r);
			});
	}
	
	
	
	/**
	 * see {@link #getInitializedInsertPreparedStatementColumnNameListForSimpleProcess()};
	 */
	private List<String> initializedInsertPreparedStatementColumnNameListForSimpleProcess;
	
	
	/**
	 * build (if not already) and return initializedInsertPreparedStatementColumnNameListForSimpleProcess
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
	private List<String> getInitializedInsertPreparedStatementColumnNameListForSimpleProcess() {
		if (initializedInsertPreparedStatementColumnNameListForSimpleProcess == null) {
			this.initializedInsertPreparedStatementColumnNameListForSimpleProcess = new ArrayList<>();
			this.initializedInsertPreparedStatementColumnNameListForSimpleProcess.add(
					ProcessLogTableSchemaUtils.processIDColumn.getName().getStringValue());
			this.initializedInsertPreparedStatementColumnNameListForSimpleProcess.add(
					ProcessLogTableSchemaUtils.insertionProcessIDColumn.getName().getStringValue());
			this.initializedInsertPreparedStatementColumnNameListForSimpleProcess.add(
					ProcessLogTableSchemaUtils.isReproducedColumn.getName().getStringValue());
			this.initializedInsertPreparedStatementColumnNameListForSimpleProcess.add(
					ProcessLogTableSchemaUtils.processStatusColumn.getName().getStringValue());
			
			this.initializedInsertPreparedStatementColumnNameListForSimpleProcess.add(
					ProcessLogTableSchemaUtils.baseProcessIDSetColumn.getName().getStringValue());
			this.initializedInsertPreparedStatementColumnNameListForSimpleProcess.add(
					ProcessLogTableSchemaUtils.insertedProcessIDSetColumn.getName().getStringValue());
			this.initializedInsertPreparedStatementColumnNameListForSimpleProcess.add(
					ProcessLogTableSchemaUtils.insertedNonProcessIDSetColumn.getName().getStringValue());
			this.initializedInsertPreparedStatementColumnNameListForSimpleProcess.add(
					ProcessLogTableSchemaUtils.involvedCfTargetValueTableRunIDSetColumn.getName().getStringValue());
		}
		
		return this.initializedInsertPreparedStatementColumnNameListForSimpleProcess;
	}
	
	/**
	 * create and return an initialized PreparedStatement for inserting a new process into the process log table for the given {@link PrimaryKeyID}
	 * only applicable for SimpleProcessPerformer but NOT for VisSchemeAppliedArchiveReproducerAndInserter;
	 * 
	 * @param spp
	 * @return
	 * @throws SQLException
	 */
	private PreparedStatement makeInitializedInsertPreparedStatement(
			PrimaryKeyID<? extends VisframeUDT> id, 
			boolean ofReproduceableProcessType, //whether the process is of ReproduceableProcessType
			List<String> initializedInsertPreparedStatementColumnNameList) throws SQLException {
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
		if(ofReproduceableProcessType && 
				this.getProcessLogTableManager().getVSAArchiveReproducerAndInserterManager().getCurrentlyRunningVSAReproducerAndInserterProcessUID()!=null &&
				this.getProcessLogTableManager().getVSAArchiveReproducerAndInserterManager().getCurrentlyRunningVSAArchiveReproducerAndInserterProcessStatus().equals(StatusType.RUNNING)) {
			ps.setObject(
					initializedInsertPreparedStatementColumnNameList.indexOf(ProcessLogTableSchemaUtils.insertionProcessIDColumn.getName().getStringValue()) + 1, 
					this.getProcessLogTableManager().getVSAArchiveReproducerAndInserterManager().getCurrentlyRunningVSAReproducerAndInserterProcessID());
		}else {
			ps.setObject(
					initializedInsertPreparedStatementColumnNameList.indexOf(ProcessLogTableSchemaUtils.insertionProcessIDColumn.getName().getStringValue()) + 1, 
					id);
		}
		
		//isReproducedColumn
		//must be set for ReproduceableProcessType process
		if(ofReproduceableProcessType) {
			ps.setBoolean(
					initializedInsertPreparedStatementColumnNameList.indexOf(ProcessLogTableSchemaUtils.isReproducedColumn.getName().getStringValue()) + 1,
					this.getProcessLogTableManager().getVSAArchiveReproducerAndInserterManager().getCurrentlyRunningVSAReproducerAndInserterProcessUID()!=null &&
						this.getProcessLogTableManager().getVSAArchiveReproducerAndInserterManager().getCurrentlyRunningVSAArchiveReproducerAndInserterProcessStatus().equals(StatusType.RUNNING));
		}else {
			ps.setObject( //TODO cannot set null for boolean column????
					initializedInsertPreparedStatementColumnNameList.indexOf(ProcessLogTableSchemaUtils.isReproducedColumn.getName().getStringValue()) + 1,
					null);
		}
		
		// processStatusColumn
		ps.setString(
				initializedInsertPreparedStatementColumnNameList.indexOf(ProcessLogTableSchemaUtils.processStatusColumn.getName().getStringValue()) + 1,
				StatusType.RUNNING.toString());
		
		//baseProcessIDSetColumn
		ps.setObject(
				initializedInsertPreparedStatementColumnNameList.indexOf(ProcessLogTableSchemaUtils.baseProcessIDSetColumn.getName().getStringValue()) + 1,
				new VfIDCollection());
		
		//insertedProcessIDSetColumn
		//must be null for non-VisSchemeAppliedArchiveReproducedAndInsertedInstance type (simple) process
		ps.setObject(
				initializedInsertPreparedStatementColumnNameList.indexOf(ProcessLogTableSchemaUtils.insertedProcessIDSetColumn.getName().getStringValue()) + 1,
				null);
		
		// insertedNonProcessIDSetColumn
		ps.setObject(
				initializedInsertPreparedStatementColumnNameList.indexOf(
						ProcessLogTableSchemaUtils.insertedNonProcessIDSetColumn.getName().getStringValue()) + 1,
				new VfIDCollection());
		
		
		//involvedCfTargetValueTableRunIDSetColumn
		//only be set to an empty set if the process is of VisInstanceRun type
		//otherwise, do nothing(thus null value)
		if(id instanceof VisInstanceRunID) {
			ps.setObject(initializedInsertPreparedStatementColumnNameList.indexOf(
					ProcessLogTableSchemaUtils.involvedCfTargetValueTableRunIDSetColumn.getName().getStringValue()) + 1,
					new VfIDCollection());
		}else {
			ps.setObject(initializedInsertPreparedStatementColumnNameList.indexOf(
					ProcessLogTableSchemaUtils.involvedCfTargetValueTableRunIDSetColumn.getName().getStringValue()) + 1,
					null);
		}
		
		////
		return ps;
		
	}
	
	///////////////////////////////////////////////////////////
	/**
	 * discard the most recently running simple process before it gets into the stage that produce data in the rdb;
	 * note that there is no following procedure that rolls back any produced data in the rdb after this method in the invoker method, 
	 * thus invoker of this method should ensure there is no data produced;
	 * otherwise, use the {@link #abortCurrentRunningSimpleProcess};
	 * 
	 * =================
	 * if {@link #mostRecentRunningSimpleProcessStatus} is not RUNNING, throw VisframeException;
	 * 
	 * 1. set the status column of currently running simple process to DISCARDED in PROCESS LOG table; 
	 * 2. set the {@link #mostRecentRunningSimpleProcessStatus} to DISCARDED;
	 * 
	 * @throws SQLException
	 */
	public void discardCurrentRunningSimpleProcess() throws SQLException {
		if (this.getMostRecentlyRunningSimpleProcessStatus() != StatusType.RUNNING) {
			throw new VisframeException("mostRecentNewProcess not running");
		}
		
		// 1
		this.setProcessStatusInLogTable(this.mostRecentRunningSimpleProcessUID, StatusType.DISCARDED);
	}
	
	/**
	 * abort the currently running simple process immediately, then roll back it; 
	 * 
	 * this method is different from {@link #discardCurrentRunningProcess()} method in that this method will roll back the process, 
	 * thus the status of the process will become ROLLEDBACK in the end;
	 * 
	 * if {@link #mostRecentRunningSimpleProcessStatus} is not RUNNING, throw VisframeException;
	 * 1. abort the running simple process 
	 * 2. set the currently running simple process's status to ABORTED in PROCESS LOG table; 
	 * 3. set the {@link #mostRecentRunningSimpleProcessStatus} to ABORTED;
	 * 4. invoke the {@link ProcessLogTableAndProcessPerformerManager#rollbackProcess(int)} method to roll back the process;
	 * 
	 * 
	 * @throws SQLException
	 */
	public void abortAndRollbackCurrentRunningSimpleProcess() throws SQLException {
		// 1
		if (this.getMostRecentlyRunningSimpleProcessStatus() != StatusType.RUNNING) {
			throw new VisframeException("mostRecentNewProcess not running");
		}
		
		// 2
//		this.getProcessLogTableManager().getExecutorService().shutdown();
//		this.getProcessLogTableManager().setExecutorService(null);
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
		
		// 3
		this.setProcessStatusInLogTable(this.mostRecentRunningSimpleProcessUID, StatusType.ABORTED);
		
		//4. roll back
		this.getProcessLogTableManager().rollbackProcess(this.mostRecentRunningSimpleProcessUID);
	}
	
	
	
	////////////////////////////////

	/**
	 * @return the mostRecentRunningSimpleProcessUID
	 */
	public Integer getMostRecentRunningSimpleProcessUID() {
		return mostRecentRunningSimpleProcessUID;
	}

	
//	/**
//	 * @return the mostRecentlyRunningSimpleProcessPerformerTracker
//	 */
//	public CallableStatusTracker3<StatusType> getMostRecentlyRunningSimpleProcessPerformerTracker() {
//		return mostRecentlyRunningSimpleProcessPerformerTracker;
//	}
//	
//
//	public Set<Throwable> getMostRecentNewSimpleProcessExceptionSet() {
//		return mostRecentRunningSimpleProcessExceptionSet;
//	}
//	
//	public void setMostRecentNewSimpleProcessExceptionSet(Set<Throwable> set) {
//		this.mostRecentRunningSimpleProcessExceptionSet = set;
//	}


	/**
	 * @return the mostRecentRunningSimpleProcessID
	 */
	public PrimaryKeyID<?> getMostRecentRunningSimpleProcessID() {
		return mostRecentRunningSimpleProcessID;
	}

}
