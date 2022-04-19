package function.evaluator.nonsqlbased;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.project.process.simple.CompositionFunctionInserter;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.evaluator.AbstractEvaluator;
import function.variable.input.InputVariable;
import function.variable.input.recordwise.RecordwiseInputVariable;
import function.variable.input.recordwise.type.CFGTargetInputVariable;
import function.variable.input.recordwise.type.RecordAttributeInputVariable;
import function.variable.input.recordwise.type.UpstreamValueTableColumnOutputVariableInputVariable;
import function.variable.output.OutputVariable;

/**
 * output variables values are evaluated record by record in java environment;
 * 
 * ==========================091320
 * note that {@link RecordwiseInputVariable}s in this evaluator type should obey the following constraints;
 * 
 * 		if of {@link CFGTargetInputVariable} type, the owner CompositionFunctionGroup of the target must be of the same owner record data of the host CompositionFunctionGroup of this evaluator;
 * 		if of {@link RecordAttributeInputVariable} type, the record data of the data table must be the same of the owner record data of the host CompositionFunctionGroup of this evaluator;
 * 		if of {@link UpstreamValueTableColumnOutputVariableInputVariable} type, trivial;
 * 
 * those constraints should be validated in the {@link CompositionFunctionInserter} class;
 * 
 * =============================091320
 * 1. different {@link InputVariable}s in the same {@link NonSQLQueryBasedEvaluator} must have different alias names;
 * 		validated in the constructor of subclasses of {@link NonSQLQueryBasedEvaluator}
 * 
 * @author tanxu
 *
 */
public abstract class NonSQLQueryBasedEvaluator extends AbstractEvaluator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7270847944047730142L;
	
	///////////////////////
	private transient List<RecordwiseInputVariable> orderedListOfRecordwiseInputVariable;
	
	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param notes
	 */
	protected NonSQLQueryBasedEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID,
			int indexID,
			VfNotes notes
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * calculate the output variable values for the given input variable string values of this Evaluator; 
	 * 
	 * note that the string value could be null, which needs to be dealt with in each specific subclass;
	 * 
	 * @param inputVariableStringValueMap
	 * @return
	 */
	public abstract Map<OutputVariable, String> evaluate(Map<SimpleName, String> inputVariableAliasNameStringValueMap);
	
	
	
	/**
	 * return a fixed list of RecordwiseInputVariables of this {@link NonSQLQueryBasedEvaluator} ordered by the alias name string values;
	 * facilitate building select clause in sql query string and processing the ResultSet;
	 * 
	 * @return
	 */
	public List<RecordwiseInputVariable> getOrderedListOfRecordwiseInputVariable(){
		if(this.orderedListOfRecordwiseInputVariable == null) {
			this.orderedListOfRecordwiseInputVariable = new ArrayList<>();
			
			for(InputVariable iv: this.getInputVariableAliasNameMap().values()) {
				if(iv instanceof RecordwiseInputVariable) {
					this.orderedListOfRecordwiseInputVariable.add((RecordwiseInputVariable)iv);
				}
			}
			
			Collections.sort(orderedListOfRecordwiseInputVariable);
		}
		
		return this.orderedListOfRecordwiseInputVariable;
	}
	
	
	
	///////////////////////////////
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public abstract NonSQLQueryBasedEvaluator reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;

}
