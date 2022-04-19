package basic.lookup.project.type.udt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.lookup.project.type.VisframeUDTTypeManagerBase;
import context.VisframeContextConstants;
import context.project.VisProjectDBContext;
import context.project.VisProjectDBFeatures;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroup;
import function.group.CompositionFunctionGroupID;
import rdb.sqltype.SQLStringType;
import rdb.table.lookup.ManagementTableColumn;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;


/**
 * ===112520-update
 * note that insertion and deletion of CompostionFunction will not trigger any changes on the vis project CFD graph,
 * rather the project CFD graph will be built from scratch every time it is needed
 * see {@link VisProjectDBFeatures#getCFDGraph()}
 * 
 * 
 * @author tanxu
 *
 */
public class VisProjectCompositionFunctionManager extends VisframeUDTTypeManagerBase<CompositionFunction, CompositionFunctionID>{

	
	/////////////////file format specific columns, may be updated if more features are needed(not put in {@link VisframeUDTManagementProcessRelatedTableColumnFactory})
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
	
	
	////////////////////
	private Map<SimpleName, ManagementTableColumn> primaryKeyAttributeNameMap;
	
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	public VisProjectCompositionFunctionManager(
			VisProjectDBContext visProjectDBContext
			) {
		super(visProjectDBContext, CompositionFunction.class, CompositionFunctionID.class);
	}
	
	/**
	 * generate the next available index ID for a new {@link CompositionFunction} of the given {@link CompositionFunctionGroupID};
	 * 
	 * normally, index ID of {@link CompositionFunction} should be consecutive integer starting from 1, 2, 3, ...
	 * @param cfgID
	 * @return
	 * @throws SQLException 
	 */
	public int findNextAvailableCompositionFunctionIndexID(CompositionFunctionGroupID cfgID) throws SQLException {
		Collection<CompositionFunctionID> compositionFunctionIDSetOfGroupID = this.getCompositionFunctionIDSetOfGroupID(cfgID);
		
		Set<Integer> existingCFIndexIDSet = new HashSet<>();
		compositionFunctionIDSetOfGroupID.forEach(e->{
			existingCFIndexIDSet.add(e.getIndexID());
		});
		
		int newIndexID=1;
		while(existingCFIndexIDSet.contains(newIndexID)) {
			newIndexID++;
		}
		
		return newIndexID;
	}
	
	
	/**
	 * retrieve and return the map from the target name to the assigned CompositionFunctionID for the given host CompositionFunctionGroupID;
	 * 
	 * note that the map key only contains the assigned ones;
	 * 
	 * @param cfgID
	 * @return
	 * @throws SQLException 
	 */
	public Map<SimpleName,CompositionFunctionID> getTargetNameAssignedCFIDMap(CompositionFunctionGroupID cfgID) throws SQLException{
		//1 retrieve all CompositionFunctions from the management table of the CompositionFunctionGroupID
		String sqlString = TableContentSQLStringFactory.buildSelectAllWithConditionString(
				SQLStringUtils.buildTableFullPathString(this.getManagementTableSchema().getSchemaName(), this.getManagementTableName()),
				TableContentSQLStringFactory.buildIDEquityConditionString(this.getHasIDTypeManagerController().getCompositionFunctionGroupManager().getPrimaryKeyAttributeNameMap(), cfgID)
				);
		
		Statement statement = this.getVisProjectDBConnection().createStatement(); 
		ResultSet rs = statement.executeQuery(sqlString);
		
		//2. for each retrieved CompositionFunction, extract the assigned target names
		Map<SimpleName,CompositionFunctionID> ret = new HashMap<>();
		
		while(rs.next()) {
			
			CompositionFunction cf = (CompositionFunction)rs.getObject(this.getManagementTableSchema().getColumnIndex(this.getVisframeUDTColumn().getName()));
			
			for(SimpleName targetName:cf.getAssignedTargetNameSet()) {
				ret.put(targetName, cf.getID());
			}
		}
		
		
		return ret;
		
	}
	
	
	/**
	 * find out the set of mandatory target of the given {@link CompositionFunctionGroup} that is not assigned to any {@link CompositionFunction};
	 * 
	 * the mandatory targets of a CompositionFunctionGroup can be obtained by {@link CompositionFunctionGroup#getMandatoryTargetNameSet()};
	 * @param cfgID
	 * @return
	 * @throws SQLException
	 */
	public Set<SimpleName> findSetOfMandatoryTargetNotAssignedToCF(CompositionFunctionGroup cfg) throws SQLException {
		Set<SimpleName> ret = new HashSet<>();
		
		Map<SimpleName,CompositionFunctionID> targetNameAssignedCFIDMap = this.getTargetNameAssignedCFIDMap(cfg.getID());
		
		for(SimpleName mandatoryTarget:cfg.getMandatoryTargetNameSet()) {
			if(!targetNameAssignedCFIDMap.keySet().contains(mandatoryTarget)) {
				ret.add(mandatoryTarget);
			}
		}
		
		return ret;
	}
	
	
	
	/**
	 * retrieve and return the collection of CompositionFunctionID in the management table with owner CFG of the given one;
	 * @param cfgID
	 * @return
	 * @throws SQLException
	 */
	public Collection<CompositionFunctionID> getCompositionFunctionIDSetOfGroupID(CompositionFunctionGroupID cfgID) throws SQLException{
		return this.getTargetNameAssignedCFIDMap(cfgID).values();
	}
	
	
	////////////////////////////////////////
//	/**
//	 * first delete the given id from the management table by invoke super.delete();
//	 * 
//	 * then delete the id from the CFD graph of the host VisProjectDBContext;
//	 * 
//	 */
//	@Override
//	public void delete(ID<? extends HasID> id) throws SQLException {
//		super.delete(id);
//		
//		//
////		this.getHostVisProjectDBContext().getCFDGraph().remove((CompositionFunctionID)id);
//	}
	
	@Override
	public Map<SimpleName, ManagementTableColumn> getPrimaryKeyAttributeNameMap() {
		if(this.primaryKeyAttributeNameMap == null) {
			primaryKeyAttributeNameMap = new HashMap<>();
		
			primaryKeyAttributeNameMap.putAll(this.getHostVisProjectDBContext().getHasIDTypeManagerController().getCompositionFunctionGroupManager().getPrimaryKeyAttributeNameMap());
			primaryKeyAttributeNameMap.put(CompositionFunctionID.INDEX_ID_COLUMN.getName(), CompositionFunctionID.INDEX_ID_COLUMN);
		}
		
		return primaryKeyAttributeNameMap;
	}
	
	@Override
	public List<ManagementTableColumn> getTypeSpecificManagementTableColumnList() {
		List<ManagementTableColumn> ret = new ArrayList<>();
//		ret.add(OWNER_RECORD_DATA_NAME_COL);
		
		return ret;
	}
	
	@Override
	protected void setTypeSpecificColumnValues(PreparedStatement ps, CompositionFunction entity) throws SQLException {
//		ps.setString(
//				this.getManagementTableSchema().getColumnIndex(OWNER_RECORD_DATA_NAME_COL.getName()), 
//				entity.getOwnerRecordDataMetadataID().getName().getStringValue());
	}

	///////////////////////////////////////////
	/**
	 */
	@Override
	protected void typeSpecificInsertionRelatedActivity(CompositionFunction cf) {
		//do nothing
	}
	
}
