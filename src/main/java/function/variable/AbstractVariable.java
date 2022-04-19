package function.variable;

import java.sql.SQLException;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;

public abstract class AbstractVariable implements Variable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2274422655977175693L;
	
	///////////////////////////
//	/**
//	 * owner record MetadataID of the host CFG of the owner cf of this AbstractVariable;
//	 * facilitate reproducing
//	 */
//	private final MetadataID ownerRecordDataMetadataID;
	
	private final CompositionFunctionID hostCompositionFunctionID;
	/////////////////////
	/**
	 * must be set before the host CompositionFunction is created and inserted into the host VisProjectDBContext;
	 */
	private final int hostComponentFunctionIndexID;
	
	private final int hostEvaluatorIndexID;
	
	private final SimpleName aliasName;
	
	private final VfNotes notes;
	
	/**
	 * constructor
	 * @param aliasName
	 * @param notes
	 * @param SQLDataType
	 * @param ownerCompositionFunctionID
	 */
	protected AbstractVariable(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID,
			int hostEvaluatorIndexID,
			SimpleName aliasName, VfNotes notes){
		
//		this.ownerRecordDataMetadataID = ownerRecordDataMetadataID;
		this.aliasName = aliasName;
		this.notes = notes;
		this.hostCompositionFunctionID = hostCompositionFunctionID;
		this.hostComponentFunctionIndexID = hostComponentFunctionIndexID;
		this.hostEvaluatorIndexID = hostEvaluatorIndexID;
	}
	
//	
//	@Override
//	public MetadataID getOwnerRecordDataMetadataID() {
//		return this.ownerRecordDataMetadataID;
//	}
	
	@Override
	public CompositionFunctionID getHostCompositionFunctionID() {
		return this.hostCompositionFunctionID;
	}
	
	@Override
	public int getHostComponentFunctionIndexID() {
		return hostComponentFunctionIndexID;
	}
	
	@Override
	public int getHostEvaluatorIndexID() {
		return hostEvaluatorIndexID;
	}
	
	@Override
	public SimpleName getAliasName() {
		return this.aliasName;
	}
	
	@Override
	public VfNotes getNotes() {
		return notes;
	}

	/**
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public abstract AbstractVariable reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;
	
	////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aliasName == null) ? 0 : aliasName.hashCode());
		result = prime * result + hostComponentFunctionIndexID;
		result = prime * result + ((hostCompositionFunctionID == null) ? 0 : hostCompositionFunctionID.hashCode());
		result = prime * result + hostEvaluatorIndexID;
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
//		result = prime * result + ((ownerRecordDataMetadataID == null) ? 0 : ownerRecordDataMetadataID.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AbstractVariable))
			return false;
		AbstractVariable other = (AbstractVariable) obj;
		if (aliasName == null) {
			if (other.aliasName != null)
				return false;
		} else if (!aliasName.equals(other.aliasName))
			return false;
		if (hostComponentFunctionIndexID != other.hostComponentFunctionIndexID)
			return false;
		if (hostCompositionFunctionID == null) {
			if (other.hostCompositionFunctionID != null)
				return false;
		} else if (!hostCompositionFunctionID.equals(other.hostCompositionFunctionID))
			return false;
		if (hostEvaluatorIndexID != other.hostEvaluatorIndexID)
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
//		if (ownerRecordDataMetadataID == null) {
//			if (other.ownerRecordDataMetadataID != null)
//				return false;
//		} else if (!ownerRecordDataMetadataID.equals(other.ownerRecordDataMetadataID))
//			return false;
		return true;
	}

	

	
	/////////////////////////////////
	
	
	
}
