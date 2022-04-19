package context.scheme.appliedarchive.reproducedandinsertedinstance.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import basic.SimpleName;
import context.project.VisProjectDBContext;
import context.scheme.VisScheme;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import dependency.dos.DOSEdge.DOSEdgeType;
import dependency.dos.integrated.IntegratedDOSGraphEdge;
import dependency.dos.integrated.IntegratedDOSGraphNode;
import metadata.MetadataID;
import operation.Operation;
import operation.OperationID;
import utils.Pair;

/**
 * helper class to track the status of Operation reproducing and insertion during the process of a VisSchemeAppliedArchiveReproducedAndInsertedInstance;
 * 
 * this class does not directly insert any reproduced Operation into host VisProjectDBContext;
 * 
 * @author tanxu
 * 
 */
public class OperationReproducingAndInsertionTracker {
	private final VisSchemeAppliedArchiveReproducerAndInserter hostVisSchemeAppliedArchiveReproducerAndInserter;
	
	/////////////////////
	private VisScheme appliedVisScheme;
	
	////////////////////////////////////////
	//supporting fields that does not changed, use the TrimmedIntegratedDOSAndCFDGraphUtils instead?
	/**
	 * map from original OperationID 
	 * to the map
	 * 		from the copy index of the original OperationID that needs to be reproduced and inserted (thus must be at the downstream of the solution set IntegratedDOSGraphNode)
	 * 		to the set of IntegratedDOSGraphNodes that is 
	 * 			1. either corresponding to the output Metadata of the original operation copy;
	 * 			2. or contains a component Metadata of the output Metadata (of composite type) of the original operation copy
	 * 
	 * note that this map will not change throughout the whole process of Operation reproducing and insertion;
	 */
	private Map<OperationID, Map<Integer, Set<IntegratedDOSGraphNode>>> originalOperationIDCopyIndexToBeReproducedAndInsertedDependingIntegratedDOSGraphNodeSetMapMap;
	
	/**
	 * map from the original OperationID
	 * to the map
	 * 		from the copy index of the original OperationID that needs to be reproduced and inserted (thus must be at the downstream of the solution set IntegratedDOSGraphNode)
	 * 		to the set of IntegratedDOSGraphEdge that contains the corresponding operation copy;
	 * 
	 */
	private Map<OperationID, Map<Integer, Set<IntegratedDOSGraphEdge>>> originalOperationIDCopyIndexToBeReproducedAndInsertedIntegratedDOSGraphEdgeSetContainingTheOperationMapMap;
	
	
	//////////////////////////////////
	//fields related with reproduceable operation copy index;
	/**
	 * IntegratedDOSGraphNodes that contains Metadata
	 * 1. in the solution set
	 * 2. resulted from Operation that has been reproduced and inserted;
	 * 3. component of Metadata whose containing IntegratedDOSGraphNode is either of 1 or 2;
	 * 
	 */
	private Set<IntegratedDOSGraphNode> processedIntegratedDOSGraphNodeSet;
	
	/**
	 * updated whenever an Operation copy is successfully reproduced and inserted or an inserted Operation copy is rolled back;
	 * {@link #addNextReproducedOperationToInsertedSet()}
	 * {@link #removeMostRecentlyInsertedOperationFromInsertedSet()}
	 */
	private Map<OperationID, Set<Integer>> reproduceableOriginalOperationIDCopyIndexSetMap;
	
	/**
	 * 
	 */
	private OperationID nextReproduceableOriginalOperationID;
	
	/**
	 * 
	 */
	private Integer nextReproduceableOriginalOperationCopyIndex;
	
	/**
	 * the next reproduced Operation to be inserted into the host VisProjectDBContext;
	 * note that this value will only be updated after {@link #addNextReproducedOperationToInsertedSet()} or {@link #removeMostRecentlyInsertedOperationFromInsertedSet()} is invoked;
	 * otherwise, its value will stay the same once assigned;
	 */
	private Operation nextReproducedOperation;
	
	////////////////////////////
	//fields related with Operations that have been successfully reproduced and inserted;
	//must be updated whenever an Operation is inserted or rolled back;
	/**
	 * map from OperationID in VisScheme with parameter dependent on input data table content 
	 * to the map 
	 * 		from the copy index of the Operation
	 * 		to the map
	 * 			from the name of parameters dependent on input data table content
	 * 			to the assigned object values
	 */
	private Map<OperationID, Map<Integer,Map<SimpleName, Object>>> originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap;
	
	
	/**
	 * 
	 */
	private Map<OperationID, Pair<OperationID, Integer>> reproducedAndInsertedOperationIDOriginalOperationIDCopyIndexPairMap;
	
	
	/**
	 * list or reproduced and inserted OperationIDs by the insertion order;
	 */
	private List<OperationID> reproducedAndInsertedOperationIDList;
	

	//***************************************************
	/**
	 * map from the the original OperationID to the 
	 * map 
	 * 		from copy index that need to be reproduced
	 * 		to the reproduced OperationID;
	 * 
	 * whenever an OperationID copy is reproduced (NOT inserted!) in {@link OperationID}, this should be updated from the {@link OperationID#reproduce(VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)}
	 * 
	 * whenever an inserted Operation is rolled back, this should be updated from {@link #removeMostRecentlyInsertedOperationFromInsertedSet()}
	 * 
	 * note that this map include both those operations that have been inserted and the next Operation that has been reproduced but not inserted yet (if any)
	 */
	private Map<OperationID, Map<Integer, OperationID>> originalOperationIDCopyIndexReproducedOperationIDMapMap;
	
	/**
	 * invoked from {@link OperationID#reproduce(VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)} whenever an OperationID is newly reproduced;
	 * @param originalID
	 * @param copyIndex
	 * @param reproducedID
	 */
	public void addToOriginalOperationIDCopyIndexReproducedOperationIDMapMap(OperationID originalID, int copyIndex, OperationID reproducedID) {
		if(!originalOperationIDCopyIndexReproducedOperationIDMapMap.containsKey(originalID))
			originalOperationIDCopyIndexReproducedOperationIDMapMap.put(originalID, new HashMap<>());
		
		this.originalOperationIDCopyIndexReproducedOperationIDMapMap.get(originalID).put(copyIndex, reproducedID);
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<OperationID, Map<Integer, OperationID>> getOriginalOperationIDCopyIndexReproducedOperationIDMapMap(){
		return this.originalOperationIDCopyIndexReproducedOperationIDMapMap;
	}
	
	
	//***************************************************
	/**
	 * map from the IntegratedDOSGraphNode that contains an output Metadata of a reproduced Operation;
	 * to the reproduced MetadataID;
	 * 
	 * when a MetadataID is reproduced, this should be updated from the {@link MetadataID#reproduce(VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)};
	 * 		note that when a composite MetadataID is reproduced due to a reproduced Operation, its component MetadataID will be reproduced as well;
	 * 
	 * whenever an inserted Operation is rolled back, this should be updated by invoking {@link #removeReproducedDependentMetadataIDOfAReproducedOperation(OperationID, int)};
	 */
	private Map<IntegratedDOSGraphNode, MetadataID> integratedDOSGraphNodeReproducedMetadataIDMap;
	
	/**
	 * update the {@link #integratedDOSGraphNodeReproducedMetadataIDMap} when a reproduced Operation is rolled back;
	 * this will remove the IntegratedDOSGraphNode containing the output Metadata of the reproduced Operation from the {@link #integratedDOSGraphNodeReproducedMetadataIDMap};
	 * also, if the output Metadata is composite, all the component Metadata containing IntegratedDOSGraphNodes will also be removed;
	 * 
	 * invoked from {@link #removeMostRecentlyInsertedOperationFromInsertedSet()};
	 * 
	 * @param originalOperationID
	 * @param copyIndex
	 */
	private void removeReproducedDependentMetadataIDOfAReproducedOperation(OperationID originalOperationID, int copyIndex) {
		this.originalOperationIDCopyIndexToBeReproducedAndInsertedDependingIntegratedDOSGraphNodeSetMapMap.get(originalOperationID).get(copyIndex).forEach(dependingIntegratedDOSGraphNode->{
			this.integratedDOSGraphNodeReproducedMetadataIDMap.remove(dependingIntegratedDOSGraphNode);
		});
	}
	
	/**
	 * invoked from the {@link MetadataID#reproduce(VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)} whenever a MetadataID is newly reproduced;
	 * @param node
	 * @param reproducedMetadataID
	 */
	public void addToIntegratedDOSGraphNodeReproducedMetadataIDMap(IntegratedDOSGraphNode node, MetadataID reproducedMetadataID) {
		this.integratedDOSGraphNodeReproducedMetadataIDMap.put(node, reproducedMetadataID);
	}
	
	public Map<IntegratedDOSGraphNode, MetadataID> getIntegratedDOSGraphNodeReproducedMetadataIDMap(){
		return this.integratedDOSGraphNodeReproducedMetadataIDMap;
	}
	
	
	
	/**
	 * constructor
	 * @param hostVisSchemeAppliedArchiveReproducerAndInserter
	 * @throws SQLException
	 */
	public OperationReproducingAndInsertionTracker(
			VisSchemeAppliedArchiveReproducerAndInserter hostVisSchemeAppliedArchiveReproducerAndInserter
			) throws SQLException{
		//TODO validations
		
		this.hostVisSchemeAppliedArchiveReproducerAndInserter = hostVisSchemeAppliedArchiveReproducerAndInserter;
		
		this.appliedVisScheme = this.hostVisSchemeAppliedArchiveReproducerAndInserter.getHostVisProjectDBContext().getHasIDTypeManagerController().getVisSchemeManager().lookup(this.hostVisSchemeAppliedArchiveReproducerAndInserter.getAppliedArchive().getAppliedVisSchemeID());
		
	}
	
	///////////////////////////////
	/**
	 * initialize and build the {@link #originalOperationIDCopyIndexToBeReproducedAndInsertedDependingIntegratedDOSGraphNodeSetMapMap};
	 * 
	 * then invoke the {@link #updateReproduceableOriginalOperationIDCopyIndexSetMap()}
	 * 
	 * @throws SQLException 
	 */
	public void initialize() throws SQLException {
		this.originalOperationIDCopyIndexToBeReproducedAndInsertedDependingIntegratedDOSGraphNodeSetMapMap = new HashMap<>();
		this.originalOperationIDCopyIndexToBeReproducedAndInsertedIntegratedDOSGraphEdgeSetContainingTheOperationMapMap = new HashMap<>();
		
		Set<IntegratedDOSGraphNode> processedNodeSet = new HashSet<>();
		processedNodeSet.addAll(this.getSolutionSet());
		
		Set<IntegratedDOSGraphEdge> edgeSetOfOperationTypeReadyToProcess = new HashSet<>();
		
		this.getSolutionSet().forEach(n->{
			this.getTrimmedIntegratedDOSGraph().incomingEdgesOf(n).forEach(e->{
				IntegratedDOSGraphNode dependingNode = this.getTrimmedIntegratedDOSGraph().getEdgeSource(e);
				
				if(!processedNodeSet.contains(dependingNode)) {
					if(e.getType().equals(DOSEdgeType.OPERATION)) {
						edgeSetOfOperationTypeReadyToProcess.add(e);
					}else {//composition
						processedNodeSet.add(dependingNode);
					}
				}
			});
		});
		
		while(!edgeSetOfOperationTypeReadyToProcess.isEmpty()) {
			Set<IntegratedDOSGraphEdge> newlyFoundEdgeSetReadyToProcess = new HashSet<>();
			
			edgeSetOfOperationTypeReadyToProcess.forEach(e->{
				//first process the dependingNode which must be resulted from operation;
				IntegratedDOSGraphNode dependingNode = this.getTrimmedIntegratedDOSGraph().getEdgeSource(e);
				
				OperationID operationID = e.getOperationID();
				int copyIndex = e.getCopyIndex();
				
				if(!this.originalOperationIDCopyIndexToBeReproducedAndInsertedDependingIntegratedDOSGraphNodeSetMapMap.containsKey(operationID)) {
					this.originalOperationIDCopyIndexToBeReproducedAndInsertedDependingIntegratedDOSGraphNodeSetMapMap.put(operationID, new HashMap<>());
					this.originalOperationIDCopyIndexToBeReproducedAndInsertedIntegratedDOSGraphEdgeSetContainingTheOperationMapMap.put(operationID, new HashMap<>());
				}
				
				if(!this.originalOperationIDCopyIndexToBeReproducedAndInsertedDependingIntegratedDOSGraphNodeSetMapMap.get(operationID).containsKey(copyIndex)) {
					this.originalOperationIDCopyIndexToBeReproducedAndInsertedDependingIntegratedDOSGraphNodeSetMapMap.get(operationID).put(copyIndex, new HashSet<>());
					this.originalOperationIDCopyIndexToBeReproducedAndInsertedIntegratedDOSGraphEdgeSetContainingTheOperationMapMap.get(operationID).put(copyIndex, new HashSet<>());
				}
				
				this.originalOperationIDCopyIndexToBeReproducedAndInsertedDependingIntegratedDOSGraphNodeSetMapMap.get(operationID).get(copyIndex).add(dependingNode);
				this.originalOperationIDCopyIndexToBeReproducedAndInsertedIntegratedDOSGraphEdgeSetContainingTheOperationMapMap.get(operationID).get(copyIndex).add(e);
				
				processedNodeSet.add(dependingNode);
				
				//then check the incoming edges of the dependingNode
				this.getTrimmedIntegratedDOSGraph().incomingEdgesOf(dependingNode).forEach(ie->{
					IntegratedDOSGraphNode dependingNode2 = this.getTrimmedIntegratedDOSGraph().getEdgeSource(ie);
					
					if(!processedNodeSet.contains(dependingNode2)) {//should always be this case
						if(ie.getType().equals(DOSEdgeType.OPERATION)) {
							newlyFoundEdgeSetReadyToProcess.add(ie);
						}else {//component metadata of the dependingNode
							//add to the originalOperationIDCopyIndexToBeReproducedAndInsertedDependingIntegratedDOSGraphNodeSetMapMap
							this.originalOperationIDCopyIndexToBeReproducedAndInsertedDependingIntegratedDOSGraphNodeSetMapMap.get(operationID).get(copyIndex).add(dependingNode2);
							//add to processedNodeSet
							processedNodeSet.add(dependingNode2);
							
							//then add all the incoming edges (if any) of dependingNode2 to newlyFoundEdgeSetReadyToProcess, because they should all be of Operation type;
							newlyFoundEdgeSetReadyToProcess.addAll(this.getTrimmedIntegratedDOSGraph().incomingEdgesOf(dependingNode2));
						}
					}
				});
			});
			
			
			edgeSetOfOperationTypeReadyToProcess.clear();
			edgeSetOfOperationTypeReadyToProcess.addAll(newlyFoundEdgeSetReadyToProcess);
		}
		
		
		
		
		/////////////////////
		this.reproducedAndInsertedOperationIDList = new ArrayList<>();
		this.reproducedAndInsertedOperationIDOriginalOperationIDCopyIndexPairMap = new HashMap<>();
		this.originalOperationIDCopyIndexReproducedOperationIDMapMap = new HashMap<>();
		this.originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap = new HashMap<>();
		this.integratedDOSGraphNodeReproducedMetadataIDMap = new HashMap<>();
		
		/////////////
		this.updateReproduceableOriginalOperationIDCopyIndexSetMap();
	}
	
	
	/////////////////////////////
	/**
	 * update the {@link #reproduceableOriginalOperationIDCopyIndexSetMap} based on current {@link #reproducedAndInsertedOperationIDList}
	 * 
	 * also update the 
	 * 		{@link #nextReproduceableOriginalOperationCopyIndex}
	 * 		{@link #nextReproduceableOriginalOperationID}
	 * 		{@link #nextReproducedOperation}
	 * 
	 * should be invoked 
	 * 1. after an Operation is reproduced and inserted into host VisProjectDBContext 
	 * 2. after an reproduced and inserted Operation is rolled back;
	 * 
	 * implementation:
	 * 		find out all IntegratedDOSGraphEdges whose 
	 * @throws SQLException 
	 */
	private void updateReproduceableOriginalOperationIDCopyIndexSetMap() throws SQLException {
		//first update the processedIntegratedDOSGraphNodeSet
		this.processedIntegratedDOSGraphNodeSet = new LinkedHashSet<>();
		this.processedIntegratedDOSGraphNodeSet.addAll(this.getSolutionSet());
		
//		this.originalOperationIDCopyIndexReproducedOperationIDMapMap.clear();
		this.originalOperationIDCopyIndexReproducedOperationIDMapMap.forEach((originalOperationID, map)->{
			map.keySet().forEach(copyIndex->{
				this.processedIntegratedDOSGraphNodeSet.addAll(
						this.originalOperationIDCopyIndexToBeReproducedAndInsertedDependingIntegratedDOSGraphNodeSetMapMap
						.get(originalOperationID).get(copyIndex)
						);
			});
		});
		
//		this.reproducedAndInsertedOperationIDList.forEach(insertedOperationID->{
//			Pair<OperationID, Integer> originalOperationIDCopyIndexPair = 
//					this.reproducedAndInsertedOperationIDOriginalOperationIDCopyIndexPairMap.get(insertedOperationID);
//			
//			this.processedIntegratedDOSGraphNodeSet.addAll(
//					this.originalOperationIDCopyIndexToBeReproducedAndInsertedDependingIntegratedDOSGraphNodeSetMapMap
//					.get(originalOperationIDCopyIndexPair.getFirst()).get(originalOperationIDCopyIndexPair.getSecond())
//					);
//			
//			this.originalOperationIDCopyIndexReproducedOperationIDMapMap
//		});
		
		//
		this.reproduceableOriginalOperationIDCopyIndexSetMap = new HashMap<>();
		
		//then find out all IntegratedDOSGraphNodes that are not in the processedIntegratedDOSGraphNodeSet and whose outgoing edges are all targets at IntegratedDOSGraphNodes in the processedIntegratedDOSGraphNodeSet;
		//those IntegratedDOSGraphEdges must be of Operation that are ready to be reproduced;
		Set<IntegratedDOSGraphNode> checkedDependingNodeSet = new HashSet<>();
		processedIntegratedDOSGraphNodeSet.forEach(n->{
			this.getTrimmedIntegratedDOSGraph().incomingEdgesOf(n).forEach(incomingEdge->{
				IntegratedDOSGraphNode dependingNode = this.getTrimmedIntegratedDOSGraph().getEdgeSource(incomingEdge);
				
				if(!processedIntegratedDOSGraphNodeSet.contains(dependingNode)) {//not processed
					if(!checkedDependingNodeSet.contains(dependingNode)) {//not checked
						//check if all the directly depended node of the dependingNode are in the processedIntegratedDOSGraphNodeSet, thus the out going edges of the dependingNode must contain Operation copy ready to reproduce;
						boolean allDependedNodeInProcessedSet = true;
						for(IntegratedDOSGraphEdge outgoingEdge:this.getTrimmedIntegratedDOSGraph().outgoingEdgesOf(dependingNode)){
							if(!processedIntegratedDOSGraphNodeSet.contains(this.getTrimmedIntegratedDOSGraph().getEdgeTarget(outgoingEdge))) {//if there is one directly depended node is not in processedIntegratedDOSGraphNodeSet, skip
								allDependedNodeInProcessedSet = false;
								break;
							}
						}
						
						if(allDependedNodeInProcessedSet) {
							//all outgoing edges must represent the same operation copy!
							IntegratedDOSGraphEdge edge = this.getTrimmedIntegratedDOSGraph().outgoingEdgesOf(dependingNode).iterator().next();
							OperationID originalOperationID = edge.getOperationID();
							int copyIndex = edge.getCopyIndex();
							
							if(!this.reproduceableOriginalOperationIDCopyIndexSetMap.containsKey(originalOperationID))
								this.reproduceableOriginalOperationIDCopyIndexSetMap.put(originalOperationID, new HashSet<>());
							
							this.reproduceableOriginalOperationIDCopyIndexSetMap.get(originalOperationID).add(copyIndex);
						}
						
						checkedDependingNodeSet.add(dependingNode);
					}
				}
			});
		});
		
		
		if(this.reproduceableOriginalOperationIDCopyIndexSetMap.isEmpty()) {
			this.nextReproduceableOriginalOperationCopyIndex = null;
			this.nextReproduceableOriginalOperationID = null;
			this.nextReproducedOperation = null;
		}else {
			this.nextReproduceableOriginalOperationID = this.reproduceableOriginalOperationIDCopyIndexSetMap.keySet().iterator().next();
			this.nextReproduceableOriginalOperationCopyIndex = this.reproduceableOriginalOperationIDCopyIndexSetMap.get(this.nextReproduceableOriginalOperationID).iterator().next();
			
			Operation originalOperation = this.appliedVisScheme.getOperationLookup().lookup(this.nextReproduceableOriginalOperationID);
			
			this.nextReproducedOperation = originalOperation.reproduce(
					this.hostVisSchemeAppliedArchiveReproducerAndInserter.getHostVisProjectDBContext(), 
					this.hostVisSchemeAppliedArchiveReproducerAndInserter, 
					this.nextReproduceableOriginalOperationCopyIndex);
		}
		
	}
	
	
	//////////////////
	/**
	 * return the {@link #nextReproducedOperation};
	 * 
	 * note that if there is no more Operation to be reproduced and inserted, the returned value will be null;
	 * 
	 * also the {@link #nextReproducedOperation} will only be updated after {@link #addNextReproducedOperationToInsertedSet()} or {@link #removeMostRecentlyInsertedOperationFromInsertedSet()} is invoked;
	 * otherwise, its value will stay the same once assigned;
	 * 
	 * @return
	 */
	public Operation nextReproducedOperationToBeInserted() {
		return this.nextReproducedOperation;
	}
	
	
	/**
	 * add the {@link #nextReproducedOperation} to the {@link #reproducedAndInsertedOperationIDList}
	 * 
	 * also update related fields;
	 * 		{@link #originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap}
	 * 		{@link #reproducedAndInsertedOperationIDOriginalOperationIDCopyIndexPairMap}
	 * then invoke the {@link #updateReproduceableOriginalOperationIDCopyIndexSetMap()}
	 * 
	 * this method must be invoked after the {@link #nextReproducedOperation} is successfully inserted into host VisProjectDBContext;
	 * 
	 * @param insertedOperation
	 * @throws SQLException 
	 */
	public void addNextReproducedOperationToInsertedSet() throws SQLException {
		//
		this.reproducedAndInsertedOperationIDList.add(this.nextReproducedOperation.getID());
		
		//
		this.reproducedAndInsertedOperationIDOriginalOperationIDCopyIndexPairMap.put(
				this.nextReproducedOperation.getID(), 
				new Pair<>(this.nextReproduceableOriginalOperationID, this.nextReproduceableOriginalOperationCopyIndex));
		
		//
		if(this.nextReproducedOperation.hasInputDataTableContentDependentParameter()) {//
			if(!this.originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap.containsKey(this.nextReproduceableOriginalOperationID))
				this.originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap.put(this.nextReproduceableOriginalOperationID, new HashMap<>());
			
			Map<SimpleName, Object> inputDataTableContentDependentParameterNameAssignedObjectValueMap = new HashMap<>();
			inputDataTableContentDependentParameterNameAssignedObjectValueMap.putAll(this.nextReproducedOperation.getInputDataTableContentDependentParameterNameValueObjectMap());
			
			this.originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap.get(this.nextReproduceableOriginalOperationID).put(
					this.nextReproduceableOriginalOperationCopyIndex, 
					inputDataTableContentDependentParameterNameAssignedObjectValueMap);
		}
		
		
		///////////
		//update the reproduceable operation copies
		this.updateReproduceableOriginalOperationIDCopyIndexSetMap();
	}
	
	
	
	
	/**
	 * remove most recently inserted Operation from the {@link #reproducedAndInsertedOperationIDList};
	 * 
	 * also update related fields;
	 * 
	 * must be invoked whenever an inserted Operation is rolled back;
	 * 
	 * 1. need to first remove the {@link #nextReproducedOperation} (if not null) from the 
	 * 		{@link #originalOperationIDCopyIndexReproducedOperationIDMapMap} before process the most recently inserted Operation
	 * 		
	 * 2. retrieve the most recently inserted OperationID and remove it from related fields
	 * 		{@link #reproducedAndInsertedOperationIDList}
	 * 		{@link #reproducedAndInsertedOperationIDOriginalOperationIDCopyIndexPairMap}
	 * 		{@link #originalOperationIDCopyIndexReproducedOperationIDMapMap}
	 * 		{@link #originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap}
	 * 
	 * 
	 * 3. 
	 * 
	 * @param insertedOperation
	 * @throws SQLException 
	 */
	public void removeMostRecentlyInsertedOperationFromInsertedSet() throws SQLException {
		//1. need to first remove the nextReproducedOperation (if not null) from the originalOperationIDCopyIndexReproducedOperationIDMapMap before process the most recently inserted Operation
		if(this.nextReproducedOperation!=null) {
			this.originalOperationIDCopyIndexReproducedOperationIDMapMap.get(this.nextReproduceableOriginalOperationID).remove(this.nextReproduceableOriginalOperationCopyIndex);
			if(this.originalOperationIDCopyIndexReproducedOperationIDMapMap.get(this.nextReproduceableOriginalOperationID).isEmpty())
				this.originalOperationIDCopyIndexReproducedOperationIDMapMap.remove(this.nextReproduceableOriginalOperationID);
		}
		
		//2
		OperationID mostRecentlyInsertedOperationID = 
				this.reproducedAndInsertedOperationIDList.remove(this.reproducedAndInsertedOperationIDList.size()-1);
		
		//
		Pair<OperationID, Integer> originalOperationIDCopyIndexPair = 
				this.reproducedAndInsertedOperationIDOriginalOperationIDCopyIndexPairMap.remove(mostRecentlyInsertedOperationID);
		
		//
		this.originalOperationIDCopyIndexReproducedOperationIDMapMap.get(originalOperationIDCopyIndexPair.getFirst()).remove(originalOperationIDCopyIndexPair.getSecond());
		if(this.originalOperationIDCopyIndexReproducedOperationIDMapMap.get(originalOperationIDCopyIndexPair.getFirst()).isEmpty())
			this.originalOperationIDCopyIndexReproducedOperationIDMapMap.remove(originalOperationIDCopyIndexPair.getFirst());
		
		//
		if(this.originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap.containsKey(originalOperationIDCopyIndexPair.getFirst())) {
			this.originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap.get(originalOperationIDCopyIndexPair.getFirst()).remove(originalOperationIDCopyIndexPair.getSecond());
			
			if(this.originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap.get(originalOperationIDCopyIndexPair.getFirst()).isEmpty())
				this.originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap.remove(originalOperationIDCopyIndexPair.getFirst());
			
		}
		
		////
		this.removeReproducedDependentMetadataIDOfAReproducedOperation(originalOperationIDCopyIndexPair.getFirst(), originalOperationIDCopyIndexPair.getSecond());
		
		////
		this.updateReproduceableOriginalOperationIDCopyIndexSetMap();
	}
	
	
	/**
	 * 
	 * @return
	 */
	public boolean allOperationsAreReproducedAndInserted() {
		return this.reproduceableOriginalOperationIDCopyIndexSetMap.isEmpty();
	}
	
	
	//////////////////

	private SimpleDirectedGraph<IntegratedDOSGraphNode, IntegratedDOSGraphEdge> getTrimmedIntegratedDOSGraph(){
		return this.hostVisSchemeAppliedArchiveReproducerAndInserter.getAppliedArchive().getTrimmedIntegratedDOSGraph();
	}
	
	private Set<IntegratedDOSGraphNode> getSolutionSet(){
		return this.hostVisSchemeAppliedArchiveReproducerAndInserter.getAppliedArchive().getSelectedSolutionSetNodeMappingMap().keySet();
	}
	
	/**
	 * @return the originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap
	 */
	public Map<OperationID, Map<Integer, Map<SimpleName, Object>>> getOriginalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap() {
		return originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap;
	}
	
	
	//////////////////////////
	/**
	 * retrieve and return the set of IntegratedDOSGraphEdge that contains the original Operation copy of the given reproducedOperationID
	 * 
	 * @param reproducedOperationID
	 * @return
	 */
	public Set<IntegratedDOSGraphEdge> getIntegratedDOSGraphEdgeSet(OperationID originalOperationID, int copyIndex){
//		Pair<OperationID, Integer> originalOperationIDCopyIndexPair = 
//				this.reproducedAndInsertedOperationIDOriginalOperationIDCopyIndexPairMap.get(reproducedOperationID);
		
		return this.originalOperationIDCopyIndexToBeReproducedAndInsertedIntegratedDOSGraphEdgeSetContainingTheOperationMapMap.get(originalOperationID).get(copyIndex);
	}

	/**
	 * @return the reproducedAndInsertedOperationIDOriginalOperationIDCopyIndexPairMap
	 */
	public Map<OperationID, Pair<OperationID, Integer>> getReproducedAndInsertedOperationIDOriginalOperationIDCopyIndexPairMap() {
		return reproducedAndInsertedOperationIDOriginalOperationIDCopyIndexPairMap;
	}
	

	
	/**
	 * @return the reproducedAndInsertedOperationIDList
	 */
	public List<OperationID> getReproducedAndInsertedOperationIDList() {
		return reproducedAndInsertedOperationIDList;
	}

}
