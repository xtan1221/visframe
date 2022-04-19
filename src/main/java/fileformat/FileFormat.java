package fileformat;

import basic.HasName;
import basic.HasNotes;
import basic.SimpleName;
import basic.lookup.VisframeUDT;
import basic.process.NonReproduceableProcessType;
import metadata.DataType;


/**
 * contain the default information regarding how to parse a source data file of this format into a specific type of Metadata object and its supporting data tables in RDB of a VisProjectDBContext;
 * @author tanxu
 *
 */
public interface FileFormat extends HasName, HasNotes, VisframeUDT, NonReproduceableProcessType{
	/**
	 * return id;
	 */
	@Override
	default FileFormatID getID() {
		return new FileFormatID(this.getName(),this.getDataType());
	}
	
	
	/**
	 * return the DataType of the Metadata object parsed from the data source file of this FileFormat;
	 * @return
	 */
	DataType getDataType();
	
	/**
	 * 
	 */
	@Override
	SimpleName getName();
	
	/**
	 * create and return a new FileFormat based on this FileFormat with the given new name and all other information exactly the same as this one;
	 * 
	 * @param newName
	 * @return
	 */
	FileFormat rename(SimpleName newName);
}
