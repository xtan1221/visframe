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
import operation.graph.SingleGenericGraphAsInputOperationTest;
import operation.vftree.VfTreeTrimmingOperationBaseTest;

/**
 * @author tanxu
 *
 */
public class SubTreeOperationTest {
	public static Map<SimpleName, Object> subTreeOperationLevelSpecificParameterNameValueObjectMap;
//	public static SubTreeOperation_dump subTreeOperation;
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
	 * Test method for {@link operation.vftree.trim.SubTreeOperation_dump#buildSubTreeOperationLevelSpecificParameterNameValueObjectMap(int, java.lang.Integer, java.lang.Double)}.
	 */
	@Test
	void testBuildSubTreeOperationLevelSpecificParameterNameValueObjectMap() {
		int newRootNodeParentID = 1;
		Integer newRootNodeChildID = null;
		Double newRootPos = null;
		
//		subTreeOperationLevelSpecificParameterNameValueObjectMap = 
//				SubTreeOperation_dump.buildSubTreeOperationLevelSpecificParameterNameValueObjectMap(newRootNodeParentID, newRootNodeChildID, newRootPos);
	}
	
	/**
	 * Test method for {@link operation.vftree.trim.SubTreeOperation_dump#SubTreeOperation(java.util.Map, java.util.Map, java.util.Map, java.util.Map)}.
	 */
	@Test
	public void testSubTreeOperation() {
		//
		AbstractOperationTest abstractOperationTest = new AbstractOperationTest();
		abstractOperationTest.testBuildAbstractOperationLevelSpecificParameterNameValueObjectMap3();
		//
		SingleGenericGraphAsInputOperationTest singleGenericGraphAsInputOperationTest = new SingleGenericGraphAsInputOperationTest();
		singleGenericGraphAsInputOperationTest.testBuildSingleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap();
		
		VfTreeTrimmingOperationBaseTest vfTreeTrimmingOperationBaseTest = new VfTreeTrimmingOperationBaseTest();
		vfTreeTrimmingOperationBaseTest.testBuildVfTreeTrimmingOperationBaseLevelSpecificParameterNameValueObjectMap();
		
		testBuildSubTreeOperationLevelSpecificParameterNameValueObjectMap();
		
		
//		subTreeOperation = new SubTreeOperation(
//				AbstractOperationTest.abstractOperationLevelSpecificParameterNameValueObjectMap,
//				SingleGenericGraphAsInputOperationTest.singleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap,
//				VfTreeTrimmingOperationBaseTest.vfTreeTrimmingOperationBaseLevelSpecificParameterNameValueObjectMap,
//				subTreeOperationLevelSpecificParameterNameValueObjectMap
//				);
	}

}
