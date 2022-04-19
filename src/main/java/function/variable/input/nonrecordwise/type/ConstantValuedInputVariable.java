package function.variable.input.nonrecordwise.type;

import java.sql.SQLException;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.evaluator.nonsqlbased.stringprocessing.StringProcessingEvaluator;
import function.variable.input.nonrecordwise.NonRecordwiseInputVariable;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;

/**
 * ConstantValuedInputVariable can only be used by {@link StringProcessingEvaluator};
 * ConstantValuedInputVariable instance's value is set and fixed upon the instance is created for a {@link StringProcessingEvaluator} instance;
 * 
 * @author tanxu
 * 
 */
public class ConstantValuedInputVariable extends NonRecordwiseInputVariable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4637963293719367854L;
	
	//////////////////////
	private final VfDefinedPrimitiveSQLDataType SQLDataType;
	private final String valueString;
	
	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param aliasName
	 * @param notes
	 * @param SQLDataType
	 * @param hostEvaluatorIndexID
	 * @param valueString cannot be null;
	 */
	public ConstantValuedInputVariable(
//			MetadataID ownerRecordDataMetadataID,
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID,
			int hostEvaluatorIndexID,
			SimpleName aliasName, VfNotes notes, 
			VfDefinedPrimitiveSQLDataType SQLDataType,
			
			/////
			String valueString
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, hostEvaluatorIndexID, aliasName, notes);

		
		if(valueString==null)
			throw new IllegalArgumentException("given valueString cannot be null!");
		
		
		this.SQLDataType = SQLDataType;
		this.valueString = valueString;
	}

	public String getValueString() {
		return valueString;
	}
	
	
	//////////////////////////////////
	@Override
	public VfDefinedPrimitiveSQLDataType getSQLDataType() {
		return SQLDataType;
	}
	
	
	/**
	 * reproduce and return a new ConstantValuedInputVariable of this one;
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public ConstantValuedInputVariable reproduce(
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
		
		/////
		String valueString = this.getValueString();
		
		//////////
		return new ConstantValuedInputVariable(
//				reproducedOwnerRecordDataMetadataID,
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedHostEvaluatorIndexID,
				reproducedAliasName,
				reproducedNotes,
				SQLDataType,
				valueString
				);
	}



	//////////////////////////////////////
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((SQLDataType == null) ? 0 : SQLDataType.hashCode());
		result = prime * result + ((valueString == null) ? 0 : valueString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ConstantValuedInputVariable))
			return false;
		ConstantValuedInputVariable other = (ConstantValuedInputVariable) obj;
		if (SQLDataType == null) {
			if (other.SQLDataType != null)
				return false;
		} else if (!SQLDataType.equals(other.SQLDataType))
			return false;
		if (valueString == null) {
			if (other.valueString != null)
				return false;
		} else if (!valueString.equals(other.valueString))
			return false;
		return true;
	}
}
