package visinstance.run.calculation.evaluator.sqlbased;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import function.evaluator.sqlbased.utils.SelectElementExpression;
import function.evaluator.sqlbased.utils.VfSQLExpression;
import function.evaluator.sqlbased.utils.WhereConditionExpression;
import function.variable.input.InputVariable;
import function.variable.input.nonrecordwise.NonRecordwiseInputVariable;
import function.variable.input.nonrecordwise.type.ConstantValuedInputVariable;
import function.variable.input.nonrecordwise.type.FreeInputVariable;
import function.variable.input.nonrecordwise.type.SQLAggregateFunctionBasedInputVariable;
import function.variable.input.recordwise.RecordwiseInputVariable;
import function.variable.input.recordwise.type.CFGTargetInputVariable;
import function.variable.input.recordwise.type.RecordAttributeInputVariable;
import function.variable.input.recordwise.type.UpstreamValueTableColumnOutputVariableInputVariable;
import function.variable.output.type.CFGTargetOutputVariable;
import function.variable.output.type.TemporaryOutputVariable;
import rdb.table.AbstractRelationalTableSchemaID;
import rdb.table.HasIDTypeRelationalTableSchema;
import rdb.table.HasIDTypeRelationalTableSchemaID;
import utils.Pair;
import utils.visframe.SelectElementProcessor;
import utils.visframe.WhereConditionProcessor;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;
import visinstance.run.calculation.function.variable.SQLAggregateFunctionBasedInputVariableCalculator;


/**
 * processor for VfSQLExpression that builds real named sql string and find out the set of input table's AbstractRelationalTableSchemaID
 * 
 * 
 * @author tanxu
 *
 */
public class VfSQLExpressionProcessor {
	private final VfSQLExpression vfSQLExpression;
	private final CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator;
	
	///////////////////
	/**
	 * map from the alias name string of the input variable to the real value string that replaces the alias name to build a runnable sql string;
	 */
	private Map<String,String> aliasNameStringInUppercaseRealValueStringMap;
	
	
	///////////////////
	private String realValueSqlString;
	private Set<HasIDTypeRelationalTableSchemaID<? extends HasIDTypeRelationalTableSchema>> inputTableSchemaIDSet;
	
	/**
	 * constructor
	 * @param vfSQLExpression
	 * @param CFTargetValueTableRunCalculator
	 * @throws SQLException 
	 */
	public VfSQLExpressionProcessor(
			VfSQLExpression vfSQLExpression,
			CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator) throws SQLException{
		if(vfSQLExpression==null)
			throw new IllegalArgumentException("given VfSQLExpression cannot be null!");
		
		if(CFTargetValueTableRunCalculator==null)
			throw new IllegalArgumentException("given CFTargetValueTableRunCalculator cannot be null!");
		
		
		
		
		this.vfSQLExpression = vfSQLExpression;
		this.CFTargetValueTableRunCalculator = CFTargetValueTableRunCalculator;
		
		/////////////////////////////
		this.preprocess();
		this.replaceAliasNamesInSqlString();
	}
	
	/**
	 * return the built sql string with real values and real names that can be used to build a runnable sql in host VisProjectDBContext;
	 * @return
	 */
	public String getRealValuedSqlString() {
		return realValueSqlString;
	}
	/**
	 * 
	 * @return
	 */
	public Set<HasIDTypeRelationalTableSchemaID<? extends HasIDTypeRelationalTableSchema>> getInputTableSchemaIDSet(){
		return this.inputTableSchemaIDSet;
	}
	
	////////////////////////////////////////
	//calculation
	/**
	 * 102320-update
	* <p>build and return the sql string by replacing the alias input variable names in the sql expression by the real values from the given {@link CFTargetValueTableRunCalculator}, 
	* also find out {@link AbstractRelationalTableSchemaID}s of all involved tables (for construction of FROM clause);</p>
	* 
	* 1. initialize {@link #aliasNameStringInUppercaseRealValueStringMap}, 
	* 		map key is the alias name of the input variables in the {@link #aliasedExpressionString}, 
	*		map value is either constant value string or full path name of column of the input variable;
	*
	* 		also initialize {@link inputTableSchemaIDSet} for input tables;
	* 
	* 2. process each {@link InputVariable} in the {@link #vfSQLExpression}
	* 		1. for {@link NonRecordwiseInputVariable}
	* 			1. for {@link ConstantValuedInputVariable}
	* 				put the constant value string to the {@link #aliasNameStringInUppercaseRealValueStringMap};
	* 
	* 			2. for {@link FreeInputVariable}
	* 				put the assigned value string of the contained {@link IndependentFreeInputVariableType} to the {@link #aliasNameStringInUppercaseRealValueStringMap};
	* 			
	* 			3. for {@link SQLAggregateFunctionBasedInputVariable}
	* 		2. for {@link RecordwiseInputVariable}
	* 			1. for {@link CFGTargetInputVariable}
	* 				1. find out the {@link CFTargetValueTableRun} of the CompositionFunction to which the target is assigned;
	* 				2. add the target value table schema ID of the CompositionFunction to which the target is assigned to the {@link inputTableSchemaIDSet};
	* 				3. build the full path name of the target column in the value table schema and add to {@link #aliasNameStringInUppercaseRealValueStringMap};
	* 				
	* 			2. for {@link RecordAttributeInputVariable}
	* 				1. add the record data table schema id to the {@link inputTableSchemaIDSet};
	* 				2. build the full path name string of the column and add to {@link #aliasNameStringInUppercaseRealValueStringMap};
	* 
	* 			3. for {@link UpstreamValueTableColumnOutputVariableInputVariable}
	* 				1. if the upstream value table column output variable is of {@link CFGTargetOutputVariable} type
	* 					1. add the CF value table schema id of the {@link #CFTargetValueTableRunCalculator} to the {@link inputTableSchemaIDSet};
	* 					2. build the full path name string of the UpstreamValueTableColumnOutputVariable and add it to the {@link #aliasNameStringInUppercaseRealValueStringMap};
	* 				2. if the upstream value table column output variable is of {@link TemporaryOutputVariable} type
	* 					1. add the temporary output variable table schema id of the {@link #CFTargetValueTableRunCalculator} to the {@link inputTableSchemaIDSet};
	* 					2. build the full path name string of the TemporaryOutputVariable and add it to the {@link #aliasNameStringInUppercaseRealValueStringMap};
	* 
	* @param calculator
	* @return
	 * @throws SQLException 
	*/
	private void preprocess() throws SQLException{
		this.aliasNameStringInUppercaseRealValueStringMap = new HashMap<>();
		this.inputTableSchemaIDSet = new HashSet<>();
		
		for(SimpleName ivName:this.vfSQLExpression.getInputVariableAliasNameMap().keySet()) {
			InputVariable iv = this.vfSQLExpression.getInputVariableAliasNameMap().get(ivName);
			
			if(iv instanceof NonRecordwiseInputVariable) {
				if(iv instanceof ConstantValuedInputVariable) {
					ConstantValuedInputVariable cviv = (ConstantValuedInputVariable)iv;
					this.aliasNameStringInUppercaseRealValueStringMap.put(cviv.getAliasName().getStringValue().toUpperCase(), cviv.getValueString());
				}else if(iv instanceof FreeInputVariable) {
					FreeInputVariable fiv = (FreeInputVariable)iv;
					this.aliasNameStringInUppercaseRealValueStringMap.put(
							fiv.getAliasName().getStringValue().toUpperCase(), 
							this.CFTargetValueTableRunCalculator.getIndependetFIVTypeStringValueMap().getAssignedStringValue(fiv.getIndependentFreeInputVariableType().getID())
							);
				}else if(iv instanceof SQLAggregateFunctionBasedInputVariable) {
					SQLAggregateFunctionBasedInputVariable afbiv = (SQLAggregateFunctionBasedInputVariable)iv;
					
					SQLAggregateFunctionBasedInputVariableCalculator calculator = new SQLAggregateFunctionBasedInputVariableCalculator(this.CFTargetValueTableRunCalculator, afbiv);
					
					this.aliasNameStringInUppercaseRealValueStringMap.put(
							afbiv.getAliasName().getStringValue().toUpperCase(), calculator.getCaluclatedStringValue());
				}
				
				
			}else {//RecordwiseInputVariable
				RecordwiseInputVariable riv = (RecordwiseInputVariable)iv;
				Pair<HasIDTypeRelationalTableSchemaID<? extends HasIDTypeRelationalTableSchema>,String> tableSchemaIDColFullPathNameStringPair =
						this.CFTargetValueTableRunCalculator.buildRecordwiseInputVariableTableSchemaIDFullPathNameStringPair(riv);
				
				this.aliasNameStringInUppercaseRealValueStringMap.put(
						riv.getAliasName().getStringValue().toUpperCase(), tableSchemaIDColFullPathNameStringPair.getSecond());
				this.inputTableSchemaIDSet.add(tableSchemaIDColFullPathNameStringPair.getFirst());
				
			}
			
		}
		
		
	}
	
	
	/**
	 * replace the alias names in the {@link VfSQLExpression#getAliasedExpressionString()} with the constructed {@link #aliasNameStringInUppercaseRealValueStringMap} to 
	 * build the real valued sql expression string;
	 * 
	 */
	private void replaceAliasNamesInSqlString() {
		if(this.vfSQLExpression instanceof SelectElementExpression) {
			SelectElementProcessor processor = new SelectElementProcessor(this.vfSQLExpression.getAliasedSqlString());
			this.realValueSqlString = processor.replace(this.aliasNameStringInUppercaseRealValueStringMap);
			
		}else if(this.vfSQLExpression instanceof WhereConditionExpression) {
			WhereConditionProcessor processor = new WhereConditionProcessor(this.vfSQLExpression.getAliasedSqlString());
			
			this.realValueSqlString = processor.replace(this.aliasNameStringInUppercaseRealValueStringMap);
		}else {
			throw new UnsupportedOperationException();
		}
		
	}

}
