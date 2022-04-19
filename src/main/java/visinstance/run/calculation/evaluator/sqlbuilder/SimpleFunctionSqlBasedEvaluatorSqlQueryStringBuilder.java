package visinstance.run.calculation.evaluator.sqlbuilder;

import function.component.ComponentFunction;
import function.evaluator.sqlbased.SimpleSQLQueryEvaluator;
import function.evaluator.sqlbased.SQLQueryBasedEvaluator;
import function.evaluator.sqlbased.utils.WhereConditionExpression;
import visinstance.run.calculation.evaluator.sqlbased.VfSQLExpressionProcessor;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;

import static sql.SQLStringUtils.*;

import java.sql.SQLException;

/**
 * 
 * @author tanxu
 *
 */
public class SimpleFunctionSqlBasedEvaluatorSqlQueryStringBuilder extends SqlQueryBasedEvaluatorSqlQueryStringBuilder {
	
	/**
	 * constructor
	 * 
	 * @param calculator
	 * @param hostComponentFunction
	 * @param evaluator
	 * @throws SQLException 
	 */
	public SimpleFunctionSqlBasedEvaluatorSqlQueryStringBuilder(
			CFTargetValueTableRunCalculator calculator,
			ComponentFunction hostComponentFunction,
			SQLQueryBasedEvaluator evaluator
			) throws SQLException {
		super(calculator, hostComponentFunction,evaluator);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * 
	 * note that it is possible WHERE clause is empty;
	 * 
	 * 1. build the initial WHERE condition string from the {@link WhereConditionExpression} of the SqlQueryBasedEvaluator
	 * 		add the involved tables AbstractRelationalTableSchemaID to {@link #recordwiseInputVariableBasedTableSchemaIDSet}
	 * 2. add additional conditions
	 * 		1. upstream PiecewiseFunction output index condition
	 * 				PiecewiseFunction index id output index value table ValueTableSchemaID;
	 * 				upstream piecewise function index id output index map;
	 * 		2. RUID column equity condition
	 * 			between owner record data table RUID column and
	 * 				RUID column of PiecewiseFunction index id output index value table
	 * 3. RUID column equity condition
	 * 			between owner record data table RUID column and
	 * 				RUID column of every value table of the same owner record data table appearing in SelectColumnExpressions and WhereConditionExpression and stored in the {@link #recordwiseInputVariableBasedTableSchemaIDSet}s;
	 * @throws SQLException 
	 */
	@Override
	protected void buildWhereClauseString() throws SQLException {
		StringBuilder sb = new StringBuilder();
		
		sb.append(WHERE_CLAUSE_HEADER);
		
		boolean nothingAddedYet = true;
		
		//1. process the WhereConditionExpression
		//must process this before the recordwiseInputVariableBasedTableSchemaRelatedCondition!
		if(this.getEvaluator() instanceof SimpleSQLQueryEvaluator) {
			
			SimpleSQLQueryEvaluator sse = (SimpleSQLQueryEvaluator) this.getEvaluator();
			
			WhereConditionExpression wcexp = sse.getWhereConditionExpression();
			if(wcexp==null) {//there is no WhereConditionExpression
				
				//do nothing
				
			}else {
				VfSQLExpressionProcessor processor = new VfSQLExpressionProcessor(wcexp,this.getCFTargetValueTableRunCalculator());
				
				sb.append(processor.getRealValuedSqlString());
				
				this.recordwiseInputVariableBasedTableSchemaIDSet.addAll(processor.getInputTableSchemaIDSet());
				
				nothingAddedYet = false;
			}
		}else {
			throw new IllegalArgumentException("invalid evaluator!");
		}
		
		
		//2
		String upstreamPiecewiseFunctionOutputIndexValueTableRelatedCondition = this.buildPiecewiseFunctionOutputIndexValueTableRelatedConditionString();
		if(!upstreamPiecewiseFunctionOutputIndexValueTableRelatedCondition.isEmpty()) {
			if(nothingAddedYet) {
				nothingAddedYet = false;
			}else {
				sb.append(" AND ");
			}
			sb.append(upstreamPiecewiseFunctionOutputIndexValueTableRelatedCondition);
		}
		
		//3
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
