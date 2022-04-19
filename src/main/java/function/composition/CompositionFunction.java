package function.composition;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import basic.HasNotes;
import basic.SimpleName;
import basic.lookup.VisframeUDT;
import basic.process.ReproduceableProcessType;
import basic.reproduce.Reproducible;
import context.VisframeContext;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.component.ComponentFunction;
import function.component.PiecewiseFunction;
import function.group.CompositionFunctionGroup;
import function.group.CompositionFunctionGroupID;
import function.variable.independent.IndependentFreeInputVariableType;
import function.variable.independent.IndependentFreeInputVariableTypeID;
import function.variable.input.InputVariable;
import function.variable.input.nonrecordwise.type.FreeInputVariable;
import function.variable.input.nonrecordwise.type.SQLAggregateFunctionBasedInputVariable;
import function.variable.input.recordwise.RecordwiseInputVariable;
import function.variable.input.recordwise.type.CFGTargetInputVariable;
import function.variable.input.recordwise.type.RecordAttributeInputVariable;
import function.variable.output.OutputVariable;
import function.variable.output.type.TemporaryOutputVariable;
import metadata.DataType;
import metadata.MetadataID;
import rdb.table.data.DataTableColumnName;
import utils.Pair;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;

/**
 * interface for a 
 * 
 * a rooted tree structure set of {@link ComponentFunction}s that calculate the same set of target of a CompositionFunction
 * 
 * @author tanxu
 *
 */
public interface CompositionFunction extends HasNotes, VisframeUDT, ReproduceableProcessType, Reproducible{
	/**
	 * return the index ID of this {@link CompositionFunction};
	 * note that each {@link CompositionFunction} of the same host {@link CompositionFunctionGroup} should have its own unique ID different from others;
	 * @return
	 */
	int getIndexID();
	
	/**
	 * return the {@link CompositionFunctionGroupID} for host {@link CompositionFunctionGroup}
	 * @return
	 */
	CompositionFunctionGroupID getHostCompositionFunctionGroupID();
	
	/**
	 * return the ID of this CompositionFunction;
	 * 
	 */
	@Override
	default CompositionFunctionID getID() {
		return new CompositionFunctionID(this.getHostCompositionFunctionGroupID(),this.getIndexID());
	}
	
	
	/**
	 * return the {@link MetadataID} of the owner {@link RecordDataMetadata} of the host {@link CompositionFunctionGroup} of this {@link CompositionFunction}
	 * @return
	 */
	MetadataID getOwnerRecordDataMetadataID();
	
	
	/**
	 * return the name set of {@link CFGTarget} of the host {@link CompositionFunctionGroup} assigned to this {@link CompositionFunction};
	 * note that this set should be explicitly assigned to this {@link CompositionFunction} and cannot be empty;
	 * 
	 * all the {@link ComponentFunction}s and their {@link Evaluator}s should be built to calculate these assigned {@link CFGTarget}s!
	 * 
	 * @return
	 */
	Set<SimpleName> getAssignedTargetNameSet();
	
	
	/**
	 * return an ordered list of target names assigned to this CompositionFunction;
	 * 
	 * @return
	 */
	default List<SimpleName> getOrderedListOfAssignedTargetName(){
		List<SimpleName> ret = new ArrayList<>();
		
		for(SimpleName sn:this.getAssignedTargetNameSet()) {
			ret.add(sn);
		}
		
		Collections.sort(ret);
		
		return ret;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/**
	 * return the root {@link ComponentFunction} of this {@link CompositionFunction}
	 * @return
	 */
	ComponentFunction getRootFunction();
	
	/**
	 * return all {@link ComponentFunction} owned by this {@link CompositionFunction}
	 * @return
	 */
	default Set<ComponentFunction> getComponentFunctionSet(){
		Set<ComponentFunction> ret = new LinkedHashSet<>();
		
		ret.add(this.getRootFunction());
		
		ret.addAll(this.getRootFunction().getAllDownstreamComponentFunctionSet());
		
		return ret;
		
	}
	
	/**
	 * return the previous ComponentFunction of the given one;
	 * return null if the given one is the root;
	 * @param cf
	 * @return
	 */
	ComponentFunction getPreviousComponentFunction(ComponentFunction cf);
	////////////////////

	/**
	 * return set of {@link InputVariable} present in {@link ComponentFunction} of this {@link CompositionFunction}
	 * facilitate constructing CFD graph
	 * 
	 * !note that for {@link SQLAggregateFunctionBasedInputVariable} type input variables, their recordwiseInputVariable1 and recordwiseInputVariable2 are not included;
	 * 
	 * @return
	 */
	Set<InputVariable> getInputVariableSet();
	
	
	/**
	 * return set of {@link OutputVariable} present in {@link ComponentFunction} of this {@link CompositionFunction}
	 * @return
	 */
	Set<OutputVariable> getOutputVariableSet();
	
	/**
	 * return the set of {@link FreeInputVariable} present in {@link ComponentFunction} of this {@link CompositionFunction}
	 * @return
	 */
	default Set<FreeInputVariable> getFreeInputVariableSet(){
		Set<FreeInputVariable> ret = new HashSet<>();
		
		this.getInputVariableSet().forEach(e->{
			if(e instanceof FreeInputVariable){
				ret.add((FreeInputVariable)e);
			}
		});
		
		return ret;
	}
	
	/**
	 * return the set of {@link IndependentFreeInputVariableType} employed by one or more {@link FreeInputVariable}s of {@link ComponentFunction} of this {@link CompositionFunction}
	 * @return
	 */
	default Map<IndependentFreeInputVariableTypeID, IndependentFreeInputVariableType> getIndependentFreeInputVariableTypeIDMap(){
		Map<IndependentFreeInputVariableTypeID, IndependentFreeInputVariableType> ret = new HashMap<>();
		
		this.getFreeInputVariableSet().forEach(e->{
			ret.put(e.getIndependentFreeInputVariableType().getID(),e.getIndependentFreeInputVariableType());
		});
		
		return ret;
	}
	
	/**
	 * return the set of {@link IndependentFreeInputVariableType} owned by this {@link CompositionFunction}
	 * @return
	 */
	default Map<IndependentFreeInputVariableTypeID, IndependentFreeInputVariableType> getOriginalIndependentFreeInputVariableTypeIDMap(){
		Map<IndependentFreeInputVariableTypeID, IndependentFreeInputVariableType> ret = new HashMap<>();
		
		this.getIndependentFreeInputVariableTypeIDMap().forEach((k,v)->{
			if(v.getOwnerCompositionFunctionID().equals(this.getID()))
				ret.put(k,v);
		});
		return ret;
	}

	/**
	 * 100720
	 * 
	 * extracts and returns the map from depended record data MetadataID to the columns that are used as input of this CompositionFunction;
	 * 
	 * note that the owner RecordDataMetadata of the host CompositionFunctionGroup of this CompositionFunction is included;
	 * 
	 * the returned map can have entries with value being empty set;
	 * 
	 * =======================
	 * 1. facilitate to identify set of depended record data of a CFD graph which can be used to induce the DOS graph;
	 * 
	 * 
	 * 2. also facilitate to extract and build depended {@link Metadata} from host VisProjectDBContext when building a VisScheme together with the {@link CompositionFunction#getDependedRecordMetadataIDInputColumnNameSetMap(context.VisframeContext)};
	 * 
	 * 		specifically, 
	 * 		1. for record data Metadata, help to extract the set of columns for the data table schema to be put in the VisScheme
	 * 		2. for Graph data ({@link DataType#GRAPH}), help to extract the additional feature columns of node and edge data;
	 * 		3. for vftree data ({@link DataType#vfTREE}), help to extract the non-mandatory feature columns of node and edge data;
	 * 
	 * ========================
	 * depended record data of a CompositionFunction
	 * 		1. owner record data of the host CompositionFunctionGroup of this CompositionFunction;
	 * 
	 * 		2. target record data of any {@link RecordwiseInputVariable#getTargetRecordDataMetadataID()} of this this CompositionFunction;
	 * 			note that for {@link UpstreamValueTableColumnOutputVariableInputVariable}s, their target record data is the same with the owner record data thus can be skipped;
	 * 			for {@link CFGTargetInputVariable}s and {@link RecordAttributeInputVariable}s, target record data should be explicitly added;
	 * 		
	 * 		3. target record data of {@link SQLAggregateFunctionBasedInputVariable#getTargetRecordMetadataID()};
	 * 
	 * 
	 * input columns of depended record data of a CompositionFunction
	 * 		1. for {@link RecordAttributeInputVariable}s, trivial
	 * 		2. for {@link SQLAggregateFunctionBasedInputVariable}s, trivial
	 * 
	 * @return
	 * @throws SQLException 
	 */
	default Map<MetadataID, Set<DataTableColumnName>> getDependedRecordMetadataIDInputColumnNameSetMap(VisframeContext hostVisframeContext) throws SQLException{
		Map<MetadataID, Set<DataTableColumnName>> dependedRecordMetadataIDInputColumnNameSetMap = new HashMap<>();
		//first add the owner record data of the CFG of this CF;
		CompositionFunctionGroup hostCFG = hostVisframeContext.getCompositionFunctionGroupLookup().lookup(this.getHostCompositionFunctionGroupID());
		dependedRecordMetadataIDInputColumnNameSetMap.put(hostCFG.getOwnerRecordDataMetadataID(), new HashSet<>());
		
		//
		for(InputVariable iv:this.getInputVariableSet()) {
			//RecordDataTableColumnInputVariable
			if(iv instanceof RecordAttributeInputVariable) {//RecordDataTableColumnInputVariable, add target record data and the column
				RecordAttributeInputVariable rdtciv = (RecordAttributeInputVariable) iv;
				MetadataID targetRecordMetadataID = rdtciv.getTargetRecordDataMetadataID();
				DataTableColumnName inputColumnName = rdtciv.getColumn().getName();
				
				if(!dependedRecordMetadataIDInputColumnNameSetMap.containsKey(targetRecordMetadataID)) {
					dependedRecordMetadataIDInputColumnNameSetMap.put(targetRecordMetadataID, new HashSet<>());
				}
				
				dependedRecordMetadataIDInputColumnNameSetMap.get(targetRecordMetadataID).add(inputColumnName);
			}else if(iv instanceof CFGTargetInputVariable) { //CFGTargetInputVariable, simply add the target record data
				CFGTargetInputVariable cfgtiv = (CFGTargetInputVariable)iv;
				MetadataID targetRecordMetadataID = cfgtiv.getTargetRecordDataMetadataID();
				
				if(!dependedRecordMetadataIDInputColumnNameSetMap.containsKey(targetRecordMetadataID)) {
					dependedRecordMetadataIDInputColumnNameSetMap.put(targetRecordMetadataID, new HashSet<>());
				}
				
			}else if(iv instanceof SQLAggregateFunctionBasedInputVariable) {////SQLAggregateFunctionBasedInputVariable, add the target record data and the columns
				SQLAggregateFunctionBasedInputVariable safbiv = (SQLAggregateFunctionBasedInputVariable) iv;
				MetadataID inputRecordMetadataID = safbiv.getTargetRecordMetadataID();
				
				if(!dependedRecordMetadataIDInputColumnNameSetMap.containsKey(inputRecordMetadataID)) {
					dependedRecordMetadataIDInputColumnNameSetMap.put(inputRecordMetadataID, new HashSet<>());
				}
				
				if(safbiv.getRecordwiseInputVariable1()!=null) {
					if(safbiv.getRecordwiseInputVariable1() instanceof RecordAttributeInputVariable) {//add column
						RecordAttributeInputVariable rdtciv = (RecordAttributeInputVariable)safbiv.getRecordwiseInputVariable1();
						
						dependedRecordMetadataIDInputColumnNameSetMap.get(inputRecordMetadataID).add(rdtciv.getColumn().getName());
					}
				}
				
				if(safbiv.getRecordwiseInputVariable2()!=null) {
					if(safbiv.getRecordwiseInputVariable2() instanceof RecordAttributeInputVariable) {//add column
						RecordAttributeInputVariable rdtciv = (RecordAttributeInputVariable)safbiv.getRecordwiseInputVariable2();
						
						dependedRecordMetadataIDInputColumnNameSetMap.get(inputRecordMetadataID).add(rdtciv.getColumn().getName());
					}
				}
			}
		}
		
		return dependedRecordMetadataIDInputColumnNameSetMap;
	}
	
	
	/**
	 * return the set of {@link CompositionFunctionID} on which this CompositionFunction is depending;
	 * 
	 * 1. this CompositionFunction is depending on another CompositionFunction cf if cf's assigned {@link CFGTarget}s is used by one or more {@link CFGTargetInputVariable}s of this one;
	 * 
	 * 2. this CompositionFunction is depending on another CompositionFunction cf if cf's original IndependentFreeInputVariableType is used by one or more {@link FreeInputVariable} of this one;
	 * @return
	 */
	default Set<CompositionFunctionID> getDependedCompositionFunctionIDSetByThisOne(VisframeContext hostVisframeContext){
		return this.getDependedCompositionFunctionIDDependencyTypeMapByThisOne(hostVisframeContext).keySet();
	}
	
	/**
	 * retrieve and return the set of depended CompositionFunctionID and the type of dependencies of this CompositionFunction;
	 * 
	 * first element in the Pair is dependency on target; second element in the Pair is dependency on IndieFIVType;
	 * @param hostVisframeContext
	 * @return map from depended CompositionFunctionID to the pair of whether the dependency is based on target and whether the dependency is based on IndepedentFreeInputVariableType, the map value Pair can have both values true but not both false! 
	 */
	default Map<CompositionFunctionID, Pair<Boolean, Boolean>> getDependedCompositionFunctionIDDependencyTypeMapByThisOne(VisframeContext hostVisframeContext){
		Map<CompositionFunctionID, Pair<Boolean, Boolean>> ret = new HashMap<>();
		
		Set<CompositionFunctionID> dependedByTarget = new HashSet<>();
		Set<CompositionFunctionID> dependedByIndieFIVType = new HashSet<>();
		
		
		//CFGTargetInputVariable and SQLAggregateFunctionBasedInputVariable
		for(InputVariable iv: this.getInputVariableSet()) {
			if(iv instanceof CFGTargetInputVariable) {
				CFGTargetInputVariable cfgtiv = (CFGTargetInputVariable) iv;
				//
				CompositionFunctionGroupID dependedCfgID = cfgtiv.getTargetCompositionFunctionGroupID();
				SimpleName targetName = cfgtiv.getTarget().getName();
				CompositionFunctionID dependedCFID = hostVisframeContext.getCompositionFuncitionID(dependedCfgID, targetName);
				dependedByTarget.add(dependedCFID);
			}else if(iv instanceof SQLAggregateFunctionBasedInputVariable) {
				//need to check the recordwiseInputVariable1 and recordwiseInputVariable2 if they are not null and of type CFGTargetInputVariable
				SQLAggregateFunctionBasedInputVariable siv = (SQLAggregateFunctionBasedInputVariable)iv; 
				if(siv.getRecordwiseInputVariable1()!=null && siv.getRecordwiseInputVariable1() instanceof CFGTargetInputVariable) {
					CFGTargetInputVariable cfgtiv = (CFGTargetInputVariable)siv.getRecordwiseInputVariable1();
					
					CompositionFunctionGroupID dependedCfgID = cfgtiv.getTargetCompositionFunctionGroupID();
					SimpleName targetName = cfgtiv.getTarget().getName();
					CompositionFunctionID dependedCFID = hostVisframeContext.getCompositionFuncitionID(dependedCfgID, targetName);
					dependedByTarget.add(dependedCFID);
				}
				
				if(siv.getRecordwiseInputVariable2()!=null && siv.getRecordwiseInputVariable2() instanceof CFGTargetInputVariable) {
					CFGTargetInputVariable cfgtiv = (CFGTargetInputVariable)siv.getRecordwiseInputVariable2();
					
					CompositionFunctionGroupID dependedCfgID = cfgtiv.getTargetCompositionFunctionGroupID();
					SimpleName targetName = cfgtiv.getTarget().getName();
					CompositionFunctionID dependedCFID = hostVisframeContext.getCompositionFuncitionID(dependedCfgID, targetName);
					dependedByTarget.add(dependedCFID);
				}
			}
			
		}
		
		
		//check every FreeInputVariable
		for(FreeInputVariable fiv:this.getFreeInputVariableSet()) {
			if(fiv.getIndependentFreeInputVariableType().getOwnerCompositionFunctionID().equals(this.getID())) {
				//no dependency on other CF, skip
			}else {
				dependedByIndieFIVType.add(fiv.getIndependentFreeInputVariableType().getOwnerCompositionFunctionID());
			}
		}
		
		dependedByTarget.forEach(e->{
			ret.put(e, new Pair<>(true, dependedByIndieFIVType.contains(e)));
		});
		
		dependedByIndieFIVType.forEach(e->{
			if(!ret.containsKey(e)) {
				ret.put(e, new Pair<>(false, true));
			}
		});
		
		
		return ret;
	}
	
	///////////////////////////////////////////
	/**
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	CompositionFunction reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;
	
	
	//////////////////////////////////////////
	///CF target value table calculation
	
	
	/**
	 * return the map from index ID to the {@link ComponentFunction} contained on {@link ComponentFunction} tree of this {@link CompositionFunction}
	 * @return
	 */
	Map<Integer, ComponentFunction> getComponentFunctionIndexIDMap();
	
	/**
	 * return the {@link ComponentFunction} with the given index ID;
	 * @param componentFunctionIndexID
	 * @return
	 */
	default ComponentFunction getComponentFunction(int componentFunctionIndexID) {
		return this.getComponentFunctionIndexIDMap().get(componentFunctionIndexID);
	}
	
	
	/**
	 * return the sorted list of index IDs of all {@link PiecewiseFunction}s on {@link ComponentFunction} tree of this {@link CompositionFunction}
	 * @return
	 */
	default List<Integer> getSortedListOfPiecewiseFunctionIndexID(){
		List<Integer> ret = new ArrayList<>();
		
		for(Integer indexID:this.getComponentFunctionIndexIDMap().keySet()) {
			if(this.getComponentFunctionIndexIDMap().get(indexID) instanceof PiecewiseFunction) {
				ret.add(indexID);
			}
		}
		
		Collections.sort(ret);
		
		return ret;
	}
	
	
	/**
	 * return the sorted list of all {@link TemporaryOutputVariable} in this {@link CompositionFunction}
	 * @return
	 */
	default List<TemporaryOutputVariable> getSortedListOfTemporaryOutputVariable(){
		List<TemporaryOutputVariable> ret = new ArrayList<>();
		
		for(OutputVariable ov:this.getOutputVariableSet()) {
			if(ov instanceof TemporaryOutputVariable) {
				ret.add((TemporaryOutputVariable)ov);
			}
		}
		
		Collections.sort(ret);
		
		return ret;
			
	}
	
	/**
	 * initialize the chain reaction to build the upstream PiecewiseFunction Index ID output index map for each ComponentFunction on the tree 
	 * by invoking the root ComponentFunction’s {@link ComponentFunction#buildUpstreamPiecewiseFunctionIndexIDOutputIndexMap(Map, Integer, Integer)} method
	 * 
	 * this process is fully depending on the structure of the rooted {@link ComponentFunction} tree contained by this {@link CompositionFunction};
	 */
	void buildUpstreamPiecewiseFunctionIndexIDOutputIndexMap();
	
	
	/**
	 * <p>calculate the value table of this {@link CompositionFunction} with the given {@link CFValueTableRunCalculator};</p>
	 * 1. invoke {@link #buildUpstreamPiecewiseFunctionIndexIDOutputIndexMap()} method;<br>
	 * 2. invoke the root ComponentFunction’s {@link ComponentFunction#calculate(CFTargetValueTableRunCalculator)} method;<br>
	 * @param targetValueTableRunCalculator
	 * @throws SQLException 
	 */
	void calculate(CFTargetValueTableRunCalculator targetValueTableRunCalculator) throws SQLException;

}
