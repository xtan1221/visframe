package basic.lookup.project.type;

import java.sql.SQLException;

import context.project.VisProjectDBContext;
import exception.IDNotFoundException;
import exception.VisframeException;
import basic.lookup.HasID;
import basic.lookup.ID;
import basic.lookup.project.VisProjectHasIDTypeManagerBase;
import rdb.table.HasIDTypeRelationalTableSchema;
import rdb.table.HasIDTypeRelationalTableSchemaID;
import sql.derby.TableSchemaUtils;


/**
 * manager class for all LookupableRelationalTableSchema in the rdb of a host VisProjectDBContext;
 * 
 * including all {@link DataTableSchema} and {@link ValueTableSchema}
 * 
 * 
 * @author tanxu
 *
 * @param <T>
 * @param <I>
 */
public class VisProjectHasIDTypeRelationalTableSchemaManagerBase<T extends HasIDTypeRelationalTableSchema, I extends HasIDTypeRelationalTableSchemaID<T>> 
extends VisProjectHasIDTypeManagerBase<T,I>{
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param managedType
	 * @param IDType
	 */
	public VisProjectHasIDTypeRelationalTableSchemaManagerBase(
			VisProjectDBContext visProjectDBContext,
			Class<T> managedType, Class<I> IDType
			) {
		super(visProjectDBContext, managedType, IDType);
	}
	

	/**
	 * lookup if the LookupableRelationalTableSchema with the given LookupableRelationalTableSchemaID exist in the visProjectDBConnection;
	 * 
	 * 
	 * @throws SQLException 
	 */
	@Override
	public boolean checkIDExistence(I tableSchemaID) throws SQLException {
		return TableSchemaUtils.doesTableExists(this.getVisProjectDBConnection(), tableSchemaID.getSchemaName(), tableSchemaID.getTableName());
	}
	
	
	/**
	 * Insert the given table schema into the corresponding db schema;
	 * 
	 * note that only for data table schema of a record metadata, need to insert the ID of the table schema to the currently running process’s INSERTED_NON_PROCESS_ID _SET column with ProcessLogTableManager. addToInsertedNonProcessIDSetColumnOfCurretnlyRunningProcess(ID) method
	 */
	@Override
	public void insert(T tableSchema) throws SQLException {
		TableSchemaUtils.createTableSchema(this.getVisProjectDBConnection(), tableSchema);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void delete(ID<? extends HasID> id) throws SQLException {
		if(!this.isValidID(id)) {
			throw new VisframeException("given tableSchemaID is not valid type");
		}
		
		I tableSchemaID = (I)id;
		
		try {
			TableSchemaUtils.dropTable(this.getVisProjectDBConnection(), tableSchemaID.getSchemaName(), tableSchemaID.getTableName());
		}catch(IllegalArgumentException e) {
			throw new IDNotFoundException("table not found!");
		}
	}

	/////////////////////////////////////////
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public boolean getIsReproducedColumnValue(I id) throws SQLException {
//		throw new VisframeException("cannot get INDEPENDENT COLUMN value for RelationalTableSchema");
//	}
//
//	
//	@Override
//	public void addDependentProcessID(ID<? extends HasID> id, PrimaryKeyID<? extends VisframeUDT> dependentProcessID)
//			throws SQLException {
//		throw new VisframeException("cannot add dependent process id to inserted RelationalTableSchema");
//	}

	
}
