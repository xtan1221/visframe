package dependency.cfd.integrated;

import java.io.Serializable;

/**
 * distinguish from the {@link CFDEdgeImpl} which does not contain copy index information;
 * @author tanxu
 *
 */
public class IntegratedCFDGraphEdge implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5391592975085087808L;
	////////////////////
	private final IntegratedCFDGraphNode dependingNode;
	private final IntegratedCFDGraphNode dependedNode;
	private final boolean basedOnIndependentFreeInputVariableType;
	private final boolean basedOnAssignedTarget; 
//	/**
//	 * copy index of the VCDNode to whose VSComponent the CompositionFunction of the dependingNode is assigned;
//	 */
//	private final int copyIndex;
	
	/**
	 * constructor
	 * @param dependingNode
	 * @param dependedNode
	 * @param basedOnIndependentFreeInputVariableType
	 * @param basedOnAssignedTarget
	 * @param copyIndex
	 */
	IntegratedCFDGraphEdge(
			IntegratedCFDGraphNode dependingNode,
			IntegratedCFDGraphNode dependedNode,
			boolean basedOnIndependentFreeInputVariableType,
			boolean basedOnAssignedTarget
//			int copyIndex
			){
		
		this.dependingNode = dependingNode;
		this.dependedNode = dependedNode;
		this.basedOnIndependentFreeInputVariableType = basedOnIndependentFreeInputVariableType;
		this.basedOnAssignedTarget = basedOnAssignedTarget;
//		this.copyIndex = copyIndex;
	}

	/**
	 * @return the dependingNode
	 */
	protected IntegratedCFDGraphNode getDependingNode() {
		return dependingNode;
	}

	/**
	 * @return the dependedNode
	 */
	protected IntegratedCFDGraphNode getDependedNode() {
		return dependedNode;
	}

	/**
	 * @return the basedOnIndependentFreeInputVariableType
	 */
	protected boolean isBasedOnIndependentFreeInputVariableType() {
		return basedOnIndependentFreeInputVariableType;
	}

	/**
	 * @return the basedOnAssignedTarget
	 */
	protected boolean isBasedOnAssignedTarget() {
		return basedOnAssignedTarget;
	}

//	/**
//	 * @return the copyIndex
//	 */
//	protected int getCopyIndex() {
//		return copyIndex;
//	}

	
	/////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (basedOnAssignedTarget ? 1231 : 1237);
		result = prime * result + (basedOnIndependentFreeInputVariableType ? 1231 : 1237);
//		result = prime * result + copyIndex;
		result = prime * result + ((dependedNode == null) ? 0 : dependedNode.hashCode());
		result = prime * result + ((dependingNode == null) ? 0 : dependingNode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof IntegratedCFDGraphEdge))
			return false;
		IntegratedCFDGraphEdge other = (IntegratedCFDGraphEdge) obj;
		if (basedOnAssignedTarget != other.basedOnAssignedTarget)
			return false;
		if (basedOnIndependentFreeInputVariableType != other.basedOnIndependentFreeInputVariableType)
			return false;
//		if (copyIndex != other.copyIndex)
//			return false;
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
		return true;
	}
	
}
