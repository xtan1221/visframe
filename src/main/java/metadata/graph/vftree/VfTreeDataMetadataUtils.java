package metadata.graph.vftree;

import java.util.LinkedHashSet;
import java.util.Set;

import metadata.DataType;
import metadata.graph.vftree.feature.VfTreeEdgeFeature;
import metadata.graph.vftree.feature.VfTreeNodeFeature;
import rdb.table.data.DataTableColumnName;

public class VfTreeDataMetadataUtils {
	
	/**
	 * make and return a new VfTreeDataMetadata based on the given original one;
	 * note that the given original VfTreeDataMetadata's DataType must be {@link DataType#vfTREE};
	 * 
	 * all content of the new VfTreeDataMetadata is the same with the original one except that:
	 * 
	 * 		only those non-mandatory additional feature columns of node and edge feature data in the given includedNodeRecordDataColumnNameSet and includedEdgeRecordDataColumnNameSet will be
	 * 		kept in the new VfTreeDataMetadata;
	 * 
	 * 
	 * @param original
	 * @param includedNodeRecordDataColumnNameSet not null; can be empty 
	 * @param includedEdgeRecordDataColumnNameSet not null; can be empty
	 * @return
	 */
	public static VfTreeDataMetadata makeNewGraphDataMetadata(
			VfTreeDataMetadata original, Set<DataTableColumnName> includedNodeRecordDataColumnNameSet, Set<DataTableColumnName> includedEdgeRecordDataColumnNameSet) {
		
		if(!original.getDataType().equals(DataType.vfTREE))
			throw new IllegalArgumentException("give original VfTreeDataMetadata is not of vfTREE type!");
		
		
		/////node non-mandatory additional feature columns to be kept
		LinkedHashSet<DataTableColumnName> newNodeRecordNonMandatoryAdditionalFeatureColumnNameSet = new LinkedHashSet<>();
		//first add mandatory additional feature columns
//		newNodeRecordAdditionalFeatureColumnNameSet.addAll(VfTreeMandatoryNodeDataTableSchemaUtils.getMandatoryAdditionalFeatureColumnNameList());
		//add non-mandatory additional feature columns if in the given includedNodeRecordDataColumnNameSet
		original.getGraphVertexFeature().getNonMandatoryAdditionalColumnNameSet().forEach(col->{
			if(includedNodeRecordDataColumnNameSet.contains(col))
				newNodeRecordNonMandatoryAdditionalFeatureColumnNameSet.add(col);
		});
		
		VfTreeNodeFeature newNodeFeature = new VfTreeNodeFeature(
//				original.getGraphVertexFeature().getIDColumnNameSet(),
				newNodeRecordNonMandatoryAdditionalFeatureColumnNameSet
				);
		
		////edge non mandatory additional feature columns to be kept
		LinkedHashSet<DataTableColumnName> newGraphEdgeRecordNonMandatoryAdditionalFeatureColumnNameSet = new LinkedHashSet<>();
		//first add mandatory additional columns
//		newGraphEdgeRecordAdditionalFeatureColumnNameSet.addAll(VfTreeMandatoryEdgeDataTableSchemaUtils.getMandatoryAdditionalFeatureColumnNameList());
		//add non mandatory additional columns
		original.getGraphEdgeFeature().getNonMandatoryAdditionalColumnNameSet().forEach(col->{
			if(includedEdgeRecordDataColumnNameSet.contains(col))
				newGraphEdgeRecordNonMandatoryAdditionalFeatureColumnNameSet.add(col);
		});
		
		//
		VfTreeEdgeFeature newEdgeFeature = new VfTreeEdgeFeature(
//				original.getGraphEdgeFeature().getIDColumnNameSet(),
				newGraphEdgeRecordNonMandatoryAdditionalFeatureColumnNameSet
				);
		
		///
		return new VfTreeDataMetadata(
				original.getName(), //MetadataName name, 
				original.getNotes(),//VfNotes notes, 
				original.getSourceType(),//SourceType sourceType,
				original.getSourceOperationID(),//OperationID sourceOperationID,
				
				////
				original.getNodeRecordDataName(),//MetadataName nodeRecordDataName,
				original.getEdgeRecordDataName(),//MetadataName edgeRecordDataName,
				newNodeFeature,//GraphVertexFeature graphNodeFeature,
				newEdgeFeature,//GraphEdgeFeature graphEdgeFeature,
				original.getBootstrapIteration()//Integer bootstrapIteration
				);
	}
}
