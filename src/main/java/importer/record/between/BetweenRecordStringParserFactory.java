package importer.record.between;

import java.io.File;
import java.io.IOException;

import fileformat.record.between.BetweenRecordStringFormatBase;
import fileformat.record.between.InterleavingBetweenRecordStringFormat;
import fileformat.record.between.SequentialBetweenRecordStringFormat;

public class BetweenRecordStringParserFactory {
	/**
	 * make and return a BetweenRecordStringParserBase
	 * @param dataFile
	 * @param betweenRecordStringFormat
	 * @return
	 * @throws IOException 
	 */
	public static BetweenRecordStringParserBase makeParser(File dataFile, BetweenRecordStringFormatBase betweenRecordStringFormat) throws IOException {
		if(betweenRecordStringFormat instanceof SequentialBetweenRecordStringFormat) {
			return new SequentialBetweenRecordStringParser(dataFile,(SequentialBetweenRecordStringFormat)betweenRecordStringFormat);
		}else if(betweenRecordStringFormat instanceof InterleavingBetweenRecordStringFormat) {
			return new InterleavingBetweenRecordStringParser(dataFile, (InterleavingBetweenRecordStringFormat)betweenRecordStringFormat);
		}else {
			throw new IllegalArgumentException("invalid type of BetweenRecordStringFormat");
		}
	}
}
