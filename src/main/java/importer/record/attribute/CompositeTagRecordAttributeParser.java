package importer.record.attribute;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fileformat.record.attribute.CompositeTagRecordAttributeFormat;
import fileformat.record.attribute.PrimitiveRecordAttributeFormat;
import fileformat.record.attribute.TagFormat;
import fileformat.record.utils.StringMarkerUtils;

public class CompositeTagRecordAttributeParser implements AttributeStringParser{
	private final CompositeTagRecordAttributeFormat compositeTagRecordAttributeFormat;
	
	////////
	private TagFormatParser tagFormatParser;
	/**
	 * constructor
	 * @param compositeTagRecordAttributeFormat
	 */
	public CompositeTagRecordAttributeParser(CompositeTagRecordAttributeFormat compositeTagRecordAttributeFormat){
		this.compositeTagRecordAttributeFormat = compositeTagRecordAttributeFormat;
	}
	
	
	protected TagFormatParser getParser() {
		if(this.tagFormatParser == null) {
			this.tagFormatParser = new TagFormatParser(this.compositeTagRecordAttributeFormat.getTagFormat()); 
		}
		return this.tagFormatParser;
	}
	
	
	/**
	 * parse the given valid compositeTagAttributeString to extract all the tag SimpleRecordAttributes and their value strings;
	 * this method will first split the input string with the tagDelimiter,
	 * then for each tag attribute string, invoke the {@link TagFormat#parse(String)} method;
	 * @param compositeTagAttributeString
	 * @return
	 */
	@Override
	public Map<PrimitiveRecordAttributeFormat,String> parse(String compositeTagAttributeString){
		Map<PrimitiveRecordAttributeFormat,String> ret = new LinkedHashMap<>();
		
		List<String> tagAttributeStringList = StringMarkerUtils.split(
				compositeTagAttributeString, this.compositeTagRecordAttributeFormat.getTagDelimiter(), false, false);//empty split of tag attribute string is always removed;
				
		for(String tagAttributeString:tagAttributeStringList) {
			ret.putAll(getParser().parse(tagAttributeString));
		}
		
		return ret;
		
	}
}
