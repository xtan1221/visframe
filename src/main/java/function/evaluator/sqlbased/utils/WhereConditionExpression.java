package function.evaluator.sqlbased.utils;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.misc.ParseCancellationException;

import basic.SimpleName;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.variable.input.InputVariable;
import utils.visframe.WhereConditionProcessor;


/**
 * contain the full information for the WHERE clause for a SimpleSqlQueryEvaluator;
 * 
 * 
 * note that no self-join is allowed between the set of tables including???
 * 	1. owner record data table
 * 	2. target value tables of the owner record data table;
 * 
 * 
 * @author tanxu
 * 
 */
public class WhereConditionExpression extends VfSQLExpression {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3639539689065224742L;

	/**
	 * 
	 * @param aliasedExpressionString
	 * @param inputVariableSet
	 */
	public WhereConditionExpression(
			String aliasedSqlString,
			Set<InputVariable> inputVariableSet
			) {
		super(aliasedSqlString, inputVariableSet);
		
		//validations
		if(inputVariableSet==null || inputVariableSet.isEmpty())
			throw new IllegalArgumentException("given inputVariableSet cannot be null or empty!");
		
		//validate with SelectElementProcessor
		try {
			WhereConditionProcessor processor = new WhereConditionProcessor(this.getAliasedSqlString());
			
			inputVariableSet.forEach(iv->{
				if(!processor.getVariableNameStringSet().contains(iv.getAliasName().getStringValue()))
					throw new IllegalArgumentException("variable in given sql string is not found in the given inputVariableSet:"+iv.getAliasName().getStringValue());
			});
			
			
		}catch(ParseCancellationException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	
	/**
	 * reproduce and return a new WhereConditionExpression of this one;
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public WhereConditionExpression reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
		String aliasedSqlString = this.getAliasedSqlString();
		
		Set<InputVariable> inputVariableSet = new HashSet<>();
		for(SimpleName sn:this.getInputVariableAliasNameMap().keySet()) {
			inputVariableSet.add(this.getInputVariableAliasNameMap().get(sn).reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex));
		}
		
		return new WhereConditionExpression(
				aliasedSqlString,
				inputVariableSet
				);
	}
	
}
