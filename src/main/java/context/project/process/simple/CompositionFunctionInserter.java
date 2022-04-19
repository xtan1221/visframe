package context.project.process.simple;

import java.sql.SQLException;
import java.util.Map;

import basic.SimpleName;
import basic.lookup.project.type.udt.VisProjectCompositionFunctionManager;
import basic.lookup.project.type.udt.VisProjectIndependentFreeInputVariableTypeManager;
import context.project.VisProjectDBContext;
import context.project.process.SimpleProcessPerformer;
import context.project.process.logtable.StatusType;
import context.project.process.logtable.VfIDCollection;
import exception.VisframeException;
import function.component.ComponentFunction;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.evaluator.Evaluator;
import function.evaluator.nonsqlbased.NonSQLQueryBasedEvaluator;
import function.group.CompositionFunctionGroup;
import function.group.CompositionFunctionGroupID;
import function.target.CFGTarget;
import function.variable.independent.IndependentFreeInputVariableType;
import function.variable.independent.IndependentFreeInputVariableTypeID;
import function.variable.input.InputVariable;
import function.variable.input.nonrecordwise.type.FreeInputVariable;
import function.variable.input.nonrecordwise.type.SQLAggregateFunctionBasedInputVariable;
import function.variable.input.recordwise.type.CFGTargetInputVariable;
import function.variable.input.recordwise.type.RecordAttributeInputVariable;
import function.variable.input.recordwise.type.UpstreamValueTableColumnOutputVariableInputVariable;
import metadata.MetadataID;

public class CompositionFunctionInserter extends SimpleProcessPerformer<CompositionFunction, CompositionFunctionID, VisProjectCompositionFunctionManager>{
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param targetEntity
	 */
	public CompositionFunctionInserter(
			VisProjectDBContext hostVisProjectDBContext,
			CompositionFunction targetEntity) {
		super(hostVisProjectDBContext, hostVisProjectDBContext.getHasIDTypeManagerController().getCompositionFunctionManager(), targetEntity);
	}
	
	/**
	 * ==========091320
	 * 1. Check if the host {@link CompositionFunctionGroupID} exists in the CompositionFunctionGroup management table or not;
	 * 
	 * 2. Check whether any assigned {@link CFGTarget}s to the {@link CompositionFunction} 
	 * 		1. are valid and existing for the host {@link CompositionFunctionGroup}
	 * 		2. has already been assigned to any existing {@link CompositionFunction} of the same host {@link CompositionFunctionGroup};
	 * 
	 * 3. check each {@link InputVariable} of each {@link Evaluator} of each {@link ComponentFunction}
	 * 		1. Check the existence of {@link CompositionFunctionGroupID} of every {@link CFGTargetInputVariable}
	 * 			also check if the target is already assigned to a CF or not (if not, invalid!) 101920-update
	 * 
	 * 		2. Check the existence of {@link MetadataID} of record data of every {@link RecordAttributeInputVariable}
	 * 		3. Check the existence of {@link MetadataID} of record data of every {@link SQLAggregateFunctionBasedInputVariable}
	 * 		======
	 * 		4. if the {@link Evaluator} is of {@link NonSQLQueryBasedEvaluator} type
	 * 				if of {@link CFGTargetInputVariable} type, the owner CompositionFunctionGroup of the target must be of the same owner record data of the host CompositionFunctionGroup of this evaluator;
	 * 				if of {@link RecordAttributeInputVariable} type, the record data of the data table must be the same of the owner record data of the host CompositionFunctionGroup of this evaluator;
	 * 				if of {@link UpstreamValueTableColumnOutputVariableInputVariable} type, trivial;
	 * 
	 * 		5. if the {@link Evaluator} is of {@link SQLQueryBasedEvaluator} type
	 * 				trivial
	 * 
	 * 4. check if the {@link IndependentFreeInputVariableType} of each {@link FreeInputVariable} is owned by the inserted {@link CompositionFunction} or not;
	 * 		if yes, do nothing here!!!!
	 * 			but in the {@link #call()} method, need to insert all such {@link IndependentFreeInputVariableType}s first with 
	 * 			{@link VisProjectIndependentFreeInputVariableTypeManager#insert(IndependentFreeInputVariableType)} method
	 * 		if not (owned by a different existing {@link CompositionFunction}), 
	 * 			Check the existence of the owner {@link CompositionFunctionID};
	 * 			check if the {@link IndependentFreeInputVariableType} exist in the IndependentFreeInputVariableType management table;
	 *
	 * 5. check existence of the ID of the {@link CompositionFunction} to be inserted
	 * 		this is equivalent to checking if the index ID of the inserted {@link CompositionFunction} is assigned to an existing {@link CompositionFunction} of the same host {@link CompositionFunctionGroup};
	 */
	@Override
	public void checkConstraints() throws SQLException {
		CompositionFunctionGroup hostCfg = this.getHasIDTypeManagerController().getCompositionFunctionGroupManager().lookup(this.getProcessEntity().getHostCompositionFunctionGroupID());
		
		//1
		if(!this.getHasIDTypeManagerController().getCompositionFunctionGroupManager().checkIDExistence(this.getProcessEntity().getHostCompositionFunctionGroupID())) {
			throw new VisframeException("host CompositionFunctionGroup of the CompositionFunction is not found in the management table");
		}
		
		//2
		Map<SimpleName,CompositionFunctionID> targetNameAssignedCFIDMap = this.getProcessTypeManager().getTargetNameAssignedCFIDMap(this.getProcessEntity().getHostCompositionFunctionGroupID());
		
		for(SimpleName assignedTargetName:this.getProcessEntity().getAssignedTargetNameSet()) {
			if(!hostCfg.getTargetNameMap().keySet().contains(assignedTargetName))
				throw new VisframeException("assigned target name is not found in the host CFG of the inserted CF!");
			if(targetNameAssignedCFIDMap.containsKey(assignedTargetName))
				throw new VisframeException("CFGTarget assigned to the CompositionFunction has been assigned to an existing CompositionFunction of the same host CompositionFunctionGroup!");
		}
		
		
		//3
		for(ComponentFunction cf:this.getProcessEntity().getComponentFunctionSet()) {
			for(Evaluator evaluator:cf.getEvaluatorSet()) {
				for(SimpleName sn:evaluator.getInputVariableAliasNameMap().keySet()) {
					InputVariable iv = evaluator.getInputVariableAliasNameMap().get(sn);
					
					if(iv instanceof CFGTargetInputVariable) {
						
						CFGTargetInputVariable cfgtiv = (CFGTargetInputVariable) iv;
						if(!this.getHasIDTypeManagerController().getCompositionFunctionGroupManager().checkIDExistence(cfgtiv.getTargetCompositionFunctionGroupID())) 
							throw new VisframeException("CompositionFunctionGroup of a CFGTargetInputVariable is not found in management table");
						
						//the target of the CFGTargetInputVariable is not assigned to any CF, invalid!
						if(!this.getHasIDTypeManagerController().getCompositionFunctionManager().getTargetNameAssignedCFIDMap(cfgtiv.getTargetCompositionFunctionGroupID()).containsKey(cfgtiv.getTarget().getName()))
							throw new VisframeException("target of a CFGTargetInputVariable is not assigned to CompositionFunction!");
						
						//the owner CompositionFunctionGroup of the target must be of the same owner record data of the host CompositionFunctionGroup of this evaluator;
						if(evaluator instanceof NonSQLQueryBasedEvaluator) {
							CompositionFunctionGroup targetCfg = this.getHasIDTypeManagerController().getCompositionFunctionGroupManager().lookup(cfgtiv.getTargetCompositionFunctionGroupID());
														
							if(!targetCfg.getOwnerRecordDataMetadataID().equals(hostCfg.getOwnerRecordDataMetadataID()))
								throw new VisframeException("owner record data of CFGTargetInputVariable of NonSqlQueryBasedEvaluator is not the same with the owner record data of the host CFG of the inserted CompositionFuction!");
							
						}
						
					}else if(iv instanceof RecordAttributeInputVariable) {
						RecordAttributeInputVariable rdtciv = (RecordAttributeInputVariable) iv;
						if(!this.getHasIDTypeManagerController().getMetadataManager().checkIDExistence(rdtciv.getTargetRecordDataMetadataID())) {
							throw new VisframeException("RecordDataMetadata of a RecordDataTableColumnInputVariable is not found in management table");
						}
						
						//the record data of the data table must be the same of the owner record data of the host CompositionFunctionGroup of this evaluator;
						if(evaluator instanceof NonSQLQueryBasedEvaluator) {
							if(!hostCfg.getOwnerRecordDataMetadataID().equals(rdtciv.getTargetRecordDataMetadataID()))
								throw new VisframeException("record data id of the RecordDataTableColumnInputVariable of NonSqlQueryBasedEvaluator is not the same with the owner record data of the host CFG of the inserted CompositionFuction!");
						}
						
					}else if(iv instanceof SQLAggregateFunctionBasedInputVariable) {
						SQLAggregateFunctionBasedInputVariable sqliv = (SQLAggregateFunctionBasedInputVariable)iv;
						if(!this.getHasIDTypeManagerController().getMetadataManager().checkIDExistence(sqliv.getTargetRecordMetadataID())) {
							throw new VisframeException("target RecordDataMetadata of a SQLAggregateFunctionBasedInputVariable is not found in management table");
						}
					}else {
						//do nothing
					}
					
				}
			}
		}
		
		
		//4
		for(IndependentFreeInputVariableTypeID ifivtID:this.getProcessEntity().getIndependentFreeInputVariableTypeIDMap().keySet()) {
			if(!ifivtID.getOwnerCompositionFunctionID().equals(this.getProcessEntity().getID())) {//owned by other CompositionFunction
				if(!this.getHasIDTypeManagerController().getCompositionFunctionManager().checkIDExistence(ifivtID.getOwnerCompositionFunctionID())){
					throw new VisframeException("owner CompositionFunction of a IndependentFreeInputVariableType is not found in management table");
				}
				
				if(!this.getHasIDTypeManagerController().getIndependentFreeInputVariableTypeManager().checkIDExistence(ifivtID)) {
					throw new VisframeException("owner CompositionFunction of a IndependentFreeInputVariableType is not found in management table");
				}
			}else {//owned by this CompositionFunction
				//not insert the IndependentFreeInputVariableType here, but in the {@link #call()} method!!!!!!!!!!!!!!!!!!!
			}
		
		}
		
		//5
		if(this.getProcessTypeManager().checkIDExistence(this.getID())) {
			throw new VisframeException("ID of the CompositionFunction to be inserted already exists in the management table");
		}
	}
	
	
	/**
	 * =================091320
	 * 1. Build the baseProcessIDSet
	 * Add the INSERTION_PROCESS_ID of the following into the {@link #baseProcessIDSet}
	 * 		the host CFG to the baseProcessIDSet
	 * 		
	 * 		each depending CF based on CFGTargetInputVariable … //include the CF to which the target is assigned rather than the host CFG (101920-update);
	 * 		each depending record data metadata base on RecordDataColumnInputVariable only if the record data is different from the owner record data of the host CFG!(101920-update)
	 * 		each depending record data metadata based on SQLAggregateFunctionBasedInputVariable only if the record data is different from the owner record data of the host CFG!(101920-update)
	 * 		
	 * 		each depending CFs whose IndependentFreeInputVariableType is used by at least one FreeInputVariable of inserted CF
	 * 
	 * 
	 * 2. check if the CF will result in cycle in CFD graph if added, if yes, throw visframeException
	 * 		{@link VisProjectDBContext#getCFDGraph()}
	 * 		{@link VisProjectCFDGraph#resultInCycle(CompositionFunction)}
	 * 
	 * 3. check if the {@link IndependentFreeInputVariableType} of each {@link FreeInputVariable} is owned by the inserted {@link CompositionFunction} or not;
	 * 		if yes, insert all such {@link IndependentFreeInputVariableType}s first with {@link VisProjectIndependentFreeInputVariableTypeManager#insert(IndependentFreeInputVariableType)} method
	 * 
	 * 4. Insert the CF into the CF management table 
	 * 		this will also add the CF to the CFD graph of the host VisProjectDBContext
	 * 
	 * 
	 * /////////////////////////////////////
	 * 5. return {@link StatusType#FINISHED};
	 */
	@Override
	public StatusType call() throws SQLException {
		//1
		this.baseProcessIDSet = new VfIDCollection();
		//host CFG
		this.baseProcessIDSet.addID(
				this.getHasIDTypeManagerController().getCompositionFunctionGroupManager().retrieveRow(this.getProcessEntity().getHostCompositionFunctionGroupID()).getInsertionProcessID());
		//
		for(InputVariable iv: this.getProcessEntity().getInputVariableSet()) {
			if(iv instanceof CFGTargetInputVariable) {
				CFGTargetInputVariable cfgtiv = (CFGTargetInputVariable) iv;
				//101920-update
				//note that since only target assigned to an CF can be used to make CFGTargetInputVariable, thus owner CF of the CFGTargetInputVariable is directly depending on the CF to which the target is assigned!
				//the dependence from the CF to the host CFG of the target is indirectly reflected as following;
						//CF using the CFGTarget input variable==> CF to which target assigned==> host CFG of the target
				//thus the host CFG of the target should not be added to the base process set;
				CompositionFunctionID targetAssignedCFID = this.getHasIDTypeManagerController().getCompositionFunctionManager().getTargetNameAssignedCFIDMap(cfgtiv.getTargetCompositionFunctionGroupID()).get(cfgtiv.getTarget().getName());
				this.baseProcessIDSet.addID(
						this.getHasIDTypeManagerController().getCompositionFunctionManager().retrieveRow(targetAssignedCFID).getInsertionProcessID());
				
			}else if(iv instanceof RecordAttributeInputVariable) {
				RecordAttributeInputVariable rdtciv = (RecordAttributeInputVariable) iv;
				if(!rdtciv.getTargetRecordDataMetadataID().equals(this.getProcessEntity().getOwnerRecordDataMetadataID())){
					//only add the record data to base process set if it is different from the owner record data of the host CFG
					//this is because the dependency from the CF to the owner record data is indirectly reflected by 
					//CF==> host CFG==> owner record data;
					this.baseProcessIDSet.addID(
							this.getHasIDTypeManagerController().getMetadataManager().retrieveRow(rdtciv.getTargetRecordDataMetadataID()).getInsertionProcessID());
				}
				
			}else if(iv instanceof SQLAggregateFunctionBasedInputVariable) {
				SQLAggregateFunctionBasedInputVariable sqliv = (SQLAggregateFunctionBasedInputVariable)iv;
				if(!sqliv.getTargetRecordMetadataID().equals(this.getProcessEntity().getOwnerRecordDataMetadataID())) {
					//only add the record data to base process set if it is different from the owner record data of the host CFG
					//this is because the dependency from the CF to the owner record data is indirectly reflected by 
					//CF==> host CFG==> owner record data;
					this.baseProcessIDSet.addID(
							this.getHasIDTypeManagerController().getMetadataManager().retrieveRow(sqliv.getTargetRecordMetadataID()).getInsertionProcessID());
				}
			}else if(iv instanceof FreeInputVariable){
				FreeInputVariable fiv = (FreeInputVariable)iv;
				IndependentFreeInputVariableType ifivt = fiv.getIndependentFreeInputVariableType();
				if(!ifivt.getOwnerCompositionFunctionID().equals(this.getProcessEntity().getID())) {
					this.baseProcessIDSet.addID(
							this.getHasIDTypeManagerController().getCompositionFunctionManager().retrieveRow(ifivt.getOwnerCompositionFunctionID()).getInsertionProcessID());
				}
			}else {
				//
			}
			
		}
		
		
		//2TODO
//		if(this.getHostVisProjectDBContext().getCFDGraph().resultInCycle(this.getProcessEntity())){
//			throw new VisframeException("inserting the CompositionFunction will result in cycle in the CFD graph");
//		}
		
		//3
		for(IndependentFreeInputVariableType ifivt:this.getProcessEntity().getOriginalIndependentFreeInputVariableTypeIDMap().values()) {
			this.getHasIDTypeManagerController().getIndependentFreeInputVariableTypeManager().insert(ifivt);
		}
		
		
		//4
		this.getProcessTypeManager().insert(this.getProcessEntity());
		
		
		
		//5
		this.postprocess();
		
		
		
		return StatusType.FINISHED;
	}
}
