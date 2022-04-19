package dependency.dos;

import java.io.Serializable;

import metadata.MetadataID;
import operation.OperationID;


/**
 * interface for an edge on a DOSGraph
 * @author tanxu
 *
 */
public interface DOSEdge extends Serializable{
	/**
	 * return the the output Metadata of the operation or child component Metadata;
	 * source node simply means the node the edge is directing from; 
	 * @return
	 */
	MetadataID getSource();
	
	/**
	 * the input MetadataID of the Operation contained in this edge or the parent composite metadata 
	 * @return
	 */
	MetadataID getSink();
	
	DOSEdgeType getType();
	
	/**
	 * return the OperationID if DOSEdgeType is OPERATION;
	 * return null otherwise;
	 * @return
	 */
	OperationID getOperationID();
	
	/**
	 * types of DOS edge
	 * @author tanxu
	 *
	 */
	public static enum DOSEdgeType {
		OPERATION,
		COMPOSITE_DATA_COMPONENT;
	}
}
