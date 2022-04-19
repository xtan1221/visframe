package dependency;

import java.io.Serializable;
import org.jgrapht.graph.SimpleDirectedGraph;

import exception.CycleFoundInDependencyGraphException;

/**
 * delegate class for a DAG represented by a {@link SimpleDirectedGraph};
 * 
 * note that the underlying {@link SimpleDirectedGraph} is not initialized by this class but should be provided as input parameter at constructor;
 * 
 * provide a set of utility methods to process the delegated {@link SimpleDirectedGraph};
 * 
 * all types of dependency graph in visframe are DAG and thus should be subclasses of this one;;
 * 
 * =======================================
 * note that all edges in dependency graph should be directing from the depending node(source) to the depended node(sink)!!!!!!
 * 
 * 1. for example in CFD graph, the edge should be from CF cf1 to CF cf2, where cf2's target is used as input variable of cf2 or cf2's FIV is used by cf1;
 * 2. in DOS graph, the edge should be from metadata m1 to m2, where m2 is the input of the operation that generate m1 or m2 is the parent composite data of m1;
 * 		!!!!!!this type might be confusing if thinking the way that edge is from the input/parent metadata to the output/child metadata;
 * 3. in VCD graph, the edge should be from VCDNode n1 to n2, where n1 is depending on n2;
 * 4. in VCCL graph, the edge should be from VCCLNode c1 to c2, where c1's VCDNode is depending on c2's VCDNode;
 * @author tanxu
 * 
 * @param <V>
 * @param <E>
 */
public abstract class DAG<V,E> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7387250517783060425L;

	
	//////////////////////////
	protected final SimpleDirectedGraph<V, E> underlyingGraph;
	
	private final Class<E> edgeType;
	
	
	/**
	 * constructor
	 * @param underlyingGraph an initialized and maybe populated SimpleDirectedGraph; not null;
	 * @param edgeType type of the edge
	 * @throws CycleFoundInDependencyGraphException 
	 */
	public DAG(SimpleDirectedGraph<V, E> underlyingGraph, Class<E> edgeType){
		//validations;
		if(underlyingGraph==null)
			throw new IllegalArgumentException("given underlyingGraph cannot be null!");
		
		//must be acyclic
		if(JGraphTDependencyGraphUtils.containsCycle(underlyingGraph)) {
			throw new CycleFoundInDependencyGraphException("");
		}
		
		this.edgeType = edgeType;
		this.underlyingGraph = underlyingGraph;
	}

	
	public SimpleDirectedGraph<V, E> getUnderlyingGraph() {
		return underlyingGraph;
	}

	
	public Class<E> getEdgeType() {
		return edgeType;
	}
	
	////////////////////////////////////
//	/**
//	 * !root nodes of a dependency graph: the nodes that are not depending on any other nodes;
//	 * 
//	 * extract a upstream rooted subgraph of the underlying graph of this DAG with the given vertextSet;
//	 * <p></p>
//	 * note that the returned graph will contain all upstream vertex in this DAG that is connected by one or more vertex in the given vertextSet, not simply extract the subgraph containing only those given vertex;
//	 * @param vertextSet
//	 * @return
//	 */
//	protected abstract SimpleDirectedGraph<V, E> extractRootedSubgraph(Set<V> vertextSet);
	
//	/**
//	 * create and return a deep clone of this DAG
//	 * @return
//	 */
//	public abstract DAG<V,E> deepClone();
	
//	/**
//	 * extract and returns a subgraph DAG of this DAG induced by the given vertextSet;<br>
//	 * note that building a subgraph of a DAG need to <br>
//	 * 1. subgraph of underlying graph<br>
//	 * 2. build the set of supportive information ?? not doable?;
//	 * @param vertextSet
//	 * @return
//	 */
//	public abstract DAG<V,E> subgraph(Set<V> vertextSet);
	
}
