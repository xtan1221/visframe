package dependency.dos;

import java.sql.SQLException;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import context.VisframeContext;
import metadata.MetadataID;

/**
 * base class to build a DOS graph from a set of target Metadata in a VisframeContext;
 * 
 * NOTE that edge direction is from the output Metadata to input Metadata if the edge is operation; or from child component metadata to parent composite metadata if the edge type is COMPOSITE_DATA_COMPONENT
 * @author tanxu
 * 
 * @param <N>
 * @param <E>
 */
abstract class DOSGraphBuilderBase<N extends DOSNode, E extends DOSEdge, G extends DOSGraphBase<N,E>> {
	private final VisframeContext hostVisframeContext;
	private final Set<MetadataID> inducingMetadataIDSet;
	
	//
	protected SimpleDirectedGraph<N,E> underlyingGraph;
	
	
	protected G DOSGraph;
	
	/**
	 * whether this DOS graph is successfully constructed; should be set to true at the end of the build() method if no exceptions are thrown;
	 */
	protected boolean successfullyBuilt = false;
	
	/**
	 * constructor
	 * @param hostVisframeContext
	 * @param inducingMetadataIDSet
	 */
	protected DOSGraphBuilderBase(VisframeContext hostVisframeContext, Set<MetadataID> inducingMetadataIDSet){
		//TODO validations
		
		this.hostVisframeContext = hostVisframeContext;
		this.inducingMetadataIDSet = inducingMetadataIDSet;
		
		try {
			this.buildUnderlyingGraph();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("SQLException is thrown during building the undering graph:"+e.getMessage());
		}
		this.buildDOSGraph();
	}
	
	protected VisframeContext getHostVisframeContext() {
		return hostVisframeContext;
	}
	
	
	public Set<MetadataID> getInducingMetadataIDSet() {
		return inducingMetadataIDSet;
	}
	
	//////////////////////////////////
	/**
	 * build the {@link #underlyingGraph}
	 * @throws SQLException 
	 */
	protected abstract void buildUnderlyingGraph() throws SQLException;
	
	
	
	protected abstract void buildDOSGraph();

	
	///////////////////////////////////////////////
	public G getDOSGraph() {
		if(!this.successfullyBuilt) {
			return null;
		}
		return DOSGraph;
	}
	

}
