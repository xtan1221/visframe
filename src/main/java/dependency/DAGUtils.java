package dependency;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.SimpleDirectedGraph;

public class DAGUtils {

	
	/**
	 * extract the subgraph from the given DAG with all and only those (directly and indirectly) depended nodes of nodes in the given set as well as the given nodes;
	 * 
	 * note that this is different from the subgraph induced by the given set, which simply contains the given nodes and the edges between them from the given original DAG;
	 * 
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @param nodeSet
	 * @return
	 */
	public static <V,E> Graph<V,E> extractDependedSubGraphByNodeSet(SimpleDirectedGraph<V,E> dag, Class<E> edgeType, Set<V> nodeSet){
		//set of nodes with 
		Set<V> unprocessedDependedNodeSet = new HashSet<>();
		Set<V> processedNodeSet = new HashSet<>();
		nodeSet.forEach(n->{
			dag.outgoingEdgesOf(n).forEach(e->{
				V target = dag.getEdgeTarget(e);
				unprocessedDependedNodeSet.add(target);
			});
			
			if(unprocessedDependedNodeSet.contains(n))
				unprocessedDependedNodeSet.remove(n);
			processedNodeSet.add(n);
		});
		
		
		while(!unprocessedDependedNodeSet.isEmpty()) {
			V node = unprocessedDependedNodeSet.iterator().next();
			
			dag.outgoingEdgesOf(node).forEach(e->{
				V target = dag.getEdgeTarget(e);
				unprocessedDependedNodeSet.add(target);
			});
			
			
			unprocessedDependedNodeSet.remove(node);
			processedNodeSet.add(node);
		}
		
		AsSubgraph<V,E> asSubgraph = new AsSubgraph<>(dag, processedNodeSet);
		
		
		return asSubgraph;
		
	}
}
