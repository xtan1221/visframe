package context.project.process.simple;

import java.sql.SQLException;
import java.util.Map;
import basic.lookup.project.type.udt.VisProjectVisInstanceManager;
import context.project.VisProjectDBContext;
import context.project.process.SimpleProcessPerformer;
import context.project.process.logtable.StatusType;
import context.project.process.logtable.VfIDCollection;
import exception.VisframeException;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroup;
import function.group.CompositionFunctionGroupID;
import function.group.ShapeCFG;
import function.variable.input.recordwise.type.CFGTargetInputVariable;
import visinstance.NativeVisInstance;
import visinstance.VisInstance;
import visinstance.VisInstanceID;
import visinstance.VisSchemeBasedVisInstance;

/**
 * =========111720-update
 * 
 * process performer that insert a VisInstance into the VisInstance management table;
 * 
 * applicable to both NativeVisInstance and VisSchemeBasedVisInstance!!
 * 
 * @author tanxu
 *
 */
public class VisInstanceInserter extends SimpleProcessPerformer<VisInstance, VisInstanceID, VisProjectVisInstanceManager>{	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param targetEntity
	 */
	public VisInstanceInserter(VisProjectDBContext hostVisProjectDBContext,VisInstance targetEntity) {
		super(hostVisProjectDBContext, hostVisProjectDBContext.getHasIDTypeManagerController().getVisInstanceManager(), targetEntity);
		
	}
	
	
	/////////////////////////////////////////
	/**
	 * 122220-update
	 * TODO
	 * for NativeVisInstance
	 * 		1. for each core ShapeCFG
	 * 			1. Check existence of core ShapeCFGs
	 * 			2. Check if all mandatory properties of the core ShapeCFGs are assigned to a CF
	 * 	 		note that when build NativeVisInstance, only ShapeCFGs with all mandatory properties assigned to a CF are allowed to be included in core ShapeCFGs set(101020-update);
	 * 		
	 * 		2. check if every CompositionFunction of core ShapeCFGs with one or more mandatory targets are included in the {@link VisInstance#getCoreShapeCFGCFIDSet()};
	 * 		
	 * 		3. (skipped)for each target of all CompositionFunctions on the CF dependency graph employed by one or more {@link CFGTargetInputVariable}, check if it is assigned to a {@link CompositionFunction} or not
	 * 			the targets of a CompositionFunction is obtained by {@link CompositionFunctionGroup#getMandatoryTargetNameSet()} of the host CompositionFunctionGroup;
	 *			need to build the CFD graph first; 
	 *
	 * 			note that it is required for a CFG target to be assigned to a CompositionFunction so that is can be used to make {@link CFGTargetInputVariable};
	 * 			thus constraint 2 should be trivial since it should be directly checked in CompositionFunctionInserter thus should be removed(101020-update);
	 * 		
	 * 		4. check if there is already a VisInstance with the same set of core shapeCFGs and the same set of core ShapeCFG's CFs
	 * 			note that it is allowed to have multiple VisInstances with the same set of core ShapeCFGs, but at least one CF of such ShapeCFGs should be different;
	 * 			this may happen if there are more CFs created for the non-mandatory unassigned targets of the core ShapeCFGs and included in a new VisInstance;(101020-update);
	 * 				? allowed 
	 * 				TODO during building the VisInstance, allow user to check if there is already an existing one with the same set of core ShapeCFG and included CFs;
	 * 
	 * for VisSchemeBasedVisInstance
	 * 		1. check the existence of the VisSchemeAppliedArchiveReproducedAndInsertedInstanceID in the management table;
	 * 
	 * 
	 * for both NativeVisInstance and VisSchemeBasedVisInstance
	 * 		check if the UID of the VisInstance is already existing in the management table;
	 * 
	 */
	@Override
	public void checkConstraints() throws SQLException {
		if(this.getProcessEntity() instanceof NativeVisInstance) {
			NativeVisInstance nvi = (NativeVisInstance)this.getProcessEntity();
			
			//1
			for(CompositionFunctionGroupID coreShapeCfgID:nvi.getCoreShapeCFGIDSet()) {
				if(!this.getHasIDTypeManagerController().getCompositionFunctionGroupManager().checkIDExistence(coreShapeCfgID)) {
					throw new VisframeException("one core shape CFG is not found in the management table");
				}
				
				CompositionFunctionGroup cfg = this.getHasIDTypeManagerController().getCompositionFunctionGroupManager().lookup(coreShapeCfgID);
				
				if(!(cfg instanceof ShapeCFG)) {
					throw new VisframeException("non ShapeCFG is included as core shape CFG");
				}
				
				ShapeCFG shapeCFG = (ShapeCFG)cfg;
				
				if(!this.getHasIDTypeManagerController().getCompositionFunctionManager().findSetOfMandatoryTargetNotAssignedToCF(shapeCFG).isEmpty()) {
					throw new VisframeException("at least one of the mandatory targets of the shape type of the ShapeCFG is not assigned to any CF");
				}
			}
			
			//3
			Map<VisInstanceID, VisInstance> idMap = this.getProcessTypeManager().retrieveAll();
			for(VisInstanceID id:idMap.keySet()) {
				if(idMap.get(id).getCoreShapeCFGCFIDSet().equals(nvi.getCoreShapeCFGCFIDSet()))
					throw new VisframeException("existing VisInstance with the same set of core shape CFGs AND same set of core Shape CFG's CFs is found in the management table!");
			}
			
		}else {//VisSchemeBasedVisInstance
			VisSchemeBasedVisInstance vsbvi = (VisSchemeBasedVisInstance) this.getProcessEntity();
			
			if(!this.getHostVisProjectDBContext().getHasIDTypeManagerController().getVisSchemeAppliedArchiveReproducedAndInsertedInstanceManager().checkIDExistence(vsbvi.getVisSchemeAppliedArchiveReproducedAndInsertedInstanceID())) {
				throw new VisframeException("VisSchemeAppliedArchiveReproducedAndInsertedInstance of the VisSchemeBasedVisInstance is not found in the management table!");
			}
		}
		
		
		//4
		if(this.getProcessTypeManager().checkIDExistence(this.getID())) {
			throw new VisframeException("ID of the VisInstance to be inserted already exists in the management table");
		}
		
	}
	
	
	/**
	 * 122220-update
	 * 1. Build the baseProcessIDSet
	 * 		for both NativeVisInstance and VisSchemeBasedVisInstance types
	 * 			only add the INSERTION_PROCESS_ID of included CFs of the core ShapeCFGs to the base process set instead of add the INSERTION_PROCESS_ID of every CF on the CFD graph to the baseProcessIDSet!!!!
	 * 			the dependency from the NativeVisInstance to the core ShapeCFGs are indirectly reflected as
	 * 			NativeVisInstance ==> CF of the core ShapeCFG ==> core ShapeCFG;
	 * 			also for those CF on the induced CFD graph, the dependency from the NativeVisInstance to them are indirectly reflected (trivial)
	 * 		
	 * 2. insert into management table
	 * 
	 * 3. invoke {@link #postprocess()}
	 * 
	 * 4. return {@link StatusType#FINISHED}
	 * 
	 */
	@Override
	public StatusType call() throws SQLException {
		//1
		this.baseProcessIDSet = new VfIDCollection();
		
		//only add the INSERTION_PROCESS_ID of included CFs of the core ShapeCFGs to the base process set
		//the dependency from the VisInstance to the core ShapeCFGs are indirectly reflected as
		//VisInstance ==> CF of the core ShapeCFG ==> core ShapeCFG;
		//also for those CF on the induced CFD graph, the dependency from the VisInstance to them are indirectly reflected (trivial)
		//for VisSchemeBasedVisInstance, their dependency on the VisSchemeAppliecArchiveReproduceAndInsertedInstance will be reflected by the dependency from the reproduced core ShapeCFGs/CFs to the VisSchemeAppliecArchiveReproduceAndInsertedInstance
		for(CompositionFunctionID cfID:this.getProcessEntity().getCoreShapeCFGCFIDSet()) {
			this.baseProcessIDSet.addID(this.getHasIDTypeManagerController().getCompositionFunctionManager().retrieveRow(cfID).getInsertionProcessID());
		}
		
		//2
		this.getProcessTypeManager().insert(this.getProcessEntity());
		
		//3
		this.postprocess();
		
		//4
		return StatusType.FINISHED;
	}


}
