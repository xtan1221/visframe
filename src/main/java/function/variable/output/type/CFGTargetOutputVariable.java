package function.variable.output.type;

import java.sql.SQLException;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.target.CFGTarget;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;


/**
 * ValueTableColumnOutputVariable whose value is assigned to a target of the host CompositionFunction and also can be used by downstream {@link UpstreamValueTableColumnOutputVariableInputVariable};
 * 
 * 
 * @author tanxu
 */
public class CFGTargetOutputVariable extends ValueTableColumnOutputVariable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5112566086093977220L;
	
	////////////
	private final CFGTarget<?> target;
	
	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param aliasName
	 * @param notes
	 * @param SQLDataType
	 * @param hostEvaluatorIndexID
	 * @param targetName
	 */
	public CFGTargetOutputVariable(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID,
			int hostEvaluatorIndexID,
			SimpleName aliasName, VfNotes notes,
			
			CFGTarget<?> target
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, hostEvaluatorIndexID, aliasName, notes);
		// TODO Auto-generated constructor stub
		
		
		this.target = target;
	}

	/**
	 * @return the target
	 */
	public CFGTarget<?> getTarget() {
		return target;
	}

	public SimpleName getTargetName() {
		return this.target.getName();
	}
	
	////////////////////////
	@Override
	public VfDefinedPrimitiveSQLDataType getSQLDataType() {
		return target.getSQLDataType();
	}
	/**
	 * reproduce and returns a new CFGTargetOutputVariable of this one;
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException 
	 */
	@Override
	public CFGTargetOutputVariable reproduce(
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
		CFGTarget<?> target = this.getTarget().reproduce();
		
		return new CFGTargetOutputVariable(
//				reproducedOwnerRecordDataMetadataID,
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedHostEvaluatorIndexID,
				reproducedAliasName,
				reproducedNotes,
				target
				);
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof CFGTargetOutputVariable))
			return false;
		CFGTargetOutputVariable other = (CFGTargetOutputVariable) obj;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	
	
}
