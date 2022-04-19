package function.variable.input.recordwise.type;

import java.sql.SQLException;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.variable.input.recordwise.RecordwiseInputVariable;
import function.variable.output.type.ValueTableColumnOutputVariable;
import metadata.MetadataID;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;


/**
 * RecordwiseInputVariable containing a {@link ValueTableColumnOutputVariable} calculated by an upstream ComponentFunction of the host ComponentFunction of the host Evaluator of this RecordwiseInputVariable
 * 
 * @author tanxu
 *
 */
public class UpstreamValueTableColumnOutputVariableInputVariable extends RecordwiseInputVariable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -461285400417823694L;
	
	
	//////////////////////////////
	private final ValueTableColumnOutputVariable upstreamValueTableColumnOutputVariable;
	
	/**
	 * constructor
	 * 
	 * note that target record data is always the same with the owner record data for UpstreamValueTableColumnOutputVariableInputVariable
	 * 
	 * @param ownerRecordDataMetadataID
	 * @param hostCompositionFunctionID
	 * @param hostComponentFunctionIndexID
	 * @param evaluatorIndexID
	 * @param aliasName
	 * @param notes
	 * @param targetRecordDataMetadataID always the same with the owner record data for UpstreamValueTableColumnOutputVariableInputVariable
	 * @param upstreamValueTableColumnOutputVariable
	 */
	public UpstreamValueTableColumnOutputVariableInputVariable(
			CompositionFunctionID hostCompositionFunctionID, 
			int hostComponentFunctionIndexID,
			int evaluatorIndexID,
			SimpleName aliasName, VfNotes notes,
			MetadataID targetRecordDataMetadataID,
			
			ValueTableColumnOutputVariable upstreamValueTableColumnOutputVariable
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, evaluatorIndexID, aliasName, notes, targetRecordDataMetadataID);
		// TODO Auto-generated constructor stub
		
		
		
		this.upstreamValueTableColumnOutputVariable = upstreamValueTableColumnOutputVariable;
	}



	public ValueTableColumnOutputVariable getUpstreamValueTableColumnOutputVariable() {
		return upstreamValueTableColumnOutputVariable;
	}

	
	////////////////////////////////////////////
	@Override
	public VfDefinedPrimitiveSQLDataType getSQLDataType() {
		return upstreamValueTableColumnOutputVariable.getSQLDataType();
	}
	
	/**
	 * reproduce and return a new UpstreamOutputVariableInputVariable of this one;
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public UpstreamValueTableColumnOutputVariableInputVariable reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		//find out the copy index of the VCDNode to which the owner record Metadata is assigned
		//note that the TargetRecordDataMetadataID is the same with the owner record Metadata
		int copyIndexOfOwnerRecordMetadata = 
				VSAArchiveReproducerAndInserter.getAppliedArchive().lookupCopyIndexOfOwnerRecordMetadata(
						this.getHostCompositionFunctionID().getHostCompositionFunctionGroupID(), copyIndex);
		
		MetadataID reproducedTargetRecordDataMetadataID = 
				this.getTargetRecordDataMetadataID().reproduce(
						hostVisProjctDBContext, 
						VSAArchiveReproducerAndInserter,
						copyIndexOfOwnerRecordMetadata);//find out the copy index of owner record data
		
		//
		CompositionFunctionID reproducedHostCompositionFunctionID =
				this.getHostCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		
		int reproducedHostComponentFunctionIndexID = this.getHostComponentFunctionIndexID();
		int reproducedHostEvaluatorIndexID = this.getHostEvaluatorIndexID();
		SimpleName reproducedAliasName = this.getAliasName().reproduce();
		VfNotes reproducedNotes = this.getNotes().reproduce();
		
		
		//the upstreamValueTableColumnOutputVariable should belong to the owner cf of this UpstreamValueTableColumnOutputVariableInputVariable, thus use the copy index of the owner cf of this UpstreamValueTableColumnOutputVariableInputVariable
		ValueTableColumnOutputVariable upstreamValueTableColumnOutputVariable = 
				this.getUpstreamValueTableColumnOutputVariable().reproduce(
						hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex); 
		
		
		return new UpstreamValueTableColumnOutputVariableInputVariable(
//				reproducedOwnerRecordDataMetadataID,
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedHostEvaluatorIndexID,
				reproducedAliasName,
				reproducedNotes,
				reproducedTargetRecordDataMetadataID,
				upstreamValueTableColumnOutputVariable
				);
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((upstreamValueTableColumnOutputVariable == null) ? 0
				: upstreamValueTableColumnOutputVariable.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof UpstreamValueTableColumnOutputVariableInputVariable))
			return false;
		UpstreamValueTableColumnOutputVariableInputVariable other = (UpstreamValueTableColumnOutputVariableInputVariable) obj;
		if (upstreamValueTableColumnOutputVariable == null) {
			if (other.upstreamValueTableColumnOutputVariable != null)
				return false;
		} else if (!upstreamValueTableColumnOutputVariable.equals(other.upstreamValueTableColumnOutputVariable))
			return false;
		return true;
	}



}
