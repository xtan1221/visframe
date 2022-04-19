package context.project.process.logtable;

import java.sql.Timestamp;

import basic.lookup.PrimaryKeyID;
import context.scheme.VisScheme;
import context.scheme.appliedarchive.VisSchemeAppliedArchive;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstance;
import fileformat.FileFormat;
import function.composition.CompositionFunction;
import function.group.CompositionFunctionGroup;
import importer.DataImporter;
import operation.Operation;
import visinstance.VisInstance;
import visinstance.run.VisInstanceRun;
import visinstance.run.layoutconfiguration.VisInstanceRunLayoutConfiguration;

/**
 * class that contains values of all columns of a row in the process log table;
 * 
 * @author tanxu
 *
 */
public class ProcessLogTableRow {
	private final int UID; //note that it is possible there are multiple rows in the LOG table with the same ID; thus UID is needed;
	private final Timestamp startTimestamp;
	private final PrimaryKeyID<?> processID; //cannot be null
	private final PrimaryKeyID<?> insertionProcessID;
	private final StatusType status; //cannot be null
	
	private final VfIDCollection baseProcessIDSet; //cannot be null
	private final VfIDCollection insertedNonProcessIDSet;//cannot be null
	private final VfIDCollection insertedProcessIDSet;
	private final Boolean reproduced;//
	private final VfIDCollection involvedCFTargetValueTableRunIDSet;//cannot be null
	
	private final FileFormat fileFormat;
	private final DataImporter dataImporter;//can be null
	private final Operation operation;
	private final CompositionFunctionGroup compositionFunctionGroup;
	private final CompositionFunction compositionFunction;
	
	private final VisScheme visScheme;
	private final VisSchemeAppliedArchive visSchemeApplierArchive;
	private final VisSchemeAppliedArchiveReproducedAndInsertedInstance VisSchemeAppliedArchiveReproducedAndInsertedInstance;
	
	private final VisInstance visInstance;
	private final VisInstanceRun visInstanceRun;
	private final VisInstanceRunLayoutConfiguration visInstanceRunLayout;
	
	/**
	 * constructor
	 * @param UID
	 * @param processID
	 * @param status
	 * @param fileFormat
	 * @param dataImporter
	 * @param operation
	 * @param compositionFunctionGroup
	 * @param compositionFunction
	 * @param nativeVisInstance
	 * @param visSchemeApplierArchive
	 * @param visInstanceRun
	 * @param baseProcessIDSet
	 * @param insertedNonProcessIDSet
	 * @param involvedCFTargetValueTableRunIDSet
	 */
	public ProcessLogTableRow(
			int UID,
			Timestamp startTimestamp,
			PrimaryKeyID<?> processID,
			PrimaryKeyID<?> insertionProcessID,
			StatusType status,

			VfIDCollection baseProcessIDSet,
			VfIDCollection insertedNonProcessIDSet,
			VfIDCollection insertedProcessIDSet,
			Boolean reproduced,
			VfIDCollection involvedCFTargetValueTableRunIDSet,
			
			FileFormat fileFormat,
			DataImporter dataImporter,
			Operation operation,
			CompositionFunctionGroup compositionFunctionGroup,
			CompositionFunction compositionFunction,
			VisInstance visInstance,
			VisScheme visScheme,
			VisSchemeAppliedArchive visSchemeApplierArchive,
			VisSchemeAppliedArchiveReproducedAndInsertedInstance VisSchemeAppliedArchiveReproducedAndInsertedInstance,
			VisInstanceRun visInstanceRun,
			VisInstanceRunLayoutConfiguration visInstanceRunLayout
			){
		//TODO validations
		//1. one and only one of the 8 ... MUST be non-null and other MUST be null;
		//2. VfIDCollections cannot be null;
		
		
		this.UID = UID;
		this.startTimestamp = startTimestamp;
		this.processID = processID;
		this.insertionProcessID = insertionProcessID;
		this.status = status;

		this.baseProcessIDSet = baseProcessIDSet;
		this.insertedNonProcessIDSet = insertedNonProcessIDSet;
		this.insertedProcessIDSet = insertedProcessIDSet;
		this.reproduced = reproduced;
		this.involvedCFTargetValueTableRunIDSet = involvedCFTargetValueTableRunIDSet;
		
		
		this.fileFormat = fileFormat;
		this.dataImporter = dataImporter;
		this.operation = operation;
		this.compositionFunctionGroup = compositionFunctionGroup;
		this.compositionFunction = compositionFunction;
		this.visInstance = visInstance;
		
		this.visScheme = visScheme;
		this.visSchemeApplierArchive = visSchemeApplierArchive;
		this.VisSchemeAppliedArchiveReproducedAndInsertedInstance = VisSchemeAppliedArchiveReproducedAndInsertedInstance;
		
		this.visInstanceRun = visInstanceRun;
		this.visInstanceRunLayout = visInstanceRunLayout;
		
	}


	/**
	 * @return the uID
	 */
	public int getUID() {
		return UID;
	}


	/**
	 * @return the startTimestamp
	 */
	public Timestamp getStartTimestamp() {
		return startTimestamp;
	}


	/**
	 * @return the processID
	 */
	public PrimaryKeyID<?> getProcessID() {
		return processID;
	}


	/**
	 * @return the insertionProcessID
	 */
	public PrimaryKeyID<?> getInsertionProcessID() {
		return insertionProcessID;
	}


	/**
	 * @return the status
	 */
	public StatusType getStatus() {
		return status;
	}


	/**
	 * @return the baseProcessIDSet
	 */
	public VfIDCollection getBaseProcessIDSet() {
		return baseProcessIDSet;
	}


	/**
	 * @return the insertedNonProcessIDSet
	 */
	public VfIDCollection getInsertedNonProcessIDSet() {
		return insertedNonProcessIDSet;
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
	public Boolean getReproduced() {
		return reproduced;
	}


	/**
	 * @return the involvedCFTargetValueTableRunIDSet
	 */
	public VfIDCollection getInvolvedCFTargetValueTableRunIDSet() {
		return involvedCFTargetValueTableRunIDSet;
	}


	/**
	 * @return the fileFormat
	 */
	public FileFormat getFileFormat() {
		return fileFormat;
	}


	/**
	 * @return the dataImporter
	 */
	public DataImporter getDataImporter() {
		return dataImporter;
	}


	/**
	 * @return the operation
	 */
	public Operation getOperation() {
		return operation;
	}


	/**
	 * @return the compositionFunctionGroup
	 */
	public CompositionFunctionGroup getCompositionFunctionGroup() {
		return compositionFunctionGroup;
	}


	/**
	 * @return the compositionFunction
	 */
	public CompositionFunction getCompositionFunction() {
		return compositionFunction;
	}


	/**
	 * @return the visScheme
	 */
	public VisScheme getVisScheme() {
		return visScheme;
	}


	/**
	 * @return the visSchemeApplierArchive
	 */
	public VisSchemeAppliedArchive getVisSchemeApplierArchive() {
		return visSchemeApplierArchive;
	}


	/**
	 * @return the visSchemeAppliedArchiveReproducedAndInsertedInstance
	 */
	public VisSchemeAppliedArchiveReproducedAndInsertedInstance getVisSchemeAppliedArchiveReproducedAndInsertedInstance() {
		return VisSchemeAppliedArchiveReproducedAndInsertedInstance;
	}


	/**
	 * @return the visInstance
	 */
	public VisInstance getVisInstance() {
		return visInstance;
	}


	/**
	 * @return the visInstanceRun
	 */
	public VisInstanceRun getVisInstanceRun() {
		return visInstanceRun;
	}


	/**
	 * @return the visInstanceRunLayout
	 */
	public VisInstanceRunLayoutConfiguration getVisInstanceRunLayout() {
		return visInstanceRunLayout;
	}
	
}
