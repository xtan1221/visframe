package operation.utils;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import basic.reproduce.DataReproducibleLinkedHashSet;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.MetadataID;
import rdb.table.data.DataTableColumnName;

/**
 * class for a LinkedHashSet of data table column names of the same DataTableSchema
 * 
 * used in Operation API to facilitate {@link Operation#reproduce()} method implementation
 * 
 * @author tanxu
 *
 */
public class DataTableColumnNameLinkedHashSet extends DataReproducibleLinkedHashSet<DataTableColumnName>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1020625110835408655L;
	
	///////////////////////////////////
	/**
	 * constructor
	 * @param set
	 */
	public DataTableColumnNameLinkedHashSet(LinkedHashSet<DataTableColumnName> set){
		super(set);
	}
	
	
	public static DataTableColumnNameLinkedHashSet emptySet() {
		return new DataTableColumnNameLinkedHashSet(new LinkedHashSet<>());
	}
	
	
	
	/**
	 * reproduce and return a new DataTableColumnNameSet of this one
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @return
	 * @throws SQLException 
	 */
	@Override
	public DataTableColumnNameLinkedHashSet reproduce(
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter, 
			MetadataID ownerRecordMetadataID, 
			int ownerMetadataCopyIndex) throws SQLException {
		LinkedHashSet<DataTableColumnName> set = new LinkedHashSet<>();
		
		for(DataTableColumnName cn:this.getSet()) {
			set.add(cn.reproduce(VSAArchiveReproducerAndInserter, ownerRecordMetadataID, ownerMetadataCopyIndex));
		}
		
		return new DataTableColumnNameLinkedHashSet(set);
	}

	
	
}
