package metadata.graph;

import java.util.LinkedHashSet;
import java.util.Set;

import metadata.DataType;
import metadata.graph.feature.GraphEdgeFeature;
import metadata.graph.feature.GraphVertexFeature;
import rdb.table.data.DataTableColumnName;

public class GraphDataMetadataUtils {
	
	/**
	 * make and return a new GraphDataMetadata based on the given original one;
	 * note that the given original GraphDataMetadata's DataType must be {@link DataType#GRAPH};
	 * 
	 * all content of the new GraphDataMetadata is the same with the original one except that:
	 * 
	 * 		only those additional feature columns of node and edge feature data in the given includedNodeRecordDataColumnNameSet and includedEdgeRecordDataColumnNameSet will be
	 * 		kept in the new GraphDataMetadata;
	 * 
	 * 
	 * @param original
	 * @param includedNodeRecordDataColumnNameSet not null; can be empty 
	 * @param includedEdgeRecordDataColumnNameSet not null; can be empty
	 * @return
	 */
	public static GraphDataMetadata makeNewGraphDataMetadata(
			GraphDataMetadata original, 
			Set<DataTableColumnName> includedNodeRecordDataColumnNameSet, 
			Set<DataTableColumnName> includedEdgeRecordDataColumnNameSet) {
		
		if(!original.getDataType().equals(DataType.GRAPH))
			throw new IllegalArgumentException("give original GraphDataMetadata is not of GRAPH type!");
		
		
		/////node additional feature columns
		LinkedHashSet<DataTableColumnName> newGraphNodeRecordAdditionalFeatureColumnNameSet = new LinkedHashSet<>();
		
		original.getGraphVertexFeature().getAdditionalFeatureColumnNameSet().forEach(col->{
			if(includedNodeRecordDataColumnNameSet.contains(col))
				newGraphNodeRecordAdditionalFeatureColumnNameSet.add(col);
		});
		
		GraphVertexFeature newGraphNodeFeature = new GraphVertexFeature(
				original.getGraphVertexFeature().getIDColumnNameSet(),
				newGraphNodeRecordAdditionalFeatureColumnNameSet
				);
		
		////edge additional feature columns
		LinkedHashSet<DataTableColumnName> newGraphEdgeRecordAdditionalFeatureColumnNameSet = new LinkedHashSet<>();
		
		original.getGraphEdgeFeature().getAdditionalFeatureColumnNameSet().forEach(col->{
			if(includedEdgeRecordDataColumnNameSet.contains(col))
				newGraphEdgeRecordAdditionalFeatureColumnNameSet.add(col);
		});
		
		GraphEdgeFeature newGraphEdgeFeature = new GraphEdgeFeature(
				original.getGraphEdgeFeature().getIDColumnNameSet(),
				newGraphEdgeRecordAdditionalFeatureColumnNameSet,
				original.getGraphEdgeFeature().isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets(),
				original.getGraphEdgeFeature().getNodeIDColumnNameEdgeSourceNodeIDColumnNameMap(),
				original.getGraphEdgeFeature().getNodeIDColumnNameEdgeSinkNodeIDColumnNameMap(),
				original.getGraphEdgeFeature().getDirectednessFeature()
				);
		
		///
		return new GraphDataMetadata(
				original.getName(), //MetadataName name, 
				original.getNotes(),//VfNotes notes, 
				original.getSourceType(),//SourceType sourceType,
				original.getSourceOperationID(),//OperationID sourceOperationID,
				
				////
				original.getNodeRecordDataName(),//MetadataName nodeRecordDataName,
				original.getEdgeRecordDataName(),//MetadataName edgeRecordDataName,
				newGraphNodeFeature,//GraphVertexFeature graphNodeFeature,
				newGraphEdgeFeature,//GraphEdgeFeature graphEdgeFeature,
				original.getObservedGraphType()//GraphMetadataType observedGraphType
				);
	}
}
