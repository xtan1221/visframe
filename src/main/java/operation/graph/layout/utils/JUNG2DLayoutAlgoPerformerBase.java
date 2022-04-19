package operation.graph.layout.utils;


import java.awt.Dimension;
import java.util.Iterator;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;



/**
 * this class is corresponding to the {@link AbstractLayout} of JUNG api;
 * 
 * parameters
 * 1. org.apache.commons.collections15.Transformer<V,Point2D> initializer
 * 		optional;
 * 		note that package of org.apache.commons.collections15.Transformer is not found; thus skip it in current version;
 * 2. Dimension size
 * 
 * @author tanxu
 *
 */
public abstract class JUNG2DLayoutAlgoPerformerBase extends GraphLayoutAlgoPerformerBase<VfGraphVertex>{
	/**
	 * the target graph of compatible type with the algorithm;
	 * note that this class assumes that the given graph's type is valid;
	 */
	private final Graph<VfGraphVertex, VfGraphEdge> targetGraph;
	
	
	/**
	 * facilitate iterating throw each vertex to retrieve the calculated coordinate from the {@link #layoutModel2D}
	 */
	protected Iterator<VfGraphVertex> vertexIterator;
	
	/**
	 * constructor
	 * @param drawingAreaHeight
	 * @param drawingAreaWidth
	 * @param targetGraph
	 */
	JUNG2DLayoutAlgoPerformerBase(
			int drawingAreaHeight, int drawingAreaWidth,
			Graph<VfGraphVertex, VfGraphEdge> targetGraph) {
		super(drawingAreaHeight, drawingAreaWidth);
		// TODO Auto-generated constructor stub
		
		this.targetGraph = targetGraph;
	}
	

	protected Graph<VfGraphVertex, VfGraphEdge> getTargetGraph() {
		return targetGraph;
	}
	
	/**
	 * return the drawing area size;
	 * @return
	 */
	protected Dimension getSize() {
		return new Dimension(this.getDrawingAreaWidth(), this.getDrawingAreaHeight());
	}
	
	/**
	 * 1. create the underlying algorithm class instance
	 * 
	 * 2. perform the algorithm
	 */
	@Override
	public abstract void initialize();
	
	
	
	

}
