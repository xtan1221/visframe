/**
 * 
 */
package generic.tree.trim.helper;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import generic.tree.VfTree;
import generic.tree.reader.projectbased.VfDataTreeReaderTest;

/**
 * @author tanxu
 *
 */
public class VfSubTreeOnExistingNodeTrimmerTest {
	public static VfSubTreeOnExistingNodeTrimmer vfSubTreeOnExistingNodeTrimmer;
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
	 * Test method for {@link generic.tree.trim.helper.VfSubTreeOnExistingNodeTrimmer#VfSubTreeOnExistingNodeTrimmer(generic.tree.VfTree, int)}.
	 * @throws SQLException 
	 */
	@Test
	void testVfSubTreeOnExistingNodeTrimmer() throws SQLException {
		VfDataTreeReaderTest vfDataTreeReaderTest = new VfDataTreeReaderTest();
		vfDataTreeReaderTest.testPerform();
		
		VfTree inputTree = VfDataTreeReaderTest.dataTreeReader;
		int newRootNodeID = 2;
		
		vfSubTreeOnExistingNodeTrimmer = new VfSubTreeOnExistingNodeTrimmer(inputTree, newRootNodeID);

		vfSubTreeOnExistingNodeTrimmer.getOutputTree().getNodeIDMap().forEach((k,v)->{
			System.out.println(v);
		});
	}

}
