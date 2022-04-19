package function.component;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import basic.HasNotes;
import basic.reproduce.Reproducible;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.evaluator.Evaluator;
import function.variable.input.InputVariable;
import function.variable.output.OutputVariable;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;


/**
 * interface for a node on the component tree owned by a {@link CompositionFunction};
 * 
 * each ComponentFunction on the same tree should have its unique index id different from others;
 * 
 * 
 * @author tanxu
 *
 */
public interface ComponentFunction extends HasNotes, Reproducible {
	/**
	 * return the {@link CompositionFunctionID} of the owner {@link CompositionFunction} of this {@link ComponentFunction};
	 * @return
	 */
	CompositionFunctionID getHostCompositionFunctionID();
	
	
	/**
	 * return a unique id among all ComponentFunctions on the same tree
	 * @return
	 */
	int getIndexID(); 
	
	
	/////////////////////////////////////////
	
	/**
	 * return the set of {@link ComponentFunction} that are right downstream of this {@link ComponentFunction};
	 * @return
	 */
	Set<ComponentFunction> getIncidentDownstreamComponentFunctionSet();
	
	/**
	 * return the set of {@link ComponentFunction} that are at downstream of this {@link ComponentFunction} (not including this one)
	 * @return
	 */
	Set<ComponentFunction> getAllDownstreamComponentFunctionSet();
	
	
	
	///////////////////////////////////////////
	/**
	 * return the set of Evaluators owned by this {@link ComponentFunction}
	 * @return
	 */
	Set<Evaluator> getEvaluatorSet();
	
	Set<InputVariable> getInputVariableSetOfThisAndAllDownstreamComponentFunctions();
	
	
	Set<OutputVariable> getOutputVariableSetOfThisAndAllDownstreamComponentFunctions();
	
	
	////calculation
	/**
	 * Return the {@link Evaluator} of this ComponentFunction with the given index ID;
	 * note that for {@link PiecewiseFunction}, the index ID of {@link Evaluator} of each condition is the same with the condition's precedence index;
	 * @param indexID
	 * @return
	 */
	Evaluator getEvaluator(int indexID);
	
	
	/**
	 * build the map from each upstream {@link PiecewiseFunction}'s index ID to the output index to this ComponentFunction;
	 * then invoke the same method of all incident downstream {@link ComponentFunction}s;
	 * 
	 * @param previousComponentFunctionBuiltMap the map of previous {@link ComponentFunction}(need to copy each entry for this function rather than directly use the map!!!!!!!)
	 * @param previousPiecewiseFunctionIndexID index id of the previous PiecewisFunction that leads to this function; null if previous function is not PiecewiseFunction
	 * @param previousPiecewiseFunctionOutputIndex the output index of the previous PiecewisFunction that leads to this function; null if previous function is not PiecewiseFunction
	 */
	void buildUpstreamPiecewiseFunctionIndexIDOutputIndexMap(
			Map<Integer,Integer> previousComponentFunctionBuiltMap, 
			Integer previousPiecewiseFunctionIndexID, 
			Integer previousPiecewiseFunctionOutputIndex);
	
	/**
	 * return the UpstreamPiecewiseFunctionIndexIDOutputIndexMap
	 * @return
	 */
	Map<Integer,Integer> getUpstreamPiecewiseFunctionIndexIDOutputIndexMap();
	
	
	/**
	 * calculate all output variables for qualified records of this ComponentFunction and update the related value tables;
	 * 
	 * then invoke the same method of all incident downstream ComponentFunctions;
	 * @param calculator
	 * @throws SQLException 
	 */
	void calculate(CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator) throws SQLException;
	
	
	
	
	//////////////////////////////////////////////
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	ComponentFunction reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;
	
}
