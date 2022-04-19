/**
 * 
 */
package importer.record.within;

import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fileformat.record.RecordDataFileFormat;
import fileformat.record.attribute.PrimitiveRecordAttributeFormat;
import fileformat.record.within.StringDelimitedRecordAttributeStringFormat;
import utils.SerializationUtils;

/**
 * @author tanxu
 *
 */
class StringDelimitedRecordAttributeStringParserTest {
	public static StringDelimitedRecordAttributeStringParser GFF3_StringDelimitedRecordAttributeStringParser;
	public static String GFF_RECORD_STRING ="Chr1	phytozomev10	CDS	5174	5326	.	+	0	ID=AT1G01010.1.TAIR10.CDS.5;Parent=AT1G01010.1.TAIR10;pacid=19656964";
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
	 * Test method for {@link importer.record.within.StringDelimitedRecordAttributeStringParser#parse(java.lang.String)}.
	 */
	@Test
	void testParse() {
		this.testStringDelimitedRecordAttributeStringParser();
		
		Map<PrimitiveRecordAttributeFormat,String> attributeStringValueMap = 
				GFF3_StringDelimitedRecordAttributeStringParser.parse(GFF_RECORD_STRING);
		
		
		for(PrimitiveRecordAttributeFormat attribute:attributeStringValueMap.keySet()) {
			System.out.println(attribute.toString()+"="+attributeStringValueMap.get(attribute));
		}
	}
	
	/**
	 * Test method for {@link importer.record.within.StringDelimitedRecordAttributeStringParser#StringDelimitedRecordAttributeStringParser(fileformat.record.within.StringDelimitedRecordAttributeStringFormat)}.
	 */
	@Test
	void testStringDelimitedRecordAttributeStringParser() {
//		StringDelimitedRecordAttributeStringFormatTest stringDelimitedRecordAttributeStringFormatTest = new StringDelimitedRecordAttributeStringFormatTest();
//		stringDelimitedRecordAttributeStringFormatTest.testStringDelimitedRecordAttributeStringFormat();
		
		RecordDataFileFormat gff3 = 
				(RecordDataFileFormat)SerializationUtils.deserializeFromFile(Paths.get("C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\record\\gff3\\GFF3_record_delimiter_single_new_line.VFF"));
		
//		GFF3_StringDelimitedRecordAttributeStringParser = new StringDelimitedRecordAttributeStringParser(StringDelimitedRecordAttributeStringFormatTest.GFF3_stringDelimitedRecordAttributeStringFormat);
		GFF3_StringDelimitedRecordAttributeStringParser = new StringDelimitedRecordAttributeStringParser((StringDelimitedRecordAttributeStringFormat)gff3.getWithinRecordAttributeStringFormat());
	}
}
