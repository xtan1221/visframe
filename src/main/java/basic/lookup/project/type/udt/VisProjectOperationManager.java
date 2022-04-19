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
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import operation.Operation;
import operation.OperationID;
import operation.OperationName;
import rdb.sqltype.SQLStringType;
import rdb.table.lookup.ManagementTableColumn;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;

public class VisProjectOperationManager extends VisframeUDTTypeManagerBase<Operation, OperationID>{
	/**
	* name of the data table for record type metadata;
	* null for other data type
	*/
	public static final ManagementTableColumn OPERATION_TYPE_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("OPERATION_TYPE"), new SQLStringType(100,false), false, false, //
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null 
	);
	
	///////////////////////////////////
	private Map<SimpleName, ManagementTableColumn> primaryKeyAttributeNameMap;
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	public VisProjectOperationManager(VisProjectDBContext visProjectDBContext) {
		super(visProjectDBContext, Operation.class, OperationID.class);
	}
	
	////////////////////////
	@Override
	public Map<SimpleName, ManagementTableColumn> getPrimaryKeyAttributeNameMap() {
		if(this.primaryKeyAttributeNameMap==null) {
			this.primaryKeyAttributeNameMap = new HashMap<>();
			
			this.primaryKeyAttributeNameMap.put(OperationID.NAME_COLUMN.getName(), OperationID.NAME_COLUMN);
		}
		
		return this.primaryKeyAttributeNameMap;
	}
	
	
	@Override
	public List<ManagementTableColumn> getTypeSpecificManagementTableColumnList() {
		List<ManagementTableColumn> ret = new ArrayList<>();
		ret.add(OPERATION_TYPE_COLUMN);
		
		return ret;
	}

	@Override
	protected void setTypeSpecificColumnValues(PreparedStatement ps, Operation entity) throws SQLException {
		String operationType = entity.getClass().getSimpleName();
		
		ps.setString(
				this.getManagementTableSchema().getColumnIndex(OPERATION_TYPE_COLUMN.getName()),
				operationType);
	}

	@Override
	protected void typeSpecificInsertionRelatedActivity(Operation t) {
		//do nothing
		//??TODO
	}
	
	//////////////////////////////////////
	/**
	 * build a reproduced OperationID based on the given one so that the reproduced one is not existing in the management table;
	 * 
	 * the reproduced OperationID has the name string in format:
	 * 		originalNameString_randomInteger 
	 * where randomInteger is a positive integer
	 * 
	 * so that the reproduced OperationID is different from all existing ones in the management table;
	 * 
	 * this method facilitates {@link OperationID#reproduce(VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)}
	 * 
	 * @param OperationID
	 * @return
	 * @throws SQLException 
	 */
	public OperationID buildReproducedID(OperationID originalOperationID) throws SQLException {
		int index = 1;
		
		//
		String nameString = originalOperationID.getInstanceName().getStringValue().concat("_").concat(Integer.toString(index));
		
		//check if there is a Operation instance in the management table with the same OperationID;
		String colName = OperationID.NAME_COLUMN.getName().getStringValue();
		boolean ofStringType = true;
		Boolean toIgnoreCase = true;
		//build the sql query string;
		String selectAllSqlStringWithTheMetadataID = TableContentSQLStringFactory.buildSelectAllSQLString(
				SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()), 
				TableContentSQLStringFactory.buildColumnValueEquityCondition(colName, nameString, ofStringType, toIgnoreCase),
				null, 
				null
				);
		//run the query
		Statement statement = this.getVisProjectDBConnection().createStatement(); 
		ResultSet rs = statement.executeQuery(selectAllSqlStringWithTheMetadataID);
		
		boolean alreadyExisting = rs.next();
		
		while(alreadyExisting) {
			index++;
			nameString = originalOperationID.getInstanceName().getStringValue().concat("_").concat(Integer.toString(index));
			selectAllSqlStringWithTheMetadataID = TableContentSQLStringFactory.buildSelectAllSQLString(
					SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()), 
					TableContentSQLStringFactory.buildColumnValueEquityCondition(colName, nameString, ofStringType, toIgnoreCase),
					null, 
					null
					);
			ResultSet rs2 = statement.executeQuery(selectAllSqlStringWithTheMetadataID);
			alreadyExisting = rs2.next();
		}
		
		//
		return new OperationID(new OperationName(nameString));
		
	}
}
