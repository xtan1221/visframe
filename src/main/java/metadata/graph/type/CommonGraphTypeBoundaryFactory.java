package metadata.graph.type;

/**
 * factory class for commonly used graph types for graph algorithm 
 * @author tanxu
 *
 */
public class CommonGraphTypeBoundaryFactory {
	public static OperationInputGraphTypeBoundary anyGraph() {
		return new OperationInputGraphTypeBoundary(false, false, true, true, true, true);
	}
	
	public static OperationInputGraphTypeBoundary anyDirected() {
		return new OperationInputGraphTypeBoundary(true, false, true, true, true, true);
	}
	
	public static OperationInputGraphTypeBoundary anyDAG() {
		return new OperationInputGraphTypeBoundary(true, false, false, false, false, true);
	}
	
	
	public static OperationInputGraphTypeBoundary anyUndirected() {
		return new OperationInputGraphTypeBoundary(false, true, true, true, true, true);
	}
	

	//undirected, acyclic graph with no loop and parallel edges
	public static OperationInputGraphTypeBoundary anyForest() {
		return new OperationInputGraphTypeBoundary(false, true, false, false, false, true);
	}
	
	//connected, undirected, acyclic and no loop and parallel edges
	public static OperationInputGraphTypeBoundary anyTree() {
		return new OperationInputGraphTypeBoundary(false, true, false, false, false, false);
	}
	
}
