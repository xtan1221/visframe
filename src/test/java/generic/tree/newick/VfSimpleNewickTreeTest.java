/**
 * 
 */
package generic.tree.newick;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fileformat.vftree.VfTreeDataFileFormatType;
import generic.tree.reader.filebased.newick.SimpleNewickFileTreeReader;

/**
 * @author tanxu
 *
 */
class VfSimpleNewickTreeTest {
	//source https://www.megasoftware.net/mega4/WebHelp/glossary/rh_newick_format.htm
	public static String testTree1 = "((raccoon, bear),((sea_lion,seal),((monkey,cat), weasel)),dog);";
	public static String testTree2 = "((raccoon:19.19959,bear:6.80041):0.84600,((sea_lion:11.99700, seal:12.00300):7.52973,((monkey:100.85930,cat:47.14069):20.59201, weasel:18.87953):2.09460):3.87382,dog:25.46154);";
	public static String test_SIMPLE_NEWICK_1 = "((raccoon:19.19959,bear:6.80041):0.84600[50],((sea_lion:11.99700, seal:12.00300):7.52973[100],((monkey:100.85930,cat:47.14069):20.59201[80], weasel:18.87953):2.09460[75]):3.87382[50],dog:25.46154);";
	public static String test_SIMPLE_NEWICK_2 = "((raccoon:19.19959,bear:6.80041)50:0.84600,((sea_lion:11.99700, seal:12.00300)100:7.52973,((monkey:100.85930,cat:47.14069)80:20.59201, weasel:18.87953)75:2.09460)50:3.87382,dog:25.46154);";
	
	
	//source : https://en.m.wikipedia.org/wiki/Newick_format
	public static String tree1 = "(,,(,));";//                               no nodes are named
	public static String tree2 = "(A,B,(C,D));";//                          leaf nodes are named
	public static String tree3 = "(A,B,(C,D)E)F;";//                        all nodes are named ***
	public static String tree4 = "(:0.1,:0.2,(:0.3,:0.4):0.5);";//          all but root node have a distance to parent
	public static String tree5 = "(:0.1,:0.2,(:0.3,:0.4):0.5):0.0;";//      all have a distance to parent
	public static String tree6 = "(A:0.1,B:0.2,(C:0.3,D:0.4):0.5);";//       distances and leaf names (popular)
	public static String tree7 = "(A:0.1,B:0.2,(C:0.3,D:0.4)E:0.5)F;";//     distances and all names
	public static String tree8 = "((B:0.2,(C:0.3,D:0.4)E:0.5)A:0.1)F;";//    a tree rooted on a leaf node (rare)
		
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
	 * Test method for {@link generic.tree.reader.filebased.newick.SimpleNewickFileTreeReader#VfSimpleNewickTree(java.lang.String, fileformat.vftree.VfTreeDataFileFormatType)}.
	 */
	@Test
	void testVfSimpleNewickTree() {
//		VfSimpleNewickTree tree1 = new VfSimpleNewickTree(testTree1, VfTreeDataFileFormatType.SIMPLE_NEWICK_1);
//		
//		VfSimpleNewickTree tree2 = new VfSimpleNewickTree(testTree2, VfTreeDataFileFormatType.SIMPLE_NEWICK_1);
//		
//		
//		VfSimpleNewickTree stree1 = new VfSimpleNewickTree(test_SIMPLE_NEWICK_1, VfTreeDataFileFormatType.SIMPLE_NEWICK_1);
		
//		SimpleNewickFileTreeReader stree2 = new SimpleNewickFileTreeReader("(raccoon:19.19959,bear:6.80041)50:0.84600;", VfTreeDataFileFormatType.SIMPLE_NEWICK_2);
		
		
		System.out.println();
	}

}
