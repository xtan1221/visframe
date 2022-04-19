package visinstance.run.calculation;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import context.project.VisProjectDBContext;
import dependency.cfd.CFDEdgeImpl;
import dependency.cfd.CFDNodeImpl;
import dependency.cfd.SimpleCFDGraph;
import dependency.cfd.SimpleCFDGraphBuilder;
import exception.VisframeException;
import function.composition.CompositionFunctionID;
import function.variable.independent.IndependentFreeInputVariableTypeID;
import visinstance.VisInstance;
import visinstance.run.VisInstanceRun;

/**
 * preprocessor of a VisInstanceRun that will
 * 1. build the CFD graph
 * 2. build the IndependetFIVTypeIDStringValueMap for each CompositionFunction on the CFD graph;
 * 
 * this class will facilitate 
 * 1. calculation of VisInstanceRun
 * 2. extraction of target value table for graphics layout;
 * 
 * @author tanxu
 *
 */
public final class VisInstanceRunPreprocessor {
	private final VisProjectDBContext hostVisProjectDBContext;
	
	private final VisInstanceRun visInstanceRun;
	
	///////////////////////
	/**
	 * CFD graph of the VisInstance of the target VisInstanceRun;
	 */
	private SimpleCFDGraph CFDGraph;
	
	/**
	 * map from the all {@link CompositionFunctionID} on CFD graph to the {@link IndependentFIVTypeIDStringValueMap};
	 */
	private Map<CompositionFunctionID, IndependentFIVTypeIDStringValueMap> cfIDIndependetFIVTypeStringValueMapMap;
	
	
	/**
	 * 
	 * @param hostVisProjectDBContext
	 * @param visInstanceRun
	 * @throws SQLException
	 */
	public VisInstanceRunPreprocessor(VisProjectDBContext hostVisProjectDBContext,
			VisInstanceRun visInstanceRun) throws SQLException{
		
		this.hostVisProjectDBContext = hostVisProjectDBContext;
		this.visInstanceRun = visInstanceRun;
		
		this.perform();
	}
	
	
	
	private void perform() throws SQLException {
		this.buildCFDGraph();
		this.buildCfIDIndependetFIVTypeIDStringValueMapMap();
	}
	
	
	/**
	 * build CFD graph of the owner VisInstance of the VisInstanceRun of this calculator 
	 * @return
	 * @throws SQLException 
	 */
	private void buildCFDGraph() throws SQLException {
		VisInstance vi = this.getHostVisProjectDBContext().getHasIDTypeManagerController().getVisInstanceManager().lookup(this.getVisInstanceRun().getVisInstanceID());
		SimpleCFDGraphBuilder builder = new SimpleCFDGraphBuilder(
				this.getHostVisProjectDBContext(), 
				vi.getCoreShapeCFGCFIDSet());
		
		this.CFDGraph = builder.getBuiltGraph();
	}
	

	/**
	 * build the {@link #cfIDIndependetFIVTypeStringValueMapMap} for all CompositionFunctions on the CFD graph;
	 */
	private void buildCfIDIndependetFIVTypeIDStringValueMapMap() {
		this.cfIDIndependetFIVTypeStringValueMapMap = new HashMap<>();
		
		//after all cf processed, all map value should be 0
		Map<CFDNodeImpl, Integer> nodeUnprocessedDependedNodeNumMap = new HashMap<>();
		//after all cf processed, this set should be empty;
		Set<CFDNodeImpl> nodeSetReadyToBeProcessed = new HashSet<>();
		
		//
		SimpleDirectedGraph<CFDNodeImpl, CFDEdgeImpl> underlyingCFDGraph = this.CFDGraph.getUnderlyingGraph();
		
		//initialize
		underlyingCFDGraph.vertexSet().forEach(v->{
			int dependedNodeNum = underlyingCFDGraph.outDegreeOf(v);
			nodeUnprocessedDependedNodeNumMap.put(v, dependedNodeNum);
			if(dependedNodeNum==0)
				nodeSetReadyToBeProcessed.add(v);
		});
		
		//iteratively build the IndependetFIVTypeIDStringValueMap for each CF on the CFD graph
		//the basic idea is that the IndependetFIVTypeIDStringValueMap of a CF can be built by merging the IndependetFIVTypeIDStringValueMap of the IndependetFIVType employed by the CF and
		//IndependetFIVTypeIDStringValueMaps of all directly depended CFs;
		while(!nodeSetReadyToBeProcessed.isEmpty()) {
			CFDNodeImpl node = nodeSetReadyToBeProcessed.iterator().next();
			//find out the  IndependentFreeInputVariableTypeIDs employed by current CF (not include those employed by depended CFs)
			Set<IndependentFreeInputVariableTypeID> indieFIVTypeSetEmployedByCf = this.CFDGraph.getCfIDEmployedIndependentFIVTypeSetMap().get(node.getCFID());
			Map<IndependentFreeInputVariableTypeID,String> indieFIVTypeStringValueMap = new HashMap<>();
			indieFIVTypeSetEmployedByCf.forEach(t->{
				indieFIVTypeStringValueMap.put(
						t,
						this.getVisInstanceRun().getCFDGraphIndependetFIVStringValueMap().getAssignedStringValue(t));
			});
			
			//add the indieFIVTypeStringValueMaps of all directly depended CFs
			underlyingCFDGraph.outgoingEdgesOf(node).forEach(e->{
				indieFIVTypeStringValueMap.putAll(
						this.cfIDIndependetFIVTypeStringValueMapMap.get(underlyingCFDGraph.getEdgeTarget(e).getCFID()).getIndependentFreeInputVariableTypeIDAssignedStringValueMap());
			});
			//create the IndependetFIVTypeIDStringValueMap and add to cfIDIndependetFIVTypeStringValueMapMap
			this.cfIDIndependetFIVTypeStringValueMapMap.put(node.getCFID(), new IndependentFIVTypeIDStringValueMap(indieFIVTypeStringValueMap));
			
			//minus one for each depending CF in the nodeUnprocessedDependedNodeNumMap
			//if any Cf's map value become 0, add to the nodeSetReadyToBeProcessed
			underlyingCFDGraph.incomingEdgesOf(node).forEach(e->{
				CFDNodeImpl dependingNode = underlyingCFDGraph.getEdgeSource(e);
				int unprocessedDependedCFNum = nodeUnprocessedDependedNodeNumMap.get(dependingNode)-1;
				nodeUnprocessedDependedNodeNumMap.put(dependingNode, unprocessedDependedCFNum);
				
				if(unprocessedDependedCFNum==0)
					nodeSetReadyToBeProcessed.add(dependingNode);
				
				if(unprocessedDependedCFNum<0)
					throw new VisframeException("map value of nodeUnprocessedDependedNodeNumMapcannot be less than 0!");
			});
			
			
			nodeSetReadyToBeProcessed.remove(node);
		}
		
		
		
		if(!nodeSetReadyToBeProcessed.isEmpty())
			throw new VisframeException("nodeSetReadyToBeProcessed must be empty after the building process is done!");
		
		if(cfIDIndependetFIVTypeStringValueMapMap.size()!=underlyingCFDGraph.vertexSet().size())
			throw new VisframeException("size of cfIDIndependetFIVTypeStringValueMapMap must be equal to the number of vertex of the cfd graph!");
		
	}
	
	//////////////////////////////////////
	/**
	 * @return the hostVisProjectDBContext
	 */
	public VisProjectDBContext getHostVisProjectDBContext() {
		return hostVisProjectDBContext;
	}



	/**
	 * @return the visInstanceRun
	 */
	public VisInstanceRun getVisInstanceRun() {
		return visInstanceRun;
	}



	/**
	 * @return the cFDGraph
	 */
	public SimpleCFDGraph getCFDGraph() {
		return CFDGraph;
	}



	/**
	 * @return the cfIDIndependetFIVTypeStringValueMapMap
	 */
	public Map<CompositionFunctionID, IndependentFIVTypeIDStringValueMap> getCfIDIndependetFIVTypeStringValueMapMap() {
		return cfIDIndependetFIVTypeStringValueMapMap;
	}


}
