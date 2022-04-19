package importer.record.between;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fileformat.record.between.InterleavingBetweenRecordStringFormat;
import fileformat.record.utils.StringMarkerUtils;
import importer.utils.StringUtils;

/**
 * parser for interleaving record strings;
 * 
 * need to first read in data content of the the full file; then separate them into segment and piece together each record string from each segment;
 * 
 * 
 * @author tanxu
 *
 */
public class InterleavingBetweenRecordStringParser extends BetweenRecordStringParserBase{

	private final InterleavingBetweenRecordStringFormat betweenRecordStringFormat;
	
	
	/**
	 * records in each segment should have same order index
	 */
	private List<String> parsedRecordStringList;
	
	/**
	 * 
	 */
	private Integer nextRecordIndex; //
	
	
	/**
	 * constructor
	 * @param dataFile
	 * @param betweenRecordStringFormat
	 */
	public InterleavingBetweenRecordStringParser(File dataFile,InterleavingBetweenRecordStringFormat betweenRecordStringFormat) {
		super(dataFile);
		
		
		this.betweenRecordStringFormat = betweenRecordStringFormat;
		
		
		this.initialize();
	}

	
	@Override
	InterleavingBetweenRecordStringFormat getBetweenRecordStringFormat() {
		return this.betweenRecordStringFormat;
	}
	
	
	/**
	 * start the data file reading and parsing
	 * 
	 * 1. read in the full data content as a single string, with new line characters replaced by "\n"
	 * 2. split the full data string by segmentDelimiter into segments
	 * 3. for each segment, split it into each record segment string by recordDelimiter
	 * 4. deal with the heading id attribute if everyRecordSegmentHasHeadingIDAttribute
	 * 		1. first segment
	 * 		2. other segments
	 * 5. concatenate each segment of a record string into full record string; 
	 * 6. all parsed record string will be stored in parsedRecordOrderIndexStringMap
	 * 
	 */
	@Override
	protected void initialize() {
		this.parsedRecordStringList = new ArrayList<>();
		this.nextRecordIndex = 0;
		
		BufferedReader bufferedReader;
		String fullDataString = "";
		try {
			bufferedReader = new BufferedReader(new FileReader(this.dataFile));
			
			//skip heading lines if needed
			if(this.getBetweenRecordStringFormat().getNumberOfHeadingLinesToSkip()>0) {
		    	int i=this.getBetweenRecordStringFormat().getNumberOfHeadingLinesToSkip();
		    	
		    	while(i>0) {
		    		bufferedReader.readLine();
		    		i--;
		    	}
		    }
			
			//read the remaining of the file
			String line;
			while ((line = bufferedReader.readLine()) != null){
				//remove comment tailing line if needed
			    if(StringMarkerUtils.contains(line, this.getBetweenRecordStringFormat().getCommentingStringMarker())) {
			    	line = StringMarkerUtils.removeTrailing(line, this.getBetweenRecordStringFormat().getCommentingStringMarker(), true);
			    }
			    
			    //add new line character if needed
			    if(this.getBetweenRecordStringFormat().isToKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord()) {
			    	fullDataString = fullDataString.concat(line).concat(StringUtils.NEW_LINE_STRING);
			    }else {
			    	fullDataString = fullDataString.concat(line);
			    }
				
			}
			
			
			//process the full data string
			//1. split into segment group
			//2. for each segment, split into record segment
			List<String> segmentGroupList = StringMarkerUtils.split(fullDataString, this.getBetweenRecordStringFormat().getSegmentDelimiter(), false, false);
			
			
			boolean firstAdded=false; //first segment has been added or not;
			
			for(String segmentGroup:segmentGroupList) {
				//note that each segment group ends with the segment delimiter, instead of a record delimiter
				List<String> recordSegementList = StringMarkerUtils.split(segmentGroup, this.getBetweenRecordStringFormat().getRecordDelimiter(), false, false);
				
				if(!firstAdded) {//first segment
					for(int i=0;i<recordSegementList.size();i++) {
						if(this.getBetweenRecordStringFormat().everyRecordSegmentHasHeadingIDAttribute()) {//first segment, need to process the heading ID attribute
							List<String> splits = StringMarkerUtils.splitByFirstN(
									recordSegementList.get(i), 
									this.getBetweenRecordStringFormat().getHeadingIDAttributeStringDelimiter(), 
									1);
							
							//process the first segment of each record string 
							segmentGroupList.add(
									splits.get(0).
									concat(this.getBetweenRecordStringFormat().getHeadingIDAttributeConatenatingStringWithSucceedingRecordString().getStringValue()).
									concat(splits.get(1))
									);
						}else {
							parsedRecordStringList.add(segmentGroup);
						}
					}
					//
					firstAdded = true;
				}else {//not first segment
					for(int i=0;i<recordSegementList.size();i++) {
						if(this.getBetweenRecordStringFormat().everyRecordSegmentHasHeadingIDAttribute()) {//not first segment, only need to remove the heading id attribute 
							List<String> splits = StringMarkerUtils.splitByFirstN(
									recordSegementList.get(i), 
									this.getBetweenRecordStringFormat().getHeadingIDAttributeStringDelimiter(), 
									1);
							
							parsedRecordStringList.set(i, 
									parsedRecordStringList.get(i)
									.concat(this.getBetweenRecordStringFormat().getRecordSegmentsCatenatingString().getStringValue())
									.concat(splits.get(1))
									);
						}else {
							parsedRecordStringList.set(
									i, 
									parsedRecordStringList.get(i)
									.concat(this.getBetweenRecordStringFormat().getRecordSegmentsCatenatingString().getStringValue())
									.concat(recordSegementList.get(i))
									);
						}
					}
					
				}
				
			}
			
			//////////now identifiedRecordStringList contains the full set of 
			
			
			
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}

	
	
	
	/**
	 * return the next record string parsed from the data file by this InterleavingRecordStringParser
	 * 1. if the nextRecordIndex is out of the range of the parsedRecordStringMap, return null;
	 * 2. otherwise, return the corresponding record string
	 */
	@Override
	public String getNextRecordString() {
		try {
			String nextRecordString = this.parsedRecordStringList.get(this.nextRecordIndex);
			this.nextRecordIndex++;
			
			return nextRecordString;
			
		}catch(IndexOutOfBoundsException e) {
			return null;
		}
	}

}
