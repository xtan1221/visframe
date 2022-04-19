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
 * extract a substring from the input variable’s string value;
 * 
 * if {@link #startPosInputVariable}'s value is negative, 0 will be used;
 * if {@link #lengthInputVariable}'s value is non-positive, output variable will be empty string;
 * 
 * 
 * 
 * if {@link #startPosInputVariable}'s value s has value so that s+1 is larger then the length of the {@link #targetInputVariable}'s value string, 
 * 		output variable will be empty string;
 * 
 * if {@link #lengthInputVariable}'s value len has value so that s+1+len is larger than the length of the {@link #targetInputVariable}'s value string, 
 * 		output variable will be the full substring starting from s to the end of the {@link #targetInputVariable}'s value string;
 * 
 * 
 * ======================================================
 * how to deal with null value of input variable for some record:
 * 
 * 		if any of {@link #targetInputVariable}, {@link #startPosInputVariable} or {@link #lengthInputVariable}'s string value is null for some record, 
 * 			the output variable will be null;
 * 
 * @author tanxu
 *
 */
public class StringSubstringEvaluator extends SimpleStringProcessingEvaluator {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6375051619942486776L;
	public static final boolean DEFAULT_TO_IGNORE_CASE = false;
	
	
	///////////////////////////
	/**
	 * input variable whose value string will be subbed; 
	 * 
	 * if null valued for some record, output variable's value will be null for such records;
	 * can be of any data type; if not string type, string format will be used;
	 * can be of any type including {@link ConstantValuedInputVariable} type; 
	 */
	private final InputVariable targetInputVariable;
	/**
	 * input variable for start position, must be integer type; must be set; 
	 * if null valued for some record, output variable's value will be null for such records;
	 * can be of any type including {@link ConstantValuedInputVariable} type; 
	 */
	private final InputVariable startPosInputVariable;
	
	/**
	 * input variable for length, must be integer type; must be set; 
	 * if null valued for some record, output variable's value will be null for such records;
	 * can be of any type including {@link ConstantValuedInputVariable} type; 
	 */
	private final InputVariable lengthInputVariable;
	
	
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
	 * @param startPosInputVariable not null; must be of int type
	 * @param lengthInputVariable; not null; must be of int type
	 * @param outputVariable not null; must be of string type
	 */
	public StringSubstringEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID, 
			int indexID,
			VfNotes notes,
			
			InputVariable targetInputVariable,
			InputVariable startPosInputVariable,
			InputVariable lengthInputVariable,
			ValueTableColumnOutputVariable outputVariable
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes);
		//validations
		if(targetInputVariable==null)
			throw new IllegalArgumentException("given targetInputVariable cannot be null!");
		
		if(startPosInputVariable==null)
			throw new IllegalArgumentException("given startPosInputVariable cannot be null!");
		if(lengthInputVariable==null)
			throw new IllegalArgumentException("given lengthInputVariable cannot be null!");
		
		if(outputVariable==null)
			throw new IllegalArgumentException("given outputVariable cannot be null!");
		if(!startPosInputVariable.getSQLDataType().isGenericInt())
			throw new IllegalArgumentException("given startPosInputVariable must be of integer type!");
		if(!lengthInputVariable.getSQLDataType().isGenericInt())
			throw new IllegalArgumentException("given lengthInputVariable must be of integer type!");
		if(!outputVariable.getSQLDataType().isOfStringType())
			throw new IllegalArgumentException("given outputVariable must be of string type!");
		
		//input variables must have different alias names
		Set<SimpleName> inputVariableAliasNameSet = new HashSet<>();
		inputVariableAliasNameSet.add(targetInputVariable.getAliasName());
		
		if(inputVariableAliasNameSet.contains(startPosInputVariable.getAliasName()))
			throw new IllegalArgumentException("duplicate alias name is found for multiple input variables:"+startPosInputVariable.getAliasName().getStringValue());
		inputVariableAliasNameSet.add(startPosInputVariable.getAliasName());
		
		if(inputVariableAliasNameSet.contains(lengthInputVariable.getAliasName()))
			throw new IllegalArgumentException("duplicate alias name is found for multiple input variables:"+lengthInputVariable.getAliasName().getStringValue());
		
		
		this.targetInputVariable = targetInputVariable;
		this.startPosInputVariable = startPosInputVariable;
		this.lengthInputVariable = lengthInputVariable;
		this.outputVariable = outputVariable;
	}
	

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	/**
	 * @return the targetInputVariable
	 */
	public InputVariable getTargetInputVariable() {
		return targetInputVariable;
	}


	/**
	 * @return the startPosInputVariable
	 */
	public InputVariable getStartPosInputVariable() {
		return startPosInputVariable;
	}


	/**
	 * @return the lengthInputVariable
	 */
	public InputVariable getLengthInputVariable() {
		return lengthInputVariable;
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
		ret.put(this.startPosInputVariable.getAliasName(), this.startPosInputVariable);
		ret.put(this.lengthInputVariable.getAliasName(), this.lengthInputVariable);
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
			inputVariableAliasNameStringValueMap.get(this.startPosInputVariable.getAliasName())==null&&!(this.startPosInputVariable instanceof ConstantValuedInputVariable)
			||
			inputVariableAliasNameStringValueMap.get(this.lengthInputVariable.getAliasName())==null&&!(this.lengthInputVariable instanceof ConstantValuedInputVariable)){
			
			ret.put(this.outputVariable, null);
			return ret;
		}
		
		////////
		String targetString;
		if(this.targetInputVariable instanceof ConstantValuedInputVariable) {
			targetString = ((ConstantValuedInputVariable)targetInputVariable).getValueString();
		}else {
			targetString = inputVariableAliasNameStringValueMap.get(this.targetInputVariable.getAliasName());
		}
		
		int start;
		if(this.startPosInputVariable instanceof ConstantValuedInputVariable) {
			start = Integer.parseInt(((ConstantValuedInputVariable)startPosInputVariable).getValueString());
		}else {
			start = Integer.parseInt(inputVariableAliasNameStringValueMap.get(this.startPosInputVariable.getAliasName()));
		}
		if(start<0)
			start=0;
		
		int length;
		if(this.lengthInputVariable instanceof ConstantValuedInputVariable) {
			length = Integer.parseInt(((ConstantValuedInputVariable)lengthInputVariable).getValueString());
		}else {
			length = Integer.parseInt(inputVariableAliasNameStringValueMap.get(this.lengthInputVariable.getAliasName()));
		}
		if(length<0)
			length = 0;
		
		
		//////
		//TODO
		if(start+1>=targetString.length()) {
			ret.put(this.outputVariable, "");
		}else if(start+length+1>=targetString.length()) {
			ret.put(this.outputVariable, targetString.substring(start));
		}else {
			ret.put(this.outputVariable, targetString.substring(start, start+length+1));
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
	public StringSubstringEvaluator reproduce(
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
		InputVariable startPosInputVariable = 
				this.getStartPosInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		InputVariable lengthInputVariable = 
				this.getLengthInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		ValueTableColumnOutputVariable outputVariable = 
				this.getOutputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		//////////////////////
		return new StringSubstringEvaluator(
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedIndexID,
				reproducedNotes,
				targetInputVariable,
				startPosInputVariable,
				lengthInputVariable,
				outputVariable
			);
	}

	/////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((lengthInputVariable == null) ? 0 : lengthInputVariable.hashCode());
		result = prime * result + ((outputVariable == null) ? 0 : outputVariable.hashCode());
		result = prime * result + ((startPosInputVariable == null) ? 0 : startPosInputVariable.hashCode());
		result = prime * result + ((targetInputVariable == null) ? 0 : targetInputVariable.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof StringSubstringEvaluator))
			return false;
		StringSubstringEvaluator other = (StringSubstringEvaluator) obj;
		if (lengthInputVariable == null) {
			if (other.lengthInputVariable != null)
				return false;
		} else if (!lengthInputVariable.equals(other.lengthInputVariable))
			return false;
		if (outputVariable == null) {
			if (other.outputVariable != null)
				return false;
		} else if (!outputVariable.equals(other.outputVariable))
			return false;
		if (startPosInputVariable == null) {
			if (other.startPosInputVariable != null)
				return false;
		} else if (!startPosInputVariable.equals(other.startPosInputVariable))
			return false;
		if (targetInputVariable == null) {
			if (other.targetInputVariable != null)
				return false;
		} else if (!targetInputVariable.equals(other.targetInputVariable))
			return false;
		return true;
	}


	
}
