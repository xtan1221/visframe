package dependency.layout;

import java.util.function.Function;

import org.jgrapht.graph.SimpleDirectedGraph;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import utils.Pair;

public class JGraphT2JUNGUtils {
	
	/**
	 * build and return a JUNG DirectedSparseGraph from a given JGraphT SimpleDirectedGraph
	 * @param <V>
	 * @param <E>
	 * @param sdg
	 * @return
	 */
	public static <V,E> DirectedSparseGraph<V,E> makeDirectedSparseGraph(SimpleDirectedGraph<V,E> simpleDirectedGraph, Function<E, Pair<V,V>> edgeSourceSinkNodePairFunction){
		DirectedSparseGraph<V,E> JUNGGraph = new DirectedSparseGraph<>();
		
		simpleDirectedGraph.vertexSet().forEach(v->{
			JUNGGraph.addVertex(v);
		});
		
		
		simpleDirectedGraph.edgeSet().forEach(e->{
			JUNGGraph.addEdge(
					e, 
					edgeSourceSinkNodePairFunction.apply(e).getFirst(), 
					edgeSourceSinkNodePairFunction.apply(e).getSecond());
		});
		
		return JUNGGraph;
	}
	
}
