package importer;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import basic.HasNotes;
import basic.lookup.VisframeUDT;
import basic.process.NonReproduceableProcessType;
import context.project.VisProjectDBContext;
import context.project.process.logtable.StatusType;
import fileformat.FileFormat;
import fileformat.FileFormatID;
import metadata.DataType;
import metadata.MetadataID;
import metadata.MetadataName;


/**
 * controller interface to store the full set of settings to import a data source file into a VisProjectDBContext’s RDB as a Metadata and supporting data tables;
 * @author tanxu
 */
public interface DataImporter extends HasNotes, Serializable, VisframeUDT, NonReproduceableProcessType, Callable<StatusType>{
	
	/**
	 * returns the URL string of the source data file;
	 * note that the source file will be read by BufferedReader's readLine() method;
	 * @return the URL string of the source data file;
	 */
	Path getDataSourcePath();
	
	/**
	 * returns the {@link FileFormatID} of the source data file;
	 * the FileFormat can be retrieved from the management table of the host VisProjectDBContext;
	 * @return
	 */
	FileFormatID getFileFormatID();
	
	/**
	 * 
	 * @return
	 */
	FileFormat getFileFormat();
	
	/**
	 * 
	 * @return
	 */
	MetadataName getMainImportedMetadataName();
	
	/**
	 * return the main target MetadataID of this DataImporter
	 * 
	 * @return
	 */
	MetadataID getMainImportedMetadataID();
	
	/**
	 * return the data type of the imported data of this DataImporter
	 * @return
	 */
	DataType getDataType();
	
	/**
	 * return the DataImporterID of this DataImporter;
	 */
	@Override
	default DataImporterID getID() {
		return new DataImporterID(this.getMainImportedMetadataID().getName(), this.getDataType());
	}
	
//	/**
//	 * return the full set of MetadataID imported by this DataImporter
//	 * @return
//	 */
//	Set<MetadataID> getImportedMetadataIDSet();
	
	
//	/**
//	 * return the set of DataTableSchemaID of this DataImporter
//	 * @return
//	 */
//	Set<DataTableSchemaID> getImportedDataTableSchemaIDSet();
	
	
	
	/////////////////////////////////
	/**
	 * set the host AbstractVisProjectDBContext for this operation before it is run;
	 * the AbstractVisProjectDBContext is stored as a transient field and will not be serialized;
	 * @param hostVisProject
	 */
	void setHostVisProjectDBContext(VisProjectDBContext hostVisProject);
	
	VisProjectDBContext getHostVisProjectDBContext();
	
	/**
	 * 
	 * @return
	 */
	FileParser getFileParser();
	
	/**
	 * import the data source file into the RDB of the given VisProjectDBContext;
	 *<p>
	 * three major tasks (order may vary):</p>
	 * <br>
	 * 1. parse the input data source file and create the data tables schema and populate them with the data read from the file with a FileParser;</br><br>
	 * 2. create Metadata and insert it into the Metadata management table;</br><br>
	 * 3. insert this DataImporter into the DataImporter management table</br>
	 * @param visProject VisProjectDBContext
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Override
	StatusType call() throws SQLException, IOException;
}
