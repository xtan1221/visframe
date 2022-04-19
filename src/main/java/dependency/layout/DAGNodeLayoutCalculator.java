package dependency.layout;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import dependency.DAG;
import utils.Pair;

/**
 * calculate layout coordinate for each node of a {@link DAG} graph on a canvass with height and width equal to 100;
 * 
 * the underlying implementation is to use JUNG DAGLayout class to calculate coordinates of each node;
 * 
 * note that the DAGLayout class is a subtype of SpringLayout;
 * 
 * 
 * @author tanxu
 * 
 * @param <V>
 * @param <E>
 */
public class DAGNodeLayoutCalculator<V,E> {
	public static final int HEIGHT = 100;
	public static final int WIDTH = 100;
	public static final int ITERATION = 100;
	///////////////////////////////
	private final DAG<V,E> dag;
	
	/**
	 * function from graph directed edge to the pair of incident nodes (first element is the source node, second element is the sink node);
	 */
	private final Function<E, Pair<V,V>> edgeSourceSinkNodePairFunction;
	
	
	/////////////
	private Map<V, Pair<Double, Double>> nodeCalculatedPosMap;
	
	
	/**
	 * constructor
	 * @param dag
	 */
	public DAGNodeLayoutCalculator(DAG<V,E> dag,
			Function<E, Pair<V,V>> edgeSourceSinkNodePairFunction){
		
		this.dag = dag;
		this.edgeSourceSinkNodePairFunction = edgeSourceSinkNodePairFunction;
		
		
		this.calculate();
	}
	
	
	private void calculate() {
		JUNGDAGLayoutPerformer<V,E> performer = 
				new JUNGDAGLayoutPerformer<>(
						JGraphT2JUNGUtils.makeDirectedSparseGraph(this.dag.getUnderlyingGraph(), edgeSourceSinkNodePairFunction),
						new Dimension(HEIGHT, WIDTH),
						ITERATION
						);
		///
		this.nodeCalculatedPosMap = new HashMap<>();
		dag.getUnderlyingGraph().vertexSet().forEach(v->{
			this.nodeCalculatedPosMap.put(v, performer.getNodePos(v));
		});
	}
	
	
	
	public Map<V, Pair<Double, Double>> getNodeCalculatedPosMap() {
		return nodeCalculatedPosMap;
	}
	
}
