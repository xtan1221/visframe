package dependency.vccl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import basic.VfNotes;
import dependency.cfd.integrated.IntegratedCFDGraphNode;
import dependency.dos.integrated.IntegratedDOSGraphNode;
import dependency.vcd.VCDNodeImpl;
import function.composition.CompositionFunctionID;
import metadata.MetadataID;

/**
 * 
 * @author tanxu
 *
 */
public class VSCopy implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7225392193020726423L;
	
	
	//////////////////////////////
	private final VCDNodeImpl ownerVCDNode;
	private final int index;
	/**
	 * notes
	 */
	private final VfNotes notes;
	////////////////////////
	//fields built during the process of building the VCCL graph;
	
	/**
	 * map from the depended VCD node to the copy linked by this one;
	 */
	private Map<VCDNodeImpl, VSCopy> dependedVCDNodeLinkedCopyMap;
	
	
	///////////////
	///related with integrated DOS and CFD graph
	///TODO included in the equals and hashcode methods?
	/**
	 * map from the MetadataID assigned to the {@link #ownerVCDNode} to the {@link IntegratedDOSGraphNode} in the integrated DOS graph;
	 */
	private Map<MetadataID, IntegratedDOSGraphNode> assignedMetadataIDIntegratedDOSNodeMap;
	/**
	 * map from the CompositionFunctionID assigned to the {@link #ownerVCDNode} to the {@link IntegratedCFDGraphNode} in the integrated CFD graph;
	 */
	private Map<CompositionFunctionID, IntegratedCFDGraphNode> assignedCFIDIntegratedCFDNodeMap;
	
	
	/**
	 * constructor
	 * @param ownerVCDNode
	 * @param index
	 */
	public VSCopy(VCDNodeImpl ownerVCDNode, int index, VfNotes notes){
		if(ownerVCDNode==null)
			throw new IllegalArgumentException("given ownerVCDNode cannot be null!");
		
		
		this.ownerVCDNode = ownerVCDNode;
		this.index = index;
		this.notes = notes;
	}
	
	
	public void makeIntegratedDOSNodes() {
		this.assignedMetadataIDIntegratedDOSNodeMap = new HashMap<>();
	
		this.getOwnerVCDNode().getAssignedMetadataIDSet().forEach(mid->{
			this.assignedMetadataIDIntegratedDOSNodeMap.put(mid, new IntegratedDOSGraphNode(this.getOwnerVCDNode(), mid, this.getIndex()));
		});
		
	}
	
	
	/**
	 * @return the assignedMetadataIDIntegratedDOSNodeMap
	 */
	public Map<MetadataID, IntegratedDOSGraphNode> getAssignedMetadataIDIntegratedDOSNodeMap() {
		return assignedMetadataIDIntegratedDOSNodeMap;
	}
	
	public void makeIntegratedCFDNodes() {
		this.assignedCFIDIntegratedCFDNodeMap = new HashMap<>();
	
		this.getOwnerVCDNode().getAssignedCFIDSet().forEach(cfid->{
			this.assignedCFIDIntegratedCFDNodeMap.put(cfid, new IntegratedCFDGraphNode(this.getOwnerVCDNode(), cfid, this.getIndex()));
		});
		
	}
	
	
	/**
	 * @return the assignedCFIDIntegratedCFDNodeMap
	 */
	public Map<CompositionFunctionID, IntegratedCFDGraphNode> getAssignedCFIDIntegratedCFDNodeMap() {
		return assignedCFIDIntegratedCFDNodeMap;
	}


	////////////////////
	/**
	 * @return the vcdNode
	 */
	public VCDNodeImpl getOwnerVCDNode() {
		return ownerVCDNode;
	}

	/**
	 * @return the notes
	 */
	public VfNotes getNotes() {
		return notes;
	}
	

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the dependedVCDNodeLinkedCopyMap
	 */
	public Map<VCDNodeImpl, VSCopy> getDependedVCDNodeLinkedCopyMap() {
		return dependedVCDNodeLinkedCopyMap;
	}
	
	/**
	 * should be set when the full copy link graph is built;
	 * 
	 * see {@link VCCLGraphBuilder} for more details
	 * @param dependedVCDNodeLinkedCopyMap
	 */
	public void setDependedVCDNodeLinkedCopyMap(Map<VCDNodeImpl, VSCopy> dependedVCDNodeLinkedCopyMap) {
		this.dependedVCDNodeLinkedCopyMap = dependedVCDNodeLinkedCopyMap;
	}
	
//	/**
//	 * add the link to the give VSCopy;
//	 * the owner VCDNode of the given VSCopy must be a depended VCDNode of the owner VCDNode of this VSCopy;
//	 * @param dependedVSCopy
//	 */
//	public void addLink(VSCopy dependedVSCopy) {
//		if(!this.ownerVCDNode.getDependedVCDNodeSet().contains(dependedVSCopy.getOwnerVCDNode()))
//			throw new IllegalArgumentException("given dependedVSCopy is not of a depended VCDNode of owner VCDNode of this VSCopy!");
//		
//		this.dependedVCDNodeLinkedCopyMap.put(dependedVSCopy.getOwnerVCDNode(), dependedVSCopy);
//	}
	
	/**
	 * remove the link to the give VSCopy;
	 * the owner VCDNode of the given VSCopy must be a depended VCDNode of the owner VCDNode of this VSCopy;
	 * @param dependingVSCopy
	 */
	public void removeLink(VSCopy dependedVSCopy) {
		if(!this.ownerVCDNode.getDependedVCDNodeSet().contains(dependedVSCopy.getOwnerVCDNode()))
			throw new IllegalArgumentException("given dependedVSCopy is not of a depended VCDNode of owner VCDNode of this VSCopy!");
		
		this.dependedVCDNodeLinkedCopyMap.remove(dependedVSCopy.getOwnerVCDNode());
	}



	////////////////////////////////////////
	///////////////////////////////
	//TODO
	//equals and hashcode methods only include the final fields??? 
	//non final fields (even though not transient) may change in the process of building the VCCL graph during which the equals method may be used???

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + ((ownerVCDNode == null) ? 0 : ownerVCDNode.hashCode());
		return result;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VSCopy))
			return false;
		VSCopy other = (VSCopy) obj;
		if (index != other.index)
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		if (ownerVCDNode == null) {
			if (other.ownerVCDNode != null)
				return false;
		} else if (!ownerVCDNode.equals(other.ownerVCDNode))
			return false;
		return true;
	}


//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result
//				+ ((assignedCFIDIntegratedCFDNodeMap == null) ? 0 : assignedCFIDIntegratedCFDNodeMap.hashCode());
//		result = prime * result + ((assignedMetadataIDIntegratedDOSNodeMap == null) ? 0
//				: assignedMetadataIDIntegratedDOSNodeMap.hashCode());
//		result = prime * result
//				+ ((dependedVCDNodeLinkedCopyMap == null) ? 0 : dependedVCDNodeLinkedCopyMap.hashCode());
//		result = prime * result + index;
//		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
//		result = prime * result + ((ownerVCDNode == null) ? 0 : ownerVCDNode.hashCode());
//		return result;
//	}
//
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!(obj instanceof VSCopy))
//			return false;
//		VSCopy other = (VSCopy) obj;
//		if (assignedCFIDIntegratedCFDNodeMap == null) {
//			if (other.assignedCFIDIntegratedCFDNodeMap != null)
//				return false;
//		} else if (!assignedCFIDIntegratedCFDNodeMap.equals(other.assignedCFIDIntegratedCFDNodeMap))
//			return false;
//		if (assignedMetadataIDIntegratedDOSNodeMap == null) {
//			if (other.assignedMetadataIDIntegratedDOSNodeMap != null)
//				return false;
//		} else if (!assignedMetadataIDIntegratedDOSNodeMap.equals(other.assignedMetadataIDIntegratedDOSNodeMap))
//			return false;
//		if (dependedVCDNodeLinkedCopyMap == null) {
//			if (other.dependedVCDNodeLinkedCopyMap != null)
//				return false;
//		} else if (!dependedVCDNodeLinkedCopyMap.equals(other.dependedVCDNodeLinkedCopyMap))
//			return false;
//		if (index != other.index)
//			return false;
//		if (notes == null) {
//			if (other.notes != null)
//				return false;
//		} else if (!notes.equals(other.notes))
//			return false;
//		if (ownerVCDNode == null) {
//			if (other.ownerVCDNode != null)
//				return false;
//		} else if (!ownerVCDNode.equals(other.ownerVCDNode))
//			return false;
//		return true;
//	}

	
	
}
