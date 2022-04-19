package importer.record;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import exception.VisframeException;
import fileformat.record.attribute.AbstractRecordAttributeFormat;
import fileformat.record.attribute.PrimitiveRecordAttributeFormat;
import importer.AbstractFileParser;
import importer.record.between.BetweenRecordStringParserBase;
import importer.record.between.BetweenRecordStringParserFactory;
import importer.record.within.WithinRecordAttributeStringParserBase;
import importer.record.within.WithinRecordAttributeStringParserFactory;
import rdb.sqltype.SQLDataType;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import rdb.table.data.DataTableName;
import rdb.table.data.DataTableSchema;
import rdb.table.data.DataTableSchemaFactory;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;
import sql.derby.TableSchemaUtils;

/**
 * 
 * @author tanxu
 *
 */
public class RecordDataFileParser extends AbstractFileParser {
	/**
	 * max number of records in a batch of PreparedStatement
	 */
	private final static int BATCH_MAX_SIZE = 1000;
	
	/////////////////
	private final RecordDataImporter dataImporter;
	
	
	////////////////////////
	/**
	 * always contains the current data table schema that includes all identified columns;
	 * 
	 * note that this table schema is used to build the data table schema through the process of parsing the data file;
	 * 
	 * 
	 */
	private DataTableSchema dataTableSchema;
	
	
	/**
	 * constructor 
	 * @param visProject cannot be null
	 * @param dataImporter cannot be null
	 */
	public RecordDataFileParser(VisProjectDBContext visProject, RecordDataImporter dataImporter){
		super(visProject);
		
		this.dataImporter = dataImporter;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public RecordDataImporter getDataImporter() {
		return this.dataImporter;
	}
	
	
	/**
	 * 0. create an initial data table schema with the selected primary key attributes and create it in the rdb of the host VisProjectDBContext
	 * 1. create the BetweenRecordStringParser object corresponding to the BetweenRecordStringFormat of the RecordFileFormat
	 * 2. create a WithinRecordAttributeStringParser object corresponding to the WithinRecordAttributeStringFormat of the RecordFileFormat
	 * 3. for each of the record string parsed from data file by the BetweenRecordStringParser, feed it to the WithinRecordAttributeStringParser to parse out all the contained attributes and value strings;
	 * 
	 * 4. after every n new records are parsed, add them to the data table;
	 * 		first need to check if there are any newly discovered tag attributes that need to be added to the data table schema;
	 * 		then insert the records into the data table;
	 * 5. continue 3 and 4 until there the data file end is reached;
	 * @throws SQLException 
	 * @throws IOException 
	 * 
	 */
	@Override
	public void perform() throws SQLException, IOException {
		BetweenRecordStringParserBase betweenRecordStringParser = BetweenRecordStringParserFactory.makeParser(
				new File(this.getDataImporter().getDataSourcePath().toString()), 
				this.getDataImporter().getFileFormat().getBetweenRecordStringFormat()
				);
		
		WithinRecordAttributeStringParserBase withinRecordAttributeStringParser = WithinRecordAttributeStringParserFactory.makeParser(
				this.getDataImporter().getFileFormat().getWithinRecordAttributeStringFormat());
		
		String recordString;
		
		//!!!process the first records to identify all primary key column, especially those tag attributes without data type known beforehand;
		recordString = betweenRecordStringParser.getNextRecordString();
		if(recordString==null) {
			throw new VisframeException("No record string found in the data file!");
		}
		
		Map<PrimitiveRecordAttributeFormat,String> attributeStringValueMap = withinRecordAttributeStringParser.parse(recordString);
		
		List<DataTableColumn> dataTableColumnList = new ArrayList<>();
		dataTableColumnList.add(DataTableSchemaFactory.makeRUIDColumn());
		Set<SimpleName> identifiedPKAttributeNameSet = new HashSet<>();
		Set<SimpleName> designatedPKAttributeNameSet = this.getDataImporter().getDesignatedPrimaryKeyAttributeNameSet();//
		for(PrimitiveRecordAttributeFormat attribute:attributeStringValueMap.keySet()) {
			if(designatedPKAttributeNameSet.contains(attribute.getName())) {
				dataTableColumnList.add(makeDataTableColumn(attribute, true));
				identifiedPKAttributeNameSet.add(attribute.getName());
			}else {
				dataTableColumnList.add(makeDataTableColumn(attribute, false));
			}
		}
		
		//if not all primary key columns are found in the first record, throw exception;
		if(designatedPKAttributeNameSet.size()!=(identifiedPKAttributeNameSet).size()) {
			throw new VisframeException("assigned primary key attributes are not fully found in the first record from the data file");
		}
		if(!designatedPKAttributeNameSet.equals(identifiedPKAttributeNameSet)) {
			throw new VisframeException("assigned primary key attributes are not fully found in the first record from the data file");
		}
		
		//add other mandatory attributes not in the primary key into the column list if they are to be kept;
		for(AbstractRecordAttributeFormat attribute:this.dataImporter.getFileFormat().getWithinRecordAttributeStringFormat().getOrderedListOfMandatoryAttribute()) {
			if(attribute instanceof PrimitiveRecordAttributeFormat) {
				PrimitiveRecordAttributeFormat pa = (PrimitiveRecordAttributeFormat)attribute;
				//check if to be kept and not in the identifiedPKAttributeNameSet;
				if(this.getDataImporter().isAttributeNameToBeKept(pa.getName())&&!designatedPKAttributeNameSet.contains(pa.getName())) {
					DataTableColumn col = makeDataTableColumn(pa,false);
					if(!dataTableColumnList.contains(col)) {//not yet added
						dataTableColumnList.add(col);
					}
				}
			}
		}
		
		//make the initial data table columns with RUID column and all primary key columns build based on first records
		this.dataTableSchema = new DataTableSchema(
				this.getVisProjectDBContext().getHasIDTypeManagerController().getDataTableSchemaManager().findNextAvailableName(new DataTableName(this.getDataImporter().getMainImportedMetadataID().getName().getStringValue())),
				dataTableColumnList);
		
		//insert the initial data table schema into the RDB of the host projectdb
		this.getVisProjectDBContext().getHasIDTypeManagerController().getDataTableSchemaManager().insert(this.dataTableSchema);
		
		///////////////////////////////////////////////
		//parse each record from the data file
		int currentBatchSize = 0;
		//initialize PreparedStatement
		//should be updated when a new attribute is discovered and added to the data table schema
		List<String> currentNonRUIDColumnUpperCaseNameListInDataTableSchema = new ArrayList<>(); //not including the RUID column since it is auto increment
		for(DataTableColumn col:this.dataTableSchema.getOrderedListOfNonRUIDColumn()) {
			currentNonRUIDColumnUpperCaseNameListInDataTableSchema.add(col.getName().getStringValue().toUpperCase());
		}
		PreparedStatement ps = this.getVisProjectDBContext().getDBConnection().prepareStatement(
				TableContentSQLStringFactory.buildInsertIntoPreparedStatementSqlString(
						SQLStringUtils.buildTableFullPathString(this.dataTableSchema.getSchemaName(), this.dataTableSchema.getName()), 
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema
						)
				);
		
		/**
		 * contains the list of newly found attributes for current record;
		 */
		List<PrimitiveRecordAttributeFormat> newFoundAttributeList;
		Set<String> addedColUpperCaseNameSet;
		while(recordString!=null) {
			
			attributeStringValueMap = withinRecordAttributeStringParser.parse(recordString);
			
			//check if any discovered tag attribute 
			//reset first
			newFoundAttributeList = new ArrayList<>();//whether a new attribute that are to be added to the data table schema is found in the current record
			List<DataTableColumnName> listOfColumnNameInDataTable = this.dataTableSchema.getOrderListOfColumnName();
			for(PrimitiveRecordAttributeFormat attribute:attributeStringValueMap.keySet()) {
				if(listOfColumnNameInDataTable.contains(attribute.getName().toDataTableColumnName())) {
					//same name with existing attributes but different data type
					if(!attribute.getSQLDataType().equals(this.dataTableSchema.getColumn(attribute.getName().toDataTableColumnName()).getSqlDataType())) {
						throw new VisframeException("multiple attributes found with same name but different data type;!!!!!!"+ attribute.getName().getStringValue());
					}
				}else {
					//a new attribute is found, check if to be kept
					if(this.getDataImporter().isAttributeNameToBeKept(attribute.getName())) { //new attribute is to be kept; thus need to alter the data table schema;
						newFoundAttributeList.add(attribute);
					}
				}
			}
			
			if(newFoundAttributeList.isEmpty()) {//no new attribute is found, all attributes are present in the current data table schema
				//add the attributes values in the current record into the PreparedStatement
				addedColUpperCaseNameSet = new HashSet<>();
				for(PrimitiveRecordAttributeFormat attribute:attributeStringValueMap.keySet()) {
					int index = currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(attribute.getName().getStringValue().toUpperCase())+1; //+1!
					addedColUpperCaseNameSet.add(attribute.getName().getStringValue().toUpperCase());
					attribute.getSQLDataType().setPreparedStatement(
							ps, 
							index,
							attributeStringValueMap.get(attribute)
							);
				}
				
				//set value of attributes in data table but not in current record (if any) and with null default value to null into the PreparedStatement
//				for(int i=0;i<currentNonRUIDColumnUpperCaseNameListInDataTableSchema.size();i++) {
//					if(!addedColUpperCaseNameSet.contains(currentNonRUIDColumnUpperCaseNameListInDataTableSchema.get(i))) {
//						ps.setObject(i+1, null);
//					}
//				}
				for(int i=0;i<currentNonRUIDColumnUpperCaseNameListInDataTableSchema.size();i++) {
					String colNameString = currentNonRUIDColumnUpperCaseNameListInDataTableSchema.get(i);
					if(!addedColUpperCaseNameSet.contains(colNameString)) {
						SQLDataType dataType = this.dataTableSchema.getColumn(new DataTableColumnName(colNameString)).getSqlDataType();
						String defaultStringValue = this.dataTableSchema.getColumn(new DataTableColumnName(colNameString)).getDefaultStringValue();
						if(defaultStringValue==null) {
							ps.setObject(i+1, null);
						}else {
							ps.setObject(i+1, dataType.getDefaultValueObject(defaultStringValue));
						}
					}
				}
				
				
				
				ps.addBatch();
				currentBatchSize++;
				
				if(currentBatchSize>BATCH_MAX_SIZE) {
					ps.executeBatch();
					ps.clearBatch();
					currentBatchSize = 0;
				}
			}else {//at least one new attribute is discovered and need to be added to the data table schema
				
				//first insert the current batch of records into the data table
				ps.executeBatch();
				ps.clearBatch();
				ps.close();
				currentBatchSize = 0;
				
				//then add the new attribute as a column into the data table schema
				List<DataTableColumn> newFoundColumnList = new ArrayList<>();
				for(PrimitiveRecordAttributeFormat attribute:newFoundAttributeList) {
					newFoundColumnList.add(makeDataTableColumn(attribute,false));
				}
				
				//add columns to the data table schema in DB
				TableSchemaUtils.addNewColumnsToExistingTable(
						this.getVisProjectDBContext().getDBConnection(), 
						this.dataTableSchema.getSchemaName(), 
						this.dataTableSchema.getName(), 
						newFoundColumnList
						);
				
				///////////////////////
				//update the initialDataTableSchema
				List<DataTableColumn> updatedColumnList = this.dataTableSchema.getOrderedListOfColumn();
				updatedColumnList.addAll(newFoundColumnList);
				this.dataTableSchema = new DataTableSchema(this.dataTableSchema.getName(),updatedColumnList);
				
				//reset prepared statement with the updated data table schema
				currentNonRUIDColumnUpperCaseNameListInDataTableSchema = new ArrayList<>(); //not including the RUID column since it is auto increment
//				dataTableColumnList = this.dataTableSchema.getOrderedListOfColumn();
				for(DataTableColumn col:dataTableSchema.getOrderedListOfNonRUIDColumn()) {
					currentNonRUIDColumnUpperCaseNameListInDataTableSchema.add(col.getName().getStringValue().toUpperCase());
				}
				
				ps = this.getVisProjectDBContext().getDBConnection().prepareStatement(
						TableContentSQLStringFactory.buildInsertIntoPreparedStatementSqlString(
								SQLStringUtils.buildTableFullPathString(this.dataTableSchema.getSchemaName(), this.dataTableSchema.getName()), 
								currentNonRUIDColumnUpperCaseNameListInDataTableSchema
								)
						);
				
				
				//add the attributes values in the current record into the PreparedStatement
				addedColUpperCaseNameSet = new HashSet<>();
				for(PrimitiveRecordAttributeFormat attribute:attributeStringValueMap.keySet()) {
					int index = currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(attribute.getName().getStringValue().toUpperCase())+1; //+1!
					addedColUpperCaseNameSet.add(attribute.getName().getStringValue().toUpperCase());
					attribute.getSQLDataType().setPreparedStatement(
							ps, 
							index,
							attributeStringValueMap.get(attribute)
							);
				}
				
//				//set value of attributes in data table but not in current record (if any) to null into the PreparedStatement
//				for(int i=0;i<currentNonRUIDColumnUpperCaseNameListInDataTableSchema.size();i++) {
//					if(!addedColUpperCaseNameSet.contains(currentNonRUIDColumnUpperCaseNameListInDataTableSchema.get(i))) {
//						ps.setObject(i+1, null);
//					}
//				}
				
				//set value of attributes in data table but not in current record (if any) and with null default value to null into the PreparedStatement
				for(int i=0;i<currentNonRUIDColumnUpperCaseNameListInDataTableSchema.size();i++) {
					String colNameString = currentNonRUIDColumnUpperCaseNameListInDataTableSchema.get(i);
					if(!addedColUpperCaseNameSet.contains(colNameString)) {
						SQLDataType dataType = this.dataTableSchema.getColumn(new DataTableColumnName(colNameString)).getSqlDataType();
						String defaultStringValue = this.dataTableSchema.getColumn(new DataTableColumnName(colNameString)).getDefaultStringValue();
						if(defaultStringValue==null) {
							ps.setObject(i+1, null);
						}else {
							ps.setObject(i+1, dataType.getDefaultValueObject(defaultStringValue));
						}
					}
				}
				
				
				ps.addBatch();
				currentBatchSize++;
			}
			
			//get the next record string;
			recordString = betweenRecordStringParser.getNextRecordString();
		}
		
		//insert the last batch into the data table;
		ps.executeBatch();
		ps.clearBatch();
		ps.close();
		
		//till now, the initialDataTableSchema is the one that can be used to make the imported RecordDataMetadata;
		
	}
	
	/**
	 * build and return a formal DataTableSchema based on {@link #dataTableSchema};
	 * 
	 * note that the formal DataTableSchema:
	 * 1. should not contain the RUID column;
	 * 2. all primary key columns should be labeled;
	 * @return
	 */
	public DataTableSchema getDataTableSchema() {
		return this.dataTableSchema;
	}
	
//	/**
//	 * create the intial data table schema that contains:
//	 * 1. RUID column, which is used as primary key
//	 */
//	private void makeInitialDataTableSchema() {
//		DataTableName tableName = this.dataImporter.getDataTableName();
//		List<DataTableColumn> orderedListOfColumn = new ArrayList<>();;
//		orderedListOfColumn.add(DataTableSchemaFactory.makePKRUIDColumn());
//		
//		for(AbstractRecordAttributeFormat attribute:this.dataImporter.getFileFormat().getRecordAttributeStringFormat().getOrderedListOfMandatoryAttribute()) {
//			if(attribute instanceof PrimitiveRecordAttributeFormat) {
//				PrimitiveRecordAttributeFormat pa = (PrimitiveRecordAttributeFormat)attribute;
//				//check ...
//				if(this.getDataImporter().isAttributeNameToBeKept(pa.getName())) {
//					
//					orderedListOfColumn.add(makeDataTableColumn(pa,false));
//				}
//			}
//		}
//		
//		this.initialDataTableSchema = new DataTableSchema(tableName, orderedListOfColumn);
//	}
	
	
	
	
	
	
	/**
	 * create a DataTableColumn with the given PrimitiveRecordAttributeFormat;
	 * @param praf
	 * @return
	 */
	static DataTableColumn makeDataTableColumn(PrimitiveRecordAttributeFormat praf, boolean isInPrimaryKey) {
		return new DataTableColumn(
				//DataTableColumnName name, SQLDataType sqlDataType, boolean inPrimaryKey,
				new DataTableColumnName(praf.getName().getStringValue()),praf.getSQLDataType(), isInPrimaryKey, //not in primary key
				//Boolean unique, Boolean notNull, String defaultStringValue,
				false, false, null,null,
				//VfNotes notes
				VfNotes.makeVisframeDefinedVfNotes());
	}
	
}
