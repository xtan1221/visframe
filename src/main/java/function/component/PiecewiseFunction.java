package function.component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import basic.VfNotes;
import basic.reproduce.Reproducible;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.evaluator.Evaluator;
import function.variable.input.InputVariable;
import function.variable.output.OutputVariable;
import function.variable.output.type.PFConditionEvaluatorBooleanOutputVariable;
import utils.Pair;
import visinstance.run.calculation.function.component.PiecewiseFunctionCalculator;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;


/**
 * constraints:
 * 1. conditional next functions cannot be empty;
 * 2. default next function cannot be null;
 * 
 * 
 * correlation between the scope of each condition is not checked in this class; 
 * 
 * =======================================================091320
 * distinguish the following terms:
 * 1. precedence index
 * 		an ordered index for all conditions of the {@link PiecewiseFunction} that determines the order of evaluating each condition;
 * 		each condition should have a unique precedence index (must be consecutive integer from 1, 2, 3, ...) and a next {@link ComponentFunction}
 * 		note that for default next {@link ComponentFunction}, it's precedence index is NULL;
 * 
 * 2. evaluator index id
 * 		each condition has an boolean type Evaluator;
 * 		each Evaluator in the same {@link ComponentFunction} has a unique index id;
 * 		for {@link PiecewiseFunction}, condition Evaluator's index id is equal to the condition's precedence index;
 * 
 * 3. output index of a PiecewiseFunction
 * 		the index of each next {@link ComponentFunction} including all conditions and the default one;
 * 		the output index for default next {@link ComponentFunction} is {@link #DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX}
 * 		the output index for each condition next {@link ComponentFunction} is equal to the precedence index of the condition
 * 		
 * @author tanxu
 *
 */
public class PiecewiseFunction extends AbstractComponentFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4831617641295256321L;

	/**
	 * the output index of this PiecewiseFunction when the default next function is used;
	 */
	public static final int DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX = 0;
	
	
	///////////////////////////////////////
	private final ComponentFunction defaultNextFunction;
	private final Map<Integer, ConditionalEvaluatorDelegator> conditionPrecedenceIndexConditionalEvaluatorMap;
	private final Map<Integer, ComponentFunction> conditionPrecedenceIndexNextFunctionMap;
	
	
	
	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param notes
	 * @param ID
	 * @param defaultNextFunction cannot be null;
	 * @param conditionPrecedenceIndexConditionalEvaluatorMap cannot be null or empty; map key must be consecutive integer from 1, 2, 3, ...
	 * @param conditionPrecedenceIndexNextFunctionMap cannot be null or empty; map key must be consecutive integer from 1, 2, 3, ... 
	 */
	public PiecewiseFunction(
			CompositionFunctionID hostCompositionFunctionID,
			int ID,
			VfNotes notes,
			
			ComponentFunction defaultNextFunction,
			List<Pair<ConditionalEvaluatorDelegator,ComponentFunction>> orderedListOfConditionEvaluatorNextComponentFunctionPairByConditionPrecedenceIndex
			) {
		super(hostCompositionFunctionID, ID, notes);
		
		//defaultNextFunction cannot be null;
		if(defaultNextFunction==null) {
			throw new IllegalArgumentException("given defaultNextFunction cannot be null!");
		}
		
		//conditionPrecedenceIndexConditionalEvaluatorMap and conditionPrecedenceIndexNextFunctionMap cannot be null or empty;
		if(orderedListOfConditionEvaluatorNextComponentFunctionPairByConditionPrecedenceIndex==null||orderedListOfConditionEvaluatorNextComponentFunctionPairByConditionPrecedenceIndex.isEmpty()) {
			throw new IllegalArgumentException("given orderedListOfConditionEvaluatorNextComponentFunctionPairByConditionPrecedenceIndex cannot be null or empty!");
		}
		
		orderedListOfConditionEvaluatorNextComponentFunctionPairByConditionPrecedenceIndex.forEach(e->{
			if(e.getFirst()==null||e.getSecond()==null)
				throw new IllegalArgumentException("ConditionalEvaluatorDelegator and ComponentFunction cannot be null!");
		});
		
		
		
		
		////////////
		this.defaultNextFunction = defaultNextFunction;
		////
		this.conditionPrecedenceIndexConditionalEvaluatorMap = new LinkedHashMap<>();
		this.conditionPrecedenceIndexNextFunctionMap = new LinkedHashMap<>();
		
		for(int i=0;i<orderedListOfConditionEvaluatorNextComponentFunctionPairByConditionPrecedenceIndex.size();i++) {
			Pair<ConditionalEvaluatorDelegator,ComponentFunction> pair = 
					orderedListOfConditionEvaluatorNextComponentFunctionPairByConditionPrecedenceIndex.get(i);
			conditionPrecedenceIndexConditionalEvaluatorMap.put(i+1, pair.getFirst());
			conditionPrecedenceIndexNextFunctionMap.put(i+1, pair.getSecond());
		}
		
		
	}

	
	public ComponentFunction getDefaultNextFunction() {
		return this.defaultNextFunction;
	}
	
	public Map<Integer, ConditionalEvaluatorDelegator> getConditionPrecedenceIndexConditionalEvaluatorMap() {
		return Collections.unmodifiableMap(conditionPrecedenceIndexConditionalEvaluatorMap);
	}

	public Map<Integer, ComponentFunction> getConditionPrecedenceIndexNextFunctionMap(){
		return Collections.unmodifiableMap(this.conditionPrecedenceIndexNextFunctionMap);
	}
	
	
	/**
	 * return an list of Evaluators in the {@link #conditionPrecedenceIndexConditionalEvaluatorMap} ordered by the precedence Index
	 * @return
	 */
	public List<Evaluator> getConditionalEvaluatrListOrderedByPrecdenceIndex(){
		List<Evaluator> ret = new ArrayList<>();
		
		//
		for(int i=1;i<this.getConditionPrecedenceIndexConditionalEvaluatorMap().size()+1;i++) {
			ret.add(this.getConditionPrecedenceIndexConditionalEvaluatorMap().get(i).getEvaluator());
		}
		
		return ret;
	}
	
	
	//////////////////////////////////////

	@Override
	public Set<ComponentFunction> getIncidentDownstreamComponentFunctionSet() {
		Set<ComponentFunction> ret = new LinkedHashSet<>();
		
		ret.addAll(this.getConditionPrecedenceIndexNextFunctionMap().values());
		ret.add(this.getDefaultNextFunction());
		
		return ret;
	}


	@Override
	public Set<ComponentFunction> getAllDownstreamComponentFunctionSet() {
		Set<ComponentFunction> ret = new LinkedHashSet<>();
		
		ret.addAll(this.getIncidentDownstreamComponentFunctionSet());
		
		this.getIncidentDownstreamComponentFunctionSet().forEach(e->{
			ret.addAll(e.getAllDownstreamComponentFunctionSet());
		});
		
		return ret;
	}

	
	@Override
	public Set<Evaluator> getEvaluatorSet() {
		Set<Evaluator> ret =  new LinkedHashSet<>();
		
		ret.addAll(this.getConditionalEvaluatrListOrderedByPrecdenceIndex());
		
		return ret;
	}


	
	
	/**
	 * return the {@link Evaluator} of the corresponding condition with the precedence list equal to the given index ID;
	 * note that for {@link PiecewiseFunction}, index ID of {@link Evaluator} of each condition is equal to the precedence index of the condition;
	 * 
	 */
	@Override
	public Evaluator getEvaluator(int indexID) {
		if(indexID==DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX) {
			throw new IllegalArgumentException("given index ID cannot be equal to DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX!");
		}
		return this.getConditionPrecedenceIndexConditionalEvaluatorMap().get(indexID).getEvaluator();
	}
	
	@Override
	public Set<InputVariable> getInputVariableSetOfThisAndAllDownstreamComponentFunctions() {
		Set<InputVariable> ret = new LinkedHashSet<>();
		
		this.getConditionPrecedenceIndexConditionalEvaluatorMap().forEach((pi, eva)->{
			ret.addAll(eva.getEvaluator().getInputVariableAliasNameMap().values());
			
			ret.addAll(this.getConditionPrecedenceIndexNextFunctionMap().get(pi).getInputVariableSetOfThisAndAllDownstreamComponentFunctions());
			
		});
		
		ret.addAll(this.getDefaultNextFunction().getInputVariableSetOfThisAndAllDownstreamComponentFunctions());
		
		return ret;
	}

	
	@Override
	public Set<OutputVariable> getOutputVariableSetOfThisAndAllDownstreamComponentFunctions() {
		Set<OutputVariable> ret = new LinkedHashSet<>();
		
		this.getConditionPrecedenceIndexConditionalEvaluatorMap().forEach((pi, eva)->{
			ret.addAll(eva.getEvaluator().getOutputVariableAliasNameMap().values());
			
			ret.addAll(this.getConditionPrecedenceIndexNextFunctionMap().get(pi).getOutputVariableSetOfThisAndAllDownstreamComponentFunctions());
			
		});
		
		ret.addAll(this.getDefaultNextFunction().getOutputVariableSetOfThisAndAllDownstreamComponentFunctions());
		
		return ret;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildUpstreamPiecewiseFunctionIndexIDOutputIndexMap(
			Map<Integer,Integer> previousComponentFunctionBuiltMap, 
			Integer previousPiecewiseFunctionIndexID, 
			Integer previousPiecewiseFunctionOutputIndex
			) {
		
		this.upstreamPiecewiseFunctionIndexIDOutputIndexMap = new HashMap<>();
		
		this.upstreamPiecewiseFunctionIndexIDOutputIndexMap.putAll(previousComponentFunctionBuiltMap);
		
		if(previousPiecewiseFunctionOutputIndex!=null) {
			this.upstreamPiecewiseFunctionIndexIDOutputIndexMap.put(previousPiecewiseFunctionIndexID, previousPiecewiseFunctionOutputIndex);
		}
		
		
		//invoke conditional next functions' same method
		for(int i:this.getConditionPrecedenceIndexNextFunctionMap().keySet()) {
			this.getConditionPrecedenceIndexNextFunctionMap().get(i).buildUpstreamPiecewiseFunctionIndexIDOutputIndexMap(
					this.upstreamPiecewiseFunctionIndexIDOutputIndexMap, 
					this.getIndexID(), 
					i);
		}
		
		
		//invoke  default next function's same method
		//output index is 0;
		this.getDefaultNextFunction().buildUpstreamPiecewiseFunctionIndexIDOutputIndexMap(
				this.upstreamPiecewiseFunctionIndexIDOutputIndexMap, 
				this.getIndexID(), 
				DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX);
	}

	
	/**
	 * create a {@link PiecewiseFunctionCalculator} and perform it;
	 * invoke the {@link ComponentFunction#calculate(CFTargetValueTableRunCalculator)} method of next ComponentFunction of each condition;
	 * invoke the {@link ComponentFunction#calculate(CFTargetValueTableRunCalculator)} method of the next function of the PiecewiseFunction;
	 * @throws SQLException 
	 */
	@Override
	public void calculate(CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator) throws SQLException {
		PiecewiseFunctionCalculator calculator = new PiecewiseFunctionCalculator(CFTargetValueTableRunCalculator, this);
		
		//TODO
		calculator.calculate();
		
		
		
		//conditional next function
		for(int precedenceIndex:this.getConditionPrecedenceIndexNextFunctionMap().keySet()) {
			this.getConditionPrecedenceIndexNextFunctionMap().get(precedenceIndex).calculate(CFTargetValueTableRunCalculator);
		}
		
		//default next function
		this.getDefaultNextFunction().calculate(CFTargetValueTableRunCalculator);
		
		
	}


	
	/////////////////////////////////////////////////////
	/**
	 * reproduce and return a new PiecewiseFunction of this one;
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public PiecewiseFunction reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		CompositionFunctionID reproducedHostCompositionFunctionID = 
				this.getHostCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		VfNotes reproducedNotes = this.getNotes().reproduce();
		int reproducedID = this.getIndexID();
		
		
		ComponentFunction reproducedDefaultNextFunction = 
				this.getDefaultNextFunction().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		/////////
		List<Pair<ConditionalEvaluatorDelegator,ComponentFunction>> orderedListOfConditionEvaluatorNextComponentFunctionPairByConditionPrecedenceIndex = new ArrayList<>();
		
		for(int i=1;i<=this.getConditionPrecedenceIndexNextFunctionMap().size();i++) {
			Pair<ConditionalEvaluatorDelegator,ComponentFunction> pair= 
					new Pair<>(
							this.getConditionPrecedenceIndexConditionalEvaluatorMap().get(i).reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex), 
							this.getConditionPrecedenceIndexNextFunctionMap().get(i).reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex));
			orderedListOfConditionEvaluatorNextComponentFunctionPairByConditionPrecedenceIndex.add(pair);
		}
		
		
		////////////////////
		return new PiecewiseFunction(
				reproducedHostCompositionFunctionID,
				reproducedID,
				reproducedNotes,
				
				reproducedDefaultNextFunction,
				orderedListOfConditionEvaluatorNextComponentFunctionPairByConditionPrecedenceIndex
				);
	}
	
	
	
	/////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((conditionPrecedenceIndexConditionalEvaluatorMap == null) ? 0
				: conditionPrecedenceIndexConditionalEvaluatorMap.hashCode());
		result = prime * result + ((conditionPrecedenceIndexNextFunctionMap == null) ? 0
				: conditionPrecedenceIndexNextFunctionMap.hashCode());
		result = prime * result + ((defaultNextFunction == null) ? 0 : defaultNextFunction.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof PiecewiseFunction))
			return false;
		PiecewiseFunction other = (PiecewiseFunction) obj;
		if (conditionPrecedenceIndexConditionalEvaluatorMap == null) {
			if (other.conditionPrecedenceIndexConditionalEvaluatorMap != null)
				return false;
		} else if (!conditionPrecedenceIndexConditionalEvaluatorMap
				.equals(other.conditionPrecedenceIndexConditionalEvaluatorMap))
			return false;
		if (conditionPrecedenceIndexNextFunctionMap == null) {
			if (other.conditionPrecedenceIndexNextFunctionMap != null)
				return false;
		} else if (!conditionPrecedenceIndexNextFunctionMap.equals(other.conditionPrecedenceIndexNextFunctionMap))
			return false;
		if (defaultNextFunction == null) {
			if (other.defaultNextFunction != null)
				return false;
		} else if (!defaultNextFunction.equals(other.defaultNextFunction))
			return false;
		return true;
	}




	////////////////////////////////////////////////
	/**
	 * delegate class to an {@link Evaluator} that ensures it containing a single output variable of type {@link PFConditionEvaluatorBooleanOutputVariable};
	 * 
	 * NOTE that when using a {@link SQLQueryBasedEvaluator} type evaluator for a {@link ConditionalEvaluatorDelegator}, be very cautious since SqlQueryBasedEvaluator
	 * can generate multiple rows for a unique RUID column value of the owner data table, and a random row is selected as the final result, which is very harmful for the integrity of PiecewiseFunction;
	 * 
	 * @author tanxu
	 * 
	 */
	public static class ConditionalEvaluatorDelegator implements Reproducible{
		/**
		 * 
		 */
		private static final long serialVersionUID = -5021460957673402260L;
		
		////////////////////////////////////
		/**
		 * delegated evaluator
		 */
		private final Evaluator evaluator;
		///////
		private transient PFConditionEvaluatorBooleanOutputVariable singlePFConditionEvaluatorBooleanOutputVariable;
		
		/**
		 * constructor;
		 * 
		 * validations
		 * @param evaluator
		 */
		public ConditionalEvaluatorDelegator(Evaluator evaluator){
			if(evaluator.getOutputVariableAliasNameMap().size()!=1) {
				throw new IllegalArgumentException("given evaluator must contain a single output variable!");
			}
			if(!(evaluator.getOutputVariableAliasNameMap().values().iterator().next() instanceof PFConditionEvaluatorBooleanOutputVariable)) {
				throw new IllegalArgumentException("given evaluator's single output variable must be of type PFConditionEvaluatorBooleanOutputVariable!");
			}
			
			this.evaluator = evaluator;
		}
		
		
		public Evaluator getEvaluator() {
			return evaluator;
		}
		
		/**
		 * return the single PFConditionEvaluatorBooleanOutputVariable;
		 * @return
		 */
		public PFConditionEvaluatorBooleanOutputVariable getOutputVariable() {
			if(this.singlePFConditionEvaluatorBooleanOutputVariable == null) {
				this.singlePFConditionEvaluatorBooleanOutputVariable = (PFConditionEvaluatorBooleanOutputVariable)this.getEvaluator().getOutputVariableAliasNameMap().values().iterator().next();
			}
			return this.singlePFConditionEvaluatorBooleanOutputVariable;
		}
		////////////////////////
		/**
		 * reproduce and return a new ConditionalEvaluatorDelegator of this one
		 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
		 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
		 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
		 * @return
		 * @throws SQLException
		 */
		@Override
		public ConditionalEvaluatorDelegator reproduce(
				VisProjectDBContext hostVisProjctDBContext,
				VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
				int copyIndex) throws SQLException {
			return new ConditionalEvaluatorDelegator(this.getEvaluator().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex));
		}


		//////////////////////////////////////////
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((evaluator == null) ? 0 : evaluator.hashCode());
			return result;
		}


		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof ConditionalEvaluatorDelegator))
				return false;
			ConditionalEvaluatorDelegator other = (ConditionalEvaluatorDelegator) obj;
			if (evaluator == null) {
				if (other.evaluator != null)
					return false;
			} else if (!evaluator.equals(other.evaluator))
				return false;
			return true;
		}
		

	}


}
