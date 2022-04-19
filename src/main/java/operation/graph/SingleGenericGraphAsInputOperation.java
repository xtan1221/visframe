package operation.graph;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.MetadataID;
import operation.AbstractOperation;
import operation.Operation;
import operation.parameter.Parameter;
import operation.parameter.ReproducibleParameter;


/**
 * base class for operation with a single generic graph(either GRAPH or vfTREE data type) as input Metadata; 
 * @author tanxu
 *
 */
public abstract class SingleGenericGraphAsInputOperation extends AbstractOperation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2090785025228474596L;
	
	//////////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildSingleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap(
			MetadataID inputGenericGraphMetadataID){
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.put(INPUT_GENERIC_GRAPH_METADATAID.getName(), inputGenericGraphMetadataID);
		
		return ret;
	}
	
	////////////////////////////////////
	/**
	 * parameter for the single generic graph data MetadataID;
	 * note that here MetadataID is used rather than MetadataName since the DataType might be GRAPH or VfTREE;
	 */
	public static final ReproducibleParameter<MetadataID> INPUT_GENERIC_GRAPH_METADATAID = 
			new ReproducibleParameter<>(MetadataID.class, new SimpleName("inputGenericGraphMetadataID"), VfNotes.makeVisframeDefinedVfNotes(), "Input generic graph data MetadataID", true, 
					m->{return m.getDataType().isGenericGraph();}, //additional constraints
					null) ;//
	public MetadataID getInputGenericGraphMetadataID() {
		return (MetadataID)this.levelSpecificParameterObjectValueMap.get(INPUT_GENERIC_GRAPH_METADATAID.getName());
	}
	
	
	/////////////////////
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap;
	/**
	 * return all Parameters defined at the GenericSQLOperation level
	 * @return
	 */
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap(){
		if(levelSpecificParameterNameMap==null) {
			levelSpecificParameterNameMap = new LinkedHashMap<>();
			levelSpecificParameterNameMap.put(INPUT_GENERIC_GRAPH_METADATAID.getName(), INPUT_GENERIC_GRAPH_METADATAID);
		}
		return levelSpecificParameterNameMap;
	}
	
	////////////////////////////////
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap;
	
	
	/**
	 * constructor
	 * @param operationLevelParameterObjectValueMap
	 * @param singleGenericGraphAsInputOperationLevelParameterObjectValueMap
	 */
	protected SingleGenericGraphAsInputOperation(
//			boolean resultedFromReproducing,
			
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			
			Map<SimpleName, Object> singleGenericGraphAsInputOperationLevelParameterObjectValueMap
			) {
		super(operationLevelParameterObjectValueMap);
		//validations
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			if(!singleGenericGraphAsInputOperationLevelParameterObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given singleGenericGraphAsInputOperationLevelParameterObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		
		this.levelSpecificParameterObjectValueMap = singleGenericGraphAsInputOperationLevelParameterObjectValueMap;
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
		//input MetadataID must be of either GRAPH or vfTREE data type
		MetadataID inputGenericGraphDataMetadata = this.getInputGenericGraphMetadataID();
		if(!inputGenericGraphDataMetadata.getDataType().isGenericGraph()) {
			throw new IllegalArgumentException("input MetadataID is not of GRAPH or vfTREE data type!");
		}
		
//		if(this.inputGraphMustBeOfVfTreeType()) {
//			if(!this.getInputGenericGraphMetadataID().getDataType().equals(DataType.vfTREE)) {
//				throw new IllegalArgumentException("given input generic graph data is not of vfTree type when inputGraphMustBeOfVfTreeType is true!");
//			}
//		}
	}
	
	
	//////////////////////
//	/**
//	 * return whether the input graph data must be of VfTreeDataMetadata or any GraphDataMetadata
//	 * @return
//	 */
//	public abstract boolean inputGraphMustBeOfVfTreeType();
	
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
	 * 
	 */
	@Override
	public Set<MetadataID> getInputMetadataIDSet() {
		if(this.inputMetadataIDSet==null) {
			this.inputMetadataIDSet = new HashSet<>();
			this.inputMetadataIDSet.add(this.getInputGenericGraphMetadataID());
		}
		
		return inputMetadataIDSet;
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * reproduce and return a parameter name value object map of parameters at {@link SingleGenericGraphAsInputOperation} level
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
	protected Map<SimpleName, Object> reproduceSingleGenericGraphAsInputOperationLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
		//first find out the copy index of the VCCLNode of input generic graph data of this operation;
		int inputGenericGraphMetadataIDCopyIndex = VSAArchiveReproducerAndInserter.getAppliedArchive().lookupCopyIndexOfOperationInputMetadata(
				this.getID(), copyIndex, this.getInputGenericGraphMetadataID());
		
		MetadataID reproducedInputGenericGraphMetadataID = 
				this.getInputGenericGraphMetadataID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, inputGenericGraphMetadataIDCopyIndex);
		
		//////////
		Map<SimpleName, Object> reproducedSingleGenericGraphAsInputOperationLevelParameterObjectValueMap = new HashMap<>();
		reproducedSingleGenericGraphAsInputOperationLevelParameterObjectValueMap.put(
				INPUT_GENERIC_GRAPH_METADATAID.getName(),
				reproducedInputGenericGraphMetadataID);
		
		return reproducedSingleGenericGraphAsInputOperationLevelParameterObjectValueMap;
	}

	
	//////////////////////////////////////////
	/**
	 * 
	 */
	@Override
	public abstract SingleGenericGraphAsInputOperation reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;

	
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
		if (!(obj instanceof SingleGenericGraphAsInputOperation))
			return false;
		SingleGenericGraphAsInputOperation other = (SingleGenericGraphAsInputOperation) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
	
	

}
