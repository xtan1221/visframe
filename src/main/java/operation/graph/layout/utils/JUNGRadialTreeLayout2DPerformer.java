package operation.graph.layout.utils;

import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.graph.Forest;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import utils.Pair;

/**
 * input graph should be Forest in JUNG, which are a collection of rooted DIRECTED acyclic graphs.
 * 
 * @author tanxu
 *
 */
public class JUNGRadialTreeLayout2DPerformer extends JUNG2DLayoutAlgoPerformerBase{
	private final int distx;
	private final int disty;
	
	////////////////////////////
	private RadialTreeLayout<VfGraphVertex, VfGraphEdge> radialTreeLayout;
	
	/**
	 * constructor
	 * @param targetForest
	 * @param distx
	 * @param disty
	 */
	JUNGRadialTreeLayout2DPerformer(
			Forest<VfGraphVertex, VfGraphEdge> targetForest,
			
			int distx,
			int disty
			) {
		super(1,1, targetForest);//int drawingAreaHeight, int drawingAreaWidth are not applicable for this layout;
		// TODO Auto-generated constructor stub
		
		this.distx = distx;
		this.disty = disty;
		
	}
	
	@Override
	public void initialize() {
		this.radialTreeLayout = new RadialTreeLayout<>(
				(Forest<VfGraphVertex, VfGraphEdge>)this.getTargetGraph(), 
				this.distx, 
				this.disty);
		this.radialTreeLayout.initialize();
	}
	
	/**
	 * polar coordinate
	 */
	@Override
	public boolean isCartesianCoordSystem() {
		return false;
	}

	@Override
	public Pair<VfGraphVertex, Coord2D> nextVertexCoord() {
		if(this.vertexIterator==null) {
			this.vertexIterator = this.getTargetGraph().getVertices().iterator();
		}
		
		while(this.vertexIterator.hasNext()) {
			VfGraphVertex next = this.vertexIterator.next();
			
			Coord2D coord = new Coord2D(
					this.radialTreeLayout.getPolarLocations().get(next).getRadius(),
					this.radialTreeLayout.getPolarLocations().get(next).getTheta()
					);
			return new Pair<>(next, coord);
		}
		
		return null;
	}
	
}
