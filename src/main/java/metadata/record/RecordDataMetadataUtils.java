package metadata.record;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import rdb.table.data.DataTableSchema;
import rdb.table.data.DataTableSchemaFactory;

public class RecordDataMetadataUtils {
	
	
	/**
	 * build and return a new RecordDataMetadata with everything the same as the given original RecordDataMetadata but only keep the minimal set of columns;
	 * 
	 * the RUID column will be auto-included; 
	 * for other columns, only those columns in primary key set or in the given includedColumnNameSet will be kept in the data table schema of the returned RecordDataMetadata
	 * 
	 * @param original
	 * @param includedColumnNameSet can be empty; could contain primary key set columns;
	 * @return
	 */
	public static RecordDataMetadata buildNewWithMinimalColumnSet(RecordDataMetadata original, Set<DataTableColumnName> includedColumnNameSet) {
		DataTableSchema table = original.getDataTableSchema();
		
		List<DataTableColumn> orderedListOfColumnOfNewTable = new ArrayList<>(); 
		orderedListOfColumnOfNewTable.add(DataTableSchemaFactory.makeRUIDColumn());
		table.getOrderedListOfColumn().forEach(c->{
			if(c.isInPrimaryKey() || includedColumnNameSet.contains(c.getName())) {
				orderedListOfColumnOfNewTable.add(c);
			}
		});
		
		
		DataTableSchema newTable = new DataTableSchema(table.getName(), orderedListOfColumnOfNewTable);
		
		
		return new RecordDataMetadata(
				original.getName(),//MetadataName name, 
				original.getNotes(),//VfNotes notes, 
				original.getSourceType(),//SourceType sourceType,
				original.getSourceCompositeDataMetadataID(),//MetadataID sourceCompositeDataMetadataID, 
				original.getSourceOperationID(),//OperationID sourceOperationID,
				
				newTable,//DataTableSchema dataTableSchema,
				original.isOfGenericGraphNode()//Boolean ofGenericGraphNode
				);
	}
}
