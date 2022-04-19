package context.project.process.simple;

import java.io.IOException;
import java.sql.SQLException;

import basic.lookup.project.type.udt.VisProjectOperationManager;
import context.project.VisProjectDBContext;
import context.project.process.SimpleProcessPerformer;
import context.project.process.logtable.StatusType;
import context.project.process.logtable.VfIDCollection;
import exception.VisframeException;
import metadata.MetadataID;
import operation.Operation;
import operation.OperationID;

/**
 * 
 * @author tanxu
 *
 */
public class OperationPerformer extends SimpleProcessPerformer<Operation, OperationID, VisProjectOperationManager>{
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param targetEntity
	 */
	public OperationPerformer(
			VisProjectDBContext hostVisProjectDBContext,
			Operation targetEntity) {
		super(hostVisProjectDBContext, hostVisProjectDBContext.getHasIDTypeManagerController().getOperationManager(), targetEntity);
	}
	
	
	/**
	 * 1. check existence of input metadata
	 * 2. check existence of inserted data
	 * 		1. output metadata
	 * 		2. output data tables
	 * 		3. Operation
	 * 
	 */
	@Override
	public void checkConstraints() throws SQLException {
		//1
		for(MetadataID mid: this.getProcessEntity().getInputMetadataIDSet()) {
			if(!this.getHasIDTypeManagerController().getMetadataManager().checkIDExistence(mid)) {
				throw new VisframeException("input MetadataID not found in management table");
			}
		}
		
		//2
		for(MetadataID mid:this.getProcessEntity().getOutputMetadataIDSet()) {
			if(this.getHasIDTypeManagerController().getMetadataManager().checkIDExistence(mid)) {
				throw new VisframeException("output MetadataID already exist in management table");
			}
		}
		
		//all output data table schema ID are automatically generated and always enforce uniqueness with existing ones, thus do not check it here
//		for(DataTableSchemaID did:this.getProcessEntity().getOutputDataTableSchemaIDSet()) {
//			if(this.getHasIDTypeManagerController().getDataTableSchemaManager().checkIDExistence(did)) {
//				throw new VisframeException("output DataTableSchemaID already exist in management table");
//			}
//		}
		
		if(this.getProcessTypeManager().checkIDExistence(this.getID())) {
			throw new VisframeException("OperationID already exist in management table");
		}
		
	}

	
	/**
	 * 1. set the {@link #baseProcessIDSet}
	 * 2. invoke the {@link Operation#call()} method
	 * 		!!need to first set the HostVisProjectDBContext
	 * 3. return {@link StatusType#FINISHED};
	 * @throws IOException 
	 */
	@Override
	public StatusType call() throws SQLException, IOException {
		//add the insertion process id of each input metadata to the baseProcessIDSet
		this.baseProcessIDSet = new VfIDCollection();
		
		for(MetadataID inputMetadataID:this.getProcessEntity().getInputMetadataIDSet()) {
			this.baseProcessIDSet.addID(this.getHasIDTypeManagerController().getMetadataManager().retrieveRow(inputMetadataID).getInsertionProcessID());
		}
		
		//
		this.getProcessEntity().setHostVisProjectDBContext(this.getHostVisProjectDBContext());
		
		
		this.getProcessEntity().call();
		
		this.postprocess();
		
		return StatusType.FINISHED;
	}

}
