package fileformat.record.between;

import fileformat.record.utils.PlainStringMarker;
import fileformat.record.utils.StringMarker;

/**
 * class for interleavingly distributed record file format; 
 * each record data's full string is residing in multiple separated string segments in the data source file of this format;
 * @author tanxu
 *
 */
public class InterleavingBetweenRecordStringFormat extends BetweenRecordStringFormatBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2918518243350083428L;
	
	////////////////////////////
	/**
	 * delimiter between segments
	 */
	private final StringMarker segmentDelimiter;//can be a plain string or regular expression
	/**
	 * 
	 */
	private final boolean everyRecordSegmentHasHeadingIDAttribute; //if true, the first attribute in the corresponding WithinRecordAttributeStringFormat must be the ID attribute which should always be the single primary key attribute
	/**
	 * 
	 */
	private final StringMarker headingIDAttributeStringDelimiter; //the heading id attribute string value will be extracted by split the full segment string by this delimiter and the first string split will be taken as the id attribute's string value;
	private final PlainStringMarker headingIDAttributeConcatenatingStringWithSucceedingRecordString;//must be plain string and must be covered by the attribute delimiter in the corresponding WithinRecordAttributeStringFormat
	
	/**
	 * be cautious with attribute delimiter,etc
	 */
	private final PlainStringMarker recordSegmentsCatenatingString;//must be plain string
	
	
	/**
	 * constructor
	 * @param numberOfHeadingLinesToSkip
	 * @param commentStringMarker
	 * @param recordDelimiter
	 * @param toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord
	 * @param segmentDelimiter cannot be null
	 * @param everyRecordSegmentHasHeadingIDAttribute null null
	 * @param headingIDAttributeStringDelimiter cannot be null if everyRecordSegmentHasHeadingIDAttribute is true;
	 * @param headingIDAttributeConcatenatingStringWithSucceedingRecordString cannot be null if everyRecordSegmentHasHeadingIDAttribute is true
	 * @param recordSegmentsCatenatingString not null
	 */
	public InterleavingBetweenRecordStringFormat(
			int numberOfHeadingLinesToSkip, PlainStringMarker commentStringMarker,
			StringMarker recordDelimiter, boolean toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord,
			///
			StringMarker segmentDelimiter,
			boolean everyRecordSegmentHasHeadingIDAttribute,
			StringMarker headingIDAttributeStringDelimiter,
			PlainStringMarker headingIDAttributeConcatenatingStringWithSucceedingRecordString,
			PlainStringMarker recordSegmentsCatenatingString
			
			) {
		super(numberOfHeadingLinesToSkip, commentStringMarker, recordDelimiter,
				toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord);
		//validation
		if(segmentDelimiter==null) {
			throw new IllegalArgumentException("given segmentDelimiter is null");
		}
		if(everyRecordSegmentHasHeadingIDAttribute) {
			if(headingIDAttributeStringDelimiter==null) {
				throw new IllegalArgumentException("given headingIDAttributeStringDelimiter is null when everyRecordSegmentHasHeadingIDAttribute is true");
			}
			if(headingIDAttributeConcatenatingStringWithSucceedingRecordString==null) {
				throw new IllegalArgumentException("given headingIDAttributeConcatenatingStringWithSucceedingRecordString is null when everyRecordSegmentHasHeadingIDAttribute is true");
			}
			
		}
		if(recordSegmentsCatenatingString==null) {
			throw new IllegalArgumentException("given recordSegmentsCatenatingString is null");
		}
		
		
		
		/////////////////////////
		this.segmentDelimiter = segmentDelimiter;
		this.everyRecordSegmentHasHeadingIDAttribute = everyRecordSegmentHasHeadingIDAttribute;
		this.headingIDAttributeStringDelimiter = headingIDAttributeStringDelimiter;
		this.headingIDAttributeConcatenatingStringWithSucceedingRecordString = headingIDAttributeConcatenatingStringWithSucceedingRecordString;
		this.recordSegmentsCatenatingString = recordSegmentsCatenatingString;
	}
	
	/**
	 * returns the StringMarker for segment delimiter
	 * @return
	 */
	public StringMarker getSegmentDelimiter() {
		return this.segmentDelimiter;
	}
	
	/**
	 * returns whether every segment of a record string is starting with an id attribute each with a distinct value for different records;
	 * if true, the mandatory simple attribute with index 0 of this format should be the id attribute and 
	 * it must be the only attribute in the primary key of the resulted data table (which should be validated in the RecordDataFileFormat constructor and the RecordDataImporter constructor);
	 * @return
	 */
	public boolean everyRecordSegmentHasHeadingIDAttribute() {
		return this.everyRecordSegmentHasHeadingIDAttribute;
	}
	
	
	//only relevant when everyRecordSegmentHasHeadingIDAttribute is true; could be different from the mandatory attribute delimiter string defined in the corresponding RecordAttributeStringFormatAndParser;
	/**
	 * returns the 
	 * @return
	 */
	public StringMarker getHeadingIDAttributeStringDelimiter() {
		return this.headingIDAttributeStringDelimiter;
	}
	
	//only relevant when everyRecordSegmentHasHeadingIDAttribute() is true, and must be consistent with the mandatory attribute delimiter defined in the corresponding RecordAttributeStringFormatAndParser;
	public PlainStringMarker getHeadingIDAttributeConatenatingStringWithSucceedingRecordString() {
		return this.headingIDAttributeConcatenatingStringWithSucceedingRecordString;
	}
	
	
	//note that string of a single attribute could be divided into several segments;
	public PlainStringMarker getRecordSegmentsCatenatingString() {
		return this.recordSegmentsCatenatingString;
	}

	
	////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (everyRecordSegmentHasHeadingIDAttribute ? 1231 : 1237);
		result = prime * result + ((headingIDAttributeConcatenatingStringWithSucceedingRecordString == null) ? 0
				: headingIDAttributeConcatenatingStringWithSucceedingRecordString.hashCode());
		result = prime * result
				+ ((headingIDAttributeStringDelimiter == null) ? 0 : headingIDAttributeStringDelimiter.hashCode());
		result = prime * result
				+ ((recordSegmentsCatenatingString == null) ? 0 : recordSegmentsCatenatingString.hashCode());
		result = prime * result + ((segmentDelimiter == null) ? 0 : segmentDelimiter.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof InterleavingBetweenRecordStringFormat))
			return false;
		InterleavingBetweenRecordStringFormat other = (InterleavingBetweenRecordStringFormat) obj;
		if (everyRecordSegmentHasHeadingIDAttribute != other.everyRecordSegmentHasHeadingIDAttribute)
			return false;
		if (headingIDAttributeConcatenatingStringWithSucceedingRecordString == null) {
			if (other.headingIDAttributeConcatenatingStringWithSucceedingRecordString != null)
				return false;
		} else if (!headingIDAttributeConcatenatingStringWithSucceedingRecordString
				.equals(other.headingIDAttributeConcatenatingStringWithSucceedingRecordString))
			return false;
		if (headingIDAttributeStringDelimiter == null) {
			if (other.headingIDAttributeStringDelimiter != null)
				return false;
		} else if (!headingIDAttributeStringDelimiter.equals(other.headingIDAttributeStringDelimiter))
			return false;
		if (recordSegmentsCatenatingString == null) {
			if (other.recordSegmentsCatenatingString != null)
				return false;
		} else if (!recordSegmentsCatenatingString.equals(other.recordSegmentsCatenatingString))
			return false;
		if (segmentDelimiter == null) {
			if (other.segmentDelimiter != null)
				return false;
		} else if (!segmentDelimiter.equals(other.segmentDelimiter))
			return false;
		return true;
	}
	
	
}
