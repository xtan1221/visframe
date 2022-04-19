/**
 * 
 */
package generic.graph.builder.JGraphT;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import generic.graph.GraphIterator;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import generic.graph.reader.project.RecordToGraphReaderImplTest;
import metadata.graph.type.GraphTypeEnforcer;
import metadata.graph.type.GraphTypeEnforcer.DirectedEnforcingMode;

/**
 * test for build a graph from a RecordToGraphReader
 * 
 * @author tanxu
 *
 */
public class JGraphTGraphBuilderTest2 {
	public static JGraphTGraphBuilder jGraphTGraphBuilder;
	
	
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
	 * force a undirected graph
	 * Test method for {@link generic.graph.builder.JGraphT.JGraphTGraphBuilder#JGraphTGraphBuilder(generic.graph.GraphIterator, metadata.graph.type.GraphTypeEnforcer, boolean)}.
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Test
	void testJGraphTGraphBuilder() throws SQLException, IOException {
		RecordToGraphReaderImplTest recordToGraphReaderImplTest = new RecordToGraphReaderImplTest();
		recordToGraphReaderImplTest.testInitialize();
		
		
		GraphIterator inputGraphIterator = RecordToGraphReaderImplTest.recordToGraphReaderImpl;
		GraphTypeEnforcer graphTypeEnforcer = new GraphTypeEnforcer(
				false,//boolean toForceDirected,
				null,//DirectedEnforcingMode directedForcingMode,
				true,//boolean toForceUndirected,
				true,//boolean toForceNoParallelEdges,
				true//boolean toForceNoSelfLoops
				);
		boolean toAddDiscoveredVertexFromInputEdgeDataTable = false;
		
		
		jGraphTGraphBuilder = new JGraphTGraphBuilder(
				inputGraphIterator, 
				graphTypeEnforcer, 
				toAddDiscoveredVertexFromInputEdgeDataTable);
		
	}
	
	/**
	 * Test method for {@link generic.graph.builder.JGraphT.JGraphTGraphBuilder#perform()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	public void testPerform() throws SQLException, IOException {
		testJGraphTGraphBuilder();
		
		jGraphTGraphBuilder.perform();
		
	}
	
	
	/**
	 * force a directed graph
	 * Test method for {@link generic.graph.builder.JGraphT.JGraphTGraphBuilder#JGraphTGraphBuilder(generic.graph.GraphIterator, metadata.graph.type.GraphTypeEnforcer, boolean)}.
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Test
	void testJGraphTGraphBuilder2() throws SQLException, IOException {
		RecordToGraphReaderImplTest recordToGraphReaderImplTest = new RecordToGraphReaderImplTest();
		recordToGraphReaderImplTest.testInitialize();
		
		
		GraphIterator inputGraphIterator = RecordToGraphReaderImplTest.recordToGraphReaderImpl;
		GraphTypeEnforcer graphTypeEnforcer = new GraphTypeEnforcer(
				true,//boolean toForceDirected,
				DirectedEnforcingMode.SIMPLE,//DirectedEnforcingMode directedForcingMode,
				false,//boolean toForceUndirected,
				true,//boolean toForceNoParallelEdges,
				true//boolean toForceNoSelfLoops
				);
		boolean toAddDiscoveredVertexFromInputEdgeDataTable = false;
		
		
		jGraphTGraphBuilder = new JGraphTGraphBuilder(
				inputGraphIterator, 
				graphTypeEnforcer, 
				toAddDiscoveredVertexFromInputEdgeDataTable);
		
	}
	
	/**
	 * Test method for {@link generic.graph.builder.JGraphT.JGraphTGraphBuilder#perform()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	public void testPerform2() throws SQLException, IOException {
		testJGraphTGraphBuilder2();
		
		jGraphTGraphBuilder.perform();
		
	}
	
	
	/**
	 * Test method for {@link generic.graph.builder.JGraphT.JGraphTGraphBuilder#getOberservedType()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testGetOberservedType() throws SQLException, IOException {
		testPerform();
		
		System.out.println(jGraphTGraphBuilder.getOberservedType());
	}
	
	/**
	 * Test method for {@link generic.graph.builder.JGraphT.JGraphTGraphBuilder#nextVertex()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testNextVertex() throws SQLException, IOException {
		testPerform();
		
		VfGraphVertex vertex;
		while((vertex=jGraphTGraphBuilder.nextVertex())!=null) {
			System.out.println(vertex.toString());
		}
	}
	
	/**
	 * Test method for {@link generic.graph.builder.JGraphT.JGraphTGraphBuilder#nextEdge()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testNextEdge() throws SQLException, IOException {
		testPerform();
		
		VfGraphEdge edge;
		while((edge=jGraphTGraphBuilder.nextEdge())!=null) {
			System.out.println(edge.simpleString());
		}
	}
	
}
