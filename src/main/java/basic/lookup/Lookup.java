package basic.lookup;

import java.sql.SQLException;

/**
 * lookup manager for the whole set of entities of a type of {@link HasID} in a {@link VisframeContext}
 * 
 * @author tanxu
 *
 * @param <T> lookupable type
 * @param <I> ID
 */
public interface Lookup<T extends HasID, I extends ID<T>> {
	
	/**
	 * retrieve the lookupable entity with the given ID from this {@link Lookup}
	 * 
	 * @param id
	 * @return
	 * @throws SQLException only applicable if the entity is retrieved from a DB;
	 */
	T lookup(I id) throws SQLException;
	
}
