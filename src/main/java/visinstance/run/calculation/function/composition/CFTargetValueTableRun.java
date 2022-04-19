package visinstance.run.calculation.function.composition;

import java.util.Map;

import basic.SimpleName;
import basic.lookup.VisframeUDT;
import basic.process.NonProcessType;
import function.composition.CompositionFunctionID;
import rdb.table.value.type.CFTargetValueTableSchema;
import rdb.table.value.type.CFTargetValueTableSchemaID;
import visinstance.run.calculation.IndependentFIVTypeIDStringValueMap;

/**
 * interface for a CFTargetValueTableRun that contains the full set of information about how this CFTargetValueTableRun is created and stored in the host VisProjectDBContext
 * 		specifically contains the information of a calculated CompositionFunction with a specific IndependetFIVTypeIDStringValueMap (if any IndependentFreeInputVariableType on the induced CFD graph);
 * 		facilitate to lookup whether a CompositionFunction has already been calculated or not thus to avoid repetitive calculation and storage;
 * 
 * always inserted by {@link VisInstanceRun} type process;
 * 		NonProcessType since it cannot be directly initialized by end user, rather it is always be initialized by a VisInstanceRun type process;
 * 		similar as Metadata which is also NonProcessType and NOT ProcessType and always inserted by either {@link DataImporter} or {@link Operation}
 * 
 * 
 * @author tanxu
 *
 */
public interface CFTargetValueTableRun extends VisframeUDT, NonProcessType{//?
	/**
	 * return the CompositionFunctionID of the target CompositionFunction of this CFTargetValueTableRun
	 * @return
	 */
	CompositionFunctionID getTargetCompositionFunctionID();
	
	
	/**
	 * return a unique integer for this CFTargetValueTableRun among all CFTargetValueTableRuns in the host VisProjectDBContext's rdb;
	 * @return
	 */
	int getRunUID();
	
	
	@Override
	default CFTargetValueTableRunID getID() {
		return new CFTargetValueTableRunID(this.getRunUID());
	}
	
	
	/**
	 * return the {@link IndependentFIVTypeIDStringValueMap} containing assigned string values for all {@link IndependentFreeInputVariableType}s present on the CFD graph induced by the target {@link CompositionFunciton} of this {@link CFTargetValueTableRun};
	 * @return
	 */
	IndependentFIVTypeIDStringValueMap getCFDGraphIndependetFIVStringValueMap();
	
	
	/**
	 * return the {@link CFTargetValueTableSchemaID} of the value table of this CFTargetValueTableRun
	 * @return
	 */
	CFTargetValueTableSchemaID getTableSchemaID();
	
	/**
	 * 
	 * @return
	 */
	CFTargetValueTableSchema getValueTableSchema();
	
	
	/**
	 * return the map from the assigned CFGTarget to the column name string in the value table schema;
	 * @return
	 */
	Map<SimpleName, SimpleName> getTargetNameColumnNameMap();
}
