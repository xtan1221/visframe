/**
 * 
 */
package importer.vftree.newick;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.VfNotes;
import context.project.VisProjectDBContextTest;
import context.project.process.logtable.StatusType;
import fileformat.FileFormatID;
import fileformat.vftree.VfTreeDataFileFormatType;
import metadata.MetadataName;

/**
 * @author tanxu
 *
 */
public class SimpleNewickVfTreeDataImporterTest {
	static final String TEST_DATA_FILE = "C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\vftree\\newick\\simple_newick_1\\mono53.bootstrap100.nwk";
	public static SimpleNewickVfTreeDataImporter simpleNewick1VfTreeDataImporter;
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
	 * Test method for {@link importer.vftree.newick.SimpleNewickVfTreeDataImporter#SimpleNewickVfTreeDataImporter(basic.VfNotes, java.nio.file.Path, fileformat.FileFormatID, metadata.MetadataName, java.lang.Integer)}.
	 */
	@Test
	public void testSimpleNewickVfTreeDataImporter() {
		VfNotes notes = VfNotes.makeVisframeDefinedVfNotes();
		Path dataSourcePath = Paths.get(TEST_DATA_FILE);
		FileFormatID fileFormatID = VfTreeDataFileFormatType.SIMPLE_NEWICK_1.getFileFormat().getID();
		MetadataName mainImportedMetadataName = new MetadataName("simple_newick_1_mono53");
		Integer bootstrapIteration = 100;
		
		simpleNewick1VfTreeDataImporter = new SimpleNewickVfTreeDataImporter(notes, dataSourcePath, fileFormatID, mainImportedMetadataName, bootstrapIteration);
		
	}
	
	/**
	 * Test method for {@link importer.vftree.newick.SimpleNewickVfTreeDataImporter#call()}.
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Test
	void testCall() throws SQLException, IOException {
		
		testSimpleNewickVfTreeDataImporter();
		
		VisProjectDBContextTest visProjectDBContextTest = new VisProjectDBContextTest();
		visProjectDBContextTest.testConnect();
		
		
		simpleNewick1VfTreeDataImporter.setHostVisProjectDBContext(VisProjectDBContextTest.TEST_PROJECT_1);
		
		
		try {
			StatusType result = simpleNewick1VfTreeDataImporter.call();
			
			System.out.println(result);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}


}
