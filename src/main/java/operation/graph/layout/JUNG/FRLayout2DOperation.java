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
import generic.graph.builder.JUNG.JUNGGraphBuilder;
import generic.graph.reader.project.GenericGraphMetadataDataReader;
import metadata.MetadataID;
import metadata.graph.type.CommonGraphTypeBoundaryFactory;
import metadata.graph.type.OperationInputGraphTypeBoundary;
import operation.graph.layout.GraphNode2DLayoutOperationBase;
import operation.graph.layout.utils.JUNGFRLayout2DPerformer;
import operation.parameter.Parameter;
import operation.parameter.primitive.DoubleParameter;
import operation.parameter.primitive.IntegerParameter;
import rdb.table.data.DataTableColumnName;

/**
 * see {@link JUNGFRLayout2DPerformer}
 * @author tanxu
 *
 */
public final class FRLayout2DOperation extends GraphNode2DLayoutOperationBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7267238020275927818L;
	////////////////////
	public static final SimpleName TYPE_NAME = new SimpleName("FRLayout2DOperation");
	public static final VfNotes TYPE_NOTES = VfNotes.makeVisframeDefinedVfNotes();
	
	
	//////////////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildFRLayout2DOperationLevelSpecificParameterNameValueObjectMap(
			double attractionMultiplier, double repulsion, int maxIterations){
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.put(ATTRACTION_MULTIPLIER.getName(), attractionMultiplier);
		ret.put(REPULSION.getName(), repulsion);
		ret.put(MAX_ITERATION.getName(), maxIterations);
		
		return ret;
	}
	//////////////////////////////////////
	/**
	 * see {@link JUNGFRLayout2DPerformer#attractionMultiplier}
	 */
	public static final DoubleParameter ATTRACTION_MULTIPLIER = 
			new DoubleParameter(new SimpleName("attractionMultiplier"), new VfNotes(), "ATTRACTION_MULTIPLIER", true, JUNGFRLayout2DPerformer.DEFAULT_ATTRACTION_MULTIPLIER, 
					p->{return p>0;}, //must be positive? //nonNullValueAdditionalConstraints ?
					false);//inputDataTableContentDependent
	public Double getAttractionMultiplier() {
		return (Double)this.levelSpecificParameterObjectValueMap.get(ATTRACTION_MULTIPLIER.getName());
	}
	/**
	 * see {@link JUNGFRLayout2DPerformer#repulsion}
	 */
	public static final DoubleParameter REPULSION = 
			new DoubleParameter(new SimpleName("repulsion"), new VfNotes(), "REPULSION", true, JUNGFRLayout2DPerformer.DEFAULT_REPULSION, 
					p->{return p>0;}, //must be positive? //nonNullValueAdditionalConstraints ?
					false);//inputDataTableContentDependent
	public Double getRepulsion() {
		return (Double)this.levelSpecificParameterObjectValueMap.get(REPULSION.getName());
	}
	/**
	 * see {@link JUNGFRLayout2DPerformer#maxIterations}
	 */
	public static final IntegerParameter MAX_ITERATION = 
			new IntegerParameter(new SimpleName("maxIterations"), new VfNotes(), "MAX_ITERATION", true, JUNGFRLayout2DPerformer.DEFAULT_MAX_ITERATION, 
					p->{return p>0;}, //must be positive
					false);//inputDataTableContentDependent
	public Integer getMaxIterations() {
		return (Integer)this.levelSpecificParameterObjectValueMap.get(MAX_ITERATION.getName());
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
			levelSpecificParameterNameMap.put(ATTRACTION_MULTIPLIER.getName(), ATTRACTION_MULTIPLIER);
			levelSpecificParameterNameMap.put(REPULSION.getName(), REPULSION);
			levelSpecificParameterNameMap.put(MAX_ITERATION.getName(), MAX_ITERATION);
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
	 * @param FRLayout2DOperationLevelParameterObjectValueMap
	 */
	public FRLayout2DOperation(
//			boolean resultedFromReproducing,
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			Map<SimpleName, Object> singleGenericGraphAsInputOperationLevelParameterObjectValueMap,
			Map<SimpleName, Object> inputGraphTypeBoundedOperationLevelParameterObjectValueMap,
			Map<SimpleName, Object> graphNode2DLayoutBaseLevelParameterObjectValueMap,
			Map<SimpleName, Object> FRLayout2DOperationLevelParameterObjectValueMap,
			boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent
			) {
		super(operationLevelParameterObjectValueMap, singleGenericGraphAsInputOperationLevelParameterObjectValueMap, 
				inputGraphTypeBoundedOperationLevelParameterObjectValueMap, graphNode2DLayoutBaseLevelParameterObjectValueMap);
		//validations
		//always first validate each value objects with the constraints implemented in the Parameter<?> object of each parameter
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			if(!FRLayout2DOperationLevelParameterObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given FRLayout2DOperationLevelParameterObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		
		this.levelSpecificParameterObjectValueMap = FRLayout2DOperationLevelParameterObjectValueMap;
		
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
	public FRLayout2DOperation reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex)
			throws SQLException {
		return new FRLayout2DOperation(
//				true, ///!!!!!!!!!!!!
				this.reproduceAbstractOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex), 
				this.reproduceSingleGenericGraphAsInputOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceInputGraphTypeBoundedOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceGraphNode2DLayoutBaseLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceFRLayout2DOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				false //toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent should be false if the operation is reproduced;
				);
	}
	

	/**
	 * reproduce and return a parameter name value object map of parameters at {@link FRLayout2DOperation} level;
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
	private Map<SimpleName, Object> reproduceFRLayout2DOperationLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		Map<SimpleName, Object> ret = new HashMap<>();
		
		ret.put(ATTRACTION_MULTIPLIER.getName(), this.getAttractionMultiplier());
		ret.put(REPULSION.getName(), this.getRepulsion());
		ret.put(MAX_ITERATION.getName(), this.getMaxIterations());
		
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
		this.graphLayout2DPerformer = new JUNGFRLayout2DPerformer(
				this.getDrawingAreaHeight(),//int drawingAreaHeight, 
				this.getDrawingAreaWidth(),//int drawingAreaWidth,
				this.graphBuilder.getUnderlyingGraph(),//Graph<VfGraphVertex, VfGraphEdge> targetGraph,
				this.getAttractionMultiplier(),//Double attractionMultiplier,
				this.getRepulsion(),//Double repulsion,
				this.getMaxIterations()//Integer maxIterations
				);
		this.graphLayout2DPerformer.initialize();
	}


	
	////////////////////////
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
		if (!(obj instanceof FRLayout2DOperation))
			return false;
		FRLayout2DOperation other = (FRLayout2DOperation) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
	
}
