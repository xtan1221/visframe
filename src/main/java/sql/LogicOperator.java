package sql;

/**
 * 
 * @author tanxu
 *
 */
public enum LogicOperator {
	AND("AND", false),
	OR("OR", false),
	NOT("NOT", true);
	
	private final String sqlStringValue;
	private final boolean unary; //if false, unary
	
	/**
	 * constructor
	 * @param sqlStringValue
	 */
	LogicOperator(String sqlStringValue, boolean unary){
		this.sqlStringValue = sqlStringValue;
		this.unary = unary;
	}
	
	public String getSqlStringValue() {
		return sqlStringValue;
	}
	
	public boolean isUnary() {
		return unary;
	}
	/**
	 * build a composite sql condition string with the given element condition strings linked by this LogicOperator;
	 * 
	 * @param conditionString1
	 * @param conditionString2 must be null if LogicOperator is unary;
	 * @return
	 */
	public String buildSqlConditionString(String conditionString1, String conditionString2) {
		if(this.equals(NOT)) {
			return "NOT (".concat(conditionString1).concat(")");
		}else if(this.equals(AND)){
			return conditionString1.concat(" AND ").concat(conditionString2);
		}else if(this.equals(OR)) {
			return conditionString1.concat(" OR ").concat(conditionString2);
		}else {
			throw new UnsupportedOperationException();
		}
	}
}
