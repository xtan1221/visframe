package dependency.vcd;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import context.VisframeContext;
import context.scheme.VSComponent;
import context.scheme.VSComponentPrecedenceList;
import dependency.cfd.SimpleCFDGraph;
import dependency.cfd.SimpleCFDGraphBuilder;
import dependency.dos.SimpleDOSGraph;
import dependency.dos.SimpleDOSGraphBuilder;
import exception.VisframeException;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroup;
import function.group.CompositionFunctionGroupID;
import metadata.MetadataID;
import metadata.SourceType;
import metadata.record.RecordDataMetadata;
import operation.Operation;
import operation.OperationID;

/**
 * base class to build a VCD graph with a VSComponent precedence list
 * @author tanxu
 */
public class VCDGraphBuilder {
	private final VisframeContext hostVisframeContext;
	private final VSComponentPrecedenceList precedenceList;
	
	///////////////
	//maps to facilitate visframe entity assignment and edge identification
	private Map<VSComponent, VCDNodeImpl> componentNodeMap;
	private Map<MetadataID, VCDNodeImpl> metadataIDAssignedVCDNodeMap;
	private Map<OperationID, VCDNodeImpl> operationIDAssignedVCDNodeMap;
	private Map<CompositionFunctionGroupID, VCDNodeImpl> compositionFunctionGroupIDAssignedVCDNodeMap;
	private Map<CompositionFunctionID, VCDNodeImpl> compositionFunctionIDAssignedVCDNodeMap;
	
	/**
	 * underlying graph
	 */
	protected SimpleDirectedGraph<VCDNodeImpl, VCDEdgeImpl> underlyingGraph;
	
	
	/**
	 * 
	 */
	protected VCDGraph VCDGraph;
	
	
	/**
	 * constructor
	 * @param hostVisframeContext not null
	 * @param componentPrecedenceList not null or empty
	 * @throws SQLException 
	 */
	public VCDGraphBuilder(VisframeContext hostVisframeContext, VSComponentPrecedenceList precedenceList) throws SQLException{
		if(hostVisframeContext==null)
			throw new IllegalArgumentException("given hostVisframeContext cannnot be null!");
		
		if(precedenceList==null)
			throw new IllegalArgumentException("given precedenceList cannnot be null!");
		
		this.hostVisframeContext = hostVisframeContext;
		this.precedenceList = precedenceList;
		
		///////////////
		this.buildUnderlyingGraph();
			
		this.buildVCDGraph();
	}
	////////////////////////////////////////
	public VCDGraph getVCDGraph() {
		return this.VCDGraph;
	}

	VisframeContext getHostVisframeContext() {
		return hostVisframeContext;
	}
	

	VSComponentPrecedenceList getPrecedenceList() {
		return precedenceList;
	}

	/////////////////////////////////////////
	/**
	 * build the {@link #underlyingGraph};
	 * 
	 * 
	 * 1. assign visframe entities to each VCDNodeImpl/VSComponent
	 * 		Metadata
	 *			If resulted from operation, assigned to the same VSComponent as the operation
	 *			If component of a composite Metadata, assigned to the same VSComponent as the composite Metadata;
	 *			Otherwise, assigned to the first VSComponent on whose DOS graph the Metadata is present;
	 *		Operation
	 *			Assigned to the first VSCompnent on whose DOS graph the operation is present;
	 *		CFG
	 *			Assigned to the first VSComponent on whose CFD graph one or more CF of the CFG is present;
	 *		CF
	 *			Assign every CF to the same VSComponent as the host CFG;
	 *
	 * 2. create VCDEdgeImpl and find out the specific dependency types represented by each of them
	 * 		1. For every operation assigned to VSComponent c_i, add an edge for each input Metadata md from c_i to the VSComponent c_j to which md is assigned if j<i;
	 *		2. For every CFG assigned to VSComponent c_i, add an edge from c_i to c_j to which the owner record Metadata of the CFG is assigned;
	 *	 	3. For every CF assigned to VSComponent c_i, add an edge for each depended CF cf from c_i to the VSComponent c_j to which cf is assigned if j<i;
	 *		4. For every CF assigned to VSComponent c_i, add an edge for each depended record Metadata rmd from c_i to the VSComponent c_j to which rmd is assigned if j<i;
	 *
	 *		!!!!!note that edges based on dependency on assigned visframe entity are not explicitly added since such dependency should be reflected by a directed path(with one or more edges) on the VCD graph built with above strategy;
	 *		
	 * @throws SQLException 
	 * 
	 * 
	 */
	protected void buildUnderlyingGraph() throws SQLException {
		this.underlyingGraph = new SimpleDirectedGraph<>(VCDEdgeImpl.class);
		
		this.componentNodeMap = new HashMap<>();
		this.metadataIDAssignedVCDNodeMap = new HashMap<>();
		this.operationIDAssignedVCDNodeMap = new HashMap<>();
		this.compositionFunctionGroupIDAssignedVCDNodeMap = new HashMap<>();
		this.compositionFunctionIDAssignedVCDNodeMap = new HashMap<>();
		
		for(VSComponent component:this.precedenceList.getList()) {
			VCDNodeImpl vcdNode = new VCDNodeImpl(component, this.precedenceList.getList().indexOf(component));
			this.underlyingGraph.addVertex(vcdNode);
			this.componentNodeMap.put(component, vcdNode);
			
			//first build the cfd graph and dos graph induced by the core shapeCFGs of the component
			Set<CompositionFunctionID> initialCFIDSet = new HashSet<>();
			for(CompositionFunctionGroupID cfgID:component.getCoreShapeCFGIDSet()) {
				initialCFIDSet.addAll(this.hostVisframeContext.getCompositionFunctionIDSetOfGroupID(cfgID));
			}
			
			SimpleCFDGraph cfdGraph = new SimpleCFDGraphBuilder(this.hostVisframeContext, initialCFIDSet).getBuiltGraph();
		
			SimpleDOSGraph dosGraph = new SimpleDOSGraphBuilder(this.hostVisframeContext, cfdGraph.getDependedRecordMetadataIDInputVariableDataTableColumnNameSetMap().keySet()).getBuiltGraph();
			
			//==================
			//assign visframe entities present on CFD and DOS graph to components
			//1. assign operation on DOS graph to current vcd node if not assigned yet
			for(OperationID operationID:dosGraph.getOperationIDSet()) {
				if(!this.operationIDAssignedVCDNodeMap.containsKey(operationID)) {//not assigned yet
					//assign the opreationID
					this.operationIDAssignedVCDNodeMap.put(operationID, vcdNode);
					vcdNode.assignOperationID(operationID);
					
					//assign output metadata of the operation to current node
					this.hostVisframeContext.getOperationLookup().lookup(operationID).getOutputMetadataIDSet().forEach(e->{
						if(this.metadataIDAssignedVCDNodeMap.containsKey(e)) //already assigned, throw exception;
							throw new VisframeException("MetadataID already assigned!");
						
						this.metadataIDAssignedVCDNodeMap.put(e, vcdNode);
						vcdNode.assignMetadataID(e);
					});
				}
			}
			
			//2. assign metadata on DOS graph
			//2.1 first assign all composite(generic graph) metadata to current vcd node if not assigned yet
			for(MetadataID metadataID: dosGraph.getMetadataIDSet()) {
				if(metadataID.getDataType().isGenericGraph()) {
					if(!this.metadataIDAssignedVCDNodeMap.containsKey(metadataID)) {//not assigned yet
						//the composite metadata must be an imported metadata, since if it is resulted from operation, it should have been added in the operationID part
						//directly assign it to the current vcdnode
						this.metadataIDAssignedVCDNodeMap.put(metadataID, vcdNode);
						vcdNode.assignMetadataID(metadataID);
					}
				}
			}
			
			//2.2 then assign all record metadata if not assigned yet
			for(MetadataID metadataID: dosGraph.getMetadataIDSet()) {
				if(!metadataID.getDataType().isGenericGraph()) {
					if(!this.metadataIDAssignedVCDNodeMap.containsKey(metadataID)) {
						//the record metadata 
						RecordDataMetadata rmd = (RecordDataMetadata) this.hostVisframeContext.getMetadataLookup().lookup(metadataID);
						if(rmd.getSourceType().equals(SourceType.STRUCTURAL_COMPONENT)) {
							//assigned component record metadata to the same vcd node with the composite data
							this.metadataIDAssignedVCDNodeMap.put(metadataID, this.metadataIDAssignedVCDNodeMap.get(rmd.getSourceCompositeDataMetadataID()));
							this.metadataIDAssignedVCDNodeMap.get(rmd.getSourceCompositeDataMetadataID()).assignMetadataID(metadataID);
							
						}else {//if not structural component of composite data, must be imported, because if resulted from operation, should already be processed in the operation part;
							//directly assign to the current vcd node
							this.metadataIDAssignedVCDNodeMap.put(metadataID, vcdNode);
							vcdNode.assignMetadataID(metadataID);
						}
					}
				}
			}
			
			//3. assign CFG and CF on CFD graph
			for(CompositionFunctionGroupID cfgID:cfdGraph.getInvolvedCFGIDCFIDSetMap().keySet()) {
				if(!this.compositionFunctionGroupIDAssignedVCDNodeMap.containsKey(cfgID)) {
					this.compositionFunctionGroupIDAssignedVCDNodeMap.put(cfgID, vcdNode);
					vcdNode.assignCompositionFunctionGroupID(cfgID);
				}
				
				cfdGraph.getInvolvedCFGIDCFIDSetMap().get(cfgID).forEach(e->{
					if(!this.compositionFunctionIDAssignedVCDNodeMap.containsKey(e))//not assigned yet
						//assign the cf to the vcd node to which the host cfg is assigned
						this.compositionFunctionIDAssignedVCDNodeMap.put(e, this.compositionFunctionGroupIDAssignedVCDNodeMap.get(cfgID));
						this.compositionFunctionGroupIDAssignedVCDNodeMap.get(cfgID).assignCompositionFunctionID(e);
				});
			}
			
			
			
			//=================================
			//create edges to directly depended nodes and add the dependency details to the edge
			
			//1. For every operation assigned to current node, for each input Metadata, if it is assigned to a different vcd node(with higher precedence), add an edge from current node to that node
			for(OperationID operationID:vcdNode.getAssignedOperationIDSet()){
				Operation operation = this.hostVisframeContext.getOperationLookup().lookup(operationID);
				
				operation.getInputMetadataIDSet().forEach(imid->{
					if(!this.metadataIDAssignedVCDNodeMap.get(imid).equals(vcdNode)) {//assigned to a different vcd node with higher precedence
						VCDEdgeImpl edge;
						if(this.underlyingGraph.containsEdge(vcdNode, this.metadataIDAssignedVCDNodeMap.get(imid))) {
							edge = this.underlyingGraph.getEdge(vcdNode, this.metadataIDAssignedVCDNodeMap.get(imid));
						}else {
							edge = new VCDEdgeImpl(vcdNode.getVSComponent(), this.metadataIDAssignedVCDNodeMap.get(imid).getVSComponent());
							this.underlyingGraph.addEdge(vcdNode, this.metadataIDAssignedVCDNodeMap.get(imid), edge);
						}
						//add dependency details
						edge.addOperationIDInputMetadataIDDependency(operationID, imid);
					}
				});
			}
			
			//2. For every CFG assigned to current vcd node, if its owner record metadata is assigned to a different node (with higher precedence) 
			//add an edge from current node to that node;
			for(CompositionFunctionGroupID cfgID: vcdNode.getAssignedCFGIDSet()) {
				CompositionFunctionGroup cfg = this.hostVisframeContext.getCompositionFunctionGroupLookup().lookup(cfgID);
				
				VCDNodeImpl ownerRecordAssignedNode = this.metadataIDAssignedVCDNodeMap.get(cfg.getOwnerRecordDataMetadataID());
				if(!ownerRecordAssignedNode.equals(vcdNode)) {
					VCDEdgeImpl edge;
					if(this.underlyingGraph.containsEdge(vcdNode, ownerRecordAssignedNode)) {
						edge = this.underlyingGraph.getEdge(vcdNode, ownerRecordAssignedNode);
					}else {
						edge = new VCDEdgeImpl(vcdNode.getVSComponent(), ownerRecordAssignedNode.getVSComponent());
						this.underlyingGraph.addEdge(vcdNode, ownerRecordAssignedNode, edge);
					}
					//add dependency details
					edge.addCfgIDOwnerRecordMetadataIDDependency(cfgID, cfg.getOwnerRecordDataMetadataID());
				}
				
			}
			
			//3 For every CF assigned to current vcd node, 
			//3.1 for each of the depended cf assigned to a different vcd node (with higher precedence), add an edge from current vcd node to that node;
			//3.2 for each of the depended record metadata assigned to a different vcd node (with higher precedence), add an edge from current vcd node to that node;
			for(CompositionFunctionID cfID:vcdNode.getAssignedCFIDSet()) {
				CompositionFunction cf = this.hostVisframeContext.getCompositionFunctionLookup().lookup(cfID);
				
				//depended cf
				cf.getDependedCompositionFunctionIDSetByThisOne(hostVisframeContext).forEach(e->{
					VCDNodeImpl dependedCFAssignedNode = this.compositionFunctionIDAssignedVCDNodeMap.get(e);
					if(!dependedCFAssignedNode.equals(vcdNode)) {
						VCDEdgeImpl edge;
						if(this.underlyingGraph.containsEdge(vcdNode, dependedCFAssignedNode)) {
							edge = this.underlyingGraph.getEdge(vcdNode, dependedCFAssignedNode);
						}else {
							edge = new VCDEdgeImpl(vcdNode.getVSComponent(), dependedCFAssignedNode.getVSComponent());
							this.underlyingGraph.addEdge(vcdNode, dependedCFAssignedNode, edge);
						}
						//add dependency details
						edge.addCfIDDependedCfIDDependency(cfID, e);
					}
				});
				
				//depended record metadata
				cf.getDependedRecordMetadataIDInputColumnNameSetMap(hostVisframeContext).keySet().forEach(e->{
					VCDNodeImpl dependedRecordMetadataAssignedNode = this.metadataIDAssignedVCDNodeMap.get(e);
					if(!dependedRecordMetadataAssignedNode.equals(vcdNode)) {
						VCDEdgeImpl edge;
						if(this.underlyingGraph.containsEdge(vcdNode, dependedRecordMetadataAssignedNode)) {
							edge = this.underlyingGraph.getEdge(vcdNode, dependedRecordMetadataAssignedNode);
						}else {
							edge = new VCDEdgeImpl(vcdNode.getVSComponent(), dependedRecordMetadataAssignedNode.getVSComponent());
							this.underlyingGraph.addEdge(vcdNode, dependedRecordMetadataAssignedNode, edge);
						}
						//add dependency details
						edge.addCfIDDependedRecordMetadataIDDependency(cfID, e);
					}
				});
			}
		}
		
		//find out the depended and depending node set of each node in the constructed vcd graph;
		this.underlyingGraph.vertexSet().forEach(v->{
			Set<VCDNodeImpl> dependedNodeSet = new HashSet<>();
			Set<VCDNodeImpl> dependingNodeSet = new HashSet<>();
			
			//depending nodes
			this.underlyingGraph.incomingEdgesOf(v).forEach(e->{
				dependingNodeSet.add(this.underlyingGraph.getEdgeSource(e));
			});
			
			//depended nodes
			this.underlyingGraph.outgoingEdgesOf(v).forEach(e->{
				dependedNodeSet.add(this.underlyingGraph.getEdgeTarget(e));
			});
			
			v.setDependedVCDNodeSet(dependedNodeSet);
			v.setDependingVCDNodeSet(dependingNodeSet);
		});
	}
	
	/**
	 * build the VCDGraph using the built {@link #underlyingGraph}
	 * if it is null; return;
	 * if the VCDGraph is successfully built, set the successfullyBuilt to true;
	 */
	private void buildVCDGraph() {
		this.VCDGraph = new VCDGraph(this.underlyingGraph, this.precedenceList);
	}
	
}
