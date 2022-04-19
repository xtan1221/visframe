package importer.record.attribute;

import java.util.Map;

import fileformat.record.attribute.PrimitiveRecordAttributeFormat;

/**
 * interface for parser that extract attribute data from a data string
 * @author tanxu
 *
 */
public interface AttributeStringParser {
	Map<PrimitiveRecordAttributeFormat,String> parse(String dataString);
}
