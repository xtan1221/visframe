package visinstance.run.calculation.evaluator.updator.utils;

import java.sql.SQLException;
import java.util.Map;

import context.project.rdb.VisProjectRDBConstants;
import function.component.PiecewiseFunction;
import function.variable.output.OutputVariable;
import function.variable.output.type.PFConditionEvaluatorBooleanOutputVariable;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;

/**
 * 
 * @author tanxu
 *
 */
public final class PiecewiseFunctionEvaluatorValueTableUpdator extends ComponentFunctionValueTableUpdator<PiecewiseFunction>{
	private final int piecewiseFunctionIndexID;
	////////////////////////
	/**
	 * the column name in the PiecewiseFunctionIndexIDOutputIndexValueTable corresponding to the host PiecewiseFunction of the target evaluator;
	 */
	private String columnNameInPiecewiseFunctionIndexIDOutputIndexValueTable;
	
	/**
	 * constructor
	 * @param calculator
	 * @param hostComponentFunction
	 * @param nonSqlQueryBasedEvaluator
	 * @param resultSet
	 * @throws SQLException 
	 */
	public PiecewiseFunctionEvaluatorValueTableUpdator(
			CFTargetValueTableRunCalculator calculator,
			int piecewiseFunctionIndexID) throws SQLException {
		super(calculator);
		// TODO Auto-generated constructor stub
		
		this.piecewiseFunctionIndexID = piecewiseFunctionIndexID;
		
		this.columnNameInPiecewiseFunctionIndexIDOutputIndexValueTable = 
				this.getCFTargetValueTableRunCalculator().getPiecewiseFunctionIndexIDOutputIndexValueTableInitializer().getPfIndexIDColumnNameMap().get(this.piecewiseFunctionIndexID).getStringValue();

	}
	
	/**
	 * update the single column of the piecewise function output index value table corresponding to the host PiecewiseFunction of the evaluator;
	 * 
	 * note that the given outputVariableAliasNameCalculatedStringValueMap should contains one single OutputVariable of boolean type;
	 * 
	 * if the string value of the output variable is 'true', update the column value of the given RUID to the output index of the corresponding condition evaluator;
	 * 
	 * else, do nothing;
	 * 
	 * @throws SQLException 
	 */
	public void updateValueTables(Map<OutputVariable, String> outputVariableAliasNameCalculatedStringValueMap, int RUID) throws SQLException {
		OutputVariable ov = outputVariableAliasNameCalculatedStringValueMap.entrySet().iterator().next().getKey();
		if(!(ov instanceof PFConditionEvaluatorBooleanOutputVariable)) {
			throw new IllegalArgumentException("given outputVariableAliasNameCalculatedStringValueMap does not contain a single PFConditionEvaluatorBooleanOutputVariable as key!");
		}
		
		String stringValue = outputVariableAliasNameCalculatedStringValueMap.get(ov);
		
		if(stringValue!=null && stringValue.equalsIgnoreCase("true")) {
			//set the column value to the output index of the corresponding condition evaluator;
			int conditionPrecedenceIndex = ov.getHostEvaluatorIndexID();
			
			String tableFullPathName = SQLStringUtils.buildTableFullPathString(
					this.getCFTargetValueTableRunCalculator().getPiecewiseFunctionIndexIDOutputIndexValueTableInitializer().getValueTableSchema().getID());
			
			String sqlString = 
					"UPDATE ".concat(tableFullPathName).concat(" ")
					.concat(TableContentSQLStringFactory.buildSetSingleColumnValueClauseSqlString(
							this.columnNameInPiecewiseFunctionIndexIDOutputIndexValueTable,
							Integer.toString(conditionPrecedenceIndex), //
							false))
					.concat("WHERE ").concat(TableContentSQLStringFactory.buildColumnValueEquityCondition(
							VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE, Integer.toString(RUID), false, null));
			
			///add to batch
			this.statement.addBatch(sqlString);
			this.currentBatchSize++;
			
			if(this.currentBatchSize>BATCH_UPDATE_LIMIT) {
				statement.executeBatch();
				this.currentBatchSize = 0;
			}
		}else {
			//do nothing
		}
		
	}
}
