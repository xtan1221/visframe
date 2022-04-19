package metadata.graph.vftree;

import basic.VfNotes;
import metadata.DataType;
import metadata.MetadataName;
import metadata.SourceType;
import metadata.graph.GraphDataMetadata;
import metadata.graph.type.GraphMetadataType;
import metadata.graph.vftree.feature.VfTreeEdgeFeature;
import metadata.graph.vftree.feature.VfTreeNodeFeature;
import operation.OperationID;


/**
 * {@link VfTreeDataMetadata} is a subtype of GraphDataMetadata;
 * In graph theory, tree is undirected, acyclic, no selfloop, no parallel edges graph;
 * 
 * in visframe, VfTreeDataMetadata contains tree with the following features:
 * 1. has a default root node in the tree data file despite the tree is rooted or not
	2. Each node is assigned a random unique integer as its id;
	3. each edge is explicitly represented as parent node – child node
	4. sibling nodes order index are represented and fixed and cannot change 
	 	note that tree trimming operation can take a tree as input and generated a new tree with those features changed;
	5. edge length and bootstrap value are default features with assigned explicit mandatory columns in edge data table, whose values can be null;
	6. Node/edge data table has a set of visframe pre-defined attributes in the additional features column set for GraphNodeFeaturea and GraphEdgeFeature;
 in sum, TreeDataMetadata represent a generic tree with a set of visframe defined features;
 those features are specifically included to facilitate the frequently used phylogenetic tree analysis and visualization
 * 
 * 
 * A GraphDataMetadata may contain tree, but if it need to be used as input of tree trimming operations, it must be transformed to a TreeDataMetadata first;
 * 
 * @author tanxu
 *	
 */
public class VfTreeDataMetadata extends GraphDataMetadata{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6628142064289346941L;

	///////////////////////////
	/**
	 * factory method to create GraphMetadataType for a VfTreeDataMetadata;
	 * note that {@link DataType#vfTREE} is a undirected graph in visframe
	 * @return
	 */
	public static GraphMetadataType makeVfTreeGraphType() {
		return new GraphMetadataType(false, true, false, false, false, false);
	}
	
	///////////////////
	private final Integer bootstrapIteration; //could be null or positive integer
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 * @param sourceType
	 * @param sourceOperationID
	 * @param nodeRecordDataName
	 * @param nodeRecordDataTableName
	 * @param edgeRecordDataName
	 * @param edgeRecordDataTableName
	 * @param graphNodeFeature
	 * @param graphEdgeFeature
	 * @param observedGraphType
	 * @param bootstrapIteration
	 */
	public VfTreeDataMetadata(
			MetadataName name, VfNotes notes, 
			SourceType sourceType, OperationID sourceOperationID,
			MetadataName nodeRecordDataName, 
			MetadataName edgeRecordDataName,
			VfTreeNodeFeature graphNodeFeature,
			VfTreeEdgeFeature graphEdgeFeature,
//			GraphMetadataType observedGraphType,
			//////
			Integer bootstrapIteration
			) {
		super(name, notes, sourceType, sourceOperationID, nodeRecordDataName, edgeRecordDataName,
				graphNodeFeature, graphEdgeFeature, makeVfTreeGraphType());
		
		//bootstrapIteration is either null or positive integer
		if(bootstrapIteration!=null &&bootstrapIteration<=0) {
			throw new IllegalArgumentException("bootstrapIteration should either be null or positive integer!");
		}
		
		this.bootstrapIteration = bootstrapIteration;
	}

	
	public Integer getBootstrapIteration() {
		return bootstrapIteration;
	}
	
	
	/////////////////
	@Override
	public VfTreeNodeFeature getGraphVertexFeature () {
		return (VfTreeNodeFeature) super.getGraphVertexFeature();
	}
	
	@Override
	public VfTreeEdgeFeature getGraphEdgeFeature () {
		return (VfTreeEdgeFeature) super.getGraphEdgeFeature();
	}
	
	//
	@Override
	public DataType getDataType(){
		return DataType.vfTREE;//return NEWICKTREE
	}


	//////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((bootstrapIteration == null) ? 0 : bootstrapIteration.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof VfTreeDataMetadata))
			return false;
		VfTreeDataMetadata other = (VfTreeDataMetadata) obj;
		if (bootstrapIteration == null) {
			if (other.bootstrapIteration != null)
				return false;
		} else if (!bootstrapIteration.equals(other.bootstrapIteration))
			return false;
		return true;
	}

}
