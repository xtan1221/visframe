package symja;

import java.util.Set;

import basic.reproduce.SimpleReproducible;

/**
 * delegate to a SYMJA expression string with a set of variable names (may be 0) that an be evaluated to a primitive value if all variables are given a concrete value;
 * 
 * since symja variable name is case SENSITIVE, symja expression string should be case sensitive as well!
 * 
 * @author tanxu
 *
 */
public class VfSymjaExpressionString implements SimpleReproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = -144157621863565692L;
	
	
	
	//////////////////////////
	private final String valueString;
	
	
	/**
	 * cannot be 
	 * @param valueString
	 */
	public VfSymjaExpressionString(String valueString){
		//check if the expression string is valid
		try {
			SymjaUtils.validateExpressionString(valueString);
		}catch(Exception e) {
			throw new IllegalArgumentException("given expressionString contains invalid symja syntax:" + e.getMessage());
		}
		
		
		this.valueString = valueString;
	}

	/**
	 * @return the expressionString
	 */
	public String getValueString() {
		return valueString;
	}

	
	/**
	 * 
	 * @return
	 */
	public Set<VfSymjaVariableName> getVfSymjaVariableNameSet(){
		return SymjaUtils.extractVariableNameSet(valueString);
	}
	
	
	@Override
	public VfSymjaExpressionString reproduce() {
		return new VfSymjaExpressionString(this.valueString);
	}

	///////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((valueString == null) ? 0 : valueString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VfSymjaExpressionString))
			return false;
		VfSymjaExpressionString other = (VfSymjaExpressionString) obj;
		if (valueString == null) {
			if (other.valueString != null)
				return false;
		} else if (!valueString.equals(other.valueString))
			return false;
		return true;
	}
	
	
	
}
