package rdb.table.data;

import java.sql.SQLException;

import basic.SimpleName;
import basic.reproduce.DataReproducible;
import context.project.rdb.VisProjectRDBConstants;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import dependency.dos.integrated.IntegratedDOSGraphNode;
import metadata.MetadataID;
import rdb.table.HasIDTypeRelationalTableSchemaID;

/**
 * 
 * @author tanxu
 *
 */
public class DataTableSchemaID extends HasIDTypeRelationalTableSchemaID<DataTableSchema> implements DataReproducible{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7950629471419795614L;
	
	/////////////////
	private final DataTableName tableName;
	
	/**
	 * constructor
	 * @param RDBSchemaName
	 * @param tableName
	 */
	public DataTableSchemaID(DataTableName tableName){
		this.tableName = tableName;
	}
	
	@Override
	public SimpleName getSchemaName() {
		return VisProjectRDBConstants.DATA_SCHEMA_NAME;
	}
	
	@Override
	public DataTableName getTableName() {
		return tableName;
	}
	
	
	
	/**
	 * reproduce and return a new DataTableSchemaID of this one;
	 * @throws SQLException 
	 */
	@Override
	public DataTableSchemaID reproduce(
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter, 
			MetadataID ownerRecordMetadataID, 
			int ownerRecordMetadataCopyIndex) throws SQLException {
		//first find out whether the IntegratedDOSGraphNode containing the copy of owner record data is in the solution set or a component of a composite Metadata of a IntegratedDOSGraphNode in solution set;
		IntegratedDOSGraphNode node = VSAArchiveReproducerAndInserter.getAppliedArchive().lookupIntegratedDOSGraphNode(ownerRecordMetadataID, ownerRecordMetadataCopyIndex);
		
		if(VSAArchiveReproducerAndInserter.getAppliedArchive().integratedDOSGraphNodeIsMapped(node)) {//the owner record metadata is mapped thus the column is mapped
			
			return VSAArchiveReproducerAndInserter.getAppliedArchive().lookupSourceRecordDataTableSchemaID(ownerRecordMetadataID, ownerRecordMetadataCopyIndex);
			
		}else {
			return new DataTableSchemaID(this.getTableName());
		}
	}
	
	
	//////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataTableSchemaID other = (DataTableSchemaID) obj;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "DataTableSchemaID [tableName=" + tableName + "]";
	}
	
	

}
