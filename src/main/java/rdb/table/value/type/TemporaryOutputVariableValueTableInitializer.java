package rdb.table.value.type;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basic.SimpleName;
import context.VisframeContextConstants;
import context.project.VisProjectDBContext;
import function.composition.CompositionFunction;
import function.variable.output.type.TemporaryOutputVariable;
import rdb.table.value.ValueTableColumn;
import rdb.table.value.ValueTableName;


/**
 * 102120-update
 * factory class that builds TemporaryOutputVariableValueTableSchema for given CompositionFunctions and CFTargetValueTableRunUIDs in a host VisProjectDBContext;
 * 
 * schema name = VisProjectRDBConstants.CALCULATION_SCHEMA_NAME
 *  
 * table name = TO_(RunUID)
 * 
 * NON-RUID column name = COL_(componentFunctionIndexID)_(evaluatorIndexID)_(variableAliasName)
 * 
 * first column is RUID column, which is also the only primary key column;
 * @author tanxu
 *
 */
public class TemporaryOutputVariableValueTableInitializer extends ValueTableInitializer<TemporaryOutputVariableValueTableSchema>{
	public final static String VALUE_TABLE_NAME_PREFIX = "TO_";
	private final static String COLUMN_NAME_PREFIX = "COL_";
	
	/**
	 * make and return a column name in Temporary output variable value table for the given TemporaryOutputVariable;
	 * 
	 * should contain information regarding host component function index id, host evaluator index id and variable alias name;
	 * 
	 * @return
	 */
	public static String makeColumnNameStringValue(TemporaryOutputVariable tov) {
		
		StringBuilder sb = new StringBuilder();
		sb.append(COLUMN_NAME_PREFIX)
		.append(tov.getHostComponentFunctionIndexID()).append(VisframeContextConstants.NAME_STRING_COMPONENT_LINKER)
		.append(tov.getHostEvaluatorIndexID()).append(VisframeContextConstants.NAME_STRING_COMPONENT_LINKER)
		.append(tov.getAliasName().getStringValue());
		
		return sb.toString();
	}
	
	/////////////////////
	private Map<TemporaryOutputVariable, SimpleName> temporaryOutputVariableColumnNameMap;
	
	/**
	 * 
	 * @param hostVisProjectDBContext
	 * @param targetCompositionFunction
	 * @param CFTargetValueTableRunUID
	 * @throws SQLException 
	 */
	public TemporaryOutputVariableValueTableInitializer(VisProjectDBContext hostVisProjectDBContext,
			CompositionFunction targetCompositionFunction, int CFTargetValueTableRunUID) throws SQLException {
		super(hostVisProjectDBContext, targetCompositionFunction, CFTargetValueTableRunUID);
		// TODO Auto-generated constructor stub
	}

	
	
	/**
	 * @return the temporaryOutputVariableColumnNameMap
	 */
	public Map<TemporaryOutputVariable, SimpleName> getTemporaryOutputVariableColumnNameMap() {
		return temporaryOutputVariableColumnNameMap;
	}


	
	@Override
	protected void makeTableSchema() {
		ValueTableName tableName = new ValueTableName(VALUE_TABLE_NAME_PREFIX.concat(Integer.toString(this.getCFTargetValueTableRunUID())));
		
		List<ValueTableColumn> tableColumnList = this.makeTableColumnList();
		
		this.valueTableSchema = new TemporaryOutputVariableValueTableSchema(tableName, tableColumnList);
	}

	
	//////////////////////////////////
	
	private List<ValueTableColumn> makeTableColumnList() {
		this.temporaryOutputVariableColumnNameMap = new HashMap<>();
		
		List<ValueTableColumn> ret = new ArrayList<>();
		//add RUID column
		ret.add(makeRUIDColumn());
		
		for(TemporaryOutputVariable tov:this.getTargetCompositionFunction().getSortedListOfTemporaryOutputVariable()) {
			SimpleName columName = new SimpleName(makeColumnNameStringValue(tov));
			
			ret.add(
					new ValueTableColumn(
							columName,
							tov.getSQLDataType(),
							false, //in primary key
							false, //unique
							false, //not null; always can be null!
							null//defaultStringValue
					));
			
			temporaryOutputVariableColumnNameMap.put(tov, columName);
		}
		
		
		return ret;
	}
	
	
}
