/**
 * 
 */
package generic.tree.reader.projectbased;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import context.project.VisProjectDBContext;
import context.project.VisProjectDBContextTest;
import generic.tree.reader.filebased.newick.SimpleNewickFileTreeReader;
import metadata.DataType;
import metadata.MetadataID;
import metadata.MetadataName;
import rdb.table.data.DataTableColumnName;

/**
 * @author tanxu
 *
 */
public class VfDataTreeReaderTest {
	public static VfDataTreeReader dataTreeReader;
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
	 * Test method for {@link generic.tree.reader.projectbased.VfDataTreeReader#VfDataTreeReader(context.project.VisProjectDBContext, metadata.MetadataID, java.util.LinkedHashSet, java.util.LinkedHashSet)}.
	 * @throws SQLException 
	 */
	@Test
	public void testVfDataTreeReader() throws SQLException {
		
		VisProjectDBContextTest visProjectDBContextTest = new VisProjectDBContextTest(); 
		visProjectDBContextTest.testConnect();
		
		
		MetadataID treeDataMetadataID = new MetadataID(new MetadataName("mono_6813_3"), DataType.vfTREE);
		
		LinkedHashSet<DataTableColumnName> nonMandatoryNodeAdditionalFeaturesColNameSetToBeIncluded = new LinkedHashSet<>();
		nonMandatoryNodeAdditionalFeaturesColNameSetToBeIncluded.add(SimpleNewickFileTreeReader.newickTreeNodeLabelStringColumn().getName());
		LinkedHashSet<DataTableColumnName> nonMandatoryEdgeAdditionalFeaturesColNameSetToBeIncluded = new LinkedHashSet<>();
		
		dataTreeReader = new VfDataTreeReader(VisProjectDBContextTest.TEST_PROJECT_1, treeDataMetadataID, nonMandatoryNodeAdditionalFeaturesColNameSetToBeIncluded, nonMandatoryEdgeAdditionalFeaturesColNameSetToBeIncluded);
		
		
	}


	/**
	 * Test method for {@link generic.tree.reader.projectbased.VfDataTreeReader#perform()}.
	 * @throws SQLException 
	 */
	@Test
	public void testPerform() throws SQLException {
		testVfDataTreeReader();
		dataTreeReader.perform();
		
		dataTreeReader.getNodeIDMap().forEach((k,v)->{
//			System.out.println(v);
		});
	}
}
