package dependency.cfd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;
import function.variable.independent.IndependentFreeInputVariableTypeID;
import metadata.MetadataID;
import rdb.table.data.DataTableColumnName;


/**
 * simple CFD graph induced by a set of initial CF in a VisframeContext;
 * 
 * this class also contains the set of depended record data Metadata and the involved data table columns;
 * 
 * this type of CFDGraph is the most frequently used in visframe;
 * 
 * @author tanxu
 *
 */
public class SimpleCFDGraph extends CFDGraphBase<CFDNodeImpl, CFDEdgeImpl>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1793589210428592587L;
	
	//////////////////////////////////
	private final Set<CompositionFunctionID> initialCFIDSet;
	/**
	 * extracted with {@link CompositionFunction#getInputRecordMetadataIDInputColumnNameSetMap()} of all {@link CompositionFunction} on this CFD graph
	 */
	private final Map<MetadataID, Set<DataTableColumnName>> dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap;
	
	/**
	 * map from CF id to the set of IndependentFreeInputVariableType employed by at least one FreeInputVariable of the CF on the CFD graph;
	 */
	private final Map<CompositionFunctionID, Set<IndependentFreeInputVariableTypeID>> cfIDEmployedIndependentFIVTypeIDSetMap;
	
	/////////////////
	private Map<CompositionFunctionID, CFDNodeImpl> cfIDNodeMap;
	private Map<CompositionFunctionGroupID, Set<CompositionFunctionID>> involvedCFGIDCFIDSetMap;
	
	
	/**
	 * 
	 * @param underlyingGraph
	 * @param initialCFIDSet not null; can be empty only if the CFD graph is an empty graph, thus the underlyingGraph should be empty as well
	 * @param dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap not null, can be empty only if the CFD graph is an empty graph, thus the underlyingGraph should be empty as well
	 * @param cfIDEmployedIndependentFIVTypeIDSetMap not null; can be empty;
	 */
	public SimpleCFDGraph(
			SimpleDirectedGraph<CFDNodeImpl, CFDEdgeImpl> underlyingGraph,
			
			Set<CompositionFunctionID> initialCFIDSet,
			Map<MetadataID, Set<DataTableColumnName>> dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap,
			Map<CompositionFunctionID, Set<IndependentFreeInputVariableTypeID>> cfIDEmployedIndependentFIVTypeIDSetMap
			) {
		super(underlyingGraph, CFDEdgeImpl.class);
		// TODO Auto-generated constructor stub
		if(initialCFIDSet==null)
			throw new IllegalArgumentException("given initialCFIDSet cannot be null!");
		
		if(initialCFIDSet.isEmpty() && !underlyingGraph.vertexSet().isEmpty())
			throw new IllegalArgumentException("given initialCFIDSet can only be empty when underlyingGraph is empty!");
		
		if(dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap==null)
			throw new IllegalArgumentException("given dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap cannot be null!");
		
		if(dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap.isEmpty() && !underlyingGraph.vertexSet().isEmpty())
			throw new IllegalArgumentException("given dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap can only be empty when underlyingGraph is empty!");
		
		if(cfIDEmployedIndependentFIVTypeIDSetMap==null)
			throw new IllegalArgumentException("given independentFreeInputVariableTypeSet cannot be null!");
		
		
		
		this.initialCFIDSet = initialCFIDSet;
		this.dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap = dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap;
		this.cfIDEmployedIndependentFIVTypeIDSetMap = cfIDEmployedIndependentFIVTypeIDSetMap;
	}

	
	public Set<CompositionFunctionID> getInitialCFIDSet() {
		return initialCFIDSet;
	}
	

	public Map<MetadataID, Set<DataTableColumnName>> getDependedRecordMetadataIDInputVariableDataTableColumnNameSetMap() {
		return dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap;
	}

	/**
	 * @return the cfIDEmployedIndependentFIVTypeSetMap
	 */
	public Map<CompositionFunctionID, Set<IndependentFreeInputVariableTypeID>> getCfIDEmployedIndependentFIVTypeSetMap() {
		return cfIDEmployedIndependentFIVTypeIDSetMap;
	}

	
	public Set<IndependentFreeInputVariableTypeID> getAllEmployedIndependentFreeInputVariableTypeIDSet(){
		Set<IndependentFreeInputVariableTypeID> ret = new LinkedHashSet<>();
		
		this.cfIDEmployedIndependentFIVTypeIDSetMap.forEach((k,v)->{
			ret.addAll(v);
		});
		
		return ret;
	}
 
	//////////////////////////
	public Map<CompositionFunctionGroupID, Set<CompositionFunctionID>> getInvolvedCFGIDCFIDSetMap() {
		if(this.involvedCFGIDCFIDSetMap==null) {
			this.involvedCFGIDCFIDSetMap = new HashMap<>();
			
			this.getUnderlyingGraph().vertexSet().forEach(e->{
				CompositionFunctionGroupID cfgID = e.getCFID().getHostCompositionFunctionGroupID();
				if(!this.involvedCFGIDCFIDSetMap.containsKey(cfgID))
					this.involvedCFGIDCFIDSetMap.put(cfgID, new HashSet<>());
				
				this.involvedCFGIDCFIDSetMap.get(cfgID).add(e.getCFID());
			});
		}
		
		return involvedCFGIDCFIDSetMap;
	}
	
	
	
	/**
	 * return the CFDNodeImpl containing the given CompositionFunctionID;
	 * @param cfid
	 * @return
	 */
	public Map<CompositionFunctionID, CFDNodeImpl> getCFIDNodeMap() {
		if(this.cfIDNodeMap==null) {
			this.cfIDNodeMap = new HashMap<>();
			
			for(CFDNodeImpl node:this.getUnderlyingGraph().vertexSet()) {
				this.cfIDNodeMap.put(node.getCFID(), node);
			}
		}
		
		return this.cfIDNodeMap;
	}
	
	
	/**
	 * return the set of CompositionFunctionIDs that depends on(be upstream incident with) the given CompositionFunctionID
	 * @param cfid
	 * @return
	 */
	public Set<CompositionFunctionID> getDependingCompositionFunctionIDSet(CompositionFunctionID cfid){
		Set<CompositionFunctionID> ret = new HashSet<>();
		
		for(CFDEdgeImpl edge: this.getUnderlyingGraph().incomingEdgesOf(this.getCFIDNodeMap().get(cfid))) {
			ret.add(edge.getSource());
		}
		
		return ret;
	}
	
	
	/**
	 * return the set of CompositionFunctionIDs on which the given CompositionFunctionID is directly depending on;
	 * @param cfid
	 * @return
	 */
	public Set<CompositionFunctionID> getDependedCompositionFunctionIDSet(CompositionFunctionID cfid){
		Set<CompositionFunctionID> ret = new HashSet<>();
		
		for(CFDEdgeImpl edge: this.getUnderlyingGraph().outgoingEdgesOf(this.getCFIDNodeMap().get(cfid))) {
			ret.add(edge.getSource());
		}
		
		
		return ret;
		
	}
	
	
	
	//////////////////////////
//
//	@Override
//	public CFDGraphBase<CFDNodeImpl, CFDEdgeImpl> deepClone() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	
}
