package operation.vftree;

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
import generic.tree.calculation.VfCalculatorTree;
import generic.tree.populator.VfTreePopulator;
import generic.tree.reader.projectbased.VfDataTreeReader;
import generic.tree.trim.AbstractVfTreeTrimmer;
import generic.tree.trim.VfRerootTreeGenericTrimmer;
import metadata.DataType;
import metadata.MetadataID;
import metadata.MetadataName;
import metadata.SourceType;
import metadata.graph.utils.GraphNameBuilder;
import metadata.graph.vftree.VfTreeDataMetadata;
import metadata.record.RecordDataMetadata;
import operation.AbstractOperation;
import operation.Operation;
import operation.parameter.DataReproducibleParameter;
import operation.parameter.Parameter;
import operation.parameter.ReproducibleParameter;
import operation.utils.DataTableColumnNameLinkedHashSet;
import rdb.table.data.DataTableColumnName;


/**
 * base class for VfTreeDataMetadata trimming operations;
 * 
 * !!!!!!note that non-mandatory additional feature columns (NOT the default additional columns defined by visframe in {@link VfTreeNodeFeatureFactory} and {@link VfTreeEdgeFeatureFactory}) 
 * of node/edge record of input VfTreeDataMetadata to be kept in the output VfTreeDataMetadata 
 * should be explicitly assigned in {@link VfTreeTrimmingOperationBase} so that node/edge record table schema of output VfTreeDataMetadata is fully determined by the Operation;
 * 
 * note that only if Operation API be deterministic regarding the input/output record data table schema, the reproducing in VisScheme applying can be legitimate;
 * 
 * 
 * =================
 * note that {@link VfTreeTrimmingOperationBase} should not be subtype of {@link InputGraphTypeBoundedOperation} 
 * since the latter can take both {@link DataType#GRAPH} and {@link DataType#vfTREE} as input while the former can only take {@link DataType#vfTREE} as input!
 * @author tanxu
 * 
 */
public abstract class VfTreeTrimmingOperationBase extends AbstractOperation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1655236636901333318L;
	
	
	//////////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildVfTreeTrimmingOperationBaseLevelSpecificParameterNameValueObjectMap(
			MetadataID inputVfTreeMetadataID,
			MetadataID inputVfTreeNodeRecordMetadataID,
			MetadataID inputVfTreeEdgeRecordMetadataID,
			LinkedHashSet<DataTableColumnName> inputNodeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData,
			LinkedHashSet<DataTableColumnName> inputEdgeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData,
			MetadataName outputVfTreeDataName){
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.put(INPUT_VFTREE_METADATAID.getName(), inputVfTreeMetadataID);
		
		ret.put(INPUT_VFTREE_NODE_RECORD_METADATAID.getName(), inputVfTreeNodeRecordMetadataID);
		
		ret.put(INPUT_VFTREE_EDGE_RECORD_METADATAID.getName(), inputVfTreeEdgeRecordMetadataID);
		
		ret.put(INPUT_NODE_RECORD_NON_MANDATORY_ADDITIONAL_FEATURE_COLUMN_SET_TO_KEEP.getName(), new DataTableColumnNameLinkedHashSet(inputNodeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData));
		
		ret.put(INPUT_EDGE_RECORD_NON_MANDATORY_ADDITIONAL_FEATURE_COLUMN_SET_TO_KEEP.getName(), new DataTableColumnNameLinkedHashSet(inputEdgeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData));
		
		ret.put(OUTPUT_VFTREE_METADATA_ID.getName(), new MetadataID(outputVfTreeDataName, DataType.vfTREE));
		
		return ret;
	}
	
	////////////////////////////////////
	/**
	 * parameter for the single vftree data MetadataID;
	 */
	public static final ReproducibleParameter<MetadataID> INPUT_VFTREE_METADATAID = 
			new ReproducibleParameter<>(MetadataID.class, new SimpleName("inputVfTreeMetadataID"), VfNotes.makeVisframeDefinedVfNotes(), "Input vftree MetadataID", true, 
					m->{return m.getDataType().equals(DataType.vfTREE);}, //additional constraints
					null) ;//
	public MetadataID getInputVfTreeMetadataID() {
		return (MetadataID)this.levelSpecificParameterObjectValueMap.get(INPUT_VFTREE_METADATAID.getName());
	}
	
	public static final ReproducibleParameter<MetadataID> INPUT_VFTREE_NODE_RECORD_METADATAID = 
			new ReproducibleParameter<>(MetadataID.class, new SimpleName("inputVfTreeNodeRecordMetadataID"), VfNotes.makeVisframeDefinedVfNotes(), "Input vftree node record MetadataID", true, 
					m->{return m.getDataType().equals(DataType.RECORD);}, //additional constraints
					null) ;//
	public MetadataID getInputVfTreeNodeRecordMetadataID() {
		return (MetadataID)this.levelSpecificParameterObjectValueMap.get(INPUT_VFTREE_NODE_RECORD_METADATAID.getName());
	}
	
	public static final ReproducibleParameter<MetadataID> INPUT_VFTREE_EDGE_RECORD_METADATAID = 
			new ReproducibleParameter<>(MetadataID.class, new SimpleName("inputVfTreeEdgeRecordMetadataID"), VfNotes.makeVisframeDefinedVfNotes(), "Input vftree edge record MetadataID", true, 
					m->{return m.getDataType().equals(DataType.RECORD);}, //additional constraints
					null) ;//
	public MetadataID getInputVfTreeEdgeRecordMetadataID() {
		return (MetadataID)this.levelSpecificParameterObjectValueMap.get(INPUT_VFTREE_EDGE_RECORD_METADATAID.getName());
	}
	
	/**
	 * parameter for set of input node record's other feature columns to be kept in the node record data of output vftree
	 */
	public static final DataReproducibleParameter<DataTableColumnNameLinkedHashSet> INPUT_NODE_RECORD_NON_MANDATORY_ADDITIONAL_FEATURE_COLUMN_SET_TO_KEEP =
			new DataReproducibleParameter<>(DataTableColumnNameLinkedHashSet.class, new SimpleName("inputNodeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData"), new VfNotes(), "input Node record non mandatory additional feature Column set to be kept int output VfTree", true, null, null);// 
	public DataTableColumnNameLinkedHashSet getInputNodeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData() {
		return (DataTableColumnNameLinkedHashSet) this.levelSpecificParameterObjectValueMap.get(INPUT_NODE_RECORD_NON_MANDATORY_ADDITIONAL_FEATURE_COLUMN_SET_TO_KEEP.getName());
	}
	/**
	 * parameter for set of input edge record's other feature columns to be kept in the edge record data of output vftree
	 */
	public static final DataReproducibleParameter<DataTableColumnNameLinkedHashSet> INPUT_EDGE_RECORD_NON_MANDATORY_ADDITIONAL_FEATURE_COLUMN_SET_TO_KEEP =
			new DataReproducibleParameter<>(DataTableColumnNameLinkedHashSet.class, new SimpleName("inputEdgeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData"), new VfNotes(), "input edge record non mandatory additional feature Column set to be kept int output VfTree", true, null, null);// 
	public DataTableColumnNameLinkedHashSet getInputEdgeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData() {
		return (DataTableColumnNameLinkedHashSet) this.levelSpecificParameterObjectValueMap.get(INPUT_EDGE_RECORD_NON_MANDATORY_ADDITIONAL_FEATURE_COLUMN_SET_TO_KEEP.getName());
	}
	
	/**
	 * parameter for output vftree Metadata name;
	 * mandatory, no default value
	 */
	public static final ReproducibleParameter <MetadataID> OUTPUT_VFTREE_METADATA_ID = 
			new ReproducibleParameter<>(MetadataID.class, new SimpleName("outputVfTreeDataID"), new VfNotes(), "Output VfTree data ID", true, 
					m->{return m.getDataType()==DataType.vfTREE;}, null);// 
	public MetadataID getOutputVfTreeDataID() {
		return (MetadataID) this.levelSpecificParameterObjectValueMap.get(OUTPUT_VFTREE_METADATA_ID.getName());
	}
	
	//////////////////
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap;
	//return all Parameters defined at the SQLOperation level
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap(){
		if(levelSpecificParameterNameMap==null) {
			levelSpecificParameterNameMap = new LinkedHashMap<>();
			
			levelSpecificParameterNameMap.put(INPUT_VFTREE_METADATAID.getName(), INPUT_VFTREE_METADATAID);
			levelSpecificParameterNameMap.put(INPUT_VFTREE_NODE_RECORD_METADATAID.getName(), INPUT_VFTREE_NODE_RECORD_METADATAID);
			levelSpecificParameterNameMap.put(INPUT_VFTREE_EDGE_RECORD_METADATAID.getName(), INPUT_VFTREE_EDGE_RECORD_METADATAID);
			
			levelSpecificParameterNameMap.put(INPUT_NODE_RECORD_NON_MANDATORY_ADDITIONAL_FEATURE_COLUMN_SET_TO_KEEP.getName(), INPUT_NODE_RECORD_NON_MANDATORY_ADDITIONAL_FEATURE_COLUMN_SET_TO_KEEP);
			levelSpecificParameterNameMap.put(INPUT_EDGE_RECORD_NON_MANDATORY_ADDITIONAL_FEATURE_COLUMN_SET_TO_KEEP.getName(), INPUT_EDGE_RECORD_NON_MANDATORY_ADDITIONAL_FEATURE_COLUMN_SET_TO_KEEP);
			
			levelSpecificParameterNameMap.put(OUTPUT_VFTREE_METADATA_ID.getName(), OUTPUT_VFTREE_METADATA_ID);
		}
		return levelSpecificParameterNameMap;
	}
	
	/////////////////////////////////////////////////////
	////=====final fields
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap;
	
	/**
	 * constructor
	 * @param operationLevelParameterObjectValueMap
	 * @param vfTreeTrimmingOperationBaseBaseLevelParameterNameObjectValueMap
	 */
	protected VfTreeTrimmingOperationBase(
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			Map<SimpleName, Object> vfTreeTrimmingOperationBaseBaseLevelParameterNameObjectValueMap) {
		super(operationLevelParameterObjectValueMap);
		
		//always first validate each value objects with the constraints implemented in the Parameter<?> object of each parameter
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			if(!vfTreeTrimmingOperationBaseBaseLevelParameterNameObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given vfTreeTrimmingOperationBaseBaseLevelParameterNameObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		
		this.levelSpecificParameterObjectValueMap = vfTreeTrimmingOperationBaseBaseLevelParameterNameObjectValueMap;
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
	
	
	/**
	 * <p>return the input VfTreeDataMetadata's MetadataID;</p>
	 * note that the node/edge record data of input VfTreeDataMetadata's is not input metadata of {@link VfTreeTrimmingOperationBase};
	 */
	@Override
	public Set<MetadataID> getInputMetadataIDSet() {
		
		Set<MetadataID> ret = new HashSet<>();
		
		ret.add(this.getInputVfTreeMetadataID());
		
		return ret;
	}
	
	/**
	 * return the output VfTreeDataMetadata's MetadataID
	 */
	@Override
	public Set<MetadataID> getOutputMetadataIDSet() {
		Set<MetadataID> ret = new HashSet<>();
		ret.add(this.getOutputVfTreeDataID());
		return ret;
	}
	
	
	/**
	 * !!!!!!!
	 * BE CAUTIOUS about the implementation of this method of {@link VfTreeTrimmingOperationBase};
	 * 
	 * the column sets in the value object of {@link INPUT_NODE_RECORD_OTHER_ADDITIONAL_FEATURE_COLUMN_SET_TO_KEEP} and {@link INPUT_EDGE_RECORD_OTHER_ADDITIONAL_FEATURE_COLUMN_SET_TO_KEEP}
	 * are all independent input columns
	 * 
	 */
	@Override
	public Map<MetadataID, Set<DataTableColumnName>> getInputRecordMetadataIDInputColumnNameSetMap() {
		Map<MetadataID, Set<DataTableColumnName>>  ret = new HashMap<>();
		
		ret.put(
				this.getInputVfTreeNodeRecordMetadataID(), 
				this.getInputNodeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData().getSet());
		ret.put(
				this.getInputVfTreeEdgeRecordMetadataID(), 
				this.getInputEdgeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData().getSet());
		
		return ret;
	}
	
	//////////////////////////////////////////////////////
	@Override
	public abstract VfTreeTrimmingOperationBase reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;
	
	/**
	 * reproduce and return a parameter name value object map of parameters at {@link VfTreeTrimmingOperationBase} level
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
	protected Map<SimpleName, Object> reproduceVfTreeTrimmingOperationBaseLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		//input tree metadata copy index; note that node/edge record data of a Graph data is assigned to the same VCDNode of the graph data, thus have same copy index with the graph data
		int inputVfTreeMetadataCopyIndex = 
				VSAArchiveReproducerAndInserter.getAppliedArchive().lookupCopyIndexOfOperationInputMetadata(
						this.getID(), copyIndex, this.getInputVfTreeMetadataID());
		
		MetadataID reproducedInputVfTreeMetadataID = 
				this.getInputVfTreeMetadataID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, inputVfTreeMetadataCopyIndex);
		
		MetadataID reproducedInputVfTreeNodeRecordMetadataID =
				this.getInputVfTreeNodeRecordMetadataID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, inputVfTreeMetadataCopyIndex);
		
		MetadataID reproducedInputVfTreeEdgeRecordMetadataID =
				this.getInputVfTreeEdgeRecordMetadataID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, inputVfTreeMetadataCopyIndex);
		
		DataTableColumnNameLinkedHashSet reproducedInputNodeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData = 
				this.getInputNodeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData().reproduce(
						VSAArchiveReproducerAndInserter, this.getInputVfTreeNodeRecordMetadataID(), inputVfTreeMetadataCopyIndex);//node and edge record data are assigned to the same VSComponent with the VfTree data
		
		DataTableColumnNameLinkedHashSet reproducedInputEdgeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData = 
				this.getInputEdgeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData().reproduce(
						VSAArchiveReproducerAndInserter, this.getInputVfTreeEdgeRecordMetadataID(), inputVfTreeMetadataCopyIndex);//node and edge record data are assigned to the same VSComponent with the VfTree data
		
		//
		MetadataID reproducedOutputVfTreeDataID = 
				this.getOutputVfTreeDataID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		//////////////////////////////
		Map<SimpleName, Object> ret = new HashMap<>();
		ret.put(INPUT_VFTREE_METADATAID.getName(), reproducedInputVfTreeMetadataID);
		ret.put(INPUT_VFTREE_NODE_RECORD_METADATAID.getName(), reproducedInputVfTreeNodeRecordMetadataID);
		ret.put(INPUT_VFTREE_EDGE_RECORD_METADATAID.getName(), reproducedInputVfTreeEdgeRecordMetadataID);
		
		ret.put(INPUT_NODE_RECORD_NON_MANDATORY_ADDITIONAL_FEATURE_COLUMN_SET_TO_KEEP.getName(), reproducedInputNodeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData);
		ret.put(INPUT_EDGE_RECORD_NON_MANDATORY_ADDITIONAL_FEATURE_COLUMN_SET_TO_KEEP.getName(), reproducedInputEdgeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData);
		ret.put(OUTPUT_VFTREE_METADATA_ID.getName(), reproducedOutputVfTreeDataID);

		return ret;
	}
	

	/////////////////////////////////////////////////////////////////////
	private transient VfTreeDataMetadata inputVfTreeDataMetadata;
	protected transient VfDataTreeReader treeReader;
	protected transient AbstractVfTreeTrimmer trimmer;
	private transient GraphNameBuilder graphNameBuilder;
	private transient VfTreePopulator populator;
	/**
	 * implementation strategy:
	 * 0. check if host VisProjectDBContext is set
//	 * 1. invoke {@link #validateParametersValueConstraints()}
//	 *  	input tree specific validations
	 * 2. general steps
	 * 		1. retrieve and build a vftree based on input {@link VfTreeDataMetadata} with {@link VfDataTreeReader}
	 * 		2. build and perform trimmer
	 * 			reroot tree with {@link VfRerootTreeGenericTrimmer} with the {@link VfDataTreeReader} as input;
	 * 		3. build the name for the node and edge record data and data table of output vftree with {@link GraphNameBuilder}
	 * 		4. populate the data tables with {@link VfTreePopulator} 
	 * 			1. calculate all attributes of each node of the output {@link VfTrimTree} of {@link VfRerootTreeGenericTrimmer} with a {@link VfCalculatorTree}
	 * 			2. create and insert tree node and edge record data table schema
	 * 			3. populate data tables
	 * 		5. build and insert the VfTreeMetadata into the metadata management table;
	 * 		6. insert this operation into operation management table;
	 * @throws SQLException 
	 */
	@Override
	public StatusType call() throws SQLException {
		//0
		if(this.getHostVisProjectDBContext()==null) {
			throw new IllegalArgumentException();
		}
		
		//1 
//		this.validateParametersValueConstraints();
		
		//
		this.inputVfTreeDataMetadata = (VfTreeDataMetadata)this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().lookup(this.getInputVfTreeMetadataID());
		
		////////////////////////////
		////2
		//2.1 VfDataTreeReader
		this.readInputVfTreeDataMetadata();
		
		//2.2 VfRerootTreeGenericTrimmer
		this.buildAndPerformTrimmer();
		
		//2.3
		graphNameBuilder = new GraphNameBuilder(this.getHostVisProjectDBContext(), this.getOutputVfTreeDataID().getName());
		
		//2.4 VfTreePopulator
		this.buildAndPerformTreePopulator();
		
		//2.5
		this.buildAndInsertMetadata();
		
		//2.6
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getOperationManager().insert(this);
		
		return StatusType.FINISHED;
	}
	
	
	private void readInputVfTreeDataMetadata() throws SQLException {
		this.treeReader = new VfDataTreeReader(
				this.getHostVisProjectDBContext(), this.getInputVfTreeMetadataID(),
				this.getInputNodeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData().getSet(),
				this.getInputEdgeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData().getSet());
		
		this.treeReader.perform();
	}
	
	protected abstract void buildAndPerformTrimmer();
	
	private void buildAndPerformTreePopulator() throws SQLException {
		this.populator = new VfTreePopulator(this.trimmer.getOutputTree(), this.getHostVisProjectDBContext(), this.graphNameBuilder.getVertexDataTableName(), this.graphNameBuilder.getEdgeDataTableName());
		this.populator.perform();
	}
	
	/**
	 * 
	 * @throws SQLException
	 */
	private void buildAndInsertMetadata() throws SQLException {
		RecordDataMetadata nodeRecordMetadata = new RecordDataMetadata(
				this.graphNameBuilder.getVertexRecordMetadataName(), VfNotes.makeVisframeDefinedVfNotes(),
				SourceType.STRUCTURAL_COMPONENT,
				this.getOutputVfTreeDataID(),
				null,
				this.populator.getNodeDataTableSchema(),
				true
				);
		RecordDataMetadata edgeRecordMetadata = new RecordDataMetadata(
				this.graphNameBuilder.getEdgeRecordMetadataName(), VfNotes.makeVisframeDefinedVfNotes(),
				SourceType.STRUCTURAL_COMPONENT,
				this.getOutputVfTreeDataID(),
				null,
				this.populator.getEdgeDataTableSchema(),
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
		VfTreeDataMetadata treeMetadata = new VfTreeDataMetadata(
				this.getOutputVfTreeDataID().getName(), VfNotes.makeVisframeDefinedVfNotes(),
				SourceType.RESULT_FROM_OPERATION, this.getID(),
				this.graphNameBuilder.getVertexRecordMetadataName(),
				this.graphNameBuilder.getEdgeRecordMetadataName(),
				this.treeReader.getVfTreeNodeFeature(),
				this.treeReader.getVfTreeEdgeFeature(),
				this.inputVfTreeDataMetadata.getBootstrapIteration()//bootstrap value should be the same with the input tree
				);
		
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().insert(treeMetadata);
	}
	
	
	///////////////////////////////////////////
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
		if (!(obj instanceof VfTreeTrimmingOperationBase))
			return false;
		VfTreeTrimmingOperationBase other = (VfTreeTrimmingOperationBase) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
}
