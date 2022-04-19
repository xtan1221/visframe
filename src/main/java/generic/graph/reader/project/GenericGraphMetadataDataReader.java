package generic.graph.reader.project;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import context.project.VisProjectDBContext;
import generic.graph.DirectedType;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import metadata.MetadataID;
import metadata.graph.GraphDataMetadata;
import metadata.graph.feature.EdgeDirectednessFeature;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;


/**
 * TODO ??
 * delegate to a wrapped {@link RecordToGraphReaderImpl};
 * 
 * read from a {@link GraphDataMetadata} in a VisProjectDBContext;
 * 
 * note that for {@link GraphDataMetadata}, the edges are always either directed or undirected based on the observed {@link GraphMetadataType}
 * 
 * @author tanxu
 * 
 */
public class GenericGraphMetadataDataReader extends VfProjectGraphReader {
	/**
	 * 
	 */
	private final MetadataID graphDataMetadataID;
	
	/////////////////////
//	private RecordDataMetadata nodeRecordDataMetadata;
//	private Map<DataTableColumnName, DataTableColumn> vertexAttributeColNameMap;
//	
//	private RecordDataMetadata edgeRecordDataMetadata;
//	private Map<DataTableColumnName, DataTableColumn> edgeAttributeColNameMap;
	
	private GraphDataMetadata graphDataMetadata;
	///
	private RecordToGraphReaderImpl recordDataToGraphDataReader;
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param graphDataMetadata
	 */
	public GenericGraphMetadataDataReader(VisProjectDBContext hostVisProjectDBContext,MetadataID genericGraphDataMetadataID) {
		super(hostVisProjectDBContext);
		
		if(!genericGraphDataMetadataID.getDataType().isGenericGraph()) {
			throw new IllegalArgumentException("given genericGraphDataMetadataID is not of graph or vftree type!");
		}
		
		this.graphDataMetadataID = genericGraphDataMetadataID;
		
	}
	
	
	public MetadataID getGraphDataMetadataID() {
		return graphDataMetadataID;
	}

	
	/**
	 * @return the graphDataMetadata
	 */
	public GraphDataMetadata getGraphDataMetadata() {
		return graphDataMetadata;
	}
	
	/////////////////////////////////////
	/**
	 * initialize the delegated {@link RecordToGraphReader}
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * 
	 */
	@Override
	public void initialize() throws IOException, SQLException {
		//lookup the GraphDataMetadata
		this.graphDataMetadata = (GraphDataMetadata)this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().lookup(this.getGraphDataMetadataID());
//		this.graphDataMetadata = (VfTreeDataMetadata)this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().lookup(this.getGraphDataMetadataID());
		
		//build the RecordToGraphReaderImpl
//		VisProjectDBContext hostVisProjectDBContext, MetadataID vertexDataSourceRecordDataID,
//		MetadataID edgeDataSourceRecordDataID, boolean toFilterOutDuplicates,
//		LinkedHashSet<DataTableColumnName> vertexIDColumnNameSet,
//		LinkedHashSet<DataTableColumnName> vertexAdditionalFeatureColumnNameSet,
//		LinkedHashSet<DataTableColumnName> edgeIDColumnNameSet,
//		LinkedHashSet<DataTableColumnName> edgeAdditionalFeatureColumnNameSet,
//		boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets,
//		LinkedHashMap<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap,
//		LinkedHashMap<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap,
//		boolean directed
		this.recordDataToGraphDataReader = new RecordToGraphReaderImpl(
				this.getHostVisProjectDBContext(), 
				true,//hasVertexDataSourceRecordData
				this.getGraphDataMetadata().getNodeRecordMetadataID(),
				this.getGraphDataMetadata().getEdgeRecordMetadataID(),
				false,//toFilterOutDuplicates is not needed, since vertex and edges should all be unique for a GraphDataMetadata
				this.getGraphDataMetadata().getGraphVertexFeature().getIDColumnNameSet(),
				this.getGraphDataMetadata().getGraphVertexFeature().getAdditionalFeatureColumnNameSet(),
				this.getGraphDataMetadata().getGraphEdgeFeature().getIDColumnNameSet(),
				this.getGraphDataMetadata().getGraphEdgeFeature().getAdditionalFeatureColumnNameSet(),
				this.getGraphDataMetadata().getGraphEdgeFeature().isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets(),
				this.getGraphDataMetadata().getGraphEdgeFeature().getNodeIDColumnNameEdgeSourceNodeIDColumnNameMap(),
				this.getGraphDataMetadata().getGraphEdgeFeature().getNodeIDColumnNameEdgeSinkNodeIDColumnNameMap(),
				this.getGraphDataMetadata().getObservedGraphType().isContainingDirectedEdgeOnly()?DirectedType.DIRECTED_FORWARD:DirectedType.UNDIRECTED //directed
				);
		this.recordDataToGraphDataReader.initialize();
	}
	
	@Override
	public VfGraphVertex nextVertex() throws IOException, SQLException {
		return this.recordDataToGraphDataReader.nextVertex();
	}
	
	@Override
	public VfGraphEdge nextEdge() throws IOException, SQLException {
		return this.recordDataToGraphDataReader.nextEdge();
	}
	
	@Override
	public void restart() throws IOException, SQLException {
		this.recordDataToGraphDataReader.restart();
	}
	
	@Override
	public boolean vertexAreDone() {
		return this.recordDataToGraphDataReader.vertexAreDone();
	}
	
	@Override
	public boolean edgeAreDone() {
		return this.recordDataToGraphDataReader.edgeAreDone();
	}
	

	
	@Override
	public EdgeDirectednessFeature getEdgeDirectednessFeature() {
		// TODO Auto-generated method stub
		return this.graphDataMetadata.getGraphEdgeFeature().getDirectednessFeature();
	}
	
	
	///////////the following method should never be invoked; TODO
	///////////////////////////
	@Override
	public LinkedHashSet<DataTableColumnName> getVertexIDColumnNameSet() {
		return this.getGraphDataMetadata().getGraphVertexFeature().getIDColumnNameSet();
	}
	
	
	@Override
	public LinkedHashSet<DataTableColumnName> getVertexAdditionalFeatureColumnNameSet() {
		return this.getGraphDataMetadata().getGraphVertexFeature().getAdditionalFeatureColumnNameSet();
	}


	@Override
	public LinkedHashSet<DataTableColumnName> getEdgeIDColumnNameSet() {
		return this.getGraphDataMetadata().getGraphEdgeFeature().getIDColumnNameSet();
	}


	@Override
	public LinkedHashSet<DataTableColumnName> getEdgeAdditionalFeatureColumnNameSet() {
		return this.getGraphDataMetadata().getGraphEdgeFeature().getAdditionalFeatureColumnNameSet();
	}

	
	@Override
	public boolean isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets() {
		return this.getGraphDataMetadata().getGraphEdgeFeature().isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets();
	}


	@Override
	public LinkedHashMap<DataTableColumnName, DataTableColumnName> getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap() {
		return this.getGraphDataMetadata().getGraphEdgeFeature().getNodeIDColumnNameEdgeSourceNodeIDColumnNameMap();
	}


	@Override
	public LinkedHashMap<DataTableColumnName, DataTableColumnName> getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap() {
		return this.getGraphDataMetadata().getGraphEdgeFeature().getNodeIDColumnNameEdgeSinkNodeIDColumnNameMap();
	}
	
	
	//////////////////////////////////////
	@Override
	public Map<DataTableColumnName, DataTableColumn> getVertexAttributeColNameMap() throws SQLException {

		return this.recordDataToGraphDataReader.getVertexAttributeColNameMap();
		
	}
	
	
	@Override
	public Map<DataTableColumnName, DataTableColumn> getEdgeAttributeColNameMap() throws SQLException {

		return this.recordDataToGraphDataReader.getEdgeAttributeColNameMap();
		
	}

}
