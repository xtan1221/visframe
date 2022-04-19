package generic.graph;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import metadata.graph.feature.EdgeDirectednessFeature;
import metadata.graph.feature.GraphEdgeFeature;
import metadata.graph.feature.GraphVertexFeature;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;

/**
 * provide utilities to iterate through all vertices and edges of a graph;
 * 
 * @author tanxu
 * 
 */
public interface GraphIterator {
	/**
	 * the full set of graph vertex attributes in the format of DataTableColumn;
	 * facilitate build the data table schema for node record data;
	 * @return
	 * @throws SQLException
	 */
	Map<DataTableColumnName, DataTableColumn> getVertexAttributeColNameMap() throws SQLException;
	
//	void addVertexAttribute(DataTableColumn col);
	
	/**
	 * the full set of graph vertex attributes in the format of DataTableColumn;
	 * facilitate build the data table schema for edge record data;
	 * @return
	 * @throws SQLException 
	 */
	Map<DataTableColumnName, DataTableColumn> getEdgeAttributeColNameMap() throws SQLException;
	
	
//	void addEdgeAttribute(DataTableColumn col);
	
	///////////////////
	LinkedHashSet<DataTableColumnName> getVertexIDColumnNameSet();
	LinkedHashSet<DataTableColumnName> getVertexAdditionalFeatureColumnNameSet();
	
	LinkedHashSet<DataTableColumnName> getEdgeIDColumnNameSet();
	LinkedHashSet<DataTableColumnName> getEdgeAdditionalFeatureColumnNameSet();
	
	boolean isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets();
	LinkedHashMap<DataTableColumnName, DataTableColumnName> getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap();
	LinkedHashMap<DataTableColumnName, DataTableColumnName> getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap();
	EdgeDirectednessFeature getEdgeDirectednessFeature();
	
	/**
	 * make and return the {@link GraphVertexFeature} if the graph of this {@link GraphIterator} is to be used to build a {@link GraphDataMetadata}
	 * the column names inside should be consistent with those returned by {@link #getVertexAttributeColNameMap()}
	 * @return
	 */
	default GraphVertexFeature makeGraphVertexFeature() {
		return new GraphVertexFeature(this.getVertexIDColumnNameSet(), this.getVertexAdditionalFeatureColumnNameSet());
	}
	
	/**
	 * make and return the {@link GraphEdgeFeature} if the graph of this {@link GraphIterator} is to be used to build a {@link GraphDataMetadata};
	 * the column names inside should be consistent with those returned by {@link #getEdgeAttributeColNameMap()}
	 * @return
	 */
	default GraphEdgeFeature makeGraphEdgeFeature() {
		return new GraphEdgeFeature(
				this.getEdgeIDColumnNameSet(), this.getEdgeAdditionalFeatureColumnNameSet(), 
				this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets(),
				this.getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap(), 
				this.getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap(),
				this.getEdgeDirectednessFeature()
				);
	}
	
	/**
	 * whether there is none of {@link VfGraphVertex} left;
	 * @return
	 */
	boolean vertexAreDone();
	
	/**
	 * whether there is none of {@link VfGraphEdge} left;
	 * @return
	 */
	boolean edgeAreDone();
	
	/**
	 * return the next {@link VfGraphVertex};
	 * return null if none left;
	 * @return
	 * @throws IOException 
	 * @throws SQLException 
	 */
	VfGraphVertex nextVertex() throws IOException, SQLException;
	
	/**
	 * return the next {@link VfGraphEdge};
	 * return null if none left;
	 * throw UnsupportedOperationException if {@link #vertexAreDone()} returns false;
	 * 
	 * @return
	 * @throws IOException 
	 * @throws SQLException 
	 */
	VfGraphEdge nextEdge() throws IOException, SQLException;
	
	
	/**
	 * restart this {@link GraphIterator} to the initial state so that the iteration of the graph can start over;
	 * 
	 * specifically, set the pointer right before the first vertex is returned by {@link #nextVertex()} method!
	 * @throws IOException 
	 * @throws SQLException 
	 */
	void restart() throws IOException, SQLException;
	
}
