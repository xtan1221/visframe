package importer.vftree;

import java.io.IOException;
import java.sql.SQLException;

import context.project.VisProjectDBContext;
import generic.tree.populator.VfTreePopulator;
import generic.tree.reader.filebased.VfDataFileTreeReader;
import importer.AbstractFileParser;
import metadata.graph.vftree.feature.VfTreeEdgeFeature;
import metadata.graph.vftree.feature.VfTreeNodeFeature;
import rdb.table.data.DataTableSchema;

public abstract class VfTreeDataFileParserBase extends AbstractFileParser{

	private final VfTreeDataImporterBase dataImporter;
	
	//////
	protected VfDataFileTreeReader reader;
	protected VfTreePopulator populator;
	
	/**
	 * constructor
	 * @param visProject
	 * @param dataImporter
	 */
	public VfTreeDataFileParserBase(VisProjectDBContext visProject, VfTreeDataImporterBase dataImporter) {
		super(visProject);
		
		this.dataImporter = dataImporter;
	}
	
	/**
	 * return the VfTreeNodeFeature of the VfDataFileTreeReader;
	 * facilitate building the imported VfTreeDataMetadata
	 * @return
	 */
	public VfTreeNodeFeature getVfTreeNodeFeature() {
		return this.reader.getVfTreeNodeFeature();
	}
	
	/**
	 * return the VfTreeEdgeFeature of the VfDataFileTreeReader;
	 * facilitate building the imported VfTreeDataMetadata
	 * @return
	 */
	public VfTreeEdgeFeature getVfTreeEdgeFeature() {
		return this.reader.getVfTreeEdgeFeature();
	}
	
	public DataTableSchema getNodeDataTableSchema() {
		return this.populator.getNodeDataTableSchema();
	}
	
	public DataTableSchema getEdgeDataTableSchema() {
		return this.populator.getEdgeDataTableSchema();
	}
	
	
	/////////////////////////////
	@Override
	public VfTreeDataImporterBase getDataImporter() {
		return dataImporter;
	}
	
	/**
	 * basic steps, order may vary for different graph file formats;
	 * 1. create and perform a {@link VfDataFileTreeReader} instance to parse all the data from the file;
	 * 		read the data file and parse the data into a VfTree;
	 * 2. create and perform a {@link VfTreePopulator} instance with the VfDataFileTreeReader instance 
	 * 		1. create and insert the data table schema for node and edge data
	 * 		2. populate the node data table;
	 * 		3. populate the edge data table;
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Override
	abstract public void perform() throws IOException, SQLException ;
	
	
}
