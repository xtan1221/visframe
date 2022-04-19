/**
 * 
 */
package importer.record.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fileformat.record.utils.RegexStringMarker;

/**
 * @author tanxu
 *
 */
class RegexStringMarkerTest {

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
	 * Test method for {@link fileformat.record.utils.RegexStringMarker#getStringValue()}.
	 */
	@Test
	void testGetStringValue() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link fileformat.record.utils.RegexStringMarker#RegexStringMarker(java.lang.String, boolean)}.
	 */
	@Test
	void testRegexStringMarker() {
//		String s = "\\d";
//		String testString = "1111";
		
//		String s = "\\d+";
//		String testString = "1111";
		
		///////////=========
//		String s = "\\t";
//		String testString = "1111	23232	dfsd";
		
//		String s = "\\t+";
//		String testString = "1111	23232	dfsd";
		
		///////==========
//		String s = "\\s";
//		String testString = "1111	23232	dfd";
		
//		String s = "\\s+";
//		String testString = "1111	23232	dfd";
		
		/////===========
		String s = "\n";
		String testString = "1111	23232	\n dfd";
		System.out.println(testString);
		
//		String s = "\\n+";
//		String testString = "1111	23232	\n dfd";
		
		RegexStringMarker rsm = new RegexStringMarker(s, true);
		
		System.out.print("aaaaa"+rsm.toString());
		System.out.println();
		System.out.println("================");
		System.out.print("bbbbb"+rsm.getStringValue());
		System.out.println();
		System.out.println("================");
		
		
		Pattern p1 = Pattern.compile(rsm.toString());
		Pattern p2 = Pattern.compile(rsm.getStringValue());
		
		Matcher m1 = p1.matcher(testString);
		System.out.println("toString():"+m1.find());
		Matcher m2 = p2.matcher(testString);
		System.out.println("stringValue():"+m2.find());
		
	}

}
