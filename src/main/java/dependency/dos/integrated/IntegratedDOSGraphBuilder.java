package dependency.dos.integrated;

import org.jgrapht.graph.SimpleDirectedGraph;

import dependency.dos.DOSEdgeImpl;
import dependency.dos.DOSNodeImpl;
import dependency.dos.SimpleDOSGraph;
import dependency.vccl.VCCLEdge;
import dependency.vccl.VSCopy;
import dependency.vcd.VCDGraph;
import dependency.vcd.VCDNodeImpl;
import metadata.MetadataID;

/**
 * builder class for an integrated DOS graph with each node represent a metadata and a vscopy index;
 * 
 * 1. each node being a metadata and a vscopy index of the vcdnode to which the metadata is assigned and corresponding to one of the assigned copy number;
 * 
 * 2. each edge being from a node of output/component metadata and depending vscopy index to the node of source/composite metadata and depended vscopy index;
 * 
 * @author tanxu
 * 
 */
public class IntegratedDOSGraphBuilder {
	private final VCDGraph vcdGraph;
	/**
	 * DOS graph of the applied VisScheme
	 */
	private final SimpleDOSGraph dosGraph;
	/**
	 * 
	 */
	private final SimpleDirectedGraph<VSCopy, VCCLEdge> vcclGraph;
	
	///////////////////////////////////
	/**
	 * 
	 */
	private SimpleDirectedGraph<IntegratedDOSGraphNode, IntegratedDOSGraphEdge> integratedDOSUnderlyingGraph;
	
	/**
	 * @return the iDOSUnderlyingGraph
	 */
	public SimpleDirectedGraph<IntegratedDOSGraphNode, IntegratedDOSGraphEdge> getBuiltIntegratedDOSUnderlyingGraph() {
		return integratedDOSUnderlyingGraph;
	}

	/**
	 * 
	 * @param vcdGraph
	 * @param dosGraph
	 * @param vcclGraph
	 */
	public IntegratedDOSGraphBuilder(
			VCDGraph vcdGraph,
			SimpleDOSGraph dosGraph,
			SimpleDirectedGraph<VSCopy, VCCLEdge> vcclGraph
			){
		//
		
		
		this.vcdGraph = vcdGraph;
		this.dosGraph = dosGraph;
		this.vcclGraph = vcclGraph;
		
		this.build();
	}
	
	/**
	 * 1. initialize the iDOSUnderlyingGraph
	 * 2. make and add nodes
	 * 3. add edges
	 */
	private void build() {
		this.integratedDOSUnderlyingGraph = new SimpleDirectedGraph<>(IntegratedDOSGraphEdge.class);
		
		this.addNodes();
		
		this.addEdges();
	}
	
	private void addNodes() {
		//make and add nodes
		this.vcclGraph.vertexSet().forEach(v->{
			v.makeIntegratedDOSNodes();
			v.getAssignedMetadataIDIntegratedDOSNodeMap().values().forEach(n->{
				this.integratedDOSUnderlyingGraph.addVertex(n);
			});
		});
	}
	
	/**
	 * add edges
	 */
	private void addEdges() {
		//for each VSCopy
		for(VSCopy targetCopy:this.vcclGraph.vertexSet()) {
			//for each assigned Metadata m
			for(MetadataID metadataID:targetCopy.getAssignedMetadataIDIntegratedDOSNodeMap().keySet()) {
				DOSNodeImpl dosNode = this.dosGraph.getMetadataIDDOSNodeMap().get(metadataID);
				//for each depended Metadata md of m;
				for(DOSEdgeImpl dosEdge:this.dosGraph.getUnderlyingGraph().outgoingEdgesOf(dosNode)){
					DOSNodeImpl dependedDOSNode = this.dosGraph.getUnderlyingGraph().getEdgeTarget(dosEdge);
					MetadataID dependedMetadataID = dependedDOSNode.getMetadataID();
					
					//find out the assigned VCDNode of md
					VCDNodeImpl dependedVCDNode = this.vcdGraph.getMetadataIDAssignedVCDNodeMap().get(dependedMetadataID);
					
					if(dependedVCDNode.equals(targetCopy.getOwnerVCDNode())) { 
						IntegratedDOSGraphNode dependingNode = targetCopy.getAssignedMetadataIDIntegratedDOSNodeMap().get(metadataID);
						IntegratedDOSGraphNode dependedNode = targetCopy.getAssignedMetadataIDIntegratedDOSNodeMap().get(dependedMetadataID);
						IntegratedDOSGraphEdge edge = new IntegratedDOSGraphEdge(
								dependingNode,
								dependedNode,
								dosEdge.getType(),
								dosEdge.getOperationID(),
								dependingNode.getCopyIndex()
								);
						//the depended MetadataID is assigned to the same VCDNode as the depending Metadata
						//thus the copy of the depended Metadata must be the same one
						this.integratedDOSUnderlyingGraph.addEdge(
								dependingNode, 
								dependedNode, 
								edge);
					}else {
						IntegratedDOSGraphNode dependingNode = targetCopy.getAssignedMetadataIDIntegratedDOSNodeMap().get(metadataID);
						//find out the copy of dependedVCDNode to which the target copy is linked
						VSCopy linkedVSCopy = targetCopy.getDependedVCDNodeLinkedCopyMap().get(dependedVCDNode);
						IntegratedDOSGraphNode dependedNode = linkedVSCopy.getAssignedMetadataIDIntegratedDOSNodeMap().get(dependedMetadataID);
						
						IntegratedDOSGraphEdge edge = new IntegratedDOSGraphEdge(
								dependingNode,
								dependedNode,
								dosEdge.getType(),
								dosEdge.getOperationID(),
								dependingNode.getCopyIndex()
								);
						this.integratedDOSUnderlyingGraph.addEdge(
								dependingNode, 
								dependedNode, 
								edge);
					}
				}
			}
		}
	}
}
