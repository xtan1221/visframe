package generic.graph.builder.JGraphT;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;

import generic.graph.UndirectedGraphCycleDetector;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;


/**
 * build a {@link UndirectedGraphCycleDetector} with the input undirected JGraphT Graph;
 * 
 * 
 * need to assign a unique integer id to each vertex of the graph;
 * 
 * 
 * 
 * ====================================
 * note that by assigning a id to a integer field in VfGraphVertex is not working!!!!!!
 * 		1. VfGraphVertex.id field
 * 		2. this.inputUndirectedGraph.getEdgeSource(e).getId() will always return 0!!!!!
 * 
 * 
 * 
 * 
 * @author tanxu
 *
 */
public class JGraphTUndirectedGraphCycleDetector {
	private final Graph<VfGraphVertex,VfGraphEdge> inputUndirectedGraph;
	
	////////////////////////
	private UndirectedGraphCycleDetector detector;
	
	////
	private Map<VfGraphVertex, Integer> vertexIDMap;
	
	private Boolean cyclic = null;
	
	
	/**
	 * constructor
	 * @param inputUndirectedGraph
	 */
	public JGraphTUndirectedGraphCycleDetector(Graph<VfGraphVertex,VfGraphEdge> inputUndirectedGraph){
		if(inputUndirectedGraph.getType().isDirected()) {
			throw new IllegalArgumentException("given inputUndirectedGraph is directed!");
		}
		
		this.inputUndirectedGraph = inputUndirectedGraph;
		this.initialize();
	}
	
	private void initialize() {
		this.vertexIDMap = new HashMap<>();
		this.detector = new UndirectedGraphCycleDetector(this.inputUndirectedGraph.vertexSet().size());
		
		//assign unique id from 0 to vertex_num-1 to each vertex;
		int id=0;
		for(VfGraphVertex v:this.inputUndirectedGraph.vertexSet()) {
//			v.setId(100);
//			id++;
//			System.out.println(id);
//			System.out.println(v+"====" +v.getId());
			this.vertexIDMap.put(v, id);
			id++;
		}
		
		
		//add each edge with the assigned id to source and sink vertex
		this.inputUndirectedGraph.edgeSet().forEach(e->{
//			System.out.println("==============");
//			System.out.println(e.simpleString());
			VfGraphVertex source = this.inputUndirectedGraph.getEdgeSource(e);
			VfGraphVertex sink = this.inputUndirectedGraph.getEdgeTarget(e);
//			System.out.println(source+"====" +source.getId());
//			System.out.println(sink+"====" +sink.getId());
			
			//
//			detector.addEdge(
//					this.inputUndirectedGraph.getEdgeSource(e).getId(),
//					this.inputUndirectedGraph.getEdgeTarget(e).getId()
//			);
			detector.addEdge(
					this.vertexIDMap.get(source),
					this.vertexIDMap.get(sink)
			);
		});
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isCyclic() {
		if(this.cyclic==null) {
			this.cyclic=this.detector.isCyclic();
			this.detector=null;
		}
		return this.cyclic;
	}
	
}
