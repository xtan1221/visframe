package basic.lookup;

import java.io.Serializable;

import basic.process.NonProcessType;
import basic.process.ProcessType;
import context.project.process.AbstractProcessPerformer;

/**
 * base interface for types that are either {@link ProcessType} and/or {@link NonProcessType}
 * 
 * for VisframeUDT types implementing marker interface {@link ProcessType}
 * 		they can be explicitly inserted into the host VisProjectDBContext with a {@link AbstractProcessPerformer}
 * for VisframeUDT types implementing marker interface {@link NonProcessType}
 * 		if not 
 * 
 * 
 * @author tanxu
 *
 */
public interface VisframeUDT extends Serializable, HasID{
//	String getUDTAliasName();

	/**
	 * return the PrimaryKeyID of this Lookupable
	 */
	@Override
	PrimaryKeyID<? extends VisframeUDT> getID();
	
//	/**
//	 * return the set of HasID entities IDs that are directly depending on this entity;
//	 * facilitate the deletion chain implementation in VisProjectDBContext;
//	 * @return
//	 */
//	Set<ID<? extends HasID>> getIDSetDirectlyDependingOnThis();
//	
//	
//	/**
//	 * return the set of HasID entities IDs that are directly depended on by this entity;
//	 * facilitate the validation chain implementation in VisProjectDBContext;
//	 */
//	Set<ID<? extends HasID>> getIDSetDirectlyDependedOnByThis();
	
}
