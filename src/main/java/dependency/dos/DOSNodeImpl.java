package dependency.dos;

import metadata.MetadataID;

/**
 * DOSNode implementation for a regular DOSGraph induced by a set of target Metadata from a VisframeContext
 * @author tanxu
 *
 */
public class DOSNodeImpl implements DOSNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1956422094949537090L;
	
	
	/////////////////////////
	private final MetadataID metadataID;
	
	/**
	 * 
	 * @param metadataID
	 */
	public DOSNodeImpl(MetadataID metadataID){
		
		this.metadataID = metadataID;
	}
	
	
	@Override
	public MetadataID getMetadataID() {
		return metadataID;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((metadataID == null) ? 0 : metadataID.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof DOSNodeImpl))
			return false;
		DOSNodeImpl other = (DOSNodeImpl) obj;
		if (metadataID == null) {
			if (other.metadataID != null)
				return false;
		} else if (!metadataID.equals(other.metadataID))
			return false;
		return true;
	}

}
