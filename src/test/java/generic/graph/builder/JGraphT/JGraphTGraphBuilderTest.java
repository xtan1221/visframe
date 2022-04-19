/**
 * 
 */
package generic.graph.builder.JGraphT;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import generic.graph.reader.filebased.SimpleGEXFFileReaderTest;
import metadata.graph.type.GraphTypeEnforcer;
import metadata.graph.type.GraphTypeEnforcer.DirectedEnforcingMode;

/**
 * test for building a graph imported from a graph file;
 * @author tanxu
 *
 */
public class JGraphTGraphBuilderTest {
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
	 * Test method for {@link generic.graph.builder.JGraphT.JGraphTGraphBuilder#JGraphTGraphBuilder(generic.graph.GraphIterator, metadata.graph.type.GraphTypeEnforcer)}.
	 * @throws IOException 
	 */
	@Test
	public void testJGraphTGraphBuilder() throws IOException {
		SimpleGEXFFileReaderTest simpleGEXFFileReaderTest = new SimpleGEXFFileReaderTest();
		simpleGEXFFileReaderTest.testSimpleGEXFFileReader();
//		boolean toForceDirected,
//		DirectedEnforcingMode directedForcingMode,
//		boolean toForceUndirected,
//		boolean toForceNoParallelEdges,
//		boolean toForceNoSelfLoops
		GraphTypeEnforcer enforcer1 = new GraphTypeEnforcer(false, null, true, true, true);
		GraphTypeEnforcer enforcer2 = new GraphTypeEnforcer(true, DirectedEnforcingMode.SIMPLE, false, true, true); //simple directed
		GraphTypeEnforcer enforcer3 = new GraphTypeEnforcer(true, DirectedEnforcingMode.SIMPLE_REVERSE, false, true, true); //simple directed
		GraphTypeEnforcer enforcer4 = new GraphTypeEnforcer(true, DirectedEnforcingMode.SIMPLE, false, false, true); //simple directed
		GraphTypeEnforcer enforcer5 = new GraphTypeEnforcer(true, DirectedEnforcingMode.SIMPLE, false, false, false); //simple directed
		
		
//		jGraphTGraphBuilder = new JGraphTGraphBuilder(SimpleGEXFFileReaderTest.reader, enforcer1);
//		jGraphTGraphBuilder = new JGraphTGraphBuilder(SimpleGEXFFileReaderTest.reader, enforcer2);
//		jGraphTGraphBuilder = new JGraphTGraphBuilder(SimpleGEXFFileReaderTest.reader, enforcer3);
		
		
//		jGraphTGraphBuilder = new JGraphTGraphBuilder(SimpleGEXFFileReaderTest.reader2, enforcer1);
//		jGraphTGraphBuilder = new JGraphTGraphBuilder(SimpleGEXFFileReaderTest.reader2, enforcer2);
//		jGraphTGraphBuilder = new JGraphTGraphBuilder(SimpleGEXFFileReaderTest.reader2, enforcer3);
//		jGraphTGraphBuilder = new JGraphTGraphBuilder(SimpleGEXFFileReaderTest.reader2, enforcer4);
//		jGraphTGraphBuilder = new JGraphTGraphBuilder(SimpleGEXFFileReaderTest.reader2, enforcer5, true);
	}
	
	/**
	 * Test method for {@link generic.graph.builder.JGraphT.JGraphTGraphBuilder#perform()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	public void testPerform() throws IOException, SQLException {
		this.testJGraphTGraphBuilder();
		
		jGraphTGraphBuilder.perform();
		
		System.out.println();
	}
	
	/**
	 * Test method for {@link generic.graph.builder.JGraphT.JGraphTGraphBuilder#getOberservedType()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testGetOberservedType() throws IOException, SQLException {
		this.testPerform();
		
		System.out.println(jGraphTGraphBuilder.getGraphTypeEnforcer());
		System.out.println(jGraphTGraphBuilder.getOberservedType());
	}
	
	/**
	 * Test method for {@link generic.graph.builder.JGraphT.JGraphTGraphBuilder#nextVertex()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testNextVertex() throws IOException, SQLException {
		this.testGetOberservedType();
		VfGraphVertex vertex;
		while((vertex=jGraphTGraphBuilder.nextVertex())!=null) {
			System.out.println(vertex);
		}
		
		System.out.println("done");
	}
	
	/**
	 * Test method for {@link generic.graph.builder.JGraphT.JGraphTGraphBuilder#nextEdge()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testNextEdge() throws IOException, SQLException {
		testNextVertex();
		
		VfGraphEdge edge;
		while((edge=jGraphTGraphBuilder.nextEdge())!=null) {
			System.out.println(edge);
		}
		
		System.out.println("done");
	}
	
	/**
	 * Test method for {@link generic.graph.builder.JGraphT.JGraphTGraphBuilder#restart()}.
	 */
	@Test
	void testRestart() {
		fail("Not yet implemented");
	}

}
