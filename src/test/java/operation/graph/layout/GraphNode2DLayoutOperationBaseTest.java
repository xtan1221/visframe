/**
 * 
 */
package operation.graph.layout;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import metadata.MetadataName;

/**
 * @author tanxu
 *
 */
public class GraphNode2DLayoutOperationBaseTest {
	public static Map<SimpleName, Object> graphNode2DLayoutOperationBaseLevelSpecificParameterNameValueObjectMap;
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
	 * Test method for {@link operation.graph.layout.GraphNode2DLayoutOperationBase#buildGraphNode2DLayoutOperationBaseLevelSpecificParameterNameValueObjectMap(double, double, metadata.MetadataName)}.
	 */
	@Test
	public void testBuildGraphNode2DLayoutOperationBaseLevelSpecificParameterNameValueObjectMap() {
		int drawingAreaWidth = 100;
		int drawingAreaHeight = 100;
		MetadataName outputLayoutRecordDataName = new MetadataName("spring_layout_test");
		
		graphNode2DLayoutOperationBaseLevelSpecificParameterNameValueObjectMap = 
				GraphNode2DLayoutOperationBase.buildGraphNode2DLayoutOperationBaseLevelSpecificParameterNameValueObjectMap(
						drawingAreaWidth, drawingAreaHeight, outputLayoutRecordDataName);
		
	}

}
