package importer.record.within;

import fileformat.record.within.StringDelimitedRecordAttributeStringFormat;
import fileformat.record.within.WithinRecordAttributeStringFormatBase;

public class WithinRecordAttributeStringParserFactory {
	
	/**
	 * 
	 * @param withinRecordAttributeStringFormat
	 * @return
	 */
	public static WithinRecordAttributeStringParserBase makeParser(WithinRecordAttributeStringFormatBase withinRecordAttributeStringFormat) {
		if(withinRecordAttributeStringFormat instanceof StringDelimitedRecordAttributeStringFormat) {
			return new StringDelimitedRecordAttributeStringParser((StringDelimitedRecordAttributeStringFormat)withinRecordAttributeStringFormat);
		}else {
			throw new IllegalArgumentException("invalid type of WithinRecordAttributeStringFormat");
		}
		
	}
}
