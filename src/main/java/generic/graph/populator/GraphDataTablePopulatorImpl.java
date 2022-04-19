package generic.graph.populator;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import context.project.VisProjectDBContext;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import generic.graph.builder.GraphBuilder;
import rdb.sqltype.SQLDataType;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import rdb.table.data.DataTableName;
import rdb.table.data.DataTableSchema;
import rdb.table.data.DataTableSchemaFactory;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;


public class GraphDataTablePopulatorImpl implements GraphDataTablePopulator {
	private final static int BATCH_MAX_SIZE = 1000; //TODO modify to a larger value
	
	////////////////
	private final VisProjectDBContext hostVisProjectDBContext;
	private final GraphBuilder inputGraphBuilder;
	private final DataTableName vertexDataTableName;
	private final DataTableName edgeDataTableName;
	
	//////////////////
	private DataTableSchema vertexRecordDataTableSchema;
	private DataTableSchema edgeRecordDataTableSchema;
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param inputGraphBuilder
	 * @param vertexDataTableName
	 * @param edgeDataTableName
	 */
	public GraphDataTablePopulatorImpl(
			VisProjectDBContext hostVisProjectDBContext, 
			GraphBuilder inputGraphBuilder,
			DataTableName vertexDataTableName, 
			DataTableName edgeDataTableName){
		this.hostVisProjectDBContext = hostVisProjectDBContext;
		this.inputGraphBuilder = inputGraphBuilder;
		this.vertexDataTableName = vertexDataTableName;
		this.edgeDataTableName = edgeDataTableName;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perform() throws SQLException {
		this.createDataTableSchema();
		
		this.insertDataTableSchema();
		
		this.populateVertexDataTable();
		
		this.populateEdgeDataTable();
	}
	
	private void createDataTableSchema() throws SQLException {
		List<DataTableColumn> orderedListOfVertexDataTableColumn = new ArrayList<>();
		
		orderedListOfVertexDataTableColumn.add(DataTableSchemaFactory.makeRUIDColumn());
		
		for(DataTableColumnName colName:this.getInputGraphBuilder().getVertexAttributeColNameMap().keySet()) {
			orderedListOfVertexDataTableColumn.add(this.getInputGraphBuilder().getVertexAttributeColNameMap().get(colName));
		}
		
		this.vertexRecordDataTableSchema = new DataTableSchema(this.getVertexDataTableName(), orderedListOfVertexDataTableColumn);
		
		///////////////////////
		List<DataTableColumn> orderedListOfEdgeDataTableColumn = new ArrayList<>();
		
		orderedListOfEdgeDataTableColumn.add(DataTableSchemaFactory.makeRUIDColumn());
		
		for(DataTableColumnName colName:this.getInputGraphBuilder().getEdgeAttributeColNameMap().keySet()) {
			orderedListOfEdgeDataTableColumn.add(this.getInputGraphBuilder().getEdgeAttributeColNameMap().get(colName));
		}
		
		this.edgeRecordDataTableSchema = new DataTableSchema(this.getEdgeDataTableName(), orderedListOfEdgeDataTableColumn);
		
	}
	
	private void insertDataTableSchema() throws SQLException {
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getDataTableSchemaManager().insert(this.vertexRecordDataTableSchema);
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getDataTableSchemaManager().insert(this.edgeRecordDataTableSchema);
	}
	
	private void populateVertexDataTable() throws SQLException {
		List<String> currentNonRUIDColumnUpperCaseNameListInDataTableSchema = new ArrayList<>(); //not including the RUID column since it is auto increment
		for(DataTableColumn col:this.vertexRecordDataTableSchema.getOrderedListOfNonRUIDColumn()) {
			currentNonRUIDColumnUpperCaseNameListInDataTableSchema.add(col.getName().getStringValue().toUpperCase());
		}
		
		PreparedStatement ps = this.getHostVisProjectDBContext().getDBConnection().prepareStatement(
				TableContentSQLStringFactory.buildInsertIntoPreparedStatementSqlString(
						SQLStringUtils.buildTableFullPathString(this.vertexRecordDataTableSchema.getSchemaName(), this.vertexRecordDataTableSchema.getName()), 
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema
						)
				);
		
		Set<String> addedColUpperCaseNameSet;
		
		VfGraphVertex vertex;
		int currentBatchSize = 0;
		//
		while((vertex=this.getInputGraphBuilder().nextVertex())!=null) {
			addedColUpperCaseNameSet = new HashSet<>();
			//set value for vertex id attributes
			for(DataTableColumnName colName:vertex.getIDAttributeNameStringValueMap().keySet()) {
				addedColUpperCaseNameSet.add(colName.getStringValue().toUpperCase());
				this.vertexRecordDataTableSchema.getColumn(colName).getSqlDataType().setPreparedStatement(
						ps, 
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(colName.getStringValue().toUpperCase())+1, 
						vertex.getIDAttributeNameStringValueMap().get(colName)
						);
			}
			//set value for vertex additional attributes
			for(DataTableColumnName colName:vertex.getAdditionalAttributeNameStringValueMap().keySet()) {
				addedColUpperCaseNameSet.add(colName.getStringValue().toUpperCase());
				this.vertexRecordDataTableSchema.getColumn(colName).getSqlDataType().setPreparedStatement(
						ps, 
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(colName.getStringValue().toUpperCase())+1, 
						vertex.getAdditionalAttributeNameStringValueMap().get(colName)
						);
			}
			
			//set null value for attributes not in the id and additional attributes of the current vertex;
			//and do not have default value
			for(int i=0;i<currentNonRUIDColumnUpperCaseNameListInDataTableSchema.size();i++) {
				String colNameString = currentNonRUIDColumnUpperCaseNameListInDataTableSchema.get(i);
				if(!addedColUpperCaseNameSet.contains(colNameString)) {
					SQLDataType dataType = this.vertexRecordDataTableSchema.getColumn(new DataTableColumnName(colNameString)).getSqlDataType();
					String defaultStringValue = this.vertexRecordDataTableSchema.getColumn(new DataTableColumnName(colNameString)).getDefaultStringValue();
					if(defaultStringValue==null) {
						ps.setObject(i+1, null);
					}else {
						ps.setObject(i+1, dataType.getDefaultValueObject(defaultStringValue));
					}
				}
			}
			
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
	}
	
	private void populateEdgeDataTable() throws SQLException {
		List<String> currentNonRUIDColumnUpperCaseNameListInDataTableSchema = new ArrayList<>(); //not including the RUID column since it is auto increment
		for(DataTableColumn col:this.edgeRecordDataTableSchema.getOrderedListOfNonRUIDColumn()) {
			currentNonRUIDColumnUpperCaseNameListInDataTableSchema.add(col.getName().getStringValue().toUpperCase());
		}
		
		PreparedStatement ps = this.getHostVisProjectDBContext().getDBConnection().prepareStatement(
				TableContentSQLStringFactory.buildInsertIntoPreparedStatementSqlString(
						SQLStringUtils.buildTableFullPathString(this.edgeRecordDataTableSchema.getSchemaName(), this.edgeRecordDataTableSchema.getName()), 
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema
						)
				);
		
		
		Set<String> addedColUpperCaseNameSet;
		
		VfGraphEdge edge;
		int currentBatchSize = 0;
		//
		while((edge=this.getInputGraphBuilder().nextEdge())!=null) {
			addedColUpperCaseNameSet = new HashSet<>();
			//set value for edge id attributes
			for(DataTableColumnName colName:edge.getIDAttributeNameStringValueMap().keySet()) {
				addedColUpperCaseNameSet.add(colName.getStringValue().toUpperCase());
				this.edgeRecordDataTableSchema.getColumn(colName).getSqlDataType().setPreparedStatement(
						ps, 
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(colName.getStringValue().toUpperCase())+1, 
						edge.getIDAttributeNameStringValueMap().get(colName)
						);
			}
			
			//source/sink node id attributes if they are disjoint from the edge id attributes
			if(this.getInputGraphBuilder().isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets()) {
				for(DataTableColumnName colName:edge.getSourceVertexIDAttributeNameStringValueMap().keySet()) {
					addedColUpperCaseNameSet.add(colName.getStringValue().toUpperCase());
					this.edgeRecordDataTableSchema.getColumn(colName).getSqlDataType().setPreparedStatement(
							ps, 
							currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(colName.getStringValue().toUpperCase())+1, 
							edge.getSourceVertexIDAttributeNameStringValueMap().get(colName)
							);
				}
				for(DataTableColumnName colName:edge.getSinkVertexIDAttributeNameStringValueMap().keySet()) {
					addedColUpperCaseNameSet.add(colName.getStringValue().toUpperCase());
					this.edgeRecordDataTableSchema.getColumn(colName).getSqlDataType().setPreparedStatement(
							ps, 
							currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(colName.getStringValue().toUpperCase())+1, 
							edge.getSinkVertexIDAttributeNameStringValueMap().get(colName)
							);
				}
				
			}
			
			
			//set value for edge additional attributes
			for(DataTableColumnName colName:edge.getAdditionalAttributeNameStringValueMap().keySet()) {
				addedColUpperCaseNameSet.add(colName.getStringValue().toUpperCase());
				this.edgeRecordDataTableSchema.getColumn(colName).getSqlDataType().setPreparedStatement(
						ps, 
						currentNonRUIDColumnUpperCaseNameListInDataTableSchema.indexOf(colName.getStringValue().toUpperCase())+1, 
						edge.getAdditionalAttributeNameStringValueMap().get(colName)
						);
			}
			
			//set null value for attributes not in the id and additional attributes of the current vertex and with null default value;
			for(int i=0;i<currentNonRUIDColumnUpperCaseNameListInDataTableSchema.size();i++) {
				String colNameString = currentNonRUIDColumnUpperCaseNameListInDataTableSchema.get(i);
				if(!addedColUpperCaseNameSet.contains(colNameString)) {
					SQLDataType dataType = this.edgeRecordDataTableSchema.getColumn(new DataTableColumnName(colNameString)).getSqlDataType();
					String defaultStringValue = this.edgeRecordDataTableSchema.getColumn(new DataTableColumnName(colNameString)).getDefaultStringValue();
					if(defaultStringValue==null) {
						ps.setObject(i+1, null);
					}else {
						ps.setObject(i+1, dataType.getDefaultValueObject(defaultStringValue));
					}
				}
			}
			
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
	}
	
	////////////////////////////////
	@Override
	public VisProjectDBContext getHostVisProjectDBContext() {
		return this.hostVisProjectDBContext;
	}
	
	@Override
	public GraphBuilder getInputGraphBuilder() {
		return this.inputGraphBuilder;
	}
	
	@Override
	public DataTableName getVertexDataTableName() {
		return this.vertexDataTableName;
	}

	@Override
	public DataTableName getEdgeDataTableName() {
		return this.edgeDataTableName;
	}

	@Override
	public DataTableSchema getVertexDataTableSchema() {
		return this.vertexRecordDataTableSchema;
	}

	@Override
	public DataTableSchema getEdgeDataTableSchema() {
		return this.edgeRecordDataTableSchema;
	}
	

}
