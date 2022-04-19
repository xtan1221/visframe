package function.variable.input.recordwise;

import java.sql.SQLException;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.variable.input.InputVariable;
import metadata.MetadataID;

/**
 * InputVariable whose value is determined by each record to be calculated individually;
 * @author tanxu
 * 
 */
public abstract class RecordwiseInputVariable extends InputVariable implements Comparable<RecordwiseInputVariable> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5746456371476216174L;
	
	/////////////////////////////////
	/**
	 * the record data of the column used as variable here; not necessarily the same with ownerRecordDataMetadataID
	 */
	private final MetadataID targetRecordDataMetadataID;
	
	/**
	 * constructor
	 * @param ownerRecordDataMetadataID
	 * @param hostCompositionFunctionID
	 * @param aliasName
	 * @param notes
	 * @param SQLDataType
	 * @param evaluatorIndexID
	 * @param targetRecordDataMetadataID the record data of the column used as variable here; not necessarily the same with ownerRecordDataMetadataID
	 */
	protected RecordwiseInputVariable(
			CompositionFunctionID hostCompositionFunctionID, 
			int hostComponentFunctionIndexID,
			int evaluatorIndexID,
			SimpleName aliasName, VfNotes notes,
			
			MetadataID targetRecordDataMetadataID
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, evaluatorIndexID, aliasName, notes);
		// TODO Auto-generated constructor stub
		
		this.targetRecordDataMetadataID = targetRecordDataMetadataID;
	}
	
	
	public MetadataID getTargetRecordDataMetadataID() {
		return targetRecordDataMetadataID;
	}



	/**
	 * compareTo
	 */
	@Override
    public int compareTo(RecordwiseInputVariable riv) {
		
		return this.getAliasName().getStringValue().toUpperCase()
				.compareTo(riv.getAliasName().getStringValue().toUpperCase());
	}
	
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public abstract RecordwiseInputVariable reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;

	
	//////////////////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((targetRecordDataMetadataID == null) ? 0 : targetRecordDataMetadataID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof RecordwiseInputVariable))
			return false;
		RecordwiseInputVariable other = (RecordwiseInputVariable) obj;
		if (targetRecordDataMetadataID == null) {
			if (other.targetRecordDataMetadataID != null)
				return false;
		} else if (!targetRecordDataMetadataID.equals(other.targetRecordDataMetadataID))
			return false;
		return true;
	}

	
	//
	
	
}
