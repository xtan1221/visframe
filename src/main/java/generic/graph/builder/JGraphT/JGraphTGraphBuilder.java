package generic.graph.builder.JGraphT;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jgrapht.Graph;

import exception.VisframeException;
import generic.graph.GraphIterator;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import generic.graph.builder.GraphBuilder;
import metadata.graph.type.GraphMetadataType;
import metadata.graph.type.GraphTypeEnforcer;
import metadata.graph.type.GraphTypeEnforcer.DirectedEnforcingMode;

/**
 * builder for a {@link org.jgrapht.Graph} instance containing the graph data from the {@link #getInputGraphIterator()} that can be used to 
 * 
 * 1. identify the observed GraphMetadataType
 * 2. facilitate JGraphT based graph processing and algorithm;
 * 
 * @author tanxu
 * 
 */
public class JGraphTGraphBuilder extends GraphBuilder {
	/**
	 * underlying JGraphT graph object containing the built graph entity;
	 */
	private Graph<VfGraphVertex, VfGraphEdge> underlyingGraph;
	
	///iterator of vertex and edge of the built graph
	private Iterator<VfGraphVertex> vertexIterator;
	private Iterator<VfGraphEdge> edgeIterator;
	
	/////////////
	
	/**
	 * constructor
	 * @param inputGraphIterator
	 * @param graphTypeEnforcer
	 * @throws IOException 
	 */
	public JGraphTGraphBuilder(
			GraphIterator inputGraphIterator, 
			GraphTypeEnforcer graphTypeEnforcer, 
			boolean toAddDiscoveredVertexFromInputEdgeDataTable) throws IOException {
		super(inputGraphIterator, graphTypeEnforcer, toAddDiscoveredVertexFromInputEdgeDataTable);
		
		//
	}
	
	/**
	 * 0. invoke {@link VfGraphVertex#reset()} to reset the ID counter
	 * 
	 * 1. initialize the {@link #underlyingGraph} based on the {@link #getGraphTypeEnforcer()};
	 * 
	 * 2. add vertex to the {@link #underlyingGraph} from the {@link #getInputGraphIterator()}
	 * 
	 * 3. add edge to the {@link #underlyingGraph} from the {@link #getInputGraphIterator()}
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Override
	public void perform() throws IOException, SQLException {
//		VfGraphVertex.reset();
		
		this.underlyingGraph = JGraphTGraphFactory.initializeGraph(this.getGraphTypeEnforcer(), VfGraphVertex.class, VfGraphEdge.class);
		
		//add vertex set
		VfGraphVertex vertex;
		while((vertex=this.getInputGraphIterator().nextVertex())!=null) {
			if(this.underlyingGraph.containsVertex(vertex)) {
				System.out.println("duplicate vertex found:"+vertex.toString());
			}else {
				this.underlyingGraph.addVertex(vertex);
			}
		}
		
		//add edges
		VfGraphEdge edge;
		while((edge=this.getInputGraphIterator().nextEdge())!=null) {
//			System.out.println("adding edge:"+edge);
			//dummy object for source and sink node of the edge with the data used in the equals() and hashCode() method of VfGraphEdge
			VfGraphVertex sourceNode = edge.getDummySourceVertex();
			VfGraphVertex sinkNode = edge.getDummySinkVertex();
			
			//if implicitly defined vertex in edge need to be added, always do it no matter the vertex is already present or not;
			if(this.isToAddDiscoveredVertexFromInputEdgeDataTable()) {
				
				if(this.underlyingGraph.containsVertex(sourceNode)) {
//					System.out.println("duplicate vertex found:"+vertex.toString());
				}else {
					this.underlyingGraph.addVertex(sourceNode);
				}
				if(this.underlyingGraph.containsVertex(sinkNode)) {
//					System.out.println("duplicate vertex found:"+vertex.toString());
				}else {
					this.underlyingGraph.addVertex(sinkNode);
				}
			}else {//
				if(!this.underlyingGraph.containsVertex(sourceNode)||!this.underlyingGraph.containsVertex(sinkNode)) {
					continue;//go to next edge
				}
			}
			
			//if self loop not allowed, skip
			if(this.getGraphTypeEnforcer().isToForceNoSelfLoops()) {
				if(sourceNode.equals(sinkNode)) {
					System.out.println("self loop found and skipped:"+edge);
					continue;
				}
			}
			
			//!parallel edges will be automatically dealt by the jgrapht Graph api based on whether parallel edges are allowed or not, thus no need to explicitly deal with it here;
			
			//directed-ness
			if(this.getGraphTypeEnforcer().isToForceDirected()) {
				if(!edge.isDirected()) {//original edge is undirected
					if(this.getGraphTypeEnforcer().getDirectedForcingMode()==DirectedEnforcingMode.SIMPLE) {
						if(!this.underlyingGraph.addEdge(sourceNode, sinkNode, edge)) {
							System.out.println("duplicate edge (or parallel edge if not allowed) found:"+edge.toString());
						}
					}else if(this.getGraphTypeEnforcer().getDirectedForcingMode()==DirectedEnforcingMode.SIMPLE_REVERSE) {
						if(!this.underlyingGraph.addEdge(sinkNode, sourceNode, edge.oppositeEdge())) {
							System.out.println("duplicate edge (or parallel edge if not allowed) found:"+edge.oppositeEdge());
						}
					}else if(this.getGraphTypeEnforcer().getDirectedForcingMode()==DirectedEnforcingMode.BI_DIRECTION) {
						//
						if(!this.underlyingGraph.addEdge(sourceNode, sinkNode, edge)) {
							System.out.println("duplicate edge (or parallel edge if not allowed) found:"+edge.toString());
						}
						
						VfGraphEdge oppositeEdge = edge.oppositeEdge();
						//add a new edge with the opposite direction and exactly the same data
						if(!this.underlyingGraph.addEdge(sinkNode, sourceNode, oppositeEdge)) {
							System.out.println("duplicate edge (or parallel edge if not allowed) found:"+oppositeEdge.toString());
						}
						
					}else {
						throw new IllegalArgumentException("");
					}
				}else {//original edge is directed, do nothing
					if(!this.underlyingGraph.addEdge(sourceNode, sinkNode, edge)) {
						System.out.println("duplicate edge (or parallel edge if not allowed) found:"+edge.toString());
					}
				}
			}else if(this.getGraphTypeEnforcer().isToForceUndirected()) {//
				if(!this.underlyingGraph.addEdge(sourceNode, sinkNode, edge)) {
					System.out.println("duplicate edge (or parallel edge if not allowed) found:"+edge.toString());
				}
			}else {
				throw new VisframeException("invalid GraphTypeEnforcer");
			}
			
			
			
			//Adds the specified edge to this graph, going from the source vertex to the target vertex.More formally, adds the specified edge, e, 
			//to this graph if this graph contains no edge e2 such that e2.equals(e). If this graph already contains such an edge, 
			//the call leaves this graph unchanged and returns false. Some graphs do not allow edge-multiplicity.
			//In such cases, if the graph already contains an edge from the specified source to the specified target, 
			//than this method does not change the graph and returns false. If the edge was added to the graph, returns true. 
			
			//The source and target vertices must already be contained in this graph. If they are not found in graph IllegalArgumentException is thrown. 
//			if(!this.underlyingGraph.addEdge(sourceNode, sinkNode, edge)) {
//				System.out.println("duplicate edge found:"+edge.toString());
//			}
		}
		
//		this.underlyingGraph.vertexSet().forEach(v->{
//			System.out.println(v);
//		});
//		
//		this.underlyingGraph.edgeSet().forEach(e->{
//			System.out.println(e.simpleString());
//		});
			
		
		
		this.built = true;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GraphMetadataType getOberservedType() {
		if(!this.isBuilt()) {
			throw new UnsupportedOperationException("graph has not been built!");
		}
		
		if(this.observedType==null) {
			containingDirectedEdgeOnly = this.getGraphTypeEnforcer().isToForceDirected();
			containingUndirectedEdgeOnly = this.getGraphTypeEnforcer().isToForceUndirected();
			containingSelfLoop = !this.getGraphTypeEnforcer().isToForceNoSelfLoops();
			containingParallelEdges = !this.getGraphTypeEnforcer().isToForceNoParallelEdges();
			
			if(containingUndirectedEdgeOnly) {//detect cycle in undirected graph is not supported in JGraphT
//				AsSimpleDirectedGraph asSimpleDirectedGraph = new AsSimpleDirectedGraph(this.underlyingGraph);
//				containingCycle = JGraphTAlgoUtils.containsCycle(asSimpleDirectedGraph.getSimpleDirectedGraph());
				JGraphTUndirectedGraphCycleDetector detector = new JGraphTUndirectedGraphCycleDetector(this.underlyingGraph);
				containingCycle = detector.isCyclic();
				
			}else {//
				containingCycle = JGraphTAlgoUtils.containsCycle(this.underlyingGraph);
			}
			
			notConnected = !JGraphTAlgoUtils.isConnected(this.underlyingGraph);
			
			this.observedType = new GraphMetadataType(containingDirectedEdgeOnly, containingUndirectedEdgeOnly, 
					containingSelfLoop, containingParallelEdges,
					containingCycle, notConnected
					);
		}
		
		return this.observedType;
	}
	
	
	@Override
	public VfGraphVertex nextVertex() {
		if(this.vertexDone) {
			throw new VisframeException("vertex iteration has been done!");
		}
		
		if(this.vertexIterator==null) {
			this.vertexIterator = this.underlyingGraph.vertexSet().iterator();
		}
		
		try {
			return this.vertexIterator.next();
		}catch(NoSuchElementException e) {//no more vertex
			this.vertexDone = true;
			return null;
		}
	}

	
	@Override
	public VfGraphEdge nextEdge() {
		if(this.edgeDone) {
			throw new VisframeException("edge iteration has been done!");
		}
		
		if(this.edgeIterator==null) {
			this.edgeIterator = this.underlyingGraph.edgeSet().iterator();
		}
		
		try {
			return this.edgeIterator.next();
		}catch(NoSuchElementException e) {//no more vertex
			this.edgeDone = true;
			return null;
		}
		
	}
	
	
	@Override
	public void restart() {
		// TODO Auto-generated method stub
		
	}


	/**
	 * @return the underlyingGraph
	 */
	public Graph<VfGraphVertex, VfGraphEdge> getUnderlyingGraph() {
		return underlyingGraph;
	}
	
	
}
