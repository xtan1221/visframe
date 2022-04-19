package dependency;

import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

public class SimpleDirectedGraphUtils {
	
	/**
	 * build and return the subgraph induced by the given vertex set;
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @param inducingVertexSet
	 * @param edgeType
	 * @return
	 */
	public static <V,E> SimpleDirectedGraph<V,E> subgraph(SimpleDirectedGraph<V,E> graph, Set<V> inducingVertexSet, Class<E> edgeType){
		SimpleDirectedGraph<V,E> subgraph = new SimpleDirectedGraph<>(edgeType);
		//first add the inducing vertex set
		inducingVertexSet.forEach(v->{
			subgraph.addVertex(v);
		});
		
		//add edges between the vertex set;
		graph.edgeSet().forEach(e->{
			V source = graph.getEdgeSource(e);
			V target = graph.getEdgeTarget(e);
			
			if(inducingVertexSet.contains(source) && inducingVertexSet.contains(target)) {
				subgraph.addEdge(source, target, e);
			}
		});
		
		return subgraph;
	}
	
	/**
	 * 
	 * @param <V>
	 * @param <E>
	 * @param originalGraph
	 * @param edgeType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <V,E> SimpleDirectedGraph<V,E> clone(SimpleDirectedGraph<V,E> originalGraph, Class<E> edgeType){
		return (SimpleDirectedGraph<V,E>)originalGraph.clone();
	}
}
