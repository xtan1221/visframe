package basic.lookup.project;

import java.util.HashMap;
import java.util.Map;

import basic.lookup.HasID;
import basic.lookup.ID;
import basic.lookup.PrimaryKeyID;
import basic.lookup.VisframeUDT;
import basic.lookup.project.type.VisframeUDTTypeManagerBase;
import basic.lookup.project.type.table.VisProjectCFTargetValueTableSchemaManager;
import basic.lookup.project.type.table.VisProjectDataTableSchemaManager;
import basic.lookup.project.type.table.VisProjectPiecewiseFunctionIndexIDOutputIndexValueTableSchemaManager;
import basic.lookup.project.type.table.VisProjectTemporaryOutputVariableValueTableSchemaManager;
import basic.lookup.project.type.udt.VisProjectCFTargetValueTableRunManager;
import basic.lookup.project.type.udt.VisProjectCompositionFunctionGroupManager;
import basic.lookup.project.type.udt.VisProjectCompositionFunctionManager;
import basic.lookup.project.type.udt.VisProjectDataImporterManager;
import basic.lookup.project.type.udt.VisProjectFileFormatManager;
import basic.lookup.project.type.udt.VisProjectIndependentFreeInputVariableTypeManager;
import basic.lookup.project.type.udt.VisProjectMetadataManager;
import basic.lookup.project.type.udt.VisProjectOperationManager;
import basic.lookup.project.type.udt.VisProjectVisInstanceManager;
import basic.lookup.project.type.udt.VisProjectVisInstanceRunLayoutConfigurationManager;
import basic.lookup.project.type.udt.VisProjectVisInstanceRunManager;
import basic.lookup.project.type.udt.VisProjectVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager;
import basic.lookup.project.type.udt.VisProjectVisSchemeAppliedArchiveManager;
import basic.lookup.project.type.udt.VisProjectVisSchemeManager;
import context.project.VisProjectDBContext;

/**
 * factory class that maintains a singleton object for each of the manager class for a visProjectDBConnection of a running VisProjectDBContext
 * @author tanxu
 * 
 */
public class VisProjectHasIDTypeManagerControllerImpl implements VisProjectHasIDTypeManagerController{
	
	
	private final VisProjectFileFormatManager fileFormatManager;
	private final VisProjectDataImporterManager dataImporterManager;
	private final VisProjectOperationManager operationManager;
	private final VisProjectMetadataManager metadataManager;
	private final VisProjectCompositionFunctionGroupManager compostionFunctionGroupManager;
	private final VisProjectCompositionFunctionManager compostionFunctionManager;
	private final VisProjectIndependentFreeInputVariableTypeManager independentFreeInputVariableTypeManager;
	private final VisProjectVisSchemeManager visSchemeManager;
	private final VisProjectVisSchemeAppliedArchiveManager visSchemeApplierArchiveManager;
	private final VisProjectVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager visSchemeApplierArchiveInstanceManager;
	private final VisProjectVisInstanceManager visInstanceManager;
	private final VisProjectVisInstanceRunManager visInstanceRunManager;
	private final VisProjectCFTargetValueTableRunManager CFTargetValueTableRunManager;
	private final VisProjectVisInstanceRunLayoutConfigurationManager visInstanceRunLayoutConfigurationManager;
	
	private final VisProjectDataTableSchemaManager dataTableSchemaManager;
	private final VisProjectCFTargetValueTableSchemaManager CFTargetValueTableSchemaManager;
	private final VisProjectPiecewiseFunctionIndexIDOutputIndexValueTableSchemaManager piecewiseFunctionIndexIDOutputIndexValueTableSchemaManager;
	private final VisProjectTemporaryOutputVariableValueTableSchemaManager temporaryOutputVariableTableSchemaManager;
	
	
	//////////////////
	private Map<Class<? extends ID<? extends HasID>>, VisProjectHasIDTypeManager<?, ?>> IDTypeManagerMap;
	
	private Map<Class<? extends PrimaryKeyID<? extends VisframeUDT>>, VisframeUDTTypeManagerBase<?, ?>> visframeUDTTypeManagerMap;
	
	/**
	 * constructor
	 * @param visProjectDBConnection
	 */
	public VisProjectHasIDTypeManagerControllerImpl(
			VisProjectDBContext projectDBContext
			){
		
		this.fileFormatManager = new VisProjectFileFormatManager(projectDBContext);
		this.dataImporterManager = new VisProjectDataImporterManager(projectDBContext);
		this.operationManager = new VisProjectOperationManager(projectDBContext);
		this.metadataManager = new VisProjectMetadataManager(projectDBContext);
		this.compostionFunctionGroupManager = new VisProjectCompositionFunctionGroupManager(projectDBContext);
		this.compostionFunctionManager = new VisProjectCompositionFunctionManager(projectDBContext);
		this.independentFreeInputVariableTypeManager = new VisProjectIndependentFreeInputVariableTypeManager(projectDBContext);
		this.visSchemeManager = new VisProjectVisSchemeManager(projectDBContext);
		this.visSchemeApplierArchiveManager = new VisProjectVisSchemeAppliedArchiveManager(projectDBContext);
		this.visSchemeApplierArchiveInstanceManager = new VisProjectVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager(projectDBContext);
		this.visInstanceManager = new VisProjectVisInstanceManager(projectDBContext);
		this.visInstanceRunManager = new VisProjectVisInstanceRunManager(projectDBContext);
		this.CFTargetValueTableRunManager = new VisProjectCFTargetValueTableRunManager(projectDBContext);
		this.visInstanceRunLayoutConfigurationManager = new VisProjectVisInstanceRunLayoutConfigurationManager(projectDBContext);
		
		this.dataTableSchemaManager = new VisProjectDataTableSchemaManager(projectDBContext);
		this.CFTargetValueTableSchemaManager = new VisProjectCFTargetValueTableSchemaManager(projectDBContext);
		this.piecewiseFunctionIndexIDOutputIndexValueTableSchemaManager = new VisProjectPiecewiseFunctionIndexIDOutputIndexValueTableSchemaManager(projectDBContext);
		this.temporaryOutputVariableTableSchemaManager = new VisProjectTemporaryOutputVariableValueTableSchemaManager(projectDBContext);
	}
	
	
	////////////////////////////////////////
	
	@Override
	public Map<Class<? extends ID<? extends HasID>>, VisProjectHasIDTypeManager<?, ?>> getIDTypeManagerMap() {
		if(this.IDTypeManagerMap==null) {
			this.IDTypeManagerMap = new HashMap<>();
			
			this.IDTypeManagerMap.putAll(this.getUDTTypeManagerMap());
			
			
			this.IDTypeManagerMap.put(this.getDataTableSchemaManager().getIDType(), this.getDataTableSchemaManager());
			this.IDTypeManagerMap.put(this.getCFTargetValueTableSchemaManager().getIDType(), this.getCFTargetValueTableSchemaManager());
			this.IDTypeManagerMap.put(this.getPiecewiseFunctionIndexIDOutputIndexValueTableSchemaManager().getIDType(), this.getPiecewiseFunctionIndexIDOutputIndexValueTableSchemaManager());
			this.IDTypeManagerMap.put(this.getTemporaryOutputVariableValueTableSchemaManager().getIDType(), this.getTemporaryOutputVariableValueTableSchemaManager());
		}
		return this.IDTypeManagerMap;
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public Map<Class<? extends PrimaryKeyID<? extends VisframeUDT>>, VisframeUDTTypeManagerBase<?, ?>> getUDTTypeManagerMap(){
		if(this.visframeUDTTypeManagerMap == null) {
			this.visframeUDTTypeManagerMap = new HashMap<>();
			
			this.visframeUDTTypeManagerMap.put(this.getFileFormatManager().getIDType(), this.getFileFormatManager());
			this.visframeUDTTypeManagerMap.put(this.getDataImporterManager().getIDType(), this.getDataImporterManager());
			this.visframeUDTTypeManagerMap.put(this.getOperationManager().getIDType(), this.getOperationManager());
			this.visframeUDTTypeManagerMap.put(this.getMetadataManager().getIDType(), this.getMetadataManager());
			this.visframeUDTTypeManagerMap.put(this.getCompositionFunctionGroupManager().getIDType(), this.getCompositionFunctionGroupManager());
			this.visframeUDTTypeManagerMap.put(this.getCompositionFunctionManager().getIDType(), this.getCompositionFunctionManager());
			this.visframeUDTTypeManagerMap.put(this.getIndependentFreeInputVariableTypeManager().getIDType(), this.getIndependentFreeInputVariableTypeManager());
			this.visframeUDTTypeManagerMap.put(this.getVisSchemeManager().getIDType(), this.getVisSchemeManager());
			this.visframeUDTTypeManagerMap.put(this.getVisSchemeAppliedArchiveManager().getIDType(), this.getVisSchemeAppliedArchiveManager());
			this.visframeUDTTypeManagerMap.put(this.getVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager().getIDType(), this.getVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager());
			this.visframeUDTTypeManagerMap.put(this.getVisInstanceManager().getIDType(), this.getVisInstanceManager());
			this.visframeUDTTypeManagerMap.put(this.getVisInstanceRunManager().getIDType(), this.getVisInstanceRunManager());
			this.visframeUDTTypeManagerMap.put(this.getCFTargetValueTableRunManager().getIDType(), this.getCFTargetValueTableRunManager());
			this.visframeUDTTypeManagerMap.put(this.getVisInstanceRunLayoutConfigurationManager().getIDType(), this.getVisInstanceRunLayoutConfigurationManager());
			
		}
		return this.visframeUDTTypeManagerMap;
	}
	
	@Override
	public VisProjectFileFormatManager getFileFormatManager() {
		return this.fileFormatManager;
	}
	@Override
	public VisProjectMetadataManager getMetadataManager() {
		return this.metadataManager;
	}
	@Override
	public VisProjectDataImporterManager getDataImporterManager() {
		return this.dataImporterManager;
	}
	@Override
	public VisProjectOperationManager getOperationManager() {
		return this.operationManager;
	}
	@Override
	public VisProjectCompositionFunctionGroupManager getCompositionFunctionGroupManager() {
		return this.compostionFunctionGroupManager;
	}
	@Override
	public VisProjectCompositionFunctionManager getCompositionFunctionManager() {
		return this.compostionFunctionManager;
	}
	@Override
	public VisProjectIndependentFreeInputVariableTypeManager getIndependentFreeInputVariableTypeManager() {
		return this.independentFreeInputVariableTypeManager;
	}
	
	@Override
	public VisProjectVisSchemeManager getVisSchemeManager() {
		return this.visSchemeManager;
	}

	@Override
	public VisProjectVisSchemeAppliedArchiveManager getVisSchemeAppliedArchiveManager() {
		return this.visSchemeApplierArchiveManager;
	}

	@Override
	public VisProjectVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager getVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager() {
		return this.visSchemeApplierArchiveInstanceManager;
	}
	
	@Override
	public VisProjectVisInstanceManager getVisInstanceManager() {
		return this.visInstanceManager;
	}
	@Override
	public VisProjectVisInstanceRunManager getVisInstanceRunManager() {
		return this.visInstanceRunManager;
	}
	@Override
	public VisProjectCFTargetValueTableRunManager getCFTargetValueTableRunManager() {
		return this.CFTargetValueTableRunManager;
	}
	@Override
	public VisProjectVisInstanceRunLayoutConfigurationManager getVisInstanceRunLayoutConfigurationManager() {
		return visInstanceRunLayoutConfigurationManager;
	}
	/////////////////////////////////////////////////
	@Override
	public VisProjectDataTableSchemaManager getDataTableSchemaManager() {
		return this.dataTableSchemaManager;
	}
	@Override
	public VisProjectCFTargetValueTableSchemaManager getCFTargetValueTableSchemaManager() {
		return this.CFTargetValueTableSchemaManager;
	}
	@Override
	public VisProjectPiecewiseFunctionIndexIDOutputIndexValueTableSchemaManager getPiecewiseFunctionIndexIDOutputIndexValueTableSchemaManager() {
		return this.piecewiseFunctionIndexIDOutputIndexValueTableSchemaManager;
	}
	@Override
	public VisProjectTemporaryOutputVariableValueTableSchemaManager getTemporaryOutputVariableValueTableSchemaManager() {
		return this.temporaryOutputVariableTableSchemaManager;
	}

}
