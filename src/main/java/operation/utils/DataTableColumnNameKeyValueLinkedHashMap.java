package operation.utils;

import java.util.LinkedHashMap;
import basic.reproduce.DataReproducibleKeyValueLinkedHashMap;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.MetadataID;
import operation.graph.build.BuildGraphFromTwoExistingRecordOperation;
import rdb.table.data.DataTableColumnName;


/**
 * class for a map from DataTableColumnName to DataTableColumnName;
 * 
 * Pseudo DataReproducible!!!!!!!!!
 * see documentation of {@link DataTableColumnNameKeyValueLinkedHashMap#reproduce(VisSchemeAppliedArchiveReproducerAndInserter, MetadataID, int)};
 * 
 * @author tanxu
 * 
 */
public class DataTableColumnNameKeyValueLinkedHashMap extends DataReproducibleKeyValueLinkedHashMap<DataTableColumnName,DataTableColumnName>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3971678534359932674L;
	
	//////////////////////////
	/**
	 * constructor
	 */
	public DataTableColumnNameKeyValueLinkedHashMap(LinkedHashMap<DataTableColumnName,DataTableColumnName> map){
		super(map);
	}
	
	
	/**
	 * it is not possible to reproduce a DataTableColumnNameKeyValueMap inside DataTableColumnNameKeyValueMap class since the key and value DataTableColumnName may belong to different ownerMetadataID;
	 * must reproduce the underlying map from where the ownerMetadataID for both key and value DataTableColumnName are available;
	 * 
	 * see {@link BuildGraphFromTwoExistingRecordOperation#reproduceBuildGraphFromExistingRecordOperationLevelParameterObjectValueMap(context.project.VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)}
	 * for example
	 * 
	 */
	@Override
	public DataTableColumnNameKeyValueLinkedHashMap reproduce(
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter, 
			MetadataID ownerRecordMetadataID, 
			int ownerMetadataCopyIndex) {
		
		
		throw new UnsupportedOperationException();
	}
	
}
