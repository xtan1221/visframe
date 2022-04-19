package metadata.graph.feature;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import metadata.graph.vftree.feature.VfTreeEdgeFeature;
import rdb.table.data.DataTableColumnName;

/**
 * update-012721
 * role of columns of data table of the graph edge record data in the owner GraphDataMetadata object
 * 
 * 
 * note that in current visframe, it only allows the following two types of scenario
 * 1. the edge ID column set is fully disjoint with the source/sink node ID columns and the directedness type indicator column (if it exists)
 * 2. the edge ID column set is a super set of the union of the source/sink node ID columns and the directedness type indicator column (if it exists)
 * 
 * @author tanxu
 * 
 */
public class GraphEdgeFeature extends GraphComponentFeatureBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7729242077475879447L;
	
	///////////////////////////
	/**
	 * update-012721 
	 * if true, the edge id columns {@link #IDColumnNameSet} is not containing any of the source/sink node ID columns and the directedness type indicator column in the {@link #directednessFeature} if it is not null;
	 * if false, the edge ID column set {@link #IDColumnNameSet} contains all the source/sink node ID columns AND the directedness type indicator column in the {@link #directednessFeature} if it is not null, and may also contain OTHER columns!!!
	 * 		or equivalently, the edge ID column set is a superset of the union of the source/sink node ID columns and the directedness type indicator column in the {@link #directednessFeature} if it is not null. 
	 * 
	 */
	private final boolean edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn;
	
	/**
	 * map from ID column name in the node feature record data to source node ID column name in edge record data
	 * 
	 * key is the column name in the node id column set of the node feature record data; value is the column name in the edge data table corresponding to that column;
	 * 
	 * cannot be null or empty;
	 * must be of the same size with {@link #nodeIDColumnNameEdgeSinkNodeIDColumnNameMap} and have the same key set
	 * cannot share any map value with {@link #nodeIDColumnNameEdgeSinkNodeIDColumnNameMap}
	 * 
	 * if {@link #edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn} is false, this map's value set is a subset of the {@link #IDColumnNameSet};
	 * otherwise, this map's value set should not overlap with {@link #IDColumnNameSet};
	 */
	private final LinkedHashMap<DataTableColumnName, DataTableColumnName> nodeIDColumnNameEdgeSourceNodeIDColumnNameMap; //
	
	/**
	 * map from ID column name in the node feature record data to sink node ID column name in edge record data
	 * 
	 * key is the column name in the node id column set of the node feature record data; value is the column name in the edge data table corresponding to that column;
	 * 
	 * cannot be null or empty;
	 * must be of the same size with {@link #nodeIDColumnNameEdgeSourceNodeIDColumnNameMap} and have the same key set
	 * cannot share any map value with {@link #nodeIDColumnNameEdgeSourceNodeIDColumnNameMap}
	 * 
	 * if {@link #edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn} is false, this map's value set is a subset of the {@link #IDColumnNameSet};
	 * otherwise, this map's value set should not overlap with {@link #IDColumnNameSet};
	 */
	private final LinkedHashMap<DataTableColumnName, DataTableColumnName> nodeIDColumnNameEdgeSinkNodeIDColumnNameMap;
	
	/**
	 * update-012721
	 * the information for identifying directed type for each edge.
	 * if it contains a non-null directedness type indicator column, 
	 * 		1. it must be different from any column in additionalFeatureColumnNameSet and source/sink node column set;
	 * 			
	 * 		2. if {@link #edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn} is false
	 * 			it must be different from any column in IDColumnNameSet.
	 * 		   else
	 * 			it must be contained in IDColumnNameSet.
	 * 
	 */
	private final EdgeDirectednessFeature directednessFeature;
	
	/**
	 * constructor
	 * @param IDColumnNameSet
	 * @param additionalFeatureColumnNameSet
	 * @param edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn
	 * @param nodeIDColumnNameEdgeSourceNodeIDColumnNameMap
	 * @param nodeIDColumnNameEdgeSinkNodeIDColumnNameMap
	 * @param directednessFeature
	 */
	public GraphEdgeFeature(
			LinkedHashSet<DataTableColumnName> IDColumnNameSet,
			LinkedHashSet<DataTableColumnName> additionalFeatureColumnNameSet,
			
			boolean edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn,
			
			LinkedHashMap<DataTableColumnName, DataTableColumnName> nodeIDColumnNameEdgeSourceNodeIDColumnNameMap,
			LinkedHashMap<DataTableColumnName, DataTableColumnName> nodeIDColumnNameEdgeSinkNodeIDColumnNameMap,
			EdgeDirectednessFeature directednessFeature
			) {
		super(IDColumnNameSet, additionalFeatureColumnNameSet);
		//validations
		if(nodeIDColumnNameEdgeSourceNodeIDColumnNameMap==null||nodeIDColumnNameEdgeSourceNodeIDColumnNameMap.isEmpty())
			throw new IllegalArgumentException("given nodeIDColumnNameEdgeSourceNodeIDColumnNameMap cannot be null or empty!");
		if(nodeIDColumnNameEdgeSinkNodeIDColumnNameMap==null||nodeIDColumnNameEdgeSinkNodeIDColumnNameMap.isEmpty())
			throw new IllegalArgumentException("given nodeIDColumnNameEdgeSinkNodeIDColumnNameMap cannot be null or empty!");
		if(directednessFeature==null)
			throw new IllegalArgumentException("given directednessFeature cannot be null or empty!");
		
		
		//if edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn is true, 
		//1. IDColumnNameSet cannot contain map values of sourceNodePrimaryKeyColumnNameEdgeColumnMap and sinkNodePrimaryKeyColumnNameEdgeColumnMap
		//2. IDColumnNameSet cannot contain the directedness indicator column in the directednessFeature if it is not null
		//if edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn is false, 
		//1. IDColumnNameSet must contain all the map values of sourceNodePrimaryKeyColumnNameEdgeColumnMap and sinkNodePrimaryKeyColumnNameEdgeColumnMap
		//2. IDColumnNameSet must contain the directedness indicator column in the directednessFeature if it is not null
		if(edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn) {
			for(DataTableColumnName colName:nodeIDColumnNameEdgeSourceNodeIDColumnNameMap.values()) {
				if(IDColumnNameSet.contains(colName)) {
					throw new IllegalArgumentException("IDColumnNameSet cannot contain source node id column name when edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn is true!");
				}
			}
			for(DataTableColumnName colName:nodeIDColumnNameEdgeSinkNodeIDColumnNameMap.values()) {
				if(IDColumnNameSet.contains(colName)) {
					throw new IllegalArgumentException("IDColumnNameSet cannot contain sink node id column name when edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn is true!");
				}
			}
			if(directednessFeature.hasDirectednessIndicatorColumn()) {
				if(IDColumnNameSet.contains(directednessFeature.getDirectednessIndicatorColumnName())) {
					throw new IllegalArgumentException("IDColumnNameSet contains directedness type indicator column in directednessFeature when edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn is true!");
				}
			}
		}else {
			for(DataTableColumnName colName:nodeIDColumnNameEdgeSourceNodeIDColumnNameMap.values()) {
				if(!IDColumnNameSet.contains(colName)) {
					throw new IllegalArgumentException("IDColumnNameSet must contain all source node id column name when edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn is false!");
				}
			}
			for(DataTableColumnName colName:nodeIDColumnNameEdgeSinkNodeIDColumnNameMap.values()) {
				if(!IDColumnNameSet.contains(colName)) {
					throw new IllegalArgumentException("IDColumnNameSet must contain all sink node id column name when edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn is false!");
				}
			}
			if(directednessFeature.hasDirectednessIndicatorColumn()) {
				if(!IDColumnNameSet.contains(directednessFeature.getDirectednessIndicatorColumnName())) {
					throw new IllegalArgumentException("IDColumnNameSet must contain directedness type indicator column in directednessFeature when edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn is false!");
				}
			}
		}
		
		//sourceNodePrimaryKeyColumnNameEdgeColumnMap and sinkNodePrimaryKeyColumnNameEdgeColumnMap must have same set of map keys and cannot have overlapping map values;
		if(!nodeIDColumnNameEdgeSourceNodeIDColumnNameMap.keySet().equals(nodeIDColumnNameEdgeSinkNodeIDColumnNameMap.keySet())) {
			throw new IllegalArgumentException("sourceNodePrimaryKeyColumnNameEdgeColumnMap and sinkNodePrimaryKeyColumnNameEdgeColumnMap must have same set of map keys;");
		}
		
		//check duplicate map value of sourceNodePrimaryKeyColumnNameEdgeColumnMap and sinkNodePrimaryKeyColumnNameEdgeColumnMap
		nodeIDColumnNameEdgeSourceNodeIDColumnNameMap.values().forEach(v->{
			if(nodeIDColumnNameEdgeSinkNodeIDColumnNameMap.values().contains(v))
				throw new IllegalArgumentException("sourceNodePrimaryKeyColumnNameEdgeColumnMap and sinkNodePrimaryKeyColumnNameEdgeColumnMap cannot have same columns;");
		});
		
		
		///////////////////////
		//validations regarding directednessFeature; see {@link #directednessFeature}
		//the directednessIndicatorColumnName of directednessFeature must be different from additionalFeatureColumnNameSet and source/sink node ID column set 
		if(directednessFeature.hasDirectednessIndicatorColumn()) {
			if(additionalFeatureColumnNameSet.contains(directednessFeature.getDirectednessIndicatorColumnName()))
				throw new IllegalArgumentException("directednessIndicatorColumnName of directednessFeature must be different from any column in additionalFeatureColumnNameSet.");
			if(nodeIDColumnNameEdgeSourceNodeIDColumnNameMap.values().contains(directednessFeature.getDirectednessIndicatorColumnName()))
				throw new IllegalArgumentException("directednessIndicatorColumnName of directednessFeature must be different from any source node column in nodeIDColumnNameEdgeSourceNodeIDColumnNameMap.");
			if(nodeIDColumnNameEdgeSinkNodeIDColumnNameMap.values().contains(directednessFeature.getDirectednessIndicatorColumnName()))
				throw new IllegalArgumentException("directednessIndicatorColumnName of directednessFeature must be different from any sink node column in nodeIDColumnNameEdgeSinkNodeIDColumnNameMap.");
		}
		
		///the additionalFeatureColumnNameSet must be disjoint with source/sink node ID columns and the directedness type indicator column in directednessFeature if it is not null
		nodeIDColumnNameEdgeSourceNodeIDColumnNameMap.values().forEach(c->{
			if(additionalFeatureColumnNameSet.contains(c))
				throw new IllegalArgumentException("additionalFeatureColumnNameSet cannot contain any of the source node columns!");
		});
		nodeIDColumnNameEdgeSinkNodeIDColumnNameMap.values().forEach(c->{
			if(additionalFeatureColumnNameSet.contains(c))
				throw new IllegalArgumentException("additionalFeatureColumnNameSet cannot contain any of the sink node columns!");
		});
		
		///////////////////////
		this.edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn = edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn;
		this.nodeIDColumnNameEdgeSourceNodeIDColumnNameMap = nodeIDColumnNameEdgeSourceNodeIDColumnNameMap;
		this.nodeIDColumnNameEdgeSinkNodeIDColumnNameMap = nodeIDColumnNameEdgeSinkNodeIDColumnNameMap;
		this.directednessFeature = directednessFeature;
	}
	
	
	/**
	 * return true if the edge id column set is equal to the union of the source/sink node column sets; otherwise, edge id column set MUST be non-overlapping with source and sink node column set;
	 * @return
	 */
	public boolean isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets() {
		return this.edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn;
	}
	

	/**
	 * @return the sourceNodeIDColumnNameEdgeColumnNameMap
	 */
	public LinkedHashMap<DataTableColumnName, DataTableColumnName> getNodeIDColumnNameEdgeSourceNodeIDColumnNameMap() {
		return nodeIDColumnNameEdgeSourceNodeIDColumnNameMap;
	}
	

	/**
	 * @return the sinkNodeIDColumnNameEdgeColumnNameMap
	 */
	public LinkedHashMap<DataTableColumnName, DataTableColumnName> getNodeIDColumnNameEdgeSinkNodeIDColumnNameMap() {
		return nodeIDColumnNameEdgeSinkNodeIDColumnNameMap;
	}


	/**
	 * @return the directednessFeature
	 */
	public EdgeDirectednessFeature getDirectednessFeature() {
		return directednessFeature;
	}



//	//return map from a GraphNodeFeature primary key column name to a RelationalTableColumn in data table of edge data for source node of an edge; 
//	//every column of node primary key must be mapped to a unique column in edge data table; 
//	//also getSourceNodePrimaryKeyColumnNameEdgeColumnMap() and getSinkNodePrimaryKeyColumnNameEdgeColumnMap cannot have overlapping map values;
//	Map<SimpleName, RelationalTableColumn> getSourceNodePrimaryKeyColumnNameEdgeColumnMap(){
//		return this.sourceNodePrimaryKeyColumnNameEdgeColumnMap;
//	}
//	//return map from a GraphNode primary key column name to a RelationalTableColumn in data table of edge data for sink node of an edge; 
//	//every column of node primary key must be mapped to a unique column in edge data table;
//	Map<SimpleName, RelationalTableColumn> getSinkNodePrimaryKeyColumnNameEdgeColumnMap(){
//		return this.sinkNodePrimaryKeyColumnNameEdgeColumnMap;
//	}

	/////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((directednessFeature == null) ? 0 : directednessFeature.hashCode());
		result = prime * result + (edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn ? 1231 : 1237);
		result = prime * result + ((nodeIDColumnNameEdgeSinkNodeIDColumnNameMap == null) ? 0
				: nodeIDColumnNameEdgeSinkNodeIDColumnNameMap.hashCode());
		result = prime * result + ((nodeIDColumnNameEdgeSourceNodeIDColumnNameMap == null) ? 0
				: nodeIDColumnNameEdgeSourceNodeIDColumnNameMap.hashCode());
		return result;
	}


	/**
	 * this method takes whether compared items are of VfTreeEdgeFeature type or not into consideration 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof GraphEdgeFeature))
			return false;
		//check if only one of this and obj is of VfTreeEdgeFeature type while the other is not
		//this will cover the types for VfTreeEdgeFeature, thus there is no need to override equals method in VfTreeEdgeFeature class
		if((obj instanceof VfTreeEdgeFeature && !(this instanceof VfTreeEdgeFeature))
				|| (this instanceof VfTreeEdgeFeature && !(obj instanceof VfTreeEdgeFeature)))
			return false;
		
		GraphEdgeFeature other = (GraphEdgeFeature) obj;
		if (directednessFeature == null) {
			if (other.directednessFeature != null)
				return false;
		} else if (!directednessFeature.equals(other.directednessFeature))
			return false;
		if (edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn != other.edgeIDColumnSetDisjointWithUnionOfSourceSinkNodeIDColumnsAndDirectednessTypeIndicatorColumn)
			return false;
		if (nodeIDColumnNameEdgeSinkNodeIDColumnNameMap == null) {
			if (other.nodeIDColumnNameEdgeSinkNodeIDColumnNameMap != null)
				return false;
		} else if (!nodeIDColumnNameEdgeSinkNodeIDColumnNameMap
				.equals(other.nodeIDColumnNameEdgeSinkNodeIDColumnNameMap))
			return false;
		if (nodeIDColumnNameEdgeSourceNodeIDColumnNameMap == null) {
			if (other.nodeIDColumnNameEdgeSourceNodeIDColumnNameMap != null)
				return false;
		} else if (!nodeIDColumnNameEdgeSourceNodeIDColumnNameMap
				.equals(other.nodeIDColumnNameEdgeSourceNodeIDColumnNameMap))
			return false;
		return true;
	}
	
	
	
}
