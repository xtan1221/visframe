/**
 * 
 */
package importer.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author tanxu
 *
 */
class StringUtilsTest {

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
	 * Test method for {@link importer.utils.StringUtils#splitS2ByS1AsExpected(java.lang.String, java.lang.String)}.
	 */
	@Test
	void testSplitS2ByS1AsExpected() {
		String dataString = "a";
		String delimiter = "a";
		
		
		List<String> ret = StringUtils.splitS2ByS1AsExpected(delimiter, dataString);
		
		assertEquals(2,ret.size());
		
		assertEquals("", ret.get(0));
		assertEquals("", ret.get(1));
		
		////////////////////////
		dataString = "aaa";
		delimiter = "a";
		
		
		ret = StringUtils.splitS2ByS1AsExpected(delimiter, dataString);
		
		assertEquals(4,ret.size());
		
		assertEquals("", ret.get(0));
		assertEquals("", ret.get(1));
		assertEquals("", ret.get(2));
		
		////////////////////////
		dataString = "ababa";
		delimiter = "a";
		
		
		ret = StringUtils.splitS2ByS1AsExpected(delimiter, dataString);
		
		assertEquals(4,ret.size());
		
		assertEquals("", ret.get(0));
		assertEquals("b", ret.get(1));
		assertEquals("b", ret.get(2));
		
		////////////////////////
		dataString = "tabababa";
		delimiter = "a";
		
		
		ret = StringUtils.splitS2ByS1AsExpected(delimiter, dataString);
		
		assertEquals(5,ret.size());
		
		assertEquals("t", ret.get(0));
		assertEquals("b", ret.get(1));
		assertEquals("", ret.get(4));
		
	}

	/**
	 * Test method for {@link importer.utils.StringUtils#splitStringByListOfSubstringLenghts(java.util.List, java.lang.String)}.
	 */
	@Test
	void testSplitStringByListOfSubstringLenghts() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link importer.utils.StringUtils#copyOfS1AtEndOfS2(java.lang.String, java.lang.String)}.
	 */
	@Test
	void testCopyOfS1AtEndOfS2() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link importer.utils.StringUtils#stringS2IsFullyDecomposableToS1(java.lang.String, java.lang.String)}.
	 */
	@Test
	void testStringS2IsFullyDecomposableToS1() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link importer.utils.StringUtils#splitByS1NotFollowingS2(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testSplitByS1NotFollowingS2() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link importer.utils.StringUtils#splitByAListOfDelimiters(java.util.List, java.lang.String)}.
	 */
	@Test
	void testSplitByAListOfDelimiters() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link importer.utils.StringUtils#splitByFirstNDelimiters(java.lang.String, java.lang.String, int)}.
	 */
	@Test
	void testSplitByFirstNDelimiters() {
		String dataString = "tabababa";
		String delimiter = "a";
		
		
		List<String> ret = StringUtils.splitByFirstNDelimiters(delimiter, dataString, 1);
		
		assertEquals(2,ret.size());
		
		assertEquals("t", ret.get(0));
		assertEquals("bababa", ret.get(1));
		
		//////////////////////
		dataString = "aaaaaaaaaa";
		delimiter = "a";
		
		
		ret = StringUtils.splitByFirstNDelimiters(delimiter, dataString, 1);
		
		assertEquals(2,ret.size());
		
		assertEquals("", ret.get(0));
		assertEquals("aaaaaaaaa", ret.get(1));
		
		//////////////////////
		dataString = "			aaaaaaaaaa";
		delimiter = "a";
		
		
		ret = StringUtils.splitByFirstNDelimiters(delimiter, dataString, 1);
		
		assertEquals(2,ret.size());
		
		assertEquals("			", ret.get(0));
		assertEquals("aaaaaaaaa", ret.get(1));
		
		
	}

	/**
	 * Test method for {@link importer.utils.StringUtils#stringS2IsCoveredByS1AsRegex(java.lang.String, java.lang.String)}.
	 */
	@Test
	void testStringS2IsCoveredByS1AsRegex() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link importer.utils.StringUtils#stringS2ContainsS1AsRegex(java.lang.String, java.lang.String)}.
	 */
	@Test
	void testStringS2ContainsS1AsRegex() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link importer.utils.StringUtils#s1IsHigherPriorityThanS2(java.lang.String, boolean, java.lang.String, boolean, boolean, boolean)}.
	 */
	@Test
	void testS1IsHigherPriorityThanS2() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link importer.utils.StringUtils#isWhiteSpaces(java.lang.String)}.
	 */
	@Test
	void testIsWhiteSpaces() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link importer.utils.StringUtils#isEmptyString(java.lang.String)}.
	 */
	@Test
	void testIsEmptyString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link importer.utils.StringUtils#splitTest()}.
	 */
	@Test
	void testSplitTest() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link importer.utils.StringUtils#catenateTest()}.
	 */
	@Test
	void testCatenateTest() {
		fail("Not yet implemented");
	}

}
