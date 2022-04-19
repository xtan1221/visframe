package generic.graph.builder.JUNG;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import metadata.graph.type.CommonGraphTypeBoundaryFactory;
import metadata.graph.type.OperationInputGraphTypeBoundary;

/**
 * initialize empty JUNG Graph<V,E> object for JUNG algorithm class;
 * 
 * JUNG does not have an implicit GraphTypeBuilder as JGraphT, but defines a set of specific Graph classes;
 * 
 * note that in JUNG, forest is graph which consists of a collection of rooted directed acyclic graphs.
 * 
 * JUNG.
 * 
 * @author tanxu
 *
 */
public class JUNGGraphFactory {
	
	/**
	 * initialize and returns an empty JUNG Graph<V,E> object with given OperationInputGraphTypeBoundary;
	 * 
	 * at least the currently included JUNG algorithms' required input graph types should be included in this method;
	 * 
	 * @param <V>
	 * @param <E>
	 * @param graphTypeBoundary
	 * @param vertexTypeClass
	 * @param edgeTypeClass
	 * @return
	 */
	public static <V,E> Graph<V,E> initializeGraph(OperationInputGraphTypeBoundary graphTypeBoundary, Class<V> vertexTypeClass, Class<E> edgeTypeClass){
		if(graphTypeBoundary.equals(CommonGraphTypeBoundaryFactory.anyGraph())) {
			return new SparseMultigraph<V,E>(); //SpringLayout
		}else if(graphTypeBoundary.equals(CommonGraphTypeBoundaryFactory.anyDAG())) {
			return new DirectedSparseGraph<V,E>();//DAGLayout
//		}else if(graphTypeBoundary.equals(CommonGraphTypeBoundaryFactory.anyForest())) {
//			return new DirectedSparseGraph<V,E>();//DAGLayout
		}else {//if 
			throw new UnsupportedOperationException("unsupported graph type for JUNG graph");
		}
		
	}
}
