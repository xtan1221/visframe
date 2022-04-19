package context.project.process.manager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import basic.lookup.PrimaryKeyID;
import context.project.process.logtable.ProcessLogTableAndProcessPerformerManager;
import context.project.process.logtable.ProcessLogTableSchemaUtils;
import context.project.process.logtable.StatusType;
import function.composition.CompositionFunctionID;
import importer.DataImporterID;
import javafx.application.Platform;
import operation.OperationID;
import sql.SQLStringUtils;
import sql.derby.TableContentSQLStringFactory;

/**
 * base class for a manager class of a specific type or a set of types of processes running in a host VisProjectDBContext;
 * 
 * @author tanxu
 *
 */
public abstract class AbstractProcessManager {
	/**
	 * 
	 */
	private final ProcessLogTableAndProcessPerformerManager processLogTableManager;
	
	//////////////////////////////////////
	
	/**
	 * 
	 * @param processLogTableManager
	 */
	AbstractProcessManager(ProcessLogTableAndProcessPerformerManager processLogTableManager){
		this.processLogTableManager = processLogTableManager;
	}

	
	/**
	 * @return the processLogTableManager
	 */
	public ProcessLogTableAndProcessPerformerManager getProcessLogTableManager() {
		return processLogTableManager;
	}
	
	/////////////////////////////////
	/**
	 * the set of Runnable to be invoked after a row's processStatusColumn value is changed
	 */
	protected Set<Runnable> processInsertedOrStatusColumnChangeEventRunnableSet;
	/**
	 * 
	 * @param runnable
	 */
	public void addToProcessInsertedOrStatusColumnChangeEventRunnable(Runnable runnable) {
		if(this.processInsertedOrStatusColumnChangeEventRunnableSet==null)
			this.processInsertedOrStatusColumnChangeEventRunnableSet = new LinkedHashSet<>();
		this.processInsertedOrStatusColumnChangeEventRunnableSet.add(runnable);
	}
	
	/**
	 * 
	 */
	protected Set<Runnable> CFTypeProcessInsertedOrStatusColumnChangeEventRunnableSet;
	/**
	 * 
	 * @param runnable
	 */
	public void addToCFTypeProcessInsertedOrStatusColumnChangeEventRunnable(Runnable runnable) {
		if(this.CFTypeProcessInsertedOrStatusColumnChangeEventRunnableSet==null)
			this.CFTypeProcessInsertedOrStatusColumnChangeEventRunnableSet = new LinkedHashSet<>();
		this.CFTypeProcessInsertedOrStatusColumnChangeEventRunnableSet.add(runnable);
	}
	
	/**
	 * Runnable to be invoked after the processStatusColumn value is changed for a process that result in insertion of new Metadata into the host VisProjectDBContext
	 * specifically, DataImporter and Operation
	 */
	protected Set<Runnable> metadataProducingTypeProcessInsertedOrStatusColumnChangeEventRunnableSet;
	/**
	 * 
	 * @param runnable
	 */
	public void addToMetadataProducingTypeProcessInsertedOrStatusColumnChangeEventRunnableSet(Runnable runnable) {
		if(this.metadataProducingTypeProcessInsertedOrStatusColumnChangeEventRunnableSet==null)
			this.metadataProducingTypeProcessInsertedOrStatusColumnChangeEventRunnableSet = new LinkedHashSet<>();
		this.metadataProducingTypeProcessInsertedOrStatusColumnChangeEventRunnableSet.add(runnable);
	}
	
	/**
	 * set the status of the process with the given UID in the process log table
	 * 
	 * @param UID
	 * @param type
	 * @throws SQLException
	 */
	public void setProcessStatusInLogTable(int UID, StatusType type) throws SQLException {
		List<String> columnNameListToBeUpdated = new ArrayList<>();
		columnNameListToBeUpdated.add(ProcessLogTableSchemaUtils.processStatusColumn.getName().getStringValue());
		
		String psSqlString = TableContentSQLStringFactory.buildUpdateColumnsPreparedStatementSqlString(
				SQLStringUtils.buildTableFullPathString(ProcessLogTableSchemaUtils.getTableSchema().getSchemaName(),
						ProcessLogTableSchemaUtils.getTableSchema().getName()),
				columnNameListToBeUpdated, TableContentSQLStringFactory.buildColumnValueEquityCondition(
						ProcessLogTableSchemaUtils.UIDColumn.getName().getStringValue(), Integer.toString(UID), false, null));
		
		PreparedStatement ps = this.getProcessLogTableManager().getHostVisProjectDBContext().getDBConnection().prepareStatement(psSqlString);
		
		ps.setObject(1, type.toString());//
		
		ps.execute();
		
		
		///TODO invoke any Runnables
		if(this.processInsertedOrStatusColumnChangeEventRunnableSet!=null)
			this.processInsertedOrStatusColumnChangeEventRunnableSet.forEach(r->{
				Platform.runLater(r);
			});
		
		
		PrimaryKeyID<?> processID = this.getProcessLogTableManager().retrieveRow(UID).getProcessID();
		
		if(processID instanceof CompositionFunctionID && this.CFTypeProcessInsertedOrStatusColumnChangeEventRunnableSet!=null)
			this.CFTypeProcessInsertedOrStatusColumnChangeEventRunnableSet.forEach(r->{
				Platform.runLater(r);
			});
		
		if((processID instanceof DataImporterID || processID instanceof OperationID) && this.metadataProducingTypeProcessInsertedOrStatusColumnChangeEventRunnableSet!=null)
			this.metadataProducingTypeProcessInsertedOrStatusColumnChangeEventRunnableSet.forEach(r->{
				Platform.runLater(r);
			});
		
	}
}
