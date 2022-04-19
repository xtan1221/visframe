package basic.lookup.project.type.table;

import java.sql.SQLException;

import basic.lookup.project.type.VisProjectHasIDTypeRelationalTableSchemaManagerBase;
import context.project.VisProjectDBContext;
import rdb.table.data.DataTableName;
import rdb.table.data.DataTableSchema;
import rdb.table.data.DataTableSchemaID;

public class VisProjectDataTableSchemaManager extends VisProjectHasIDTypeRelationalTableSchemaManagerBase<DataTableSchema, DataTableSchemaID> {
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	public VisProjectDataTableSchemaManager(VisProjectDBContext visProjectDBContext) {
		super(visProjectDBContext, DataTableSchema.class, DataTableSchemaID.class);
	}
	
	/**
	 * find and return the next un-occupied data table name of the given original one;
	 * 
	 * 1. if there is no existing data table with name same as the given one, return it;
	 * 2. else, add an integer index starting from 1 to the end of the original name concatenated with a '_' and check if it is used; if no, return it, otherwise, increase the index by 1 and check again until the first available one is found;
	 * 
	 * @param originalName
	 * @return
	 * @throws SQLException 
	 */
	public DataTableName findNextAvailableName(DataTableName originalName) throws SQLException {
		if(!this.checkIDExistence(new DataTableSchemaID(originalName))){
			return originalName;
		}
		
		int index = 1;
		DataTableSchemaID newID = new DataTableSchemaID(new DataTableName(originalName.getStringValue().concat("_").concat(Integer.toString(index))));
		
		while(this.checkIDExistence(newID)) {
			index++;
			newID = new DataTableSchemaID(new DataTableName(originalName.getStringValue().concat("_").concat(Integer.toString(index))));
		}
		
		return newID.getTableName();
		
	}
	
	/**
	 * 
	 * 111620-do not add the DataTableSchemaID to the insertedNonProcessIDSet of currently running simple process in the process log table;
	 * when roll back the process that inserted the record Metadata of the DataTableSchema, invoke the delete(ID) method of the VisProjectMetadataManager, 
	 * which will first delete the data table schema;
	 * ============================================================
	 * also insert the DataTableSchema in the InsertedNonProcessIDSetColumn of the currently running process;
	 * 
	 * this is not applicable to table schema related with CFTargetValueTableRun;
	 * 
	 */
	@Override
	public void insert(DataTableSchema tableSchema) throws SQLException {
		super.insert(tableSchema);
		
		//insert the ID to the currently running process’s INSERTED_NON_PROCESS_ID _SET column
//		this.getProcessLogTableManager().addToInsertedNonProcessIDSetColumnOfCurrentlyRunningSimpleProcess(tableSchema.getID()); //TODO commented out for testing for {@link RecordDataImporterTest#testCall()}
	}
}
