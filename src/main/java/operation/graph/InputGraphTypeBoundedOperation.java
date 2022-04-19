package operation.graph;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import basic.SimpleName;
import context.project.VisProjectDBContext;
import context.project.process.logtable.StatusType;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.graph.type.GraphMetadataType;
import metadata.graph.type.OperationInputGraphTypeBoundary;
import operation.parameter.Parameter;


/**
 * base class for graph operation whose single input generic graph data's GraphMetadataType is bounded by the operation type;
 * 
 * The operation types requires an input graph data with GraphMetadataType that is falling into an OperationInputGraphTypeBoundary;
 * The validation of the input graph data’s GraphMetadataType will be carried out inside the perform() method;
 * 
 * Input parameter is ComplexName of the graph data’s name, and the GraphMetadataType is not given in the constructor(strong reason to do so!), 
 * thus cannot be validated in the constructor;
 * 
 * note that the OperationInputGraphTypeBoundary for each final subclass of {@link InputGraphTypeBoundedOperation} is fixed, thus, it should be built as a static final field for each final subclass; 
 * 
 * ========================
 * no parameter at this level
 * @author tanxu
 *
 */
public abstract class InputGraphTypeBoundedOperation extends SingleGenericGraphAsInputOperation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6385503559799847543L;

	
	////////////////////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values;
	 * 
	 * always return empty map since there is no level specific parameter;
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildInputGraphTypeBoundedOperationLevelSpecificParameterNameValueObjectMap(){
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		
		return ret;
	}
	
	
	//////////////////////////////
	/**
	 * return all Parameters defined at the GenericSQLOperation level
	 * @return
	 */
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap(){
		return new LinkedHashMap<>();
	}
	
	////////////////////////////////
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap;
	
	
	
	/**
	 * constructor
	 * @param operationLevelParameterObjectValueMap
	 * @param singleGenericGraphAsInputOperationLevelParameterObjectValueMap
	 */
	public InputGraphTypeBoundedOperation(
//			boolean resultedFromReproducing,
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			Map<SimpleName, Object> singleGenericGraphAsInputOperationLevelParameterObjectValueMap,
			Map<SimpleName, Object> inputGraphTypeBoundedOperationLevelParameterObjectValueMap
			) {
		super(operationLevelParameterObjectValueMap, singleGenericGraphAsInputOperationLevelParameterObjectValueMap);
		
		//The validation of the input graph data’s GraphMetadataType will be carried out inside the perform() method;
		//Input parameter is ComplexName of the graph data’s name, and the GraphMetadataType is not given in the constructor(strong reason to do so!), 
		//thus cannot be validated in the constructor
		
		
		this.levelSpecificParameterObjectValueMap = inputGraphTypeBoundedOperationLevelParameterObjectValueMap;
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
	
	/////////////////////////////////////////
//	/**
//	 * input graphs DO NOT need to be VfTREE; can be either {@link DataType#GRAPH} or {@link DataType#vfTREE} 
//	 */
//	@Override
//	public boolean inputGraphMustBeOfVfTreeType() {
//		return false;
//	}
	
	/**
	 * return true if the given observed {@link GraphMetadataType} is consistent with the {@link #getOperationInputGraphTypeBoundary()};
	 * false otherwise;
	 * 
	 * must be invoked in {@link #call()} method before carrying out the operation;
	 * 
	 * @param inputGraphObservedType
	 * @return
	 */
	protected boolean validateInputGraphObservedType(GraphMetadataType inputGraphObservedType) {
		return this.getOperationInputGraphTypeBoundary().isEqualToOrMoreGenericThan(inputGraphObservedType);
	}
	
	//////////////////////////////////////////
	/**
	 * reproduce and return a parameter name value object map of parameters at {@link InputGraphTypeBoundedOperation} level;
	 * empty map since no parameter at this level
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
	 * @return
	 * @throws SQLException 
	 */
	protected Map<SimpleName, Object> reproduceInputGraphTypeBoundedOperationLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		Map<SimpleName, Object> ret = new HashMap<>();
		return ret;
	}
	
	
	/////////////////////////////////////////////////
	/**
	 * return the OperationInputGraphTypeBoundary of the specific subclass;
	 * 
	 * @return
	 */
	public abstract OperationInputGraphTypeBoundary getOperationInputGraphTypeBoundary();
	
	/**
	 * perform the operation with the given VisProjectDBContext;
	 * 
	 * 1. check if the host VisProjectDBContext is set;
	 * 
	 * 2. lookup and retrieve the input {@link GraphDataMetadata} and invoke {@link #validateInputGraphObservedType(GraphMetadataType)} with the observed {@link GraphMetadataType}
	 * 
	 * 3. perform based on specific operation type;
	 * 
	 * @return 
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws InputGraphMetadataTypeNotWithinOperationGraphTypeBoundaryException if the input graph's {@link GraphMetadataType} is not within the {@link OperationInputGraphTypeBoundary} of this operation;
	 */
	@Override
	public abstract StatusType call() throws SQLException, IOException;


	
	///////////////////////////////////////
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
		if (!(obj instanceof InputGraphTypeBoundedOperation))
			return false;
		InputGraphTypeBoundedOperation other = (InputGraphTypeBoundedOperation) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
	
	
}
