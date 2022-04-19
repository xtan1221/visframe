/**
 * 
 */
package generic.graph.reader.project;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import context.project.VisProjectDBContext;
import context.project.VisProjectDBContextTest;
import generic.graph.DirectedType;
import metadata.MetadataID;
import metadata.MetadataName;
import metadata.graph.vftree.VfTreeMandatoryEdgeDataTableSchemaUtils;
import metadata.graph.vftree.VfTreeMandatoryNodeDataTableSchemaUtils;
import rdb.table.data.DataTableColumnName;

/**
 * @author tanxu
 *
 */
class RecordToGraphReaderImpl2Test {
	public static RecordToGraphReaderImpl2 recordToGraphReaderImpl;
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
	 * Test method for {@link generic.graph.reader.project.RecordToGraphReaderImpl2#nextEdge()}.
	 */
	@Test
	void testNextEdge() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link generic.graph.reader.project.RecordToGraphReaderImpl2#RecordToGraphReaderImpl2(context.project.VisProjectDBContext, boolean, metadata.MetadataID, metadata.MetadataID, boolean, java.util.LinkedHashSet, java.util.LinkedHashSet, java.util.LinkedHashSet, java.util.LinkedHashSet, boolean, java.util.LinkedHashMap, java.util.LinkedHashMap, rdb.table.data.DataTableColumnName, java.util.Map, generic.graph.DirectedType)}.
	 */
	@Test
	void testRecordToGraphReaderImpl2() {
//		VisProjectDBContextTest visProjectDBContextTest = new VisProjectDBContextTest();
//		visProjectDBContextTest.testConnect();
//		
//		VisProjectDBContext hostVisProjectDBContext = VisProjectDBContextTest.TEST_PROJECT_1;
//		
//		boolean hasVertexDataSourceRecordData = true;
//		MetadataID vertexDataSourceRecordDataID = MetadataID.record(new MetadataName("mono50_NODE"));
//		MetadataID edgeDataSourceRecordDataID = MetadataID.record(new MetadataName("mono50_EDGE"));
//		boolean toFilterOutDuplicates = false;
//		
//		LinkedHashSet<DataTableColumnName> inputNodeDataTableColumnSetAsNodeID = new LinkedHashSet<>();
//		inputNodeDataTableColumnSetAsNodeID.add(VfTreeMandatoryNodeDataTableSchemaUtils.nodeIDColumn().getName());
//		LinkedHashSet<DataTableColumnName> inputNodeDataTableColumnSetAsAdditionalFeature = new LinkedHashSet<>();
//		
//		LinkedHashSet<DataTableColumnName> inputEdgeDataTableColumnSetAsEdgeID = new LinkedHashSet<>();
//		inputEdgeDataTableColumnSetAsEdgeID.add(VfTreeMandatoryEdgeDataTableSchemaUtils.parentNodeIDColumn().getName());
//		inputEdgeDataTableColumnSetAsEdgeID.add(VfTreeMandatoryEdgeDataTableSchemaUtils.childNodeIDColumn().getName());
//		LinkedHashSet<DataTableColumnName> inputEdgeDataTableColumnSetAsAdditionalFeature = new LinkedHashSet<>();
//		
//		boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets = false;
//		LinkedHashMap<DataTableColumnName,DataTableColumnName> nodeIDColumnNameEdgeSourceNodeIDColumnNameMap = new LinkedHashMap<>();
//		nodeIDColumnNameEdgeSourceNodeIDColumnNameMap.put(VfTreeMandatoryNodeDataTableSchemaUtils.nodeIDColumn().getName(), VfTreeMandatoryEdgeDataTableSchemaUtils.parentNodeIDColumn().getName());
//		LinkedHashMap<DataTableColumnName,DataTableColumnName> nodeIDColumnNameEdgeSinkNodeIDColumnNameMap = new LinkedHashMap<>();
//		nodeIDColumnNameEdgeSinkNodeIDColumnNameMap.put(VfTreeMandatoryNodeDataTableSchemaUtils.nodeIDColumn().getName(), VfTreeMandatoryEdgeDataTableSchemaUtils.childNodeIDColumn().getName());
//		
//		
//		DataTableColumnName edgeDirectednessIndicatorColumnName = 
//		Map<String, DirectedType> colStringValueDirectedTypeMap,
//		DirectedType defaultDirectedType = DirectedType.UNDIRECTED;
//		recordToGraphReaderImpl = new RecordToGraphReaderImpl2(
//				hostVisProjectDBContext, 
//				hasVertexDataSourceRecordData,
//				vertexDataSourceRecordDataID,
//				edgeDataSourceRecordDataID, toFilterOutDuplicates,
//				inputNodeDataTableColumnSetAsNodeID,
//				inputNodeDataTableColumnSetAsAdditionalFeature,
//				inputEdgeDataTableColumnSetAsEdgeID,
//				inputEdgeDataTableColumnSetAsAdditionalFeature,
//				edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets,
//				nodeIDColumnNameEdgeSourceNodeIDColumnNameMap,
//				nodeIDColumnNameEdgeSinkNodeIDColumnNameMap,
//				
//				edgeDirectednessIndicatorColumnName,
//				colStringValueDirectedTypeMap,
//				defaultDirectedType);
	}
	
	/**
	 * Test method for {@link generic.graph.reader.project.RecordToGraphReader#initialize()}.
	 */
	@Test
	void testInitialize() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link generic.graph.reader.project.RecordToGraphReader#nextVertex()}.
	 */
	@Test
	void testNextVertex() {
		fail("Not yet implemented");
	}

}
