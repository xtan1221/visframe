package rdb.table.value.type;

import java.sql.SQLException;
import java.sql.Statement;

import basic.SimpleName;
import context.project.VisProjectDBContext;
import context.project.rdb.VisProjectRDBConstants;
import function.composition.CompositionFunction;
import metadata.record.RecordDataMetadata;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.table.value.ValueTableColumn;
import rdb.table.value.ValueTableSchema;
import sql.derby.TableContentSQLStringFactory;
import sql.derby.TableSchemaUtils;


/**
 * base class that initialize a value table schema that are used in the calculation of a CompositionFunction with a {@link IndependentFIVTypeIDStringValueMap} for a host VisProjectDBContext;
 * 
 * note that the only primary key column of value table is the RUID column;
 * 
 * specifically, this class will 
 * 1. create the {@link ValueTableSchema};
 * 2. insert it into the corresponding schema of the host VisProjectDBContext
 * 3. populate the values of RUID column
 * 4. populate the values of any other columns if necessary;
 * 
 * after the initialization here, the calculation of each ComponentFunction on the target CompostionFunction can start right away;
 * 
 * @author tanxu
 *
 */
public abstract class ValueTableInitializer<T extends ValueTableSchema> {
	/**
	 * static factory method to make a new RUID column
	 * @return
	 */
	protected static ValueTableColumn makeRUIDColumn() {
		return new ValueTableColumn(
				new SimpleName(VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE),
				SQLDataTypeFactory.integerType(),
				true, //value table's pk contains only the RUID column;
				true, 
				true,
				null);
	}
	
	///////////////////////////////////
	private final VisProjectDBContext hostVisProjectDBContext;
	private final CompositionFunction targetCompositionFunction;
	private final int CFTargetValueTableRunUID;
	
	//////////////////////////////////////////
	protected T valueTableSchema;
	

	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @throws SQLException 
	 */
	protected ValueTableInitializer(
			VisProjectDBContext hostVisProjectDBContext,
			CompositionFunction targetCompositionFunction,
			int CFTargetValueTableRunUID
			) throws SQLException{
		if(hostVisProjectDBContext==null)
			throw new IllegalArgumentException("given hostVisProjectDBContext cannot be null!");
		if(targetCompositionFunction==null)
			throw new IllegalArgumentException("given targetCompositionFunction cannot be null!");
		
		this.hostVisProjectDBContext = hostVisProjectDBContext;
		this.targetCompositionFunction = targetCompositionFunction;
		this.CFTargetValueTableRunUID = CFTargetValueTableRunUID;
		
		//
		this.makeTableSchema();
	}
	

	/**
	 * @return the valueTableSchema
	 */
	public T getValueTableSchema() {
		return valueTableSchema;
	}

	
	/**
	 * @return the hostVisProjectDBContext
	 */
	protected VisProjectDBContext getHostVisProjectDBContext() {
		return hostVisProjectDBContext;
	}


	/**
	 * @return the targetCompositionFunction
	 */
	protected CompositionFunction getTargetCompositionFunction() {
		return targetCompositionFunction;
	}

	/**
	 * @return the cFTargetValueTableRunUID
	 */
	protected int getCFTargetValueTableRunUID() {
		return CFTargetValueTableRunUID;
	}


	
	
	/**
	 * build the table schema;
	 * 
	 * 
	 * @param cf
	 * @param CFTargetValueTableRunUID
	 * @return
	 * @throws SQLException 
	 */
	protected abstract void makeTableSchema() throws SQLException;
	
	///////////////////////
	/**
	 * 
	 * @throws SQLException
	 */
	public void insertIntoHostVisProjectDBContextAndPopulateRUIDColumn() throws SQLException {
		this.insertIntoHostVisProjectDBContext();
		this.populateRUIDColumn();
	}
	
	
	/**
	 * insert the built value table schema into the host VisProjectDBContext;
	 * @throws SQLException 
	 */
	private void insertIntoHostVisProjectDBContext() throws SQLException {
		TableSchemaUtils.createTableSchema(this.getHostVisProjectDBContext().getDBConnection(), this.valueTableSchema);
	}
	
	
	/**
	 * populate the RUID column with the owner record data table's RUID column;
	 * @throws SQLException 
	 */
	private void populateRUIDColumn() throws SQLException {
		RecordDataMetadata ownerRecordData = (RecordDataMetadata) this.getHostVisProjectDBContext().getMetadataLookup().lookup(this.getTargetCompositionFunction().getOwnerRecordDataMetadataID());
		String sqlString = TableContentSQLStringFactory.buildCopyColumnDataOfATableToColumnOfBTableSqlQueryString(
				ownerRecordData.getDataTableSchema().getID(), VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE, //source 
				this.valueTableSchema.getID(), VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE //target
				);
		
		Statement statement = this.getHostVisProjectDBContext().getDBConnection().createStatement();
		statement.execute(sqlString);
	}
	
	
	
}
