package generic.graph.builder.JGraphT;

import org.jgrapht.Graph;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import metadata.graph.type.GraphTypeEnforcer;
import metadata.graph.type.OperationInputGraphTypeBoundary;

/**
 * initialize an empty JGraphT Graph<V,E> object with visframe defined graph types
 * @author tanxu
 *
 */
public class JGraphTGraphFactory {
	/**
	 * initialize and returns an empty JGraphT Graph<V,E> object with given GraphTypeEnforcer;
	 * 
	 * note that for graph create by this method , when adding edges, the edge object must be explicitly given:
	 * 		use the method {@link Graph#addEdge(Object, Object, Object)} instead of {@link Graph#addEdge(Object, Object)};
	 * 
	 * see {@link GraphTypeBuilder} for details;
	 * 
	 * @param <V>
	 * @param <E>
	 * @param typeEnforcer
	 * @param vertexTypeClass
	 * @param edgeTypeClass
	 * @return
	 */
	public static <V,E> Graph<V,E> initializeGraph(GraphTypeEnforcer typeEnforcer, Class<V> vertexTypeClass, Class<E> edgeTypeClass){
		GraphTypeBuilder<V, E> graphTypeBuilder;
		if(typeEnforcer.isToForceDirected()) {
			graphTypeBuilder = GraphTypeBuilder.<V,E> directed();
		}else if(typeEnforcer.isToForceUndirected()) {
			graphTypeBuilder = GraphTypeBuilder.<V,E> undirected();
		}else {//
//			graphTypeBuilder = GraphTypeBuilder.<V,E> mixed();
			throw new UnsupportedOperationException("mixed type graph is not supported!");
		}
		
		graphTypeBuilder = graphTypeBuilder.allowingMultipleEdges(!typeEnforcer.isToForceNoParallelEdges());
		graphTypeBuilder = graphTypeBuilder.allowingSelfLoops(!typeEnforcer.isToForceNoSelfLoops());
		
		return graphTypeBuilder.buildGraph();
	}
	
	/**
	 * initialize and returns an empty JGraphT Graph<V,E> object with given OperationInputGraphTypeBoundary;
	 * @param <V>
	 * @param <E>
	 * @param graphTypeBoundary
	 * @param vertexTypeClass
	 * @param edgeTypeClass
	 * @return
	 */
	public static <V,E> Graph<V,E> initializeGraph(OperationInputGraphTypeBoundary graphTypeBoundary, Class<V> vertexTypeClass, Class<E> edgeTypeClass){
		GraphTypeBuilder<V, E> graphTypeBuilder;
		if(graphTypeBoundary.isContainingDirectedEdgeOnly()) {
			graphTypeBuilder = GraphTypeBuilder.<V,E> directed();
		}else if(graphTypeBoundary.isContainingUndirectedEdgeOnly()) {
			graphTypeBuilder = GraphTypeBuilder.<V,E> undirected();
		}else {//
			graphTypeBuilder = GraphTypeBuilder.<V,E> mixed();
		}
		
		graphTypeBuilder = graphTypeBuilder.allowingMultipleEdges(graphTypeBoundary.isContainingParallelEdges());
		graphTypeBuilder = graphTypeBuilder.allowingSelfLoops(graphTypeBoundary.isContainingSelfLoop());
		
		return graphTypeBuilder.buildGraph();
	}
}
