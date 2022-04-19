package operation.sql.generic;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.project.process.logtable.StatusType;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.MetadataID;
import metadata.SourceType;
import metadata.record.RecordDataMetadata;
import operation.AbstractOperation;
import operation.Operation;
import operation.parameter.Parameter;
import operation.parameter.ReproducibleParameter;
import operation.sql.SQLOperationBase;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import rdb.table.data.DataTableName;
import rdb.table.data.DataTableSchema;
import rdb.table.data.DataTableSchemaFactory;
import sql.SQLStringUtils;


/**
 * 112020-update
 * based on {@link GenericSQLQuery} which alias names of table name and column name, making the reproducing trivial;
 * 
 * =====================================
 * SQL operation with user-defined SQL query;
 * 
 * Allow any valid SQL query that generates a table view, including SQL aggregate functions that take in only data table name as input without any column names;
 * 
 * details
 * 1. user provide a full SQL string with each table name and column name in alias names;
 * 
 * 2. visframe will try to detect all the alias names of table names and column names in the sql string;
 * 		1. independent table name
 * 		2. dotted column name: Table_Name.Column_Name;
 * 
 * 3. user then need to check if the recognized table and column names are correct
 * 
 * 4. if correct, user need to select a record data table from the VisProjectDBContext for each independent table alias name
 * 		also select a record data table column for each dotted column name;
 * 
 * 
 * @author tanxu
 *
 */
public final class GenericSQLOperation extends SQLOperationBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1617893281156361008L;
	///////////////////////
	public static final SimpleName TYPE_NAME = new SimpleName("GenericSQLOperation");
	public static final VfNotes TYPE_NOTES = new VfNotes("operation with user-defined SQL query");
	
	////////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildGenericSQLOperationLevelSpecificParameterNameValueObjectMap(
			GenericSQLQuery genericSQLQuery){
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.put(GENERIC_SQL_QUERY.getName(), genericSQLQuery);
		
		return ret;
	}
	///////////////////////
	/**
	 * parameter for user-defined SQL query including the base sql string and the input record data name, data table name and column name set
	 */
	public static final ReproducibleParameter<GenericSQLQuery> GENERIC_SQL_QUERY =
			new ReproducibleParameter<>(GenericSQLQuery.class, new SimpleName("genericSQLQuery"), new VfNotes(), "SQL query content", true, null, null);// 
	public GenericSQLQuery getGenericSQLQuery() {
		return (GenericSQLQuery) this.levelSpecificParameterObjectValueMap.get(GENERIC_SQL_QUERY.getName());
	}
	
	
	////////////////////
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap;
	/**
	 * return all Parameters defined at the GenericSQLOperation level
	 * @return
	 */
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap(){
		if(levelSpecificParameterNameMap==null) {
			levelSpecificParameterNameMap = new LinkedHashMap<>();
			levelSpecificParameterNameMap.put(GENERIC_SQL_QUERY.getName(), GENERIC_SQL_QUERY);
		}	
		return levelSpecificParameterNameMap;
	}
	
	////////////////////////////////
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap;
	
	
	/////====constructor
	/**
	 * 
	 * @param operationLevelParameterObjectValueMap full set of parameters even if the parameter value object is null
	 * @param SQLOperationBaseLevelParameterObjectValueMap full set of parameters even if the parameter value object is null
	 * @param genericSQLOperationLevelParameterObjectValueMap full set of parameters even if the parameter value object is null
	 */
	public GenericSQLOperation(
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			
			Map<SimpleName, Object> SQLOperationBaseLevelParameterObjectValueMap,
			
			Map<SimpleName, Object> genericSQLOperationLevelParameterObjectValueMap,
			
			boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent
			) {
		super(operationLevelParameterObjectValueMap, SQLOperationBaseLevelParameterObjectValueMap);
		//validations
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			if(!genericSQLOperationLevelParameterObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given GenericSQLOperationLevelParameterObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		
		//
		this.levelSpecificParameterObjectValueMap = genericSQLOperationLevelParameterObjectValueMap;
		
		///////////////check value constraints
		this.validateParametersValueConstraints(toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent);
	
		//set the output record Metadata of the GenericSQLQuery;
		this.getGenericSQLQuery().setOutputRecordMetadataID(this.getOutputRecordDataID());
		this.getGenericSQLQuery().setOwnerOperationID(this.getID());
		
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
	
	
	///////////////////////////////////////////
	@Override
	public SimpleName getOperationTypeName() {
		return TYPE_NAME;
	}

	
	@Override
	public VfNotes getOperationTypeNotes() {
		return TYPE_NOTES;
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
		this.levelSpecificParameterObjectValueMap.put(parameterName, value);
	}
	
	//////////////////////////////////////////////////
	/**
	 * return the set of input record MetadataID
	 */
	@Override
	public Set<MetadataID> getInputMetadataIDSet() {
		if(this.inputMetadataIDSet==null) {
			this.inputMetadataIDSet = new HashSet<>(this.getGenericSQLQuery().getTableAliasNameRecordDataMetadataIDMap().values());
		}
		
		return inputMetadataIDSet;
	}
	
	
	/**
	 * return the map from input record MetadataID to the set of column names used as independent input column of this operation
	 */
	@Override
	public Map<MetadataID, Set<DataTableColumnName>> getInputRecordMetadataIDInputColumnNameSetMap() {
		
		Map<MetadataID, Set<DataTableColumnName>> ret = new HashMap<>();
		
		this.getGenericSQLQuery().getTableAliasNameRecordDataMetadataIDMap().values().forEach(e->{
			ret.put(e, new HashSet<>());
		});
		
		this.getGenericSQLQuery().getTableAliasNameColumnAliasNameDataTableColumnNameMapMap().forEach((k,v)->{//k=table name, v=map<string,column>
			
			v.forEach((colNameAliasString,colName)->{//string, DataTableColumnName
				ret.get(this.getGenericSQLQuery().getTableAliasNameRecordDataMetadataIDMap().get(k)).add(colName);
			});
			
		});
		
		return ret;
	}
	
	////////////////////////////////////////////
	/**
	 * reproduce and return a parameter name object value map for parameters at GenericSQLOperation level;
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @return
	 * @throws SQLException 
	 */
	private Map<SimpleName, Object> reproduceGenericSQLOperationLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException{
		Map<SimpleName, Object> reproducedGenericSQLOperationLevelParameterObjectValueMap = new HashMap<>();
		
		GenericSQLQuery reproducedGenericSqlQuery = this.getGenericSQLQuery().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		reproducedGenericSQLOperationLevelParameterObjectValueMap.put(GENERIC_SQL_QUERY.getName(), reproducedGenericSqlQuery);
		
		return reproducedGenericSQLOperationLevelParameterObjectValueMap;
	}
	
	/**
	 * reproduce and return a new GenericSQLOperation of this one;
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @throws SQLException 
	 */
	@Override
	public GenericSQLOperation reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
		Map<SimpleName, Object> reproducedOperationLevelParameterObjectValueMap = 
				this.reproduceAbstractOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		Map<SimpleName, Object> reproducedSQLOperationBaseLevelParameterObjectValueMap = 
				this.reproduceSQLOperationBaseLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		Map<SimpleName, Object> reproducedGenericSQLOperationLevelParameterObjectValueMap = 
				this.reproduceGenericSQLOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		return new GenericSQLOperation(
				reproducedOperationLevelParameterObjectValueMap,
				reproducedSQLOperationBaseLevelParameterObjectValueMap,
				reproducedGenericSQLOperationLevelParameterObjectValueMap,
				false //toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent should be false if the operation is reproduced;
				);
	}
	
	
	///////////////////////////////////////////////////////
	
	/**
	 * prepare
	 * 1. check whether host VisProjectDBContext is set or not;
	 * 2. invoke {@link #validateParametersValueConstraints()} TODO
	 * 
	 * main steps
	 * 
	 * 1. build and insert data table schema of output record data table;
	 * 
	 * 2. build SQL string based on user defined SQL query that can directly populate the output data table;
	 * 
	 * 3. run the SQL string to populate the output data table;
	 * 
	 * 4. build and insert output RecordDataMetadata into metadata management table;
	 *  
	 * 5. insert this operation into operation management table;
	 * @throws SQLException 
	 */
	@Override
	public StatusType call() throws SQLException {
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
		this.executeSQLQueryString(); //which will directly populate output data table;
		
		//4
		this.buildAndInsertOutputRecordDataMetadata();
		
		//5
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getOperationManager().insert(this);
		
		
		return StatusType.FINISHED;
	}
	
	/**
	 * build and insert the output data table schema into the host VisProjectDBContext;
	 * @throws SQLException
	 */
	private void buildAndInsertOutputDataTableSchema() throws SQLException {
		DataTableName tableName = 
				this.getHostVisProjectDBContext().getHasIDTypeManagerController().getDataTableSchemaManager()
				.findNextAvailableName(new DataTableName(this.getOutputRecordDataID().getName().getStringValue()));
		
		//
		List<DataTableColumn> orderedListOfColumn = new ArrayList<>();
		orderedListOfColumn.add(DataTableSchemaFactory.makeRUIDColumn());
		orderedListOfColumn.addAll(this.getGenericSQLQuery().getOrderedListOfColumnInOutputDataTable());
		
		this.outputDataTableSchema = 
				new DataTableSchema(tableName, orderedListOfColumn);
		
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getDataTableSchemaManager().insert(this.outputDataTableSchema);
	}
	
	/**
	 * build the SQL string based on user defind SQL query string that will directly populate the output data table schema;
	 * 
	 * 1. replace the alias in user defined SQL query with real table/column names in the host VisProjectDBContext;
	 * 2. add alias to each term in outmost SELECT clause;
	 * 3. add row number column and alias to the outmost SELECT clause;
	 * 4. build the SQL query that extract one single record from each group with the same values of columns corresponding to the primary key columns in output data table schema;
	 * 5. build the INSERT INTO string that directly popuate the output data table schema;
	 * 		note that the SQL string built by this step will be stored in the {@link #SQLString};
	 * @throws SQLException 
	 */
	private void buildSQLQueryString() throws SQLException {
		//1
		String s1 = this.getGenericSQLQuery().buildCustomizedSQLStringWithRealTableAndColumnNames(this.getHostVisProjectDBContext());
		
		//2
		String s2 = GenericSQLOperationUtils.addAliasToEachTermInSelectClause(s1);
		
		//3
		String s3 = GenericSQLOperationUtils.addRowNumberCol(s2);
		
		//4
		List<Integer> primaryKeyColIndexList = new ArrayList<>();
		this.outputDataTableSchema.getPrimaryKeyColumnNameSet().forEach(colName->{
			primaryKeyColIndexList.add(this.outputDataTableSchema.getColumnIndex(colName));//to be consistent with 
		});
		
		String s4 = GenericSQLOperationUtils.buildSelectUniqueRowsSQLQueryString(
				s3, this.outputDataTableSchema.getOrderedListOfNonRUIDColumn().size(), primaryKeyColIndexList);
		
		//5
		List<String> outputTableColumnNameList = new ArrayList<>();
		this.outputDataTableSchema.getOrderedListOfNonRUIDColumn().forEach(col->{
			outputTableColumnNameList.add(col.getName().getStringValue());
		});
		this.SQLString = GenericSQLOperationUtils.buildInsertIntoOutputDataTableSchemaSQLStringWithResultOfSelectFromSQLQuery(
				SQLStringUtils.buildTableFullPathString(this.outputDataTableSchema.getID()),
				outputTableColumnNameList, 
				s4);
	}
	
	/**
	 * note that the built {@link #SQLString} will directly populate output data table;
	 * 
	 * @throws SQLException 
	 */
	private void executeSQLQueryString() throws SQLException {
		Statement statement = this.getHostVisProjectDBContext().getDBConnection().createStatement();
		
		statement.execute(this.SQLString);
	}
	
	
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
	
	
	//////////////////////////////////////
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
		if (!(obj instanceof GenericSQLOperation))
			return false;
		GenericSQLOperation other = (GenericSQLOperation) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
	
	
	
	
}
