package dependency;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;



/**
 * upstream: node A is on the upstream of node B if and only if there is at least one directed path from A to B;
 * 			in CFD graph, a CF c1 is on upstream of another CF c2 if and only if c1 is depended (directly or indirectly) by c2
 * 			in DOS graph, a Metadata m1 is on upstream of m2 if and only if m2 is depended by m1
 * 			
 * 			!!!!!!!!thus, CFD and DOS graph dependency are opposite in terms of the flow direction of the underlying DAG;
 * 
 * downstream
 * 
 * 
 * 
 * @author tanxu
 *
 */
public class JGraphTDependencyGraphUtils {
	
	/**
	 * extract and return a subgraph with the given set of vertex from the given input graph;
	 * 
	 * note that this 
	 * @param <N>
	 * @param <E>
	 * @param inputGraph
	 * @param vertextSet
	 * @return
	 */
	public static <G extends Graph<N,E>, N,E> G subgraph(G inputGraph, Set<N> vertextSet){
		
		//TODO
		return null;
	}
	
	
	
	/**
	 * return the underlying SimpleGraph of the input graph, which is undirected, no self loop, no parallel edges;
	 * @param <N>
	 * @param <E>
	 * @param inputGraph
	 * @return
	 */
	public static <N,E> SimpleGraph<N,E> simpleUndirected(Graph<N,E> inputGraph){
		//TODO
		return null;
	}
	
	
	/**
	 * return whether the given SimpleDirectedGraph contains directed cycles or not;
	 * @param <N>
	 * @param <E>
	 * @param inputGraph
	 * @return
	 */
	public static <N,E> boolean containsCycle(SimpleDirectedGraph<N,E> inputGraph) {
		CycleDetector<N,E> cd = new CycleDetector<>(inputGraph);
		return cd.detectCycles();
	}
	
	/**
	 * extract all downstream connected (both directly and indirectly) (nodes on a directed path POINTING FROM the given node) nodes of the given nodes in the the given SimpleDirectedGraph;
	 * including the given nodes;
	 * 
	 * @param <N>
	 * @param <E>
	 * @param inputGraph
	 * @param node
	 * @return
	 */
	public static <N,E> Set<N> downstreamConnectedNodeSet(SimpleDirectedGraph<N,E> inputGraph, Set<N> node){
		//TODO
		
		return null;
	}
	
	/**
	 * extract all upstream connected (both directly and indirectly) (nodes on a directed path POINTING TO the given node) nodes of the given nodes in the the given SimpleDirectedGraph
	 * node A is on the upstream of node B if and only if there is at least one directed path from A to B;
	 * @param <N>
	 * @param <E>
	 * @param inputGraph
	 * @param node
	 * @return
	 */
	public static <N,E> Set<N> upstreamConnectedNodeSet(SimpleDirectedGraph<N,E> inputGraph, Set<N> node){
		//TODO
		
		return null;
	}
	
	
	/**
	 * extract the upstream incident nodes (nodes with a directed edge POINTING TO the given node) of the given node in the given SimpleDirectedGraph;
	 * node A is on the upstream of node B if and only if there is at least one directed path from A to B;
	 * @param <N>
	 * @param <E>
	 * @param inputGraph
	 * @param node
	 * @return
	 */
	public static <N,E> Set<N> upstreamIncidentNodeSet(SimpleDirectedGraph<N,E> inputGraph, N node){
		Set<N> ret = new HashSet<>();
		for(E e:inputGraph.incomingEdgesOf(node)) {
			ret.add(inputGraph.getEdgeSource(e));
		}
		return ret;
	}
	
	/**
	 * extract the downstream incident nodes(nodes with a directed edge POINTING FROM the given node) of the given node in the given SimpleDirectedGraph;
	 * @param <N>
	 * @param <E>
	 * @param inputGraph
	 * @param node
	 * @return
	 */
	public static <N,E> Set<N> downstreamIncidentNodeSet(SimpleDirectedGraph<N,E> inputGraph, N node){
		Set<N> ret = new HashSet<>();
		for(E e:inputGraph.outgoingEdgesOf(node)) {
			ret.add(inputGraph.getEdgeSource(e));
		}
		return ret;
	}
}
