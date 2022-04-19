package basic.lookup;

/**
 * a type with an explicit approach to lookup in a {@link VisframeContext}: a LookupManager
 * 
 * may or may not have a UDT and management table in the rdb of host VisProjectDBContext;
 * 
 * for lookup of types such as Metadata, Operation with a UDT, use the {@link UDTLookupable};
 * 
 * 
 * for DataTableSchema and ValueTableSchema lookup, use this interface
 * 
 * 
 * @author tanxu
 *	
 */
public interface HasID{
	/**
	 * return the PrimaryKeyID of this Lookupable
	 */
	ID<? extends HasID> getID();
	
	
	////////////////////////////////////////////
//	/**
//	 * return whether this {@link HasID} entity can be deleted directly from a VisProjectDBContext;
//	 * if true, this entity can be deleted directly and may or may not trigger a deletion chain reaction;
//	 * if false, the deletion of this entity can only be triggered by some other {@link HasID} entity with this method return true as a part of the resulted deletion chain reaction;
//	 * 
//	 * note that for a type of HasID, whether this method return true or false may still depends on the specific information it contains (thus not type specific);
//	 * @return
//	 */
//	boolean isDirectlyDeletable();
	
	
	
//	/**
//	 * whether this HasID object is considered as equal to the given entity in the same VisframeContext;
//	 * 
//	 * this method is used to check a more stringent uniqueness constraints of entities than ID;
//	 * 
//	 * for some types of entities, if they are to be put in the same VisframeContext, ID uniqueness is the only constraints need to be checked;
//	 * 		all HasIDTypeRelationalTableSchema subtypes
//	 * 		Metadata, Operation, CFG, IndependentFreeInputVariableType;
//	 * however, for other types, need to check extra constraints to allow entities co-existing in the same VisframeContext;
//	 * 		CompositinoFunction : assigned targets
//	 * 		VisInstanceRun : CFDGraphIndependetFIVStringValueMap?
//	 * 
//	 * 
//	 * @param entity
//	 * @return
//	 */
//	boolean isVisframeEqualTo(HasID entity);
}
