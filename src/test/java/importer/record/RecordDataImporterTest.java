/**
 * 
 */
package importer.record;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContextTest;
import context.project.process.logtable.StatusType;
import fileformat.FileFormatID;
import fileformat.record.RecordDataFileFormat.PrimaryKeyAttributeNameSet;
import metadata.DataType;
import metadata.MetadataName;

/**
 * @author tanxu
 *
 */
public class RecordDataImporterTest {
	public static RecordDataImporter GFF3_RecordDataImporter;
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
	 * Test method for {@link importer.record.RecordDataImporter#RecordDataImporter(basic.VfNotes, java.nio.file.Path, fileformat.FileFormatID, metadata.MetadataName, fileformat.record.RecordDataFileFormat.PrimaryKeyAttributeNameSet, boolean, java.util.Set, boolean, java.util.Set)}.
	 */
	@Test
	public void testRecordDataImporter() {
		VfNotes notes = VfNotes.makeVisframeDefinedVfNotes();
		Path dataSourcePath = Paths.get("C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\record\\gff3\\23.gff3");
		FileFormatID fileFormatID = new FileFormatID(new SimpleName("GFF3_revised"),DataType.RECORD);
		MetadataName mainImportedMetadataName = new MetadataName("TestGff3");
		PrimaryKeyAttributeNameSet alternativePrimaryKeyAttributeNameSet = null;
		boolean toIncludeAllMandatoryPrimitiveRecordAttributesInResultedDataTable = true;
		Set<SimpleName> mandatorySimpleRecordAttributeNameSetIncludedInResultedDataTable = null;
		boolean toIncludeAllDiscoverdTagAttriubteInResultedDataTable = true;
		Set<SimpleName> discoveredTagSimpleRecordAttributeNameSetIncludedInResultedDataTable = null;
		
		GFF3_RecordDataImporter = new RecordDataImporter(
				notes, dataSourcePath, fileFormatID, mainImportedMetadataName,
				//
				alternativePrimaryKeyAttributeNameSet,
				toIncludeAllMandatoryPrimitiveRecordAttributesInResultedDataTable,
				mandatorySimpleRecordAttributeNameSetIncludedInResultedDataTable,
				toIncludeAllDiscoverdTagAttriubteInResultedDataTable,
				discoveredTagSimpleRecordAttributeNameSetIncludedInResultedDataTable);
	}
	
	/**
	 * Test method for {@link importer.record.RecordDataImporter#readAndParseIntoDataTables()}.
	 */
	@Test
	void testReadAndParseIntoDataTables() {
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link importer.record.RecordDataImporter#call()}.
	 * 
	 * 
	 * this test can only proceeds till the {@link VisProjectHasIDTypeRelationalTableSchemaManagerBase#insert(T)} method is reached, which requires a running process in the host VisProjectDBContext;
	 * 
	 * thus to test this method, need to comment out the specific line in that method;
	 * 
	 * @throws SQLException 
	 */
	@Test
	void testCall() throws SQLException {
		this.testRecordDataImporter();
		
		VisProjectDBContextTest visProjectDBContextTest = new VisProjectDBContextTest();
		visProjectDBContextTest.testConnect();
		
		GFF3_RecordDataImporter.setHostVisProjectDBContext(VisProjectDBContextTest.TEST_PROJECT_1);
		
		try {
			StatusType result = GFF3_RecordDataImporter.call();
			
			System.out.println(result);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
