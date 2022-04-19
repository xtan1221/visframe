package context.scheme.appliedarchive.reproducedandinsertedinstance.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import context.project.VisProjectDBContext;
import context.scheme.VisScheme;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import dependency.cfd.integrated.IntegratedCFDGraphEdge;
import dependency.cfd.integrated.IntegratedCFDGraphNode;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;

/**
 * note that CompositionFunctions can be all reproduced at once before insertion, but the insertion order should be from the depended ones to the depending ones;
 * 
 * @author tanxu
 *
 */
public class CFReproducingAndInsertionTracker {
	private final VisSchemeAppliedArchiveReproducerAndInserter hostVisSchemeAppliedArchiveReproducerAndInserter;
	
	///////////////////////
	private VisScheme appliedVisScheme;
	
	
	////////////////////////////
	/**
	 * list of reproduced CompositionFunctions with the order such that the depending CompositionFunctions are always after the depended ones;
	 * 
	 * when inserting the reproduced CompositionFunctions, the insertion order should be from the first to the last in this list;
	 */
	private List<CompositionFunction> reproducedCompositionFunctionListOrderedByDependency;
	
	//********************************************
	/**
	 * map from original CompositionFunctionID
	 * to the map
	 * 		from the copy index of the original CompositionFunctionID to be reproduced and inserted
	 * 		to the reproduced CompositionFunctionID
	 * 
	 * must be updated whenever a CompositionFunctionID is newly reproduced;
	 * 		invoked from {@link CompositionFunctionID#reproduce(VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)}
	 * 
	 * or a CompositionFunction is rolled back;
	 * 
	 * also used to build the target VisSchemeAppliedArchiveReproducedAndInsertedInstance as constructor parameter;
	 */
	private Map<CompositionFunctionID, Map<Integer, CompositionFunctionID>> originalCFIDCopyIndexReproducedCFIDMapMap;
	/**
	 * invoked from {@link CompositionFunctionID#reproduce(VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)} whenever a CompositionFunctionID is newly reproduced;
	 * @param originalID
	 * @param copyIndex
	 * @param reproducedID
	 */
	public void addToOriginalCFIDCopyIndexReproducedCFIDMapMap(CompositionFunctionID originalID, int copyIndex, CompositionFunctionID reproducedID) {
		if(!this.originalCFIDCopyIndexReproducedCFIDMapMap.containsKey(originalID))
			this.originalCFIDCopyIndexReproducedCFIDMapMap.put(originalID, new HashMap<>());
		
		this.originalCFIDCopyIndexReproducedCFIDMapMap.get(originalID).put(copyIndex, reproducedID);
	}
	
	/**
	 * constructor
	 * @param VSAReproducerAndInserter
	 * @param hostVisProjectDBContext
	 * @param appliedVisScheme
	 * @param applierArchive
	 * @throws SQLException 
	 */
	public CFReproducingAndInsertionTracker(
			VisSchemeAppliedArchiveReproducerAndInserter hostVisSchemeAppliedArchiveReproducerAndInserter
			) throws SQLException{
		//VALIDATIONS TODO
		this.hostVisSchemeAppliedArchiveReproducerAndInserter = hostVisSchemeAppliedArchiveReproducerAndInserter;
		
		this.appliedVisScheme = this.hostVisSchemeAppliedArchiveReproducerAndInserter.getHostVisProjectDBContext().getHasIDTypeManagerController().getVisSchemeManager().lookup(this.hostVisSchemeAppliedArchiveReproducerAndInserter.getAppliedArchive().getAppliedVisSchemeID());
		
		//////////////////////////
		this.originalCFIDCopyIndexReproducedCFIDMapMap = new HashMap<>();
		this.reproducedCompositionFunctionListOrderedByDependency = new ArrayList<>();
		
//		this.build();
	}
	
	
	
	/**
	 * try to reproduce and insert all CFs into the host VisProjectDBContext;
	 * also add the successfully reproduced and inserted CFID to the {@link #reproducedAndInsertedCFIDListOrderedByTime};
	 * 
	 * if all CFs are successfully reproduced and inserted, set {@link #allCFsSuccessfullyReproducedAndInserted} to true;
	 * otherwise, set it to false;
	 * 
	 * note that this can only be invoked when all CFGs are successfully reproduced and inserted;
	 * @throws SQLException 
	 */
	public void build() throws SQLException {
		Map<IntegratedCFDGraphNode, Integer> integratedCFDGraphNodeNumOfUnprocessedDependedNodeMap = new HashMap<>();
		Set<IntegratedCFDGraphNode> integratedCFDGraphNodeSetReadyToBeReproduced = new HashSet<>();
		Set<IntegratedCFDGraphNode> reproducedNodeSet = new HashSet<>();
		
		//initialize
		this.getTrimmedIntegratedCFDGraph().vertexSet().forEach(v->{
			int dependedNodeNum = this.getTrimmedIntegratedCFDGraph().outDegreeOf(v); //number of depended node;
			integratedCFDGraphNodeNumOfUnprocessedDependedNodeMap.put(
					v, 
					dependedNodeNum
					);
			if(dependedNodeNum==0)
				integratedCFDGraphNodeSetReadyToBeReproduced.add(v);
		});
		
		///
		while(!integratedCFDGraphNodeSetReadyToBeReproduced.isEmpty()) {
			
			for(IntegratedCFDGraphNode node:integratedCFDGraphNodeSetReadyToBeReproduced){
				CompositionFunctionID originalCFID = node.getCfID();
				int copyIndex = node.getCopyIndex();
				CompositionFunction originalCFInVisScheme = this.appliedVisScheme.getCompositionFunctionLookup().lookup(originalCFID);
				CompositionFunction reproducedCF = originalCFInVisScheme.reproduce(
						this.hostVisSchemeAppliedArchiveReproducerAndInserter.getHostVisProjectDBContext(), 
						this.hostVisSchemeAppliedArchiveReproducerAndInserter, 
						copyIndex);
				
				this.reproducedCompositionFunctionListOrderedByDependency.add(reproducedCF);
				
				reproducedNodeSet.add(node);
				
				//update the integratedCFDGraphNodeNumOfUnprocessedDependedNodeMap
				this.getTrimmedIntegratedCFDGraph().incomingEdgesOf(node).forEach(e->{
					IntegratedCFDGraphNode dependingNode = this.getTrimmedIntegratedCFDGraph().getEdgeSource(e);
					integratedCFDGraphNodeNumOfUnprocessedDependedNodeMap.put(
							dependingNode, 
							integratedCFDGraphNodeNumOfUnprocessedDependedNodeMap.get(dependingNode)-1);
				});
			}
			
			
			//update integratedCFDGraphNodeSetReadyToBeReproduced
			integratedCFDGraphNodeSetReadyToBeReproduced.clear();
			integratedCFDGraphNodeNumOfUnprocessedDependedNodeMap.forEach((v,n)->{
				if(n==0 && !reproducedNodeSet.contains(v)) {
					integratedCFDGraphNodeSetReadyToBeReproduced.add(v);
				}
			});
		}
		
	}
	
	private SimpleDirectedGraph<IntegratedCFDGraphNode, IntegratedCFDGraphEdge> getTrimmedIntegratedCFDGraph(){
		return this.hostVisSchemeAppliedArchiveReproducerAndInserter.getAppliedArchive().getTrimmedIntegratedCFDGraph();
	}
	
	/**
	 * remove all the reproduced CFGs;
	 * 
	 * should be invoked when all reproduced and inserted CFs are rolled back;
	 */
	public void removeAllReproducedCFs() {
		this.originalCFIDCopyIndexReproducedCFIDMapMap.clear();
		this.reproducedCompositionFunctionListOrderedByDependency.clear();
	}
	
	//////////////////////
	/**
	 * 
	 * @return
	 */
	public Map<CompositionFunctionID, Map<Integer, CompositionFunctionID>> getOriginalCFIDCopyIndexReproducedCFIDMapMap(){
		return this.originalCFIDCopyIndexReproducedCFIDMapMap;
	}

	/**
	 * @return the reproducedCompositionFunctionListOrderedByDependency
	 */
	public List<CompositionFunction> getReproducedCompositionFunctionListOrderedByDependency() {
		return reproducedCompositionFunctionListOrderedByDependency;
	}
	
	public List<CompositionFunctionID> getReproducedCompositionFunctionIDListOrderedByDependency(){
		List<CompositionFunctionID> ret = new ArrayList<>();
		this.getReproducedCompositionFunctionListOrderedByDependency().forEach(cf->{
			ret.add(cf.getID());
		});
		
		return ret;
	}
}
