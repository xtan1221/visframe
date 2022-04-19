package basic.lookup.scheme.type;

import java.util.Map;

import basic.lookup.scheme.VisSchemeLookup;
import function.variable.independent.IndependentFreeInputVariableType;
import function.variable.independent.IndependentFreeInputVariableTypeID;

public class VisSchemeIndependentFreeInputVariableTypeLookup implements VisSchemeLookup<IndependentFreeInputVariableType, IndependentFreeInputVariableTypeID>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7543949778231961489L;
	
	/////////////////
	private final Map<IndependentFreeInputVariableTypeID, IndependentFreeInputVariableType> independentFreeInputVariableTypeIDMap;
	
	public VisSchemeIndependentFreeInputVariableTypeLookup(Map<IndependentFreeInputVariableTypeID, IndependentFreeInputVariableType> independentFreeInputVariableTypeIDMap) {
		this.independentFreeInputVariableTypeIDMap = independentFreeInputVariableTypeIDMap;
	}
	
	@Override
	public Map<IndependentFreeInputVariableTypeID, IndependentFreeInputVariableType> getMap() {
		return independentFreeInputVariableTypeIDMap;
	}

}
