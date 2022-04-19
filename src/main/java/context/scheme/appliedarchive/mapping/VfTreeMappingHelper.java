package context.scheme.appliedarchive.mapping;

import java.util.LinkedHashSet;
import java.util.Set;

import context.scheme.appliedarchive.TrimmedIntegratedDOSAndCFDGraphUtils;
import dependency.dos.integrated.IntegratedDOSGraphNode;
import metadata.MetadataID;
import metadata.graph.vftree.VfTreeDataMetadata;
import metadata.record.RecordDataMetadata;
import rdb.table.data.DataTableColumn;
import utils.Pair;

public class VfTreeMappingHelper {
	private final TrimmedIntegratedDOSAndCFDGraphUtils trimmedIntegratedDOSAndCFDGraphUtils;
	private final IntegratedDOSGraphNode targetVfTreeDataContainingNode;
	
	/////////////////////////////////
	private MetadataID targetVfTreeMetadataID;
	
	/**
	 * whether or not the IntegratedDOSGraphNode corresponding to the node record data of the {@link #targetVfTreeDataContainingNode} is also on the {@link #trimmedIntegratedDOSGraph}
	 * if true, related columns should be identified and not null
	 * 		{@link #targetNodeRecordMetadataID}
	 * 		{@link #targetNodeRecordDataNonMandatoryAdditionalFeatureColumSetToBeMapped}
	 * otherwise, those column sets need to be null;
	 * 
	 * note that at least one of {@link #targetNodeRecordDataIncluded} and {@link #targetEdgeRecordDataIncluded} must be true;
	 */
	private boolean targetNodeRecordDataIncluded;
	
	
	/**
	 * whether or not the IntegratedDOSGraphNode corresponding to the edge record data of the {@link #targetVfTreeDataContainingNode} is also on the {@link #trimmedIntegratedDOSGraph}
	 * 
	 * if true, related columns should be identified
	 * 		{@link #targetEdgeRecordMetadataID}
	 * 		{@link #targetEdgeRecordDataNonMandatoryAdditionalFeatureColumSetToBeMapped}
	 * 		
	 * otherwise, those column sets need to be null;
	 * 
	 * note that at least one of {@link #targetNodeRecordDataIncluded} and {@link #targetEdgeRecordDataIncluded} must be true;
	 */
	private boolean targetEdgeRecordDataIncluded;
	
	//node
	private IntegratedDOSGraphNode targetNodeRecordDataContainingNode;
	private MetadataID targetNodeRecordMetadataID;
	private Set<DataTableColumn> targetNodeRecordDataNonMandatoryAdditionalFeatureColumSetToBeMapped;
	
	//edge
	private IntegratedDOSGraphNode targetEdgeRecordDataContainingNode;
	private MetadataID targetEdgeRecordMetadataID;
	private Set<DataTableColumn> targetEdgeRecordDataNonMandatoryAdditionalFeatureColumSetToBeMapped;
	
	
	public VfTreeMappingHelper(
			TrimmedIntegratedDOSAndCFDGraphUtils trimmedIntegratedDOSAndCFDGraphUtilsGraph,
			IntegratedDOSGraphNode targetGenericGraphDataContainingNode
			){
		//TODO
		
		this.trimmedIntegratedDOSAndCFDGraphUtils = trimmedIntegratedDOSAndCFDGraphUtilsGraph;
		this.targetVfTreeDataContainingNode = targetGenericGraphDataContainingNode;
		
		
		//////////
		this.detect();
	}
	
	
	private void detect() {
		this.targetVfTreeMetadataID = this.targetVfTreeDataContainingNode.getMetadataID();
		
		VfTreeDataMetadata vftreeData = 
				(VfTreeDataMetadata)this.trimmedIntegratedDOSAndCFDGraphUtils.getVisScheme().getMetadataLookup().lookup(this.targetVfTreeMetadataID);
		
		Pair<IntegratedDOSGraphNode,IntegratedDOSGraphNode> nodeAndEdgeRecordDataNodePair = 
				this.trimmedIntegratedDOSAndCFDGraphUtils.getGenericGraphIntegratedDOSGraphNodeNodeAndEdgeIntegratedDOSGraphNodePairMap().get(this.targetVfTreeDataContainingNode);
		
		this.targetNodeRecordMetadataID = vftreeData.getNodeRecordMetadataID();
		this.targetEdgeRecordMetadataID = vftreeData.getEdgeRecordMetadataID();
		
		
		this.targetNodeRecordDataContainingNode = nodeAndEdgeRecordDataNodePair.getFirst();
		this.targetEdgeRecordDataContainingNode = nodeAndEdgeRecordDataNodePair.getSecond();
		
		
		
		if(targetNodeRecordDataContainingNode==null) {
			this.targetNodeRecordDataIncluded = false;
		}else {
			this.targetNodeRecordDataIncluded = true;
			
			
			this.targetNodeRecordDataNonMandatoryAdditionalFeatureColumSetToBeMapped = new LinkedHashSet<>();
			
			RecordDataMetadata targetNodeRecordMetadata = 
					(RecordDataMetadata)this.trimmedIntegratedDOSAndCFDGraphUtils.getVisScheme().getMetadataLookup().lookup(this.targetNodeRecordMetadataID);
			
			vftreeData.getGraphVertexFeature().getNonMandatoryAdditionalColumnNameSet().forEach(c->{
				this.targetNodeRecordDataNonMandatoryAdditionalFeatureColumSetToBeMapped.add(targetNodeRecordMetadata.getDataTableSchema().getColumn(c));
			});
		}
		
		
		if(targetEdgeRecordDataContainingNode==null) {
			this.targetEdgeRecordDataIncluded = false;
		}else {
			this.targetEdgeRecordDataIncluded = true;
			
			
			this.targetEdgeRecordDataNonMandatoryAdditionalFeatureColumSetToBeMapped = new LinkedHashSet<>();
			
			RecordDataMetadata targetEdgeRecordMetadata = 
					(RecordDataMetadata)this.trimmedIntegratedDOSAndCFDGraphUtils.getVisScheme().getMetadataLookup().lookup(this.targetEdgeRecordMetadataID);
			
			vftreeData.getGraphVertexFeature().getNonMandatoryAdditionalColumnNameSet().forEach(c->{
				this.targetEdgeRecordDataNonMandatoryAdditionalFeatureColumSetToBeMapped.add(targetEdgeRecordMetadata.getDataTableSchema().getColumn(c));
			});
		}
		
	}

	///////////////////////////
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
		return targetVfTreeDataContainingNode;
	}


	/**
	 * @return the targetGenericGraphMetadataID
	 */
	public MetadataID getTargetGenericGraphMetadataID() {
		return targetVfTreeMetadataID;
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
	 * @return the targetNodeRecordDataNonMandatoryAdditionalFeatureColumSetToBeMapped
	 */
	public Set<DataTableColumn> getTargetNodeRecordDataNonMandatoryAdditionalFeatureColumSetToBeMapped() {
		return targetNodeRecordDataNonMandatoryAdditionalFeatureColumSetToBeMapped;
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
	 * @return the targetEdgeRecordDataNonMandatoryAdditionalFeatureColumSetToBeMapped
	 */
	public Set<DataTableColumn> getTargetEdgeRecordDataNonMandatoryAdditionalFeatureColumSetToBeMapped() {
		return targetEdgeRecordDataNonMandatoryAdditionalFeatureColumSetToBeMapped;
	}
}
