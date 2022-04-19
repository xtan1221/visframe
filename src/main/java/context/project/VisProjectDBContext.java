package context.project;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import basic.lookup.Lookup;
import basic.lookup.project.VisProjectHasIDTypeManagerController;
import basic.lookup.project.VisProjectHasIDTypeManagerControllerImpl;
import context.VisframeContext;
import context.project.process.logtable.ProcessLogTableAndProcessPerformerManager;
import context.project.process.logtable.ProcessLogTableRow;
import context.project.process.logtable.ProcessLogTableSchemaUtils;
import context.project.process.logtable.StatusType;
import context.project.rdb.initialize.VisProjectDBSchemaBuilder;
import context.project.rdb.initialize.VisProjectDBUDTBuilder;
import context.project.rdb.initialize.VisProjectManagementTableBuilder;
import dependency.cfd.SimpleCFDGraph;
import dependency.cfd.SimpleCFDGraphBuilder;
import dependency.dos.SimpleDOSGraph;
import dependency.dos.SimpleDOSGraphBuilder;
import exception.VisframeException;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroup;
import function.group.CompositionFunctionGroupID;
import function.variable.independent.IndependentFreeInputVariableType;
import function.variable.independent.IndependentFreeInputVariableTypeID;
import metadata.Metadata;
import metadata.MetadataID;
import metadata.record.RecordDataMetadata;
import operation.Operation;
import operation.OperationID;
import rdb.table.data.DataTableSchemaID;
import sql.derby.DerbyDBUtils;

/**
 * a VisProjectDBContext
 * @author tanxu
 */
public class VisProjectDBContext implements VisProjectDBFeatures, VisframeContext{
	private final Path projectParentDirectoryPath;
	
	///////////////////////////
	private SimpleName projectName;//cannot be null 
	private VfNotes notes;
	
	//
	private Connection projectDBCon;
	
	/**
	 * 
	 */
	private VisProjectHasIDTypeManagerController hasIDTypeManagerController;
	
	/**
	 * 
	 */
	private ProcessLogTableAndProcessPerformerManager processLogTableAndProcessPerformerManager;
	
	
	/**
	 * constructor
	 * @param projectName
	 * @param notes
	 * @param fullPathOfParentDirectory
	 */
	public VisProjectDBContext(SimpleName projectName, Path projectParentDirectoryPath) {
		this.projectName = projectName;
		this.projectParentDirectoryPath = projectParentDirectoryPath;
	}
	
	
	/////////////////////////////////////////////////////////////
	///VisframeContext methods
	@Override
	public SimpleName getName() {
		return this.projectName;
	}

	
	@Override
	public VfNotes getNotes() {
		return this.notes;
	}
	
	
	//////////////////////////
	
	@Override
	public Lookup<Metadata, MetadataID> getMetadataLookup() {
		return this.getHasIDTypeManagerController().getMetadataManager();
	}
	
	
	@Override
	public Lookup<Operation, OperationID> getOperationLookup() {
		return this.getHasIDTypeManagerController().getOperationManager();
	}
	
	
	@Override
	public Lookup<CompositionFunctionGroup, CompositionFunctionGroupID> getCompositionFunctionGroupLookup() {
		return this.getHasIDTypeManagerController().getCompositionFunctionGroupManager();
	}


	@Override
	public Lookup<CompositionFunction, CompositionFunctionID> getCompositionFunctionLookup() {
		return this.getHasIDTypeManagerController().getCompositionFunctionManager();
	}
	

	@Override
	public Lookup<IndependentFreeInputVariableType, IndependentFreeInputVariableTypeID> getIndependentFreeInputVariableTypeLookup() {
		return this.getHasIDTypeManagerController().getIndependentFreeInputVariableTypeManager();
	}
	
	
	//////////////////
	/**
	 * 
	 */
	@Override
	public CompositionFunctionID getCompositionFuncitionID(CompositionFunctionGroupID cfgID, SimpleName targetName) {
		try {
			return this.getHasIDTypeManagerController().getCompositionFunctionManager().getTargetNameAssignedCFIDMap(cfgID).get(targetName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	public Set<CompositionFunctionID> getCompositionFunctionIDSetOfGroupID(CompositionFunctionGroupID cfgID) {
		try {
			return new HashSet<>(this.getHasIDTypeManagerController().getCompositionFunctionManager().getCompositionFunctionIDSetOfGroupID(cfgID));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	public DataTableSchemaID getDataTableSchemaID(MetadataID recordMetadataID) {
		try {
			RecordDataMetadata rdm = (RecordDataMetadata)this.getHasIDTypeManagerController().getMetadataManager().lookup(recordMetadataID);
			return rdm.getDataTableSchema().getID();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);//debug
		}
		return null;
	}
	
	@Override
	public DataTableSchemaID getOwnerRecordDataTableSchemaID(CompositionFunctionGroupID cfgID) {
		try {
			return this.getDataTableSchemaID(
					this.getHasIDTypeManagerController().getCompositionFunctionGroupManager().lookup(cfgID).getOwnerRecordDataMetadataID()
					);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);//debug
		}
		return null;
	}
	
	/////////////////////////////////////////////////////////////////////
	//VisProjectDBFeatures interface
	@Override
	public void setName(SimpleName newName) {
		this.projectName = newName;
	}
	
	@Override
	public void setNotes(VfNotes newNotes) {
		this.notes = newNotes;
	}
	
	@Override
	public Path getProjectParentDirectoryPath() {
		return this.projectParentDirectoryPath;
	}
	
	@Override
	public Path getDBParentPath() {
		return Paths.get(this.getProjectParentDirectoryPath().toString(), this.getName().getStringValue());
	}
	
	@Override
	public Connection getDBConnection() throws SQLException {
		if(this.projectDBCon==null || this.projectDBCon.isClosed()) {
			this.projectDBCon = DerbyDBUtils.getEmbeddedDBConnection(this.getDBParentPath(), VisProjectDBUtils.DB_DIR_NAME, true);
		}
		return this.projectDBCon;
	}
	
	@Override
	public void connect() throws SQLException {
		System.out.println("connected to DB...");
		this.hasIDTypeManagerController = new VisProjectHasIDTypeManagerControllerImpl(this);
//		System.out.println(this.getHasIDTypeManagerController());
		
		this.processLogTableAndProcessPerformerManager = new ProcessLogTableAndProcessPerformerManager(this);
		
		//////
		VisProjectDBSchemaBuilder schemaBuilder = new VisProjectDBSchemaBuilder(this);
		VisProjectDBUDTBuilder udtBuilder = new VisProjectDBUDTBuilder(this);
		VisProjectManagementTableBuilder managementTableBuilder = new VisProjectManagementTableBuilder(this);
		
		////
		if(schemaBuilder.allExist() && udtBuilder.allExist() && managementTableBuilder.allExist() && this.getProcessLogTableAndProcessPerformerManager().doesTableExist()) {//reconnect to a pre-existing db
			System.out.println("all features are present...");
			this.checkDBIntegrity();
			this.checkDataConsistency();
			this.retrieveNotes();
		}else {//at least one feature is missing, first time connection to new db
			System.out.println("...initialize new project DB...");
			schemaBuilder.initialize();
			udtBuilder.initialize();
			managementTableBuilder.initialize();
			this.getProcessLogTableAndProcessPerformerManager().createTableInHostProjectDB();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkDBIntegrity() {
		//TODO
	}
	
	/**
	 * {@inheritDoc}
	 * @throws SQLException 
	 */
	@Override
	public void checkDataConsistency() throws SQLException {
		ResultSet rs = this.getProcessLogTableAndProcessPerformerManager().getResultSetOfAllProcessOrderedByStartTimeDESC();
		
		// roll back undone processes
		while (rs.next()) {
			int UID = rs.getInt(ProcessLogTableSchemaUtils.UIDColumn.getName().getStringValue());
			StatusType status = StatusType
					.valueOf(rs.getString(ProcessLogTableSchemaUtils.processStatusColumn.getName().getStringValue()));
			
			if (!status.isDone()) {
				this.getProcessLogTableAndProcessPerformerManager().rollbackProcess(UID);
			}
		}
		
		//TODO
		//more validations
	}
	
	
	/**
	 * every time this method is invoked, a new {@link SimpleCFDGraphBuilder} will be created and the CFD graph will be built from scratch;
	 */
	@Override
	public SimpleCFDGraph getCFDGraph() throws SQLException {
		Set<CompositionFunctionID> initialCFIDSet = this.getHasIDTypeManagerController().getCompositionFunctionManager().retrieveAll().keySet();
		SimpleCFDGraphBuilder builder = new SimpleCFDGraphBuilder(this, initialCFIDSet);
		return builder.getBuiltGraph();
	}
	
	/**
	 * every time this method is invoked, a new SimpleDOSGraphBuilder will be created and the DOS graph will be built from scratch;
	 */
	@Override
	public SimpleDOSGraph getDOSGraph() throws SQLException {
		Set<MetadataID> inducingMetadataIDSet = this.getHasIDTypeManagerController().getMetadataManager().retrieveAll().keySet();
		
		SimpleDOSGraphBuilder builder = new SimpleDOSGraphBuilder(this, inducingMetadataIDSet);
		
		return builder.getBuiltGraph();
	}
	
	
	@Override
	public void retrieveNotes() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * 
	 */
	@Override
	public void disconnect() throws SQLException {
		if(this.projectDBCon==null) {
			return;
		}else {
			this.projectDBCon.close();
			
			DerbyDBUtils.shutDownEmbeddedDB(this.getDBParentPath(), VisProjectDBUtils.DB_DIR_NAME);
		}
		
	}
	
	@Override
	public VisProjectHasIDTypeManagerController getHasIDTypeManagerController() {
		return this.hasIDTypeManagerController;
	}
	
	@Override
	public ProcessLogTableAndProcessPerformerManager getProcessLogTableAndProcessPerformerManager() {
		return this.processLogTableAndProcessPerformerManager;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rollbackFinishedProcess(int processUID) throws SQLException {
		ProcessLogTableRow row = this.getProcessLogTableAndProcessPerformerManager().retrieveRow(processUID);
		if(row==null) {
			throw new VisframeException("given process UID is not found in process log table!");
		}
		
		
		if(!row.getStatus().equals(StatusType.FINISHED)) {
			throw new VisframeException("given process's status is not FINISHED!");
		}
		
		
		if(row.getReproduced()!=null && row.getReproduced())
			throw new VisframeException("cannot start a new roll back from a reproduced process!");
		
		
		this.getProcessLogTableAndProcessPerformerManager().rollbackProcess(processUID);
	}
	
}
