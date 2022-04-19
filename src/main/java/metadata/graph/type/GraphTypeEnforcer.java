package metadata.graph.type;

import basic.reproduce.SimpleReproducible;
import generic.graph.DirectedType;

/**
 * enforce the graph type regardless of the default graph type of the generator;
 * 
 * for example, a specific graph file format may contain undirected graph only, but if the GraphTypeEnforcer of the DataImporter is set to enforce directed, the 
 * imported graph data will be directed;
 * 
 * 1. data importing
 * after a graph data is read from a file to build a Graph object based on some graph api, the graph data will be imported into rdb of VisProjectDBContext;
 * before importing, need to pre-process the graph data with the graph api (JGRAPHT) based on the GraphTypeEnforcer;
 * 
 * 2. graph transformation operation
 * when creating a graph transformation operation instance, need to create a GraphTypeEnforcer to guild the transformation together with the input GraphDataMetadata to be transformed;
 * 
 * @author tanxu
 * 
 */
public class GraphTypeEnforcer implements SimpleReproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6597289053912858773L;
	
	/////////////////////////////
	/**
	 * one and only one of {@link #toForceDirected} and {@link #toForceUndirected} can be true; 
	 */
	private final boolean toForceDirected;
	/**
	 * if toForceDirected, this must be set;
	 */
	private final DirectedEnforcingMode directedForcingMode;
	/**
	 * if true, transform all directed edges to undirected
	 */
	private final boolean toForceUndirected;
	
	/**
	 * if true, remove all parallel edges and only keep a random one
	 */
	private final boolean toForceNoParallelEdges;
	
	/**
	 * if true, remove all self loop edges
	 */
	private final boolean toForceNoSelfLoops;
	
	/**
	 * constructor;
	 * at least one of the boolean type parameter must be true, otherwise, the enforcer does not enforce any graph property, thus invalid
	 * @param toForceDirected
	 * @param directedForcingMode
	 * @param toForceUndirected
	 * @param toForceNoParallelEdges
	 * @param toForceNoSelfLoops
	 */
	public GraphTypeEnforcer(
			boolean toForceDirected,
			DirectedEnforcingMode directedForcingMode,
			boolean toForceUndirected,
			boolean toForceNoParallelEdges,
			boolean toForceNoSelfLoops
			) {
		//toForceDirected and toForceUndirected cannot both be true or false(visframe only allows either directed or undirected GraphDataMetadata, but not mixed type)
		if((toForceDirected && toForceUndirected) || (!toForceDirected && !toForceUndirected)) {
			throw new IllegalArgumentException("toForceDirected and toForceUndirected cannot both be true or false(visframe only allows either directed or undirected GraphDataMetadata, but not mixed type)");
		}
		//if toForceDirected is true, directedForcingMode can not be null
		if(toForceDirected && directedForcingMode==null) {
			throw new IllegalArgumentException("if toForceDirected is true, directedForcingMode can not be null!");
		}
		
		
		this.toForceDirected = toForceDirected;
		this.directedForcingMode = directedForcingMode;
		this.toForceUndirected = toForceUndirected;
		this.toForceNoParallelEdges = toForceNoParallelEdges;
		this.toForceNoSelfLoops = toForceNoSelfLoops;
	}
	
	public boolean isToForceDirected() {
		return toForceDirected;
	}
	
	public DirectedEnforcingMode getDirectedForcingMode() {
		return directedForcingMode;
	}

	public boolean isToForceUndirected() {
		return toForceUndirected;
	}

	public boolean isToForceNoParallelEdges() {
		return toForceNoParallelEdges;
	}

	public boolean isToForceNoSelfLoops() {
		return toForceNoSelfLoops;
	}
	
	public DirectedType getDirectedType() {
		if(this.toForceUndirected) {
			return DirectedType.UNDIRECTED;
		}else {
			if(this.directedForcingMode.equals(DirectedEnforcingMode.SIMPLE)) {
				return DirectedType.DIRECTED_FORWARD;
			}else if(this.directedForcingMode.equals(DirectedEnforcingMode.SIMPLE_REVERSE)) {
				return DirectedType.DIRECTED_BACKWARD;
			}else if(this.directedForcingMode.equals(DirectedEnforcingMode.BI_DIRECTION)) {
				return DirectedType.BI_DIRECTED;
			}else {
				throw new UnsupportedOperationException("TODO");
			}
		}
	}
	
	/**
	 * reproduce and return a new GraphTypeEnforcer based on this one;
	 * 
	 */
	@Override
	public GraphTypeEnforcer reproduce() {
//		boolean toForceDirected,
//		DirectedEnforcingMode directedForcingMode,
//		boolean toForceUndirected,
//		boolean toForceNoParallelEdges,
//		boolean toForceNoSelfLoops
		return new GraphTypeEnforcer(
				this.isToForceDirected(),
				this.getDirectedForcingMode(),
				this.isToForceDirected(),
				this.isToForceNoParallelEdges(),
				this.isToForceNoSelfLoops()
				);
	}
	
	@Override
	public String toString() {
		return "GraphTypeEnforcer [toForceDirected=" + toForceDirected + ", directedForcingMode=" + directedForcingMode
				+ ", toForceUndirected=" + toForceUndirected + ", toForceNoParallelEdges=" + toForceNoParallelEdges
				+ ", toForceNoSelfLoops=" + toForceNoSelfLoops + "]";
	}

	
	///////////////////////

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((directedForcingMode == null) ? 0 : directedForcingMode.hashCode());
		result = prime * result + (toForceDirected ? 1231 : 1237);
		result = prime * result + (toForceNoParallelEdges ? 1231 : 1237);
		result = prime * result + (toForceNoSelfLoops ? 1231 : 1237);
		result = prime * result + (toForceUndirected ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof GraphTypeEnforcer))
			return false;
		GraphTypeEnforcer other = (GraphTypeEnforcer) obj;
		if (directedForcingMode != other.directedForcingMode)
			return false;
		if (toForceDirected != other.toForceDirected)
			return false;
		if (toForceNoParallelEdges != other.toForceNoParallelEdges)
			return false;
		if (toForceNoSelfLoops != other.toForceNoSelfLoops)
			return false;
		if (toForceUndirected != other.toForceUndirected)
			return false;
		return true;
	}


	/**
	 * how to realize enforcing directed edges
	 * @author tanxu
	 *
	 */
	public static enum DirectedEnforcingMode{
		SIMPLE("simply treat undirected edge as directed with direction from source node to sink node"),//simply treat undirected edge as directed with direction from source node to sink node
		SIMPLE_REVERSE("simply treat undirected edge as directed with direction from sink node to source node"),//simply treat undirected edge as directed with direction from sink node to source node
		BI_DIRECTION("add an edge with the same additional feature values with opposite direction");//add an edge with the same additional feature values with opposite direction
		
		private final String description;
		
		DirectedEnforcingMode(String description){
			
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
		
	}

}
