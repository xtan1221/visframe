package function.evaluator.nonsqlbased.stringprocessing;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.variable.input.InputVariable;
import function.variable.input.nonrecordwise.type.ConstantValuedInputVariable;
import function.variable.output.OutputVariable;
import function.variable.output.type.ValueTableColumnOutputVariable;

/**
 * change the string case to upper or lower;
 * 
 * ======================================================
 * how to deal with null value of input variable for some record:
 * 
 * 		if any of the two input variables' ({@link #targetInputVariable} and {@link #caseChangeIndicatorInputVariable}) string value is null for some record, 
 * 		the output variable will be null;
 * 
 * @author tanxu
 * 
 */
public class StringCaseChangeEvaluator extends SimpleStringProcessingEvaluator {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4303039400827124974L;
	
	
	////////////////////////////////////
	/**
	 * input variable whose value string case will be changed; 
	 * 
	 * if null valued, output variable's value will be null;
	 * 
	 * can be of any data type; if not string type, string format will be used;
	 * 
	 * can be of any type including {@link ConstantValuedInputVariable} type; though, FreeInputVariable should be rare case;
	 * 
	 */
	private final InputVariable targetInputVariable;
	
	
	/**
	 * input variable indicating how to change string case; must be of boolean type; 
	 * 
	 * together with {@link #toUpperCaseWhenTrue} to determine whether to change to upper case or lower case;
	 * 
	 * if null valued, output variable be null value;
	 * 
	 * can be of any type including {@link ConstantValuedInputVariable} type; 
	 */
	private final InputVariable caseChangeIndicatorInputVariable;
	
	/**
	 * whether when {@link #caseChangeIndicatorInputVariable} is true, change to upper case or not (lower case);
	 */
	private final boolean toUpperCaseWhenTrue;
	
	/**
	 * output variable for the concatenated string; must be of string type;
	 * since it is not boolean type, thus cannot be used for PiecewiseFunction's condition evaluator, thus ValueTableColumnOutputVariable type rather than EvaluatorSpecificOutputVariable
	 */
	private final ValueTableColumnOutputVariable outputVariable;
	
	
	/**
	 * 
	 * @param hostCompositionFunctionID
	 * @param hostComponentFunctionIndexID
	 * @param notes
	 * @param indexID
	 * @param targetInputVariable not null; see {@link #targetInputVariable} for more constraints;
	 * @param caseChangeIndicatorInputVariable not null; must be of boolean type see {@link #caseChangeIndicatorInputVariable}
	 * @param toUpperCaseWhenTrue see {@link #toUpperCaseWhenTrue}
	 * @param outputVariable not null; must be of string type
	 */
	public StringCaseChangeEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID,
			int indexID,
			VfNotes notes,
			
			InputVariable targetInputVariable,
			InputVariable caseChangeIndicatorInputVariable,
			boolean toUpperCaseWhenTrue,
			ValueTableColumnOutputVariable outputVariable
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes);
		
		if(targetInputVariable==null)
			throw new IllegalArgumentException("given targetInputVariable cannot be null!");
		
		if(caseChangeIndicatorInputVariable==null)
			throw new IllegalArgumentException("given caseChangeIndicatorInputVariable cannot be null!");
		
		if(outputVariable==null)
			throw new IllegalArgumentException("given outputVariable cannot be null!");
		
		
		if(!caseChangeIndicatorInputVariable.getSQLDataType().isBoolean()) {
			throw new IllegalArgumentException("given caseChangeIndicatorInputVariable must be of boolean data type!");
		}
		
		if(!outputVariable.getSQLDataType().isOfStringType()) {
			throw new IllegalArgumentException("given outputVariable must be of string data type!");
		}
		
		/////////////////////////////////////////
		//input variables must have different alias names
		Set<SimpleName> inputVariableAliasNameSet = new HashSet<>();
		inputVariableAliasNameSet.add(caseChangeIndicatorInputVariable.getAliasName());
		inputVariableAliasNameSet.add(targetInputVariable.getAliasName());
		if(inputVariableAliasNameSet.size()!=2) {
			throw new IllegalArgumentException("given targetInputVariable and caseChangeIndicatorInputVariable must have different alias name!");
		}
		
		////////////////
		this.targetInputVariable = targetInputVariable;
		this.caseChangeIndicatorInputVariable = caseChangeIndicatorInputVariable;
		this.toUpperCaseWhenTrue = toUpperCaseWhenTrue;
		this.outputVariable = outputVariable;
	}

	public InputVariable getTargetInputVariable() {
		return targetInputVariable;
	}
	
	public InputVariable getCaseChangeIndicatorInputVariable() {
		return caseChangeIndicatorInputVariable;
	}
	
	public boolean isToUpperCaseWhenTrue() {
		return toUpperCaseWhenTrue;
	}
	
	public ValueTableColumnOutputVariable getOutputVariable() {
		return outputVariable;
	}
	
	//////////////////////////
	@Override
	public Map<SimpleName, InputVariable> getInputVariableAliasNameMap() {
//		if(this.inputVariableAliasNameMap==null) {
		Map<SimpleName, InputVariable> ret = new HashMap<>();
		ret.put(this.caseChangeIndicatorInputVariable.getAliasName(), this.caseChangeIndicatorInputVariable);
		ret.put(this.targetInputVariable.getAliasName(), this.targetInputVariable);
//		}
		return ret;
	}
	
	@Override
	public Map<SimpleName, OutputVariable> getOutputVariableAliasNameMap(){
//		if(this.outputVariableAliasNameMap == null) {
		Map<SimpleName, OutputVariable> ret = new HashMap<>();
		ret.put(this.outputVariable.getAliasName(), this.outputVariable);
//		}
		
		return ret;
	}
	
	
	//////////////////////////////
	@Override
	public Map<OutputVariable, String> evaluate(Map<SimpleName, String> inputVariableAliasNameStringValueMap) {
		Map<OutputVariable, String> ret = new HashMap<>();
		
		//if any of the two input variables' string value is null, the output variable will be null;
		if(!(this.caseChangeIndicatorInputVariable instanceof ConstantValuedInputVariable) && inputVariableAliasNameStringValueMap.get(this.caseChangeIndicatorInputVariable.getAliasName())==null 
				|| 
				!(this.targetInputVariable instanceof ConstantValuedInputVariable) && inputVariableAliasNameStringValueMap.get(this.targetInputVariable.getAliasName())==null){
			ret.put(this.outputVariable, null);
			return ret;
		}
		
		
		boolean caseChangeIndicatorInputVariableValue;
		if(this.caseChangeIndicatorInputVariable instanceof ConstantValuedInputVariable) {
			caseChangeIndicatorInputVariableValue = Boolean.parseBoolean(((ConstantValuedInputVariable)caseChangeIndicatorInputVariable).getValueString());
		}else {
			caseChangeIndicatorInputVariableValue = Boolean.parseBoolean(inputVariableAliasNameStringValueMap.get(this.caseChangeIndicatorInputVariable.getAliasName()));
		}
		
		String targetInputVariableValue;
		if(this.targetInputVariable instanceof ConstantValuedInputVariable) {
			targetInputVariableValue = ((ConstantValuedInputVariable)caseChangeIndicatorInputVariable).getValueString();
		}else {
			targetInputVariableValue = inputVariableAliasNameStringValueMap.get(this.targetInputVariable.getAliasName());
		}
		
		//////
		if(this.toUpperCaseWhenTrue) {
			if(caseChangeIndicatorInputVariableValue) {
				ret.put(this.outputVariable, targetInputVariableValue.toUpperCase());
			}else {
				ret.put(this.outputVariable, targetInputVariableValue.toLowerCase());
			}
		}else {
			if(!caseChangeIndicatorInputVariableValue) {
				ret.put(this.outputVariable, targetInputVariableValue.toUpperCase());
			}else {
				ret.put(this.outputVariable, targetInputVariableValue.toLowerCase());
			}
		}
		
		return ret;
	}

	
	
	//////////////////////
	/**
	 * reproduce and return a new StringCaseChange of this one;
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public StringCaseChangeEvaluator reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
		CompositionFunctionID reproducedHostCompositionFunctionID = 
				this.getHostCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		int reproducedHostComponentFunctionIndexID = this.getHostComponentFunctionIndexID();
		int reproducedIndexID = this.getIndexID();
		VfNotes reproducedNotes = this.getNotes().reproduce();
		
		InputVariable targetInputVariable = this.getTargetInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		InputVariable caseChangeIndicatorInputVariable = 
				this.getCaseChangeIndicatorInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		boolean toUpperCaseWhenTrue = this.isToUpperCaseWhenTrue();
		ValueTableColumnOutputVariable outputVariable = 
				this.getOutputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		
		//////////////////////////
		return new StringCaseChangeEvaluator(
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedIndexID,
				reproducedNotes,
				targetInputVariable,
				caseChangeIndicatorInputVariable,
				toUpperCaseWhenTrue,
				outputVariable
				);
		
	}

	
	
	///////////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((caseChangeIndicatorInputVariable == null) ? 0 : caseChangeIndicatorInputVariable.hashCode());
		result = prime * result + ((outputVariable == null) ? 0 : outputVariable.hashCode());
		result = prime * result + ((targetInputVariable == null) ? 0 : targetInputVariable.hashCode());
		result = prime * result + (toUpperCaseWhenTrue ? 1231 : 1237);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof StringCaseChangeEvaluator))
			return false;
		StringCaseChangeEvaluator other = (StringCaseChangeEvaluator) obj;
		if (caseChangeIndicatorInputVariable == null) {
			if (other.caseChangeIndicatorInputVariable != null)
				return false;
		} else if (!caseChangeIndicatorInputVariable.equals(other.caseChangeIndicatorInputVariable))
			return false;
		if (outputVariable == null) {
			if (other.outputVariable != null)
				return false;
		} else if (!outputVariable.equals(other.outputVariable))
			return false;
		if (targetInputVariable == null) {
			if (other.targetInputVariable != null)
				return false;
		} else if (!targetInputVariable.equals(other.targetInputVariable))
			return false;
		if (toUpperCaseWhenTrue != other.toUpperCaseWhenTrue)
			return false;
		return true;
	}

	
	
	
}
