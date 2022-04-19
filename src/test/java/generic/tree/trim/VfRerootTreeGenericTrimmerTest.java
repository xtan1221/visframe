/**
 * 
 */
package generic.tree.trim;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import generic.tree.VfTree;
import generic.tree.reader.projectbased.VfDataTreeReaderTest;
import generic.tree.trim.helper.PositionOnTree;

/**
 * @author tanxu
 *
 */
class VfRerootTreeGenericTrimmerTest {
	public static VfRerootTreeGenericTrimmer vfRerootTreeGenericTrimmer;
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
	 * Test method for {@link generic.tree.trim.VfRerootTreeGenericTrimmer#VfRerootTreeGenericTrimmer(generic.tree.VfTree, int, java.lang.Integer, java.lang.Double)}.
	 * @throws SQLException 
	 */
	@Test
	void testVfRerootTreeGenericTrimmer() throws SQLException {
		VfDataTreeReaderTest vfDataTreeReaderTest = new VfDataTreeReaderTest();
		vfDataTreeReaderTest.testPerform();
		
		
		VfTree inputTree = VfDataTreeReaderTest.dataTreeReader;
		int childNodeID = 1;
		Integer parentNodeID = null;
		Double newRootNodePos = null;
		
		PositionOnTree position = new PositionOnTree(childNodeID, parentNodeID, newRootNodePos, false);
		
		vfRerootTreeGenericTrimmer = new VfRerootTreeGenericTrimmer(inputTree, position);
	}
	
	/**
	 * new node
	 * Test method for {@link generic.tree.trim.VfRerootTreeGenericTrimmer#VfRerootTreeGenericTrimmer(generic.tree.VfTree, int, java.lang.Integer, java.lang.Double)}.
	 * @throws SQLException 
	 */
	@Test
	void testVfRerootTreeGenericTrimmer2() throws SQLException {
		VfDataTreeReaderTest vfDataTreeReaderTest = new VfDataTreeReaderTest();
		vfDataTreeReaderTest.testPerform();
		
		
		VfTree inputTree = VfDataTreeReaderTest.dataTreeReader;
		Integer childNodeID = 1;
		int parentNodeID = 0;
		Double newRootNodePos = 0.2;
		
		PositionOnTree position = new PositionOnTree(childNodeID, parentNodeID, newRootNodePos, false);
		
		vfRerootTreeGenericTrimmer = new VfRerootTreeGenericTrimmer(inputTree, position);
	}
	
	/**
	 * Test method for {@link generic.tree.trim.VfRerootTreeGenericTrimmer#perform()}.
	 * @throws SQLException 
	 */
	@Test
	void testPerform() throws SQLException {
		testVfRerootTreeGenericTrimmer2();
		vfRerootTreeGenericTrimmer.perform();
		
		vfRerootTreeGenericTrimmer.getOutputTree().getNodeIDMap().forEach((k,v)->{
			System.out.println(v);
		});
	}

}
