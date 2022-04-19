package fileformat.record.between;

import java.io.Serializable;

import fileformat.record.utils.PlainStringMarker;
import fileformat.record.utils.StringMarker;
import fileformat.record.utils.StringMarkerUtils;

/**
 * class that contains structural information regarding how a data file content string is composed of record strings; 
 * also methods to extract all record strings from a given data file reader object;
 * @author tanxu
 *
 */
public abstract class BetweenRecordStringFormatBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8113288255232312971L;
	
	////////////////////////////
	/**
	 * number of heading lines to skip no matter it is commented or not;
	 * 0 if none;
	 * note that this information is dominant to following;
	 * 
	 */
	private final int numberOfHeadingLinesToSkip;
	
	/**
	 * comment string marker indicating the succeeding part of the same line are comment, which should be removed when extracting data string;
	 * MUST be plain string {@link PlainStringMarker};
	 */
	private final PlainStringMarker commentStringMarker; //can only be plain string
	
	/**
	 * string delimiter that separate strings of different records;
	 * 
	 * could be either a plain string or a regular expression;
	 */
	private final StringMarker recordDelimiter; //
	
	/**
	 * whether the new line character between lines should be kept as part of the data string or not;
	 * 
	 * if true, all types of characters that represent a new line will be replaced with "\n";
	 * 
	 * this scenario occurs if new line character is used as attribute delimiter of the within record string format;
	 */
	private final boolean toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord; 
	
	
	/**
	 * constructor
	 * @param numberOfHeadingLinesToSkip if negative value, will be set to 0;
	 * @param commentStringMarker can be null if no comment string exist;
	 * @param recordDelimiter cannot be null;
	 * @param toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord only relevant when record delimiter does not contain new line character
	 */
	public BetweenRecordStringFormatBase(
			int numberOfHeadingLinesToSkip, 
			PlainStringMarker commentStringMarker, 
			StringMarker recordDelimiter,
			boolean toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord
			) {
		if(numberOfHeadingLinesToSkip<0) {
			throw new IllegalArgumentException("numberOfHeadingLinesToSkip cannot be negative");
		}
		
		if(recordDelimiter==null) {
			throw new IllegalArgumentException("recordDelimiter cannot be null");
		}
		
		if(StringMarkerUtils.isNewLineCharacter(recordDelimiter)){
			if(toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord) {
				throw new IllegalArgumentException("recordDelimiter is new line character but toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord is true");
			}
		}
		
		this.numberOfHeadingLinesToSkip = numberOfHeadingLinesToSkip;
		this.commentStringMarker = commentStringMarker;
		this.recordDelimiter = recordDelimiter;
		this.toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord = toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord;
		
	}
	
	
	/**
	 * returns number of heading lines to skip no matter it is commented or not;
	 * 
	 * @return
	 */
	public int getNumberOfHeadingLinesToSkip() {
		return this.numberOfHeadingLinesToSkip;
	}
	
	
	/**
	 * return PlainStringMarker indicating comment string; 
	 * if null, no comment string exist in the data source file of this format; 
	 * if non-null, always skip any content after the comment string on a line; 
	 * @return
	 */
	public PlainStringMarker getCommentingStringMarker() {
		return this.commentStringMarker;
	}
	
	
	/**
	 * return the record delimiter StringMarker; could be either plain string or a regular expression
	 * @return
	 */
	public StringMarker getRecordDelimiter() {
		return this.recordDelimiter;
	}
	
	
	/**
	 * whether to keep new line character that appear within string pieces of the same record; only relevant when new line character is not RECOGNIZED as record delimiter;
	 * if true, all types of new line characters will be replaced by \n and kept, 
	 * otherwise, will be removed before concatenating the pieces into a complete record string;
	 * 
	 * @return
	 */
	public boolean isToKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord() {
		return this.toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord;
	}

	
	public boolean isSequential() {
		return this instanceof SequentialBetweenRecordStringFormat;
	}


	//////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commentStringMarker == null) ? 0 : commentStringMarker.hashCode());
		result = prime * result + numberOfHeadingLinesToSkip;
		result = prime * result + ((recordDelimiter == null) ? 0 : recordDelimiter.hashCode());
		result = prime * result + (toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord ? 1231 : 1237);
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof BetweenRecordStringFormatBase))
			return false;
		BetweenRecordStringFormatBase other = (BetweenRecordStringFormatBase) obj;
		if (commentStringMarker == null) {
			if (other.commentStringMarker != null)
				return false;
		} else if (!commentStringMarker.equals(other.commentStringMarker))
			return false;
		if (numberOfHeadingLinesToSkip != other.numberOfHeadingLinesToSkip)
			return false;
		if (recordDelimiter == null) {
			if (other.recordDelimiter != null)
				return false;
		} else if (!recordDelimiter.equals(other.recordDelimiter))
			return false;
		if (toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord != other.toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord)
			return false;
		return true;
	}
	
}
