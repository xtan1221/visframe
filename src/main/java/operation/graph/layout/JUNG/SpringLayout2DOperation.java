package operation.graph.layout.JUNG;

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
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import generic.graph.builder.JUNG.JUNGGraphBuilder;
import generic.graph.reader.project.GenericGraphMetadataDataReader;
import metadata.MetadataID;
import metadata.graph.type.CommonGraphTypeBoundaryFactory;
import metadata.graph.type.OperationInputGraphTypeBoundary;
import operation.graph.layout.GraphNode2DLayoutOperationBase;
import operation.graph.layout.utils.JUNGSpringLayout2DPerformer;
import operation.parameter.Parameter;
import operation.parameter.primitive.DoubleParameter;
import operation.parameter.primitive.IntegerParameter;
import rdb.table.data.DataTableColumnName;

/**
 * 
 * delegate to {@link SpringLayout} in JUNG api;
 * 
 * see http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/algorithms/layout/SpringLayout.html
 * 
 * 
 * =====================================
 * main parameters for the class are:
 * 1. a graph
 * 
 * 2. a length function of each edge (OPTIONAL)
 * 		Transformer<E,Integer> length_function;
 * 		this is optional, if not set, use the unit length for each length;
 * 
 * 3. dimension of the available space for layout of type Dimension; (OPTIONAL????)
 * 
 * 4. force multiplier of double type; (OPTIONAL)
 * 
 * 		specify how strongly an edge "wants" to be its default length (higher values indicate a greater attraction for the default length), which affects how much its endpoints move at each timestep. The default value is 1/3. A value of 0 turns off any attempt by the layout to cause edges to conform to the default length. Negative values cause long edges to get longer and short edges to get shorter; use at your own risk.
 * 
 * 5. repulsion range of int type (OPTIONAL)
 * 		node repulsion range (in drawing area units) for this instance. Outside this range, nodes do not repel each other. The default value is 100. Negative values are treated as their positive equivalents.
 * 
 * 6. stretch of double type (OPTIONAL)
 * 		This value specifies how much the degrees of an edge's incident vertices should influence how easily the endpoints of that edge can move (that is, that edge's tendency to change its length).
 *		The default value is 0.70. Positive values less than 1 cause high-degree vertices to move less than low-degree vertices, and values > 1 cause high-degree vertices to move more than low-degree vertices. Negative values will have unpredictable and inconsistent results.
 * 
 * 
 * =========================================
 * calculated layout are cartesian coordinates;
 * 
 * =========================================
 * OperationInputGraphTypeBoundary should be ?
 * 
 * 
 * =========================================
 * method to retrieve the calculated layout:
 * {@link AbstractLayout#getX(V)} //return the calculated x coordinate for vertex v
 * {@link AbstractLayout#getY(V)} //return the calculated y coordinate for vertex v
 * 
 * =========================================
 * see {@link JUNGSpringLayout2DPerformer}
 * @author tanxu
 *
 */
public final class SpringLayout2DOperation extends GraphNode2DLayoutOperationBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2074792132380236374L;
	
	////////////////////
	public static final SimpleName TYPE_NAME = new SimpleName("SpringLayout2DOperation");
	public static final VfNotes TYPE_NOTES = VfNotes.makeVisframeDefinedVfNotes();
	
	//////////////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildSpringLayout2DOperationLevelSpecificParameterNameValueObjectMap(
			double forceMultiplier,	int repulsionRange,
			double stretch, int iterations){
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.put(FORCE_MULTIPLIER.getName(), forceMultiplier);
		ret.put(REPULSION_RANGE.getName(), repulsionRange);
		ret.put(STRETCH.getName(), stretch);
		ret.put(ITERATIONS.getName(), iterations);
		return ret;
	}
	
	//////////////////////////////////////
	/**
	 * see {@link JUNGSpringLayout2DPerformer#forceMultiplier}
	 */
	public static final DoubleParameter FORCE_MULTIPLIER = 
			new DoubleParameter(new SimpleName("forceMultiplier"), new VfNotes(), "FORCE_MULTIPLIER", true, JUNGSpringLayout2DPerformer.DEFAULT_FORCE_MULTIPLIER, 
					e->{return true;},  //nonNullValueAdditionalConstraints ?
					false);//inputDataTableContentDependent
	public Double getForceMultiplier() {
		return (Double)this.levelSpecificParameterObjectValueMap.get(FORCE_MULTIPLIER.getName());
	}
	/**
	 * see {@link JUNGSpringLayout2DPerformer#repulsionRange}
	 */
	public static final IntegerParameter REPULSION_RANGE = 
			new IntegerParameter(new SimpleName("repulsionRange"), new VfNotes(), "REPULSION_RANGE", true, JUNGSpringLayout2DPerformer.DEFAULT_REPULSION_RANGE, 
					p->{return p>0;}, //must be positive
					false);//inputDataTableContentDependent
	public Integer getRepulsionRange() {
		return (Integer)this.levelSpecificParameterObjectValueMap.get(REPULSION_RANGE.getName());
	}
	//
	public static final DoubleParameter STRETCH = 
			new DoubleParameter(new SimpleName("stretch"), new VfNotes(), "STRETCH", true, JUNGSpringLayout2DPerformer.DEFAULT_STRETCH, 
					e->{return true;}, //must be positive? //nonNullValueAdditionalConstraints ?
					false);//inputDataTableContentDependent
	public Double getStretch() {
		return (Double)this.levelSpecificParameterObjectValueMap.get(STRETCH.getName());
	}
	//
	public static final IntegerParameter ITERATIONS = 
			new IntegerParameter(new SimpleName("iterations"), new VfNotes(), "ITERATIONS", true, JUNGSpringLayout2DPerformer.DEFAULT_ITERATIONS, 
					p->{return p>0;}, //must be positive
					false);//inputDataTableContentDependent
	public Integer getIterations() {
		return (Integer)this.levelSpecificParameterObjectValueMap.get(ITERATIONS.getName());
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
			levelSpecificParameterNameMap.put(FORCE_MULTIPLIER.getName(), FORCE_MULTIPLIER);
			levelSpecificParameterNameMap.put(REPULSION_RANGE.getName(), REPULSION_RANGE);
			levelSpecificParameterNameMap.put(STRETCH.getName(), STRETCH);
			levelSpecificParameterNameMap.put(ITERATIONS.getName(), ITERATIONS);
		}
		return levelSpecificParameterNameMap;
	}
	
	////////////////////////////////
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap;
	
	/**
	 * constructor
	 * @param operationLevelParameterObjectValueMap
	 * @param singleGenericGraphAsInputOperationLevelParameterObjectValueMap
	 * @param inputGraphTypeBoundedOperationLevelParameterObjectValueMap
	 * @param graphNode2DLayoutBaseLevelParameterObjectValueMap
	 * @param springLayout2DOperationLevelParameterObjectValueMap
	 */
	public SpringLayout2DOperation(
//			boolean resultedFromReproducing,
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			Map<SimpleName, Object> singleGenericGraphAsInputOperationLevelParameterObjectValueMap,
			Map<SimpleName, Object> inputGraphTypeBoundedOperationLevelParameterObjectValueMap,
			Map<SimpleName, Object> graphNode2DLayoutBaseLevelParameterObjectValueMap,
			Map<SimpleName, Object> springLayout2DOperationLevelParameterObjectValueMap,
			boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent
			) {
		super(operationLevelParameterObjectValueMap, singleGenericGraphAsInputOperationLevelParameterObjectValueMap, 
				inputGraphTypeBoundedOperationLevelParameterObjectValueMap, graphNode2DLayoutBaseLevelParameterObjectValueMap);
		//validations
		//always first validate each value objects with the constraints implemented in the Parameter<?> object of each parameter
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			if(!springLayout2DOperationLevelParameterObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given springLayout2DOperationLevelParameterObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		
		this.levelSpecificParameterObjectValueMap = springLayout2DOperationLevelParameterObjectValueMap;
		
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
		return CommonGraphTypeBoundaryFactory.anyGraph();//?TODO
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
	public SpringLayout2DOperation reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex)throws SQLException {
		return new SpringLayout2DOperation(
//				true, //////!!!!!!
				this.reproduceAbstractOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex), 
				this.reproduceSingleGenericGraphAsInputOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceInputGraphTypeBoundedOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceGraphNode2DLayoutBaseLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceSpringLayout2DOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				false //toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent should be false if the operation is reproduced;
				);
	}
	

	/**
	 * reproduce and return a parameter name value object map of parameters at {@link SpringLayout2DOperation} level;
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
	private Map<SimpleName, Object> reproduceSpringLayout2DOperationLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		Map<SimpleName, Object> ret = new HashMap<>();
		
		ret.put(FORCE_MULTIPLIER.getName(), this.getForceMultiplier());
		ret.put(REPULSION_RANGE.getName(), this.getRepulsionRange());
		ret.put(STRETCH.getName(), this.getStretch());
		ret.put(ITERATIONS.getName(), this.getIterations());
		
		return ret;
	}
	
	//////////////////////////////////////////////////
	////call() method related;
	private transient JUNGGraphBuilder graphBuilder;
	@Override
	protected void buildGraph() throws IOException, SQLException {
		GenericGraphMetadataDataReader genericGraphMetadataDataReader = new GenericGraphMetadataDataReader(this.getHostVisProjectDBContext(), this.getInputGenericGraphMetadataID());
		genericGraphMetadataDataReader.initialize();
		
		//?
//		GraphTypeEnforcer graphTypeEnforcer = new GraphTypeEnforcer(
//				this.targetGraphDataMetadata.getObservedGraphType().isContainingDirectedEdgeOnly(),//boolean toForceDirected,
//				this.targetGraphDataMetadata.getObservedGraphType().isContainingDirectedEdgeOnly()?DirectedEnforcingMode.SIMPLE:null,//DirectedEnforcingMode directedForcingMode,
//				this.targetGraphDataMetadata.getObservedGraphType().isContainingUndirectedEdgeOnly(), //boolean toForceUndirected,
//				false,//boolean toForceNoParallelEdges; do nothing, build as it is
//				false//boolean toForceNoSelfLoops; do nothing, build as it is
//				); 
		
		this.graphBuilder = new JUNGGraphBuilder(
				genericGraphMetadataDataReader,
				this.getOperationInputGraphTypeBoundary()
				);
		this.graphBuilder.perform();
	}
	
	
	@Override
	protected void buildLayoutAlgoPerformer() {
		this.graphLayout2DPerformer = new JUNGSpringLayout2DPerformer(
				this.getDrawingAreaHeight(),//int drawingAreaHeight, 
				this.getDrawingAreaWidth(),//int drawingAreaWidth,
				this.graphBuilder.getUnderlyingGraph(),//Graph<VfGraphVertex, VfGraphEdge> targetGraph,
				this.getForceMultiplier(),//Double attractionMultiplier,
				this.getRepulsionRange(),//Double repulsion,
				this.getStretch(),//Integer maxIterations
				this.getIterations()
				);
		this.graphLayout2DPerformer.initialize();
	}

	
	////////////////////////////////////
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
		if (!(obj instanceof SpringLayout2DOperation))
			return false;
		SpringLayout2DOperation other = (SpringLayout2DOperation) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
	
	
	
}
