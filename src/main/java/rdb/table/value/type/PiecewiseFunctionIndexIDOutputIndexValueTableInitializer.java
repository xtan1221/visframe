package rdb.table.value.type;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basic.SimpleName;
import context.project.VisProjectDBContext;
import function.component.PiecewiseFunction;
import function.composition.CompositionFunction;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.table.value.ValueTableColumn;
import rdb.table.value.ValueTableName;

/**
 * 102120-update
 * factory class that builds PiecewiseFunctionIndexIDOutputIndexValueTableSchema for given CompositionFunctions and CFTargetValueTableRun UIDs in a host VisProjectDBContext;
 * 
 * schema name = VisProjectRDBConstants.CALCULATION_SCHEMA_NAME
 *  
 * table name = PF_(RunUID)
 * 
 * NON-RUID column name = COL_(componentFunctionIndexID)
 * 		all non-RUID column can be null;
 * 		also the default value of each non-RUID column are set to {@link PiecewiseFunction#DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX};
 * 
 * 
 * first column is RUID column, which is the only primary key column;
 * 
 * @author tanxu
 *
 */
public class PiecewiseFunctionIndexIDOutputIndexValueTableInitializer extends ValueTableInitializer<PiecewiseFunctionIndexIDOutputIndexValueTableSchema>{
	public static final String VALUE_TABLE_NAME_PREFIX = "PF_";
	private static final String COL_NAME_PREFIX = "COL_";
	
	/////////////////////////////////
	private Map<Integer, SimpleName> pfIndexIDColumnNameMap;
	
	
	/**
	 * 
	 * @param hostVisProjectDBContext
	 * @param targetCompositionFunction
	 * @param CFTargetValueTableRunUID
	 * @throws SQLException 
	 */
	public PiecewiseFunctionIndexIDOutputIndexValueTableInitializer(VisProjectDBContext hostVisProjectDBContext,
			CompositionFunction targetCompositionFunction, int CFTargetValueTableRunUID) throws SQLException {
		super(hostVisProjectDBContext, targetCompositionFunction, CFTargetValueTableRunUID);
		// TODO Auto-generated constructor stub
	}
	
	
	
	/**
	 * @return the pfIndexIDColumnNameMap
	 */
	public Map<Integer, SimpleName> getPfIndexIDColumnNameMap() {
		return pfIndexIDColumnNameMap;
	}

	
	/////////////////////////////////
	@Override
	protected void makeTableSchema() {
		ValueTableName tableName = new ValueTableName(VALUE_TABLE_NAME_PREFIX.concat(Integer.toString(this.getCFTargetValueTableRunUID())));
		List<ValueTableColumn> tableColumnList = this.makeTableColumnList();
		
		this.valueTableSchema = new PiecewiseFunctionIndexIDOutputIndexValueTableSchema(tableName, tableColumnList);
	}
	
	
	/**
	 * 
	 * @return
	 */
	private List<ValueTableColumn> makeTableColumnList() {
		this.pfIndexIDColumnNameMap = new HashMap<>();
		
		List<ValueTableColumn> ret = new ArrayList<>();
		//add RUID column
		ret.add(makeRUIDColumn());
		
		for(int indexID: this.getTargetCompositionFunction().getSortedListOfPiecewiseFunctionIndexID()) {
			SimpleName columName = new SimpleName(COL_NAME_PREFIX.concat(Integer.toString(indexID)));
			
			ret.add(
					new ValueTableColumn(
							columName,
							SQLDataTypeFactory.integerType(),
							false, //inPrimaryKey 
							false, //unique
							true, //not null; default value is the DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX;
							Integer.toString(PiecewiseFunction.DEFAULT_NEXT_FUNCTION_OUTPUT_INDEX) //default value is the index for default next function
					));
			
			this.pfIndexIDColumnNameMap.put(indexID, columName);
			
		}
		
		return ret;
	}
	
}
