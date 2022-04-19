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
import utils.Pair;

/**
 * @author tanxu
 *
 */
class VfGraphEdgeUtilsTest {
	static DataTableColumnName vertexIDAttributeName1 = new DataTableColumnName("v1");
	static DataTableColumnName vertexIDAttributeName2 = new DataTableColumnName("v2");
	static DataTableColumnName vertexIDAttributeName3 = new DataTableColumnName("v3");

	static DataTableColumnName edgeSourceVertexIDAttributeName1 = new DataTableColumnName("es1");
	static DataTableColumnName edgeSourceVertexIDAttributeName2 = new DataTableColumnName("es2");
	static DataTableColumnName edgeSourceVertexIDAttributeName3 = new DataTableColumnName("es3");
	
	static DataTableColumnName edgeTargetVertexIDAttributeName1 = new DataTableColumnName("et1");
	static DataTableColumnName edgeTargetVertexIDAttributeName2 = new DataTableColumnName("et2");
	static DataTableColumnName edgeTargetVertexIDAttributeName3 = new DataTableColumnName("et3");
	
	
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
	 * Test method for {@link generic.graph.VfGraphEdgeUtils#swap(java.util.Map, java.util.Map, java.util.Map, java.util.Map)}.
	 */
	@Test
	void testSwap() {
		Map<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap = new LinkedHashMap<>();
		Map<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap = new LinkedHashMap<>();
		Map<DataTableColumnName, String> sourceVertexIDAttributeNameStringValueMap = new LinkedHashMap<>();
		Map<DataTableColumnName, String> sinkVertexIDAttributeNameStringValueMap = new LinkedHashMap<>();
		
		vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap.put(vertexIDAttributeName1,  edgeSourceVertexIDAttributeName1);
		vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap.put(vertexIDAttributeName2,  edgeSourceVertexIDAttributeName2);
		vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap.put(vertexIDAttributeName3,  edgeSourceVertexIDAttributeName3);
		
		vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap.put(vertexIDAttributeName1,  edgeTargetVertexIDAttributeName1);
		vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap.put(vertexIDAttributeName2,  edgeTargetVertexIDAttributeName2);
		vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap.put(vertexIDAttributeName3,  edgeTargetVertexIDAttributeName3);
		
		sourceVertexIDAttributeNameStringValueMap.put(edgeSourceVertexIDAttributeName1, "1");
		sourceVertexIDAttributeNameStringValueMap.put(edgeSourceVertexIDAttributeName2, "2");
		sourceVertexIDAttributeNameStringValueMap.put(edgeSourceVertexIDAttributeName3, "3");
		
		sinkVertexIDAttributeNameStringValueMap.put(edgeTargetVertexIDAttributeName1, "a");
		sinkVertexIDAttributeNameStringValueMap.put(edgeTargetVertexIDAttributeName2, "b");
		sinkVertexIDAttributeNameStringValueMap.put(edgeTargetVertexIDAttributeName3, "c");
		
		
		Pair<Map<DataTableColumnName, String>, Map<DataTableColumnName, String>> ret = 
				VfGraphEdgeUtils.swap(vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap, vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap, 
				sourceVertexIDAttributeNameStringValueMap, sinkVertexIDAttributeNameStringValueMap);
		
		
		System.out.println(ret.getFirst());
		System.out.println(ret.getSecond());
	}

}
