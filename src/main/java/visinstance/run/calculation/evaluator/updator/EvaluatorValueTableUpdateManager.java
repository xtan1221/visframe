package visinstance.run.calculation.evaluator.updator;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import function.component.ComponentFunction;
import function.evaluator.Evaluator;
import function.evaluator.nonsqlbased.NonSQLQueryBasedEvaluator;
import function.variable.input.nonrecordwise.type.SQLAggregateFunctionBasedInputVariable;
import function.variable.output.OutputVariable;
import visinstance.run.calculation.evaluator.updator.utils.ComponentFunctionValueTableUpdator;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;
import visinstance.run.calculation.function.variable.SQLAggregateFunctionBasedInputVariableCalculator;


/**
 * base class to process the ResultSet of a sql query built by {@link EvaluatorFullSqlQueryStringBuilderBase} and update the related value tables;
 * 
 * each subtype of this class corresponds to a subtype of {@link EvaluatorFullSqlQueryStringBuilderBase};
 * 
 * @author tanxu
 * 
 */
public abstract class EvaluatorValueTableUpdateManager<C extends ComponentFunction, E extends Evaluator, U extends ComponentFunctionValueTableUpdator<C>> {
	private final CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator;
	private final E evaluator;
	protected final ResultSet resultSet;
	protected final U componentFunctionValueTableUpdator;
	////////////////////////////////
	
	/**
	 * store all the calculated string values for all encountered SQLAggregateFunctionBasedInputVariable of the target evaluator
	 */
	private Map<SQLAggregateFunctionBasedInputVariable, String> SQLAggregateFunctionBasedInputVariableCalculatedStringValueMap;
	
	/**
	 * number of columns in the ResultSet including the RUID column
	 */
	protected int columnNum;
	
	/**
	 * constructor
	 * @param calculator
	 * @param hostComponentFunction
	 * @param resultSet
	 * @throws SQLException 
	 */
	protected EvaluatorValueTableUpdateManager(
			CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator,
			E evaluator,
			ResultSet resultSet,
			U componentFunctionValueTableUpdator) throws SQLException{
		if(CFTargetValueTableRunCalculator==null)
			throw new IllegalArgumentException("given CFTargetValueTableRunCalculator cannot be null!");
		if(evaluator==null)
			throw new IllegalArgumentException("given evaluator cannot be null!");
		if(resultSet==null)
			throw new IllegalArgumentException("given resultSet cannot be null!");
		
		
		
		this.CFTargetValueTableRunCalculator = CFTargetValueTableRunCalculator;
		this.evaluator = evaluator;
		this.resultSet = resultSet;
		this.componentFunctionValueTableUpdator = componentFunctionValueTableUpdator;
		
		
		this.preprocess();
		
	}
	
	protected CFTargetValueTableRunCalculator getCFTargetValueTableRunCalculator() {
		return CFTargetValueTableRunCalculator;
	}

	/**
	 * return the target Evaluator
	 * @return
	 */
	protected E getEvaluator() {
		return this.evaluator;
	}
	
	/////////////////////////////////
	/**
	 * find out the number of columns in the ResultSet
	 * @throws SQLException
	 */
	private void preprocess() throws SQLException {
		ResultSetMetaData rsmd = this.resultSet.getMetaData();
		this.columnNum = rsmd.getColumnCount();
	}
	
	
	///////////////////////////////
	/**
	 * calculate and return the calculated string value of the given SQLAggregateFunctionBasedInputVariable
	 * @param siv
	 * @return
	 * @throws SQLException 
	 */
	protected String getSQLAggregateFunctionBasedInputVariableCalculatedStringValue(SQLAggregateFunctionBasedInputVariable siv) throws SQLException {
		if(this.SQLAggregateFunctionBasedInputVariableCalculatedStringValueMap == null) {
			this.SQLAggregateFunctionBasedInputVariableCalculatedStringValueMap = new HashMap<>();
		}
		
		//
		if(!this.SQLAggregateFunctionBasedInputVariableCalculatedStringValueMap.containsKey(siv)) {
			SQLAggregateFunctionBasedInputVariableCalculator calculator = 
					new SQLAggregateFunctionBasedInputVariableCalculator(
							this.CFTargetValueTableRunCalculator,
							siv);
			
			this.SQLAggregateFunctionBasedInputVariableCalculatedStringValueMap.put(siv, calculator.getCaluclatedStringValue());
		}
		
		return this.SQLAggregateFunctionBasedInputVariableCalculatedStringValueMap.get(siv);
	}
	
	///////////////////////////////////////

	/**
	 * get the next row in the {@link resultSet} with a unique RUID from previous ones and return the List of Object values for the columns in the row;
	 * return null if the {@link resultSet} is fully traversed;
	 * 
	 * the order of the object values in the returned list is the same with the order of {@link NonSQLQueryBasedEvaluator#getOrderedListOfRecordwiseInputVariable()}
	 * @return
	 * @throws SQLException 
	 */
	protected abstract List<Object> getNextRUIDObjectValueList() throws SQLException;
	
	
	/**
	 * update the value tables with the extracted output variable calculated string values; 
	 * @param outputVariableCalculatedStringValueMap
	 * @param RUID
	 * @throws SQLException 
	 */
	protected void updateValueTables(Map<OutputVariable, String> outputVariableCalculatedStringValueMap, int RUID) throws SQLException{
		this.componentFunctionValueTableUpdator.updateValueTables(outputVariableCalculatedStringValueMap, RUID);
	}
	
	
	///////////////////////////////////////
	/**
	 * perform this updator;
	 * 
	 * 
	 * @throws SQLException 
	 */
	public abstract void perform() throws SQLException;
	
}
