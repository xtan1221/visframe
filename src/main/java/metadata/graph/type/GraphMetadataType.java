package metadata.graph.type;


/**
 * graph type for a GraphDataMetadata in rdb of a VisProjectDBContext;
 * 
 * the type is a specific unambiguous type of graph; different from OperationInputGraphType, which is a boundary of graph types
 * @author tanxu
 *
 */
public class GraphMetadataType extends GraphTypeBase{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8719185051577157454L;

	/**
	 * constructor
	 * @param containingDirectedEdgeOnly
	 * @param containingUndirectedEdgeOnly
	 * @param containingSelfLoop
	 * @param containingParallelEdges
	 * @param containingCycle null if unknown
	 * @param notConnected null if unknown
	 */
	public GraphMetadataType(
			boolean containingDirectedEdgeOnly, boolean containingUndirectedEdgeOnly,
			boolean containingSelfLoop, boolean containingParallelEdges, 
			Boolean containingCycle,
			Boolean notConnected) {
		super(containingDirectedEdgeOnly, containingUndirectedEdgeOnly, containingSelfLoop, containingParallelEdges,
				containingCycle, notConnected);
		// TODO validations
		//one of containingDirectedEdgeOnly and containingUndirectedEdgeOnly must be true, the other one false;
		
		
	}
	
	
	/**
	 * static factory method to create and return a new GraphMetadataType for a tree type
	 */
	public static GraphMetadataType makeTreeType(){
		return new GraphMetadataType(false, true, false, false, false, false);
	}
	
	
	
	//derivative features from the basic features
	Boolean isForest() {
		return this.isContainingUndirectedEdgeOnly()&&!this.isContainingCycle()&&!this.isContainingParallelEdges();
	}
	
	Boolean isTree() {
		return this.isForest() && !this.isNotConnected();
	}


	@Override
	public String toString() {
		return "GraphMetadataType [isContainingUndirectedEdgeOnly()=" + isContainingUndirectedEdgeOnly()
				+ ", isContainingDirectedEdgeOnly()=" + isContainingDirectedEdgeOnly() + ", isContainingSelfLoop()="
				+ isContainingSelfLoop() + ", isContainingParallelEdges()=" + isContainingParallelEdges()
				+ ", isContainingCycle()=" + isContainingCycle() + ", isNotConnected()=" + isNotConnected() + "]";
	}
	
	
	
}
