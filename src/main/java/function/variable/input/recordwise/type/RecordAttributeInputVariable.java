package function.variable.input.recordwise.type;

import java.sql.SQLException;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.variable.input.recordwise.RecordwiseInputVariable;
import metadata.MetadataID;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableSchemaID;

/**
 * contains a single record Metadata's attribute
 * @author tanxu
 *
 */
public class RecordAttributeInputVariable extends RecordwiseInputVariable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9218642449404448890L;
	
	////////////////////
	/**
	 * the DataTableSchemaID for the data table of the owner record Metadata of the attribute
	 */
	private final DataTableSchemaID schemaID;
	private final DataTableColumn column;
	
	
	/**
	 * constructor
	 * @param ownerRecordDataMetadataID
	 * @param hostCompositionFunctionID
	 * @param hostComponentFunctionIndexID
	 * @param evaluatorIndexID
	 * @param aliasName
	 * @param notes
	 * @param targetRecordDataMetadataID the MetadataID of the owner record data of the target column; not necessarily the same with ownerRecordDataMetadataID
	 * @param schemaID
	 * @param column
	 */
	public RecordAttributeInputVariable(
			CompositionFunctionID hostCompositionFunctionID, 
			int hostComponentFunctionIndexID,
			int evaluatorIndexID,
			SimpleName aliasName, 
			VfNotes notes,
			
			MetadataID targetRecordDataMetadataID,
			DataTableSchemaID schemaID,
			DataTableColumn column
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, evaluatorIndexID, aliasName, notes, targetRecordDataMetadataID);
		// TODO Auto-generated constructor stub
		
		
		this.schemaID = schemaID;
		this.column = column;
	}
	

	public DataTableSchemaID getDataTableSchemaID() {
		return schemaID;
	}
	
	public DataTableColumn getColumn() {
		return column;
	}
	
	
	/////////////////////////////////////
	@Override
	public VfDefinedPrimitiveSQLDataType getSQLDataType() {
		return column.getSqlDataType();
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
	public RecordAttributeInputVariable reproduce(
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
		
		
		///////////
		DataTableSchemaID dataTableSchemaID = 
				this.getDataTableSchemaID().reproduce(VSAArchiveReproducerAndInserter, this.getTargetRecordDataMetadataID(), copyIndexOfTargetRecordMetadataID);
		
		DataTableColumn column = 
				this.getColumn().reproduce(VSAArchiveReproducerAndInserter, this.getTargetRecordDataMetadataID(), copyIndexOfTargetRecordMetadataID);
		
		
		///////////////////
		return new RecordAttributeInputVariable(
//				reproducedOwnerRecordDataMetadataID,
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedHostEvaluatorIndexID,
				reproducedAliasName,
				reproducedNotes,
				targetRecordMetadataID,
				dataTableSchemaID,
				column
				);
	}

	
	/////////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result + ((schemaID == null) ? 0 : schemaID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof RecordAttributeInputVariable))
			return false;
		RecordAttributeInputVariable other = (RecordAttributeInputVariable) obj;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!column.equals(other.column))
			return false;
		if (schemaID == null) {
			if (other.schemaID != null)
				return false;
		} else if (!schemaID.equals(other.schemaID))
			return false;
		return true;
	}

	

	//////////////////////////////
	
}
