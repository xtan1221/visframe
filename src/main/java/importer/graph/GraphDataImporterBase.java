package importer.graph;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import basic.VfNotes;
import basic.lookup.project.type.udt.VisProjectMetadataManager;
import context.project.process.logtable.StatusType;
import fileformat.FileFormat;
import fileformat.FileFormatID;
import fileformat.graph.GraphDataFileFormat;
import fileformat.graph.GraphDataFileFormatType;
import importer.AbstractDataImporter;
import metadata.DataType;
import metadata.MetadataName;
import metadata.SourceType;
import metadata.graph.GraphDataMetadata;
import metadata.graph.type.GraphTypeEnforcer;
import metadata.graph.utils.GraphNameBuilder;
import metadata.record.RecordDataMetadata;
import rdb.table.data.DataTableName;


/**
 * base class for graph data importer;
 * 
 * note that for a specific graph format type, an instance of subclass of this class should be explicitly created with importing settings(if any) specific to that type
 * @author tanxu
 *
 */
public abstract class GraphDataImporterBase extends AbstractDataImporter{
	/**
	 * 
	 */
	private static final long serialVersionUID = 863810309686053110L;
	
	
	/////////////////////////////
	private final GraphTypeEnforcer graphTypeEnforcer; //
	
	/////////////////
	private transient MetadataName nodeRecordMetadataDataName;
	private transient MetadataName edgeRecordMetadataDataName;
	
	private transient DataTableName nodeRecordDataTableName;
	private transient DataTableName edgeRecordDataTableName;
	
	/**
	 * constructor
	 * @param notes
	 * @param dataSourceURLString
	 * @param metadataName
	 * @param fileFormat
	 * @param graphTypeEnforcer
	 */
	protected GraphDataImporterBase(
			VfNotes notes, Path dataSourcePath, FileFormatID fileFormatID, 
			MetadataName mainImportedMetadataName,
			
			GraphTypeEnforcer graphTypeEnforcer
			) {
		super(notes, dataSourcePath, fileFormatID, mainImportedMetadataName);
		
		if(graphTypeEnforcer == null)
			throw new IllegalArgumentException("given graphTypeEnforcer cannot be null!");
		if(fileFormatID.getType()!=DataType.GRAPH) {
			throw new IllegalArgumentException("given fileFormatID is not of GRAPH type");
		}
		
		///////////
		
		this.graphTypeEnforcer = graphTypeEnforcer;
	}
	
	public GraphTypeEnforcer getGraphTypeEnforcer() {
		return graphTypeEnforcer;
	}

	/**
	 * @return the nodeRecordMetadataDataName
	 */
	public MetadataName getNodeRecordMetadataDataName() {
		return nodeRecordMetadataDataName;
	}

	/**
	 * @return the edgeRecordMetadataDataName
	 */
	public MetadataName getEdgeRecordMetadataDataName() {
		return edgeRecordMetadataDataName;
	}

	/**
	 * @return the nodeRecordDataTableName
	 */
	public DataTableName getNodeRecordDataTableName() {
		return nodeRecordDataTableName;
	}
	
	/**
	 * @return the edgeRecordDataTableName
	 */
	public DataTableName getEdgeRecordDataTableName() {
		return edgeRecordDataTableName;
	}
	
	
	
	/////////////////////////////////
	/**
	 * build the name for node and edge record data Metadata
	 * 		1. build initial name for node and edge record data name
	 * 		2. find out the next available name with {@link VisProjectMetadataManager#findNextAvailableMetadataName(MetadataName, metadata.DataType)}
	 * @throws SQLException 
	 * 
	 */
	protected void buildNodeAndEdgeRecordMetadataAndDataTableNames() throws SQLException {
		GraphNameBuilder builder = new GraphNameBuilder(this.getHostVisProjectDBContext(), this.getMainImportedMetadataName());
		
		this.nodeRecordMetadataDataName = builder.getVertexRecordMetadataName();
		this.edgeRecordMetadataDataName = builder.getEdgeRecordMetadataName();
		
		this.nodeRecordDataTableName = builder.getVertexDataTableName();
		this.edgeRecordDataTableName = builder.getEdgeDataTableName();
	}
	
	

	/**
	 * 
	 */
	@Override
	public DataType getDataType() {
		return DataType.GRAPH;
	}
	
	
	/**
	 * returns the {@link FileFormat} of the source data file;
	 * @return
	 */
	@Override
	public GraphDataFileFormat getFileFormat() {
		return GraphDataFileFormatType.valueOf(this.getFileFormatID().getName().getStringValue()).getFileFormat();
	}
	
	
	@Override
	public abstract GraphDataFileParserBase getFileParser();
	
	/**
	 * 1. create an instance of subclass of GraphDataFileParserBase corresponding to the subclass of this class;
	 * 2. invoke the perform method of GraphDataFileParserBase subclass instance;
	 * 		1. read and parse the data file to build a underlying graph object and GraphNodeFeature and GraphEdgeFeature
	 * 		2. create table schema for node and edge data table and populate them with the data in the underlying graph object
	 * 3. create node and edge RecordDataMetadata and insert them into the Metadata management table;
	 * 4. create GraphDataMetadata for the imported graph and insert it into the the Metadata management table;
	 * 5. 
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Override
	public abstract StatusType call() throws SQLException, IOException;
	
	
	
	/**
	 * 
	 * @throws SQLException 
	 * 
	 */
	@Override
	protected void createAndStoreImportedMetadata() throws SQLException {
//		MetadataName name, VfNotes notes, 
//		SourceType sourceType,
//		MetadataID sourceCompositeDataMetadataID, OperationID sourceOperationID,
//		
//		DataTableSchema dataTableSchema,
//		Boolean ofGenericGraphNode
		RecordDataMetadata nodeRecordMetadata = new RecordDataMetadata(
				this.getNodeRecordMetadataDataName(), VfNotes.makeVisframeDefinedVfNotes(),
				SourceType.STRUCTURAL_COMPONENT,
				this.getMainImportedMetadataID(),
				null,
				this.getFileParser().getVertexRecordDataTableSchema(),
				true
				);
		RecordDataMetadata edgeRecordMetadata = new RecordDataMetadata(
				this.getEdgeRecordMetadataDataName(), VfNotes.makeVisframeDefinedVfNotes(),
				SourceType.STRUCTURAL_COMPONENT,
				this.getMainImportedMetadataID(),
				null,
				this.getFileParser().getEdgeRecordDataTableSchema(),
				false
				);
		
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().insert(nodeRecordMetadata);
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().insert(edgeRecordMetadata);
//		MetadataName name, VfNotes notes, 
//		SourceType sourceType, OperationID sourceOperationID,
//		MetadataName nodeRecordDataName, 
//		MetadataName edgeRecordDataName,
//		VfTreeNodeFeature graphNodeFeature, 
//		VfTreeEdgeFeature graphEdgeFeature,
//		//////
//		Integer bootstrapIteration
		GraphDataMetadata graphMetadata = new GraphDataMetadata(
				this.getMainImportedMetadataName(), VfNotes.makeVisframeDefinedVfNotes(),
				SourceType.IMPORTED, null,
				this.getNodeRecordMetadataDataName(),
				this.getEdgeRecordMetadataDataName(),
				this.getFileParser().getGraphVertexFeature(),
				this.getFileParser().getGraphEdgeFeature(),
				this.getFileParser().getObservedGraphType()
				);
		
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().insert(graphMetadata);
		
	}
	
	/**
	 * 
	 */
	@Override
	protected void storeDataImporter() throws SQLException {
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getDataImporterManager().insert(this);
	}

	


	
	
	/////////////////////////////
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((graphTypeEnforcer == null) ? 0 : graphTypeEnforcer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof GraphDataImporterBase))
			return false;
		GraphDataImporterBase other = (GraphDataImporterBase) obj;
		if (graphTypeEnforcer == null) {
			if (other.graphTypeEnforcer != null)
				return false;
		} else if (!graphTypeEnforcer.equals(other.graphTypeEnforcer))
			return false;
		return true;
	}
	
}
