package context.scheme.appliedarchive;

import java.io.IOException;
import java.sql.SQLException;

import basic.lookup.project.type.udt.VisProjectVisSchemeAppliedArchiveManager;
import context.project.VisProjectDBContext;
import context.project.process.SimpleProcessPerformer;
import context.project.process.logtable.StatusType;
import context.project.process.logtable.VfIDCollection;
import context.scheme.appliedarchive.mapping.GenericGraphMapping;
import context.scheme.appliedarchive.mapping.MetadataMapping;
import dependency.dos.integrated.IntegratedDOSGraphNode;
import exception.VisframeException;

/**
 * SimpleProcessPerformer class that insert a built VisSchemeApplierArchive into the VisSchemeApplierArchive management table of the host VisProjectDBContext;
 * 
 * @author tanxu
 *
 */
public class VisSchemeAppliedArchiveInserter extends SimpleProcessPerformer<VisSchemeAppliedArchive, VisSchemeAppliedArchiveID, VisProjectVisSchemeAppliedArchiveManager>{
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param targetEntity
	 */
	public VisSchemeAppliedArchiveInserter(
			VisProjectDBContext hostVisProjectDBContext,
			VisSchemeAppliedArchive targetEntity) {
		super(hostVisProjectDBContext, hostVisProjectDBContext.getHasIDTypeManagerController().getVisSchemeAppliedArchiveManager(), targetEntity);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 1. check the applied VisScheme exists
	 * 2. check Metadata selected to make mapping for solution set on trimmed integrated DOS graph exists;
	 * 		check the existence of the columns of the source record Metadata selected in the mapping?? not necessary??;
	 * 3. check UID of this VisSchemeApplierArchive not already existing;
	 */
	@Override
	public void checkConstraints() throws SQLException {
		if(!this.getHasIDTypeManagerController().getVisSchemeManager().checkIDExistence(this.getProcessEntity().getAppliedVisSchemeID())) {
			throw new VisframeException("applied VisSchemeID is not found in the VisScheme management table!");
		}
		
		for(IntegratedDOSGraphNode node:this.getProcessEntity().getSelectedSolutionSetNodeMappingMap().keySet()){
			MetadataMapping mapping = this.getProcessEntity().getSelectedSolutionSetNodeMappingMap().get(node);
			if(!this.getHasIDTypeManagerController().getMetadataManager().checkIDExistence(mapping.getSourceMetadataID())) {
				throw new VisframeException("at least one source MetadataID of MetadataMapping for selected Solution set is not found in the Metadata management table!");
			}
			
			if(mapping instanceof GenericGraphMapping) {
				GenericGraphMapping ggmapping = (GenericGraphMapping)mapping;
				if(ggmapping.getSourceNodeRecordMetadataID()!=null) {
					if(!this.getHasIDTypeManagerController().getMetadataManager().checkIDExistence(ggmapping.getSourceNodeRecordMetadataID())) {
						throw new VisframeException("at least one source MetadataID of MetadataMapping for selected Solution set is not found in the Metadata management table!");
					}
				}
				if(ggmapping.getSourceEdgeRecordMetadataID()!=null) {
					if(!this.getHasIDTypeManagerController().getMetadataManager().checkIDExistence(ggmapping.getSourceEdgeRecordMetadataID())) {
						throw new VisframeException("at least one source MetadataID of MetadataMapping for selected Solution set is not found in the Metadata management table!");
					}
				}
			}
		}
		
		if(this.getHasIDTypeManagerController().getVisSchemeAppliedArchiveManager().checkIDExistence(this.getProcessEntity().getID())){
			throw new VisframeException("ID of the target VisSchemeApplierArchive is already existing in the management table!");
		}
	}
	
	
	/**
	 * 1. Build the baseProcessIDSet
	 * 		insertion process of the applied VisScheme
	 * 		insertion process of Metadata selected to make mapping for solution set on trimmed integrated DOS graph;
	 * 			note that for GenericGraphMapping, only need to add the insertion process of the generic Graph metadata
	 * 				this is because the component metadata of the generic graph metadata should have the same insertion process;
	 * 2. insert
	 * 
	 * 3. return {@link StatusType#FINISHED}
	 */
	@Override
	public StatusType call() throws SQLException, IOException {
		//1
		this.baseProcessIDSet = new VfIDCollection();
		
		//insertion process of the applied VisScheme
		this.baseProcessIDSet.addID(this.getHasIDTypeManagerController().getVisSchemeManager().retrieveRow(this.getProcessEntity().getAppliedVisSchemeID()).getInsertionProcessID());
		//insertion process of Metadata selected to make mapping for solution set on trimmed integrated DOS graph;
		for(IntegratedDOSGraphNode node:this.getProcessEntity().getSelectedSolutionSetNodeMappingMap().keySet()){
			MetadataMapping mapping = this.getProcessEntity().getSelectedSolutionSetNodeMappingMap().get(node);
			this.baseProcessIDSet.addID(this.getHasIDTypeManagerController().getMetadataManager().retrieveRow(mapping.getSourceMetadataID()).getInsertionProcessID());
		}
		
		//2
		this.getProcessTypeManager().insert(this.getProcessEntity());
		
		this.postprocess();
		
		//3
		return StatusType.FINISHED;
	}

}
