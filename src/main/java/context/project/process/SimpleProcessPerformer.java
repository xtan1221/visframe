package context.project.process;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import basic.lookup.HasID;
import basic.lookup.ID;
import basic.lookup.PrimaryKeyID;
import basic.lookup.VisframeUDT;
import basic.lookup.project.type.VisframeUDTTypeManagerBase;
import context.project.VisProjectDBContext;
import context.project.process.logtable.ProcessLogTableRow;
import context.project.process.logtable.StatusType;
import context.project.process.logtable.VfIDCollection;
import context.project.process.manager.SimpleProcessManager;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstance;
import javafx.application.Platform;
import visinstance.run.VisInstanceRunID;

/**
 * class for insertion a pre-built {@link ProcessType} VisframeUDT entity into the host VisProjectDBContext;
 * 
 * suitable for all types of {@link ProcessType} except for {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance};
 * 
 * the VisframeUDT entity to be inserted into the host VisProjectDBContext is built before hand and directly given at the constructor
 * 
 * this class implements {@link Callable} interface for performing the main step of inserting the entity;
 * 
 * @author tanxu
 *
 * @param <T>
 * @param <I>
 * @param <M>
 */
public abstract class SimpleProcessPerformer<T extends VisframeUDT, I extends PrimaryKeyID<T>, M extends VisframeUDTTypeManagerBase<T,I>> extends AbstractProcessPerformer<T, I, M> implements Callable<StatusType>{
	public static final int MAX_WAIT_TIME = 60;
	public static final TimeUnit WAIT_TIME_UNIT = TimeUnit.SECONDS;
	public static final int MAX_WAIT_ROUND = 10;
	
	/////////////////////////////////////////////
	private final T targetEntity;
	/**
	 * action to take after the simple process is successfully finished;
	 */
	private Runnable successfullyFinishedAction;
	

	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	protected SimpleProcessPerformer(
			VisProjectDBContext hostVisProjectDBContext,
			M processTypeManager,
			T targetEntity) {
		super(hostVisProjectDBContext,processTypeManager);
		
		this.targetEntity = targetEntity;
		
	}
	
	/**
	 * @return the successfullyFinishedAction
	 */
	public Runnable getSuccessfullyFinishedAction() {
		return successfullyFinishedAction;
	}

	
	/**
	 * @param successfullyFinishedAction the successfullyFinishedAction to set
	 */
	public void setSuccessfullyFinishedAction(Runnable successfullyFinishedAction) {
		this.successfullyFinishedAction = successfullyFinishedAction;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public I getID() {
		return (I) this.getProcessEntity().getID();
	}
	
	/**
	 * return the simple process entity of this SimpleProcessPerformer;
	 * @return
	 */
	public T getProcessEntity() {
		return this.targetEntity;
	}
	
	
	/**
	 * core step to generate any new data; find out all base processes and add them to the {@link #baseProcessIDSet};
	 * 
	 * 1. perform the major steps normally;
	 * 		
	 * 2. then invoke the postprocess() method;
	 * 
	 * 3. invoke {@link #close()}
	 * 
	 * 4. at the end, return {@link StatusType#FINISHED};
	 * 
	 * note that cannot use TRY-CATCH clause since any thrown exception should be caught by the CallableStatusTracker in the invoker method;
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Override
	public abstract StatusType call() throws SQLException, IOException;
	
	
	
	/**
	 * post process after the process entity is successfully inserted into the management table;
	 * 
	 * 1. Set the BASE_PROCESS_ID_SET column value of the 
	 * 		1. process log table 
	 * 			{@link SimpleProcessManager#setBaseProcessIDSetColumnOfCurrentlyRunningSimpleProcess(VfIDCollection)} method
	 * 		2. the management table with {@link #baseProcessIDSet};
	 * 			{@link VisframeUDTTypeManagerBase#setBaseProcessIDSetColumn(PrimaryKeyID, VfIDCollection)};
	 * 		
	 * 2. Add the process ID to the DEPENDENT_PROCESS_ID set of every process in the {@link #baseProcessIDSet}
	 * 		{@link VisframeUDTTypeManagerBase#addDependentProcessID(PrimaryKeyID, PrimaryKeyID)};
	 * 
	 * 3. for simple process except for VisInstanceRun, 
	 * 		Add all IDs in INSERTED_NON_PROCESS_ID_SET column of LOG table to the INSERTED_NON_PROCESS_ID SET column of the management table for the process;
	 * 			{@link VisframeUDTTypeManagerBase#setInsertedNonProcessIDSetColumn(PrimaryKeyID, VfIDCollection)};
	 * 
	 * 4. for VisInstanceRun process, set the value of INVOLVED_CFTARGETVALUETABLERUN_ID_SET column
	 * 			{@link VisframeUDTTypeManagerBase#setInvolvedCFTargetValueTableRunIDSetColumn(PrimaryKeyID, VfIDCollection)};
	 * 
	 * 5. for simple process except for VisInstanceRun, 
	 * 		Set the IS_TEMP column  value to false for all IDs in the INSERTED_NON_PROCESS_ID_SET column of the process log table;
	 * 		{@link VisframeUDTTypeManagerBase#formalize(ID)}
	 * 
	 * 6. Set the IS_TEMP column  value to false for the process entity in its management table; 
	 * 		{@link VisframeUDTTypeManagerBase#formalize(ID)}
	 * 
	 * 7. Set the status of process entity to FINISH in LOG table
	 * 		{@link AbstractProcessManager#setProcessStatusInLogTable(int, StatusType);}
	 * 
	 * 8. invoke the {@link #successfullyFinishedAction} if not null;
	 * 		{@link Platform#runLater(Runnable)}
	 * @throws SQLException 
	 */
	@Override
	public void postprocess() throws SQLException {
		//1
		this.getProcessLogTableAndProcessPerformerManager().getSimpleProcessManager().setBaseProcessIDSetColumnOfCurrentlyRunningSimpleProcess(this.baseProcessIDSet);
		this.getProcessTypeManager().setBaseProcessIDSetColumn(this.getID(), this.baseProcessIDSet);
		
		//2
		for(ID<? extends HasID> id:this.baseProcessIDSet.getAllIDs()) {
			//get the VisframeUDTTypeManagerBase of the base ID
			VisframeUDTTypeManagerBase<?,?> manager = (VisframeUDTTypeManagerBase<?,?>)this.getHasIDTypeManagerController().getManager(id);
			manager.addDependentProcessID(id, this.getID());
		}
		
		//
		ProcessLogTableRow processRow = 
				this.getProcessLogTableAndProcessPerformerManager().retrieveRow(
						this.getProcessLogTableAndProcessPerformerManager().getSimpleProcessManager().getMostRecentRunningSimpleProcessUID());
		
		
		if(!(this.getID() instanceof VisInstanceRunID)) {
			//3
			this.getProcessTypeManager().setInsertedNonProcessIDSetColumn(
					this.getID(), 
					processRow.getInsertedNonProcessIDSet());
		}else{
			//4
			this.getProcessTypeManager().setInvolvedCFTargetValueTableRunIDSetColumn(this.getID(), processRow.getInvolvedCFTargetValueTableRunIDSet());
//			
		}
		
		if(!(this.getID() instanceof VisInstanceRunID)) {
			//5
			for(ID<? extends HasID> id:processRow.getInsertedNonProcessIDSet().getAllIDs()) {
				//first get the VisframeUDTTypeManagerBase of the inserted non process ID;
				VisframeUDTTypeManagerBase<?,?> manager = (VisframeUDTTypeManagerBase<?,?>)this.getHasIDTypeManagerController().getManager(id);
				manager.formalize(id);
			}
		}
		
		//6
		this.getProcessTypeManager().formalize(this.getID());
		
		
		//7
		this.getProcessLogTableAndProcessPerformerManager().getSimpleProcessManager().setProcessStatusInLogTable(
				this.getProcessLogTableAndProcessPerformerManager().getSimpleProcessManager().getMostRecentRunningSimpleProcessUID(),
				StatusType.FINISHED);
		
		
		//8
		if(this.getSuccessfullyFinishedAction()!=null) {
//			System.out.println("invoke SuccessfullyFinishedAction!!!!!!!!!");
			Platform.runLater(this.getSuccessfullyFinishedAction());
		}
	}
	
	
}
