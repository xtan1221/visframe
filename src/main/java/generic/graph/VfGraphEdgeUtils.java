package generic.graph;

import java.util.LinkedHashMap;
import java.util.Map;

import rdb.table.data.DataTableColumnName;
import utils.Pair;

public class VfGraphEdgeUtils {

	
	/**
	 * swap the source and sink vertex's id attribute name string value map;
	 * note that the id attributes of source and sink vertex are those in the edge data table schema, not the id columns in the vertex data table schema, thus they can be different and swapping of them is non-trivial;
	 * @param vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap
	 * @param vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap
	 * @param sourceVertexIDAttributeNameStringValueMap
	 * @param sinkVertexIDAttributeNameStringValueMap
	 * @return
	 */
	public static Pair<Map<DataTableColumnName, String>, Map<DataTableColumnName, String>> swap(
			Map<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap,
			Map<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap,
			Map<DataTableColumnName, String> sourceVertexIDAttributeNameStringValueMap,
			Map<DataTableColumnName, String> sinkVertexIDAttributeNameStringValueMap){
		
		Map<DataTableColumnName, String> swappedSourceMap = new LinkedHashMap<>();//the new source vertex that contains the input sink vertex id columns values
		Map<DataTableColumnName, String> swappedSinkMap = new LinkedHashMap<>();
		
		for(DataTableColumnName vertexIDAttributeName:vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap.keySet()) {
			swappedSourceMap.put(
					vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap.get(vertexIDAttributeName), 
					sinkVertexIDAttributeNameStringValueMap.get(vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap.get(vertexIDAttributeName)));
			
			swappedSinkMap.put(
					vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap.get(vertexIDAttributeName), 
					sourceVertexIDAttributeNameStringValueMap.get(vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap.get(vertexIDAttributeName)));
		}
		
		return new Pair<>(swappedSourceMap, swappedSinkMap);
	}
}
