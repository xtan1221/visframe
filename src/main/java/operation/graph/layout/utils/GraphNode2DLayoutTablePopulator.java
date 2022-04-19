package operation.graph.layout.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import context.project.VisProjectDBContext;
import generic.graph.VfGraphVertex;
import metadata.MetadataID;
import operation.graph.layout.utils.GraphLayoutAlgoPerformerBase.Coord2D;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import rdb.table.data.DataTableName;
import rdb.table.data.DataTableSchema;
import rdb.table.data.DataTableSchemaFactory;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;
import utils.Pair;

/**
 * class to create and insert data table schema and populate it with calculated coordinates for each nodes in the graph
 * 
 * applicable for any GraphLayoutAlgoPerformerBase with specific underlying graph api and algorithm;
 * 
 * @author tanxu
 *
 */
public final class GraphNode2DLayoutTablePopulator {
	private final static int BATCH_MAX_SIZE = 1000;
	
	
	///////////////////////
	private final VisProjectDBContext hostVisProjectDBContext;
	private final DataTableSchema targetGraphVertexRecordDataTableSchema;
	private final MetadataID outputRecordDataMetadataID;
	private final GraphLayoutAlgoPerformerBase<VfGraphVertex> algoPerformer;
	
	
	private DataTableSchema outputDataTableSchema;
	
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param graphNode2DLayoutIterator
	 * @param dataTableName
	 */
	public GraphNode2DLayoutTablePopulator(
			VisProjectDBContext hostVisProjectDBContext, 
			DataTableSchema targetGraphVertexRecordDataTableSchema,
			MetadataID outputRecordDataMetadataID,
			GraphLayoutAlgoPerformerBase<VfGraphVertex> algoPerformer){
		this.hostVisProjectDBContext = hostVisProjectDBContext;
		this.targetGraphVertexRecordDataTableSchema = targetGraphVertexRecordDataTableSchema;
		this.outputRecordDataMetadataID = outputRecordDataMetadataID;
		
		this.algoPerformer = algoPerformer;
		
	}
	

	/**
	 * @return the outputDataTableSchema
	 */
	public DataTableSchema getOutputDataTableSchema() {
		return outputDataTableSchema;
	}


	/**
	 * 1. create and insert output DataTableSchema
	 * 		
	 * 2. populate the table
	 * @throws SQLException 
	 */
	public void perform() throws SQLException {
		this.buildAndInsertDataTableSchema();
		
		this.populateLayoutDataTable();
	}
	
	
	private void buildAndInsertDataTableSchema() throws SQLException {
		DataTableName tableName = this.hostVisProjectDBContext.getHasIDTypeManagerController().getDataTableSchemaManager().findNextAvailableName(
				new DataTableName(this.outputRecordDataMetadataID.getName().getStringValue()));
		
		
		List<DataTableColumn> orderedListOfVertexDataTableColumn = new ArrayList<>();
		//RUID
		orderedListOfVertexDataTableColumn.add(DataTableSchemaFactory.makeRUIDColumn());
		//id columns of vertex
		for(DataTableColumn col:this.targetGraphVertexRecordDataTableSchema.getOrderedListOfNonRUIDColumn()) {
			if(col.isInPrimaryKey()) {
				orderedListOfVertexDataTableColumn.add(col);
			}
		}
		//coordinate columns
		orderedListOfVertexDataTableColumn.addAll(GraphNode2DLayoutCoordinateColumnUtils.get2DCoordColumnList(this.algoPerformer.isCartesianCoordSystem()));
		
		
		this.outputDataTableSchema = new DataTableSchema(tableName, orderedListOfVertexDataTableColumn);
		
		
		this.hostVisProjectDBContext.getHasIDTypeManagerController().getDataTableSchemaManager().insert(this.outputDataTableSchema);
	}
	
	
	private void populateLayoutDataTable() throws SQLException {
		//prepare
		List<String> currentNonRUIDColumnUpperCaseNameListInDataTableSchema = new ArrayList<>(); //not including the RUID column since it is auto increment
		for(DataTableColumn col:this.outputDataTableSchema.getOrderedListOfNonRUIDColumn()) {
			currentNonRUIDColumnUpperCaseNameListInDataTableSchema.add(col.getName().getStringValue().toUpperCase());
		}
		
		PreparedStatement ps = this.getHostVisProjectDBContext().getDBConnection().prepareStatement(
				TableContentSQLStringFactory.buildInsertIntoPreparedStatementSqlString(
						SQLStringUtils.buildTableFullPathString(this.outputDataTableSchema.getSchemaName(), this.outputDataTableSchema.getName()), 
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema
						)
				);
		
		
		//////////////////////
		//populate
		Set<String> addedColUpperCaseNameSet;
		int currentBatchSize = 0;
		
		Pair<VfGraphVertex, Coord2D> nextVertexCoord;
		
		while((nextVertexCoord=this.algoPerformer.nextVertexCoord())!=null) {
			
			addedColUpperCaseNameSet = new HashSet<>();
			
			//set value for vertex id attributes
			for(DataTableColumnName colName:nextVertexCoord.getFirst().getIDAttributeNameStringValueMap().keySet()) {
				addedColUpperCaseNameSet.add(colName.getStringValue().toUpperCase());
				this.outputDataTableSchema.getColumn(colName).getSqlDataType().setPreparedStatement(
						ps, 
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(colName.getStringValue().toUpperCase())+1, 
						nextVertexCoord.getFirst().getIDAttributeNameStringValueMap().get(colName)
						);
			}
			
			//set value for calculated 2d coordinate
			this.getFirstCoordColumn().getSqlDataType().setPreparedStatement(
					ps, 
					currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(this.getFirstCoordColumn().getName().getStringValue().toUpperCase())+1, 
					Double.isNaN(nextVertexCoord.getSecond().getCoord1())?null:Double.toString(nextVertexCoord.getSecond().getCoord1())
					);
			this.getSecondCoordColumn().getSqlDataType().setPreparedStatement(
					ps, 
					currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(this.getSecondCoordColumn().getName().getStringValue().toUpperCase())+1, 
					Double.isNaN(nextVertexCoord.getSecond().getCoord2())?null:Double.toString(nextVertexCoord.getSecond().getCoord2())
					);
			
			currentBatchSize++;
			ps.addBatch();
			
			if(currentBatchSize>BATCH_MAX_SIZE) {
				ps.executeBatch();
				ps.clearBatch();
				currentBatchSize = 0;
			}
		}
		
		
		//last batch
		ps.executeBatch();
		ps.clearBatch();
		ps.close();
	}
	
	
	/**
	 * x or radius
	 */
	private DataTableColumn firstCoordColumn;
	private DataTableColumn getFirstCoordColumn() {
		if(this.firstCoordColumn==null) {
			this.firstCoordColumn = GraphNode2DLayoutCoordinateColumnUtils.get2DCoordColumnList(this.algoPerformer.isCartesianCoordSystem()).get(0);
		}
		return this.firstCoordColumn;
	}
	/**
	 * y or theta
	 */
	private DataTableColumn secondCoordColumn;
	private DataTableColumn getSecondCoordColumn() {
		if(this.secondCoordColumn==null) {
			this.secondCoordColumn = GraphNode2DLayoutCoordinateColumnUtils.get2DCoordColumnList(this.algoPerformer.isCartesianCoordSystem()).get(1);
		}
		return this.secondCoordColumn;
	}
	
	
	private VisProjectDBContext getHostVisProjectDBContext() {
		// TODO Auto-generated method stub
		return this.hostVisProjectDBContext;
	}
}
