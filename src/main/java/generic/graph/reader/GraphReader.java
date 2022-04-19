package generic.graph.reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import generic.graph.AbstractGraphIterator;

/**
 * read a graph from a data source as it is;
 * 
 * data source could be a data file of a graph file format or data from a VisProjectDBContext;
 * 
 * @author tanxu
 * 
 */
public abstract class GraphReader extends AbstractGraphIterator {
	
	
	
	/**
	 * initialize the reader to prepare for the node and edge reading;
	 * should be invoked at the end of constructor of each final subtype class of this class;
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public abstract void initialize() throws FileNotFoundException, IOException, SQLException;
	
}
