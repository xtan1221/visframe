package context.project.process.simple;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

import basic.lookup.project.type.udt.VisProjectDataImporterManager;
import context.project.VisProjectDBContext;
import context.project.process.SimpleProcessPerformer;
import context.project.process.logtable.StatusType;
import context.project.process.logtable.VfIDCollection;
import exception.VisframeException;
import importer.DataImporter;
import importer.DataImporterID;

public class DataImporterPerformer extends SimpleProcessPerformer<DataImporter, DataImporterID, VisProjectDataImporterManager>{
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param dataImporter
	 */
	public DataImporterPerformer(VisProjectDBContext hostVisProjectDBContext, DataImporter dataImporter) {
		super(hostVisProjectDBContext, hostVisProjectDBContext.getHasIDTypeManagerController().getDataImporterManager(),dataImporter);
	}
	
	
	/**
	 * 1. check data source existence
	 * 2. check inserted id existence
	 * 		1. inserted Metadatas
	 * 		2. inserted data tables
	 * 		3. DataImporter
	 * 3. check existence of FileFormat
	 */
	@Override
	public void checkConstraints() throws SQLException {
		//1
		if(!Files.exists(this.getProcessEntity().getDataSourcePath())) {
			throw new VisframeException("data source does not exist");
		}
		//2
		if(this.getProcessTypeManager().checkIDExistence(this.getID())) {
			throw new VisframeException("ID of the DataImporter already exist in the management table");
		}
		
//		for(MetadataID mid:this.getProcessEntity().getImportedMetadataIDSet()) {
		if(this.getHasIDTypeManagerController().getMetadataManager().checkIDExistence(this.getProcessEntity().getMainImportedMetadataID())) {
			throw new VisframeException("ID of a Metadata to be imported already exist in the management table");
		}
//		}
		
		//Data table schema name is automatically generated and always unique;
//		for(DataTableSchemaID dtid:this.getProcessEntity().getImportedDataTableSchemaIDSet()) {
//			if(this.getHasIDTypeManagerController().getDataTableSchemaManager().checkIDExistence(dtid)) {
//				throw new VisframeException("ID of a data table to be imported already exist in the management table");
//			}
//		}
		
		//3
		if(!this.getHasIDTypeManagerController().getFileFormatManager().checkIDExistence(this.getProcessEntity().getFileFormatID())) {
			throw new VisframeException("FileFormat for the DataImporter does not exist in the management table");
		}
		
	}
	
	/**
	 * 1. set the {@link #baseProcessIDSet}
	 * 2. invoke the {@link DataImporter#call()} method
	 * 		!!need to first set the HostVisProjectDBContext
	 * 3. return {@link StatusType#FINISHED};
	 * @throws IOException 
	 */
	@Override
	public StatusType call() throws SQLException, IOException {
		//for testing and debug, comment out afterwards;
//		try {
//			Thread.sleep(20000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		this.baseProcessIDSet = new VfIDCollection();
		this.baseProcessIDSet.addID(this.getProcessEntity().getFileFormatID());
		
		//
		this.getProcessEntity().setHostVisProjectDBContext(this.getHostVisProjectDBContext());
		
		this.getProcessEntity().call();
		
		this.postprocess();
		
		return StatusType.FINISHED;
	}
	
}
