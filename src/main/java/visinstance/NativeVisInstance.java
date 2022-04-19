package visinstance;

import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;

/**
 * type of VisInstance not directly based on any VisSchemeAppliedArchiveReproducedAndInsertedInstance;
 * 
 * note that 
 * 
 * 1. the core ShapeCFGs are directly selected from the set of ShapeCFGs that are legible for core ShapeCFGs
 * 
 * 2. for the set of CFs of each selected core ShapeCFGs
 * 		1. all CF with one or more mandatory targets are auto-included in the {@link #getCoreShapeCFGCFIDSet()};
 * 		2. CF without any mandatory target are optional and must be explicitly given in the {@link #getCoreShapeCFGCFIDSet()};
 * 
 * @author tanxu
 *
 */
public class NativeVisInstance extends AbstractVisInstance{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7319115867868810050L;
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 * @param UID
	 * @param coreShapeCFGIDSet
	 * @param coreShapeCFGIDCFIDSetMap
	 */
	public NativeVisInstance(
			SimpleName name, VfNotes notes, int UID,
			Set<CompositionFunctionGroupID> coreShapeCFGIDSet,
			Map<CompositionFunctionGroupID, Set<CompositionFunctionID>> coreShapeCFGIDCFIDSetMap) {
		super(name, notes, UID, coreShapeCFGIDSet, coreShapeCFGIDCFIDSetMap);
	}
	
}
