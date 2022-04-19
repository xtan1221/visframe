package generic.tree.trim.helper;

import java.io.Serializable;

/**
 * 
 * a position on a tree;
 * 
 * not reproducible;
 * 
 * @author tanxu
 *
 */
public final class PositionOnTree implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4717822406448251562L;

	////////////////////////////////
	/**
	 * target node if {@link #parentNodeID} is null;
	 */
	private final int childNodeID; 
	
	/**
	 * if null, the position is exactly on the child node and {@link #pos} must be null as well;
	 */
	private final Integer parentNodeID;
	/**
	 * if not null, must be a value in (0,1) for a position between the {@link #childNodeID} and {@link #parentNodeID};
	 * 		the closer to 1, the closer to the child node;
	 * if null, the position is exactly on the child node and {@link #parentNodeID} must be null as well;
	 */
	private final Double pos; 
	
	/**
	 * whether the position is exactly on the root node of the owner tree;
	 */
	private final boolean onRootNode;
	
	/**
	 * constructor
	 * @param parentNodeID
	 * @param childNodeID
	 * @param newRootNodePos
	 * @param onRootNode
	 */
	public PositionOnTree(int childNodeID, Integer parentNodeID, Double pos, boolean onRootNode){
		if(parentNodeID!=null) {
			if(pos==null||pos>=1 || pos<=0) {
				throw new IllegalArgumentException("given pos must be non-null and in range (0,1) when parentNodeID is not null!");
			}
		}else {//parentNodeID is null
			if(pos!=null) {
				throw new IllegalArgumentException("given pos must be null when parentNodeID is null!");
			}
		}
		
		if(onRootNode) {
			if(parentNodeID!=null || pos!=null) {
				throw new IllegalArgumentException("given parentNodeID and pos must be null when onRootNode is true!");
			}
		}
		
		this.childNodeID = childNodeID;
		this.parentNodeID = parentNodeID;
		this.pos = pos;
		this.onRootNode = onRootNode;
	}
	
	/**
	 * whether the position is on an existing tree node or a position between two existing nodes;
	 * @return
	 */
	public boolean isOnExistingNode() {
		return this.parentNodeID==null;
	}

	/**
	 * @return the childNodeID
	 */
	public int getChildNodeID() {
		return childNodeID;
	}

	/**
	 * @return the parentNodeID
	 */
	public Integer getParentNodeID() {
		return parentNodeID;
	}

	/**
	 * if not null, must be a value in (0,1) for a position between the {@link #childNodeID} and {@link #parentNodeID};
	 * 		the closer to 1, the closer to the child node;
	 * if null, the position is exactly on the child node and {@link #parentNodeID} must be null as well;
	 * @return the pos
	 */
	public Double getPos() {
		return pos;
	}

	/**
	 * whether the position is exactly on the root node of the owner tree;
	 * @return
	 */
	public boolean isOnRootNode() {
		return onRootNode;
	}

	

	/////////////////////
	
	@Override
	public String toString() {
		return "PositionOnTree [childNodeID=" + childNodeID + ", parentNodeID=" + parentNodeID + ", pos=" + pos
				+ ", onRootNode=" + onRootNode + "]";
	}

	
	/////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + childNodeID;
		result = prime * result + (onRootNode ? 1231 : 1237);
		result = prime * result + ((parentNodeID == null) ? 0 : parentNodeID.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PositionOnTree))
			return false;
		PositionOnTree other = (PositionOnTree) obj;
		if (childNodeID != other.childNodeID)
			return false;
		if (onRootNode != other.onRootNode)
			return false;
		if (parentNodeID == null) {
			if (other.parentNodeID != null)
				return false;
		} else if (!parentNodeID.equals(other.parentNodeID))
			return false;
		if (pos == null) {
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		return true;
	}

	
	
}
