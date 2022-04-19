package fileformat.record.attribute;

import basic.SimpleName;
import basic.VfNotes;
import fileformat.record.utils.StringMarker;


/**
 * a RecordAttribute type that is composed of indeterminant number of tag attribute strings concatenated by tagDelimiter;
 * 
 * to simplify:
 * 		composite tag attribute always contain a single type of delimiter between tag attribute strings and the composite tag attribute string cannot start with a delimiter string
 * 
 * note that tag attribute does not have null valued string; if an tag attribute is null, simply leave it out from the record
 * 
 * 
 * @author tanxu
 *
 */
public class CompositeTagRecordAttributeFormat extends AbstractRecordAttributeFormat {
	/**
	 * 
	 */
	private static final long serialVersionUID = -133359147673966238L;
	
	
	//////////////////
	private final StringMarker tagDelimiter;//delimiter string between tag strings
	
	private final TagFormat tagFormat;
	
	
	/**
	 * constructor
	 * @param name cannot be null
	 * @param notes cannot be null
	 * @param tagFormat cannot be null
	 * @param tagDelimiter cannot be null
	 */
	public CompositeTagRecordAttributeFormat(
			SimpleName name, VfNotes notes,
			////
			TagFormat tagFormat,
			StringMarker tagDelimiter
			) {
		super(name, notes);
		// validations
		if(tagDelimiter==null) {
			throw new IllegalArgumentException("tagDelimiter cannot be null!");
		}
		
		if(tagFormat == null) {
			throw new IllegalArgumentException("tagFormat cannot be null");
		}
		
		
		this.tagFormat = tagFormat;
		
		this.tagDelimiter = tagDelimiter;
	}
	
	/**
	 * returns the TagFormatAndParser of this CompositeTagRecordAttributeAndParser
	 * @return
	 */
	public TagFormat getTagFormat() {
		return this.tagFormat;
	}
	
	/**
	 * returns the StringMarker for tag string delimiter
	 * @return
	 */
	public StringMarker getTagDelimiter() {
		return this.tagDelimiter;
	}

	
	///////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((tagDelimiter == null) ? 0 : tagDelimiter.hashCode());
		result = prime * result + ((tagFormat == null) ? 0 : tagFormat.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof CompositeTagRecordAttributeFormat))
			return false;
		CompositeTagRecordAttributeFormat other = (CompositeTagRecordAttributeFormat) obj;
		if (tagDelimiter == null) {
			if (other.tagDelimiter != null)
				return false;
		} else if (!tagDelimiter.equals(other.tagDelimiter))
			return false;
		if (tagFormat == null) {
			if (other.tagFormat != null)
				return false;
		} else if (!tagFormat.equals(other.tagFormat))
			return false;
		return true;
	}
	
	
}
