package dependency;

import org.jgrapht.graph.SimpleDirectedGraph;

/**
 * factory class for DAG testing data
 * @author tanxu
 *
 */
public class TestDAGFactory {
	
	
	/**
	 * single node
	 * @return
	 */
	public static SimpleDirectedGraph<Integer, SimpleEdge> makeSingleVertexDAG(){
		SimpleDirectedGraph<Integer, SimpleEdge> ret = new SimpleDirectedGraph<>(SimpleEdge.class);
		
		ret.addVertex(1);
		
		return ret;
	}
	
	/**
	 * two nodes
	 * @return
	 */
	public static SimpleDirectedGraph<Integer, SimpleEdge> makeTwoNodesDAG(){
		SimpleDirectedGraph<Integer, SimpleEdge> ret = new SimpleDirectedGraph<>(SimpleEdge.class);
		
		ret.addVertex(1);
		ret.addVertex(2);
		
		ret.addEdge(2, 1, new SimpleEdge(2,1));
		
		return ret;
	}
	
	
	/**
	 * three nodes 
	 * minimal CLCBCC
	 * 
	 * @return
	 */
	public static SimpleDirectedGraph<Integer, SimpleEdge> makeThreeNodeDAG(){
		SimpleDirectedGraph<Integer, SimpleEdge> ret = new SimpleDirectedGraph<>(SimpleEdge.class);
		
		ret.addVertex(1);
		ret.addVertex(2);
		ret.addVertex(3);
//		ret.addVertex(4);
		
		ret.addEdge(2, 1, new SimpleEdge(2,1));
		ret.addEdge(3, 1, new SimpleEdge(3,1));
		ret.addEdge(3, 2, new SimpleEdge(3,2));
//		ret.addEdge(4, 3);
		return ret;
	}
	
	/**
	 * three nodes 
	 * minimal CLCBCC
	 * 
	 * @return
	 */
	public static SimpleDirectedGraph<Integer, SimpleEdge> make4NodeDAG(){
		SimpleDirectedGraph<Integer, SimpleEdge> ret = new SimpleDirectedGraph<>(SimpleEdge.class);
		
		ret.addVertex(1);
		ret.addVertex(2);
		ret.addVertex(3);
		ret.addVertex(4);
		
		ret.addEdge(2, 1, new SimpleEdge(2,1));
		ret.addEdge(3, 1, new SimpleEdge(3,1));
		ret.addEdge(3, 2, new SimpleEdge(3,2));
		ret.addEdge(4, 2, new SimpleEdge(4,2));
		return ret;
	}
	
	/**
	 * four nodes 
	 * minimal CLCBCC
	 * 
	 * @return
	 */
	public static SimpleDirectedGraph<Integer, SimpleEdge> make4NodeDAG2(){
		SimpleDirectedGraph<Integer, SimpleEdge> ret = new SimpleDirectedGraph<>(SimpleEdge.class);
		
		ret.addVertex(1);
		ret.addVertex(2);
		ret.addVertex(3);
		ret.addVertex(4);
		
		ret.addEdge(2, 1, new SimpleEdge(2,1));
		ret.addEdge(3, 1, new SimpleEdge(3,1));
		ret.addEdge(4, 2, new SimpleEdge(3,2));
		ret.addEdge(4, 3, new SimpleEdge(4,2));
		return ret;
	}
	
	
	/**
	 * dag with only two levels of nodes
	 * @return
	 */
	public static SimpleDirectedGraph<Integer, SimpleEdge> makeBipartiteDAG(){
		SimpleDirectedGraph<Integer, SimpleEdge> ret = new SimpleDirectedGraph<>(SimpleEdge.class);
		
		ret.addVertex(1);
		ret.addVertex(2);
		ret.addVertex(3);
		ret.addVertex(4);
		
		ret.addEdge(3, 1, new SimpleEdge(3,1));
		ret.addEdge(3, 2, new SimpleEdge(3,2));
		ret.addEdge(4, 1, new SimpleEdge(4,1));
		ret.addEdge(4, 2, new SimpleEdge(4,2));
		return ret;
	}
	
	/**
	 * dag with only two levels of nodes
	 * total 5 nodes;
	 * @return
	 */
	public static SimpleDirectedGraph<Integer, SimpleEdge> makeBipartiteDAG2(){
		SimpleDirectedGraph<Integer, SimpleEdge> ret = new SimpleDirectedGraph<>(SimpleEdge.class);
		
		ret.addVertex(1);
		ret.addVertex(2);
		ret.addVertex(3);
		ret.addVertex(4);
		ret.addVertex(5);
		
		
		ret.addEdge(3, 1, new SimpleEdge(3,1));
		ret.addEdge(3, 2, new SimpleEdge(3,2));
		ret.addEdge(4, 1, new SimpleEdge(4,1));
		ret.addEdge(4, 2, new SimpleEdge(4,2));
		
		
		return ret;
	}
	
	/**
	 * dag with only two levels of nodes
	 * total 5 nodes;
	 * @return
	 */
	public static SimpleDirectedGraph<Integer, SimpleEdge> makeComplexDAG(){
		SimpleDirectedGraph<Integer, SimpleEdge> ret = new SimpleDirectedGraph<>(SimpleEdge.class);
		
		ret.addVertex(1);
		ret.addVertex(2);
		ret.addVertex(3);
		ret.addVertex(4);
		ret.addVertex(5);
		ret.addVertex(6);
		ret.addVertex(7);
		
		ret.addEdge(3, 1, new SimpleEdge(3,1));
		ret.addEdge(3, 2, new SimpleEdge(3,2));
		ret.addEdge(4, 1, new SimpleEdge(4,1));
		ret.addEdge(4, 2, new SimpleEdge(4,2));
		ret.addEdge(4, 5, new SimpleEdge(4,5));
		ret.addEdge(6, 1, new SimpleEdge(6,1));
		ret.addEdge(6, 3, new SimpleEdge(6,3));
		ret.addEdge(7, 4, new SimpleEdge(7,4));
		ret.addEdge(7, 5, new SimpleEdge(7,5));
		
		return ret;
	}
	
	/**
	 * dag with only two levels of nodes
	 * total 5 nodes;
	 * @return
	 */
	public static SimpleDirectedGraph<Integer, SimpleEdge> makeComplexDAG2(){
		SimpleDirectedGraph<Integer, SimpleEdge> ret = new SimpleDirectedGraph<>(SimpleEdge.class);
		
		ret.addVertex(1);
		ret.addVertex(2);
		ret.addVertex(3);
		ret.addVertex(4);
//		ret.addVertex(5);
		ret.addVertex(6);
//		ret.addVertex(7);
		
		ret.addEdge(3, 1, new SimpleEdge(3,1));
		ret.addEdge(3, 2, new SimpleEdge(3,2));
		ret.addEdge(4, 1, new SimpleEdge(4,1));
		ret.addEdge(4, 2, new SimpleEdge(4,2));
//		ret.addEdge(4, 5, new SimpleEdge(4,5));
		ret.addEdge(6, 1, new SimpleEdge(6,1));
		ret.addEdge(6, 3, new SimpleEdge(6,3));
//		ret.addEdge(7, 4, new SimpleEdge(7,4));
//		ret.addEdge(7, 5, new SimpleEdge(7,5));
		
		return ret;
	}
	
	
	/**
	 * 4 nodes, two root path
	 * 
	 * @return
	 */
	public static SimpleDirectedGraph<Integer, SimpleEdge> makeSolutionSetSelectionDAG(){
		SimpleDirectedGraph<Integer, SimpleEdge> ret = new SimpleDirectedGraph<>(SimpleEdge.class);
		
		ret.addVertex(1);
		ret.addVertex(2);
		ret.addVertex(3);
		ret.addVertex(4);
		ret.addVertex(5);
		ret.addVertex(6);
		ret.addVertex(7);
		
		ret.addEdge(3, 1, new SimpleEdge(3,1));
		ret.addEdge(4, 2, new SimpleEdge(4,2));
		ret.addEdge(4, 3, new SimpleEdge(4,3));
		
		ret.addEdge(5, 4, new SimpleEdge(5,4));
		ret.addEdge(6, 4, new SimpleEdge(6,4));
		
		ret.addEdge(7, 2, new SimpleEdge(7,2));
		return ret;
	}
	
	
	
	//////////////////////////////
	public static class SimpleEdge{
		private final int source;
		private final int target;
		
		SimpleEdge(int source, int target){
			this.source = source;
			this.target = target;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + source;
			result = prime * result + target;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof SimpleEdge))
				return false;
			SimpleEdge other = (SimpleEdge) obj;
			if (source != other.source)
				return false;
			if (target != other.target)
				return false;
			return true;
		}
		
		
		
	}
	
}
