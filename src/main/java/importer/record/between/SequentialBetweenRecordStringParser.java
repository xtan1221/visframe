package importer.record.between;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import fileformat.record.between.SequentialBetweenRecordStringFormat;
import fileformat.record.utils.StringMarkerUtils;
import importer.utils.StringUtils;


/**
 * parser for the data file of a RecordDataFileFormat whose BetweenRecordStringFormat is SequentialRecordStringFormat;
 * 
 * this parser will extract complete strings for each record from the data file based on the {@link SequentialBetweenRecordStringFormat};
 * 
 * 
 * @author tanxu
 *	
 */
public class SequentialBetweenRecordStringParser extends BetweenRecordStringParserBase{
	public static final int BATCH_SIZE = 1000; //related with the available memory size; should not be directly set by the user;
	
	////////////////
	private final SequentialBetweenRecordStringFormat betweenRecordStringFormat;
	
	/////////////////////
	private BufferedReader bufferedReader;//contains the current pointer location in the data file 
	
	private String currentRecordString;
	
	private Queue<String> queueOfCurrentBatchOfRecordString;
	
	private boolean fileEndReached;

	
	/**
	 * constructor
	 * @param dataFile
	 * @param betweenRecordStringFormat
	 * @throws IOException 
	 */
	public SequentialBetweenRecordStringParser(File dataFile,SequentialBetweenRecordStringFormat betweenRecordStringFormat) throws IOException {
		super(dataFile);
		
		this.betweenRecordStringFormat = betweenRecordStringFormat;
		
		this.initialize();
	}
	
	@Override
	SequentialBetweenRecordStringFormat getBetweenRecordStringFormat() {
		return betweenRecordStringFormat;
	}
	
	/**
	 * initialize the BufferedReader and parse the first batch of record string
	 * @throws IOException 
	 */
	@Override
	protected void initialize() throws IOException {
		this.fileEndReached = false;
		this.queueOfCurrentBatchOfRecordString = new LinkedList<>();
		this.currentRecordString = "";
		
		
		this.bufferedReader = new BufferedReader(new FileReader(this.dataFile));
		
		//skip heading lines if needed
		if(this.getBetweenRecordStringFormat().getNumberOfHeadingLinesToSkip()>0) {
	    	int i=this.getBetweenRecordStringFormat().getNumberOfHeadingLinesToSkip();
	    	
	    	while(i>0) {
	    		this.bufferedReader.readLine();
	    		i--;
	    	}
	    }
		
		this.continueParsingNextBatch();
			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	}
	
	/**
	 * continue to read and parse the data content from the data file starting from the ending point of last batch (in the BufferedReader);
	 * 
	 * invoked when the queueOfCurrentBatchOfRecordString is empty but fileEndReached is false;
	 * 
	 * if the file end is reached in the process, set the fileEndReached to true;
	 * @throws IOException 
	 */
	private void continueParsingNextBatch() throws IOException {
		String line;
		while ((line = this.bufferedReader.readLine()) != null){ 
			//always trim
			line = line.trim();
//		    System.out.println(line); 
		    //remove comment tailing line if needed
		    if(StringMarkerUtils.contains(line, this.getBetweenRecordStringFormat().getCommentingStringMarker())) {
		    	line = StringMarkerUtils.removeTrailing(line, this.getBetweenRecordStringFormat().getCommentingStringMarker(), true);
		    }
		    
//		    System.out.print(this.getBetweenRecordStringFormat().getRecordDelimiter().getStringValue());
//		    System.out.print(StringUtils.NEW_LINE_STRING);
//		    if(StringMarkerUtils.isNewLineCharacter(StringEscapeUtils.unescapeJava(this.getBetweenRecordStringFormat().getRecordDelimiter().getStringValue()))) {
		    if(StringMarkerUtils.isNewLineCharacter(this.getBetweenRecordStringFormat().getRecordDelimiter())) {
				
		    	this.currentRecordString = this.currentRecordString.concat(line);
		    	
		    	if(this.currentRecordString.isEmpty()) {
		    		//do nothing
		    	}else {
			    	//currentRecordString is a full record line;
			    	this.queueOfCurrentBatchOfRecordString.add(this.currentRecordString);
			    	//reset
			    	this.currentRecordString = "";
		    	}
		    }else {//
		    	if(this.getBetweenRecordStringFormat().isToKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord()) {
			    	this.currentRecordString = this.currentRecordString.concat(line).concat(StringUtils.NEW_LINE_STRING);
			    }else {
			    	this.currentRecordString = this.currentRecordString.concat(line);
			    }
		    	
		    	//parse record strings if containing record delimiter
			    if(StringMarkerUtils.contains(this.currentRecordString, this.getBetweenRecordStringFormat().getRecordDelimiter())) {
			    	//note that the last element in the returned string array is always a incomplete record string
			    	List<String> splits = StringMarkerUtils.split(this.currentRecordString, this.getBetweenRecordStringFormat().getRecordDelimiter(),false,true);
			    	
			    	for(int i=0;i<splits.size()-1;i++) {//
			    		this.queueOfCurrentBatchOfRecordString.add(splits.get(i));
			    	}
			    	
			    	this.currentRecordString = splits.get(splits.size()-1);
			    	
			    }
		    }
		    
		    if(this.queueOfCurrentBatchOfRecordString.size()>=BATCH_SIZE) {
				return;//current batch is full;
			}
		    
		    
//		    //always add new line character to the read line
//		    line = line.concat(StringUtils.NEW_LINE_STRING);
//		    
//		    //add new line character if needed
//		    //only relevant when record delimiter does not contain new line character
//		    if(this.getBetweenRecordStringFormat().isToKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord()) {
//		    	this.currentRecordString = this.currentRecordString.concat(line).concat(StringUtils.NEW_LINE_STRING);
//		    }else {
//		    	this.currentRecordString = this.currentRecordString.concat(line);
//		    }
//		    
//		    //parse record strings if containing record delimiter
//		    if(StringMarkerUtils.contains(this.currentRecordString, this.getBetweenRecordStringFormat().getRecordDelimiter())) {
//		    	//note that the last element in the returned string array is always a incomplete record string
//		    	List<String> splits = StringMarkerUtils.split(this.currentRecordString, this.getBetweenRecordStringFormat().getRecordDelimiter(),false,true);
//		    	
//		    	for(int i=0;i<splits.size()-1;i++) {//
//		    		this.queueOfCurrentBatchOfRecordString.add(splits.get(i));
//		    	}
//		    	
//		    	this.currentRecordString = splits.get(splits.size()-1);
//		    	
//		    	if(this.queueOfCurrentBatchOfRecordString.size()>=BATCH_SIZE) {
//					return;//current batch is full;
//				}
//		    }
		    
		}
		
		//this point can only be reached if the file end is reached
		this.fileEndReached = true;
	}
	
	
	/**
	 * return the next record string parsed from the data file; 
	 * return null if the file end is reached and no record string left in the queueOfCurrentBatchOfRecordString
	 * @throws IOException 
	 */
	@Override
	public String getNextRecordString() throws IOException {
		if(!this.fileEndReached && this.queueOfCurrentBatchOfRecordString.isEmpty()) {
//			try {
			this.continueParsingNextBatch();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
		
		return this.queueOfCurrentBatchOfRecordString.poll();//note that this return null if queue is empty;
	}
	
}
