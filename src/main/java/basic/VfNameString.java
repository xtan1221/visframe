package basic;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 1. cannot be empty string;
 * 2. can only contain alphabetic letter, digit letter, and underscore
 * 3. must start with alphabetic letter
 * 4. case insensitive
 * 5. has a maximal length
 * @author tanxu
 *	
 */
public abstract class VfNameString implements Serializable, Comparable<VfNameString>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6321297357321366671L;
	
	///////////////////////////////
	/**
	 * maximal length of string allowed
	 */
	public static final int MAX_STRING_LENGTH = 100;
	
	//can only contain letter, number and underscore
	public static Pattern PATTERN = Pattern.compile("[^(\\w)]"); //\w is A word character: [a-zA-Z_0-9] note here underscore is contained!!!!!
	//
	public static Pattern PATTERN2 = Pattern.compile("^[\\d_].*$");
	////////////////////////////
	private final String stringValue;
	
	
	/**
	 * constructor
	 * validations:
	 * 
	 * @param stringValue
	 */
	public VfNameString(String stringValue) {
		//TODO validations!!
		if(stringValue==null||stringValue.isEmpty()) {
			throw new IllegalArgumentException("stringValue cannot be null or empty");
		}
		
		Matcher m = PATTERN.matcher(stringValue);
		if(m.find()) {
			throw new IllegalArgumentException("stringValue can only contain word character: [a-zA-Z0-9] and underscore: _; ".concat(stringValue));
		}
		Matcher m2 = PATTERN2.matcher(stringValue);
		if(m2.matches()) {
			throw new IllegalArgumentException("stringValue must start with alphabetical character!");
		}
		
		if(stringValue.length()>MAX_STRING_LENGTH) {
			System.out.println(stringValue+" has length = "+Integer.toString(stringValue.length()));
			throw new IllegalArgumentException("stringValue must be shorter than "+Integer.toString(MAX_STRING_LENGTH));
		}
		
		
		this.stringValue = stringValue;
	}
	
	

//	/**
//	 * 
//	 */
//	public Reproduceable reproduce() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	
	
	public String getStringValue() {
		return this.stringValue;
	}
	
	
	/////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stringValue == null) ? 0 : stringValue.toUpperCase().hashCode());//case insensitive
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VfNameString other = (VfNameString) obj;
		if (stringValue == null) {
			if (other.stringValue != null)
				return false;
		} else if (!stringValue.equalsIgnoreCase(other.stringValue))
			return false;
		return true;
	}


	
	@Override
	public String toString() {
		return stringValue;
	}


	
	@Override
	public int compareTo(VfNameString o) {
		return this.stringValue.toUpperCase().compareTo(o.getStringValue().toUpperCase());
	}
	
	
}
