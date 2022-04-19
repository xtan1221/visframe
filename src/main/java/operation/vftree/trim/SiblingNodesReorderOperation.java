package operation.vftree.trim;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import generic.tree.trim.VfSilblingReorderTrimmer;
import generic.tree.trim.helper.SiblingReorderPattern;
import operation.AbstractOperation;
import operation.Operation;
import operation.parameter.MiscTypeParameter;
import operation.parameter.Parameter;
import operation.vftree.VfTreeTrimmingOperationBase;


/**
 * reorder sibling nodes of a parent node;
 * 
 * see {@link VfSilblingReorderTrimmer}
 * @author tanxu
 * 
 */
public final class SiblingNodesReorderOperation extends VfTreeTrimmingOperationBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5860972854631546784L;
	
	//////////////////////////////////
	public static final SimpleName TYPE_NAME = new SimpleName("SiblingNodesReorderOperation");
	public static final VfNotes TYPE_NOTES = new VfNotes();
	
	
	//////////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildSiblingNodesReorderOperationLevelSpecificParameterNameValueObjectMap(
			SiblingReorderPattern siblingReorderPattern){
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.put(SIBLING_REORDER_PATTERN.getName(), siblingReorderPattern);
		
		return ret;
	}
	
	////////////////////////////////////
	
	////////
	/**
	 * parameter for the SiblingReorderPattern;
	 * 
	 * mandatory, no default value, (input data table content dependent)
	 */
	public static final MiscTypeParameter<SiblingReorderPattern> SIBLING_REORDER_PATTERN= 
			new MiscTypeParameter<>(SiblingReorderPattern.class, new SimpleName("silbingReorderPattern"), new VfNotes(), "sibling reorder pattern", 
					true, null, null);// mandatory, defaultValue, nonNullValueAdditionalConstraints
	public SiblingReorderPattern getSilbingReorderPattern() {
		return (SiblingReorderPattern) this.levelSpecificParameterObjectValueMap.get(SIBLING_REORDER_PATTERN.getName());
	}
	
	////////////////////
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap;
	//return all Parameters defined at the SQLOperation level
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap(){
		if(levelSpecificParameterNameMap==null) {
			levelSpecificParameterNameMap = new LinkedHashMap<>();
			levelSpecificParameterNameMap.put(SIBLING_REORDER_PATTERN.getName(), SIBLING_REORDER_PATTERN);
		}
		return levelSpecificParameterNameMap;
	}
	
	///////////////////////////////////////////////////////
	////=====final fields
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap;
		
	
	/**
	 * constructor
	 * @param operationLevelParameterObjectValueMap
	 * @param vfTreeTrimmingOperationBaseBaseLevelParameterNameObjectValueMap
	 * @param siblingNodesReorderOperationLevelParameterNameObjectValueMap
	 * @throws SQLException 
	 */
	public SiblingNodesReorderOperation(
//			boolean resultedFromReproducing,
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			
			Map<SimpleName, Object> vfTreeTrimmingOperationBaseBaseLevelParameterNameObjectValueMap,
			
			Map<SimpleName, Object> siblingNodesReorderOperationLevelParameterNameObjectValueMap,
			
			boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent
			) {
		super(operationLevelParameterObjectValueMap, vfTreeTrimmingOperationBaseBaseLevelParameterNameObjectValueMap);

		//always first validate each value objects with the constraints implemented in the Parameter<?> object of each parameter
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {

			if(!siblingNodesReorderOperationLevelParameterNameObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given siblingNodesReorderOperationLevelParameterNameObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
			
		}
		
		this.levelSpecificParameterObjectValueMap = siblingNodesReorderOperationLevelParameterNameObjectValueMap;
		
		////
		this.validateParametersValueConstraints(toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent);
	}

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

		
	}
	

	/////////////////////////////////
	@Override
	public SimpleName getOperationTypeName() {
		return TYPE_NAME;
	}


	@Override
	public VfNotes getOperationTypeNotes() {
		return TYPE_NOTES;
	}

	///////////////////////////////////////////////////////
	
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
	
	
	
	///////////////////////////////////////////////////////
	/**
	 * reproduce and return a parameter name value object map of parameters at {@link SiblingNodesReorderOperation} level
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
	private Map<SimpleName, Object> reproduceSiblingNodesReorderOperationLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex){
		Map<SimpleName, Object> ret = new HashMap<>();
		
		ret.put(SIBLING_REORDER_PATTERN.getName(), null);
		
		return ret;
	}

	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @throws SQLException 
	 */
	@Override
	public SiblingNodesReorderOperation reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		return new SiblingNodesReorderOperation(
				this.reproduceAbstractOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceVfTreeTrimmingOperationBaseLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceSiblingNodesReorderOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				false //toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent should be false if the operation is reproduced;
				);
	}
	///////////////////////////////////////////////////////
	//call() method related
	/**
	 * 
	 */
	@Override
	protected void buildAndPerformTrimmer() {
		this.trimmer = new VfSilblingReorderTrimmer(this.treeReader, this.getSilbingReorderPattern());
		this.trimmer.perform();
	}

	
	////////////////////////////////////////////
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
		if (!(obj instanceof SiblingNodesReorderOperation))
			return false;
		SiblingNodesReorderOperation other = (SiblingNodesReorderOperation) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
	
	

}
