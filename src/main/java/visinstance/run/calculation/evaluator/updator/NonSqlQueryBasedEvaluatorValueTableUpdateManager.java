package visinstance.run.calculation.evaluator.updator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basic.SimpleName;
import function.component.ComponentFunction;
import function.evaluator.nonsqlbased.NonSQLQueryBasedEvaluator;
import function.variable.input.InputVariable;
import function.variable.input.nonrecordwise.NonRecordwiseInputVariable;
import function.variable.input.nonrecordwise.type.ConstantValuedInputVariable;
import function.variable.input.nonrecordwise.type.FreeInputVariable;
import function.variable.input.nonrecordwise.type.SQLAggregateFunctionBasedInputVariable;
import function.variable.input.recordwise.RecordwiseInputVariable;
import function.variable.output.OutputVariable;
import visinstance.run.calculation.evaluator.sqlbuilder.NonSqlBasedEvaluatorSqlQueryStringBuilder;
import visinstance.run.calculation.evaluator.updator.utils.ComponentFunctionValueTableUpdator;
import visinstance.run.calculation.function.variable.SQLAggregateFunctionBasedInputVariableCalculator;

/**
 * 102320-update
 * base class to perform value table updating with a {@link NonSQLQueryBasedEvaluator} after a sql query built by a 
 * {@link NonSqlBasedEvaluatorSqlQueryStringBuilder} is built and run;
 * 
 * note that the sql query string built by {@link NonSqlBasedEvaluatorSqlQueryStringBuilder} has the set of select elements in the order of:
 * 1. RUID column
 * 2. Ordered List Of {@link RecordwiseInputVariable}s retrieved by {@link NonSQLQueryBasedEvaluator#getOrderedListOfRecordwiseInputVariable()};
 * 
 * 
 * basic steps:
 * 
 * for each RUID of the owner record data table:
 * 1. find out the values of input variables of the NonSqlQueryBasedEvaluator;
 * 		1. values of {@link RecordwiseInputVariable}s from the ResultSet of the sql query built by {@link NonSqlBasedEvaluatorSqlQueryStringBuilder};
 * 		2. values of {@link NonRecordwiseInputVariable}s;
 * 
 * 2. calculate the output variable values of the {@link NonSQLQueryBasedEvaluator} with the input variable values;
 * 
 * 3. insert the calculated output variable values into the corresponding value tables;
 * 		1. {@link PiecewiseFunctionNonSqlBasedEvaluatorValueTableUpdator}
 * 		2. {@link SimpleFunctionNonSqlBasedEvaluatorValueTableUpdator}
 * 
 * 
 * 
 * @author tanxu
 * 
 */
public class NonSqlQueryBasedEvaluatorValueTableUpdateManager<C extends ComponentFunction,U extends ComponentFunctionValueTableUpdator<C>> extends EvaluatorValueTableUpdateManager<C,NonSQLQueryBasedEvaluator,U>{
	
	/////////////////////
	/**
	 * map of string values for all {@link NonRecordwiseInputVariable}s of the {@link NonSQLQueryBasedEvaluator} which are the same for all records of the owner record data table;
	 * 
	 */
	private Map<SimpleName, String> nonRecordwiseInputVariableAliasNameStringValueMap;
	
	
	/**
	 * the value of RUID column of current row;
	 */
	protected int currentRUID;
	
	/**
	 * 
	 * @param CFTargetValueTableRunCalculator
	 * @param evaluator
	 * @param resultSet
	 * @param componentFunctionValueTableUpdator
	 * @throws SQLException 
	 */
	public NonSqlQueryBasedEvaluatorValueTableUpdateManager(
			visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator,
			NonSQLQueryBasedEvaluator evaluator, 
			ResultSet resultSet,
			U componentFunctionValueTableUpdator) throws SQLException {
		super(CFTargetValueTableRunCalculator, evaluator, resultSet, componentFunctionValueTableUpdator);
		// TODO Auto-generated constructor stub
	}
	

	//////////////////////////////////////
	
	/**
	 * build and return the map from alias name of each {@link RecordwiseInputVariable} to the string value 
	 * 
	 * note that the index of each RecordwiseInputVariable in the given objectValueList is equal to the index in the {@link NonSQLQueryBasedEvaluator#getOrderedListOfRecordwiseInputVariable()} + 1!
	 * 		this is because the first element in the given objectValueList is the value of the RUID column;
	 * 
	 * @param objectValueList
	 * @return
	 */
	private Map<SimpleName, String> getRecordwiseInputVariableAliasNameStringValueMap(List<Object> objectValueList){
		Map<SimpleName, String> ret = new HashMap<>();
		
		for(InputVariable iv:this.getEvaluator().getInputVariableAliasNameMap().values()) {
			if(iv instanceof RecordwiseInputVariable) {
				Object objectValue = objectValueList.get(this.getEvaluator().getOrderedListOfRecordwiseInputVariable().indexOf(iv)+1);//index+1, because the first one is the RUID column, should skip it;
				
				ret.put(iv.getAliasName(),objectValue==null?null:objectValue.toString());
			}
		}
		
		
		return ret;
	}

	/**
	 * build (if not already) and return the string values of all {@link NonRecordwiseInputVariable}s of the target {@link NonSQLQueryBasedEvaluator};
	 * @throws SQLException 
	 * 
	 */
	private Map<SimpleName, String> getNonRecordwiseInputVariableAliasNameStringValueMap() throws SQLException{
		if(this.nonRecordwiseInputVariableAliasNameStringValueMap == null) {
			this.nonRecordwiseInputVariableAliasNameStringValueMap = new HashMap<>();
			for(InputVariable iv:this.getEvaluator().getInputVariableAliasNameMap().values()) {
				if(iv instanceof NonRecordwiseInputVariable) {
					if(iv instanceof ConstantValuedInputVariable) {
						ConstantValuedInputVariable cviv = (ConstantValuedInputVariable)iv;
						this.nonRecordwiseInputVariableAliasNameStringValueMap.put(cviv.getAliasName(), cviv.getValueString());
						
					}else if(iv instanceof FreeInputVariable) {
						FreeInputVariable fiv = (FreeInputVariable)iv;
						
						this.nonRecordwiseInputVariableAliasNameStringValueMap.put(
								fiv.getAliasName(), 
								this.getCFTargetValueTableRunCalculator().getIndependetFIVTypeStringValueMap().getAssignedStringValue(fiv.getIndependentFreeInputVariableType().getID()));
						
					}else if(iv instanceof SQLAggregateFunctionBasedInputVariable) {
						
						SQLAggregateFunctionBasedInputVariable siv = (SQLAggregateFunctionBasedInputVariable)iv;
						SQLAggregateFunctionBasedInputVariableCalculator calculator = 
								new SQLAggregateFunctionBasedInputVariableCalculator(this.getCFTargetValueTableRunCalculator(), siv);
						this.nonRecordwiseInputVariableAliasNameStringValueMap.put(
								siv.getAliasName(),
								calculator.getCaluclatedStringValue()
								);
					}else {
						throw new UnsupportedOperationException("unrecognized NonRecordwiseInputVariable type!");
					}
				}
			}
		}
		
		return this.nonRecordwiseInputVariableAliasNameStringValueMap;
	}
	/////////////////////////////////////////
	/**
	 * get the Object values of the list of all selected elements of next row in the resultSet 
	 * 
	 * 1. the first list element with index 0 is the RUID column value
	 * 		note that the first column in the ResultSet has index 1 rather than 0!
	 * 2. following list elements (starting with index 1) are the values of the RecordwiseInputVariables with the same order as returned by {@link NonSQLQueryBasedEvaluator#getOrderedListOfRecordwiseInputVariable()};
	 * 
	 * return null if the ResultSet reaches the end;
	 */
	@Override
	protected List<Object> getNextRUIDObjectValueList() throws SQLException {
		if(this.resultSet.next()) {
			
			List<Object> ret = new ArrayList<>();
			
			for(int i=1;i<=this.columnNum;i++) {
				ret.add(this.resultSet.getObject(i));
			}
			
			return ret;
		}else {
			return null;
		}
	}
	
	
	/**
	 * process the ResultSet to retrieve a single row r for each owner data table RUID column value
	 * calculate output variables of the nonSqlQueryBasedEvaluator with the column values in r (and any FIV and NonRecordwiseInputVariables);
	 * use the output variables of the nonSqlQueryBasedEvaluator to update the value tables;
	 * @throws SQLException 
	 */
	@Override
	public void perform() throws SQLException {
		List<Object> nextRUIDRecordwiseInputVariableValueObjectList;
		while((nextRUIDRecordwiseInputVariableValueObjectList = this.getNextRUIDObjectValueList())!=null) {
			
			Map<SimpleName, String> inputVariableAliasNameStringValueMap = new HashMap<>();
			inputVariableAliasNameStringValueMap.putAll(this.getNonRecordwiseInputVariableAliasNameStringValueMap());
			inputVariableAliasNameStringValueMap.putAll(this.getRecordwiseInputVariableAliasNameStringValueMap(nextRUIDRecordwiseInputVariableValueObjectList));
			
			Map<OutputVariable, String> outputVariableCalculatedStringValueMap = 
					this.getEvaluator().evaluate(inputVariableAliasNameStringValueMap);
			
			this.updateValueTables(
					outputVariableCalculatedStringValueMap,
					(Integer)nextRUIDRecordwiseInputVariableValueObjectList.get(0)
					);
		}
		
		//update the last batch;
		this.componentFunctionValueTableUpdator.updateLastBatch();
		//post processing?????
		//TODO
	}
	
	
	
	
}
