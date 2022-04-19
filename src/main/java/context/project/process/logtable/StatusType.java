package context.project.process.logtable;

/**
 * type of status of either an started AbstractProcessPerformer object in VisProjectDBContext 
 * or a {@link Callable} object submitted to the ExecutorService of the ProcessLogTableAndProcessPerformerManager;
 * @author tanxu
 *
 */
public enum StatusType {
	RUNNING(-1, false, true),
	/**
	 * terminated before getting into the stage that produces data in the rdb; no roll back is needed
	 */
	DISCARDED (2, true, false),
	/**
	 * terminated after getting into the stage that produces data in the rdb; need to be rolled back;
	 */
	ABORTED (-2, false, true),//terminated but not being rolled back
	ROLLINGBACK(-3,false, true),//
	ROLLEDBACK(0, true, false),
	FINISHED(1,true, true);
	
	//////////////////////
	private final int indicator;
	private final boolean done; //whether this process is finished and not need to be dealt with so that a new process can start
	private final boolean rollbackable;//whether a new roll back can be initialized for process with this status or not;
	
	
	StatusType(int index, boolean done, boolean rollbackable){
		this.indicator = index;
		this.done = done;
		this.rollbackable = rollbackable;
	}
	
	/**
	 * whether this process is in a status that does not need to be dealt with so that a new process can start;
	 * @return
	 */
	public boolean isDone() {
		return done;
	}
	
	
	/**
	 * whether a new roll back can be initialized for process with this status or not;
	 * @return
	 */
	public boolean isRollbackable() {
		return this.rollbackable;
	}

	
	public int getIndicator() {
		return indicator;
	}
	
}
