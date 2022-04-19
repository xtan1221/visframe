package generic.graph.reader.filebased;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import generic.graph.reader.GraphReader;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;

/**
 * read a graph from a data file of a graph file format;
 * 
 * node and edge related attributes could be retrieved from the data file (for example, GEXF) or pre-defined;
 * 
 * this class is applicable to graph file format with a single data file;
 * 
 * this class is applicable to two major types of graph file format in terms of the data structure:
 * 1. vertex data and edge data are separated and vertex are grouped together before all edges; for example GEXF
 * 		for this type, implementation of file parsing should be straight-forward by first parsing all the vertex data then the edge data;
 * 2. vertex data and edge data are mixed and each edge and its two vertex are together;
 * 		for this type, implementation of file parsing will take two round of file reading:
 * 		1. the first round will identify all the vertex;
 * 		2. the second round will identify all the edges;
 * 		note that this implementation is so to be consistent with the {@link GraphIterator} interface
 * 
 * 
 * set of information to be identified by GraphFileReader except for the node and edge entities:
 * 1. full set of node record DataTableColumns
 * 		{@link #vertexAttributeColNameMap}
 * 2. full set of edge record DataTableColumns
 * 		{@link #edgeAttributeColNameMap}
 * 3. node id column set and additional column set
 * 		{@link #vertexIDColumnNameSet}
 * 		{@link #vertexAdditionalFeatureColumnNameSet}
 * 4. edge id column set and additional column set
 * 		{@link #edgeIDColumnNameSet}
 * 		{@link #edgeAdditionalFeatureColumnNameSet}
 * 5. whether edge's source/sink node id column set is disjoint with (or included in) the edge id column set;
 * 		{@link #edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets}
 * 6. map from node id column names to the edge's source and sink node id column names
 * 		{@link #vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap}
 * 		{@link #vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap}
 * 
 * @author tanxu
 * 
 */
public abstract class GraphFileReader extends GraphReader {
	private final Path dataFilePath;
	
	protected Map<DataTableColumnName, DataTableColumn> vertexAttributeColNameMap = new LinkedHashMap<>();
	protected Map<DataTableColumnName, DataTableColumn> edgeAttributeColNameMap = new LinkedHashMap<>();
	
	////////////information that must be extracted when the GraphFileReader is done parsing the data file;
	protected LinkedHashSet<DataTableColumnName> vertexIDColumnNameSet = new LinkedHashSet<>();
	protected LinkedHashSet<DataTableColumnName> vertexAdditionalFeatureColumnNameSet = new LinkedHashSet<>();
	
	protected LinkedHashSet<DataTableColumnName> edgeIDColumnNameSet = new LinkedHashSet<>();
	protected LinkedHashSet<DataTableColumnName> edgeAdditionalFeatureColumnNameSet = new LinkedHashSet<>();
	
	protected boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;
	protected LinkedHashMap<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap = new LinkedHashMap<>();
	protected LinkedHashMap<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap = new LinkedHashMap<>();
	
	
	/**
	 * constructor
	 * @param dataFilePath
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	protected GraphFileReader(Path dataFilePath){
		
		this.dataFilePath = dataFilePath;
	}
	
	/**
	 * 
	 * @return
	 */
	protected Path getDataFilePath() {
		return this.dataFilePath;
	}
	
	@Override
	public abstract void initialize() throws FileNotFoundException, IOException;
	
	/**
	 * this class is applicable to two major types of graph file format in terms of the data structure:
	 * 1. vertex data and edge data are separated and vertex are grouped together before all edges; for example GEXF
	 * 		for this type, implementation of file parsing should be straight-forward by first parsing all the vertex data then the edge data;
	 * 2. vertex data and edge data are mixed and each edge and its two vertex are together;
	 * 		for this type, implementation of file parsing will take two round of file reading:
	 * 		1. the first round will identify all the vertex;
	 * 		2. the second round will identify all the edges;
	 * 		note that this implementation is so to be consistent with the {@link GraphIterator} interface
	 * @throws IOException 
	 */
	@Override
	public abstract VfGraphVertex nextVertex() throws IOException;

	/**
	 * this class is applicable to two major types of graph file format in terms of the data structure:
	 * 1. vertex data and edge data are separated and vertex are grouped together before all edges; for example GEXF
	 * 		for this type, implementation of file parsing should be straight-forward by first parsing all the vertex data then the edge data;
	 * 2. vertex data and edge data are mixed and each edge and its two vertex are together;
	 * 		for this type, implementation of file parsing will take two round of file reading:
	 * 		1. the first round will identify all the vertex;
	 * 		2. the second round will identify all the edges;
	 * 		note that this implementation is so to be consistent with the {@link GraphIterator} interface
	 * @throws IOException 
	 */
	@Override
	public abstract VfGraphEdge nextEdge() throws IOException;
	
	///////////////////

	@Override
	public Map<DataTableColumnName, DataTableColumn> getVertexAttributeColNameMap() {
		return this.vertexAttributeColNameMap;
	}
	
	
	@Override
	public Map<DataTableColumnName, DataTableColumn> getEdgeAttributeColNameMap() {
		return this.edgeAttributeColNameMap;
	}
	


	/////////////////////////////////////////////////////
	
//	/**
//	 * build and return the {@link GraphVertexFeature} for the imported GraphDataMetadata
//	 * @return
//	 */
//	public GraphVertexFeature getGraphVertexFeature() {
//		return new GraphVertexFeature(this.getVertexIDColumnNameSet(), this.getVertexAdditionalFeatureColumnNameSet());
//	}
	
//	/**
//	 * build and return the {@link GraphEdgeFeature} for the imported GraphDataMetadata
//	 * @return
//	 */
//	public GraphEdgeFeature getGraphEdgeFeature() {
//		return new GraphEdgeFeature(
//				this.getEdgeIDColumnNameSet(), 
//				this.getEdgeAdditionalFeatureColumnNameSet(), 
//				this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets(),
//				this.getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap(),
//				this.getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap()
//				);
//	}
	
	
	////////////////////////
	@Override
	public LinkedHashSet<DataTableColumnName> getVertexIDColumnNameSet() {
		return this.vertexIDColumnNameSet;
	}

	@Override
	public LinkedHashSet<DataTableColumnName> getVertexAdditionalFeatureColumnNameSet() {
		return this.vertexAdditionalFeatureColumnNameSet;
	}
	
	@Override
	public LinkedHashSet<DataTableColumnName> getEdgeIDColumnNameSet() {
		return this.edgeIDColumnNameSet;
	}

	@Override
	public LinkedHashSet<DataTableColumnName> getEdgeAdditionalFeatureColumnNameSet() {
		return this.edgeAdditionalFeatureColumnNameSet;
	}

	@Override
	public boolean isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets() {
		return this.edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;
	}

	@Override
	public LinkedHashMap<DataTableColumnName, DataTableColumnName> getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap() {
		return this.vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap;
	}

	@Override
	public LinkedHashMap<DataTableColumnName, DataTableColumnName> getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap() {
		return this.vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap;
	}
	
}
