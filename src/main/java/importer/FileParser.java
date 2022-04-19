package importer;

import java.io.IOException;
import java.sql.SQLException;

import context.project.VisProjectDBContext;

/**
 * interface for parser class that read in the data from the input data file 
 * 
 * and parse it into visframe defined data format based on the FileFormat of a {@link DataImporter} instance 
 * 
 * and create data tables schema and populate it with the data from the input data source file;
 * 
 * 
 * @author tanxu
 * 
 */
public interface FileParser{
	/**
	 * return the DataImporter which contains the input data source file, FileFormat and parsing settings
	 * 
	 * @return
	 */
	DataImporter getDataImporter();
	
	/**
	 * return the VisProjectDBContext of this FileParser
	 * @return
	 */
	VisProjectDBContext getVisProjectDBContext();
	
	/**
	 * perform the file parsing;
	 * 1. create initial data table schema
	 * 2. read the data file and populate the table schema
	 * @throws SQLException 
	 * @throws IOException 
	 */
	void perform() throws SQLException, IOException;
}
