package function.evaluator.nonsqlbased;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.variable.input.InputVariable;
import function.variable.input.recordwise.RecordwiseInputVariable;
import function.variable.output.OutputVariable;


/**
 * 
 * ==========================
 * how to deal with null value of input variable for some record:
 * 
 * 		if {@link #inputVariable} has null value for some record, the output variable will be evaluated to be null for those records;
 * 
 * 
 * @author tanxu
 *
 */
public class TypeTransformer extends NonSQLQueryBasedEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4287902378624868747L;
	
	//////////////////////////
	private final RecordwiseInputVariable inputVariable;
	private final OutputVariable outputVariable;
	
	/**
	 * 
	 * @param hostCompositionFunctionID
	 * @param notes
	 * @param inputVariable not null; data type must be different from outputVariable
	 * @param outputVariable not null; data type must be different from inputVariable
	 */
	protected TypeTransformer(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID, 
			int indexID,
			VfNotes notes,
			
			RecordwiseInputVariable inputVariable,
			OutputVariable outputVariable
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes);
		//validations
		if(inputVariable==null)
			throw new IllegalArgumentException("given inputVariable cannot be null!");
		if(outputVariable==null)
			throw new IllegalArgumentException("given outputVariable cannot be null!");
		
		//input and output variable must be of different data type in terms of the corresponding java data type since the type transformation is carried out with java methods
		//TODO
		
		
		
		this.inputVariable = inputVariable;
		this.outputVariable = outputVariable;
	}

	public RecordwiseInputVariable getInputVariable() {
		return inputVariable;
	}


	public OutputVariable getOutputVariable() {
		return outputVariable;
	}

	////////////////////////////////////////////
	@Override
	public Map<SimpleName, InputVariable> getInputVariableAliasNameMap() {
//		if(this.inputVariableAliasNameMap==null) {
		Map<SimpleName, InputVariable> ret = new HashMap<>();
			
		ret.put(this.inputVariable.getAliasName(), this.inputVariable);
			
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
		
		//TODO
		ret.put(this.outputVariable, inputVariableAliasNameStringValueMap.get(this.inputVariable.getAliasName()));
		
		
		return ret;
	}

	///////////////////////
	/**
	 * reproduce and return a new TypeTransformer of this one;
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public TypeTransformer reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		CompositionFunctionID reproducedHostCompositionFunctionID = 
				this.getHostCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		int reproducedHostComponentFunctionIndexID = this.getHostComponentFunctionIndexID();
		int reproducedIndexID = this.getIndexID();
		VfNotes reproducedNotes = this.getNotes().reproduce();
		
		RecordwiseInputVariable inputVariable = this.getInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		OutputVariable outputVariable = this.getOutputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		/////////////////////
		TypeTransformer reproduced = new TypeTransformer(
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedIndexID,
				reproducedNotes,
				
				inputVariable,
				outputVariable
				);
		return reproduced;
	}


	//////////////
	//TODO equals and hashcode
}
