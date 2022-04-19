package visinstance.run.calculation.function.component;

import java.sql.ResultSet;
import java.sql.SQLException;

import function.component.PiecewiseFunction;
import function.evaluator.Evaluator;
import function.evaluator.nonsqlbased.NonSQLQueryBasedEvaluator;
import function.evaluator.sqlbased.SQLQueryBasedEvaluator;
import visinstance.run.calculation.evaluator.sqlbuilder.PiecewiseFunctionNonSqlBasedEvaluatorSqlQueryStringBuilder;
import visinstance.run.calculation.evaluator.sqlbuilder.PiecewiseFunctionSqlBasedEvaluatorSqlQueryStringBuilder;
import visinstance.run.calculation.evaluator.updator.NonSqlQueryBasedEvaluatorValueTableUpdateManager;
import visinstance.run.calculation.evaluator.updator.SqlQueryBasedEvaluatorValueTableUpdateManager;
import visinstance.run.calculation.evaluator.updator.utils.PiecewiseFunctionEvaluatorValueTableUpdator;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;


public class PiecewiseFunctionCalculator extends ComponentFunctionCalculator<PiecewiseFunction, PiecewiseFunctionEvaluatorValueTableUpdator> {
	
	/**
	 * constructor
	 * @param calculator
	 * @param function
	 * @throws SQLException 
	 */
	public PiecewiseFunctionCalculator(
			CFTargetValueTableRunCalculator calculator,
			PiecewiseFunction function) throws SQLException {
		super(calculator, function, new PiecewiseFunctionEvaluatorValueTableUpdator(calculator, function.getIndexID()));
	}
	
	
	/**
	 * <p>calculate evaluator of each condition with buildFullSqlQuerString(), runFullSqlQueryString() and insertResultIntoValueTables() methods;</p>
	 *	note that for non-RUID columns in PiecewiseFunctionIndexIDOutputIndexValueTable, they all have default value equal to the {@link PiecewiseFunction#DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX}; 
	 *  thus,no need to explicitly set the values of those columns after conditional evaluators are processed;
	 * @throws SQLException 
	 */
	@Override
	public void calculate() throws SQLException {
		for(Evaluator e : this.getTargetComponentFunction().getConditionalEvaluatrListOrderedByPrecdenceIndex()) {
			String fullSqlQuerString = this.buildFullSqlQuerString(e);
			
			ResultSet rs = this.runFullSqlQueryString(fullSqlQuerString);
			
			this.updateResultIntoValueTables(e, rs);
		}
		
	}
	
	
	
	/**
	 * if sql based, create a {@link PiecewiseFunctionSqlBasedEvaluatorSqlQueryStringBuilder} to build the sql string
	 * if non sql based, create a {@link PiecewiseFunctionNonSqlBasedEvaluatorSqlQueryStringBuilder} to build the sql string
	 * @throws SQLException 
	 */
	@Override
	protected String buildFullSqlQuerString(Evaluator e) throws SQLException {
		if(e instanceof SQLQueryBasedEvaluator) {
			PiecewiseFunctionSqlBasedEvaluatorSqlQueryStringBuilder builder = 
					new PiecewiseFunctionSqlBasedEvaluatorSqlQueryStringBuilder(
							this.getCFTargetValueTableRunCalculator(),
							this.getTargetComponentFunction(),
							(SQLQueryBasedEvaluator)e
							);
			return builder.getBuiltFullSqlQuerString();
		}else if(e instanceof NonSQLQueryBasedEvaluator) {
			PiecewiseFunctionNonSqlBasedEvaluatorSqlQueryStringBuilder builder =
					new PiecewiseFunctionNonSqlBasedEvaluatorSqlQueryStringBuilder(
							this.getCFTargetValueTableRunCalculator(),
							this.getTargetComponentFunction(),
							(NonSQLQueryBasedEvaluator)e
							);
			return builder.getBuiltFullSqlQuerString();
		}else {
			throw new IllegalArgumentException();
		}
	}
	
	
	
	/**
	 * update the PiecewiseFunctionIndexIDOutputIndexValueTable with the calculated output variable of the conditional evaluator;
	 * @throws SQLException if any error occurs during the process of updating the value table; 
	 */
	@Override
	protected void updateResultIntoValueTables(Evaluator e, ResultSet rs) throws SQLException {
		if(e instanceof SQLQueryBasedEvaluator) {
			SqlQueryBasedEvaluatorValueTableUpdateManager<PiecewiseFunction, PiecewiseFunctionEvaluatorValueTableUpdator> updator = 
					new SqlQueryBasedEvaluatorValueTableUpdateManager<>(
							this.getCFTargetValueTableRunCalculator(),
							(SQLQueryBasedEvaluator)e,
							rs,
							this.getComponentFunctionValueTableUpdator());
			
			updator.perform();
			
		}else if(e instanceof NonSQLQueryBasedEvaluator) {
			NonSqlQueryBasedEvaluatorValueTableUpdateManager<PiecewiseFunction, PiecewiseFunctionEvaluatorValueTableUpdator> updator = 
					new NonSqlQueryBasedEvaluatorValueTableUpdateManager<>(
							this.getCFTargetValueTableRunCalculator(), 
							(NonSQLQueryBasedEvaluator)e, 
							rs,
							this.getComponentFunctionValueTableUpdator());
			
			updator.perform();
		}else {
			throw new IllegalArgumentException("unrecognized evaluator type!");
		}
	}
	
}
