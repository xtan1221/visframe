/**
 * 
 */
package generic.graph.builder.JUNG;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import generic.graph.reader.project.GenericGraphMetadataDataReader;
import generic.graph.reader.project.GenericGraphMetadataDataReaderTest;
import metadata.graph.type.CommonGraphTypeBoundaryFactory;
import metadata.graph.type.OperationInputGraphTypeBoundary;

/**
 * @author tanxu
 *
 */
public class JUNGGraphBuilderTest {
	public static JUNGGraphBuilder JUNGGraphBuilder;
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
	 * Test method for {@link generic.graph.builder.JUNG.JUNGGraphBuilder#JUNGGraphBuilder(generic.graph.reader.project.GenericGraphMetadataDataReader, metadata.graph.type.OperationInputGraphTypeBoundary)}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testJUNGGraphBuilder() throws SQLException, IOException {
		GenericGraphMetadataDataReaderTest genericGraphMetadataDataReaderTest = new GenericGraphMetadataDataReaderTest();
		genericGraphMetadataDataReaderTest.testInitializeFromVfTree();
		
		GenericGraphMetadataDataReader inputGraphMetadataDataReader = GenericGraphMetadataDataReaderTest.genericGraphMetadataDataReader;
		
		OperationInputGraphTypeBoundary graphTypeBoundary = CommonGraphTypeBoundaryFactory.anyGraph();
		
		JUNGGraphBuilder = new JUNGGraphBuilder(inputGraphMetadataDataReader, graphTypeBoundary);
	}
	
	
	/**
	 * Test method for {@link generic.graph.builder.JUNG.JUNGGraphBuilder#perform()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	public void testPerform() throws SQLException, IOException {
		testJUNGGraphBuilder();
		JUNGGraphBuilder.perform();
	}

	////////////////////////////
	/**
	 * forest graph
	 * Test method for {@link generic.graph.builder.JUNG.JUNGGraphBuilder#JUNGGraphBuilder(generic.graph.reader.project.GenericGraphMetadataDataReader, metadata.graph.type.OperationInputGraphTypeBoundary)}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testJUNGGraphBuilder2() throws SQLException, IOException {
		GenericGraphMetadataDataReaderTest genericGraphMetadataDataReaderTest = new GenericGraphMetadataDataReaderTest();
		genericGraphMetadataDataReaderTest.testInitializeFromVfTree();
		
		GenericGraphMetadataDataReader inputGraphMetadataDataReader = GenericGraphMetadataDataReaderTest.genericGraphMetadataDataReader;
		
		OperationInputGraphTypeBoundary graphTypeBoundary = CommonGraphTypeBoundaryFactory.anyForest();
		
		JUNGGraphBuilder = new JUNGGraphBuilder(inputGraphMetadataDataReader, graphTypeBoundary);
	}
	
	
	/**
	 * Test method for {@link generic.graph.builder.JUNG.JUNGGraphBuilder#perform()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	public void testPerform2() throws SQLException, IOException {
		testJUNGGraphBuilder2();
		JUNGGraphBuilder.perform();
	}

	
}
