package function.evaluator.nonsqlbased;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.evaluator.CanBeUsedForPiecewiseFunctionConditionEvaluatorType;
import function.variable.input.InputVariable;
import function.variable.input.recordwise.RecordwiseInputVariable;
import function.variable.output.OutputVariable;

/**
 * evaluator with a single boolean type {@link OutputVariable} whose value is evaluated by checking if the value of a {@link RecordwiseInputVariable} is null or not;
 * 
 * in theory, the {@link #recordwiseInputVariable} should be nullable, otherwise this {@link Evaluator}'s output variable is always false, and becomes trivial;
 * 
 * 
 * @author tanxu
 * 
 */
public class RecordwiseInputVariableIsNullValuedEvaluator extends NonSQLQueryBasedEvaluator implements CanBeUsedForPiecewiseFunctionConditionEvaluatorType {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8584595688111875651L;
	
	////////////////////////
	/**
	 * can be of any data type
	 */
	private final RecordwiseInputVariable recordwiseInputVariable;
	
	/**
	 * output variable, must be of Boolean type;
	 * 
	 * since it is boolean type, thus can be used as PiecewiseFunction condition evaluator, thus EvaluatorSpecificOutputVariable type rather than ValueTableColumnOutputVariable
	 */
	private final OutputVariable outputVariable; 
	
	
	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param hostComponentFunctionIndexID
	 * @param indexID
	 * @param notes
	 * @param recordwiseInputVariable not null;
	 * @param outputVariable not null; must be of boolean type
	 */
	public RecordwiseInputVariableIsNullValuedEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID, int indexID, VfNotes notes,
			
			RecordwiseInputVariable recordwiseInputVariable,
			OutputVariable outputVariable
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes);
		// TODO Auto-generated constructor stub
		
		if(recordwiseInputVariable==null)
			throw new IllegalArgumentException("given recordwiseInputVariable cannot be null!");
		
		
		if(outputVariable==null)
			throw new IllegalArgumentException("given outputVariable cannot be null!");
		
		if(!outputVariable.getSQLDataType().isBoolean())
			throw new IllegalArgumentException("given outputVariable must be of boolean type!");
		
		
		this.recordwiseInputVariable = recordwiseInputVariable;
		this.outputVariable = outputVariable;
	}
	


	/**
	 * @return the recordwiseInputVariable
	 */
	public RecordwiseInputVariable getRecordwiseInputVariable() {
		return recordwiseInputVariable;
	}



	/**
	 * @return the outputVariable
	 */
	public OutputVariable getOutputVariable() {
		return outputVariable;
	}


	////////////////////////////////////////////
	@Override
	public Map<SimpleName, InputVariable> getInputVariableAliasNameMap() {
		Map<SimpleName, InputVariable> ret = new HashMap<>();
		
		ret.put(this.recordwiseInputVariable.getAliasName(), this.recordwiseInputVariable);
		
		return ret;
	}



	@Override
	public Map<SimpleName, OutputVariable> getOutputVariableAliasNameMap() {
		Map<SimpleName, OutputVariable> ret = new HashMap<>();
		ret.put(this.outputVariable.getAliasName(), this.outputVariable);
		
		return ret;
	}


	/**
	 * check if the the string value of the {@link #recordwiseInputVariable} is null or not and return the result as the value of the {@link #outputVariable};
	 */
	@Override
	public Map<OutputVariable, String> evaluate(Map<SimpleName, String> inputVariableAliasNameStringValueMap) {
		Map<OutputVariable, String> ret = new HashMap<>();
		
		ret.put(this.outputVariable, inputVariableAliasNameStringValueMap.get(this.recordwiseInputVariable.getAliasName())==null?"true":"false");
		
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
	public RecordwiseInputVariableIsNullValuedEvaluator reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex)throws SQLException {
		CompositionFunctionID reproducedHostCompositionFunctionID = 
				this.getHostCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		int reproducedHostComponentFunctionIndexID = this.getHostComponentFunctionIndexID();
		int reproducedIndexID = this.getIndexID();
		VfNotes reproducedNotes = this.getNotes().reproduce();
		
		RecordwiseInputVariable reproducedRecordwiseInputVariable = 
				this.getRecordwiseInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		OutputVariable reproducedOutputVariable = 
				this.getOutputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		
		//////////////////
		return new RecordwiseInputVariableIsNullValuedEvaluator(
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedIndexID,
				reproducedNotes,
				reproducedRecordwiseInputVariable,
				reproducedOutputVariable
			);
	}



	/////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((outputVariable == null) ? 0 : outputVariable.hashCode());
		result = prime * result + ((recordwiseInputVariable == null) ? 0 : recordwiseInputVariable.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof RecordwiseInputVariableIsNullValuedEvaluator))
			return false;
		RecordwiseInputVariableIsNullValuedEvaluator other = (RecordwiseInputVariableIsNullValuedEvaluator) obj;
		if (outputVariable == null) {
			if (other.outputVariable != null)
				return false;
		} else if (!outputVariable.equals(other.outputVariable))
			return false;
		if (recordwiseInputVariable == null) {
			if (other.recordwiseInputVariable != null)
				return false;
		} else if (!recordwiseInputVariable.equals(other.recordwiseInputVariable))
			return false;
		return true;
	}

	
	
}
