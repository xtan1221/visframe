package importer.record.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fileformat.record.RecordDataFileFormat;
import fileformat.record.utils.PlainStringMarker;
import fileformat.record.utils.RegexStringMarker;
import fileformat.record.utils.StringMarkerUtils;
import utils.SerializationUtils;

class StringMarkerUtilsTest {
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void testS1CoversS2() {
		fail("Not yet implemented");
	}
	
	@Test
	void testContains() {
		String dataString = "aaaaaaaaa";
		
		PlainStringMarker marker = new PlainStringMarker("a",true);
		
		assertEquals(true, StringMarkerUtils.contains(dataString, marker));
		
		
		dataString = "aaaaaaaaa";
		
		RegexStringMarker marker2 = new RegexStringMarker("\\d",false);
		
		assertEquals(false, StringMarkerUtils.contains(dataString, marker2));
		
	}
	
	@Test
	void testRemoveTrailing() {
		String dataString = "aaaaaaaaa";
		PlainStringMarker delimiter = new PlainStringMarker("a",true);
		
		assertEquals("",StringMarkerUtils.removeTrailing(dataString, delimiter, true));
		
		dataString = "abcdefg";
		delimiter = new PlainStringMarker("bc",true);
		
		assertEquals("a",StringMarkerUtils.removeTrailing(dataString, delimiter, true));
		
	}

	@Test
	void testSplit() {
		fail("Not yet implemented");
	}

	
	/**
	 * Test method for {@link fileformat.record.utils.StringMarkerUtils#isNewLineCharacter(java.lang.String)}.
	 */
	@Test
	void testIsNewLineCharacter() {
//		String s = "\n";
//		StringMarker recordDelimiter = new RegexStringMarker("\n",false);
//		
//		RecordDataFileFormat gff3 = 
//				(RecordDataFileFormat)SerializationUtils.deserializeFromFile(Paths.get("C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\record\\gff3\\GFF3_single.VFF"));
//		RecordDataFileFormat gff3_2 = 
//				(RecordDataFileFormat)SerializationUtils.deserializeFromFile(Paths.get("C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\record\\gff3\\GFF3_double.VFF"));
//		RecordDataFileFormat gff3_3 = 
//				(RecordDataFileFormat)SerializationUtils.deserializeFromFile(Paths.get("C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\record\\gff3\\GFF3_triple.VFF"));
//		
//		RecordDataFileFormat gff3_4 = 
//				(RecordDataFileFormat)SerializationUtils.deserializeFromFile(Paths.get("C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\record\\gff3\\tab.VFF"));
		
//		System.out.print("aaaaaaaa".concat(gff3.getBetweenRecordStringFormat().getRecordDelimiter().getStringValue()));
//		System.out.print("aaaaaaaa".concat("\\n"));
//		System.out.print("aaaaaaaa".concat("\n"));
//		System.out.println("new line");
//		
//		System.out.println(gff3.getBetweenRecordStringFormat().getRecordDelimiter().getStringValue().hashCode());
//		System.out.println("\n".hashCode());
		
		
//		String in = "a\\tb\\n\\\"c\\\"";
		String in = "\\s+";
		
	    System.out.println(in);
	    // a\tb\n\"c\"

	    String out = StringEscapeUtils.unescapeJava(in);
	    //a	b
	    //"c"
	    
	    
	    System.out.println(out);
//		assertEquals(true,StringMarkerUtils.isNewLineCharacter(recordDelimiter.getStringValue()));
		
	}
}
