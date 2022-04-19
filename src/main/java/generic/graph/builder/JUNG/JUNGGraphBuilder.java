package generic.graph.builder.JUNG;

import java.io.IOException;
import java.sql.SQLException;

import edu.uci.ics.jung.graph.Graph;
import generic.graph.GraphIterator;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import generic.graph.builder.GraphBuilder;
import generic.graph.reader.project.GenericGraphMetadataDataReader;
import metadata.graph.type.GraphMetadataType;
import metadata.graph.type.OperationInputGraphTypeBoundary;

/**
 * builder for a {@link edu.uci.ics.jung.graph.Graph} instance based on an existing {@link GraphDataMetadata} from a VisProjectDBContext; 
 * 
 * input {@link GraphIterator} is {@link GenericGraphMetadataDataReader};
 * 
 * facilitate JUNG based graph processing and algorithm;
 * 
 * NOT involve in Graph building from non-graph data or graph type transformation (which are all supported by {@link JGraphTGraphBuilder}); 
 * thus, no need to perform any validations of the input graph; 
 * 
 * @author tanxu
 *
 */
public class JUNGGraphBuilder extends GraphBuilder{
	private final OperationInputGraphTypeBoundary graphTypeBoundary;
	

	///////////////
	private Graph<VfGraphVertex, VfGraphEdge> underlyingGraph;
	
	
	/**
	 * constructor
	 * @param inputGraphMetadataDataReader input graph which is based on an existing GraphMetadataData;
	 * @param graphTypeBoundary the graph type boundary of the operation that utilize this JUNGGraphBuilder
	 */
	public JUNGGraphBuilder(
			GenericGraphMetadataDataReader inputGraphMetadataDataReader,
			
			OperationInputGraphTypeBoundary graphTypeBoundary) {
		super(inputGraphMetadataDataReader, 
				null, //graphTypeEnforcer
				false); //toAddDiscoveredVertexFromInputEdgeDataTable is always false;
		
		if(!graphTypeBoundary.isEqualToOrMoreGenericThan(inputGraphMetadataDataReader.getGraphDataMetadata().getObservedGraphType())) {
			throw new IllegalArgumentException("observed graph type of GraphMetadataData of the given GenericGraphMetadataDataReader's is not compatible with the given OperationInputGraphTypeBoundary!");
		}
		
		this.graphTypeBoundary = graphTypeBoundary;
	}
	

	
	/**
	 * @return the graphTypeBoundary
	 */
	public OperationInputGraphTypeBoundary getGraphTypeBoundary() {
		return graphTypeBoundary;
	}
	/**
	 * @return the underlyingGraph
	 */
	public Graph<VfGraphVertex, VfGraphEdge> getUnderlyingGraph() {
		if(this.isBuilt()) {
			return underlyingGraph;
		}else {
			throw new UnsupportedOperationException();
		}
		
	}
	
	/**
	 * 
	 */
	@Override
	public void perform() throws IOException, SQLException {
		this.underlyingGraph = JUNGGraphFactory.initializeGraph(this.graphTypeBoundary, VfGraphVertex.class, VfGraphEdge.class);
		
		//add vertex set
		VfGraphVertex vertex;
		while((vertex=this.getInputGraphIterator().nextVertex())!=null) {
			if(this.underlyingGraph.containsVertex(vertex)) {
				System.out.println("duplicate vertex found:"+vertex.toString());
			}else {
				this.underlyingGraph.addVertex(vertex);
			}
		}
		
		//
		//add edges
		VfGraphEdge edge;
		while((edge=this.getInputGraphIterator().nextEdge())!=null) {
//			System.out.println("adding edge:"+edge);
			//dummy object for source and sink node of the edge with the data used in the equals() and hashCode() method of VfGraphEdge
			VfGraphVertex sourceNode = edge.getDummySourceVertex();
			VfGraphVertex sinkNode = edge.getDummySinkVertex();
			
			//no need to check vertex existence, since all edges are read from a GraphDataMetadata
			
			//self loop
			//it is not possible that the GraphDataMetadata contains self loop while OperationInputGraphTypeBoundary does not allow self loop;
			//thus, no need to perform extra validation on self loop edges
			
			//Parallel edges
			//if OperationInputGraphTypeBoundary does not allow parallel edges, GraphDataMetadata will never contain parallel edges, thus
			//no need to perform extra validation on self loop edges
			
			
			//directed-ness
			//if OperationInputGraphTypeBoundary require directed edges
			//		GraphDataMetadata's edges should all be directed; otherwise, it is not allowed in the constructor
			//if OperationInputGraphTypeBoundary require undirected edges
			//		GraphDataMetadata's edges should all be undirected; otherwise, it is not allowed in the constructor
			//thus, no need to perform extra operations on edges like 
			
			this.underlyingGraph.addEdge(edge, sourceNode, sinkNode);
			
			
//			if(this.getGraphTypeEnforcer().isToForceDirected()) {
//				if(!edge.isDirected()) {//original edge is undirected
//					if(this.getGraphTypeEnforcer().getDirectedForcingMode()==DirectedEnforcingMode.SIMPLE) {
//						if(!this.underlyingGraph.addEdge(edge, sourceNode, sinkNode)) {
//							System.out.println("duplicate edge (or parallel edge if not allowed) found:"+edge.toString());
//						}
//					}else if(this.getGraphTypeEnforcer().getDirectedForcingMode()==DirectedEnforcingMode.SIMPLE_REVERSE) {
//						if(!this.underlyingGraph.addEdge(edge.oppositeEdge(), sinkNode, sourceNode)) {
//							System.out.println("duplicate edge (or parallel edge if not allowed) found:"+edge.oppositeEdge());
//						}
//					}else if(this.getGraphTypeEnforcer().getDirectedForcingMode()==DirectedEnforcingMode.BI_DIRECTION) {
//						//
//						if(!this.underlyingGraph.addEdge(edge, sourceNode, sinkNode)) {
//							System.out.println("duplicate edge (or parallel edge if not allowed) found:"+edge.toString());
//						}
//						
//						VfGraphEdge oppositeEdge = edge.oppositeEdge();
//						//add a new edge with the opposite direction and exactly the same data
//						if(!this.underlyingGraph.addEdge(oppositeEdge, sinkNode, sourceNode)) {
//							System.out.println("duplicate edge (or parallel edge if not allowed) found:"+oppositeEdge.toString());
//						}
//						
//					}else {
//						throw new IllegalArgumentException("");
//					}
//				}else {//original edge is directed, do nothing
//					if(!this.underlyingGraph.addEdge(edge, sourceNode, sinkNode)) {
//						System.out.println("duplicate edge (or parallel edge if not allowed) found:"+edge.toString());
//					}
//				}
//			}else if(this.getGraphTypeEnforcer().isToForceUndirected()) {//
//				if(!this.underlyingGraph.addEdge(edge, sourceNode, sinkNode)) {
//					System.out.println("duplicate edge (or parallel edge if not allowed) found:"+edge.toString());
//				}
//			}else {
//				throw new VisframeException("invalid GraphTypeEnforcer");
//			}
//			
			
		}
		

		this.built = true;
	}

	
	
	///////////////////////////////////////
	//those methods should never be invoked
	@Override
	public GraphMetadataType getOberservedType() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public VfGraphVertex nextVertex() {
		throw new UnsupportedOperationException();
	}

	@Override
	public VfGraphEdge nextEdge() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void restart() {
		throw new UnsupportedOperationException();
	}
	
}
