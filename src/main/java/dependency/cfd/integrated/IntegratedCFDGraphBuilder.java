package dependency.cfd.integrated;

import org.jgrapht.graph.SimpleDirectedGraph;

import dependency.cfd.CFDEdgeImpl;
import dependency.cfd.CFDNodeImpl;
import dependency.cfd.SimpleCFDGraph;
import dependency.vccl.VCCLEdge;
import dependency.vccl.VSCopy;
import dependency.vcd.VCDGraph;
import dependency.vcd.VCDNodeImpl;
import function.composition.CompositionFunctionID;

/**
 * builder class for CFD graph with
 * 
 * 1. each node being a CompositionFunction and a vscopy index corresponding to one of the assigned copy number to the assigned VCDNode of the CompositionFunction;
 * 
 * 2. each edge being from the node of depending cf and depending vsccopy index to the node of depended cf and depended vscopy index;
 * 
 * @author tanxu
 *
 */
public class IntegratedCFDGraphBuilder {
	private final VCDGraph vcdGraph;
	/**
	 * CFD graph of the applied VisScheme
	 */
	private final SimpleCFDGraph cfdGraph;
	
	/**
	 * vccl graph
	 */
	private final SimpleDirectedGraph<VSCopy, VCCLEdge> vcclGraph;
	
	
	////////////
	private SimpleDirectedGraph<IntegratedCFDGraphNode, IntegratedCFDGraphEdge> integratedCFDUnderlyingGraph;
	

	/**
	 * constructor
	 * @param vcdGraph
	 * @param cfdGraph
	 * @param vcclGraph
	 */
	public IntegratedCFDGraphBuilder(
			VCDGraph vcdGraph,
			SimpleCFDGraph cfdGraph,
			SimpleDirectedGraph<VSCopy, VCCLEdge> vcclGraph
			){
		//
		
		this.vcdGraph = vcdGraph;
		this.cfdGraph = cfdGraph;
		this.vcclGraph = vcclGraph;
		
		this.build();
	}
	
	
	/**
	 * 1. initialize the iDOSUnderlyingGraph
	 * 2. make and add nodes
	 * 3. add edges
	 */
	private void build() {
		this.integratedCFDUnderlyingGraph = new SimpleDirectedGraph<>(IntegratedCFDGraphEdge.class);
		
		this.addNodes();
		
		this.addEdges();
	}
	
	private void addNodes() {
		//make and add nodes
		this.vcclGraph.vertexSet().forEach(v->{
			v.makeIntegratedCFDNodes();
			v.getAssignedCFIDIntegratedCFDNodeMap().values().forEach(n->{
				this.integratedCFDUnderlyingGraph.addVertex(n);
			});
		});
	}
	
	/**
	 * add edges
	 */
	private void addEdges() {
		//for each VSCopy
		for(VSCopy targetCopy:this.vcclGraph.vertexSet()) {
			//for each assigned CompositionFunction cf
			for(CompositionFunctionID cfid:targetCopy.getAssignedCFIDIntegratedCFDNodeMap().keySet()) {
				CFDNodeImpl cfdNode = this.cfdGraph.getCFIDNodeMap().get(cfid);
				//for each depended CompositionFunction cfu of cf on the cfd graph;
				for(CFDEdgeImpl cfdEdge:this.cfdGraph.getUnderlyingGraph().outgoingEdgesOf(cfdNode)){
					CFDNodeImpl dependedCFDNode = this.cfdGraph.getUnderlyingGraph().getEdgeTarget(cfdEdge);
					CompositionFunctionID dependedCfID = dependedCFDNode.getCFID();
					
					//find out the assigned VCDNode of cfu
					VCDNodeImpl dependedVCDNode = this.vcdGraph.getCFIDAssignedVCDNodeMap().get(dependedCfID);
					
					if(dependedVCDNode.equals(targetCopy.getOwnerVCDNode())) {
						IntegratedCFDGraphNode dependingNode = targetCopy.getAssignedCFIDIntegratedCFDNodeMap().get(cfid);
						IntegratedCFDGraphNode dependedNode = targetCopy.getAssignedCFIDIntegratedCFDNodeMap().get(dependedCfID);
						IntegratedCFDGraphEdge edge = new IntegratedCFDGraphEdge(
								dependingNode,
								dependedNode,
								cfdEdge.isBasedOnIndependentFreeInputVariableType(),
								cfdEdge.isBasedOnAssignedTarget()
//								dependingNode.getCopyIndex()
								);
						//the depended cf is assigned to the same VCDNode as the depending cf
						//thus the copy of the depended Metadata must be the same one
						this.integratedCFDUnderlyingGraph.addEdge(
								dependingNode, 
								dependedNode, 
								edge);
					}else {
						//find out the copy of dependedVCDNode to which the target copy is linked
						VSCopy linkedVSCopy = targetCopy.getDependedVCDNodeLinkedCopyMap().get(dependedVCDNode);
						IntegratedCFDGraphNode dependingNode = targetCopy.getAssignedCFIDIntegratedCFDNodeMap().get(cfid);
						IntegratedCFDGraphNode dependedNode = linkedVSCopy.getAssignedCFIDIntegratedCFDNodeMap().get(dependedCfID);
						IntegratedCFDGraphEdge edge = new IntegratedCFDGraphEdge(
								dependingNode,
								dependedNode,
								cfdEdge.isBasedOnIndependentFreeInputVariableType(),
								cfdEdge.isBasedOnAssignedTarget()
//								dependingNode.getCopyIndex()
								);
						
						this.integratedCFDUnderlyingGraph.addEdge(
								dependingNode, 
								dependedNode, 
								edge);
					}
				}
			}
		}
	}
	
	//////////////////

	/**
	 * @return the iCFDUnderlyingGraph
	 */
	public SimpleDirectedGraph<IntegratedCFDGraphNode, IntegratedCFDGraphEdge> getBuiltIntegratedCFDUnderlyingGraph() {
		return integratedCFDUnderlyingGraph;
	}

}
