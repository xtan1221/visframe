package basic.reproduce;

import java.io.Serializable;
import java.sql.SQLException;

import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.MetadataID;
import metadata.MetadataName;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import rdb.table.data.DataTableName;
import rdb.table.data.DataTableSchema;

/**
 * ======================================================
 * 111920-update
 * 
 * for entity types that is owned by a record Metadata;
 * 
 * the owner record MetadataID is needed to find out 
 * 1. whether the IntegratedDOSGraphNode containing the owner record Metadata is in solution set or 
 * 		the owner record data is component of a composite Metadata whose IntegratedDOSGraphNode is in solution set, 
 * 		thus mapped from existing data in host VisProjectDBContext 
 * 2. or reproduced
 * 
 * 
 * currently {@link MetadataName}, {@link DataTableName}, {@link DataTableColumnName};
 * 
 * !!!!also for any general reproducible types that contains one or more DataReproducible fields but with no access to the owner MetadataID information;
 * for example {@link DataTableColumn}, {@link DataTableSchema};
 * 
 * note that for MetadataID, since it has access to the ownerMetadataID(which is itself), thus, MetadataID is Reproducible(trivial, it can be any of those two);
 * 
 * 
 * @author tanxu
 * 
 */
public interface DataReproducible extends Serializable{
	
	/**
	 * 
	 * @param visSchemeAppliedArchiveReproducedAndInsertedInstanceUID
	 * @param ownerRecordMetadataID the MetadataID of the owner record data
	 * @param ownerMetadataCopyIndex
	 * @return
	 * @throws SQLException
	 */
	DataReproducible reproduce(
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter, 
			MetadataID ownerRecordMetadataID, 
			int ownerMetadataCopyIndex) throws SQLException;
}
