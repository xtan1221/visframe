package importer.record.within;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import exception.VisframeException;
import fileformat.record.attribute.AbstractRecordAttributeFormat;
import fileformat.record.attribute.CompositeTagRecordAttributeFormat;
import fileformat.record.attribute.PrimitiveRecordAttributeFormat;
import fileformat.record.utils.StringMarkerUtils;
import fileformat.record.within.StringDelimitedRecordAttributeStringFormat;
import importer.record.attribute.CompositeTagRecordAttributeParser;
import importer.record.attribute.TagFormatParser;

public class StringDelimitedRecordAttributeStringParser extends WithinRecordAttributeStringParserBase{
	private final StringDelimitedRecordAttributeStringFormat withinRecordAttributeStringFormat;
	
	///////////
	private TagFormatParser tailingTagFormatParser;
	
	/**
	 * constructor
	 * @param withinRecordAttributeStringFormat
	 */
	StringDelimitedRecordAttributeStringParser(StringDelimitedRecordAttributeStringFormat withinRecordAttributeStringFormat){
		this.withinRecordAttributeStringFormat = withinRecordAttributeStringFormat;
	}
	
	
	@Override
	StringDelimitedRecordAttributeStringFormat getWithinRecordAttributeStringFormat() {
		return this.withinRecordAttributeStringFormat;
	}
	
	
	/**
	 * 
	 * @return
	 */
	private TagFormatParser getTailingTagFormatParser() {
		if(this.tailingTagFormatParser == null) {
			this.tailingTagFormatParser = new TagFormatParser(this.getWithinRecordAttributeStringFormat().getTailingTagAttributesFormat());
		}
		return this.tailingTagFormatParser;
	}
	
	
	@Override
	public
	Map<PrimitiveRecordAttributeFormat, String> parse(String recordString) {
		Map<PrimitiveRecordAttributeFormat, String> ret = new LinkedHashMap<>();
		
		
		//split record string by attribute delimiters
		List<String> attributeStringList;
		
		if(this.getWithinRecordAttributeStringFormat().hasSingleMandatoryAttributeDelimiter()) {
			if(this.getWithinRecordAttributeStringFormat().isRecordStringStartingWithMandatoryAttributeDelimiter()) {
				attributeStringList = StringMarkerUtils.split(
						recordString, 
						this.getWithinRecordAttributeStringFormat().getSingleMandatoryAttributeDelimiter(), 
						false, //toKeepFirstEmpty, 
						true//toKeepLastEmpty??
						);
			}else {
				attributeStringList = StringMarkerUtils.split(
						recordString, 
						this.getWithinRecordAttributeStringFormat().getSingleMandatoryAttributeDelimiter(), 
						true, //toKeepFirstEmpty,
						true//toKeepLastEmpty??
						);
			}
		
		}else {
			attributeStringList = StringMarkerUtils.split(
					recordString, 
					this.getWithinRecordAttributeStringFormat().getMandatoryAttributeDelimiterList(), 
					this.getWithinRecordAttributeStringFormat().isRecordStringStartingWithMandatoryAttributeDelimiter()
					);
		}
		
		if(attributeStringList.size()<this.getWithinRecordAttributeStringFormat().getOrderedListOfMandatoryAttribute().size()) {
			throw new VisframeException("parsed attribute number is less than mandatory attribute number");
		}
		
		//parse the first n-1 mandatory attribute strings
		for(int i=0;i<this.getWithinRecordAttributeStringFormat().getOrderedListOfMandatoryAttribute().size()-1;i++) {
			AbstractRecordAttributeFormat mandatoryAttribute = this.getWithinRecordAttributeStringFormat().getOrderedListOfMandatoryAttribute().get(i);
			if(mandatoryAttribute instanceof PrimitiveRecordAttributeFormat) { //need to check if null valued string
				if(this.getWithinRecordAttributeStringFormat().isNullValuedString(attributeStringList.get(i))) {
					ret.put((PrimitiveRecordAttributeFormat)mandatoryAttribute, null);
				}else {
					ret.put((PrimitiveRecordAttributeFormat)mandatoryAttribute, attributeStringList.get(i));
				}
			}else {
				CompositeTagRecordAttributeFormat compositeTagFormat = (CompositeTagRecordAttributeFormat)mandatoryAttribute;
				ret.putAll(new CompositeTagRecordAttributeParser(compositeTagFormat).parse(attributeStringList.get(i)));
			}
			
		}
		
		//deal with tailing tags if exist
		if(!this.getWithinRecordAttributeStringFormat().hasTailingTagAttributes()) {//no tailing tag attribute
			//1. find out the string for last mandatory attribute
			String lastMandatoryAttributeString;
			if(!this.getWithinRecordAttributeStringFormat().isToMergeTailingExtraDelimitedStringsToLastMandatoryAttribute()) {//skip tailing extra delimited strings;
				
				lastMandatoryAttributeString = attributeStringList.get(this.getWithinRecordAttributeStringFormat().getOrderedListOfMandatoryAttribute().size()-1);
				
			}else {//merge tailing extra delimited string to tha last mandatory attribute
				lastMandatoryAttributeString = "";
				boolean nothingAddedYet = true;
				for(int i=this.getWithinRecordAttributeStringFormat().getOrderedListOfMandatoryAttribute().size()-1;i<attributeStringList.size();i++) {
					if(nothingAddedYet) {
						lastMandatoryAttributeString = 
								lastMandatoryAttributeString
								.concat(attributeStringList.get(i));
						nothingAddedYet = true;
					}else {
						lastMandatoryAttributeString = 
								lastMandatoryAttributeString
								.concat(this.getWithinRecordAttributeStringFormat().getConcatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute().getStringValue())
								.concat(attributeStringList.get(i));
					}
					
				}
			}
			
			//parse the attribute string
			AbstractRecordAttributeFormat lastMandatoryAttributeFormat = this.getWithinRecordAttributeStringFormat().getOrderedListOfMandatoryAttribute().get(this.getWithinRecordAttributeStringFormat().getOrderedListOfMandatoryAttribute().size()-1);
			
			if(lastMandatoryAttributeFormat instanceof PrimitiveRecordAttributeFormat) {
				ret.put((PrimitiveRecordAttributeFormat)lastMandatoryAttributeFormat, lastMandatoryAttributeString);
			}else {
				CompositeTagRecordAttributeFormat compositeTagFormat = (CompositeTagRecordAttributeFormat)lastMandatoryAttributeFormat;
				ret.putAll(new CompositeTagRecordAttributeParser(compositeTagFormat).parse(lastMandatoryAttributeString));
			}
			
			
		}else {//has tailing tag attribute
			for(int i=this.getWithinRecordAttributeStringFormat().getOrderedListOfMandatoryAttribute().size()-1;i<attributeStringList.size();i++) {
				ret.putAll(this.getTailingTagFormatParser().parse(attributeStringList.get(i)));
			}
			
		}
		
		
		
		
		return ret;
	}
	
	
}
