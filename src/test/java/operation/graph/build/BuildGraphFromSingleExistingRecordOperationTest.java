/**
 * 
 */
package operation.graph.build;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashSet;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import metadata.graph.vftree.VfTreeMandatoryEdgeDataTableSchemaUtils;
import operation.AbstractOperationTest;
import rdb.table.data.DataTableColumnName;

/**
 * @author tanxu
 *
 */
public class BuildGraphFromSingleExistingRecordOperationTest {
	public static Map<SimpleName, Object> buildGraphFromSingleExistingRecordOperationLevelSpecificParameterNameValueObjectMap;
	public static BuildGraphFromSingleExistingRecordOperation buildGraphFromSingleExistingRecordOperation;
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
	 * Test method for {@link operation.graph.build.BuildGraphFromSingleExistingRecordOperation#buildBuildGraphFromSingleExistingRecordOperationLevelSpecificParameterNameValueObjectMap(java.util.LinkedHashSet, java.util.LinkedHashSet)}.
	 */
	@Test
	public	void testBuildBuildGraphFromSingleExistingRecordOperationLevelSpecificParameterNameValueObjectMap() {
		LinkedHashSet<DataTableColumnName> sourceVertexIDColumnNameLinkedHashSet = new LinkedHashSet<>();
		sourceVertexIDColumnNameLinkedHashSet.add(VfTreeMandatoryEdgeDataTableSchemaUtils.parentNodeIDColumn().getName());
		LinkedHashSet<DataTableColumnName> sinkVertexIDColumnNameLinkedHashSet = new LinkedHashSet<>();
		sinkVertexIDColumnNameLinkedHashSet.add(VfTreeMandatoryEdgeDataTableSchemaUtils.childNodeIDColumn().getName());
		
		
		buildGraphFromSingleExistingRecordOperationLevelSpecificParameterNameValueObjectMap = 
				BuildGraphFromSingleExistingRecordOperation.buildBuildGraphFromSingleExistingRecordOperationLevelSpecificParameterNameValueObjectMap(
						sourceVertexIDColumnNameLinkedHashSet,
						sinkVertexIDColumnNameLinkedHashSet
						);
		
	}
	/**
	 * Test method for {@link operation.graph.build.BuildGraphFromSingleExistingRecordOperation#BuildGraphFromSingleExistingRecordOperation(java.util.Map, java.util.Map, java.util.Map)}.
	 */
	@Test
	public void testBuildGraphFromSingleExistingRecordOperation() {
		AbstractOperationTest abstractOperationTest = new AbstractOperationTest();
		abstractOperationTest.testBuildAbstractOperationLevelSpecificParameterNameValueObjectMap2();
		
		BuildGraphFromExistingRecordOperationBaseTest buildGraphFromExistingRecordOperationBaseTest = new BuildGraphFromExistingRecordOperationBaseTest();
		buildGraphFromExistingRecordOperationBaseTest.testBuildBuildGraphFromExistingRecordOperationBaseLevelSpecificParameterNameValueObjectMap2();
		
		this.testBuildBuildGraphFromSingleExistingRecordOperationLevelSpecificParameterNameValueObjectMap();
		
		
		buildGraphFromSingleExistingRecordOperation = new BuildGraphFromSingleExistingRecordOperation(
//				false,
				AbstractOperationTest.abstractOperationLevelSpecificParameterNameValueObjectMap,
				BuildGraphFromExistingRecordOperationBaseTest.buildGraphFromExistingRecordOperationBaseLevelSpecificParameterNameValueObjectMap,
				buildGraphFromSingleExistingRecordOperationLevelSpecificParameterNameValueObjectMap,
				true
				);
		
		for(SimpleName name:buildGraphFromSingleExistingRecordOperation.getAllParameterNameMapOfCurrentAndAboveLevels().keySet()) {
			System.out.println(buildGraphFromSingleExistingRecordOperation.getAllParameterNameMapOfCurrentAndAboveLevels().get(name));
		}
		
		System.out.println("==========================");
		for(SimpleName name:buildGraphFromSingleExistingRecordOperation.getAllParameterNameValueObjectMapOfCurrentAndAboveLevels().keySet()) {
			System.out.println(buildGraphFromSingleExistingRecordOperation.getAllParameterNameValueObjectMapOfCurrentAndAboveLevels().get(name));
		}
	}

	/**
	 * Test method for {@link operation.graph.build.BuildGraphFromExistingRecordOperationBase#call()}.
	 */
	@Test
	void testCall() {
		fail("Not yet implemented");
	}

}
