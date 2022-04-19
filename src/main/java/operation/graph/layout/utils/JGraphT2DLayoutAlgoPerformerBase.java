package operation.graph.layout.utils;

import java.util.Iterator;
import java.util.function.Function;

import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.MapLayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;

import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import utils.Pair;

/**
 * base class for performing a Jgrapht 2D layout algorithm;
 * 
 * 
 * note that Function<VfGraphVertex,Point2D> for initial location of each vertex is not mandatory;
 * 		if no initializer is provide, random position will be generated for each vertex as the initial position in the corresponding LayoutAlgorithm2D class
 * 
 * @author tanxu
 *
 */
public abstract class JGraphT2DLayoutAlgoPerformerBase extends GraphLayoutAlgoPerformerBase<VfGraphVertex>{
	/**
	 * the target graph of compatible type with the algorithm;
	 * note that this class assumes that the given graph's type is valid;
	 */
	private final Graph<VfGraphVertex, VfGraphEdge> targetGraph;
	private final Function<VfGraphVertex,Point2D> initialLayoutFunction;
	
	//////////////////
	/**
	 * the layout model that contains the calculated coordinate for each vertex;
	 * 
	 */
	private LayoutModel2D<VfGraphVertex> layoutModel2D;
	/**
	 * facilitate iterating throw each vertex to retrieve the calculated coordinate from the {@link #layoutModel2D}
	 */
	protected Iterator<VfGraphVertex> vertexIterator;
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param inputGraphMetadataID
	 * @param height
	 * @param width
	 */
	JGraphT2DLayoutAlgoPerformerBase(
			int drawingAreaHeight, int drawingAreaWidth,
			
			Graph<VfGraphVertex, VfGraphEdge> targetGraph,
			Function<VfGraphVertex,Point2D> initialLayoutFunction) {
		
		super(drawingAreaHeight, drawingAreaWidth);

		this.targetGraph = targetGraph;
		this.initialLayoutFunction = initialLayoutFunction;
	}
	
	protected Graph<VfGraphVertex, VfGraphEdge> getTargetGraph() {
		return targetGraph;
	}

	protected Function<VfGraphVertex,Point2D> getInitialLayoutFunction() {
		return initialLayoutFunction;
	}

	
	/**
	 * build and return the MapLayoutModel2D for the layout algorithm;
	 * @return
	 */
	protected LayoutModel2D<VfGraphVertex> getLayoutModel2D() {
		if(this.layoutModel2D == null) {
			this.layoutModel2D = new MapLayoutModel2D<>(new Box2D(this.getDrawingAreaWidth(), this.getDrawingAreaHeight()));
		}
		
		return this.layoutModel2D;
	}
	
	//////////////////////////////////////////////////////////
	
	/**
	 * 1. create the underlying algorithm class instance
	 * 
	 * 2. perform the algorithm
	 */
	@Override
	public abstract void initialize();
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Pair<VfGraphVertex, Coord2D> nextVertexCoord() {
		if(this.vertexIterator==null) {
			this.vertexIterator = this.getTargetGraph().vertexSet().iterator();
		}
		
		while(this.vertexIterator.hasNext()) {
			VfGraphVertex next = this.vertexIterator.next();
			
			Coord2D coord = new Coord2D(
					this.getLayoutModel2D().get(next).getX(), 
					this.getLayoutModel2D().get(next).getY());
			return new Pair<>(next, coord);
		}
		
		return null;
	}

}
