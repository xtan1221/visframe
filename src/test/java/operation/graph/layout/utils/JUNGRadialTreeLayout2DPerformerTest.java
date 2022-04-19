/**
 * 
 */
package operation.graph.layout.utils;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.uci.ics.jung.graph.Forest;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import generic.graph.builder.JUNG.JUNGGraphBuilderTest;
import operation.graph.layout.utils.GraphLayoutAlgoPerformerBase.Coord2D;
import utils.Pair;

/**
 * 
 * @author tanxu
 *
 */
class JUNGRadialTreeLayout2DPerformerTest {
	public static JUNGRadialTreeLayout2DPerformer JUNGRadialTreeLayout2DPerformer;
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
	 * Test method for {@link operation.graph.layout.utils.JUNGRadialTreeLayout2DPerformer#JUNGRadialTreeLayout2DPerformer(edu.uci.ics.jung.graph.Forest, int, int)}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testJUNGRadialTreeLayout2DPerformer() throws SQLException, IOException {
		
		JUNGGraphBuilderTest jUNGGraphBuilderTest = new JUNGGraphBuilderTest();
		jUNGGraphBuilderTest.testPerform2();
		
		Forest<VfGraphVertex, VfGraphEdge> targetGraph = (Forest<VfGraphVertex, VfGraphEdge>) JUNGGraphBuilderTest.JUNGGraphBuilder.getUnderlyingGraph();
		
		int distx = 100;
		int disty = 100;
		
		JUNGRadialTreeLayout2DPerformer = new JUNGRadialTreeLayout2DPerformer(targetGraph, distx, disty);
		
	}
	
	
	/**
	 * Test method for {@link operation.graph.layout.utils.JUNGRadialTreeLayout2DPerformer#initialize()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testInitialize() throws SQLException, IOException {
		testJUNGRadialTreeLayout2DPerformer();
		JUNGRadialTreeLayout2DPerformer.initialize();
	}

	/**
	 * Test method for {@link operation.graph.layout.utils.JUNGRadialTreeLayout2DPerformer#nextVertexCoord()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testNextVertexCoord() throws SQLException, IOException {
		this.testInitialize();
		
		Pair<VfGraphVertex, Coord2D> calculatedCoord;
		
		while((calculatedCoord=JUNGRadialTreeLayout2DPerformer.nextVertexCoord())!=null) {
			System.out.println("=========");
			System.out.println(calculatedCoord.getFirst());
			System.out.println(calculatedCoord.getSecond());
		}
	}


}
