package function.evaluator.nonsqlbased;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.evaluator.CanBeUsedForPiecewiseFunctionConditionEvaluatorType;
import function.variable.input.InputVariable;
import function.variable.output.OutputVariable;
import symja.VfSymjaVariableName;
import symja.VfSymjaSinglePrimitiveOutputExpression;

/**
 * 
 * 
 * ===========================
 * how to deal with null value of input variable for some record:
 * 		if any of the input variable has null value for some record, the output variable will be evaluated to null for such records;
 * 
 * @author tanxu
 * 
 */
public class SymjaExpressionEvaluator extends NonSQLQueryBasedEvaluator implements CanBeUsedForPiecewiseFunctionConditionEvaluatorType{
	/**
	 * 
	 */
	private static final long serialVersionUID = -53627984658301890L;
	
	/////////////////////////////
	/**
	 * 
	 */
	private final VfSymjaSinglePrimitiveOutputExpression symjaExpression;
	
	/**
	 * BiMap from the {@link InputVariable} to the corresponding {@link VfSymjaVariableName} in {@link #symjaExpression};
	 * note that both map keys and values should be unique;
	 * 
	 * can be empty if the {@link #symjaExpression} is a constant value;
	 * 
	 * each {@link VfSymjaVariableName} in the {@link #symjaExpression} must be present in this map;
	 * also the data type must be consistent;
	 */
	private final BiMap<VfSymjaVariableName, InputVariable> vfSymjaVariableNameInputVariableMap;
	
	
	/**
	 * output variable of type consistent with the {@link #symjaExpression};
	 */
	private final OutputVariable outputVariable;
	
	
	/**
	 * 
	 * @param hostCompositionFunctionID
	 * @param hostComponentFunctionIndexID
	 * @param indexID
	 * @param notes
	 * @param symjaExpression not null;
	 * @param inputVariableVfSymjaVariableNameMap not null; can be empty if the value of the symja expression is a constant? if not empty, map key and values cannot be null;
	 * @param outputVariable not null;
	 */
	public SymjaExpressionEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID, 
			int indexID,
			VfNotes notes,
			
			VfSymjaSinglePrimitiveOutputExpression symjaExpression,
			BiMap<VfSymjaVariableName, InputVariable> vfSymjaVariableNameInputVariableMap,
			OutputVariable outputVariable
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes);
		//validations
		if(symjaExpression==null)
			throw new IllegalArgumentException("given symjaExpression cannot be null!");
		if(vfSymjaVariableNameInputVariableMap==null)
			throw new IllegalArgumentException("given VfSymjaVariableNameInputVariableMap cannot be null!");
		if(outputVariable==null)
			throw new IllegalArgumentException("given outputVariable cannot be null!");
		
		
		
		//
		vfSymjaVariableNameInputVariableMap.forEach((k,v)->{
			if(k==null||v==null)
				throw new IllegalArgumentException("null map value and/or key is found in given VfSymjaVariableNameInputVariableMap");
		});
		
		for(VfSymjaVariableName vn:vfSymjaVariableNameInputVariableMap.keySet())
			if(!symjaExpression.getVariableNameSQLDataTypeMap().get(vn).equals((vfSymjaVariableNameInputVariableMap.get(vn).getSQLDataType())))
				throw new IllegalArgumentException("inconsistent data type found for the same VfSymjaVariableName in given symjaExpression and VfSymjaVariableNameInputVariableMap!");
		
		
		////input variables must have different alias names
		Set<SimpleName> aliasNameSet = new HashSet<>();
		for(InputVariable iv:vfSymjaVariableNameInputVariableMap.values()) {
			if(aliasNameSet.contains(iv.getAliasName()))
				throw new IllegalArgumentException("multiple input variables in given VfSymjaVariableNameInputVariableMap are found to have same alias name:"+iv.getAliasName().getStringValue());
		}
		
		
		this.symjaExpression = symjaExpression;
		this.vfSymjaVariableNameInputVariableMap = vfSymjaVariableNameInputVariableMap;
		this.outputVariable = outputVariable;
	}
	
	/////////////////////////////////////
	
	/**
	 * @return the symjaExpression
	 */
	public VfSymjaSinglePrimitiveOutputExpression getSymjaExpression() {
		return symjaExpression;
	}


	public BiMap<VfSymjaVariableName, InputVariable> getVfSymjaVariableNameInputVariableMap() {
		return vfSymjaVariableNameInputVariableMap;
	}

	/**
	 * @return the outputVariable
	 */
	public OutputVariable getOutputVariable() {
		return outputVariable;
	}
	////////////////////////////
	
	@Override
	public Map<SimpleName, InputVariable> getInputVariableAliasNameMap() {
//		if(this.inputVariableAliasNameMap==null) {
		Map<SimpleName, InputVariable> ret = new HashMap<>();
			
		this.getVfSymjaVariableNameInputVariableMap().values().forEach(e->{
			ret.put(e.getAliasName(), e);
		});
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
	
	
	//////////////////////////
	@Override
	public Map<OutputVariable, String> evaluate(Map<SimpleName, String> inputVariableAliasNameStringValueMap){
		Map<OutputVariable, String> ret = new HashMap<>();
		
		/////////
		Map<VfSymjaVariableName,String> syjaExpressionVariableNameStringValueMap = new HashMap<>();
		
		for(VfSymjaVariableName vn:this.getVfSymjaVariableNameInputVariableMap().keySet()) {
			if(inputVariableAliasNameStringValueMap.get(this.getVfSymjaVariableNameInputVariableMap().get(vn).getAliasName())==null) {//output variable value set to null if one input variable value is null;
				ret.put(this.outputVariable, null);
				return ret;
			}
			//
			syjaExpressionVariableNameStringValueMap.put(vn, inputVariableAliasNameStringValueMap.get(this.getVfSymjaVariableNameInputVariableMap().get(vn).getAliasName()));
		}
		
		///////////
		ret.put(this.outputVariable, this.symjaExpression.evaluate(syjaExpressionVariableNameStringValueMap));
		
		
		return ret;
	}
	
	
	////////////////////////////////
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public SymjaExpressionEvaluator reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
		CompositionFunctionID reproducedHostCompositionFunctionID = 
				this.getHostCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		int reproducedHostComponentFunctionIndexID = this.getHostComponentFunctionIndexID();
		int reproducedIndexID = this.getIndexID();
		VfNotes reproducedNotes = this.getNotes().reproduce();		
		//
		BiMap<VfSymjaVariableName, InputVariable> vfSymjaVariableNameInputVariableMap = HashBiMap.create();
		
		for(VfSymjaVariableName vn:this.getVfSymjaVariableNameInputVariableMap().keySet()) {
			vfSymjaVariableNameInputVariableMap.put(
					vn.reproduce(),
					this.getVfSymjaVariableNameInputVariableMap().get(vn).reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex)
					);
		}
		
		//
		OutputVariable outputVariable = this.getOutputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		SymjaExpressionEvaluator reproduced = new SymjaExpressionEvaluator(
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedIndexID,
				reproducedNotes,
				symjaExpression,
				vfSymjaVariableNameInputVariableMap,
				outputVariable
				);
		
		return reproduced;
	}

	
	//////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((outputVariable == null) ? 0 : outputVariable.hashCode());
		result = prime * result + ((symjaExpression == null) ? 0 : symjaExpression.hashCode());
		result = prime * result
				+ ((vfSymjaVariableNameInputVariableMap == null) ? 0 : vfSymjaVariableNameInputVariableMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof SymjaExpressionEvaluator))
			return false;
		SymjaExpressionEvaluator other = (SymjaExpressionEvaluator) obj;
		if (outputVariable == null) {
			if (other.outputVariable != null)
				return false;
		} else if (!outputVariable.equals(other.outputVariable))
			return false;
		if (symjaExpression == null) {
			if (other.symjaExpression != null)
				return false;
		} else if (!symjaExpression.equals(other.symjaExpression))
			return false;
		if (vfSymjaVariableNameInputVariableMap == null) {
			if (other.vfSymjaVariableNameInputVariableMap != null)
				return false;
		} else if (!vfSymjaVariableNameInputVariableMap.equals(other.vfSymjaVariableNameInputVariableMap))
			return false;
		return true;
	}
	
	
	

}
