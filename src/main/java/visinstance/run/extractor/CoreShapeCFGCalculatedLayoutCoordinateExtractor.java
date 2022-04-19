package visinstance.run.extractor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import basic.SimpleName;
import context.project.VisProjectDBContext;
import context.project.rdb.VisProjectRDBConstants;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.group.ShapeCFG;
import javafx.geometry.Point2D;
import sql.SQLStringUtils;
import visinstance.run.calculation.function.composition.CFTargetValueTableRun;

/**
 * extractor for layout coordinate of shape entities of each record of the owner record data;
 * 
 * @author tanxu
 *
 */
public final class CoreShapeCFGCalculatedLayoutCoordinateExtractor extends CoreShapeCFGExtractorBase<Point2D> {
	
	/**
	 * the list of CFTargetValueTableRun of CompositionFunctions to which one or more layout related targets are assigned;
	 */
	private List<CFTargetValueTableRun> CFTargetValueTableRunListOfCFWithLayoutRelatedTargetAssigned;
	
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
	
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param coreShapeCFG
	 * @param compostionFunctionCFTargetValueTableRunMap
	 * @throws SQLException
	 */
	CoreShapeCFGCalculatedLayoutCoordinateExtractor(
			VisProjectDBContext hostVisProjectDBContext, ShapeCFG coreShapeCFG,
			Map<CompositionFunction, CFTargetValueTableRun> compostionFunctionCFTargetValueTableRunMap)
			throws SQLException {
		super(hostVisProjectDBContext, coreShapeCFG, compostionFunctionCFTargetValueTableRunMap);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void preprocess() throws SQLException {
		Map<SimpleName, CompositionFunctionID> targetNameAssignedCFIDMap = 
				this.getHostVisProjectDBContext().getHasIDTypeManagerController().getCompositionFunctionManager().getTargetNameAssignedCFIDMap(this.getCoreShapeCFG().getID());
		
		
		this.xLayoutTargetName = this.getCoreShapeCFG().getShapeType().getXLayoutLeafTarget().getName();
		this.yLayoutTargetName = this.getCoreShapeCFG().getShapeType().getYLayoutLeafTarget().getName();
		
		this.xLayoutCalculatedInTargetValueTable = targetNameAssignedCFIDMap.containsKey(this.xLayoutTargetName);
		this.yLayoutCalculatedInTargetValueTable = targetNameAssignedCFIDMap.containsKey(this.yLayoutTargetName);
		
		//always build sql and run it;
		//even if both x and y layout are not calculated, still need to extract the RUID column of each record of owner record data table to make the layout of each of them;
		this.buildSqlQueryString();
		this.runSqlQuery();
	}
	
	
	/**
	 * select
	 * 1. RUID column of owner record data table;
	 * 2. column of the cf target value table corresponding to the assigned layout related target;
	 * 
	 */
	@Override
	protected String buildSelectSqlString() throws SQLException {
		this.CFTargetValueTableRunListOfCFWithLayoutRelatedTargetAssigned = new ArrayList<>();
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		
		//owner record data table RUID column;
		sb.append(SQLStringUtils.buildTableColumnFullPathString(
						this.getOwnerRecordDataTableSchema().getID(), 
						VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE));
		
		Map<SimpleName, CompositionFunctionID> targetNameAssignedCFIDMap = 
				this.getHostVisProjectDBContext().getHasIDTypeManagerController().getCompositionFunctionManager().getTargetNameAssignedCFIDMap(this.getCoreShapeCFG().getID());
		
		//x layout
		if(this.xLayoutCalculatedInTargetValueTable) {
			CFTargetValueTableRun xAssignedCFTargetValueTableRun = 
					this.getCompostionFunctionCFTargetValueTableRunMap().get(
						this.getCompositionFunction(
								targetNameAssignedCFIDMap.get(this.xLayoutTargetName)));
			
			sb.append(", ").append(
					SQLStringUtils.buildTableColumnFullPathString(
							xAssignedCFTargetValueTableRun.getTableSchemaID(),
							this.xLayoutTargetName
							));
			
			this.CFTargetValueTableRunListOfCFWithLayoutRelatedTargetAssigned.add(xAssignedCFTargetValueTableRun);
		}
		
		//y layout
		if(this.yLayoutCalculatedInTargetValueTable) {
			CFTargetValueTableRun yAssignedCFTargetValueTableRun = 
					this.getCompostionFunctionCFTargetValueTableRunMap().get(
						this.getCompositionFunction(
								targetNameAssignedCFIDMap.get(this.yLayoutTargetName)));
			
			sb.append(", ").append(
					SQLStringUtils.buildTableColumnFullPathString(
							yAssignedCFTargetValueTableRun.getTableSchemaID(),
							this.yLayoutTargetName
							));
			
			if(!this.CFTargetValueTableRunListOfCFWithLayoutRelatedTargetAssigned.contains(yAssignedCFTargetValueTableRun))
				this.CFTargetValueTableRunListOfCFWithLayoutRelatedTargetAssigned.add(yAssignedCFTargetValueTableRun);
		}
		//
		return sb.toString();
	}
	
	
	/**
	 * from
	 * 1. owner record data table;
	 * 2. CF target value table of CompositionFunction to which the layout related target of the shape type are assigned;
	 */
	@Override
	protected String buildFromSqlString() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("FROM ");
		
		//owner record data table
		sb.append(
				SQLStringUtils.buildTableFullPathString(this.getOwnerRecordDataTableSchema().getID()));
		
		
		//all CF Target value table in CFTargetValueTableRunListOfCFWithLayoutRelatedTargetAssigned
		this.CFTargetValueTableRunListOfCFWithLayoutRelatedTargetAssigned.forEach(r->{
			sb.append(", ").append(SQLStringUtils.buildTableFullPathString(r.getTableSchemaID()));
		});
		
		return sb.toString();
	}
	
	
	/**
	 * where
	 * 1. equity between RUID column of owner record data table and RUID columns of cf target value table in {@link #CFTargetValueTableRunListOfCFWithLayoutRelatedTargetAssigned}
	 */
	@Override
	protected String buildWhereSqlString() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("WHERE ");
		
		boolean nothingAddedYet = true;
		for(CFTargetValueTableRun run: this.CFTargetValueTableRunListOfCFWithLayoutRelatedTargetAssigned){
			
			if(nothingAddedYet) {
				nothingAddedYet = false;
			}else {
				sb.append(", ");
			}
			
			//RUID column equity condition;
			sb.append(
					SQLStringUtils.buildEquityConditionString(
							SQLStringUtils.buildTableColumnFullPathString(this.getOwnerRecordDataTableSchema().getID(), VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE),
							SQLStringUtils.buildTableColumnFullPathString(run.getTableSchemaID(), VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE)
					));
		}
		
		return sb.toString();
	}
	

	/**
	 * build and return the calculated layout coordinate of the next record of the owner record data table;
	 * 
	 * if there is no record left, return null;
	 * 
	 * note that the first column(index = 1) in the built SELECT clause is RUID column of owner record data table;
	 * second and third columns are x and y; see {@link #buildSelectSqlString()} for details;
	 * @throws SQLException 
	 */
	@Override
	public Point2D nextRecord() throws SQLException{
		double x;
		double y;
		if(this.resultSet.next()) {
			if(this.xLayoutCalculatedInTargetValueTable)
				x = this.resultSet.getDouble(this.xLayoutTargetName.getStringValue().toUpperCase());
			else
				x = Double.parseDouble(this.getCoreShapeCFG().getShapeType().getXLayoutLeafTarget().getDefaultStringValue());
			
			if(this.yLayoutCalculatedInTargetValueTable)
				y = this.resultSet.getDouble(this.yLayoutTargetName.getStringValue().toUpperCase());
			else
				y = Double.parseDouble(this.getCoreShapeCFG().getShapeType().getYLayoutLeafTarget().getDefaultStringValue());
			
			return new Point2D(x,y);
			
		}else {
			return null;
		}
	}

	
}
