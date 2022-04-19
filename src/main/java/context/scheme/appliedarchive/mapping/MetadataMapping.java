package context.scheme.appliedarchive.mapping;

import java.io.Serializable;
import java.util.function.Predicate;

import metadata.DataType;
import metadata.MetadataID;

/**
 * base class for metadata mapping of selected solution metadata set in the VisScheme applying;
 * 
 * @author tanxu
 *
 */
public abstract class MetadataMapping implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2631727732685061804L;

	/////////////////////////////////
	/**
	 * the MetadataID of the Metadata contained by the IntegratedDOSNode;
	 */
	private final MetadataID targetMetadataID;
	
	/**
	 * the MetadataID of the Metadata from host VisProjectDBContext selected to be mapped to the {@link #targetMetadataID}
	 */
	private final MetadataID sourceMetadataID;
	
	/**
	 * constructor
	 * @param targetMetadataID
	 * @param sourceMetadataID
	 */
	protected MetadataMapping(
			MetadataID targetMetadataID, MetadataID sourceMetadataID
			){
		//validations
		if(targetMetadataID==null)
			throw new IllegalArgumentException("given targetMetadataID cannot be null!");
		if(sourceMetadataID==null)
			throw new IllegalArgumentException("given sourceMetadataID cannot be null!");
		if(!this.getTargetMetadataTypePredicate().test(targetMetadataID.getDataType()))
			throw new IllegalArgumentException("given targetMetadataID's data type is not valid!");
		if(!this.getSourceMetadataTypePredicate().test(sourceMetadataID.getDataType()))
			throw new IllegalArgumentException("given sourceMetadataID's data type is not valid!");
		
		
		this.targetMetadataID = targetMetadataID;
		this.sourceMetadataID = sourceMetadataID;
	}

	
	public MetadataID getSourceMetadataID() {
		return this.sourceMetadataID;
	}
	
	public MetadataID getTargetMetadataID() {
		return this.targetMetadataID;
	}
	
	
	////////////////////////////
	/**
	 * return the predicate that checks whether the data type of the {@link #targetMetadataID} is valid or not
	 * @return the targetMetadataTypePredicate
	 */
	protected abstract Predicate<DataType> getTargetMetadataTypePredicate();

	/**
	 * return the predicate that checks whether the data type of the {@link #sourceMetadataID} is valid or not
	 * @return the sourceMetadataTypePredicate
	 */
	protected abstract Predicate<DataType> getSourceMetadataTypePredicate();


	//////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sourceMetadataID == null) ? 0 : sourceMetadataID.hashCode());
		result = prime * result + ((targetMetadataID == null) ? 0 : targetMetadataID.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MetadataMapping))
			return false;
		MetadataMapping other = (MetadataMapping) obj;
		if (sourceMetadataID == null) {
			if (other.sourceMetadataID != null)
				return false;
		} else if (!sourceMetadataID.equals(other.sourceMetadataID))
			return false;
		if (targetMetadataID == null) {
			if (other.targetMetadataID != null)
				return false;
		} else if (!targetMetadataID.equals(other.targetMetadataID))
			return false;
		return true;
	}

	
	
}
