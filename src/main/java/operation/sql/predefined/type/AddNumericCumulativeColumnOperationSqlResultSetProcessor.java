package operation.sql.predefined.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;

import context.project.VisProjectDBContext;
import operation.sql.predefined.utils.CumulativeColumnSymjaExpressionDelegate;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import rdb.table.data.DataTableSchema;
import sql.ResultSetUtils;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;

/**
 * if the cumulative col is of integer type, the calculated value will be transformed to integer by {@link Math#round(double)} method;
 * 		1. if the calculated value is an integer, this will make no difference;
 * 		2. but if the calculated value is not an integer, this will lose information;
 * 
 * 
 * @author tanxu
 *
 */
class AddNumericCumulativeColumnOperationSqlResultSetProcessor {
	public static final int BATCH_SIZE = 1000;
	
	///////////////////////////////////
	private final VisProjectDBContext hostVisProjectDBContext;
	private final DataTableSchema outputDataTableSchema;

	/**
	 * see {@link AddNumericCumulativeColumnOperation#getInitialValue()}
	 */
	private final double initialValue;
	/**
	 * see {@link AddNumericCumulativeColumnOperation#getCumulativeNumericColumnNameInOutputDataTable()};
	 */
	private final DataTableColumnName cumulativeNumericColName;
	/**
	 * see {@link AddNumericCumulativeColumnOperation#getCumulativeNumericColumnSymjaExpression()};
	 */
	private final CumulativeColumnSymjaExpressionDelegate symjaExpressionDelegate;
	
	/**
	 * see {@link AddNumericCumulativeColumnOperation#getGroupByColumnNameSet()}
	 */
	private final Map<DataTableColumnName, DataTableColumn> groupByColNameMap;
	/**
	 * see {@link AddNumericCumulativeColumnOperation#getOrderByColumnNameSet()}
	 */
	private final Map<DataTableColumnName, DataTableColumn> orderByColNameMap;
	/**
	 * see {@link AddNumericCumulativeColumnOperation#getOtherKeptNonPKColumnNameSet()}
	 */
	private final Map<DataTableColumnName, DataTableColumn> otherNonPKColNameMap;
	/**
	 * primary key columns of input record data table not included in above sets;
	 */
	private final Map<DataTableColumnName, DataTableColumn> otherPKColNameMap;
	/**
	 * see {@link CumulativeColumnSymjaExpressionDelegate#getColumnSymjaVariableNameMap()}
	 */
	private final Map<DataTableColumnName, DataTableColumn> symjaExpressionVariableColNameMap;
	
	/**
	 * result set by sql query that select all the columns involved in above sets from the input data table;
	 */
	private final ResultSet rs;
	
	
	////////////////////////////////////////
	private Map<DataTableColumnName, String> currentGroupGroupByColStringValMap = null;
	//
	private Map<DataTableColumnName, String> currentRecordOrderByColStringValMap;
	private Map<DataTableColumnName, String> currentRecordOtherNonPKColStringValMap;
	private Map<DataTableColumnName, String> currentRecordOtherPKColStringValMap;//primary key columns of input record data table not included in group by and order column sets;
	private Map<DataTableColumnName, String> currentRecordSymjaExpressionVariableColStringValMap;
	private double currentRecordCalculatedCumulativeColValue;
	
	
	
	private List<String> currentNonRUIDColumnUpperCaseNameListInDataTableSchema;
	private PreparedStatement ps;
	private int currentBatchSize=0;
	
	/**
	 * 
	 * @param hostVisProjectDBContext
	 * @param outputDataTableSchema
	 * @param initialValue
	 * @param cumulativeNumericColName
	 * @param symjaExpressionDelegate
	 * @param groupByColNameMap
	 * @param orderByColNameMap
	 * @param otherNonPKColNameMap
	 * @param otherPKColNameMap
	 * @param symjaExpressionVariableColNameMap
	 * @param rs
	 */
	AddNumericCumulativeColumnOperationSqlResultSetProcessor(
			VisProjectDBContext hostVisProjectDBContext, DataTableSchema outputDataTableSchema,
			double initialValue, DataTableColumnName cumulativeNumericColName, CumulativeColumnSymjaExpressionDelegate symjaExpressionDelegate,
			
			Map<DataTableColumnName, DataTableColumn> groupByColNameMap,
			Map<DataTableColumnName, DataTableColumn> orderByColNameMap,
			Map<DataTableColumnName, DataTableColumn> otherNonPKColNameMap,
			Map<DataTableColumnName, DataTableColumn> otherPKColNameMap,
			Map<DataTableColumnName, DataTableColumn> symjaExpressionVariableColNameMap,
			
			ResultSet rs
			){
		
		this.hostVisProjectDBContext = hostVisProjectDBContext;
		this.outputDataTableSchema = outputDataTableSchema;
		
		this.initialValue = initialValue;
		this.cumulativeNumericColName = cumulativeNumericColName;
		this.symjaExpressionDelegate = symjaExpressionDelegate;
		
		this.groupByColNameMap = groupByColNameMap;
		this.orderByColNameMap = orderByColNameMap;
		this.otherNonPKColNameMap = otherNonPKColNameMap;
		this.otherPKColNameMap = otherPKColNameMap;
		this.symjaExpressionVariableColNameMap = symjaExpressionVariableColNameMap;
		
		
		this.rs = rs;
	}
	
	
	/**
	 * invoked after the constructor
	 * @throws SQLException
	 */
	void perform() throws SQLException {
		this.buildPreparedStatement();
		this.processResultSet();
		this.postprocess();
	}
	

	/**
	 * @throws SQLException 
	 * 
	 */
	private void buildPreparedStatement() throws SQLException {
		currentNonRUIDColumnUpperCaseNameListInDataTableSchema = new ArrayList<>(); //not including the RUID column since it is auto increment
		for(DataTableColumn col:this.outputDataTableSchema.getOrderedListOfNonRUIDColumn()) {
			currentNonRUIDColumnUpperCaseNameListInDataTableSchema.add(col.getName().getStringValue().toUpperCase());
		}
		
		ps = this.hostVisProjectDBContext.getDBConnection().prepareStatement(
				TableContentSQLStringFactory.buildInsertIntoPreparedStatementSqlString(
						SQLStringUtils.buildTableFullPathString(this.outputDataTableSchema.getSchemaName(), this.outputDataTableSchema.getName()), 
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema
						)
				);
	}
	
	/**
	 * @throws SQLException 
	 * 
	 */
	private void processResultSet() throws SQLException {
		Double previousRecordVal = null;
		
		
		while(this.rs.next()) {
			//extract group by col string values of the current record
			Map<DataTableColumnName, String> groupByColNameStringValueMap = ResultSetUtils.getCurrentRecordColumnNameStringValueMap(rs, groupByColNameMap);
			//extract other column string values of current reocrd
			this.currentRecordOrderByColStringValMap = ResultSetUtils.getCurrentRecordColumnNameStringValueMap(rs,this.orderByColNameMap);
			this.currentRecordOtherNonPKColStringValMap = ResultSetUtils.getCurrentRecordColumnNameStringValueMap(rs,this.otherNonPKColNameMap);
			this.currentRecordOtherPKColStringValMap = ResultSetUtils.getCurrentRecordColumnNameStringValueMap(rs,this.otherPKColNameMap);
			
			this.currentRecordSymjaExpressionVariableColStringValMap = ResultSetUtils.getCurrentRecordColumnNameStringValueMap(rs,this.symjaExpressionVariableColNameMap);
			
			if(!Objects.equal(this.currentGroupGroupByColStringValMap, groupByColNameStringValueMap)){//new group
				if(this.currentGroupGroupByColStringValMap!=null) {//there was a previous group
					//do nothing?
				}
				
				//reset
				this.currentRecordCalculatedCumulativeColValue = this.initialValue;
				this.currentGroupGroupByColStringValMap = groupByColNameStringValueMap;
				
			}else {//same group
//				String v = this.symjaExpressionDelegate.evaluate(
//						this.currentRecordSymjaExpressionVariableColStringValMap, 
//						Double.toString(previousRecordVal));
				this.currentRecordCalculatedCumulativeColValue = 
						Double.parseDouble(
								this.symjaExpressionDelegate.evaluate(
										this.currentRecordSymjaExpressionVariableColStringValMap, 
										Double.toString(previousRecordVal)));
			}
			
			//add to PreparedStatement
			this.addToPreparedStatement();
			
			//store current calculated value as previousRecordVal
			previousRecordVal = this.currentRecordCalculatedCumulativeColValue;
		}
		
	}
	
	
	/**
	 * @throws SQLException 
	 * 
	 */
	private void addToPreparedStatement() throws SQLException {
		//set group by col name values
		for(DataTableColumnName groupByColName:this.currentGroupGroupByColStringValMap.keySet()) {
			this.outputDataTableSchema.getColumn(groupByColName).getSqlDataType().setPreparedStatement(
					ps, 
					currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(groupByColName.getStringValue().toUpperCase())+1, 
					currentGroupGroupByColStringValMap.get(groupByColName)
					);
		}
		//order by cols
		for(DataTableColumnName groupByColName:this.currentRecordOrderByColStringValMap.keySet()) {
			this.outputDataTableSchema.getColumn(groupByColName).getSqlDataType().setPreparedStatement(
					ps, 
					currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(groupByColName.getStringValue().toUpperCase())+1, 
					currentRecordOrderByColStringValMap.get(groupByColName)
					);
		}
		//other pk cols
		for(DataTableColumnName groupByColName:this.currentRecordOtherPKColStringValMap.keySet()) {
			this.outputDataTableSchema.getColumn(groupByColName).getSqlDataType().setPreparedStatement(
					ps, 
					currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(groupByColName.getStringValue().toUpperCase())+1, 
					currentRecordOtherPKColStringValMap.get(groupByColName)
					);
		}
		//other non-pk cols
		for(DataTableColumnName groupByColName:this.currentRecordOtherNonPKColStringValMap.keySet()) {
			this.outputDataTableSchema.getColumn(groupByColName).getSqlDataType().setPreparedStatement(
					ps, 
					currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(groupByColName.getStringValue().toUpperCase())+1, 
					currentRecordOtherNonPKColStringValMap.get(groupByColName)
					);
		}
		
		//cumulative column
		String currentRecordCalculatedCumulativeColStringValue;
		if(this.cumulativeColIsIntegerType()) {
			int intVal = (int)Math.round(this.currentRecordCalculatedCumulativeColValue);
			if(intVal!=this.currentRecordCalculatedCumulativeColValue) {
				System.out.println("calculated cumulative column value is not an integer as wanted:"+this.currentRecordCalculatedCumulativeColValue);
			}
			currentRecordCalculatedCumulativeColStringValue = Integer.toString(intVal);
		}else {
			currentRecordCalculatedCumulativeColStringValue = Double.toString(
					this.currentRecordCalculatedCumulativeColValue);
		}
		
		this.outputDataTableSchema.getColumn(this.cumulativeNumericColName).getSqlDataType().setPreparedStatement(
				ps, 
				currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(this.cumulativeNumericColName.getStringValue().toUpperCase())+1, 
				currentRecordCalculatedCumulativeColStringValue
				);
		
		//////////////////////////////
		currentBatchSize++;
		ps.addBatch();
		
		if(currentBatchSize>BATCH_SIZE) {
			ps.executeBatch();
			ps.clearBatch();
			currentBatchSize = 0;
		}
		
	}

	
	private boolean cumulativeColIsIntegerType() {
		// TODO Auto-generated method stub
		return this.symjaExpressionDelegate.getSymjaExpression().getSqlDataType().isGenericInt();
	}


	/**
	 * @throws SQLException 
	 * 
	 */
	private void postprocess() throws SQLException {
		ps.executeBatch();
		ps.clearBatch();
		ps.close();
	}


}
