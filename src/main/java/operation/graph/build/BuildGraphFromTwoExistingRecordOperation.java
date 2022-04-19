package operation.graph.build;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import generic.graph.reader.project.RecordToGraphReaderFactory;
import metadata.DataType;
import metadata.MetadataID;
import metadata.graph.GraphDataMetadata;
import operation.parameter.DataReproducibleParameter;
import operation.parameter.Parameter;
import operation.parameter.ReproducibleParameter;
import operation.parameter.primitive.BooleanParameter;
import operation.utils.DataTableColumnNameKeyValueLinkedHashMap;
import operation.utils.DataTableColumnNameLinkedHashSet;
import rdb.table.data.DataTableColumnName;


/**
 * build a {@link GraphDataMetadata} with two input RecordDataMetadata as data source for the node and edge data of the GraphDataMetadata;
 * 
 * note that for the built GraphDataMetadata, new RecordDataMetadata for the node and edge data as well as their data tables will be created in the host VisProjectDBContext;
 * 
 * 
 * the directed-ness of edges can be either all as the same type or use a column as indicator for the DirectedType;
 * 
 * 
 * the source/sink vertex id columns of the edge record data should have the SAME data type with the id columns of the vertex;
 * 
 * @author tanxu
 */
public final class BuildGraphFromTwoExistingRecordOperation extends BuildGraphFromExistingRecordOperationBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4198721288843538534L;
	
	////////////////////////////
	public static final SimpleName TYPE_NAME = new SimpleName("BuildGraphFromTwoExistingRecordOperation");
	public static final VfNotes TYPE_NOTES = VfNotes.makeVisframeDefinedVfNotes();
	
	///////////////////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildBuildGraphFromTwoExistingRecordOperationLevelSpecificParameterNameValueObjectMap(
			MetadataID inputNodeRecordDataMetadataID, 
			LinkedHashSet<DataTableColumnName> inputNodeDataTableColumnSetAsNodeID, 
			LinkedHashSet<DataTableColumnName> inputNodeDataTableColumnSetAsAdditionalFeature,
			
			//edge record data related
			LinkedHashMap<DataTableColumnName,DataTableColumnName> nodeIDColumnNameEdgeSourceNodeIDColumnNameMap,
			LinkedHashMap<DataTableColumnName,DataTableColumnName> nodeIDColumnNameEdgeSinkNodeIDColumnNameMap,
			
			boolean toAddDiscoveredVertexFromInputEdgeDataTable
			
			){
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.put(INPUT_NODE_RECORD_METADATAID.getName(), inputNodeRecordDataMetadataID);
		ret.put(INPUT_NODE_DATA_TABLE_COLUMN_SET_AS_NODE_ID.getName(), new DataTableColumnNameLinkedHashSet(inputNodeDataTableColumnSetAsNodeID));
		ret.put(INPUT_NODE_DATA_TABLE_COLUMN_SET_AS_ADDITIONAL_FEATURE.getName(), new DataTableColumnNameLinkedHashSet(inputNodeDataTableColumnSetAsAdditionalFeature));
		
		ret.put(NODE_ID_COLUMN_NAME_EDGE_SOURCE_NODE_ID_COLUMN_NAME_MAP.getName(), new DataTableColumnNameKeyValueLinkedHashMap(nodeIDColumnNameEdgeSourceNodeIDColumnNameMap));
		ret.put(NODE_ID_COLUMN_NAME_EDGE_SINK_NODE_ID_COLUMN_NAME_MAP.getName(), new DataTableColumnNameKeyValueLinkedHashMap(nodeIDColumnNameEdgeSinkNodeIDColumnNameMap));
		
		ret.put(TO_ADD_DISCOVERED_VERTEX_FROM_INPUT_EDGE_DATA_TABLE.getName(), toAddDiscoveredVertexFromInputEdgeDataTable);
		
		return ret;
	}
	
	///////////////////////////////////////////
	/**
	 * parameter for MetadataID of the input record data for the node record of the graph data to build;
	 * cannot be null;
	 */
	public static final ReproducibleParameter<MetadataID> INPUT_NODE_RECORD_METADATAID =
			new ReproducibleParameter<>(MetadataID.class, new SimpleName("inputNodeRecordDataMetadataID"), VfNotes.makeVisframeDefinedVfNotes(), "inputNodeRecordDataMetadataID", true, 
					m->{return m.getDataType()==DataType.RECORD;},
					null);// 
	
	/**
	 * parameter for set of columns of the input record data used as NODE ID for node record of graph to build;
	 * the given column set is independent from the primary key column set of the input data table; thus can contain none or any number of columns in the primary key as well as non-primary key;
	 * cannot be null or empty set;
	 */ 
	public static final DataReproducibleParameter<DataTableColumnNameLinkedHashSet> INPUT_NODE_DATA_TABLE_COLUMN_SET_AS_NODE_ID =
			new DataReproducibleParameter<>(DataTableColumnNameLinkedHashSet.class, new SimpleName("inputNodeDataTableColumnSetAsNodeID"), VfNotes.makeVisframeDefinedVfNotes(), "inputNodeDataTableColumnSetAsNodeID", true, null, null);
	
	/**
	 * parameter for set of columns of the input record data used as additional features for node record of graph to build;
	 * the given column set is independent from the primary key column set of the input data table; thus can contain none or any number of columns in the primary key as well as non-primary key;
	 * cannot be null, but can be empty set;
	 */
	public static final DataReproducibleParameter<DataTableColumnNameLinkedHashSet> INPUT_NODE_DATA_TABLE_COLUMN_SET_AS_ADDITIONAL_FEATURE =
			new DataReproducibleParameter<>(DataTableColumnNameLinkedHashSet.class, new SimpleName("inputNodeDataTableColumnSetAsAdditionalFeature"), VfNotes.makeVisframeDefinedVfNotes(), "inputNodeDataTableColumnSetAsAdditionalFeature", true, null, null);
	
	//////////
	
	/**
	 * parameter for a map from input edge record column name to the column name of the node id columns in the input node record data for source node ID columns;
	 * the given column set is independent from the primary key column set of the input data table; thus can contain none or any number of columns in the primary key as well as non-primary key;
	 * cannot be null or empty;
	 * key set must be disjoint with {@link #NODE_ID_COLUMN_NAME_EDGE_SINK_NODE_ID_COLUMN_NAME_MAP};
	 */
	public static final DataReproducibleParameter<DataTableColumnNameKeyValueLinkedHashMap> NODE_ID_COLUMN_NAME_EDGE_SOURCE_NODE_ID_COLUMN_NAME_MAP =
			new DataReproducibleParameter<>(DataTableColumnNameKeyValueLinkedHashMap.class, new SimpleName("nodeIDColumnNameEdgeSourceNodeIDColumnNameMap"), VfNotes.makeVisframeDefinedVfNotes(), "nodeIDColumnNameEdgeSourceNodeIDColumnNameMap", true, null, null);
	
	/**
	 * parameter for a map from input edge record column name to the column name of the node id columns in the input node record data for sink node ID columns;
	 * cannot be null or empty;
	 * the given column set is independent from the primary key column set of the input data table; thus can contain none or any number of columns in the primary key as well as non-primary key;
	 * key set must be disjoint with {@link #NODE_ID_COLUMN_NAME_EDGE_SOURCE_NODE_ID_COLUMN_NAME_MAP};
	 */
	public static final DataReproducibleParameter<DataTableColumnNameKeyValueLinkedHashMap> NODE_ID_COLUMN_NAME_EDGE_SINK_NODE_ID_COLUMN_NAME_MAP =
			new DataReproducibleParameter<>(DataTableColumnNameKeyValueLinkedHashMap.class, new SimpleName("nodeIDColumnNameEdgeSinkNodeIDColumnNameMap"), VfNotes.makeVisframeDefinedVfNotes(), "nodeIDColumnNameEdgeSinkNodeIDColumnNameMap", true, null, null);
	
	
	/**
	 * TODO ????
	 * parameter for whether to add source/sink vertex from the input edge data table but not found in the input vertex data table to the output Graph data's vertex record data;
	 */
	public static final BooleanParameter TO_ADD_DISCOVERED_VERTEX_FROM_INPUT_EDGE_DATA_TABLE =
			new BooleanParameter(new SimpleName("toAddDiscoveredVertexFromInputEdgeDataTable"), VfNotes.makeVisframeDefinedVfNotes(), "toAddDiscoveredVertexFromInputEdgeDataTable", true, null, false);
	
	/////
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap;
	/**
	 * return all Parameters defined at the GenericSQLOperation level
	 * @return
	 */
	protected static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap(){
		if(levelSpecificParameterNameMap==null) {
			levelSpecificParameterNameMap=new HashMap<>();
			
			levelSpecificParameterNameMap.put(INPUT_NODE_RECORD_METADATAID.getName(), INPUT_NODE_RECORD_METADATAID);
			levelSpecificParameterNameMap.put(INPUT_NODE_DATA_TABLE_COLUMN_SET_AS_NODE_ID.getName(), INPUT_NODE_DATA_TABLE_COLUMN_SET_AS_NODE_ID);
			levelSpecificParameterNameMap.put(INPUT_NODE_DATA_TABLE_COLUMN_SET_AS_ADDITIONAL_FEATURE.getName(), INPUT_NODE_DATA_TABLE_COLUMN_SET_AS_ADDITIONAL_FEATURE);
			
			levelSpecificParameterNameMap.put(NODE_ID_COLUMN_NAME_EDGE_SOURCE_NODE_ID_COLUMN_NAME_MAP.getName(), NODE_ID_COLUMN_NAME_EDGE_SOURCE_NODE_ID_COLUMN_NAME_MAP);
			levelSpecificParameterNameMap.put(NODE_ID_COLUMN_NAME_EDGE_SINK_NODE_ID_COLUMN_NAME_MAP.getName(), NODE_ID_COLUMN_NAME_EDGE_SINK_NODE_ID_COLUMN_NAME_MAP);
		
			levelSpecificParameterNameMap.put(TO_ADD_DISCOVERED_VERTEX_FROM_INPUT_EDGE_DATA_TABLE.getName(), TO_ADD_DISCOVERED_VERTEX_FROM_INPUT_EDGE_DATA_TABLE);
			
		}
		return levelSpecificParameterNameMap;
	}
	
	
	
	////////////////////////////////
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap;
	
	/**
	 * constructor
	 * @param operationLevelParameterObjectValueMap
	 * @param buildGraphFromExistingRecordOperationLevelParameterObjectValueMap
	 */
	public BuildGraphFromTwoExistingRecordOperation(			
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			
			Map<SimpleName, Object> buildGraphFromExistingRecordOperationBaseLevelParameterObjectValueMap,
			
			Map<SimpleName, Object> buildGraphFromExistingRecordOperationLevelParameterObjectValueMap,
			
			boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent
			) {
		super(operationLevelParameterObjectValueMap, buildGraphFromExistingRecordOperationBaseLevelParameterObjectValueMap);
		
		//basic validations
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			if(!buildGraphFromExistingRecordOperationLevelParameterObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given buildGraphFromExistingRecordOperationLevelParameterObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		
		this.levelSpecificParameterObjectValueMap = buildGraphFromExistingRecordOperationLevelParameterObjectValueMap;
		
		///////////////check value constraints
		this.validateParametersValueConstraints(toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent);
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
				throw new IllegalArgumentException("invalid value object found for buildGraphFromExistingRecordOperationLevelParameterObjectValueMap:"+parameterName);
			}
		}
		//3. additional inter-parameter constraints involving parameters at this level
		//TODO
		//then validate constraints directly depending on the value object of input parameters but not implemented in the Parameter<?> object
				//INPUT_NODE_RECORD_METADATAID and INPUT_EDGE_RECORD_METADATAID must be of RECORD data type and have different record name
				
				//INPUT_NODE_DATA_TABLE_COLUMN_SET_AS_NODE_ID value set must be non-empty;
				
				//INPUT_NODE_DATA_TABLE_COLUMN_SET_AS_ADDITIONAL_FEATURE value set can be empty, and must be disjoint with INPUT_NODE_DATA_TABLE_COLUMN_SET_AS_NODE_ID
				
				//INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_NODE_ID, INPUT_EDGE_DATA_TABLE_COLUMN_MAP_TO_SOURCE_NODE_ID_COLUMN and INPUT_EDGE_DATA_TABLE_COLUMN_MAP_TO_SINK_NODE_ID_COLUMN value set cannot be empty
				//INPUT_EDGE_DATA_TABLE_COLUMN_MAP_TO_SOURCE_NODE_ID_COLUMN and INPUT_EDGE_DATA_TABLE_COLUMN_MAP_TO_SINK_NODE_ID_COLUMN value set must be disjoint
				
				//if boolean value of INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_NODE_ID is true, 
				//value set of INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_NODE_ID must be equal to union of value sets of INPUT_EDGE_DATA_TABLE_COLUMN_MAP_TO_SOURCE_NODE_ID_COLUMN and INPUT_EDGE_DATA_TABLE_COLUMN_MAP_TO_SINK_NODE_ID_COLUMN
				
				//if boolean value of INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_NODE_ID is false
				//value set of INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_NODE_ID must be disjoint with value sets of INPUT_EDGE_DATA_TABLE_COLUMN_MAP_TO_SOURCE_NODE_ID_COLUMN and INPUT_EDGE_DATA_TABLE_COLUMN_MAP_TO_SINK_NODE_ID_COLUMN
				
				
				//OUTPUT_NODE_RECORD_DATA_NAME and OUTPUT_EDGE_RECORD_DATA_NAME must be different
				//OUTPUT_NODE_DATA_TABLE_NAME and OUTPUT_EDGE_DATA_TABLE_NAME must be different
						
				
	}
	///////////////////////////////////////////////////////////////
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
	
	/**
	 * 
	 */
	@Override
	public Map<SimpleName, Object> getAllParameterNameValueObjectMapOfCurrentAndAboveLevels() {
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.putAll(super.getAllParameterNameMapOfCurrentAndAboveLevels());
		ret.putAll(this.levelSpecificParameterObjectValueMap);
		
		return ret;
	}
	
	
	@Override
	public void setParameterValueObject(SimpleName parameterName, Object value) {
		if(levelSpecificParameterNameMap().containsKey(parameterName)) {
			this.setLevelSpecificParameterValueObject(parameterName, value);
		}else {//not level specific parameter
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
	
	
	//////////////////////////////////////////
	///delegate gettor methods to each parameter's type
	public MetadataID getInputNodeRecordDataMetadataID() {
		return (MetadataID)this.levelSpecificParameterObjectValueMap.get(INPUT_NODE_RECORD_METADATAID.getName());
	}
	
	public DataTableColumnNameLinkedHashSet getInputNodeDataTableColumnSetAsNodeID(){
		return (DataTableColumnNameLinkedHashSet)this.levelSpecificParameterObjectValueMap.get(INPUT_NODE_DATA_TABLE_COLUMN_SET_AS_NODE_ID.getName());
	}
	
	public DataTableColumnNameLinkedHashSet getinputNodeDataTableColumnSetAsAdditionalFeature(){
		return (DataTableColumnNameLinkedHashSet)this.levelSpecificParameterObjectValueMap.get(INPUT_NODE_DATA_TABLE_COLUMN_SET_AS_ADDITIONAL_FEATURE.getName());
	}
	
	
	public DataTableColumnNameKeyValueLinkedHashMap getNodeIDColumnNameEdgeSourceNodeIDColumnNameMap(){
		return (DataTableColumnNameKeyValueLinkedHashMap)this.levelSpecificParameterObjectValueMap.get(NODE_ID_COLUMN_NAME_EDGE_SOURCE_NODE_ID_COLUMN_NAME_MAP.getName());
	}
	
	public DataTableColumnNameKeyValueLinkedHashMap getNodeIDColumnNameEdgeSinkNodeIDColumnNameMap(){
		return (DataTableColumnNameKeyValueLinkedHashMap)this.levelSpecificParameterObjectValueMap.get(NODE_ID_COLUMN_NAME_EDGE_SINK_NODE_ID_COLUMN_NAME_MAP.getName());
	}
	
	
	////////////////////////////
	@Override
	public boolean isToAddDiscoveredVertexFromInputEdgeDataTable() {
		return (Boolean)this.levelSpecificParameterObjectValueMap.get(TO_ADD_DISCOVERED_VERTEX_FROM_INPUT_EDGE_DATA_TABLE.getName());
	}
	
	
	//////////////////

	@Override
	public SimpleName getOperationTypeName() {
		return TYPE_NAME;
	}

	
	@Override
	public VfNotes getOperationTypeNotes() {
		return TYPE_NOTES;
	}
	
	@Override
	public Set<MetadataID> getInputMetadataIDSet() {
		if(this.inputMetadataIDSet==null) {
			this.inputMetadataIDSet = new HashSet<>();
			
			MetadataID inputNodeRecordMetadataID = this.getInputNodeRecordDataMetadataID();
			MetadataID inputEdgeRecordMetadataID = this.getInputEdgeRecordDataMetadataID();
			
			this.inputMetadataIDSet.add(inputNodeRecordMetadataID);
			this.inputMetadataIDSet.add(inputEdgeRecordMetadataID);
		}
		
		return inputMetadataIDSet;
	}
	
	
	
	
	
	/**
	 * {@inheritDoc}
	 * input node/edge record data's columns as the structural features of the graph data;
	 */
	@Override
	public Map<MetadataID, Set<DataTableColumnName>> getInputRecordMetadataIDInputColumnNameSetMap() {
		Map<MetadataID, Set<DataTableColumnName>> inputRecordMetadataIDIndependentInputColumnNameSetMap = new HashMap<>();
		
		Set<DataTableColumnName> inputNodeRecordDataIndependentInputColumnNameSet = new HashSet<>();
		inputNodeRecordDataIndependentInputColumnNameSet.addAll(this.getInputNodeDataTableColumnSetAsNodeID().getSet());
		inputNodeRecordDataIndependentInputColumnNameSet.addAll(this.getinputNodeDataTableColumnSetAsAdditionalFeature().getSet());
		
		Set<DataTableColumnName> inputEdgeRecordDataIndependentInputColumnNameSet = new HashSet<>();
		inputEdgeRecordDataIndependentInputColumnNameSet.addAll(this.getInputEdgeDataTableColumnSetAsEdgeID().getSet());
		inputEdgeRecordDataIndependentInputColumnNameSet.addAll(this.getInputEdgeDataTableColumnSetAsAdditionalFeature().getSet());
		if(this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets()) {
			inputEdgeRecordDataIndependentInputColumnNameSet.addAll(this.getNodeIDColumnNameEdgeSinkNodeIDColumnNameMap().getMap().values());
			inputEdgeRecordDataIndependentInputColumnNameSet.addAll(this.getNodeIDColumnNameEdgeSourceNodeIDColumnNameMap().getMap().values());
		}
		
		//directed type indicator column in the input edge data table;
		if(this.hasDirectedTypeIndicatorColumn()) {
			inputEdgeRecordDataIndependentInputColumnNameSet.add(this.getDirectedTypeIndicatorColumnName());
		}
		
		inputRecordMetadataIDIndependentInputColumnNameSetMap.put(this.getInputNodeRecordDataMetadataID(), inputNodeRecordDataIndependentInputColumnNameSet);
		inputRecordMetadataIDIndependentInputColumnNameSetMap.put(this.getInputEdgeRecordDataMetadataID(), inputEdgeRecordDataIndependentInputColumnNameSet);
		
		return inputRecordMetadataIDIndependentInputColumnNameSetMap;
	}
	
//	
//	@Override
//	public Set<DataTableSchemaID> getOutputDataTableSchemaIDSet() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
	///////////////////////////////////////////////////////
	
	/**
	 * reproduce and return a BuildGraphFromTwoExistingRecordOperation based on this one
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @throws SQLException 
	 */
	@Override
	public BuildGraphFromTwoExistingRecordOperation reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
		return new BuildGraphFromTwoExistingRecordOperation(
//				true,///////////
				this.reproduceAbstractOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex), 
				this.reproduceBuildGraphFromExistingRecordOperationBaseLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceBuildGraphFromExistingRecordOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				false //toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent should be false if the operation is reproduced;
				);
	}
	
	
	/**
	 * reproduce and return the values for parameters at {@link BuildGraphFromExistingRecordOperation} level;
	 * 
	 * note that the returned map should contain all the parameters at this level, including those with {@link Parameter#isInputDataTableContentDependent()} returning false and true;
	 *
	 * 
	 * note that for parameters with {@link Parameter#isInputDataTableContentDependent()} returning false,
	 * the reproduced value should be normally generated;
	 * 
	 * for parameters with {@link Parameter#isInputDataTableContentDependent()} returning true, 
	 * the value for the parameter should be null;
	 * 
	 * @param visSchemeApplierArchive
	 * @param copyIndex
	 * @return
	 * @throws SQLException 
	 */
	public Map<SimpleName, Object> reproduceBuildGraphFromExistingRecordOperationLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException{
		//first find out the copy index of the VCCLNode of input node record and edge record data of this operation;
		int inputEdgeRecordDataCopyIndex = VSAArchiveReproducerAndInserter.getAppliedArchive().lookupCopyIndexOfOperationInputMetadata(
				this.getID(), copyIndex, this.getInputEdgeRecordDataMetadataID());
		int inputNodeRecordDataCopyIndex = VSAArchiveReproducerAndInserter.getAppliedArchive().lookupCopyIndexOfOperationInputMetadata(
				this.getID(), copyIndex, this.getInputNodeRecordDataMetadataID());
		
		//node
		MetadataID inputNodeRecordDataMetadataID = 
				this.getInputNodeRecordDataMetadataID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, inputNodeRecordDataCopyIndex);
		
		//re-implement the reproduce of data table column
		DataTableColumnNameLinkedHashSet inputNodeDataTableColumnSetAsNodeID = 
				this.getInputNodeDataTableColumnSetAsNodeID().reproduce(
						VSAArchiveReproducerAndInserter, this.getInputNodeRecordDataMetadataID(), inputNodeRecordDataCopyIndex);
		
		DataTableColumnNameLinkedHashSet inputNodeDataTableColumnSetAsAdditionalFeature = 
				this.getinputNodeDataTableColumnSetAsAdditionalFeature().reproduce(
						VSAArchiveReproducerAndInserter, this.getInputNodeRecordDataMetadataID(), inputNodeRecordDataCopyIndex);
		
		
		//DataTableColumnNameKeyValueMap must be reproduced in this class rather than in DataTableColumnNameKeyValueMap class;
		//since key and value DataTableColumnName may belong to different owner recordMetadataIDs
		LinkedHashMap<DataTableColumnName,DataTableColumnName> reproducedNodeIDColumnNameEdgeSourceNodeIDColumnNameMap = new LinkedHashMap<>();
		for(DataTableColumnName inputEdgeColumnName:this.getNodeIDColumnNameEdgeSourceNodeIDColumnNameMap().getMap().keySet()) {
			DataTableColumnName sourceNodeIDColumnName = this.getNodeIDColumnNameEdgeSourceNodeIDColumnNameMap().getMap().get(inputEdgeColumnName);
			reproducedNodeIDColumnNameEdgeSourceNodeIDColumnNameMap.put(
					inputEdgeColumnName.reproduce(VSAArchiveReproducerAndInserter, this.getInputEdgeRecordDataMetadataID(), inputEdgeRecordDataCopyIndex),
					sourceNodeIDColumnName.reproduce(VSAArchiveReproducerAndInserter, this.getInputNodeRecordDataMetadataID(), inputNodeRecordDataCopyIndex));
		}
		DataTableColumnNameKeyValueLinkedHashMap reproducedNodeIDColumnNameEdgeSourceNodeIDColumnNameKeyValueMap = 
				new DataTableColumnNameKeyValueLinkedHashMap(reproducedNodeIDColumnNameEdgeSourceNodeIDColumnNameMap);
		
		LinkedHashMap<DataTableColumnName,DataTableColumnName> reproducedNodeIDColumnNameEdgeSinkNodeIDColumnNameMap = new LinkedHashMap<>();
		for(DataTableColumnName inputEdgeColumnName:this.getNodeIDColumnNameEdgeSinkNodeIDColumnNameMap().getMap().keySet()) {
			DataTableColumnName sourceNodeIDColumnName = this.getNodeIDColumnNameEdgeSinkNodeIDColumnNameMap().getMap().get(inputEdgeColumnName);
			reproducedNodeIDColumnNameEdgeSinkNodeIDColumnNameMap.put(
					inputEdgeColumnName.reproduce(VSAArchiveReproducerAndInserter, this.getInputEdgeRecordDataMetadataID(), inputEdgeRecordDataCopyIndex),
					sourceNodeIDColumnName.reproduce(VSAArchiveReproducerAndInserter, this.getInputNodeRecordDataMetadataID(), inputNodeRecordDataCopyIndex));
		}
		
		DataTableColumnNameKeyValueLinkedHashMap reproducedNodeIDColumnNameEdgeSinkNodeIDColumnNameKeyValueMap = 
				new DataTableColumnNameKeyValueLinkedHashMap(reproducedNodeIDColumnNameEdgeSinkNodeIDColumnNameMap);
		
		boolean toAddDiscoveredVertexFromInputEdgeDataTable = this.isToAddDiscoveredVertexFromInputEdgeDataTable();
		
		
		Map<SimpleName, Object> ret = new HashMap<>();
	
		ret.put(INPUT_NODE_RECORD_METADATAID.getName(), inputNodeRecordDataMetadataID);
		ret.put(INPUT_NODE_DATA_TABLE_COLUMN_SET_AS_NODE_ID.getName(), inputNodeDataTableColumnSetAsNodeID);
		ret.put(INPUT_NODE_DATA_TABLE_COLUMN_SET_AS_ADDITIONAL_FEATURE.getName(), inputNodeDataTableColumnSetAsAdditionalFeature);
	
		ret.put(NODE_ID_COLUMN_NAME_EDGE_SOURCE_NODE_ID_COLUMN_NAME_MAP.getName(), reproducedNodeIDColumnNameEdgeSourceNodeIDColumnNameKeyValueMap);
		ret.put(NODE_ID_COLUMN_NAME_EDGE_SINK_NODE_ID_COLUMN_NAME_MAP.getName(), reproducedNodeIDColumnNameEdgeSinkNodeIDColumnNameKeyValueMap);
		
		ret.put(TO_ADD_DISCOVERED_VERTEX_FROM_INPUT_EDGE_DATA_TABLE.getName(), toAddDiscoveredVertexFromInputEdgeDataTable);
		return ret;
	}

	
	///////////////////////////////////////////
	///facilitating methods for call() method
	
	@Override
	protected void makeRecordToGraphReader() throws SQLException {
		//RecordToGraphReader
		RecordToGraphReaderFactory recordToGraphReaderFactory = new RecordToGraphReaderFactory(
				this.getHostVisProjectDBContext(),
				true, //@param hasVertexDataSourceRecordData
				this.getInputNodeRecordDataMetadataID(),
				this.getInputEdgeRecordDataMetadataID(),
				true, //toFilterOutDuplicates during the reader step
				this.getInputNodeDataTableColumnSetAsNodeID().getSet(),
				this.getinputNodeDataTableColumnSetAsAdditionalFeature().getSet(),
				this.getInputEdgeDataTableColumnSetAsEdgeID().getSet(),
				this.getInputEdgeDataTableColumnSetAsAdditionalFeature().getSet(),
				this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets(),
				this.getNodeIDColumnNameEdgeSourceNodeIDColumnNameMap().getMap(),
				this.getNodeIDColumnNameEdgeSinkNodeIDColumnNameMap().getMap()
				);
		
		if(this.hasDirectedTypeIndicatorColumn()) {
			recordToGraphReader = recordToGraphReaderFactory.build(
					this.getDirectedTypeIndicatorColumnName(), 
					this.getDirectedIndicatorColumnStringValueDirectedTypeMap().getMap(), 
					this.getDefaultDirectedType());
		}else {
			recordToGraphReader = recordToGraphReaderFactory.build(this.getDefaultDirectedType());
		}
		
		recordToGraphReader.initialize();
	}


	////////////////////////////
	
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
		if (!(obj instanceof BuildGraphFromTwoExistingRecordOperation))
			return false;
		BuildGraphFromTwoExistingRecordOperation other = (BuildGraphFromTwoExistingRecordOperation) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
}
