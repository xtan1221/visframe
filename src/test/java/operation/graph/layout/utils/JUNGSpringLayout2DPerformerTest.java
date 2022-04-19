/**
 * 
 */
package operation.graph.layout.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.uci.ics.jung.graph.Graph;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import generic.graph.builder.JUNG.JUNGGraphBuilderTest;
import operation.graph.layout.utils.GraphLayoutAlgoPerformerBase.Coord2D;
import utils.Pair;

/**
 * 
 * 
 * 
 * @author tanxu
 *
 */
class JUNGSpringLayout2DPerformerTest {
	public static JUNGSpringLayout2DPerformer JUNGSpringLayout2DPerformer;
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
	 * Test method for {@link operation.graph.layout.utils.JUNGSpringLayout2DPerformer#JUNGSpringLayout2DPerformer(int, int, edu.uci.ics.jung.graph.Graph, java.lang.Double, java.lang.Integer, java.lang.Double, java.lang.Integer)}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testJUNGSpringLayout2DPerformer() throws SQLException, IOException {
		int drawingAreaHeight = 100;
		int drawingAreaWidth = 100;
		
		JUNGGraphBuilderTest jUNGGraphBuilderTest = new JUNGGraphBuilderTest();
		jUNGGraphBuilderTest.testPerform();
		
		Graph<VfGraphVertex, VfGraphEdge> targetGraph = JUNGGraphBuilderTest.JUNGGraphBuilder.getUnderlyingGraph();
		
		Double forceMultiplier = null;
		Integer repulsionRange = 30;
		Double stretch = null;
		
		Integer iterations = 100;
		
		
		JUNGSpringLayout2DPerformer = new JUNGSpringLayout2DPerformer(
				drawingAreaHeight, drawingAreaWidth,
				targetGraph,
				
				forceMultiplier,
				repulsionRange,
				stretch,
				
				iterations);
		
		
	}

	/**
	 * Test method for {@link operation.graph.layout.utils.JUNGSpringLayout2DPerformer#initialize()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testInitialize() throws SQLException, IOException {
		testJUNGSpringLayout2DPerformer();
		JUNGSpringLayout2DPerformer.initialize();
	}

	/**
	 * Test method for {@link operation.graph.layout.utils.JUNGSpringLayout2DPerformer#nextVertexCoord()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testNextVertexCoord() throws SQLException, IOException {
		this.testInitialize();
		
		Pair<VfGraphVertex, Coord2D> calculatedCoord;
		
		while((calculatedCoord=JUNGSpringLayout2DPerformer.nextVertexCoord())!=null) {
			System.out.println("=========");
			System.out.println(calculatedCoord.getFirst());
			System.out.println(calculatedCoord.getSecond());
		}
	}

}
