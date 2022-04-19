package importer.graph.gexf;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import basic.VfNotes;
import context.project.process.logtable.StatusType;
import exception.VisframeException;
import fileformat.graph.GraphDataFileFormatType;
import importer.graph.GraphDataImporterBase;
import metadata.MetadataName;
import metadata.graph.type.GraphTypeEnforcer;


/**
 * class for data importer for a graph of {@link GraphDataFileFormatType#SIMPLE_GEXF} format;
 * 
 * this importer will always include any GEXF defined default attributes of node and edge appearing in the GEXF data file;
 * 		see Node Specification section and Edge Specification section in the GEXF 1.2draft Primer
 * 
 * transformation of data type of attributes
 * 		GEXF data type string -> visframe sql data type
 * 		"integer" -> INTEGER
 * 		"long" -> LONG
 * 		"double" and "float" -> DOUBLE
 * 		"boolean" -> BOOLEAN
 * 		other types including "string" and "liststring" and "anyURI" -> VARCHAR[50]
 * 
 * 
 * @author tanxu
 *
 */
public class SimpleGEXFGraphDataImporter extends GraphDataImporterBase{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8425221213103261049L;
	
	///////////////////////////
	private transient SimpleGEXFGraphDataFileParser parser;
	
	
	/**
	 * constructor
	 * @param notes
	 * @param dataSourcePath
	 * @param fileFormatID
	 * @param mainImportedMetadataName
	 * @param typeEnforcer
	 * @param nodeRecordDataName
	 * @param edgeRecordDataName
	 */
	public SimpleGEXFGraphDataImporter(
			VfNotes notes, Path dataSourcePath,
			MetadataName mainImportedMetadataName, GraphTypeEnforcer typeEnforcer
//			MetadataName nodeRecordDataName,
//			MetadataName edgeRecordDataName
			
			) {
		super(notes, dataSourcePath, GraphDataFileFormatType.SIMPLE_GEXF.getFileFormat().getID(), mainImportedMetadataName, typeEnforcer);
		// TODO Auto-generated constructor stub
		
//		
//		if(!fileFormatID.getName().equals(GraphDataFileFormatType.SIMPLE_GEXF.getName())) {
//			throw new IllegalArgumentException("given fileFormatID is not of SIMPLE_GEXF type!");
//		}
	}
	
	
	@Override
	public SimpleGEXFGraphDataFileParser getFileParser() {
		return parser;
	}
	
	/////////////////////////////////
	@Override
	public StatusType call() throws SQLException, IOException {
		if(this.getHostVisProjectDBContext()==null) {
			throw new VisframeException("host VisProjectDBContext is not set, cannot import data");
		}
		
		this.buildNodeAndEdgeRecordMetadataAndDataTableNames();
		
		this.readAndParseIntoDataTables();
		
		this.createAndStoreImportedMetadata();
		
		this.storeDataImporter();
		
		//
		return StatusType.FINISHED;	
	}
	
	@Override
	protected void readAndParseIntoDataTables() throws IOException, SQLException {
		
		parser = new SimpleGEXFGraphDataFileParser(this.getHostVisProjectDBContext(), this);
		
		parser.perform();
	}
	
}
