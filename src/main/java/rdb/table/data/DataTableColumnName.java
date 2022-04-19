package rdb.table.data;

import java.sql.SQLException;

import basic.VfNameString;
import basic.reproduce.DataReproducible;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import dependency.dos.integrated.IntegratedDOSGraphNode;
import metadata.MetadataID;

public class DataTableColumnName extends VfNameString implements DataReproducible{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5761606011464461646L;

	/**
	 * constructor
	 * @param stringValue
	 */
	public DataTableColumnName(String stringValue) {
		super(stringValue);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * reproduce and return a new DataTableColumnName of this one;
	 */
	@Override
	public DataTableColumnName reproduce(
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter, 
			MetadataID ownerMetadataID, 
			int ownerMetadataCopyIndex) throws SQLException{
		//check if the IntegratedDOSGraphNode of the owner record Metadata in the trimmed integrated DOS graph is in solution set or a component of a composite Metadata of a IntegratedDOSGraphNode in solution set;
		IntegratedDOSGraphNode node = VSAArchiveReproducerAndInserter.getAppliedArchive().lookupIntegratedDOSGraphNode(ownerMetadataID, ownerMetadataCopyIndex);
		
		if(VSAArchiveReproducerAndInserter.getAppliedArchive().integratedDOSGraphNodeIsMapped(node)) {//the owner record metadata is mapped thus the column is mapped
			
			return VSAArchiveReproducerAndInserter.getAppliedArchive().lookupSourceDataTableColumnName(ownerMetadataID, ownerMetadataCopyIndex, this);
			
		}else {
			return new DataTableColumnName(this.getStringValue());
		}
	}
	
}
