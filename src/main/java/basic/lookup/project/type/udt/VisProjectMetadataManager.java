package basic.lookup.project.type.udt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basic.SimpleName;
import basic.lookup.HasID;
import basic.lookup.ID;
import basic.lookup.project.type.VisframeUDTTypeManagerBase;
import context.VisframeContextConstants;
import context.project.VisProjectDBContext;
import context.project.VisProjectDBFeatures;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.DataType;
import metadata.Metadata;
import metadata.MetadataID;
import metadata.MetadataName;
import metadata.record.RecordDataMetadata;
import rdb.sqltype.SQLStringType;
import rdb.table.lookup.ManagementTableColumn;
import sql.LogicOperator;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;


/**
 * manager class for Metadata objects;
 * 
 * ===112520-update
 * note that insertion and deletion of Metadata will not trigger any changes on the vis project DOS graph,
 * rather the project DOS graph will be built from scratch every time it is needed
 * see {@link VisProjectDBFeatures#getDOSGraph()}
 * 
 * 
 * @author tanxu
 */
public class VisProjectMetadataManager extends VisframeUDTTypeManagerBase<Metadata,MetadataID> {
	/**
	* name of the data table for record type metadata;
	* null for other data type
	*/
	public static final ManagementTableColumn RECORD_DATA_TABLE_NAME = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("RECORD_DATA_TABLE_NAME"), new SQLStringType(VisframeContextConstants.MAX_METADATA_NAME_LEN,false), false, false, //
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null 
	);
	
	/////////////////////////////////////////
	private Map<SimpleName, ManagementTableColumn> primaryKeyAttributeNameMap;
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	public VisProjectMetadataManager(VisProjectDBContext visProjectDBContext) {
		super(visProjectDBContext, Metadata.class, MetadataID.class);
	}
	
	/**
	 * find and return the next un-occupied Metadata name of the given original one and data type;
	 * 
	 * 1. if there is no existing Metadata with name and data type same as the given one, return it;
	 * 2. else, add an integer index starting from 1 to the end of the original name concatenated with a '_' and check if it is used; if no, return it, otherwise, increase the index by 1 and check again until the first available one is found;
	 *  
	 * @param originalName
	 * @param type
	 * @return
	 * @throws SQLException 
	 */
	public MetadataName findNextAvailableMetadataName(MetadataName originalName, DataType type) throws SQLException {
		
		if(!this.checkIDExistence(new MetadataID(originalName, type))){
			return originalName;
		}
		
		int index = 1;
		MetadataID newID = new MetadataID(new MetadataName(originalName.getStringValue().concat("_").concat(Integer.toString(index))),type);
		
		while(this.checkIDExistence(newID)) {
			index++;
			newID = new MetadataID(new MetadataName(originalName.getStringValue().concat("_").concat(Integer.toString(index))),type);
		}
		
		return newID.getName();
		
	}
	
	
	////////////////////////////////////////////////
	@Override
	public Map<SimpleName, ManagementTableColumn> getPrimaryKeyAttributeNameMap() {
		if(this.primaryKeyAttributeNameMap == null) {
			primaryKeyAttributeNameMap = new HashMap<>();
			
			primaryKeyAttributeNameMap.put(MetadataID.NAME_COLUMN.getName(), MetadataID.NAME_COLUMN);
			primaryKeyAttributeNameMap.put(MetadataID.TYPE_COLUMN.getName(), MetadataID.TYPE_COLUMN);
		}
		return primaryKeyAttributeNameMap;
	}
	
	
	@Override
	public List<ManagementTableColumn> getTypeSpecificManagementTableColumnList() {
		List<ManagementTableColumn> ret = new ArrayList<>();
		ret.add(RECORD_DATA_TABLE_NAME);
		
		return ret;
	}
	
	@Override
	protected void setTypeSpecificColumnValues(PreparedStatement ps, Metadata entity) throws SQLException {
		String dataTableName;
		if(entity instanceof RecordDataMetadata) {
			dataTableName = ((RecordDataMetadata)entity).getDataTableSchema().getID().getTableName().getStringValue();
		}else {
			dataTableName = null;
		}
		
		
		ps.setString(
				this.getManagementTableSchema().getColumnIndex(RECORD_DATA_TABLE_NAME.getName()),
				dataTableName);
	}
	
	/**
	 * first check if the Metadata is of record type, delete the data table schema;
	 * 
	 * then delete the given id from the management table by invoke super.delete();
	 * 
	 */
	@Override
	public void delete(ID<? extends HasID> id) throws SQLException {
		if(!this.isValidID(id)) {
			throw new IllegalArgumentException("given id is not of valid type");
		}
		//
		Metadata md = this.lookup((MetadataID)id);
		if(md.getDataType().equals(DataType.RECORD)) {
			this.getHostVisProjectDBContext().getHasIDTypeManagerController().getDataTableSchemaManager().delete(((RecordDataMetadata)md).getDataTableSchema().getID());
		}
		
		///
		String sqlString = TableContentSQLStringFactory.buildDeleteWithConditionSqlString(
				SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()),
				TableContentSQLStringFactory.buildIDEquityConditionString(this.getPrimaryKeyAttributeNameMap(), md.getID()) //need to downcast id to I;
				);
		
		Statement statement = this.getVisProjectDBConnection().createStatement();
		statement.execute(sqlString);
		
		//
//		this.getHostVisProjectDBContext().getDOSGraph().removeMetadata((MetadataID)id);
	}
	
	/**
	 */
	@Override
	protected void typeSpecificInsertionRelatedActivity(Metadata t) throws SQLException {
//		this.getHostVisProjectDBContext().getDOSGraph().addMetadata(t);
		//do nothing
	}
	
	//////////////////////////////////////
	/**
	 * build a reproduced MetadataID based on the given one so that the reproduced one is not existing in the management table;
	 * 
	 * the reproduced MetadataID has the name string in format:
	 * 		originalNameString_randomInteger 
	 * where randomInteger is a positive integer
	 * so that the reproduced MetadataID is different from all existing ones in the management table;
	 * 
	 * this method facilitates {@link MetadataID#reproduce(VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)
	 * 
	 * @param originalMetadataID
	 * @return
	 * @throws SQLException 
	 */
	public MetadataID buildReproducedID(MetadataID originalMetadataID) throws SQLException {
		int index = 1;
		
		//
		String nameString = originalMetadataID.getName().getStringValue().concat("_").concat(Integer.toString(index));
		
		//check if there is a Metadata in the management table with the same nameString and data type;
		List<String> colNameList = new ArrayList<>();
		List<Boolean> ofStringTypeList = new ArrayList<>(); 
		List<Boolean> toIgnoreCaseList = new ArrayList<>(); 
		List<LogicOperator> logicOperatorList = new ArrayList<>();
		colNameList.add(MetadataID.NAME_COLUMN.getName().getStringValue());
		ofStringTypeList.add(true);
		toIgnoreCaseList.add(true);
		colNameList.add(MetadataID.TYPE_COLUMN.getName().getStringValue());
		ofStringTypeList.add(true);
		toIgnoreCaseList.add(true);
		
		logicOperatorList.add(LogicOperator.AND);
		
		//column value
		List<String> valueStringList = new ArrayList<>();
		valueStringList.add(nameString);
		valueStringList.add(originalMetadataID.getDataType().toString());
		//build the sql query string;
		String selectAllSqlStringWithTheMetadataID = TableContentSQLStringFactory.buildSelectAllSQLString(
				SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()), 
				TableContentSQLStringFactory.buildColumnValueEquityCondition(colNameList, valueStringList, ofStringTypeList, toIgnoreCaseList, logicOperatorList),
				null, 
				null
				);
		//run the query
		Statement statement = this.getVisProjectDBConnection().createStatement(); 
		ResultSet rs = statement.executeQuery(selectAllSqlStringWithTheMetadataID);
		
		boolean alreadyExisting = rs.next();
		while(alreadyExisting) {
			index++;
			nameString = originalMetadataID.getName().getStringValue().concat("_").concat(Integer.toString(index));
			
			valueStringList = new ArrayList<>();
			valueStringList.add(nameString);
			valueStringList.add(originalMetadataID.getDataType().toString());
			
			selectAllSqlStringWithTheMetadataID = TableContentSQLStringFactory.buildSelectAllSQLString(
					SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()), 
					TableContentSQLStringFactory.buildColumnValueEquityCondition(colNameList, valueStringList, ofStringTypeList, toIgnoreCaseList, logicOperatorList),
					null, 
					null
					);
			ResultSet rs2 = statement.executeQuery(selectAllSqlStringWithTheMetadataID);
			alreadyExisting = rs2.next();
		}
		
		//
		return new MetadataID(
				new MetadataName(nameString),
				originalMetadataID.getDataType()
				);
		
	}
	
	
	
}
