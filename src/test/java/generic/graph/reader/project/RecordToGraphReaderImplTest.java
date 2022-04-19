/**
 * 
 */
package generic.graph.reader.project;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import context.project.VisProjectDBContext;
import context.project.VisProjectDBContextTest;
import generic.graph.DirectedType;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import metadata.MetadataID;
import metadata.MetadataName;
import metadata.graph.vftree.VfTreeMandatoryEdgeDataTableSchemaUtils;
import metadata.graph.vftree.VfTreeMandatoryNodeDataTableSchemaUtils;
import rdb.table.data.DataTableColumnName;

/**
 * 
 * test RecordToGraphReaderImpl with two input record data one for vertex and one for node;
 * 
 * see {@link RecordToGraphReaderImplTest2} for test with one single input record data;
 * 
 * @author tanxu
 *
 */
public class RecordToGraphReaderImplTest {
	public static RecordToGraphReaderImpl recordToGraphReaderImpl;
	
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
	 * Test method for {@link generic.graph.reader.project.RecordToGraphReaderImpl#RecordToGraphReaderImpl(context.project.VisProjectDBContext, boolean, metadata.MetadataID, metadata.MetadataID, boolean, java.util.LinkedHashSet, java.util.LinkedHashSet, java.util.LinkedHashSet, java.util.LinkedHashSet, boolean, java.util.LinkedHashMap, java.util.LinkedHashMap, generic.graph.DirectedType)}.
	 * @throws SQLException 
	 */
	@Test
	public void testRecordToGraphReaderImpl() throws SQLException {
		VisProjectDBContextTest visProjectDBContextTest = new VisProjectDBContextTest();
		visProjectDBContextTest.testConnect();
		
		VisProjectDBContext hostVisProjectDBContext = VisProjectDBContextTest.TEST_PROJECT_1;
		
		boolean hasVertexDataSourceRecordData = true;
		MetadataID vertexDataSourceRecordDataID = MetadataID.record(new MetadataName("mono_6813_3_NODE")); 
		MetadataID edgeDataSourceRecordDataID = MetadataID.record(new MetadataName("mono_6813_3_EDGE"));
		
//		MetadataID vertexDataSourceRecordDataID = MetadataID.record(new MetadataName("mono_1_2_NODE")); 
//		MetadataID edgeDataSourceRecordDataID = MetadataID.record(new MetadataName("mono_1_2_EDGE"));
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
		
		DirectedType defaultDirectedType = DirectedType.UNDIRECTED;
		recordToGraphReaderImpl = new RecordToGraphReaderImpl(
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
				nodeIDColumnNameEdgeSinkNodeIDColumnNameMap,
				defaultDirectedType);
	}
	

	/**
	 * Test method for {@link generic.graph.reader.project.RecordToGraphReader#initialize()}.
	 * @throws SQLException 
	 */
	@Test
	public void testInitialize() throws SQLException {
		testRecordToGraphReaderImpl();
		
		recordToGraphReaderImpl.initialize();
	}
	

	/**
	 * Test method for {@link generic.graph.reader.project.RecordToGraphReader#nextVertex()}.
	 * @throws SQLException 
	 */
	@Test
	void testNextVertex() throws SQLException {
		testInitialize();
		
		VfGraphVertex vertex;
		while((vertex=recordToGraphReaderImpl.nextVertex())!=null) {
			System.out.println(vertex.toString());
		}
	}

	/**
	 * Test method for {@link generic.graph.reader.project.RecordToGraphReaderImpl#nextEdge()}.
	 * @throws SQLException 
	 */
	@Test
	void testNextEdge() throws SQLException {
		testNextVertex();
		
		VfGraphEdge edge;
		while((edge=recordToGraphReaderImpl.nextEdge())!=null) {
			System.out.println(edge.simpleString());
		}
	}

}
