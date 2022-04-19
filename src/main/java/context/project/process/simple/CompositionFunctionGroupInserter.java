package context.project.process.simple;

import java.sql.SQLException;

import basic.lookup.project.type.udt.VisProjectCompositionFunctionGroupManager;
import context.project.VisProjectDBContext;
import context.project.process.SimpleProcessPerformer;
import context.project.process.logtable.StatusType;
import context.project.process.logtable.VfIDCollection;
import exception.VisframeException;
import function.group.CompositionFunctionGroup;
import function.group.CompositionFunctionGroupID;

/**
 * class that is responsible for validating and inserting a new {@link CompositionFunctionGroup} instance into the host {@link VisProjectDBContext};
 * 
 * @author tanxu
 *
 */
public class CompositionFunctionGroupInserter extends SimpleProcessPerformer<CompositionFunctionGroup, CompositionFunctionGroupID, VisProjectCompositionFunctionGroupManager>{
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param targetEntity
	 */
	public CompositionFunctionGroupInserter(
			VisProjectDBContext hostVisProjectDBContext,
			CompositionFunctionGroup targetEntity) {
		super(hostVisProjectDBContext, hostVisProjectDBContext.getHasIDTypeManagerController().getCompositionFunctionGroupManager(), targetEntity);
	}
	
	/**
	 * 1. check the existence of owner {@link RecordDataMetadata} in the Metadata management table;
	 * 2. check whether the {@link CompositionFunctionGroupID} of the {@link CompositionFunctionGroup} to be inserted is already taken by an existing {@link CompositionFunctionGroup}
	 */
	@Override
	public void checkConstraints() throws SQLException {
		if(!this.getHasIDTypeManagerController().getMetadataManager().checkIDExistence(this.getProcessEntity().getOwnerRecordDataMetadataID())) {
			throw new VisframeException("owner RecordDataMetadata of the CompositionFunctionGroup is not found in the management table!");
		}
		
		if(this.getProcessTypeManager().checkIDExistence(this.getID())) {
			throw new VisframeException("CompositionFunctionGroupID of the CompositionFunctionGroup to be inserted already exists in the management table!");
		}
	}
	
	/**
	 * 1. set the {@link #baseProcessIDSet}
	 * 2. insert the CFG into management table
	 * 4. return {@link StatusType#FINISHED};
	 */
	@Override
	public StatusType call() throws SQLException {
		//add the insertion process id of owner record metadata to the baseProcessIDSet
		this.baseProcessIDSet = new VfIDCollection();
		
		this.baseProcessIDSet.addID(this.getHasIDTypeManagerController().getMetadataManager().retrieveRow(this.getProcessEntity().getOwnerRecordDataMetadataID()).getInsertionProcessID());
		
		//
		this.getProcessTypeManager().insert(this.getProcessEntity());
		
		this.postprocess();
		
		return StatusType.FINISHED;
	}


}
