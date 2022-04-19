package function.evaluator.nonsqlbased.stringprocessing;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
 * concatenate a list of input variables' string value and output a single string output variable;
 * 
 * if variables in concatenatedInputVariableList are not of string type, transform them to string type first;
 * 
 * 
 * =======================================
 * how to deal with null value of input variable for some record:
 * 		if any of the input variable in {@link #concatenatedInputVariableList} has null value for some record, the variable will be ignored when calculating;
 * 		
 * 		if {@link #concatenatingStringInputVariable} is null or has null value for some record, 
 * 			if {@link #defaultConcatenatingString} is not null, use it as concatenating string;
 * 			else, the output variable will be calculated to null for the corresponding records;
 * 
 * @author tanxu
 * 
 */
public class StringConcatenationEvaluator extends SimpleStringProcessingEvaluator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5586197920032307475L;

	
	////////////////////////////////
	/**
	 * list of input variables whose value strings are to be concatenated;
	 * 
	 * for input variable of non-string/varchar type, will be transformed into string format first when calculating;
	 * 
	 * if a variable’s value is null, ignore it;
	 * 
	 * can be of any type including {@link ConstantValuedInputVariable} type;
	 */
	private final List<InputVariable> concatenatedInputVariableList;
	
	/**
	 * input variable whose string value is used as concatenating string; 
	 * 
	 * if null or string value is null for some record, use the {@link #defaultConcatenatingString};
	 * 
	 * can be of any type including {@link ConstantValuedInputVariable} type;
	 */
	private final InputVariable concatenatingStringInputVariable;
	
	/**
	 * concatenating string if {@link #concatenatingStringInputVariable} has null value;
	 * if this string is set to null, the output variable's value will be null if {@link #concatenatingStringInputVariable} has null value;
	 */
	private final String defaultConcatenatingString;
	
	/**
	 * output variable for the concatenated string; must be of string type;
	 * 
	 * since it is not boolean type, thus cannot be used for PiecewiseFunction's condition evaluator, thus ValueTableColumnOutputVariable type rather than EvaluatorSpecificOutputVariable
	 */
	private final ValueTableColumnOutputVariable outputVariable;
	
	/**
	 * 
	 * @param hostCompositionFunctionID
	 * @param hostComponentFunctionIndexID
	 * @param notes
	 * @param indexID
	 * @param concatenatedInputVariableList cannot be null or empty; each InputVariable can be of any data type;
	 * @param concatenatingStringInputVariable can be null, but a non-null defaultConcatenatingString should be given; if not null, data type can be of any type;
	 * @param defaultConcatenatingString can be null, but a non-null concatenatingInputVariable must be given; if null and concatenatingInputVariable's string value is null for some record, output variable will be calculated to null;
	 * @param outputVariable cannot be null; must be of string type;
	 */
	public StringConcatenationEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID,
			int indexID,
			VfNotes notes,
			
			List<InputVariable> concatenatedInputVariableList,
			InputVariable concatenatingStringInputVariable,
			String defaultConcatenatingString,
			ValueTableColumnOutputVariable outputVariable
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes);
		
		//validations
		//concatenatedInputVariableList must be non-null and non-empty;
		if(concatenatedInputVariableList==null||concatenatedInputVariableList.isEmpty()) {
			throw new IllegalArgumentException("given concatenatedInputVariableList cannot be null or empty!");
		}
		
		if(concatenatingStringInputVariable==null&&defaultConcatenatingString==null) {
			throw new IllegalArgumentException("given concatenatingInputVariable and defaultConcatenatingString cannot both be null!");
		}
		
		//outputVariable must be of string type
		if(!outputVariable.getSQLDataType().isOfStringType()) {
			throw new IllegalArgumentException("given outputVariable must be of string type!");
		}
		
		
		//input variables must have different alias names
		Set<SimpleName> inputVariableAliasNameSet = new HashSet<>();
		concatenatedInputVariableList.forEach(e->{
			if(inputVariableAliasNameSet.contains(e.getAliasName())) {
				throw new IllegalArgumentException("duplicate alias name is found for multiple input variables:"+e.getAliasName().getStringValue());
			}
			inputVariableAliasNameSet.add(e.getAliasName());
		});
		
		if(concatenatingStringInputVariable!=null)
			if(inputVariableAliasNameSet.contains(concatenatingStringInputVariable.getAliasName()))
				throw new IllegalArgumentException("duplicate alias name is found for multiple input variables:"+concatenatingStringInputVariable.getAliasName().getStringValue());
		
		
		/////////////////////////////
		this.concatenatedInputVariableList = concatenatedInputVariableList;
		this.concatenatingStringInputVariable = concatenatingStringInputVariable;
		this.defaultConcatenatingString = defaultConcatenatingString;
		this.outputVariable = outputVariable;
		
	}



	public List<InputVariable> getConcatenatedInputVariableList() {
		return concatenatedInputVariableList;
	}


	public InputVariable getConcatenatingStringInputVariable() {
		return concatenatingStringInputVariable;
	}


	public String getDefaultConcatenatingString() {
		return defaultConcatenatingString;
	}

	public ValueTableColumnOutputVariable getOutputVariable() {
		return outputVariable;
	}
	
	////////////////////////////////////////////
	@Override
	public Map<SimpleName, InputVariable> getInputVariableAliasNameMap() {
//		if(this.inputVariableAliasNameMap==null) {
		Map<SimpleName, InputVariable> ret = new HashMap<>();
			
		this.concatenatedInputVariableList.forEach(e->{
			ret.put(e.getAliasName(), e);
		});
		
		
		if(this.concatenatingStringInputVariable!=null)
			ret.put(this.concatenatingStringInputVariable.getAliasName(), this.concatenatingStringInputVariable);
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
	
	//////////////////////////////////
	@Override
	public Map<OutputVariable, String> evaluate(Map<SimpleName, String> inputVariableAliasNameStringValueMap){
		Map<OutputVariable, String> ret = new HashMap<>();
		
		
		//////////////
		String concatenatingString;
		
		if(this.concatenatingStringInputVariable==null || inputVariableAliasNameStringValueMap.get(this.concatenatingStringInputVariable.getAliasName())==null) {
			if(this.defaultConcatenatingString==null) {//no concatenating string, output variable will be null;
				ret.put(this.getOutputVariable(), null);
				return ret;
			}else {
				concatenatingString = this.defaultConcatenatingString;
			}
		}else {
			if(this.concatenatingStringInputVariable instanceof ConstantValuedInputVariable) {
				concatenatingString = ((ConstantValuedInputVariable)this.concatenatingStringInputVariable).getValueString();
			}else {
				concatenatingString = inputVariableAliasNameStringValueMap.get(this.concatenatingStringInputVariable.getAliasName());
			}
		}
		
		
		
		/////////////////////
		StringBuilder sb = new StringBuilder();
		boolean nothingAddedYet = true;
		for(InputVariable iv: this.getConcatenatedInputVariableList()) {
			String variableString;
			
			if(inputVariableAliasNameStringValueMap.get(iv.getAliasName())==null) {
				if(iv instanceof ConstantValuedInputVariable) {
					variableString = ((ConstantValuedInputVariable)iv).getValueString();
				}else {
					continue;
				}
				
			}else {
				variableString = inputVariableAliasNameStringValueMap.get(iv.getAliasName());
			}
			
			if(nothingAddedYet) {
				nothingAddedYet = false;
			}else {
				sb.append(concatenatingString);
			}
			sb.append(variableString);
		}
		
		
		ret.put(this.outputVariable, sb.toString());
		
		return ret;
	}


	
	///////////////////////////////////////////
	/**
	 * reproduce and return a new StringConcatenation of this one;
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public StringConcatenationEvaluator reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		////
		CompositionFunctionID reproducedHostCompositionFunctionID = 
				this.getHostCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		int reproducedHostComponentFunctionIndexID = this.getHostComponentFunctionIndexID();
		int reproducedIndexID = this.getIndexID();
		VfNotes reproducedNotes = this.getNotes().reproduce();
		
		
		List<InputVariable> concatenatedInputVariableList = new ArrayList<>();
		for(InputVariable iv:this.getConcatenatedInputVariableList()) {
			concatenatedInputVariableList.add(iv.reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex));
		}
		
		InputVariable concatenatingStringInputVariable = 
				this.getConcatenatingStringInputVariable()==null?null:
					this.getConcatenatingStringInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		String defaultConcatenatingString = this.getDefaultConcatenatingString();
		
		ValueTableColumnOutputVariable outputVariable = 
				this.getOutputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		
		////////////////////////
		StringConcatenationEvaluator ret = new StringConcatenationEvaluator(
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedIndexID,
				reproducedNotes,
				concatenatedInputVariableList,
				concatenatingStringInputVariable,
				defaultConcatenatingString,
				outputVariable
				);
		
		return ret;
	}


	////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((concatenatedInputVariableList == null) ? 0 : concatenatedInputVariableList.hashCode());
		result = prime * result
				+ ((concatenatingStringInputVariable == null) ? 0 : concatenatingStringInputVariable.hashCode());
		result = prime * result + ((defaultConcatenatingString == null) ? 0 : defaultConcatenatingString.hashCode());
		result = prime * result + ((outputVariable == null) ? 0 : outputVariable.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof StringConcatenationEvaluator))
			return false;
		StringConcatenationEvaluator other = (StringConcatenationEvaluator) obj;
		if (concatenatedInputVariableList == null) {
			if (other.concatenatedInputVariableList != null)
				return false;
		} else if (!concatenatedInputVariableList.equals(other.concatenatedInputVariableList))
			return false;
		if (concatenatingStringInputVariable == null) {
			if (other.concatenatingStringInputVariable != null)
				return false;
		} else if (!concatenatingStringInputVariable.equals(other.concatenatingStringInputVariable))
			return false;
		if (defaultConcatenatingString == null) {
			if (other.defaultConcatenatingString != null)
				return false;
		} else if (!defaultConcatenatingString.equals(other.defaultConcatenatingString))
			return false;
		if (outputVariable == null) {
			if (other.outputVariable != null)
				return false;
		} else if (!outputVariable.equals(other.outputVariable))
			return false;
		return true;
	}


}
