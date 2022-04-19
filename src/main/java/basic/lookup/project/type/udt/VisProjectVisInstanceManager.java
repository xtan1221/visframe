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
import basic.lookup.project.type.VisframeUDTTypeManagerBase;
import context.VisframeContextConstants;
import context.project.VisProjectDBContext;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.sqltype.SQLStringType;
import rdb.table.lookup.ManagementTableColumn;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;
import visinstance.VisInstance;
import visinstance.VisInstanceID;
import visinstance.VisSchemeBasedVisInstance;

public class VisProjectVisInstanceManager extends VisframeUDTTypeManagerBase<VisInstance, VisInstanceID>{
	
	public static final ManagementTableColumn VISINSTANCE_NAME = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("NAME"), new SQLStringType(VisframeContextConstants.MAX_METADATA_NAME_LEN,false), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null 
		);
	
	public static final ManagementTableColumn isVisSchemeBasedColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("IS_VISSCHEME_BASED"), SQLDataTypeFactory.booleanType(), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null 
			);
	
	/**
	 * null for NativeVisInstance;
	 * non-null for VisSchemeBasedVisInstance;
	 */
	public static final ManagementTableColumn VisScheme_UID_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("VisScheme_UID"), new SQLStringType(VisframeContextConstants.MAX_METADATA_NAME_LEN,false), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null 
			);
	
	/**
	 * null for NativeVisInstance;
	 * non-null for VisSchemeBasedVisInstance;
	 */
	public static final ManagementTableColumn VisSchemeApplierArchive_UID_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("VisSchemeApplierArchive_UID"), new SQLStringType(VisframeContextConstants.MAX_METADATA_NAME_LEN,false), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null 
			);
	/**
	 * null for NativeVisInstance;
	 * non-null for VisSchemeBasedVisInstance;
	 */
	public static final ManagementTableColumn VisSchemeApplierArchiveReproducedAndInsertedInstance_UID_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("VisSchemeApplierArchiveReproducedAndInsertedInstance_UID"), new SQLStringType(VisframeContextConstants.MAX_METADATA_NAME_LEN,false), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			false, null, null 
			);
	
	
	////////////////////
	private Map<SimpleName, ManagementTableColumn> primaryKeyAttributeNameMap;
	
	private String selectAllSortedByUIDWithASCOrderSqlString;
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	public VisProjectVisInstanceManager(VisProjectDBContext visProjectDBContext) {
		super(visProjectDBContext, VisInstance.class, VisInstanceID.class);
	}
	
	
//	/**
//	 * check if any of the existing VisInstance has the same set of core ShapeCFG with the given coreShapeCFGIDSet;
//	 * 
//	 * in detail, retrieve all VisInstance objects and check the coreShapeCFGIDSet of each of them with the given one;
//	 * if one is found, return true;
//	 * else, return false;
//	 * @param coreShapeCFGIDSet
//	 * @return
//	 * @throws SQLException 
//	 */
//	public boolean isVisInstanceWithSameCoreShapeCFGSetAlreadyExisting(Set<CompositionFunctionGroupID> coreShapeCFGIDSet) throws SQLException {
//		//TODO alternative, use sql statement to save memory
//
//		Map<VisInstanceID, VisInstance> allMap = this.retrieveAll();
//		for(VisInstanceID id: allMap.keySet()) {
//			if(allMap.get(id).getCoreShapeCFGIDSet().equals(coreShapeCFGIDSet)) {
//				return true;
//			}
//		}
//		
//		return false;
//	}
	
	
	/**
	 * find and return the next UID in the host VisProjectDBContext's rdb
	 * @return
	 * @throws SQLException 
	 */
	public int findNextAvailableUID() throws SQLException {
		Statement statement = this.getVisProjectDBConnection().createStatement(); 
		ResultSet rs = statement.executeQuery(this.getSelectAllSortedByUIDWithASCOrderSqlString());
		
		int ret = 1;
		if(!rs.next()) {//there is no rows, return 1;
			return 1;
		}else {
			int i = rs.getInt(this.getManagementTableSchema().getColumnIndex(VisInstanceID.UID_COLUMN.getName()));
			if(i>ret) //first row's UID is larger than 1, simply return 1;
				return ret;
			else //first row must be equal to 1;
				ret++;
		}
		
		while(rs.next()) {
			int i = rs.getInt(this.getManagementTableSchema().getColumnIndex(VisInstanceID.UID_COLUMN.getName()));
			if(i==ret) //current row's UID is 1 larger than previous one;
				ret++;
			else //current row's UID is 2 or more larger than previous one, available UID found!;
				break;
		}
		
		return ret;
	}
	/**
	 * build and return the sql string that select all from the management table sorted by UID column with DESC order;
	 * used to find out the value of largest UID;
	 * @return
	 */
	private String getSelectAllSortedByUIDWithASCOrderSqlString() {
		if(this.selectAllSortedByUIDWithASCOrderSqlString==null) {
			List<String> orderByColumnNameList = new ArrayList<>();
			orderByColumnNameList.add(VisInstanceID.UID_COLUMN.getName().getStringValue());
			List<Boolean> orderByASCList = new ArrayList<>();
			orderByASCList.add(true);
			
			selectAllSortedByUIDWithASCOrderSqlString = TableContentSQLStringFactory.buildSelectAllSQLString(
					SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()), 
					null, 
					orderByColumnNameList, orderByASCList
					);
		}
		return this.selectAllSortedByUIDWithASCOrderSqlString;
	}
	
	///////////////////////////////////////////////
	@Override
	public Map<SimpleName, ManagementTableColumn> getPrimaryKeyAttributeNameMap() {
		if(this.primaryKeyAttributeNameMap == null) {
			primaryKeyAttributeNameMap = new HashMap<>();
			
			primaryKeyAttributeNameMap.put(VisInstanceID.UID_COLUMN.getName(), VisInstanceID.UID_COLUMN);
		}
		
		return primaryKeyAttributeNameMap;
	}


	@Override
	public List<ManagementTableColumn> getTypeSpecificManagementTableColumnList() {
		List<ManagementTableColumn> ret = new ArrayList<>();
		
		ret.add(VISINSTANCE_NAME);
		ret.add(isVisSchemeBasedColumn);
		ret.add(VisScheme_UID_COLUMN);
		ret.add(VisSchemeApplierArchive_UID_COLUMN);
		ret.add(VisSchemeApplierArchiveReproducedAndInsertedInstance_UID_COLUMN);
		return ret;
	}
	
	
	@Override
	protected void setTypeSpecificColumnValues(PreparedStatement ps, VisInstance entity) throws SQLException {
		ps.setBoolean(
				this.getManagementTableSchema().getColumnIndex(isVisSchemeBasedColumn.getName()), 
				entity instanceof VisSchemeBasedVisInstance
				);
		ps.setString(
				this.getManagementTableSchema().getColumnIndex(VISINSTANCE_NAME.getName()), 
				entity.getName().getStringValue());
		
		if(entity instanceof VisSchemeBasedVisInstance) {
			VisSchemeBasedVisInstance vsbv = (VisSchemeBasedVisInstance)entity;
			ps.setString(this.getManagementTableSchema().getColumnIndex(VisScheme_UID_COLUMN.getName()), 
					Integer.toString(vsbv.getVisSchemeID().getUID()));
			ps.setString(this.getManagementTableSchema().getColumnIndex(VisSchemeApplierArchive_UID_COLUMN.getName()), 
					Integer.toString(vsbv.getVisSchemeAppliedArchiveID().getUID()));
			ps.setString(this.getManagementTableSchema().getColumnIndex(VisSchemeApplierArchiveReproducedAndInsertedInstance_UID_COLUMN.getName()), 
					Integer.toString(vsbv.getVisSchemeAppliedArchiveReproducedAndInsertedInstanceID().getUID()));
		}else {
			ps.setString(this.getManagementTableSchema().getColumnIndex(VisScheme_UID_COLUMN.getName()), 
					"N/A");
			ps.setString(this.getManagementTableSchema().getColumnIndex(VisSchemeApplierArchive_UID_COLUMN.getName()), 
					"N/A");
			ps.setString(this.getManagementTableSchema().getColumnIndex(VisSchemeApplierArchiveReproducedAndInsertedInstance_UID_COLUMN.getName()), 
					"N/A");
		}
	}
	
	
	@Override
	protected void typeSpecificInsertionRelatedActivity(VisInstance t) {
		//do nothing
	}
	

}
