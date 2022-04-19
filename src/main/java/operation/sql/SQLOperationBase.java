package operation.sql;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.DataType;
import metadata.MetadataID;
import metadata.MetadataName;
import metadata.record.RecordDataMetadata;
import operation.AbstractOperation;
import operation.Operation;
import operation.parameter.Parameter;
import operation.parameter.ReproducibleParameter;
import rdb.table.data.DataTableSchema;


/**
 * base class for operation that is based on sql query and result in an single output data table for the single output RecordDataMetadata;
 * 
 * 
 * @author tanxu
 *
 */
public abstract class SQLOperationBase extends AbstractOperation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5243451449185211928L;

	
	////////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildSQLOperationBaseLevelSpecificParameterNameValueObjectMap(
			MetadataName outputRecordDataName){
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.put(OUTPUT_RECORD_DATA_ID.getName(), new MetadataID(outputRecordDataName, DataType.RECORD));
		
		return ret;
	}
	
	/////////////////////////////////////
	
	/////==== SQLOperation level OperationParameters, shared by all concrete operation subtypes
	/**
	 * <p>the parameter for the name of the output RecordDataMetadata of a {@link SQLOperationBase} type operation</p>
	 * note that all {@link SQLOperationBase} subtypes have one single output RecordDataMetadata;
	 */
	public static final ReproducibleParameter<MetadataID> OUTPUT_RECORD_DATA_ID = 
			new ReproducibleParameter<>(MetadataID.class, new SimpleName("outputRecordDataID"), new VfNotes(), "Output record data ID", 
					true, m->{return m.getDataType()==DataType.RECORD;}, null);// 
	public MetadataID getOutputRecordDataID() {
		return (MetadataID)this.levelSpecificParameterObjectValueMap.get(OUTPUT_RECORD_DATA_ID.getName());
	}
	
	
	////////////////////
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap;
	/**
	 * return all Parameters defined at the {@link SQLOperationBase} level
	 * @return
	 */
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap(){
		if(levelSpecificParameterNameMap==null) {
			levelSpecificParameterNameMap = new LinkedHashMap<>();
			
			levelSpecificParameterNameMap.put(OUTPUT_RECORD_DATA_ID.getName(), OUTPUT_RECORD_DATA_ID);
		}
		
		return levelSpecificParameterNameMap;
	}
	
	///////////////////////////////////////////////////
	////=====final fields
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap;
	
	
	//////////////////////////
	//transient fields
	/**
	 * 
	 */
	protected transient RecordDataMetadata outputRecordMetadata;
	/**
	 * DataTableSchema of output record data 
	 */
	protected transient DataTableSchema outputDataTableSchema;
	/**
	 * SQL string whose result is either used to directly populate the output data table ({@link GenericSQLOperation}) or 
	 * processed to be inserted into output data table with ResultSet ({@link SingleInputRecordDataPredefinedSQLOperation})
	 */
	protected transient String SQLString;
	
	
	
	/////====constructor 
	/**
	 * constructor
	 * @param operationLevelParameterObjectValueMap
	 * @param SQLOperationBaseLevelParameterNameObjectValueMap
	 */
	protected SQLOperationBase(
//			boolean resultedFromReproducing, 
			
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			
			Map<SimpleName, Object> SQLOperationBaseLevelParameterNameObjectValueMap
	){
		super(operationLevelParameterObjectValueMap);
		
		//validations
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			if(!SQLOperationBaseLevelParameterNameObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given SQLOperationBaseLevelParamterObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		
		this.levelSpecificParameterObjectValueMap = SQLOperationBaseLevelParameterNameObjectValueMap;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws SQLException 
	 */
	@Override
	protected void validateParametersValueConstraints(boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent) {
		//1. super class's constraints
		super.validateParametersValueConstraints(toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent);
		//2. level specific parameter's basic constraints defined by the Parameter class
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			Parameter<?> parameter = levelSpecificParameterNameMap().get(parameterName);
			
			if(!parameter.validateObjectValue(levelSpecificParameterObjectValueMap.get(parameterName), toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent)){
				throw new IllegalArgumentException("invalid value object found for singleGenericGraphAsInputOperationLevelParameterObjectValueMap:"+parameterName);
			}
		}
		//3. additional inter-parameter constraints involving parameters at this level
		//TODO
	}
	
	///////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<SimpleName, Parameter<?>> getAllParameterNameMapOfCurrentAndAboveLevels() {
		Map<SimpleName, Parameter<?>> ret = new LinkedHashMap<>();
		ret.putAll(super.getAllParameterNameMapOfCurrentAndAboveLevels());
		ret.putAll(levelSpecificParameterNameMap());
		return ret;
	}
	
	@Override
	public Map<SimpleName, Object> getAllParameterNameValueObjectMapOfCurrentAndAboveLevels() {
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.putAll(super.getAllParameterNameMapOfCurrentAndAboveLevels());
		ret.putAll(levelSpecificParameterNameMap());
		
		return ret;
	}
	
	/**
	 * since {@link AbstractOperation} is the root class of {@link Operation} hierarchy, if the given parameter is not in {@link #getLevelSpecificParameterNameMap()}, throw {@link IllegalArgumentException}
	 */
	@Override
	public void setParameterValueObject(SimpleName parameterName, Object value) {
		if(levelSpecificParameterNameMap().containsKey(parameterName)) {
			this.setLevelSpecificParameterValueObject(parameterName, value);
		}else {
			super.setParameterValueObject(parameterName, value);
		}
	}
	
	@Override
	public void setLevelSpecificParameterValueObject(SimpleName parameterName, Object value) {
//		if(!levelSpecificParameterNameMap().get(parameterName).validateObjectValue(value, this.isReproduced())) {
//			throw new IllegalArgumentException("given parameter value object is invalid:"+value);
//		}
		
		this.levelSpecificParameterObjectValueMap.put(parameterName, value);
	}
	
	
	//////////////////////////////////////////////////
	/**
	 * reproduce and return a parameter name value object map of parameters at SQLOperationBase level
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @return
	 * @throws SQLException 
	 */
	protected Map<SimpleName, Object> reproduceSQLOperationBaseLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		MetadataID reproducedOutputRecordDataID =
				this.getOutputRecordDataID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		/////////////////////////
		Map<SimpleName, Object> ret = new HashMap<>();
		ret.put(OUTPUT_RECORD_DATA_ID.getName(), reproducedOutputRecordDataID);
		
		return ret;
	}
	
	

	///////////
	/**
	 * returns the set of output MetadataID
	 * @return
	 */
	@Override
	public Set<MetadataID> getOutputMetadataIDSet(){
		Set<MetadataID> ret = new HashSet<>();
		ret.add(this.getOutputRecordDataID());
		return ret;
	}

	
	
	
	///////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((levelSpecificParameterObjectValueMap == null) ? 0
				: levelSpecificParameterObjectValueMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof SQLOperationBase))
			return false;
		SQLOperationBase other = (SQLOperationBase) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
	

//	
//	protected Map<SimpleName, Object> getSQLOperationBaseLevelParameterObjectValueMap() {
//		return SQLOperationBaseLevelParameterNameObjectValueMap;
//	}
	
	
	////original concrete methods
//	/**
//	 * build and return the RecordMetadata of output record data object;
//	 * @return
//	 */
//	public RecordDataMetadata getOutputRecordDataMetadata() {
//		if(this.outputRecordMetadata == null) {
////			ComplexName name, VfNotes notes, 
////			SourceType sourceType,
////			MetadataID sourceCompositeDataMetadataID, OperationID sourceOperationID,
////			
////			RelationalTableSchemaID dataTableID,
//////			RelationalTableSchema dataTableSchema,
////			Boolean ofGraphNode,
////			Boolean ofTreeNode
//			this.outputRecordMetadata = new RecordDataMetadata(
//					this.getOutputRecordDataName(),
//					new VfNotes(), //record data is generated by an operation
//					SourceType.RESULT_FROM_OPERATION,
//					null,//
//					this.getID(),
////					this.getOutputDataTableSchemaID(),
//					this.getOutputDataTableSchema(),
//					null
//					);
//		}
//		
//		
//		return this.outputRecordMetadata;
//	}

//	/**
//	 * construct and return the full sql string that can perform the sql operation and populate the output data table:
//	 * INSERT INTO ... SELECT ...
//	 */
//	protected abstract String getFullSqlString();
//	
	
	//////abstract methods
//	/**
//	 * return the set of data tables and their columns present in the sql query
//	 * the column set cannot be null but can be empty??;
//	 * @return
//	 */
//	protected abstract Map<RelationalTableSchemaID,Set<SimpleName>> getInputDataTableColumnSetMap();
	
//	/**
//	 * return the output data table RelationalTableSchemaID;
//	 * this method is not implemented in {@link SQLOperationBase} because in GenericSQLOperation, the full RelationalTableSchema of output data table is given as input parameters;
//	 * @return
//	 */
//	protected abstract DataTableSchemaID getOutputDataTableSchemaID();
	
//	/**
//	 * get the DataTableSchema for the output record data
//	 * @return
//	 */
//	protected abstract DataTableSchema getOutputDataTableSchema();
	
}
