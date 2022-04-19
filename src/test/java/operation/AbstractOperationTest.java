/**
 * 
 */
package operation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import basic.VfNotes;

/**
 * @author tanxu
 *
 */
public class AbstractOperationTest {
	public static Map<SimpleName, Object> abstractOperationLevelSpecificParameterNameValueObjectMap;
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
	 * Test method for {@link operation.AbstractOperation#buildAbstractOperationLevelSpecificParameterNameValueObjectMap(operation.OperationName, basic.VfNotes)}.
	 */
	@Test
	public void testBuildAbstractOperationLevelSpecificParameterNameValueObjectMap() {
		OperationName name = new OperationName("test_operation_9"); 
		VfNotes notes = VfNotes.makeVisframeDefinedVfNotes();
		
		abstractOperationLevelSpecificParameterNameValueObjectMap = 
				AbstractOperation.buildAbstractOperationLevelSpecificParameterNameValueObjectMap(name, notes);
		
	}

	/**
	 * Test method for {@link operation.AbstractOperation#buildAbstractOperationLevelSpecificParameterNameValueObjectMap(operation.OperationName, basic.VfNotes)}.
	 */
	@Test
	public void testBuildAbstractOperationLevelSpecificParameterNameValueObjectMap2() {
		OperationName name = new OperationName("test_build_graph_from_single_record_operation"); 
		VfNotes notes = VfNotes.makeVisframeDefinedVfNotes();
		
		abstractOperationLevelSpecificParameterNameValueObjectMap = 
				AbstractOperation.buildAbstractOperationLevelSpecificParameterNameValueObjectMap(name, notes);
		
	}
	
	/**
	 * transform graph operation
	 * from vftree to graph
	 * Test method for {@link operation.AbstractOperation#buildAbstractOperationLevelSpecificParameterNameValueObjectMap(operation.OperationName, basic.VfNotes)}.
	 */
	@Test
	public void testBuildAbstractOperationLevelSpecificParameterNameValueObjectMap3() {
//		OperationName name = new OperationName("transform_vftree_to_graph_bi_directed"); 
//		OperationName name = new OperationName("circular_layout_test1"); 
//		OperationName name = new OperationName("spring_layout_test1"); 
//		OperationName name = new OperationName("reroot_tree_test1"); 
//		OperationName name = new OperationName("sub_tree_test1"); 
//		OperationName name = new OperationName("sibling_reorder_tree_test1"); 
//		OperationName name = new OperationName("group_and_bin_count_min0_max7500_2"); 
//		OperationName name = new OperationName("group_and_bin_count_desc_sorted_test1"); 
//		OperationName name = new OperationName("add_numeric_col_simple_index");
		OperationName name = new OperationName("add_numeric_col_3");
		VfNotes notes = VfNotes.makeVisframeDefinedVfNotes();
		
		abstractOperationLevelSpecificParameterNameValueObjectMap = 
				AbstractOperation.buildAbstractOperationLevelSpecificParameterNameValueObjectMap(name, notes);
		
	}
}
