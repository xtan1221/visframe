package visinstance.run.calculation.evaluator.sqlbuilder;

import static sql.SQLStringUtils.*;

import java.sql.SQLException;
import java.util.HashSet;
import basic.SimpleName;
import context.project.rdb.VisProjectRDBConstants;
import function.component.ComponentFunction;
import function.composition.CompositionFunctionID;
import function.evaluator.Evaluator;
import function.evaluator.nonsqlbased.NonSQLQueryBasedEvaluator;
import function.variable.input.recordwise.RecordwiseInputVariable;
import function.variable.input.recordwise.type.CFGTargetInputVariable;
import function.variable.input.recordwise.type.RecordAttributeInputVariable;
import function.variable.input.recordwise.type.UpstreamValueTableColumnOutputVariableInputVariable;
import function.variable.output.OutputVariable;
import function.variable.output.type.CFGTargetOutputVariable;
import function.variable.output.type.TemporaryOutputVariable;
import function.variable.output.type.ValueTableColumnOutputVariable;
import rdb.table.AbstractRelationalTableSchemaID;
import rdb.table.data.DataTableColumnName;
import rdb.table.data.DataTableSchemaID;
import rdb.table.value.type.CFTargetValueTableSchemaID;
import rdb.table.value.type.TemporaryOutputVariableValueTableInitializer;
import visinstance.run.calculation.evaluator.updator.NonSqlQueryBasedEvaluatorValueTableUpdateManager;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;

/**
 * builder for sql query string for {@link NonSqlBasedEvaluator};
 * 
 * the main task is to retrieve the {@link RecordwiseInputVariable}s of the NonSqlBasedEvaluator to facilitate calculating the value of the output variables;
 * 
 * note that the {@link NonRecordwiseInputVariable}s of the NonSqlBasedEvaluator do not need to be retrieved with sql query;
 * 
 * after the sql query is run and the ResultSet is generated, need to extract the {@link RecordwiseInputVariable}s' values for each RUID value of the owner record data table
 * together with the {@link NonRecordwiseInputVariable} values to calculate the output variables of the NonSqlBasedEvaluator with a {@link NonSqlQueryBasedEvaluatorValueTableUpdateManager};
 * 
 * 
 * @author tanxu
 * 
 */
public abstract class NonSqlBasedEvaluatorSqlQueryStringBuilder extends EvaluatorFullSqlQueryStringBuilderBase<NonSQLQueryBasedEvaluator> {
	/**
	 * constructor
	 * @param calculator
	 * @param hostComponentFunction
	 * @param evaluator
	 * @throws SQLException 
	 */
	protected NonSqlBasedEvaluatorSqlQueryStringBuilder(
			CFTargetValueTableRunCalculator calculator,
			ComponentFunction hostComponentFunction,
			NonSQLQueryBasedEvaluator evaluator) throws SQLException {
		super(calculator, hostComponentFunction, evaluator);
		// TODO Auto-generated constructor stub
		
		
	}
	
	@Override
	protected void build() throws SQLException {
		
		this.buildSelectClauseString();
		this.buildWhereClauseString();
		//
		this.buildFromClauseString();
		
		//
		this.buildFullSqlQueryString();
	}
	
	/**
	 * find out the {@link RecordwiseInputVariable}s of the evaluator and build the select clause with leading RUID column of the owner record data table;
	 * 
	 * //TODO??
	 * 		for all {@link RecordwiseInputVariable}s, build a list of them with the same order as they are present in the SELECT clause;
	 * 
	 * also build the {@link #recordwiseInputVariableBasedTableSchemaIDSet} by adding the {@link AbstractRelationalTableSchemaID} of each {@link RecordwiseInputVariable};
	 * 
	 * @throws SQLException 
	 */
	@Override
	protected void buildSelectClauseString() throws SQLException {

		this.recordwiseInputVariableBasedTableSchemaIDSet = new HashSet<>();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(SELECT_CLAUSE_HEADER);
		
		//RUID column of owner record data table of host CFG
		sb.append(buildTableColumnFullPathString(
						this.getCFTargetValueTableRunCalculator().getOwnerRecordDataMetadata().getDataTableSchema().getID(), 
						VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE));
		
		//auto-include the owner record data table of host CFG
		this.recordwiseInputVariableBasedTableSchemaIDSet.add(this.getCFTargetValueTableRunCalculator().getOwnerRecordDataMetadata().getDataTableSchema().getID());
		
		//RecordwiseInputVariable of the evaluator
		//{@link NonSqlQueryBasedEvaluator#getOrderedListOfRecordwiseInputVariable()}
		for(RecordwiseInputVariable iv:this.getEvaluator().getOrderedListOfRecordwiseInputVariable()) {
			if(iv instanceof RecordAttributeInputVariable) {//must be the owner record data table;
				RecordAttributeInputVariable rciv = (RecordAttributeInputVariable)iv;
				DataTableSchemaID schemaID = rciv.getDataTableSchemaID();
				DataTableColumnName columnName = rciv.getColumn().getName();
				//validate
				if(!schemaID.equals(this.getCFTargetValueTableRunCalculator().getOwnerRecordDataMetadata().getDataTableSchema().getID())) {
					throw new IllegalArgumentException();
				}
				
				sb.append(", ").append(buildTableColumnFullPathString(schemaID, columnName));
				
				this.recordwiseInputVariableBasedTableSchemaIDSet.add(schemaID);
				
			}else if(iv instanceof CFGTargetInputVariable) {//must be of the CFG of the same owner record data table
				CFGTargetInputVariable civ = (CFGTargetInputVariable)iv;
				
				CompositionFunctionID dependedCFID = this.getCFTargetValueTableRunCalculator().getVisInstanceRunCalculator().getHostVisProjectDBContext()
						.getCompositionFuncitionID(civ.getTargetCompositionFunctionGroupID(), civ.getTarget().getName());
				
				CFTargetValueTableSchemaID dependedCFTargetValueTableSchemaID = 
						this.getCFTargetValueTableRunCalculator().getVisInstanceRunCalculator().getCalculatedCFIDTargetValueTableRunMap().get(dependedCFID).getTableSchemaID();
				
				sb.append(COMMA).append(
						buildTableColumnFullPathString(
								dependedCFTargetValueTableSchemaID,
								civ.getTarget().getName())
						);
				
				this.recordwiseInputVariableBasedTableSchemaIDSet.add(dependedCFTargetValueTableSchemaID);
				
			}else if(iv instanceof UpstreamValueTableColumnOutputVariableInputVariable) {//TemporaryOutputVarible value table
				UpstreamValueTableColumnOutputVariableInputVariable uiv = (UpstreamValueTableColumnOutputVariableInputVariable)iv;
				
				int upstreamOVHostComponentFunctionIndexID = uiv.getUpstreamValueTableColumnOutputVariable().getHostComponentFunctionIndexID();
				int upstreamOVHostEvaluatorIndexID = uiv.getUpstreamValueTableColumnOutputVariable().getHostEvaluatorIndexID();
				SimpleName upstreamOVAliasName = uiv.getUpstreamValueTableColumnOutputVariable().getAliasName();
				
				
				Evaluator upstreamEvaluator = this.getCFTargetValueTableRunCalculator()
						.getTargetCompositionFunction().getComponentFunction(upstreamOVHostComponentFunctionIndexID)
						.getEvaluator(upstreamOVHostEvaluatorIndexID);
				OutputVariable upstreamOutputVariable = 
						upstreamEvaluator.getOutputVariableAliasNameMap().get(upstreamOVAliasName);
				
				if(upstreamOutputVariable instanceof ValueTableColumnOutputVariable) {
					if(upstreamOutputVariable instanceof CFGTargetOutputVariable) {//upstream output variable with an assigned target of the CF
						CFGTargetOutputVariable cov = (CFGTargetOutputVariable)upstreamOutputVariable;
						
						
						sb.append(COMMA).append(
								buildTableColumnFullPathString(
										this.getCFTargetValueTableRunCalculator().getCFTargetValueTableInitializer().getValueTableSchema().getID(), //value table schemaID
										cov.getTargetName()//column name
								)
						);
						
						this.recordwiseInputVariableBasedTableSchemaIDSet.add(
								this.getCFTargetValueTableRunCalculator().getCFTargetValueTableInitializer().getValueTableSchema().getID());
				
					}else if(upstreamOutputVariable instanceof TemporaryOutputVariable) {
						TemporaryOutputVariable tov = (TemporaryOutputVariable)upstreamOutputVariable;
						
						sb.append(COMMA).append(
								buildTableColumnFullPathString(
										this.getCFTargetValueTableRunCalculator().getTemporaryOutputVariableValueTableInitializer().getValueTableSchema().getID(), //value table schemaID
										TemporaryOutputVariableValueTableInitializer.makeColumnNameStringValue(tov)//column name for the temporary output variable in TemporaryOutputVariableValueTable
								)
						);
				
						this.recordwiseInputVariableBasedTableSchemaIDSet.add(
								this.getCFTargetValueTableRunCalculator().getTemporaryOutputVariableValueTableInitializer().getValueTableSchema().getID());
						
					}else {
						throw new IllegalArgumentException();
					}
					
				}else {//not ValueTableColumnOutputVariable type, not allowed
					throw new IllegalArgumentException();
				}
			}else {
				throw new IllegalArgumentException();
			}
			
		}
		
		this.selectClauseString = sb.toString();
	}
	
	
	/**
	 * build the full sql query string;
	 * 
	 * format: SELECT ... FROM ... WHERE ...
	 */
	@Override
	protected void buildFullSqlQueryString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.selectClauseString).append(SPACE)
		.append(this.fromClauseString).append(SPACE)
		.append(this.whereClauseString);
		
		this.builtFullSqlQueryString = sb.toString();
		
	}

}
