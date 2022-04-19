package symja;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basic.reproduce.SimpleReproducible;


/**
 * ==============================
 * visframe allowed symja expression variable naming convention (tested in visframe but may not be the same with the symja's own convention, which may be less strict)!:
 * 1. must start with alphabetical letter;
 * 2. can only contain alphabetical letter and digit; underscore is NOT allowed!
 * for example, "2r4s" is equivalent to 2*r4s; 'r4s2dww22' is a valid variable name
 * 
 * 
 * ===============================
 * symja variable name are case sensitive, thus 'A' and 'a' in expression 'a+4-A' are considered as two different variables!!!!!!!!!!!!!!!!!!
 * @author tanxu
 *
 */
public class VfSymjaVariableName implements SimpleReproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8087911656082497659L;
	
	
	////////////////////////
	private static Pattern PATTERN = Pattern.compile("[^[a-zA-Z0-9]]"); //\w is A word character: [a-zA-Z_0-9]
	//
	private static Pattern PATTERN2 = Pattern.compile("^[\\d].*$");
	
	//////////////////
	private final String value;
	
	/**
	 * constructor
	 * @param value
	 */
	public VfSymjaVariableName(String value){
		if(value==null||value.isEmpty()) {
			throw new IllegalArgumentException("variableName cannot be null or empty");
		}
		Matcher m = PATTERN.matcher(value);
		if(m.find()) {
			throw new IllegalArgumentException("variableName can only contain alphabetical or digital character: [a-zA-Z0-9]");
		}
		Matcher m2 = PATTERN2.matcher(value);
		if(m2.matches()) {
			throw new IllegalArgumentException("stringValue must start with alphabetical character!");
		}
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	
	@Override
	public VfSymjaVariableName reproduce() {
		return new VfSymjaVariableName(this.value);
	}
	
	//////////////////////////////////
	@Override
	public String toString() {
		return this.value;
	}
	
	////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VfSymjaVariableName))
			return false;
		VfSymjaVariableName other = (VfSymjaVariableName) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}
