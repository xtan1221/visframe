package basic.lookup.project;

import java.sql.Connection;
import java.sql.SQLException;

import basic.lookup.ID;
import basic.lookup.VisframeUDT;
import basic.process.NonProcessType;
import basic.process.NonReproduceableProcessType;
import basic.process.ProcessType;
import basic.process.ReproduceableProcessType;
import context.project.VisProjectDBContext;
import context.project.process.logtable.ProcessLogTableAndProcessPerformerManager;
import basic.lookup.HasID;


/**
 * interface for manager class that manages a {@link HasID} type entities in a VisProjectDBContext's rdb;
 * 
 * @author tanxu
 *
 * @param <T>
 * @param <I>
 */
public interface VisProjectHasIDTypeManager<T extends HasID,I extends ID<T>>{
	/**
	 * return the class of the managed type
	 * @return
	 */
	Class<T> getManagedType();
	
	/**
	 * check if the given entity is the same type or a sub-type of the the type managed by this manager
	 * @param entity
	 * @return
	 */
	default boolean isValidEntity(Object entity) {
		return this.getManagedType().isAssignableFrom(entity.getClass());
	}
	
	/**
	 * return the class of ID type
	 */
	Class<I> getIDType();
	
	/**
	 * check if the given object is the same ID type managed by this manager
	 * @param id
	 * @return
	 */
	default boolean isValidID(Object id) {
		return this.getIDType().isAssignableFrom(id.getClass());
	}
	
	
	/**
	 * return true if the managed type T is a sub-type of ProcessType interface; false otherwise;
	 * 
	 * note that managed type can only be either ProcessType or NonProcessType, but not both;
	 * @return
	 */
	default boolean isOfProcessType() {
		return ProcessType.class.isAssignableFrom(this.getManagedType());
	}
	
	/**
	 * 
	 * @return
	 */
	default boolean isOfReproduceableProcessType() {
		return ReproduceableProcessType.class.isAssignableFrom(this.getManagedType());
	}
	
	default boolean isOfNonReproduceableProcessType() {
		return NonReproduceableProcessType.class.isAssignableFrom(this.getManagedType());
	}
	
	
	/**
	 * return true if the entity type T is a sub-type of NonProcessType interface; false otherwise;
	 * note that managed type can only be either ProcessType or NonProcessType, but not both;
	 * @return
	 */
	default boolean isOfNonProcessType() {
		return NonProcessType.class.isAssignableFrom(this.getManagedType());
	}
	
	/**
	 * return whether the managed type of this manager is a sub-type of VisframeUDT;
	 * @return
	 */
	default boolean isVisframeUDTType() {
		return VisframeUDT.class.isAssignableFrom(this.getManagedType());
	}
	
	/**
	 * return whether there is a management table where to store all the entities of the managed type of this manager;
	 * 
	 * @return
	 */
	default boolean hasManagementTable() {
		return this.isVisframeUDTType();
	}
	
	/////////////////////////////////////////////
	/**
	 * return the host {@link VisProjectDBContext}
	 * @return
	 */
	VisProjectDBContext getHostVisProjectDBContext();
	
	/**
	 * return the {@link VisProjectHasIDTypeManagerController} that hold a singleton object of VisProjectManager for each type of {@link HasID} of the host {@link VisProjectDBContext} of this manager;
	 * @return
	 */
	default VisProjectHasIDTypeManagerController getHasIDTypeManagerController() {
		return this.getHostVisProjectDBContext().getHasIDTypeManagerController();
	}
	
	
	/**
	 * return the {@link Connection} to the rdb of the host {@link VisProjectDBContext} of this VisProjectManager
	 * @return
	 * @throws SQLException 
	 */
	default Connection getVisProjectDBConnection() throws SQLException {
		return this.getHostVisProjectDBContext().getDBConnection();
	}
	
	
	/**
	 * return the {@link ProcessLogTableAndProcessPerformerManager} singleton object of the host {@link VisProjectDBContext} of this manager;
	 * @return
	 */
	default ProcessLogTableAndProcessPerformerManager getProcessLogTableManager() {
		return this.getHostVisProjectDBContext().getProcessLogTableAndProcessPerformerManager();
	}
	
	////////////////////////////////////////////////
	/**
	 * check if the given ID exists or not in the host {@link VisProjectDBContext}'s DB of this manager
	 * @param id
	 * @return
	 * @throws SQLException 
	 */
	boolean checkIDExistence(I id) throws SQLException;
	
//	/**
//	 * return true if there is an existing entity that is visframeEquils with the given entity exist;
//	 * 
//	 * false otherwise;
//	 * @param t
//	 * @return
//	 */
//	boolean checkVisframeDuplicateExistence(T t);
	
	
	/**
	 * insert the given entity of the managed type of this manager into the DB of the host {@link VisProjectDBContext};
	 * 
	 * 
	 * 
	 * @param t
	 * @throws SQLException 
	 */
	void insert(T t) throws SQLException;
	
	
	//////////////////////////////////////////////
//	/**
//	 * return whether the entity of the given ID is a process or not;
//	 * 
//	 * if {@link #hasManagementTable()} returns false, always return false;
//	 * else if the {@link #isOfProcessType()} method returns false, return false;
//	 * else if the {@link #isOfNonProcessType()} method returns true(type can be both process or non-process, need to check the specific entity)
//	 * 		if the INDEPENDENT column value of the given ID is false
//	 * 			return false;
//	 * 		else 
//	 * 			return true;
//	 * else
//	 * 		return true;
//	 * @param id
//	 * @return
//	 * @throws SQLException 
//	 */
//	default boolean isProcess(I id) throws SQLException {
//		if(!this.hasManagementTable()) {
//			return false;
//		}
//		
//		if(!this.isOfProcessType()) {
//			return false;
//		}
//		
//		if(this.isOfNonProcessType()) {
//			return this.getIsReproducedColumnValue(id);
//		}else {
//			return true;
//		}
//	}
	
//	/**
//	 * return the value of column {@link VisframeUDTManagementProcessRelatedTableColumnFactory#isReproducedColumn} value 
//	 * of the entity of the given ID in the management table;
//	 * 
//	 * this column is only present in the management table schema of {@link ReproduceableProcessType} VisframeUDTs;
//	 * 
//	 * if {@link #hasManagementTable()} returns false, throw VisframeException;
//	 * if the VisframeUDT type is not of {@link ReproduceableProcessType}, throw VisframeException;
//	 * 
//	 * @param id
//	 * @return
//	 * @throws SQLException 
//	 */
//	boolean getIsReproducedColumnValue(I id) throws SQLException;
//	
//	
//	/**
//	 * add the given ID to the DEPENDENT_PROCESS_ID_SET column of the entity of the given ID; only valid for {@link ProcessType} VisframeUDT entity;
//	 * 
//	 * @param dependedProcessEntityID
//	 * @param dependentProcessID
//	 * @throws SQLException 
//	 */
//	void addDependentProcessID(ID<? extends HasID> dependedProcessEntityID, PrimaryKeyID<? extends VisframeUDT> dependentProcessID) throws SQLException;
//	
	
	/**
	 * delete the entity with the given id;
	 * need to first check if the given ID is of the ID type managed by this manager
	 * if not, throw IllegalArgumentException;
	 * else, downcast the given id to the specific ID type first, then perform the delete  
	 * @param id
	 * @throws SQLException
	 */
	void delete(ID<? extends HasID> id) throws SQLException;
	
	
	
	
//	/**
//	 * delete an existing {@link HasID} entity with the given ID from the rdb of the host VisProjectDBContext;
//	 * 
//	 * 1. retrieve the target entity E;
//	 * 		for VisfameUDT type, retrieve the object 
//	 * 		for RelationalTableSchema type, ???
//	 * 
//	 * 2. find out all depending Lookupable entities E2 (on E) of any type;
//	 * 		for each E2, invoke the corresponding VisProjectManagerBase's delete(...) method of those Lookupable object with E2's ID;
//	 * 
//	 * 3. delete E from the host VisProjectDBContext;
//	 * 		for VisfameUDT type, use DELETE ... FROM ... WHERE sql string;
//	 * 		for RelationalTableSchema type, use DROP TABLE ... sql string;
//	 * 		
//	 * @param id
//	 * @throws SQLException 
//	 */
//	void delete(I id) throws SQLException;
	
}
