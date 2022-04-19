package visinstance.run.extractor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basic.SimpleName;
import context.project.VisProjectDBContext;
import context.project.rdb.VisProjectRDBConstants;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.group.ShapeCFG;
import graphics.shape.shape2D.fx.VfShapeTypeFXNodeFactory;
import graphics.shape.shape2D.fx.VfShapeTypeFXNodeFactoryFactory;
import javafx.geometry.Point2D;
import sql.SQLStringUtils;
import utils.Pair;
import visinstance.run.calculation.function.composition.CFTargetValueTableRun;

/**
 * extractor of all calculated target values of a core ShapeCFG of a VisInstanceRun in the host VisProjectDBContext;
 * 
 * @author tanxu
 *
 */
public final class CoreShapeCFGCalculatedTargetExtractor extends CoreShapeCFGExtractorBase<Pair<Point2D, Map<SimpleName, String>>> {

	/////////////////////////////////////
	/**
	 * 
	 */
	private SimpleName xLayoutTargetName;
	private SimpleName yLayoutTargetName;
	
	/**
	 * whether the x layout property is assigned to a CompositionFunction and thus calculated as a column in the corresponding target value table;
	 * if not, the default value should be used;
	 */
	private boolean xLayoutCalculatedInTargetValueTable;
	/**
	 * whether the y layout property is assigned to a CompositionFunction and thus calculated as a column in the corresponding target value table;
	 * if not, the default value should be used;
	 */
	private boolean yLayoutCalculatedInTargetValueTable;
	
	//the list of columns from all target value tables in the same order as in the built SELECT clause;
	private List<SimpleName> selectedTargetColumnNameList;
	
	/**
	 * 
	 */
	private VfShapeTypeFXNodeFactory<?,?> vfShapeTypeFXNodeFactory;
	

	/**
	 * 
	 * @param hostVisProjectDBContext
	 * @param coreShapeCFG
	 * @param compostionFunctionCFTargetValueTableRunMap
	 * @throws SQLException
	 */
	public CoreShapeCFGCalculatedTargetExtractor(
			VisProjectDBContext hostVisProjectDBContext, ShapeCFG coreShapeCFG,
			Map<CompositionFunction, CFTargetValueTableRun> compostionFunctionCFTargetValueTableRunMap)
			throws SQLException {
		super(hostVisProjectDBContext, coreShapeCFG, compostionFunctionCFTargetValueTableRunMap);
		// TODO Auto-generated constructor stub
	}

	/////////////////////////////

	/**
	 * 
	 */
	@Override
	protected void preprocess() throws SQLException {
		Map<SimpleName, CompositionFunctionID> targetNameAssignedCFIDMap = 
				this.getHostVisProjectDBContext().getHasIDTypeManagerController().getCompositionFunctionManager().getTargetNameAssignedCFIDMap(this.getCoreShapeCFG().getID());
		
		
		this.xLayoutTargetName = this.getCoreShapeCFG().getShapeType().getXLayoutLeafTarget().getName();
		this.yLayoutTargetName = this.getCoreShapeCFG().getShapeType().getYLayoutLeafTarget().getName();
		
		this.xLayoutCalculatedInTargetValueTable = targetNameAssignedCFIDMap.containsKey(this.xLayoutTargetName);
		this.yLayoutCalculatedInTargetValueTable = targetNameAssignedCFIDMap.containsKey(this.yLayoutTargetName);
		
		this.buildSqlQueryString();
		this.runSqlQuery();
	}
	
	/**
	 * select the 
	 * 1. RUID column from owner record data table;
	 * 2. all non-RUID columns from all CF Target value table of the core ShapeCFG;
	 * 		each column corresponds to an assigned targets;
	 * @throws SQLException 
	 */
	@Override
	protected String buildSelectSqlString() throws SQLException {
		this.selectedTargetColumnNameList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		
		
		//owner record data table RUID column;
		sb.append(
				SQLStringUtils.buildTableColumnFullPathString(
						this.getOwnerRecordDataTableSchema().getID(), 
						VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE));
		
		
		//all non-RUID columns from all CF Target value table of the core ShapeCFG;
		//each column corresponds to an assigned targets;
		this.getCompostionFunctionCFTargetValueTableRunMap().forEach((k,v)->{
			//for each assigned target of the CompositionFunction
			k.getAssignedTargetNameSet().forEach(n->{
				selectedTargetColumnNameList.add(n);
				sb.append(", ").append(
						SQLStringUtils.buildTableColumnFullPathString(
								v.getTableSchemaID(),
								n)
						);
			});
		});
		
		
		return sb.toString();
	}
	
	/**
	 * from tables:
	 * 1. owner record data table;
	 * 2. all CF Target value table of the core ShapeCFG;
	 * @throws SQLException 
	 */
	@Override
	protected String buildFromSqlString() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("FROM ");
		
		//owner record data table
		sb.append(
				SQLStringUtils.buildTableFullPathString(this.getOwnerRecordDataTableSchema().getID()));
		
		
		//all CF Target value table of the core ShapeCFG;
		this.getCompostionFunctionCFTargetValueTableRunMap().forEach((k,v)->{
			sb.append(", ").append(SQLStringUtils.buildTableFullPathString(v.getTableSchemaID()));
		});
		
		return sb.toString();
	}

	/**
	 * conditions:
	 * 1. equity between owner record data table RUID column and RUID column of each CF Target value table of the core ShapeCFG;
	 * @throws SQLException 
	 */
	@Override
	protected String buildWhereSqlString() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("WHERE ");
		
		boolean nothingAddedYet = true;
		for(CompositionFunction cf: this.getCompostionFunctionCFTargetValueTableRunMap().keySet()){
			
			if(nothingAddedYet) {
				nothingAddedYet = false;
			}else {
				sb.append(", ");
			}
			//RUID column equity condition;
			sb.append(
					SQLStringUtils.buildEquityConditionString(
							SQLStringUtils.buildTableColumnFullPathString(this.getOwnerRecordDataTableSchema().getID(), VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE),
							SQLStringUtils.buildTableColumnFullPathString(this.getCompostionFunctionCFTargetValueTableRunMap().get(cf).getTableSchemaID(), VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE)
					));
		}
		
		return sb.toString();
	}

	
	/**
	 * extract and return the
	 * 		1.layout coordinate
	 * 				consistent with {@link CoreShapeCFGCalculatedLayoutCoordinateExtractor}
	 * 		2.map from all assigned target name to the calculated string value (may be null);
	 * 
	 * of the next record of owner record data table from the CF target value tables of the core ShapeCFG;
	 * 
	 * return null if there is no record left;
	 * @return
	 * @throws SQLException 
	 */
	@Override
	public Pair<Point2D, Map<SimpleName, String>> nextRecord() throws SQLException{
		double x;
		double y;
		if(resultSet.next()) {
			if(this.xLayoutCalculatedInTargetValueTable)
				x = this.resultSet.getDouble(this.xLayoutTargetName.getStringValue().toUpperCase());
			else
				x = Double.parseDouble(this.getCoreShapeCFG().getShapeType().getXLayoutLeafTarget().getDefaultStringValue());
			
			if(this.yLayoutCalculatedInTargetValueTable)
				y = this.resultSet.getDouble(this.yLayoutTargetName.getStringValue().toUpperCase());
			else
				y = Double.parseDouble(this.getCoreShapeCFG().getShapeType().getYLayoutLeafTarget().getDefaultStringValue());
			
			Point2D layoutCoord = new Point2D(x,y);
			
			
			Map<SimpleName, String> allCalculatedTargetNameValueStringMap = new HashMap<>();
			
			for(int i=0;i<this.selectedTargetColumnNameList.size();i++) {
				allCalculatedTargetNameValueStringMap.put(this.selectedTargetColumnNameList.get(i), 
						resultSet.getString(i+2)); //+2 since the first column index is 1, also the first column is RUID column of owner record data table;
			}
			
			return new Pair<>(layoutCoord, allCalculatedTargetNameValueStringMap);
			
		}else {
			return null;
		}
	}


	/////////////////////////
	/**
	 * @return the vfShapeTypeFXNodeFactory
	 */
	public VfShapeTypeFXNodeFactory<?, ?> getVfShapeTypeFXNodeFactory() {
		if(this.vfShapeTypeFXNodeFactory == null) {
			this.vfShapeTypeFXNodeFactory = VfShapeTypeFXNodeFactoryFactory.getFactory(this.getCoreShapeCFG().getShapeType());
		}
		return vfShapeTypeFXNodeFactory;
	}

}
