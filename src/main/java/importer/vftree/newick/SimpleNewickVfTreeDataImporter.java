package importer.vftree.newick;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import basic.VfNotes;
import context.project.process.logtable.StatusType;
import exception.VisframeException;
import fileformat.FileFormatID;
import fileformat.vftree.VfTreeDataFileFormatType;
import importer.vftree.VfTreeDataImporterBase;
import metadata.MetadataName;

/**
 * importer for {@link VfTreeDataFileFormatType#SIMPLE_NEWICK_1} or {@link VfTreeDataFileFormatType#SIMPLE_NEWICK_2} format tree
 * @author tanxu
 *
 */
public class SimpleNewickVfTreeDataImporter extends VfTreeDataImporterBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6477426964148046586L;
	
	///////////////////
	private transient SimpleNewickVfTreeDataFileParser parser;
	
	
	/**
	 * constructor
	 * @param notes
	 * @param dataSourcePath
	 * @param fileFormatID
	 * @param mainImportedMetadataName
	 * @param bootstrapIteration
	 */
	public SimpleNewickVfTreeDataImporter(
			VfNotes notes, Path dataSourcePath, FileFormatID fileFormatID,
			MetadataName mainImportedMetadataName, Integer bootstrapIteration
			) {
		super(notes, dataSourcePath, fileFormatID, 
				mainImportedMetadataName, bootstrapIteration);
		
		//fileFormatID must be either {@link VfTreeDataFileFormatType#SIMPLE_NEWICK_1} or {@link VfTreeDataFileFormatType#SIMPLE_NEWICK_2} format 
		if(!fileFormatID.getName().equals(VfTreeDataFileFormatType.SIMPLE_NEWICK_1.getName()) && !fileFormatID.getName().equals(VfTreeDataFileFormatType.SIMPLE_NEWICK_2.getName())) {
			throw new IllegalArgumentException("given fileFormatID is not of SIMPLE_NEWICK_1 nor SIMPLE_NEWICK_2!");
		}
		
	}

	@Override
	public SimpleNewickVfTreeDataFileParser getFileParser() {
		return parser;
	}
	
	
	/////////////////////////////////////////
	/**
	 * {@inheritDoc}
	 * @throws IOException 
	 */
	@Override
	public StatusType call() throws SQLException, IOException {
		if(this.getHostVisProjectDBContext()==null) {
			throw new VisframeException("host VisProjectDBContext is not set, cannot import data");
		}
		
		//1
		this.buildNodeAndEdgeRecordMetadataAndDataTableNames();
		
		//2
		this.readAndParseIntoDataTables();
		
		/////////////////////////////////////////////////////
		//3
		this.createAndStoreImportedMetadata();
		
		//4
		this.storeDataImporter();
		
		//
		return StatusType.FINISHED;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Override
	protected void readAndParseIntoDataTables() throws IOException, SQLException {
		parser = new SimpleNewickVfTreeDataFileParser(this.getHostVisProjectDBContext(), this);
		parser.perform();
	}

	
}
