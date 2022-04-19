package visinstance.run.calculation.function.composition;

import java.util.HashMap;
import java.util.Map;

import basic.SimpleName;
import basic.lookup.PrimaryKeyID;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.table.lookup.ManagementTableColumn;


/**
 * CFTargetValueTableRunID class that contains a unique id among all CFTargetValueTableRuns in the same host VisProjectDBContext
 * 
 * @author tanxu
 *
 */
public class CFTargetValueTableRunID implements PrimaryKeyID<CFTargetValueTableRun>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 52782558278127312L;
	
	public static final ManagementTableColumn RUN_UID_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("RUN_UID"), SQLDataTypeFactory.integerType(), true, false, //
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
			);
	
	/////////////////////////////
	private final int runUID;
	
	/**
	 * 
	 * @param targetCompositionFunctionID
	 * @param runUID
	 * @param CFDGraphIndependetFIVStringValueMap
	 */
	public CFTargetValueTableRunID(
			int runUID
			){
		this.runUID = runUID;
	}
	
	
	/////////////////////////////////
	public int getRunUID() {
		return runUID;
	}
	
	
	@Override
	public Map<SimpleName, String> getPrimaryKeyAttributeNameStringValueMap() {
		Map<SimpleName, String> ret = new HashMap<>();
		ret.put(RUN_UID_COLUMN.getName(),Integer.toString(this.getRunUID()));
		return ret;
	}

	@Override
	public Map<SimpleName, Boolean> getPrimaryKeyAttributeNameToIgnoreCaseMap() {
		Map<SimpleName, Boolean> ret = new HashMap<>();
		ret.put(RUN_UID_COLUMN.getName(), null);
		return ret;
	}
	
	///////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + runUID;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CFTargetValueTableRunID))
			return false;
		CFTargetValueTableRunID other = (CFTargetValueTableRunID) obj;
		if (runUID != other.runUID)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "CFTargetValueTableRunID [runUID=" + runUID + "]";
	}
}
