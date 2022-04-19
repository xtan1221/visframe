/**
 * 
 */
package operation.vftree.trim;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import generic.tree.trim.helper.SiblingReorderPattern;
import operation.AbstractOperationTest;
import operation.graph.SingleGenericGraphAsInputOperationTest;
import operation.vftree.VfTreeTrimmingOperationBaseTest;

/**
 * @author tanxu
 *
 */
public class SiblingNodesReorderOperationTest {
	public static Map<SimpleName, Object> siblingNodesReorderOperationLevelSpecificParameterNameValueObjectMap;
	public static SiblingNodesReorderOperation siblingNodesReorderOperation;
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
	 * Test method for {@link operation.vftree.trim.SiblingNodesReorderOperation#buildSiblingNodesReorderOperationLevelSpecificParameterNameValueObjectMap(generic.tree.trim.helper.SiblingReorderPattern)}.
	 */
	@Test
	void testBuildSiblingNodesReorderOperationLevelSpecificParameterNameValueObjectMap() {
		Map<Integer,Map<Integer, Integer>> parentNodeIDToOriginalSwappedIndexMapMap = new HashMap<>();
		
		int parentNodeID = 2;
		Map<Integer,Integer> siblingNodesOldAndNewOrderIndexMap = new HashMap<>();
		siblingNodesOldAndNewOrderIndexMap.put(0, 1);
		siblingNodesOldAndNewOrderIndexMap.put(1, 0);
		parentNodeIDToOriginalSwappedIndexMapMap.put(parentNodeID, siblingNodesOldAndNewOrderIndexMap);
		
		parentNodeID = 0;
		Map<Integer,Integer> siblingNodesOldAndNewOrderIndexMap2 = new HashMap<>();
		siblingNodesOldAndNewOrderIndexMap2.put(0, 1);
		siblingNodesOldAndNewOrderIndexMap2.put(1, 0);
		parentNodeIDToOriginalSwappedIndexMapMap.put(parentNodeID, siblingNodesOldAndNewOrderIndexMap2);
		
		
		SiblingReorderPattern siblingReorderPattern = new SiblingReorderPattern(parentNodeIDToOriginalSwappedIndexMapMap);
		
		
		siblingNodesReorderOperationLevelSpecificParameterNameValueObjectMap = SiblingNodesReorderOperation.buildSiblingNodesReorderOperationLevelSpecificParameterNameValueObjectMap(siblingReorderPattern);
		
	}

	/**
	 * Test method for {@link operation.vftree.trim.SiblingNodesReorderOperation#SiblingNodesReorderOperation(java.util.Map, java.util.Map, java.util.Map, java.util.Map)}.
	 */
	@Test
	public void testSiblingNodesReorderOperation() {
		//
		AbstractOperationTest abstractOperationTest = new AbstractOperationTest();
		abstractOperationTest.testBuildAbstractOperationLevelSpecificParameterNameValueObjectMap3();
		//
		SingleGenericGraphAsInputOperationTest singleGenericGraphAsInputOperationTest = new SingleGenericGraphAsInputOperationTest();
		singleGenericGraphAsInputOperationTest.testBuildSingleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap();
		
		VfTreeTrimmingOperationBaseTest vfTreeTrimmingOperationBaseTest = new VfTreeTrimmingOperationBaseTest();
		vfTreeTrimmingOperationBaseTest.testBuildVfTreeTrimmingOperationBaseLevelSpecificParameterNameValueObjectMap();
		
		testBuildSiblingNodesReorderOperationLevelSpecificParameterNameValueObjectMap();
		
		
//		siblingNodesReorderOperation = new SiblingNodesReorderOperation(
//				AbstractOperationTest.abstractOperationLevelSpecificParameterNameValueObjectMap,
//				SingleGenericGraphAsInputOperationTest.singleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap,
//				VfTreeTrimmingOperationBaseTest.vfTreeTrimmingOperationBaseLevelSpecificParameterNameValueObjectMap,
//				siblingNodesReorderOperationLevelSpecificParameterNameValueObjectMap
//				);
	}

}
