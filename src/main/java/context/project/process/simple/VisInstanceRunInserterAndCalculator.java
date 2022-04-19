package context.project.process.simple;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import basic.lookup.project.type.VisframeUDTManagementProcessRelatedTableColumnFactory;
import basic.lookup.project.type.udt.VisProjectCFTargetValueTableRunManager;
import basic.lookup.project.type.udt.VisProjectVisInstanceRunManager;
import context.project.VisProjectDBContext;
import context.project.process.SimpleProcessPerformer;
import context.project.process.logtable.ProcessLogTableAndProcessPerformerManager;
import context.project.process.logtable.ProcessLogTableSchemaUtils;
import context.project.process.logtable.StatusType;
import context.project.process.logtable.VfIDCollection;
import dependency.cfd.CFDEdgeImpl;
import dependency.cfd.CFDNodeImpl;
import exception.VisframeException;
import function.composition.CompositionFunctionID;
import visinstance.run.VisInstanceRun;
import visinstance.run.VisInstanceRunID;
import visinstance.run.calculation.IndependentFIVTypeIDStringValueMap;
import visinstance.run.calculation.VisInstanceRunPreprocessor;
import visinstance.run.calculation.function.composition.CFTargetValueTableRun;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunImpl;


/**
 * base class that insert and carry out all the calculations of a VisInstanceRun in a host VisProjectDBContext;
 * 
 * 1. update VisInstanceRun management table 
 * 2. update CFTargetValueTableRun management table
 * 3. generate CFTarget value tables in the VALUE schema
 * 4. create PiecewiseFunctionOutputIndex table and temporary output variable table in the CALCULATION schema to facilitate the collection and remove them after calculation is done;
 * 
 * @author tanxu
 * 
 */
public class VisInstanceRunInserterAndCalculator extends SimpleProcessPerformer<VisInstanceRun, VisInstanceRunID, VisProjectVisInstanceRunManager>{
	
	//////////////////////////////
	/**
	 * 
	 */
	private VisInstanceRunPreprocessor visInstanceRunPreprocessor;
	
	/**
	 * store the map from calculated CompositionFunctionID(both previously calculated and newly calculated) on the CFD graph to the {@link CFTargetValueTableRun}
	 */
	private Map<CompositionFunctionID, CFTargetValueTableRun> calculatedCFIDTargetValueTableRunMap;
	
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param visInstanceID
	 * @param runUID
	 * @param simpleCFDGraph
	 * @param assignedCFDGraphIndependetFIVStringValueMap
	 * @throws SQLException 
	 */
	public VisInstanceRunInserterAndCalculator(
			VisProjectDBContext hostVisProjectDBContext,
			VisInstanceRun visInstanceRun
			) throws SQLException{
		
		super(hostVisProjectDBContext, hostVisProjectDBContext.getHasIDTypeManagerController().getVisInstanceRunManager(), visInstanceRun);
		
		
		//
		this.visInstanceRunPreprocessor = new VisInstanceRunPreprocessor(this.getHostVisProjectDBContext(), this.getProcessEntity());
	}
	
	/**
	 * @return the calculatedCFIDTargetValueTableRunMap
	 */
	public Map<CompositionFunctionID, CFTargetValueTableRun> getCalculatedCFIDTargetValueTableRunMap() {
		return calculatedCFIDTargetValueTableRunMap;
	}


	/**
	 * @return the visInstanceRunPreprocessor
	 */
	public VisInstanceRunPreprocessor getVisInstanceRunPreprocessor() {
		return visInstanceRunPreprocessor;
	}


	///////////////////////////////////////////
	/**
	 * 1. check if the VisInstance exists in the host VisProjectDBContext;
	 * 
	 * 2. check if the VisInstanceRun is already calculated
	 * 		if yes, throw exception;
	 * 		
	 */
	@Override
	public void checkConstraints() throws SQLException{
		//1
		if(this.getHasIDTypeManagerController().getVisInstanceManager().lookup(this.getProcessEntity().getVisInstanceID())==null) {
			throw new VisframeException("VisInstance of the VisInstanceRun is not found in the host VisProjectDBContext!");
		}
		
		//2
		if(this.getProcessTypeManager().isVisInstanceRunAlreadyCalculated(this.getProcessEntity().getVisInstanceID(), this.getProcessEntity().getCFDGraphIndependetFIVStringValueMap())) {
			throw new VisframeException("target VisInstanceRun is already calculated!");
		}
	}
	
	
	
	/**
	 * 1. initialize and build the {@link #baseProcessIDSet};
	 * 		1. Insertion process of host VisInstance
	 * 		note that involved CFTargetValueTableRuns are not counted as base process, rather they are put in the {{@link VisframeUDTManagementProcessRelatedTableColumnFactory#involvedCfTargetValueTableRunIDSetColumn}} column
	 * 
	 * 2. calculate each CF on the CFD
	 * 		1. lookup the CFTargetValueTableRun object with the CFDGraphIndependetFIVStringValueMap for f
	 * 				{@link VisProjectCFTargetValueTableRunManager#lookupRunWithSameCFDGraphIndependetFIVStringValueMapAlreadyCalculated(CompositionFunctionID, IndependentFIVTypeIDStringValueMap)}
	 * 		
	 * 		2. check the returned value
	 * 			if not null, 
	 * 				add the previously calculated CFTargetValueTableRun's ValueTableSchameID of f to {@link #processedCFIDTargetValueTableSchemaIDMap};
	 * 			else// the CFTargetValueTableRun is not calculated yet;
	 * 				build a new {@link AbstractCFTargetValueTableRunCalculator} to 
	 * 					build and insert the {@link CFTargetValueTableRun} into the CFTargetValueTableRun management table;
	 * 
	 * 		(removed)4. add the involved CFTargetValueTableSchemaID to the PROCESS LOG table by invoking {@link ProcessLogTableAndProcessPerformerManager#addToInvolvedCFTargetValueTableRunIDSetColumnOfCurrentlyRunningProcess(CFTargetValueTableRunID)};
	 * 			see {@link ProcessLogTableSchemaUtils#involvedCfTargetValueTableRunIDSetColumn}
	 * 		!!!!!note that this is done in the {@link VisProjectCFTargetValueTableRunManager#addEmployerVisInstanceRunID(CFTargetValueTableRunID, VisInstanceRunID)} method		
	 * 
	 * 
	 * 		5. go to the next CF
	 * 
	 * 3. Insert the VisInstanceRun into the CF management table;
	 * 
	 * 4. return {@link StatusType#FINISHED};
	 * @throws SQLException 
	 */
	@Override
	public StatusType call() throws SQLException{
		//1
		this.baseProcessIDSet = new VfIDCollection();
		this.baseProcessIDSet.addID(this.getProcessEntity().getVisInstanceID());//insertion process id of a VisInstance is its own VisInstanceID
		
		
		//2
		//prepare
		Map<CFDNodeImpl, Integer> nodeUnprocessedDependedNodeNumMap = new HashMap<>(); //for all CFs on the CFD graph
		Set<CFDNodeImpl> nodeSetReadyToBeProcessed = new HashSet<>(); //with all directly depended CFs processed (if any)
		SimpleDirectedGraph<CFDNodeImpl,CFDEdgeImpl> underlyingCFDGraph = this.getVisInstanceRunPreprocessor().getCFDGraph().getUnderlyingGraph();
		
		underlyingCFDGraph.vertexSet().forEach(v->{
			int dependedNodeNum = underlyingCFDGraph.outDegreeOf(v);
			nodeUnprocessedDependedNodeNumMap.put(v, dependedNodeNum);
			
			if(dependedNodeNum==0)
				nodeSetReadyToBeProcessed.add(v);
		});
		
		//iteratively process each CF
		this.calculatedCFIDTargetValueTableRunMap = new HashMap<>();
		while(!nodeSetReadyToBeProcessed.isEmpty()) {
			CFDNodeImpl node = nodeSetReadyToBeProcessed.iterator().next();
			
			//check if the CFTargetValueTableRun is already calculated for the cfID with the corresponding IndieFIVType values;
			CFTargetValueTableRun run = this.getHasIDTypeManagerController().getCFTargetValueTableRunManager().lookupRun(
					node.getCFID(), this.getVisInstanceRunPreprocessor().getCfIDIndependetFIVTypeStringValueMapMap().get(node.getCFID()));
			if(run!=null) {//already calculated
				//simply add to the employer visinstanceRun id set of the CFTargetValueTableRun;
				this.getHasIDTypeManagerController().getCFTargetValueTableRunManager().addEmployerVisInstanceRunID(run.getID(), this.getID());
				
			}else {//not calculated yet
				//note that the following order should be obeyed
				//1. build the CFTargetValueTableRun
						//1. create and insert the value table schema into host VisProjectDBContext
				//2. insert the CFTargetValueTableRun into the management table;
				//3. calculate and populate the value tables;
				//this order will take care of roll back caused by error occurred during the process of calculation and populating of the value tables;
				
				
				//build the CFTargetValueTableRunCalculator 
				//this WILL build the needed value table schema but NOT insert them into the host VisProjectDBContext;
				//the built CFTargetValueTable schema is needed to build the CFTargetValueTableRunImpl
				CFTargetValueTableRunCalculator calculator = 
						new CFTargetValueTableRunCalculator(
								this,
								this.getHasIDTypeManagerController().getCompositionFunctionManager().lookup(node.getCFID()),
								this.getVisInstanceRunPreprocessor().getCfIDIndependetFIVTypeStringValueMapMap().get(node.getCFID())
								);
				
				//create the newly created CFTargetValueTableRun 
				run =  new CFTargetValueTableRunImpl(
						calculator.getRunUID(),
						node.getCFID(),
						this.getVisInstanceRunPreprocessor().getCfIDIndependetFIVTypeStringValueMapMap().get(node.getCFID()),
						calculator.getCFTargetValueTableInitializer().getValueTableSchema(),
						calculator.getCFTargetValueTableInitializer().getTargetNameColNameMap()
						);
				
				//insert CFTargetValueTableRun into the CFTargetValueTableRun management table; 
				this.getHasIDTypeManagerController().getCFTargetValueTableRunManager().insert(run);
				
				//add to the employer VisInstanceRun ID set column of the CFTargetValueTableRun management table;
				//this will also add the CFTargetValueTableRunID into the InvolvedCFTargetValueTableRunIDSet column of the process log table;
				this.getHasIDTypeManagerController().getCFTargetValueTableRunManager().addEmployerVisInstanceRunID(run.getID(), this.getID());
				
				
				//calculate the value table of the CompositionFunction in the host VisProjectDBContext
				calculator.perform();
			}
			
			//update calculatedCFIDTargetValueTableRunMap
			this.calculatedCFIDTargetValueTableRunMap.put(node.getCFID(), run);
			
			
			//post process
			//update the nodeUnprocessedDependedNodeNumMap and nodeSetReadyToBeProcessed
			underlyingCFDGraph.incomingEdgesOf(node).forEach(e->{
				CFDNodeImpl dependingNode = underlyingCFDGraph.getEdgeSource(e);
				int unprocessedDependedNodeNum = nodeUnprocessedDependedNodeNumMap.get(dependingNode)-1;
				nodeUnprocessedDependedNodeNumMap.put(dependingNode, unprocessedDependedNodeNum);
				
				if(unprocessedDependedNodeNum==0)
					nodeSetReadyToBeProcessed.add(dependingNode);
			});
			
			
			nodeSetReadyToBeProcessed.remove(node);
		}
		
		//
		//insert the VisInstanceRun into management table
		this.getProcessTypeManager().insert(this.getProcessEntity());
		
		
		//
		this.postprocess();
		
		
		
		//
		return StatusType.FINISHED;
	}

}
