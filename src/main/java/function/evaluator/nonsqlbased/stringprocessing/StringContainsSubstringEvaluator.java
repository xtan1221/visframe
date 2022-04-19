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
import function.evaluator.CanBeUsedForPiecewiseFunctionConditionEvaluatorType;
import function.variable.input.InputVariable;
import function.variable.input.nonrecordwise.type.ConstantValuedInputVariable;
import function.variable.output.OutputVariable;

/**
 * 
 * check whether the string value of the target variable contains the string value of the substring variable
 * 
 * 
 * ========================
 * how to deal with null value of input variable for some record:
 * 		if any of the {@link #targetInputVariable} or {@link #substringInputVariable} has null value for some record, the output variable will be calculated to be null;
 * 		if {@link #toIgnoreCaseInputVariable} is null or has null value for some record, the {@link #toIgnoreCaseByDefault} will be used;
 * @author tanxu
 *
 */
public class StringContainsSubstringEvaluator extends SimpleStringProcessingEvaluator implements CanBeUsedForPiecewiseFunctionConditionEvaluatorType {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3240208238439478961L;
	
	////////////////////////////
	/**
	 * input variable whose string value will be checked; 
	 * 
	 * if null valued for some record, output variable should be null for that record;
	 * 
	 * can be of any type including {@link ConstantValuedInputVariable} type; however, FreeInputVariable should be rare case
	 */
	private final InputVariable targetInputVariable;
	
	/**
	 * input variable whose string value will be used as substring; 
	 * 
	 * if null valued for some record, output variable should be null for that record;
	 * 
	 * can be of any type including {@link ConstantValuedInputVariable} type;
	 */
	private final InputVariable substringInputVariable;
	
	/**
	 * 
	 * Boolean type whose value indicate whether to ignore case or not; can be null;
	 * 
	 * if null valued for some record or this parameter is null, use the {@link #toIgnoreCaseByDefault};
	 * 
	 */
	private final InputVariable toIgnoreCaseInputVariable;
	
	/**
	 * whether to ignore case or not; used only if {@link toIgnoreCaseInputVariable} is null or has null value for some record;
	 */
	private final boolean toIgnoreCaseByDefault;
	
	
	/**
	 * output variable, must be of Boolean type;
	 * 
	 * since it is boolean type, thus can be used as PiecewiseFunction condition evaluator, thus EvaluatorSpecificOutputVariable type rather than ValueTableColumnOutputVariable
	 */
	private final OutputVariable outputVariable;
	
	
	/**
	 * 
	 * @param hostCompositionFunctionID
	 * @param hostComponentFunctionIndexID
	 * @param indexID
	 * @param notes
	 * @param targetInputVariable not null;
	 * @param substringInputVariable not null;
	 * @param toIgnoreCaseInputVariable can be null; must be of boolean type if not null;
	 * @param toIgnoreCaseByDefault
	 * @param outputVariable not null; must be of boolean type;
	 */
	public StringContainsSubstringEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID,
			int indexID,
			VfNotes notes,
			
			InputVariable targetInputVariable,
			InputVariable substringInputVariable,
			InputVariable toIgnoreCaseInputVariable,
			boolean toIgnoreCaseByDefault,
			OutputVariable outputVariable
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes);
		//validations
		
		if(targetInputVariable==null)
			throw new IllegalArgumentException("given targetInputVariable cannot be null!");
		
		if(substringInputVariable==null)
			throw new IllegalArgumentException("given substringInputVariable cannot be null!");
		
		if(toIgnoreCaseInputVariable!=null && !toIgnoreCaseInputVariable.getSQLDataType().isBoolean())
			throw new IllegalArgumentException("given toIgnoreCaseInputVariable must be of boolean type if not null!");
		
		if(outputVariable==null)
			throw new IllegalArgumentException("given outputVariable cannot be null!");
		
		if(!outputVariable.getSQLDataType().isBoolean())
			throw new IllegalArgumentException("given outputVariable must be of boolean type!");
		
		
		//input variables must have different alias names
		Set<SimpleName> inputVariableAliasNameSet = new HashSet<>();
		inputVariableAliasNameSet.add(targetInputVariable.getAliasName());
		
		if(inputVariableAliasNameSet.contains(substringInputVariable.getAliasName()))
			throw new IllegalArgumentException("duplicate alias name is found for multiple input variables:"+substringInputVariable.getAliasName().getStringValue());
		inputVariableAliasNameSet.add(substringInputVariable.getAliasName());
		
		if(toIgnoreCaseInputVariable!=null)
			if(inputVariableAliasNameSet.contains(toIgnoreCaseInputVariable.getAliasName()))
				throw new IllegalArgumentException("duplicate alias name is found for multiple input variables:"+toIgnoreCaseInputVariable.getAliasName().getStringValue());
		
		
		/////////////////
		this.targetInputVariable = targetInputVariable;
		this.substringInputVariable = substringInputVariable;
		this.toIgnoreCaseInputVariable = toIgnoreCaseInputVariable;
		this.toIgnoreCaseByDefault = toIgnoreCaseByDefault;
		this.outputVariable = outputVariable;
	}

	public InputVariable getTargetInputVariable() {
		return targetInputVariable;
	}

	public InputVariable getSubstringInputVariable() {
		return substringInputVariable;
	}

	public InputVariable getToIgnoreCaseInputVariable() {
		return toIgnoreCaseInputVariable;
	}

	public boolean isToIgnoreCaseByDefault() {
		return toIgnoreCaseByDefault;
	}

	public OutputVariable getOutputVariable() {
		return outputVariable;
	}

	////////////////////////////////////////////
	@Override
	public Map<SimpleName, InputVariable> getInputVariableAliasNameMap() {
//		if(this.inputVariableAliasNameMap==null) {
		Map<SimpleName, InputVariable> ret = new HashMap<>();
			
		ret.put(this.targetInputVariable.getAliasName(), this.targetInputVariable);
		ret.put(this.substringInputVariable.getAliasName(), this.substringInputVariable);
			
		if(this.toIgnoreCaseInputVariable!=null)
			ret.put(this.toIgnoreCaseInputVariable.getAliasName(), this.toIgnoreCaseInputVariable);
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

	
	///////////////////////////////////
	@Override
	public Map<OutputVariable, String> evaluate(Map<SimpleName, String> inputVariableAliasNameStringValueMap){
		Map<OutputVariable, String> ret = new HashMap<>();
		
		//if targetInputVariable or substringInputVariable is not of ConstantValuedInputVariable and its value is null in given inputVariableAliasNameStringValueMap, output variable is evaluated to null;
		if(!(this.targetInputVariable instanceof ConstantValuedInputVariable) && inputVariableAliasNameStringValueMap.get(this.targetInputVariable.getAliasName())==null
				||
				!(this.substringInputVariable instanceof ConstantValuedInputVariable) && inputVariableAliasNameStringValueMap.get(this.substringInputVariable.getAliasName())==null) {
			ret.put(this.outputVariable, null);
			return ret;
		}
		
		//////////////
		boolean toIgnoreCase;
		
		if(this.toIgnoreCaseInputVariable!=null) {
			if(this.toIgnoreCaseInputVariable instanceof ConstantValuedInputVariable) {
				toIgnoreCase = Boolean.parseBoolean(((ConstantValuedInputVariable)this.toIgnoreCaseInputVariable).getValueString());
			}else {
				if(inputVariableAliasNameStringValueMap.get(this.toIgnoreCaseInputVariable.getAliasName())==null) {
					toIgnoreCase = this.toIgnoreCaseByDefault;
				}else {
					toIgnoreCase = Boolean.parseBoolean(inputVariableAliasNameStringValueMap.get(this.toIgnoreCaseInputVariable.getAliasName()));
				}
			}
		}else {
			toIgnoreCase = this.toIgnoreCaseByDefault;
		}
		
		////////////////
		String targetString;
		if(this.targetInputVariable instanceof ConstantValuedInputVariable) {
			targetString = ((ConstantValuedInputVariable)targetInputVariable).getValueString();
		}else {
			targetString = inputVariableAliasNameStringValueMap.get(this.targetInputVariable.getAliasName());
		}
		String subString;
		if(this.substringInputVariable instanceof ConstantValuedInputVariable) {
			subString = ((ConstantValuedInputVariable)substringInputVariable).getValueString();
		}else {
			subString = inputVariableAliasNameStringValueMap.get(this.substringInputVariable.getAliasName());
		}
		
		
		if(toIgnoreCase) {
			ret.put(this.outputVariable, 
					Boolean.toString(
							targetString.toLowerCase()
							.contains(subString.toLowerCase()))
					);
		}else {
			ret.put(this.outputVariable, 
					Boolean.toString(
							targetString
							.contains(subString))
					);
		}
		
		
		return ret;
	}


	///////////////////////////
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public StringContainsSubstringEvaluator reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		CompositionFunctionID reproducedHostCompositionFunctionID = 
				this.getHostCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		int reproducedHostComponentFunctionIndexID = this.getHostComponentFunctionIndexID();
		int reproducedIndexID = this.getIndexID();
		VfNotes reproducedNotes = this.getNotes().reproduce();
		
		InputVariable targetInputVariable = 
				this.getTargetInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		InputVariable substringInputVariable = 
				this.getSubstringInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		InputVariable toIgnoreCaseInputVariable = 
				this.getToIgnoreCaseInputVariable()==null?null:
					this.getToIgnoreCaseInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		boolean toIgnoreCaseByDefault = this.isToIgnoreCaseByDefault();
		OutputVariable outputVariable = 
				this.getOutputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		
		return new StringContainsSubstringEvaluator(
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedIndexID,
				reproducedNotes,
				targetInputVariable,
				substringInputVariable,
				toIgnoreCaseInputVariable,
				toIgnoreCaseByDefault,
				outputVariable
			);
	}

	
	
	//////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((outputVariable == null) ? 0 : outputVariable.hashCode());
		result = prime * result + ((substringInputVariable == null) ? 0 : substringInputVariable.hashCode());
		result = prime * result + ((targetInputVariable == null) ? 0 : targetInputVariable.hashCode());
		result = prime * result + (toIgnoreCaseByDefault ? 1231 : 1237);
		result = prime * result + ((toIgnoreCaseInputVariable == null) ? 0 : toIgnoreCaseInputVariable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof StringContainsSubstringEvaluator))
			return false;
		StringContainsSubstringEvaluator other = (StringContainsSubstringEvaluator) obj;
		if (outputVariable == null) {
			if (other.outputVariable != null)
				return false;
		} else if (!outputVariable.equals(other.outputVariable))
			return false;
		if (substringInputVariable == null) {
			if (other.substringInputVariable != null)
				return false;
		} else if (!substringInputVariable.equals(other.substringInputVariable))
			return false;
		if (targetInputVariable == null) {
			if (other.targetInputVariable != null)
				return false;
		} else if (!targetInputVariable.equals(other.targetInputVariable))
			return false;
		if (toIgnoreCaseByDefault != other.toIgnoreCaseByDefault)
			return false;
		if (toIgnoreCaseInputVariable == null) {
			if (other.toIgnoreCaseInputVariable != null)
				return false;
		} else if (!toIgnoreCaseInputVariable.equals(other.toIgnoreCaseInputVariable))
			return false;
		return true;
	}

	
	
}
