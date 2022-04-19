package basic.lookup.project;

import java.sql.SQLException;
import java.util.Map;

import basic.lookup.HasID;
import basic.lookup.ID;
import basic.lookup.PrimaryKeyID;
import basic.lookup.VisframeUDT;
import basic.lookup.project.type.VisframeUDTManagementTableRow;
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
import exception.VisframeException;

/**
 * interface for a delegate class maintaining a singleton object for each of the manager classes and provide a set of access methods to them in a VisProjectDBContext;
 * 
 * VisProjectDBContext's management:
 * 
 * insertion chain is enforced by the corresponding 'performer'
	for example, imported Metadata and their data tables are inserted by the DataImporter;
	thus, the dataImporter is responsible for the integrity of the Metadata and data table;
 
deletion chain
	implemented in the delete() method
	when delete a entity e, trigger deletion of all its depending entities (entities that depends on e) first;

validation chain;
	implemented in the validate method
	when validate an entity e, trigger the validate method of its depended entities (entiteis on which e is depending) first


 * @author tanxu
 * 
 */
public interface VisProjectHasIDTypeManagerController {
	
	/**
	 * return the map from ID type to manager object;
	 * @return
	 */
	Map<Class<? extends ID<? extends HasID>>, VisProjectHasIDTypeManager<?,?>> getIDTypeManagerMap();
	
	/**
	 * return the map from PrimaryKeyID type to the visframe UDT type manager object
	 * @return
	 */
	Map<Class<? extends PrimaryKeyID<? extends VisframeUDT>>, VisframeUDTTypeManagerBase<?, ?>> getUDTTypeManagerMap();
	/**
	 * return the corresponding VisProjectManagerBase to the given ID object;
	 * 
	 * @param id
	 * @return
	 */
	default VisProjectHasIDTypeManager<?,?> getManager(ID<? extends HasID> id){
		return this.getIDTypeManagerMap().get(id.getClass());
		
	}
	
	/**
	 * delete the entity of the given ID from its management table;
	 * @param id
	 * @throws SQLException
	 */
	default void delete(ID<? extends HasID> id) throws SQLException {
		this.getIDTypeManagerMap().get(id.getClass()).delete(id);
	}
	
	
	/**
	 * retrieve the VisframeUDTManagementTableRow from the corresponding management table for the given id;
	 * if the type of the given id does not have management table, throw VisframeException;
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	default VisframeUDTManagementTableRow<?,?> retrieveManagementTableRow(ID<? extends HasID> id) throws SQLException{
		VisProjectHasIDTypeManager<?,?> manager = this.getIDTypeManagerMap().get(id.getClass());
		if(manager.hasManagementTable()) {
			VisframeUDTTypeManagerBase<?,?> visframeUDTTypeManager = (VisframeUDTTypeManagerBase<?,?>) manager;
			
			return visframeUDTTypeManager.retrieveRow(id);
			
		}else {
			throw new VisframeException("cannot retrieve row for ID of HasID type without management table");
		}
		
	}
	
	
	/////////VisframeUDT manager gettors
	VisProjectFileFormatManager getFileFormatManager();
	VisProjectDataImporterManager getDataImporterManager();
	VisProjectOperationManager getOperationManager();
	VisProjectMetadataManager getMetadataManager();
	
	VisProjectCompositionFunctionGroupManager getCompositionFunctionGroupManager();
	VisProjectCompositionFunctionManager getCompositionFunctionManager();
	VisProjectIndependentFreeInputVariableTypeManager getIndependentFreeInputVariableTypeManager();
	
	VisProjectVisSchemeManager getVisSchemeManager();
	VisProjectVisSchemeAppliedArchiveManager getVisSchemeAppliedArchiveManager();
	VisProjectVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager getVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager();
	
	VisProjectVisInstanceManager getVisInstanceManager();
	VisProjectVisInstanceRunManager getVisInstanceRunManager();
	VisProjectCFTargetValueTableRunManager getCFTargetValueTableRunManager();
	VisProjectVisInstanceRunLayoutConfigurationManager getVisInstanceRunLayoutConfigurationManager();
	
	//////////////AbstractRelationalTableSchema
	VisProjectDataTableSchemaManager getDataTableSchemaManager();
	VisProjectCFTargetValueTableSchemaManager getCFTargetValueTableSchemaManager();
	VisProjectPiecewiseFunctionIndexIDOutputIndexValueTableSchemaManager getPiecewiseFunctionIndexIDOutputIndexValueTableSchemaManager();
	VisProjectTemporaryOutputVariableValueTableSchemaManager getTemporaryOutputVariableValueTableSchemaManager();
}
