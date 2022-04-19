package context.project.process.logtable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import basic.SimpleName;
import basic.lookup.PrimaryKeyID;
import basic.lookup.VisframeUDT;
import basic.lookup.project.type.VisframeUDTManagementProcessRelatedTableColumnFactory;
import context.project.rdb.initialize.VisProjectDBUDTConstants;
import context.scheme.VisScheme;
import context.scheme.appliedarchive.VisSchemeAppliedArchive;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstance;
import exception.VisframeException;
import fileformat.FileFormat;
import function.composition.CompositionFunction;
import function.group.CompositionFunctionGroup;
import importer.DataImporter;
import operation.Operation;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.sqltype.SQLStringType;
import rdb.sqltype.UncheckedPrimitiveSQLDataType;
import rdb.table.lookup.ManagementTableColumn;
import rdb.table.lookup.ManagementTableSchema;
import visinstance.VisInstance;
import visinstance.run.VisInstanceRun;
import visinstance.run.layoutconfiguration.VisInstanceRunLayoutConfiguration;

//schema = MANAGEMENT
//table name = PROCESS_LOG
//initialization = empty table

//constraints
//
public final class ProcessLogTableSchemaUtils {
	/**
	 * name of the process log table
	 */
	public static final SimpleName PROCESS_LOG_TABLE_NAME = new SimpleName("VF_PROCESS_LOG");
	
	/**
	 * return the full list of ManagementTableColumn;
	 * facilitate to build the process log table schema;
	 * @return
	 */
	private static List<ManagementTableColumn> getColumnList(){
		List<ManagementTableColumn> ret = new ArrayList<>();
		
		ret.add(UIDColumn);
		ret.add(startTimeColumn);
		
		ret.add(processIDColumn);
		ret.add(insertionProcessIDColumn);
		ret.add(isReproducedColumn);
		ret.add(processStatusColumn);
		
		ret.add(baseProcessIDSetColumn);
		ret.add(insertedProcessIDSetColumn);
		ret.add(insertedNonProcessIDSetColumn);
		ret.add(involvedCfTargetValueTableRunIDSetColumn);
		
		//////
		ret.add(fileFormatColumn);
		ret.add(dataImporterColumn);
		ret.add(operationColumn);
		ret.add(cfgColumn);
		ret.add(cfColumn);
		
		ret.add(visSchemeColumn);
		ret.add(visSchemeAppliedArchiveColumn);
		ret.add(visSchemeAppliedArchiveReproducedAndInsertedInstanceColumn);
		
		ret.add(visInstanceColumn);
		ret.add(visInstanceRunColumn);
		ret.add(visInstanceRunLayoutConfigColumn);
		return ret;
	}
	
	
	/**
	 * return the process log table schema
	 * @return
	 */
	public static ManagementTableSchema getTableSchema() {
		return new ManagementTableSchema(PROCESS_LOG_TABLE_NAME, getColumnList());
	}
	
	
	//////////////////////////////////
	/**
	 * get and return the column name for the process entity object column for the
	 * given type in the PROCESS LOG table;
	 * 
	 * @param type
	 * @return
	 */
	public static SimpleName getProcessTypeEntityColumnName(VisframeUDT type) {
		SimpleName columnName;
		if (type instanceof FileFormat) {
			columnName = ProcessLogTableSchemaUtils.fileFormatColumn.getName();
		} else if (type instanceof DataImporter) {
			columnName = ProcessLogTableSchemaUtils.dataImporterColumn.getName();
		} else if (type instanceof Operation) {
			columnName = ProcessLogTableSchemaUtils.operationColumn.getName();
		} else if (type instanceof CompositionFunctionGroup) {
			columnName = ProcessLogTableSchemaUtils.cfgColumn.getName();
		} else if (type instanceof CompositionFunction) {
			columnName = ProcessLogTableSchemaUtils.cfColumn.getName();
		} else if (type instanceof VisScheme) {
			columnName = ProcessLogTableSchemaUtils.visSchemeColumn.getName();
		} else if (type instanceof VisSchemeAppliedArchive) {
			columnName = ProcessLogTableSchemaUtils.visSchemeAppliedArchiveColumn.getName();
		} else if (type instanceof VisSchemeAppliedArchiveReproducedAndInsertedInstance) {
			columnName = ProcessLogTableSchemaUtils.visSchemeAppliedArchiveReproducedAndInsertedInstanceColumn.getName();
		} else if (type instanceof VisInstance) {
			columnName = ProcessLogTableSchemaUtils.visInstanceColumn.getName();
		} else if (type instanceof VisInstanceRun) {
			columnName = ProcessLogTableSchemaUtils.visInstanceRunColumn.getName();
		}	else if (type instanceof VisInstanceRunLayoutConfiguration) {
			columnName = ProcessLogTableSchemaUtils.visInstanceRunLayoutConfigColumn.getName();
		} else {
			throw new VisframeException("unrecognized process type entity");
		}
		
		return columnName;
	}

	/**
	 * extract the currently pointed row from the given ResultSet to create and return a ProcessLogTableRow;
	 * 
	 * Note that this method does not check if the cursor is pointing to a valid row
	 * or null; thus the invoker method should check it before invoking this method;
	 *
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static ProcessLogTableRow retrieveRow(ResultSet rs) throws SQLException {
		return new ProcessLogTableRow(
				(Integer) rs.getObject(ProcessLogTableSchemaUtils.UIDColumn.getName().getStringValue()),
				(Timestamp) rs.getObject(ProcessLogTableSchemaUtils.startTimeColumn.getName().getStringValue()), // processID
				(PrimaryKeyID<?>) rs.getObject(ProcessLogTableSchemaUtils.processIDColumn.getName().getStringValue()), // processID
				(PrimaryKeyID<?>) rs.getObject(ProcessLogTableSchemaUtils.insertionProcessIDColumn.getName().getStringValue()), // processID
				StatusType.valueOf(rs.getString(ProcessLogTableSchemaUtils.processStatusColumn.getName().getStringValue())), // status
				
				(VfIDCollection) rs.getObject(ProcessLogTableSchemaUtils.baseProcessIDSetColumn.getName().getStringValue()), // baseProcessIDSet
				(VfIDCollection) rs.getObject(ProcessLogTableSchemaUtils.insertedNonProcessIDSetColumn.getName().getStringValue()), // insertedNonProcessIDSet
				(VfIDCollection) rs.getObject(ProcessLogTableSchemaUtils.insertedProcessIDSetColumn.getName().getStringValue()), // insertedProcessIDSet
				(Boolean)rs.getObject(ProcessLogTableSchemaUtils.isReproducedColumn.getName().getStringValue()), // isReproduced
				(VfIDCollection) rs.getObject(ProcessLogTableSchemaUtils.involvedCfTargetValueTableRunIDSetColumn.getName().getStringValue()),// involvedCFTargetValueTableRunIDSet
				
				(FileFormat) rs.getObject(ProcessLogTableSchemaUtils.fileFormatColumn.getName().getStringValue()), // fileFormat
				(DataImporter) rs.getObject(ProcessLogTableSchemaUtils.dataImporterColumn.getName().getStringValue()), // dataImporter
				(Operation) rs.getObject(ProcessLogTableSchemaUtils.operationColumn.getName().getStringValue()), // operation
				(CompositionFunctionGroup) rs.getObject(ProcessLogTableSchemaUtils.cfgColumn.getName().getStringValue()), // compositionFunctionGroup
				(CompositionFunction) rs.getObject(ProcessLogTableSchemaUtils.cfColumn.getName().getStringValue()), // compositionFunction
				(VisInstance) rs.getObject(ProcessLogTableSchemaUtils.visInstanceColumn.getName().getStringValue()), // VisInstance
				(VisScheme) rs.getObject(ProcessLogTableSchemaUtils.visSchemeColumn.getName().getStringValue()), 
				(VisSchemeAppliedArchive) rs.getObject(ProcessLogTableSchemaUtils.visSchemeAppliedArchiveColumn.getName().getStringValue()), // visSchemeApplierArchive
				(VisSchemeAppliedArchiveReproducedAndInsertedInstance) rs.getObject(
						ProcessLogTableSchemaUtils.visSchemeAppliedArchiveReproducedAndInsertedInstanceColumn.getName().getStringValue()), // visSchemeAppliedArchiveReproducedAndInsertedInstance
				(VisInstanceRun) rs.getObject(ProcessLogTableSchemaUtils.visInstanceRunColumn.getName().getStringValue()), // visInstanceRun
				(VisInstanceRunLayoutConfiguration) rs.getObject(ProcessLogTableSchemaUtils.visInstanceRunLayoutConfigColumn.getName().getStringValue()) // visInstanceRun
		);
	}
	
	
	////////////////////////////////
	/**
	 * UID column, int, primary key;//ID of the corresponding process //auto increment;
	 */
	public static final ManagementTableColumn UIDColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("UID"), SQLDataTypeFactory.integerType(), false, true,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null,
			"GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)"
			);
	
	/**
	 * Start time column, NOT NULL
	 */
	public static final ManagementTableColumn startTimeColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("START_TIME"), new UncheckedPrimitiveSQLDataType("TIMESTAMP", false, false, false, false, false), false, false, 
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, "DEFAULT CURRENT_TIMESTAMP" //
			);
	
	
	/**
	 * ID column of the process;
	 * note that for a unique ID, there can be at most one row in the LOG table with STATUS == FINISHED; 
	 * it is allowed to have multiple rows with the same IDs whose status are not FINISHED;
	 * 
	 */
	public static final ManagementTableColumn processIDColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("PROCESS_ID"), VisProjectDBUDTConstants.getSQLDataType(PrimaryKeyID.class), false, false, //note that for UDT, UNIQUE constraints is not relevant
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null 
			);
	
	/**
	 * ID column of the process that inserts this process;
	 * 
	 * for ReproduceableProcessType process reproduced and inserted by a VisSchemeAppliedArchiveReproducerAndInserter
	 * 		the insertion process is the VisSchemeAppliedArchiveReproducerAndInserter
	 * else
	 * 		the insertion process is the process itself
	 */
	public static final ManagementTableColumn insertionProcessIDColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("INSERTION_PROCESS_ID"), VisProjectDBUDTConstants.getSQLDataType(PrimaryKeyID.class), false, false, //note that for UDT, UNIQUE constraints is not relevant
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null 
			);
	
	
	
	/**
	 * Status column
	 */
	public static final ManagementTableColumn processStatusColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("STATUS"), new SQLStringType(20,false), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
			);
	
	
	/////////////////////////////////
	/**
	 * BASE_PROCESS_ID_SET column, NOT NULL; //can be empty set, not null
	 * store all process ID on which this process is based by (utilizing non-processes entities inserted by those base process)?;
	 * 
	 * see {@link VisframeUDTManagementProcessRelatedTableColumnFactory#baseProcessIDSetColumn}
	 */
	public static final ManagementTableColumn baseProcessIDSetColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("BASE_PROCESS_ID_SET"), VisProjectDBUDTConstants.getSQLDataType(VfIDCollection.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
			);
	
	
	/**
	 * INSERTED_NON_PROCESS_ID set column, VfIDCollection UDT, 
	 * can be NULL 
	 * 		(must be null for VisSchemeAppliedArchiveReproducedAndInsertedInstance);
	 * 		must be non-null for other process types
	 * 
	 * store all inserted non process IDs by this process;
	 * 
	 * see {@link VisframeUDTManagementProcessRelatedTableColumnFactory#insertedNonProcessIDSetColumn}
	 */
	public static final ManagementTableColumn insertedNonProcessIDSetColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("INSERTED_NON_PROCESS_ID_SET"), VisProjectDBUDTConstants.getSQLDataType(VfIDCollection.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	/**
	 * VisSchemeAppliedArchiveReproducedAndInsertedInstance specific, can be null(must be null for non-VisSchemeAppliedArchiveReproducedAndInsertedInstance);
	 * 
	 * store the full set of 
	 * see {@link VisframeUDTManagementProcessRelatedTableColumnFactory#insertedNonProcessIDSetColumn}
	 */
	public static final ManagementTableColumn insertedProcessIDSetColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("INSERTED_PROCESS_ID_SET"), VisProjectDBUDTConstants.getSQLDataType(VfIDCollection.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	/**
	 * whether the process is reproduced by a VisSchemeAppliedArchiveReproducedAndInsertedInstance;
	 * 
	 * can be null
	 * 		non-null for {@link ReproduceableProcessType}
	 * 		must be null for other ProcessTypes
	 */
	public static final ManagementTableColumn isReproducedColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("IS_REPRODUCED"), SQLDataTypeFactory.booleanType(), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	/**
	 * INSERTED_CFTARGETVALUETABLERUN_ID_SET column, UDT;
	 * VisInstanceRun specific; empty set for other types
	 * can be null
	 * 		non-null for VisInstanceRun
	 * 		null for other process types;
	 * 
	 * store all the involved CfTargetValueTableRunIDs for this VisInstanceRun process; if not VisInstance, always empty
	 * 
	 * see {@link VisframeUDTManagementProcessRelatedTableColumnFactory#involvedCfTargetValueTableRunIDSetColumn}
	 */
	public static final ManagementTableColumn involvedCfTargetValueTableRunIDSetColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("INVOLVED_CFTARGETVALUETABLERUN_ID_SET"), VisProjectDBUDTConstants.getSQLDataType(VfIDCollection.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	////////////////////////////////////////////////////
	/**
	 * FileFormat column UDT;
	 */
	public static final ManagementTableColumn fileFormatColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("FILEFORMAT"), VisProjectDBUDTConstants.getSQLDataType(FileFormat.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	/**
	 * DataImporter column UDT;
	 */
	public static final ManagementTableColumn dataImporterColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("DATAIMPORTER"), VisProjectDBUDTConstants.getSQLDataType(DataImporter.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	
	/**
	 * Operation column UDT;
	 */
	public static final ManagementTableColumn operationColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("OPERATION"), VisProjectDBUDTConstants.getSQLDataType(Operation.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	
	/**
	 * CFG column UDT;
	 */
	public static final ManagementTableColumn cfgColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("COMPOSITIONFUNCTIONGROUP"), VisProjectDBUDTConstants.getSQLDataType(CompositionFunctionGroup.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	
	/**
	 * CF column UDT;
	 */
	public static final ManagementTableColumn cfColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("COMPOSITIONFUNCTION"), VisProjectDBUDTConstants.getSQLDataType(CompositionFunction.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	/**
	 * NativeVisInstance column UDT;
	 */
	public static final ManagementTableColumn visInstanceColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("VISINSTANCE"), VisProjectDBUDTConstants.getSQLDataType(VisInstance.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);

	/**
	 * VisScheme column UDT;
	 */
	public static final ManagementTableColumn visSchemeColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("VISSCHEME"), VisProjectDBUDTConstants.getSQLDataType(VisScheme.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	/**
	 * VisSchemeApplierArchive column UDT;//
	 */
	public static final ManagementTableColumn visSchemeAppliedArchiveColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("VISSCHEMEAPPLIERARCHIVE"), VisProjectDBUDTConstants.getSQLDataType(VisSchemeAppliedArchive.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	/**
	 * VisSchemeAppliedArchiveReproducedAndInsertedInstance column UDT;
	 */
	public static final ManagementTableColumn visSchemeAppliedArchiveReproducedAndInsertedInstanceColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("visSchemeAppliedArchiveReproducedAndInsertedInstance"), VisProjectDBUDTConstants.getSQLDataType(VisSchemeAppliedArchiveReproducedAndInsertedInstance.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	/**
	 * VisInstanceRun column UDT;
	 */
	public static final ManagementTableColumn visInstanceRunColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("VISINSTANCERUN"), VisProjectDBUDTConstants.getSQLDataType(VisInstanceRun.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
	
	/**
	 * VisInstanceRunLayout column UDT;
	 */
	public static final ManagementTableColumn visInstanceRunLayoutConfigColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("VISINSTANCERUNLAYOUT"), VisProjectDBUDTConstants.getSQLDataType(VisInstanceRunLayoutConfiguration.class), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null
			);
}
