package fileformat.record.within;

import java.util.List;

import fileformat.record.RecordDataFileFormat.PrimaryKeyAttributeNameSet;
import fileformat.record.attribute.AbstractRecordAttributeFormat;
import fileformat.record.attribute.TagFormat;
import fileformat.record.utils.PlainStringMarker;
import fileformat.record.utils.StringMarker;

/**
 * class for RecordAttributeStringFormatAndParser subtype whose attribute strings are delimited by string delimiter; 
 * @author tanxu
 */
public class StringDelimitedRecordAttributeStringFormat extends WithinRecordAttributeStringFormatBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7432870163336947490L;
	
	/////////////////////////////////
	private final boolean hasSingleMandatoryAttributeDelimiter;
	private final StringMarker singleMandatoryAttributeDelimiter;
	private final List<StringMarker> mandatoryAttributeDelimiterList;
	
	private final boolean recordStringStartingWithMandatoryAttributeDelimiter;
	
	/**
	 * 
	 * @param orderedListOfMandatoryAttribute
	 * @param defaultPrimaryKeyAttributeNameSet
	 * @param nullValueMandatoryAttributeString
	 * @param hasTailingTagAttributes
	 * @param tailingTagAttributesFormat
	 * @param toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute  only relevant if hasSingleMandatoryAttributeDelimiter is true
	 * @param concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute
	 * @param hasSingleMandatoryAttributeDelimiter if false, all attribute delimiter are explicitly given and the number should be equal to orderedListOfMandatoryAttribute.size() - 1
	 * @param singleMandatoryAttributeDelimiter cannot be null if hasSingleMandatoryAttributeDelimiter is true; must be null otherwise
	 * @param mandatoryAttributeDelimiterList cannot be null and empty if hasSingleMandatoryAttributeDelimiter is false, must be null otherwise;
	 * @param recordStringStartingWithMandatoryAttributeDelimiter not null
	 */
	public StringDelimitedRecordAttributeStringFormat(
			List<AbstractRecordAttributeFormat> orderedListOfMandatoryAttribute,
			PrimaryKeyAttributeNameSet defaultPrimaryKeyAttributeNameSet,
			PlainStringMarker nullValueMandatoryAttributeString, 
			boolean hasTailingTagAttributes,
			TagFormat tailingTagAttributesFormat,
			boolean toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute,
			PlainStringMarker concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute,
			///////
			boolean hasSingleMandatoryAttributeDelimiter,
			StringMarker singleMandatoryAttributeDelimiter,
			List<StringMarker> mandatoryAttributeDelimiterList,
			boolean recordStringStartingWithMandatoryAttributeDelimiter
			) {
		super(orderedListOfMandatoryAttribute, defaultPrimaryKeyAttributeNameSet, nullValueMandatoryAttributeString,
				hasTailingTagAttributes, tailingTagAttributesFormat,
				toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute,
				concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute);
		
		//validations
		if(hasSingleMandatoryAttributeDelimiter) {
			if(singleMandatoryAttributeDelimiter==null) {
				throw new IllegalArgumentException("given singleMandatoryAttributeDelimiter is null when hasSingleMandatoryAttributeDelimiter is true");
			}
		}else {
			if(mandatoryAttributeDelimiterList==null||mandatoryAttributeDelimiterList.isEmpty()) {
				throw new IllegalArgumentException("given mandatoryAttributeDelimiterList is null or empty when hasSingleMandatoryAttributeDelimiter is false");
			}
		}
		
		//
		if(!hasSingleMandatoryAttributeDelimiter) {
			int expectedAttributeDelimiterNum;
			if(recordStringStartingWithMandatoryAttributeDelimiter) {
				expectedAttributeDelimiterNum = orderedListOfMandatoryAttribute.size();
			}else {
				expectedAttributeDelimiterNum = orderedListOfMandatoryAttribute.size() - 1;
			}
			
			
			if(expectedAttributeDelimiterNum!=mandatoryAttributeDelimiterList.size()) {
				throw new IllegalArgumentException("inconsistent size of orderedListOfMandatoryAttribute and mandatoryAttributeDelimiterList are given");
			}
		}
		
		
		
		//delimiter related
		//nullValueMandatoryAttributeString, tailingTagAttributesFormatAndParser related, singleMandatoryAttributeDelimiter, mandatoryAttributeDelimiterList
		//attribute delimiter [==> tailingTagAttributesFormatAndParser related delimiter/string markers] [==> nullValueMandatoryAttributeString]
		
		
		//
		
		
		this.hasSingleMandatoryAttributeDelimiter = hasSingleMandatoryAttributeDelimiter;
		this.singleMandatoryAttributeDelimiter = singleMandatoryAttributeDelimiter;
		this.mandatoryAttributeDelimiterList = mandatoryAttributeDelimiterList;
		this.recordStringStartingWithMandatoryAttributeDelimiter = recordStringStartingWithMandatoryAttributeDelimiter;
	}

	
	////////////////////////////////////////
	public boolean hasSingleMandatoryAttributeDelimiter() {
		return hasSingleMandatoryAttributeDelimiter;
	}
	
	/**
	 * return the StringMarker that delimits the mandatory RecordAttributes in the record string;
	 * @return
	 */
	public StringMarker getSingleMandatoryAttributeDelimiter() {
		return this.singleMandatoryAttributeDelimiter;
	}

	public List<StringMarker> getMandatoryAttributeDelimiterList() {
		return mandatoryAttributeDelimiterList;
	}
	
	
	/**
	 * return whether each record string is starting with an attribute delimiter string; 
	 * @return
	 */
	public boolean isRecordStringStartingWithMandatoryAttributeDelimiter() {
		return this.recordStringStartingWithMandatoryAttributeDelimiter;
	}
	

	@Override
	public String toString() {
		return "StringDelimitedRecordAttributeStringFormat [hasSingleMandatoryAttributeDelimiter="
				+ hasSingleMandatoryAttributeDelimiter + ", singleMandatoryAttributeDelimiter="
				+ singleMandatoryAttributeDelimiter + ", mandatoryAttributeDelimiterList="
				+ mandatoryAttributeDelimiterList + ", recordStringStartingWithMandatoryAttributeDelimiter="
				+ recordStringStartingWithMandatoryAttributeDelimiter + "]";
	}

	////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (hasSingleMandatoryAttributeDelimiter ? 1231 : 1237);
		result = prime * result
				+ ((mandatoryAttributeDelimiterList == null) ? 0 : mandatoryAttributeDelimiterList.hashCode());
		result = prime * result + (recordStringStartingWithMandatoryAttributeDelimiter ? 1231 : 1237);
		result = prime * result
				+ ((singleMandatoryAttributeDelimiter == null) ? 0 : singleMandatoryAttributeDelimiter.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof StringDelimitedRecordAttributeStringFormat))
			return false;
		StringDelimitedRecordAttributeStringFormat other = (StringDelimitedRecordAttributeStringFormat) obj;
		if (hasSingleMandatoryAttributeDelimiter != other.hasSingleMandatoryAttributeDelimiter)
			return false;
		if (mandatoryAttributeDelimiterList == null) {
			if (other.mandatoryAttributeDelimiterList != null)
				return false;
		} else if (!mandatoryAttributeDelimiterList.equals(other.mandatoryAttributeDelimiterList))
			return false;
		if (recordStringStartingWithMandatoryAttributeDelimiter != other.recordStringStartingWithMandatoryAttributeDelimiter)
			return false;
		if (singleMandatoryAttributeDelimiter == null) {
			if (other.singleMandatoryAttributeDelimiter != null)
				return false;
		} else if (!singleMandatoryAttributeDelimiter.equals(other.singleMandatoryAttributeDelimiter))
			return false;
		return true;
	}
	
	
	
	
}
