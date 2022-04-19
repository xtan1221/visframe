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
 * extract the length of the input variable’s string value
 * 
 * @author tanxu
 *
 */
public class StringLengthEvaluator extends SimpleStringProcessingEvaluator {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3513329971629833964L;
	
	
	////////////////////////
	/**
	 * input variable whose string value length will be counted; 
	 * 
	 * if null valued for some record, output variable should be null for that record;
	 * 
	 * can be of any type including {@link ConstantValuedInputVariable} type; however, FreeInputVariable should be rare case
	 */
	private final InputVariable targetInputVariable;
	
	/**
	 * 
	 * Boolean type whose value indicate whether to trim the string value or not; can be null;
	 * 
	 * if null valued for some record or this parameter is null, use the {@link #toTrimInputVariableByDefault};
	 * 
	 */
	private final InputVariable toTrimInputVariable;
	
	/**
	 * whether to trim the string value or not; used only if {@link toTrimInputVariable} is null or has null value for some record;
	 */
	private final boolean toTrimInputVariableByDefault;
	
	
	/**
	 * output variable for the concatenated string; must be of int type;
	 * since it is not boolean type, thus cannot be used for PiecewiseFunction's condition evaluator, thus ValueTableColumnOutputVariable type rather than EvaluatorSpecificOutputVariable
	 */
	private final ValueTableColumnOutputVariable outputVariable;
	
	
	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param hostComponentFunctionIndexID
	 * @param indexID
	 * @param notes
	 * @param targetInputVariable not null;
	 * @param toTrimInputVariable can be null; must be of boolean type if not null;
	 * @param toTrimInputVariableByDefault 
	 * @param outputVariable not null; must be of int type
	 */
	public StringLengthEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID,
			int indexID,
			VfNotes notes,
			
			InputVariable targetInputVariable,
			InputVariable toTrimInputVariable,
			boolean toTrimInputVariableByDefault,
			ValueTableColumnOutputVariable outputVariable
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes);
		
		//validations
		if(targetInputVariable==null)
			throw new IllegalArgumentException("given targetInputVariable cannot be null!");
		
		if(toTrimInputVariable!=null && !toTrimInputVariable.getSQLDataType().isBoolean())
			throw new IllegalArgumentException("given toTrimInputVariable must be of boolean type if not null!");
		
		if(outputVariable==null)
			throw new IllegalArgumentException("given outputVariable cannot be null!");
		
		if(!outputVariable.getSQLDataType().isGenericInt())
			throw new IllegalArgumentException("given outputVariable must be of int type!");
		
		//input variables must have different alias names
		Set<SimpleName> inputVariableAliasNameSet = new HashSet<>();
		inputVariableAliasNameSet.add(targetInputVariable.getAliasName());
		
		if(toTrimInputVariable!=null)
			if(inputVariableAliasNameSet.contains(toTrimInputVariable.getAliasName()))
				throw new IllegalArgumentException("duplicate alias name is found for multiple input variables:"+toTrimInputVariable.getAliasName().getStringValue());
		
		
		
		////////////////////////
		this.targetInputVariable = targetInputVariable;
		this.toTrimInputVariable = toTrimInputVariable;
		this.toTrimInputVariableByDefault = toTrimInputVariableByDefault;
		this.outputVariable = outputVariable;
	}
	
	
	/**
	 * @return the targetInputVariable
	 */
	public InputVariable getTargetInputVariable() {
		return targetInputVariable;
	}


	/**
	 * @return the toTrimInputVariable
	 */
	public InputVariable getToTrimInputVariable() {
		return toTrimInputVariable;
	}


	/**
	 * @return the toTrimInputVariableByDefault
	 */
	public boolean isToTrimInputVariableByDefault() {
		return toTrimInputVariableByDefault;
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
			
		if(this.toTrimInputVariable!=null)
			ret.put(this.toTrimInputVariable.getAliasName(), this.toTrimInputVariable);
			
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
		
		//if targetInputVariable' string value is null, the output variable will be null;
		if(inputVariableAliasNameStringValueMap.get(this.targetInputVariable.getAliasName())==null 
				&& !(this.targetInputVariable instanceof ConstantValuedInputVariable)){
			ret.put(this.outputVariable, null);
			return ret;
		}
		
		/////////
		boolean toTrim;
		if(this.toTrimInputVariable instanceof ConstantValuedInputVariable) {
			toTrim = Boolean.parseBoolean(((ConstantValuedInputVariable)toTrimInputVariable).getValueString());
		}else {
			if(inputVariableAliasNameStringValueMap.get(this.toTrimInputVariable.getAliasName())==null) {
				toTrim = this.toTrimInputVariableByDefault;
			}else {
				toTrim = Boolean.parseBoolean(inputVariableAliasNameStringValueMap.get(this.toTrimInputVariable.getAliasName()));
			}
		}
		////////
		String targetString;
		if(this.targetInputVariable instanceof ConstantValuedInputVariable) {
			targetString = ((ConstantValuedInputVariable)targetInputVariable).getValueString();
		}else {
			targetString = inputVariableAliasNameStringValueMap.get(this.targetInputVariable.getAliasName());
		}
		
		//////
		if(toTrim) {
			ret.put(this.outputVariable, Integer.toString(targetString.trim().length()));
		}else {
			ret.put(this.outputVariable, Integer.toString(targetString.length()));
		}
		
		return ret;
	}
	
	
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public StringLengthEvaluator reproduce(
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
		InputVariable toTrimInputVariable = 
				this.getToTrimInputVariable()==null?null:
					this.getToTrimInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		boolean toTrimInputVariableByDefault = this.isToTrimInputVariableByDefault();
		ValueTableColumnOutputVariable outputVariable = 
				this.getOutputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		
		return new StringLengthEvaluator(
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedIndexID,
				reproducedNotes,
				targetInputVariable,
				toTrimInputVariable,
				toTrimInputVariableByDefault,
				outputVariable
			);
	}

	/////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((outputVariable == null) ? 0 : outputVariable.hashCode());
		result = prime * result + ((targetInputVariable == null) ? 0 : targetInputVariable.hashCode());
		result = prime * result + ((toTrimInputVariable == null) ? 0 : toTrimInputVariable.hashCode());
		result = prime * result + (toTrimInputVariableByDefault ? 1231 : 1237);
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof StringLengthEvaluator))
			return false;
		StringLengthEvaluator other = (StringLengthEvaluator) obj;
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
		if (toTrimInputVariable == null) {
			if (other.toTrimInputVariable != null)
				return false;
		} else if (!toTrimInputVariable.equals(other.toTrimInputVariable))
			return false;
		if (toTrimInputVariableByDefault != other.toTrimInputVariableByDefault)
			return false;
		return true;
	}

	
}
