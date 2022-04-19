package basic.lookup.scheme;

import java.io.Serializable;
import java.util.Map;

import basic.lookup.ID;
import basic.lookup.Lookup;
import basic.lookup.HasID;

/**
 * marker interface for Lookups in VisScheme
 * @author tanxu
 *
 * @param <T>
 */
public interface VisSchemeLookup<T extends HasID, I extends ID<T>> extends Lookup<T,I>, Serializable{
	/**
	 * return the map from the lookupable's ID to the lookupable entity;
	 * @return
	 */
	Map<I,T> getMap();
	
	/**
	 * retrieve the Lookupable entity with the given ID from this Lookup
	 * @param id
	 * @return
	 */
	default T lookup(I id) {
		return this.getMap().get(id);
	}
	
}
