package basic.lookup.project.type;

import java.sql.Timestamp;
import java.util.Map;

import basic.SimpleName;
import basic.lookup.PrimaryKeyID;
import basic.lookup.VisframeUDT;
import context.project.process.logtable.VfIDCollection;


/**
 * contains a row from a VisframeUDT management table with the core set of information;
 * 
 * note that the column set of management table schema are different between different types of VisframeUDT;
 * 
 * 1. primary key attribute columns are different among each VisframeUDT types (trivial) 
 * 2. UDT object column is shared by all VisframeUDT types;
 * note that primary key attributes columns are implicitly included in the UDT object, thus no need to put it as a field;
 * =======================
 * 3. VisframeUDT specific columns that are not related with process performer of the entity or insertion process;
 * 4. process related columns (either the process itself or the insertion process)
 * 		see columns in {@link VisframeUDTManagementProcessRelatedTableColumnFactory}
 * 		and the {@link VisframeUDTTypeManagerBase#getProcessRelatedColumnList()} for details;
 * 	
 * @author tanxu
 *
 * @param <T>
 * @param <I>
 */
public class VisframeUDTManagementTableRow<T extends VisframeUDT,I extends PrimaryKeyID<T>> {
	//
	private final VisframeUDTTypeManagerBase<T,I> manager;
	
	
	/////////////////
	private final T entity;
	
	///////////
	
	///////////////////////
	//all VisframeUDT types
	private final Timestamp insertionTimestamp;
	//all visframeUDT types except for CFTargetValueTableRun
	private final PrimaryKeyID<? extends VisframeUDT> insertionProcessID;
	private final Boolean temporary;
	
	
	//process type visframeUDT types; null for other types
	private final VfIDCollection baseProcessIDSet;//all ProcessType
	private final VfIDCollection dependentProcessIDSet;//all ProcessType
	private final VfIDCollection insertedNonProcessIDSet; //ProcessType except for VisSchemeAppliedArchiveReproducedAndInsertedInstance and VisInstance;
	private final VfIDCollection insertedProcessIDSet; //VisSchemeAppliedArchiveReproducedAndInsertedInstance specific
	
	private final Boolean reproduced;//ReproduceableProcessType type specific; null for other types
	private final String VSAAReproducedAndInsertedInstanceUID;////ReproduceableProcessType type specific; null for other types and for ReproduceableProcessType entity that is not reproduced ;
	
	/**
	 * CFTargetValueTableRun specific, null for other types
	 */
	private final VfIDCollection employerVisInstanceRunIDSet;
	/**
	 * VisInstanceRun specific; null for other types
	 */
	private final VfIDCollection involvedCfTargetValueTableRunIDSet;
	
	/////////////
	/**
	 * type specific column name object value map;
	 */
	private final Map<SimpleName, Object> typeSpecificAttributeNameObjectValueMap;
	
	/**
	 * constructor
	 * @param manager
	 * @param entity
	 * @param insertionTimestamp
	 * @param insertionProcessID
	 * @param temporary
	 * @param baseProcessIDSet
	 * @param dependentProcessIDSet
	 * @param insertedNonProcessIDSet
	 * @param insertedProcessIDSet
	 * @param reproduced
	 * @param VSAAReproducedAndInsertedInstanceUID
	 * @param employerVisInstanceRunIDSet
	 * @param involvedCfTargetValueTableRunIDSet
	 * @param typeSpecificAttributeNameObjectValueMap cannot be null; can be empty;
	 */
	VisframeUDTManagementTableRow(
			VisframeUDTTypeManagerBase<T,I> manager,
			
			T entity,
			
			Timestamp insertionTimestamp,
			
			PrimaryKeyID<? extends VisframeUDT> insertionProcessID, Boolean temporary,
			
			VfIDCollection baseProcessIDSet, VfIDCollection dependentProcessIDSet,
			VfIDCollection insertedNonProcessIDSet, VfIDCollection insertedProcessIDSet, 
			
			Boolean reproduced,
			String VSAAReproducedAndInsertedInstanceUID,
			
			VfIDCollection employerVisInstanceRunIDSet,
			
			VfIDCollection involvedCfTargetValueTableRunIDSet,
			
			Map<SimpleName, Object> typeSpecificAttributeNameObjectValueMap
			){
		//TODO validations
		if(reproduced!=null && reproduced && VSAAReproducedAndInsertedInstanceUID==null)
			throw new IllegalArgumentException("VSAAReproducedAndInsertedInstanceUIDColumn cannot be null when reproduced is true!");
		
		
		///////////////////////
		this.manager = manager;
		
		this.entity = entity;
		
		this.insertionProcessID = insertionProcessID;
		this.temporary= temporary;
		
		this.insertionTimestamp = insertionTimestamp;
		
		this.baseProcessIDSet = baseProcessIDSet;
		this.insertedNonProcessIDSet = insertedNonProcessIDSet;
		this.dependentProcessIDSet = dependentProcessIDSet;
		this.insertedProcessIDSet = insertedProcessIDSet;
		this.reproduced = reproduced;
		this.VSAAReproducedAndInsertedInstanceUID = VSAAReproducedAndInsertedInstanceUID;
		
		this.employerVisInstanceRunIDSet = employerVisInstanceRunIDSet;
		this.involvedCfTargetValueTableRunIDSet = involvedCfTargetValueTableRunIDSet;
		
		this.typeSpecificAttributeNameObjectValueMap = typeSpecificAttributeNameObjectValueMap;
	}
	
	
	//=============for both process and non-process types
	public T getEntity() {
		return this.entity;
	}
	
	
	/**
	 * get the ID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	I getID() {
		return (I)this.getEntity().getID();
	}
	
	/**
	 * return the InsertionProcessID;
	 * non-null for all visframeUDT types;
	 * null for CFTargetValueTableRun;
	 * @return
	 */
	public PrimaryKeyID<? extends VisframeUDT> getInsertionProcessID(){
		return this.insertionProcessID;
	}
	
	
	public Timestamp getInsertionTimestamp() {
		return insertionTimestamp;
	}

	public Boolean getIsTemporary() {
		return temporary;
	}

	public VfIDCollection getBaseProcessIDSet() {
		return baseProcessIDSet;
	}

	public VfIDCollection getInsertedNonProcessIDSet() {
		return insertedNonProcessIDSet;
	}

	public VfIDCollection getDependentProcessIDSet() {
		return dependentProcessIDSet;
	}
	
	/**
	 * @return the insertedProcessIDSet
	 */
	public VfIDCollection getInsertedProcessIDSet() {
		return insertedProcessIDSet;
	}

	/**
	 * @return the reproduced
	 */
	public Boolean getIsReproduced() {
		return reproduced;
	}

	/**
	 * @return the vSAAReproducedAndInsertedInstanceUIDColumn
	 */
	public String getVSAAReproducedAndInsertedInstanceUID() {
		return VSAAReproducedAndInsertedInstanceUID;
	}
	
	
	public VfIDCollection getEmployerVisInstanceRunIDSet() {
		return employerVisInstanceRunIDSet;
	}

	public VfIDCollection getInvolvedCfTargetValueTableRunIDSet() {
		return involvedCfTargetValueTableRunIDSet;
	}

	public Map<SimpleName, Object> getTypeSpecificAttributeNameObjectValueMap() {
		return typeSpecificAttributeNameObjectValueMap;
	}


	
	//////////////////////////////////////
	@Override
	public String toString() {
		return "VisframeUDTManagementTableRow [manager=" + manager + ", entity=" + entity + ", insertionTimestamp="
				+ insertionTimestamp + ", insertionProcessID=" + insertionProcessID + ", temporary=" + temporary
				+ ", baseProcessIDSet=" + baseProcessIDSet + ", dependentProcessIDSet=" + dependentProcessIDSet
				+ ", insertedNonProcessIDSet=" + insertedNonProcessIDSet + ", insertedProcessIDSet="
				+ insertedProcessIDSet + ", reproduced=" + reproduced + ", VSAAReproducedAndInsertedInstanceUIDColumn="
				+ VSAAReproducedAndInsertedInstanceUID + ", employerVisInstanceRunIDSet="
				+ employerVisInstanceRunIDSet + ", involvedCfTargetValueTableRunIDSet="
				+ involvedCfTargetValueTableRunIDSet + ", typeSpecificAttributeNameObjectValueMap="
				+ typeSpecificAttributeNameObjectValueMap + "]";
	}


}
