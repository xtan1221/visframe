package dependency.cfd;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import context.VisframeContext;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.variable.independent.IndependentFreeInputVariableTypeID;
import metadata.MetadataID;
import rdb.table.data.DataTableColumnName;
import utils.Pair;


/**
 * an implementation of CFDGraphBuilder with Node type being CFDNodeImpl and edge type being CFDEdgeImpl
 * 
 * 
 * 
 * @author tanxu
 *
 */
public class SimpleCFDGraphBuilder{
	private final VisframeContext hostVisframeContext;
	/**
	 * not necessarily turn out to be leaf nodes on the resulted CFD graph
	 */
	private final Set<CompositionFunctionID> initialCFIDSet;
	
	////////////////////////////
	protected SimpleDirectedGraph<CFDNodeImpl,CFDEdgeImpl> underlyingGraph;
	
	protected SimpleCFDGraph CFDGraph;
	
	
	/**
	 * set of CompositionFunctionID whose upstream incident CompositionFunctionIDs(depending ones) need to be checked;
	 */
	private Set<CompositionFunctionID> uncheckedCFIDSet;
	/**
	 * set of CompositionFunctionID whose upstream incident CompositionFunctionIDs are checked
	 */
	private Set<CompositionFunctionID> checkedCFIDSet;
	
	/**
	 * the set of depended record data MetadataID and involved DataTableColumnNames of the built SimpleCFDGraph in the host VisframeContext
	 */
	private Map<MetadataID, Set<DataTableColumnName>> dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap;
	
	private Map<CompositionFunctionID, Set<IndependentFreeInputVariableTypeID>> cfIDEmployedIndependentFIVTypeSetMap;
	
	/**
	 * constructor
	 * @param hostVisframeContext
	 * @param initialCFIDSet not null; can be empty
	 * @throws SQLException 
	 */
	public SimpleCFDGraphBuilder(
			VisframeContext hostVisframeContext, Set<CompositionFunctionID> initialCFIDSet
			) throws SQLException {
		if(hostVisframeContext==null)
			throw new IllegalArgumentException("given hostVisframeContext cannot be null!");
		
		if(initialCFIDSet==null)
			throw new IllegalArgumentException("given initialCFIDSet cannot be null!");
		
		
		this.hostVisframeContext = hostVisframeContext;
		this.initialCFIDSet = initialCFIDSet;
		
		this.buildUnderlyingGraph();
		this.buildCFDGraph();
	}
	
	VisframeContext getHostVisframeContext() {
		return hostVisframeContext;
	}
	
	

	Set<CompositionFunctionID> getInitialCFIDSet() {
		return initialCFIDSet;
	}
	

	
	public SimpleCFDGraph getBuiltGraph(){
		return this.CFDGraph;
	}
	
	
	////////////////////////////////
	/**
	 * build the SimpleDirectedGraph with the following strategy:
	 * 1. if a CF a has a FreeInputVariable as input variable and the FreeInputVariable is owned by another CF b, add an edge from the CF a to b;
	 * 
	 * 
	 * after the SimpleDirectedGraph is built, create a DAG<CFDNodeImpl,CFDEdgeImpl> with it;
	 * @throws SQLException 
	 * 
	 */
	protected void buildUnderlyingGraph() throws SQLException {
		this.underlyingGraph = new SimpleDirectedGraph<>(CFDEdgeImpl.class);
		//initialize
		this.uncheckedCFIDSet = new HashSet<>(); //updated set of CFs that need to be added to the graph;
		this.checkedCFIDSet = new HashSet<>();
		
		this.uncheckedCFIDSet.addAll(this.getInitialCFIDSet());
		this.dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap = new HashMap<>();
		this.cfIDEmployedIndependentFIVTypeSetMap = new HashMap<>();
		
		//keep tracking CompositionFunctions until none left
		while(!this.uncheckedCFIDSet.isEmpty()) {
			CompositionFunctionID currentCfID = this.uncheckedCFIDSet.iterator().next();
			//first add the unchecked CompositionFunctionID as node to the underlying graph(though it might have been added already)
			this.underlyingGraph.addVertex(new CFDNodeImpl(currentCfID));
			
			//
			CompositionFunction currentCf = this.getHostVisframeContext().getCompositionFunctionLookup().lookup(currentCfID);
			//first add the input record data columns of the current CF
			this.dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap.putAll(currentCf.getDependedRecordMetadataIDInputColumnNameSetMap(this.getHostVisframeContext()));
			this.cfIDEmployedIndependentFIVTypeSetMap.put(currentCfID, currentCf.getIndependentFreeInputVariableTypeIDMap().keySet());
			
			
			//then check the depended CFs of the current CF
			Map<CompositionFunctionID, Pair<Boolean, Boolean>> dependedCompositionFunctionIDDependencyTypeMap = 
					currentCf.getDependedCompositionFunctionIDDependencyTypeMapByThisOne(this.getHostVisframeContext());
			for(CompositionFunctionID dependedCFID:dependedCompositionFunctionIDDependencyTypeMap.keySet()) {
				Pair<Boolean, Boolean> dependencyType = dependedCompositionFunctionIDDependencyTypeMap.get(dependedCFID);
				CFDNodeImpl sourceNode = new CFDNodeImpl(currentCfID);
				CFDNodeImpl sinkNode = new CFDNodeImpl(dependedCFID);
				CFDEdgeImpl cfdEdge = new CFDEdgeImpl(currentCfID, dependedCFID, dependencyType.getFirst(), dependencyType.getSecond());
				
				if(!this.checkedCFIDSet.contains(dependedCFID)) {//not checked yet
					this.uncheckedCFIDSet.add(dependedCFID);
					this.underlyingGraph.addVertex(sinkNode);
				}
				
				//process edge
				if(this.underlyingGraph.containsEdge(cfdEdge)) {//edge already exists, do nothing
//					this.underlyingGraph.getEdge(sourceNode, sinkNode).setBasedOnAssignedTarget(true);
				}else {//edge is not existing, add it
					this.underlyingGraph.addEdge(sourceNode, sinkNode, cfdEdge);
				}
			}
			
			//update
			this.uncheckedCFIDSet.remove(currentCfID);
			this.checkedCFIDSet.add(currentCfID);
			
		}
		
	}

	
	private void buildCFDGraph() {
		if(this.underlyingGraph==null) {
			return;
		}
		
		//build the DAG; CycleFoundInDependencyGraphException will be thrown from the constructor if cycle found
		this.CFDGraph = new SimpleCFDGraph(this.underlyingGraph, this.initialCFIDSet, this.dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap, this.cfIDEmployedIndependentFIVTypeSetMap);
		
	}
	
}
