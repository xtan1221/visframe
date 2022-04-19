package context.scheme;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import basic.lookup.scheme.type.VisSchemeCompositionFunctionGroupLookup;
import basic.lookup.scheme.type.VisSchemeCompositionFunctionLookup;
import basic.lookup.scheme.type.VisSchemeIndependentFreeInputVariableTypeLookup;
import basic.lookup.scheme.type.VisSchemeMetadataLookup;
import basic.lookup.scheme.type.VisSchemeOperationLookup;
import context.project.VisProjectDBContext;
import dependency.cfd.SimpleCFDGraph;
import dependency.dos.SimpleDOSGraph;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroup;
import function.group.CompositionFunctionGroupID;
import function.variable.independent.IndependentFreeInputVariableType;
import function.variable.independent.IndependentFreeInputVariableTypeID;
import metadata.DataType;
import metadata.Metadata;
import metadata.MetadataID;
import metadata.SourceType;
import metadata.graph.GraphDataMetadata;
import metadata.graph.GraphDataMetadataUtils;
import metadata.graph.vftree.VfTreeDataMetadata;
import metadata.graph.vftree.VfTreeDataMetadataUtils;
import metadata.graph.vftree.VfTreeMandatoryEdgeDataTableSchemaUtils;
import metadata.graph.vftree.VfTreeMandatoryNodeDataTableSchemaUtils;
import metadata.record.RecordDataMetadata;
import metadata.record.RecordDataMetadataUtils;
import operation.Operation;
import operation.OperationID;
import rdb.table.data.DataTableColumnName;

/**
 * an implementation of VisSchemeBuilderBase
 * 
 * @author tanxu
 *
 */
public class VisSchemeBuilderImpl extends VisSchemeBuilderBase {
	
	/**
	 * map from the record data MetadataID in the built DOS graph 
	 * to the set of column names that are explicitly used as one or more of the following
	 *
	 * 1. input variable of some evaluators of some CompositionFunction on the CFD graph
	 * 2. input parameter of some operations on the DOS graph;
	 * 
	 * this map can be built by union of the 
	 * {@link SimpleCFDGraph#getDependedRecordMetadataIDInputVariableDataTableColumnNameSetMap()} and
	 * {@link SimpleDOSGraph#getOperationInputRecordMetadataIDInputColumnNameSetMap()}
	 * 
	 * note that for some record Metadata that is not depended by any CF/CFG and used as input metadata of any Operation, they should still be put in this map;
	 * 		such record Metadata are either
	 * 		1. component record data of a Composite metadata and the component record data is not in the inducing set of the DOS graph and not depended by any Metadata in the inducing set;
	 * 		2. output metadata of operation and the output Metadata is not in the inducing set of the DOS graph and not depended by any Metadata in the inducing set;
	 * 
	 * =============================
	 * facilitate to construct the metadata in the metadata lookup of the VisScheme together with the {@link #involvedMetadataID}
	 * 		specifically, facilitate to identify the set of input columns of each record metadata in the DOS graph;
	 */
	private Map<MetadataID, Set<DataTableColumnName>> involvedRecordDataMetadataIDInputColumnNameSetMap;
	
	/**
	 * the full set of MetadataID involved in the target VisScheme;
	 * should be the same set with the {@link SimpleDOSGraph#getMetadataIDSet()}
	 * 
	 * =============================
	 * facilitate to construct the metadata in the metadata lookup of the VisScheme together with the {@link #involvedRecordDataMetadataIDInputColumnNameSetMap}
	 */
	private Set<MetadataID> involvedMetadataID;
	
	/**
	 * Metadata involved in CFD and DOS
	 */
	private Map<MetadataID, Metadata> metadataIDMap;
	
	////////////////////////////////////////////////
	/**
	 * the set of Operations in the target VisScheme;
	 * should be the same set with the {@link SimpleDOSGraph#getOperationIDSet()}
	 */
	private Map<OperationID, Operation> operationIDMap;
	
	//////////////////////////////////////////////
	/**
	 * the set of CompositionFunctionGroups whose one ore more CompositionFunctions are nodes on the CFD graph;
	 * 
	 */
	private Map<CompositionFunctionGroupID, CompositionFunctionGroup> compositionFunctionGroupIDMap;
	
	///////////////////////////////////////////////
	/**
	 * the set of CompositionFunctions that are present on the CFD graph as nodes;
	 */
	private Map<CompositionFunctionID, CompositionFunction> compositionFunctionIDMap;
	
	private Map<CompositionFunctionGroupID,Map<SimpleName, CompositionFunctionID>> compositionFunctionGroupIDTargetNameAssignedCFIDMapMap;
	
	//////////////////////////////////////////////
	/**
	 * the set of IndependentFreeInputVariableTypes that are owned by CompositionFunctions in {@link #compositionFunctionIDMap}
	 */
	private Map<IndependentFreeInputVariableTypeID, IndependentFreeInputVariableType> independentFreeInputVariableTypeIDMap;
	
	
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param name
	 * @param notes
	 * @param componentPrecedenceList
	 */
	public VisSchemeBuilderImpl(
			VisProjectDBContext hostVisProjectDBContext, 
			SimpleName name, VfNotes notes,
			List<VSComponent> componentPrecedenceList) {
		super(hostVisProjectDBContext, name, notes, componentPrecedenceList);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	protected void buildMetadataLookup() throws SQLException {
		this.metadataIDMap = new HashMap<>();
		
		//=================================step 1. preprocessing
		this.involvedMetadataID = this.simpleDOSGraph.getMetadataIDSet();
		
		//
		this.involvedRecordDataMetadataIDInputColumnNameSetMap = new HashMap<>();
		this.involvedMetadataID.forEach(mid->{
			if(mid.getDataType().equals(DataType.RECORD)) {
				this.involvedRecordDataMetadataIDInputColumnNameSetMap.put(mid, new HashSet<>());
			}
		});
		
		//add the explicitly depended/input columns in CFD and DOS graph
		this.simpleCFDGraph.getDependedRecordMetadataIDInputVariableDataTableColumnNameSetMap().forEach((mid,colNameSet)->{
			this.involvedRecordDataMetadataIDInputColumnNameSetMap.get(mid).addAll(colNameSet);
		});
		this.simpleDOSGraph.getOperationInputRecordMetadataIDInputColumnNameSetMap().forEach((mid,colNameSet)->{
			this.involvedRecordDataMetadataIDInputColumnNameSetMap.get(mid).addAll(colNameSet);
		});
		//CANNOT use putAll method in this scenario, the second will over-write the first;
//		this.involvedRecordDataMetadataIDInputColumnNameSetMap.putAll(this.simpleCFDGraph.getDependedRecordMetadataIDInputVariableDataTableColumnNameSetMap());
//		this.involvedRecordDataMetadataIDInputColumnNameSetMap.putAll(this.simpleDOSGraph.getOperationInputRecordMetadataIDInputColumnNameSetMap());
		
		//if of component of GRAPH data
		//1. node feature record data
		//2. edge feature record data
		//if of component of VFTree data
		//1. node feature record data
		//2. edge feature record data
		
		//add the mandatory additional feature columns of any node/edge component record data of VfTreeDataMetadata if their parent VfTree data is also included in the DOS graph
		for(MetadataID id:this.involvedRecordDataMetadataIDInputColumnNameSetMap.keySet()) {
			
			RecordDataMetadata originalRecordData = (RecordDataMetadata) this.getHostVisProjectDBContext().getMetadataLookup().lookup(id);
			
			//first add all primary key column names;
			this.involvedRecordDataMetadataIDInputColumnNameSetMap.get(id).addAll(originalRecordData.getDataTableSchema().getPrimaryKeyColumnNameSet());
			
			//if of component record data of a composite data;
			if(originalRecordData.getSourceType().equals(SourceType.STRUCTURAL_COMPONENT)) {
				if(originalRecordData.getSourceCompositeDataMetadataID().getDataType().equals(DataType.GRAPH)) {
					//graph data
					if(originalRecordData.isOfGenericGraphNode()) {
						//node feature
						//do nothing;
						//additional feature columns are added only if they are used as depended/input column in CFG graph or DOS graph;
						
					}else {//edge feature
						GraphDataMetadata graphData = (GraphDataMetadata)this.getHostVisProjectDBContext().getMetadataLookup().lookup(originalRecordData.getSourceCompositeDataMetadataID());
						
						if(graphData.getGraphEdgeFeature().isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets()) {
							//source/sink node id columns should always be added if disjoint with edge id column set;
							this.involvedRecordDataMetadataIDInputColumnNameSetMap.get(id).addAll(graphData.getGraphEdgeFeature().getNodeIDColumnNameEdgeSourceNodeIDColumnNameMap().keySet());
							this.involvedRecordDataMetadataIDInputColumnNameSetMap.get(id).addAll(graphData.getGraphEdgeFeature().getNodeIDColumnNameEdgeSinkNodeIDColumnNameMap().keySet());
						}
					}
				}else if(originalRecordData.getSourceCompositeDataMetadataID().getDataType().equals(DataType.vfTREE)) {
					//vftree data
					if(originalRecordData.isOfGenericGraphNode()) {
						//node feature 
						//all mandatory additional feature columns should be added
						this.involvedRecordDataMetadataIDInputColumnNameSetMap.get(id).addAll(VfTreeMandatoryNodeDataTableSchemaUtils.getMandatoryAdditionalFeatureColumnNameList());
					}else {
						//edge feature
						//all mandatory additional feature columns should be added
						this.involvedRecordDataMetadataIDInputColumnNameSetMap.get(id).addAll(VfTreeMandatoryEdgeDataTableSchemaUtils.getMandatoryAdditionalFeatureColumnNameList());
					}
				}else {
					//other types of composite data, not implemented yet
				}
			}
		}
		
		//=======================================step 2. build Metadata
		//first build the record data metadata based on the original ones
		//every thing is the same with the original one except the non-Primary key columns of the data table schema;
		//in detail, all primary key set columns and non-primary key set columns in the involvedRecordDataMetadataIDInputColumnNameSetMap will be included;
		Map<MetadataID, RecordDataMetadata> rebuiltRecordDataMetadataIDMap = new HashMap<>();
		for(MetadataID id:this.involvedMetadataID) {
			if(id.getDataType().equals(DataType.RECORD)) {
				RecordDataMetadata originalRecordData = (RecordDataMetadata) this.getHostVisProjectDBContext().getMetadataLookup().lookup(id);
				
				rebuiltRecordDataMetadataIDMap.put(
						id, 
						RecordDataMetadataUtils.buildNewWithMinimalColumnSet(
								originalRecordData, 
								this.involvedRecordDataMetadataIDInputColumnNameSetMap.get(id)));
			}
		}
		
		//build the GRAPH type metadata based on original ones; (not including the vfTREE data)
		//everything is the same except the GraphVertexFeature and GraphEdgeFeature;
		//only those additionalFeatureColumnNameSet of the node record data in the involvedRecordDataMetadataIDInputColumnNameSetMap will be included;
		//only those additionalFeatureColumnNameSet of the edge record data in the involvedRecordDataMetadataIDInputColumnNameSetMap will be included;
		Map<MetadataID, GraphDataMetadata> rebuiltGraphDataMetadataIDMap = new HashMap<>();
		for(MetadataID id:this.involvedMetadataID) {
			if(id.getDataType().equals(DataType.GRAPH)) {
				GraphDataMetadata originalGraphData = (GraphDataMetadata) this.getHostVisProjectDBContext().getMetadataLookup().lookup(id);
				
				rebuiltGraphDataMetadataIDMap.put(
						id, 
						GraphDataMetadataUtils.makeNewGraphDataMetadata(
								originalGraphData, 
								this.involvedRecordDataMetadataIDInputColumnNameSetMap.get(originalGraphData.getNodeRecordMetadataID()), 
								this.involvedRecordDataMetadataIDInputColumnNameSetMap.get(originalGraphData.getEdgeRecordMetadataID()))
						);
			}
		}
		
		//build the vfTREE type metadata based on the original ones;
		//everything is the same except the VfTreeVertexFeature and VfTreeEdgeFeature;
		//all mandatory additional feature columns will be auto-included together with the id columns
		//for non-mandatory additional features columns, only those in the involvedRecordDataMetadataIDInputColumnNameSetMap will be included;
		Map<MetadataID, VfTreeDataMetadata> rebuiltVfTreeDataMetadataIDMap = new HashMap<>();
		for(MetadataID id:this.involvedMetadataID) {
			if(id.getDataType().equals(DataType.vfTREE)) {
				VfTreeDataMetadata originalVftreeData = (VfTreeDataMetadata) this.getHostVisProjectDBContext().getMetadataLookup().lookup(id);
				
				rebuiltVfTreeDataMetadataIDMap.put(
						id, 
						VfTreeDataMetadataUtils.makeNewGraphDataMetadata(
								originalVftreeData, 
								this.involvedRecordDataMetadataIDInputColumnNameSetMap.get(originalVftreeData.getNodeRecordMetadataID()), 
								this.involvedRecordDataMetadataIDInputColumnNameSetMap.get(originalVftreeData.getEdgeRecordMetadataID()))
						);
			}
		}
		
		/////////
		this.metadataIDMap.putAll(rebuiltRecordDataMetadataIDMap);
		this.metadataIDMap.putAll(rebuiltGraphDataMetadataIDMap);
		this.metadataIDMap.putAll(rebuiltVfTreeDataMetadataIDMap);
		
		this.metadataLookup = new VisSchemeMetadataLookup(this.metadataIDMap);
	}
	
	
	@Override
	protected void buildOperationLookup() throws SQLException {
		this.operationIDMap = new HashMap<>();

		for(OperationID id:this.simpleDOSGraph.getOperationIDSet()){
			Operation operation = this.getHostVisProjectDBContext().getOperationLookup().lookup(id);
			this.operationIDMap.put(id, operation);
		}
		
		this.operationLookup = new VisSchemeOperationLookup(this.operationIDMap);
	}
	
	/**
	 * this method will build the CFG, CF and IndependentFIVType lookup together
	 * @throws SQLException 
	 */
	@Override
	protected void buildCompositionFunctionGroupLookup() throws SQLException {
		this.compositionFunctionGroupIDMap = new HashMap<>();
		this.compositionFunctionIDMap = new HashMap<>();
		this.compositionFunctionGroupIDTargetNameAssignedCFIDMapMap = new HashMap<>();
		this.independentFreeInputVariableTypeIDMap = new HashMap<>();
		
		for(CompositionFunctionGroupID cfgID:this.simpleCFDGraph.getInvolvedCFGIDCFIDSetMap().keySet()) {
			CompositionFunctionGroup cfg = this.getHostVisProjectDBContext().getCompositionFunctionGroupLookup().lookup(cfgID);
			
			this.compositionFunctionGroupIDMap.put(cfgID, cfg);
			this.compositionFunctionGroupIDTargetNameAssignedCFIDMapMap.put(cfgID, new HashMap<>());
			
			for(CompositionFunctionID cfID:this.simpleCFDGraph.getInvolvedCFGIDCFIDSetMap().get(cfgID)) {
				CompositionFunction cf = this.getHostVisProjectDBContext().getCompositionFunctionLookup().lookup(cfID);
				
				this.compositionFunctionIDMap.put(cfID, cf);
				
				cf.getAssignedTargetNameSet().forEach(target->{
					this.compositionFunctionGroupIDTargetNameAssignedCFIDMapMap.get(cfgID).put(target, cfID);
				});
				
				//update the independentFreeInputVariableTypeIDMap
				cf.getIndependentFreeInputVariableTypeIDMap().forEach((k,v)->{
					if(k.getOwnerCompositionFunctionID().equals(cfID)) {
						
							this.independentFreeInputVariableTypeIDMap.put(k, v);
						
					}
						
				});
				
			}
		}
		
		///////////////////
		this.compositionFunctionGroupLookup = new VisSchemeCompositionFunctionGroupLookup(this.compositionFunctionGroupIDMap);
		this.compositionFunctionLookup = new VisSchemeCompositionFunctionLookup(this.compositionFunctionIDMap, this.compositionFunctionGroupIDTargetNameAssignedCFIDMapMap);
		this.independentFreeInputVariableTypeLookup = new VisSchemeIndependentFreeInputVariableTypeLookup(this.independentFreeInputVariableTypeIDMap);
	}
	
	
	
	
	/**
	 * see {@link #buildCompositionFunctionGroupLookup()}
	 */
	@Override
	protected void buildCompositionFunctionLookup() {
		//do nothing
	}
	
	/**
	 * see {@link #buildCompositionFunctionGroupLookup()}
	 */
	@Override
	protected void buildIndependentFreeInputVariableTypeLookup() {
		//do nothing
	}
	
}
