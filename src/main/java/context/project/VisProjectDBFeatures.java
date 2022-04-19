package context.project;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

import basic.SimpleName;
import basic.VfNotes;
import basic.lookup.project.VisProjectHasIDTypeManagerController;
import context.project.process.logtable.ProcessLogTableAndProcessPerformerManager;
import context.project.process.logtable.StatusType;
import dependency.cfd.SimpleCFDGraph;
import dependency.dos.SimpleDOSGraph;


/**
 * features that are specific to VisProjectDBContext but absent for VisScheme;
 * 
 * @author tanxu
 */
public interface VisProjectDBFeatures {
	/**
	 * return the path of the directory where the directory of the VisProjectDBContext is residing;
	 * 
	 * the full path of the project directory = parent path\\project name
	 * the full path of the rdb folder of a project is composed of parent path + name of the project;
	 * 
	 * @return
	 */
	Path getProjectParentDirectoryPath();
	
	/**
	 * set the name of the project
	 * @param newName
	 */
	void setName(SimpleName newName);
	
	/**
	 * set the notes of the project
	 * @param newNotes
	 */
	void setNotes(VfNotes newNotes);
	
	/**
	 * return the parent path for the RDB of the project; 
	 * 
	 * @return
	 */
	Path getDBParentPath();
	
	/**
	 * create(if not yet) and return the Connection of the project RDB;
	 * 
	 * @return
	 * @throws SQLException 
	 */
	Connection getDBConnection() throws SQLException;
	
	
	/**
	 * connect to the rdb;
	 * 1. make Connection object
	 * 2. check DB, if first time (no visframe DB schema found)
	 * 		initialize the DB with a VisProjectDBInitializer object
	 * 3. if reconnected a previously created DB
	 * 		invoke {@link #checkDBIntegrity()};
	 * 		then invoke {@link #checkDataConsistency()};
	 * 		
	 * 4. create project CFD graph by invoking {@link #updateCFDGraph()}
	 * 
	 * 5. retrieve the VfNotes of this project;
	 * @throws SQLException 
	 */
	void connect() throws SQLException;
	
	
	/**
	 * check if the basic features including db schema, UDT, management tables are not missing;
	 * invoked every time the previously created DB was reconnected;
	 * if any missing, throw corresponding VisframeException;
	 * 
	 * {@link VisProjectDBSchemaBuilder#allExist()}
	 * {@link VisProjectDBUDTBuilder#allExist()}
	 * {@link VisProjectManagementTableBuilder#allExist()}
	 * 
	 */
	void checkDBIntegrity();
	
	
	/**
	 * check the consistency of data in the rdb of the Visframe project;
	 * 
	 * this is a pre-process step every time the rdb of the visframe project is reconnected;
	 * 
	 * this method will trigger a comprehensive scrutiny(deep scan) on every existing data/entity to check if their directly depended entity/data exists;
	 * 
	 * =====================================
	 * basic implementation:
	 * 		for each process in log table sorted by start time (with ASC sort order)
	 * 			if StatusType is done //{@link StatusType#isDone()}
	 * 				1. FINISHED
	 * 					check if process exists in management table
	 * 					if not, roll back 
	 * 					(theoretically not possible based on current design of visframe, skip)
	 * 				2. ROLLEDBACK
	 * 					check if process exists in management table
	 * 					if yes, roll back
	 * 					(theoretically not possible based on current design of visframe, skip)
	 * 				3. DISCARDED
	 * 					check if process exists in management table
	 * 					if yes, roll back
	 * 					(theoretically not possible based on current design of visframe, skip)
	 * 			else//RUNNING, ROLLINGBACK or ABORTED
	 * 				roll back;
	 * 
	 * reasoning behind the implementation strategy:
	 * 		since every VisframeUDT entity in rdb is inserted by a specific process in the process log table, thus the above strategy should be sufficient to identify all data inconsistency and fix them;
	 * 		as long as the implementation strategy of performing a process and insertion/deletion of an entity to/from management table or rdb schema is designed with respect to the implicit dependency between entities, there is no need to explicitly consider every possible combination of data inconsistency;
	 * 			for example, a data table in DATA schema without an owner record Metadata is not possible if 
	 * 			1. when performing a process that result in a record Metadata, 
	 * 					first insert the Metadata into the management table, then create the data table schema and populate it;
	 * 			2. when deleting a Metadata (triggered by rolling back of the insertion process),
	 * 					first delete the data table from DATA schema then delete the Metadata from the management table;
	 * =======================================
	 * more implementation required:
	 * 
	 * however, some of the process are performed alternatively each with their specific strong reasons
	 * 		for example, when importing a record data file, the data table is inserted before the record metadata, 
	 * 		which contradicts the above assumptions and makes it possible that a data table is not owned by any record Metadata (crash occurs between the two steps);
	 * 
	 * 		for CFTargetValueTableRun, the cf target value table is inserted before it is logged to the Involved cftarget value table run column?????
	 * ========================================
	 * in sum, how to perform the deep scan of data consistency is determined by 
	 * 1. how the performing of a processes is implemented in terms of the insertion order of involved data/entities and 
	 * 2. how deletion of an entity is implemented in terms of the deletion order of involved data/entities
	 * 
	 * 
	 * @throws SQLException 
	 */
	void checkDataConsistency() throws SQLException;
	
	
	/**
	 * return the up-to-date SimpleCFDGraph based on current set of CompositionFunctions in the management table;
	 * 
	 * @return
	 * @throws SQLException 
	 */
	SimpleCFDGraph getCFDGraph() throws SQLException;
	
	/**
	 * return the up-to-date SimpleDOSGraph based on current set of Metadata in the management table;
	 * 
	 * @return
	 * @throws SQLException 
	 */
	SimpleDOSGraph getDOSGraph() throws SQLException;
	
	
	////////////////////////
	/**
	 * retrieve notes from the Project_Infor table
	 */
	void retrieveNotes();
	
	
	/**
	 * disconnect the Connection to the rdb of this VisProjectDBContext;
	 * close the DB {@link Connection}
	 * 
	 * 
	 * note that this does not check if any process is not done yet, 
	 * to do so, invoke the {@link #allProcessesAreDone()} first, if yes, either abort it and invoke this method or wait until it finishes before invoke this method;
	 * ????
	 * 
	 * @throws SQLException 
	 * 
	 */
	void disconnect() throws SQLException;
	
	/**
	 * check if there is a process currently running;
	 * @return
	 * @throws SQLException 
	 */
	default boolean allProcessesAreDone() throws SQLException {
		return this.getProcessLogTableAndProcessPerformerManager().getSimpleProcessManager().allSimpleProcessesAreDone() && 
				this.getProcessLogTableAndProcessPerformerManager().getVSAArchiveReproducerAndInserterManager().allVSAArchiveReproducerAndInserterProcessesAreDone();
	}
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * returns a factory object that maintains a singleton object for manager class of all the {@link HasID} types in VisProjectDBContext;
	 * 
	 * all the basic operations such as insert/lookup/delete of a row in the management tables are done by first get the Manager object and then invoke the corresponding method of the Manager class
	 * @return
	 */
	VisProjectHasIDTypeManagerController getHasIDTypeManagerController();
	
	/**
	 * return a singleton ProcessLogTableManager object of this VisProjectDBFeatures;
	 * @return
	 */
	ProcessLogTableAndProcessPerformerManager getProcessLogTableAndProcessPerformerManager();
	
	/**
	 * roll back the finished process of the given process UID in LOG table;
	 * 
	 * which will also roll back any directly and indirectly dependent processes of this process if any;
	 * 
	 * note that a roll back can only be triggered starting from either 
	 * 		1. a {@link NonReproduceableProcessType} process or 
	 * 		2. a {@link ReproduceableProcessType} that is not reproduced by a {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance}
	 * 
	 * if the given processUID is a {@link ReproduceableProcessType} process reproduced by a {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance},
	 * throw VisframeException;
	 * 
	 * 
	 * invoke {@link ProcessLogTableAndProcessPerformerManager#rollbackProcess(int)}
	 * 
	 * note that this method differs from the {@link ProcessLogTableAndProcessPerformerManager#rollbackProcess(int)} in that
	 * 1. this method can only be used to roll back finished non-reproduced process;
	 * 2. the {@link ProcessLogTableAndProcessPerformerManager#rollbackProcess(int)} can roll back any process whether it is finished or not or reproduced or not;
	 * 
	 * @param processUID
	 * @throws SQLException
	 */
	void rollbackFinishedProcess(int processUID) throws SQLException;
	
}
