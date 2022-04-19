package dependency.vccl;

import java.io.Serializable;

public class VCCLEdge implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8509822812182478308L;
	
	////////////////
	private final VSCopy dependingCopy;
	private final VSCopy dependedCopy;
	
	/**
	 * 
	 * @param dependingCopy
	 * @param dependedCopy
	 */
	VCCLEdge(VSCopy dependingCopy, VSCopy dependedCopy){
		this.dependingCopy = dependingCopy;
		this.dependedCopy = dependedCopy;
	}

	/**
	 * @return the source
	 */
	public VSCopy getDependingCopy() {
		return dependingCopy;
	}

	/**
	 * @return the sink
	 */
	public VSCopy getDependedCopy() {
		return dependedCopy;
	}

	
	////////////////////////////
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
		if (!(obj instanceof VCCLEdge))
			return false;
		VCCLEdge other = (VCCLEdge) obj;
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
