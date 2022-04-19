/**
 * 
 */
package operation.vftree.trim;

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
import operation.vftree.VfTreeTrimmingOperationBaseTest;

/**
 * @author tanxu
 *
 */
public class RerootTreeOperationTest {
	public static Map<SimpleName, Object> rerootTreeOperationLevelSpecificParameterNameValueObjectMap;
//	public static RerootTreeOperation_dump rerootTreeOperation;
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
	 * Test method for {@link operation.vftree.trim.RerootTreeOperation_dump#buildRerootTreeOperationLevelSpecificParameterNameValueObjectMap(int, java.lang.Integer, java.lang.Double)}.
	 */
	@Test
	void testBuildRerootTreeOperationLevelSpecificParameterNameValueObjectMap() {
		int newRootNodeParentID = 1;
		Integer newRootNodeChildID = null;
		Double newRootPos = null;
//		rerootTreeOperationLevelSpecificParameterNameValueObjectMap = 
//				RerootTreeOperation_dump.buildRerootTreeOperationLevelSpecificParameterNameValueObjectMap(newRootNodeParentID, newRootNodeChildID, newRootPos);
	}
	
	/**
	 * Test method for {@link operation.vftree.trim.RerootTreeOperation_dump#RerootTreeOperation(java.util.Map, java.util.Map, java.util.Map, java.util.Map)}.
	 */
	@Test
	public void testRerootTreeOperation() {
		//
		AbstractOperationTest abstractOperationTest = new AbstractOperationTest();
		abstractOperationTest.testBuildAbstractOperationLevelSpecificParameterNameValueObjectMap3();
		//
		SingleGenericGraphAsInputOperationTest singleGenericGraphAsInputOperationTest = new SingleGenericGraphAsInputOperationTest();
		singleGenericGraphAsInputOperationTest.testBuildSingleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap();
		
		VfTreeTrimmingOperationBaseTest vfTreeTrimmingOperationBaseTest = new VfTreeTrimmingOperationBaseTest();
		vfTreeTrimmingOperationBaseTest.testBuildVfTreeTrimmingOperationBaseLevelSpecificParameterNameValueObjectMap();
		
		testBuildRerootTreeOperationLevelSpecificParameterNameValueObjectMap();
		
		
//		rerootTreeOperation = new RerootTreeOperation(
//				AbstractOperationTest.abstractOperationLevelSpecificParameterNameValueObjectMap,
//				SingleGenericGraphAsInputOperationTest.singleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap,
//				VfTreeTrimmingOperationBaseTest.vfTreeTrimmingOperationBaseLevelSpecificParameterNameValueObjectMap,
//				rerootTreeOperationLevelSpecificParameterNameValueObjectMap
//				);
	}
	
	
	/**
	 * Test method for {@link operation.vftree.trim.RerootTreeOperation_dump#call()}.
	 */
	@Test
	void testCall() {
		fail("Not yet implemented");
	}


}
