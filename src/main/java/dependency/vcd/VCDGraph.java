package dependency.vcd;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jgrapht.graph.SimpleDirectedGraph;

import context.scheme.VSComponent;
import context.scheme.VSComponentPrecedenceList;
import dependency.DAG;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;
import metadata.MetadataID;
import operation.OperationID;


/**
 * 
 * @author tanxu
 *
 */
public class VCDGraph extends DAG<VCDNodeImpl, VCDEdgeImpl> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5721296839354331758L;
	
	
	///////////////////////
	private final VSComponentPrecedenceList precedenceList;
	
	//////////////////////
	private transient Map<VSComponent, VCDNodeImpl> componentVCDNodeMap;
	
	private transient Map<MetadataID, VCDNodeImpl> metadataIDAssignedVCDNodeMap;
	private transient Map<OperationID, VCDNodeImpl> operationIDAssignedVCDNodeMap;
	private transient Map<CompositionFunctionGroupID, VCDNodeImpl> cfgIDAssignedVCDNodeMap;
	private transient Map<CompositionFunctionID, VCDNodeImpl> cfIDAssignedVCDNodeMap;
	
	/**
	 * constructor
	 * @param underlyingGraph
	 */
	public VCDGraph(SimpleDirectedGraph<VCDNodeImpl, VCDEdgeImpl> underlyingGraph,
			VSComponentPrecedenceList precedenceList) {
		super(underlyingGraph,VCDEdgeImpl.class);
		// TODO Auto-generated constructor stub
		
		
		this.precedenceList = precedenceList;
	}
	
	
	public VSComponentPrecedenceList getPrecedenceList() {
		return precedenceList;
	}

	

	/**
	 * return the map from VSComponent to the VCDNodeImpl;
	 * @return
	 */
	public Map<VSComponent, VCDNodeImpl> getComponentVCDNodeMap() {
		if(this.componentVCDNodeMap==null) {
			this.componentVCDNodeMap = new HashMap<>();
			
			this.getUnderlyingGraph().vertexSet().forEach(e->{
				this.componentVCDNodeMap.put(e.getVSComponent(), e);
			});
			
		}
		return componentVCDNodeMap;
	}
	
	
	/**
	 * return the assigned VCDNode of the given MetadataID;
	 * 
	 * @param metadataID
	 * @return
	 */
	public Map<MetadataID, VCDNodeImpl> getMetadataIDAssignedVCDNodeMap() {
		if(this.metadataIDAssignedVCDNodeMap == null) {
			this.metadataIDAssignedVCDNodeMap = new HashMap<>();
			
			for(VCDNodeImpl node:this.getUnderlyingGraph().vertexSet()) {
				for(MetadataID m:node.getAssignedMetadataIDSet()) {
					this.metadataIDAssignedVCDNodeMap.put(m, node);
				}
			}
		}
		
		return Collections.unmodifiableMap(this.metadataIDAssignedVCDNodeMap);
	}
	
	/**
	 * return the assigned VCDNode of the given MetadataID;
	 * 
	 * @param metadataID
	 * @return
	 */
	public Map<OperationID, VCDNodeImpl> getOperationIDAssignedVCDNodeMap() {
		if(this.operationIDAssignedVCDNodeMap == null) {
			this.operationIDAssignedVCDNodeMap = new HashMap<>();
			
			
			for(VCDNodeImpl node:this.getUnderlyingGraph().vertexSet()) {
				for(OperationID o:node.getAssignedOperationIDSet()) {
					this.operationIDAssignedVCDNodeMap.put(o, node);
				}
			}
		}
		
		return Collections.unmodifiableMap(this.operationIDAssignedVCDNodeMap);
	}
	
	/**
	 * return the assigned VCDNode of the given MetadataID;
	 * 
	 * @param metadataID
	 * @return
	 */
	public Map<CompositionFunctionGroupID, VCDNodeImpl> getCFGIDAssignedVCDNodeMap() {
		if(this.cfgIDAssignedVCDNodeMap == null) {
			this.cfgIDAssignedVCDNodeMap = new HashMap<>();
			
			
			for(VCDNodeImpl node:this.getUnderlyingGraph().vertexSet()) {
				for(CompositionFunctionGroupID cfgid:node.getAssignedCFGIDSet()) {
					this.cfgIDAssignedVCDNodeMap.put(cfgid,node);
				}
			}
		}
		
		return Collections.unmodifiableMap(this.cfgIDAssignedVCDNodeMap);
	}
	
	
	
	/**
	 * return the assigned VCDNode of the given MetadataID;
	 * 
	 * @param metadataID
	 * @return
	 */
	public Map<CompositionFunctionID, VCDNodeImpl> getCFIDAssignedVCDNodeMap() {
		if(this.cfIDAssignedVCDNodeMap == null) {
			this.cfIDAssignedVCDNodeMap = new HashMap<>();
			
			
			for(VCDNodeImpl node:this.getUnderlyingGraph().vertexSet()) {
				for(CompositionFunctionID cfid:node.getAssignedCFIDSet()) {
					this.cfIDAssignedVCDNodeMap.put(cfid,node);
				}
			}
		}
		
		return Collections.unmodifiableMap(this.cfIDAssignedVCDNodeMap);
	}
	

	
	//////////////////////////////
//	//TODO
//	@Override
//	public VCDGraph deepClone() {
//		SimpleDirectedGraph<VCDNodeImpl, VCDEdgeImpl> clonedUnderlyingGraph = new SimpleDirectedGraph<>(this.getEdgeType());
//		//deep clone every node and add it to the clonedUnderlyingGraph
//		for(VCDNodeImpl node:this.getUnderlyingGraph().vertexSet()) {
//			clonedUnderlyingGraph.addVertex(node.deepClone());//down cast
//		}
//		
//		//deep clone every node and add it to the clonedUnderlyingGraph
//		for(VCDEdgeImpl edge:this.getUnderlyingGraph().edgeSet()) {
//			clonedUnderlyingGraph.addEdge(
//					this.getUnderlyingGraph().getEdgeSource(edge).deepClone(), 
//					this.getUnderlyingGraph().getEdgeTarget(edge).deepClone(),
//					edge.deepClone()
//					);
//		}
//		
//		List<VSComponent> list = new ArrayList<>(); 
//		list.addAll(this.precedenceList.getList());
//		
//		VCDGraph deepClonedVCDGraph = new VCDGraph(clonedUnderlyingGraph, new VSComponentPrecedenceList(list));
//		
//		return deepClonedVCDGraph;
//	}
	
}
