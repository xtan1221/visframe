package function.evaluator.nonsqlbased.stringprocessing;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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
 * remove all empty spaces in the target input variable’s string value;
 * 
 * ======================================================
 * how to deal with null value of input variable for some record:
 * 
 * 		if targetInputVariable's string value is null for some record, 
 * 			the output variable will be null;
 * 
 * @author tanxu
 *
 */
public class StringRemoveEmptySpaceEvaluator extends SimpleStringProcessingEvaluator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6775229918168464441L;
	
	
	/////////////////////////////
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
	 * @param outputVariable not null; must be of string type;
	 */
	public StringRemoveEmptySpaceEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID,
			int indexID,
			VfNotes notes,
			
			InputVariable targetInputVariable,
			ValueTableColumnOutputVariable outputVariable
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes);
		//validations
		if(targetInputVariable==null)
			throw new IllegalArgumentException("given targetInputVariable cannot be null!");
		
		if(outputVariable==null)
			throw new IllegalArgumentException("given outputVariable cannot be null!");
		
		if(!outputVariable.getSQLDataType().isOfStringType())
			throw new IllegalArgumentException("given outputVariable must be of string type!");
		
		
		this.targetInputVariable = targetInputVariable;
		this.outputVariable = outputVariable;
	}
	

	/**
	 * @return the targetInputVariable
	 */
	public InputVariable getTargetInputVariable() {
		return targetInputVariable;
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
		if(!(this.targetInputVariable instanceof ConstantValuedInputVariable) && inputVariableAliasNameStringValueMap.get(this.targetInputVariable.getAliasName())==null) { 
			ret.put(this.outputVariable, null);
			return ret;
		}
		
		
		String targetString;
		if(this.targetInputVariable instanceof ConstantValuedInputVariable) {
			targetString = ((ConstantValuedInputVariable)targetInputVariable).getValueString();
		}else {
			targetString = inputVariableAliasNameStringValueMap.get(this.targetInputVariable.getAliasName());
		}
		
		//////
		ret.put(this.outputVariable, targetString.replaceAll("\\s", ""));
		
		return ret;
	}

	/////////////////////////////////
	
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public StringRemoveEmptySpaceEvaluator reproduce(
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
		ValueTableColumnOutputVariable outputVariable = 
				this.getOutputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		//////////////
		return new StringRemoveEmptySpaceEvaluator(
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedIndexID,
				reproducedNotes,
				targetInputVariable,
				outputVariable
				);
	}

	//////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((outputVariable == null) ? 0 : outputVariable.hashCode());
		result = prime * result + ((targetInputVariable == null) ? 0 : targetInputVariable.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof StringRemoveEmptySpaceEvaluator))
			return false;
		StringRemoveEmptySpaceEvaluator other = (StringRemoveEmptySpaceEvaluator) obj;
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
		return true;
	}
	
	
	
}