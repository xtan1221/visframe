package operation.graph.layout.utils;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import utils.Pair;


/**
 * performer class for {@link FRLayout} in JUNG api;
 * 
 * 
 * note that the meaning of attractionMultiplier and repulsion is not checked as well as their domain;
 * 
 * 
 * target graph can be of any type? 
 * @author tanxu
 */
public class JUNGFRLayout2DPerformer extends JUNG2DLayoutAlgoPerformerBase{
	//default values defined in {@link FRLayout}
	public static final double DEFAULT_ATTRACTION_MULTIPLIER = 0.75;
	public static final double DEFAULT_REPULSION = 0.75;
	public static final int DEFAULT_MAX_ITERATION = 700;
	
	
	/////////////
	/**
	 * attraction multiplier: how much edges try to keep their vertices together
	 */
	private final Double attractionMultiplier;
	/**
	 * repulsion multiplier: how much vertices try to push each other apart
	 */
	private final Double repulsion;
	/**
	 * maximum iterations: how many iterations this algorithm will use before stopping
	 */
	private final Integer maxIterations;
	
	////////////////////
	private FRLayout<VfGraphVertex, VfGraphEdge> fRLayout;
	
	/**
	 * constructor
	 * @param drawingAreaHeight
	 * @param drawingAreaWidth
	 * @param targetGraph
	 * @param attractionMultiplier
	 * @param repulsion
	 * @param maxIterations
	 */
	public JUNGFRLayout2DPerformer(
			int drawingAreaHeight, int drawingAreaWidth,
			Graph<VfGraphVertex, VfGraphEdge> targetGraph,
			
			Double attractionMultiplier,
			Double repulsion,
			Integer maxIterations
			) {
		super(drawingAreaHeight, drawingAreaWidth, targetGraph);
		// TODO Auto-generated constructor stub
		
		
		this.attractionMultiplier = attractionMultiplier==null?DEFAULT_ATTRACTION_MULTIPLIER:attractionMultiplier;
		this.repulsion = repulsion==null?DEFAULT_REPULSION:repulsion;
		this.maxIterations = maxIterations==null?DEFAULT_MAX_ITERATION:maxIterations;
	}

	
	@Override
	public boolean isCartesianCoordSystem() {
		return true;
	}
	
	@Override
	public void initialize() {
		this.fRLayout = new FRLayout<>(this.getTargetGraph(), this.getSize());
		
		this.fRLayout.setAttractionMultiplier(this.attractionMultiplier);
		this.fRLayout.setRepulsionMultiplier(this.repulsion);
		this.fRLayout.setMaxIterations(this.maxIterations);
		
		//Initializes fields in the node that may not have been set during the constructor??
		this.fRLayout.initialize();
		
		//
		while(!this.fRLayout.done()) {//Returns true once the current iteration has passed the maximum count, MAX_ITERATIONS
			this.fRLayout.step();
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Pair<VfGraphVertex, Coord2D> nextVertexCoord() {
		if(this.vertexIterator==null) {
			this.vertexIterator = this.getTargetGraph().getVertices().iterator();
		}
		
		while(this.vertexIterator.hasNext()) {
			VfGraphVertex next = this.vertexIterator.next();
			
			Coord2D coord = new Coord2D(
					this.fRLayout.getX(next),
					this.fRLayout.getY(next)
					);
			return new Pair<>(next, coord);
		}
		
		return null;
	}

}
