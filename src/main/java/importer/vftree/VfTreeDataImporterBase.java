package importer.vftree;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import basic.VfNotes;
import basic.lookup.project.type.udt.VisProjectMetadataManager;
import context.project.process.logtable.StatusType;
import fileformat.FileFormat;
import fileformat.FileFormatID;
import fileformat.vftree.VfTreeDataFileFormat;
import fileformat.vftree.VfTreeDataFileFormatType;
import importer.AbstractDataImporter;
import metadata.DataType;
import metadata.MetadataName;
import metadata.SourceType;
import metadata.graph.utils.GraphNameBuilder;
import metadata.graph.vftree.VfTreeDataMetadata;
import metadata.record.RecordDataMetadata;
import rdb.table.data.DataTableName;
import rdb.table.data.DataTableSchema;

/**
 * 
 * @author tanxu
 *
 */
public abstract class VfTreeDataImporterBase extends AbstractDataImporter{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6924601251362204375L;

	
	/////////////////////////////
	private final Integer bootstrapIteration;
	
	////////
//	private transient VfTreeDataFileFormat fileFormat; 
	
	private transient MetadataName nodeRecordMetadataDataName;
	private transient MetadataName edgeRecordMetadataDataName;
	
	private transient DataTableName nodeRecordDataTableName;
	private transient DataTableName edgeRecordDataTableName;
	
//	protected transient DataTableSchema nodeDataTableSchema;
//	protected transient DataTableSchema edgeDataTableSchema;
	
	
	/**
	 * constructor
	 * @param notes
	 * @param dataSourceURLString
	 * @param metadataName
	 * @param fileFormat
	 * @param bootstrapIteration
	 * @param nodeRecordDataName
	 * @param edgeRecordDataName
	 */
	public VfTreeDataImporterBase(
			VfNotes notes, Path dataSourcePath, FileFormatID fileFormatID, 
			MetadataName mainImportedMetadataName,
			////
			Integer bootstrapIteration
			) {
		super(notes, dataSourcePath, fileFormatID, mainImportedMetadataName);
		
		if(fileFormatID.getType()!=DataType.vfTREE) {
			throw new IllegalArgumentException("given fileFormatID is not of RECORD type");
		}
		
		this.bootstrapIteration = bootstrapIteration;
	}
	
	
	public Integer getBootstrapIteration() {
		return bootstrapIteration;
	}
	
	

	@Override
	public DataType getDataType() {
		return DataType.vfTREE;
	}
	
	/**
	 * returns the {@link FileFormat} of the source data file;
	 * @return
	 */
	@Override
	public VfTreeDataFileFormat getFileFormat() {
		return VfTreeDataFileFormatType.valueOf(this.getFileFormatID().getName().getStringValue()).getFileFormat();
	}
	
	////////////////////////////////

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
	
	/**
	 * @return the nodeDataTableSchema
	 */
	public DataTableSchema getNodeDataTableSchema() {
		return this.getFileParser().getNodeDataTableSchema();
	}

	
	/**
	 * @return the edgeDataTableSchema
	 */
	public DataTableSchema getEdgeDataTableSchema() {
		return this.getFileParser().getEdgeDataTableSchema();
	}

	
	/**
	 * generic pipeline of importing a VfTreeDataMetadata
	 * ////pre-processing
	 * 0. check if host {@link VisProjectDBContext} is set;
	 * 
	 * 1. invoke {@link #buildNodeAndEdgeRecordMetadataAndDataTableNames()} method
	 * 		create the record data name and data table name for node and edge record data
	 * 
	 * 
	 * 
	 * ////////////
	 * 2. invoke {@link #readAndParseIntoDataTables()}
	 * 		create a {@link VfTreeDataFileParserBase} and perform it;
	 * 			read and parse data file into a VfTree;
	 * 			create and insert node and edge record data table schema into host VisProjectDBContext;
	 * 			populate node and edge record data tables with the VfTree parsed from data file;
	 * 
	 * 3. invoke {@link #createAndStoreImportedMetadata()} 
	 * 		create {@link RecordDataMetadata} for node and edge record data and insert them into metadata management table;
	 * 		create {@link VfTreeDataMetadata} and insert it into Metadata management table;
	 * 		
	 * 4. invoke {@link #storeDataImporter()}
	 * 		insert this {@ SimpleNewickVfTreeDataImporter} into the DataImporter management table;
	 * @throws SQLException
	 * @throws IOException 
	 */
	@Override
	public abstract StatusType call() throws SQLException, IOException;
	
	@Override
	public abstract VfTreeDataFileParserBase getFileParser();
	
	
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
	 * read the data file and populate the node and edge data tables
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Override
	abstract protected void readAndParseIntoDataTables() throws IOException, SQLException;
	
	
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
				this.getNodeDataTableSchema(),
				true
				);
		RecordDataMetadata edgeRecordMetadata = new RecordDataMetadata(
				this.getEdgeRecordMetadataDataName(), VfNotes.makeVisframeDefinedVfNotes(),
				SourceType.STRUCTURAL_COMPONENT,
				this.getMainImportedMetadataID(),
				null,
				this.getEdgeDataTableSchema(),
				false
				);
		

//		MetadataName name, VfNotes notes, 
//		SourceType sourceType, OperationID sourceOperationID,
//		MetadataName nodeRecordDataName, 
//		MetadataName edgeRecordDataName,
//		VfTreeNodeFeature graphNodeFeature, 
//		VfTreeEdgeFeature graphEdgeFeature,
//		//////
//		Integer bootstrapIteration
		VfTreeDataMetadata treeMetadata = new VfTreeDataMetadata(
				this.getMainImportedMetadataName(), VfNotes.makeVisframeDefinedVfNotes(),
				SourceType.IMPORTED, null,
				this.getNodeRecordMetadataDataName(),
				this.getEdgeRecordMetadataDataName(),
				this.getFileParser().getVfTreeNodeFeature(),
				this.getFileParser().getVfTreeEdgeFeature(),
				this.getBootstrapIteration()
				);
		
		
		///////
		//insert the VfTreeDataMetadata first before the node and edge record metadata
		//this is because VfTreeDataMetadata does not depends on the node/edge record metadata in the DOS graph but node/edge record data depend on the VfTreeDataMetadata
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().insert(treeMetadata);
		
		//
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().insert(nodeRecordMetadata);
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().insert(edgeRecordMetadata);
	}
	
	/**
	 *  {@inheritDoc}
	 */
	@Override
	protected void storeDataImporter() throws SQLException {
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getDataImporterManager().insert(this);
	}


	///////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((bootstrapIteration == null) ? 0 : bootstrapIteration.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof VfTreeDataImporterBase))
			return false;
		VfTreeDataImporterBase other = (VfTreeDataImporterBase) obj;
		if (bootstrapIteration == null) {
			if (other.bootstrapIteration != null)
				return false;
		} else if (!bootstrapIteration.equals(other.bootstrapIteration))
			return false;
		return true;
	}


	
}
