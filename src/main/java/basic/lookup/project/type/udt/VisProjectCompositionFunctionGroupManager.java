package basic.lookup.project.type.udt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.lookup.project.type.VisframeUDTTypeManagerBase;
import context.VisframeContextConstants;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import context.scheme.appliedarchive.reproducedandinsertedinstance.utils.CFGReproducingAndInsertionTracker;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroup;
import function.group.CompositionFunctionGroupID;
import function.group.CompositionFunctionGroupName;
import function.group.ShapeCFG;
import function.target.CFGTarget;
import rdb.sqltype.SQLStringType;
import rdb.table.lookup.ManagementTableColumn;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;

public class VisProjectCompositionFunctionGroupManager extends VisframeUDTTypeManagerBase<CompositionFunctionGroup,CompositionFunctionGroupID>{
	/**
	* whether the file format is a default one defined by visframe or not;
	* if true, cannot be deleted from the management table;
	*/
	public static final ManagementTableColumn OWNER_RECORD_DATA_NAME_COL = new ManagementTableColumn(
		//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
		new SimpleName("OWNER_RECORD_DATA_NAME"), new SQLStringType(VisframeContextConstants.MAX_METADATA_NAME_LEN,false), false, false,
		//Boolean notNull, String defaultStringValue, String additionalConstraints
		true, null, null 
	);
	
	/**
	* name of the data table for record type metadata;
	* null for other data type
	*/
	public static final ManagementTableColumn CFG_TYPE_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("TYPE"), new SQLStringType(50, false), false, false, //
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null 
	);
	
	//////////////////////
	private Map<SimpleName, ManagementTableColumn> primaryKeyAttributeNameMap;
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	public VisProjectCompositionFunctionGroupManager(VisProjectDBContext visProjectDBContext) {
		super(visProjectDBContext, CompositionFunctionGroup.class, CompositionFunctionGroupID.class);
	}
	
	/**
	 * return the set of targets of the given CompositionFunctionGroupID that are not assigned to a CompositionFunction;
	 * @param cfgID
	 * @return
	 * @throws SQLException
	 */
	public Map<SimpleName, CFGTarget<?>> getCFGTargetNameMapUnassignedToCF(CompositionFunctionGroupID cfgID) throws SQLException{
		Map<SimpleName, CFGTarget<?>> ret = new LinkedHashMap<>();
		ret.putAll(this.lookup(cfgID).getTargetNameMap());
		
		for(CompositionFunctionID cfID:this.getHasIDTypeManagerController().getCompositionFunctionManager().getCompositionFunctionIDSetOfGroupID(cfgID)) {
			this.getHasIDTypeManagerController().getCompositionFunctionManager().lookup(cfID).getAssignedTargetNameSet().forEach(e->{
				ret.remove(e);
			});
		}
		
		return ret;
	}
	
	
	/**
	 * retrieve and return the current set of CompositionFunctionGroupID for ShapeCFGs all of whose mandatory targets are assigned to one CompositionFunction;
	 * @return
	 * @throws SQLException 
	 */
	public Set<CompositionFunctionGroupID> getShapeCFGIDSetWithAllMandatoryTargetsAssignedToCF() throws SQLException{
		Set<CompositionFunctionGroupID> ret = new HashSet<>();
		
		//add those ShapeCFG type CFG with all mandatory targets assigned to a CompositionFunction
		Map<CompositionFunctionGroupID, CompositionFunctionGroup> cfgIDMap = 
				this.getHostVisProjectDBContext().getHasIDTypeManagerController().getCompositionFunctionGroupManager().retrieveAll();
		for(CompositionFunctionGroupID cfgID: cfgIDMap.keySet()){
			CompositionFunctionGroup cfg = cfgIDMap.get(cfgID);
			if(cfg instanceof ShapeCFG && this.getHostVisProjectDBContext().getHasIDTypeManagerController().getCompositionFunctionGroupManager().getMandatoryCFGTargetNameMapUnassignedToCF(cfgID).isEmpty())
				ret.add(cfgID);
		}
		
		return ret;
	}
	
	
	/** 
	 * return the set of mandatory targets of the given CompositionFunctionGroupID not assigned to any CompositionFunction
	 * @param cfgID
	 * @return
	 * @throws SQLException
	 */
	public Map<SimpleName, CFGTarget<?>> getMandatoryCFGTargetNameMapUnassignedToCF(CompositionFunctionGroupID cfgID) throws SQLException{
		Map<SimpleName, CFGTarget<?>> ret = new LinkedHashMap<>();
		
		Map<SimpleName, ? extends CFGTarget<?>> allTargetNameMap = this.lookup(cfgID).getTargetNameMap();
		Set<SimpleName> mandatoryTargetNameSet = this.lookup(cfgID).getMandatoryTargetNameSet();
		
		allTargetNameMap.forEach((k,v)->{
			if(mandatoryTargetNameSet.contains(k))
				ret.put(k, v);
		});
		
		
		for(CompositionFunctionID cfID:this.getHasIDTypeManagerController().getCompositionFunctionManager().getCompositionFunctionIDSetOfGroupID(cfgID)) {
			this.getHasIDTypeManagerController().getCompositionFunctionManager().lookup(cfID).getAssignedTargetNameSet().forEach(e->{
				ret.remove(e);
			});
		}
		
		return ret;
	}
	/////////////////////////
	@Override
	public Map<SimpleName, ManagementTableColumn> getPrimaryKeyAttributeNameMap() {
		if(this.primaryKeyAttributeNameMap == null) {
			primaryKeyAttributeNameMap = new HashMap<>();
		
			primaryKeyAttributeNameMap.put(CompositionFunctionGroupID.NAME_COLUMN.getName(), CompositionFunctionGroupID.NAME_COLUMN);
		}
		return primaryKeyAttributeNameMap;
	}
	
	@Override
	public List<ManagementTableColumn> getTypeSpecificManagementTableColumnList() {
		List<ManagementTableColumn> ret = new ArrayList<>();
		ret.add(OWNER_RECORD_DATA_NAME_COL);
		ret.add(CFG_TYPE_COLUMN);
		return ret;
	}

	@Override
	protected void setTypeSpecificColumnValues(PreparedStatement ps, CompositionFunctionGroup entity)
			throws SQLException {
		
		ps.setString(
				this.getManagementTableSchema().getColumnIndex(OWNER_RECORD_DATA_NAME_COL.getName()),
				entity.getOwnerRecordDataMetadataID().getName().getStringValue());
		
		ps.setString(
				this.getManagementTableSchema().getColumnIndex(CFG_TYPE_COLUMN.getName()),
				entity.getClass().getSimpleName());
	}

	
	
	//////////////////////////
	@Override
	protected void typeSpecificInsertionRelatedActivity(CompositionFunctionGroup t) {
		//do nothing
	}
	
	//////////////////////////////
	//////////////////////////////////////
	/**
	 * build a reproduced CompositionFunctionGroupID based on the given one so that the reproduced one is not existing in the management table;
	 * 
	 * the reproduced CompositionFunctionGroupID has the name string in format:
	 * 		originalNameString_randomInteger 
	 * where randomInteger is a positive integer
	 * 
	 * so that the reproduced CompositionFunctionGroupID is different from all existing ones in the management table;
	 * 
	 * this method facilitates {@link CompositionFunctionGroupID#reproduce(VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)}
	 * 
	 * note that different from Operations, CFGs are all reproduced before inserted into host VisProjectDBContext, 
	 * thus in this method, to check if a CFGID is already occupied or not, 
	 * need to check both those in the management table and those in the {@link CFGReproducingAndInsertionTracker#getReproducedCFGIDListByInsertionOrder()}
	 * 
	 * @param CompositionFunctionGroupID
	 * @return
	 * @throws SQLException 
	 */
	public CompositionFunctionGroupID buildReproducedID(CompositionFunctionGroupID originalID, CFGReproducingAndInsertionTracker CFGReproducingAndInsertionTracker) throws SQLException {
		int index = 1;
		
		//
		String nameString = originalID.getName().getStringValue().concat("_").concat(Integer.toString(index));
		
		//check if there is a Operation instance in the management table with the same OperationID;
		String colName = CompositionFunctionGroupID.NAME_COLUMN.getName().getStringValue();
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
		
		CompositionFunctionGroupID candidateCFGID = new CompositionFunctionGroupID(new CompositionFunctionGroupName(nameString));
		
		boolean alreadyExisting = rs.next() || CFGReproducingAndInsertionTracker.reproducedCFGIDAlreadyGeneratedByThisRun(candidateCFGID);
		
		while(alreadyExisting) {
			index++;
			nameString = originalID.getName().getStringValue().concat("_").concat(Integer.toString(index));
			selectAllSqlStringWithTheMetadataID = TableContentSQLStringFactory.buildSelectAllSQLString(
					SQLStringUtils.buildTableFullPathString(this.getManagementTableSchemeName(), this.getManagementTableName()), 
					TableContentSQLStringFactory.buildColumnValueEquityCondition(colName, nameString, ofStringType, toIgnoreCase),
					null, 
					null
					);
			
			candidateCFGID = new CompositionFunctionGroupID(new CompositionFunctionGroupName(nameString));
			
			ResultSet rs2 = statement.executeQuery(selectAllSqlStringWithTheMetadataID);
			
			alreadyExisting = rs2.next() || CFGReproducingAndInsertionTracker.reproducedCFGIDAlreadyGeneratedByThisRun(candidateCFGID);;
		}
		
		//
		return candidateCFGID;
	}
	
}
