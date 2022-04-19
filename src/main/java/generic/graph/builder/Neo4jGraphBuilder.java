package generic.graph.builder;

import generic.graph.GraphIterator;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import metadata.graph.feature.EdgeDirectednessFeature;
import metadata.graph.type.GraphMetadataType;
import metadata.graph.type.GraphTypeEnforcer;


/**
 * builder for a neo4j graph database containing the graph data from the {@link #getInputGraphIterator()} that can be used to 
 * 
 * 1. identify the observed GraphMetadataType
 * 2. facilitate neo4j based graph processing and algorithm;
 * 
 * @author tanxu
 *
 */
public class Neo4jGraphBuilder extends GraphBuilder {
	
	Neo4jGraphBuilder(GraphIterator inputGraphIterator, GraphTypeEnforcer graphTypeEnforcer, boolean toAddDiscoveredVertexFromInputEdgeDataTable) {
		super(inputGraphIterator, graphTypeEnforcer, toAddDiscoveredVertexFromInputEdgeDataTable);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void perform() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GraphMetadataType getOberservedType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VfGraphVertex nextVertex() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VfGraphEdge nextEdge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void restart() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public EdgeDirectednessFeature getEdgeDirectednessFeature() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
