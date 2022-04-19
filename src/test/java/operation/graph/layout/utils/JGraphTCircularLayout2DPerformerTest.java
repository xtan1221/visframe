/**
 * 
 */
package operation.graph.layout.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.function.Function;

import org.jgrapht.Graph;
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
class JGraphTCircularLayout2DPerformerTest {
	public static JGraphTCircularLayout2DPerformer jGraphTCircularLayout2D;
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
	 * Test method for {@link operation.graph.layout.utils.JGraphTCircularLayout2DPerformer#JGraphTCircularLayout2D(double, double, org.jgrapht.Graph, double)}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testJGraphTCircularLayout2DPerformer() throws SQLException, IOException {
		int drawingAreaHeight = 100;
		int drawingAreaWidth = 100;
		
		JGraphTGraphBuilderTest2 jGraphTGraphBuilderTest2 = new JGraphTGraphBuilderTest2();
		jGraphTGraphBuilderTest2.testPerform();
		
		Graph<VfGraphVertex, VfGraphEdge> targetGraph = JGraphTGraphBuilderTest2.jGraphTGraphBuilder.getUnderlyingGraph();
		
		double radius = 40;
		
		jGraphTCircularLayout2D = new JGraphTCircularLayout2DPerformer(
				drawingAreaHeight, drawingAreaWidth, 
				targetGraph, null, //Function<VfGraphVertex, Point2D> initialLayoutFunction
				radius);
		
	}


	/**
	 * Test method for {@link operation.graph.layout.utils.JGraphTCircularLayout2DPerformer#initialize()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testInitialize() throws SQLException, IOException {
		this.testJGraphTCircularLayout2DPerformer();
		
		jGraphTCircularLayout2D.initialize();
		
	}
	
	
	/**
	 * Test method for {@link operation.graph.layout.utils.JGraphTCircularLayout2DPerformer#nextVertexCoord()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testNextVertexCoord() throws SQLException, IOException {
		this.testInitialize();
		
		Pair<VfGraphVertex, Coord2D> calculatedCoord;
		
		while((calculatedCoord=jGraphTCircularLayout2D.nextVertexCoord())!=null) {
			System.out.println("=========");
			System.out.println(calculatedCoord.getFirst());
			System.out.println(calculatedCoord.getSecond());
		}
		
	}

}
