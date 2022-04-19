package function.group;

import java.sql.SQLException;

import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.DataType;
import metadata.MetadataID;

public abstract class AbstractCompositionFunctionGroup implements CompositionFunctionGroup {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4664640774833760567L;
	
	//////////////////////////
	private final CompositionFunctionGroupName name;
	private final VfNotes notes;
	private final MetadataID ownerRecordDataMetadataID;
	
	/**
	 * constructor
	 * @param name not null
	 * @param notes not null
	 * @param ownerRecordDataMetadataID not null
	 */
	protected AbstractCompositionFunctionGroup(
			CompositionFunctionGroupName name, VfNotes notes,
			MetadataID ownerRecordDataMetadataID){
		//validations
		if(!ownerRecordDataMetadataID.getDataType().equals(DataType.RECORD)) {
			throw new IllegalArgumentException("given ownerRecordDataMetadataID is not of RECORD data type!");
		}
		
		this.name = name;
		this.notes = notes;
		this.ownerRecordDataMetadataID = ownerRecordDataMetadataID;
	}
	
	
	@Override
	public VfNotes getNotes() {
		return this.notes;
	}

	@Override
	public CompositionFunctionGroupName getName() {
		return this.name;
	}
	
	
	@Override
	public MetadataID getOwnerRecordDataMetadataID() {
		return this.ownerRecordDataMetadataID;
	}
	
	
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced CFG will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this CFG is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public abstract AbstractCompositionFunctionGroup reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;


	//////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + ((ownerRecordDataMetadataID == null) ? 0 : ownerRecordDataMetadataID.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AbstractCompositionFunctionGroup))
			return false;
		AbstractCompositionFunctionGroup other = (AbstractCompositionFunctionGroup) obj;
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
		if (ownerRecordDataMetadataID == null) {
			if (other.ownerRecordDataMetadataID != null)
				return false;
		} else if (!ownerRecordDataMetadataID.equals(other.ownerRecordDataMetadataID))
			return false;
		return true;
	}

}
