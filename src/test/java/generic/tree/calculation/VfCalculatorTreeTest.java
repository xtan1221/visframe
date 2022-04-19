/**
 * 
 */
package generic.tree.calculation;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import generic.tree.reader.filebased.newick.SimpleNewickFileTreeReaderTest;

/**
 * @author tanxu
 *
 */
class VfCalculatorTreeTest {
	public static VfCalculatorTree vfCalculatorTree;
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
	 * Test method for {@link generic.tree.calculation.VfCalculatorTree#VfCalculatorTree(generic.tree.VfTree)}.
	 * @throws IOException 
	 */
	@Test
	void testVfCalculatorTree() throws IOException {
		SimpleNewickFileTreeReaderTest simpleNewickFileTreeReaderTest = new SimpleNewickFileTreeReaderTest();
		simpleNewickFileTreeReaderTest.testPerform();
		
		vfCalculatorTree = new VfCalculatorTree(SimpleNewickFileTreeReaderTest.simpleNewickFileTreeReader);
	}

}
