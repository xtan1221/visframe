package operation.graph.layout.utils;

import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import utils.Pair;


/**
 * 
 * @author tanxu
 * 
 */
public class JUNGSpringLayout2DPerformer extends JUNG2DLayoutAlgoPerformerBase{
	public static final double DEFAULT_FORCE_MULTIPLIER = 1/3;
	public static final int DEFAULT_REPULSION_RANGE = 100;
	public static final double DEFAULT_STRETCH = 0.70;
	
	public static final int DEFAULT_ITERATIONS = 700;
	
	/**
	 * the force multiplier for this instance. 
	 * This value is used to specify how strongly an edge "wants" to be its default length (higher values indicate a greater attraction for the default length), 
	 * which affects how much its endpoints move at each timestep. 
	 * The default value is 1/3. 
	 * A value of 0 turns off any attempt by the layout to cause edges to conform to the default length. 
	 * Negative values cause long edges to get longer and short edges to get shorter; use at your own risk.
	 */
	private final double forceMultiplier;
	
	/**
	 * the node repulsion range (in drawing area units) for this instance. 
	 * Outside this range, nodes do not repel each other. 
	 * The default value is 100. Negative values are treated as their positive equivalents
	 */
	private final int repulsionRange;
	
	/**
	 * the stretch parameter for this instance. 
	 * This value specifies how much the degrees of an edge's incident vertices should influence how easily the endpoints of that edge can move (that is, that edge's tendency to change its length).
	 * The default value is 0.70. 
	 * Positive values less than 1 cause high-degree vertices to move less than low-degree vertices, and values > 1 cause high-degree vertices to move more than low-degree vertices. 
	 * Negative values will have unpredictable and inconsistent results.
	 */
	private final double stretch;
	
	
	/**
	 * number of times the {@link SpringLayout#step()} method will be invoked;
	 */
	private final int iterations;
	
	
	/////////////////////////
	private SpringLayout<VfGraphVertex, VfGraphEdge> springLayout;
	
	/**
	 * constructor
	 * @param drawingAreaHeight
	 * @param drawingAreaWidth
	 * @param targetGraph
	 * @param forceMultiplier
	 * @param repulsionRange
	 * @param stretch
	 */
	public JUNGSpringLayout2DPerformer(
			int drawingAreaHeight, int drawingAreaWidth,
			Graph<VfGraphVertex, VfGraphEdge> targetGraph,
			
			Double forceMultiplier,
			Integer repulsionRange,
			Double stretch,
			
			Integer iterations
			) {
		super(drawingAreaHeight, drawingAreaWidth, targetGraph);
		// TODO Auto-generated constructor stub
		
		this.forceMultiplier = forceMultiplier==null?DEFAULT_FORCE_MULTIPLIER:forceMultiplier;
		this.repulsionRange = repulsionRange==null?DEFAULT_REPULSION_RANGE:repulsionRange;
		this.stretch = stretch==null?DEFAULT_STRETCH:stretch;
		
		this.iterations = iterations==null?DEFAULT_ITERATIONS:iterations;
	}

	@Override
	public void initialize() {
		this.springLayout = new SpringLayout<>(this.getTargetGraph());
		
		this.springLayout.setForceMultiplier(this.forceMultiplier);
		this.springLayout.setRepulsionRange(this.repulsionRange);
		this.springLayout.setStretch(this.stretch);
		
		this.springLayout.setSize(this.getSize());
		
		
		this.springLayout.initialize();
		
		//SpringLayout.done() never returns true; thus need to explicitly set number of steps
		int i=0;
		while(i<this.iterations) {
			this.springLayout.step();
			i++;
//			System.out.println(i);
		}
	}

	@Override
	public boolean isCartesianCoordSystem() {
		return true;
	}
	
	@Override
	public Pair<VfGraphVertex, Coord2D> nextVertexCoord() {
		if(this.vertexIterator==null) {
			this.vertexIterator = this.getTargetGraph().getVertices().iterator();
		}
		
		while(this.vertexIterator.hasNext()) {
			VfGraphVertex next = this.vertexIterator.next();
			
			Coord2D coord = new Coord2D(
					this.springLayout.getX(next),
					this.springLayout.getY(next)
					);
			return new Pair<>(next, coord);
		}
		
		return null;
	}

}
