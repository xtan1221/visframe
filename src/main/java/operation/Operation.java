package operation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import basic.HasName;
import basic.HasNotes;
import basic.SimpleName;
import basic.VfNotes;
import basic.lookup.VisframeUDT;
import basic.process.ReproduceableProcessType;
import basic.reproduce.Reproducible;
import context.project.VisProjectDBContext;
import context.project.process.logtable.StatusType;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunction;
import metadata.DataType;
import metadata.MetadataID;
import operation.parameter.Parameter;
import rdb.table.data.DataTableColumnName;


/**
 * base interface to perform processing of input Metadata(s) to generate output Metadata(s) and its data tables;
 * <p>
 * For an operation instance to be successfully created and inserted into the management table in rdb of host VisProjectDBContext, 
 * it need to get through the validations in the constructor and its perform() method needs to be successfully carried out;
 * </p>
 * <p>
 * regarding parameter value validation:
 *  </p><p>
 * 1. In constructor, parameter value objects are only validated with the basic constraints implemented in the corresponding Parameter<?> object and constraints directly based on the value object of the parameters
	For example, Bin_MIN and BIN_MAX values of GroupAndBinCountOperation, if both non-null, BIN_MIN < BIN_MAX
	Throw IllegalArgumentException() if invalid
	</p><p>
	2. All other validations regarding related feature/attribute of the value object should be either explicitly or implicitly done in the perform() method
	This will include both input data table content dependent and independent parameters;
	Also, this will include (input data table content independent)parameters of successfully performed operation instance whose reproduced copy may still result in error or exception
	Input graph type checked graph operation
	Throw OperationUnperformableExeption() if error occurs?????
</p>
 * @author tanxu
 * 
 */
public interface Operation extends HasName, HasNotes, VisframeUDT, Reproducible, ReproduceableProcessType, Callable<StatusType>{
	/**
	 * return the operation type; 
	 * should be implemented in each concrete subclass that represent a specific operation type;
	 * @return
	 */
	default SimpleName getOperationTypeName() {
		//!!not the operation instance name;
		return new SimpleName(this.getClass().getSimpleName());
	}
	
	
	/**
	 * return the operation type notes; 
	 * should be implemented in each concrete subclass that represent a specific operation type;
	 * @return
	 */
	VfNotes getOperationTypeNotes();//!!not the operation instance notes
	
	
	/////////////////////////////////////////
//	/**
//	 * whether or not this instance of Operation is resulted from reproducing of a previously defined one or not (created from scratch);
//	 * only relevant when this Operation is residing in a VisProjectDBContext (irrelevant when in a VisScheme);
//	 * 
//	 * facilitate to validate the parameter values given to constructor;
//	 * if the operation is reproduced, the parameters dependent on input data table content will have null value, thus the validation in constructor should skip those parameters;
//	 * @return
//	 */
//	boolean isReproduced();
	
	
	/**
	 * return the OperationID of this Operation instance
	 */
	@Override
	OperationID getID();
	
	/**
	 * return the set of parameters defined at current level and all super class levels;
	 * should be implemented at every level of classes in Operation hierarchy;
	 * @return
	 */
	Map<SimpleName, Parameter<?>> getAllParameterNameMapOfCurrentAndAboveLevels();
	
	/**
	 * return the set of parameters values defined at current level and all super class levels;
	 * should be implemented at every level of classes in Operation hierarchy;
	 * 
	 * 
	 * @return
	 */
	Map<SimpleName, Object> getAllParameterNameValueObjectMapOfCurrentAndAboveLevels();
	
	/**
	 * set the value of the parameter as the given object;
	 * 
	 * should be implemented at every level of classes in Operation hierarchy;
	 * 
	 * implementation strategy
	 * 		if the given parameter is level specific defined, invoke the {@link #setLevelSpecificParameterValueObject(SimpleName, Object)} method;
	 * 		else, invoke the super class's {@link #setParameterValueObject(SimpleName, Object)}
	 * 
	 * @param parameterName
	 * @param value
	 */
	void setParameterValueObject(SimpleName parameterName, Object value);
	
	/**
	 * set the value of the parameter defined at current class level;
	 * 
	 * should be implemented at every level of classes in Operation hierarchy;
	 * @param parameterName
	 * @param value
	 */
	void setLevelSpecificParameterValueObject(SimpleName parameterName, Object value);
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////related with VisScheme applying/////////////////////////////////////////
	//
	//===============input and output Metadata related
	/**
	 * return the set of input MetadataID; 
	 * 
	 * facilitate checking existence of input MetadataID before performing this operation;
	 * also facilitate building the DOS graph;
	 * 
	 * must be implemented in final operation subclass;
	 * @return
	 */
	Set<MetadataID> getInputMetadataIDSet();
	

	/**
	 * extract and return the map from input record MetadataID to the set of column names that are used as input of this Operation instance;
	 * 
	 * note that if the input Metadata of this Operation instance is of GRAPH or vfTREE type, need to extract the input columns of both node and edge record data and include them in the returned map;
	 * 
	 * ==========================================================
	 * 1. facilitate to extract and build depended Metadata from host VisProjectDBContext when building a VisScheme together with the {@link CompositionFunction#getDependedRecordMetadataIDInputColumnNameSetMap(context.VisframeContext)};
	 * 		
	 * 		specifically, 
	 * 		1. for record data Metadata, help to extract the set of columns for the data table schema to be put in the VisScheme
	 * 		2. for {@link DataType#GRAPH} type Metadata, help to extract the additional feature columns of node and edge data;
	 * 		3. for {@link DataType#vfTREE} type Metadata, help to extract the NON-MANDATORY feature columns of node and edge data;
	 * 
	 * 2. note that the extracted Metadata will facilitate to identify the needed columns when performing input data mapping in VisScheme applying???
	 * 
	 * 
	 * ===========================================================
	 * should be implemented by each final subclass
	 * @return
	 */
	Map<MetadataID,Set<DataTableColumnName>> getInputRecordMetadataIDInputColumnNameSetMap();
	
	/**
	 * returns the set of output MetadataID;
	 * 
	 * facilitate validation of the uniqueness of the output MetadataID of this operation before performing it;
	 * 
	 * for composite metadata resulted from the operation, only return the composite metadata's ID without the component MetadataID since they are automatically generated to enforce the uniqueness constraints;
	 * 
	 * @return
	 */
	Set<MetadataID> getOutputMetadataIDSet();
	
	
	////////////////////////////////////operation parameter related
	/**
	 * return whether any {@link Parameter}'s {@link Parameter#isInputDataTableContentDependent()} returns true;  
	 * @return
	 */
	default boolean hasInputDataTableContentDependentParameter(){
		for(Parameter<?> p:this.getAllParameterNameMapOfCurrentAndAboveLevels().values()) {
			if(p.isInputDataTableContentDependent()) {
				return true;
			}
		}
		
		return false;
	};
	
	/**
	 * return the {@link Parameter}s whose {@link Parameter#isInputDataTableContentDependent()} returns true;  
	 * if none, return empty set;
	 * @return
	 */
	default Map<SimpleName,Parameter<?>> getInputDataTableContentDependentParameterNameMap(){
		Map<SimpleName,Parameter<?>> ret = new HashMap<>();
		for(SimpleName parameterName:this.getAllParameterNameMapOfCurrentAndAboveLevels().keySet()) {
			if(this.getAllParameterNameMapOfCurrentAndAboveLevels().get(parameterName).isInputDataTableContentDependent()) {
				ret.put(parameterName, this.getAllParameterNameMapOfCurrentAndAboveLevels().get(parameterName));
			}
		}
		return ret;
	}
	
	/**
	 * return the map from the name of parameters dependent on input data table content to the value object;
	 * if there is no such parameters, returned map will be empty;
	 * @return
	 */
	default Map<SimpleName, Object> getInputDataTableContentDependentParameterNameValueObjectMap(){
		Map<SimpleName, Object> ret = new HashMap<>();
		
		this.getInputDataTableContentDependentParameterNameMap().keySet().forEach(n->{
			ret.put(n,this.getAllParameterNameValueObjectMapOfCurrentAndAboveLevels().get(n));
		});
		
		return ret;
	}
	
	/**
	 * set the value of the {@link Parameter}s whose {@link Parameter#isInputDataTableContentDependent()} returns true;  ;
	 * 
	 * throw {@link UnsupportedOperationException} if {@link #hasInputDataTableContentDependentParameter()} returns false;
	 * @param inputDataTableContentDependentParameterNameValueObjectMap
	 */
	default void setInputDataTableContentDependentParameterValue(Map<SimpleName,Object> inputDataTableContentDependentParameterNameValueObjectMap) {
		//check if any InputDataTableContentDependentParameter
		if(!this.hasInputDataTableContentDependentParameter()) {
			throw new UnsupportedOperationException("operation type does not have any parameter dependent on input data table content");
		}
		
		//check if the given value map covers the full set of InputDataTableContentDependentParameters and the given object value is valid 
		for(SimpleName parameterName:this.getInputDataTableContentDependentParameterNameMap().keySet()) {
			if(!inputDataTableContentDependentParameterNameValueObjectMap.containsKey(parameterName)) {
				throw new IllegalArgumentException("input data table content dependent parameter is not found in the given inputDataTableContentDependentParameterNameValueObjectMap:"+parameterName.getStringValue());
			}
			if(!this.getInputDataTableContentDependentParameterNameMap().get(parameterName).validateObjectValue(inputDataTableContentDependentParameterNameValueObjectMap.get(parameterName), true)) {
				throw new IllegalArgumentException("given object value for the parameter in inputDataTableContentDependentParameterNameValueObjectMap is not of valid type:"+parameterName);
			}
			
			//set the value object
			this.setParameterValueObject(parameterName, inputDataTableContentDependentParameterNameValueObjectMap.get(parameterName));
//			this.getAllParameterNameValueObjectMapOfCurrentAndAboveLevels().put(parameterName, inputDataTableContentDependentParameterNameValueObjectMap.get(parameterName));
		}
	}
	
	
	/////////////////////////////////////////////
	/**
	 * set the host {@link VisProjectDBContext} for this operation before it is run or to be reproduced;
	 * the host VisProjectDBContext is stored as a transient field and will not be serialized;
	 * @param hostVisProject
	 */
	void setHostVisProjectDBContext(VisProjectDBContext hostVisProjectDBContext);
	
	/**
	 * return the host {@link VisProjectDBContext} of this Operation for it to be run;
	 * @return
	 */
	VisProjectDBContext getHostVisProjectDBContext();
	
	/**
	 * reproduce this Operation and return the reproduced one;
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @return
	 * @throws SQLException
	 */
	Operation reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;
	

	/**
	 * if {@link #getHostVisProjectDBContext()} returns null, throw VisframeException;
	 * 
	 * perform the operation;
	 * 
	 * @throws SQLException 
	 * @throws IOException 
	 * 
	 */
	@Override
	StatusType call() throws SQLException, IOException;
}
