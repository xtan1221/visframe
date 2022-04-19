/**
 * 
 */
package generic.tree.reader.filebased.newick;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fileformat.vftree.VfTreeDataFileFormatType;

/**
 * @author tanxu
 *
 */
public class SimpleNewickFileTreeReaderTest {
	public static SimpleNewickFileTreeReader simpleNewickFileTreeReader;
	static final String DATA_FILE_DIR = "C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\vftree\\newick\\simple_newick_1\\mono50.bootstrap100.nwk";;
	static final String DATA_FILE_DIR2 = "C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\vftree\\newick\\simple_newick_1\\mono6813.bootstrap100.nwk";;
	
	
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
	 * Test method for {@link generic.tree.reader.filebased.newick.SimpleNewickFileTreeReader#SimpleNewickFileTreeReader(java.nio.file.Path, fileformat.vftree.VfTreeDataFileFormatType)}.
	 */
	@Test
	public void testSimpleNewickFileTreeReader() {
		Path treeFilePath = Paths.get(DATA_FILE_DIR2);
		VfTreeDataFileFormatType formatType = VfTreeDataFileFormatType.SIMPLE_NEWICK_1;
		simpleNewickFileTreeReader = new SimpleNewickFileTreeReader(treeFilePath, formatType);
	}
	

	/**
	 * Test method for {@link generic.tree.reader.filebased.newick.SimpleNewickFileTreeReader#perform()}.
	 * @throws IOException 
	 */
	@Test
	public void testPerform() throws IOException {
		testSimpleNewickFileTreeReader();
		simpleNewickFileTreeReader.perform();
		
		System.out.println("");
	}


}
