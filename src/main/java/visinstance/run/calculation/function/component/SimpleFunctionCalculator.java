package visinstance.run.calculation.function.component;

import java.sql.ResultSet;
import java.sql.SQLException;

import function.component.SimpleFunction;
import function.evaluator.Evaluator;
import function.evaluator.nonsqlbased.NonSQLQueryBasedEvaluator;
import function.evaluator.sqlbased.SQLQueryBasedEvaluator;
import visinstance.run.calculation.evaluator.sqlbuilder.SimpleFunctionNonSqlBasedEvaluatorSqlQueryStringBuilder;
import visinstance.run.calculation.evaluator.sqlbuilder.SimpleFunctionSqlBasedEvaluatorSqlQueryStringBuilder;
import visinstance.run.calculation.evaluator.updator.NonSqlQueryBasedEvaluatorValueTableUpdateManager;
import visinstance.run.calculation.evaluator.updator.SqlQueryBasedEvaluatorValueTableUpdateManager;
import visinstance.run.calculation.evaluator.updator.utils.SimpleFunctionEvaluatorValueTableUpdator;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;

public class SimpleFunctionCalculator extends ComponentFunctionCalculator<SimpleFunction, SimpleFunctionEvaluatorValueTableUpdator> {
	
	/**
	 * constructor
	 * @param calculator
	 * @param function
	 * @throws SQLException 
	 */
	public SimpleFunctionCalculator(
			CFTargetValueTableRunCalculator calculator,
			SimpleFunction function) throws SQLException {
		super(calculator, function, new SimpleFunctionEvaluatorValueTableUpdator(calculator));
		
	}
	
	/**
	 * calculate each evaluator;
	 * @throws SQLException 
	 */
	@Override
	public void calculate() throws SQLException {
		for(Evaluator e : this.getTargetComponentFunction().getEvaluatorIndexIDMap().values()) {
			
			String fullSqlQuerString = this.buildFullSqlQuerString(e);
			
			ResultSet rs = this.runFullSqlQueryString(fullSqlQuerString);
			
			this.updateResultIntoValueTables(e, rs);
		}
	}

	
	/**
	 * if sql based, create a {@link SimpleFunctionSqlBasedEvaluatorSqlQueryStringBuilder} to build the sql string;
	 * if non sql based, create a {@link SimpleFunctionNonSqlBasedEvaluatorSqlStringBuilder} to build the sql string
	 * @throws SQLException 
	 */
	@Override
	protected String buildFullSqlQuerString(Evaluator e) throws SQLException {
		if(e instanceof SQLQueryBasedEvaluator) {
			SimpleFunctionSqlBasedEvaluatorSqlQueryStringBuilder builder = 
					new SimpleFunctionSqlBasedEvaluatorSqlQueryStringBuilder(
							this.getCFTargetValueTableRunCalculator(),
							this.getTargetComponentFunction(),
							(SQLQueryBasedEvaluator)e
							);
			return builder.getBuiltFullSqlQuerString();
		}else if(e instanceof NonSQLQueryBasedEvaluator) {
			SimpleFunctionNonSqlBasedEvaluatorSqlQueryStringBuilder builder =
					new SimpleFunctionNonSqlBasedEvaluatorSqlQueryStringBuilder(
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
	 * if sql based,  create a {@link SimpleFunctionSqlBasedEvaluatorValueTableUpdator} with the ResultSet;
	 * if non sql based, create a {@link SimpleFunctionNonSqlBasedEvaluatorValueTableUpdator} with the ResultSet
	 * @throws SQLException 
	 */
	@Override
	protected void updateResultIntoValueTables(Evaluator e, ResultSet rs) throws SQLException {
		if(e instanceof SQLQueryBasedEvaluator) {
			SqlQueryBasedEvaluatorValueTableUpdateManager<SimpleFunction, SimpleFunctionEvaluatorValueTableUpdator> updator = 
					new SqlQueryBasedEvaluatorValueTableUpdateManager<>(
							this.getCFTargetValueTableRunCalculator(),
							(SQLQueryBasedEvaluator)e,
							rs,
							this.getComponentFunctionValueTableUpdator());
			
			updator.perform();
			
		}else if(e instanceof NonSQLQueryBasedEvaluator) {
			NonSqlQueryBasedEvaluatorValueTableUpdateManager<SimpleFunction, SimpleFunctionEvaluatorValueTableUpdator> updator = 
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
