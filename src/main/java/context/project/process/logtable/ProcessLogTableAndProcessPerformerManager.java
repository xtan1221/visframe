package context.project.process.logtable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import basic.lookup.HasID;
import basic.lookup.ID;
import basic.lookup.PrimaryKeyID;
import basic.lookup.VisframeUDT;
import basic.lookup.project.type.VisframeUDTManagementTableRow;
import basic.lookup.project.type.VisframeUDTTypeManagerBase;
import basic.lookup.project.type.udt.VisProjectCFTargetValueTableRunManager;
import context.project.VisProjectDBContext;
import context.project.VisProjectDBFeatures;
import context.project.process.SimpleProcessPerformer;
import context.project.process.manager.SimpleProcessManager;
import context.project.process.manager.VisSchemeAppliedArchiveReproducingAndInsertionManager;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstance;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstanceID;
import exception.IDNotFoundException;
import exception.VisframeException;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;
import sql.derby.TableSchemaUtils;
import visinstance.VisInstance;
import visinstance.run.VisInstanceRunID;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunID;

/**
 * manager class that manages process Log table and process running in a host VisProjectDBContext;
 * 
 * @author tanxu
 *
 */
public class ProcessLogTableAndProcessPerformerManager {

	/////////////////////////
	private final VisProjectDBFeatures hostVisProjectDBFeatures;
	
	/////////////////////////////////////////////
	/**
	 * the ExecutorService that runs the {@link SimpleProcessPerformer}; also used
	 * to abort currently running {@link SimpleProcessPerformer};
	 * 
	 * ExecutorService allows multiple tasks to run at the same time (concurrency);
	 * however, current Visframe do not utilize this feature (to be added in future
	 * updates); ExecutorService is mainly used to shutdown running task that is
	 * stuck or taking too long time; thus, there should be at most one task running
	 * in the ExecutorService at a time; when a new task is to be submitted to
	 * ExecutorService, need to check if any task is still running; if yes, do not
	 * allow the new task; see {@link #startNewTask(Runnable)} when a task is done
	 * (completed or aborted) need to set the status of the ExecutorService to idle
	 * (implementation in each Runnable class); see {@link #freeExecutorService()}
	 */
	private ExecutorService executorService;
	
	/**
	 * sql string that select all rows from the process log table sorted by start
	 * time column; facilitate querying previous processes in the log table;
	 */
	private String selectAllOrderedByStartTimeDESCSqlString;
	
	/////////////////////////
	private SimpleProcessManager simpleProcessManager;
	
	private VisSchemeAppliedArchiveReproducingAndInsertionManager VSAArchiveReproducerAndInserterManager;
	
	//////////////////////////////////////
	/**
	 * constructor
	 */
	public ProcessLogTableAndProcessPerformerManager(VisProjectDBFeatures hostVisProjectDBFeatures) {
		this.hostVisProjectDBFeatures = hostVisProjectDBFeatures;
		
		///
		this.simpleProcessManager = new SimpleProcessManager(this);
		this.VSAArchiveReproducerAndInserterManager = new VisSchemeAppliedArchiveReproducingAndInsertionManager(this);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public VisProjectDBFeatures getHostVisProjectDBContext() {
		return hostVisProjectDBFeatures;
	}

	/**
	 * @return the simpleProcessManager
	 */
	public SimpleProcessManager getSimpleProcessManager() {
		return simpleProcessManager;
	}

	/**
	 * @return the vSAArchiveReproducerAndInserterManager
	 */
	public VisSchemeAppliedArchiveReproducingAndInsertionManager getVSAArchiveReproducerAndInserterManager() {
		return VSAArchiveReproducerAndInserterManager;
	}
	

	///////////////////////////////////////
	/**
	 * return the ExecutorService object //if a null is returned, it indicates the
	 * current running process is aborted?????
	 * 
	 * @return
	 */
	public ExecutorService getExecutorService() {
		if (this.executorService == null) {
			this.executorService = Executors.newFixedThreadPool(10);
		}
		return this.executorService;
	}

//	/**
//	 * @param executorService the executorService to set
//	 */
//	public void setExecutorService(ExecutorService executorService) {
//		this.executorService = executorService;
//	}
	
	
	//////////////////////////////
	
	/**
	 * ======111720-update after VisSchemeAppliedArchiveReproducedAndInsertedInstance is added
	 * 
	 * roll back the process of the given process UID in LOG table; 
	 * which will also roll back any directly and indirectly dependent processes of this process if any;
	 * 
	 * this method should be distinguished from the {@link VisProjectDBContext#rollbackFinishedProcess(int)}!!!
	 * 
	 * implementation strategy: 
	 * 1. retrieve the ProcessLogTableRow in Process LOG table with the given UID, if not found, throw visframeException
	 * 		{@link ProcessLogTableAndProcessPerformerManager#retrieveRow(int)}
	 * 
	 * 2. If the status is not rollbackable, return; else, continue;
	 * 		{@link StatusType#isRollbackable()} 
	 * 
	 * 3. set the status to {@link StatusType#ROLLINGBACK}
	 * 		mark the process first, thus if crash occurs in following steps before the rolling back is successfully finished, the engine still knows this process need to be rolled back next time it is reconnected;
	 * 		{@link #setProcessStatusInLogTable(int, StatusType)}
	 * 
	 * 4. roll back dependent processes
	 * 		retrieve the {@link VisframeUDTManagementTableRow} of the process from the management table with {@link VisframeUDTTypeManagerBase#retrieveRow(ID)};
	 * 		1. If not found, skip this step
	 * 			(crash occurred last time after step of deleting the process entity from management table) 
	 * 		2. Else, for each ID in the dependentProcessIDSet column of the VisframeUDTManagementTableRow
	 * 			{@link VisframeUDTManagementTableRow#getDependentProcessIDSet()} method
	 * 				1. find out the rows with the ID and rollbackable status in the process log table; 
	 * 					note that dependent process status could be of RUNNING, ABORTED, ROLLINGBACK or FINISHED in the process log table
	 * 					note that there should be one such row for each ID (there might be other rows with the same ID but with different status(ROLLEDBACK|DISCARDED), which are irrelevant); 
	 * 				2. Find out the UID of the process
	 * 				3. Invoke the {@link #rollbackProcess(int)} method;
	 * 				4. catch and ignore any thrown IDNotFoundException;
	 * 
	 * 5. Roll back inserted VisframeUDT entities
	 * 		1. for {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance} process, 
	 * 			for each ID in insertedProcessIDSetColumn in the process log table
	 * 				{@link ProcessLogTableRow#getInsertedProcessIDSet()}
	 * 				1. identify the row in the process log table and find out the process UID;
	 * 				2. roll back the reproduced and inserted ReproduceableProcessType entities by recursively invoking the {@link #rollbackProcess(int)} method with the process UID;
	 * 				Entity might be absent if crash occurred last time during or after this step, thus catch and ignore any IDNotFoundException; 
	 * 		
	 * 		2. for {@link VisInstance} process,
	 * 			for each ID in involvedCfTargetValueTableRunIDSetColumn retrieved from the LOG table;
	 * 				{@link ProcessLogTableRow#getInvolvedCFTargetValueTableRunIDSet()}; 
	 * 			invoke the {@link VisProjectCFTargetValueTableRunManager#removeEmployerVisInstanceRunID(CFTargetValueTableRunID, VisInstanceRunID)} method;
	 * 			Entity might be absent if crash occurred last time during or after this step, thus catch and ignore any IDNotFoundException; 
	 * 		
	 * 		3. for all process types except for VisSchemeAppliedArchiveReproducedAndInsertedInstance and VisInstance,
	 * 			invoke the {@link VisframeUDTTypeManagerBase#delete(ID)} of every ID in the insertedNonProcessIDSetColumn retrieved from the LOG table;
	 * 				{@link ProcessLogTableRow#getInsertedNonProcessIDSet()}
	 * 				Entity might be absent if crash occurred last time during or after this step, thus catch and ignore any IDNotFoundException;
	 * 
	 * 6. Delete the rolled back process entity from the management table 
	 * 		{@link VisframeUDTTypeManagerBase#delete(ID)}; 
	 * 		Might be absent if crash occurred last time after this step, thus catch and ignore any thrown IDNotFoundException; 
	 * 
	 * 7. for ReproduceableProcessType process reproduced and inserted by a VisSchemeAppliedArchiveReproducerAndInserter (TODO re-check)
	 * 		remove the process ID from the insertedProcessIDSet column of the VisSchemeAppliedArchiveReproducerAndInserter in the process log table;
	 * 			the status of the VisSchemeAppliedArchiveReproducerAndInserter process might be 
	 *			1. RUNNING
	 *				the reproduced and inserted process entity is rolled back during building a VisSchemeAppliedArchiveReproducedAndInsertedInstance
	 *				for example, an inserted Operation is rolled back;
	 *				need to update the insertedProcessID set column of this running VisSchemeAppliedArchiveReproducerAndInserter process in process log table!
	 *			2. ROLLINGBACK
	 *				the VisSchemeAppliedArchiveReproducedAndInsertedInstance is being rolling back (either from FINISHED status or aborted)
	 *				there is no need to update insertedProcessID set column of this VisSchemeAppliedArchiveReproducerAndInserter process in process log table!!!
	 * 					since all will be rolled back!
	 * 
	 * 8. Set the status of the process to ROLLEDBACK in process log table;
	 * 		ROLLEDBACK indicate the process is successfully rolled back and all directly or indirectly dependent processes are also ROLLEDBACK;
	 * 
	 * @param processUID
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public void rollbackProcess(int processUID) throws SQLException {
		// 1
		ProcessLogTableRow processLogTableRow = this.retrieveRow(processUID);
		
		if (processLogTableRow == null) {
			throw new VisframeException("PROCESS UID is not found in the PROCESS LOG table");
		}
		
		// 2
		if (!processLogTableRow.getStatus().isRollbackable()) {
			return;
		}
		
		// 3
		if(processLogTableRow.getProcessID() instanceof VisSchemeAppliedArchiveReproducedAndInsertedInstanceID) {
			this.getVSAArchiveReproducerAndInserterManager().setProcessStatusInLogTable(processUID, StatusType.ROLLINGBACK);
		}else {
			this.getSimpleProcessManager().setProcessStatusInLogTable(processUID, StatusType.ROLLINGBACK);
		}
		
		// 4
		VisframeUDTManagementTableRow<?, ?> managementTableRow = 
				this.getHostVisProjectDBContext().getHasIDTypeManagerController().retrieveManagementTableRow(processLogTableRow.getProcessID());
		if (managementTableRow == null) {
			// skip
		} else {
			//roll back all dependent process in the DependentProcessIDSet column of the management table
			for(ID<? extends HasID> id : managementTableRow.getDependentProcessIDSet().getAllIDs()) {
				try {
					// SELECT * FROM log table ORDERED BY start_time
					Statement statement = this.hostVisProjectDBFeatures.getDBConnection().createStatement();
					ResultSet rs = statement.executeQuery(this.getSelectAllOrderedByStartTimeDESCSqlString());
					
					// find out the row with the process ID and rollbackable status (there should be either none or one)
					while (rs.next()) {
						int UID = rs.getInt(ProcessLogTableSchemaUtils.UIDColumn.getName().getStringValue());
						PrimaryKeyID<? extends VisframeUDT> processID = 
								(PrimaryKeyID<? extends VisframeUDT>) rs.getObject(ProcessLogTableSchemaUtils.processIDColumn.getName().getStringValue());
						StatusType status = 
								StatusType.valueOf(rs.getString(ProcessLogTableSchemaUtils.processStatusColumn.getName().getStringValue()));
						
						if(processID.equals(id) && status.isRollbackable()) {
							this.rollbackProcess(UID);
							break;
						}
					}
				} catch (IDNotFoundException e) { // catch and ignore any thrown IDNotFoundException; either crashed from last time or rolled back by a depended process that is also in the insertedProcessIDSetColumn of the VisSchemeAppliedArchiveReproducedAndInsertedInstance; 
//							e.printStackTrace();
				}
			}
			
		}
		
		
		// 5 Roll back inserted VisframeUDT entities
		if(processLogTableRow.getProcessID() instanceof VisSchemeAppliedArchiveReproducedAndInsertedInstanceID) {
			//note that since the reproduced and inserted ReproduceableProcessType may depends on each other, thus roll back of some will automatically result in roll back of other;
			for (ID<? extends HasID> id : processLogTableRow.getInsertedProcessIDSet().getAllIDs()) {
				try {
					// SELECT * FROM log table ORDERED BY start_time
					Statement statement = this.hostVisProjectDBFeatures.getDBConnection().createStatement();
					ResultSet rs = statement.executeQuery(this.getSelectAllOrderedByStartTimeDESCSqlString());
					
					// find out the row with the process ID and rollbackable status (there should be either none or one)
					while (rs.next()) {
						int UID = rs.getInt(ProcessLogTableSchemaUtils.UIDColumn.getName().getStringValue());
						PrimaryKeyID<? extends VisframeUDT> processID = 
								(PrimaryKeyID<? extends VisframeUDT>) rs.getObject(ProcessLogTableSchemaUtils.processIDColumn.getName().getStringValue());
						StatusType status = 
								StatusType.valueOf(rs.getString(ProcessLogTableSchemaUtils.processStatusColumn.getName().getStringValue()));
						
						if(processID.equals(id) && status.isRollbackable()) {
							this.rollbackProcess(UID);
							break;
						}
					}
				} catch (IDNotFoundException e) { // catch and ignore any thrown IDNotFoundException; either crashed from last time or rolled back by a depended process that is also in the insertedProcessIDSetColumn of the VisSchemeAppliedArchiveReproducedAndInsertedInstance; 
//					e.printStackTrace();
				}
			}
		}else if(processLogTableRow.getProcessID() instanceof VisInstanceRunID) {//
			for (ID<? extends HasID> id : processLogTableRow.getInvolvedCFTargetValueTableRunIDSet().getAllIDs()) {
				try {
					this.getHostVisProjectDBContext().getHasIDTypeManagerController().getCFTargetValueTableRunManager().removeEmployerVisInstanceRunID(
							(CFTargetValueTableRunID) id, 
							(VisInstanceRunID) processLogTableRow.getProcessID()
							);
				} catch (IDNotFoundException e) { // catch and ignore any thrown IDNotFoundException
//					e.printStackTrace();
				}
			}
		}else {//
			//try to delete the inserted non process id if exist;
			for (ID<? extends HasID> id : processLogTableRow.getInsertedNonProcessIDSet().getAllIDs()) {
				try {
					this.getHostVisProjectDBContext().getHasIDTypeManagerController().getManager(id).delete(id);
				} catch (IDNotFoundException e) { // catch and ignore any thrown IDNotFoundException
//					e.printStackTrace();
				}
			}
		}
		
		
		
		// 6
		try {
			this.getHostVisProjectDBContext().getHasIDTypeManagerController().delete(processLogTableRow.getProcessID());
		} catch (IDNotFoundException e) { // catch and ignore any thrown IDNotFoundException
			e.printStackTrace();
		}
		
		
		//7 ReproduceableProcessType process reproduced and inserted by a VisSchemeAppliedArchiveReproducerAndInserter
		//the status of the VisSchemeAppliedArchiveReproducerAndInserter process might be 
		//1. RUNNING
				//the process entity is rolled back during building a VisSchemeAppliedArchiveReproducedAndInsertedInstance
				//for example, an inserted Operation is rolled back;
				//need to update the insertedProcessID set column of this running VisSchemeAppliedArchiveReproducerAndInserter process in process log table
		//2. ROLLINGBACK
				//the VisSchemeAppliedArchiveReproducedAndInsertedInstance is being rolling back
				//there is no need to update insertedProcessID set column of this VisSchemeAppliedArchiveReproducerAndInserter process in process log table!!!
		if(processLogTableRow.getReproduced()!=null && processLogTableRow.getReproduced()) {//
			if(this.getVSAArchiveReproducerAndInserterManager().getCurrentlyRunningVSAReproducerAndInserterProcessUID()!=null && 
					this.getVSAArchiveReproducerAndInserterManager().getCurrentlyRunningVSAArchiveReproducerAndInserterProcessStatus().equals(StatusType.RUNNING)) {
				this.getVSAArchiveReproducerAndInserterManager().removeFromInsertedProcessIDSetColumnOfCurrentlyRunningVSAArchiveReproducerAndInserterProcess(processLogTableRow.getProcessID());
			}
		}
		
		// 8
		if(processLogTableRow.getProcessID() instanceof VisSchemeAppliedArchiveReproducedAndInsertedInstanceID) {
			this.getVSAArchiveReproducerAndInserterManager().setProcessStatusInLogTable(processUID, StatusType.ROLLEDBACK);
		}else {
			this.getSimpleProcessManager().setProcessStatusInLogTable(processUID, StatusType.ROLLEDBACK);
		}
	}
	
	/**
	 * roll back the finished process with the given PrimaryKeyID;
	 * if there is no such finished process in the process log table, throw exception;
	 * 
	 * delegate to {@link #rollbackProcess(int)} method
	 * @param processID
	 * @throws SQLException
	 */
	public void rollbackProcess(PrimaryKeyID<?> processID) throws SQLException {
		Integer processUID = this.getFinishedProcessUID(processID);
		
		if(processUID==null)
			throw new VisframeException("given processID is not found finished in process log table!");
		
		this.rollbackProcess(processUID);
		
	}
	//////////////////////////////////////////////////////
	/**
	 * build and return selectAllOrderedByStartTimeSqlString with DESC order so that
	 * the latest inserted process is the first row;
	 * 
	 * @return
	 */
	private String getSelectAllOrderedByStartTimeDESCSqlString() {
		if (this.selectAllOrderedByStartTimeDESCSqlString == null) {
			List<String> orderByColumnNameList = new ArrayList<>();
			List<Boolean> orderByASCList = new ArrayList<>();
			orderByColumnNameList.add(ProcessLogTableSchemaUtils.startTimeColumn.getName().getStringValue());
			orderByASCList.add(false);
			this.selectAllOrderedByStartTimeDESCSqlString = TableContentSQLStringFactory.buildSelectAllSQLString(
					SQLStringUtils.buildTableFullPathString(ProcessLogTableSchemaUtils.getTableSchema().getSchemaName(),
							ProcessLogTableSchemaUtils.getTableSchema().getName()),
					null, orderByColumnNameList, orderByASCList);
		}
		return selectAllOrderedByStartTimeDESCSqlString;
	}
	
	
	/**
	 * return the ResultSet that query for all rows in the process log table ordered by insertion time;
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getResultSetOfAllProcessOrderedByStartTimeDESC() throws SQLException {
		Statement statement = this.hostVisProjectDBFeatures.getDBConnection().createStatement();
		ResultSet rs = statement.executeQuery(this.getSelectAllOrderedByStartTimeDESCSqlString());
		return rs;
	}
	
	/////////////////////////////////////////////////////////////
	/**
	 * build and return the ProcessLogTableRow with the given UID; if not row with
	 * the UID, return null;
	 * 
	 * @param UID
	 * @return
	 * @throws SQLException
	 */
	public ProcessLogTableRow retrieveRow(int UID) throws SQLException {
		String sqlString = TableContentSQLStringFactory.buildSelectAllWithConditionString(
				SQLStringUtils.buildTableFullPathString(ProcessLogTableSchemaUtils.getTableSchema().getSchemaName(),
						ProcessLogTableSchemaUtils.getTableSchema().getName()),
				TableContentSQLStringFactory.buildColumnValueEquityCondition(
						ProcessLogTableSchemaUtils.UIDColumn.getName().getStringValue(), Integer.toString(UID),
						ProcessLogTableSchemaUtils.UIDColumn.getSqlDataType().isOfStringType(),
						null//toIgnoreCase 
						));
		
		Statement statement = this.getHostVisProjectDBContext().getDBConnection().createStatement();
		ResultSet rs = statement.executeQuery(sqlString);
		
		//
		if (!rs.next()) {// empty
			return null;
		}
		
		return ProcessLogTableSchemaUtils.retrieveRow(rs);
	}

	
	/**
	 * retrieve and return a full list of rows sorted by startTimeColumn(ASC sort
	 * order) in this process log table;
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<ProcessLogTableRow> retrieveAllAsList() throws SQLException {
		List<String> orderByColumnNameList = new ArrayList<>();
		orderByColumnNameList.add(ProcessLogTableSchemaUtils.startTimeColumn.getName().getStringValue());
		List<Boolean> orderByASCList = new ArrayList<>();
		orderByASCList.add(true);// ASC sort order
		
		String sqlString = TableContentSQLStringFactory.buildSelectAllSQLString(SQLStringUtils
				.buildTableFullPathString(ProcessLogTableSchemaUtils.getTableSchema().getSchemaName(), ProcessLogTableSchemaUtils.getTableSchema().getName()), null,
				orderByColumnNameList, orderByASCList);
		
//		System.out.println(sqlString);
		Statement statement = this.getHostVisProjectDBContext().getDBConnection().createStatement();
		ResultSet rs = statement.executeQuery(sqlString);

		List<ProcessLogTableRow> ret = new ArrayList<>();

		while (rs.next()) {
			ret.add(ProcessLogTableSchemaUtils.retrieveRow(rs));
		}

		return ret;
	}
	
	///////////////////////////////////////////////////////////
	
	/**
	 * check if there is a process with the given ID whose status is FINISHED in the log table or not;
	 * 
	 * if true, any new process with the same ID cannot be inserted and started;
	 * 
	 * note that it is allowed to insert new process with ID that is existing in the
	 * LOG table with status!=FINISHED;
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public boolean isProcessIDFinished(PrimaryKeyID<?> id) throws SQLException {
		String sqlString = 
				TableContentSQLStringFactory.buildSelectAllWithConditionString(
						SQLStringUtils.buildTableFullPathString(
								ProcessLogTableSchemaUtils.getTableSchema().getSchemaName(),ProcessLogTableSchemaUtils.getTableSchema().getName()),
						TableContentSQLStringFactory.buildColumnValueEquityCondition(
								ProcessLogTableSchemaUtils.processStatusColumn.getName().getStringValue(),
								StatusType.FINISHED.toString(),
								ProcessLogTableSchemaUtils.processStatusColumn.getSqlDataType().isOfStringType(),
								true//toIgnoreCase 
								)
				);
		
		Statement statement = this.hostVisProjectDBFeatures.getDBConnection().createStatement();

		ResultSet rs = statement.executeQuery(sqlString);
		
		while (rs.next()) {
			ProcessLogTableRow row = ProcessLogTableSchemaUtils.retrieveRow(rs);
			if (id.equals(row.getProcessID())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * find out and return the UID of the given ID of a process in the process log table with status = FINISHED;
	 * 
	 * if there is no such process, return null;
	 * 
	 * @param id
	 * @return
	 * @throws SQLException 
	 */
	public Integer getFinishedProcessUID(PrimaryKeyID<?> id) throws SQLException {
		ResultSet rs = this.getResultSetOfAllProcessOrderedByStartTimeDESC();
		
		while (rs.next()) {
			ProcessLogTableRow row = ProcessLogTableSchemaUtils.retrieveRow(rs);
			if (id.equals(row.getProcessID()) && row.getStatus().equals(StatusType.FINISHED)) {
				return row.getUID();
			}
		}
		
		return null;
	}
	

	
	//////////////////////////////////////////////
	/**
	 * check if the table schema exists in the DB or not
	 * 
	 * @return
	 * @throws SQLException
	 */
	public boolean doesTableExist() throws SQLException {
		return TableSchemaUtils.doesTableExists(this.getHostVisProjectDBContext().getDBConnection(),
				ProcessLogTableSchemaUtils.getTableSchema().getSchemaName(), ProcessLogTableSchemaUtils.getTableSchema().getName());
	}
	
	/**
	 * create the table schema in the host project DB
	 * 
	 * @throws SQLException
	 */
	public void createTableInHostProjectDB() throws SQLException {
		TableSchemaUtils.createTableSchema(this.getHostVisProjectDBContext().getDBConnection(), ProcessLogTableSchemaUtils.getTableSchema());
	}
}
