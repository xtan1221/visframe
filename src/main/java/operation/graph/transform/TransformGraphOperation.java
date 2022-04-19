package operation.graph.transform;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
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
import generic.graph.builder.GraphBuilder;
import generic.graph.builder.JGraphT.JGraphTGraphBuilder;
import generic.graph.populator.GraphDataTablePopulator;
import generic.graph.populator.GraphDataTablePopulatorImpl;
import generic.graph.reader.project.GenericGraphMetadataDataReader;
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
import operation.graph.SingleGenericGraphAsInputOperation;
import operation.parameter.Parameter;
import operation.parameter.ReproducibleParameter;
import operation.parameter.SimpleReproducibleParameter;
import rdb.table.data.DataTableColumnName;


/**
 * operation that transform a GraphDataMetadata with DataType GRAPH or VfTREE with GraphMetadataType A to another GraphDataMetadata with DataType=GRAPH of GraphMetadataType B with a GraphTypeEnforcer;
 * 
 * input graph type can be either GRAPH or vfTREE
 * 
 * output graph type can only be GRAPH
 * 
 * there is no constraints regarding the input generic graph data;
 * 
 * 
 * NOTE that this operation does not check if the input graph's observed GraphMetadataType is already consistent with provided GraphTypeEnforcer (thus transform will change nothing) or not;
 * and will always generate output graph;
 * 
 * @author tanxu
 *
 */
public final class TransformGraphOperation extends SingleGenericGraphAsInputOperation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3603185925707646761L;
	
	////////////////////////////
	public static final SimpleName TYPE_NAME = new SimpleName("TransformGraphOperation");
	public static final VfNotes TYPE_NOTES = VfNotes.makeVisframeDefinedVfNotes();
	
	
	////////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildTransformGraphOperationLevelSpecificParameterNameValueObjectMap(
			GraphTypeEnforcer graphTypeEnforcer,
			MetadataName outputGraphDataName
			){
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.put(GRAPH_TYPE_ENFORCER.getName(), graphTypeEnforcer);
		ret.put(OUTPUT_GRAPH_DATA_ID.getName(), new MetadataID(outputGraphDataName, DataType.GRAPH));
		
		return ret;
	}
	
	/////////////////////////////////////
	/**
	 * parameter for the graph type enforcer for the output graph data
	 */
	public static final SimpleReproducibleParameter<GraphTypeEnforcer> GRAPH_TYPE_ENFORCER =
			new SimpleReproducibleParameter<>(GraphTypeEnforcer.class, new SimpleName("graphTypeEnforcer"), VfNotes.makeVisframeDefinedVfNotes(), "graphTypeEnforcer", true, null, null);
	public GraphTypeEnforcer getGraphTypeEnforcer() {
		return (GraphTypeEnforcer)this.levelSpecificParameterObjectValueMap.get(GRAPH_TYPE_ENFORCER.getName());
	}
	
	/**
	 * parameter for the single output graph data name
	 */
	public static final ReproducibleParameter<MetadataID> OUTPUT_GRAPH_DATA_ID =
			new ReproducibleParameter<>(MetadataID.class, new SimpleName("outputGraphDataID"), VfNotes.makeVisframeDefinedVfNotes(), "Output graph data ID", true, 
					m->{return m.getDataType()==DataType.GRAPH;}, null);
	public MetadataID getOutputGraphDataID() {
		return (MetadataID) this.levelSpecificParameterObjectValueMap.get(OUTPUT_GRAPH_DATA_ID.getName());
	}
	
	///////////////////////
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap;
	/**
	 * return all Parameters defined at the GenericSQLOperation level
	 * @return
	 */
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap(){
		if(levelSpecificParameterNameMap==null) {
			levelSpecificParameterNameMap = new LinkedHashMap<>();
			
			levelSpecificParameterNameMap.put(GRAPH_TYPE_ENFORCER.getName(), GRAPH_TYPE_ENFORCER);
			
			levelSpecificParameterNameMap.put(OUTPUT_GRAPH_DATA_ID.getName(), OUTPUT_GRAPH_DATA_ID);
		}
		return levelSpecificParameterNameMap;
	}
	
	////////////////////////////////
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap;
	
	
	/**
	 * constructor
	 * @param operationLevelParameterObjectValueMap
	 * @param singleGenericGraphAsInputOperationLevelParameterObjectValueMap
	 * @param transformGraphOperationLevelParameterObjectValueMap
	 */
	public TransformGraphOperation(
//			boolean resultedFromReproducing,
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			Map<SimpleName, Object> singleGenericGraphAsInputOperationLevelParameterObjectValueMap,
			Map<SimpleName, Object> transformGraphOperationLevelParameterObjectValueMap,
			boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent
			) {
		super(operationLevelParameterObjectValueMap, singleGenericGraphAsInputOperationLevelParameterObjectValueMap);
		//validations
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			if(!transformGraphOperationLevelParameterObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given transformGraphOperationLevelParameterObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		
		
		this.levelSpecificParameterObjectValueMap = transformGraphOperationLevelParameterObjectValueMap;
		
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
				throw new IllegalArgumentException("invalid value object found for singleGenericGraphAsInputOperationLevelParameterObjectValueMap:"+parameterName);
			}
		}
		//3. additional inter-parameter constraints involving parameters at this level
		//TODO

		//then validate constraints directly depending on the value object of input parameters but not implemented in the Parameter<?> object
		//OUTPUT_NODE_RECORD_DATA_NAME and OUTPUT_EDGE_RECORD_DATA_NAME must be different
		//OUTPUT_NODE_DATA_TABLE_NAME and OUTPUT_EDGE_DATA_TABLE_NAME must be different
		
		
	}
	
	
	////////////////////////
	@Override
	public SimpleName getOperationTypeName() {
		return TYPE_NAME;
	}
	

	@Override
	public VfNotes getOperationTypeNotes() {
		return TYPE_NOTES;
	}
	
	///////////////////////////////////////////////////////////
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
	/////////////////////////////////////////////////////////
	
	
	@Override
	public Set<MetadataID> getOutputMetadataIDSet() {
		Set<MetadataID> ret = new HashSet<>();
		ret.add(this.getOutputGraphDataID());
		return ret;
	}
	
//	/**
//	 * input graph can be of any generic graph type
//	 */
//	@Override
//	public boolean inputGraphMustBeOfVfTreeType() {
//		return false;
//	}
	
	/**
	 * no independent input columns for node/edge record data for {@link TransformGraphOperation}, thus return empty map;
	 * ???
	 * return all the additional features of node and edge record data?
	 * TODO - revisit when working on reproduce
	 */
	@Override
	public Map<MetadataID, Set<DataTableColumnName>> getInputRecordMetadataIDInputColumnNameSetMap() {
		return new HashMap<>();
	}
	
	
	/////////////////////////////////////////////
	
	/**
	 * reproduce and return a new TransformGraphOperation
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public TransformGraphOperation reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
		return new TransformGraphOperation(
//				true, //!!!!!!!!!!!!!!
				this.reproduceAbstractOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceSingleGenericGraphAsInputOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceTransformGraphOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				false //toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent should be false if the operation is reproduced;
				);
	}
	
	
	/**
	 * reproduce and return the values for parameters at {@link TransformGraphOperation} level;
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
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @return
	 * @throws SQLException
	 */
	private Map<SimpleName, Object> reproduceTransformGraphOperationLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException{
		GraphTypeEnforcer reproducedGraphTypeEnforcer = this.getGraphTypeEnforcer().reproduce();
		
		MetadataID reproducedOutputGraphDataID = this.getOutputGraphDataID().reproduce(
				hostVisProjctDBContext, VSAArchiveReproducerAndInserter,copyIndex);//copy index of output Metadata is the same with the operation!
		
		//////////////
		Map<SimpleName, Object> ret = new HashMap<>();
		
		ret.put(GRAPH_TYPE_ENFORCER.getName(),reproducedGraphTypeEnforcer);
		
		ret.put(OUTPUT_GRAPH_DATA_ID.getName(), reproducedOutputGraphDataID);
		
		return ret;
	}
	
	
	/////////////////////////////////////////////////////////////////
	private transient GenericGraphMetadataDataReader graphMetadataDataReader;
	private transient GraphNameBuilder graphNameBuilder;
	private transient GraphBuilder graphBuilder;
	private transient GraphDataTablePopulator graphPopulator;
	
	/**
	 * perform the operation; see {@link BuildGraphFromTwoExistingRecordOperation_pre#call()} for example implementation;
	 * 
	 * 0. check if host {@link VisProjectDBContext} is set;
	 * 
//	 * 1. invoke {@link #validateParametersValueConstraints()}
	 * 
	 * 2. build a {@link GenericGraphMetadataDataReader} to read the input generic graph data of either {@link DataType#GRAPH} or {@link DataType#vfTREE} type;
	 * 
	 * 3. build the output {@link GraphDataMetadata}'s vertex and edge record data name and data table names;
	 * 
	 * 4. build {@link GraphBuilder} with the {@link GenericGraphMetadataDataReader} and the {@link GraphTypeEnforcer}
	 * 
	 * 5. build a {@link GraphDataTablePopulator} to populate the vertex and edge data tables;
	 * 		note that GraphDataTablePopulator will build and insert output {@link GraphDataMetadata}'s vertex and edge record data table schema;
	 * 
	 * 6. build and insert the output Metadata into Metadata management table 
	 * 		including the {@link GraphDataTablePopulator} and the {@link RecordDataMetadata} for vertex and edge;
	 * 
	 * 7. insert this {@link Operation} into the Operation management table;
	 * @throws SQLException 
	 * @throws IOException 
	 * 
	 */
	@Override
	public StatusType call() throws SQLException, IOException {
		//0
		if(this.getHostVisProjectDBContext()==null) {
			throw new UnsupportedOperationException("cannot perform operation when host VisProjectDBContext is null!");
		}
		//1 
//		this.validateParametersValueConstraints();
		
		
		//2
		this.makeGraphMetadataDataReader();
		
		//3
		//name builder for the vertex and edge record data names and data table names;
		graphNameBuilder = new GraphNameBuilder(this.getHostVisProjectDBContext(), this.getOutputGraphDataID().getName());
		
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
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void makeGraphMetadataDataReader() throws SQLException, IOException{
		this.graphMetadataDataReader = new GenericGraphMetadataDataReader(this.getHostVisProjectDBContext(), this.getInputGenericGraphMetadataID());
		this.graphMetadataDataReader.initialize();
	}
	
	/**
	 * 
	 * @throws IOException
	 * @throws SQLException
	 */
	private void createAndPerformGraphBuilder() throws IOException, SQLException {
		graphBuilder = new JGraphTGraphBuilder(
				this.graphMetadataDataReader, this.getGraphTypeEnforcer(), 
				false//this.isToAddDiscoveredVertexFromInputEdgeDataTable()
				);
		
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
				this.getOutputGraphDataID(),
				null,
				this.graphPopulator.getVertexDataTableSchema(),
				true
				);
		RecordDataMetadata edgeRecordMetadata = new RecordDataMetadata(
				this.graphNameBuilder.getEdgeRecordMetadataName(), VfNotes.makeVisframeDefinedVfNotes(),
				SourceType.STRUCTURAL_COMPONENT,
				this.getOutputGraphDataID(),
				null,
				this.graphPopulator.getEdgeDataTableSchema(),
				false
				);
		
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().insert(nodeRecordMetadata);
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().insert(edgeRecordMetadata);
//				MetadataName name, VfNotes notes, 
//				SourceType sourceType, OperationID sourceOperationID,
//				MetadataName nodeRecordDataName, 
//				MetadataName edgeRecordDataName,
//				VfTreeNodeFeature graphNodeFeature, 
//				VfTreeEdgeFeature graphEdgeFeature,
//				//////
//				Integer bootstrapIteration
		GraphDataMetadata graphMetadata = new GraphDataMetadata(
				this.getOutputGraphDataID().getName(), VfNotes.makeVisframeDefinedVfNotes(),
				SourceType.RESULT_FROM_OPERATION, this.getID(),
				this.graphNameBuilder.getVertexRecordMetadataName(),
				this.graphNameBuilder.getEdgeRecordMetadataName(),
				this.graphBuilder.makeGraphVertexFeature(),
				this.graphBuilder.makeGraphEdgeFeature(),
				this.graphBuilder.getOberservedType()
				);
		
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().insert(graphMetadata);
	}
	
	
	///////////////////////////////////
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
		if (!(obj instanceof TransformGraphOperation))
			return false;
		TransformGraphOperation other = (TransformGraphOperation) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
	
	
	
}
