/**
 * 
 */
package generic.graph.populator;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import context.project.VisProjectDBContext;
import context.project.VisProjectDBContextTest;
import generic.graph.builder.GraphBuilder;
import generic.graph.builder.JGraphT.JGraphTGraphBuilderTest;
import rdb.table.data.DataTableName;

/**
 * @author tanxu
 *
 */
class GraphDataTablePopulatorImplTest {
	public static GraphDataTablePopulatorImpl populator;
	public static VisProjectDBContext hostVisProjectDBContext;
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
	 * Test method for {@link generic.graph.populator.GraphDataTablePopulatorImpl#GraphDataTablePopulatorImpl(context.project.VisProjectDBContext, generic.graph.builder.GraphBuilder, rdb.table.data.DataTableName, rdb.table.data.DataTableName)}.
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Test
	void testGraphDataTablePopulatorImpl() throws SQLException, IOException {
		VisProjectDBContextTest visProjectDBContextTest = new VisProjectDBContextTest();
		visProjectDBContextTest.testConnect();
		JGraphTGraphBuilderTest jGraphTGraphBuilderTest = new JGraphTGraphBuilderTest();
		jGraphTGraphBuilderTest.testPerform();
		
		hostVisProjectDBContext = VisProjectDBContextTest.TEST_PROJECT_5;
		GraphBuilder inputGraphBuilder = JGraphTGraphBuilderTest.jGraphTGraphBuilder;
		DataTableName vertexDataTableName = new DataTableName("test_graph_vertex10");
		DataTableName edgeDataTableName = new DataTableName("test_graph_edge10");
		
		
		populator = new GraphDataTablePopulatorImpl(hostVisProjectDBContext,
				inputGraphBuilder,
				vertexDataTableName,
				edgeDataTableName
				);
		
//		hostVisProjectDBContext.disconnect();
	}

	/**
	 * Test method for {@link generic.graph.populator.GraphDataTablePopulatorImpl#perform()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testPerform() throws SQLException, IOException {
		testGraphDataTablePopulatorImpl();
		
		
		populator.perform();
		
		
		hostVisProjectDBContext.disconnect();
	}

}
