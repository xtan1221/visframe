package visinstance;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import basic.HasName;
import basic.HasNotes;
import basic.SimpleName;
import basic.lookup.VisframeUDT;
import basic.process.NonReproduceableProcessType;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;


/**
 * 122220-update
 * 
 * in essence, building a VisInstance is equivalent to building a CFD graph; 
 * once the VisInstance is created, the corresponding CFD graph is also determined and should never change throughout the life span of the VisInstance;
 * 
 * this is realized by explicitly including the set of CompositionFunctions of each core ShapeCFGs thus even if more CompositionFunctions (whose assigned targets must all be non-mandatory) are created for these core ShapeCFGs, 
 * there will be no affect to the VisInstance; 
 * 
 * note that all core ShapeCFGs' mandatory {@link CFGTarget#}(cannot be null and non-null default value is not assigned) targets must all be assigned to one single {@link CompositionFunction} and the {@link CompositionFunction} must be included in the {@link #getCoreShapeCFGCFIDSet()}
 * 
 * @author tanxu
 *
 */
public interface VisInstance extends VisframeUDT, HasName, HasNotes, NonReproduceableProcessType{
	/**
	 * a unique integer value distinguish this VisInstance from all others in the same host VisProjectDBContext
	 * @return
	 */
	int getUID();
	
	@Override
	default VisInstanceID getID() {
		return new VisInstanceID(this.getUID());
	}
	
	/**
	 * note that this name is not in the primary key of the VisInstance; it is a descriptive information;
	 * thus it may be duplicate with some other VisInstance in the same host VisProjectDBContext;
	 */
	@Override
	SimpleName getName();
	
	
	/**
	 * return the set of CompositionFunctionGroupID for core ShapeCFGs of this VisInstance
	 * 
	 * note that all mandatory targets of each core ShapeCFG should be assigned to a CompositionFunction which must be included in the {@link #getCoreShapeCFGCFIDSet()};
	 * 
	 * not null or empty;
	 * @return
	 */
	Set<CompositionFunctionGroupID> getCoreShapeCFGIDSet();
	
	/**
	 * return the map from core ShapeCFG ID to set of CompositionFunctionIDs included for this VisInstance;
	 * 
	 * note that when a VisInstance is created, the involved CFs on the CFD graph will be the fixed set through the whole life span of the VisInstance;
	 * 
	 * 1. this is to avoid any unwanted effect resulted from any later added CompositionFunctions after the {@link VisInstance} was created;
	 * 
	 * 2. also this allows selection of a subset of CompositionFunctions of each CompositionFunctionGroup!
	 * 
	 * note that for core ShapeCFGs, their CompositionFunctions assigned one or more mandatory targets must all be included in this map;
	 * otherwise, the VisInstance is invalid;
	 * 
	 * 
	 * not null or empty; must be consistent with {@link #getCoreShapeCFGIDSet()};
	 * @return
	 */
	Map<CompositionFunctionGroupID, Set<CompositionFunctionID>> getCoreShapeCFGIDIncludedCFIDSetMap();
	
	/**
	 * return the full set of CFID for all core ShapeCFGs;
	 * 
	 * facilitate to induce the CFD graph based on which the calculation of this VisInstance is performed;
	 * 
	 * @return
	 */
	default Set<CompositionFunctionID> getCoreShapeCFGCFIDSet(){
		Set<CompositionFunctionID> ret = new HashSet<>();
		
		for(CompositionFunctionGroupID cfgID:this.getCoreShapeCFGIDIncludedCFIDSetMap().keySet()) {
			ret.addAll(this.getCoreShapeCFGIDIncludedCFIDSetMap().get(cfgID));
		}
		
		return ret;
	}
	
}
