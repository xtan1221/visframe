package function.evaluator.sqlbased.utils;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.misc.ParseCancellationException;

import basic.SimpleName;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.variable.input.InputVariable;
import function.variable.output.OutputVariable;
import utils.visframe.SelectElementProcessor;

/**
 * a single component in a selection clause that corresponds to a column of the resulted table view for a {@link SimpleSQLQueryEvaluator};
 * 
 * also corresponds to an {@link OutputVariable};
 * 
 * note that whether the given {@link #expressionSQLDataType} is consistent with the {@link #getAliasedExpressionString()} is not checked in this class;
 * 
 * @author tanxu
 * 
 */
public class SelectElementExpression extends VfSQLExpression {
	/**
	 * 
	 */
	private static final long serialVersionUID = 959567855016080901L;
	
	//////////////////////
	private final OutputVariable outputVariable;
	
	/**
	 * constructor
	 * @param aliasedExpressionString not null;
	 * @param inputVariableAliasNameMap not null or empty
	 * @param outputVariable not null;
	 */
	public SelectElementExpression(
			String aliasedSqlString,
			Set<InputVariable> inputVariableSet,
			
			OutputVariable outputVariable
			) {
		super(aliasedSqlString, inputVariableSet);
		//validations
		//expressionSQLDataType and outputVariable must be consistent
		if(outputVariable==null)
			throw new IllegalArgumentException("given outputVariable cannot be null!");
		if(inputVariableSet==null || inputVariableSet.isEmpty())
			throw new IllegalArgumentException("given inputVariableSet cannot be null or empty!");
		
		//validate with SelectElementProcessor
		try {
			SelectElementProcessor processor = new SelectElementProcessor(this.getAliasedSqlString());
			
			inputVariableSet.forEach(iv->{
				if(!processor.getVariableNameStringSet().contains(iv.getAliasName().getStringValue()))
					throw new IllegalArgumentException("variable in given sql string is not found in the given inputVariableSet:"+iv.getAliasName().getStringValue());
			});
			
			
		}catch(ParseCancellationException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		
		
		
		this.outputVariable = outputVariable;
	}
	
	public OutputVariable getOutputVariable() {
		return outputVariable;
	}
	
	
	/////////////////////////////
	/**
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public SelectElementExpression reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
		String aliasedSqlString = this.getAliasedSqlString();
		
		Set<InputVariable> inputVariableSet = new HashSet<>();
		for(SimpleName sn:this.getInputVariableAliasNameMap().keySet()) {
			inputVariableSet.add(this.getInputVariableAliasNameMap().get(sn).reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex));
		}
		
		OutputVariable outputVariable = this.getOutputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		return new SelectElementExpression(
				aliasedSqlString,
				inputVariableSet,
				outputVariable
				);
	}

	
	
	////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((outputVariable == null) ? 0 : outputVariable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SelectElementExpression))
			return false;
		SelectElementExpression other = (SelectElementExpression) obj;
		if (outputVariable == null) {
			if (other.outputVariable != null)
				return false;
		} else if (!outputVariable.equals(other.outputVariable))
			return false;
		return true;
	}

	
	@Override
	public String toString() {
		return "SelectElementExpression [outputVariable=" + outputVariable + ", getInputVariableAliasNameMap()="
				+ getInputVariableAliasNameMap() + ", getAliasedSqlString()=" + getAliasedSqlString() + "]";
	}
	
	
	
}
