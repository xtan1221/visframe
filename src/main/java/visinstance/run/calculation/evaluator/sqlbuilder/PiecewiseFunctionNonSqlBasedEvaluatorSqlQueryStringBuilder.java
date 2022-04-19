package visinstance.run.calculation.evaluator.sqlbuilder;

import static sql.SQLStringUtils.*;

import java.sql.SQLException;

import function.component.ComponentFunction;
import function.component.PiecewiseFunction;
import function.evaluator.nonsqlbased.NonSQLQueryBasedEvaluator;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;

public class PiecewiseFunctionNonSqlBasedEvaluatorSqlQueryStringBuilder
		extends NonSqlBasedEvaluatorSqlQueryStringBuilder {

	/**
	 * constructor
	 * @param calculator
	 * @param hostComponentFunction
	 * @param evaluator
	 * @throws SQLException 
	 */
	public PiecewiseFunctionNonSqlBasedEvaluatorSqlQueryStringBuilder(
			CFTargetValueTableRunCalculator calculator,
			ComponentFunction hostComponentFunction,
			NonSQLQueryBasedEvaluator evaluator) throws SQLException {
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
	 * 
	 * 2. RUID column equity condition
	 * 			between owner record data table RUID column and
	 * 				RUID column of every value table of the same owner record data table appearing in SelectColumnExpressions and WhereConditionExpression and stored in the {@link #recordwiseInputVariableBasedTableSchemaIDSet}s;
	 * 
	 * 3. column in PiecewiseFunction index id output index value table corresponding to current piecewise function’s output index is {@link PiecewiseFunction#DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX}; 
	 * 		(if not {@link PiecewiseFunction#DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX}, the records are processed by previous conditions)
	 * 		if the record has been evaluated true by a previous conditional evaluator, its column value will be the precedence index of the corresponding condition, thus cannot be DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX;
	 * @throws SQLException 
	 */
	@Override
	protected void buildWhereClauseString() throws SQLException {
		StringBuilder sb = new StringBuilder();
		
		sb.append(WHERE_CLAUSE_HEADER);
		
		boolean nothingAddedYet = true;
		
		//1
		String upstreamPiecewiseFunctionOutputIndexValueTableRelatedCondition = 
				this.buildPiecewiseFunctionOutputIndexValueTableRelatedConditionString();
		if(!upstreamPiecewiseFunctionOutputIndexValueTableRelatedCondition.isEmpty()) {
			nothingAddedYet = false;
			sb.append(upstreamPiecewiseFunctionOutputIndexValueTableRelatedCondition);
		}
		
		//2
		String recordwiseInputVariableBasedTableSchemaRelatedCondition = 
				this.buildRecordwiseInputVariableBasedTableSchemaRUIDColumnEquityConditionString();
		if(!recordwiseInputVariableBasedTableSchemaRelatedCondition.isEmpty()) {
			if(nothingAddedYet) {
				nothingAddedYet = false;
			}else {
				sb.append(" AND ");
			}
			sb.append(recordwiseInputVariableBasedTableSchemaRelatedCondition);
		}
		
		
		//3. column in PiecewiseFunction index id output index value table corresponding to current piecewise function’s output index is {@link PiecewiseFunction#DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX}; 
		//if the record has been evaluated true by previous conditional evaluators, their column value will be the precedence index of the corresponding condition, thus cannot be DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX;
		String conditionString3 = 
				buildEquityConditionString(
					buildTableColumnFullPathString(
							this.getCFTargetValueTableRunCalculator().getPiecewiseFunctionIndexIDOutputIndexValueTableInitializer().getValueTableSchema().getID(),
							this.getCFTargetValueTableRunCalculator().getPiecewiseFunctionIndexIDOutputIndexValueTableInitializer().getPfIndexIDColumnNameMap().get(this.getHostComponentFunction().getIndexID()).getStringValue()
							),
					PiecewiseFunction.DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX
				);
		if(nothingAddedYet) {
			nothingAddedYet = false;
		}else {
			sb.append(" AND ");
		}
		sb.append(conditionString3);
		
		
		//////set the {@link #whereClauseString}
		if(nothingAddedYet) {
			this.whereClauseString = "";
		}else {
			this.whereClauseString = sb.toString();
		}
		
	}

}
