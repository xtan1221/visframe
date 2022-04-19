/**
 * 
 */
package generic.tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fileformat.vftree.VfTreeDataFileFormatType;
import generic.tree.reader.filebased.newick.SimpleNewickParserUtils;
import utils.Pair;
import utils.Triple;

/**
 * @author tanxu
 *
 */
class NewickParserUtilsTest {
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
	 * Test method for {@link generic.tree.reader.filebased.newick.SimpleNewickParserUtils#validateFullNewickString(java.lang.String)}.
	 */
	@Test
	void testValidateNewickString() {
//		NewickParserUtils.validateFullNewickString(testTree1);
//		NewickParserUtils.validateFullNewickString(testTree2);
//		NewickParserUtils.validateFullNewickString(test_SIMPLE_NEWICK_1);
//		NewickParserUtils.validateFullNewickString(test_SIMPLE_NEWICK_2);
//		
//		NewickParserUtils.validateFullNewickString(tree1);
//		NewickParserUtils.validateFullNewickString(tree2);
//		NewickParserUtils.validateFullNewickString(tree3);
//		NewickParserUtils.validateFullNewickString(tree4);
//		NewickParserUtils.validateFullNewickString(tree5);
//		NewickParserUtils.validateFullNewickString(tree6);
//		NewickParserUtils.validateFullNewickString(tree7);
//		NewickParserUtils.validateFullNewickString(tree8);
		
		
//		NewickParserUtils.validateFullNewickString("a,b;"); //throw exception
		
		SimpleNewickParserUtils.validateFullNewickString("(a,b),c;");//throw exception;
	}

	/**
	 * Test method for {@link generic.tree.reader.filebased.newick.SimpleNewickParserUtils#hasImplicitRoot(java.lang.String)}.
	 */
	@Test
	void testHasImplicitRoot() {
		assertEquals(false,SimpleNewickParserUtils.hasImplicitRoot("(a,b);"));
		assertEquals(true,SimpleNewickParserUtils.hasImplicitRoot("(a,b)c:4;"));//here c is the node label of the parent node of a and b
		assertEquals(false,SimpleNewickParserUtils.hasImplicitRoot("(a,b)c;")); //here c is the node label of the parent node of a and b, 
		assertEquals(true,SimpleNewickParserUtils.hasImplicitRoot("(a,b):10;")); //parent node of a and b has a distance=10 to its parent node, which is not shown by node label or parenthesis, thus has implicit root;
	}

	/**
	 * Test method for {@link generic.tree.reader.filebased.newick.SimpleNewickParserUtils#preprocessNewickStringFromFile(java.lang.String)}.
	 */
	@Test
	void testPreprocessNewickStringFromFile() {
		tree1 = SimpleNewickParserUtils.preprocessNewickStringFromFile(tree1);
		tree2 = SimpleNewickParserUtils.preprocessNewickStringFromFile(tree2);
		tree3 = SimpleNewickParserUtils.preprocessNewickStringFromFile(tree3);
		tree4 = SimpleNewickParserUtils.preprocessNewickStringFromFile(tree4);
		tree5 = SimpleNewickParserUtils.preprocessNewickStringFromFile(tree5);
		tree6 = SimpleNewickParserUtils.preprocessNewickStringFromFile(tree6);
		tree7 = SimpleNewickParserUtils.preprocessNewickStringFromFile(tree7);
		tree8 = SimpleNewickParserUtils.preprocessNewickStringFromFile(tree8);
		
		testTree1 = SimpleNewickParserUtils.preprocessNewickStringFromFile(testTree1);
		testTree2 = SimpleNewickParserUtils.preprocessNewickStringFromFile(testTree2);
		test_SIMPLE_NEWICK_1 = SimpleNewickParserUtils.preprocessNewickStringFromFile(test_SIMPLE_NEWICK_1);
		test_SIMPLE_NEWICK_2 = SimpleNewickParserUtils.preprocessNewickStringFromFile(test_SIMPLE_NEWICK_2);
		
	}

	/**
	 * Test method for {@link generic.tree.reader.filebased.newick.SimpleNewickParserUtils#extract_SIMPLE_NEWICK_1_ChildrenNodeStringNodeLabelStringAndBranchLabelString(java.lang.String)}.
	 */
	@Test
	void testExtractChildrenNodeStringNodeLabelStringAndBranchLabelString() {
//		testPreprocessNewickStringFromFile();
		
//		Triple<String, String, String> ret1 = NewickParserUtils.extractChildrenNodeStringNodeLabelStringAndBranchLabelString(tree1);
//		
//		Triple<String, String, String> ret2 = NewickParserUtils.extractChildrenNodeStringNodeLabelStringAndBranchLabelString(tree2);
//		
//		Triple<String, String, String> ret3 = NewickParserUtils.extractChildrenNodeStringNodeLabelStringAndBranchLabelString(tree3);
//		
//		Triple<String, String, String> ret4 = NewickParserUtils.extractChildrenNodeStringNodeLabelStringAndBranchLabelString(tree4);
//		
//		Triple<String, String, String> ret5 = NewickParserUtils.extractChildrenNodeStringNodeLabelStringAndBranchLabelString(tree5);
//		
//		Triple<String, String, String> ret6 = NewickParserUtils.extractChildrenNodeStringNodeLabelStringAndBranchLabelString(tree6);
//		
//		Triple<String, String, String> ret7 = NewickParserUtils.extractChildrenNodeStringNodeLabelStringAndBranchLabelString(tree7);
		
		Triple<String, String, String> ret8 = SimpleNewickParserUtils.extractChildrenNodeStringNodeLabelStringAndBranchLabelString("(raccoon:19.19959,bear:6.80041)50:0.84600", VfTreeDataFileFormatType.SIMPLE_NEWICK_2);
		
		
		System.out.println();
	}

	/**
	 * Test method for {@link generic.tree.reader.filebased.newick.SimpleNewickParserUtils#splitNakedInternalNodeStringIntoChildrenNodeStrings(java.lang.String)}.
	 */
	@Test
	void testSplitNakedInternalNodeStringIntoChildrenNodeStrings() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link generic.tree.reader.filebased.newick.SimpleNewickParserUtils#parseEdgeLabelStringForLengthAndBootstrap(java.lang.String, fileformat.vftree.VfTreeDataFileFormatType)}.
	 */
	@Test
	void testParseEdgeLabelStringForLengthAndBootstrap() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link generic.tree.reader.filebased.newick.SimpleNewickParserUtils#parse_NEWICK_1_EdgeLabelStringForLengthAndBootstrap(java.lang.String)}.
	 */
	@Test
	void testParse_NEWICK_1_EdgeLabelStringForLengthAndBootstrap() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link generic.tree.reader.filebased.newick.SimpleNewickParserUtils#parse_NEWICK_2_EdgeLabelStringForLengthAndBootstrap(java.lang.String)}.
	 */
	@Test
	void testParse_NEWICK_2_EdgeLabelStringForLengthAndBootstrap() {
		Pair<Double,Integer> ret = SimpleNewickParserUtils.parseEdgeLabelStringForLengthAndBootstrap("50:0.84600",VfTreeDataFileFormatType.SIMPLE_NEWICK_2);
//		Pair<Double,Integer> ret2 = NewickParserUtils.parse_NEWICK_2_EdgeLabelStringForLengthAndBootstrap(":25.46154");
//		Pair<Double,Integer> ret3 = NewickParserUtils.parse_NEWICK_2_EdgeLabelStringForLengthAndBootstrap("92");
//		Pair<Double,Integer> ret4 = NewickParserUtils.parse_NEWICK_2_EdgeLabelStringForLengthAndBootstrap("");
		
		
		System.out.println();
	}

}
