package metadata;

import basic.VfNotes;
import operation.OperationID;

/**
 * 
 * @author tanxu
 *
 */
public abstract class AbstractMetadata implements Metadata {
	/**
	 * 
	 */
	private static final long serialVersionUID = 840036960496976217L;
	
	
	/////////////////
	private final MetadataName name;
	private final VfNotes notes;
	private final SourceType sourceType;
	private final MetadataID sourceCompositeDataMetadataID;
	private final OperationID sourceOperationID;
	
	/**
	 * constructor
	 * @param name cannot be null
	 * @param notes cannot be null
	 * @param sourceType cannot be null
	 * @param sourceCompositeDataMetadataID null if ...
	 * @param sourceOperationID null if ...
	 */
	public AbstractMetadata(
			MetadataName name, VfNotes notes,
			SourceType sourceType,
			MetadataID sourceCompositeDataMetadataID,
			OperationID sourceOperationID
			){
		//TODO validations
		
		
		
		this.name = name;
		this.notes = notes;
		this.sourceType = sourceType;
		this.sourceCompositeDataMetadataID = sourceCompositeDataMetadataID;
		this.sourceOperationID = sourceOperationID;
	}
	
	
	/**
	 * returns name of this AbstractMetadata
	 */
	@Override
	public MetadataName getName() {
		return this.name;
	}
	
	/**
	 * returns notes of this AbstractMetadata
	 */
	@Override
	public VfNotes getNotes() {
		return this.notes;
	}
	
	/**
	 * returns SourceType of this AbstractMetadata; cannot be null;
	 */
	@Override
	public SourceType getSourceType() {
		return this.sourceType;
	}
	
	/**
	 * returns MetadataID of the source CompositeDataMetadata of this AbstractMetadata if the sourceType is {@link SourceType#STRUCTURAL_COMPONENT};
	 * returns null otherwise;
	 * @return
	 */
	@Override
	public MetadataID getSourceCompositeDataMetadataID() {
		return this.sourceCompositeDataMetadataID;
	}
	
	/**
	 * returns the OperationID of the Operation that produced this AbstractMetadata if the sourceType is {@link SourceType#RESULT_FROM_OPERATION};
	 * returns null otherwise;
	 * @return
	 */
	@Override
	public OperationID getSourceOperationID() {
		return this.sourceOperationID;
	}

	///////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result
				+ ((sourceCompositeDataMetadataID == null) ? 0 : sourceCompositeDataMetadataID.hashCode());
		result = prime * result + ((sourceOperationID == null) ? 0 : sourceOperationID.hashCode());
		result = prime * result + ((sourceType == null) ? 0 : sourceType.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AbstractMetadata))
			return false;
		AbstractMetadata other = (AbstractMetadata) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		if (sourceCompositeDataMetadataID == null) {
			if (other.sourceCompositeDataMetadataID != null)
				return false;
		} else if (!sourceCompositeDataMetadataID.equals(other.sourceCompositeDataMetadataID))
			return false;
		if (sourceOperationID == null) {
			if (other.sourceOperationID != null)
				return false;
		} else if (!sourceOperationID.equals(other.sourceOperationID))
			return false;
		if (sourceType != other.sourceType)
			return false;
		return true;
	}
	
	
	
}
