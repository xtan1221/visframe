package context.project.process;

import java.sql.SQLException;

import basic.lookup.ID;
import basic.lookup.PrimaryKeyID;
import basic.lookup.VisframeUDT;
import basic.lookup.project.VisProjectHasIDTypeManagerController;
import basic.lookup.project.type.VisframeUDTTypeManagerBase;
import context.project.VisProjectDBContext;
import context.project.process.logtable.Closable;
import context.project.process.logtable.ProcessLogTableAndProcessPerformerManager;
import context.project.process.logtable.VfIDCollection;
import context.project.process.manager.SimpleProcessManager;
import context.project.process.manager.VisSchemeAppliedArchiveReproducingAndInsertionManager;

/**
 * base class for insertion a {@link ProcessType} VisframeUDT entity into the host VisProjectDBContext;
 * 
 * 
 * @author tanxu
 * 
 * @param <T>
 * @param <I>
 */
public abstract class AbstractProcessPerformer<T extends VisframeUDT, I extends PrimaryKeyID<T>, M extends VisframeUDTTypeManagerBase<T,I>> implements Closable{
	private VisProjectDBContext hostVisProjectDBContext;
	/**
	 * the manager class for the type that is performed by this performer
	 */
	private M processTypeManager;
	
	/**
	 * base Process ID set for the process of this AbstractProcessPerformer;
	 * the ProcessType VisframeUDT ID set directly based on which this AbstractProcessPerformer could be performed;
	 */
	protected VfIDCollection baseProcessIDSet;
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	protected AbstractProcessPerformer(VisProjectDBContext hostVisProjectDBContext, M processTypeManager){
		this.hostVisProjectDBContext = hostVisProjectDBContext;
		this.processTypeManager=processTypeManager;
	}
	
	
	////////////////////////////////////////
	/**
	 * return the id of the process entity to be performed by this process performer
	 * @return
	 */
	public abstract I getID();
	
	/**
	 * check all constraints that can be done without entering the perform stage for the specific process type; throw VisframeException if any constraints is violated;
	 * must be invoked before the process entering perform stage;
	 * 		thus cannot perform any insertion in this step since the process is not started yet!;
	 * 
	 * note that some constraints can only be checked in the perform stage; the basic rule is try to perform as much constraint checking as possible in this step to save computational resources;
	 * 
	 * 
	 * common types of constraints:
	 * 1. Check whether the IDs to be inserted by the process (including the process itself) already exist with {@link VisframeUDTTypeManagerBase#checkIDExistence(I)} 
	 * 		note that the {@link ProcessLogTableAndProcessPerformerManager#isProcessIDFinished(PrimaryKeyID)} method only checks if the process is in the LOG table, it is possible it is still in the management table even not in LOG table (inserted as non process type entity for example); 
	 * 		for inserted non process IDs, this method is only relevant if ID of inserted data of this process is not enforced to be unique (for {@link VisSchemeReproducerUtils} and {@link VisInstanceRun}, unique IDs are automatically generated for the reproduced entities, thus no need to check this);
	 * 		if any duplicate ID found, set the status to DISCARDED and throw VisframeException;
	 * 2. check existence of all dependent entities in the DB (applicable for those that do not need to enter the perform stage);
	 * 3. check any other constraints on each specific type of process;
	 * @throws SQLException 
	 */
	public abstract void checkConstraints() throws SQLException;
	
	/**
	 * post process after the process entity is successfully inserted into the management table;
	 * 
	 * 1. Set the BASE_PROCESS_ID_SET column value of the 
	 * 		1. process log table and 
	 * 			for Simple process, invoke the {@link SimpleProcessManager#setBaseProcessIDSetColumnOfCurrentlyRunningSimpleProcess(VfIDCollection)} method
	 * 			for VisSchemeAppliedArchiveReproducerAndInserter, invoke {@link VisSchemeAppliedArchiveReproducingAndInsertionManager#setBaseProcessIDSetColumnOfCurrentlyRunningVSAArchiveReproducerAndInserterProcess(VfIDCollection)}
	 * 		2. the management table with {@link #baseProcessIDSet};
	 * 			{@link VisframeUDTTypeManagerBase#setBaseProcessIDSetColumn(PrimaryKeyID, VfIDCollection)};
	 * 		
	 * 2. Add the process ID to the DEPENDENT_PROCESS_ID set of every process in the {@link #baseProcessIDSet}
	 * 		{@link VisframeUDTTypeManagerBase#addDependentProcessID(PrimaryKeyID, PrimaryKeyID)};
	 * 
	 * 3. for process except for VisSchemeAppliedArchiveReproducerAndInserter and VisInstanceRun, 
	 * 		Add all IDs in INSERTED_NON_PROCESS_ID_SET column of LOG table to the INSERTED_NON_PROCESS_ID SET column of the management table for the process;
	 * 			{@link VisframeUDTTypeManagerBase#setInsertedNonProcessIDSetColumn(PrimaryKeyID, VfIDCollection)};
	 * 
	 * 4. for VisInstanceRun process, set the value of INVOLVED_CFTARGETVALUETABLERUN_ID_SET column
	 * 			{@link VisframeUDTTypeManagerBase#setInvolvedCFTargetValueTableRunIDSetColumn(PrimaryKeyID, VfIDCollection)};
	 * 
	 * 5. for VisSchemeAppliedArchiveReproducerAndInserter process type only,
	 * 		set the value of InsertedProcessIDSetColumn;
	 * 		{@link VisframeUDTTypeManagerBase#setInsertedProcessIDSetColumn(PrimaryKeyID, VfIDCollection)}
	 * 
	 * 6. for process except for VisSchemeAppliedArchiveReproducerAndInserter and VisInstanceRun, 
	 * 		Set the IS_TEMP column  value to false for all IDs in the INSERTED_NON_PROCESS_ID_SET column of the process log table;
	 * 		{@link VisframeUDTTypeManagerBase#formalize(ID)}
	 * 
	 * 7. for VisSchemeAppliedArchiveReproducerAndInserter process type only,
	 * 		Set the IS_TEMP column  value to false for all IDs in the INSERTED_PROCESS_ID_SET column of the process log table;
	 * 		{@link VisframeUDTTypeManagerBase#formalize(ID)}
	 * 
	 * 8. Set the IS_TEMP column  value to false for the process entity in its management table; 
	 * 		{@link VisframeUDTTypeManagerBase#formalize(ID)}
	 * 
	 * 
	 * 9. Set the status of process entity to FINISH in LOG table
	 * 		{@link AbstractProcessManager#setProcessStatusInLogTable(int, StatusType);}
	 * 
	 * @throws SQLException 
	 */
	public abstract void postprocess() throws SQLException;

	
	
	///////////////utility methods
	public VisProjectDBContext getHostVisProjectDBContext() {
		return hostVisProjectDBContext;
	}
	
	public M getProcessTypeManager() {
		return processTypeManager;
	}
	
	public VisProjectHasIDTypeManagerController getHasIDTypeManagerController() {
		return this.getHostVisProjectDBContext().getHasIDTypeManagerController();
	}

	/**
	 * @return the baseProcessIDSet
	 */
	public VfIDCollection getBaseProcessIDSet() {
		return baseProcessIDSet;
	}

	
	/**
	 * 
	 * @return
	 */
	protected ProcessLogTableAndProcessPerformerManager getProcessLogTableAndProcessPerformerManager() {
		return this.getHostVisProjectDBContext().getProcessLogTableAndProcessPerformerManager();
	}
	
	
	/**
	 * close this {@link #AbstractProcessPerformer} object so that it can no longer impact the original host VisProjectDBContext;
	 * invoked when this object is finished or aborted
	 */
	@Override
	public void close() {
		this.hostVisProjectDBContext = null;
		this.processTypeManager = null;
	}
}
