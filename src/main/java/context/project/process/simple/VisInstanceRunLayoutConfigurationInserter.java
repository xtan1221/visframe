package context.project.process.simple;

import java.sql.SQLException;
import basic.lookup.project.type.udt.VisProjectVisInstanceRunLayoutConfigurationManager;
import context.project.VisProjectDBContext;
import context.project.process.SimpleProcessPerformer;
import context.project.process.logtable.StatusType;
import context.project.process.logtable.VfIDCollection;
import exception.VisframeException;
import visinstance.run.layoutconfiguration.VisInstanceRunLayoutConfiguration;
import visinstance.run.layoutconfiguration.VisInstanceRunLayoutConfigurationID;

/**
 * process performer that insert a VisInstanceRunLayout into the VisInstanceRunLayout management table;
 * 
 * @author tanxu
 *
 */
public class VisInstanceRunLayoutConfigurationInserter extends SimpleProcessPerformer<VisInstanceRunLayoutConfiguration, VisInstanceRunLayoutConfigurationID, VisProjectVisInstanceRunLayoutConfigurationManager>{	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param targetEntity
	 */
	public VisInstanceRunLayoutConfigurationInserter(VisProjectDBContext hostVisProjectDBContext,VisInstanceRunLayoutConfiguration targetEntity) {
		super(hostVisProjectDBContext, hostVisProjectDBContext.getHasIDTypeManagerController().getVisInstanceRunLayoutConfigurationManager(), targetEntity);
		
	}
	
	
	/////////////////////////////////////////
	/**
	 * 1. check if the VisInstanceRun exists
	 * 
	 * 2. check if VisInstanceRunLayoutID already exists
	 */
	@Override
	public void checkConstraints() throws SQLException {
		//1
		if(!this.getHostVisProjectDBContext().getHasIDTypeManagerController().getVisInstanceRunManager()
				.checkIDExistence(this.getProcessEntity().getVisInstanceRunID()))
			throw new VisframeException("ID of the VisInstanceRun of the VisInstanceRunLayout to be inserted is not found in the VisInstanceRun management table");
		
		
		//2
		if(this.getProcessTypeManager().checkIDExistence(this.getID())) {
			throw new VisframeException("ID of the VisInstanceRunLayout to be inserted already exists in the management table");
		}
	}
	
	
	/**
	 * 1. Build the baseProcessIDSet
	 * 		insertion process ID of the VisInstanceRunID
	 * 
	 * 2. insert into management table
	 * 
	 * 3. invoke {@link #postprocess()}
	 * 
	 * 4. return {@link StatusType#FINISHED}
	 * 
	 */
	@Override
	public StatusType call() throws SQLException {
		//1
		this.baseProcessIDSet = new VfIDCollection();
		this.baseProcessIDSet.addID(this.getProcessEntity().getVisInstanceRunID());
		
		
		//2
		this.getProcessTypeManager().insert(this.getProcessEntity());
		
		//3
		this.postprocess();
		
		//4
		return StatusType.FINISHED;
	}


}
