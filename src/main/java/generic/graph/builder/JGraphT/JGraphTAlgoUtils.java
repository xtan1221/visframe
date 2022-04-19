package generic.graph.builder.JGraphT;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.cycle.CycleDetector;

/**
 * algorithm based utilities
 * @author tanxu
 *
 */
public class JGraphTAlgoUtils {
	
	/**
	 * for directed graph, check if the graph is weakly connected;
	 * 
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @return
	 */
	public static <V,E> boolean isConnected(Graph<V,E> graph) {
		ConnectivityInspector<V,E> inspector = new ConnectivityInspector<>(graph);
		
		return inspector.isConnected();
	}
	
	/**
	 * if given graph is undirected, return null since CycleDetector only support directed graph;
	 * 
	 * 
	 * to work around, create a simplified directed graph based on the undirected graph with only the node and edge;
	 * 
	 * @param <N>
	 * @param <E>
	 * @param inputGraph
	 * @return
	 */
	public static <N,E> Boolean containsCycle(Graph<N,E> inputGraph) {
		if(inputGraph.getType().isDirected()) {
			CycleDetector<N,E> cd = new CycleDetector<>(inputGraph);
			return cd.detectCycles();
		}else {
			return null;
		}
		
	}
}
