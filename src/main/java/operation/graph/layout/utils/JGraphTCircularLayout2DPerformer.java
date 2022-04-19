package operation.graph.layout.utils;

import java.util.function.Function;

import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.CircularLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.Point2D;

import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;

/**
 * delegate to {@link CircularLayoutAlgorithm2D}
 * 
 * 
 * @author tanxu
 *
 */
public class JGraphTCircularLayout2DPerformer extends JGraphT2DLayoutAlgoPerformerBase {
	/**
	 * 
	 */
	private final double radius;
	
	
	//////////////////
	private CircularLayoutAlgorithm2D<VfGraphVertex, VfGraphEdge> circularLayoutAlgorithm2D;
	
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param inputGraphMetadataID
	 * @param height
	 * @param width
	 * @param initialLayoutFunction
	 * @param radius
	 */
	public JGraphTCircularLayout2DPerformer(
			int drawingAreaHeight, int drawingAreaWidth,
			Graph<VfGraphVertex, VfGraphEdge> targetGraph, Function<VfGraphVertex, Point2D> initialLayoutFunction,
			
			double radius) {
		super(drawingAreaHeight, drawingAreaWidth, targetGraph, initialLayoutFunction);//Function<VfGraphVertex, Point2D> initialLayoutFunction,
		if(radius<=0) {
			throw new IllegalArgumentException("given radius cannot be non-positive!");
		}
		this.radius = radius;
	}

	/**
	 * see {@link CircularLayoutAlgorithm2D} for details
	 */
	@Override
	public boolean isCartesianCoordSystem() {
		return true;
	}
	
	
	@Override
	public void initialize() {
		this.circularLayoutAlgorithm2D = new CircularLayoutAlgorithm2D<>(this.radius);
		
		if(this.getInitialLayoutFunction()!=null) {
			this.circularLayoutAlgorithm2D.setInitializer(this.getInitialLayoutFunction());
		}
		
		this.circularLayoutAlgorithm2D.layout(this.getTargetGraph(), this.getLayoutModel2D());
	}
	
	
}
