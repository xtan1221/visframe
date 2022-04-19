package visinstance.run.calculation.evaluator.sqlbuilder;

import static context.project.rdb.VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE;
import static sql.SQLStringUtils.*;

import java.sql.SQLException;
import java.util.Set;

import function.component.ComponentFunction;
import function.component.PiecewiseFunction;
import function.evaluator.Evaluator;
import rdb.table.HasIDTypeRelationalTableSchema;
import rdb.table.HasIDTypeRelationalTableSchemaID;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;


/**
 * base class to build a full sql query string that generates a table view to facilitate calculating an evaluator;
 * 
 * the main types:
 * 		sql based evaluator
 * 			SimpleFunction
 * 			PiecewiseFunction
 * 		non sql based evaluator
 * 			SimpleFunction
 * 			PiecewiseFunction
 * 
 * 
 * @author tanxu
 *
 */
public abstract class EvaluatorFullSqlQueryStringBuilderBase<T extends Evaluator> {
	private final CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator;
	private final ComponentFunction hostComponentFunction;
	private final T evaluator;
	/**
	 * the set of table schema ID of RecordwiseInputVariables to be included in the SELECT and WHERE clauses 
	 * note that the owner record data table of the host CFG should be auto-included
	 * 		the RUID column of owner record data table of host CFG is always present in the SELECT clause;
	 * 		note that it is possible that there is no InputVariable based on owner record data, so it should be auto-included;
	 * 
	 */
	protected Set<HasIDTypeRelationalTableSchemaID<? extends HasIDTypeRelationalTableSchema>> recordwiseInputVariableBasedTableSchemaIDSet;
	protected String selectClauseString;
	protected String whereClauseString;
	protected String fromClauseString;
	
	//
	protected String builtFullSqlQueryString;
	
	/**
	 * constructor
	 * @param calculator
	 * @param hostComponentFunction
	 * @throws SQLException 
	 */
	EvaluatorFullSqlQueryStringBuilderBase(
			CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator,
			ComponentFunction hostComponentFunction,
			T evaluator) throws SQLException{
		
		this.CFTargetValueTableRunCalculator = CFTargetValueTableRunCalculator;
		this.hostComponentFunction = hostComponentFunction;
		this.evaluator = evaluator;
		
		//invoke the build method
		this.build();
	}
	

	protected CFTargetValueTableRunCalculator getCFTargetValueTableRunCalculator() {
		return CFTargetValueTableRunCalculator;
	}
	
	
	protected ComponentFunction getHostComponentFunction() {
		return hostComponentFunction;
	}
	

	protected T getEvaluator() {
		return this.evaluator;
	}
	
	////////////////////////////////
	/**
	 * build and return the full sql query string by concatenating every built clause string;
	 * @return
	 */
	public String getBuiltFullSqlQuerString() {
		return this.builtFullSqlQueryString;
	}
	
	
	///////////////////////////////////////////
	
	
	/**
	 * build all clauses by invoking the corresponding methods in an certain order;
	 * @throws SQLException 
	 */
	protected abstract void build() throws SQLException;
	
	
	/**
	 * build the SELECT clause string and find out involved input Table Schema IDs and add to inputTableSchemaIDSet
	 * @throws SQLException 
	 */
	protected abstract void buildSelectClauseString() throws SQLException;
	
	/**
	 * build the where condition related with the PiecewiseFunctionOutputIndexValueTable;
	 * only relevant if the upstream PiecewiseFunction set is non-empty;
	 * 
	 * 1. RUID column equity between owner record data table and PiecewiseFunction index id output index value table
	 * 2. upstream PiecewiseFunction output index condition
	 * @return
	 * @throws SQLException 
	 */
	protected String buildPiecewiseFunctionOutputIndexValueTableRelatedConditionString() throws SQLException {
		StringBuilder sb = new StringBuilder();
		boolean nothingAddedYet = true;
		
		//1. add condition between owner record data table's RUID column and RUID column of the PiecewiseFunction index id output index value table 
		//if the host ComponentFunction is of PiecewiseFunction type 
		//AND/OR
		//if there is at least one upstream PiecewiseFunction;
		if(this.getHostComponentFunction() instanceof PiecewiseFunction || !this.getHostComponentFunction().getUpstreamPiecewiseFunctionIndexIDOutputIndexMap().isEmpty()) {
			String conditionString = buildEquityConditionString(
					buildTableColumnFullPathString(
							this.getCFTargetValueTableRunCalculator().getOwnerRecordDataMetadata().getDataTableSchema().getID(), 
							RUID_COLUMN_NAME_STRING_VALUE),
					buildTableColumnFullPathString(
							this.getCFTargetValueTableRunCalculator().getPiecewiseFunctionIndexIDOutputIndexValueTableInitializer().getValueTableSchema().getID(), 
							RUID_COLUMN_NAME_STRING_VALUE)
					);
			sb.append(conditionString);
			
			nothingAddedYet = false;
		}
		
		//2. only add upstream PiecewiseFunction output index condition if there is at least one upstream PiecewiseFunction;
		if(!this.getHostComponentFunction().getUpstreamPiecewiseFunctionIndexIDOutputIndexMap().isEmpty()) {//
			
			for(int uspfid:this.getHostComponentFunction().getUpstreamPiecewiseFunctionIndexIDOutputIndexMap().keySet()) {
				String columnNameStringValue = 
						this.getCFTargetValueTableRunCalculator().getPiecewiseFunctionIndexIDOutputIndexValueTableInitializer().getPfIndexIDColumnNameMap().get(uspfid).getStringValue();
						
				String columnFullPathName = buildTableColumnFullPathString(
						this.getCFTargetValueTableRunCalculator().getPiecewiseFunctionIndexIDOutputIndexValueTableInitializer().getValueTableSchema().getID(),
						columnNameStringValue);
				
				String conditionString = buildEquityConditionString(
						columnFullPathName, 
						this.getHostComponentFunction().getUpstreamPiecewiseFunctionIndexIDOutputIndexMap().get(uspfid));
				//
				if(nothingAddedYet) {
					nothingAddedYet = true;
				}else {
					sb.append(" AND ");
				}
				sb.append(conditionString);
			}
			
			return sb.toString();
		}
		
		
		return sb.toString();
	}
	
	/**
	 * build where condition on equity between the owner record data table schema's RUID column and RUID column of all other recordwiseInputVariable Based TableSchema
	 * 
	 * RUID column equity condition
	 * 		between owner record data table RUID column and 
	 * 			RUID column of EVERY value table of the same owner record data table appearing in the SELECT clause that are put in {@link #recordwiseInputVariableBasedTableSchemaIDSet};
	 * @return
	 * @throws SQLException
	 */
	protected String buildRecordwiseInputVariableBasedTableSchemaRUIDColumnEquityConditionString() throws SQLException {
		StringBuilder sb = new StringBuilder();
		boolean nothingAddedYet = true;
		for(HasIDTypeRelationalTableSchemaID<? extends HasIDTypeRelationalTableSchema> schemaID:this.recordwiseInputVariableBasedTableSchemaIDSet) {
			//skip the owner record data table in the {@link #recordwiseInputVariableBasedTableSchemaIDSet};
			if(schemaID.equals(this.getCFTargetValueTableRunCalculator().getOwnerRecordDataMetadata().getDataTableSchema().getID()))
				continue;
			
			String conditionString = buildEquityConditionString(
					buildTableColumnFullPathString(
							this.getCFTargetValueTableRunCalculator().getOwnerRecordDataMetadata().getDataTableSchema().getID(), 
							RUID_COLUMN_NAME_STRING_VALUE),
					buildTableColumnFullPathString(schemaID, RUID_COLUMN_NAME_STRING_VALUE)
					);
			
			if(nothingAddedYet) {
				sb.append(conditionString);
				nothingAddedYet = false;
			}else {
				sb.append(SPACE).append(AND).append(SPACE)
				.append(conditionString);
			}
			
		}
		
		//
		if(nothingAddedYet) {
			return "";
		}else {
			return sb.toString();
		}
	}
	
	/**
	 * build the WHERE clause string and find out the involved input table schema IDs and add to inputTableSchemaIDSet
	 * @throws SQLException 
	 */
	protected abstract void buildWhereClauseString() throws SQLException;
	
	
	/**
	 * build the FROM clause string with all RelationalTableSchemaID in inputTableSchemaIDSet;
	 * not including FROM keyword?
	 */
	protected void buildFromClauseString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(FROM_CLAUSE_HEADER);
		
		boolean firstAdded = false;
		for(HasIDTypeRelationalTableSchemaID<? extends HasIDTypeRelationalTableSchema> schemaID:this.recordwiseInputVariableBasedTableSchemaIDSet) {
			if(firstAdded) {
				sb.append(COMMA).append(buildTableFullPathString(schemaID));
			}else {
				sb.append(buildTableFullPathString(schemaID));
				firstAdded = true;
			}
		}
		
		//add PiecewiseFunction index id output index value table 
		//if the host ComponentFunction is of PiecewiseFunction type 
		//AND/OR
		//if there is at least one upstream PiecewiseFunction;
		if(this.getHostComponentFunction() instanceof PiecewiseFunction || !this.getHostComponentFunction().getUpstreamPiecewiseFunctionIndexIDOutputIndexMap().isEmpty()) {
			sb.append(", ").append(buildTableFullPathString(
					this.getCFTargetValueTableRunCalculator().getPiecewiseFunctionIndexIDOutputIndexValueTableInitializer().getValueTableSchema().getID()));
		}
		
		//
		this.fromClauseString = sb.toString();
	}
	
	
	/**
	 * build the full SQL query String with the built clause strings;
	 */
	protected abstract void buildFullSqlQueryString();
}
