/**
 * 
 */
package operation.graph.layout.JUNG;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import operation.AbstractOperationTest;
import operation.graph.InputGraphTypeBoundedOperationTest;
import operation.graph.SingleGenericGraphAsInputOperationTest;
import operation.graph.layout.GraphNode2DLayoutOperationBaseTest;
import operation.graph.layout.utils.JUNGSpringLayout2DPerformer;

/**
 * @author tanxu
 *
 */
public class SpringLayout2DOperationTest {
	public static Map<SimpleName, Object> springLayout2DOperationLevelSpecificParameterNameValueObjectMap;
	public static SpringLayout2DOperation springLayout2DOperation;
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
	 * Test method for {@link operation.graph.layout.JUNG.SpringLayout2DOperation#buildSpringLayout2DOperationLevelSpecificParameterNameValueObjectMap(double, int, double, int)}.
	 */
	@Test
	void testBuildSpringLayout2DOperationLevelSpecificParameterNameValueObjectMap() {
		double forceMultiplier = JUNGSpringLayout2DPerformer.DEFAULT_FORCE_MULTIPLIER;
		int repulsionRange = JUNGSpringLayout2DPerformer.DEFAULT_REPULSION_RANGE;
		double stretch = JUNGSpringLayout2DPerformer.DEFAULT_STRETCH;
		int iterations = JUNGSpringLayout2DPerformer.DEFAULT_ITERATIONS;
		
		springLayout2DOperationLevelSpecificParameterNameValueObjectMap = SpringLayout2DOperation.buildSpringLayout2DOperationLevelSpecificParameterNameValueObjectMap(forceMultiplier, repulsionRange, stretch, iterations);
	}

	/**
	 * Test method for {@link operation.graph.layout.JUNG.SpringLayout2DOperation#SpringLayout2DOperation(java.util.Map, java.util.Map, java.util.Map, java.util.Map, java.util.Map)}.
	 */
	@Test
	public void testSpringLayout2DOperation() {
		//
		AbstractOperationTest abstractOperationTest = new AbstractOperationTest();
		abstractOperationTest.testBuildAbstractOperationLevelSpecificParameterNameValueObjectMap3();
		//
		SingleGenericGraphAsInputOperationTest singleGenericGraphAsInputOperationTest = new SingleGenericGraphAsInputOperationTest();
		singleGenericGraphAsInputOperationTest.testBuildSingleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap();
		
		//
		InputGraphTypeBoundedOperationTest inputGraphTypeBoundedOperationTest = new InputGraphTypeBoundedOperationTest();
		inputGraphTypeBoundedOperationTest.testBuildInputGraphTypeBoundedOperationLevelSpecificParameterNameValueObjectMap();
		
		//
		GraphNode2DLayoutOperationBaseTest graphNode2DLayoutOperationBaseTest = new GraphNode2DLayoutOperationBaseTest();
		graphNode2DLayoutOperationBaseTest.testBuildGraphNode2DLayoutOperationBaseLevelSpecificParameterNameValueObjectMap();
		
		//
		this.testBuildSpringLayout2DOperationLevelSpecificParameterNameValueObjectMap();
		
		
		springLayout2DOperation = new SpringLayout2DOperation(
//				false,
				AbstractOperationTest.abstractOperationLevelSpecificParameterNameValueObjectMap,
				SingleGenericGraphAsInputOperationTest.singleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap,
				InputGraphTypeBoundedOperationTest.inputGraphTypeBoundedOperationLevelSpecificParameterNameValueObjectMap,
				GraphNode2DLayoutOperationBaseTest.graphNode2DLayoutOperationBaseLevelSpecificParameterNameValueObjectMap,
				springLayout2DOperationLevelSpecificParameterNameValueObjectMap,
				true
				);
	}

}
