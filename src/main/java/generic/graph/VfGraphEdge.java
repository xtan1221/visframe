package generic.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import rdb.table.data.DataTableColumnName;

/**
 * 
 * @author tanxu
 * 
 */
public class VfGraphEdge {
	/**
	 * edge ID attributes that distinguish this vertex from other ones in the same graph;
	 * 
	 * cannot be null or empty; map values cannot be null;
	 * ID attributes may be disjoint from the source and sink vertex ID attributes or containing them;
	 * 
	 */
	private final Map<DataTableColumnName, String> IDAttributeNameStringValueMap;
	/**
	 * if true, sourceVertexIDAttributeNameStringValueMap and sinkVertexIDAttributeNameStringValueMap are not included in {@link #IDAttributeNameStringValueMap}
	 * else, sourceVertexIDAttributeNameStringValueMap and sinkVertexIDAttributeNameStringValueMap are subset of IDAttributeNameStringValueMap;
	 */
	private final boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;
	//source and sink attribute are either fully covered by ID attributes or fully disjoint with ID attributes
	private final Map<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap;
	private final Map<DataTableColumnName,String> sourceVertexIDAttributeNameStringValueMap;
	private final Map<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap;
	private final Map<DataTableColumnName,String> sinkVertexIDAttributeNameStringValueMap;
	
	//additional attributes are always disjoint with ID/source ID/sink ID attributes;
	private final Map<DataTableColumnName, String> additionalAttributeNameStringValueMap;
	
	private final boolean directed;
	
	/**
	 * constructor
	 * @param IDAttributeNameStringValueMap
	 * @param sourceVertexIDAttributeNameStringValueMap
	 * @param sinkVertexIDAttributeNameStringValueMap
	 * @param additionalAttributeNameStringValueMap
	 */
	public VfGraphEdge(
			Map<DataTableColumnName, String> IDAttributeNameStringValueMap, 
			boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets,
			Map<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap,
			Map<DataTableColumnName,String> sourceVertexIDAttributeNameStringValueMap,
			Map<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap,
			Map<DataTableColumnName,String> sinkVertexIDAttributeNameStringValueMap,
			Map<DataTableColumnName, String> additionalAttributeNameStringValueMap,
			boolean directed){
		this.IDAttributeNameStringValueMap = IDAttributeNameStringValueMap;
		this.edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets = edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;
		this.vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap = vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap;
		this.sourceVertexIDAttributeNameStringValueMap = sourceVertexIDAttributeNameStringValueMap;
		this.vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap = vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap;
		this.sinkVertexIDAttributeNameStringValueMap = sinkVertexIDAttributeNameStringValueMap;
		this.additionalAttributeNameStringValueMap = additionalAttributeNameStringValueMap;
		this.directed = directed;
	}
	
	/**
	 * @return the vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap
	 */
	public Map<DataTableColumnName, DataTableColumnName> getVertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap() {
		return Collections.unmodifiableMap(vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap);
	}

	/**
	 * @return the vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap
	 */
	public Map<DataTableColumnName, DataTableColumnName> getVertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap() {
		return Collections.unmodifiableMap(vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap);
	}

	/**
	 * @return the iDAttributeNameStringValueMap
	 */
	public Map<DataTableColumnName, String> getIDAttributeNameStringValueMap() {
		return Collections.unmodifiableMap(IDAttributeNameStringValueMap);
	}


	/**
	 * @return the sourceVertexIDAttributeNameStringValueMap
	 */
	public Map<DataTableColumnName, String> getSourceVertexIDAttributeNameStringValueMap() {
		return Collections.unmodifiableMap(sourceVertexIDAttributeNameStringValueMap);
	}


	
	/**
	 * @return the edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets
	 */
	public boolean isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets() {
		return edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;
	}
	/**
	 * @return the sinkVertexIDAttributeNameStringValueMap
	 */
	public Map<DataTableColumnName, String> getSinkVertexIDAttributeNameStringValueMap() {
		return Collections.unmodifiableMap(sinkVertexIDAttributeNameStringValueMap);
	}


	/**
	 * @return the additionalAttributeNameStringValueMap
	 */
	public Map<DataTableColumnName, String> getAdditionalAttributeNameStringValueMap() {
		return Collections.unmodifiableMap(additionalAttributeNameStringValueMap);
	}
	
	public boolean isDirected() {
		return directed;
	}
	
	/**
	 * return a VfGraphVertex containing the vertex ID attributes values of the source vertex of this edge;
	 * 
	 * the returned VfGraphVertex is used to facilitate checking if the two vertex of an edge exist in a graph using equals() method to check vertex uniqueness such as JGraphT;
	 * 
	 * @return
	 */
	public VfGraphVertex getDummySourceVertex() {
		Map<DataTableColumnName, String> IDAttributeNameStringValueMap = new HashMap<>();
		Map<DataTableColumnName, String> additionalAttributeNameStringValueMap = new HashMap<>();
		
		for(DataTableColumnName vertexIDColName:this.vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap.keySet()) {
			IDAttributeNameStringValueMap.put(
					vertexIDColName, 
					this.sourceVertexIDAttributeNameStringValueMap.get(this.vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap.get(vertexIDColName)));
		}
		
		return new VfGraphVertex(IDAttributeNameStringValueMap, additionalAttributeNameStringValueMap);
	}
	
	/**
	 * return a VfGraphVertex containing the vertex ID attributes values of the sink vertex of this edge;
	 * 
	 * the returned VfGraphVertex is used to facilitate checking if the two vertex of an edge exist in a graph using equals() method to check vertex uniqueness such as JGraphT;
	 * 
	 * @return
	 */
	public VfGraphVertex getDummySinkVertex() {
		Map<DataTableColumnName, String> IDAttributeNameStringValueMap = new HashMap<>();
		Map<DataTableColumnName, String> additionalAttributeNameStringValueMap = new HashMap<>();
		
		for(DataTableColumnName vertexIDColName:this.vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap.keySet()) {
			IDAttributeNameStringValueMap.put(
					vertexIDColName, 
					this.sinkVertexIDAttributeNameStringValueMap.get(this.vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap.get(vertexIDColName)));
		}
		
		
		return new VfGraphVertex(IDAttributeNameStringValueMap, additionalAttributeNameStringValueMap);
	}
	
	/**
	 * return a new VfGraphEdge e with 
	 * 1. opposite direction;
	 * 		source node of e = sink node of this edge;
	 * 		sink node of e = source node of this edge;
	 * 
	 * 2. exactly the same set of additional attributes set and values;
	 * 
	 * 3. e.directed = this.directed;
	 * @return
	 */
	public VfGraphEdge oppositeEdge() {
		Map<DataTableColumnName, String> IDAttributeNameStringValueMap;
		
		boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets = this.edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;
		
		Map<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap = new HashMap<>();
		vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap.putAll(this.getVertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap());//column name should not be swapped
		
		Map<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap = new HashMap<>();
		vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap.putAll(this.getVertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap());//column name should not be swapped
		
		Map<DataTableColumnName, String> additionalAttributeNameStringValueMap = new HashMap<>();
		additionalAttributeNameStringValueMap.putAll(this.getAdditionalAttributeNameStringValueMap());
		
		
		boolean directed = this.directed;
		
		
		Map<DataTableColumnName,String> sourceVertexIDAttributeNameStringValueMap = new HashMap<>();

		Map<DataTableColumnName,String> sinkVertexIDAttributeNameStringValueMap = new HashMap<>();
		
		if(this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets()) {
			IDAttributeNameStringValueMap = new HashMap<>();
			IDAttributeNameStringValueMap.putAll(this.IDAttributeNameStringValueMap);
		}else {//need to swap the attribute values of source/sink node id attributes
			IDAttributeNameStringValueMap = new HashMap<>();
			IDAttributeNameStringValueMap.putAll(this.IDAttributeNameStringValueMap);
			//set the source vertex id attribute values in IDAttributeNameStringValueMap to the corresponding sink vertex id attribute value
			for(DataTableColumnName colName:this.getVertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap().keySet()) {
				IDAttributeNameStringValueMap.put(
						this.getVertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap().get(colName), 
						this.getSinkVertexIDAttributeNameStringValueMap().get(this.getVertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap().get(colName)));
				sourceVertexIDAttributeNameStringValueMap.put(
						this.getVertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap().get(colName), 
						this.getSinkVertexIDAttributeNameStringValueMap().get(this.getVertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap().get(colName)));
			}
			
			//set the sink vertex id attribute values in IDAttributeNameStringValueMap to the corresponding source vertex id attribute value
			for(DataTableColumnName colName:this.getVertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap().keySet()) {
				IDAttributeNameStringValueMap.put(
						this.getVertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap().get(colName), 
						this.getSourceVertexIDAttributeNameStringValueMap().get(this.getVertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap().get(colName)));
				sinkVertexIDAttributeNameStringValueMap.put(
						this.getVertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap().get(colName), 
						this.getSourceVertexIDAttributeNameStringValueMap().get(this.getVertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap().get(colName)));
			}
			
		}
		
		return new VfGraphEdge(IDAttributeNameStringValueMap,
				edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets,
				vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap,
				sourceVertexIDAttributeNameStringValueMap,
				vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap,
				sinkVertexIDAttributeNameStringValueMap,
				additionalAttributeNameStringValueMap,
				directed
				);
	}
	
	
	public String simpleString() {
		return "VfGraphEdge [IDAttributeNameStringValueMap=" + IDAttributeNameStringValueMap
//				+ ", edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets="
//				+ edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets
//				+ ", vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap=" + vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap
				+ ", sourceVertexIDAttributeNameStringValueMap=" + sourceVertexIDAttributeNameStringValueMap
//				+ ", vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap=" + vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap 
				+ ", sinkVertexIDAttributeNameStringValueMap=" + sinkVertexIDAttributeNameStringValueMap 
				+ ", additionalAttributeNameStringValueMap=" + additionalAttributeNameStringValueMap 
				+ ", directed=" + directed + "]";
	}
	//////////////////////////////

	
	@Override
	public String toString() {
		return "VfGraphEdge [IDAttributeNameStringValueMap=" + IDAttributeNameStringValueMap
				+ ", edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets="
				+ edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets
				+ ", vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap="
				+ vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap
				+ ", sourceVertexIDAttributeNameStringValueMap=" + sourceVertexIDAttributeNameStringValueMap
				+ ", vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap="
				+ vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap + ", sinkVertexIDAttributeNameStringValueMap="
				+ sinkVertexIDAttributeNameStringValueMap + ", additionalAttributeNameStringValueMap="
				+ additionalAttributeNameStringValueMap + ", directed=" + directed + "]";
	}

	
	///////////////////
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result
//				+ ((IDAttributeNameStringValueMap == null) ? 0 : IDAttributeNameStringValueMap.hashCode());
//		result = prime * result + (directed ? 1231 : 1237);
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!(obj instanceof VfGraphEdge))
//			return false;
//		VfGraphEdge other = (VfGraphEdge) obj;
//		if (IDAttributeNameStringValueMap == null) {
//			if (other.IDAttributeNameStringValueMap != null)
//				return false;
//		} else if (!IDAttributeNameStringValueMap.equals(other.IDAttributeNameStringValueMap))
//			return false;
//		if (directed != other.directed)
//			return false;
//		return true;
//	}

	/////////////////////////////////////////////////////////
	
	@Override
	public int hashCode() {
		if(this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets()) {//
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((IDAttributeNameStringValueMap == null) ? 0 : IDAttributeNameStringValueMap.hashCode());
			result = prime * result + ((sinkVertexIDAttributeNameStringValueMap == null) ? 0
					: sinkVertexIDAttributeNameStringValueMap.hashCode());
			result = prime * result + ((sourceVertexIDAttributeNameStringValueMap == null) ? 0
					: sourceVertexIDAttributeNameStringValueMap.hashCode());
			return result;
		}else {//edge ID attributes does not include source/sink node id attribute
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((IDAttributeNameStringValueMap == null) ? 0 : IDAttributeNameStringValueMap.hashCode());
			result = prime * result + (directed ? 1231 : 1237);
			return result;
		}
		
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VfGraphEdge))
			return false;
		VfGraphEdge other = (VfGraphEdge) obj;
		//
		if(other.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets()==this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets()) {
			if(this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets()) {
				if (IDAttributeNameStringValueMap == null) {
					if (other.IDAttributeNameStringValueMap != null)
						return false;
				} else if (!IDAttributeNameStringValueMap.equals(other.IDAttributeNameStringValueMap))
					return false;
				if (sinkVertexIDAttributeNameStringValueMap == null) {
					if (other.sinkVertexIDAttributeNameStringValueMap != null)
						return false;
				} else if (!sinkVertexIDAttributeNameStringValueMap.equals(other.sinkVertexIDAttributeNameStringValueMap))
					return false;
				if (sourceVertexIDAttributeNameStringValueMap == null) {
					if (other.sourceVertexIDAttributeNameStringValueMap != null)
						return false;
				} else if (!sourceVertexIDAttributeNameStringValueMap.equals(other.sourceVertexIDAttributeNameStringValueMap))
					return false;
				if (directed != other.directed)
					return false;
				return true;
				
			}else {//source/sink node id attributes are included in edge id attributes;
				if (IDAttributeNameStringValueMap == null) {
					if (other.IDAttributeNameStringValueMap != null)
						return false;
				} else if (!IDAttributeNameStringValueMap.equals(other.IDAttributeNameStringValueMap))
					return false;
				if (directed != other.directed)
					return false;
				return true;
			}
			
		}else {
			return false;
		}
		
		
	}

	
	
}
