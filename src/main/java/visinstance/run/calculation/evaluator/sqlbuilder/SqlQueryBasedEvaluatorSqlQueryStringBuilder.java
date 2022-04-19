package visinstance.run.calculation.evaluator.sqlbuilder;

import static sql.SQLStringUtils.*;

import java.sql.SQLException;
import java.util.HashSet;
import context.project.rdb.VisProjectRDBConstants;
import function.component.ComponentFunction;
import function.evaluator.sqlbased.SimpleSQLQueryEvaluator;
import function.evaluator.sqlbased.SQLQueryBasedEvaluator;
import function.evaluator.sqlbased.utils.SelectElementExpression;
import visinstance.run.calculation.evaluator.sqlbased.VfSQLExpressionProcessor;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;

/**
 * build the sql query for SqlQueryBasedEvaluator so that the output variables of the SqlQueryBasedEvaluator can be directly retrieved from the ResultSet of the sql query;
 * 
 * after the sql query is built and run and ResultSet is generated, the only post-process step is to find out a value for output variables for each RUID of the owner record data table;
 * 
 * @author tanxu
 *
 */
public abstract class SqlQueryBasedEvaluatorSqlQueryStringBuilder extends EvaluatorFullSqlQueryStringBuilderBase<SQLQueryBasedEvaluator> {
	
	
	protected String orderByClauseString;
	
	/**
	 * constructor
	 * @param calculator
	 * @param hostComponentFunction
	 * @param evaluator
	 * @throws SQLException 
	 */
	SqlQueryBasedEvaluatorSqlQueryStringBuilder(
			CFTargetValueTableRunCalculator calculator,
			ComponentFunction hostComponentFunction,
			
			SQLQueryBasedEvaluator evaluator
			) throws SQLException {
		super(calculator, hostComponentFunction, evaluator);
		// TODO Auto-generated constructor stub
		
		
	}

	

	/**
	 * group by clause with the RUID column of owner record data table;
	 * @throws SQLException 
	 * 
	 */
	private void buildOrderByClauseString() throws SQLException {
		StringBuilder sb = new StringBuilder();
		
		sb.append("ORDER BY ").append(
				buildTableColumnFullPathString(
				this.getCFTargetValueTableRunCalculator().getOwnerRecordDataMetadata().getDataTableSchema().getID(), 
				VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE));
		
		this.orderByClauseString = sb.toString();
	}
	
	
	/**
	 * invoke the buildSelectClauseString(), buildWhereClauseString() , buildFromClauseString() and buildGroupByClauseString() methods;
	 * @throws SQLException 
	 */
	@Override
	protected void build() throws SQLException {
		
		this.buildSelectClauseString();
		this.buildWhereClauseString();
		//must be invoked after the first two;
		this.buildFromClauseString();
		this.buildOrderByClauseString();
		
		
		//
		this.buildFullSqlQueryString();
	}
	
	/**
	 * build the SELECT clause term strings for each {@link SelectElementExpression};
	 * 		also add the involved input table's AbstractRelationalTableSchemaID to {@link #recordwiseInputVariableBasedTableSchemaIDSet}
	 * build the full SELECT clause string with a heading RUID column of the owner data table;
	 * @throws SQLException 
	 */
	@Override
	protected void buildSelectClauseString() throws SQLException {
		this.recordwiseInputVariableBasedTableSchemaIDSet = new HashSet<>();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(SELECT_CLAUSE_HEADER);
		
		//add RUID column of owner record data table of host CFG
		sb.append(buildTableColumnFullPathString(
						this.getCFTargetValueTableRunCalculator().getOwnerRecordDataMetadata().getDataTableSchema().getID(), 
						VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE));
		
		//auto-include the owner record data table of host CFG
		this.recordwiseInputVariableBasedTableSchemaIDSet.add(this.getCFTargetValueTableRunCalculator().getOwnerRecordDataMetadata().getDataTableSchema().getID());
				
		//
		if(this.getEvaluator() instanceof SimpleSQLQueryEvaluator) {
			
			SimpleSQLQueryEvaluator sse = (SimpleSQLQueryEvaluator) this.getEvaluator();
			
			for(SelectElementExpression scexp:sse.getSelectClauseExpressionList()) {
				VfSQLExpressionProcessor processor = new VfSQLExpressionProcessor(scexp,this.getCFTargetValueTableRunCalculator());
				
				//first add the sql string for the column
				sb.append(", ").append(processor.getRealValuedSqlString());
				
				//add the input table schema IDs
				this.recordwiseInputVariableBasedTableSchemaIDSet.addAll(processor.getInputTableSchemaIDSet());
				
			}
			
			
		}else {//other unrecognizable SqlQueryBasedEvaluator types;
			throw new IllegalArgumentException("unrecognizable SqlQueryBasedEvaluator types");
		}
		
		
		this.selectClauseString = sb.toString();
	}
	
	
	/**
	 * build the full sql query string;
	 * 
	 * format: SELECT ... FROM ... WHERE ... GROUP BY ...
	 */
	@Override
	protected void buildFullSqlQueryString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.selectClauseString).append(SPACE)
		.append(this.fromClauseString).append(SPACE)
		.append(this.whereClauseString).append(SPACE)
		.append(this.orderByClauseString);
		
		this.builtFullSqlQueryString = sb.toString();
	}

}
