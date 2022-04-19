package dependency.layout;

import java.awt.Dimension;

import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import utils.Pair;


/**
 * perform a DAGLayout in JUNG api;
 * 
 * 
 * @author tanxu
 *
 * @param <V>
 * @param <E>
 */
public class JUNGDAGLayoutPerformer<V,E> {
	private final DirectedSparseGraph<V,E> directedSparseGraph;
	private final Dimension size;
	private final int iteration;
	
	//////////////////////
	private DAGLayout<V,E> dagLayout;
	
	/**
	 * 
	 * @param directedSparseGraph
	 * @param size
	 * @param iteration
	 */
	JUNGDAGLayoutPerformer(DirectedSparseGraph<V,E> directedSparseGraph,
			Dimension size,
			int iteration
			){
		this.directedSparseGraph = directedSparseGraph;
		this.size = size;
		this.iteration = iteration;
		
		
		this.dagLayout = new DAGLayout<>(this.directedSparseGraph);
		this.dagLayout.setSize(this.size);
		
		int i=0;
		while(i<this.iteration) {
			this.dagLayout.step();
			i++;
		}
	}
	
	
	public Pair<Double, Double> getNodePos(V node){
		return new Pair<>(this.dagLayout.getX(node), this.dagLayout.getY(node));
	}
}
