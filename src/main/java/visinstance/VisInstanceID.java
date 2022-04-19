package visinstance;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import basic.SimpleName;
import basic.lookup.PrimaryKeyID;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.table.lookup.ManagementTableColumn;


/**
 * //the real primary key for a VisInstance is the CoreShapeCFGIDSet;
//when lookup an existing VisInstance object, use the UID;
//when check whether a VisInstance with a specific set of core shapeCFGs is already existing in the host VisProjectDBContext, use the CoreShapeCFGIDSet
 * @author tanxu
 *
 */
public class VisInstanceID implements PrimaryKeyID<VisInstance>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6650298442404014053L;
	
	/////////////////////////
	public static final ManagementTableColumn UID_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("VISINSTANCE_UID"), SQLDataTypeFactory.integerType(), true, false, //unique must be false because it is used by VisInstanceRun as one of its PK attribute
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
			);
	
	/////////////////////
	private final int UID;
	
	/**
	 * constructor
	 * @param UID
	 * @param coreShapeCFGIDSet
	 */
	public VisInstanceID(int UID){
		this.UID = UID;
	}

	public int getUID() {
		return UID;
	}
	
	///////////////////////////////
	
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
	////////////////////////////////////
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
		if (!(obj instanceof VisInstanceID))
			return false;
		VisInstanceID other = (VisInstanceID) obj;
		if (UID != other.UID)
			return false;
		return true;
	}



	@Override
	public String toString() {
		return "VisInstanceID [UID=" + UID + "]";
	}

}
