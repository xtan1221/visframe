package generic.graph.populator;

import java.sql.SQLException;

import context.project.VisProjectDBContext;
import generic.graph.builder.GraphBuilder;
import rdb.table.data.DataTableName;
import rdb.table.data.DataTableSchema;

/**
 * interface that create, insert and populate vertex and edge data table table of the graph data; 
 * @author tanxu
 *
 */
public interface GraphDataTablePopulator {
	/**
	 * host {@link VisProjectDBContext}
	 * @return
	 */
	VisProjectDBContext getHostVisProjectDBContext();
	
	/**
	 * built graph with {@link GraphTypeEnforcer} to be populated to the data tables;
	 * @return
	 */
	GraphBuilder getInputGraphBuilder();
	
	/**
	 * vertex data table name;
	 * @return
	 */
	DataTableName getVertexDataTableName();
	
	/**
	 * edge data table name
	 * @return
	 */
	DataTableName getEdgeDataTableName();
	
	/**
	 * 
	 * @return
	 */
	DataTableSchema getVertexDataTableSchema();
	
	/**
	 * 
	 * @return
	 */
	DataTableSchema getEdgeDataTableSchema();
	
	
	
	/**
	 * populate
	 * 
	 * 1. create and insert the data table schema for node and edge data
	 * 2. populate the node data table;
	 * 3. populate the edge data table;
	 * @throws SQLException 
	 */
	void perform() throws SQLException;
}
