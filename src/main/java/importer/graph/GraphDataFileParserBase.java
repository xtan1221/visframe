package importer.graph;

import java.io.IOException;
import java.sql.SQLException;

import generic.graph.reader.GraphReader;
import context.project.VisProjectDBContext;
import generic.graph.builder.GraphBuilder;
import generic.graph.populator.GraphDataTablePopulator;
import generic.graph.reader.filebased.GraphFileReader;
import importer.AbstractFileParser;
import metadata.graph.feature.GraphEdgeFeature;
import metadata.graph.feature.GraphVertexFeature;
import metadata.graph.type.GraphMetadataType;
import rdb.table.data.DataTableSchema;

/**
 * base class for Graph data file parser;
 * 
 * for each specific GraphDataFileFormatType, a subclass should be implemented
 * 
 * differs from {@link GraphReader}; see {@link #perform()}
 * 
 * @author tanxu
 *
 */
public abstract class GraphDataFileParserBase extends AbstractFileParser {
	/**
	 * 
	 */
	private final GraphDataImporterBase dataImporter;
	
	
	//information to be constructed during parsing the data file
//	protected DataTableSchema nodeDataTableSchema;
//	protected DataTableSchema edgeDataTableSchema;
	
//	protected GraphVertexFeature nodeFeature;
//	protected GraphEdgeFeature edgeFeature;
//	protected GraphMetadataType observedType;
	
	/**
	 * constructor
	 * @param visProject
	 * @param dataImporter
	 */
	protected GraphDataFileParserBase(VisProjectDBContext visProject,GraphDataImporterBase dataImporter) {
		super(visProject);
		// TODO Auto-generated constructor stub
		
		
		this.dataImporter = dataImporter;
	}
	
	/////////////////
	public DataTableSchema getVertexRecordDataTableSchema() {
		return this.getGraphDataTablePopulator().getVertexDataTableSchema();
	}
	
	public DataTableSchema getEdgeRecordDataTableSchema() {
		return this.getGraphDataTablePopulator().getEdgeDataTableSchema();
	}
	
	public GraphVertexFeature getGraphVertexFeature() {
		return this.getFileReader().makeGraphVertexFeature();
	}
	
	public GraphEdgeFeature getGraphEdgeFeature() {
		return this.getFileReader().makeGraphEdgeFeature();
	}
	
	/**
	 * return the observed graph type of the imported GraphDataMetadata
	 * @return
	 */
	public GraphMetadataType getObservedGraphType() {
		return this.getGraphBuilder().getOberservedType();
	}
	
	
	protected abstract GraphFileReader getFileReader();
	protected abstract GraphBuilder getGraphBuilder();
	protected abstract GraphDataTablePopulator getGraphDataTablePopulator();
	
	
	//////////////////////////////
	@Override
	public GraphDataImporterBase getDataImporter() {
		return dataImporter;
	}
	
	/**
	 * basic steps, order may vary for different graph file formats;
	 * 1. create and perform a {@link GraphFileReader} instance to parse all the data from the file;
	 * 		also build the vertex and edge data table columns;
	 * 		also find out all the information need to build the GraphVertexFeature and GraphEdgeFeature of the imported GraphDataMetadata;
	 * 2. create and perform a {@link GraphBuilder} instance with the GraphFileReader instance to build a graph object of a specific graph engine;
	 * 		perform the {@link GraphTypeEnforcer};
	 * 		identify the observed {@link GraphMetadataType};
	 * 3. create and perform a {@link GraphDataTablePopulator} instance with the GraphBuilder instance;
	 * 		create the data table schema for node and edge record data and populate them with the data from the graph object built by the GraphBuilder;
	 * 
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Override
	abstract public void perform() throws IOException, SQLException;

}
