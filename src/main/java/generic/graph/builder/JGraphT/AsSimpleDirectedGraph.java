package generic.graph.builder.JGraphT;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;

/**
 * create a simple directed graph (no self-loop, no multi-edge, no edge weight, etc) with only the structural information of the graph kept based on a undirected graph;
 * 
 * 
 * @author tanxu
 *
 */
public class AsSimpleDirectedGraph {
	private final Graph<VfGraphVertex,VfGraphEdge> inputUndirectedGraph;
	
	////////////////////////////////////
	private SimpleDirectedGraph<Integer,DefaultEdge> simpleDirectedGraph;
	
	/**
	 * constructor
	 * @param inputUndirectedGraph
	 */
	AsSimpleDirectedGraph(Graph<VfGraphVertex,VfGraphEdge> inputUndirectedGraph){
		this.inputUndirectedGraph = inputUndirectedGraph;
		this.build();
	}
	
	
	private void build() {
		this.simpleDirectedGraph = new SimpleDirectedGraph<>(DefaultEdge.class);
		
		for(VfGraphVertex vertex:this.inputUndirectedGraph.vertexSet()) {
			this.simpleDirectedGraph.addVertex(vertex.hashCode());
		}
		
		for(VfGraphEdge edge:this.inputUndirectedGraph.edgeSet()) {
			VfGraphVertex sourceNode = edge.getDummySourceVertex();
			VfGraphVertex sinkNode = edge.getDummySinkVertex();
			
			this.simpleDirectedGraph.addEdge(sourceNode.hashCode(), sinkNode.hashCode());
			this.simpleDirectedGraph.addEdge(sinkNode.hashCode(), sourceNode.hashCode());
		}
	}

	
	/**
	 * @return the simpleDirectedGraph
	 */
	public SimpleDirectedGraph<Integer, DefaultEdge> getSimpleDirectedGraph() {
		return simpleDirectedGraph;
	}

}
