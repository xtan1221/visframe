package context;

import java.util.Set;

import basic.HasName;
import basic.HasNotes;
import basic.SimpleName;
import basic.lookup.Lookup;
import context.project.VisProjectDBContext;
import context.scheme.VisScheme;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroup;
import function.group.CompositionFunctionGroupID;
import function.variable.independent.IndependentFreeInputVariableType;
import function.variable.independent.IndependentFreeInputVariableTypeID;
import metadata.Metadata;
import metadata.MetadataID;
import operation.Operation;
import operation.OperationID;
import rdb.table.data.DataTableSchemaID;

/**
 * features that are needed by both {@link VisScheme} and {@link VisProjectDBContext}
 * 
 * @author tanxu
 * 
 */
public interface VisframeContext extends HasName, HasNotes{
	/**
	 * lookup a Metadata with a MetadataID
	 * @return
	 */
	Lookup<Metadata,MetadataID> getMetadataLookup();

	/**
	 * lookup an Operation with an OperationID
	 * @return
	 */
	Lookup<Operation, OperationID> getOperationLookup();
	
	/**
	 * lookup a CompositionFunctionGroup with a CompositionFunctionGropuID
	 * @return
	 */
	Lookup<CompositionFunctionGroup, CompositionFunctionGroupID> getCompositionFunctionGroupLookup();
	
	/**
	 * lookup a CompositionFunciton with a CompositionFunctionID
	 * @return
	 */
	Lookup<CompositionFunction, CompositionFunctionID> getCompositionFunctionLookup();
	
	/**
	 * 
	 * @return
	 */
	Lookup<IndependentFreeInputVariableType, IndependentFreeInputVariableTypeID> getIndependentFreeInputVariableTypeLookup();
	
	
	//////////////////////////////////////////////////////
	//facilitating retrieval methods based on the Lookups
	/**
	 * lookup CompositionFunctionID of the given CompositionFunctionGroupID to which the targetName is assigned;
	 * facilitate building CFD graph
	 * 
	 * @param cfgID
	 * @param targetName
	 * @return
	 */
	CompositionFunctionID getCompositionFuncitionID(CompositionFunctionGroupID cfgID, SimpleName targetName);
	
	
	/**
	 * lookup all CompositionFunctionID of the given CompositionFunctionGroupID;
	 * facilitate building CFD graph
	 * @param cfgID
	 * @return
	 */
	Set<CompositionFunctionID> getCompositionFunctionIDSetOfGroupID(CompositionFunctionGroupID cfgID);
	
	/**
	 * lookup the DataTableSchemaID of the given record data's MetadataID;
	 * 
	 * @param recordMetadataID
	 * @return
	 */
	DataTableSchemaID getDataTableSchemaID(MetadataID recordMetadataID);
	
	/**
	 * lookup and return the DataTableSchemaID of the owner RecordDataMetadata of the given CompositionFunctionGroupID
	 * @param cfID
	 * @return
	 */
	DataTableSchemaID getOwnerRecordDataTableSchemaID(CompositionFunctionGroupID cfgID);	
}
