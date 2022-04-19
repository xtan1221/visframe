package operation.vftree.trim;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import generic.tree.reader.projectbased.VfDataTreeReader;
import generic.tree.trim.VfRerootTreeGenericTrimmer;
import generic.tree.trim.helper.PositionOnTree;
import operation.AbstractOperation;
import operation.Operation;
import operation.parameter.MiscTypeParameter;
import operation.parameter.Parameter;
import operation.vftree.VfTreeTrimmingOperationBase;

/**
 * reroot tree 
 * 
 * see {@link VfRerootTreeGenericTrimmer}
 * @author tanxu
 * 
 */
public final class RerootTreeOperation extends VfTreeTrimmingOperationBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2624080329592289482L;

	/////////////
	public static final SimpleName TYPE_NAME = new SimpleName("RerootTreeOperation");
	public static final VfNotes TYPE_NOTES = new VfNotes();
	
	//////////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildRerootTreeOperationLevelSpecificParameterNameValueObjectMap(
			PositionOnTree newRootPositionOnTree){
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.put(NEW_ROOT_POSITION_ON_TREE.getName(), newRootPositionOnTree);
		
		return ret;
	}
	
	////////////////////////////////////
	
	/**
	 * parameter for the position of the new root on the tree
	 * mandatory, no default value, input data table content dependent;
	 * 
	 * note that it is not allowed to reroot the tree with the existing root node;
	 */
	public static final MiscTypeParameter<PositionOnTree> NEW_ROOT_POSITION_ON_TREE = 
			new MiscTypeParameter<>(
					PositionOnTree.class, 
					new SimpleName("newRootPositionOnTree"), VfNotes.makeVisframeDefinedVfNotes(), "NEW_ROOT_POSITION_ON_TREE", 
					true, null, //mandatory, default value
					e->{return !e.isOnRootNode();} //non null value constraints; new root node cannot be on existing root node
					);
	
	public PositionOnTree getNewRootPositionOnTree() {
		return (PositionOnTree)this.levelSpecificParameterObjectValueMap.get(NEW_ROOT_POSITION_ON_TREE.getName());
	}
	
	////////////////////////
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap;

	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap(){
		
		if(levelSpecificParameterNameMap==null) {
			levelSpecificParameterNameMap = new LinkedHashMap<>();
			levelSpecificParameterNameMap.put(NEW_ROOT_POSITION_ON_TREE.getName(), NEW_ROOT_POSITION_ON_TREE);
		}
		return levelSpecificParameterNameMap;
	}
	
	
	////=====final fields
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap;
		
	/**
	 * constructor
	 * @param operationLevelParameterObjectValueMap
	 * @param singleGenericGraphAsInputOperationLevelParameterObjectValueMap
	 * @param vfTreeTrimmingOperationBaseBaseLevelParameterNameObjectValueMap
	 * @param rerootTreeOperationLevelParameterNameObjectValueMap
	 * @param toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent
	 * @throws SQLException 
	 */
	public RerootTreeOperation(
//			boolean resultedFromReproducing,
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			
			Map<SimpleName, Object> vfTreeTrimmingOperationBaseBaseLevelParameterNameObjectValueMap,
			
			Map<SimpleName, Object> rerootTreeOperationLevelParameterNameObjectValueMap,
			
			boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent

			) {
		super(operationLevelParameterObjectValueMap, vfTreeTrimmingOperationBaseBaseLevelParameterNameObjectValueMap);
		
		//always first validate each value objects with the constraints implemented in the Parameter<?> object of each parameter
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			if(!rerootTreeOperationLevelParameterNameObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given rerootTreeOperationLevelParameterNameObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		
		this.levelSpecificParameterObjectValueMap = rerootTreeOperationLevelParameterNameObjectValueMap;
		
		
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
		//
	}
	
	
	///////////////////////////
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
	
	///////////////////////////////////////////////////////////////
	/**
	 * reproduce and return a parameter name value object map of parameters at {@link RerootTreeOperation} level
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
	private Map<SimpleName, Object> reproduceRerootTreeOperationLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex){
		Map<SimpleName, Object> ret = new HashMap<>();
		
		ret.put(NEW_ROOT_POSITION_ON_TREE.getName(), null);
		
		return ret;
	}

	/**
	 * reproduce and return a new RerootTreeOperation of this one;
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @throws SQLException 
	 */
	@Override
	public RerootTreeOperation reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		return new RerootTreeOperation(
				this.reproduceAbstractOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceVfTreeTrimmingOperationBaseLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceRerootTreeOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				false //toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent should be false if the operation is reproduced;
				);
	}
	
	
	/////////////////////////////////////////////////////////////////////
	/////////call() method related
	/**
	 * reroot tree with {@link VfRerootTreeGenericTrimmer} with the {@link VfDataTreeReader} as input;
	 */
	@Override
	protected void buildAndPerformTrimmer() {
		this.trimmer = new VfRerootTreeGenericTrimmer(
				this.treeReader, this.getNewRootPositionOnTree());
		this.trimmer.perform();
	}

	
	//////////////////////////////////
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
		if (!(obj instanceof RerootTreeOperation))
			return false;
		RerootTreeOperation other = (RerootTreeOperation) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
	
	
	
	
}
