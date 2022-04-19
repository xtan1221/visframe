package dependency.vccl.utils;

import java.util.Map;

import basic.VfNotes;

public class NodeCopy<V> {
	private final V node;
	private final int copyIndex;
	private final VfNotes notes;
	
	//////////////////////
	private Map<V, NodeCopy<V>> dependedNodeLinkedNodeCopyMap;
	
	/**
	 * 
	 * @param node
	 * @param copyIndex
	 */
	public NodeCopy(V node, int copyIndex, VfNotes notes){
		this.node = node;
		this.copyIndex = copyIndex;
		this.notes = notes;
	}
	
	
	/**
	 * @return the node
	 */
	public V getNode() {
		return node;
	}
	
	
	/**
	 * @return the copyIndex
	 */
	public int getCopyIndex() {
		return copyIndex;
	}

	
	/**
	 * @param dependedNodeLinkedNodeCopyMap the dependedNodeLinkedNodeCopyMap to set
	 */
	public void setDependedNodeLinkedNodeCopyMap(Map<V, NodeCopy<V>> dependedNodeLinkedNodeCopyMap) {
		this.dependedNodeLinkedNodeCopyMap = dependedNodeLinkedNodeCopyMap;
	}
	
	/**
	 * @return the dependedNodeLinkedCopyIndexMap
	 */
	public Map<V, NodeCopy<V>> getDependedNodeLinkedNodeCopyMap() {
		return dependedNodeLinkedNodeCopyMap;
	}


	/**
	 * @return the notes
	 */
	public VfNotes getNotes() {
		return notes;
	}
//
//	/**
//	 * @param notes the notes to set
//	 */
//	public void setNotes(VfNotes notes) {
//		this.notes = notes;
//	}
	
	
	
	@Override
	public String toString() {
		return "NodeCopy [node=" + node + ", copyIndex=" + copyIndex + ", notes=" + notes + "]";
	}

	

	//////////////////////////////////////////
	//only include the final fields!!!
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + copyIndex;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof NodeCopy))
			return false;
		@SuppressWarnings("unchecked")
		NodeCopy<V> other = (NodeCopy<V>) obj;
		if (copyIndex != other.copyIndex)
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		return true;
	}
	
}
