package visinstance.run.calculation.evaluator.sqlbuilder;

import static sql.SQLStringUtils.WHERE_CLAUSE_HEADER;
import static sql.SQLStringUtils.buildEquityConditionString;
import static sql.SQLStringUtils.buildTableColumnFullPathString;

import java.sql.SQLException;

import function.component.ComponentFunction;
import function.component.PiecewiseFunction;
import function.evaluator.sqlbased.SimpleSQLQueryEvaluator;
import function.evaluator.sqlbased.SQLQueryBasedEvaluator;
import function.evaluator.sqlbased.utils.WhereConditionExpression;
import visinstance.run.calculation.evaluator.sqlbased.VfSQLExpressionProcessor;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;



public class PiecewiseFunctionSqlBasedEvaluatorSqlQueryStringBuilder extends SqlQueryBasedEvaluatorSqlQueryStringBuilder {
	/**
	 * constructor
	 * @param calculator
	 * @param hostComponentFunction
	 * @param evaluator
	 * @throws SQLException 
	 */
	public PiecewiseFunctionSqlBasedEvaluatorSqlQueryStringBuilder(
			CFTargetValueTableRunCalculator calculator,
			ComponentFunction hostComponentFunction,
			SQLQueryBasedEvaluator evaluator
			) throws SQLException {
		super(calculator, hostComponentFunction, evaluator);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * 1. build the initial WHERE condition string from the {@link WhereConditionExpression} of the SqlQueryBasedEvaluator;
	 * 		add the involved tables AbstractRelationalTableSchemaID to {@link #recordwiseInputVariableBasedTableSchemaIDSet}
	 * 2. add additional conditions
	 * 		1. upstream PiecewiseFunction output index condition
	 * 				
	 * 		2. RUID column equity condition
	 * 			between owner record data table RUID column and
	 * 				RUID column of every value table of the same owner record data table appearing in SelectColumnExpressions and WhereConditionExpression and stored in the {@link inputTableSchemaIDSet}s;
	 * 				RUID column of PiecewiseFunction index id output index value table
	 * 		3. column in PiecewiseFunction index id output index value table corresponding to current piecewise function’s output index is {@link PiecewiseFunction#DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX}; 
	 * 		(if not {@link PiecewiseFunction#DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX}, the records are processed by previous conditions)
	 * 		if the record has been evaluated true by a previous conditional evaluator, its column value will be the precedence index of the corresponding condition, thus cannot be DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX;
	 * @throws SQLException 
	 */
	@Override
	protected void buildWhereClauseString() throws SQLException {
		StringBuilder sb = new StringBuilder();
		
		sb.append(WHERE_CLAUSE_HEADER);
		
		boolean nothingAddedYet = true;
		
		//first process the WhereConditionExpression
		if(this.getEvaluator() instanceof SimpleSQLQueryEvaluator) {
			
			SimpleSQLQueryEvaluator sse = (SimpleSQLQueryEvaluator) this.getEvaluator();
			
			WhereConditionExpression wcexp = sse.getWhereConditionExpression();
			if(wcexp==null) {
				
				//do nothing
				
			}else {
				VfSQLExpressionProcessor processor = new VfSQLExpressionProcessor(wcexp,this.getCFTargetValueTableRunCalculator());
				
				sb.append(processor.getRealValuedSqlString());
				
				this.recordwiseInputVariableBasedTableSchemaIDSet.addAll(processor.getInputTableSchemaIDSet());
				
				nothingAddedYet = false;
			}
		}else {
			throw new IllegalArgumentException();
		}
		
		//
		String upstreamPiecewiseFunctionOutputIndexValueTableRelatedCondition = 
				this.buildPiecewiseFunctionOutputIndexValueTableRelatedConditionString();
		if(!upstreamPiecewiseFunctionOutputIndexValueTableRelatedCondition.isEmpty()) {
			if(nothingAddedYet) {
				nothingAddedYet = false;
			}else {
				sb.append(" AND ");
			}
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
		String conditionString3 = buildEquityConditionString(
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
