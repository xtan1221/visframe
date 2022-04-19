package visinstance.run.calculation.function.cfg;

import java.util.Set;

import basic.lookup.VisframeUDT;
import basic.process.NonProcessType;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;
import visinstance.run.calculation.IndependentFIVTypeIDStringValueMap;

/**
 * similar with {@link CFTargetValueTableRun}
 * 
 * stores the lookup information for a core ShapeCFG with 
 * 		1. a specific set of children CompositionFunctions (which must cover all the mandatory targets of the core ShapeCFG);
 * 			note that CompositionFunction assigned with only non-mandatory targets can be created;
 * 
 * 		2. a specific IndependetFIVTypeIDStringValueMap of all IndependentFreeInputVariableTypes present on the CFD graph induced by the set of CompositionFunction;
 * 
 *  
 * @author tanxu
 *
 */
public interface CalculatedCoreShapeCFG extends VisframeUDT, NonProcessType{
	CompositionFunctionGroupID getCoreShapeCFGID();
	
	Set<CompositionFunctionID> getIncludedCompositionFunctionIDSet();
	
	IndependentFIVTypeIDStringValueMap getCFDGraphIndependetFIVStringValueMap();
}
