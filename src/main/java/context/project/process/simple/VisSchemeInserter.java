package context.project.process.simple;

import java.sql.SQLException;

import basic.lookup.project.type.udt.VisProjectVisSchemeManager;
import context.project.VisProjectDBContext;
import context.project.process.SimpleProcessPerformer;
import context.project.process.logtable.StatusType;
import context.project.process.logtable.VfIDCollection;
import context.scheme.VisScheme;
import context.scheme.VisSchemeID;
import exception.VisframeException;

public class VisSchemeInserter extends SimpleProcessPerformer<VisScheme, VisSchemeID, VisProjectVisSchemeManager> {
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param targetEntity
	 */
	public VisSchemeInserter(
			VisProjectDBContext hostVisProjectDBContext,
			VisScheme targetEntity) {
		super(hostVisProjectDBContext, hostVisProjectDBContext.getHasIDTypeManagerController().getVisSchemeManager(), targetEntity);
	}
	
	////////////////////////////////////////////
	
	/**
	 * check if the id of the VisScheme already exists or not;
	 */
	@Override
	public void checkConstraints() throws SQLException {
		if(this.getProcessTypeManager().checkIDExistence(this.getID())) {
			throw new VisframeException("ID of VisScheme to be inserted already exists in the management table");
		}
	}
	
	/**
	 * 
	 * insert the VisScheme into the management table of the host VisProjectDBContext;
	 * 
	 * return {@link StatusType#FINISHED}
	 * 
	 * @throws SQLException 
	 * 
	 */
	@Override
	public StatusType call() throws SQLException {
		this.baseProcessIDSet = new VfIDCollection();
		
		//no base process for VisScheme
		
		
		//
		this.getProcessTypeManager().insert(this.getProcessEntity());
		
		this.postprocess();
		
		return StatusType.FINISHED;
	}

}
