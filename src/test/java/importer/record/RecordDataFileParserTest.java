/**
 * 
 */
package importer.record;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import context.project.VisProjectDBContext;
import context.project.VisProjectDBContextTest;

/**
 * @author tanxu
 *
 */
class RecordDataFileParserTest {
	public static RecordDataFileParser GFF3_RecordDataFileParser;
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
	 * Test method for {@link importer.record.RecordDataFileParser#RecordDataFileParser(context.project.VisProjectDBContext, importer.record.RecordDataImporter)}.
	 * @throws SQLException 
	 */
	@Test
	void testRecordDataFileParser() throws SQLException {
		VisProjectDBContextTest visProjectDBContextTest = new VisProjectDBContextTest();
		visProjectDBContextTest.testConnect();
		
		RecordDataImporterTest recordDataImporterTest = new RecordDataImporterTest();
		recordDataImporterTest.testRecordDataImporter();
		
		GFF3_RecordDataFileParser = new RecordDataFileParser(VisProjectDBContextTest.TEST_PROJECT_1,RecordDataImporterTest.GFF3_RecordDataImporter);
		
//		VisProjectDBContextTest.TEST_PROJECT_1.disconnect();
	}
	
	
//	/**
//	 * Test method for {@link importer.record.RecordDataFileParser#perform()}.
//	 * @throws SQLException 
//	 */
//	@Test
//	void testPerform() throws SQLException {
//		this.testRecordDataFileParser();
//		
//		
//		
//		GFF3_RecordDataFileParser.perform();
//		
//		
//		
//		VisProjectDBContextTest.TEST_PROJECT_1.disconnect();
//	}

}
