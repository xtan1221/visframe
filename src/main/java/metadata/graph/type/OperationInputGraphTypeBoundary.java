package metadata.graph.type;

/**
 * required graph type boundary for input graph data;
 * 
 * if containingDirectedEdgeOnly and containingUndirectedEdgeOnly are both false, the operation can accept graph with any edge types including mixed type graph
 * @author tanxu
 *
 */
public class OperationInputGraphTypeBoundary extends GraphTypeBase{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1446850622258646987L;
	
	/**
	 * constructor
	 * @param containingDirectedEdgeOnly whether the operation only accepts graph with all edges being directed
	 * @param containingUndirectedEdgeOnly whether the operation only accepts graph with all edges being undirected
	 * @param containingSelfLoop whether the operation accept graphs with self loops
	 * @param containingParallelEdges whether the operation accept graphs with parallel edges
	 * @param containingCycle whether the operation accepts graphs with cycles;
	 * @param notConnected if true, the operation can accept graphs with multiple connected components(!?); if false, can only accept connected graph(for directed graph, weakly connected)
	 */
	public OperationInputGraphTypeBoundary(boolean containingDirectedEdgeOnly, boolean containingUndirectedEdgeOnly,
			boolean containingSelfLoop, boolean containingParallelEdges, boolean containingCycle,
			boolean notConnected) {
		super(containingDirectedEdgeOnly, containingUndirectedEdgeOnly, containingSelfLoop, containingParallelEdges,
				containingCycle, notConnected);
		// TODO validations
		//containingDirectedEdgeOnly and containingUndirectedEdgeOnly cannot both be true;
		//but can both be false, which represent mixed type graph
		
	}
	
	/**
	 * create and return a OperationInputGraphTypeBoundary by intersecting this OperationInputGraphTypeBoundary with the given OperationInputGraphTypeBoundary;
	 * 
	 * @param gt
	 * @return
	 */
	public OperationInputGraphTypeBoundary intersect(OperationInputGraphTypeBoundary gt) {
		boolean containingDirectedEdgeOnly = this.isContainingDirectedEdgeOnly()&&gt.isContainingDirectedEdgeOnly();//false if any one is false, true if both true
		boolean containingUndirectedEdgeOnly = this.isContainingUndirectedEdgeOnly()&&gt.isContainingUndirectedEdgeOnly();//false if any one is false, true if both true
		boolean containingSelfLoop = this.isContainingSelfLoop() && gt.isContainingSelfLoop();//false if any one is false, true if both true
		boolean containingParallelEdges = this.isContainingParallelEdges() && gt.isContainingParallelEdges();//false if any one is false, true if both true
		boolean containingCycle = this.isContainingCycle() && gt.isContainingCycle();//false if any one is false, true if both true
		boolean notConnected = this.isNotConnected() && gt.isNotConnected();//false if any one is false, true if both true!!
		
		return new OperationInputGraphTypeBoundary(
				containingDirectedEdgeOnly, containingUndirectedEdgeOnly,
				containingSelfLoop,
				containingParallelEdges,
				containingCycle,
				notConnected
				);
		
	}
	
	/**
	 * whether this graph type boundary is covering the given GraphMetadataType;
	 * returns null if unknown;
	 * @param gt
	 * @return
	 */
	public Boolean isEqualToOrMoreGenericThan(GraphMetadataType gt) {
		try { 
			return 
					directednessEqualToOrMoreGenericThan(gt) && 
					containingSelfLoopsEqualToOrMoreGenricThan(gt) &&
					containingParallelEdgesEqualToOrMoreGenricThan(gt) && 
					containingCyclesEqualToOrMoreGenricThan(gt) &&
					notConnectedEqualToOrMoreGenricThan(gt);
		}catch(NullPointerException e) {//if any of the method returns null, return null;
			return null;
		}
	}
	
	
	/**
	 * directed or undirected can be treated as mixed (more generic)
	 * @param gt
	 * @return
	 */
	boolean directednessEqualToOrMoreGenericThan(GraphMetadataType gt) {
//		if(this.containingBothDirectedAndDirectedEdge) {
//			return true;
//		}else 
		if(!this.isContainingDirectedEdgeOnly() && !this.isContainingUndirectedEdgeOnly()) {//both false, containing both
			return true;
		}else if(this.isContainingDirectedEdgeOnly()) {
			return gt.isContainingDirectedEdgeOnly();
		}else if(this.isContainingUndirectedEdgeOnly()) {
			return gt.isContainingUndirectedEdgeOnly();
		}else {
			return false;
		}
	}
	
	//not containing self loops can be treated as containing self loops (more generic)
	Boolean containingSelfLoopsEqualToOrMoreGenricThan(GraphMetadataType gt) {
//		if(this.isContainingSelfLoop()==null) {
//			return null;
//		}
		
		if(this.isContainingSelfLoop()) {
			return true;
		}else {
			return !gt.isContainingSelfLoop();
		}
	}
	
	//not containing parallel edges can be treated as containing parallel edges (more generic)
	Boolean containingParallelEdgesEqualToOrMoreGenricThan(GraphMetadataType gt) {
//		if(this.isContainingParallelEdges()==null) {
//			return null;
//		}
		
		if(this.isContainingParallelEdges()) {
			return true;
		}else {
			return !gt.isContainingParallelEdges();
		}
	}
	
	//not containing cycle can be treated as containing cycle (more generic)
	Boolean containingCyclesEqualToOrMoreGenricThan(GraphMetadataType gt) {
		if(this.isContainingCycle()==null) {
			return null;
		}
		
		if(this.isContainingCycle()) {
			return true;
		}else {
			return !gt.isContainingCycle();
		}
	}
	
	//connected can be treated as not connected (more generic)
	Boolean notConnectedEqualToOrMoreGenricThan(GraphMetadataType gt) {
		if(this.isNotConnected()==null) {
			return null;
		}
		
		if(this.isNotConnected()) {
			return true;
		}else {
			return !gt.isNotConnected();
		}
	}
	
}
