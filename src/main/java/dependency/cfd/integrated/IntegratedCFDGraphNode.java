package dependency.cfd.integrated;

import java.io.Serializable;

import dependency.vcd.VCDNodeImpl;
import function.composition.CompositionFunctionID;


/**
 * class for node of integrated CFD graph;
 * 
 * @author tanxu
 *
 */
public final class IntegratedCFDGraphNode implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3496175374908085398L;
	
	//////////////////
	private final VCDNodeImpl assignedVCDNode;
	private final CompositionFunctionID cfID;
	/**
	 * copy index of the assigned assignedVCDNode of the contained CFID represented by this {@link IntegratedCFDGraphNode}
	 */
	private final int copyIndex;
	
	/**
	 * constructor
	 * @param assignedVCDNode
	 * @param cfID
	 * @param copyIndex
	 */
	public IntegratedCFDGraphNode(
			VCDNodeImpl assignedVCDNode,
			CompositionFunctionID cfID,
			int copyIndex
			){
		
		this.assignedVCDNode = assignedVCDNode;
		this.cfID = cfID;
		this.copyIndex = copyIndex;
	}

	
	/**
	 * @return the assignedVCDNode
	 */
	public VCDNodeImpl getAssignedVCDNode() {
		return assignedVCDNode;
	}


	/**
	 * @return the cfID
	 */
	public CompositionFunctionID getCfID() {
		return cfID;
	}


	/**
	 * @return the copyIndex
	 */
	public int getCopyIndex() {
		return copyIndex;
	}


	///////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignedVCDNode == null) ? 0 : assignedVCDNode.hashCode());
		result = prime * result + ((cfID == null) ? 0 : cfID.hashCode());
		result = prime * result + copyIndex;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof IntegratedCFDGraphNode))
			return false;
		IntegratedCFDGraphNode other = (IntegratedCFDGraphNode) obj;
		if (assignedVCDNode == null) {
			if (other.assignedVCDNode != null)
				return false;
		} else if (!assignedVCDNode.equals(other.assignedVCDNode))
			return false;
		if (cfID == null) {
			if (other.cfID != null)
				return false;
		} else if (!cfID.equals(other.cfID))
			return false;
		if (copyIndex != other.copyIndex)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "IntegratedCFDGraphNode [assignedVCDNode=" + assignedVCDNode + ", cfID=" + cfID + ", copyIndex="
				+ copyIndex + "]";
	}

	
	
}
