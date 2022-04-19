/**
 * 
 */
package operation.graph.layout.jgrapht;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import context.project.VisProjectDBContextTest;
import context.project.process.logtable.StatusType;
import operation.AbstractOperationTest;
import operation.graph.InputGraphTypeBoundedOperationTest;
import operation.graph.SingleGenericGraphAsInputOperationTest;
import operation.graph.layout.GraphNode2DLayoutOperationBaseTest;

/**
 * @author tanxu
 *
 */
public class CircularLayout2DOperationTest {
	public static Map<SimpleName, Object> circularLayout2DOperationLevelSpecificParameterNameValueObjectMap;
	public static CircularLayout2DOperation circularLayout2DOperation;
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
	 * Test method for {@link operation.graph.layout.jgrapht.CircularLayout2DOperation#buildCircularLayout2DOperationLevelSpecificParameterNameValueObjectMap(double)}.
	 */
	@Test
	void testBuildCircularLayout2DOperationLevelSpecificParameterNameValueObjectMap() {
		double radius = 40;
		
		circularLayout2DOperationLevelSpecificParameterNameValueObjectMap = CircularLayout2DOperation.buildCircularLayout2DOperationLevelSpecificParameterNameValueObjectMap(radius);
	}

	/**
	 * Test method for {@link operation.graph.layout.jgrapht.CircularLayout2DOperation#CircularLayout2DOperation(java.util.Map, java.util.Map, java.util.Map, java.util.Map, java.util.Map)}.
	 */
	@Test
	public void testCircularLayout2DOperation() {
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
		this.testBuildCircularLayout2DOperationLevelSpecificParameterNameValueObjectMap();
		
		
		circularLayout2DOperation = new CircularLayout2DOperation(
//				false,
				AbstractOperationTest.abstractOperationLevelSpecificParameterNameValueObjectMap,
				SingleGenericGraphAsInputOperationTest.singleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap,
				InputGraphTypeBoundedOperationTest.inputGraphTypeBoundedOperationLevelSpecificParameterNameValueObjectMap,
				GraphNode2DLayoutOperationBaseTest.graphNode2DLayoutOperationBaseLevelSpecificParameterNameValueObjectMap,
				circularLayout2DOperationLevelSpecificParameterNameValueObjectMap,
				true
				);
		
	}

	/**
	 * Test method for {@link operation.graph.layout.GraphNode2DLayoutOperationBase#call()}.
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Test
	void testCall() throws SQLException, IOException {
		VisProjectDBContextTest visProjectDBContextTest = new VisProjectDBContextTest();
		visProjectDBContextTest.testConnect();
		
		
		this.testCircularLayout2DOperation();
		
		
		circularLayout2DOperation.setHostVisProjectDBContext(VisProjectDBContextTest.TEST_PROJECT_1);
		
		
		StatusType st = circularLayout2DOperation.call();
		
		System.out.println(st);
	}

}
