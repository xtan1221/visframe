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
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstance;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstanceID;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.table.lookup.ManagementTableColumn;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;

/**
 * manager class for management table for VisSchemeApplierArchiveReproducedAndInsertedInstances in a host VisProjectDBContext
 * @author tanxu
 *
 */
public class VisProjectVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager extends VisframeUDTTypeManagerBase<VisSchemeAppliedArchiveReproducedAndInsertedInstance, VisSchemeAppliedArchiveReproducedAndInsertedInstanceID>{
	
	/////////////////VisSchemeApplierArchiveReproducedAndInsertedInstance specific columns, may be updated if more features are needed(not put in {@link VisframeUDTManagementProcessRelatedTableColumnFactory})
	/**
	 * the UID of the applied VisScheme of the VisSchemeApplierArchive
	 */
	public static final ManagementTableColumn VIS_SCHEME_UID_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("VISSCHEME_UID"), SQLDataTypeFactory.integerType(), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null 
			);
	/**
	 * the UID of the VisSchemeApplierArchive;
	 */
	public static final ManagementTableColumn VIS_SCHEME_APPLIER_ARCHIVE_UID_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("VISSCHEME_APPLIER_ARCHIVE_UID"), SQLDataTypeFactory.integerType(), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null 
			);
	
	////////////////////
	private Map<SimpleName, ManagementTableColumn> primaryKeyAttributeNameMap;
	////////////////////////
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	public VisProjectVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager(
			VisProjectDBContext visProjectDBContext) {
		super(visProjectDBContext, VisSchemeAppliedArchiveReproducedAndInsertedInstance.class, VisSchemeAppliedArchiveReproducedAndInsertedInstanceID.class);
	}
	
	
	//////////////////
	private String selectAllSortedByUIDWithASCOrderSqlString;
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
			int i = rs.getInt(this.getManagementTableSchema().getColumnIndex(VisSchemeAppliedArchiveReproducedAndInsertedInstanceID.UID_COLUMN.getName()));
			if(i>ret) //first row's UID is larger than 1, simply return 1;
				return ret;
			else //first row must be equal to 1;
				ret++;
		}
		
		while(rs.next()) {
			int i = rs.getInt(this.getManagementTableSchema().getColumnIndex(VisSchemeAppliedArchiveReproducedAndInsertedInstanceID.UID_COLUMN.getName()));
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
			orderByColumnNameList.add(VisSchemeAppliedArchiveReproducedAndInsertedInstanceID.UID_COLUMN.getName().getStringValue());
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

	
	/////////////////////////////////////////////////////////////////
	@Override
	public Map<SimpleName, ManagementTableColumn> getPrimaryKeyAttributeNameMap() {
		if(this.primaryKeyAttributeNameMap == null) {
			primaryKeyAttributeNameMap = new HashMap<>();
		
			primaryKeyAttributeNameMap.put(VisSchemeAppliedArchiveReproducedAndInsertedInstanceID.UID_COLUMN.getName(), VisSchemeAppliedArchiveReproducedAndInsertedInstanceID.UID_COLUMN);
		}
		return primaryKeyAttributeNameMap;
	}
	
	
	@Override
	public List<ManagementTableColumn> getTypeSpecificManagementTableColumnList() {
		List<ManagementTableColumn> ret = new ArrayList<>();
		ret.add(VIS_SCHEME_UID_COLUMN);
		ret.add(VIS_SCHEME_APPLIER_ARCHIVE_UID_COLUMN);
		return ret;
	}
	
	
	//////////////////////////////////////////////////////
	
	/**
	 * this method is only applicable for user-defined FileFormat
	 */
	@Override
	protected void setTypeSpecificColumnValues(PreparedStatement ps, VisSchemeAppliedArchiveReproducedAndInsertedInstance entity) throws SQLException {
		ps.setInt(
				this.getManagementTableSchema().getColumnIndex(VIS_SCHEME_UID_COLUMN.getName()),
				entity.getAppliedVisSchemeID().getUID()
				);
		ps.setInt(
				this.getManagementTableSchema().getColumnIndex(VIS_SCHEME_APPLIER_ARCHIVE_UID_COLUMN.getName()),
				entity.getApplierArchiveID().getUID()
				);
	}
	
	@Override
	protected void typeSpecificInsertionRelatedActivity(VisSchemeAppliedArchiveReproducedAndInsertedInstance t) {
		//do nothing
	}
	
}
