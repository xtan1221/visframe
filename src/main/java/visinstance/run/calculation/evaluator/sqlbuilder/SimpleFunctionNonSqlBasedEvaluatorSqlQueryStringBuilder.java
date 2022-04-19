package visinstance.run.calculation.evaluator.sqlbuilder;

import static sql.SQLStringUtils.WHERE_CLAUSE_HEADER;
import java.sql.SQLException;

import function.component.ComponentFunction;
import function.evaluator.nonsqlbased.NonSQLQueryBasedEvaluator;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;

/**
 * 
 * @author tanxu
 *
 */
public class SimpleFunctionNonSqlBasedEvaluatorSqlQueryStringBuilder extends NonSqlBasedEvaluatorSqlQueryStringBuilder {
	
	/**
	 * constructor
	 * @param calculator
	 * @param hostComponentFunction
	 * @param evaluator
	 * @throws SQLException 
	 */
	public SimpleFunctionNonSqlBasedEvaluatorSqlQueryStringBuilder(
			CFTargetValueTableRunCalculator calculator,
			ComponentFunction hostComponentFunction,
			NonSQLQueryBasedEvaluator evaluator
			) throws SQLException {
		super(calculator, hostComponentFunction, evaluator);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * 1. add additional conditions
	 * 		1. upstream PiecewiseFunction output index condition
	 * 				PiecewiseFunction index id output index value table ValueTableSchemaID;
	 * 				upstream piecewise function index id output index map;
	 * 		2. RUID column equity condition
	 * 			between owner record data table RUID column and
	 * 				RUID column of PiecewiseFunction index id output index value table
	 * 2. RUID column equity condition
	 * 			between owner record data table RUID column and
	 * 				RUID column of every value table of the same owner record data table appearing in SelectColumnExpressions and WhereConditionExpression and stored in the {@link #recordwiseInputVariableBasedTableSchemaIDSet}s;
	 * @throws SQLException 
	 */
	@Override
	protected void buildWhereClauseString() throws SQLException {
		StringBuilder sb = new StringBuilder();
		
		sb.append(WHERE_CLAUSE_HEADER);
		
		boolean nothingAddedYet = true;
		
		//1
		String upstreamPiecewiseFunctionOutputIndexValueTableRelatedCondition = this.buildPiecewiseFunctionOutputIndexValueTableRelatedConditionString();
		if(!upstreamPiecewiseFunctionOutputIndexValueTableRelatedCondition.isEmpty()) {
			nothingAddedYet = false;
			sb.append(upstreamPiecewiseFunctionOutputIndexValueTableRelatedCondition);
		}
		
		//2
		String recordwiseInputVariableBasedTableSchemaRelatedCondition = this.buildRecordwiseInputVariableBasedTableSchemaRUIDColumnEquityConditionString();
		if(!recordwiseInputVariableBasedTableSchemaRelatedCondition.isEmpty()) {
			if(nothingAddedYet) {
				nothingAddedYet = false;
			}else {
				sb.append(" AND ");
			}
			sb.append(recordwiseInputVariableBasedTableSchemaRelatedCondition);
		}
		
		
		//////set the {@link #whereClauseString}
		if(nothingAddedYet) {
			this.whereClauseString = "";
		}else {
			this.whereClauseString = sb.toString();
		}
		
	}

}
