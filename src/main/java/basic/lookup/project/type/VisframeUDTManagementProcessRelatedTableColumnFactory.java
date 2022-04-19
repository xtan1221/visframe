package basic.lookup.project.type;

import basic.SimpleName;
import basic.lookup.PrimaryKeyID;
import context.project.process.logtable.VfIDCollection;
import context.project.rdb.initialize.VisProjectDBUDTConstants;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstance;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.sqltype.SQLStringType;
import rdb.sqltype.UncheckedPrimitiveSQLDataType;
import rdb.table.lookup.ManagementTableColumn;
import visinstance.VisInstance;


/**
 * factory class to make the columns in management tables of VisframeUDT types that are EXCLUSIVELY utilized by ProcessPerformer;
 * 
 * 1. common columns shared by all or a set of VisframeUDT types' management tables;
 * 2. VisframeUDT specific columns;
 * 
 * @author tanxu
 *
 */
public class VisframeUDTManagementProcessRelatedTableColumnFactory {
	///=====================all VisframeUDT types 
	/**
	 * Insertion_time column;
	 * the time of the insertion of the entity
	 */
	public static final ManagementTableColumn insertionTimeColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("INSERTION_TIME"), new UncheckedPrimitiveSQLDataType("TIMESTAMP", false, false, false, false,false), false, false, 
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null 
			);
	
	
	//================All type of VisframeUDT(including both process type and non-process type) management table except for CfTargetValueTableRun
	/**
	 * IS_TEMP column;
	 * whether the VisframeUDT entity of a row in a management table is temporary or not;
	 * if true, the INSERTION_PROCESS of the entity is not finished yet;
	 * only after the insertion process of a entity is finished, the IS_TEMP column can have TRUE value;
	 * must be set to true after the insertion process is successfully inserted into the host VisProjectDBContext to avoid rolling back when data consistency is checked;
	 */
	public static final ManagementTableColumn isTempColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("IS_TEMP"), SQLDataTypeFactory.booleanType(), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	/**
	 * INSERTION_PROCESS_ID column; cannot be null;
	 * applicable for all VisframeUDT except for {@link CFTargetValueTableRun};
	 * 
	 * the ID of the ProcessType VisframeUDT entity that inserts the entity of this row;
	 * 
	 * for NonProcessType
	 * 		trivial
	 * for ProcessType
	 * 		must always be the NonReproduceableProcessType entity itself! (122020-update)
	 * 		
	 * 		note that even for ReproduceableProcessType entity reproduced by a {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance}, its insertion process ID should also be itself rather than the {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance};
	 * 		instead, the reproducing {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance} is stored in a column {@link #VSAAReproducedAndInsertedInstanceUIDColumn}
	 * 		
	 * ==========================
	 * note that notNull is false is for the visframe defined FileFormats; see {@link VisProjectFileFormatManager#insertVisframeDefinedFileFormat}
	 */
	public static final ManagementTableColumn insertionProcessIDColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("INSERTION_PROCESS_ID"), VisProjectDBUDTConstants.getSQLDataType(PrimaryKeyID.class), false, false, 
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null 
			);
	

	//================all ProcessType VisframeUDT management table specific
	/**
	 * DEPENDENT PROCESS ID SET column; 
	 * 
	 * ID set of all process type entity that depends on this entity;
	 * 
	 * Facilitate roll back of the process entity;
	 * 
	 * note that for {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance}, the reproduced and inserted ReproduceableProcessType of it is not included in its dependentProcessIDSetColumn;
	 * rather, they are included in the {@link #insertedProcessIDSetColumn};
	 * 
	 * note that notNull is false is for the visframe defined FileFormats; see {@link VisProjectFileFormatManager#insertVisframeDefinedFileFormat}
	 * 
	 * note that CfTargetValueTableRun is of NonProcessType, thus does not have this column;
	 */
	public static final ManagementTableColumn dependentProcessIDSetColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("DEPENDENT_PROCESS_ID_SET"), VisProjectDBUDTConstants.getSQLDataType(VfIDCollection.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	
	/**
	 * BASE PROCESS ID SET column of the process type entity of this row;
	 * 
	 * note that for reproduced and inserted ReproduceableProcessType entities by {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance}, the
	 * {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance} is not included in the baseProcessIDSetColumn of the ReproduceableProcessType entities;
	 * rather, the {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance} is set to the {@link #insertionProcessIDColumn} of such entities;
	 * 
	 * note that notNull is false is for the visframe defined FileFormats; see {@link VisProjectFileFormatManager#insertVisframeDefinedFileFormat}
	 * 
	 * see {@link ProcessLogTableSchemaUtils#baseProcessIDSetColumn};
	 */
	public static final ManagementTableColumn baseProcessIDSetColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("BASE_PROCESS_ID_SET"), VisProjectDBUDTConstants.getSQLDataType(VfIDCollection.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	
	////////////////All Process types except for VisSchemeAppliedArchiveReproducedAndInsertedInstance and VisInstance
	/**
	 * INSERTED_NON_PROCESS_ID SET column;
	 * 
	 * The set of ID of (non process type) entities inserted during the performing of the process entity;
	 * Identified during the process of performing the process;
	 * Those entities’ BASE PROCESS ID column should be equal to this process entity;
	 * 
	 * only applicable for FileFormatImporter, DataImporter, Operation, CompositionFunction ??(TODO not done yet)
	 * 
	 * 
	 * NOT in management table for {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance} and {@link VisInstance} type processes
	 * 		1. for {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance}, see {@link #insertedProcessIDSetColumn};
	 * 		2. for {@link VisInstanceRun}, see {@link #involvedCfTargetValueTableRunIDSetColumn};
	 * note that notNull is false is for the visframe defined FileFormats; see {@link VisProjectFileFormatManager#insertVisframeDefinedFileFormat}
	 * see {@link ProcessLogTableSchemaUtils#insertedNonProcessIDSetColumn};
	 */
	public static final ManagementTableColumn insertedNonProcessIDSetColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("INSERTED_NON_PROCESS_ID_SET"), VisProjectDBUDTConstants.getSQLDataType(VfIDCollection.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	/////////////////////////////////////////////////////////////////////////////////////
	//==============VisSchemeAppliedArchiveReproducedAndInsertedInstance management table specific
	/**
	 * {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance} specific;
	 * the set of inserted process by this VisSchemeAppliedArchiveReproducedAndInsertedInstance that are of {@link ReproduceableProcessType};
	 */
	public static final ManagementTableColumn insertedProcessIDSetColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("INSERTED_PROCESS_ID_SET"), VisProjectDBUDTConstants.getSQLDataType(VfIDCollection.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	//=========== ReproduceableProcessType management table specific
	//Operation, CFG, CF
	/**
	 * IS_REPRODUCED column;
	 * specific for {@link ReproduceableProcessType} including Operation, CompositionFunctionGroup and CompositionFunction;
	 * 
	 * whether the entity of the row is reproduced by a {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance} or not;
	 * 
	 * if false, the entity is created from scratch;
	 * if true, the entity is reproduced and inserted by a {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance} type process 
	 */
	public static final ManagementTableColumn isReproducedColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("IS_REPRODUCED"), SQLDataTypeFactory.booleanType(), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
			);
	

	/**
	 * the UID of the {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance} that reproduces the entity;
	 * 
	 * specific to ReproduceableProcessType;
	 * 
	 * in detail, if a ReproduceableProcessType entity is reproduced by a {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance}
	 * 		its {@link #isReproducedColumn} should be true and its {@link #VSAAReproducedAndInsertedInstanceUIDColumn} should be set to the UID of that {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance};
	 * otherwise, 
	 * 		its {@link #isReproducedColumn} should be false and its {@link #VSAAReproducedAndInsertedInstanceUIDColumn} must be null;
	 * 
	 */
	public static final ManagementTableColumn VSAAReproducedAndInsertedInstanceUIDColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("VSAAReproducedAndInsertedInstance_UID"), new SQLStringType(5,false), false, false, 
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null 
			);
	
	
	//==============CfTargetValueTableRun management table specific
	/**
	 * EMPLOYER_VISINSTANCERUNID_SET column, UDT, NOT NULL, not empty (if empty, the CFTargetValueTableRun will be deleted);
	 * store ID set of all the VisInstanceRun that contains the CfTargetValueTableRun of this row;
	 */
	public static final ManagementTableColumn employerVisInstanceRunIDSetColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("EMPLOYER_VISINSTANCERUN_ID_SET"), VisProjectDBUDTConstants.getSQLDataType(VfIDCollection.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
			);
	
	
	//===============VisInstanceRun management table specific
	/**
	 * INSERTED_CFTARGETVALUETABLERUN_ID_SET column, UDT, NOT NULL;// VisInstanceRun specific; empty set for other types;
	 * 
	 * store all the involved CfTargetValueTableRunIDs for this VisInstanceRun process; if not VisInstance, always empty;
	 * 
	 * see see {@link ProcessLogTableSchemaUtils#involvedCfTargetValueTableRunIDSetColumn};
	 */
	public static final ManagementTableColumn involvedCfTargetValueTableRunIDSetColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("INVOLVED_CFTARGETVALUETABLERUN_ID_SET"), VisProjectDBUDTConstants.getSQLDataType(VfIDCollection.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
			);
	
	
	
}
