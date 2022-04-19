package operation.graph.layout.jgrapht;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import generic.graph.builder.JGraphT.JGraphTGraphBuilder;
import generic.graph.reader.project.GenericGraphMetadataDataReader;
import operation.graph.layout.utils.JGraphTCircularLayout2DPerformer;
import metadata.MetadataID;
import metadata.graph.type.CommonGraphTypeBoundaryFactory;
import metadata.graph.type.GraphTypeEnforcer;
import metadata.graph.type.OperationInputGraphTypeBoundary;
import metadata.graph.type.GraphTypeEnforcer.DirectedEnforcingMode;
import operation.graph.layout.GraphNode2DLayoutOperationBase;
import operation.parameter.Parameter;
import operation.parameter.primitive.DoubleParameter;
import rdb.table.data.DataTableColumnName;


/**
 * delegate to {@link org.jgrapht.alg.drawing.CircularLayoutAlgorithm2D};
 * 
 * see https://jgrapht.org/javadoc/org.jgrapht.core/org/jgrapht/alg/drawing/CircularLayoutAlgorithm2D.html
 * 
 * to create a CircularLayoutAlgorithm2D object, the following parameters are needed:
 * 1. an input Graph<V,E>
 * 		any graph type;
 * 
 * 2. a LayoutModel2D<V> interface
 * 		drawing area height and width is needed
 * 
 * 3. a Function<V,Point2D> that initialize the location of each vertex; 
 * 		current version does not support an explicit initializer function; use null value;
 * 		if no initializer is provide, random position will be generated for each vertex as the initial position in the corresponding LayoutAlgorithm2D class
 * 
 * @author tanxu
 * 
 */
public final class CircularLayout2DOperation extends GraphNode2DLayoutOperationBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4122285091365090068L;
	
	////////////////////
//	public static final double DEFAULT_RADIUS = GraphNode2DLayoutOperationBase.DEFAULT_DRAWING_AREA_WIDTH*0.4;
	
	public static final SimpleName TYPE_NAME = new SimpleName("CircularLayout2DOperation");
	public static final VfNotes TYPE_NOTES = VfNotes.makeVisframeDefinedVfNotes();
	
	
	//////////////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildCircularLayout2DOperationLevelSpecificParameterNameValueObjectMap(double radius){
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.put(RADIUS.getName(), radius);
		
		return ret;
	}
	//////////////////////////////////////
	/**
	 * radius of the circle;
	 * 
	 * mandatory;
	 */
	public static final DoubleParameter RADIUS = 
			new DoubleParameter(new SimpleName("radius"), new VfNotes(), "RADIUS", true, null, 
					p->{return p>0;}, //must be positive
					false);//inputDataTableContentDependent
	public Double getRadius() {
		return (Double)this.levelSpecificParameterObjectValueMap.get(RADIUS.getName());
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
			levelSpecificParameterNameMap.put(RADIUS.getName(), RADIUS);
		}
		return levelSpecificParameterNameMap;
	}
	
	////////////////////////////////
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap;
	
	/**
	 * constructor
	 * @param operationLevelParameterObjectValueMap
	 * @param singleGenericGraphAsInputOperationLevelParameterObjectValueMap
	 * @param graphNode2DLayoutBaseLevelParameterObjectValueMap
	 * @param circularLayout2DOperationLevelParameterObjectValueMap
	 */
	public CircularLayout2DOperation(
//			boolean resultedFromReproducing,
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			Map<SimpleName, Object> singleGenericGraphAsInputOperationLevelParameterObjectValueMap,
			Map<SimpleName, Object> inputGraphTypeBoundedOperationLevelParameterObjectValueMap,
			Map<SimpleName, Object> graphNode2DLayoutBaseLevelParameterObjectValueMap,
			Map<SimpleName, Object> circularLayout2DOperationLevelParameterObjectValueMap,
			boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent
			) {
		super(operationLevelParameterObjectValueMap, singleGenericGraphAsInputOperationLevelParameterObjectValueMap, 
				inputGraphTypeBoundedOperationLevelParameterObjectValueMap, graphNode2DLayoutBaseLevelParameterObjectValueMap);
		//validations
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			if(!circularLayout2DOperationLevelParameterObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given graphNode2DLayoutBaseLevelParameterObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		
		this.levelSpecificParameterObjectValueMap = circularLayout2DOperationLevelParameterObjectValueMap;
		
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
		//radius must be less than the half of drawing area height and width????
		
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
	
	////////////////////////////////////
	
	@Override
	public SimpleName getOperationTypeName() {
		return TYPE_NAME;
	}

	
	@Override
	public VfNotes getOperationTypeNotes() {
		return TYPE_NOTES;
	}
	
	/**
	 * no independent input columns
	 */
	@Override
	public Map<MetadataID, Set<DataTableColumnName>> getInputRecordMetadataIDInputColumnNameSetMap() {
		return new HashMap<>();
	}
	
	/**
	 * any graph;
	 */
	@Override
	public OperationInputGraphTypeBoundary getOperationInputGraphTypeBoundary() {
		return CommonGraphTypeBoundaryFactory.anyGraph();
	}
	
	
	//////////////////////////////////////////////////
	/**
	 * 
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public CircularLayout2DOperation reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex)
			throws SQLException {
		return new CircularLayout2DOperation(
//				true,///////!!!!!!!!!!!!!!!!!!
				this.reproduceAbstractOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex), 
				this.reproduceSingleGenericGraphAsInputOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceInputGraphTypeBoundedOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceGraphNode2DLayoutBaseLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceCircularLayout2DOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				false //toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent should be false if the operation is reproduced;
				);
	}
	

	/**
	 * reproduce and return a parameter name value object map of parameters at {@link CircularLayout2DOperation} level;
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
	private Map<SimpleName, Object> reproduceCircularLayout2DOperationLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		Map<SimpleName, Object> ret = new HashMap<>();
		
		ret.put(RADIUS.getName(), this.getRadius());
		
		return ret;
	}
	
	//////////////////////////////////////////////////
	private transient JGraphTGraphBuilder JGraphTGraphBuilder;
	@Override
	protected void buildGraph() throws IOException, SQLException {
		GenericGraphMetadataDataReader genericGraphMetadataDataReader = new GenericGraphMetadataDataReader(this.getHostVisProjectDBContext(), this.getInputGenericGraphMetadataID());
		genericGraphMetadataDataReader.initialize();
		
		GraphTypeEnforcer graphTypeEnforcer = new GraphTypeEnforcer(
				this.targetGraphDataMetadata.getObservedGraphType().isContainingDirectedEdgeOnly(),//boolean toForceDirected,
				this.targetGraphDataMetadata.getObservedGraphType().isContainingDirectedEdgeOnly()?DirectedEnforcingMode.SIMPLE:null,//DirectedEnforcingMode directedForcingMode,
				this.targetGraphDataMetadata.getObservedGraphType().isContainingUndirectedEdgeOnly(), //boolean toForceUndirected,
				false,//boolean toForceNoParallelEdges; do nothing, build as it is
				false//boolean toForceNoSelfLoops; do nothing, build as it is
				); 
		
		this.JGraphTGraphBuilder = new JGraphTGraphBuilder(
				genericGraphMetadataDataReader,//GraphIterator inputGraphIterator, 
				graphTypeEnforcer, //GraphTypeEnforcer graphTypeEnforcer, 
				false//boolean toAddDiscoveredVertexFromInputEdgeDataTable
				);
		this.JGraphTGraphBuilder.perform();
	}
	
	////call() method related;
	@Override
	protected void buildLayoutAlgoPerformer() {
		this.graphLayout2DPerformer = new JGraphTCircularLayout2DPerformer(
				this.getDrawingAreaHeight(),//int drawingAreaHeight, 
				this.getDrawingAreaWidth(),//int drawingAreaWidth,
				this.JGraphTGraphBuilder.getUnderlyingGraph(),//Graph<VfGraphVertex, VfGraphEdge> targetGraph,
				null, //Function<VfGraphVertex, Point2D> initialLayoutFunction
				
				this.getRadius()//double radius
				);
		this.graphLayout2DPerformer.initialize();
	}
	
	
	
	////////////////////////////////////////
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
		if (!(obj instanceof CircularLayout2DOperation))
			return false;
		CircularLayout2DOperation other = (CircularLayout2DOperation) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
	
	
	
}
