package generic.graph.builder;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import generic.graph.AbstractGraphIterator;
import generic.graph.GraphIterator;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import metadata.graph.feature.EdgeDirectednessFeature;
import metadata.graph.type.GraphMetadataType;
import metadata.graph.type.GraphTypeEnforcer;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;

/**
 * build a graph entity of a specific GRAPH engine from a {@link GraphIterator} with a specific {@link GraphTypeEnforcer};
 * 
 * 1. to perform graph type enforcement;
 * 2. to identify the observed graph type;
 * 3. to facilitate applying engine specific processing and algorithm;
 * 
 * @author tanxu
 * 
 */
public abstract class GraphBuilder extends AbstractGraphIterator {
	private final GraphTypeEnforcer graphTypeEnforcer;
	private final GraphIterator inputGraphIterator;
	
	/**
	 * whether or not to add vertex found in edge but not in the vertex to the built graph;
	 * if true, this allows implicitly defined vertex in edge from the {@link inputGraphIterator} , otherwise, those vertex will be ignored as well as the edges;
	 * <p></p>
	 * this feature makes it possible to build a graph based on a single record data; see {@link BuildGraphFromSingleExistingRecordOperation_pre} 
	 */
	private final boolean toAddDiscoveredVertexFromInputEdgeDataTable;
	
	
	///////////////////
	protected boolean built = false;
	
	
	protected boolean containingDirectedEdgeOnly;
	protected boolean containingUndirectedEdgeOnly;
	protected boolean containingSelfLoop;
	protected boolean containingParallelEdges;
	protected Boolean containingCycle;
	protected Boolean notConnected;
	
	protected GraphMetadataType observedType;
	
	/**
	 * constructor
	 * @param inputGraphIterator
	 * @param graphTypeEnforcer
	 * @param toAddDiscoveredVertexFromInputEdgeDataTable
	 */
	protected GraphBuilder(
			GraphIterator inputGraphIterator, 
			GraphTypeEnforcer graphTypeEnforcer,
			boolean toAddDiscoveredVertexFromInputEdgeDataTable){
		this.inputGraphIterator = inputGraphIterator;
		this.graphTypeEnforcer = graphTypeEnforcer;
		this.toAddDiscoveredVertexFromInputEdgeDataTable = toAddDiscoveredVertexFromInputEdgeDataTable;
	}
	
	/**
	 * GraphTypeEnforcer that guides the building process
	 * @return
	 */
	public GraphTypeEnforcer getGraphTypeEnforcer() {
		return this.graphTypeEnforcer;
	}
	
	
	/**
	 * source of data for graph node and edges
	 * @return
	 */
	public GraphIterator getInputGraphIterator() {
		return this.inputGraphIterator;
	}
	
	public boolean isToAddDiscoveredVertexFromInputEdgeDataTable() {
		return toAddDiscoveredVertexFromInputEdgeDataTable;
	}
	
	/**
	 * @return the built
	 */
	public boolean isBuilt() {
		return built;
	}
	
	///////////////////////////////
	/**
	 * build the graph based on the data from the {@link #inputGraphIterator} and perform the GraphTypeEnforcer;
	 * ====
	 * the node and edge attributes of this builder should be the same with the {@link #inputGraphIterator};
	 * 
	 * 1. initialize the graph entity of the specific graph engine;
	 * 2. retrieve the graph node and edges from the input {@link GraphIterator} and integrate them into the graph entity of the specific graph engine;
	 * 3. GraphTypeEnforcer should be applied either on the fly or after all node and edges are retrieved and inserted, depending on the GraphTypeEnforcer type and the graph engine;
	 * ====
	 * there are two major types of implementation regarding how to perform the GraphTypeEnforcer;
	 * 		1. on the fly
	 * 		2. build the graph entity as it is from the original {@link #inputGraphIterator}, then perform the GraphTypeEnforcer;
	 * which implementation to be used is depending on the specific graph engine; 
	 * ====
	 * after the graph is successfully built, set the {@link #built} to true;
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public abstract void perform() throws IOException, SQLException;
	
	/**
	 * try to identify and return the {@link GraphMetadataType} of the built graph;
	 * 
	 * @return
	 * @throws UnsupportedOperation if the underlying graph engine is incapable of doing so
	 */
	public abstract GraphMetadataType getOberservedType();
	
	
	/**
	 * retrieve and return the next {@link VfGraphVertex} from the built graph by this GraphBuilder which is based on the {@link #inputGraphIterator} and the {@link #graphTypeEnforcer};
	 * the VfGraphVertex should have the same set of attributes with the vertex in the given {@link #inputGraphIterator};
	 * return null if none vertex is left and set the {@link #vertexAreDone()} to true;
	 * 
	 * @throws UnsupportedOperationException if {@link #isBuilt()} returns false;
	 */
	@Override
	public abstract VfGraphVertex nextVertex();

	
	/**
	 * retrieve and return the next {@link VfGraphEdge} from the built graph by this GraphBuilder which is based on the {@link #inputGraphIterator} and the {@link #graphTypeEnforcer};
	 * the VfGraphEdge should have the same set of attributes with the edge in the given {@link #inputGraphIterator};
	 * return null if none edge is left and set the {@link #edgeAreDone()} to true;
	 * 
	 * @throws UnsupportedOperationException if {@link #vertexAreDone()} or {@link #isBuilt()} returns false;
	 */
	@Override
	public abstract VfGraphEdge nextEdge();

	
	
	////////////////////////
	@Override
	public LinkedHashSet<DataTableColumnName> getVertexIDColumnNameSet() {
		return this.getInputGraphIterator().getVertexIDColumnNameSet();
	}
	
	@Override
	public LinkedHashSet<DataTableColumnName> getVertexAdditionalFeatureColumnNameSet() {
		return this.getInputGraphIterator().getVertexAdditionalFeatureColumnNameSet();
	}

	@Override
	public LinkedHashSet<DataTableColumnName> getEdgeIDColumnNameSet() {
		return this.getInputGraphIterator().getEdgeIDColumnNameSet();
	}

	@Override
	public LinkedHashSet<DataTableColumnName> getEdgeAdditionalFeatureColumnNameSet() {
		return this.getInputGraphIterator().getEdgeAdditionalFeatureColumnNameSet();
	}
	
	@Override
	public boolean isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets() {
		return this.getInputGraphIterator().isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets();
	}

	@Override
	public LinkedHashMap<DataTableColumnName, DataTableColumnName> getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap() {
		return this.getInputGraphIterator().getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap();
	}

	@Override
	public LinkedHashMap<DataTableColumnName, DataTableColumnName> getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap() {
		return this.getInputGraphIterator().getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap();
	}
	
	/**
	 * based on the {@link #graphTypeEnforcer} TODO?????
	 */
	@Override
	public EdgeDirectednessFeature getEdgeDirectednessFeature() {
		// TODO Auto-generated method stub
		return new EdgeDirectednessFeature(
				false,//boolean hasDirectednessIndicatorColumn,
				null,//DataTableColumnName directednessIndicatorColumnName,
				this.graphTypeEnforcer.getDirectedType(),//DirectedType defaultDirectedType,
				null//Map<String, DirectedType> columnValueStringDirectedTypeMap
				);
	}
	
	/////////////////////////
	@Override
	public Map<DataTableColumnName, DataTableColumn> getVertexAttributeColNameMap() throws SQLException {
		return this.getInputGraphIterator().getVertexAttributeColNameMap();
	}

	@Override
	public Map<DataTableColumnName, DataTableColumn> getEdgeAttributeColNameMap() throws SQLException {
		return this.getInputGraphIterator().getEdgeAttributeColNameMap();
	}
	

}
