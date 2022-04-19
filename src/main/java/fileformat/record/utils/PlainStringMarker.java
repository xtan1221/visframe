package fileformat.record.utils;


/**
 * a plain string
 * @author tanxu
 * 
 */
public class PlainStringMarker extends StringMarker {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2373864669711702536L;
	
	///////////////////////
	
	/**
	 * constructor
	 * @param stringValue
	 * @param caseSensitive
	 */
	public PlainStringMarker(String stringValue, boolean caseSensitive) {
		super(stringValue, caseSensitive);
	}
	
	@Override
	public String getStringValue() {
		return this.underlyingStringValue;
	}
	
	@Override
	public String toString() {
		return "PlainStringMarker [underlyingStringValue=" + underlyingStringValue + ", isCaseSensitive()="
				+ isCaseSensitive() + "]";
	}

	
	////////////////////////
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof PlainStringMarker))
			return false;
		return true;
	}
	
}
