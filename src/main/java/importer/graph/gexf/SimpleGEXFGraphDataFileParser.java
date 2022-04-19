package importer.graph.gexf;

import java.io.IOException;
import java.sql.SQLException;

import context.project.VisProjectDBContext;
import generic.graph.builder.GraphBuilder;
import generic.graph.builder.JGraphT.JGraphTGraphBuilder;
import generic.graph.populator.GraphDataTablePopulator;
import generic.graph.populator.GraphDataTablePopulatorImpl;
import generic.graph.reader.filebased.SimpleGEXFFileReader;
import importer.graph.GraphDataFileParserBase;

/**
 * 
 * @author tanxu
 *
 */
public class SimpleGEXFGraphDataFileParser extends GraphDataFileParserBase {
	
	private SimpleGEXFFileReader fileReader;
	private GraphBuilder graphBuilder;
	private GraphDataTablePopulator graphDataTablePopulator;
	
	/**
	 * constructor
	 * @param visProject
	 * @param dataImporter
	 */
	public SimpleGEXFGraphDataFileParser(VisProjectDBContext visProject, SimpleGEXFGraphDataImporter dataImporter) {
		super(visProject, dataImporter);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected SimpleGEXFFileReader getFileReader() {
		return this.fileReader;
	}
	
	@Override
	protected GraphBuilder getGraphBuilder() {
		return this.graphBuilder;
	}
	
	@Override
	protected GraphDataTablePopulator getGraphDataTablePopulator() {
		return this.graphDataTablePopulator;
	}
	
	//////////////////////////////////////////
	/**
	 * this method will read and parse the data from data file, build the node/edge record data table schema and populate the data tables with the parsed data from the file;
	 * 
	 * 1. create file reader {@link SimpleGEXFFileReader}
	 * 2. create builder {@link JGraphTGraphBuilder}
	 * 3. create data table populator {@link GraphDataTablePopulator}
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Override
	public void perform() throws IOException, SQLException {
		this.fileReader = new SimpleGEXFFileReader(this.getDataImporter().getDataSourcePath());
		
		//
		this.graphBuilder = new JGraphTGraphBuilder(this.fileReader, this.getDataImporter().getGraphTypeEnforcer(), false); //do not add discovered vertex from edge, not necessary
		this.graphBuilder.perform();
		
		this.graphDataTablePopulator = new GraphDataTablePopulatorImpl(
				this.getVisProjectDBContext(),
				this.graphBuilder,
				this.getDataImporter().getNodeRecordDataTableName(),
				this.getDataImporter().getEdgeRecordDataTableName()
				);
		
		this.graphDataTablePopulator.perform();
	}


}
