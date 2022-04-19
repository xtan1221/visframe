package context.scheme.appliedarchive.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import exception.VisframeException;
import metadata.DataType;
import metadata.MetadataID;
import metadata.graph.GraphDataMetadata;
import metadata.graph.feature.GraphEdgeFeature;
import metadata.graph.feature.GraphVertexFeature;
import operation.graph.SingleGenericGraphAsInputOperation;
import operation.vftree.VfTreeTrimmingOperationBase;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import rdb.table.data.DataTableSchemaID;

/**
 * mapping from a generic graph metadata G1 (of either a {@link DataType#GRAPH} or {@link DataType#vfTREE}) from host VisProjectDBContext 
 * to a node selected in solution set on the trimmed integrated DOS graph containing {@link DataType#GRAPH} G2
 * 
 * the target generic graph metadata G2 must be input metadata of one or more {@link SingleGenericGraphAsInputOperation},
 * but cannot be input Metadata of any {@link VfTreeTrimmingOperationBase};
 * 
 * rules and constraints:
 * {@link GraphDataMetadata}
 * 1. for node record data {@link GraphVertexFeature}
 * 		Node id columns (primary key columns) of G2 must be fully one-to-one mapped from node id columns (primary key columns) of G1;
 * 			thus the target and source graph data's node record data's node id columns must be of the same size;
 *		Additional feature input columns (non primary key columns) of G2 must be mapped from additional feature columns (non primary key columns) of G1;
 *		
 * 2. for edge record data {@link GraphEdgeFeature}
 * 		{@link GraphEdgeFeature#isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets()} of G1 and G2 must be the same;
 * 
 * note that the same column in the source graph data's node/edge record data cannot be mapped to multiple columns of the target graph's node/edge record data;
 * 
 * the mapped column pairs should have consistent data type (see {@link RecordMapping});
 * 
 * =================================
 * note that no matter whether {@link #edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets} is true or not
 * the maps {@link #targetSourceEdgeDataSinkNodeIDColumnMap} and {@link #targetSourceEdgeDataSourceNodeIDColumnMap} can be fully built from the 
 * 1. {@link #targetSourceNodeRecordDataNodeIDColumMap}
 * 2. the {@link GraphEdgeFeature#nodeIDColumnNameEdgeSourceNodeIDColumnNameMap} and {@link GraphEdgeFeature#nodeIDColumnNameEdgeSinkNodeIDColumnNameMap} in GraphEdgeFeature of both source and target generic graph data
 * 
 * thus, when building a GenericGraphMapping, the maps {@link #targetSourceEdgeDataSinkNodeIDColumnMap} and {@link #targetSourceEdgeDataSourceNodeIDColumnMap} do not need to be explicitly built,
 * rather, they should be built after other maps are created;
 * 
 * see {@link }
 * 
 * @author tanxu
 * 
 */
public class GenericGraphMapping extends MetadataMapping{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6989479156540670002L;
	
	//////////////////////////////////
	private final MetadataID targetNodeRecordMetadataID;
	private final MetadataID targetEdgeRecordMetadataID;
	private final MetadataID sourceNodeRecordMetadataID;
	private final MetadataID sourceEdgeRecordMetadataID;
	
	private final DataTableSchemaID sourceNodeRecordDataTableSchemaID;
	private final DataTableSchemaID sourceEdgeRecordDataTableSchemaID;
	
	////////node record data column mapping
	private final boolean targetNodeRecordDataIncluded;
	/**
	 * map for node id columns in node record data from the target graph data to the source graph data;
	 * must be non-empty;
	 */
	private final Map<DataTableColumn, DataTableColumn> targetSourceNodeRecordDataNodeIDColumMap;
	
	/**
	 * map for node additional feature columns in node record data from the target graph data to the source graph data;
	 * must be non-null but can be empty;
	 */
	private final Map<DataTableColumn, DataTableColumn> targetSourceNodeRecordDataAdditionalFeatureColumMap;
	
	/////////edge record data column mapping
	private final boolean targetEdgeRecordDataIncluded;
	/**
	 * whether or not the edge id columns is disjoint with the source and sink node id columns in the edge record data;
	 * must be the same with both the target and source graph data's {@link GraphEdgeFeature};
	 */
	private final Boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;
	/**
	 * the map for edge id columns in edge record data from the target graph data to the source graph data;
	 * must be non-empty;
	 * 
	 * note that 
	 * if {@link #edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets} is true, 
	 * 		the source and target node id columns in edge record data is not included and should be explicitly given in the {@link #targetSourceEdgeRecordDataSourceSinkNodeIDColumnMap};
	 * otherwise, they are included in the map and the {@link #targetSourceEdgeRecordDataSourceSinkNodeIDColumnMap} should be null;
	 */
	private final Map<DataTableColumn, DataTableColumn> targetSourceEdgeRecordDataEdgeIdColumnMap;
	
	/**
	 * the map for source node id columns in the edge record data from the target graph data to the source graph data;
	 * 
	 * must be null if {@link #edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets} is false;
	 * 		this is because the map can be deduced from the {@link #targetSourceEdgeRecordDataEdgeIdColumnMap} and 
	 * 		the map between the node id column of node feature record and source/sink node id column of edge feature record data of the source and target generic graph data (both of which are known)
	 * 
	 * must be explicitly given and non-empty otherwise;
	 */
	private final Map<DataTableColumn, DataTableColumn> targetSourceEdgeDataSourceNodeIDColumnMap;
	
	/**
	 * the map for sink node id columns in the edge record data from the target graph data to the source graph data;
	 * 
	 * must be null if {@link #edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets} is false;
	 * 		this is because the map can be deduced from the {@link #targetSourceEdgeRecordDataEdgeIdColumnMap} and 
	 * 		the map between the node id column of node feature record and source/sink node id column of edge feature record data of the source and target generic graph data (both of which are known)
	 * 
	 * must be explicitly given and non-empty otherwise;
	 */
	private final Map<DataTableColumn, DataTableColumn> targetSourceEdgeDataSinkNodeIDColumnMap;
	
	
	/**
	 * the map for additional feature columns of edge record data from the target graph data to the source graph data;
	 * not null, can be empty;
	 */
	private final Map<DataTableColumn, DataTableColumn> targetSourceEdgeRecordDataAdditionalFeatureColumMap;
	
	/////////////////////////////
	private transient Map<DataTableColumn, DataTableColumn> targetSourceNodeRecordDataColumMap;
	private transient Map<DataTableColumn, DataTableColumn> targetSourceEdgeRecordDataColumMap;
	private transient Map<DataTableColumnName, DataTableColumnName> targetSourceNodeRecordDataColumNameMap;
	private transient Map<DataTableColumnName, DataTableColumnName> targetSourceEdgeRecordDataColumNameMap;
	
	/**
	 * constructor
	 * @param targetMetadataID
	 * @param sourceMetadataID
	 * @param targetNodeRecordDataIncluded cannot be both false with targetEdgeRecordDataIncluded;
	 * @param targetEdgeRecordDataIncluded cannot be both false with targetNodeRecordDataIncluded;
	 * @param targetNodeRecordMetadataID not null
	 * @param targetEdgeRecordMetadataID not null
	 * @param sourceNodeRecordMetadataID not null
	 * @param sourceEdgeRecordMetadataID not null
	 * @param sourceNodeRecordDataTableSchemaID not null
	 * @param sourceEdgeRecordDataTableSchemaID not null
	 * @param targetSourceNodeRecordDataNodeIDColumMap not null or empty
	 * @param targetSourceNodeRecordDataAdditionalFeatureColumMap null if targetNodeRecordDataIncluded is false, non-null otherwise
	 * @param edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets null if targetEdgeRecordDataIncluded is false, non-null otherwise
	 * @param targetSourceEdgeRecordDataEdgeIdColumnMap null if targetEdgeRecordDataIncluded is false, non-null and non-empty otherwise
	 * @param targetSourceEdgeDataSourceNodeIDColumnMap null if targetEdgeRecordDataIncluded is false or edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets is false, non-null and non-empty otherwise
	 * @param targetSourceEdgeDataSinkNodeIDColumnMap null if targetEdgeRecordDataIncluded is false or edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets is false, non-null and non-empty otherwise
	 * @param targetSourceEdgeRecordDataAdditionalFeatureColumMap null if targetEdgeRecordDataIncluded is false, non-null otherwise
	 */
	public GenericGraphMapping(
			MetadataID targetMetadataID, MetadataID sourceMetadataID,
			
			boolean targetNodeRecordDataIncluded,
			boolean targetEdgeRecordDataIncluded,
			
			MetadataID targetNodeRecordMetadataID, MetadataID targetEdgeRecordMetadataID,
			MetadataID sourceNodeRecordMetadataID, MetadataID sourceEdgeRecordMetadataID,
			DataTableSchemaID sourceNodeRecordDataTableSchemaID,
			DataTableSchemaID sourceEdgeRecordDataTableSchemaID,
			
			Map<DataTableColumn, DataTableColumn> targetSourceNodeRecordDataNodeIDColumMap,
			Map<DataTableColumn, DataTableColumn> targetSourceNodeRecordDataAdditionalFeatureColumMap,
			
			Boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets,
			Map<DataTableColumn, DataTableColumn> targetSourceEdgeRecordDataEdgeIdColumnMap,
			Map<DataTableColumn, DataTableColumn> targetSourceEdgeDataSourceNodeIDColumnMap,
			Map<DataTableColumn, DataTableColumn> targetSourceEdgeDataSinkNodeIDColumnMap,
			Map<DataTableColumn, DataTableColumn> targetSourceEdgeRecordDataAdditionalFeatureColumMap
			) {
		super(targetMetadataID, sourceMetadataID);
		
		//TODO validations
		
		
		
		this.targetNodeRecordMetadataID = targetNodeRecordMetadataID;
		this.targetEdgeRecordMetadataID = targetEdgeRecordMetadataID;
		
		this.targetNodeRecordDataIncluded = targetNodeRecordDataIncluded;
		this.targetEdgeRecordDataIncluded = targetEdgeRecordDataIncluded;
		
		
		this.sourceNodeRecordMetadataID = sourceNodeRecordMetadataID;
		this.sourceEdgeRecordMetadataID = sourceEdgeRecordMetadataID;
		this.sourceNodeRecordDataTableSchemaID = sourceNodeRecordDataTableSchemaID;
		this.sourceEdgeRecordDataTableSchemaID = sourceEdgeRecordDataTableSchemaID;
		//
		this.targetSourceNodeRecordDataNodeIDColumMap = targetSourceNodeRecordDataNodeIDColumMap;
		this.targetSourceNodeRecordDataAdditionalFeatureColumMap = targetSourceNodeRecordDataAdditionalFeatureColumMap;
		//
		this.edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets = edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;
		this.targetSourceEdgeRecordDataEdgeIdColumnMap = targetSourceEdgeRecordDataEdgeIdColumnMap;
		
		this.targetSourceEdgeDataSourceNodeIDColumnMap = targetSourceEdgeDataSourceNodeIDColumnMap;
		this.targetSourceEdgeDataSinkNodeIDColumnMap = targetSourceEdgeDataSinkNodeIDColumnMap;
		
		this.targetSourceEdgeRecordDataAdditionalFeatureColumMap = targetSourceEdgeRecordDataAdditionalFeatureColumMap;
	}
	
	/**
	 * @return the targetNodeRecordMetadataID
	 */
	public MetadataID getTargetNodeRecordMetadataID() {
		return targetNodeRecordMetadataID;
	}

	/**
	 * @return the targetEdgeRecordMetadataID
	 */
	public MetadataID getTargetEdgeRecordMetadataID() {
		return targetEdgeRecordMetadataID;
	}

	/**
	 * @return the sourceNodeRecordMetadataID
	 */
	public MetadataID getSourceNodeRecordMetadataID() {
		return sourceNodeRecordMetadataID;
	}

	/**
	 * @return the sourceEdgeRecordMetadataID
	 */
	public MetadataID getSourceEdgeRecordMetadataID() {
		return sourceEdgeRecordMetadataID;
	}

	
	/**
	 * @return the targetSourceNodeRecordDataNodeIDColumMap
	 */
	public Map<DataTableColumn, DataTableColumn> getTargetSourceNodeRecordDataNodeIDColumMap() {
		return targetSourceNodeRecordDataNodeIDColumMap;
	}

	/**
	 * @return the targetSourceNodeRecordDataAdditionalFeatureColumMap
	 */
	public Map<DataTableColumn, DataTableColumn> getTargetSourceNodeRecordDataAdditionalFeatureColumMap() {
		return targetSourceNodeRecordDataAdditionalFeatureColumMap;
	}

	/**
	 * @return the edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets
	 */
	public Boolean getEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets() {
		return edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;
	}

	/**
	 * @return the targetSourceEdgeRecordDataEdgeIdColumnMap
	 */
	public Map<DataTableColumn, DataTableColumn> getTargetSourceEdgeRecordDataEdgeIdColumnMap() {
		return targetSourceEdgeRecordDataEdgeIdColumnMap;
	}

	/**
	 * @return the targetSourceEdgeDataSourceNodeIDColumnMap
	 */
	public Map<DataTableColumn, DataTableColumn> getTargetSourceEdgeDataSourceNodeIDColumnMap() {
		return targetSourceEdgeDataSourceNodeIDColumnMap;
	}

	/**
	 * @return the targetSourceEdgeDataSinkNodeIDColumnMap
	 */
	public Map<DataTableColumn, DataTableColumn> getTargetSourceEdgeDataSinkNodeIDColumnMap() {
		return targetSourceEdgeDataSinkNodeIDColumnMap;
	}

	/**
	 * @return the targetSourceEdgeRecordDataAdditionalFeatureColumMap
	 */
	public Map<DataTableColumn, DataTableColumn> getTargetSourceEdgeRecordDataAdditionalFeatureColumMap() {
		return targetSourceEdgeRecordDataAdditionalFeatureColumMap;
	}

	/**
	 * @return the sourceNodeRecordDataTableSchemaID
	 */
	public DataTableSchemaID getSourceNodeRecordDataTableSchemaID() {
		return sourceNodeRecordDataTableSchemaID;
	}

	/**
	 * @return the sourceEdgeRecordDataTableSchemaID
	 */
	public DataTableSchemaID getSourceEdgeRecordDataTableSchemaID() {
		return sourceEdgeRecordDataTableSchemaID;
	}

	
	/**
	 * @return the targetEdgeRecordDataIncluded
	 */
	public boolean isTargetEdgeRecordDataIncluded() {
		return targetEdgeRecordDataIncluded;
	}

	/**
	 * @return the targetNodeRecordDataIncluded
	 */
	public boolean isTargetNodeRecordDataIncluded() {
		return targetNodeRecordDataIncluded;
	}

	@Override
	protected Predicate<DataType> getTargetMetadataTypePredicate() {
		return e->{return e.equals(DataType.GRAPH) || e.equals(DataType.vfTREE);};
	}

	@Override
	protected Predicate<DataType> getSourceMetadataTypePredicate() {
		return e->{return e.equals(DataType.GRAPH) || e.equals(DataType.vfTREE);};
	}
	
	
	////////////////////////
	/**
	 * find out and return the DataTableColumn of the source graph data's node record data mapped to the given target DataTableColumn of the target graph data's node record data;
	 * 
	 * should never be invoked if {@link #targetNodeRecordDataIncluded} is false;
	 * 
	 * @param targetColumn
	 * @return
	 */
	public Map<DataTableColumn, DataTableColumn> getTargetSourceNodeRecordDataColumMap() {
		if(this.isTargetNodeRecordDataIncluded()) {
			if(this.targetSourceNodeRecordDataColumMap == null) {
				this.targetSourceNodeRecordDataColumMap = new HashMap<>();
				this.targetSourceNodeRecordDataColumMap.putAll(this.targetSourceNodeRecordDataNodeIDColumMap);
				
				this.targetSourceNodeRecordDataColumMap.putAll(this.targetSourceNodeRecordDataAdditionalFeatureColumMap);
			}
			return this.targetSourceNodeRecordDataColumMap;
		}else {
			throw new VisframeException("cannot be invoked when targetNodeRecordDataIncluded is false!");
		}
	}
	
	/**
	 * find out and return the DataTableColumn of the source graph data's edge record data mapped to the given target DataTableColumn of the target graph data's edge record data;
	 * 
	 * should never be invoked if {@link #targetEdgeRecordDataIncluded} is false;
	 * 
	 * @param targetColumn
	 * @return
	 */
	public Map<DataTableColumn, DataTableColumn> getTargetSourceEdgeRecordDataColumMap() {
		if(this.isTargetEdgeRecordDataIncluded()) {
			if(this.targetSourceEdgeRecordDataColumMap == null) {
				this.targetSourceEdgeRecordDataColumMap = new HashMap<>();
				this.targetSourceEdgeRecordDataColumMap.putAll(this.targetSourceEdgeRecordDataEdgeIdColumnMap);
				
				if(this.edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets) {
					this.targetSourceEdgeRecordDataColumMap.putAll(this.targetSourceEdgeDataSourceNodeIDColumnMap);
					this.targetSourceEdgeRecordDataColumMap.putAll(this.targetSourceEdgeDataSinkNodeIDColumnMap);
				}
				this.targetSourceEdgeRecordDataColumMap.putAll(this.targetSourceEdgeRecordDataAdditionalFeatureColumMap);
			}
			return this.targetSourceEdgeRecordDataColumMap;
		}else {
			throw new VisframeException("cannot be invoked when targetEdgeRecordDataIncluded is false!");
		}
	}

	
	/**
	 * @return the targetSourceNodeRecordDataColumNameMap
	 */
	public Map<DataTableColumnName, DataTableColumnName> getTargetSourceNodeRecordDataColumNameMap() {
		if(this.isTargetNodeRecordDataIncluded()) {
			if(this.targetSourceNodeRecordDataColumNameMap==null) {
				this.targetSourceNodeRecordDataColumNameMap = new HashMap<>();
				this.getTargetSourceNodeRecordDataColumMap().forEach((k,v)->{
					this.targetSourceNodeRecordDataColumNameMap.put(k.getName(), v.getName());
				});
			}
			return targetSourceNodeRecordDataColumNameMap;
		}else {
			throw new VisframeException("cannot be invoked when targetNodeRecordDataIncluded is false!");
		}
	}
	
	/**
	 * @return the targetSourceEdgeRecordDataColumNameMap
	 */
	public Map<DataTableColumnName, DataTableColumnName> getTargetSourceEdgeRecordDataColumNameMap() {
		if(this.isTargetEdgeRecordDataIncluded()) {
			if(this.targetSourceEdgeRecordDataColumNameMap==null) {
				this.targetSourceEdgeRecordDataColumNameMap = new HashMap<>();
				this.getTargetSourceEdgeRecordDataColumMap().forEach((k,v)->{
					this.targetSourceEdgeRecordDataColumNameMap.put(k.getName(), v.getName());
				});
			}
			return targetSourceEdgeRecordDataColumNameMap;
		}else {
			throw new VisframeException("cannot be invoked when targetEdgeRecordDataIncluded is false!");
		}
	}

	
	////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets == null) ? 0
				: edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets.hashCode());
		result = prime * result
				+ ((sourceEdgeRecordDataTableSchemaID == null) ? 0 : sourceEdgeRecordDataTableSchemaID.hashCode());
		result = prime * result + ((sourceEdgeRecordMetadataID == null) ? 0 : sourceEdgeRecordMetadataID.hashCode());
		result = prime * result
				+ ((sourceNodeRecordDataTableSchemaID == null) ? 0 : sourceNodeRecordDataTableSchemaID.hashCode());
		result = prime * result + ((sourceNodeRecordMetadataID == null) ? 0 : sourceNodeRecordMetadataID.hashCode());
		result = prime * result + (targetEdgeRecordDataIncluded ? 1231 : 1237);
		result = prime * result + ((targetEdgeRecordMetadataID == null) ? 0 : targetEdgeRecordMetadataID.hashCode());
		result = prime * result + (targetNodeRecordDataIncluded ? 1231 : 1237);
		result = prime * result + ((targetNodeRecordMetadataID == null) ? 0 : targetNodeRecordMetadataID.hashCode());
		result = prime * result + ((targetSourceEdgeDataSinkNodeIDColumnMap == null) ? 0
				: targetSourceEdgeDataSinkNodeIDColumnMap.hashCode());
		result = prime * result + ((targetSourceEdgeDataSourceNodeIDColumnMap == null) ? 0
				: targetSourceEdgeDataSourceNodeIDColumnMap.hashCode());
		result = prime * result + ((targetSourceEdgeRecordDataAdditionalFeatureColumMap == null) ? 0
				: targetSourceEdgeRecordDataAdditionalFeatureColumMap.hashCode());
		result = prime * result + ((targetSourceEdgeRecordDataEdgeIdColumnMap == null) ? 0
				: targetSourceEdgeRecordDataEdgeIdColumnMap.hashCode());
		result = prime * result + ((targetSourceNodeRecordDataAdditionalFeatureColumMap == null) ? 0
				: targetSourceNodeRecordDataAdditionalFeatureColumMap.hashCode());
		result = prime * result + ((targetSourceNodeRecordDataNodeIDColumMap == null) ? 0
				: targetSourceNodeRecordDataNodeIDColumMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof GenericGraphMapping))
			return false;
		GenericGraphMapping other = (GenericGraphMapping) obj;
		if (edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets == null) {
			if (other.edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets != null)
				return false;
		} else if (!edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets
				.equals(other.edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets))
			return false;
		if (sourceEdgeRecordDataTableSchemaID == null) {
			if (other.sourceEdgeRecordDataTableSchemaID != null)
				return false;
		} else if (!sourceEdgeRecordDataTableSchemaID.equals(other.sourceEdgeRecordDataTableSchemaID))
			return false;
		if (sourceEdgeRecordMetadataID == null) {
			if (other.sourceEdgeRecordMetadataID != null)
				return false;
		} else if (!sourceEdgeRecordMetadataID.equals(other.sourceEdgeRecordMetadataID))
			return false;
		if (sourceNodeRecordDataTableSchemaID == null) {
			if (other.sourceNodeRecordDataTableSchemaID != null)
				return false;
		} else if (!sourceNodeRecordDataTableSchemaID.equals(other.sourceNodeRecordDataTableSchemaID))
			return false;
		if (sourceNodeRecordMetadataID == null) {
			if (other.sourceNodeRecordMetadataID != null)
				return false;
		} else if (!sourceNodeRecordMetadataID.equals(other.sourceNodeRecordMetadataID))
			return false;
		if (targetEdgeRecordDataIncluded != other.targetEdgeRecordDataIncluded)
			return false;
		if (targetEdgeRecordMetadataID == null) {
			if (other.targetEdgeRecordMetadataID != null)
				return false;
		} else if (!targetEdgeRecordMetadataID.equals(other.targetEdgeRecordMetadataID))
			return false;
		if (targetNodeRecordDataIncluded != other.targetNodeRecordDataIncluded)
			return false;
		if (targetNodeRecordMetadataID == null) {
			if (other.targetNodeRecordMetadataID != null)
				return false;
		} else if (!targetNodeRecordMetadataID.equals(other.targetNodeRecordMetadataID))
			return false;
		if (targetSourceEdgeDataSinkNodeIDColumnMap == null) {
			if (other.targetSourceEdgeDataSinkNodeIDColumnMap != null)
				return false;
		} else if (!targetSourceEdgeDataSinkNodeIDColumnMap.equals(other.targetSourceEdgeDataSinkNodeIDColumnMap))
			return false;
		if (targetSourceEdgeDataSourceNodeIDColumnMap == null) {
			if (other.targetSourceEdgeDataSourceNodeIDColumnMap != null)
				return false;
		} else if (!targetSourceEdgeDataSourceNodeIDColumnMap.equals(other.targetSourceEdgeDataSourceNodeIDColumnMap))
			return false;
		if (targetSourceEdgeRecordDataAdditionalFeatureColumMap == null) {
			if (other.targetSourceEdgeRecordDataAdditionalFeatureColumMap != null)
				return false;
		} else if (!targetSourceEdgeRecordDataAdditionalFeatureColumMap
				.equals(other.targetSourceEdgeRecordDataAdditionalFeatureColumMap))
			return false;
		if (targetSourceEdgeRecordDataEdgeIdColumnMap == null) {
			if (other.targetSourceEdgeRecordDataEdgeIdColumnMap != null)
				return false;
		} else if (!targetSourceEdgeRecordDataEdgeIdColumnMap.equals(other.targetSourceEdgeRecordDataEdgeIdColumnMap))
			return false;
		if (targetSourceNodeRecordDataAdditionalFeatureColumMap == null) {
			if (other.targetSourceNodeRecordDataAdditionalFeatureColumMap != null)
				return false;
		} else if (!targetSourceNodeRecordDataAdditionalFeatureColumMap
				.equals(other.targetSourceNodeRecordDataAdditionalFeatureColumMap))
			return false;
		if (targetSourceNodeRecordDataNodeIDColumMap == null) {
			if (other.targetSourceNodeRecordDataNodeIDColumMap != null)
				return false;
		} else if (!targetSourceNodeRecordDataNodeIDColumMap.equals(other.targetSourceNodeRecordDataNodeIDColumMap))
			return false;
		return true;
	}

}
