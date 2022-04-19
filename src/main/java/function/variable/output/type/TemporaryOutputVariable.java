package function.variable.output.type;

import java.sql.SQLException;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;

/**
 * ValueTableColumnOutputVariable whose value is not assigned to any target of the host CompositionFunction, but only be used by downstream {@link UpstreamValueTableColumnOutputVariableInputVariable}
 * @author tanxu
 *
 */
public class TemporaryOutputVariable extends ValueTableColumnOutputVariable implements Comparable<TemporaryOutputVariable>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5212630472743821458L;

	/////////////////////////
	private final VfDefinedPrimitiveSQLDataType SQLDataType;
	
	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param aliasName
	 * @param notes
	 * @param SQLDataType
	 * @param hostEvaluatorIndexID
	 */
	public TemporaryOutputVariable(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID,
			int hostEvaluatorIndexID,
			SimpleName aliasName, VfNotes notes, 
			
			VfDefinedPrimitiveSQLDataType SQLDataType) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, hostEvaluatorIndexID, aliasName, notes);
		// TODO Auto-generated constructor stub
		
		this.SQLDataType = SQLDataType;
	}
	
	
	@Override
	public VfDefinedPrimitiveSQLDataType getSQLDataType() {
		return SQLDataType;
	}
	
	@Override
	public int compareTo(TemporaryOutputVariable tov) {
		// TODO Auto-generated method stub
		if(this.getHostComponentFunctionIndexID()==tov.getHostComponentFunctionIndexID()) {
			if(this.getHostEvaluatorIndexID() == tov.getHostEvaluatorIndexID()) {
				return this.getAliasName().compareTo(tov.getAliasName());
			}else {
				return this.getHostEvaluatorIndexID()-tov.getHostEvaluatorIndexID();
			}
		}else {
			//??TODO
			return ((Integer)this.getHostComponentFunctionIndexID()).compareTo((Integer)tov.getHostComponentFunctionIndexID());
		}
	}

	
	
	
	/////////////////////////////
	/**
	 * reproduce and returns a new TemporaryOutputVariable of this one;
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public TemporaryOutputVariable reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
//		//find out the copy index of the VCDNode to which the owner record Metadata is assigned
//		int copyIndexOfOwnerRecordMetadata = 
//				VSAArchiveReproducerAndInserter.getApplierArchive().lookupCopyIndexOfOwnerRecordMetadata(
//						this.getHostCompositionFunctionID().getHostCompositionFunctionGroupID(), copyIndex);
//		
//		MetadataID reproducedOwnerRecordDataMetadataID = 
//				this.getOwnerRecordDataMetadataID().reproduce(
//						hostVisProjctDBContext, 
//						VSAArchiveReproducerAndInserter,
//						copyIndexOfOwnerRecordMetadata);//find out the copy index of owner record data
		
		//
		CompositionFunctionID reproducedHostCompositionFunctionID =
				this.getHostCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		
		int reproducedHostComponentFunctionIndexID = this.getHostComponentFunctionIndexID();
		int reproducedHostEvaluatorIndexID = this.getHostEvaluatorIndexID();
		SimpleName reproducedAliasName = this.getAliasName().reproduce();
		VfNotes reproducedNotes = this.getNotes().reproduce();
		
		VfDefinedPrimitiveSQLDataType SQLDataType = this.getSQLDataType().reproduce();
		
		
		//
		return new TemporaryOutputVariable(
//				reproducedOwnerRecordDataMetadataID,
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedHostEvaluatorIndexID,
				reproducedAliasName,
				reproducedNotes,
				SQLDataType
				);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((SQLDataType == null) ? 0 : SQLDataType.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof TemporaryOutputVariable))
			return false;
		TemporaryOutputVariable other = (TemporaryOutputVariable) obj;
		if (SQLDataType == null) {
			if (other.SQLDataType != null)
				return false;
		} else if (!SQLDataType.equals(other.SQLDataType))
			return false;
		return true;
	}

	///////////////////////////
	
}
