/**
 * 
 */
package importer.record.between;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fileformat.record.RecordDataFileFormat;
import fileformat.record.between.SequentialBetweenRecordStringFormat;
import fileformat.record.between.SequentialBetweenRecordStringFormatTest;
import utils.SerializationUtils;

/**
 * @author tanxu
 *
 */
class SequentialBetweenRecordStringParserTest {
	public static SequentialBetweenRecordStringParser GFF3_SequentialBetweenRecordStringParser;
	
	public static File GFF3_TESTING_DATA_FILE = new File("C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\record\\gff3\\23.gff3");
	
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
	 * Test method for {@link importer.record.between.SequentialBetweenRecordStringParser#SequentialBetweenRecordStringParser(java.io.File, fileformat.record.between.SequentialBetweenRecordStringFormat)}.
	 * @throws IOException 
	 */
	@Test
	void testSequentialBetweenRecordStringParser() throws IOException {
		SequentialBetweenRecordStringFormatTest sequentialBetweenRecordStringFormatTest = new SequentialBetweenRecordStringFormatTest();
		sequentialBetweenRecordStringFormatTest.testSequentialBetweenRecordStringFormat_gff3();
		
		RecordDataFileFormat gff3 = 
				(RecordDataFileFormat)SerializationUtils.deserializeFromFile(Paths.get("C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\record\\gff3\\GFF3_record_delimiter_single_new_line.VFF"));
//		RecordDataFileFormat gff3 = 
//				(RecordDataFileFormat)SerializationUtils.deserializeFromFile(Paths.get("C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\record\\gff3\\GFF3_double.VFF"));
		System.out.println(gff3.getBetweenRecordStringFormat().getRecordDelimiter().getStringValue());
//		GFF3_SequentialBetweenRecordStringParser = new SequentialBetweenRecordStringParser(GFF3_TESTING_DATA_FILE, SequentialBetweenRecordStringFormatTest.GFF3_SequentialBetweenRecordStringFormat);
		GFF3_SequentialBetweenRecordStringParser = new SequentialBetweenRecordStringParser(GFF3_TESTING_DATA_FILE, (SequentialBetweenRecordStringFormat)gff3.getBetweenRecordStringFormat());
		
		String line = null;
		while((line = GFF3_SequentialBetweenRecordStringParser.getNextRecordString())!=null) {
			System.out.println("==="+line);
		}
		System.out.println("done");
	}
	
}
