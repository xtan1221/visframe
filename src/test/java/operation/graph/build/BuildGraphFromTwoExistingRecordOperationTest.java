/**
 * 
 */
package operation.graph.build;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import context.project.VisProjectDBContextTest;
import context.project.process.logtable.StatusType;
import generic.graph.DirectedType;
import metadata.MetadataID;
import metadata.MetadataName;
import metadata.graph.type.GraphTypeEnforcer;
import metadata.graph.vftree.VfTreeMandatoryEdgeDataTableSchemaUtils;
import metadata.graph.vftree.VfTreeMandatoryNodeDataTableSchemaUtils;
import operation.AbstractOperationTest;
import rdb.table.data.DataTableColumnName;

/**
 * @author tanxu
 *
 */
public class BuildGraphFromTwoExistingRecordOperationTest {
	public static Map<SimpleName, Object> buildGraphFromTwoExistingRecordOperationLevelSpecificParameterNameValueObjectMap;
	
	public static BuildGraphFromTwoExistingRecordOperation buildGraphFromTwoExistingRecordOperation;
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
	 * Test method for {@link operation.graph.build.BuildGraphFromTwoExistingRecordOperation#buildBuildGraphFromTwoExistingRecordOperationLevelSpecificParameterNameValueObjectMap(metadata.MetadataID, java.util.LinkedHashSet, java.util.LinkedHashSet, metadata.MetadataID, java.util.LinkedHashSet, java.util.LinkedHashSet, boolean, java.util.LinkedHashMap, java.util.LinkedHashMap, metadata.graph.type.GraphTypeEnforcer, boolean, rdb.table.data.DataTableColumnName, java.util.Map, generic.graph.DirectedType, boolean, metadata.MetadataName)}.
	 */
	@Test
	void testbuildBuildGraphFromTwoExistingRecordOperationLevelSpecificParameterNameValueObjectMap() {
		
		MetadataID inputNodeRecordDataMetadataID = MetadataID.record(new MetadataName("mono_50_NODE"));
		LinkedHashSet<DataTableColumnName> inputNodeDataTableColumnSetAsNodeID = new LinkedHashSet<>();
		inputNodeDataTableColumnSetAsNodeID.add(VfTreeMandatoryNodeDataTableSchemaUtils.nodeIDColumn().getName());
		LinkedHashSet<DataTableColumnName> inputNodeDataTableColumnSetAsAdditionalFeature = new LinkedHashSet<>();

		LinkedHashMap<DataTableColumnName,DataTableColumnName> nodeIDColumnNameEdgeSourceNodeIDColumnNameMap = new LinkedHashMap<>();
		nodeIDColumnNameEdgeSourceNodeIDColumnNameMap.put(VfTreeMandatoryNodeDataTableSchemaUtils.nodeIDColumn().getName(), VfTreeMandatoryEdgeDataTableSchemaUtils.parentNodeIDColumn().getName());
		LinkedHashMap<DataTableColumnName,DataTableColumnName> nodeIDColumnNameEdgeSinkNodeIDColumnNameMap = new LinkedHashMap<>();
		nodeIDColumnNameEdgeSinkNodeIDColumnNameMap.put(VfTreeMandatoryNodeDataTableSchemaUtils.nodeIDColumn().getName(), VfTreeMandatoryEdgeDataTableSchemaUtils.childNodeIDColumn().getName());
		
		boolean toAddDiscoveredVertexFromInputEdgeDataTable = false;
		
		buildGraphFromTwoExistingRecordOperationLevelSpecificParameterNameValueObjectMap = 
				BuildGraphFromTwoExistingRecordOperation.buildBuildGraphFromTwoExistingRecordOperationLevelSpecificParameterNameValueObjectMap(
						inputNodeRecordDataMetadataID,
						inputNodeDataTableColumnSetAsNodeID, 
						inputNodeDataTableColumnSetAsAdditionalFeature, 
						nodeIDColumnNameEdgeSourceNodeIDColumnNameMap,
						nodeIDColumnNameEdgeSinkNodeIDColumnNameMap, 
						toAddDiscoveredVertexFromInputEdgeDataTable);
	}
	
	/**
	 * Test method for {@link operation.graph.build.BuildGraphFromTwoExistingRecordOperation#BuildGraphFromTwoExistingRecordOperation(java.util.Map, java.util.Map)}.
	 */
	@Test
	public void testBuildGraphFromTwoExistingRecordOperation() {
		AbstractOperationTest abstractOperationTest = new AbstractOperationTest();
		abstractOperationTest.testBuildAbstractOperationLevelSpecificParameterNameValueObjectMap();
		
		BuildGraphFromExistingRecordOperationBaseTest buildGraphFromExistingRecordOperationBaseTest = new BuildGraphFromExistingRecordOperationBaseTest();
		buildGraphFromExistingRecordOperationBaseTest.testBuildBuildGraphFromExistingRecordOperationBaseLevelSpecificParameterNameValueObjectMap();
		
		this.testbuildBuildGraphFromTwoExistingRecordOperationLevelSpecificParameterNameValueObjectMap();
		
		
		buildGraphFromTwoExistingRecordOperation = new BuildGraphFromTwoExistingRecordOperation(
//				false,
				AbstractOperationTest.abstractOperationLevelSpecificParameterNameValueObjectMap,
				BuildGraphFromExistingRecordOperationBaseTest.buildGraphFromExistingRecordOperationBaseLevelSpecificParameterNameValueObjectMap,
				buildGraphFromTwoExistingRecordOperationLevelSpecificParameterNameValueObjectMap,
				true
				);
		
		for(SimpleName name:buildGraphFromTwoExistingRecordOperation.getAllParameterNameMapOfCurrentAndAboveLevels().keySet()) {
			System.out.println(buildGraphFromTwoExistingRecordOperation.getAllParameterNameMapOfCurrentAndAboveLevels().get(name));
		}
		
		System.out.println("==========================");
		for(SimpleName name:buildGraphFromTwoExistingRecordOperation.getAllParameterNameValueObjectMapOfCurrentAndAboveLevels().keySet()) {
			System.out.println(buildGraphFromTwoExistingRecordOperation.getAllParameterNameValueObjectMapOfCurrentAndAboveLevels().get(name));
		}
	}
	
	/**
	 * Test method for {@link operation.graph.build.BuildGraphFromTwoExistingRecordOperation#call()}.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	void testCall() throws SQLException, IOException {
		VisProjectDBContextTest visProjectDBContextTest = new VisProjectDBContextTest();
		visProjectDBContextTest.testConnect();
		
		testBuildGraphFromTwoExistingRecordOperation();
		
		buildGraphFromTwoExistingRecordOperation.setHostVisProjectDBContext(VisProjectDBContextTest.TEST_PROJECT_1);
		
		StatusType st = buildGraphFromTwoExistingRecordOperation.call();
		
		System.out.println(st);
	}

}
