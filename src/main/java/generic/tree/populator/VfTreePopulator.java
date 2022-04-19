package generic.tree.populator;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import context.project.VisProjectDBContext;
import generic.tree.VfTree;
import generic.tree.calculation.VfCalculatorTree;
import generic.tree.calculation.VfCalculatorTreeNode;
import metadata.graph.vftree.VfTreeMandatoryEdgeDataTableSchemaUtils;
import metadata.graph.vftree.VfTreeMandatoryNodeDataTableSchemaUtils;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import rdb.table.data.DataTableName;
import rdb.table.data.DataTableSchema;
import rdb.table.data.DataTableSchemaFactory;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;

/**
 * class for populating the node/edge data table of the corresponding VfTreeDataMetadata in rdb of a VisProjectDBContext;
 * 
 * this class will create a VfCalculatorTree with the input VfTree object which will calculate all the needed features in the visframe defined VfTree node/edge data table
 * that involve recursion and are not available in the VfTree interface:
 * 1. leaf index;
 * 2. distance to root;
 * 3. edge num to root;
 * 
 * @author tanxu
 *
 */
public final class VfTreePopulator{
	private final VfTree inputTree;
	private final VisProjectDBContext hostVisProjectDBContext;
	private final DataTableName nodeDataTableName;
	private final DataTableName edgeDataTableName;
	
	//////////////
	private DataTableSchema nodeDataTableSchema;
	private DataTableSchema edgeDataTableSchema;
	
	/**
	 * VfCalculatorTree based on the input VfTree with all information calculated so that the data table populating can start;
	 */
	private VfCalculatorTree calculatorTree;
	
	/**
	 * constructor
	 * @param inputTree
	 */
	public VfTreePopulator(
			VfTree inputTree, 
			VisProjectDBContext hostVisProjectDBContext,
			DataTableName nodeDataTableName,
			DataTableName edgeDataTableName){
		
		this.inputTree = inputTree;
		this.hostVisProjectDBContext = hostVisProjectDBContext;
		this.nodeDataTableName = nodeDataTableName;
		this.edgeDataTableName = edgeDataTableName;
	}
	
	////////////////////////////////
	/**
	 * populate
	 * 
	 * 1. create and insert the data table schema for node and edge data
	 * 2. populate the node data table;
	 * 3. populate the edge data table;
	 * 
	 * @throws SQLException 
	 */
	public void perform() throws SQLException{
		this.calculatorTree = new VfCalculatorTree(this.inputTree);
		
		////////
		this.createDataTableSchema();
		
		this.insertDataTableSchema();
		
		this.populateVertexDataTable();
		
		this.populateEdgeDataTable();
	}
	
	
	private void createDataTableSchema() {
		List<DataTableColumn> orderedListOfVertexDataTableColumn = new ArrayList<>();
		
		orderedListOfVertexDataTableColumn.add(DataTableSchemaFactory.makeRUIDColumn());
		
		orderedListOfVertexDataTableColumn.addAll(VfTreeMandatoryNodeDataTableSchemaUtils.getMandatoryAttributeColumnList());
		
		for(DataTableColumnName colName:this.calculatorTree.getNonMandatoryAdditionalNodeFeatureColumnNameMap().keySet()) {
			orderedListOfVertexDataTableColumn.add(this.calculatorTree.getNonMandatoryAdditionalNodeFeatureColumnNameMap().get(colName));
		}
		
		this.nodeDataTableSchema = new DataTableSchema(this.getNodeDataTableName(), orderedListOfVertexDataTableColumn);
		///////////
		List<DataTableColumn> orderedListOfEdgeDataTableColumn = new ArrayList<>();
		
		orderedListOfEdgeDataTableColumn.add(DataTableSchemaFactory.makeRUIDColumn());
		
		orderedListOfEdgeDataTableColumn.addAll(VfTreeMandatoryEdgeDataTableSchemaUtils.getMandatoryAttributeColumnList());
		
		for(DataTableColumnName colName:this.calculatorTree.getNonMandatoryAdditionalEdgeFeatureColumnNameMap().keySet()) {
			orderedListOfEdgeDataTableColumn.add(this.calculatorTree.getNonMandatoryAdditionalEdgeFeatureColumnNameMap().get(colName));
		}
		
		this.edgeDataTableSchema = new DataTableSchema(this.getEdgeDataTableName(), orderedListOfEdgeDataTableColumn);
		
	}
	
	private void insertDataTableSchema() throws SQLException {
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getDataTableSchemaManager().insert(this.nodeDataTableSchema);
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getDataTableSchemaManager().insert(this.edgeDataTableSchema);
	}
	
	
	private void populateVertexDataTable() throws SQLException {
		List<String> currentNonRUIDColumnUpperCaseNameListInDataTableSchema = new ArrayList<>(); //not including the RUID column since it is auto increment
		for(DataTableColumn col:this.getNodeDataTableSchema().getOrderedListOfNonRUIDColumn()) {
			currentNonRUIDColumnUpperCaseNameListInDataTableSchema.add(col.getName().getStringValue().toUpperCase());
		}
		
		PreparedStatement ps = this.getHostVisProjectDBContext().getDBConnection().prepareStatement(
				TableContentSQLStringFactory.buildInsertIntoPreparedStatementSqlString(
						SQLStringUtils.buildTableFullPathString(this.getNodeDataTableSchema().getSchemaName(), this.getNodeDataTableSchema().getName()), 
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema
						)
				);
		
		for(Integer id:this.calculatorTree.getNodeIDMap().keySet()) {
			VfCalculatorTreeNode node = (VfCalculatorTreeNode)this.calculatorTree.getNodeIDMap().get(id);
			
			//////////////////////////set the value for node id column
			ps.setInt(
					currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(VfTreeMandatoryNodeDataTableSchemaUtils.nodeIDColumn().getName().getStringValue().toUpperCase())+1, 
					node.getID());
			
			////////////////////////////set the values for node mandatory additional columns
			//nodeParentIDColumn
			if(node.getParentNodeID()!=null) {
				ps.setInt(
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(VfTreeMandatoryNodeDataTableSchemaUtils.nodeParentIDColumn().getName().getStringValue().toUpperCase())+1, 
						node.getParentNodeID());
			}else {
				ps.setObject(
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(VfTreeMandatoryNodeDataTableSchemaUtils.nodeParentIDColumn().getName().getStringValue().toUpperCase())+1, 
						null);
			}
			//nodeSiblingIndexColumn
			ps.setInt(
					currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(VfTreeMandatoryNodeDataTableSchemaUtils.nodeSiblingIndexColumn().getName().getStringValue().toUpperCase())+1, 
					node.getSiblingOrderIndex());
			//nodeIsLeafColumn,
			ps.setBoolean(
					currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(VfTreeMandatoryNodeDataTableSchemaUtils.nodeIsLeafColumn().getName().getStringValue().toUpperCase())+1, 
					node.isLeaf());
			//nodeDistanceToRootColumn
			if(node.getDistanceToRootNode()!=null) {
				ps.setDouble(
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(VfTreeMandatoryNodeDataTableSchemaUtils.nodeDistanceToRootColumn().getName().getStringValue().toUpperCase())+1, 
						node.getDistanceToRootNode());
			}else {
				ps.setObject(
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(VfTreeMandatoryNodeDataTableSchemaUtils.nodeDistanceToRootColumn().getName().getStringValue().toUpperCase())+1, 
						null);
			}
			//edgeNumToRootColumn
			ps.setInt(
					currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(VfTreeMandatoryNodeDataTableSchemaUtils.edgeNumToRootColumn().getName().getStringValue().toUpperCase())+1, 
					node.getEdgeNumToRoot());
			//leafIndexColumn
			ps.setDouble(
					currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(VfTreeMandatoryNodeDataTableSchemaUtils.leafIndexColumn().getName().getStringValue().toUpperCase())+1, 
					node.getLeafIndex());
			
			
			///////////////////////////set the values for node non-mandatory additional columns
			for(DataTableColumnName colName:this.calculatorTree.getNonMandatoryAdditionalNodeFeatureColumnNameMap().keySet()) {
				if(node.getNonMandatoryAdditionalNodeFeatureColumnNameValueStringMap().get(colName)!=null) {
					this.getNodeDataTableSchema().getColumn(colName).getSqlDataType().setPreparedStatement(
							ps, 
							currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(colName.getStringValue().toUpperCase())+1, 
							node.getNonMandatoryAdditionalNodeFeatureColumnNameValueStringMap().get(colName)
							);
				}else {
					ps.setObject(
							currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(colName.getStringValue().toUpperCase())+1, 
							null);
				}
			}
			
			ps.addBatch();
		}
		
		ps.executeBatch();
		ps.clearBatch();
		ps.close();
	}
	
	
	private void populateEdgeDataTable() throws SQLException {
		List<String> currentNonRUIDColumnUpperCaseNameListInDataTableSchema = new ArrayList<>(); //not including the RUID column since it is auto increment
		for(DataTableColumn col:this.getEdgeDataTableSchema().getOrderedListOfNonRUIDColumn()) {
			currentNonRUIDColumnUpperCaseNameListInDataTableSchema.add(col.getName().getStringValue().toUpperCase());
		}
		
		PreparedStatement ps = this.getHostVisProjectDBContext().getDBConnection().prepareStatement(
				TableContentSQLStringFactory.buildInsertIntoPreparedStatementSqlString(
						SQLStringUtils.buildTableFullPathString(this.getEdgeDataTableSchema().getSchemaName(), this.getEdgeDataTableSchema().getName()), 
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema
						)
				);
		
		for(Integer id:this.calculatorTree.getNodeIDMap().keySet()) {
			VfCalculatorTreeNode node = (VfCalculatorTreeNode)this.calculatorTree.getNodeIDMap().get(id);
			
//			System.out.println(node.getID()+"    "+node.getParentNodeID());
			
			if(node.getParentNodeID()==null) {//skip root node
				continue;
			}
			
			//////////////set the value for edge id columns
			//parentNodeIDColumn
			ps.setInt(
					currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(VfTreeMandatoryEdgeDataTableSchemaUtils.parentNodeIDColumn().getName().getStringValue().toUpperCase())+1, 
					node.getParentNodeID());
			//childNodeIDColumn
			ps.setInt(
					currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(VfTreeMandatoryEdgeDataTableSchemaUtils.childNodeIDColumn().getName().getStringValue().toUpperCase())+1, 
					node.getID());
			
			
			////////////////set the values for edge mandatory additional columns
			//lengthColumn
			if(node.getDistanceToParentNode()!=null) {
				ps.setDouble(
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(VfTreeMandatoryEdgeDataTableSchemaUtils.lengthColumn().getName().getStringValue().toUpperCase())+1, 
						node.getDistanceToParentNode());
			}else {
				ps.setObject(
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(VfTreeMandatoryEdgeDataTableSchemaUtils.lengthColumn().getName().getStringValue().toUpperCase())+1, 
						null);
			}
			//bootstrapColumn
			if(node.getBootstrapValueToParentNode()!=null) {
				ps.setDouble(
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(VfTreeMandatoryEdgeDataTableSchemaUtils.bootstrapColumn().getName().getStringValue().toUpperCase())+1, 
						node.getBootstrapValueToParentNode());
			}else {
				ps.setObject(
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(VfTreeMandatoryEdgeDataTableSchemaUtils.bootstrapColumn().getName().getStringValue().toUpperCase())+1, 
						null);
			}
			
			
			////////////////////////////////set the values for node non-mandatory additional columns
			for(DataTableColumnName colName:this.calculatorTree.getNonMandatoryAdditionalEdgeFeatureColumnNameMap().keySet()) {
				if(node.getNonMandatoryAdditionalFeatureColumnNameValueStringMapOfEdgeToParent().get(colName)!=null) {
					this.getEdgeDataTableSchema().getColumn(colName).getSqlDataType().setPreparedStatement(
							ps, 
							currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(colName.getStringValue().toUpperCase())+1, 
							node.getNonMandatoryAdditionalFeatureColumnNameValueStringMapOfEdgeToParent().get(colName)
							);
				}else {
					ps.setObject(
							currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(colName.getStringValue().toUpperCase())+1, 
							null);
				}
			}
			
			ps.addBatch();
		}
		
		ps.executeBatch();
		ps.clearBatch();
		ps.close();
	}
	
	
	
	public VisProjectDBContext getHostVisProjectDBContext() {
		return this.hostVisProjectDBContext;
	}
	
	/**
	 * vertex data table name;
	 * @return
	 */
	DataTableName getNodeDataTableName() {
		return this.nodeDataTableName;
	}
	
	/**
	 * edge data table name
	 * @return
	 */
	DataTableName getEdgeDataTableName() {
		return this.edgeDataTableName;
	}
	
	/**
	 * 
	 * @return
	 */
	public DataTableSchema getNodeDataTableSchema() {
		return this.nodeDataTableSchema;
	}
	
	/**
	 * 
	 * @return
	 */
	public DataTableSchema getEdgeDataTableSchema() {
		return this.edgeDataTableSchema;
	}


}
