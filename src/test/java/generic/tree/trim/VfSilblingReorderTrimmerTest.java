/**
 * 
 */
package generic.tree.trim;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import generic.tree.VfTree;
import generic.tree.reader.projectbased.VfDataTreeReaderTest;
import generic.tree.trim.helper.SiblingReorderPattern;

/**
 * @author tanxu
 *
 */
class VfSilblingReorderTrimmerTest {
	public static VfSilblingReorderTrimmer vfSilblingReorderTrimmer;
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
	 * Test method for {@link generic.tree.trim.VfSilblingReorderTrimmer#VfSilblingReorderTrimmer(generic.tree.VfTree, int, java.util.Map)}.
	 * @throws SQLException 
	 */
	@Test
	void testVfSilblingReorderTrimmer() throws SQLException {
		VfDataTreeReaderTest vfDataTreeReaderTest = new VfDataTreeReaderTest();
		vfDataTreeReaderTest.testPerform();
		VfTree inputTree = VfDataTreeReaderTest.dataTreeReader;
		
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
		
		vfSilblingReorderTrimmer = new VfSilblingReorderTrimmer(inputTree, siblingReorderPattern);
	}
	
	/**
	 * Test method for {@link generic.tree.trim.VfSilblingReorderTrimmer#perform()}.
	 * @throws SQLException 
	 */
	@Test
	void testPerform() throws SQLException {
		testVfSilblingReorderTrimmer();
		
		vfSilblingReorderTrimmer.perform();
		
		vfSilblingReorderTrimmer.getOutputTree().getNodeIDMap().forEach((k,v)->{
			System.out.println(v);
		});
	}
	
}
