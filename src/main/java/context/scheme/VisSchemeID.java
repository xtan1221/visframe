package context.scheme;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import basic.SimpleName;
import basic.lookup.PrimaryKeyID;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.table.lookup.ManagementTableColumn;

/**
 * 
 * @author tanxu
 *
 */
public class VisSchemeID implements PrimaryKeyID<VisScheme>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1882193705582207787L;
	
	///////////////////
	public static final ManagementTableColumn UID_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("UID"), SQLDataTypeFactory.integerType(), true, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
			);
	
	////////////////////////////////////
	private final int UID;
	
	/**
	 * constructor
	 * @param UID
	 */
	public VisSchemeID(int UID){
		this.UID = UID;
	}

	
	public int getUID() {
		return UID;
	}
	
	
	@Override
	public Map<SimpleName, String> getPrimaryKeyAttributeNameStringValueMap() {
		Map<SimpleName, String> ret = new HashMap<>();
		ret.put(UID_COLUMN.getName(),Integer.toString(this.getUID()));
		return ret;
	}
	
	@Override
	public Map<SimpleName, Boolean> getPrimaryKeyAttributeNameToIgnoreCaseMap() {
		Map<SimpleName, Boolean> ret = new LinkedHashMap<>();
		ret.put(UID_COLUMN.getName(), null);
		return ret;
	}
	//////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + UID;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VisSchemeID))
			return false;
		VisSchemeID other = (VisSchemeID) obj;
		if (UID != other.UID)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "VisSchemeID [UID=" + UID + "]";
	}
}
