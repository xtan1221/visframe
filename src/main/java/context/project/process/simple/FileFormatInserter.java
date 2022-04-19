package context.project.process.simple;

import java.sql.SQLException;

import basic.lookup.project.type.udt.VisProjectFileFormatManager;
import context.project.VisProjectDBContext;
import context.project.process.SimpleProcessPerformer;
import context.project.process.logtable.StatusType;
import context.project.process.logtable.VfIDCollection;
import exception.VisframeException;
import fileformat.FileFormat;
import fileformat.FileFormatID;

/**
 * 
 * @author tanxu
 *
 */
public class FileFormatInserter extends SimpleProcessPerformer<FileFormat, FileFormatID, VisProjectFileFormatManager>{
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param fileFormat
	 */
	public FileFormatInserter(
			VisProjectDBContext hostVisProjectDBContext,
			FileFormat fileFormat
			) {
		super(hostVisProjectDBContext, 
				hostVisProjectDBContext.getHasIDTypeManagerController().getFileFormatManager(),
				fileFormat);
	}
	
	/**
	 * check if the id of the FileFormat already exists or not;
	 * 
	 * @throws SQLException 
	 * 
	 */
	@Override
	public void checkConstraints() throws SQLException {
		if(this.getProcessTypeManager().checkIDExistence(this.getID())) {
			throw new VisframeException("ID of FileFormat to be inserted already exists in the management table");
		}
	}
	
	
	/**
	 * 
	 * insert the FileFormat into the management table of the host VisProjectDBContext;
	 * 
	 * invoke {@link #close()}
	 * 
	 * return {@link StatusType#FINISHED}
	 * 
	 * @throws SQLException 
	 * 
	 */
	@Override
	public StatusType call() throws SQLException {
		this.baseProcessIDSet = new VfIDCollection();
		
		this.getProcessTypeManager().insert(this.getProcessEntity());
		
		this.postprocess();
//		this.close();
		
		return StatusType.FINISHED;
	}
	
}
