package fileformat.record.within;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import basic.SimpleName;
import fileformat.record.RecordDataFileFormat.PrimaryKeyAttributeNameSet;
import fileformat.record.attribute.AbstractRecordAttributeFormat;
import fileformat.record.attribute.PrimitiveRecordAttributeFormat;
import fileformat.record.attribute.TagFormat;
import fileformat.record.utils.PlainStringMarker;

/**
 * base class for structural information regarding how attributes are composing a record for a RecordDataFileFormat
 * and methods to extract all primitive type attributes from a given full record string of this format;
 * @author tanxu
 *
 */
public abstract class WithinRecordAttributeStringFormatBase implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7060925992796417240L;
	
	///////////////////////////////
	private final List<AbstractRecordAttributeFormat> orderedListOfMandatoryAttribute;//could be either primitive type attribute or composite attribute
	private final PrimaryKeyAttributeNameSet defaultPrimaryKeyAttributeNameSet;//could contain primitive attribute and/or tag attributes(either from the composite attribute or tailing tag attributes?)
	/**
	 * attribute value string that indicates the attribute is null valued;
	 * only applicable for {@link PrimitiveRecordAttributeFormat}
	 */
	private final PlainStringMarker nullValueMandatoryAttributeString;
	private final boolean hasTailingTagAttributes;
	private final TagFormat tailingTagAttributesFormat;
	
	private final boolean toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute;
	private final PlainStringMarker concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute;
	
	///////////////////////////////////
	/**
	 * set of mandatory primitive attribute name in this {@link WithinRecordAttributeStringFormatBase}
	 */
	private transient Set<SimpleName> mandatoryPrimitiveRecordAttributeNameSet;
	/**
	 * constructor
	 * 
	 * validations
	 * 1. toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute is only relevant when hasTailingTagAttributes is false 
	 * and the last mandatory RecordAttribute is of SimpleRecordAttribute type;??
	 * 
	 * 
	 * 
	 * @param orderedListOfMandatoryAttribute cannot be null or empty; the name of all SimpleRecordAttribute type attributes must be distinct;
	 * @param defaultPrimaryKeyAttributeNameSet cannot be null; primary key attributes from primitive attributes should be found in orderedListOfMandatoryAttribute;
	 * @param nullValueMandatoryAttributeString ?
	 * @param hasTailingTagAttributes not null
	 * @param tailingTagAttributesFormat cannot be null if hasTailingTagAttributes is true; must be null otherwise;
	 * @param toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute only relevant if hasTailingTagAttributes is false;
	 * @param concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute cannot be null if hasTailingTagAttributes is false and toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute is true;
	 */
	public WithinRecordAttributeStringFormatBase(
			List<AbstractRecordAttributeFormat> orderedListOfMandatoryAttribute,
			PrimaryKeyAttributeNameSet defaultPrimaryKeyAttributeNameSet,
			PlainStringMarker nullValueMandatoryAttributeString,
			boolean hasTailingTagAttributes,
			TagFormat tailingTagAttributesFormat,
			boolean toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute,
			PlainStringMarker concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute
			) {
		
		if(orderedListOfMandatoryAttribute==null||orderedListOfMandatoryAttribute.isEmpty()) {
			throw new IllegalArgumentException("given orderedListOfMandatoryAttribute is null or empty");
		}
		if(hasTailingTagAttributes) {
			if(tailingTagAttributesFormat==null) {
				throw new IllegalArgumentException("given tailingTagAttributesFormatAndParser is null when hasTailingTagAttributes is true");
			}
		}
		if(toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute) {
			if(concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute==null) {
				throw new IllegalArgumentException("given concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute is null when toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute is true");
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		this.orderedListOfMandatoryAttribute = orderedListOfMandatoryAttribute;
		this.defaultPrimaryKeyAttributeNameSet = defaultPrimaryKeyAttributeNameSet;
		this.nullValueMandatoryAttributeString = nullValueMandatoryAttributeString;
		this.hasTailingTagAttributes = hasTailingTagAttributes;
		this.tailingTagAttributesFormat = tailingTagAttributesFormat;
		this.toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute = toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute;
		this.concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute = concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute;
		
	}
	
	/**
	 * returns the ordered list of all leading mandatory RecordAttribute except for the tailing tag attributes if exist;
	 * the string for those attributes MUST be present in every valid record string in the same order as they appear in this list; 
	 * the name of all SimpleRecordAttribute type attributes must be distinct;
	 * @return
	 */
	public List<AbstractRecordAttributeFormat> getOrderedListOfMandatoryAttribute(){
		return this.orderedListOfMandatoryAttribute;
	}
	
	/**
	 * return the PrimaryKeyAttributeNameSet of this RecordAttributeStringFormatAndParser;
	 * @return
	 */
	public PrimaryKeyAttributeNameSet getDefaultPrimaryKeyAttributeNameSet() {
		return this.defaultPrimaryKeyAttributeNameSet;
	}
	
	
	
	/**
	 * returns the PlainStringMarker that indicates a mandatory RecordAttribute is null valued in the record string;
	 * if null, all mandatory RecordAttribute cannot be null valued; otherwise, set the value of the mandatory RecordAttribute with this string value to null;
	 * @return
	 */
	public PlainStringMarker getNullValueMandatoryAttributeString() {
		return this.nullValueMandatoryAttributeString;
	}
	
	/**
	 * check if the given attributeValueString is null valued by checking with the {@link #nullValueMandatoryAttributeString}
	 * @param attributeValueString
	 * @return
	 */
	public boolean isNullValuedString(String attributeValueString) {
		if(this.getNullValueMandatoryAttributeString()==null) {
			return false;
		}else {
			return this.getNullValueMandatoryAttributeString().getStringValue().equals(attributeValueString);
		}
	}
	
	/**
	 * returns whether there are arbitrary number of strings for tag attributes delimited the same way as the leading mandatory RecordAttributes at the end of each record string;
	 * if true, the TagFormatAndParser must be set; 
	 * @return
	 */
	public boolean hasTailingTagAttributes() {
		return this.hasTailingTagAttributes;
	}
	
	/**
	 * returns the TagFormatAndParser for tailing tag attributes if {@link hasTailingTagAttributes()} returns true;
	 * @return
	 */
	public TagFormat getTailingTagAttributesFormat() {
		return this.tailingTagAttributesFormat;
	}
	
	
	/**
	 * returns whether or not to merge tailing extra strings that are delimited the same way as the leading mandatory RecordAttributes to the string value of last mandatory SimpleRecordAttribute; 
	 * only relevant when {@link hasTailingTagAttributes()} returns false and the last mandatory RecordAttribute is of SimpleRecordAttribute type;
	 * ignore the tailing extra delimited strings if false;
	 * @return
	 */
	public boolean isToMergeTailingExtraDelimitedStringsToLastMandatoryAttribute() {
		return this.toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute;
	}
	
	/**
	 * return the PlainStringMarker used to concatenate the tailing extra delimited strings to the last mandatory SimpleRecordAttribute 
	 * if {@link hasTailingTagAttributes()} returns false and {@link isToMergeTailingExtraDelimitedStringsToLastMandatoryAttribute()} returns true;
	 * @return
	 */
	public PlainStringMarker getConcatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute() {
		return this.concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute;
	}
	
	/**
	 * return the set of mandatory primitive attribute names
	 * @return
	 */
	public Set<SimpleName> getMandatoryPrimitiveRecordAttributeNameSet() {
		if(this.mandatoryPrimitiveRecordAttributeNameSet == null) {
			this.mandatoryPrimitiveRecordAttributeNameSet = new HashSet<>();
			
			for(AbstractRecordAttributeFormat attribute:this.getOrderedListOfMandatoryAttribute()) {
				if(attribute instanceof PrimitiveRecordAttributeFormat) {
					this.mandatoryPrimitiveRecordAttributeNameSet.add(((PrimitiveRecordAttributeFormat)attribute).getName());
				}
			}
		}
		
		return this.mandatoryPrimitiveRecordAttributeNameSet;
	}

	
	public boolean isStringDelimited() {
		return this instanceof StringDelimitedRecordAttributeStringFormat;
	}

	
	
	
	///////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute == null) ? 0
						: concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute.hashCode());
		result = prime * result
				+ ((defaultPrimaryKeyAttributeNameSet == null) ? 0 : defaultPrimaryKeyAttributeNameSet.hashCode());
		result = prime * result + (hasTailingTagAttributes ? 1231 : 1237);
		result = prime * result
				+ ((nullValueMandatoryAttributeString == null) ? 0 : nullValueMandatoryAttributeString.hashCode());
		result = prime * result
				+ ((orderedListOfMandatoryAttribute == null) ? 0 : orderedListOfMandatoryAttribute.hashCode());
		result = prime * result + ((tailingTagAttributesFormat == null) ? 0 : tailingTagAttributesFormat.hashCode());
		result = prime * result + (toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof WithinRecordAttributeStringFormatBase))
			return false;
		WithinRecordAttributeStringFormatBase other = (WithinRecordAttributeStringFormatBase) obj;
		if (concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute == null) {
			if (other.concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute != null)
				return false;
		} else if (!concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute
				.equals(other.concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute))
			return false;
		if (defaultPrimaryKeyAttributeNameSet == null) {
			if (other.defaultPrimaryKeyAttributeNameSet != null)
				return false;
		} else if (!defaultPrimaryKeyAttributeNameSet.equals(other.defaultPrimaryKeyAttributeNameSet))
			return false;
		if (hasTailingTagAttributes != other.hasTailingTagAttributes)
			return false;
		if (nullValueMandatoryAttributeString == null) {
			if (other.nullValueMandatoryAttributeString != null)
				return false;
		} else if (!nullValueMandatoryAttributeString.equals(other.nullValueMandatoryAttributeString))
			return false;
		if (orderedListOfMandatoryAttribute == null) {
			if (other.orderedListOfMandatoryAttribute != null)
				return false;
		} else if (!orderedListOfMandatoryAttribute.equals(other.orderedListOfMandatoryAttribute))
			return false;
		if (tailingTagAttributesFormat == null) {
			if (other.tailingTagAttributesFormat != null)
				return false;
		} else if (!tailingTagAttributesFormat.equals(other.tailingTagAttributesFormat))
			return false;
		if (toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute != other.toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute)
			return false;
		return true;
	}
	
	
}
