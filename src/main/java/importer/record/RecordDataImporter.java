package importer.record;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.process.logtable.StatusType;
import exception.VisframeException;
import fileformat.FileFormatID;
import fileformat.record.RecordDataFileFormat;
import fileformat.record.RecordDataFileFormat.PrimaryKeyAttributeNameSet;
import importer.AbstractDataImporter;
import importer.FileParser;
import metadata.DataType;
import metadata.MetadataName;
import metadata.SourceType;
import metadata.record.RecordDataMetadata;

/**
 * DataImporter for RecordDataMatadata from a RecordFileFormat file;
 * 
 * this class contains Record data specific importing settings that could vary to each specific input data file of the same RecordFileFormat;
 * @author tanxu
 */
public class RecordDataImporter extends AbstractDataImporter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6419019250578850170L;
	
	//////////////////////////
	
	/**
	 * a different PrimaryKeyAttributeNameSet specific to the data source file of this AbstractRecordDataImporter from the default one in the {@link RecordDataFileFormat};
	 * could contains discovered tag attributes, if true, every record data must contains non-null values for those tag attributes;
	 * 
	 * if null, use the one defined in the RecordDataFileFormat;
	 */
	private final PrimaryKeyAttributeNameSet alternativePrimaryKeyAttributeNameSet;
	
	/**
	 * whether or not include all primitive type attribute in the mandatory attribute list of the RecordDataFileFormat as columns in the data table;
	 * if false, need to specific a non-empty subset in {@link #mandatoryPrimitiveRecordAttributeNameSetIncludedInResultedDataTable}
	 */
	private final boolean toIncludeAllMandatoryPrimitiveRecordAttributesInResultedDataTable;
	/**
	 * set of mandatory simple attributes to be included in the resulted RecordDataMetadata's data table schema;
	 * if null, include all of them in the data table schema; 
	 * cannot be empty;
	 */
	private final Set<SimpleName> mandatoryPrimitiveRecordAttributeNameSetIncludedInResultedDataTable;
	
	/**
	 * if true, add all discovered tag attributes as columns in the data table;
	 * if false, need to specify a set of tag attributes names in discoveredTagAttributeNameSetIncludedInResultedDataTable
	 */
	private final boolean toIncludeAllDiscoverdTagAttriubteInResultedDataTable;
	
	/**
	 * set of discovered tag simple attribute to be put in the data table;
	 */
	private final Set<SimpleName> discoveredTagAttributeNameSetIncludedInResultedDataTable;
	
	
	
	///////////////////////////
//	/**
//	 * data table name created by visframe based on the record Metadata name;
//	 */
//	private transient DataTableName dataTableName;
	/**
	 * contains the default information about the data file content
	 */
	private transient RecordDataFileFormat recordDataFileFormat;
	
	private transient RecordDataFileParser parser;
	
	/**
	 * constructor
	 * validation:
	 * 1. if RecordDataFileFormat's BetweenRecordStringFormatAndParser is InterleavingRecordFormatAndParser type, and everyRecordSegmentHasHeadingIDAttribute() method returns true, 
	 * the alternativePrimaryKeyAttributeNameSet must be null;
	 * 
	 * @param notes cannot be null
	 * @param dataSourcePath cannot be null or empty; 
	 * @param fileFormatID cannot be null
	 * @param mainImportedMetadataName cannot be null
	 * @param alternativePrimaryKeyAttributeNameSet  could be null; if null, use the default primary key attribute set;
	 * @param toIncludeAllMandatoryPrimitiveRecordAttributesInResultedDataTable 
	 * @param mandatorySimpleRecordAttributeNameSetIncludedInResultedDataTable can never be empty set; can be null only if toIncludeAllMandatoryPrimitiveRecordAttributesInResultedDataTable is true; otherwise, must be non-empty; note that mandatory simple attributes in primary key set will always be included;
	 * @param toIncludeAllDiscoverdTagAttriubteInResultedDataTable 
	 * @param discoveredTagSimpleRecordAttributeNameSetIncludedInResultedDataTable if toIncludeAllDiscoverdTagAttriubteInResultedDataTable is true, must be null; otherwise, must be non-null but can be empty; note that tag attributes in primary key set will always be included;
	 */
	public RecordDataImporter(
			VfNotes notes, Path dataSourcePath, FileFormatID fileFormatID, 
			MetadataName mainImportedMetadataName,
			//
//			DataTableName dataTableName,
//			RecordDataFileFormat recordDataFileFormat, 
			PrimaryKeyAttributeNameSet alternativePrimaryKeyAttributeNameSet,
			boolean toIncludeAllMandatoryPrimitiveRecordAttributesInResultedDataTable,
			Set<SimpleName> mandatorySimpleRecordAttributeNameSetIncludedInResultedDataTable,
			boolean toIncludeAllDiscoverdTagAttriubteInResultedDataTable,
			Set<SimpleName> discoveredTagSimpleRecordAttributeNameSetIncludedInResultedDataTable
			) {
		super(notes, dataSourcePath, fileFormatID, mainImportedMetadataName);
		//validations
		if(fileFormatID.getType()!=DataType.RECORD) {
			throw new IllegalArgumentException("given fileFormatID is not of RECORD type");
		}
//		if(mainImportedMetadataID.getDataType()!=DataType.RECORD) {
//			throw new IllegalArgumentException("given mainImportedMetadataID is not of RECORD type");
//		}
		
		
		
		//
//		this.dataTableName = dataTableName;
		this.alternativePrimaryKeyAttributeNameSet = alternativePrimaryKeyAttributeNameSet;
		
		
		this.toIncludeAllMandatoryPrimitiveRecordAttributesInResultedDataTable = toIncludeAllMandatoryPrimitiveRecordAttributesInResultedDataTable;
		this.mandatoryPrimitiveRecordAttributeNameSetIncludedInResultedDataTable = mandatorySimpleRecordAttributeNameSetIncludedInResultedDataTable;
		
		this.toIncludeAllDiscoverdTagAttriubteInResultedDataTable = toIncludeAllDiscoverdTagAttriubteInResultedDataTable;
		this.discoveredTagAttributeNameSetIncludedInResultedDataTable = discoveredTagSimpleRecordAttributeNameSetIncludedInResultedDataTable;
	}

	

	//////////////////////////////////////
//	/**
//	 * returns the name of the supporting data table in the RDB of the owner VisProjectDBContext that hold the record data from the source data file;
//	 * @return
//	 */
//	public DataTableName getDataTableName() {
//		return this.dataTableName;
//	}
//	
//	
//	public DataTableSchemaID getDataTableSchemaID() {
//		return new DataTableSchemaID(this.getDataTableName());
//	}
	
	/**
	 * get the full set of names of primary key attributes for this specific {@link RecordDataImporter}
	 * @return
	 */
	public Set<SimpleName> getDesignatedPrimaryKeyAttributeNameSet(){
		if(this.alternativePrimaryKeyAttributeNameSet!=null) {
			return this.alternativePrimaryKeyAttributeNameSet.getFullNameSet();
		}else {
			return this.getFileFormat().getWithinRecordAttributeStringFormat().getDefaultPrimaryKeyAttributeNameSet().getFullNameSet();
		}
	}
	
	
	/**
	 * returns set of names of mandatory SimpleRecordAttributes that are to be put in the table schema as columns in the resulted data table;
	 * this set should not contain any attribute in the PrimaryKeyAttributeNameSet returned by getPrimaryKeyAttributeNameSet();
	 * if null, put all mandatory SimpleRecordAttributes defined in the RecordDataFileFormat in the resulted data table schema;
	 * if empty set, put only those in the PrimaryKeyAttributeSet returned by {@link getPrimaryKeyAttributeNameSet()};
	 * @return
	 */
	public Set<SimpleName> getMandatoryPrimitiveRecordAttributeNameSetIncludedInResultedDataTable(){
		return this.mandatoryPrimitiveRecordAttributeNameSetIncludedInResultedDataTable;
	}
	
    
	/**
	 * returns set of names of discovered tag SimpleRecordAttributes that are to be put in the table schema as columns in the resulted data table;
	 * only add the column to the table schema once the first occurrence is found, and the data type is recognized;
	 * if null, put all discovered tag SimpleRecordAttributes as columns in the data table schema;
	 * if empty, put only those in the PrimaryKeyAttributeSet returned by {@link getPrimaryKeyAttributeNameSet()};
	 * @return
	 */
	public Set<SimpleName> getDiscoveredTagAttributeNameSetIncludedInResultedDataTable(){
		return this.discoveredTagAttributeNameSetIncludedInResultedDataTable;
	}
	
	
	public boolean isToIncludeAllMandatoryPrimitiveRecordAttributesInResultedDataTable() {
		return toIncludeAllMandatoryPrimitiveRecordAttributesInResultedDataTable;
	}

	
	public boolean isToIncludeAllDiscoverdTagAttriubteInResultedDataTable() {
		return toIncludeAllDiscoverdTagAttriubteInResultedDataTable;
	}
	

	public PrimaryKeyAttributeNameSet getAlternativePrimaryKeyAttributeNameSet() {
		return alternativePrimaryKeyAttributeNameSet;
	}

	
	/**
	 * check if the given attribute name should be kept in the data table schema or not
	 * @param attributeName
	 * @return
	 */
	public boolean isAttributeNameToBeKept(SimpleName attributeName) {
		//
		if(this.getDesignatedPrimaryKeyAttributeNameSet().contains(attributeName)) {
			return true;
		}
		
		if(this.getFileFormat().getWithinRecordAttributeStringFormat().getMandatoryPrimitiveRecordAttributeNameSet().contains(attributeName)) {//attribute is a mandatory primitive attribute
			if(this.getMandatoryPrimitiveRecordAttributeNameSetIncludedInResultedDataTable()!=null) {
				return this.getMandatoryPrimitiveRecordAttributeNameSetIncludedInResultedDataTable().contains(attributeName);
			}else {
				return true;//mandatoryPrimitiveRecordAttributeNameSetIncludedInResultedDataTable is null, keep all mandatory primitive attributes
			}
		}else {//attribute is a discovered tag attribute
			if(this.toIncludeAllDiscoverdTagAttriubteInResultedDataTable) { //
				return true;
			}else {//
				if(this.getDiscoveredTagAttributeNameSetIncludedInResultedDataTable()!=null) {
					return this.getDiscoveredTagAttributeNameSetIncludedInResultedDataTable().contains(attributeName);
				}else {//
					return false;
				}
				
			}
		}
	}
	
	
	//*********************************************************************
	//
	//**********************************************************************
	/**
	 * 
	 */
	@Override
	public DataType getDataType() {
		return DataType.RECORD;
	}
	
	@Override
	public RecordDataFileFormat getFileFormat() {
		return recordDataFileFormat;
	}
	


	@Override
	public FileParser getFileParser() {
		return this.parser;
	}
	
	
	/**
	 * 1. create a RecordDataFileParser and invoke its perform() method
	 * 		1. create data table schema in the rdb of the host VisProjectDBContext
	 * 		2. read and parse the data file to extract record and attributes to populate the data table;
	 * 2. create RecordDataMetadata and insert it into Metadata management table;
	 * 3. insert this RecordDataImporter into the DataImporter management table;
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Override
	public StatusType call() throws SQLException, IOException {
		if(this.getHostVisProjectDBContext()==null) {
			throw new VisframeException("host VisProjectDBContext is not set, cannot import data");
		}
		
		this.readAndParseIntoDataTables();
		this.createAndStoreImportedMetadata();
		this.storeDataImporter();
		
		//
		return StatusType.FINISHED;
	}
	
	
	@Override
	protected void readAndParseIntoDataTables() throws SQLException, IOException {
		//retrieve the RecordDataFileFormat from the management table
		this.recordDataFileFormat = (RecordDataFileFormat)this.getHostVisProjectDBContext().getHasIDTypeManagerController().getFileFormatManager().lookup(this.getFileFormatID());
		
		this.parser = new RecordDataFileParser(this.getHostVisProjectDBContext(), this);
		
		parser.perform();
	}
	
	
	@Override
	protected void createAndStoreImportedMetadata() throws SQLException {
		//make the RecordDataMetadata 
		RecordDataMetadata importedRecordData = new RecordDataMetadata(
//				MetadataName name, VfNotes notes, 
				this.getMainImportedMetadataID().getName(), VfNotes.makeVisframeDefinedVfNotes(),
//				SourceType sourceType,
				SourceType.IMPORTED,
//				MetadataID sourceCompositeDataMetadataID, OperationID sourceOperationID,
				null, null,
//				
//				DataTableSchema dataTableSchema,
				this.parser.getDataTableSchema(),
//				Boolean ofGenericGraphNode
				null
				);
		
		//insert the RecordDataMetadata into the management table;
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().insert(importedRecordData);
	}
	
	
	@Override
	protected void storeDataImporter() throws SQLException {
		//insert the DataImporter into the management table;
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getDataImporterManager().insert(this);
	}


	///////////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((alternativePrimaryKeyAttributeNameSet == null) ? 0
				: alternativePrimaryKeyAttributeNameSet.hashCode());
		result = prime * result + ((discoveredTagAttributeNameSetIncludedInResultedDataTable == null) ? 0
				: discoveredTagAttributeNameSetIncludedInResultedDataTable.hashCode());
		result = prime * result + ((mandatoryPrimitiveRecordAttributeNameSetIncludedInResultedDataTable == null) ? 0
				: mandatoryPrimitiveRecordAttributeNameSetIncludedInResultedDataTable.hashCode());
		result = prime * result + (toIncludeAllDiscoverdTagAttriubteInResultedDataTable ? 1231 : 1237);
		result = prime * result + (toIncludeAllMandatoryPrimitiveRecordAttributesInResultedDataTable ? 1231 : 1237);
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof RecordDataImporter))
			return false;
		RecordDataImporter other = (RecordDataImporter) obj;
		if (alternativePrimaryKeyAttributeNameSet == null) {
			if (other.alternativePrimaryKeyAttributeNameSet != null)
				return false;
		} else if (!alternativePrimaryKeyAttributeNameSet.equals(other.alternativePrimaryKeyAttributeNameSet))
			return false;
		if (discoveredTagAttributeNameSetIncludedInResultedDataTable == null) {
			if (other.discoveredTagAttributeNameSetIncludedInResultedDataTable != null)
				return false;
		} else if (!discoveredTagAttributeNameSetIncludedInResultedDataTable
				.equals(other.discoveredTagAttributeNameSetIncludedInResultedDataTable))
			return false;
		if (mandatoryPrimitiveRecordAttributeNameSetIncludedInResultedDataTable == null) {
			if (other.mandatoryPrimitiveRecordAttributeNameSetIncludedInResultedDataTable != null)
				return false;
		} else if (!mandatoryPrimitiveRecordAttributeNameSetIncludedInResultedDataTable
				.equals(other.mandatoryPrimitiveRecordAttributeNameSetIncludedInResultedDataTable))
			return false;
		if (toIncludeAllDiscoverdTagAttriubteInResultedDataTable != other.toIncludeAllDiscoverdTagAttriubteInResultedDataTable)
			return false;
		if (toIncludeAllMandatoryPrimitiveRecordAttributesInResultedDataTable != other.toIncludeAllMandatoryPrimitiveRecordAttributesInResultedDataTable)
			return false;
		return true;
	}

	
	
}
