package function.variable.input.recordwise.type;

import java.sql.SQLException;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;
import function.target.CFGTarget;
import function.variable.input.recordwise.RecordwiseInputVariable;
import metadata.MetadataID;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;


/**
 * RecordwiseInputVariable containing a target of a CompositionFunctionGroup;
 * 
 * 
 * 
 * !!!!!cannot be a target of the host CompositionFunctionGroup of this variable that is assigned to the host {@link CompositionFunction};
 * 
 * 
 * also must be a target that has been assigned to an existing CompositionFunction in the host VisframeContext; - 101820
 * 
 * 
 * @author tanxu
 *
 */
public class CFGTargetInputVariable extends RecordwiseInputVariable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6638660448028723783L;
	
	////////////////////////////
	private final CompositionFunctionGroupID targetCompositionFunctionGroupID;
	
	private final CFGTarget<?> target;
	
	/**
	 * 
	 * @param ownerRecordDataMetadataID
	 * @param hostCompositionFunctionID
	 * @param hostComponentFunctionIndexID
	 * @param evaluatorIndexID
	 * @param aliasName
	 * @param notes
	 * @param targetRecordDataMetadataID the owner record data of the targetCompositionFunctionGroupID; not necessarily the same with ownerRecordDataMetadataID
	 * @param targetCompositionFunctionGroupID
	 * @param target cannot be a target of the host CompositionFunctionGroup of this variable that is assigned to the host {@link CompositionFunction};
	 */
	public CFGTargetInputVariable(
			CompositionFunctionID hostCompositionFunctionID, 
			int hostComponentFunctionIndexID,
			int evaluatorIndexID,
			SimpleName aliasName, VfNotes notes,
			
			MetadataID targetRecordDataMetadataID,
			CompositionFunctionGroupID targetCompositionFunctionGroupID,
			CFGTarget<?> target
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, evaluatorIndexID, aliasName, notes, targetRecordDataMetadataID);
		// TODO Auto-generated constructor stub
		
		
		this.targetCompositionFunctionGroupID = targetCompositionFunctionGroupID;
		this.target = target;
	}


	public CompositionFunctionGroupID getTargetCompositionFunctionGroupID() {
		return targetCompositionFunctionGroupID;
	}


	public CFGTarget<?> getTarget() {
		return target;
	}
	
	/////////////////////
	@Override
	public VfDefinedPrimitiveSQLDataType getSQLDataType() {
		return target.getSQLDataType();
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
	public CFGTargetInputVariable reproduce(
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
		
		//
		//first find out the copy index of the target record Metadata which is a depended record data of the owner cf of SQLAggregateFunctionBasedInputVariable
		int copyIndexOfTargetRecordMetadataID = 
				VSAArchiveReproducerAndInserter.getAppliedArchive().lookupDependedRecordMetadataCopyIndex(
						this.getHostCompositionFunctionID(), copyIndex, this.getTargetRecordDataMetadataID());
		
		MetadataID targetRecordMetadataID = 
				this.getTargetRecordDataMetadataID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndexOfTargetRecordMetadataID);
		
		//first find out the copy index of the targetCompositionFunctionGroupID, which should be the same with the assigned CF of the target of the targetCompositionFunctionGroupID;
		CompositionFunctionID targetAssignedCfID = 
				VSAArchiveReproducerAndInserter.getAppliedArchive().getCFGIDTargetAssignedCFIDMapMapInVisScheme().get(this.getTargetCompositionFunctionGroupID()).get(this.getTarget());
		int copyIndexOfTargetCompositionFunctionGroupID = 
				VSAArchiveReproducerAndInserter.getAppliedArchive().lookupDependedCFCopyIndex(
						this.getHostCompositionFunctionID(), copyIndex, targetAssignedCfID); //note that the copy index of CFG is the same with its CFs
		CompositionFunctionGroupID targetCompositionFunctionGroupID = 
				this.getTargetCompositionFunctionGroupID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndexOfTargetCompositionFunctionGroupID);
		
		//
		CFGTarget<?> target = this.getTarget().reproduce();
		
		
		return new CFGTargetInputVariable(
//				reproducedOwnerRecordDataMetadataID,
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedHostEvaluatorIndexID,
				reproducedAliasName,
				reproducedNotes,
				targetRecordMetadataID,
				targetCompositionFunctionGroupID,
				target
				);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result
				+ ((targetCompositionFunctionGroupID == null) ? 0 : targetCompositionFunctionGroupID.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof CFGTargetInputVariable))
			return false;
		CFGTargetInputVariable other = (CFGTargetInputVariable) obj;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (targetCompositionFunctionGroupID == null) {
			if (other.targetCompositionFunctionGroupID != null)
				return false;
		} else if (!targetCompositionFunctionGroupID.equals(other.targetCompositionFunctionGroupID))
			return false;
		return true;
	}

	//////////////////////////////////////

}
