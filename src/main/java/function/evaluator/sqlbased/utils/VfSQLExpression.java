package function.evaluator.sqlbased.utils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.reproduce.Reproducible;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.group.CompositionFunctionGroup;
import function.variable.input.InputVariable;

/**
 * 
 * an expression for a term in SELECT clause or a whole WHERE clause;
 * a term in SELECT clause is a piece of string corresponding to a column of the resulted view/table; terms are normally separated by comma;
 * 
 * facilitate building of {@link SQLQueryBasedEvaluator};
 * 
 * ========================================================
 * note that {@link #aliasedExpressionString} rather than the sql string with real table/column names make {@link #reproduce()} method implementation much easier;
 * 
 * use alias name of input variables in the sql string;
 * 
 * Aggregate functions is NOT allowed (if needed, use a variable alias which represent a {@link SQLAggregateFunctionBasedInputVariable})
 * 
 * to generate the runnable sql string, need to replace the alias name with the real schema.table_name.col_names;(cons)
 * 
 * To reproduce, directly use the original aliasedExpressionString;(pros)
 * 
 * distinguish with with {@link AbstractVfBaseSqlQuery} for {@link GenericSQLOperation};
 * 
 * ===========================================================
 * the visible tables to creator of a {@link VfSQLExpression} include:
 * 	1. record data tables including the owner record data in the host VisProjectDBContext's rdb;
 * 	2. a pseudo target value tables of {@link CompositionFunctionGroup}s in the host VisProjectDBContext's rdb;
 * also note that data table and all target value tables of the data table are implicitly related by the RUID column;	
 * 
 * ===========================================================
 * current underlying rdb engine is APACHE DERBY, thus the syntax should be compatible with it;
 * 
 * note that syntax of the {@link #aliasedExpressionString} is not checked by this class;
 * 
 * @author tanxu
 */
public abstract class VfSQLExpression implements Reproducible {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3230659848163503148L;
	
	////////////////////////
	/**
	 * the expression string with the {@link InputVariable#getAliasName()} of each {@link InputVariable} obeying SQL syntax;
	 * 
	 * no aggregate function is allowed; if needed, create a {@link SQLAggregateFunctionInputVariable}
	 */
	private final String aliasedSqlString;
	
	/**
	 * map from alias name of each {@link InputVariable} to the InputVariable;
	 * 
	 * the alias names are directly used in the {@link #aliasedExpressionString}
	 */
	private final Map<SimpleName, InputVariable> inputVariableAliasNameMap;
	
	
	/**
	 * constructor
	 * @param aliasedExpressionString not null or empty string
	 * @param inputVariableSet not null; can be empty??????
	 */
	VfSQLExpression(
			String aliasedSqlString,
			Set<InputVariable> inputVariableSet
			){
		//validations
		if(aliasedSqlString==null||aliasedSqlString.isEmpty())
			throw new IllegalArgumentException("given aliasedSqlString cannot be null or empty!");

		//inputVariableSet not null; but can be empty???????????
		//TODO
		
		
		//alias name of each input variable should be present in aliasedExpressionString
		//TODO
		
		
		
		//unique alias name for each given input variable;
		this.inputVariableAliasNameMap = new HashMap<>();
		inputVariableSet.forEach(e->{
			if(this.inputVariableAliasNameMap.containsKey(e.getAliasName())) {
				throw new IllegalArgumentException("duplicate alias name found for input variables in given inputVariableSet!");
			}
			this.inputVariableAliasNameMap.put(e.getAliasName(), e);
		});
		
		
		this.aliasedSqlString = aliasedSqlString;
	}
	
	public Map<SimpleName, InputVariable> getInputVariableAliasNameMap() {
		return inputVariableAliasNameMap;
	}

	public String getAliasedSqlString() {
		return aliasedSqlString;
	}

	
	//////////////////////////////////////////
	/**
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public abstract VfSQLExpression reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;

	
	
	//////////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aliasedSqlString == null) ? 0 : aliasedSqlString.hashCode());
		result = prime * result + ((inputVariableAliasNameMap == null) ? 0 : inputVariableAliasNameMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VfSQLExpression))
			return false;
		VfSQLExpression other = (VfSQLExpression) obj;
		if (aliasedSqlString == null) {
			if (other.aliasedSqlString != null)
				return false;
		} else if (!aliasedSqlString.equals(other.aliasedSqlString))
			return false;
		if (inputVariableAliasNameMap == null) {
			if (other.inputVariableAliasNameMap != null)
				return false;
		} else if (!inputVariableAliasNameMap.equals(other.inputVariableAliasNameMap))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VfSQLExpression [aliasedSqlString=" + aliasedSqlString + ", inputVariableAliasNameMap="
				+ inputVariableAliasNameMap + "]";
	}

	
	
}
