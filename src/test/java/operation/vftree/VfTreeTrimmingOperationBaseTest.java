/**
 * 
 */
package operation.vftree;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashSet;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import metadata.MetadataName;
import rdb.table.data.DataTableColumnName;

/**
 * @author tanxu
 *
 */
public class VfTreeTrimmingOperationBaseTest {
	public static Map<SimpleName, Object> vfTreeTrimmingOperationBaseLevelSpecificParameterNameValueObjectMap;
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
	 * Test method for {@link operation.vftree.VfTreeTrimmingOperationBase#buildVfTreeTrimmingOperationBaseLevelSpecificParameterNameValueObjectMap(java.util.LinkedHashSet, java.util.LinkedHashSet, metadata.MetadataName)}.
	 */
	@Test
	public void testBuildVfTreeTrimmingOperationBaseLevelSpecificParameterNameValueObjectMap() {
		
		LinkedHashSet<DataTableColumnName> inputNodeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData = new LinkedHashSet<>();
		LinkedHashSet<DataTableColumnName> inputEdgeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData = new LinkedHashSet<>();
//		MetadataName outputVfTreeDataName = new MetadataName("subtree_mono_6813_3_tree_1");
		MetadataName outputVfTreeDataName = new MetadataName("sibling_reorder_mono_6813_3_tree");
		
//		vfTreeTrimmingOperationBaseLevelSpecificParameterNameValueObjectMap = 
//				VfTreeTrimmingOperationBase.buildVfTreeTrimmingOperationBaseLevelSpecificParameterNameValueObjectMap(
//						inputNodeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData, 
//						inputEdgeRecordNonMandatoryAdditionalFeatureColumnSetToKeepInOutputVfTreeData, 
//						outputVfTreeDataName);
	}

}
