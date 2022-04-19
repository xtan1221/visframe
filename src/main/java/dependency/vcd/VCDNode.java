package dependency.vcd;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import context.scheme.VSComponent;
import dependency.vccl.VSCopy;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;
import metadata.MetadataID;
import operation.OperationID;


/**
 * interface for a VCDNode on a VCD graph
 * 
 * also contains the assigned visframe entities to this VCDNode;
 * 		see {@link VCDGraphBuilder#buildUnderlyingGraph()} for details about how visframe entities are assigned;
 * 
 * 
 * assignment of visframe entities to each VCDNode/VSComponent
 * 		Metadata
 *			If resulted from operation, assigned to the same VSComponent as the operation
 *			If component of a composite Metadata, assigned to the same VSComponent as the composite Metadata;
 *			Otherwise, assigned to the first VSComponent on whose DOS graph the Metadata is present;
 *		Operation
 *			Assigned to the first VSComponent on whose DOS graph the operation is present;
 *		CFG
 *			Assigned to the first VSComponent on whose CFD graph one or more CF of the CFG is present;
 *		CF
 *			Assign every CF to the same VSComponent as the host CFG;
 *
 * @author tanxu
 *
 */
public interface VCDNode extends Serializable{
	/**
	 * return the contained VSComponent;
	 * @return
	 */
	VSComponent getVSComponent();
	
	/**
	 * return the index of the contained VSComponent in the precedence list;
	 * first one's index is 0;
	 * @return
	 */
	int getPrecedenceIndex();
	
	Set<VCDNodeImpl> getDependedVCDNodeSet();
	
	Set<VCDNodeImpl> getDependingVCDNodeSet();
	
	/**
	 * set the set of depended VCDNode of this one;
	 * must be set after the underlying SimpleDirectedGraph of the VCD graph is built;
	 * @param dependedVCDNodeSet
	 */
	void setDependedVCDNodeSet(Set<VCDNodeImpl> dependedVCDNodeSet);
	/**
	 * set the set of depending VCDNode of this one;
	 * must be set after the underlying SimpleDirectedGraph of the VCD graph is built;
	 * @param dependedVCDNodeSet
	 */
	void setDependingVCDNodeSet(Set<VCDNodeImpl> dependingVCDNodeSet);
	
	
//	int getAssignedCopyNum();
	
//	/**
//	 * assign the give copy number to this VCDNode;
//	 * 
//	 * if the copy number is changed from positive to 0, also invoke the {@link #assignCopyNum(int)} method of all depending VCDNodes with copy number 0!
//	 * 
//	 * also update the VSCopy index map;
//	 */
//	void assignCopyNum(int copyNum);
	
	
//	/**
//	 * return whether all depended VCDNode of this one has been assigned a positive copy number;
//	 * @return
//	 */
//	default boolean allDependedNodeAssignedPositiveCopyNum() {
//		for(VCDNode node:this.getDependedVCDNodeSet()) {
//			if(node.getAssignedCopyNum()==0)
//				return false;
//		}
//		
//		return true;
//	}
	
	/**
	 * return the map from index to each VSCopy
	 * @return
	 */
	Map<Integer, VSCopy> getVSCopyIndexMap();
	
	
	/**
	 * return the assigned MetadataIDs to this VCDNode;
	 * 
	 * @return
	 */
	Set<MetadataID> getAssignedMetadataIDSet();
	/**
	 * assign the given MetadataID to this VCDNode
	 * @param id
	 */
	void assignMetadataID(MetadataID id);
	
	/**
	 * return the assigned OperationIDs to this VCDNode
	 * @return
	 */
	Set<OperationID> getAssignedOperationIDSet();
	/**
	 * assign the given OperationID to this VCDNode
	 * @param id
	 */
	void assignOperationID(OperationID id);
	
	/**
	 * return the assigned CompositionFunctionGroupID to this VCDNode
	 * @return
	 */
	Set<CompositionFunctionGroupID> getAssignedCFGIDSet();
	
	/**
	 * assign the given CompositionFunctionGroupID to this VCDNode
	 * @param id
	 */
	void assignCompositionFunctionGroupID(CompositionFunctionGroupID id);
	
	/**
	 * return the assigned CompositionFunctionID to this VCDNode
	 * @return
	 */
	Set<CompositionFunctionID> getAssignedCFIDSet();
	
	/**
	 * assign the given CompositionFunctionID to this VCDNode
	 * @param id
	 */
	void assignCompositionFunctionID(CompositionFunctionID id);
	
	////////////////////////////
//	/**
//	 * create a deep clone of this node
//	 * @return
//	 */
//	VCDNode deepClone();
}
