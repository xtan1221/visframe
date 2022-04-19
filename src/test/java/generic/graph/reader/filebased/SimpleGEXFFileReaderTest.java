/**
 * 
 */
package generic.graph.reader.filebased;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;

/**
 * @author tanxu
 *
 */
public class SimpleGEXFFileReaderTest {
//	public static Path testFilePath = Paths.get("C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\graph\\gexf\\simple.gexf");
	public static Path testFilePath2 = Paths.get("C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\graph\\gexf\\simple2_mutual.gexf");
	public static SimpleGEXFFileReader reader;
	public static SimpleGEXFFileReader reader2;
	
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

	
	///////////////////////////////
	/**
	 * Test method for {@link generic.graph.reader.filebased.SimpleGEXFFileReader#SimpleGEXFFileReader(java.nio.file.Path)}.
	 * @throws IOException 
	 */
	@Test
	public void testSimpleGEXFFileReader() throws IOException {
		
//		reader = new SimpleGEXFFileReader(testFilePath);
		reader2 = new SimpleGEXFFileReader(testFilePath2);
	}
	
	/**
	 * Test method for {@link generic.graph.reader.filebased.SimpleGEXFFileReader#nextVertex()}.
	 * @throws IOException 
	 */
	@Test
	void testNextVertex() throws IOException {
		
		testSimpleGEXFFileReader();
		
		VfGraphVertex vertex;
		while((vertex=reader2.nextVertex())!=null) {
			System.out.println(vertex);
		}
		
		System.out.println("done");
	}
	
	/**
	 * Test method for {@link generic.graph.reader.filebased.SimpleGEXFFileReader#nextEdge()}.
	 * @throws IOException 
	 */
	@Test
	void testNextEdge() throws IOException {
		testNextVertex();
		
		VfGraphEdge edge;
		while((edge=reader2.nextEdge())!=null) {
			System.out.println(edge);
		}
		
		System.out.println("done");
	}
	
	/**
	 * Test method for {@link generic.graph.reader.filebased.SimpleGEXFFileReader#restart()}.
	 * @throws IOException 
	 */
	@Test
	void testRestart() throws IOException {
		testNextEdge();
		
		reader.restart();
		
		VfGraphVertex vertex;
		while((vertex=reader.nextVertex())!=null) {
			System.out.println(vertex);
		}
		
		System.out.println("vertex done");
		
		VfGraphEdge edge;
		while((edge=reader.nextEdge())!=null) {
			System.out.println(edge);
		}
		
		System.out.println("edge done");
	}
	
}
