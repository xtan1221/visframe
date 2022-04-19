package operation.graph.layout;

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
import generic.graph.VfGraphVertex;
import metadata.DataType;
import metadata.MetadataID;
import metadata.MetadataName;
import metadata.SourceType;
import metadata.graph.GraphDataMetadata;
import metadata.record.RecordDataMetadata;
import operation.graph.InputGraphTypeBoundedOperation;
import operation.graph.layout.utils.GraphLayoutAlgoPerformerBase;
import operation.graph.layout.utils.GraphNode2DLayoutTablePopulator;
import operation.parameter.Parameter;
import operation.parameter.ReproducibleParameter;
import operation.parameter.primitive.IntegerParameter;
import rdb.table.data.DataTableSchema;


/**
 * base class for graph node layout on a 2D space;
 * output data is a RecordDataMetadata that contains the calculated x and y coordinate for each node entity in the input generic Graph data;
 * 
 * 
 * note that for some types of graph layout algorithms, the calculation is based on random number, thus each run may result in different layout;
 * 
 * 
 * @author tanxu
 * 
 */
public abstract class GraphNode2DLayoutOperationBase extends InputGraphTypeBoundedOperation {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 704603610069916325L;
	
	///////////////////////
//	public static final int DEFAULT_DRAWING_AREA_WIDTH = 100;
//	
//	public static final int DEFAULT_DRAWING_AREA_HEIGHT = 100;
	
	/////////////////////

	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildGraphNode2DLayoutOperationBaseLevelSpecificParameterNameValueObjectMap(
			int drawingAreaWidth, int drawingAreaHeight,
			MetadataName outputLayoutRecordDataName){
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		
		ret.put(DRAWING_AREA_WIDTH.getName(), drawingAreaWidth);
		ret.put(DRAWING_AREA_HEIGHT.getName(), drawingAreaHeight);
		
		ret.put(OUTPUT_LAYOUT_RECORD_DATA_ID.getName(), new MetadataID(outputLayoutRecordDataName, DataType.RECORD));
		
		return ret;
	}
	
	
	////////////////////////////////////
	/**
	 * width of the drawing area for the layout calculation;
	 * mandatory; 
	 */
	public static final IntegerParameter DRAWING_AREA_WIDTH = 
			new IntegerParameter(new SimpleName("drawingAreaWidth"), new VfNotes(), "DRAWING_AREA_WIDTH", true, null, 
					p->{return p>0;}, //must be positive
					false);//inputDataTableContentDependent
	public Integer getDrawingAreaWidth() {
		return (Integer)this.levelSpecificParameterObjectValueMap.get(DRAWING_AREA_WIDTH.getName());
	}
	
	/**
	 * height of the drawing area for the layout calculation;
	 * mandatory; 
	 */
	public static final IntegerParameter DRAWING_AREA_HEIGHT = 
			new IntegerParameter(new SimpleName("drawingAreaHeight"), new VfNotes(), "DRAWING_AREA_HEIGHT", true, null, 
					p-> p>0, //must be positive
					false);//inputDataTableContentDependent
	public Integer getDrawingAreaHeight() {
		return (Integer)this.levelSpecificParameterObjectValueMap.get(DRAWING_AREA_HEIGHT.getName());
	}
	
	/**
	 * parameter for output record data name;
	 */
	public static final ReproducibleParameter<MetadataID> OUTPUT_LAYOUT_RECORD_DATA_ID = 
			new ReproducibleParameter<>(MetadataID.class, new SimpleName("outputLayoutRecordDataID"), new VfNotes(), "Output layout record data ID", true, 
					m->{return m.getDataType()==DataType.RECORD;}, null);//
	public MetadataID getOutputLayoutRecordDataID() {
		return (MetadataID)this.levelSpecificParameterObjectValueMap.get(OUTPUT_LAYOUT_RECORD_DATA_ID.getName());
	}
	
	//////////////////////////////
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap;
	/**
	 * return all Parameters defined at the GenericSQLOperation level
	 * @return
	 */
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap(){
		if(levelSpecificParameterNameMap==null) {
			levelSpecificParameterNameMap = new LinkedHashMap<>();
			levelSpecificParameterNameMap.put(DRAWING_AREA_WIDTH.getName(), DRAWING_AREA_WIDTH);
			levelSpecificParameterNameMap.put(DRAWING_AREA_HEIGHT.getName(), DRAWING_AREA_HEIGHT);
			levelSpecificParameterNameMap.put(OUTPUT_LAYOUT_RECORD_DATA_ID.getName(), OUTPUT_LAYOUT_RECORD_DATA_ID);
		}
		return levelSpecificParameterNameMap;
	}
	
	////////////////////////////////
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap;
	
	
	protected transient RecordDataMetadata outputLayoutRecordDataMetadata;
	protected transient DataTableSchema outputLayoutDataTableSchema;
	
	
	/**
	 * constructor
	 * @param operationLevelParameterObjectValueMap
	 * @param singleGenericGraphAsInputOperationLevelParameterObjectValueMap
	 * @param graphNode2DLayoutBaseLevelParameterObjectValueMap
	 */
	protected GraphNode2DLayoutOperationBase(
//			boolean resultedFromReproducing,
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			Map<SimpleName, Object> singleGenericGraphAsInputOperationLevelParameterObjectValueMap,
			Map<SimpleName, Object> inputGraphTypeBoundedOperationLevelParameterObjectValueMap,
			
			Map<SimpleName, Object> graphNode2DLayoutBaseLevelParameterObjectValueMap
			) {
		super(operationLevelParameterObjectValueMap, singleGenericGraphAsInputOperationLevelParameterObjectValueMap, inputGraphTypeBoundedOperationLevelParameterObjectValueMap);
		//validations
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			if(!graphNode2DLayoutBaseLevelParameterObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given graphNode2DLayoutBaseLevelParameterObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		
		this.levelSpecificParameterObjectValueMap=graphNode2DLayoutBaseLevelParameterObjectValueMap;
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
		//no additional constraint validation
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
	
	/////////////////////////////////////////

	/**
	 * reproduce and return a parameter name value object map of parameters at {@link GraphNode2DLayoutBase} level
	 * 
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
	 * @return
	 * @throws SQLException 
	 */
	protected Map<SimpleName, Object> reproduceGraphNode2DLayoutBaseLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
		MetadataID reproducedOutputLayoutRecordDataID = 
				this.getOutputLayoutRecordDataID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		Map<SimpleName, Object> ret = new HashMap<>();
		ret.put(DRAWING_AREA_WIDTH.getName(), this.getDrawingAreaWidth());
		ret.put(DRAWING_AREA_HEIGHT.getName(), this.getDrawingAreaHeight());
		ret.put(OUTPUT_LAYOUT_RECORD_DATA_ID.getName(), reproducedOutputLayoutRecordDataID);
		
		return ret;
	}
	
	
	////////////////////////////
	@Override
	public Set<MetadataID> getOutputMetadataIDSet() {
		Set<MetadataID> ret = new HashSet<>();
		ret.add(this.getOutputLayoutRecordDataID());
		return ret;
	}

	//////////////////////////////////////////////////
	
	protected transient GraphDataMetadata targetGraphDataMetadata;
	
	protected transient GraphLayoutAlgoPerformerBase<VfGraphVertex> graphLayout2DPerformer;
	private transient GraphNode2DLayoutTablePopulator tablePopulator;
	
	/**
	 * 0. check if host VisProjectDBContext is set;
	 * 
	 * 1. invoke {@link #validateParametersValueConstraints()}
	 * 
	 * 2. retrieve target GraphDataMetadata and check if graph type is valid
	 * 
	 * 3. build jgrapht graph with JGraphTGraphBuilder
	 * 		the GraphTypeEnforcer should be based on the observed graph type of input graph;
	 * 
	 * 4. build and initialize a {@link GraphLayoutAlgoPerformerBase} with the built graph 
	 * 		this step should be implemented by each final type for a specific algorithm and graph api;
	 * 
	 * 5. build and perform a {@link GraphNode2DLayoutTablePopulator}
	 * 		1. build output record data table name and schema and insert into host VisProjectDBContext;
	 * 		
	 * 		2. Populate the output record data table with the {@link GraphLayoutAlgoPerformerBase};
	 * 
	 * 6. insert the output RecordDataMetadata into metadata management table;
	 * 
	 * 7. insert this operation into operation management table;
	 * 
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Override
	public StatusType call() throws SQLException, IOException {
		//0
		if(this.getHostVisProjectDBContext()==null) {
			throw new IllegalArgumentException();
		}
		
		//1
//		this.validateParametersValueConstraints();
		
		//2
		this.retrieveAndValidateGraphDataMetadata();
		
		//3
		this.buildGraph();
		
		//4
		this.buildLayoutAlgoPerformer();
		
		//5
		this.createAndPopulateOutputDataTable();
		
		//6
		this.buildAndInsertOutputRecordDataMetadata();
		
		//7
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getOperationManager().insert(this);
				
		//
		return StatusType.FINISHED;
	}
	
	private void retrieveAndValidateGraphDataMetadata() throws SQLException {
		this.targetGraphDataMetadata = (GraphDataMetadata)this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().lookup(this.getInputGenericGraphMetadataID());
	
		if(this.targetGraphDataMetadata==null) {
			throw new IllegalArgumentException("given graph MetatadataID is not found in host VisProjectDBContext");
		}
		
		if(!this.validateInputGraphObservedType(this.targetGraphDataMetadata.getObservedGraphType())) {
			throw new IllegalArgumentException("given graph data's observed type is not compatible with the OperationInputGraphTypeBoundary of the operation!");
		}
	}

	protected abstract void buildGraph() throws IOException, SQLException;
	
	
	/**
	 * build an instance of a subtype of {@link GraphLayoutAlgoPerformerBase} that perform the specific layout algorithm
	 */
	protected abstract void buildLayoutAlgoPerformer();
	
	
	private void createAndPopulateOutputDataTable() throws SQLException {
		RecordDataMetadata graphNodeRecordDataMetadata = (RecordDataMetadata)this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().lookup(this.targetGraphDataMetadata.getNodeRecordMetadataID());
		
		this.tablePopulator = new GraphNode2DLayoutTablePopulator(
				this.getHostVisProjectDBContext(),//VisProjectDBContext hostVisProjectDBContext, 
				graphNodeRecordDataMetadata.getDataTableSchema(),// DataTableSchema targetGraphVertexRecordDataTableSchema,
				this.getOutputLayoutRecordDataID(),// MetadataID outputRecordDataMetadataID,
				this.graphLayout2DPerformer//GraphLayoutAlgoPerformerBase<VfGraphVertex> algoPerformer
				);
		
		this.tablePopulator.perform();
	}
	
	private void buildAndInsertOutputRecordDataMetadata() throws SQLException {
		RecordDataMetadata outputRecordDataMetadata = new RecordDataMetadata(
				this.getOutputLayoutRecordDataID().getName(), 
				VfNotes.makeVisframeDefinedVfNotes(),
				SourceType.RESULT_FROM_OPERATION,
				null,
				this.getID(),
				this.tablePopulator.getOutputDataTableSchema(),
				null
				);
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().insert(outputRecordDataMetadata);
	}

	
	//////////////////////////////////////////////////
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
		if (!(obj instanceof GraphNode2DLayoutOperationBase))
			return false;
		GraphNode2DLayoutOperationBase other = (GraphNode2DLayoutOperationBase) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
	
}
