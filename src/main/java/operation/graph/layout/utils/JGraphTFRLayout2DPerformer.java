package operation.graph.layout.utils;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;

import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;

/**
 * 
 * delegate to {@link FRLayoutAlgorithm2D}
 * 
 * note that based on testing result, when input graph's vertex size is large, large iteration number will result in NaN(java's constant for NOT A NUMBER) coordinate;
 * 		for example for graph based on mono1 tree, when iteration = 10, valid coordinates will be calculated;
 * 		but when iteration > 40, all vertex will not have calculated coordinates (all N/A);
 * 		however, for small graph (mono6813), when iteration is quite large (>1000000), valid coordinate will still be produced;
 * 
 * @author tanxu
 *
 */
public class JGraphTFRLayout2DPerformer extends JGraphT2DLayoutAlgoPerformerBase {
	public static final int DEFAULT_ITERATIONS = FRLayoutAlgorithm2D.DEFAULT_ITERATIONS;//100
	public static final double DEFAULT_NORMALIZATION_FACTOR = FRLayoutAlgorithm2D.DEFAULT_NORMALIZATION_FACTOR;//0.5
	
	
	///////////////////////
	private final int iterations;
	private final double normalizationFactor;
	/**
	 * if temperatureModelSupplier is not provided, the FRLayoutAlgorithm2D will create a default one;
	 */
	private final BiFunction<LayoutModel2D<VfGraphVertex>, Integer, FRLayoutAlgorithm2D.TemperatureModel> temperatureModelSupplier;
	private final Random rng;
	
	///////////////////////////////
	private FRLayoutAlgorithm2D<VfGraphVertex, VfGraphEdge> fRLayoutAlgorithm2D;
	
	/**
	 * full constructor
	 * @param drawingAreaHeight
	 * @param drawingAreaWidth
	 * @param targetGraph
	 * @param initialLayoutFunction
	 * @param iterations
	 * @param normalizationFactor
	 * @param temperatureModelSupplier can be null; a default one will be created automatically; see {@link FRLayoutAlgorithm2D#FRLayoutAlgorithm2D(int, double, Random)}
	 * @param rng can be null; a new Random will be generated automatically;  see {@link FRLayoutAlgorithm2D#FRLayoutAlgorithm2D(int, double)}
	 */
	JGraphTFRLayout2DPerformer(
			int drawingAreaHeight, int drawingAreaWidth,
			Graph<VfGraphVertex, VfGraphEdge> targetGraph, Function<VfGraphVertex, Point2D> initialLayoutFunction,
			
			Integer iterations,Double normalizationFactor,
			BiFunction<LayoutModel2D<VfGraphVertex>, Integer, FRLayoutAlgorithm2D.TemperatureModel> temperatureModelSupplier,
			Random rng
			) {
		super(drawingAreaHeight, drawingAreaWidth, targetGraph, initialLayoutFunction);
		// TODO Auto-generated constructor stub
		
		this.iterations = iterations==null?DEFAULT_ITERATIONS:iterations;
		this.normalizationFactor = normalizationFactor==null?DEFAULT_NORMALIZATION_FACTOR:normalizationFactor;
		this.temperatureModelSupplier = temperatureModelSupplier;
		this.rng = rng;
	}
	
	@Override
	public boolean isCartesianCoordSystem() {
		return true;
	}

	
	@Override
	public void initialize() {
		if(this.temperatureModelSupplier==null) {
			if(this.rng==null) {
				this.fRLayoutAlgorithm2D = new FRLayoutAlgorithm2D<>(this.iterations, this.normalizationFactor);
			}else {
				this.fRLayoutAlgorithm2D = new FRLayoutAlgorithm2D<>(this.iterations, this.normalizationFactor, this.rng);
			}
		}else {
			if(this.rng==null) {
				this.fRLayoutAlgorithm2D = new FRLayoutAlgorithm2D<>(this.iterations, this.normalizationFactor, this.temperatureModelSupplier, new Random());
			}else {
				this.fRLayoutAlgorithm2D = new FRLayoutAlgorithm2D<>(this.iterations, this.normalizationFactor, this.temperatureModelSupplier, this.rng);
			}
		}
		
		if(this.getInitialLayoutFunction()!=null) {
			this.fRLayoutAlgorithm2D.setInitializer(this.getInitialLayoutFunction());
		}
		
//		this.fRLayoutAlgorithm2D = new FRLayoutAlgorithm2D<>(1000000);
		this.fRLayoutAlgorithm2D.layout(this.getTargetGraph(), this.getLayoutModel2D());
	}
	
}
