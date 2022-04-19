package function.evaluator;

import java.sql.SQLException;
import java.util.Map;
import basic.HasNotes;
import basic.SimpleName;
import basic.reproduce.Reproducible;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.evaluator.nonsqlbased.NonSQLQueryBasedEvaluator;
import function.evaluator.nonsqlbased.stringprocessing.StringProcessingEvaluator;
import function.evaluator.sqlbased.utils.VfSQLExpression;
import function.target.CFGTarget;
import function.variable.input.InputVariable;
import function.variable.input.nonrecordwise.type.ConstantValuedInputVariable;
import function.variable.output.OutputVariable;
import function.variable.output.type.CFGTargetOutputVariable;



/**
 * interface for defining a calculation rule that transform a set of input variables into a set of output variables;
 * 
 * ==================================================091320
 * input variables are of {@link InputVariable} type;
 * 
 * output variables must be of type {@link OutputVariable}
 * 
 * basic constraints: 
 * 1. uniqueness constraints of {@link InputVariable}s
 * 		only {@link InputVariable}s in the same {@link NonSQLQueryBasedEvaluator} are required to have different alias names;
 * 			note that this is a strong constraints than needed for {@link StringProcessingEvaluator} if {@link ConstantValuedInputVariable} is used;
 * 			this constraints is implicitly required by the {@link NonSQLQueryBasedEvaluator#evaluate(Map)} method;
 * 		
 * 		for those in the same {@link SQLQueryBasedEvaluator}, alias name uniqueness are required for each {@link VfSQLExpression};
 * 		
 * 2. {@link OutputVariable}s of the same {@link Evaluator} must have different alias names
 * 	also, {@link OutputVariable}s of the same {@link Evaluator} of type {@link CFGTargetOutputVariable} must be assigned to different {@link CFGTarget}s;
 * 
 * ==========================091320
 * note that how to deal with null value or invalid non-null value of {@link InputVariable}s and calculate the value of {@link OutputVariable} 
 * is implemented in each specific subclass of {@link Evaluator};
 * 
 * 
 * @author tanxu
 * 
 */
public interface Evaluator extends HasNotes, Reproducible{
	/**
	 * return the CompositionFunctionID of the host CompositionFunction of this Evaluator;
	 * @return
	 */
	CompositionFunctionID getHostCompositionFunctionID();

	/**
	 * return the index ID of the owner {@link ComponentFunction} of this {@link Evaluator}
	 * @return
	 */
	int getHostComponentFunctionIndexID();
	
	/**
	 * return the unique index id of this evaluator which is different from other evaluators in the same component function
	 * @return
	 */
	int getIndexID();
	
	/**
	 * return the set of {@link InputVariable} contained in this Evaluator;
	 * 
	 * note that for {@link SQLAggregateFunctionBasedInputVariable} type input variables, DO NOT include their recordwiseInputVariable1 and recordwiseInputVariable2;
	 * @return
	 */
	Map<SimpleName, InputVariable> getInputVariableAliasNameMap();
	
	
	/**
	 * return the set of {@link OutputVariable} contained in this Evaluator;
	 * @return
	 */
	Map<SimpleName, OutputVariable> getOutputVariableAliasNameMap();
	
	
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	Evaluator reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;
	
}
