package visinstance.run.calculation;

import java.io.Serializable;
import java.util.Map;

import function.variable.independent.IndependentFreeInputVariableTypeID;


/**
 * serializable class that delegates to a map from IndependentFreeInputVariableTypeID to string value;
 * 
 * @author tanxu
 *
 */
public class IndependentFIVTypeIDStringValueMap implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1710301521161458657L;
	
	/////////////////
	private final Map<IndependentFreeInputVariableTypeID,String> independentFreeInputVariableTypeIDAssignedStringValueMap;
	
	/**
	 * constructor
	 * @param independentFreeInputVariableTypeIDAssignedStringValueMap can not be null; can be empty; map values cannot be null;
	 */
	public IndependentFIVTypeIDStringValueMap(
			Map<IndependentFreeInputVariableTypeID,String> independentFreeInputVariableTypeIDAssignedStringValueMap
			){
		if(independentFreeInputVariableTypeIDAssignedStringValueMap==null)
			throw new IllegalArgumentException("given independetFreeInputVariableTypeIDAssignedStringValueMap cannot be null!");
		
		independentFreeInputVariableTypeIDAssignedStringValueMap.values().forEach(v->{
			if(v==null)
				throw new IllegalArgumentException("given independetFreeInputVariableTypeIDAssignedStringValueMap's values cannot be null!");
		});
		
		
		this.independentFreeInputVariableTypeIDAssignedStringValueMap = independentFreeInputVariableTypeIDAssignedStringValueMap;
	}


	public Map<IndependentFreeInputVariableTypeID,String> getIndependentFreeInputVariableTypeIDAssignedStringValueMap() {
		return independentFreeInputVariableTypeIDAssignedStringValueMap;
	}
	
	public String getAssignedStringValue(IndependentFreeInputVariableTypeID ifivt) {
		return this.getIndependentFreeInputVariableTypeIDAssignedStringValueMap().get(ifivt);
	}


	//////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((independentFreeInputVariableTypeIDAssignedStringValueMap == null) ? 0
				: independentFreeInputVariableTypeIDAssignedStringValueMap.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof IndependentFIVTypeIDStringValueMap))
			return false;
		IndependentFIVTypeIDStringValueMap other = (IndependentFIVTypeIDStringValueMap) obj;
		if (independentFreeInputVariableTypeIDAssignedStringValueMap == null) {
			if (other.independentFreeInputVariableTypeIDAssignedStringValueMap != null)
				return false;
		} else if (!independentFreeInputVariableTypeIDAssignedStringValueMap
				.equals(other.independentFreeInputVariableTypeIDAssignedStringValueMap))
			return false;
		return true;
	}
	
}
