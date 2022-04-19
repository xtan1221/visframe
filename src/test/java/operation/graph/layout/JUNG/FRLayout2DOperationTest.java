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
import operation.graph.layout.jgrapht.CircularLayout2DOperation;
import operation.graph.layout.utils.JUNGFRLayout2DPerformer;

/**
 * @author tanxu
 *
 */
public class FRLayout2DOperationTest {
	public static Map<SimpleName, Object> FRLayout2DOperationLevelSpecificParameterNameValueObjectMap;
	public static FRLayout2DOperation fRLayout2DOperation;
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
	 * Test method for {@link operation.graph.layout.JUNG.FRLayout2DOperation#buildFRLayout2DOperationLevelSpecificParameterNameValueObjectMap(double, double, int)}.
	 */
	@Test
	void testBuildFRLayout2DOperationLevelSpecificParameterNameValueObjectMap() {
		double attractionMultiplier = JUNGFRLayout2DPerformer.DEFAULT_ATTRACTION_MULTIPLIER;
		double repulsion = JUNGFRLayout2DPerformer.DEFAULT_REPULSION;
		int maxIterations = 100;
		
		FRLayout2DOperationLevelSpecificParameterNameValueObjectMap = FRLayout2DOperation.buildFRLayout2DOperationLevelSpecificParameterNameValueObjectMap(attractionMultiplier, repulsion, maxIterations);
		
	}

	/**
	 * Test method for {@link operation.graph.layout.JUNG.FRLayout2DOperation#FRLayout2DOperation(java.util.Map, java.util.Map, java.util.Map, java.util.Map, java.util.Map)}.
	 */
	@Test
	public void testFRLayout2DOperation() {
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
		this.testBuildFRLayout2DOperationLevelSpecificParameterNameValueObjectMap();
		
		
		fRLayout2DOperation = new FRLayout2DOperation(
//				false,
				AbstractOperationTest.abstractOperationLevelSpecificParameterNameValueObjectMap,
				SingleGenericGraphAsInputOperationTest.singleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap,
				InputGraphTypeBoundedOperationTest.inputGraphTypeBoundedOperationLevelSpecificParameterNameValueObjectMap,
				GraphNode2DLayoutOperationBaseTest.graphNode2DLayoutOperationBaseLevelSpecificParameterNameValueObjectMap,
				FRLayout2DOperationLevelSpecificParameterNameValueObjectMap,
				true
				);
	}
	
}
