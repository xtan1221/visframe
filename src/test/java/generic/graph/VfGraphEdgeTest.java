/**
 * 
 */
package generic.graph;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rdb.table.data.DataTableColumnName;

/**
 * @author tanxu
 *
 */
class VfGraphEdgeTest {
	static VfGraphEdge edge1;
	static VfGraphEdge edge2;
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
		/////
		Map<DataTableColumnName, String> IDAttributeNameStringValueMap = new LinkedHashMap<>();
		IDAttributeNameStringValueMap.put(new DataTableColumnName("id"), "1");
		
		boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets = true;
		Map<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap = new LinkedHashMap<>();
		vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap.put(new DataTableColumnName("id"), new DataTableColumnName("source"));
		Map<DataTableColumnName,String> sourceVertexIDAttributeNameStringValueMap = new LinkedHashMap<>();
		sourceVertexIDAttributeNameStringValueMap.put(new DataTableColumnName("source"), "222");
		Map<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap = new LinkedHashMap<>();
		vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap.put(new DataTableColumnName("id"), new DataTableColumnName("target"));
		Map<DataTableColumnName,String> sinkVertexIDAttributeNameStringValueMap = new LinkedHashMap<>();
		sinkVertexIDAttributeNameStringValueMap.put(new DataTableColumnName("target"), "333");
		Map<DataTableColumnName, String> additionalAttributeNameStringValueMap = new LinkedHashMap<>();
		boolean directed = true; 
		
		//
		edge1 = new VfGraphEdge(IDAttributeNameStringValueMap,edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets,
				vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap,
				sourceVertexIDAttributeNameStringValueMap,
				vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap,
				sinkVertexIDAttributeNameStringValueMap,
				additionalAttributeNameStringValueMap,
				directed
				);
		
		
		///////////////////
		Map<DataTableColumnName, String> IDAttributeNameStringValueMap2 = new LinkedHashMap<>();
		IDAttributeNameStringValueMap2.put(new DataTableColumnName("id"), "1");
		IDAttributeNameStringValueMap2.put(new DataTableColumnName("source"), "222");
		IDAttributeNameStringValueMap2.put(new DataTableColumnName("target"), "333");
		boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets2 = false;
		Map<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap2 = new LinkedHashMap<>();
		vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap2.put(new DataTableColumnName("id"), new DataTableColumnName("source"));
		Map<DataTableColumnName,String> sourceVertexIDAttributeNameStringValueMap2 = new LinkedHashMap<>();
		sourceVertexIDAttributeNameStringValueMap2.put(new DataTableColumnName("source"), "222");
		Map<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap2 = new LinkedHashMap<>();
		vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap2.put(new DataTableColumnName("id"), new DataTableColumnName("target"));
		Map<DataTableColumnName,String> sinkVertexIDAttributeNameStringValueMap2 = new LinkedHashMap<>();
		sinkVertexIDAttributeNameStringValueMap2.put(new DataTableColumnName("target"), "333");
		Map<DataTableColumnName, String> additionalAttributeNameStringValueMap2 = new LinkedHashMap<>();
		boolean directed2 = true; 
		
		//
		edge2 = new VfGraphEdge(IDAttributeNameStringValueMap2,edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets2,
				vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap2,
				sourceVertexIDAttributeNameStringValueMap2,
				vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap2,
				sinkVertexIDAttributeNameStringValueMap2,
				additionalAttributeNameStringValueMap2,
				directed2
				);
		
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link generic.graph.VfGraphEdge#oppositeEdge()}.
	 */
	@Test
	void testOppositeEdge() {
		
		VfGraphEdge oppositeEdge = edge1.oppositeEdge();
		
		System.out.println(edge1);
		System.out.println(oppositeEdge);
		System.out.println("================");
		
		VfGraphEdge oppositeEdge2 = edge2.oppositeEdge();
		
		System.out.println(edge2);
		System.out.println(oppositeEdge2);
		
	}

	/**
	 * Test method for {@link generic.graph.VfGraphEdge#equals(java.lang.Object)}.
	 */
	@Test
	void testEqualsObject() {
		assertEquals(false, edge1.equals(edge1.oppositeEdge()));
		assertEquals(false, edge2.equals(edge2.oppositeEdge()));
	}
	
}
