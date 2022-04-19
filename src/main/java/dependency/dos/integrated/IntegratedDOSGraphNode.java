package dependency.dos.integrated;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import dependency.vcd.VCDNodeImpl;
import function.composition.CompositionFunction;
import metadata.DataType;
import metadata.MetadataID;
import operation.Operation;
import operation.graph.SingleGenericGraphAsInputOperation;
import rdb.table.data.DataTableColumnName;

/**
 * class for node of integrated DOS graph;
 * 
 * @author tanxu
 *
 */
public class IntegratedDOSGraphNode implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2331601917047798385L;
	
	////////////////////
	private final VCDNodeImpl assignedVCDNode;
	private final MetadataID metadataID;
	/**
	 * the copy index of the {@link #assignedVCDNode} of the {@link #metadataID} represented by this {@link IntegratedDOSGraphNode}
	 */
	private final int copyIndex;
	
	///////////////////////////////////////////////
	//related with solution set mapping
	//those fields are built during the process of building the owner VisSchemeAppliedArchive
	//should be included in the equals and hashcode methods????TODO
	/**
	 * the set of columns of the {@link #metadataID} that are 
	 * 1. used as input by any Operations on the {@link #trimmedIntegratedDOSGraph}
	 * 		retrieved by {@link Operation#getInputRecordMetadataIDInputColumnNameSetMap()} method;
	 * 2. depended record data by any CompositionFunction on the {@link #trimmedIntegratedCFDGraph}; 
	 * 		retrieved by {@link CompositionFunction#getDependedRecordMetadataIDInputColumnNameSetMap(context.VisframeContext)} method;
	 * 
	 * only relevant if {@link #metadataID} is of RECORD type, must be null otherwise;
	 * 
	 * value is set by {@link SolutionSetSelectorAndMapper} as a preprocessing step;
	 */
	private Set<DataTableColumnName> inputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs = null;
	
	/**
	 * the IntegratedDOSNode containing the parent generic graph data of this IntegratedDOSNode;
	 * only relevant if this IntegratedDOSNode contains a record metadata that is a component of a generic graph data;
	 * must be null otherwise;
	 * 
	 * value is set by {@link SolutionSetSelectorAndMapper} as a preprocessing step;
	 */
	private IntegratedDOSGraphNode parentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs = null;
	
	/**
	 * the IntegratedDOSNode containing the node record component of this IntegratedDOSNode;
	 * only relevant if {@link #metadataID} is of GRAPH or vfTREE type and its node record component IntegratedDOSNode is also on the trimmed integrated DOS graph;
	 * must be null otherwise;
	 * 
	 * value is set by {@link SolutionSetSelectorAndMapper} as a preprocessing step;
	 * the input columns of node component on the trimmed integrated CFD and DOS graph can be retrieved by 
	 * {@link #getInputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs()} of this field if not null;
	 */
	private IntegratedDOSGraphNode nodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs = null;
	/**
	 * the IntegratedDOSNode containing the edge record component of this IntegratedDOSNode;
	 * only relevant if {@link #metadataID} is of GRAPH or vfTREE type and its node record component IntegratedDOSNode is also on the trimmed integrated DOS graph; 
	 * must be null otherwise;

	 * value is set by {@link SolutionSetSelectorAndMapper} as a preprocessing step;
	 * the input columns of node component on the trimmed integrated CFD and DOS graph can be retrieved by 
	 * {@link #getInputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs()} of this field if not null;
	 */
	private IntegratedDOSGraphNode edgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs = null;
	
	/**
	 * whether or not {@link #metadataID} is used as input Metadata of one or more {@link SingleGenericGraphAsInputOperation}s on the trimmed integrated DOS graph;
	 * false otherwise;
	 * 
	 * facilitate to identify the {@link #allowedSourceMetadataDataTypeSet} of a source Metadata from the host VisProjectDBContext to be mapped to this IntegratedDOSNode;
	 * 
	 * value is set by {@link SolutionSetSelectorAndMapper} as a preprocessing step;
	 */
	private boolean inputMetadataOfSingleGenericGraphAsInputOperation = false;
	
	/**
	 * whether or not {@link #metadataID} is used as input Metadata of one or more {@link VfTreeTrimmingOperationBase}s on the trimmed integrated DOS graph;
	 * false otherwise;
	 * 
	 * facilitate to identify the {@link #allowedSourceMetadataDataTypeSet} of a source Metadata from the host VisProjectDBContext to be mapped to this IntegratedDOSNode;
	 * 
	 * value is set by {@link SolutionSetSelectorAndMapper} as a preprocessing step;
	 */
	private boolean inputMetadataOfVfTreeTrimmingOperationBase = false;
	
	/**
	 * set of DataTypes of Metadata from host VisProjectDBContext that are allowed to be mapped to the Metadata contained by this IntegratedDOSNode;
	 * 
	 * the set is based on the value of {@link #inputMetadataOfSingleGenericGraphAsInputOperation} and {@link #inputMetadataOfVfTreeTrimmingOperationBase};
	 * can only be invoked after the two values are set by the {@link SolutionSetSelectorAndMapper};
	 * 
	 * see {@link #getAllowedSourceMetadataDataTypeSet()} method for details;
	 */
	private Set<DataType> allowedSourceMetadataDataTypeSet = null;
	
	/**
	 * constructor
	 * @param assignedVCDNode
	 * @param metadataID
	 * @param copyIndex
	 */
	public IntegratedDOSGraphNode(VCDNodeImpl assignedVCDNode, MetadataID metadataID, int copyIndex){
		if(assignedVCDNode==null)
			throw new IllegalArgumentException("given assignedVCDNode cannot be null!");
		if(metadataID==null)
			throw new IllegalArgumentException("given metadataID cannot be null!");
		
		this.assignedVCDNode = assignedVCDNode;
		this.metadataID = metadataID;
		this.copyIndex = copyIndex;
	}
	
	/**
	 * @return the assignedVCDNode
	 */
	public VCDNodeImpl getAssignedVCDNode() {
		return assignedVCDNode;
	}
	
	/**
	 * @return the metadataID
	 */
	public MetadataID getMetadataID() {
		return metadataID;
	}

	/**
	 * @return the copyIndex
	 */
	public int getCopyIndex() {
		return copyIndex;
	}
	
	/////////////////////////////
	/**
	 * @return the inputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs
	 */
	public Set<DataTableColumnName> getInputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs() {
		return inputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs;
	}

	/**
	 * @param inputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs the inputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs to set
	 */
	public void setInputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs(
			Set<DataTableColumnName> inputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs) {
		this.inputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs = inputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs;
	}

	/**
	 * @return the parentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs
	 */
	public IntegratedDOSGraphNode getParentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs() {
		return parentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs;
	}

	/**
	 * @param parentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs the parentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs to set
	 */
	public void setParentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs(
			IntegratedDOSGraphNode parentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs) {
		this.parentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs = parentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs;
	}
	
	/**
	 * @return the nodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs
	 */
	public IntegratedDOSGraphNode getNodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs() {
		return nodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs;
	}

	/**
	 * @param nodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs the nodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs to set
	 */
	public void setNodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs(
			IntegratedDOSGraphNode nodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs) {
		this.nodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs = nodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs;
	}

	/**
	 * @return the edgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs
	 */
	public IntegratedDOSGraphNode getEdgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs() {
		return edgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs;
	}
	
	/**
	 * @param edgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs the edgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs to set
	 */
	public void setEdgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs(
			IntegratedDOSGraphNode edgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs) {
		this.edgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs = edgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs;
	}
	
	/**
	 * @return the inputMetadataOfSingleGenericGraphAsInputOperation
	 */
	public boolean isInputMetadataOfSingleGenericGraphAsInputOperation() {
		return inputMetadataOfSingleGenericGraphAsInputOperation;
	}

	/**
	 * @param inputMetadataOfSingleGenericGraphAsInputOperation the inputMetadataOfSingleGenericGraphAsInputOperation to set
	 */
	public void setInputMetadataOfSingleGenericGraphAsInputOperation(
			boolean inputMetadataOfSingleGenericGraphAsInputOperation) {
		this.inputMetadataOfSingleGenericGraphAsInputOperation = inputMetadataOfSingleGenericGraphAsInputOperation;
	}

	/**
	 * @return the inputMetadataOfVfTreeTrimmingOperationBase
	 */
	public boolean isInputMetadataOfVfTreeTrimmingOperationBase() {
		return inputMetadataOfVfTreeTrimmingOperationBase;
	}

	/**
	 * @param inputMetadataOfVfTreeTrimmingOperationBase the inputMetadataOfVfTreeTrimmingOperationBase to set
	 */
	public void setInputMetadataOfVfTreeTrimmingOperationBase(boolean inputMetadataOfVfTreeTrimmingOperationBase) {
		this.inputMetadataOfVfTreeTrimmingOperationBase = inputMetadataOfVfTreeTrimmingOperationBase;
	}


	/**
	 * 
	 * @return
	 */
	public Set<DataType> getAllowedSourceMetadataDataTypeSet(){
		if(this.allowedSourceMetadataDataTypeSet==null) {
			this.allowedSourceMetadataDataTypeSet = new HashSet<>();
			if(this.isInputMetadataOfVfTreeTrimmingOperationBase()) {
				this.allowedSourceMetadataDataTypeSet.add(DataType.vfTREE);
			}else {
				if(this.isInputMetadataOfSingleGenericGraphAsInputOperation()) {
					this.allowedSourceMetadataDataTypeSet.add(DataType.vfTREE);
					this.allowedSourceMetadataDataTypeSet.add(DataType.GRAPH);
				}else {
					this.allowedSourceMetadataDataTypeSet.add(DataType.RECORD);
				}
			}
		}
		
		return this.allowedSourceMetadataDataTypeSet;
	}

	

	////////////////////////////
	///////////////////////////////
	//TODO
	//equals and hashcode methods only include the final fields??? 
	//non final fields (even though not transient) may change in the process of building the integrated DOS graph during which the equals method may be used???
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignedVCDNode == null) ? 0 : assignedVCDNode.hashCode());
		result = prime * result + copyIndex;
		result = prime * result + ((metadataID == null) ? 0 : metadataID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof IntegratedDOSGraphNode))
			return false;
		IntegratedDOSGraphNode other = (IntegratedDOSGraphNode) obj;
		if (assignedVCDNode == null) {
			if (other.assignedVCDNode != null)
				return false;
		} else if (!assignedVCDNode.equals(other.assignedVCDNode))
			return false;
		if (copyIndex != other.copyIndex)
			return false;
		if (metadataID == null) {
			if (other.metadataID != null)
				return false;
		} else if (!metadataID.equals(other.metadataID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "IntegratedDOSGraphNode [assignedVCDNode=" + assignedVCDNode + ", metadataID=" + metadataID
				+ ", copyIndex=" + copyIndex + "]";
	}

	
	
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result
//				+ ((allowedSourceMetadataDataTypeSet == null) ? 0 : allowedSourceMetadataDataTypeSet.hashCode());
//		result = prime * result + ((assignedVCDNode == null) ? 0 : assignedVCDNode.hashCode());
//		result = prime * result + copyIndex;
//		result = prime * result + ((edgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs == null) ? 0
//				: edgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs.hashCode());
//		result = prime * result + ((inputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs == null) ? 0
//				: inputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs.hashCode());
//		result = prime * result + (inputMetadataOfSingleGenericGraphAsInputOperation ? 1231 : 1237);
//		result = prime * result + (inputMetadataOfVfTreeTrimmingOperationBase ? 1231 : 1237);
//		result = prime * result + ((metadataID == null) ? 0 : metadataID.hashCode());
//		result = prime * result + ((nodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs == null) ? 0
//				: nodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs.hashCode());
//		result = prime * result + ((parentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs == null) ? 0
//				: parentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!(obj instanceof IntegratedDOSGraphNode))
//			return false;
//		IntegratedDOSGraphNode other = (IntegratedDOSGraphNode) obj;
//		if (allowedSourceMetadataDataTypeSet == null) {
//			if (other.allowedSourceMetadataDataTypeSet != null)
//				return false;
//		} else if (!allowedSourceMetadataDataTypeSet.equals(other.allowedSourceMetadataDataTypeSet))
//			return false;
//		if (assignedVCDNode == null) {
//			if (other.assignedVCDNode != null)
//				return false;
//		} else if (!assignedVCDNode.equals(other.assignedVCDNode))
//			return false;
//		if (copyIndex != other.copyIndex)
//			return false;
//		if (edgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs == null) {
//			if (other.edgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs != null)
//				return false;
//		} else if (!edgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs
//				.equals(other.edgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs))
//			return false;
//		if (inputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs == null) {
//			if (other.inputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs != null)
//				return false;
//		} else if (!inputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs
//				.equals(other.inputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs))
//			return false;
//		if (inputMetadataOfSingleGenericGraphAsInputOperation != other.inputMetadataOfSingleGenericGraphAsInputOperation)
//			return false;
//		if (inputMetadataOfVfTreeTrimmingOperationBase != other.inputMetadataOfVfTreeTrimmingOperationBase)
//			return false;
//		if (metadataID == null) {
//			if (other.metadataID != null)
//				return false;
//		} else if (!metadataID.equals(other.metadataID))
//			return false;
//		if (nodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs == null) {
//			if (other.nodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs != null)
//				return false;
//		} else if (!nodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs
//				.equals(other.nodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs))
//			return false;
//		if (parentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs == null) {
//			if (other.parentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs != null)
//				return false;
//		} else if (!parentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs
//				.equals(other.parentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs))
//			return false;
//		return true;
//	}
//	
	
}
