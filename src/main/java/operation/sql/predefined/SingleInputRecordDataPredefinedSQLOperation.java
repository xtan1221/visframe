package operation.sql.predefined;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.project.process.logtable.StatusType;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.DataType;
import metadata.MetadataID;
import metadata.SourceType;
import metadata.record.RecordDataMetadata;
import operation.AbstractOperation;
import operation.Operation;
import operation.parameter.Parameter;
import operation.parameter.ReproducibleParameter;
import rdb.table.data.DataTableSchema;

public abstract class SingleInputRecordDataPredefinedSQLOperation extends PredefinedSQLBasedOperation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8728457686083272512L;

	
	////////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildSingleInputRecordDataPredefinedSQLOperationLevelSpecificParameterNameValueObjectMap(
			MetadataID inputRecordDataMetadataID){
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.put(INPUT_RECORD_METADATAID.getName(), inputRecordDataMetadataID);
		
		return ret;
	}
	
	/////////////////////////////////////
	/**
	 * parameter for the single input record data name
	 */
	public static final ReproducibleParameter<MetadataID> INPUT_RECORD_METADATAID =
			new ReproducibleParameter<>(MetadataID.class, new SimpleName("inputRecordDataMetadataID"), new VfNotes(), "Input record data MetadataID", true, 
					m->{return m.getDataType()==DataType.RECORD;},
					null);// 
	public MetadataID getInputRecordDataMetadataID() {
		return (MetadataID)this.levelSpecificParameterObjectValueMap.get(INPUT_RECORD_METADATAID.getName());
	}
	
	////////////////////
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap;
	//return all Parameters defined at the SQLOperation level
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap(){
		if(levelSpecificParameterNameMap==null) {
			levelSpecificParameterNameMap = new LinkedHashMap<>();
			levelSpecificParameterNameMap.put(INPUT_RECORD_METADATAID.getName(), INPUT_RECORD_METADATAID);
		}
		
		return levelSpecificParameterNameMap;
	}
	
	///////////////////////////////
	////=====final fields
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap;
	
	
	/**
	 * constructor
	 * @param operationLevelParameterObjectValueMap
	 * @param SQLOperationBaseLevelParameterObjectValueMap
	 * @param predefinedSQLBasedOperationLevelParameterObjectValueMap
	 * @param singleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap
	 */
	protected SingleInputRecordDataPredefinedSQLOperation(
//			boolean resultedFromReproducing,
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			Map<SimpleName, Object> SQLOperationBaseLevelParameterObjectValueMap,
			Map<SimpleName, Object> predefinedSQLBasedOperationLevelParameterObjectValueMap,
			Map<SimpleName, Object> singleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap
			) {
		super(operationLevelParameterObjectValueMap, SQLOperationBaseLevelParameterObjectValueMap,
				predefinedSQLBasedOperationLevelParameterObjectValueMap);
		//validations
		//always first validate each value objects with the constraints implemented in the Parameter<?> object of each parameter
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			if(!singleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given singleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		
		this.levelSpecificParameterObjectValueMap = singleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap;
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
				throw new IllegalArgumentException("invalid value object found for singleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap:"+parameterName);
			}
		}
		//3. additional inter-parameter constraints involving parameters at this level
		//TODO

		
		//then validate constraints directly depending on the value object of input parameters but not implemented in the Parameter<?> object
		//input MetadataID is of RECORD type
		MetadataID inputRecordMetadataID = (MetadataID)levelSpecificParameterObjectValueMap.get(INPUT_RECORD_METADATAID.getName());
		if(inputRecordMetadataID.getDataType()!=DataType.RECORD) {
			throw new IllegalArgumentException("given input record data MetadataID is not of RECORD data type");
		}
		
	}
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
	
	///////////////////////////////////////////////////////////////////
	/**
	 * reproduce and return a parameter name value object map of parameters at SingleInputRecordDataPredefinedSQLOperation level
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @return
	 * @throws SQLException 
	 */
	protected Map<SimpleName, Object> reproduceSingleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		MetadataID reproducedInputRecordDataMetadataID = 
				this.getInputRecordDataMetadataID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		/////////////////
		Map<SimpleName, Object> reproducedSingleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap = new HashMap<>();
		reproducedSingleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap.put(INPUT_RECORD_METADATAID.getName(), reproducedInputRecordDataMetadataID);
		
		return reproducedSingleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap;
	}
	
	
	///////////////////////////////////
	
	@Override
	public Set<MetadataID> getInputMetadataIDSet() {
		if(this.inputMetadataIDSet==null) {
			this.inputMetadataIDSet = new HashSet<>();
			
			this.inputMetadataIDSet.add(this.getInputRecordDataMetadataID());
		}
		
		return this.inputMetadataIDSet;
	}
	
	
	///////////////////////////////////
	//
	protected transient RecordDataMetadata inputRecordDataMetadata;
	/**
	 * result set for the {@link #SQLString}
	 */
	protected transient ResultSet resultSet;
	
	/**
	 * prepare
	 * 1. check whether host VisProjectDBContext is set or not;
	 * 2. invoke {@link #validateParametersValueConstraints()} TODO
	 * 
	 * main steps
	 * 
	 * 1. build and insert data table schema of output record data table;
	 * 
	 * 2. build sql string 
	 * 		to group and sort the input record data table;
	 * 
	 * 3. run the sql string to generate a ResultSet
	 * 
	 * 4. populate the output record data table with the ResultSet
	 * 	
	 * 5. build and insert output RecordDataMetadata into metadata management table;
	 *  
	 * 6. insert this operation into operation management table;
	 * @throws SQLException 
	 */
	@Override
	public final StatusType call() throws SQLException {
		//prepare steps
		//1
		if(this.getHostVisProjectDBContext()==null) {
			throw new IllegalArgumentException("host VisProjectDBContext is null!");
		}
		
		//2
		this.validateParametersValueConstraints(true); //??TODO
		
		//main steps
		//1
		this.buildAndInsertOutputDataTableSchema();
		
		//2
		this.buildSQLQueryString();
		
		//3
		this.executeSQLQueryString();
		
		//4
		this.populateOutputDataTable();
		
		//5
		this.buildAndInsertOutputRecordDataMetadata();
		
		//6
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getOperationManager().insert(this);
		
		
		return StatusType.FINISHED;
	}

	/**
	 * build and insert the {@link DataTableSchema} of the output record data;
	 * 
	 * @throws SQLException 
	 */
	protected abstract void buildAndInsertOutputDataTableSchema() throws SQLException;
	
	/**
	 * build sql string to preprocess the input record data table and retrieve a view;
	 */
	protected abstract void buildSQLQueryString();
	
	
	/**
	 * execute the built sql query string to get a ResultSet
	 * @throws SQLException 
	 */
	private void executeSQLQueryString() throws SQLException {
		
		Statement statement = this.getHostVisProjectDBContext().getDBConnection().createStatement();
		
		this.resultSet = statement.executeQuery(this.SQLString);
	}
	
	/**
	 * process the ResultSet and populate the output record data table 
	 * 		
	 * @throws SQLException 
	 */
	protected abstract void populateOutputDataTable() throws SQLException;
	
	/**
	 * @throws SQLException 
	 * 
	 */
	private void buildAndInsertOutputRecordDataMetadata() throws SQLException {
		RecordDataMetadata outputRecordDataMetadata = new RecordDataMetadata(
				this.getOutputRecordDataID().getName(), VfNotes.makeVisframeDefinedVfNotes(),
				SourceType.RESULT_FROM_OPERATION,
				null,
				this.getID(),
				this.outputDataTableSchema,
				null
				);
		
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().insert(outputRecordDataMetadata);
	}

	
	///////////////////////////////
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
		if (!(obj instanceof SingleInputRecordDataPredefinedSQLOperation))
			return false;
		SingleInputRecordDataPredefinedSQLOperation other = (SingleInputRecordDataPredefinedSQLOperation) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
	
	
}
