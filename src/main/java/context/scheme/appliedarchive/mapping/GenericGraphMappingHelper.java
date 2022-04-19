package context.scheme.appliedarchive.mapping;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import context.scheme.appliedarchive.TrimmedIntegratedDOSAndCFDGraphUtils;
import dependency.dos.integrated.IntegratedDOSGraphNode;
import metadata.MetadataID;
import metadata.graph.GraphDataMetadata;
import metadata.record.RecordDataMetadata;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import utils.Pair;


/**
 * helper class to find out the set of information need to create a {@link GenericGraphMapping}
 * 
 * note that this class only be used when the target metadata is not constrained to a VfTree source data;
 * 
 * for cases where only source VfTree can be used to map, use {@link VfTreeMappingHelper};
 * 
 * ============================================================
 * note that no matter whether edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets is true or not
 * the map between the source/sink node id columns in the edge feature data of source and target generic graph data can be fully built from the 
 * 1. map between the node id set columns of node features data between the  source and target generic graph data
 * 2. the nodeIDColumnNameEdgeSourceNodeIDColumnNameMap and nodeIDColumnNameEdgeSinkNodeIDColumnNameMap in GraphEdgeFeature of both source and target generic graph data
 * 
 * @author tanxu
 *
 */
public class GenericGraphMappingHelper {
	private final TrimmedIntegratedDOSAndCFDGraphUtils trimmedIntegratedDOSAndCFDGraphUtils;
	private final IntegratedDOSGraphNode targetGenericGraphDataContainingNode;
	
	/////////////////////////////////
	private MetadataID targetGenericGraphMetadataID;
	private GraphDataMetadata targetGraphData;
	
	/**
	 * whether or not the IntegratedDOSGraphNode corresponding to the node record data of the {@link #targetGenericGraphDataContainingNode} is also on the {@link #trimmedIntegratedDOSGraph}
	 * 
	 * always find out the fields no matter this field is true or not
	 * 		{@link #targetNodeRecordMetadataID}
	 * 		{@link #targetNodeRecordMetadata}
	 * 		{@link #targetNodeRecordDataNodeIDColumSet}
	 * 
	 * if false, the {@link #targetNodeRecordDataAdditionalFeatureColumSetToBeMapped} do not need to be found out, thus null;
	 * 
	 * !!!!!this is because the mapping for edge feature record data depends on the mapping of node feature's ID column set mapping, thus it should always be built;
	 * ============
	 * note that at least one of {@link #targetNodeRecordDataIncluded} and {@link #targetEdgeRecordDataIncluded} must be true;
	 */
	private boolean targetNodeRecordDataIncluded;
	
	/**
	 * whether or not the IntegratedDOSGraphNode corresponding to the edge record data of the {@link #targetGenericGraphDataContainingNode} is also on the {@link #trimmedIntegratedDOSGraph}
	 * 
	 * if true, related columns should be identified
	 * 		{@link #targetEdgeRecordMetadataID}
	 * 		{@link #targetEdgeRecordDataEdgeIDColumSet}
	 * 		{@link #targetGenericGraphEdgeRecordDataEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets}
	 * 		{@link #targetEdgeRecordDataSourceNodeIDColumSet}
	 * 			note that this field should be null if {@link #targetGenericGraphEdgeRecordDataEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets} is false;
	 * 		{@link #targetEdgeRecordDataSinkNodeIDColumSet}
	 * 			note that this field should be null if {@link #targetGenericGraphEdgeRecordDataEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets} is false;
	 * 		{@link #targetEdgeRecordDataAdditionalFeatureColumSetToBeMapped}
	 * 		
	 * otherwise, those column sets need to be null;
	 * 
	 * note that at least one of {@link #targetNodeRecordDataIncluded} and {@link #targetEdgeRecordDataIncluded} must be true;
	 */
	private boolean targetEdgeRecordDataIncluded;
	
	
	//////////////////node data related fields
	private IntegratedDOSGraphNode targetNodeRecordDataContainingNode;
	private MetadataID targetNodeRecordMetadataID;
	private RecordDataMetadata targetNodeRecordMetadata;
	
	
	private Set<DataTableColumn> targetNodeRecordDataNodeIDColumSet; //primary key columns of target node record data
	/**
	 * set of non-primary key(additional feature) columns of target node record data that needs to be mapped from the source generic graph data's node record data's non additional feature columns;
	 * 
	 * can be a subset of the non-primary key columns set and can be null;
	 */
	private Set<DataTableColumn> targetNodeRecordDataAdditionalFeatureColumSetToBeMapped;
	
	
	/////////////////edge data related fields
	private IntegratedDOSGraphNode targetEdgeRecordDataContainingNode;
	private MetadataID targetEdgeRecordMetadataID;
	private RecordDataMetadata targetEdgeRecordMetadata;
	
	private Set<DataTableColumn> targetEdgeRecordDataEdgeIDColumSet; //primary key columns of target edge record data
	
	/**
	 * whether or not the edge id column set is disjoint with the source/sink node id column set in the edge record data of the target generic graph data;
	 */
	private Boolean targetGenericGraphEdgeRecordDataEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;

	/**
	 * null if {@link #targetGenericGraphEdgeRecordDataEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets} is false;
	 * non-null and non-empty otherwise;
	 */
	private Set<DataTableColumn> targetEdgeRecordDataSourceNodeIDColumSet;
	/**
	 * null if {@link #targetGenericGraphEdgeRecordDataEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets} is false;
	 * non-null and non-empty otherwise;
	 */
	private Set<DataTableColumn> targetEdgeRecordDataSinkNodeIDColumSet;
	
	private Set<DataTableColumn> targetEdgeRecordDataAdditionalFeatureColumSetToBeMapped;
	
	
	/**
	 * 
	 * @param trimmedIntegratedDOSAndCFDGraphUtilsGraph
	 * @param targetGenericGraphDataContainingNode
	 */
	public GenericGraphMappingHelper(
			TrimmedIntegratedDOSAndCFDGraphUtils trimmedIntegratedDOSAndCFDGraphUtilsGraph,
			IntegratedDOSGraphNode targetGenericGraphDataContainingNode
			){
		//TODO
		
		this.trimmedIntegratedDOSAndCFDGraphUtils = trimmedIntegratedDOSAndCFDGraphUtilsGraph;
		this.targetGenericGraphDataContainingNode = targetGenericGraphDataContainingNode;
		
		
		//////////
		this.detect();
	}
	
	/**
	 * 
	 */
	private void detect() {
		this.targetGenericGraphMetadataID = this.targetGenericGraphDataContainingNode.getMetadataID();
		this.targetGraphData = 
				(GraphDataMetadata)this.trimmedIntegratedDOSAndCFDGraphUtils.getVisScheme().getMetadataLookup().lookup(this.targetGenericGraphMetadataID);
		

		Pair<IntegratedDOSGraphNode,IntegratedDOSGraphNode> nodeAndEdgeRecordDataNodePair = 
				this.trimmedIntegratedDOSAndCFDGraphUtils.getGenericGraphIntegratedDOSGraphNodeNodeAndEdgeIntegratedDOSGraphNodePairMap().get(this.targetGenericGraphDataContainingNode);
		
		

		
		//////
		this.targetNodeRecordDataContainingNode = nodeAndEdgeRecordDataNodePair.getFirst();
		this.targetEdgeRecordDataContainingNode = nodeAndEdgeRecordDataNodePair.getSecond();
		
		
		/////////////////////////node feature record data related
		///always find out the target Node feature id column set no matter it is on the trimmed integrated DOS graph or not;
		this.targetNodeRecordMetadataID = this.targetGraphData.getNodeRecordMetadataID();
		this.targetNodeRecordMetadata = (RecordDataMetadata)this.trimmedIntegratedDOSAndCFDGraphUtils.getVisScheme().getMetadataLookup().lookup(this.targetNodeRecordMetadataID);
		
		this.targetNodeRecordDataNodeIDColumSet = new LinkedHashSet<>();
		targetGraphData.getGraphVertexFeature().getIDColumnNameSet().forEach(colName->{
			this.targetNodeRecordDataNodeIDColumSet.add(targetNodeRecordMetadata.getDataTableSchema().getColumn(colName));
		});
		
		if(targetNodeRecordDataContainingNode==null) {
			this.targetNodeRecordDataIncluded = false;
		}else {
			this.targetNodeRecordDataIncluded = true;
			this.detectNodeDataRelatedInfor();
		}
		
		
		////////////////////////////edge feature record data related
		if(targetEdgeRecordDataContainingNode==null) {
			this.targetEdgeRecordDataIncluded = false;
		}else {
			this.targetEdgeRecordDataIncluded = true;
			this.targetEdgeRecordMetadataID = targetEdgeRecordDataContainingNode.getMetadataID();
			this.targetEdgeRecordMetadata = (RecordDataMetadata)this.trimmedIntegratedDOSAndCFDGraphUtils.getVisScheme().getMetadataLookup().lookup(this.targetEdgeRecordMetadataID);
			this.detectEdgeDataRelatedInfor();
		}
	}
	
	/**
	 * detect the 
	 */
	private void detectNodeDataRelatedInfor() {
		Set<DataTableColumnName> inputColumnNameSet = new HashSet<>();
		//
		inputColumnNameSet.addAll(
				this.trimmedIntegratedDOSAndCFDGraphUtils.getRecordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap()
				.get(this.targetNodeRecordDataContainingNode));
		
		
		///////////////
		RecordDataMetadata targetNodeRecordMetadata = 
				(RecordDataMetadata)this.trimmedIntegratedDOSAndCFDGraphUtils.getVisScheme().getMetadataLookup().lookup(this.targetNodeRecordMetadataID);
		
		this.targetNodeRecordDataAdditionalFeatureColumSetToBeMapped = new LinkedHashSet<>();
		
		targetGraphData.getGraphVertexFeature().getAdditionalFeatureColumnNameSet().forEach(colName->{
			this.targetNodeRecordDataAdditionalFeatureColumSetToBeMapped.add(targetNodeRecordMetadata.getDataTableSchema().getColumn(colName));
		});
		
	}
	
	
	/**
	 * 
	 * {@link #targetEdgeRecordDataEdgeIDColumSet}
	 * {@link #targetGenericGraphEdgeRecordDataEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets}
	 * {@link #targetEdgeRecordDataSourceNodeIDColumSet}
	 * 		note that this field should be null if {@link #targetGenericGraphEdgeRecordDataEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets} is false;
	 * {@link #targetEdgeRecordDataSinkNodeIDColumSet}
	 * 		note that this field should be null if {@link #targetGenericGraphEdgeRecordDataEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets} is false;
	 * {@link #targetEdgeRecordDataAdditionalFeatureColumSetToBeMapped}
	 */
	private void detectEdgeDataRelatedInfor() {
		Set<DataTableColumnName> inputColumnNameSet = new HashSet<>();
		//
		inputColumnNameSet.addAll(
				this.trimmedIntegratedDOSAndCFDGraphUtils.getRecordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap()
				.get(this.targetEdgeRecordDataContainingNode));
		
		RecordDataMetadata targetEdgeRecordMetadata = 
				(RecordDataMetadata)this.trimmedIntegratedDOSAndCFDGraphUtils.getVisScheme().getMetadataLookup().lookup(this.targetEdgeRecordMetadataID);
		
		this.targetEdgeRecordDataEdgeIDColumSet = new LinkedHashSet<>();
		this.targetEdgeRecordDataAdditionalFeatureColumSetToBeMapped = new LinkedHashSet<>();
		targetGraphData.getGraphEdgeFeature().getIDColumnNameSet().forEach(colName->{
			this.targetEdgeRecordDataEdgeIDColumSet.add(targetEdgeRecordMetadata.getDataTableSchema().getColumn(colName));
		});
		targetGraphData.getGraphEdgeFeature().getAdditionalFeatureColumnNameSet().forEach(colName->{
			this.targetEdgeRecordDataAdditionalFeatureColumSetToBeMapped.add(targetEdgeRecordMetadata.getDataTableSchema().getColumn(colName));
		});
		
		
		//////////////////////
		this.targetGenericGraphEdgeRecordDataEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets = targetGraphData.getGraphEdgeFeature().isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets();
//		
		if(this.targetGenericGraphEdgeRecordDataEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets) {
			this.targetEdgeRecordDataSourceNodeIDColumSet = new LinkedHashSet<>();
			this.targetEdgeRecordDataSinkNodeIDColumSet = new LinkedHashSet<>();
			
			targetGraphData.getGraphEdgeFeature().getNodeIDColumnNameEdgeSourceNodeIDColumnNameMap().forEach((c1,c2)->{
				this.targetEdgeRecordDataSourceNodeIDColumSet.add(targetEdgeRecordMetadata.getDataTableSchema().getColumn(c2));
			});
			
			targetGraphData.getGraphEdgeFeature().getNodeIDColumnNameEdgeSinkNodeIDColumnNameMap().forEach((c1,c2)->{
				this.targetEdgeRecordDataSinkNodeIDColumSet.add(targetEdgeRecordMetadata.getDataTableSchema().getColumn(c2));
			});
		}
	}
	
	
	/**
	 * @return the trimmedIntegratedDOSAndCFDGraphUtils
	 */
	public TrimmedIntegratedDOSAndCFDGraphUtils getTrimmedIntegratedDOSAndCFDGraphUtils() {
		return trimmedIntegratedDOSAndCFDGraphUtils;
	}

	/**
	 * @return the targetGenericGraphDataContainingNode
	 */
	public IntegratedDOSGraphNode getTargetGenericGraphDataContainingNode() {
		return targetGenericGraphDataContainingNode;
	}

	/**
	 * @return the targetGenericGraphMetadataID
	 */
	public MetadataID getTargetGenericGraphMetadataID() {
		return targetGenericGraphMetadataID;
	}

	/**
	 * @return the targetGraphData
	 */
	public GraphDataMetadata getTargetGraphData() {
		return targetGraphData;
	}

	/**
	 * @return the targetNodeRecordDataIncluded
	 */
	public boolean isTargetNodeRecordDataIncluded() {
		return targetNodeRecordDataIncluded;
	}

	/**
	 * @return the targetEdgeRecordDataIncluded
	 */
	public boolean isTargetEdgeRecordDataIncluded() {
		return targetEdgeRecordDataIncluded;
	}

	/**
	 * @return the targetNodeRecordDataContainingNode
	 */
	public IntegratedDOSGraphNode getTargetNodeRecordDataContainingNode() {
		return targetNodeRecordDataContainingNode;
	}

	/**
	 * @return the targetNodeRecordMetadataID
	 */
	public MetadataID getTargetNodeRecordMetadataID() {
		return targetNodeRecordMetadataID;
	}

	/**
	 * @return the targetNodeRecordDataNodeIDColumSet
	 */
	public Set<DataTableColumn> getTargetNodeRecordDataNodeIDColumSet() {
		return targetNodeRecordDataNodeIDColumSet;
	}

	/**
	 * @return the targetNodeRecordDataAdditionalFeatureColumSetToBeMapped
	 */
	public Set<DataTableColumn> getTargetNodeRecordDataAdditionalFeatureColumSetToBeMapped() {
		return targetNodeRecordDataAdditionalFeatureColumSetToBeMapped;
	}

	/**
	 * @return the targetEdgeRecordDataContainingNode
	 */
	public IntegratedDOSGraphNode getTargetEdgeRecordDataContainingNode() {
		return targetEdgeRecordDataContainingNode;
	}

	/**
	 * @return the targetEdgeRecordMetadataID
	 */
	public MetadataID getTargetEdgeRecordMetadataID() {
		return targetEdgeRecordMetadataID;
	}

	/**
	 * @return the targetEdgeRecordDataEdgeIDColumSet
	 */
	public Set<DataTableColumn> getTargetEdgeRecordDataEdgeIDColumSet() {
		return targetEdgeRecordDataEdgeIDColumSet;
	}

	/**
	 * @return the targetGenericGraphEdgeRecordDataEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets
	 */
	public Boolean getTargetGenericGraphEdgeRecordDataEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets() {
		return targetGenericGraphEdgeRecordDataEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;
	}

	/**
	 * @return the targetEdgeRecordDataSourceNodeIDColumSet
	 */
	public Set<DataTableColumn> getTargetEdgeRecordDataSourceNodeIDColumSet() {
		return targetEdgeRecordDataSourceNodeIDColumSet;
	}

	/**
	 * @return the targetEdgeRecordDataSinkNodeIDColumSet
	 */
	public Set<DataTableColumn> getTargetEdgeRecordDataSinkNodeIDColumSet() {
		return targetEdgeRecordDataSinkNodeIDColumSet;
	}

	/**
	 * @return the targetEdgeRecordDataAdditionalFeatureColumSetToBeMapped
	 */
	public Set<DataTableColumn> getTargetEdgeRecordDataAdditionalFeatureColumSetToBeMapped() {
		return targetEdgeRecordDataAdditionalFeatureColumSetToBeMapped;
	}

	/**
	 * @return the targetNodeRecordMetadata
	 */
	public RecordDataMetadata getTargetNodeRecordMetadata() {
		return targetNodeRecordMetadata;
	}

	/**
	 * @return the targetEdgeRecordMetadata
	 */
	public RecordDataMetadata getTargetEdgeRecordMetadata() {
		return targetEdgeRecordMetadata;
	}


}
