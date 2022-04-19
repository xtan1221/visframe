package context.scheme.appliedarchive.reproducedandinsertedinstance;

import java.sql.SQLException;
import basic.lookup.HasID;
import basic.lookup.ID;
import basic.lookup.PrimaryKeyID;
import basic.lookup.project.type.VisframeUDTTypeManagerBase;
import basic.lookup.project.type.udt.VisProjectVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager;
import basic.reproduce.Reproducible;
import context.project.VisProjectDBContext;
import context.project.process.AbstractProcessPerformer;
import context.project.process.logtable.ProcessLogTableRow;
import context.project.process.logtable.StatusType;
import context.project.process.logtable.VfIDCollection;
import context.project.process.manager.VisSchemeAppliedArchiveReproducingAndInsertionManager;
import exception.VisframeException;
import context.scheme.appliedarchive.VisSchemeAppliedArchive;
import context.scheme.appliedarchive.reproducedandinsertedinstance.utils.OperationReproducingAndInsertionTracker;
import context.scheme.appliedarchive.reproducedandinsertedinstance.utils.CFGReproducingAndInsertionTracker;
import context.scheme.appliedarchive.reproducedandinsertedinstance.utils.CFReproducingAndInsertionTracker;

/**
 * class that build an instance of a VisSchemeAppliedArchive by reproducing and inserting it into host VisProjectDBContext;
 * 
 * 1. initialize by constructor
 * 
 * 2. start it in process log table
 * 		{@link VisSchemeAppliedArchiveReproducingAndInsertionManager#startNewVSAArchiveReproducerAndInserter(VisSchemeAppliedArchiveReproducerAndInserter)} method
 * 		which will check constraints by invoking {@link #checkConstraints()} and insert into the process log table;
 * 
 * 3. reproduce and insert each Operations on the trimmed Integrated DOS graph of the VisSchemeApplierArchive (if any);
 * 		{@link #OperationReproducingAndInsertionTracker}
 * 
 * 4. reproduce and insert each CompositionFunctionGroup on the trimmed integrated CFD graph of the VisSchemeApplierArchive
 * 		{@link #CFGAndCFReproducerAndInserter}
 * 
 * 5. reproduce and insert each CompositionFunction on the trimmed integrated CFD graph of the VisSchemeApplierArchive
 * 		{@link #CFGAndCFReproducerAndInserter}
 * 
 * 6. insert the {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance} into the management table;
 * 		{@link #insertIntoManagementTable()}
 * 
 * 7. post process
 * 		{@link #postprocess()}
 * 
 * @author tanxu
 * 
 */
public class VisSchemeAppliedArchiveReproducerAndInserter extends 
	AbstractProcessPerformer<VisSchemeAppliedArchiveReproducedAndInsertedInstance,VisSchemeAppliedArchiveReproducedAndInsertedInstanceID,VisProjectVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager>{
	
	/////////////////
	private final VisSchemeAppliedArchive applierArchive;
	
	
	///////////////////////////////////
	/**
	 * UID of the target VisSchemeAppliedArchiveReproducedAndInsertedInstance in the management table;
	 * NOT the process UID!!!
	 */
	private int visSchemeAppliedArchiveReproducedAndInsertedInstanceUID;
	
	
	//////////////
	/**
	 * used by {@link Reproducible#reproduce(VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)} method
	 */
	private OperationReproducingAndInsertionTracker operationReproducingAndInsertionTracker;
	
	/**
	 * should be re-initialized whenever inserted Operation is rolled back;
	 * used by {@link Reproducible#reproduce(VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)} method
	 */
	private CFGReproducingAndInsertionTracker CFGReproducingAndInsertionTracker;
	

	/**
	 * should be re-initialized whenever inserted CFG is rolled back;
	 * used by {@link Reproducible#reproduce(VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)} method
	 */
	private CFReproducingAndInsertionTracker CFReproducingAndInsertionTracker;
	
	////////////////////////////

	////////////////////////////////////
	
	
	/**
	 * constructor
	 * also generate a unique UID for the VisInstance to be created by this Applier
	 * @param hostVisProjectDBContext
	 * @param appliedVisScheme
	 * @throws SQLException 
	 */
	public VisSchemeAppliedArchiveReproducerAndInserter(
			VisProjectDBContext hostVisProjectDBContext,
			VisSchemeAppliedArchive applierArchive
			) throws SQLException{
		super(hostVisProjectDBContext, hostVisProjectDBContext.getHasIDTypeManagerController().getVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager());
		
		//TODO validations
		this.applierArchive = applierArchive;
		
		//
		this.generateUID();
		this.makeBasedProcessIDSet();
		
	}
	
	/**
	 * generate uid for the target VisSchemeBasedVisInstance
	 * @throws SQLException 
	 */
	private void generateUID() throws SQLException {
		this.visSchemeAppliedArchiveReproducedAndInsertedInstanceUID = 
				this.getHostVisProjectDBContext().getHasIDTypeManagerController().getVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager().findNextAvailableUID();
	}
	
	/**
	 * build the {@link #baseProcessIDSet}
	 * 
	 * 		add the insertion process of the VisSchemeAppliedArchive;
	 * 
	 * @throws SQLException 
	 */
	private void makeBasedProcessIDSet() throws SQLException {
		this.baseProcessIDSet = new VfIDCollection();
		
		
		this.baseProcessIDSet.addID(this.getHasIDTypeManagerController().getVisSchemeAppliedArchiveManager().retrieveRow(this.applierArchive.getID()).getInsertionProcessID());
	}
	
	///////////////////////////////////////
	/**
	 * check constraints;
	 * invoked inside the {@link VisSchemeAppliedArchiveReproducingAndInsertionManager#startNewVSAArchiveReproducerAndInserter(VisSchemeAppliedArchiveReproducerAndInserter)} method 
	 * after this object is created and before the first operation is reproduced and inserted by {@link #operationReproducerAndInserter}
	 * 
	 * 
	 * 1. check if the {@link #applierArchive} exists in the management table;
	 * 2. check if the UID is not 
	 * @throws SQLException 
	 */
	@Override
	public void checkConstraints() throws SQLException {
		if(!this.getHasIDTypeManagerController().getVisSchemeAppliedArchiveManager().checkIDExistence(this.getAppliedArchive().getID())) {
			throw new VisframeException("VisSchemeAppliedArchive is not found in the VisSchemeAppliedArchive management table!");
		}
		
		if(this.getHasIDTypeManagerController().getVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager().checkIDExistence(this.getID())){
			throw new VisframeException("ID of the target VisSchemeAppliedArchiveReproducedAndInsertedInstance is already existing in the management table!");
		}
	}
	
	
	
	/////////////////////////////////////////////////////////
//	/**
//	 * return the set of reproduced and inserted process ID set;
//	 * 
//	 * throw exception if Operation, CFG, CF are not all successfully reproduced and inserted;
//	 * 
//	 * @return
//	 */
//	public VfIDCollection getReproducedAndInsertedProcessIDSet() {
//		return this.reproducedAndInsertedProcessIDCollection;
//	}
	
	@Override
	public VisSchemeAppliedArchiveReproducedAndInsertedInstanceID getID() {
		return new VisSchemeAppliedArchiveReproducedAndInsertedInstanceID(this.visSchemeAppliedArchiveReproducedAndInsertedInstanceUID);
	}
	
	//////////////////////////////////////////////////////////
	
	/**
	 * finish this {@link VisSchemeAppliedArchiveReproducerAndInserter}
	 * 
	 * must be invoked AFTER all Operation, CFG and CFs are successfully reproduced and inserted into host VisProjectDBContext
	 * 		so that the VisSchemeAppliedArchiveReproducedAndInsertedInstance can be built;
	 * 
	 * implementation:
	 * 
	 * 1. invoke the {@link #insertIntoManagementTable()}
	 * 
	 * 2. invoke the {@link #postprocess()}
	 * 
	 * @throws SQLException
	 */
	public void finish(VisSchemeAppliedArchiveReproducedAndInsertedInstance builtInstance) throws SQLException {
		
		this.insertIntoManagementTable(builtInstance);
		this.postprocess();
	}
	
	/**
	 * build the {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance} and insert it into the management table;
	 * 
	 * must be invoked 
	 * 1. AFTER all Operation, CFG and CFs are successfully reproduced and inserted into host VisProjectDBContext
	 * 		so that the VisSchemeAppliedArchiveReproducedAndInsertedInstance can be built;
	 * 2. before {@link #postprocess()} method is invoked;
	 * 
	 * 
	 * 
	 * @throws SQLException 
	 */
	private void insertIntoManagementTable(VisSchemeAppliedArchiveReproducedAndInsertedInstance builtInstance) throws SQLException {
//		VisSchemeAppliedArchiveReproducedAndInsertedInstance instance = 
//				new VisSchemeAppliedArchiveReproducedAndInsertedInstance(
//						this.notes,
//						this.getAppliedArchive().getAppliedVisSchemeID(),
//						this.getAppliedArchive().getID(),
//						this.visSchemeAppliedArchiveReproducedAndInsertedInstanceUID,
//						this.operationReproducingAndInsertionTracker.getOriginalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap(),
//						this.CFGReproducingAndInsertionTracker.getOriginalCFGIDCopyIndexReproducedCFGIDMapMap(),
//						this.CFReproducingAndInsertionTracker.getOriginalCFIDCopyIndexReproducedCFIDMapMap()
//						);
		
		
		this.getProcessTypeManager().insert(builtInstance);
	}
	
	
	/**
	 * post process;
	 * invoked after 
	 * 1. all Operations, CFGs and CFs are successfully reproduced and inserted
	 * 2. the VisSchemeAppliedArchiveReproducedAndInsertedInstance is built and inserted into the management table;
	 * 
	 * steps:
	 * 1. Set the BASE_PROCESS_ID_SET column value of the 
	 * 		1. process log table and 
	 * 			{@link VisSchemeAppliedArchiveReproducingAndInsertionManager#setBaseProcessIDSetColumnOfCurrentlyRunningVSAArchiveReproducerAndInserterProcess(VfIDCollection)}
	 * 		2. the management table with {@link #baseProcessIDSet};
	 * 			{@link VisframeUDTTypeManagerBase#setBaseProcessIDSetColumn(PrimaryKeyID, VfIDCollection)};
	 * 
	 * 2. Add the process ID to the DEPENDENT_PROCESS_ID set of every process in the {@link #baseProcessIDSet}
	 * 		{@link VisframeUDTTypeManagerBase#addDependentProcessID(PrimaryKeyID, PrimaryKeyID)};
	 * 
	 * 3. for VisSchemeAppliedArchiveReproducerAndInserter process type only,
	 * 		set the value of InsertedProcessIDSetColumn;
	 * 		{@link VisframeUDTTypeManagerBase#setInsertedProcessIDSetColumn(PrimaryKeyID, VfIDCollection)}
	 * 
	 * ==============
	 * note that all reproduced and inserted processes(Operation, CFG, CF) have been formalized by their SimpleProcesPerformer's postprocess method, 
	 * thus do not need to formalize those inserted processes here;
	 * however, this may be modified in future;
	 * ================
	 * 
	 * 4. Set the IS_TEMP column  value to false for the process entity in its management table; 
	 * 		{@link VisframeUDTTypeManagerBase#formalize(ID)}
	 * 
	 * 5. Set the status of process entity to FINISH in LOG table
	 * 		{@link AbstractProcessManager#setProcessStatusInLogTable(int, StatusType);}
	 * 
	 * 
	 * @throws SQLException 
	 */
	@Override
	public void postprocess() throws SQLException {
		//1
		this.getProcessLogTableAndProcessPerformerManager().getVSAArchiveReproducerAndInserterManager().setBaseProcessIDSetColumnOfCurrentlyRunningVSAArchiveReproducerAndInserterProcess(this.baseProcessIDSet);
		this.getProcessTypeManager().setBaseProcessIDSetColumn(this.getID(), this.baseProcessIDSet);
		
		//2
		for(ID<? extends HasID> id:this.baseProcessIDSet.getAllIDs()) {
			VisframeUDTTypeManagerBase<?,?> manager = (VisframeUDTTypeManagerBase<?,?>)this.getHasIDTypeManagerController().getManager(id);
			manager.addDependentProcessID(id, this.getID());
		}
		
		//
		ProcessLogTableRow processRow = 
				this.getProcessLogTableAndProcessPerformerManager().retrieveRow(
						this.getProcessLogTableAndProcessPerformerManager().getVSAArchiveReproducerAndInserterManager().getCurrentlyRunningVSAReproducerAndInserterProcessUID());
		
		//3
		this.getProcessTypeManager().setInsertedProcessIDSetColumn(
				this.getID(), 
				processRow.getInsertedProcessIDSet()
				);
		
		
		//4
		this.getProcessTypeManager().formalize(this.getID());
		
		
//		//TODO
//		this.postprocess();
		
		
		//5
		this.getProcessLogTableAndProcessPerformerManager().getSimpleProcessManager().setProcessStatusInLogTable(
				this.getProcessLogTableAndProcessPerformerManager().getVSAArchiveReproducerAndInserterManager().getCurrentlyRunningVSAReproducerAndInserterProcessUID(),
				StatusType.FINISHED);
	}
	
	////////////////////////////////////////////////
	/**
	 * discard this {@link VisSchemeAppliedArchiveReproducerAndInserter} and set the status in the process log table to DISCARDED
	 * @throws SQLException 
	 */
	public void discard() throws SQLException {
		this.getProcessLogTableAndProcessPerformerManager().getVSAArchiveReproducerAndInserterManager().discardCurrentlyRunningVSAArchiveReproducerAndInserter();
	}
	
	/**
	 * abort and roll back this {@link VisSchemeAppliedArchiveReproducerAndInserter}
	 * @throws SQLException
	 */
	public void abortAndRollback() throws SQLException {
		this.getProcessLogTableAndProcessPerformerManager().getVSAArchiveReproducerAndInserterManager().abortAndRollbackCurrentlyRunningVSAArchiveReproducerAndInserter();
	}

	
	///////////////////////////////////////////
	/**
	 * @return the applierArchive
	 */
	public VisSchemeAppliedArchive getAppliedArchive() {
		return applierArchive;
	}
	

	/**
	 * @return the visSchemeAppliedArchiveReproducedAndInsertedInstanceUID
	 */
	public int getVisSchemeAppliedArchiveReproducedAndInsertedInstanceUID() {
		return visSchemeAppliedArchiveReproducedAndInsertedInstanceUID;
	}


//	/**
//	 * @return the reproducedAndInsertedProcessIDCollection
//	 */
//	public VfIDCollection getReproducedAndInsertedProcessIDCollection() {
//		return reproducedAndInsertedProcessIDCollection;
//	}
	
	////////////////////////////////////////////
	/**
	 * @return the operationReproducingAndInsertionTracker
	 */
	public OperationReproducingAndInsertionTracker getOperationReproducingAndInsertionTracker() {
		return operationReproducingAndInsertionTracker;
	}

	/**
	 * initialize the {@link #operationReproducingAndInsertionTracker};
	 * 
	 * then invoke the {@ OperationReproducingAndInsertionTracker#initialize()} method
	 * @throws SQLException 
	 */
	public void initializeOperationReproducingAndInsertionTracker() throws SQLException {
		this.operationReproducingAndInsertionTracker = new OperationReproducingAndInsertionTracker(this);
		
		this.operationReproducingAndInsertionTracker.initialize();
	}

	/**
	 * @return the cFGReproducingAndInsertionTracker
	 */
	public CFGReproducingAndInsertionTracker getCFGReproducingAndInsertionTracker() {
		return CFGReproducingAndInsertionTracker;
	}
	
	/**
	 * initialize the {@link #CFGReproducingAndInsertionTracker};
	 * 
	 * must be invoked whenever the operations are all inserted;
	 * 
	 * @throws SQLException 
	 */
	public void initializeCFGReproducingAndInsertionTracker() throws SQLException {
		CFGReproducingAndInsertionTracker = new CFGReproducingAndInsertionTracker(this);
		
		this.CFGReproducingAndInsertionTracker.build();
	}
	
	/**
	 * @return the cFReproducingAndInsertionTracker
	 */
	public CFReproducingAndInsertionTracker getCFReproducingAndInsertionTracker() {
		return CFReproducingAndInsertionTracker;
	}

	/**
	 * initialize the {@link #CFReproducingAndInsertionTracker};
	 * must be invoked whenever the cfgs are all inserted;
	 * @throws SQLException 
	 */
	public void initializeCFReproducingAndInsertionTracker() throws SQLException {
		CFReproducingAndInsertionTracker = new CFReproducingAndInsertionTracker(this);
		
		this.CFReproducingAndInsertionTracker.build();
	}

}
