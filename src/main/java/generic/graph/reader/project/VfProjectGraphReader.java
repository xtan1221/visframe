package generic.graph.reader.project;

import context.project.VisProjectDBContext;
import generic.graph.reader.GraphReader;

public abstract class VfProjectGraphReader extends GraphReader {
	private final VisProjectDBContext hostVisProjectDBContext;
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	VfProjectGraphReader(VisProjectDBContext hostVisProjectDBContext){
		
		this.hostVisProjectDBContext = hostVisProjectDBContext;
	}
	
	
	
	VisProjectDBContext getHostVisProjectDBContext() {
		return this.hostVisProjectDBContext;
	}
	
}