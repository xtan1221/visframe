package dependency.dos;

import org.jgrapht.graph.SimpleDirectedGraph;

import dependency.DAG;
import dependency.dos.DOSEdge.DOSEdgeType;

/**
 * decorator class for manipulating a built DOS graph(Data Object Source);
 * 
 * the underlying DOS graph is not changeable in this class;
 * 
 * NOTE that edge direction is from the output Metadata(source) to input Metadata(sink) if the edge is operation;
 * 
 * or from child component metadata to parent composite metadata if the edge type is {@link DOSEdgeType#COMPOSITE_DATA_COMPONENT}
 * 
 * @author tanxu
 *
 * @param <N>
 * @param <E>
 */
public abstract class DOSGraphBase<N extends DOSNode, E extends DOSEdge> extends DAG<N,E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4494262984790935983L;

	////////////////////////////
	
	/**
	 * constructor
	 * @param underlyingGraph not null;
	 * @param edgeType not null;
	 * @param inducingNodeSet not null or empty
	 * @param dependedRecordMetadataIDOperationParameterDataTableColumnNameSetMap can be null if not available?
	 */
	public DOSGraphBase(
			SimpleDirectedGraph<N, E> underlyingGraph, Class<E> edgeType) {
		super(underlyingGraph, edgeType);
		// TODO Auto-generated constructor stub
		
	}
	

	
	////////////////////////////////
//	@Override
//	public DAG<N, E> deepClone() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	
//	@Override
//	protected SimpleDirectedGraph<N, E> extractRootedSubgraph(Set<N> vertextSet) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
}
