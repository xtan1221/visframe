package basic.lookup.scheme.type;

import java.util.Map;

import basic.SimpleName;
import basic.lookup.scheme.VisSchemeLookup;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;

/**
 * 
 * @author tanxu
 *
 */
public class VisSchemeCompositionFunctionLookup implements VisSchemeLookup<CompositionFunction,CompositionFunctionID>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4672682455436473557L;
	
	
	////////////////////
	private final Map<CompositionFunctionID, CompositionFunction> compositionFunctionIDMap;
	/**
	 * lookup assigned CompositionFunctionID of a target
	 */
	private final Map<CompositionFunctionGroupID,Map<SimpleName, CompositionFunctionID>> compositionFunctionGroupIDTargetNameAssignedCFIDMapMap;
	
	/**
	 * constructor
	 * @param compositionFunctionIDMap
	 * @param compositionFunctionGroupIDTargetNameAssignedCFIDMapMap
	 */
	public VisSchemeCompositionFunctionLookup(
			Map<CompositionFunctionID, CompositionFunction> compositionFunctionIDMap,
			Map<CompositionFunctionGroupID,Map<SimpleName, CompositionFunctionID>> compositionFunctionGroupIDTargetNameAssignedCFIDMapMap
			){
		this.compositionFunctionIDMap = compositionFunctionIDMap;
		this.compositionFunctionGroupIDTargetNameAssignedCFIDMapMap = compositionFunctionGroupIDTargetNameAssignedCFIDMapMap;
	}

	@Override
	public Map<CompositionFunctionID, CompositionFunction> getMap() {
		return compositionFunctionIDMap;
	}
	
	public Map<CompositionFunctionGroupID,Map<SimpleName, CompositionFunctionID>> getCompositionFunctionGroupIDTargetNameAssignedCFIDMapMap() {
		return compositionFunctionGroupIDTargetNameAssignedCFIDMapMap;
	}
	
	
}
