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
 * 
 * 
 * replace first or all substring in input string with a new string;
 * 
 * underlying method is {@link String#replace(CharSequence, CharSequence)} or {@link String#replaceAll(String, String)}
 * 
 * ======================================================
 * how to deal with null value of input variable for some record:
 * 
 * 		if any of {@link #targetInputVariable}, {@link #targetSubstringInputVariable} and {@link #replacingStringInputVariable}'s string value is null for some record, 
 * 			the output variable will be null;
 * 		if targetSubstringInputVariable's string value is empty string, the output variable's value will be evaluated to null;
 * @author tanxu
 *
 */
public class StringReplaceEvaluator extends SimpleStringProcessingEvaluator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4931326568617901051L;	
	
	///////////////////////////////
	/**
	 * input variable whose value string will be replaced; 
	 * if null valued, output variable's value will be null;
	 * can be of any data type; if not string type, string format will be used;
	 * can be of any type including {@link ConstantValuedInputVariable} type; 
	 */
	private final InputVariable targetInputVariable;
	
	/**
	 * input variable whose string value will be replaced by the string value of the {@link #replacingStringInputVariable}; 
	 * if string value is null or empty string for some record, output variable value will be null; 
	 * can be of any data type; if not string type, string format will be used;
	 * can be of any type including {@link ConstantValuedInputVariable} type; 
	 */
	private final InputVariable targetSubstringInputVariable;
	/**
	 * input variable whose string value will replace the string value of the {@link #targetSubstringInputVariable}; 
	 * if string value is null for some record, output variable value will be null; 
	 * can be of any data type; if not string type, string format will be used;
	 * can be of any type including {@link ConstantValuedInputVariable} type; 
	 */
	private final InputVariable replacingStringInputVariable;
	/**
	 * input variable of Boolean type; can be null;
	 * if true, replace all substrings in the target input variable’s string value; 
	 * if false or null valued, replace the first case;
	 * 
	 * if null or string value is null for some record, use {@link #toReplaceAllByDefault}
	 */
	private final InputVariable toReplaceAllInputVariable;
	
	/**
	 * if true, replace all by invoking {@link String#replaceAll(String, String)} method;
	 * if false, replace the first one;
	 */
	private final boolean toReplaceAllByDefault;
	
	/**
	 * output variable for the concatenated string; must be of string type;
	 * since it is not boolean type, thus cannot be used for PiecewiseFunction's condition evaluator, thus ValueTableColumnOutputVariable type rather than EvaluatorSpecificOutputVariable
	 */
	private final ValueTableColumnOutputVariable outputVariable;
	
	
	/**
	 * 
	 * @param hostCompositionFunctionID
	 * @param hostComponentFunctionIndexID
	 * @param indexID
	 * @param notes
	 * @param targetInputVariable not null;
	 * @param targetSubstringInputVariable not null;
	 * @param replacingStringInputVariable not null;
	 * @param toReplaceAllInputVariable can be null; must be of boolean type if not null;
	 * @param toReplaceAllByDefault 
	 * @param outputVariable cannot be null; must be of string type
	 */
	public StringReplaceEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID, 
			int indexID,
			VfNotes notes,
			
			InputVariable targetInputVariable,
			InputVariable targetSubstringInputVariable,
			InputVariable replacingStringInputVariable,
			InputVariable toReplaceAllInputVariable,
			boolean toReplaceAllByDefault,
			ValueTableColumnOutputVariable outputVariable
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes);
		//validations
		if(targetInputVariable==null)
			throw new IllegalArgumentException("given targetInputVariable cannot be null!");
		
		if(targetSubstringInputVariable==null)
			throw new IllegalArgumentException("given targetSubstringInputVariable cannot be null!");
		
		if(replacingStringInputVariable==null)
			throw new IllegalArgumentException("given replacingStringInputVariable cannot be null!");
		
		if(toReplaceAllInputVariable!=null && !toReplaceAllInputVariable.getSQLDataType().isBoolean())
			throw new IllegalArgumentException("given toReplaceAllInputVariable must be of boolean type if not null!");
		
		if(outputVariable==null)
			throw new IllegalArgumentException("given outputVariable cannot be null!");
		
		if(!outputVariable.getSQLDataType().isOfStringType())
			throw new IllegalArgumentException("given outputVariable must be of string type!");
		
		//if targetSubstringInputVariable is of ConstantValuedInputVariable type, its string value cannot be empty string;
		if(targetSubstringInputVariable instanceof ConstantValuedInputVariable) {
			if(((ConstantValuedInputVariable)targetSubstringInputVariable).getValueString().isEmpty()) {
				throw new IllegalArgumentException("given ConstantValuedInputVariable type targetSubstringInputVariable cannot have empty string value!");
			}
		}
		
		
		//input variables must have different alias names
		Set<SimpleName> inputVariableAliasNameSet = new HashSet<>();
		inputVariableAliasNameSet.add(targetInputVariable.getAliasName());
		
		if(inputVariableAliasNameSet.contains(targetSubstringInputVariable.getAliasName()))
			throw new IllegalArgumentException("duplicate alias name is found for multiple input variables:"+targetSubstringInputVariable.getAliasName().getStringValue());
		inputVariableAliasNameSet.add(targetSubstringInputVariable.getAliasName());
		
		if(inputVariableAliasNameSet.contains(replacingStringInputVariable.getAliasName()))
			throw new IllegalArgumentException("duplicate alias name is found for multiple input variables:"+replacingStringInputVariable.getAliasName().getStringValue());
		inputVariableAliasNameSet.add(replacingStringInputVariable.getAliasName());
		
		if(toReplaceAllInputVariable!=null)
			if(inputVariableAliasNameSet.contains(toReplaceAllInputVariable.getAliasName()))
				throw new IllegalArgumentException("duplicate alias name is found for multiple input variables:"+toReplaceAllInputVariable.getAliasName().getStringValue());
		
		/////////////////////////////
		this.targetInputVariable = targetInputVariable;
		this.targetSubstringInputVariable = targetSubstringInputVariable;
		this.replacingStringInputVariable = replacingStringInputVariable;
		this.toReplaceAllInputVariable = toReplaceAllInputVariable;
		this.toReplaceAllByDefault = toReplaceAllByDefault;
		this.outputVariable = outputVariable;
	}
	
	////////////////////////////
	/**
	 * @return the targetInputVariable
	 */
	public InputVariable getTargetInputVariable() {
		return targetInputVariable;
	}
	/**
	 * @return the targetSubstringInputVariable
	 */
	public InputVariable getTargetSubstringInputVariable() {
		return targetSubstringInputVariable;
	}

	/**
	 * @return the replacingStringInputVariable
	 */
	public InputVariable getReplacingStringInputVariable() {
		return replacingStringInputVariable;
	}

	/**
	 * @return the toReplaceAllInputVariable
	 */
	public InputVariable getToReplaceAllInputVariable() {
		return toReplaceAllInputVariable;
	}

	/**
	 * @return the toReplaceAllByDefault
	 */
	public boolean isToReplaceAllByDefault() {
		return toReplaceAllByDefault;
	}

	/**
	 * @return the outputVariable
	 */
	public ValueTableColumnOutputVariable getOutputVariable() {
		return outputVariable;
	}

	//////////////////////////
	@Override
	public Map<SimpleName, InputVariable> getInputVariableAliasNameMap() {
//		if(this.inputVariableAliasNameMap==null) {
		Map<SimpleName, InputVariable> ret = new HashMap<>();
		ret.put(this.targetInputVariable.getAliasName(), this.targetInputVariable);
		ret.put(this.targetSubstringInputVariable.getAliasName(), this.targetSubstringInputVariable);
		ret.put(this.replacingStringInputVariable.getAliasName(), this.replacingStringInputVariable);
			
		if(this.toReplaceAllInputVariable!=null)
			ret.put(this.toReplaceAllInputVariable.getAliasName(), this.toReplaceAllInputVariable);
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
		
		//if one of targetInputVariable, targetSubstringInputVariable or replacingStringInputVariable's string value is null, the output variable will be null;
		if(inputVariableAliasNameStringValueMap.get(this.targetInputVariable.getAliasName())==null&&!(this.targetInputVariable instanceof ConstantValuedInputVariable)
			||
			inputVariableAliasNameStringValueMap.get(this.targetSubstringInputVariable.getAliasName())==null&&!(this.targetSubstringInputVariable instanceof ConstantValuedInputVariable)
			||
			inputVariableAliasNameStringValueMap.get(this.replacingStringInputVariable.getAliasName())==null&&!(this.replacingStringInputVariable instanceof ConstantValuedInputVariable)){
			
			ret.put(this.outputVariable, null);
			return ret;
		}
		
		//if the targetSubstringInputVariable string value is empty string, the the output variable will be null
		if(inputVariableAliasNameStringValueMap.get(targetSubstringInputVariable.getAliasName()).isEmpty()) {
			ret.put(this.outputVariable, null);
			return ret;
		}
		
		
		/////////
		boolean toReplaceAll;
		if(this.toReplaceAllInputVariable instanceof ConstantValuedInputVariable) {
			toReplaceAll = Boolean.parseBoolean(((ConstantValuedInputVariable)toReplaceAllInputVariable).getValueString());
		}else {
			if(inputVariableAliasNameStringValueMap.get(this.toReplaceAllInputVariable.getAliasName())==null) {
				toReplaceAll = this.toReplaceAllByDefault;
			}else {
				toReplaceAll = Boolean.parseBoolean(inputVariableAliasNameStringValueMap.get(this.toReplaceAllInputVariable.getAliasName()));
			}
		}
		
		////////
		String targetString;
		if(this.targetInputVariable instanceof ConstantValuedInputVariable) {
			targetString = ((ConstantValuedInputVariable)targetInputVariable).getValueString();
		}else {
			targetString = inputVariableAliasNameStringValueMap.get(this.targetInputVariable.getAliasName());
		}
		String targetSubstring=null;
		if(this.targetSubstringInputVariable instanceof ConstantValuedInputVariable) {
			targetString = ((ConstantValuedInputVariable)targetSubstringInputVariable).getValueString();
		}else {
			targetString = inputVariableAliasNameStringValueMap.get(this.targetSubstringInputVariable.getAliasName());
		}
		String replacingString=null;
		if(this.replacingStringInputVariable instanceof ConstantValuedInputVariable) {
			targetString = ((ConstantValuedInputVariable)replacingStringInputVariable).getValueString();
		}else {
			targetString = inputVariableAliasNameStringValueMap.get(this.replacingStringInputVariable.getAliasName());
		}
		
		
		//////
		if(toReplaceAll) {
			ret.put(this.outputVariable, targetString.replaceAll(targetSubstring, replacingString));
		}else {
			ret.put(this.outputVariable, targetString.replaceFirst(targetSubstring, replacingString));
		}
		
		return ret;
	}
	

	////////////////////////////////////////
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public StringReplaceEvaluator reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		////
		CompositionFunctionID reproducedHostCompositionFunctionID = 
				this.getHostCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		int reproducedHostComponentFunctionIndexID = this.getHostComponentFunctionIndexID();
		int reproducedIndexID = this.getIndexID();
		VfNotes reproducedNotes = this.getNotes().reproduce();
		
		InputVariable targetInputVariable = 
				this.getTargetInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		InputVariable targetSubstringInputVariable = 
				this.getTargetSubstringInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		InputVariable replacingStringInputVariable = 
				this.getReplacingStringInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		InputVariable toReplaceAllInputVariable = 
				this.getToReplaceAllInputVariable()==null?null:
					this.getToReplaceAllInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		boolean toReplaceAllByDefault = this.isToReplaceAllByDefault();
		ValueTableColumnOutputVariable outputVariable = 
				this.getOutputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		///////////////////////
		return new StringReplaceEvaluator(
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedIndexID,
				reproducedNotes,
				targetInputVariable,
				targetSubstringInputVariable,
				replacingStringInputVariable,
				toReplaceAllInputVariable,
				toReplaceAllByDefault,
				outputVariable
			);
	}

	
	
	////////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((outputVariable == null) ? 0 : outputVariable.hashCode());
		result = prime * result
				+ ((replacingStringInputVariable == null) ? 0 : replacingStringInputVariable.hashCode());
		result = prime * result + ((targetInputVariable == null) ? 0 : targetInputVariable.hashCode());
		result = prime * result
				+ ((targetSubstringInputVariable == null) ? 0 : targetSubstringInputVariable.hashCode());
		result = prime * result + (toReplaceAllByDefault ? 1231 : 1237);
		result = prime * result + ((toReplaceAllInputVariable == null) ? 0 : toReplaceAllInputVariable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof StringReplaceEvaluator))
			return false;
		StringReplaceEvaluator other = (StringReplaceEvaluator) obj;
		if (outputVariable == null) {
			if (other.outputVariable != null)
				return false;
		} else if (!outputVariable.equals(other.outputVariable))
			return false;
		if (replacingStringInputVariable == null) {
			if (other.replacingStringInputVariable != null)
				return false;
		} else if (!replacingStringInputVariable.equals(other.replacingStringInputVariable))
			return false;
		if (targetInputVariable == null) {
			if (other.targetInputVariable != null)
				return false;
		} else if (!targetInputVariable.equals(other.targetInputVariable))
			return false;
		if (targetSubstringInputVariable == null) {
			if (other.targetSubstringInputVariable != null)
				return false;
		} else if (!targetSubstringInputVariable.equals(other.targetSubstringInputVariable))
			return false;
		if (toReplaceAllByDefault != other.toReplaceAllByDefault)
			return false;
		if (toReplaceAllInputVariable == null) {
			if (other.toReplaceAllInputVariable != null)
				return false;
		} else if (!toReplaceAllInputVariable.equals(other.toReplaceAllInputVariable))
			return false;
		return true;
	}

	
	

}