package rdb.table.data;

import java.sql.SQLException;

import basic.HasNotes;
import basic.VfNotes;
import basic.reproduce.DataReproducible;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.MetadataID;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;
import rdb.table.HasIDTypeRelationalTableColumn;

public class DataTableColumn extends HasIDTypeRelationalTableColumn implements HasNotes, DataReproducible{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4643827016810828238L;
	
	
	//////////////////
	private final VfNotes notes;
	
	/**
	 * constructor
	 * @param name
	 * @param sqlDataType
	 * @param inPrimaryKey
	 * @param unique
	 * @param notNull
	 * @param defaultStringValue
	 * @param additionalConstraints
	 * @param notes
	 */
	public DataTableColumn(
			DataTableColumnName name, VfDefinedPrimitiveSQLDataType sqlDataType, boolean inPrimaryKey,
			Boolean unique, Boolean notNull, String defaultStringValue, String additionalConstraints,
			
			VfNotes notes
			) {
		super(name, sqlDataType, inPrimaryKey, unique, notNull, defaultStringValue, additionalConstraints);//String additionalConstraints
		
		if(notes == null)
			throw new IllegalArgumentException("given notes cannot be null!");
		
		
		this.notes = notes;
	}
	
	@Override
	public DataTableColumnName getName() {
		return (DataTableColumnName)this.name;
	}

	@Override
	public VfNotes getNotes() {
		return notes;
	}
	
	@Override
	public VfDefinedPrimitiveSQLDataType getSqlDataType() {
		return (VfDefinedPrimitiveSQLDataType)sqlDataType;
	}
	
	/**
	 * reproduce and return a new DataTableColumn of this one;
	 */
	@Override
	public DataTableColumn reproduce(
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter, 
			MetadataID ownerMetadataID, 
			int ownerMetadataCopyIndex) throws SQLException{
		return new DataTableColumn(
				this.getName().reproduce(VSAArchiveReproducerAndInserter, ownerMetadataID, ownerMetadataCopyIndex),
				this.getSqlDataType().reproduce(),
				this.isInPrimaryKey(),
				this.isUnique(),
				this.isNotNull(),
				this.getDefaultStringValue(),
				this.getAdditionalConstraints(),
				this.getNotes().reproduce()
				);
	}

	
	///////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof DataTableColumn))
			return false;
		DataTableColumn other = (DataTableColumn) obj;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataTableColumn [name=" + name + "]";
	}
	
	////////////////////////
	
	
}
