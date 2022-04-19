package fileformat.record.within;

import java.util.List;
import java.util.Map;

import fileformat.record.RecordDataFileFormat.PrimaryKeyAttributeNameSet;
import fileformat.record.attribute.AbstractRecordAttributeFormat;
import fileformat.record.attribute.PrimitiveRecordAttributeFormat;
import fileformat.record.attribute.TagFormat;
import fileformat.record.utils.PlainStringMarker;


/**
 * class for RecordAttributeStringFormatAndParser subtype whose attribute strings are delimited by string lengths;
 *  
 * @author tanxu
 *
 */
public abstract class StringLengthDelimitedRecordAttributeStringFormat
		extends WithinRecordAttributeStringFormatBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4855380528805457134L;

	/**
	 * constructor
	 * @param orderedListOfMandatoryAttribute
	 * @param defaultPrimaryKeyAttributeNameSet
	 * @param nullValueMandatoryAttributeString
	 * @param hasTailingTagAttributes
	 * @param tailingTagAttributesFormat
	 * @param toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute
	 * @param concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute
	 */
	public StringLengthDelimitedRecordAttributeStringFormat(
			List<AbstractRecordAttributeFormat> orderedListOfMandatoryAttribute,
			PrimaryKeyAttributeNameSet defaultPrimaryKeyAttributeNameSet,
			PlainStringMarker nullValueMandatoryAttributeString, boolean hasTailingTagAttributes,
			TagFormat tailingTagAttributesFormat,
			boolean toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute,
			PlainStringMarker concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute) {
		super(orderedListOfMandatoryAttribute, defaultPrimaryKeyAttributeNameSet, nullValueMandatoryAttributeString,
				hasTailingTagAttributes, tailingTagAttributesFormat,
				toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute,
				concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute);
		// TODO Auto-generated constructor stub
	}

	
	abstract Map<PrimitiveRecordAttributeFormat, String> parse(String recordString);

}
