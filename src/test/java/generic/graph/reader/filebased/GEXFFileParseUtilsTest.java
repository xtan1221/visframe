/**
 * 
 */
package generic.graph.reader.filebased;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fileformat.graph.gexf.GEXFUserDefinedAttribute;
import utils.Pair;

/**
 * @author tanxu
 *
 */
class GEXFFileParseUtilsTest {
	public static String TEST_STRING_1 = "<graph defaultedgetype=\"undirected\" idtype=\"string\" mode=\"static\">";
	public static String TEST_STRING_2 = "<attributes class=\"node\" mode=\"static\">";
	public static String TEST_STRING_3 = "<attribute id=\"0\" title=\"url\" type=\"string\"/>";
	public static String TEST_STRING_4 = "<node id=\"0\" label=\"Gephi\">";
	public static String TEST_STRING_5 = "<attvalue for=\"0\" value=\"http://gephi.org\"/>";
	
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
	 * Test method for {@link generic.graph.reader.filebased.GEXFFileParseUtils#parseLineAttributeNameStringValueMap(java.lang.String)}.
	 */
	@Test
	void testParseLineAttributeNameStringValueMap() {
		
//		Map<String,String> ret1 = GEXFFileParseUtils.parseLineAttributeNameStringValueMap(TEST_STRING_1);
//		Map<String,String> ret2 = GEXFFileParseUtils.parseLineAttributeNameStringValueMap(TEST_STRING_2);
		Map<String,String> ret3 = GEXFFileParseUtils.parseLineAttributeNameStringValueMap(TEST_STRING_3);
//		Map<String,String> ret4 = GEXFFileParseUtils.parseLineAttributeNameStringValueMap(TEST_STRING_4);
//		Map<String,String> ret5 = GEXFFileParseUtils.parseLineAttributeNameStringValueMap(TEST_STRING_5);
		
		
	}

	/**
	 * Test method for {@link generic.graph.reader.filebased.GEXFFileParseUtils#newAttributeDeclarationLine(java.lang.String)}.
	 */
	@Test
	void testNewAttributeDeclarationLine() {
		String test1 = "<attribute id=\"0\" title=\"url\" type=\"string\"/>";
		
		assertEquals(true, GEXFFileParseUtils.newAttributeDeclarationLine(test1));
	}
	
	/**
	 * Test method for {@link generic.graph.reader.filebased.GEXFFileParseUtils#parseAttribute(java.lang.String)}.
	 */
	@Test
	void testParseAttribute() {
		String test1 = "<attribute id=\"0\" title=\"url\" type=\"string\"/>";
		String test2 = "<attribute id=\"1\" title=\"indegree\" type=\"float\"/>";
		String test3 = " <attribute id=\"2\" title=\"frog\" type=\"boolean\"><default>true</default></attribute>";
		
		GEXFUserDefinedAttribute attribute1 = GEXFFileParseUtils.parseAttribute(test1);
		GEXFUserDefinedAttribute attribute2 = GEXFFileParseUtils.parseAttribute(test2);
		GEXFUserDefinedAttribute attribute3 = GEXFFileParseUtils.parseAttribute(test3);
		
		System.out.println();
	}

	/**
	 * Test method for {@link generic.graph.reader.filebased.GEXFFileParseUtils#parseNode(java.lang.String)}.
	 */
	@Test
	void testParseNode() {
		String test1 = "<node id=\"0\" label=\"Hello\"/>";
		String test2 = "<node id=\"0\" label=\"Gephi\"><attvalues><attvalue for=\"0\" value=\"http://gephi.org\"/><attvalue for=\"1\" value=\"1\"/></attvalues></node>";
		
//		Pair<Map<String,String>,Map<Integer,String>> result1 = GEXFFileParseUtils.parseNode(test1);
		Pair<Map<String,String>,Map<Integer,String>> result2 = GEXFFileParseUtils.parseNode(test2);
		
		System.out.println();
	}

}
