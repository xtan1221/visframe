package importer.record.within;

import java.util.Map;

import fileformat.record.attribute.PrimitiveRecordAttributeFormat;
import fileformat.record.within.WithinRecordAttributeStringFormatBase;
import importer.record.attribute.AttributeStringParser;

public abstract class WithinRecordAttributeStringParserBase implements AttributeStringParser{
	
	abstract WithinRecordAttributeStringFormatBase getWithinRecordAttributeStringFormat();
	
	/**
	 * returns the set of SimpleRecordAttributes and their value string parsed from the given valid full record string, 
	 * which could include mandatory SimpleRecordAttribute or discovered tag SimpleRecordAttributes or both;
	 * 
	 * @param recordString
	 * @return
	 */
	@Override
	public abstract Map<PrimitiveRecordAttributeFormat,String> parse(String recordString);
}
