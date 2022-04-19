package dependency.cfd;

import function.composition.CompositionFunctionID;

public class CFDNodeImpl implements CFDNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7433455040185133782L;
	
	
	///////////////////////
	private final CompositionFunctionID CFID;
	
	/**
	 * constructor
	 * @param CFID
	 */
	public CFDNodeImpl(CompositionFunctionID CFID){
		if(CFID == null) {
			throw new IllegalArgumentException("given CFID cannot be null!");
		}
		
		this.CFID = CFID;
	}
	
	
	@Override
	public CompositionFunctionID getCFID() {
		return CFID;
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((CFID == null) ? 0 : CFID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CFDNodeImpl))
			return false;
		CFDNodeImpl other = (CFDNodeImpl) obj;
		if (CFID == null) {
			if (other.CFID != null)
				return false;
		} else if (!CFID.equals(other.CFID))
			return false;
		return true;
	}
	
	
}
