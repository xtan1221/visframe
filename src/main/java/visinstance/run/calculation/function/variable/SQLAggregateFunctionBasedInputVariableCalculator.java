package visinstance.run.calculation.function.variable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import exception.VisframeException;
import function.variable.input.nonrecordwise.type.SQLAggregateFunctionBasedInputVariable;
import function.variable.input.nonrecordwise.type.SQLAggregateFunctionBasedInputVariable.VfSQLAggregateFunctionType;
import metadata.record.RecordDataMetadata;
import rdb.table.HasIDTypeRelationalTableSchema;
import rdb.table.HasIDTypeRelationalTableSchemaID;
import sql.SQLStringUtils;
import utils.Pair;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;

/**
 * calculator class for a {@link SQLAggregateFunctionBasedInputVariable} with a {@link CFTargetValueTableRunCalculator};
 * 
 * @author tanxu
 *
 */
public class SQLAggregateFunctionBasedInputVariableCalculator {
	private final CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator;
	private final SQLAggregateFunctionBasedInputVariable inputVariable;
	
	
	/////
	private String sqlQueryString;
	private String caluclatedStringValue;
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param inputVariable
	 * @throws SQLException 
	 */
	public SQLAggregateFunctionBasedInputVariableCalculator(
			CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator,
			SQLAggregateFunctionBasedInputVariable inputVariable
			) throws SQLException{
		if(CFTargetValueTableRunCalculator == null)
			throw new IllegalArgumentException("given CFTargetValueTableRunCalculator cannot be null!");
		if(inputVariable == null)
			throw new IllegalArgumentException("given inputVariable cannot be null!");
		
		
		this.CFTargetValueTableRunCalculator = CFTargetValueTableRunCalculator;
		this.inputVariable = inputVariable;
		
		//
		this.buildSqlQueryString();
		
		this.query();
		
	}
	
	

	/**
	 * build the sql query string;
	 * @throws SQLException 
	 */
	private void buildSqlQueryString() throws SQLException {
		String table1FullPathName = null;
		String column1FullPathName = null;
		
		//
		String table2FullPathName = null;
		String column2FullPathName=null;
		
		if(this.inputVariable.getAggregateFunctionType().getRequiredNumOfColumn()==null) {//no required input column; for example, COUNT; only need to build the table full path name;
			RecordDataMetadata rmd = (RecordDataMetadata) this.CFTargetValueTableRunCalculator.getHostVisProjectDBContext().getMetadataLookup().lookup(this.inputVariable.getTargetRecordMetadataID());
			table1FullPathName = SQLStringUtils.buildTableFullPathString(rmd.getDataTableSchema().getID());
		}else if(this.inputVariable.getAggregateFunctionType().getRequiredNumOfColumn()==1){
			Pair<HasIDTypeRelationalTableSchemaID<? extends HasIDTypeRelationalTableSchema>,String> tableSchemaIDColumnNameStringPair =
					this.CFTargetValueTableRunCalculator.buildRecordwiseInputVariableTableSchemaIDFullPathNameStringPair(this.inputVariable.getRecordwiseInputVariable1());
			table1FullPathName = SQLStringUtils.buildTableFullPathString(tableSchemaIDColumnNameStringPair.getFirst());
			column1FullPathName = tableSchemaIDColumnNameStringPair.getSecond();
		}else if(this.inputVariable.getAggregateFunctionType().getRequiredNumOfColumn()==2){
			Pair<HasIDTypeRelationalTableSchemaID<? extends HasIDTypeRelationalTableSchema>,String> col1TableSchemaIDColumnNameStringPair =
					this.CFTargetValueTableRunCalculator.buildRecordwiseInputVariableTableSchemaIDFullPathNameStringPair(this.inputVariable.getRecordwiseInputVariable1());
			table1FullPathName = SQLStringUtils.buildTableFullPathString(col1TableSchemaIDColumnNameStringPair.getFirst());
			column1FullPathName = col1TableSchemaIDColumnNameStringPair.getSecond();
			
			Pair<HasIDTypeRelationalTableSchemaID<? extends HasIDTypeRelationalTableSchema>,String> col2TableSchemaIDColumnNameStringPair =
					this.CFTargetValueTableRunCalculator.buildRecordwiseInputVariableTableSchemaIDFullPathNameStringPair(this.inputVariable.getRecordwiseInputVariable1());
			table2FullPathName = SQLStringUtils.buildTableFullPathString(col2TableSchemaIDColumnNameStringPair.getFirst());
			column2FullPathName = col2TableSchemaIDColumnNameStringPair.getSecond();
		}
		
		
		if(this.inputVariable.getAggregateFunctionType().equals(VfSQLAggregateFunctionType.AVG)) {
			
			this.sqlQueryString = "SELECT AVG(".concat(column1FullPathName).concat(") FROM ").concat(table1FullPathName);
			
		}else if(this.inputVariable.getAggregateFunctionType().equals(VfSQLAggregateFunctionType.COUNT)) {
			this.sqlQueryString = "SELECT COUNT(*) FROM ".concat(table1FullPathName);
		}else if(this.inputVariable.getAggregateFunctionType().equals(VfSQLAggregateFunctionType.MAX)) {
			this.sqlQueryString = "SELECT MAX(".concat(column1FullPathName).concat(") FROM ").concat(table1FullPathName);
		}else if(this.inputVariable.getAggregateFunctionType().equals(VfSQLAggregateFunctionType.MIN)) {
			this.sqlQueryString = "SELECT MIN(".concat(column1FullPathName).concat(") FROM ").concat(table1FullPathName);
		}else if(this.inputVariable.getAggregateFunctionType().equals(VfSQLAggregateFunctionType.SUM)) {
			this.sqlQueryString = "SELECT SUM(".concat(column1FullPathName).concat(") FROM ").concat(table1FullPathName);
		}else if(this.inputVariable.getAggregateFunctionType().equals(VfSQLAggregateFunctionType.CORRELATE)) {
			//TODO no function in derby??
		}else if(this.inputVariable.getAggregateFunctionType().equals(VfSQLAggregateFunctionType.CONTAINING_NULL)) {
			//SELECT col FROM table WHERE col IS NULL;
			this.sqlQueryString = "SELECT".concat(column1FullPathName).concat(" FROM ").concat(table1FullPathName).concat(" WHERE ").concat(column1FullPathName).concat(" IS NULL");
		}
		
	}
	
	
	
	/**
	 * perform the sql query string and process the returned ResultSet;
	 * @throws SQLException 
	 */
	private void query() throws SQLException {
		Statement statement = this.CFTargetValueTableRunCalculator.getHostVisProjectDBContext().getDBConnection().createStatement(); 
		ResultSet rs = statement.executeQuery(this.sqlQueryString);
		
		if(!rs.next()) {//check if there is any row in the returned query ResultSet
			if(this.inputVariable.getAggregateFunctionType().equals(VfSQLAggregateFunctionType.CONTAINING_NULL)) {
				this.caluclatedStringValue = "false";
			}else {
				throw new VisframeException("no result is found for visframe aggregate function query!");
			}
		}else {
			if(this.inputVariable.getAggregateFunctionType().equals(VfSQLAggregateFunctionType.AVG)) {
				this.caluclatedStringValue = rs.getString(1);
			}else if(this.inputVariable.getAggregateFunctionType().equals(VfSQLAggregateFunctionType.COUNT)) {
				this.caluclatedStringValue = rs.getString(1);
			}else if(this.inputVariable.getAggregateFunctionType().equals(VfSQLAggregateFunctionType.MAX)) {
				this.caluclatedStringValue = rs.getString(1);
			}else if(this.inputVariable.getAggregateFunctionType().equals(VfSQLAggregateFunctionType.MIN)) {
				this.caluclatedStringValue = rs.getString(1);
			}else if(this.inputVariable.getAggregateFunctionType().equals(VfSQLAggregateFunctionType.SUM)) {
				this.caluclatedStringValue = rs.getString(1);
			}else if(this.inputVariable.getAggregateFunctionType().equals(VfSQLAggregateFunctionType.CORRELATE)) {
				//TODO no function in derby??
			}else if(this.inputVariable.getAggregateFunctionType().equals(VfSQLAggregateFunctionType.CONTAINING_NULL)) {
				this.caluclatedStringValue = "true";
			}
		}
		
	}
	
	///////////////
	/**
	 * return the calculated string value
	 * @return
	 */
	public String getCaluclatedStringValue() {
		return caluclatedStringValue;
	}
	
}
