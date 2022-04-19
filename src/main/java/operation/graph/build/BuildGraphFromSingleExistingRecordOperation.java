package operation.graph.build;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import generic.graph.reader.project.RecordToGraphReaderFactory;
import metadata.MetadataID;
import operation.parameter.DataReproducibleParameter;
import operation.parameter.Parameter;
import operation.utils.DataTableColumnNameLinkedHashSet;
import rdb.table.data.DataTableColumnName;

/**
 * build a GraphDataMetadata from a single RecordDataMetadata with each record containing edge of the graph;
 * 
 * input parameters:
 * 
 * 1. input record data name;
 * 
 * 2. the name of the source node id columns will be used to build the vertex record data table schema of the output GraphDataMetadata;
 * 
 * the source/sink vertex id columns of the edge record data should have the SAME data type with the id columns of the vertex;
 * 
 * @author tanxu
 * 
 */
public final class BuildGraphFromSingleExistingRecordOperation extends BuildGraphFromExistingRecordOperationBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1734187665606658533L;
	
	///////////////////////////////
	public static final SimpleName TYPE_NAME = new SimpleName("BuildGraphFromSingleExistingRecordOperation");
	public static final VfNotes TYPE_NOTES = VfNotes.makeVisframeDefinedVfNotes();
	///////////////////////////////////////////
	
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values;
	 * note that any pair of columns with the same index in the given two list of columns should have same data type;
	 * @param sourceVertexIDColumnNameLinkedHashSet
	 * @param sinkVertexIDColumnNameLinkedHashSet
	 * @return
	 */
	public static Map<SimpleName, Object> buildBuildGraphFromSingleExistingRecordOperationLevelSpecificParameterNameValueObjectMap(
			LinkedHashSet<DataTableColumnName> sourceVertexIDColumnNameLinkedHashSet, 
			LinkedHashSet<DataTableColumnName> sinkVertexIDColumnNameLinkedHashSet
			){
		Map<SimpleName, Object> ret = new HashMap<>();
		ret.put(SOURCE_VERTEX_ID_COLUMN_NAME_LINKED_HASH_SET.getName(), new DataTableColumnNameLinkedHashSet(sourceVertexIDColumnNameLinkedHashSet));
		ret.put(SINK_VERTEX_ID_COLUMN_NAME_LINKED_HASH_SET.getName(), new DataTableColumnNameLinkedHashSet(sinkVertexIDColumnNameLinkedHashSet));
		
		return ret;
	}
	
	
	
	//////////////////////////////////
	/**
	 * parameter for an ordered list of column names for source node ID columns;
	 * the given column set is independent from the primary key column set of the input data table; thus can contain none or any number of columns in the primary key as well as non-primary key;
	 * cannot be null or empty;
	 * must be disjoint with {@link #SINK_VERTEX_ID_COLUMN_NAME_LINKED_HASH_SET};
	 */
	public static final DataReproducibleParameter<DataTableColumnNameLinkedHashSet> SOURCE_VERTEX_ID_COLUMN_NAME_LINKED_HASH_SET =
			new DataReproducibleParameter<>(DataTableColumnNameLinkedHashSet.class, new SimpleName("sourceVertexIDColumnNameLinkedHashSet"), VfNotes.makeVisframeDefinedVfNotes(), "sourceVertexIDColumnNameLinkedHashSet", true, null, null);

	/**
	 * parameter for an ordered list of column names for sink node ID columns;
	 * cannot be null or empty;
	 * the given column set is independent from the primary key column set of the input data table; thus can contain none or any number of columns in the primary key as well as non-primary key;
	 * must be disjoint with {@link #SOURCE_VERTEX_ID_COLUMN_NAME_LINKED_HASH_SET};
	 */
	public static final DataReproducibleParameter<DataTableColumnNameLinkedHashSet> SINK_VERTEX_ID_COLUMN_NAME_LINKED_HASH_SET =
			new DataReproducibleParameter<>(DataTableColumnNameLinkedHashSet.class, new SimpleName("sinkVertexIDColumnNameLinkedHashSet"), VfNotes.makeVisframeDefinedVfNotes(), "sinkVertexIDColumnNameLinkedHashSet", true, null, null);
	
	
	//note that the order of SOURCE_VERTEX_ID_COLUMN_NAME_LINKED_HASH_SET and SINK_VERTEX_ID_COLUMN_NAME_LINKED_HASH_SET is critical
	//every pair of columns with the same order index in the two set will be treated as the same feature of the resulting node ID column set;
	//thus the pair of columns should have the same SQLDataType!
	/////////
	
	
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap;
	/**
	 * return all Parameters defined at the GenericSQLOperation level
	 * @return
	 */
	protected static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap(){
		if(levelSpecificParameterNameMap==null) {
			levelSpecificParameterNameMap = new HashMap<>();
			
			levelSpecificParameterNameMap.put(SOURCE_VERTEX_ID_COLUMN_NAME_LINKED_HASH_SET.getName(), SOURCE_VERTEX_ID_COLUMN_NAME_LINKED_HASH_SET);
			levelSpecificParameterNameMap.put(SINK_VERTEX_ID_COLUMN_NAME_LINKED_HASH_SET.getName(), SINK_VERTEX_ID_COLUMN_NAME_LINKED_HASH_SET);
		}
		return levelSpecificParameterNameMap;
	}
	
	
	////////////////////////////////
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap;
	
	
	/**
	 * constructor
	 * @param operationLevelParameterObjectValueMap
	 * @param buildGraphFromExistingRecordOperationBaseLevelParameterObjectValueMap
	 * @param buildGraphFromSingleExistingRecordOperationLevelParameterObjectValueMap
	 */
	public BuildGraphFromSingleExistingRecordOperation(			
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			
			Map<SimpleName, Object> buildGraphFromExistingRecordOperationBaseLevelParameterObjectValueMap,
			
			Map<SimpleName, Object> buildGraphFromSingleExistingRecordOperationLevelParameterObjectValueMap,
			
			boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent) {
		super(operationLevelParameterObjectValueMap, buildGraphFromExistingRecordOperationBaseLevelParameterObjectValueMap);
		
		//basic validations
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			if(!buildGraphFromSingleExistingRecordOperationLevelParameterObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given buildGraphFromSingleExistingRecordOperationLevelParameterObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		
		
		this.levelSpecificParameterObjectValueMap = buildGraphFromSingleExistingRecordOperationLevelParameterObjectValueMap;
		
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
				throw new IllegalArgumentException("invalid value object found for buildGraphFromSingleExistingRecordOperationLevelParameterObjectValueMap:"+parameterName);
			}
		}
		//3. additional inter-parameter constraints involving parameters at this level
		//sourceVertexIDColumnNameLinkedHashSet and sinkVertexIDColumnNameLinkedHashSet should be non-null and non-empty and have same size;
		if(this.getSourceVertexIDColumnNameLinkedHashSet().getSet().isEmpty()||this.getSinkVertexIDColumnNameLinkedHashSet().getSet().isEmpty()) {
			throw new IllegalArgumentException("sourceVertexIDColumnNameLinkedHashSet and sinkVertexIDColumnNameLinkedHashSet should be non-null and non-empty!");
		}
		
		if(this.getSourceVertexIDColumnNameLinkedHashSet().getSet().size()!=this.getSinkVertexIDColumnNameLinkedHashSet().getSet().size()) {
			throw new IllegalArgumentException("sourceVertexIDColumnNameLinkedHashSet and sinkVertexIDColumnNameLinkedHashSet should be of same size!");
		}
		
		//if edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets is true ...
		if(this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets()) {
			for(DataTableColumnName colName:this.getSourceVertexIDColumnNameLinkedHashSet().getSet()) {
				if(this.getInputEdgeDataTableColumnSetAsEdgeID().getSet().contains(colName)) {
					throw new IllegalArgumentException("inputEdgeDataTableColumnSetAsEdgeID contains one or more column in source vertex column id set when edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets is true!");
				}
			}
			for(DataTableColumnName colName:this.getSinkVertexIDColumnNameLinkedHashSet().getSet()) {
				if(this.getInputEdgeDataTableColumnSetAsEdgeID().getSet().contains(colName)) {
					throw new IllegalArgumentException("inputEdgeDataTableColumnSetAsEdgeID contains one or more column in source vertex column id set when edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets is true!");
				}
			}
		}else {//merged
			for(DataTableColumnName colName:this.getSourceVertexIDColumnNameLinkedHashSet().getSet()) {
				if(!this.getInputEdgeDataTableColumnSetAsEdgeID().getSet().contains(colName)) {
					throw new IllegalArgumentException("inputEdgeDataTableColumnSetAsEdgeID does not contain one or more column in source vertex column id set when edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets is false!");
				}
			}
			for(DataTableColumnName colName:this.getSinkVertexIDColumnNameLinkedHashSet().getSet()) {
				if(!this.getInputEdgeDataTableColumnSetAsEdgeID().getSet().contains(colName)) {
					throw new IllegalArgumentException("inputEdgeDataTableColumnSetAsEdgeID does not contain one or more column in source vertex column id set when edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets is false!");
				}
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
//		return buildGraphFromSingleExistingRecordOperationLevelParameterNameMap();
//	}
//	
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
	
	public DataTableColumnNameLinkedHashSet getSourceVertexIDColumnNameLinkedHashSet(){
		return (DataTableColumnNameLinkedHashSet)this.levelSpecificParameterObjectValueMap.get(SOURCE_VERTEX_ID_COLUMN_NAME_LINKED_HASH_SET.getName());
	}
	
	public DataTableColumnNameLinkedHashSet getSinkVertexIDColumnNameLinkedHashSet(){
		return (DataTableColumnNameLinkedHashSet)this.levelSpecificParameterObjectValueMap.get(SINK_VERTEX_ID_COLUMN_NAME_LINKED_HASH_SET.getName());
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

	///////////////////////////////////////////////////////////////////
	/**
	 * reproduce this Operation and return the reproduced one;
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public BuildGraphFromSingleExistingRecordOperation reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex)
			throws SQLException {
		return new BuildGraphFromSingleExistingRecordOperation(
//				true, ///!!!!!!!!!!!!!!!resultedFromReproducing
				this.reproduceAbstractOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex), 
				this.reproduceBuildGraphFromExistingRecordOperationBaseLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceBuildGraphFromSingleExistingRecordOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				false //toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent should be false if the operation is reproduced;
				);
	}
	
	/**
	 * reproduce and return the values for parameters at {@link BuildGraphFromSingleExistingRecordOperation} level;
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
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @return
	 * @throws SQLException 
	 */
	private Map<SimpleName, Object> reproduceBuildGraphFromSingleExistingRecordOperationLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException{
		//first find out the copy index of the VCCLNode of input edge record data of this operation;
		int inputEdgeRecordDataCopyIndex = VSAArchiveReproducerAndInserter.getAppliedArchive().lookupCopyIndexOfOperationInputMetadata(
				this.getID(), copyIndex, this.getInputEdgeRecordDataMetadataID());
		//
		DataTableColumnNameLinkedHashSet sourceVertexIDColumnNameLinkedHashSet = 
				this.getSourceVertexIDColumnNameLinkedHashSet().reproduce(VSAArchiveReproducerAndInserter, this.getInputEdgeRecordDataMetadataID(), inputEdgeRecordDataCopyIndex);
		DataTableColumnNameLinkedHashSet sinkVertexIDColumnNameLinkedHashSet = 
				this.getSinkVertexIDColumnNameLinkedHashSet().reproduce(VSAArchiveReproducerAndInserter, this.getInputEdgeRecordDataMetadataID(), inputEdgeRecordDataCopyIndex);
		
		Map<SimpleName, Object> ret = new HashMap<>();
		ret.put(SOURCE_VERTEX_ID_COLUMN_NAME_LINKED_HASH_SET.getName(), sourceVertexIDColumnNameLinkedHashSet);
		ret.put(SINK_VERTEX_ID_COLUMN_NAME_LINKED_HASH_SET.getName(), sinkVertexIDColumnNameLinkedHashSet);
			
		return ret;
	}

	
	//////////////////////////////////////////////////////////////////

	@Override
	public Set<MetadataID> getInputMetadataIDSet() {
		if(this.inputMetadataIDSet==null) {
			this.inputMetadataIDSet = new HashSet<>();
			
			MetadataID inputEdgeRecordMetadataID = this.getInputEdgeRecordDataMetadataID();
			
			this.inputMetadataIDSet.add(inputEdgeRecordMetadataID);
		}
		
		return inputMetadataIDSet;
	}

	/**
	 * all input columns are  
	 */
	@Override
	public Map<MetadataID, Set<DataTableColumnName>> getInputRecordMetadataIDInputColumnNameSetMap() {
		Map<MetadataID, Set<DataTableColumnName>> inputRecordMetadataIDIndependentInputColumnNameSetMap = new LinkedHashMap<>();
		
		Set<DataTableColumnName> inputEdgeRecordDataIndependentInputColumnNameSet = new LinkedHashSet<>();
		inputEdgeRecordDataIndependentInputColumnNameSet.addAll(this.getInputEdgeDataTableColumnSetAsEdgeID().getSet());
		inputEdgeRecordDataIndependentInputColumnNameSet.addAll(this.getInputEdgeDataTableColumnSetAsAdditionalFeature().getSet());
		if(this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets()) {
			inputEdgeRecordDataIndependentInputColumnNameSet.addAll(this.getSourceVertexIDColumnNameLinkedHashSet().getSet());
			inputEdgeRecordDataIndependentInputColumnNameSet.addAll(this.getSinkVertexIDColumnNameLinkedHashSet().getSet());
		}
		
		//directed type indicator column in the input edge data table;
		if(this.hasDirectedTypeIndicatorColumn()) {
			inputEdgeRecordDataIndependentInputColumnNameSet.add(this.getDirectedTypeIndicatorColumnName());
		}
		
		inputRecordMetadataIDIndependentInputColumnNameSetMap.put(this.getInputEdgeRecordDataMetadataID(), inputEdgeRecordDataIndependentInputColumnNameSet);
		
		return inputRecordMetadataIDIndependentInputColumnNameSetMap;
	}
	
	
	///////////////////////////////////////////////////////////////////////
	///facilitating methods for call() method
	
	@Override
	protected void makeRecordToGraphReader() throws SQLException {
		//RecordToGraphReader
		
		LinkedHashMap<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap = new LinkedHashMap<>();
		LinkedHashMap<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap = new LinkedHashMap<>();
		
		Iterator<DataTableColumnName> sinkVertexIDColumnIterator = this.getSinkVertexIDColumnNameLinkedHashSet().getSet().iterator();
		for(DataTableColumnName colName:this.getSourceVertexIDColumnNameLinkedHashSet().getSet()) {
			vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap.put(colName, colName);
			vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap.put(colName, sinkVertexIDColumnIterator.next());
		}
		
		/////
		RecordToGraphReaderFactory recordToGraphReaderFactory = new RecordToGraphReaderFactory(
				this.getHostVisProjectDBContext(),
				false, //@param hasVertexDataSourceRecordData
				null, //this.getInputNodeRecordDataMetadataID(),
				this.getInputEdgeRecordDataMetadataID(),
				true, //toFilterOutDuplicates during the reader step
				null,
				null,
				this.getInputEdgeDataTableColumnSetAsEdgeID().getSet(),
				this.getInputEdgeDataTableColumnSetAsAdditionalFeature().getSet(),
				this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets(),
				vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap,
				vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap
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

	/**
	 * always true
	 */
	@Override
	public boolean isToAddDiscoveredVertexFromInputEdgeDataTable() {
		return true;
	}
	
	
	//////////////////////
	public String toString() {
		
		return Integer.toString(this.getSourceVertexIDColumnNameLinkedHashSet().getSet().size());
	}

	
	/////////////////////////////////////
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
		if (!(obj instanceof BuildGraphFromSingleExistingRecordOperation))
			return false;
		BuildGraphFromSingleExistingRecordOperation other = (BuildGraphFromSingleExistingRecordOperation) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
	
}
