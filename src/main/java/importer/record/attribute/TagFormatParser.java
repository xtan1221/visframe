package importer.record.attribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basic.SimpleName;
import basic.VfNotes;
import fileformat.record.attribute.PrimitiveRecordAttributeFormat;
import fileformat.record.attribute.TagFormat;
import fileformat.record.utils.StringMarkerUtils;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;

public class TagFormatParser implements AttributeStringParser{
	private final TagFormat tagFormat;
	
	/**
	 * constructor
	 * @param tagFormat
	 */
	public TagFormatParser(TagFormat tagFormat){
		this.tagFormat = tagFormat;
	}
	
	/**
	 * parse the tag attribute string of format defined by this TagFormatAndParser, and return the created SimpleRecordAttribute and value string of it based on this TagFormatAndParser;
	 * @param tagAttributeString
	 * @return
	 */
	@Override
	public Map<PrimitiveRecordAttributeFormat, String> parse(String tagAttributeString){
		Map<PrimitiveRecordAttributeFormat, String> ret = new HashMap<>();
		
		List<String> splits = StringMarkerUtils.split(tagAttributeString, this.tagFormat.getComponentDelimiter(), true, true);
		
		SimpleName attributeName = new SimpleName(splits.get(this.tagFormat.getNameComponentStringIndex()));
		String attributeStringValue = splits.get(this.tagFormat.getValueComponentStringIndex());
		VfDefinedPrimitiveSQLDataType attributeDataType;
		//has data type indicator and the indicator string is in the map;
		if(this.tagFormat.hasDataTypeIndicatorComponent() && this.tagFormat.getDataTypeIndicatorComponentStringSQLDataTypeMap().containsKey(splits.get(this.tagFormat.getDataTypeIndicatorComponentStringIndex()))) {
			attributeDataType = this.tagFormat.getDataTypeIndicatorComponentStringSQLDataTypeMap().get(splits.get(this.tagFormat.getDataTypeIndicatorComponentStringIndex()));
		}else {
			attributeDataType = this.tagFormat.getDefaultSQLDataType();
		}
		
		//
		PrimitiveRecordAttributeFormat attribute = new PrimitiveRecordAttributeFormat(attributeName, VfNotes.makeVisframeDefinedVfNotes(), attributeDataType);
		
		ret.put(attribute, attributeStringValue);
		
		return ret;
	}
}
