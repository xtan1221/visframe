package fileformat.record.utils;

import java.io.Serializable;

/**
 * container class for a delimiter or concatenating string or identifier string
 * exclusively used in record data file format
 * @author tanxu
 *
 */
public abstract class StringMarker implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6739017470646851759L;
	
	
	/////////////////////
	protected final String underlyingStringValue;
	private final boolean caseSensitive;
	
	/**
	 * constructor
	 * @param underlyingStringValue
	 */
	StringMarker(String underlyingStringValue, boolean caseSensitive){
		if(underlyingStringValue==null) {
			throw new IllegalArgumentException("stringValue is null");
		}
		
		this.underlyingStringValue = underlyingStringValue;
		this.caseSensitive = caseSensitive;
	}
	
	/**
	 * return the string value that is to be used in string parsing
	 * @return
	 */
	public abstract String getStringValue();
	
	
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	
	/////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (caseSensitive ? 1231 : 1237);
		result = prime * result + ((underlyingStringValue == null) ? 0 : underlyingStringValue.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof StringMarker))
			return false;
		StringMarker other = (StringMarker) obj;
		if (caseSensitive != other.caseSensitive)
			return false;
		if (underlyingStringValue == null) {
			if (other.underlyingStringValue != null)
				return false;
		} else if (!underlyingStringValue.equals(other.underlyingStringValue))
			return false;
		return true;
	}
	
}
