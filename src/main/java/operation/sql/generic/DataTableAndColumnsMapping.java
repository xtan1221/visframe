package operation.sql.generic;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import basic.reproduce.Reproducible;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.MetadataID;
import rdb.table.data.DataTableColumnName;

/**
 * contains the mapping for a table and its columns in the SQL string of a {@link GenericSQLQuery} of a {@link GenericSQLOperation};
 * 
 * note that the owner record MetadataID of the data table is an input record Metadata of the {@link GenericSQLOperation};
 * 
 * @author tanxu
 * 
 */
public class DataTableAndColumnsMapping implements Reproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6932571519300466547L;
	
	
	//////////////////////////////////////////
	/**
	 * alias name of the data table of the {@link #ownerRecordMetadataID} used in the SQL query string;
	 * should be in UPPER case and should obey the general SQL naming convention; (see {@link GenericSQLQuery});
	 * cannot be null or empty string;
	 */
	private final String tableAliasName;
	
	/**
	 * MetadataID of the owner record Metadata of the data table;
	 * cannot be null;
	 */
	private final MetadataID ownerRecordMetadataID;
	
	/**
	 * map from the alias name of columns of the data table of the {@link #ownerRecordMetadataID} used in the SQL query string;
	 * alias names should be in UPPER case and obey the general SQL naming convention; (see {@link GenericSQLQuery});
	 * map keys cannot be null or empty;
	 * map cannot be null, but can be empty (in cases where only table name is involved??);
	 */
	private final Map<String, DataTableColumnName> columnAliasNameDataTableColumnNameMap;
	
	/**
	 * 
	 * @param tableAliasName
	 * @param recordMetadataID
	 * @param columnAliasNameDataTableColumnNameMap
	 */
	public DataTableAndColumnsMapping(
			String tableAliasName, 
			MetadataID recordMetadataID, 
			Map<String, DataTableColumnName> columnAliasNameDataTableColumnNameMap){
		//
		if(tableAliasName==null ||tableAliasName.isEmpty())
			throw new IllegalArgumentException("given tableAliasName cannot be null or empty!");
		if(recordMetadataID==null)
			throw new IllegalArgumentException("given recordMetadataID cannot be null!");
		if(columnAliasNameDataTableColumnNameMap==null || columnAliasNameDataTableColumnNameMap.isEmpty())
			throw new IllegalArgumentException("given columnAliasNameDataTableColumnNameMap cannot be null or empty!");
		columnAliasNameDataTableColumnNameMap.forEach((s,n)->{
			if(s==null||s.isEmpty())
				throw new IllegalArgumentException("given column alias name string cannot be null or empty!");
			if(n==null)
				throw new IllegalArgumentException("given data table column name cannot be null!");
		});
		
		
		/////////////////////
		this.tableAliasName = tableAliasName;
		this.ownerRecordMetadataID = recordMetadataID;
		this.columnAliasNameDataTableColumnNameMap = columnAliasNameDataTableColumnNameMap;
	}

	/**
	 * @return the tableAliasName
	 */
	public String getTableAliasName() {
		return tableAliasName;
	}

	/**
	 * @return the recordMetadataID
	 */
	public MetadataID getRecordMetadataID() {
		return ownerRecordMetadataID;
	}

	/**
	 * @return the columnAliasNameDataTableColumnNameMap
	 */
	public Map<String, DataTableColumnName> getColumnAliasNameDataTableColumnNameMap() {
		return columnAliasNameDataTableColumnNameMap;
	}

	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param recordMetadataCopyIndex copy index of the VCDNode/VSComponent to which the record Metadata is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public DataTableAndColumnsMapping reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter, 
			int recordMetadataCopyIndex)
			throws SQLException {
		String reproducedTableAliasName = this.tableAliasName;
		
		MetadataID reproducedRecordMetadataID = 
				this.getRecordMetadataID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, recordMetadataCopyIndex);
		
		Map<String, DataTableColumnName> reproducedColumnAliasNameDataTableColumnNameMap = new HashMap<>();
		for(String aliasName:this.columnAliasNameDataTableColumnNameMap.keySet()) {
			reproducedColumnAliasNameDataTableColumnNameMap.put(
					aliasName, 
					this.columnAliasNameDataTableColumnNameMap.get(aliasName).reproduce(
							VSAArchiveReproducerAndInserter, this.ownerRecordMetadataID, recordMetadataCopyIndex));
		}
		
		///////////////////
		return new DataTableAndColumnsMapping(
				reproducedTableAliasName,
				reproducedRecordMetadataID,
				reproducedColumnAliasNameDataTableColumnNameMap
				);
	}
	
	
	///////////////////////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columnAliasNameDataTableColumnNameMap == null) ? 0
				: columnAliasNameDataTableColumnNameMap.hashCode());
		result = prime * result + ((ownerRecordMetadataID == null) ? 0 : ownerRecordMetadataID.hashCode());
		result = prime * result + ((tableAliasName == null) ? 0 : tableAliasName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof DataTableAndColumnsMapping))
			return false;
		DataTableAndColumnsMapping other = (DataTableAndColumnsMapping) obj;
		if (columnAliasNameDataTableColumnNameMap == null) {
			if (other.columnAliasNameDataTableColumnNameMap != null)
				return false;
		} else if (!columnAliasNameDataTableColumnNameMap.equals(other.columnAliasNameDataTableColumnNameMap))
			return false;
		if (ownerRecordMetadataID == null) {
			if (other.ownerRecordMetadataID != null)
				return false;
		} else if (!ownerRecordMetadataID.equals(other.ownerRecordMetadataID))
			return false;
		if (tableAliasName == null) {
			if (other.tableAliasName != null)
				return false;
		} else if (!tableAliasName.equals(other.tableAliasName))
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "TableAndColumnsMapping [tableAliasName=" + tableAliasName + ", recordMetadataID=" + ownerRecordMetadataID
				+ ", columnAliasNameDataTableColumnNameMap=" + columnAliasNameDataTableColumnNameMap + "]";
	}

}
