package dependency.dos;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import context.VisframeContext;
import dependency.dos.DOSEdge.DOSEdgeType;
import metadata.CompositeDataMetadata;
import metadata.Metadata;
import metadata.MetadataID;
import metadata.SourceType;
import operation.Operation;
import rdb.table.data.DataTableColumnName;


/**
 * DOSGraphBuilder implementation for a regular DOSGraph induced by a set of target Metadata from a VisframeContext;
 * 
 * NOTE that edge direction is from the output Metadata to input Metadata if the edge is operation; or from child component metadata to parent composite metadata if the edge type is COMPOSITE_DATA_COMPONENT
 * 
 * ===================
 * important notes!! (120920)
 * 
 * 1. if a Composite Metadata is included in the DOS graph (either it is in the inducing set and/or it is directly or indirectly depended by some Metadata in the inducing set);
 * 		all of its component Metadata should be included in the DOS graph as well, no matter they are in the inducing set and/or depended by any Metadata from the inducing set or not;
 * 
 * 		for example, for a Graph type Metadata, 
 * 		if its node record Metadata is in inducing set I or depended by one Metadata in I, but its edge record data is neither in I nor depended by any Metadata in I,
 * 		the built DOS should still contain the graph Metadata, node record metadata and the edge record metadata;
 * 
 * 2. if an Operation is included in the DOS (at least one of its output Metadata is in the inducing set and/or is directly or indirectly depended by some Metadata in the inducing set);
 * 		all of its output Metadata should be included in the DOS graph, no matter they are in the inducing set and/or depended by some Metadata in the inducing set or not;
 * 		
 * 		note that in current visframe version, only Operation types with single output Metadata are included;		
 * 
 * the above two points are consistent with the designed strategy to apply a VisScheme to build a VisSchemeAppliedArchive;
 * 		specifically, when building the integrated DOS graph, the nodes representing the Metadata not directly and indirectly depended by any depended record data of the trimmed integrated CFD graph will all be trimmed;
 * 
 * see {@link #buildUnderlyingGraph()} for implementation details;
 * @author tanxu
 *
 */
public class SimpleDOSGraphBuilder{
	
	private final VisframeContext hostVisframeContext;
	private final Set<MetadataID> inducingMetadataIDSet;
	
	////////////////////////////
	protected SimpleDirectedGraph<DOSNodeImpl, DOSEdgeImpl> underlyingGraph;
	
	
	protected SimpleDOSGraph DOSGraph;
	
	/**
	 * map from record data MetadataID to the set of column names used as input parameter of one or more Operations on the DOS graph;
	 * 
	 * extracted with {@link Operation#getInputRecordMetadataIDInputColumnNameSetMap()} method of all {@link Operation}s on the DOS graph;
	 */
	private Map<MetadataID, Set<DataTableColumnName>> operationInputRecordMetadataIDInputColumnNameSetMap;
	
	
	/////////////////
	/**
	 * set of MetadataID whose source is checked and the whose node and edge to the source is added to the graph;
	 * when the DOS graph is fully constructed, all MetadataID on the graph should be put in this set;
	 */
	private Set<MetadataID> checkedMetadataIDSet;
	/**
	 * set of MetadataID that is identified to be on the DOS graph but not added yet; thus whose source is not checked;
	 * when the DOS graph is fully constructed, this set should be empty;
	 */
	private Set<MetadataID> uncheckedMetadataIDSet;
	
	
	/**
	 * constructor
	 * @param hostVisframeContext
	 * @param inducingMetadataIDSet not null, can be empty
	 */
	public SimpleDOSGraphBuilder(VisframeContext hostVisframeContext, Set<MetadataID> inducingMetadataIDSet) {
		
		if(inducingMetadataIDSet==null)
			throw new IllegalArgumentException("given inducingMetadataIDSet cannot be null!");
		
		this.hostVisframeContext = hostVisframeContext;
		this.inducingMetadataIDSet = inducingMetadataIDSet;
		
		try {
			this.buildUnderlyingGraph();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("SQLException is thrown during building the undering graph:"+e.getMessage());
		}
		
		this.buildDOSGraph();
	}
	

	protected VisframeContext getHostVisframeContext() {
		return hostVisframeContext;
	}
	
	
	public Set<MetadataID> getInducingMetadataIDSet() {
		return inducingMetadataIDSet;
	}
	
	public SimpleDOSGraph getBuiltGraph() {
		return DOSGraph;
	}
	
	
	////////////////////////////
	/**
	 * build the underlying SimpleDirectedGraph as well as the {@link #metadataIDSet} and {@link #operationInputRecordMetadataIDInputColumnNameSetMap};
	 * 
	 * NOTE that edge direction is from the output Metadata to input Metadata if the edge is operation; 
	 * or from child component metadata to parent composite metadata if the edge type is COMPOSITE_DATA_COMPONENT
	 * 
	 * ======================
	 * see {@link SimpleDOSGraphBuilder} for strategy on how to deal with 
	 * 1. composite Metadata with a subset of component Metadata in inducing set or depended by some in inducing set
	 * 2. operation with a subset of output Metadata in inducing set or depended by some in inducing set
	 * 
	 * @throws SQLException 
	 * 
	 */
	protected void buildUnderlyingGraph() throws SQLException {
		this.underlyingGraph = new SimpleDirectedGraph<>(DOSEdgeImpl.class);
		this.operationInputRecordMetadataIDInputColumnNameSetMap = new HashMap<>();
		Map<MetadataID, DOSNodeImpl> metadataIDDOSNodeMap = new HashMap<>();
		
		
		//first add all metadataID in the inducing metadata id set as nodes to the underlying graph
		//this is done so in case sink and source metadata are both included in the inducing set; ??
		this.getInducingMetadataIDSet().forEach(mid->{
			DOSNodeImpl node = new DOSNodeImpl(mid);
			this.underlyingGraph.addVertex(node);
			metadataIDDOSNodeMap.put(mid, node);
		});
		
		/////////initialize
		this.checkedMetadataIDSet = new HashSet<>();
		this.uncheckedMetadataIDSet = new HashSet<>();
		this.uncheckedMetadataIDSet.addAll(this.getInducingMetadataIDSet());

		//
		while(!this.uncheckedMetadataIDSet.isEmpty()) {
			//retrieve an unchecked MetadataID 
			MetadataID id = this.uncheckedMetadataIDSet.iterator().next();
			Metadata metadata = this.getHostVisframeContext().getMetadataLookup().lookup(id);
			
			//first add the metadataID as a node to the underlying graph;
			DOSNodeImpl node = new DOSNodeImpl(id);
			this.underlyingGraph.addVertex(node);
			metadataIDDOSNodeMap.put(id, node);
			
			//check source type
			if(metadata.getSourceType().equals(SourceType.IMPORTED)) {
				//root is reached, no depended nodes, do nothing
			}else if(metadata.getSourceType().equals(SourceType.RESULT_FROM_OPERATION)){
				//add one edge from the MetadataID to each input MetadataID of the operation;
				//for each of the input Metadata, if they are not in the checked set, add them to the unchecked set!!!
				//otherwise, do nothing
				Operation operation = this.getHostVisframeContext().getOperationLookup().lookup(metadata.getSourceOperationID());
				operation.getInputMetadataIDSet().forEach(in->{
					DOSEdgeImpl edge = new DOSEdgeImpl(id, in, DOSEdgeType.OPERATION, metadata.getSourceOperationID());
					
					DOSNodeImpl inputMetadataNode;
					if(metadataIDDOSNodeMap.containsKey(in)) {
						inputMetadataNode = metadataIDDOSNodeMap.get(in);
					}else {
						inputMetadataNode = new DOSNodeImpl(in);
						metadataIDDOSNodeMap.put(in, inputMetadataNode);
						this.underlyingGraph.addVertex(inputMetadataNode);
					}
					
					this.underlyingGraph.addEdge(metadataIDDOSNodeMap.get(id), inputMetadataNode, edge);
					
					if(!this.checkedMetadataIDSet.contains(in) && !this.uncheckedMetadataIDSet.contains(in)) {
						this.uncheckedMetadataIDSet.add(in);
					}
				});
				
				//note that current visframe does not support Operation types with multiple output Metadata;
				//however, in future versions, this type of Operation may be added;
				//if an Operation of such type is included in a DOS graph, all of its output Metadata should be included as nodes as well as the edges
				//if other output Metadata is not in the checked set, add them to the unchecked set;
				//note that if already in checked set, the edges from the other output Metadata to the input Metadata should have already been created in previous iterations;
				//if not in the check set, such edges will dealt with later after being added to the unchecked set;
				//thus no need to add the edges here
				operation.getOutputMetadataIDSet().forEach(out->{
					if(!id.equals(out)) { //other output Metadata
						if(!this.checkedMetadataIDSet.contains(out) && !this.uncheckedMetadataIDSet.contains(out)) {//not dealt with before, add to unchecked set;
							this.uncheckedMetadataIDSet.add(out);
						}
					}
				});
				
				//update the inputRecordMetadataIDInputColumnNameSetMap
				operation.getInputRecordMetadataIDInputColumnNameSetMap().forEach((k,v)->{
					if(!this.operationInputRecordMetadataIDInputColumnNameSetMap.containsKey(k))
						this.operationInputRecordMetadataIDInputColumnNameSetMap.put(k, new HashSet<>());
					
					this.operationInputRecordMetadataIDInputColumnNameSetMap.get(k).addAll(v);
				});
				
			}else if(metadata.getSourceType().equals(SourceType.STRUCTURAL_COMPONENT)){
				
				CompositeDataMetadata parentCompositeData = (CompositeDataMetadata)this.hostVisframeContext.getMetadataLookup().lookup(metadata.getSourceCompositeDataMetadataID());
				//
				DOSNodeImpl compositeMetadataNode;
				if(metadataIDDOSNodeMap.containsKey(parentCompositeData.getID())) {
					compositeMetadataNode = metadataIDDOSNodeMap.get(parentCompositeData.getID());
				}else {
					compositeMetadataNode = new DOSNodeImpl(parentCompositeData.getID());
					metadataIDDOSNodeMap.put(parentCompositeData.getID(), compositeMetadataNode);
					this.underlyingGraph.addVertex(compositeMetadataNode);
				}
				
				//add a edge from the MetadataID to the parent composite metadata
				DOSEdgeImpl edge = new DOSEdgeImpl(id, parentCompositeData.getID(), DOSEdgeType.COMPOSITE_DATA_COMPONENT, null);
				this.underlyingGraph.addEdge(metadataIDDOSNodeMap.get(id), compositeMetadataNode, edge);
				
				//check other component record Metadata of the parent composite Metadata, if not in the checked set, add them to the unchecked set;
				parentCompositeData.getComponentRecordDataMetadataIDSet().forEach(componentRecordData->{
					if(!id.equals(componentRecordData)) {
						if(!this.checkedMetadataIDSet.contains(componentRecordData) && !this.uncheckedMetadataIDSet.contains(componentRecordData)) {//not dealt with before, add to unchecked set;
							this.uncheckedMetadataIDSet.add(componentRecordData);
						}
					}
				});
				
				//for parent composite Metadata, if it is not in the checked set, add it to the unchecked set!!!
				//otherwise, do nothing
				if(!this.checkedMetadataIDSet.contains(metadata.getSourceCompositeDataMetadataID()) && !this.uncheckedMetadataIDSet.contains(metadata.getSourceCompositeDataMetadataID())) {
					this.uncheckedMetadataIDSet.add(metadata.getSourceCompositeDataMetadataID());
				}
			}
			
			//remove from the unchecked set
			this.uncheckedMetadataIDSet.remove(id);
			
			//add to the checked set
			this.checkedMetadataIDSet.add(id);
		}
		
	}
	
	private void buildDOSGraph() {
		//build the DAG; CycleFoundInDependencyGraphException will be thrown from the constructor if cycle found
		this.DOSGraph = new SimpleDOSGraph(
				this.underlyingGraph, this.getInducingMetadataIDSet(), 
//				this.metadataIDSet, this.operationIDSet, 
				this.operationInputRecordMetadataIDInputColumnNameSetMap);
	}

}
