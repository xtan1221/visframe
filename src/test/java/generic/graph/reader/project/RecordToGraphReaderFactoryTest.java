/**
 * 
 */
package generic.graph.reader.project;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
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
class RecordToGraphReaderFactoryTest {
	public static RecordToGraphReaderFactory recordToGraphReaderFactory1;
	
	public static RecordToGraphReader recordToGraphReader1_DEFAULT_DIRECTED_TYPE;
	
	public static RecordToGraphReader recordToGraphReader1_WITH_DIRECTED_TYPE_INDICATOR_COLUMN;
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
	 * read from node and vertex record data of a vftree in a VisProjectDBContextTest;
	 * 
	 * Test method for {@link generic.graph.reader.project.RecordToGraphReaderFactory#RecordToGraphReaderFactory(context.project.VisProjectDBContext, boolean, metadata.MetadataID, metadata.MetadataID, boolean, java.util.LinkedHashSet, java.util.LinkedHashSet, java.util.LinkedHashSet, java.util.LinkedHashSet, boolean, java.util.LinkedHashMap, java.util.LinkedHashMap)}.
	 * @throws SQLException 
	 */
	@Test
	void testRecordToGraphReaderFactory1() throws SQLException {
		VisProjectDBContextTest visProjectDBContextTest = new VisProjectDBContextTest();
		visProjectDBContextTest.testConnect();
		
		VisProjectDBContext hostVisProjectDBContext = VisProjectDBContextTest.TEST_PROJECT_1;
		
		boolean hasVertexDataSourceRecordData = true;
		MetadataID vertexDataSourceRecordDataID = MetadataID.record(new MetadataName("mono50_NODE"));
		MetadataID edgeDataSourceRecordDataID = MetadataID.record(new MetadataName("mono50_EDGE"));
		boolean toFilterOutDuplicates = false;
		
		LinkedHashSet<DataTableColumnName> inputNodeDataTableColumnSetAsNodeID = new LinkedHashSet<>();
		inputNodeDataTableColumnSetAsNodeID.add(VfTreeMandatoryNodeDataTableSchemaUtils.nodeIDColumn().getName());
		LinkedHashSet<DataTableColumnName> inputNodeDataTableColumnSetAsAdditionalFeature = new LinkedHashSet<>();
		
		LinkedHashSet<DataTableColumnName> inputEdgeDataTableColumnSetAsEdgeID = new LinkedHashSet<>();
		inputEdgeDataTableColumnSetAsEdgeID.add(VfTreeMandatoryEdgeDataTableSchemaUtils.parentNodeIDColumn().getName());
		inputEdgeDataTableColumnSetAsEdgeID.add(VfTreeMandatoryEdgeDataTableSchemaUtils.childNodeIDColumn().getName());
		LinkedHashSet<DataTableColumnName> inputEdgeDataTableColumnSetAsAdditionalFeature = new LinkedHashSet<>();
		
		boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets = false;
		LinkedHashMap<DataTableColumnName,DataTableColumnName> nodeIDColumnNameEdgeSourceNodeIDColumnNameMap = new LinkedHashMap<>();
		nodeIDColumnNameEdgeSourceNodeIDColumnNameMap.put(VfTreeMandatoryNodeDataTableSchemaUtils.nodeIDColumn().getName(), VfTreeMandatoryEdgeDataTableSchemaUtils.parentNodeIDColumn().getName());
		LinkedHashMap<DataTableColumnName,DataTableColumnName> nodeIDColumnNameEdgeSinkNodeIDColumnNameMap = new LinkedHashMap<>();
		nodeIDColumnNameEdgeSinkNodeIDColumnNameMap.put(VfTreeMandatoryNodeDataTableSchemaUtils.nodeIDColumn().getName(), VfTreeMandatoryEdgeDataTableSchemaUtils.childNodeIDColumn().getName());
		
		recordToGraphReaderFactory1 = new RecordToGraphReaderFactory(
				hostVisProjectDBContext, 
				hasVertexDataSourceRecordData,
				vertexDataSourceRecordDataID,
				edgeDataSourceRecordDataID, toFilterOutDuplicates,
				inputNodeDataTableColumnSetAsNodeID,
				inputNodeDataTableColumnSetAsAdditionalFeature,
				inputEdgeDataTableColumnSetAsEdgeID,
				inputEdgeDataTableColumnSetAsAdditionalFeature,
				edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets,
				nodeIDColumnNameEdgeSourceNodeIDColumnNameMap,
				nodeIDColumnNameEdgeSinkNodeIDColumnNameMap);
	}
	
	/**
	 * Test method for {@link generic.graph.reader.project.RecordToGraphReaderFactory#build(generic.graph.DirectedType)}.
	 * @throws SQLException 
	 */
	@Test
	void testBuildDirectedType1() throws SQLException {
		this.testRecordToGraphReaderFactory1();
		
		recordToGraphReader1_DEFAULT_DIRECTED_TYPE = recordToGraphReaderFactory1.build(DirectedType.UNDIRECTED);
		
		
		
	}
	
	/**
	 * Test method for {@link generic.graph.reader.project.RecordToGraphReaderFactory#build(rdb.table.data.DataTableColumnName, java.util.Map, generic.graph.DirectedType)}.
	 * @throws SQLException 
	 */
	@Test
	void testBuildDataTableColumnNameMapOfStringDirectedTypeDirectedType() throws SQLException {
//		this.testRecordToGraphReaderFactory1();
//		
//		DataTableColumnName edgeDirectednessIndicatorColumnName;
//		Map<String, DirectedType> colStringValueDirectedTypeMap;
//		DirectedType defaultDirectedType;
//		
//		
//		recordToGraphReader1_WITH_DIRECTED_TYPE_INDICATOR_COLUMN = recordToGraphReaderFactory1.build(edgeDirectednessIndicatorColumnName, colStringValueDirectedTypeMap, defaultDirectedType);
	}

}
