package operation;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.MetadataID;
import operation.parameter.Parameter;
import operation.parameter.ReproducibleParameter;
import operation.parameter.SimpleReproducibleParameter;

/**
 * base class for Operation with the basic parameters shared by all Operation types defined;
 * 
 * @author tanxu
 *
 */
public abstract class AbstractOperation implements Operation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7289747480850481456L;
	
	////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildAbstractOperationLevelSpecificParameterNameValueObjectMap(OperationName name, VfNotes notes){
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.put(INSTANCE_ID.getName(), new OperationID(name));
		ret.put(NOTES.getName(), notes);
		return ret;
	}
	
	//////////////////////////////////
	/////====base Operation level {@link Parameter}s, shared by all Operation subtypes
	/**
	 * parameter for the ID of this Operation instance;
	 */
	public static final ReproducibleParameter<OperationID> INSTANCE_ID = 
			new ReproducibleParameter<>(OperationID.class, new SimpleName("instance_ID"), VfNotes.makeVisframeDefinedVfNotes(), "Operation Instance ID", true, null, null); 
	
	/**
	 * parameter for the notes of this Operation instance
	 */
	public static final SimpleReproducibleParameter<VfNotes> NOTES = 
			new SimpleReproducibleParameter<>(VfNotes.class, new SimpleName("notes"), VfNotes.makeVisframeDefinedVfNotes(), "Operation Instance Notes", true, null, null); 
	
	
	/////////////////////
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap;
	/**
	 * return all OperationParameters defined at the AbstractOperation level including the name and notes of the operation instance
	 * @return
	 */
	private static Map<SimpleName, Parameter <?>> levelSpecificParameterNameMap(){
		if(levelSpecificParameterNameMap==null) {
			levelSpecificParameterNameMap=new HashMap<>();
			levelSpecificParameterNameMap.put(INSTANCE_ID.getName(), INSTANCE_ID);
			levelSpecificParameterNameMap.put(NOTES.getName(), NOTES);
		}
		
		return levelSpecificParameterNameMap;
	}
	
	
	///======================================
	////=====fields
	/**
	 * map from parameter name to the value object of parameters defined at {@link AbstractOperation} level
	 */
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap;
	
	
	///////////
	/**
	 * full set of input MetadataIDs
	 */
	protected transient Set<MetadataID> inputMetadataIDSet;
	
	/**
	 * host VisProjectDBContext of this Operation
	 */
	private transient VisProjectDBContext hostVisProjectDBContext;
	
	/**
	 * constructor
	 * @param operationLevelParamterObjectValueMap
	 */
	protected AbstractOperation(
			Map<SimpleName, Object> abstractOperationLevelParameterObjectValueMap) {
		//only validate whether all required parameters are present in the given map;
		//the validation of parameter values are done in {@link #validateParametersValueConstraints()}, which is invoked only at the beginning of call() method
		//this is because null value is allowed for mandatory parameter with null default values and are dependent on input data table content when reproducing; 
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			if(!abstractOperationLevelParameterObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given abstractOperationLevelParameterObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		
		this.levelSpecificParameterObjectValueMap = abstractOperationLevelParameterObjectValueMap;
	}
	
	/**
	 * validate the constraints of parameter values of all parameters of current and above levels 
	 * 
	 * 1. must be invoked at the end of the constructor of each final sub class of {@link Operation};
	 * 		thus the host VisProjectDBContext should NOT be used in this method;
	 * 
	 * 2. must be implemented by every level of {@link Operation} subclass;
	 * 
	 * implementation strategy:
	 * 		1. invoke super.validateParameterValue() to validate all above level parameters' values if this class is extending a super class;
	 * 		2. invoke the {@link Parameter#validateObjectValue(Object)} for each parameter;
	 * 		3. validate additional inter-parameter constraints at current and above class level if any;
	 * 
	 * throws IllegalArgumentException if any constraints is violated;
	 * 
	 * @throws SQLException 
	 */
	protected void validateParametersValueConstraints(boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent) {
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			Parameter<?> parameter = levelSpecificParameterNameMap().get(parameterName);
			
			if(!parameter.validateObjectValue(this.levelSpecificParameterObjectValueMap.get(parameterName), toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent)){
				throw new IllegalArgumentException("invalid value object found for operationLevelParameter:"+parameterName);
			}
		}
	}

	///////////////////////////////
	@Override
	public VfNotes getNotes() {
		return (VfNotes)this.levelSpecificParameterObjectValueMap.get(NOTES.getName());
	}

	@Override
	public OperationID getID() {
		return (OperationID)this.levelSpecificParameterObjectValueMap.get(INSTANCE_ID.getName());
	}
	
	@Override
	public OperationName getName() {
		return this.getID().getInstanceName();
	}
	
	////////////////////////////////////////////
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<SimpleName, Parameter<?>> getAllParameterNameMapOfCurrentAndAboveLevels() {
		return levelSpecificParameterNameMap();
	}
	
	@Override
	public Map<SimpleName, Object> getAllParameterNameValueObjectMapOfCurrentAndAboveLevels() {
		return this.levelSpecificParameterObjectValueMap;
	}
	
	
	/**
	 * since {@link AbstractOperation} is the root class of {@link Operation} hierarchy, if the given parameter is not in {@link #getLevelSpecificParameterNameMap()}, throw {@link IllegalArgumentException}
	 */
	@Override
	public void setParameterValueObject(SimpleName parameterName, Object value) {
		if(levelSpecificParameterNameMap().containsKey(parameterName)) {
			this.setLevelSpecificParameterValueObject(parameterName, value);
		}else {
			throw new IllegalArgumentException("given parameterName is not identified:"+parameterName.getStringValue());
		}
	}
	
	@Override
	public void setLevelSpecificParameterValueObject(SimpleName parameterName, Object value) {
//		if(!levelSpecificParameterNameMap().get(parameterName).validateObjectValue(value, this.isReproduced())) {
//			throw new IllegalArgumentException("given parameter value object is invalid:"+value);
//		}
		
		this.levelSpecificParameterObjectValueMap.put(parameterName, value);
	}
	
	//////////////////////////////
	
	/**
	 * reproduce and return a parameter name value object map of parameters at {@link AbstractOperation} level
	 * 
	 * 
	 * note that for parameters with {@link Parameter#isInputDataTableContentDependent()} returning false,
	 * the reproduced value should be normally generated;
	 * 
	 * for parameters with {@link Parameter#isInputDataTableContentDependent()} returning true, 
	 * the value for the parameter should be null;
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @return
	 * @throws SQLException 
	 */
	protected Map<SimpleName, Object> reproduceAbstractOperationLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
		Map<SimpleName, Object> reproducedAbstractOperationLevelParameterObjectValueMap = new HashMap<>();
		
		reproducedAbstractOperationLevelParameterObjectValueMap.put(INSTANCE_ID.getName(),this.getID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex));
		reproducedAbstractOperationLevelParameterObjectValueMap.put(NOTES.getName(),this.getNotes().reproduce());
		
		return reproducedAbstractOperationLevelParameterObjectValueMap;
	}
	
	/**
	 * 
	 * @throws SQLException 
	 * 
	 */
	@Override
	public abstract AbstractOperation reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;
	
	
	
	/////////////////////////////////
	@Override
	public void setHostVisProjectDBContext(VisProjectDBContext hostVisProjectDBContext) {
		this.hostVisProjectDBContext = hostVisProjectDBContext;
	}
	
	@Override
	public VisProjectDBContext getHostVisProjectDBContext() {
		return this.hostVisProjectDBContext;
	}

	
	/////////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((levelSpecificParameterObjectValueMap == null) ? 0
				: levelSpecificParameterObjectValueMap.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AbstractOperation))
			return false;
		AbstractOperation other = (AbstractOperation) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}

	
	
}
