package importer.vftree.newick;

import java.io.IOException;
import java.sql.SQLException;

import context.project.VisProjectDBContext;
import generic.tree.populator.VfTreePopulator;
import generic.tree.reader.filebased.newick.SimpleNewickFileTreeReader;
import importer.vftree.VfTreeDataFileParserBase;

public class SimpleNewickVfTreeDataFileParser extends VfTreeDataFileParserBase{
	/**
	 * constructor
	 * @param visProject
	 * @param dataImporter
	 */
	public SimpleNewickVfTreeDataFileParser(VisProjectDBContext visProject, SimpleNewickVfTreeDataImporter dataImporter) {
		super(visProject, dataImporter);
	}
	
	/**
	 * 1. create and perform a {@link SimpleNewickFileTreeReader} instance to parse all the data from the file;
	 * 		read the data file and parse the data into a VfTree;
	 * 2. create and perform a {@link VfTreePopulator} instance with the VfDataFileTreeReader instance 
	 * 		1. create and insert the data table schema for node and edge data
	 * 		2. populate the node data table;
	 * 		3. populate the edge data table;
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Override
	public void perform() throws IOException, SQLException {
		this.reader = new SimpleNewickFileTreeReader(this.getDataImporter().getDataSourcePath(), this.getDataImporter().getFileFormat().getFileType());
		reader.perform();
		
		this.populator = new VfTreePopulator(
				reader, this.getDataImporter().getHostVisProjectDBContext(), 
				this.getDataImporter().getNodeRecordDataTableName(), this.getDataImporter().getEdgeRecordDataTableName());
		populator.perform();
		
		
	}
}
