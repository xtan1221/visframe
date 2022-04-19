package basic.lookup.scheme.type;

import java.util.Map;

import basic.lookup.scheme.VisSchemeLookup;
import function.group.CompositionFunctionGroup;
import function.group.CompositionFunctionGroupID;

/**
 * lookup for CompositionFunctionGroup and CompositionFunctionID to which a target of a CompositionFunctionGroup is assigned
 * @author tanxu
 *
 */
public class VisSchemeCompositionFunctionGroupLookup implements VisSchemeLookup<CompositionFunctionGroup,CompositionFunctionGroupID>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3923554768963770449L;
	
	/////
	private final Map<CompositionFunctionGroupID, CompositionFunctionGroup> compositionFunctionGroupIDMap;
	
	/**
	 * constructor
	 * @param compositionFunctionGroupIDMap
	 */
	public VisSchemeCompositionFunctionGroupLookup(
			Map<CompositionFunctionGroupID, CompositionFunctionGroup> compositionFunctionGroupIDMap
			){
		this.compositionFunctionGroupIDMap = compositionFunctionGroupIDMap;
	}
	
	@Override
	public Map<CompositionFunctionGroupID, CompositionFunctionGroup> getMap() {
		return compositionFunctionGroupIDMap;
	}
}
