package dependency.cfd;

import org.jgrapht.graph.SimpleDirectedGraph;

import dependency.DAG;


/**
 * decorator class for CFD graph (CompositionFunction Dependency graph)
 * 
 * the underlying CFD graph is not changeable in this class;
 * 
 * edge is from depending CF(source node) to depended CF(sink/target node)
 * @author tanxu
 *
 * @param <N>
 * @param <E>
 */
public abstract class CFDGraphBase<N extends CFDNode, E extends CFDEdge> extends DAG<N,E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7778985312950456441L;

	
	/**
	 * constructor
	 * @param underlyingGraph
	 * @param dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap
	 */
	public CFDGraphBase(
			SimpleDirectedGraph<N, E> underlyingGraph, Class<E> edgeType
//			Map<MetadataID, Set<SimpleName>> dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap
			) {
		super(underlyingGraph, edgeType);
		// TODO Auto-generated constructor stub
		
		
//		this.dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap = dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap;
	}
	

//	public Map<MetadataID, Set<SimpleName>> getDependedRecordMetadataIDInputVariableDataTableColumnNameSetMap() {
//		return dependedRecordMetadataIDInputVariableDataTableColumnNameSetMap;
//	}

	
	
	
//	
//	@Override
//	public abstract CFDGraphBase<N, E> deepClone();
	
	
//	@Override
//	protected SimpleDirectedGraph<N, E> extractRootedSubgraph(Set<N> vertextSet) {
//		// TODO Auto-generated method stub
//		return null;
//	}


	
}
