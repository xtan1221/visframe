package metadata.graph.type;

import java.io.Serializable;

/**
 * either observed type for an existing GraphMetadata in a VisframeContext or 
 * a description of an expected GraphMetadata input for an operation
 * @author tanxu
 *
 */
public class GraphTypeBase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7616814443138502491L;
	
	//features directly set by the generator of the GraphDataMetadata;
	//such as DataImporter or graph based Operation;
	//cannot be discovered from data content because edge isDirected column is not included in the edge data table;
	//GraphMetadataType and OperationInputGraphType have different constraints on containingDirectedEdgeOnly and containingUndirectedEdgeOnly
	private final boolean containingDirectedEdgeOnly;
	private final boolean containingUndirectedEdgeOnly;
	
	////////////////
	//basic features discovered from the generated graph
	private final boolean containingSelfLoop;
	private final boolean containingParallelEdges;
	private final Boolean containingCycle;
	private final Boolean notConnected;
	
	/**
	 * constructor
	 * @param containingDirectedEdgeOnly
	 * @param containingUndirectedEdgeOnly
	 * @param containingSelfLoop
	 * @param containingParallelEdges
	 * @param containingCycle
	 * @param notConnected
	 */
	public GraphTypeBase(
			boolean containingDirectedEdgeOnly,
			boolean containingUndirectedEdgeOnly,
			boolean containingSelfLoop,
			boolean containingParallelEdges,
			Boolean containingCycle,
			Boolean notConnected
			) {
		//TODO validations
		//containingDirectedEdgeOnly and containingUndirectedEdgeOnly cannot both be true;
		
		//if containingSelfLoop is true, containingCycle must be true;
		
		this.containingDirectedEdgeOnly = containingDirectedEdgeOnly;
		this.containingUndirectedEdgeOnly = containingUndirectedEdgeOnly;
		this.containingSelfLoop = containingSelfLoop;
		this.containingParallelEdges = containingParallelEdges;
		this.containingCycle = containingCycle;
		this.notConnected = notConnected;
	}
	
	public boolean isContainingUndirectedEdgeOnly() {
		return containingUndirectedEdgeOnly;
	}

	public boolean isContainingDirectedEdgeOnly() {
		return containingDirectedEdgeOnly;
	}
	public boolean isContainingSelfLoop() {
		return containingSelfLoop;
	}

	public boolean isContainingParallelEdges() {
		return containingParallelEdges;
	}


	public Boolean isContainingCycle() {
		return containingCycle;
	}

	public Boolean isNotConnected() {
		return notConnected;
	}


	///////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((containingCycle == null) ? 0 : containingCycle.hashCode());
		result = prime * result + (containingDirectedEdgeOnly ? 1231 : 1237);
		result = prime * result + (containingParallelEdges ? 1231 : 1237);
		result = prime * result + (containingSelfLoop ? 1231 : 1237);
		result = prime * result + (containingUndirectedEdgeOnly ? 1231 : 1237);
		result = prime * result + ((notConnected == null) ? 0 : notConnected.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof GraphTypeBase))
			return false;
		GraphTypeBase other = (GraphTypeBase) obj;
		if (containingCycle == null) {
			if (other.containingCycle != null)
				return false;
		} else if (!containingCycle.equals(other.containingCycle))
			return false;
		if (containingDirectedEdgeOnly != other.containingDirectedEdgeOnly)
			return false;
		if (containingParallelEdges != other.containingParallelEdges)
			return false;
		if (containingSelfLoop != other.containingSelfLoop)
			return false;
		if (containingUndirectedEdgeOnly != other.containingUndirectedEdgeOnly)
			return false;
		if (notConnected == null) {
			if (other.notConnected != null)
				return false;
		} else if (!notConnected.equals(other.notConnected))
			return false;
		return true;
	}
	
}
