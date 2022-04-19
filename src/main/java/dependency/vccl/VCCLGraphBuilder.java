package dependency.vccl;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.graph.SimpleDirectedGraph;

import dependency.cfd.integrated.IntegratedCFDGraphBuilder;
import dependency.dos.integrated.IntegratedDOSGraphBuilder;
import dependency.vccl.utils.DAGNodeCopyLinkAssigner;
import dependency.vccl.utils.NodeCopy;
import dependency.vccl.utils.helper.CopyLink;
import dependency.vcd.VCDNodeImpl;

/**
 * helper class that 
 * 
 * 1. builds a vccl underlying graph with node type being VSCopy and edge type being VCCLEdge from 
 * a copy link graph built by a {@link DAGNodeCopyLinkAssigner};
 * 
 * 2. also will set the dependedVCDNodeLinkedCopyMap of each VSCopy node after the VCCL graph is built;
 * 		{@link VSCopy#setDependedVCDNodeLinkedCopyMap(java.util.Map)};
 * 
 * 
 * ========================
 * note that the built copy link graph is used to build the integrated cfd graph with {@link IntegratedCFDGraphBuilder}
 * and dos graph {@link IntegratedDOSGraphBuilder} in the process of building a VisSchemeAppliedArchive;
 * 
 * @author tanxu
 * 
 */
public class VCCLGraphBuilder {
	///////////////
//	private final SimpleDirectedGraph<VCDNodeImpl, VCDEdgeImpl> fullVCDGraph;
	/**
	 * built by {@link DAGNodeCopyLinkAssigner}
	 */
	private final SimpleDirectedGraph<NodeCopy<VCDNodeImpl>, CopyLink<VCDNodeImpl>> fullCopyLinkGraph;
	
	/////////////////////
	/**
	 * 
	 */
	private SimpleDirectedGraph<VSCopy, VCCLEdge> builtVCCLGraph;
	
	
	/**
	 * constructor
	 * @param vcdGraph
	 */
	public VCCLGraphBuilder(
//			SimpleDirectedGraph<VCDNodeImpl, VCDEdgeImpl> fullVCDGraph,
			SimpleDirectedGraph<NodeCopy<VCDNodeImpl>, CopyLink<VCDNodeImpl>> fullCopyLinkGraph){
		
//		this.fullVCDGraph = fullVCDGraph;
		this.fullCopyLinkGraph = fullCopyLinkGraph;
		
		this.build();
		
		this.setDependedVCDNodeLinkedCopyMapOfAllVSCopies();
	}
	
	
	private void build() {
		this.builtVCCLGraph = new SimpleDirectedGraph<>(VCCLEdge.class);
				
		//first create and add nodes
		this.fullCopyLinkGraph.vertexSet().forEach(v->{
			v.getNode().getVSCopyIndexMap().values().forEach(c->{
				this.builtVCCLGraph.addVertex(c);
			});
		});
		
		
		//create and add copy links
		this.fullCopyLinkGraph.edgeSet().forEach(e->{
			NodeCopy<VCDNodeImpl> dependingCopy = this.fullCopyLinkGraph.getEdgeSource(e);
			NodeCopy<VCDNodeImpl> dependedCopy = this.fullCopyLinkGraph.getEdgeTarget(e);
			
			VSCopy dependingVSCopy = dependingCopy.getNode().getVSCopyIndexMap().get(dependingCopy.getCopyIndex());
			
			VSCopy dependedVSCopy = dependedCopy.getNode().getVSCopyIndexMap().get(dependedCopy.getCopyIndex());
			
			VCCLEdge edge = new VCCLEdge(dependingVSCopy, dependedVSCopy);
			this.builtVCCLGraph.addEdge(
					dependingVSCopy, 
					dependedVSCopy, 
					edge);
		});
	}
	
	
	/**
	 * set the dependedVCDNodeLinkedCopyMap for each VSCopy on the built vccl graph
	 * 		use the method {@link VSCopy#setDependedVCDNodeLinkedCopyMap(Map)};
	 */
	private void setDependedVCDNodeLinkedCopyMapOfAllVSCopies() {
		Map<VSCopy, Map<VCDNodeImpl, VSCopy>> vscopyDependedVCDNodeLinkedVSCopyMapMap = new HashMap<>();
		//first initialize the map for each VSCopy 
		this.builtVCCLGraph.vertexSet().forEach(v->{
			vscopyDependedVCDNodeLinkedVSCopyMapMap.put(v, new HashMap<>());
		});
		
		//
		this.builtVCCLGraph.edgeSet().forEach(e->{
			VSCopy dependingVSCopy = this.builtVCCLGraph.getEdgeSource(e);
			VSCopy dependedVSCopy = this.builtVCCLGraph.getEdgeTarget(e);
			
			vscopyDependedVCDNodeLinkedVSCopyMapMap.get(dependingVSCopy).put(dependedVSCopy.getOwnerVCDNode(), dependedVSCopy);
		});
		
		
		vscopyDependedVCDNodeLinkedVSCopyMapMap.forEach((copy,map)->{
			copy.setDependedVCDNodeLinkedCopyMap(map);
		});
		
	}
	
	///////////////////////////////////
	/**
	 * @return the builtVCCLGraph
	 */
	public SimpleDirectedGraph<VSCopy, VCCLEdge> getBuiltVCCLGraph() {
		return builtVCCLGraph;
	}
	
}
