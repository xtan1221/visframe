package dependency.vccl.utils.helper;

import dependency.vccl.utils.NodeCopy;

public class CopyLink<V> {
	private final NodeCopy<V> dependingCopy;
	private final NodeCopy<V> dependedCopy;
	
	/**
	 * 
	 * @param dependingCopy
	 * @param dependedCopy
	 */
	public CopyLink(NodeCopy<V> dependingCopy, NodeCopy<V> dependedCopy) {
		this.dependingCopy = dependingCopy;
		this.dependedCopy = dependedCopy;
	}
	

	/**
	 * @return the dependingCopy
	 */
	public NodeCopy<V> getDependingCopy() {
		return dependingCopy;
	}

	/**
	 * @return the dependedCopy
	 */
	public NodeCopy<V> getDependedCopy() {
		return dependedCopy;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dependedCopy == null) ? 0 : dependedCopy.hashCode());
		result = prime * result + ((dependingCopy == null) ? 0 : dependingCopy.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CopyLink))
			return false;
		@SuppressWarnings("unchecked")
		CopyLink<V> other = (CopyLink<V>) obj;
		if (dependedCopy == null) {
			if (other.dependedCopy != null)
				return false;
		} else if (!dependedCopy.equals(other.dependedCopy))
			return false;
		if (dependingCopy == null) {
			if (other.dependingCopy != null)
				return false;
		} else if (!dependingCopy.equals(other.dependingCopy))
			return false;
		return true;
	}

	
	
}
