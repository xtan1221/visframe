package visinstance.run.calculation.function.composition;

import java.util.Map;

import basic.SimpleName;
import function.composition.CompositionFunctionID;
import rdb.table.value.type.CFTargetValueTableSchema;
import rdb.table.value.type.CFTargetValueTableSchemaID;
import visinstance.run.calculation.IndependentFIVTypeIDStringValueMap;

/**
 * 
 * 
 * @author tanxu
 *
 */
public class CFTargetValueTableRunImpl implements CFTargetValueTableRun {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4973783166320512994L;
	
	
	/////////////////////////////////////
	private final int runUID;
	private final CompositionFunctionID compositionFunctionID;
	private final IndependentFIVTypeIDStringValueMap independetFIVTypeStringValueMap;
	private final CFTargetValueTableSchema CFTargetValueTableSchema;
	private final Map<SimpleName, SimpleName> targetNameColumnNameMap;
	/**
	 * 
	 * @param runUID
	 * @param compositionFunctionID
	 * @param independetFIVTypeStringValueMap
	 * @param valueTableSchemaID
	 */
	public CFTargetValueTableRunImpl(
			int runUID,
			CompositionFunctionID compositionFunctionID,
			IndependentFIVTypeIDStringValueMap independetFIVTypeStringValueMap,
			CFTargetValueTableSchema CFTargetValueTableSchema,
			Map<SimpleName, SimpleName> targetNameColumnNameMap
			){
		if(compositionFunctionID==null)
			throw new IllegalArgumentException("given compositionFunctionID cannot be null!");
		if(independetFIVTypeStringValueMap==null)
			throw new IllegalArgumentException("given independetFIVTypeStringValueMap cannot be null!");
		if(CFTargetValueTableSchema==null)
			throw new IllegalArgumentException("given CFTargetValueTableSchema cannot be null!");
		if(targetNameColumnNameMap==null||targetNameColumnNameMap.isEmpty())
			throw new IllegalArgumentException("given targetNameColumnNameMap cannot be null or empty!");
		
		
		
		this.runUID = runUID;
		this.compositionFunctionID = compositionFunctionID;
		this.independetFIVTypeStringValueMap = independetFIVTypeStringValueMap;
		this.CFTargetValueTableSchema = CFTargetValueTableSchema;
		this.targetNameColumnNameMap = targetNameColumnNameMap;
	}
	
	
	///////////////////////////////////////
	@Override
	public CompositionFunctionID getTargetCompositionFunctionID() {
		return compositionFunctionID;
	}

	@Override
	public IndependentFIVTypeIDStringValueMap getCFDGraphIndependetFIVStringValueMap() {
		return independetFIVTypeStringValueMap;
	}

	@Override
	public int getRunUID() {
		return runUID;
	}
	
	@Override
	public CFTargetValueTableSchemaID getTableSchemaID() {
		return this.CFTargetValueTableSchema.getID();
	}


	@Override
	public CFTargetValueTableSchema getValueTableSchema() {
		return this.CFTargetValueTableSchema;
	}

	@Override
	public Map<SimpleName, SimpleName> getTargetNameColumnNameMap() {
		return this.targetNameColumnNameMap;
	}



	////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((CFTargetValueTableSchema == null) ? 0 : CFTargetValueTableSchema.hashCode());
		result = prime * result + ((compositionFunctionID == null) ? 0 : compositionFunctionID.hashCode());
		result = prime * result
				+ ((independetFIVTypeStringValueMap == null) ? 0 : independetFIVTypeStringValueMap.hashCode());
		result = prime * result + runUID;
		result = prime * result + ((targetNameColumnNameMap == null) ? 0 : targetNameColumnNameMap.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CFTargetValueTableRunImpl))
			return false;
		CFTargetValueTableRunImpl other = (CFTargetValueTableRunImpl) obj;
		if (CFTargetValueTableSchema == null) {
			if (other.CFTargetValueTableSchema != null)
				return false;
		} else if (!CFTargetValueTableSchema.equals(other.CFTargetValueTableSchema))
			return false;
		if (compositionFunctionID == null) {
			if (other.compositionFunctionID != null)
				return false;
		} else if (!compositionFunctionID.equals(other.compositionFunctionID))
			return false;
		if (independetFIVTypeStringValueMap == null) {
			if (other.independetFIVTypeStringValueMap != null)
				return false;
		} else if (!independetFIVTypeStringValueMap.equals(other.independetFIVTypeStringValueMap))
			return false;
		if (runUID != other.runUID)
			return false;
		if (targetNameColumnNameMap == null) {
			if (other.targetNameColumnNameMap != null)
				return false;
		} else if (!targetNameColumnNameMap.equals(other.targetNameColumnNameMap))
			return false;
		return true;
	}

	
	@Override
	public String toString() {
		return "CFTargetValueTableRunImpl [runUID=" + runUID + "]";
	}

	
}
