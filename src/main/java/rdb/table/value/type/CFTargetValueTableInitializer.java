package rdb.table.value.type;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basic.SimpleName;
import context.project.VisProjectDBContext;
import function.composition.CompositionFunction;
import function.group.CompositionFunctionGroup;
import function.target.CFGTarget;
import rdb.table.value.ValueTableColumn;
import rdb.table.value.ValueTableName;


/**
 * 102120-update
 * factory class that builds CFTargetValueTableSchemas for given CompositionFunctions and CFTargetValueTableRun UIDs in a host VisProjectDBContext;
 * 
 * schema name = VisProjectRDBConstants.VALUE_SCHEMA_NAME
 *  
 * table name = CF_(RunUID) //since CFTargetValueTableRun UID is unique for each CFTargetValueTableRun among all CFTargetValueTableRuns in the same host VisProjectDBContext;
 * 
 * non-RUID column name = target name
 * 		note that for graphics property target, the name is the full path name on graphics property tree;
 * 		note that non-RUID column should always be CAN BE NULL rather than use the {@link CFGTarget#canBeNull()}
 * 			this is because during calculation, all columns with no default value will be set to null when RUID column is populated;
 * 			if target cannot be null, and it has no default value, error will be resulted;
 * 			thus, after calculation is done, check each non-RUID column if it cannot be null and if it contains null valued rows;	
 * 
 * 
 * first column is RUID column, which is also the only primary key column;
 * 		note that RUID column in the data table of record Metadata is not in the primary key set;
 * 
 * @author tanxu
 *
 */
public class CFTargetValueTableInitializer extends ValueTableInitializer<CFTargetValueTableSchema>{
	public static final String CFTargetValueTableNAME_PREFIX = "CF_";
	
	////////////////////////////////////
	private Map<SimpleName, SimpleName> targetNameColNameMap;
	
	
	/**
	 * 
	 * @param hostVisProjectDBContext
	 * @param targetCompositionFunction
	 * @param CFTargetValueTableRunUID
	 * @throws SQLException 
	 */
	public CFTargetValueTableInitializer(
			VisProjectDBContext hostVisProjectDBContext,
			CompositionFunction targetCompositionFunction, int CFTargetValueTableRunUID) throws SQLException {
		super(hostVisProjectDBContext, targetCompositionFunction, CFTargetValueTableRunUID);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @return the targetNameColNameMap
	 */
	public Map<SimpleName, SimpleName> getTargetNameColNameMap() {
		return targetNameColNameMap;
	}
	
	/////////////////////////////////////////////
	/**
	 * table name I = CF_(CFTargetValueTableRunUID)
	 * 
	 * RUID column is the only primary key column;
	 * @throws SQLException 
	 * 
	 */
	@Override
	protected void makeTableSchema() throws SQLException {
		ValueTableName tableName = new ValueTableName(CFTargetValueTableNAME_PREFIX.concat(Integer.toString(this.getCFTargetValueTableRunUID())));
		List<ValueTableColumn> tableColumnList = this.makeTableColumnList();
		
		this.valueTableSchema = new CFTargetValueTableSchema(tableName, tableColumnList);
	}

	
	
	/**
	 * for each assigned target to the CompositionFunction, create a ValueTableColumn;
	 * 
	 * column name is the same with the target name;
	 * @throws SQLException 
	 * 
	 */
	private List<ValueTableColumn> makeTableColumnList() throws SQLException {
		this.targetNameColNameMap = new HashMap<>();
		CompositionFunctionGroup cfg = 
				this.getHostVisProjectDBContext().getHasIDTypeManagerController().getCompositionFunctionGroupManager().lookup(this.getTargetCompositionFunction().getHostCompositionFunctionGroupID());
		List<ValueTableColumn> ret = new ArrayList<>();
		//add RUID column
		ret.add(makeRUIDColumn());
		
		//add assigned targets
		for(SimpleName targetName:this.getTargetCompositionFunction().getOrderedListOfAssignedTargetName()) {
			CFGTarget<?> target = cfg.getTargetNameMap().get(targetName);
			
			ret.add(
					new ValueTableColumn(
							target.getName(),
							target.getSQLDataType(),
							false, //in primary key
							false, //unique
							false,//Boolean notNull; instead of target.canBeNull(), always can be null in the calculation stage; otherwise, may result in error when populating the RUID column; after calculation is done, check column values with the target.canBeNull()
							target.getDefaultStringValue()
					));
			
			targetNameColNameMap.put(targetName, targetName);
		}
		
		return ret;
			
	}
	
	

}