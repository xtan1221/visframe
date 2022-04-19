package operation.graph.build;

import java.io.IOException;
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
import context.project.process.logtable.StatusType;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import generic.graph.DirectedType;
import generic.graph.builder.GraphBuilder;
import generic.graph.builder.JGraphT.JGraphTGraphBuilder;
import generic.graph.populator.GraphDataTablePopulator;
import generic.graph.populator.GraphDataTablePopulatorImpl;
import generic.graph.reader.project.RecordToGraphReader;
import metadata.DataType;
import metadata.MetadataID;
import metadata.MetadataName;
import metadata.SourceType;
import metadata.graph.GraphDataMetadata;
import metadata.graph.type.GraphTypeEnforcer;
import metadata.graph.utils.GraphNameBuilder;
import metadata.record.RecordDataMetadata;
import operation.AbstractOperation;
import operation.Operation;
import operation.graph.utils.DirectedColumnIndicatorStringValueDirectedTypeMap;
import operation.parameter.DataReproducibleParameter;
import operation.parameter.MiscTypeParameter;
import operation.parameter.Parameter;
import operation.parameter.ReproducibleParameter;
import operation.parameter.SimpleReproducibleParameter;
import operation.parameter.primitive.BooleanParameter;
import operation.utils.DataTableColumnNameLinkedHashSet;
import rdb.table.data.DataTableColumnName;


/**
 * build a {@link GraphDataMetadata} from existing RecordDataMetadata as data source for the node and edge data of the GraphDataMetadata;
 * 
 * note that for graph data, vertex can be either stored in a separate record data (thus, additional features can be present) or together with edge record data (thus, no vertex additional features);
 * 
 * the source/sink vertex id columns of the edge record data should have the SAME data type with the id columns of the vertex;
 * 
 * @author tanxu
 */
public abstract class BuildGraphFromExistingRecordOperationBase extends AbstractOperation {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2024081752958967919L;
	
	///////////////////////////
	public static final SimpleName TYPE_NAME = new SimpleName("BuildGraphFromExistingRecordOperationBase");
	public static final VfNotes TYPE_NOTES = VfNotes.makeVisframeDefinedVfNotes();
	///////////////////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildBuildGraphFromExistingRecordOperationBaseLevelSpecificParameterNameValueObjectMap(
			MetadataID inputEdgeRecordDataMetadataID, 
			
			LinkedHashSet<DataTableColumnName> inputEdgeDataTableColumnSetAsEdgeID, 
			LinkedHashSet<DataTableColumnName> inputEdgeDataTableColumnSetAsAdditionalFeature,
			boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets, 
			
			GraphTypeEnforcer graphTypeEnforcer,
			
			boolean hasDirectedTypeIndicatorColumn,
			DataTableColumnName directedTypeIndicatorColumnName,
			HashMap<String,DirectedType> directedIndicatorColumnStringValueDirectedTypeMap,
			DirectedType defaultDirectedType,
			
			MetadataName outputGraphDataName
			){
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.put(INPUT_EDGE_RECORD_METADATAID.getName(), inputEdgeRecordDataMetadataID);
		ret.put(INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_EDGE_ID.getName(), new DataTableColumnNameLinkedHashSet(inputEdgeDataTableColumnSetAsEdgeID));
		ret.put(INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_ADDITIONAL_FEATURE.getName(), new DataTableColumnNameLinkedHashSet(inputEdgeDataTableColumnSetAsAdditionalFeature));
		
		ret.put(EDGE_ID_COLUMN_SET_DISJOINT_WITH_SOURCE_AND_SINK_NODE_ID_COLUMN_SETS.getName(), edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets);
		
		ret.put(HAS_DIRECTED_TYPE_INDICATOR_COLUMN.getName(), hasDirectedTypeIndicatorColumn);
		ret.put(DIRECTED_TYPE_INDICATOR_COLUMN_NAME.getName(), directedTypeIndicatorColumnName);
		ret.put(DIRECTED_INDICATOR_COLUMN_STRING_VALUE_DIRECTED_TYPE_MAP.getName(), new DirectedColumnIndicatorStringValueDirectedTypeMap(directedIndicatorColumnStringValueDirectedTypeMap));
		ret.put(DEFAULT_DIRECTED_TYPE.getName(), defaultDirectedType);
		
		ret.put(GRAPH_TYPE_ENFORCER.getName(), graphTypeEnforcer);
		
		ret.put(OUTPUT_GRAPH_DATA_ID.getName(), new MetadataID(outputGraphDataName, DataType.GRAPH));
		
		return ret;
	}
	
	///////////////////////////////////////////
	//////////
	/**
	 * parameter for MetadataID of the input record data for the edge record of the graph data to build;
	 * cannot be null;
	 */
	public static final ReproducibleParameter<MetadataID> INPUT_EDGE_RECORD_METADATAID =
			new ReproducibleParameter<>(MetadataID.class, new SimpleName("inputEdgeRecordDataMetadataID"), VfNotes.makeVisframeDefinedVfNotes(), "inputEdgeRecordDataMetadataID", true, 
					m->{return m.getDataType()==DataType.RECORD;},
					null);// 
	
	/**
	 * parameter for set of columns of the input record data used as EDGE ID for node record of graph to build;
	 * the given column set is independent from the primary key column set of the input data table; thus can contain none or any number of columns in the primary key as well as non-primary key;
	 * cannot be null or empty
	 */
	public static final DataReproducibleParameter<DataTableColumnNameLinkedHashSet> INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_EDGE_ID =
			new DataReproducibleParameter<>(DataTableColumnNameLinkedHashSet.class, new SimpleName("inputEdgeDataTableColumnSetAsEdgeID"), VfNotes.makeVisframeDefinedVfNotes(), "inputEdgeDataTableColumnSetAsEdgeID", true, null, null);

	/**
	 * parameter for set of columns of the input record data used as additional features for edge record of graph to build;
	 * the given column set is independent from the primary key column set of the input data table; thus can contain none or any number of columns in the primary key as well as non-primary key;
	 * note that this set can be empty, but not null; also must be disjoint with key set of edge id column set and source/sink node id column set
	 */
	public static final DataReproducibleParameter<DataTableColumnNameLinkedHashSet> INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_ADDITIONAL_FEATURE =
			new DataReproducibleParameter<>(DataTableColumnNameLinkedHashSet.class, new SimpleName("inputEdgeDataTableColumnSetAsAdditionalFeature"), VfNotes.makeVisframeDefinedVfNotes(), "inputEdgeDataTableColumnSetAsAdditionalFeature", true, null, null);
	
	/**
	 * parameter for whether the graph data to build has its edge data's EDGE ID columns disjoint with the source node ID column set and sink node ID column set;
	 *
	 * if true, the value set of {@link #INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_EDGE_ID} must be disjoint with key set of {@link #NODE_ID_COLUMN_NAME_EDGE_SOURCE_NODE_ID_COLUMN_NAME_MAP} and {@link #NODE_ID_COLUMN_NAME_EDGE_SINK_NODE_ID_COLUMN_NAME_MAP};
	 * if false, the value set of {@link #INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_EDGE_ID} must be super set of key set of {@link #NODE_ID_COLUMN_NAME_EDGE_SOURCE_NODE_ID_COLUMN_NAME_MAP} and {@link #NODE_ID_COLUMN_NAME_EDGE_SINK_NODE_ID_COLUMN_NAME_MAP};
	 */
	public static final BooleanParameter EDGE_ID_COLUMN_SET_DISJOINT_WITH_SOURCE_AND_SINK_NODE_ID_COLUMN_SETS =
			new BooleanParameter(new SimpleName("edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets"), VfNotes.makeVisframeDefinedVfNotes(), "edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets", true, null, false);
	
	/////////
	/**
	 * parameter for GraphTypeEnforcer of the output graph;
	 */
	public static final SimpleReproducibleParameter<GraphTypeEnforcer> GRAPH_TYPE_ENFORCER =
			new SimpleReproducibleParameter<>(GraphTypeEnforcer.class, new SimpleName("graphTypeEnforcer"), VfNotes.makeVisframeDefinedVfNotes(), "graphTypeEnforcer", true, null, null);
	
	
	////////edge directed type related parameters
	//if DIRECTED_TYPE_INDICATOR_COLUMN_NAME is null, DEFAULT_DIRECTED_TYPE must be non-null and DIRECTED_INDICATOR_COLUMN_STRING_VALUE_DIRECTED_TYPE_MAP must be null;
	//if DIRECTED_TYPE_INDICATOR_COLUMN_NAME is not null, DIRECTED_INDICATOR_COLUMN_STRING_VALUE_DIRECTED_TYPE_MAP must be not null;
	
	/**
	 * true if there is a column whose string value indicates the directed type of the edge in the edge data table schema;
	 */
	public static final BooleanParameter HAS_DIRECTED_TYPE_INDICATOR_COLUMN =
			new BooleanParameter(new SimpleName("hasDirectedTypeIndicatorColumn"), VfNotes.makeVisframeDefinedVfNotes(), "hasDirectedTypeIndicatorColumn", true, null, false);
	
	
	/**
	 * directed type indicator column name, must be non-null if {@link #HAS_DIRECTED_TYPE_INDICATOR_COLUMN} is true, null otherwise;
	 */
	public static final DataReproducibleParameter<DataTableColumnName> DIRECTED_TYPE_INDICATOR_COLUMN_NAME =
			new DataReproducibleParameter<>(DataTableColumnName.class, new SimpleName("directedTypeIndicatorColumnName"), VfNotes.makeVisframeDefinedVfNotes(), "directedTypeIndicatorColumnName", false, null, null);
	
	/**
	 * input data table content dependent!;
	 * 
	 * Map<String, DirectedType> directedIndicatorColStringValueDirectedTypeMap
	 * 
	 * must be non-null if{@link #HAS_DIRECTED_TYPE_INDICATOR_COLUMN} is true, null otherwise;
	 */
	public static final MiscTypeParameter<DirectedColumnIndicatorStringValueDirectedTypeMap> DIRECTED_INDICATOR_COLUMN_STRING_VALUE_DIRECTED_TYPE_MAP = 
			new MiscTypeParameter<>(DirectedColumnIndicatorStringValueDirectedTypeMap.class, new SimpleName("directedIndicatorColumnStringValueDirectedTypeMap"), VfNotes.makeVisframeDefinedVfNotes(), "directedIndicatorColumnStringValueDirectedTypeMap", false, null, null);
	
	/**
	 * default directed type;
	 * if {@link HAS_DIRECTED_TYPE_INDICATOR_COLUMN} is false, this must be non-null;
	 */
	public static final SimpleReproducibleParameter<DirectedType> DEFAULT_DIRECTED_TYPE =
			new SimpleReproducibleParameter<>(DirectedType.class, new SimpleName("defaultDirectedType"), VfNotes.makeVisframeDefinedVfNotes(), "defaultDirectedType", false, null, null);
	
	/////////////////////////////
	/**
	 * parameter for output graph data's ID
	 */
	public static final ReproducibleParameter<MetadataID> OUTPUT_GRAPH_DATA_ID =
			new ReproducibleParameter<>(MetadataID.class, new SimpleName("outputGraphDataID"), VfNotes.makeVisframeDefinedVfNotes(), "Output graph data ID", true, 
					m->{return m.getDataType()==DataType.GRAPH;}, null);
	
	
	///
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap;
	/**
	 * return all Parameters defined at the GenericSQLOperation level
	 * @return
	 */
	protected static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap(){
		if(levelSpecificParameterNameMap==null) {
			levelSpecificParameterNameMap = new HashMap<>();
			levelSpecificParameterNameMap.put(INPUT_EDGE_RECORD_METADATAID.getName(), INPUT_EDGE_RECORD_METADATAID);
			levelSpecificParameterNameMap.put(INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_EDGE_ID.getName(), INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_EDGE_ID);
			levelSpecificParameterNameMap.put(INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_ADDITIONAL_FEATURE.getName(), INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_ADDITIONAL_FEATURE);
			levelSpecificParameterNameMap.put(EDGE_ID_COLUMN_SET_DISJOINT_WITH_SOURCE_AND_SINK_NODE_ID_COLUMN_SETS.getName(), EDGE_ID_COLUMN_SET_DISJOINT_WITH_SOURCE_AND_SINK_NODE_ID_COLUMN_SETS);

			levelSpecificParameterNameMap.put(GRAPH_TYPE_ENFORCER.getName(), GRAPH_TYPE_ENFORCER);
			
			levelSpecificParameterNameMap.put(HAS_DIRECTED_TYPE_INDICATOR_COLUMN.getName(), HAS_DIRECTED_TYPE_INDICATOR_COLUMN);			
			levelSpecificParameterNameMap.put(DIRECTED_TYPE_INDICATOR_COLUMN_NAME.getName(), DIRECTED_TYPE_INDICATOR_COLUMN_NAME);
			levelSpecificParameterNameMap.put(DIRECTED_INDICATOR_COLUMN_STRING_VALUE_DIRECTED_TYPE_MAP.getName(), DIRECTED_INDICATOR_COLUMN_STRING_VALUE_DIRECTED_TYPE_MAP);
			levelSpecificParameterNameMap.put(DEFAULT_DIRECTED_TYPE.getName(), DEFAULT_DIRECTED_TYPE);
			
			
			levelSpecificParameterNameMap.put(OUTPUT_GRAPH_DATA_ID.getName(), OUTPUT_GRAPH_DATA_ID);
		}
		return levelSpecificParameterNameMap;
	}
	
	
	
	////////////////////////////////
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap;
	
	/**
	 * constructor
	 * @param operationLevelParameterObjectValueMap
	 * @param buildGraphFromExistingRecordOperationBaseLevelParameterObjectValueMap
	 */
	public BuildGraphFromExistingRecordOperationBase(
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			
			Map<SimpleName, Object> buildGraphFromExistingRecordOperationBaseLevelParameterObjectValueMap
			) {
		super(operationLevelParameterObjectValueMap);
		//basic validations
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {			
			if(!buildGraphFromExistingRecordOperationBaseLevelParameterObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given buildGraphFromExistingRecordOperationLevelParameterObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		
		this.levelSpecificParameterObjectValueMap = buildGraphFromExistingRecordOperationBaseLevelParameterObjectValueMap;
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
				throw new IllegalArgumentException("invalid value object found for buildGraphFromExistingRecordOperationLevelParameter:"+parameterName);
			}
		}
		//3. additional inter-parameter constraints involving parameters at this level
		if(this.getInputEdgeRecordDataMetadataID().getDataType()!=DataType.RECORD) {
			throw new IllegalArgumentException();
		}
		
		if(this.getInputEdgeDataTableColumnSetAsEdgeID().getSet().isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		this.getInputEdgeDataTableColumnSetAsAdditionalFeature().getSet().forEach(e->{
			if(this.getInputEdgeDataTableColumnSetAsEdgeID().getSet().contains(e)) {
				throw new IllegalArgumentException();
			}
		});
		
		//if boolean value of INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_NODE_ID is true, 
		//value set of INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_NODE_ID must be equal to union of value sets of INPUT_EDGE_DATA_TABLE_COLUMN_MAP_TO_SOURCE_NODE_ID_COLUMN and INPUT_EDGE_DATA_TABLE_COLUMN_MAP_TO_SINK_NODE_ID_COLUMN
		
		//if boolean value of INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_NODE_ID is false
		//value set of INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_NODE_ID must be disjoint with value sets of INPUT_EDGE_DATA_TABLE_COLUMN_MAP_TO_SOURCE_NODE_ID_COLUMN and INPUT_EDGE_DATA_TABLE_COLUMN_MAP_TO_SINK_NODE_ID_COLUMN
		
		//when hasDirectedTypeIndicatorColumn is true, directedTypeIndicatorColumnName and directedIndicatorColumnStringValueDirectedTypeMap cannot be null;
		if(this.hasDirectedTypeIndicatorColumn()) {
			if(this.getDirectedTypeIndicatorColumnName()==null) {
				throw new IllegalArgumentException("directedTypeIndicatorColumnName cannot be null when hasDirectedTypeIndicatorColumn is true!");
			}
			if(this.getDirectedIndicatorColumnStringValueDirectedTypeMap()==null) {
				throw new IllegalArgumentException("directedIndicatorColumnStringValueDirectedTypeMap cannot be null when hasDirectedTypeIndicatorColumn is true!");
			}
		}else {//when hasDirectedTypeIndicatorColumn is false, defaultDirectedType must be non-null
			if(this.getDefaultDirectedType()==null) {
				throw new IllegalArgumentException("hasDirectedTypeIndicatorColumn cannot be null when hasDirectedTypeIndicatorColumn is false!");
			}
		}
		
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
	
//	@Override
//	public Map<SimpleName, Parameter<?>> getLevelSpecificParameterNameMap() {
//		return buildGraphFromExistingRecordOperationBaseLevelParameterNameMap();
//	}
	
//	@Override
//	public Map<SimpleName, Object> getLevelSpecificParameterNameValueObjectMap() {
//		return Collections.unmodifiableMap(levelSpecificParameterObjectValueMap);
//	}
	
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
	
	public MetadataID getInputEdgeRecordDataMetadataID() {
		return (MetadataID)levelSpecificParameterObjectValueMap.get(INPUT_EDGE_RECORD_METADATAID.getName());
	}
	
	public DataTableColumnNameLinkedHashSet getInputEdgeDataTableColumnSetAsEdgeID(){
		return (DataTableColumnNameLinkedHashSet)levelSpecificParameterObjectValueMap.get(INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_EDGE_ID.getName());
	}
	
	public boolean isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets() {
		return (Boolean) levelSpecificParameterObjectValueMap.get(EDGE_ID_COLUMN_SET_DISJOINT_WITH_SOURCE_AND_SINK_NODE_ID_COLUMN_SETS.getName());
	}
	
	
	public DataTableColumnNameLinkedHashSet getInputEdgeDataTableColumnSetAsAdditionalFeature(){
		return (DataTableColumnNameLinkedHashSet)levelSpecificParameterObjectValueMap.get(INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_ADDITIONAL_FEATURE.getName());
	}
	
	public GraphTypeEnforcer getGraphTypeEnforcer() {
		return (GraphTypeEnforcer)levelSpecificParameterObjectValueMap.get(GRAPH_TYPE_ENFORCER.getName());
	}
	
	public boolean hasDirectedTypeIndicatorColumn() {
		return (Boolean)levelSpecificParameterObjectValueMap.get(HAS_DIRECTED_TYPE_INDICATOR_COLUMN.getName());
	}
	public DataTableColumnName getDirectedTypeIndicatorColumnName() {
		return levelSpecificParameterObjectValueMap.get(DIRECTED_TYPE_INDICATOR_COLUMN_NAME.getName())==null?
				null:(DataTableColumnName)levelSpecificParameterObjectValueMap.get(DIRECTED_TYPE_INDICATOR_COLUMN_NAME.getName());
	}
	
	public DirectedColumnIndicatorStringValueDirectedTypeMap getDirectedIndicatorColumnStringValueDirectedTypeMap() {
		return levelSpecificParameterObjectValueMap.get(DIRECTED_INDICATOR_COLUMN_STRING_VALUE_DIRECTED_TYPE_MAP.getName())==null?
				null:(DirectedColumnIndicatorStringValueDirectedTypeMap)levelSpecificParameterObjectValueMap.get(DIRECTED_INDICATOR_COLUMN_STRING_VALUE_DIRECTED_TYPE_MAP.getName());
	}
	
	public DirectedType getDefaultDirectedType() {
		return levelSpecificParameterObjectValueMap.get(DEFAULT_DIRECTED_TYPE.getName())==null?
				null:(DirectedType)levelSpecificParameterObjectValueMap.get(DEFAULT_DIRECTED_TYPE.getName());
	}
	
	public MetadataID getOutputGraphMetadataID() {
		return (MetadataID)levelSpecificParameterObjectValueMap.get(OUTPUT_GRAPH_DATA_ID.getName());
	}
	
	
	public abstract boolean isToAddDiscoveredVertexFromInputEdgeDataTable();
	
	//////////////////////////////////////////////////////	
	
	/**
	 * reproduce and return the values for parameters at {@link BuildGraphFromExistingRecordOperationBase} level;
	 * 
	 * note that the returned map should contain all the parameters at this level, including those with {@link Parameter#isInputDataTableContentDependent()} returning false and true;
	 * 
	 * note that for parameters with {@link Parameter#isInputDataTableContentDependent()} returning false,
	 * the reproduced value should be normally generated;
	 * 
	 * for parameters with {@link Parameter#isInputDataTableContentDependent()} returning true, 
	 * the value for the parameter should be null;
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @return
	 * @throws SQLException 
	 */
	protected Map<SimpleName, Object> reproduceBuildGraphFromExistingRecordOperationBaseLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		//first find out the copy index of the VCCLNode of input edge record data of this operation;
		int inputEdgeRecordDataCopyIndex = VSAArchiveReproducerAndInserter.getAppliedArchive().lookupCopyIndexOfOperationInputMetadata(
				this.getID(), copyIndex, this.getInputEdgeRecordDataMetadataID());
		//edge
		MetadataID inputEdgeRecordDataMetadataID = 
				this.getInputEdgeRecordDataMetadataID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, inputEdgeRecordDataCopyIndex);
		
		//re-implement
		DataTableColumnNameLinkedHashSet inputEdgeDataTableColumnSetAsEdgeID =
				this.getInputEdgeDataTableColumnSetAsEdgeID().reproduce(VSAArchiveReproducerAndInserter, this.getInputEdgeRecordDataMetadataID(), inputEdgeRecordDataCopyIndex);
		
		
		DataTableColumnNameLinkedHashSet inputEdgeDataTableColumnSetAsAdditionalFeature = 
				this.getInputEdgeDataTableColumnSetAsAdditionalFeature().reproduce(VSAArchiveReproducerAndInserter, this.getInputEdgeRecordDataMetadataID(), inputEdgeRecordDataCopyIndex);
		
		
		Boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets = this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets();
		
		GraphTypeEnforcer graphTypeEnforcer = this.getGraphTypeEnforcer().reproduce();
		
		boolean hasDirectedTypeIndicatorColumn = this.hasDirectedTypeIndicatorColumn();
		
		//
		DataTableColumnName directedTypeIndicatorColumnName = hasDirectedTypeIndicatorColumn?
				this.getDirectedTypeIndicatorColumnName().reproduce(VSAArchiveReproducerAndInserter, this.getInputEdgeRecordDataMetadataID(), inputEdgeRecordDataCopyIndex)
				:null;
		
		//always null since it is input data table content dependent;
		DirectedColumnIndicatorStringValueDirectedTypeMap directedIndicatorColumnStringValueDirectedTypeMap = null;
		
		DirectedType defaultDirectedType = this.getDefaultDirectedType();
		
		//output graph
		MetadataID outputGraphDataID = this.getOutputGraphMetadataID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		
		Map<SimpleName, Object> ret = new HashMap<>();
		
		ret.put(INPUT_EDGE_RECORD_METADATAID.getName(), inputEdgeRecordDataMetadataID);
		ret.put(INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_EDGE_ID.getName(), inputEdgeDataTableColumnSetAsEdgeID);
		ret.put(EDGE_ID_COLUMN_SET_DISJOINT_WITH_SOURCE_AND_SINK_NODE_ID_COLUMN_SETS.getName(), edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets);
		
		ret.put(INPUT_EDGE_DATA_TABLE_COLUMN_SET_AS_ADDITIONAL_FEATURE.getName(), inputEdgeDataTableColumnSetAsAdditionalFeature);
		
		ret.put(GRAPH_TYPE_ENFORCER.getName(), graphTypeEnforcer);
		
		ret.put(HAS_DIRECTED_TYPE_INDICATOR_COLUMN.getName(), hasDirectedTypeIndicatorColumn);
		ret.put(DIRECTED_TYPE_INDICATOR_COLUMN_NAME.getName(),directedTypeIndicatorColumnName);
		ret.put(DIRECTED_INDICATOR_COLUMN_STRING_VALUE_DIRECTED_TYPE_MAP.getName(), directedIndicatorColumnStringValueDirectedTypeMap);
		ret.put(DEFAULT_DIRECTED_TYPE.getName(), defaultDirectedType);
		
		ret.put(OUTPUT_GRAPH_DATA_ID.getName(), outputGraphDataID);
		
		return ret;
	}

	
	///////////////////////////////////////////
	@Override
	public Set<MetadataID> getOutputMetadataIDSet() {
		Set<MetadataID> ret = new HashSet<>();
		ret.add(this.getOutputGraphMetadataID());
		return ret;
	}
	
	/////////////////////////////////
	protected transient RecordToGraphReader recordToGraphReader;
	private transient GraphNameBuilder graphNameBuilder;
	private transient GraphBuilder graphBuilder;
	private transient GraphDataTablePopulator graphPopulator;
	
	/**
	 * perform this operation;
	 * 
	 * 0. check if the host VisProjectDBContext is set or not;
	 * 
//	 * 1. invoke {@link #validateParametersValueConstraints()}
	 * 
	 * 2. create a {@link RecordToGraphReader} that read in the input node and edge record data table into {@link VfGraphVertex} and {@link VfGraphEdge}
	 * 
	 * 3. build the node and edge record data name and data table name;
	 * 
	 * 4. build a {@link GraphBuilder} with the {@link RecordToGraphReader} and {@link GraphTypeEnforcer}
	 * 
	 * 5. build a {@link GraphDataTablePopulator} with the {@link GraphBuilder} to populate the node and edge record data tables
	 * 		GraphDataTablePopulator will first build the node and edge record data table schema and insert into host VisProjectDBContext
	 * 		then populate them;
	 * 6. build and insert the node and edge RecordDataMetadata and the GraphDataMetadata into metadata management table
	 * 
	 * 7. insert this {@link Operation} object into the operation management table;
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Override
	public StatusType call() throws SQLException, IOException{
		//0
		if(this.getHostVisProjectDBContext()==null) {
			throw new UnsupportedOperationException("cannot perform operation when host VisProjectDBContext is null!");
		}
		
		//1
//		this.validateParametersValueConstraints();
		
		//2
		this.makeRecordToGraphReader();
		
		//3
		//name builder for the vertex and edge record data names and data table names;
		graphNameBuilder = new GraphNameBuilder(this.getHostVisProjectDBContext(), this.getOutputGraphMetadataID().getName());
		
		//4
		this.createAndPerformGraphBuilder();
		
		//5 this step will create and insert vertex and edge data table schema before populating them;
		this.populateVertexAndEdgeDataTables();
		
		//6
		this.buildAndInsertMetadata();
		
		//7
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getOperationManager().insert(this);
		
		
		return StatusType.FINISHED;
	}
	
	/**
	 * create a {@link RecordToGraphReader}
	 * @throws SQLException
	 */
	protected abstract void makeRecordToGraphReader() throws SQLException;
	
	/**
	 * 
	 * @throws IOException
	 * @throws SQLException
	 */
	private void createAndPerformGraphBuilder() throws IOException, SQLException {
		graphBuilder = new JGraphTGraphBuilder(this.recordToGraphReader, this.getGraphTypeEnforcer(), this.isToAddDiscoveredVertexFromInputEdgeDataTable());
		
		graphBuilder.perform();
	}
	
	/**
	 * 
	 * @throws SQLException
	 */
	private void populateVertexAndEdgeDataTables() throws SQLException {
		this.graphPopulator = new GraphDataTablePopulatorImpl(
				this.getHostVisProjectDBContext(), this.graphBuilder, 
				this.graphNameBuilder.getVertexDataTableName(), this.graphNameBuilder.getEdgeDataTableName());
		
		this.graphPopulator.perform();
	}
	
	/**
	 * 
	 * @throws SQLException
	 */
	private void buildAndInsertMetadata() throws SQLException {
		RecordDataMetadata nodeRecordMetadata = new RecordDataMetadata(
				this.graphNameBuilder.getVertexRecordMetadataName(), VfNotes.makeVisframeDefinedVfNotes(),
				SourceType.STRUCTURAL_COMPONENT,
				this.getOutputGraphMetadataID(),
				null,
				this.graphPopulator.getVertexDataTableSchema(),
				true
				);
		RecordDataMetadata edgeRecordMetadata = new RecordDataMetadata(
				this.graphNameBuilder.getEdgeRecordMetadataName(), VfNotes.makeVisframeDefinedVfNotes(),
				SourceType.STRUCTURAL_COMPONENT,
				this.getOutputGraphMetadataID(),
				null,
				this.graphPopulator.getEdgeDataTableSchema(),
				false
				);
		
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().insert(nodeRecordMetadata);
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().insert(edgeRecordMetadata);
//		MetadataName name, VfNotes notes, 
//		SourceType sourceType, OperationID sourceOperationID,
//		MetadataName nodeRecordDataName, 
//		MetadataName edgeRecordDataName,
//		VfTreeNodeFeature graphNodeFeature, 
//		VfTreeEdgeFeature graphEdgeFeature,
//		//////
//		Integer bootstrapIteration
		GraphDataMetadata graphMetadata = new GraphDataMetadata(
				this.getOutputGraphMetadataID().getName(), VfNotes.makeVisframeDefinedVfNotes(),
				SourceType.RESULT_FROM_OPERATION, this.getID(),
				this.graphNameBuilder.getVertexRecordMetadataName(),
				this.graphNameBuilder.getEdgeRecordMetadataName(),
				this.graphBuilder.makeGraphVertexFeature(),
				this.graphBuilder.makeGraphEdgeFeature(),
				this.graphBuilder.getOberservedType()
				);
		
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().insert(graphMetadata);
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
		if (!(obj instanceof BuildGraphFromExistingRecordOperationBase))
			return false;
		BuildGraphFromExistingRecordOperationBase other = (BuildGraphFromExistingRecordOperationBase) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
	
}
