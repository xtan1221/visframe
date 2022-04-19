package fileformat.record.utils;

/**
 * 
 * @author tanxu
 *
 */
public class RegexStringMarker extends StringMarker {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8776725752132161692L;
	
	/**
	 * constructor
	 * @param stringValue
	 */
	public RegexStringMarker(String stringValue, boolean caseSensitive) {
		super(stringValue,caseSensitive);
	}
	
	
//	/**
//	 * return the regular expression string of this RegexStringMarker, which may be different from the underlying string value;
//	 * @return
//	 */
//	public String getRegexStringValue() {
//		//java will take any string from TextField as plain string by default;
//		//thus if regular expression with escape \ is given, java will internally add another escape before it, which make the wanted regular expression not working;
//		//for example, if new line character '\n' is given and wanted in the TextField for a RegexStringMarker, 
//		//the internal java String retrieved by TextField.getText() method will actually be '\\n', which is not a new line character!
//		//StringEscapeUtils.unescapeJava(String) will remove the added escape by the TextField.getText() method to restore the original regular expression!
//		
//		return StringEscapeUtils.unescapeJava(super.getStringValue());
//	}

	
	@Override
	public String getStringValue() {
		//this method seems working only for "\\n" ==> "\n", "\\t"==>"\t", but not for "\\s" ==>"s"!!!!!!!
		//see {@link RegexStringMarkerTest#testRegexStringMarker()} for testing
//		return StringEscapeUtils.unescapeJava(this.underlyingStringValue);
		return this.underlyingStringValue;
	}


	@Override
	public String toString() {
		return "RegexStringMarker [underlyingStringValue=" + underlyingStringValue + ", isCaseSensitive()="
				+ isCaseSensitive() + "]";
	}


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
		if (!(obj instanceof RegexStringMarker))
			return false;
		return true;
	}

	
}
