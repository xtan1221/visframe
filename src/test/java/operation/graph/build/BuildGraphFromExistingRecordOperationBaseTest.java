/**
 * 
 */
package operation.graph.build;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import generic.graph.DirectedType;
import metadata.MetadataID;
import metadata.MetadataName;
import metadata.graph.type.GraphTypeEnforcer;
import metadata.graph.vftree.VfTreeMandatoryEdgeDataTableSchemaUtils;
import metadata.graph.vftree.VfTreeMandatoryNodeDataTableSchemaUtils;
import rdb.table.data.DataTableColumnName;

/**
 * @author tanxu
 *
 */
public class BuildGraphFromExistingRecordOperationBaseTest {
	public static Map<SimpleName, Object> buildGraphFromExistingRecordOperationBaseLevelSpecificParameterNameValueObjectMap;
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
	 * Test method for {@link operation.graph.build.BuildGraphFromExistingRecordOperationBase#buildBuildGraphFromExistingRecordOperationBaseLevelSpecificParameterNameValueObjectMap(metadata.MetadataID, java.util.LinkedHashSet, java.util.LinkedHashSet, boolean, metadata.graph.type.GraphTypeEnforcer, boolean, rdb.table.data.DataTableColumnName, java.util.Map, generic.graph.DirectedType, metadata.MetadataName)}.
	 */
	@Test
	public void testBuildBuildGraphFromExistingRecordOperationBaseLevelSpecificParameterNameValueObjectMap() {
		
		MetadataID inputEdgeRecordDataMetadataID = MetadataID.record(new MetadataName("mono_50_EDGE"));
		LinkedHashSet<DataTableColumnName> inputEdgeDataTableColumnSetAsEdgeID = new LinkedHashSet<>();
		inputEdgeDataTableColumnSetAsEdgeID.add(VfTreeMandatoryEdgeDataTableSchemaUtils.parentNodeIDColumn().getName());
		inputEdgeDataTableColumnSetAsEdgeID.add(VfTreeMandatoryEdgeDataTableSchemaUtils.childNodeIDColumn().getName());
		LinkedHashSet<DataTableColumnName> inputEdgeDataTableColumnSetAsAdditionalFeature = new LinkedHashSet<>();
		
		boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets = false;//vftree's edge record data's source/sink node id columns = edge id columns

		
		GraphTypeEnforcer graphTypeEnforcer = new GraphTypeEnforcer(
				false, //boolean toForceDirected,
				null,//DirectedEnforcingMode directedForcingMode,
				true,//boolean toForceUndirected,
				true,//boolean toForceNoParallelEdges,
				true//boolean toForceNoSelfLoops);
				);
		
		boolean hasDirectedTypeIndicatorColumn = false;
		DataTableColumnName directedTypeIndicatorColumnName = null;
		HashMap<String,DirectedType> directedIndicatorColumnStringValueDirectedTypeMap = null;
		DirectedType defaultDirectedType = DirectedType.UNDIRECTED;
		
		
		MetadataName outputGraphDataName = new MetadataName("graph_built_from_mono50_7");
		
		buildGraphFromExistingRecordOperationBaseLevelSpecificParameterNameValueObjectMap = BuildGraphFromExistingRecordOperationBase.buildBuildGraphFromExistingRecordOperationBaseLevelSpecificParameterNameValueObjectMap(
				inputEdgeRecordDataMetadataID, 
				inputEdgeDataTableColumnSetAsEdgeID, 
				inputEdgeDataTableColumnSetAsAdditionalFeature, 
				edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets, 
				graphTypeEnforcer, 
				hasDirectedTypeIndicatorColumn, 
				directedTypeIndicatorColumnName, 
				directedIndicatorColumnStringValueDirectedTypeMap, 
				defaultDirectedType, 
				outputGraphDataName);
		
	}
	
	/**
	 * Test method for {@link operation.graph.build.BuildGraphFromExistingRecordOperationBase#buildBuildGraphFromExistingRecordOperationBaseLevelSpecificParameterNameValueObjectMap(metadata.MetadataID, java.util.LinkedHashSet, java.util.LinkedHashSet, boolean, metadata.graph.type.GraphTypeEnforcer, boolean, rdb.table.data.DataTableColumnName, java.util.Map, generic.graph.DirectedType, metadata.MetadataName)}.
	 */
	@Test
	public void testBuildBuildGraphFromExistingRecordOperationBaseLevelSpecificParameterNameValueObjectMap2() {
		
		MetadataID inputEdgeRecordDataMetadataID = MetadataID.record(new MetadataName("mono_50_EDGE"));
		LinkedHashSet<DataTableColumnName> inputEdgeDataTableColumnSetAsEdgeID = new LinkedHashSet<>();
		inputEdgeDataTableColumnSetAsEdgeID.add(VfTreeMandatoryEdgeDataTableSchemaUtils.parentNodeIDColumn().getName());
		inputEdgeDataTableColumnSetAsEdgeID.add(VfTreeMandatoryEdgeDataTableSchemaUtils.childNodeIDColumn().getName());
		LinkedHashSet<DataTableColumnName> inputEdgeDataTableColumnSetAsAdditionalFeature = new LinkedHashSet<>();
		
		boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets = false;//vftree's edge record data's source/sink node id columns = edge id columns

		GraphTypeEnforcer graphTypeEnforcer = new GraphTypeEnforcer(
				false, //boolean toForceDirected,
				null,//DirectedEnforcingMode directedForcingMode,
				true,//boolean toForceUndirected,
				true,//boolean toForceNoParallelEdges,
				true//boolean toForceNoSelfLoops);
				);
		
		boolean hasDirectedTypeIndicatorColumn = false;
		DataTableColumnName directedTypeIndicatorColumnName = null;
		HashMap<String,DirectedType> directedIndicatorColumnStringValueDirectedTypeMap = null;
		DirectedType defaultDirectedType = DirectedType.UNDIRECTED;
		
		
		MetadataName outputGraphDataName = new MetadataName("graph_built_from_mono50_7");
		
		buildGraphFromExistingRecordOperationBaseLevelSpecificParameterNameValueObjectMap = BuildGraphFromExistingRecordOperationBase.buildBuildGraphFromExistingRecordOperationBaseLevelSpecificParameterNameValueObjectMap(
				inputEdgeRecordDataMetadataID, 
				inputEdgeDataTableColumnSetAsEdgeID, 
				inputEdgeDataTableColumnSetAsAdditionalFeature, 
				edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets, 
				graphTypeEnforcer, 
				hasDirectedTypeIndicatorColumn, 
				directedTypeIndicatorColumnName, 
				directedIndicatorColumnStringValueDirectedTypeMap, 
				defaultDirectedType, 
				outputGraphDataName);
		
	}

}
