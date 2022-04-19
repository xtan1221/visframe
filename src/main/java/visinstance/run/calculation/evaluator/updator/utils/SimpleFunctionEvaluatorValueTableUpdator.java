package visinstance.run.calculation.evaluator.updator.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import context.project.rdb.VisProjectRDBConstants;
import function.component.SimpleFunction;
import function.variable.output.OutputVariable;
import function.variable.output.type.CFGTargetOutputVariable;
import function.variable.output.type.TemporaryOutputVariable;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;

/**
 * 
 * @author tanxu
 *
 */
public final class SimpleFunctionEvaluatorValueTableUpdator extends ComponentFunctionValueTableUpdator<SimpleFunction>{

	/**
	 * 
	 * @param calculator
	 * @param hostComponentFunction
	 * @param nonSqlQueryBasedEvaluator
	 * @param resultSet
	 * @throws SQLException 
	 */
	public SimpleFunctionEvaluatorValueTableUpdator(
			CFTargetValueTableRunCalculator calculator) throws SQLException {
		super(calculator);
		// TODO Auto-generated constructor stub
		
	}
	
	/**
	 * insert the output variable values into the CFTarget Value Table and/or Temporary output variable value table accordingly;
	 * 
	 * 1. build the sql string for CFTarget Value Table and Temporary output variable value table
	 * 		 UPDATE table_name SET column1 = value1, column2 = value2, ... WHERE RUID = value;
	 *		
	 * 2. perform the sql string;
	 * @throws SQLException 
	 * 
	 */
	@Override
	public void updateValueTables(Map<OutputVariable, String> outputVariableAliasNameCalculatedStringValueMap, int RUID) throws SQLException {
		boolean CFGTargetOutputVariableFound = false;
		boolean temporaryOutputVariableFound = false;
		
		//list of columns in the cf target value table to be updated
		List<String> targetValueTableColumnList = new ArrayList<>();
		List<String> targetValueTableColumnValueStringList = new ArrayList<>();
		List<Boolean> targetValueTableColumnIsOfStringTypeList = new ArrayList<>();
		
		List<String> temporaryOutputVariableValueTableColumnList = new ArrayList<>();
		List<String> temporaryOutputVariableValueTableColumnValueStringList = new ArrayList<>();
		List<Boolean> temporaryOutputVariableValueTableColumnIsOfStringTypeList = new ArrayList<>();
		
		
		for(OutputVariable ov:outputVariableAliasNameCalculatedStringValueMap.keySet()) {
			if(ov instanceof CFGTargetOutputVariable) {
				CFGTargetOutputVariable tov = (CFGTargetOutputVariable)ov;
				CFGTargetOutputVariableFound = true;
				
				targetValueTableColumnList.add(
						this.getCFTargetValueTableRunCalculator().getCFTargetValueTableInitializer().getTargetNameColNameMap().get(tov.getTargetName()).getStringValue());
				
				//need to find out the valid value for the CFGTarget rather than directly used the value calculated by the evaluator
				//since the calculated value may be invalid!!!!!!!!
				targetValueTableColumnValueStringList.add(
						tov.getTarget().processCalculatedStringValue(outputVariableAliasNameCalculatedStringValueMap.get(tov)));
				
				
				targetValueTableColumnIsOfStringTypeList.add(tov.getSQLDataType().isOfStringType());
				
			}else if(ov instanceof TemporaryOutputVariable) {
				TemporaryOutputVariable tov = (TemporaryOutputVariable)ov;
				temporaryOutputVariableFound = true;
				
				temporaryOutputVariableValueTableColumnList.add(
						this.getCFTargetValueTableRunCalculator().getTemporaryOutputVariableValueTableInitializer().getTemporaryOutputVariableColumnNameMap().get(tov).getStringValue());
				
				temporaryOutputVariableValueTableColumnValueStringList.add(
						outputVariableAliasNameCalculatedStringValueMap.get(ov));
				
				temporaryOutputVariableValueTableColumnIsOfStringTypeList.add(tov.getSQLDataType().isOfStringType());
			}else {
				throw new IllegalArgumentException("given outputVariableAliasNameCalculatedStringValueMap contains invalid OutputVariable type!");
			}
		}
		
		
		//build target value table update sql and run it
		if(CFGTargetOutputVariableFound) {
			String tableFullPathName = SQLStringUtils.buildTableFullPathString(
					this.getCFTargetValueTableRunCalculator().getCFTargetValueTableInitializer().getValueTableSchema().getID());
			String sqlString = "UPDATE ".concat(tableFullPathName).concat(" ")
					.concat(TableContentSQLStringFactory.buildSetColumnValueClauseSqlString(
							targetValueTableColumnList, targetValueTableColumnValueStringList, targetValueTableColumnIsOfStringTypeList))
					.concat(" WHERE ").concat(TableContentSQLStringFactory.buildColumnValueEquityCondition(
							VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE, Integer.toString(RUID), false, null));
			
			
			this.statement.addBatch(sqlString);
			this.currentBatchSize++;
		}
		
		//build temporary output variable value table update sql and run it;
		if(temporaryOutputVariableFound) {
			String tableFullPathName = SQLStringUtils.buildTableFullPathString(
					this.getCFTargetValueTableRunCalculator().getTemporaryOutputVariableValueTableInitializer().getValueTableSchema().getID());
			String sqlString = "UPDATE ".concat(tableFullPathName).concat(" ")
					.concat(TableContentSQLStringFactory.buildSetColumnValueClauseSqlString(
							temporaryOutputVariableValueTableColumnList, temporaryOutputVariableValueTableColumnValueStringList, temporaryOutputVariableValueTableColumnIsOfStringTypeList))
					.concat(" WHERE ").concat(TableContentSQLStringFactory.buildColumnValueEquityCondition(
							VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE, Integer.toString(RUID), false, null));
			
			
			this.statement.addBatch(sqlString);
			this.currentBatchSize++;
		}
		
		if(this.currentBatchSize>BATCH_UPDATE_LIMIT) {
			statement.executeBatch();
			this.currentBatchSize = 0;
		}
	}
}
