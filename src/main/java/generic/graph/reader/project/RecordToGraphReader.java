package generic.graph.reader.project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import basic.VfNotes;
import context.project.VisProjectDBContext;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import metadata.DataType;
import metadata.MetadataID;
import metadata.record.RecordDataMetadata;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import rdb.table.data.DataTableSchemaFactory;
import sql.ResultSetUtils;
import sql.derby.TableContentSQLStringFactory;

/**
 * read two record data metadata from a VisProjectDBContext, one as the graph node another as the graph edge;
 * 
 * facilitate operation of building Graph from exiting record data metadata;
 * 
 * see {@link BuildGraphFromTwoExistingRecordOperation_pre}
 * 
 * !!note that the input RecordDataMetadata for vertex and edge data source do not need to have the primary key attributes the same with the graph data to be built;
 * 
 * 
 * =======================
 * this class does not require the input record data to be a graph and try to extract the valid data 
 * 
 * 1. for input vertex record data
 * 		can be null and all vertex should be extracted and kept from the input edge record data in following steps;
 * 		if not null,
 * 		1. columns in vertexIDColumnNameSet can be of primary key set of the input record data or not;
 * 				when reading, those records with null valued columns in vertexIDColumnNameSet will be skipped;
 * 				
 * 2. for edge record data
 * 		1. only records 
 * 				1. with non-null valued columns in source and sink node id attributes and 
 * 				2. non-null valued columns in all edgeIDColumnNameSet 
 * 			will be considered;
 *
 * 3. whether to filter out duplicate vertex and edges is dependent on the given {@link #toFilterOutDuplicates};
 * 		if false, the duplicates should be dealt with by downstream step, which normally is a {@link GraphBuilder}
 * 		if true, the duplicates should be filtered out;
 * 			query from the data table
 * 
 * 4. this class does not specify the directed-ness of edges, which should be dealt with by each subclass of this class;
 * 
 * 5. this class should try to extract all vertex and edges from the input record data, and DOES NOT validate whether vertex in the edge are present in the vertex record data table or not;
 * 		this should be dealt with by {@link GraphBuilder};
 * 
 * @author tanxu
 * 
 */
public abstract class RecordToGraphReader extends VfProjectGraphReader {
	/**
	 * whether there is a non-null vertexDataSourceRecordDataID and related fields for it
	 * if true, {@link #vertexDataSourceRecordDataID}, {@link #vertexIDColumnNameSet} and {@link #vertexAdditionalFeatureColumnNameSet} should all be non-null;
	 * if false, those fields should all be null;
	 * 		also, the vertex record data's data table schema of the target graph should contain the columns same as the source vertex id column set given in {@link #vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap}
	 */
	private final boolean hasVertexDataSourceRecordData;
	/**
	 * can be null;
	 * if null, all vertex should be extracted from the edge record data table in the downstream step;
	 */
	private final MetadataID vertexDataSourceRecordDataID; 
	private final MetadataID edgeDataSourceRecordDataID;
	
	/**
	 * whether to filter out duplicate vertex and edge when parsing the data tables;
	 * 
	 * if false, the duplicates should be dealt with by downstream step, which normally is a {@link GraphBuilder}
	 */
	private final boolean toFilterOutDuplicates;
	
	/////////////////////
	/**
	 */
	private final LinkedHashSet<DataTableColumnName> vertexIDColumnNameSet;
	/**
	 */
	private final LinkedHashSet<DataTableColumnName> vertexAdditionalFeatureColumnNameSet;
	
	/**
	 */
	private final LinkedHashSet<DataTableColumnName> edgeIDColumnNameSet;
	/**
	 */
	private final LinkedHashSet<DataTableColumnName> edgeAdditionalFeatureColumnNameSet;
	
	private final boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;
	
	/**
	 */
	private final LinkedHashMap<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap;
	/**
	 */
	private final LinkedHashMap<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap;
	
	
	//this should not be done at this stage, rather, should be dealt with by next step, which is {@link GraphBuilder}
	/**
	 * whether to add vertex found in input edge record data table but not in the input vertex record data table to the resulted vertex set;
	 * if true, this allows implicitly defined vertex in edge record data table, otherwise, those vertex will be ignored as well as the edges;
	 * <p></p>
	 * this feature makes it possible to build a graph based on a single record data; see {@link BuildGraphFromSingleExistingRecordOperation_pre} 
	 */
//	private final boolean toAddDiscoveredVertexFromInputEdgeDataTable;
	
	
	//////////////////
	protected RecordDataMetadata vertexDataSourceRecordData;
	protected RecordDataMetadata edgeDataSourceRecordData;
	//built based on the {@link #vertexDataSourceRecordData} and vertexIDColumnNameSet and vertexAdditionalFeatureColumnNameSet
	protected Map<DataTableColumnName, DataTableColumn> vertexAttributeColNameMap;
	//built based on the {@link #edgeDataSourceRecordData} and edge related columns
	protected Map<DataTableColumnName, DataTableColumn> edgeAttributeColNameMap;
	
	
	protected ResultSet vertexRecordDataTableResultSet;
	protected ResultSet edgeRecordDataTableResultSet;
	
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param hasVertexDataSourceRecordData
	 * @param vertexDataSourceRecordDataID can be null;
	 * @param edgeDataSourceRecordDataID cannot be null;
	 * @param toFilterOutDuplicates
	 * @param vertexIDColumnNameSet must be null if vertexDataSourceRecordDataID is null, otherwise, must be non-null and non-empty;
	 * @param vertexAdditionalFeatureColumnNameSet must be null if vertexDataSourceRecordDataID is null, otherwise, must be non-null but can be empty;
	 * @param edgeIDColumnNameSet
	 * @param edgeAdditionalFeatureColumnNameSet
	 * @param edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets
	 * @param vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap if hasVertexDataSourceRecordData is false, every entry in this map should contain the same key and value column name from the input edge source data table;
	 * @param vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap if hasVertexDataSourceRecordData is false, the map key set should be the same with the map key set in vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap
	 */
	RecordToGraphReader(
			VisProjectDBContext hostVisProjectDBContext,
			boolean hasVertexDataSourceRecordData,
			MetadataID vertexDataSourceRecordDataID,
			MetadataID edgeDataSourceRecordDataID,
			boolean toFilterOutDuplicates,
			
			LinkedHashSet<DataTableColumnName> vertexIDColumnNameSet,
			LinkedHashSet<DataTableColumnName> vertexAdditionalFeatureColumnNameSet,
			
			LinkedHashSet<DataTableColumnName> edgeIDColumnNameSet,
			LinkedHashSet<DataTableColumnName> edgeAdditionalFeatureColumnNameSet,
			boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets,
			LinkedHashMap<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap,
			LinkedHashMap<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap
//			boolean toAddDiscoveredVertexFromInputEdgeDataTable
			) {
		super(hostVisProjectDBContext);
		
		
		
		if(hasVertexDataSourceRecordData) {
			if(vertexDataSourceRecordDataID==null) {
				throw new IllegalArgumentException("given vertexDataSourceRecordDataID is null when hasVertexDataSourceRecordData is true!");
			}
			if(!vertexDataSourceRecordDataID.getDataType().equals(DataType.RECORD)) {
				throw new IllegalArgumentException("given vertexDataSourceRecordDataID is not of RECORD data type!");
			}
			///
			if(vertexIDColumnNameSet==null ||vertexIDColumnNameSet.isEmpty()) {
				throw new IllegalArgumentException("given vertexIDColumnNameSet cannot be null or empty!");
			}
			if(vertexAdditionalFeatureColumnNameSet ==null) {
				throw new IllegalArgumentException("given vertexAdditionalFeatureColumnNameSet cannot be null!");
			}
			for(DataTableColumnName col:vertexIDColumnNameSet) {
				if(vertexAdditionalFeatureColumnNameSet.contains(col)) {
					throw new IllegalArgumentException("at least one col is found in both vertexIDColumnNameSet and vertexAdditionalFeatureColumnNameSet:"+col.getStringValue());
				}
			}
			
		}else {
			//
		}
		
		
		if(edgeDataSourceRecordDataID==null) {
			throw new IllegalArgumentException("given edgeDataSourceRecordDataID is null!");
		}
		if(!edgeDataSourceRecordDataID.getDataType().equals(DataType.RECORD)) {
			throw new IllegalArgumentException("given edgeDataSourceRecordDataID is not of RECORD data type!");
		}
		
		////
		if(edgeIDColumnNameSet==null ||edgeIDColumnNameSet.isEmpty()) {
			throw new IllegalArgumentException("given vertexIDColumnNameSet cannot be null or empty!");
		}
		if(edgeAdditionalFeatureColumnNameSet == null) {
			throw new IllegalArgumentException("given edgeAdditionalFeatureColumnNameSet cannot be null or empty!");
		}
		for(DataTableColumnName col:edgeIDColumnNameSet) {
			if(edgeAdditionalFeatureColumnNameSet.contains(col)) {
				throw new IllegalArgumentException("at least one col is found in both edgeIDColumnNameSet and edgeAdditionalFeatureColumnNameSet:"+col.getStringValue());
			}
		}
		
		
		////
		
		//TODO more validations
		
		
		this.hasVertexDataSourceRecordData = hasVertexDataSourceRecordData;
		this.vertexDataSourceRecordDataID = vertexDataSourceRecordDataID;
		this.edgeDataSourceRecordDataID = edgeDataSourceRecordDataID;
		this.toFilterOutDuplicates = toFilterOutDuplicates;
		
		this.vertexIDColumnNameSet = vertexIDColumnNameSet;
		this.vertexAdditionalFeatureColumnNameSet = vertexAdditionalFeatureColumnNameSet;
		
		this.edgeIDColumnNameSet = edgeIDColumnNameSet;
		this.edgeAdditionalFeatureColumnNameSet = edgeAdditionalFeatureColumnNameSet;
		this.edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets = edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;
		this.vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap = vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap;
		this.vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap = vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap;
//		this.toAddDiscoveredVertexFromInputEdgeDataTable = toAddDiscoveredVertexFromInputEdgeDataTable;
	}
	
	
	public boolean hasVertexDataSourceRecordData() {
		return hasVertexDataSourceRecordData;
	}
	
	
	public MetadataID getVertexDataSourceRecordDataID() {
		return vertexDataSourceRecordDataID;
	}

	public MetadataID getEdgeDataSourceRecordDataID() {
		return edgeDataSourceRecordDataID;
	}

	public boolean isToFilterOutDuplicates() {
		return toFilterOutDuplicates;
	}
	
	/////////////////////////////////////////////////////////
	/**
	 * build the sql query string and perform it to build the {@link #vertexRecordDataTableResultSet} to extract records from the given vertex record data table;
	 * 
	 * if {@link #toFilterOutDuplicates} is false, the built sql query will not filter out VfGraphVertex with exactly the same values of vertexIDColumnNameSet;
	 * otherwise, the built sql query will filter out those duplicates;
	 * 
	 * @throws SQLException 
	 */
	protected void queryVertexDataTable() throws SQLException {
		String sqlQueryString;
		
		//vertex id and additional columns
		Set<String> selectedColNameList = new LinkedHashSet<>();
		for(DataTableColumnName colName:this.getVertexAttributeColNameMap().keySet()) {
			selectedColNameList.add(colName.getStringValue());
		}
		
		//vertex id columns
		Set<String> groupByColNameList = new LinkedHashSet<>();
		for(DataTableColumnName colName:this.getVertexIDColumnNameSet()) {
			groupByColNameList.add(colName.getStringValue());
		}
		
		//vertex id columns not null
		String conditionString = TableContentSQLStringFactory.buildAllColumnValueNotNullConditionConditionString(groupByColNameList);
		
		//build the sql query string
		if(this.isToFilterOutDuplicates()) {//select one row from each group with the same vertex id columns values
			sqlQueryString = TableContentSQLStringFactory.buildSelectOneFromEachGroupSQLString(
					this.vertexDataSourceRecordData.getDataTableSchema().getSchemaName().getStringValue(), 
					this.vertexDataSourceRecordData.getDataTableSchema().getName().getStringValue(), 
					DataTableSchemaFactory.makeRUIDColumn().getName().getStringValue(), 
					selectedColNameList, 
					groupByColNameList, 
					conditionString);
		}else {
			sqlQueryString = TableContentSQLStringFactory.buildSelectSQLString(
					this.vertexDataSourceRecordData.getDataTableSchema().getSchemaName().getStringValue(), 
					this.vertexDataSourceRecordData.getDataTableSchema().getName().getStringValue(), 
					selectedColNameList,
					conditionString
					);
		}
		
		//perform the query;
		Statement statement = this.getHostVisProjectDBContext().getDBConnection().createStatement();
		
		this.vertexRecordDataTableResultSet = statement.executeQuery(sqlQueryString);
	}
	
	
	/**
	 * build the sql query string and perform it to build the {@link #vertexRecordDataTableResultSet};
	 * 
	 * if {@link #toFilterOutDuplicates} is false, the built sql query will not filter out VfGraphVertex with exactly the same values of vertexIDColumnNameSet;
	 * otherwise, the built sql query will filter out those duplicates;
	 * @throws SQLException 
	 */
	protected void queryEdgeDataTable() throws SQLException {
		String sqlQueryString;
		
		//selected columns include all columns returned by {@link #getEdgeAttributeColNameMap()}
		Set<String> selectedColNameList = new LinkedHashSet<>();
		for(DataTableColumnName colName:this.getEdgeAttributeColNameMap().keySet()) {
			selectedColNameList.add(colName.getStringValue());
		}
		
		//group-by columns include all edge id columns;
		Set<String> groupByColNameList = new LinkedHashSet<>();
		for(DataTableColumnName colName:this.getEdgeIDColumnNameSet()) {
			groupByColNameList.add(colName.getStringValue());
		}
		
		//group-by columns should all be non-null
		String conditionString = TableContentSQLStringFactory.buildAllColumnValueNotNullConditionConditionString(groupByColNameList);
		
		//build the sql query string
		if(this.isToFilterOutDuplicates()) {//select one row from each group with the same vertex id columns values
			sqlQueryString = TableContentSQLStringFactory.buildSelectOneFromEachGroupSQLString(
					this.edgeDataSourceRecordData.getDataTableSchema().getSchemaName().getStringValue(), 
					this.edgeDataSourceRecordData.getDataTableSchema().getName().getStringValue(), 
					DataTableSchemaFactory.makeRUIDColumn().getName().getStringValue(), 
					selectedColNameList, 
					groupByColNameList, 
					conditionString);
		}else {
			sqlQueryString = TableContentSQLStringFactory.buildSelectSQLString(
					this.edgeDataSourceRecordData.getDataTableSchema().getSchemaName().getStringValue(), 
					this.edgeDataSourceRecordData.getDataTableSchema().getName().getStringValue(), 
					selectedColNameList,
					conditionString
					);
		}
		
		//perform the query;
		Statement statement = this.getHostVisProjectDBContext().getDBConnection().createStatement();
		
		this.edgeRecordDataTableResultSet = statement.executeQuery(sqlQueryString);
		
	}
	
	
	////////////////////////////////////////////////////////////////
	/**
	 * {@inheritDoc}
	 * 
	 * <p></p>
	 * need to extract the corresponding vertex ID DataTableColumns and vertex additional feature DataTableColumns from the input record data for vertex and modify them accordingly;
	 * 
	 */
	@Override
	public Map<DataTableColumnName, DataTableColumn> getVertexAttributeColNameMap() throws SQLException {
		if(this.vertexAttributeColNameMap==null) {
			this.vertexAttributeColNameMap = new LinkedHashMap<>();
			
			if(this.hasVertexDataSourceRecordData) {
				for(DataTableColumn col:this.vertexDataSourceRecordData.getDataTableSchema().getOrderedListOfNonRUIDColumn()) {
					//only add the columns that are in the vertex ID or additional attribute set;
					if(this.getVertexIDColumnNameSet().contains(col.getName())) {
						//need to set the inPrimaryKey to true if attribute is assigned to the id column set
	//					DataTableColumnName name, SQLDataType sqlDataType, boolean inPrimaryKey,
	//					Boolean unique, Boolean notNull, String defaultStringValue, String additionalConstraints,
	//					VfNotes notes
						this.vertexAttributeColNameMap.put(
								col.getName(), 
								new DataTableColumn(
										col.getName(), col.getSqlDataType(), true, //in primary key
										col.isUnique(), true, //not null
										col.getDefaultStringValue(), col.getAdditionalConstraints(), VfNotes.makeVisframeDefinedVfNotes()));
					}else if(this.getVertexAdditionalFeatureColumnNameSet().contains(col.getName())) {
						this.vertexAttributeColNameMap.put(
								col.getName(), 
								new DataTableColumn(
										col.getName(), col.getSqlDataType(), false, //not in primary key
										col.isUnique(), col.isNotNull(), col.getDefaultStringValue(), col.getAdditionalConstraints(), VfNotes.makeVisframeDefinedVfNotes()));
					}
				}
			}else {//there is no input vertex record data, thus the columns for the vertex record data of the target graph should be built based on the input edge source record data;
				for(DataTableColumn col: this.edgeDataSourceRecordData.getDataTableSchema().getOrderedListOfNonRUIDColumn()) {
					if(this.getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap().keySet().contains(col.getName())) {
						this.vertexAttributeColNameMap.put(col.getName(), 
								new DataTableColumn(
										col.getName(), col.getSqlDataType(), true, //in primary key
										col.isUnique(), true, //not null
										col.getDefaultStringValue(), col.getAdditionalConstraints(), VfNotes.makeVisframeDefinedVfNotes()));
					}
				}
			}
		}
		
		return this.vertexAttributeColNameMap;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * extract the edge ID columns, source/sink node id columns and the edge additional features columns from the input edge record data table;
	 */
	@Override
	public Map<DataTableColumnName, DataTableColumn> getEdgeAttributeColNameMap() throws SQLException {
		if(this.edgeAttributeColNameMap==null) {
			this.edgeAttributeColNameMap = new HashMap<>();
			//only add the columns that are included in the edge related column set
			for(DataTableColumn col:this.edgeDataSourceRecordData.getDataTableSchema().getOrderedListOfNonRUIDColumn()) {
				
				if(this.getEdgeIDColumnNameSet().contains(col.getName())){//edge id columns, must be in primary key and not null
					this.edgeAttributeColNameMap.put(
							col.getName(), 
							new DataTableColumn(
									col.getName(), col.getSqlDataType(), true, //in primary key
									col.isUnique(), true, //not null
									col.getDefaultStringValue(), col.getAdditionalConstraints(), VfNotes.makeVisframeDefinedVfNotes()));
				}else if(
						this.getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap().containsValue(col.getName())||
						this.getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap().containsValue(col.getName())) {//source or sink node id columns
					if(this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets()) {//disjoint from edge id columns, must be not null and NOT in primary key
						this.edgeAttributeColNameMap.put(
								col.getName(), 
								new DataTableColumn(
										col.getName(), col.getSqlDataType(), false, //NOT in primary key
										col.isUnique(), true, //not null
										col.getDefaultStringValue(), col.getAdditionalConstraints(), VfNotes.makeVisframeDefinedVfNotes()));
					}else {//
						//merged to edge id columns, added as edge id columns and do nothing; should never be reached in practice;
					}
				}else if(this.getEdgeAdditionalFeatureColumnNameSet().contains(col.getName())) {//must NOT be in primary key;
					this.edgeAttributeColNameMap.put(
							col.getName(), 
							new DataTableColumn(
									col.getName(), col.getSqlDataType(), false, //not in primary key
									col.isUnique(), col.isNotNull(), col.getDefaultStringValue(), col.getAdditionalConstraints(), VfNotes.makeVisframeDefinedVfNotes()));
					
				}else {
					//do nothing
				}
				
			}
		}
		
		return this.edgeAttributeColNameMap;
	}
	
	///////////////////////////////////////////////
	/**
	 * initialize and preprocess;
	 */
	@Override
	public void initialize() throws SQLException {
		this.vertexDone = false;
		this.edgeDone = false;
		
		this.preprocessing();
	}
	
	/**
	 * 1. extract input vertex and edge RecordDataMetadata;
	 * 2. validate column existence;
	 * 3. build the {@link #vertexAttributeColNameMap} and {@link #edgeAttributeColNameMap}
	 * @throws SQLException 
	 */
	private void preprocessing() throws SQLException {
		if(this.hasVertexDataSourceRecordData) {
			this.vertexDataSourceRecordData = (RecordDataMetadata) this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().lookup(this.getVertexDataSourceRecordDataID());
		}else {
			this.vertexDataSourceRecordData = null;
		}
		
		this.edgeDataSourceRecordData = (RecordDataMetadata) this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().lookup(this.getEdgeDataSourceRecordDataID());
		
		//check if all input columns are existing in the input vertex data table schema if hasVertexDataSourceRecordData is true
		if(this.hasVertexDataSourceRecordData) {
			for(DataTableColumnName col:this.getVertexIDColumnNameSet()) {
				if(!this.vertexDataSourceRecordData.getDataTableSchema().getOrderListOfColumnName().contains(col)) {
					throw new IllegalArgumentException("at least one column in the given vertexIDColumnNameSet is not found in the vertexDataSourceRecordData:"+col.getStringValue());
				}
			}
			for(DataTableColumnName col:this.getVertexAdditionalFeatureColumnNameSet()) {
				if(!this.vertexDataSourceRecordData.getDataTableSchema().getOrderListOfColumnName().contains(col)) {
					throw new IllegalArgumentException("at least one column in the given vertexAdditionalFeatureColumnNameSet is not found in the vertexDataSourceRecordData:"+col.getStringValue());
				}
			}
		}
		//check if all input columns are existing in the input edge data table schema
		for(DataTableColumnName col:this.getEdgeIDColumnNameSet()) {
			if(!this.edgeDataSourceRecordData.getDataTableSchema().getOrderListOfColumnName().contains(col)) {
				throw new IllegalArgumentException("at least one column in the given EdgeIDColumnNameSet is not found in the edgeDataSourceRecordData:"+col.getStringValue());
			}
		}
		
		for(DataTableColumnName col:this.getEdgeAdditionalFeatureColumnNameSet()) {
			if(!this.edgeDataSourceRecordData.getDataTableSchema().getOrderListOfColumnName().contains(col)) {
				throw new IllegalArgumentException("at least one column in the given edgeAdditionalFeatureColumnNameSet is not found in the edgeDataSourceRecordData:"+col.getStringValue());
			}
		}
		for(DataTableColumnName col:this.getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap().values()) {
			if(!this.edgeDataSourceRecordData.getDataTableSchema().getOrderListOfColumnName().contains(col)) {
				throw new IllegalArgumentException("at least one column in the given source vertex ID column set is not found in the edgeDataSourceRecordData:"+col.getStringValue());
			}
		}
		for(DataTableColumnName col:this.getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap().values()) {
			if(!this.edgeDataSourceRecordData.getDataTableSchema().getOrderListOfColumnName().contains(col)) {
				throw new IllegalArgumentException("at least one column in the given sink vertex ID column set is not found in the edgeDataSourceRecordData:"+col.getStringValue());
			}
		}
		
		//if there is no input vertex source record data, only edge record data;
		if(!this.hasVertexDataSourceRecordData) {
			//map key and value should be the same for each entry in the vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap
			for(DataTableColumnName col:this.getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap().keySet()) {
				if(!this.getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap().get(col).equals(col)) {
					throw new IllegalArgumentException("map key and value should be the same in vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap when hasVertexDataSourceRecordData is false!");
				}
			}
			//
			for(DataTableColumnName col:this.getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap().keySet()) {
				if(!this.getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap().containsKey(col)) {
					throw new IllegalArgumentException("map key set of vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap should be the same with vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap when hasVertexDataSourceRecordData is false!");
				}
			}
		}
		
		//////////////////////
		this.getVertexAttributeColNameMap();
		this.getEdgeAttributeColNameMap();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>steps</p>
	 * 
	 * 1. create a sql query string to retrieve the columns in {@link #getVertexIDColumnNameSet()} and {@link #getVertexAdditionalFeatureColumnNameSet()} of all records from the vertex record data table with non-null columns values in {@link #getVertexIDColumnNameSet()}
	 * 
	 * 2. perform the sql query and get the ResultSet;
	 * 
	 * 3. parse the next record and build a VfGraphVertex and return it;
	 * 
	 * /////////////////////////
	 * return the next VfGraphVertex parsed and constructed from the vertex record data; return null if none left;
	 * 
	 * this method will try to build and return a VfGraphVertex for all record with non-null values in {@link #vertexIDColumnNameSet};
	 * 
	 * @throws SQLException 
	 */
	@Override
	public VfGraphVertex nextVertex() throws SQLException {
		if(this.vertexDone) {
			throw new UnsupportedOperationException();
		}
		
		//there is no input vertex data source record data; simply skip this step;
		if(!this.hasVertexDataSourceRecordData) {
			this.vertexDone = true;
			return null;
		}
		
		
		if(this.vertexRecordDataTableResultSet==null) {
			this.queryVertexDataTable();
		}
		
		//
		while(this.vertexRecordDataTableResultSet.next()) {
			Map<DataTableColumnName, String> colNameStringValueMap = ResultSetUtils.getCurrentRecordColumnNameStringValueMap(this.vertexRecordDataTableResultSet, this.getVertexAttributeColNameMap());
		
			Map<DataTableColumnName, String> IDAttributeNameStringValueMap = new HashMap<>();
			for(DataTableColumnName colName:this.getVertexIDColumnNameSet()) {
				IDAttributeNameStringValueMap.put(colName, colNameStringValueMap.get(colName));
			}
			
			Map<DataTableColumnName, String> additionalAttributeNameStringValueMap = new HashMap<>();
			for(DataTableColumnName colName:this.getVertexAdditionalFeatureColumnNameSet()) {
				additionalAttributeNameStringValueMap.put(colName, colNameStringValueMap.get(colName));
			}
			
			VfGraphVertex ret = new VfGraphVertex(IDAttributeNameStringValueMap, additionalAttributeNameStringValueMap);
			
			return ret;
		}
		
		this.vertexRecordDataTableResultSet.close();
		
		this.vertexDone = true;
		
		return null;
	}
	
	
	/**
	 * after this method is invoked, the {@link #nextVertex()} can be invoked to build and retrieve vertex from the input vertex record data table from start, the same is with {@link #nextEdge()};
	 */
	@Override
	public void restart() {
		this.edgeDone = false;
		this.vertexDone = false;
		
		//throw SQL exception should be ignored
		try {
			this.vertexRecordDataTableResultSet.close();
			this.vertexRecordDataTableResultSet = null;
			this.edgeRecordDataTableResultSet.close();
			this.edgeRecordDataTableResultSet = null;
		} catch (SQLException e) {
			//do nothing
		}
	}
	
	
	//////////////////////////////////////

	
	/**
	 * return the next VfGraphEdge parsed and constructed from the edge record data; return null if none left;
	 * 
	 * this method will try to build and return a VfGraphEdge for all record with non-null values in {@link #edgeIDColumnNameSet} and source and sink vertex id columns(if {@link #edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets} is true);
	 * 
	 * if {@link #toFilterOutDuplicates} is false, this method will not filter out VfGraphEdge with exactly the same values of {@link #edgeIDColumnNameSet};
	 * otherwise, this method will filter out those duplicates;
	 * 
	 * directed-ness of extracted edges should be dealt with by subclass;
	 * 
	 * @throws SQLException 
	 */
	@Override
	public abstract VfGraphEdge nextEdge() throws SQLException;

	
	
	
	////////////////////////////////////////////////
	@Override
	public LinkedHashSet<DataTableColumnName> getVertexIDColumnNameSet() {
		return this.vertexIDColumnNameSet;
	}
	
	@Override
	public LinkedHashSet<DataTableColumnName> getVertexAdditionalFeatureColumnNameSet() {
		return this.vertexAdditionalFeatureColumnNameSet;
	}
	
	@Override
	public LinkedHashSet<DataTableColumnName> getEdgeIDColumnNameSet() {
		return this.edgeIDColumnNameSet;
	}

	@Override
	public LinkedHashSet<DataTableColumnName> getEdgeAdditionalFeatureColumnNameSet() {
		return this.edgeAdditionalFeatureColumnNameSet;
	}
	
	@Override
	public boolean isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets() {
		return this.edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;
	}

	@Override
	public LinkedHashMap<DataTableColumnName, DataTableColumnName> getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap() {
		return this.vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap;
	}

	@Override
	public LinkedHashMap<DataTableColumnName, DataTableColumnName> getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap() {
		return this.vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap;
	}


}
