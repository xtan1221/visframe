package importer.record.between;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import fileformat.record.between.BetweenRecordStringFormatBase;

/**
 * read the data content from a file with a specific BetweenRecordStringFormat to parse out all the record data strings;
 * 
 * each subtype of BetweenRecordStringFormat class should have its own unique subclass that implements BetweenRecordStringParser;
 * 
 * @author tanxu
 *
 */
public abstract class BetweenRecordStringParserBase {
	/**
	 * 
	 */
	protected final File dataFile;
	
	/**
	 * file containing the data content
	 * @param dataFile data file
	 */
	BetweenRecordStringParserBase(File dataFile){
		this.dataFile = dataFile;
	}
	
	/**
	 * 
	 * @return
	 */
	abstract BetweenRecordStringFormatBase getBetweenRecordStringFormat();
	
	/**
	 * initialize reading and parsing
	 * 
	 * @param reader
	 * @return
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	protected abstract void initialize() throws FileNotFoundException, IOException;
	
	/**
	 * return the full string for next record with the bufferedReader; only relevant when bufferedReader is set;
	 * return null if the bufferedReader reaches the end of the file;
	 * throw UnsupportedOperationException if the bufferedReader is null;
	 * @return
	 * @throws IOException 
	 */
	public abstract String getNextRecordString() throws IOException;
}
