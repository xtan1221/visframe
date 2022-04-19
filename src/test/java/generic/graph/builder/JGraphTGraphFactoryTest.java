/**
 * 
 */
package generic.graph.builder;

import static org.junit.jupiter.api.Assertions.*;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import generic.graph.builder.JGraphT.JGraphTGraphFactory;
import metadata.graph.type.GraphTypeEnforcer;
import metadata.graph.type.GraphTypeEnforcer.DirectedEnforcingMode;

/**
 * @author tanxu
 *
 */
class JGraphTGraphFactoryTest {

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
	 * Test method for {@link generic.graph.builder.JGraphT.JGraphTGraphFactory#initializeGraph(metadata.graph.type.GraphTypeEnforcer, java.lang.Class, java.lang.Class)}.
	 */
	@Test
	void testInitializeGraphGraphTypeEnforcerClassOfVClassOfE() {
//		boolean toForceDirected,
//		DirectedEnforcingMode directedForcingMode,
//		boolean toForceUndirected,
//		boolean toForceNoParallelEdges,
//		boolean toForceNoSelfLoops
		GraphTypeEnforcer enforcer = new GraphTypeEnforcer(false, null, true, true, true);
		
		
		Graph<Integer,Integer> graph = JGraphTGraphFactory.initializeGraph(enforcer, Integer.class, Integer.class);
		
		graph.addVertex(1);
		graph.addVertex(2);
		graph.addVertex(3);
		graph.addVertex(4);
		graph.addVertex(5);
		
		//add edges with the same pair of nodes
		assertEquals(true, graph.addEdge(1, 2, 1));
		assertEquals(false, graph.addEdge(2, 1, 1));
		
		
		//test parallel edges
		//adding parallel edges to graph not allowing parallel edges will simply ignore the added edge and return false;
		assertEquals(true, graph.addEdge(3, 4, 4));
		assertEquals(false, graph.addEdge(3, 4, 7));
		
		//test self loop
		//adding self loop edge to graph not allowing loop will cause IllegalArgumentException thrown!
		try {
			assertEquals(false, graph.addEdge(3, 3, 2));
		}catch(IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println(graph);
	}

}
