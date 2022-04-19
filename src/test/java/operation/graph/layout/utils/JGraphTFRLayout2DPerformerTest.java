/**
 * 
 */
package operation.graph.layout.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import generic.graph.builder.JGraphT.JGraphTGraphBuilderTest2;
import operation.graph.layout.utils.GraphLayoutAlgoPerformerBase.Coord2D;
import utils.Pair;

/**
 * @author tanxu
 *
 */
class JGraphTFRLayout2DPerformerTest {
	public static JGraphTFRLayout2DPerformer jGraphTFRLayout2DPerformer;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link operation.graph.layout.utils.JGraphTFRLayout2DPerformer#JGraphTFRLayout2DPerformer(double, double, org.jgrapht.Graph, java.util.function.Function, java.lang.Integer, java.lang.Double, java.util.function.BiFunction, java.util.Random)}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testJGraphTFRLayout2DPerformer() throws SQLException, IOException {
		int drawingAreaHeight = 100;
		int drawingAreaWidth = 100;
		
		JGraphTGraphBuilderTest2 jGraphTGraphBuilderTest2 = new JGraphTGraphBuilderTest2();
		jGraphTGraphBuilderTest2.testPerform2();
		
		Graph<VfGraphVertex, VfGraphEdge> targetGraph = JGraphTGraphBuilderTest2.jGraphTGraphBuilder.getUnderlyingGraph();
		
		Function<VfGraphVertex, Point2D> initialLayoutFunction = null;
		
		Integer iterations = 100;
		Double normalizationFactor = null;
		
		BiFunction<LayoutModel2D<VfGraphVertex>, Integer, FRLayoutAlgorithm2D.TemperatureModel> temperatureModelSupplier = null;
		
		Random rng = null;
		
		jGraphTFRLayout2DPerformer = new JGraphTFRLayout2DPerformer(
				drawingAreaHeight, drawingAreaWidth,
				targetGraph, initialLayoutFunction,
				
				iterations,normalizationFactor,
				temperatureModelSupplier,
				rng);
	}
	
	
	/**
	 * Test method for {@link operation.graph.layout.utils.JGraphTFRLayout2DPerformer#initialize()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testInitialize() throws SQLException, IOException {
		testJGraphTFRLayout2DPerformer();
		jGraphTFRLayout2DPerformer.initialize();
	}

	/**
	 * Test method for {@link operation.graph.layout.utils.JGraphTFRLayout2DPerformer#nextVertexCoord()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testNextVertexCoord() throws SQLException, IOException {
		this.testInitialize();
		
		Pair<VfGraphVertex, Coord2D> calculatedCoord;
		
		while((calculatedCoord=jGraphTFRLayout2DPerformer.nextVertexCoord())!=null) {
			System.out.println("=========");
			System.out.println(calculatedCoord.getFirst());
			System.out.println(calculatedCoord.getSecond());
		}
		
	}
}
