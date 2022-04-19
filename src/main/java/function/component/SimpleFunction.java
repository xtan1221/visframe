package function.component;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.evaluator.Evaluator;
import function.variable.input.InputVariable;
import function.variable.output.OutputVariable;
import function.variable.output.type.CFGTargetOutputVariable;
import visinstance.run.calculation.function.component.SimpleFunctionCalculator;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;


/**
 * constraints;
 * 
 * @author tanxu
 * 
 */
public class SimpleFunction extends AbstractComponentFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6075058258683702435L;
	
	//////////////////////////
	private final ComponentFunction next;
	private final Map<Integer,Evaluator> evaluatorIndexIDMap;
	
	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param notes
	 * @param indexID
	 * @param next can be null;
	 * @param evaluatorIndexIDMap cannot be null or empty; map values cannot be null;
	 */
	public SimpleFunction(
			CompositionFunctionID hostCompositionFunctionID,
			int indexID,
			VfNotes notes,
			
			ComponentFunction next,
			Map<Integer,Evaluator> evaluatorIndexIDMap
			) {
		super(hostCompositionFunctionID, indexID, notes);
		//validations
		if(evaluatorIndexIDMap==null||evaluatorIndexIDMap.isEmpty()) {
			throw new IllegalArgumentException("given evaluatorIndexIDMap cannot be null or empty! index ID = "+indexID);
		}
		
		for(Evaluator e:evaluatorIndexIDMap.values()) {
			if(e==null) {
				throw new IllegalArgumentException("map value Evaluator of given evaluatorIndexIDMap cannot be null! index ID = "+indexID);
			}
		}
		
		//same {@link CFGTarget} cannot be assigned to {@link CFGTargetOutputVariable}s of multiple {@link Evaluator}s
		Set<SimpleName> CFGTargetNameSet = new HashSet<>();
		for(int i:evaluatorIndexIDMap.keySet()) {
			for(OutputVariable ov:evaluatorIndexIDMap.get(i).getOutputVariableAliasNameMap().values()) {
				if(ov instanceof CFGTargetOutputVariable) {
					CFGTargetOutputVariable targetOV = (CFGTargetOutputVariable)ov;
					if(CFGTargetNameSet.contains(targetOV.getTargetName())){
						throw new IllegalArgumentException(
								"same CFGTarget is assigned to multiple CFGTargetOutputVariables of evaluators of the same SimpleFunction; target name = "+targetOV.getTargetName().getStringValue()+" index ID = "+indexID);
					}
					CFGTargetNameSet.add(targetOV.getTargetName());
				}
			}
		}
		
		this.next = next;
		this.evaluatorIndexIDMap = evaluatorIndexIDMap;
	}
	
	/**
	 * return the next {@link ComponentFunction} of this one;
	 * can be null if this {@link SimpleFunction} is a leaf node of the tree
	 * @return
	 */
	public ComponentFunction getNext() {
		return this.next;
	}
	
	/**
	 * @return
	 */
	public Map<Integer,Evaluator> getEvaluatorIndexIDMap() {
		return Collections.unmodifiableMap(evaluatorIndexIDMap);
	}

	

	/////////////////////////////

	@Override
	public Set<ComponentFunction> getIncidentDownstreamComponentFunctionSet() {
		Set<ComponentFunction> ret = new LinkedHashSet<>();
		if(this.getNext()!=null) {
			ret.add(this.getNext());
		}
		return ret;
	}

	@Override
	public Set<ComponentFunction> getAllDownstreamComponentFunctionSet() {
		Set<ComponentFunction> ret = new LinkedHashSet<>();
		if(this.getNext()!=null) {
			ret.add(this.getNext());
			ret.addAll(this.getNext().getAllDownstreamComponentFunctionSet());
		}
		return ret;
	}

	@Override
	public Set<Evaluator> getEvaluatorSet() {
		Set<Evaluator> ret= new LinkedHashSet<>();
		ret.addAll(this.getEvaluatorIndexIDMap().values());
		return ret;
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Evaluator getEvaluator(int indexID) {
		return this.getEvaluatorIndexIDMap().get(indexID);
	}
	
	
	
	@Override
	public Set<InputVariable> getInputVariableSetOfThisAndAllDownstreamComponentFunctions() {
		Set<InputVariable> ret = new LinkedHashSet<>();
		
		this.getEvaluatorSet().forEach(e->{
			ret.addAll(e.getInputVariableAliasNameMap().values());
		});
		if(this.getNext()!=null)
			ret.addAll(this.getNext().getInputVariableSetOfThisAndAllDownstreamComponentFunctions());
		
		return ret;
	}
	
	
	@Override
	public Set<OutputVariable> getOutputVariableSetOfThisAndAllDownstreamComponentFunctions() {
		Set<OutputVariable> ret = new LinkedHashSet<>();
		ret.addAll(this.getOutputVariableSet());
//		
//		this.getEvaluatorSet().forEach(e->{
//			ret.addAll(e.getOutputVariableAliasNameMap().values());
//		});
		
		if(this.getNext()!=null)
			ret.addAll(this.getNext().getOutputVariableSetOfThisAndAllDownstreamComponentFunctions());
		
		return ret;
	}

	/**
	 * return the OutputVariable set of this SimpleFunction
	 * @return
	 */
	public Set<OutputVariable> getOutputVariableSet() {
		Set<OutputVariable> ret = new LinkedHashSet<>();
		
		this.getEvaluatorSet().forEach(e->{
			ret.addAll(e.getOutputVariableAliasNameMap().values());
		});
		
		return ret;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildUpstreamPiecewiseFunctionIndexIDOutputIndexMap(
			Map<Integer,Integer> previousComponentFunctionBuiltMap, Integer previousPiecewiseFunctionIndexID, Integer previousPiecewiseFunctionOutputIndex
			) {
		this.upstreamPiecewiseFunctionIndexIDOutputIndexMap = new HashMap<>();
		
		this.upstreamPiecewiseFunctionIndexIDOutputIndexMap.putAll(previousComponentFunctionBuiltMap);
		
		if(previousPiecewiseFunctionOutputIndex!=null) {
			this.upstreamPiecewiseFunctionIndexIDOutputIndexMap.put(previousPiecewiseFunctionIndexID, previousPiecewiseFunctionOutputIndex);
		}
		
		if(this.getNext()!=null) {
			this.getNext().buildUpstreamPiecewiseFunctionIndexIDOutputIndexMap(
					this.upstreamPiecewiseFunctionIndexIDOutputIndexMap, null, null);
		}
		
	}


	
	
	/**
	 * create a {@link SimpleFunctionCalculator} and perform it;
	 * 
	 * invoke the next ComponentFunction’s {@link ComponentFunction#calculate(CFTargetValueTableRunCalculator)} method
	 * @throws SQLException 
	 */
	@Override
	public void calculate(CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator) throws SQLException {
		SimpleFunctionCalculator calculator = new SimpleFunctionCalculator(CFTargetValueTableRunCalculator, this);
		
		calculator.calculate();
		
		//next function
		if(this.getNext()!=null) {
			this.getNext().calculate(CFTargetValueTableRunCalculator);
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * reproduce and return a new SimpleFunction of this one;
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public SimpleFunction reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
		CompositionFunctionID reproducedHostCompositionFunctionID = 
				this.getHostCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		VfNotes reproducedNotes = this.getNotes().reproduce();
		int reproducedID = this.getIndexID();
		
		ComponentFunction reproducedNextFunction = 
				this.getNext()==null?null:this.getNext().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		Map<Integer,Evaluator> reproducedEvaluatorIndexIDMap = new HashMap<>();
		
		for(Integer index:this.getEvaluatorIndexIDMap().keySet()) {
			reproducedEvaluatorIndexIDMap.put(index, this.getEvaluatorIndexIDMap().get(index).reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex));
		}
		
		return new SimpleFunction(
				reproducedHostCompositionFunctionID,
				reproducedID,
				reproducedNotes,
				
				reproducedNextFunction,
				reproducedEvaluatorIndexIDMap
				);
	}

	
	
	//////////////////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((evaluatorIndexIDMap == null) ? 0 : evaluatorIndexIDMap.hashCode());
		result = prime * result + ((next == null) ? 0 : next.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof SimpleFunction))
			return false;
		SimpleFunction other = (SimpleFunction) obj;
		if (evaluatorIndexIDMap == null) {
			if (other.evaluatorIndexIDMap != null)
				return false;
		} else if (!evaluatorIndexIDMap.equals(other.evaluatorIndexIDMap))
			return false;
		if (next == null) {
			if (other.next != null)
				return false;
		} else if (!next.equals(other.next))
			return false;
		return true;
	}


}
