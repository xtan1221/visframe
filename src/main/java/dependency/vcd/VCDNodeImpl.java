package dependency.vcd;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import context.scheme.VSComponent;
import dependency.vccl.VSCopy;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;
import metadata.MetadataID;
import operation.OperationID;

/**
 * an implementation of VCDNode for {@link VCDGraphBuilderImpl};
 * 
 * 
 * ////////////////////////////////////
 * see {@link VCDNode} for how to assign visframe entities to each VCDNode;
 * 
 * @author tanxu
 * 
 */
public class VCDNodeImpl implements VCDNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5237816681894762813L;

	////////////////////////////
	private final VSComponent component;
	private final int precedenceIndex;
	
	/**
	 * the set of VCDNodeImpl on which this VCDNodeImpl is directly depending on (with a VCDEdge);
	 */
	private Set<VCDNodeImpl> dependedVCDNodeSet;
	/**
	 * the set of VCDNodeImpl that are directly depending on this VCDNodeImpl;
	 */
	private Set<VCDNodeImpl> dependingVCDNodeSet;
	
	///////////////////////
//	private Integer copyNum;
	/**
	 * map from index to each VSCopy;
	 * should be set after copy number is assigned to this VCDNodeImpl in the process of building a VisSchemeAppliedArchive;
	 */
	private Map<Integer, VSCopy> vscopyIndexMap;
	

	///////////////////////
	/**
	 * assignment of visframe entities to each VCDNodeImpl/VSComponent
	 * 		Metadata
	 *			If resulted from operation, assigned to the same VSComponent as the operation
	 *			If component of a composite Metadata, assigned to the same VSComponent as the composite Metadata;
	 *			Otherwise, assigned to the first VSComponent on whose DOS graph the Metadata is present;
	 *		Operation
	 *			Assigned to the first VSCompnent on whose DOS graph the operation is present;
	 *		CFG
	 *			Assigned to the first VSComponent on whose CFD graph one or more CF of the CFG is present;
	 *		CF
	 *			Assign every CF to the same VSComponent as the host CFG;
	 */
	private Set<MetadataID> assignedMetadataIDSet;
	private Set<OperationID> assignedOperationIDSet;
	private Set<CompositionFunctionGroupID> assignedCFGIDSet;
	private Set<CompositionFunctionID> assignedCFIDSet;
	
	/**
	 * 
	 * @param component
	 */
	VCDNodeImpl(
			VSComponent component, int index
			){
		if(component==null)
			throw new IllegalArgumentException("given component cannot be null!");
		
		
		this.component = component;
		this.precedenceIndex = index;
				
		this.assignedMetadataIDSet = new LinkedHashSet<>();
		this.assignedOperationIDSet = new LinkedHashSet<>();
		this.assignedCFGIDSet = new LinkedHashSet<>();
		this.assignedCFIDSet = new LinkedHashSet<>();
		
	}
	
	/**
	 * @param dependedVCDNodeSet the dependedVCDNodeSet to set
	 */
	public void setDependedVCDNodeSet(Set<VCDNodeImpl> dependedVCDNodeSet) {
		this.dependedVCDNodeSet = dependedVCDNodeSet;
	}
	
	/**
	 * @param dependingVCDNodeSet the dependingVCDNodeSet to set
	 */
	public void setDependingVCDNodeSet(Set<VCDNodeImpl> dependingVCDNodeSet) {
		this.dependingVCDNodeSet = dependingVCDNodeSet;
	}


	
	/**
	 * @param vscopyIndexMap the vscopyIndexMap to set
	 */
	public void setVscopyIndexMap(Map<Integer, VSCopy> vscopyIndexMap) {
		this.vscopyIndexMap = vscopyIndexMap;
	}

	
	@Override
	public VSComponent getVSComponent() {
		return component;
	}
	
	@Override
	public int getPrecedenceIndex() {
		return this.precedenceIndex;
	}
	
	/**
	 * @return the dependedVCDNodeSet
	 */
	public Set<VCDNodeImpl> getDependedVCDNodeSet() {
		return dependedVCDNodeSet;
	}
	
	/**
	 * @return the dependingVCDNodeSet
	 */
	public Set<VCDNodeImpl> getDependingVCDNodeSet() {
		return dependingVCDNodeSet;
	}

//	@Override
//	public int getAssignedCopyNum() {
//		return this.copyNum;
//	}
	
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public void assignCopyNum(int copyNum) {
//		if(copyNum<0) {
//			throw new IllegalArgumentException("copy number cannot be negative!");
//		}
//		
//		if(this.copyNum==null) {
//			this.copyNum = copyNum;
//			this.vscopyIndexMap=new HashMap<>();
//		}else {
//			//copy number changes from 0 to positive
//			if(this.copyNum==0 && copyNum>0) {
//				this.getDependingVCDNodeSet().forEach(v->{
//					v.assignCopyNum(0);
//				});
//				
//			}else {//from positive to positive or stay the same
//				
//			}
//			
//			this.vscopyIndexMap.clear();
//		}
//		
//		this.copyNum = copyNum;
//		
//		//update the vscopyIndexMap;
//		if(this.copyNum>0) {
//			for(int i=1;i<=this.copyNum;i++)
//				this.vscopyIndexMap.put(i, new VSCopy(this,i));
//		}
//	}
	


	@Override
	public Map<Integer, VSCopy> getVSCopyIndexMap() {
		return this.vscopyIndexMap;
	}
	////////////////////////////////////
	@Override
	public Set<MetadataID> getAssignedMetadataIDSet() {
		return Collections.unmodifiableSet(this.assignedMetadataIDSet);
	}

	@Override
	public void assignMetadataID(MetadataID id) {
		this.assignedMetadataIDSet.add(id);
	}

	@Override
	public Set<OperationID> getAssignedOperationIDSet() {
		return Collections.unmodifiableSet(this.assignedOperationIDSet);
	}

	@Override
	public void assignOperationID(OperationID id) {
		this.assignedOperationIDSet.add(id);
	}

	@Override
	public Set<CompositionFunctionGroupID> getAssignedCFGIDSet() {
		return Collections.unmodifiableSet(this.assignedCFGIDSet);
	}

	@Override
	public void assignCompositionFunctionGroupID(CompositionFunctionGroupID id) {
		this.assignedCFGIDSet.add(id);
	}

	@Override
	public Set<CompositionFunctionID> getAssignedCFIDSet() {
		return Collections.unmodifiableSet(this.assignedCFIDSet);
	}

	@Override
	public void assignCompositionFunctionID(CompositionFunctionID id) {
		this.assignedCFIDSet.add(id);
	}


	
	///////////////////////////////
	//TODO
	//equals and hashcode methods only include the final fields??? 
	//non final fields (even though not transient) may change in the process of building the VCDGraph during which the equals method may be used???
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((component == null) ? 0 : component.hashCode());
		result = prime * result + precedenceIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VCDNodeImpl))
			return false;
		VCDNodeImpl other = (VCDNodeImpl) obj;
		if (component == null) {
			if (other.component != null)
				return false;
		} else if (!component.equals(other.component))
			return false;
		if (precedenceIndex != other.precedenceIndex)
			return false;
		return true;
	}

	
	
	@Override
	public String toString() {
		return "VCDNodeImpl [precedenceIndex=" + precedenceIndex + "]";
	}
	
	
	
	///////////////////
	
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((assignedCFGIDSet == null) ? 0 : assignedCFGIDSet.hashCode());
//		result = prime * result + ((assignedCFIDSet == null) ? 0 : assignedCFIDSet.hashCode());
//		result = prime * result + ((assignedMetadataIDSet == null) ? 0 : assignedMetadataIDSet.hashCode());
//		result = prime * result + ((assignedOperationIDSet == null) ? 0 : assignedOperationIDSet.hashCode());
//		result = prime * result + ((component == null) ? 0 : component.hashCode());
//		result = prime * result + ((copyNum == null) ? 0 : copyNum.hashCode());
//		result = prime * result + ((dependedVCDNodeSet == null) ? 0 : dependedVCDNodeSet.hashCode());
//		result = prime * result + ((dependingVCDNodeSet == null) ? 0 : dependingVCDNodeSet.hashCode());
//		result = prime * result + precedenceIndex;
//		result = prime * result + ((vscopyIndexMap == null) ? 0 : vscopyIndexMap.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!(obj instanceof VCDNodeImpl))
//			return false;
//		VCDNodeImpl other = (VCDNodeImpl) obj;
//		if (assignedCFGIDSet == null) {
//			if (other.assignedCFGIDSet != null)
//				return false;
//		} else if (!assignedCFGIDSet.equals(other.assignedCFGIDSet))
//			return false;
//		if (assignedCFIDSet == null) {
//			if (other.assignedCFIDSet != null)
//				return false;
//		} else if (!assignedCFIDSet.equals(other.assignedCFIDSet))
//			return false;
//		if (assignedMetadataIDSet == null) {
//			if (other.assignedMetadataIDSet != null)
//				return false;
//		} else if (!assignedMetadataIDSet.equals(other.assignedMetadataIDSet))
//			return false;
//		if (assignedOperationIDSet == null) {
//			if (other.assignedOperationIDSet != null)
//				return false;
//		} else if (!assignedOperationIDSet.equals(other.assignedOperationIDSet))
//			return false;
//		if (component == null) {
//			if (other.component != null)
//				return false;
//		} else if (!component.equals(other.component))
//			return false;
//		if (copyNum == null) {
//			if (other.copyNum != null)
//				return false;
//		} else if (!copyNum.equals(other.copyNum))
//			return false;
//		if (dependedVCDNodeSet == null) {
//			if (other.dependedVCDNodeSet != null)
//				return false;
//		} else if (!dependedVCDNodeSet.equals(other.dependedVCDNodeSet))
//			return false;
//		if (dependingVCDNodeSet == null) {
//			if (other.dependingVCDNodeSet != null)
//				return false;
//		} else if (!dependingVCDNodeSet.equals(other.dependingVCDNodeSet))
//			return false;
//		if (precedenceIndex != other.precedenceIndex)
//			return false;
//		if (vscopyIndexMap == null) {
//			if (other.vscopyIndexMap != null)
//				return false;
//		} else if (!vscopyIndexMap.equals(other.vscopyIndexMap))
//			return false;
//		return true;
//	}

}
