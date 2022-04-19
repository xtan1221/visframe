package fileformat.record.between;

import fileformat.record.utils.PlainStringMarker;
import fileformat.record.utils.StringMarker;


/**
 * class for sequentially distributed record file format; 
 * each record data's full string is residing in a single string segment in the data source file of this format;
 * @author tanxu
 *
 */
public class SequentialBetweenRecordStringFormat extends BetweenRecordStringFormatBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6563585135962080971L;
	
	/**
	 * constructor
	 * @param numberOfHeadingLinesToSkip
	 * @param commentStringMarker
	 * @param recordDelimiter
	 * @param toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord
	 */
	public SequentialBetweenRecordStringFormat(
			int numberOfHeadingLinesToSkip, 
			PlainStringMarker commentStringMarker,
			StringMarker recordDelimiter, 
			boolean toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord
			) {
		super(numberOfHeadingLinesToSkip, commentStringMarker, recordDelimiter,
				toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord);
		
	}
	
}
