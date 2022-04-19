package symja;

import java.util.HashMap;
import java.util.Map;

import basic.reproduce.SimpleReproducible;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;

/**
 * base class for visframe symja expression that is evaluated to a single primitive value;
 * 
 * note that all the variable name in this class are alias, thus {@link VfSymjaSinglePrimitiveOutputExpression} is {@link SimpleReproducible}
 * 
 * 
 * @author tanxu
 * 
 */
public class VfSymjaSinglePrimitiveOutputExpression implements SimpleReproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4814040859751476588L;
	
	/////////////////////////////
	/**
	 * type for the result of the expression
	 */
	private final VfDefinedPrimitiveSQLDataType sqlDataType;
	
	/**
	 * symja expression string with the alias names of variables;
	 * variable names are case SENSITIVE!!!!!
	 */
	private final VfSymjaExpressionString expressionString;
	
	/**
	 * input variable name and data type;
	 */
	private final Map<VfSymjaVariableName, VfDefinedPrimitiveSQLDataType> variableNameSQLDataTypeMap;
	
	
	/**
	 * constructor
	 * @param sqlDataType not null
	 * @param expressionString not empty; must be evaluated to a single primitive value;
	 * @param variableNameSQLDataTypeMap not null; can be empty if the expressionString is a constant value(trivial)
	 */
	public VfSymjaSinglePrimitiveOutputExpression(
			VfDefinedPrimitiveSQLDataType sqlDataType,
			VfSymjaExpressionString expressionString,
			Map<VfSymjaVariableName, VfDefinedPrimitiveSQLDataType> variableNameSQLDataTypeMap
			){
		//TODO validations
		if(!sqlDataType.isPrimitive())
			throw new IllegalArgumentException("given sqlDataType is not primitive!");
		
		
		//check if the expression string's type is the same as the given sqlDataType
		//TODO
		
		//check if all string name of the variables are present in the expressionString
		expressionString.getVfSymjaVariableNameSet().forEach(n->{
			if(!variableNameSQLDataTypeMap.keySet().contains(n)) {
				throw new IllegalArgumentException("variable in given expressionString is not found in the given variableNameSQLDataTypeMap:"+n);
			}
		});
		
		
		
		this.sqlDataType = sqlDataType;
		this.expressionString = expressionString;
		this.variableNameSQLDataTypeMap = variableNameSQLDataTypeMap;
	}
	
	
	/////////////////////////////////////////////
	/**
	 * calculate the string value of the symja expression string with the given variable string values;
	 * @param variableNameStringValueMap
	 * @return
	 */
	public String evaluate(Map<VfSymjaVariableName, String> variableNameStringValueMap) {
		//evaluate the symja expression string's value with the given string values of the variables
		return SymjaUtils.evaluate(this.expressionString.getValueString(), variableNameStringValueMap, this.getVariableNameSQLDataTypeMap());
	}
	
	////////////////////////////////////////
	
	/**
	 * @return the sqlDataType
	 */
	public VfDefinedPrimitiveSQLDataType getSqlDataType() {
		return sqlDataType;
	}


	/**
	 * @return the expressionString
	 */
	public VfSymjaExpressionString getExpressionString() {
		return expressionString;
	}


	public Map<VfSymjaVariableName, VfDefinedPrimitiveSQLDataType> getVariableNameSQLDataTypeMap() {
		return variableNameSQLDataTypeMap;
	}
	
	////////////////////////////
	/**
	 * reproduce and return a new VfSymjaExpression of this one;
	 */
	@Override
	public VfSymjaSinglePrimitiveOutputExpression reproduce() {
		Map<VfSymjaVariableName, VfDefinedPrimitiveSQLDataType> variableNameSQLDataTypeMap = new HashMap<>();
		
		for(VfSymjaVariableName vn:this.getVariableNameSQLDataTypeMap().keySet()) {
			variableNameSQLDataTypeMap.put(vn.reproduce(), this.getVariableNameSQLDataTypeMap().get(vn).reproduce());
		}
		
		return new VfSymjaSinglePrimitiveOutputExpression(
				this.getSqlDataType().reproduce(),
				this.getExpressionString().reproduce(),
				variableNameSQLDataTypeMap
				);
	}


	//////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expressionString == null) ? 0 : expressionString.hashCode());
		result = prime * result + ((sqlDataType == null) ? 0 : sqlDataType.hashCode());
		result = prime * result + ((variableNameSQLDataTypeMap == null) ? 0 : variableNameSQLDataTypeMap.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VfSymjaSinglePrimitiveOutputExpression))
			return false;
		VfSymjaSinglePrimitiveOutputExpression other = (VfSymjaSinglePrimitiveOutputExpression) obj;
		if (expressionString == null) {
			if (other.expressionString != null)
				return false;
		} else if (!expressionString.equals(other.expressionString))
			return false;
		if (sqlDataType == null) {
			if (other.sqlDataType != null)
				return false;
		} else if (!sqlDataType.equals(other.sqlDataType))
			return false;
		if (variableNameSQLDataTypeMap == null) {
			if (other.variableNameSQLDataTypeMap != null)
				return false;
		} else if (!variableNameSQLDataTypeMap.equals(other.variableNameSQLDataTypeMap))
			return false;
		return true;
	}
	
	
	

}
