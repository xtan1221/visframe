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
import context.scheme.VisScheme;
import context.scheme.VisSchemeID;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.sqltype.SQLStringType;
import rdb.table.lookup.ManagementTableColumn;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;

public class VisProjectVisSchemeManager extends VisframeUDTTypeManagerBase<VisScheme, VisSchemeID>{
	/**
	* whether the file format is a default one defined by visframe or not;
	* if true, cannot be deleted from the management table;
	*/
	public static final ManagementTableColumn VISSCHEME_NAME = new ManagementTableColumn(
		//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
		new SimpleName("VISSCHEME_NAME"), new SQLStringType(VisframeContextConstants.MAX_METADATA_NAME_LEN,false), false, false,
		//Boolean notNull, String defaultStringValue, String additionalConstraints
		true, null, null 
	);
	public static final ManagementTableColumn IMPORTED = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("IMPORTED"), SQLDataTypeFactory.booleanType(), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null 
		);
	
	////////////////////
	private Map<SimpleName, ManagementTableColumn> primaryKeyAttributeNameMap;
	
	private String selectAllSortedByUIDWithDESCOrderSqlString;
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	public VisProjectVisSchemeManager(VisProjectDBContext visProjectDBContext) {
		super(visProjectDBContext, VisScheme.class, VisSchemeID.class);
	}
	
	/**
	 * find and return the next available UID for a new VisScheme in the host VisProjectDBContext's rdb;
	 * 
	 * 1. sort the management by the UID column with DESC;
	 * 2. if no row found, return 1;
	 * 3. else, retrieve the first row and get the UID column value i
	 * 4. return i+1
	 * @return
	 * @throws SQLException 
	 */
	public int findNextAvaiableUID() throws SQLException {
		Statement statement = this.getVisProjectDBConnection().createStatement(); 
		ResultSet rs = statement.executeQuery(this.getSelectAllSortedByUIDWithDESCOrderSqlString());
		
		if(!rs.next()) {
			return 1;
		}
		
		return rs.getInt(this.getManagementTableSchema().getColumnIndex(VisSchemeID.UID_COLUMN.getName()))+1;
		
	}
	
	/**
	 * build and return the sql string that select all from the management table sorted by UID column with DESC order;
	 * used to find out the value of largest UID;
	 * @return
	 */
	private String getSelectAllSortedByUIDWithDESCOrderSqlString() {
		if(this.selectAllSortedByUIDWithDESCOrderSqlString==null) {
			List<String> orderByColumnNameList = new ArrayList<>();
			orderByColumnNameList.add(VisSchemeID.UID_COLUMN.getName().getStringValue());
			List<Boolean> orderByASCList = new ArrayList<>();
			orderByASCList.add(false);
			
			selectAllSortedByUIDWithDESCOrderSqlString = TableContentSQLStringFactory.buildSelectAllSQLString(
					SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()), 
					null, 
					orderByColumnNameList, orderByASCList
					);
		}
		return this.selectAllSortedByUIDWithDESCOrderSqlString;
	}
	
	///////////////////////////////////////
	@Override
	public Map<SimpleName, ManagementTableColumn> getPrimaryKeyAttributeNameMap() {
		if(this.primaryKeyAttributeNameMap == null) {
			primaryKeyAttributeNameMap = new HashMap<>();
		
			primaryKeyAttributeNameMap.put(VisSchemeID.UID_COLUMN.getName(), VisSchemeID.UID_COLUMN);
		}
		return primaryKeyAttributeNameMap;
	}

	@Override
	public List<ManagementTableColumn> getTypeSpecificManagementTableColumnList() {
		List<ManagementTableColumn> ret = new ArrayList<>();
		ret.add(VISSCHEME_NAME);
		ret.add(IMPORTED);
		return ret;
	}

	@Override
	protected void setTypeSpecificColumnValues(PreparedStatement ps, VisScheme entity) throws SQLException {
		ps.setString(
				this.getManagementTableSchema().getColumnIndex(VISSCHEME_NAME.getName()),
				entity.getName().getStringValue());
		
		ps.setBoolean(
				this.getManagementTableSchema().getColumnIndex(IMPORTED.getName()), 
				entity.isImported());
	}
	
	@Override
	protected void typeSpecificInsertionRelatedActivity(VisScheme t) {
		//do nothing
	}

}
