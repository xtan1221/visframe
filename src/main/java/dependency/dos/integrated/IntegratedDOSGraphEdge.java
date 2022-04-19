package dependency.dos.integrated;

import java.io.Serializable;

import dependency.dos.DOSEdge.DOSEdgeType;
import operation.OperationID;

/**
 * distinguish from {@link DOSEdgeImpl} which does not contain the copy index information;
 * @author tanxu
 *
 */
public final class IntegratedDOSGraphEdge implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7490919103295624922L;
	
	///////////////////////
	private final IntegratedDOSGraphNode dependingNode;
	private final IntegratedDOSGraphNode dependedNode;
	private final DOSEdgeType type;
	private final OperationID operationID;
	/**
	 * 
	 * if the type is OPERATION, 
	 * 		the copy index of the VCDNode to whose VSComponent the Operation is assigned;
	 * 		note that the output Metadata of operation is assigned to the same VSComponent as the Operation; thus the copy index is the same for the depending Metadata IntegratedDOSGraphNode;
	 * if the type is COMPOSITE_DATA_COMPONENT, 
	 * 		the copy index of the VCDNode to whose VSComponent the composite Metadata/depended IntegratedDOSGraphNode is assigned;
	 * 		note that the component Metadata is assigned to the same VSComponent as the composite Metadata, thus the copy index is the same;
	 */
	private final int copyIndex;
	
	/**
	 * constructor
	 * @param dependingNode
	 * @param dependedNode
	 * @param type
	 * @param operationID
	 * @param copyIndex
	 */
	IntegratedDOSGraphEdge(
			IntegratedDOSGraphNode dependingNode,
			IntegratedDOSGraphNode dependedNode,
			DOSEdgeType type,
			OperationID operationID,
			int copyIndex
			){
		
		
		this.dependingNode = dependingNode;
		this.dependedNode = dependedNode;
		this.type = type;
		this.operationID = operationID;
		this.copyIndex = copyIndex;
	}

	/**
	 * @return the dependingNode
	 */
	public IntegratedDOSGraphNode getDependingNode() {
		return dependingNode;
	}

	/**
	 * @return the dependedNode
	 */
	public IntegratedDOSGraphNode getDependedNode() {
		return dependedNode;
	}

	/**
	 * @return the type
	 */
	public DOSEdgeType getType() {
		return type;
	}

	/**
	 * @return the operationID
	 */
	public OperationID getOperationID() {
		return operationID;
	}

	/**
	 * @return the copyIndex
	 */
	public int getCopyIndex() {
		return copyIndex;
	}

	////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + copyIndex;
		result = prime * result + ((dependedNode == null) ? 0 : dependedNode.hashCode());
		result = prime * result + ((dependingNode == null) ? 0 : dependingNode.hashCode());
		result = prime * result + ((operationID == null) ? 0 : operationID.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof IntegratedDOSGraphEdge))
			return false;
		IntegratedDOSGraphEdge other = (IntegratedDOSGraphEdge) obj;
		if (copyIndex != other.copyIndex)
			return false;
		if (dependedNode == null) {
			if (other.dependedNode != null)
				return false;
		} else if (!dependedNode.equals(other.dependedNode))
			return false;
		if (dependingNode == null) {
			if (other.dependingNode != null)
				return false;
		} else if (!dependingNode.equals(other.dependingNode))
			return false;
		if (operationID == null) {
			if (other.operationID != null)
				return false;
		} else if (!operationID.equals(other.operationID))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	
	
}
