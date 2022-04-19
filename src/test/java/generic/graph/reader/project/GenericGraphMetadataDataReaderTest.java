/**
 * 
 */
package generic.graph.reader.project;

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
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import metadata.DataType;
import metadata.MetadataID;
import metadata.MetadataName;

/**
 * @author tanxu
 *
 */
public class GenericGraphMetadataDataReaderTest {
	public static GenericGraphMetadataDataReader genericGraphMetadataDataReader;
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
	 * Test method for {@link generic.graph.reader.project.GenericGraphMetadataDataReader#GenericGraphMetadataDataReader(context.project.VisProjectDBContext, metadata.MetadataID)}.
	 * @throws SQLException 
	 */
	@Test
	void testGenericGraphMetadataDataReaderFromGraph() throws SQLException {
		VisProjectDBContextTest visProjectDBContextTest = new VisProjectDBContextTest();
		visProjectDBContextTest.testConnect();
		
		MetadataID genericGraphDataMetadataID = new MetadataID(new MetadataName("graph_built_from_mono50"), DataType.GRAPH);
		
		genericGraphMetadataDataReader = new GenericGraphMetadataDataReader(VisProjectDBContextTest.TEST_PROJECT_1, genericGraphDataMetadataID);
		
	}
	
	
	
	/**
	 * Test method for {@link generic.graph.reader.project.GenericGraphMetadataDataReader#initialize()}.
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Test
	public void testInitializeFromGraph() throws SQLException, IOException {
		
		testGenericGraphMetadataDataReaderFromGraph();
		
		genericGraphMetadataDataReader.initialize();
		
		VfGraphVertex vertex;
		while((vertex=genericGraphMetadataDataReader.nextVertex())!=null) {
			System.out.println(vertex);
		}
		VfGraphEdge edge;
		while((edge=genericGraphMetadataDataReader.nextEdge())!=null) {
			System.out.println(edge.simpleString());
		}
	}
	
	
	////////////////////////
	/**
	 * Test method for {@link generic.graph.reader.project.GenericGraphMetadataDataReader#GenericGraphMetadataDataReader(context.project.VisProjectDBContext, metadata.MetadataID)}.
	 * @throws SQLException 
	 */
	@Test
	void testGenericGraphMetadataDataReaderFromVfTree() throws SQLException {
		VisProjectDBContextTest visProjectDBContextTest = new VisProjectDBContextTest();
		visProjectDBContextTest.testConnect();
		
//		MetadataID genericGraphDataMetadataID = new MetadataID(new MetadataName("mono_6813_3"), DataType.vfTREE);
		MetadataID genericGraphDataMetadataID = new MetadataID(new MetadataName("mono_1_2"), DataType.vfTREE);
		
		genericGraphMetadataDataReader = new GenericGraphMetadataDataReader(VisProjectDBContextTest.TEST_PROJECT_1, genericGraphDataMetadataID);
		
	}
	
	/**
	 * Test method for {@link generic.graph.reader.project.GenericGraphMetadataDataReader#initialize()}.
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Test
	public void testInitializeFromVfTree() throws SQLException, IOException {
		
		testGenericGraphMetadataDataReaderFromVfTree();
		
		genericGraphMetadataDataReader.initialize();
		
//		VfGraphVertex vertex;
//		while((vertex=genericGraphMetadataDataReader.nextVertex())!=null) {
//			System.out.println(vertex);
//		}
//		VfGraphEdge edge;
//		while((edge=genericGraphMetadataDataReader.nextEdge())!=null) {
//			System.out.println(edge.simpleString());
//		}
	}

}
