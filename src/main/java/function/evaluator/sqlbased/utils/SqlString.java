package function.evaluator.sqlbased.utils;

import basic.reproduce.SimpleReproducible;

/**
 * delegate to an sql string with aliased variables
 * 
 * for future implementation of sql syntax checking and validation as well as sql query string parsing;
 * 
 * variables could be table, column, or primitive variable, etc;
 * 
 * case sensitive; (should be for variable names, keywords, case insensitive, for quoted values, case sensitive!) TODO
 * @author tanxu
 *
 */
public class SqlString implements SimpleReproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4566014427825315262L;
	
	
	private final String valueString;
	
	/**
	 * 
	 * @param valueString
	 */
	public SqlString(String valueString){
		//Validations
		//TODO
		
		this.valueString = valueString;
	}
	

	public String getValueString() {
		return valueString;
	}


	@Override
	public SqlString reproduce() {
		return new SqlString(this.getValueString());
	}


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
		if (!(obj instanceof SqlString))
			return false;
		SqlString other = (SqlString) obj;
		if (valueString == null) {
			if (other.valueString != null)
				return false;
		} else if (!valueString.equals(other.valueString))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "SqlString [valueString=" + valueString + "]";
	}
	
	
	
}
