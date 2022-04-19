package basic.lookup;

import java.util.Map;

import basic.SimpleName;

/**
 * a group of attributes and their values of a unique object of a specific type of UDTLookupable;
 * 
 * the contained attributes name and value string can be used to build the sql query string's where condition to query the lookup table of the UDTLookupable type in the rdb of host VisProjectDBContext;
 * 
 * @author tanxu
 *
 */
public interface PrimaryKeyID<T extends VisframeUDT> extends ID<T>{
	/**
	 * return the map from primary key attribute name to the string value of this PrimaryKeyID object
	 * @return
	 */
	Map<SimpleName, String> getPrimaryKeyAttributeNameStringValueMap();
	
	/**
	 * return the map from the primary key attribute name to whether to ignore case when comparing the value string for equity checking;
	 * 		
	 * should be non-null if corresponding {@link ManagementTableColumn} is of SQL string type; should be NULL for other SQL types
	 * 		specifically, for all attribute of VfNameString type, should always be true;
	 * 
	 * facilitate to determine whether to ignore case when query from corresponding management table;
	 * 
	 * @return
	 */
	Map<SimpleName, Boolean> getPrimaryKeyAttributeNameToIgnoreCaseMap();
	
	/**
	 * implemented by each final subtype
	 * @return
	 */
	@Override
	int hashCode();
	/**
	 * implemented by each final subtype
	 * @return
	 */
	@Override
	boolean equals(Object o);
}
