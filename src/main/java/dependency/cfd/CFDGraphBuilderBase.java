package dependency.cfd;

import java.sql.SQLException;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import context.VisframeContext;
import function.composition.CompositionFunctionID;

/**
 * base builder class for a {@link CFDGraphBase} with a set of initial {@link CompositionFunction}s in a {@link VisframeContext};
 * 
 * @author tanxu
 *
 * @param <N>
 * @param <E>
 * @param <G>
 */
abstract class CFDGraphBuilderBase<N extends CFDNode, E extends CFDEdge, G extends CFDGraphBase<N,E>> {
	private final VisframeContext hostVisframeContext;
	/**
	 * not necessarily turn out to be leaf nodes on the resulted CFD graph
	 */
	private final Set<CompositionFunctionID> initialCFIDSet;
	
	
	////////////
	protected SimpleDirectedGraph<N,E> underlyingGraph;
	
	protected G CFDGraph;
	
	
	/**
	 * whether this CFD graph is successfully constructed; should be set to true at the end of the build() method if no exceptions are thrown;
	 */
	protected boolean successfullyBuilt = false;
	
	
	/**
	 * constructor
	 * @param hostVisframeContext not null;
	 * @param initialCFIDSet not null nor empty
	 * @throws SQLException 
	 */
	CFDGraphBuilderBase(
			VisframeContext hostVisframeContext,
			Set<CompositionFunctionID> initialCFIDSet) throws SQLException{
		//validations
		if(hostVisframeContext==null)
			throw new IllegalArgumentException("given hostVisframeContext cannot be null!");
		
		if(initialCFIDSet==null || initialCFIDSet.isEmpty())
			throw new IllegalArgumentException("given initialCFIDSet cannot be null or empty!");
		
		
		
		this.hostVisframeContext = hostVisframeContext;
		this.initialCFIDSet = initialCFIDSet;
		
		this.buildUnderlyingGraph();
		this.buildCFDGraph();
	}
	
	VisframeContext getHostVisframeContext() {
		return hostVisframeContext;
	}
	
	

	Set<CompositionFunctionID> getInitialCFIDSet() {
		return initialCFIDSet;
	}
	
	/////////////////////////////////////
	
	/**
	 * build the {@link #underlyingGraph} and the {@link #dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap};
	 * <br><br>
	 * throw InputVariableCFGTargetNotAssignedToCFException if an involved CF has an CFGTargetInputVariable with CFGTarget of a CFG that is not assigned to any CF;
	 * @throws SQLException 
	 */
	protected abstract void buildUnderlyingGraph() throws SQLException;
	
	
	/**
	 * build the DAG for the CFD graph;
	 * if {@link #underlyingGraph} is null, return;
	 * 
	 */
	protected abstract void buildCFDGraph();
	
	
	/**
	 * return the DAG of the built CFD graph if it is successfully built; return null otherwise
	 * @return
	 */
	public G getBuiltGraph(){
		if(!this.successfullyBuilt) {
			return null;
		}
		return this.CFDGraph;
	}
	
}
