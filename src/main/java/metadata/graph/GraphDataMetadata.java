package metadata.graph;

import java.util.LinkedHashSet;
import java.util.Set;

import basic.VfNotes;
import metadata.CompositeDataMetadata;
import metadata.DataType;
import metadata.MetadataID;
import metadata.MetadataName;
import metadata.SourceType;
import metadata.graph.feature.GraphEdgeFeature;
import metadata.graph.feature.GraphVertexFeature;
import metadata.graph.type.GraphMetadataType;
import operation.OperationID;

/**
 * graph data defined in visframe;
 * 
 * @author tanxu
 *
 */
public class GraphDataMetadata extends CompositeDataMetadata {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4301550589005828936L;
	
	//////////////////////////////
	private final MetadataName nodeRecordDataName;
	private final MetadataName edgeRecordDataName;
	private final GraphVertexFeature graphNodeFeature;
	private final GraphEdgeFeature graphEdgeFeature;
	
	//the observed status of the graph type based on the data in the node/edge data table;
	private final GraphMetadataType observedGraphType;
	
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
	 * @param graphNodeFeature the id columns should be the same with the primary key columns of the node record data;
	 * @param graphEdgeFeature the id columns should be the same with the primary key columns of the edge record data;
	 * @param observedGraphType
	 */
	public GraphDataMetadata(
			MetadataName name, VfNotes notes, 
			SourceType sourceType,
			OperationID sourceOperationID,
			
			////
			MetadataName nodeRecordDataName,
			MetadataName edgeRecordDataName,
			GraphVertexFeature graphNodeFeature,
			GraphEdgeFeature graphEdgeFeature,
			GraphMetadataType observedGraphType
			) {
		super(name, notes, sourceType, null, sourceOperationID);//
		// TODO validations
		
		this.nodeRecordDataName = nodeRecordDataName;
		this.edgeRecordDataName = edgeRecordDataName;
		
		this.graphNodeFeature = graphNodeFeature;
		this.graphEdgeFeature = graphEdgeFeature;
		
		this.observedGraphType = observedGraphType;
	}
	
	public GraphMetadataType getObservedGraphType() {
		return this.observedGraphType;
	}

	public GraphVertexFeature getGraphVertexFeature () {
		return this.graphNodeFeature;
	}
	public GraphEdgeFeature getGraphEdgeFeature () {
		return this.graphEdgeFeature;
	}
	
	public MetadataID getNodeRecordMetadataID() {
		return new MetadataID(this.getNodeRecordDataName(),DataType.RECORD);
	}
	public MetadataName getNodeRecordDataName() {
		return nodeRecordDataName;
	}

	public MetadataID getEdgeRecordMetadataID() {
		return new MetadataID(this.getEdgeRecordDataName(),DataType.RECORD);
	}
	public MetadataName getEdgeRecordDataName() {
		return edgeRecordDataName;
	}
	
//	/**
//	 *
//	 * @return
//	 */
//	public Set<DataTableColumnName> getNodeRecordDataStructuralColumnNameSet(){
//		//TODO
//		return null;
//	}
//	
//	
//	public Set<DataTableColumnName> getEdgeRecordDataStructuralColumnNameSet(){
//		//TODO
//		return null;
//	}
	
	
	/////////////////////////////
	@Override
	public DataType getDataType() {
		return DataType.GRAPH;
	}//return GRAPH

	
	@Override
	public DataType getComponentDataType() {
		return DataType.RECORD;
	}
	
	@Override
	public Set<MetadataID> getComponentRecordDataMetadataIDSet() {
		Set<MetadataID> ret = new LinkedHashSet<>();
		
		ret.add(this.getNodeRecordMetadataID());
		ret.add(this.getEdgeRecordMetadataID());
		
		return ret;
	}

	
	////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((edgeRecordDataName == null) ? 0 : edgeRecordDataName.hashCode());
		result = prime * result + ((graphEdgeFeature == null) ? 0 : graphEdgeFeature.hashCode());
		result = prime * result + ((graphNodeFeature == null) ? 0 : graphNodeFeature.hashCode());
		result = prime * result + ((nodeRecordDataName == null) ? 0 : nodeRecordDataName.hashCode());
		result = prime * result + ((observedGraphType == null) ? 0 : observedGraphType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof GraphDataMetadata))
			return false;
		GraphDataMetadata other = (GraphDataMetadata) obj;
		if (edgeRecordDataName == null) {
			if (other.edgeRecordDataName != null)
				return false;
		} else if (!edgeRecordDataName.equals(other.edgeRecordDataName))
			return false;
		if (graphEdgeFeature == null) {
			if (other.graphEdgeFeature != null)
				return false;
		} else if (!graphEdgeFeature.equals(other.graphEdgeFeature))
			return false;
		if (graphNodeFeature == null) {
			if (other.graphNodeFeature != null)
				return false;
		} else if (!graphNodeFeature.equals(other.graphNodeFeature))
			return false;
		if (nodeRecordDataName == null) {
			if (other.nodeRecordDataName != null)
				return false;
		} else if (!nodeRecordDataName.equals(other.nodeRecordDataName))
			return false;
		if (observedGraphType == null) {
			if (other.observedGraphType != null)
				return false;
		} else if (!observedGraphType.equals(other.observedGraphType))
			return false;
		return true;
	}
	
	
}
