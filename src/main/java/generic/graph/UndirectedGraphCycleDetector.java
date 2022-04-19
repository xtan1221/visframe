package generic.graph;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * https://www.geeksforgeeks.org/detect-cycle-undirected-graph/
 * 
 * 
 * 
 * @author tanxu
 *
 */
public class UndirectedGraphCycleDetector {
	
	private int vertexNum;   // No. of vertices; also the id of all vertex should be from 0 to vertexNum-1
    private LinkedList<Integer> adj[]; // Adjacency List Representation 
  
    /**
     * Constructor
     * @param v 
     */
	public UndirectedGraphCycleDetector(int v) { 
        vertexNum = v; 
        adj = new LinkedList[v]; 
        for(int i=0; i<v; ++i) 
            adj[i] = new LinkedList(); 
    }
    
    /**
     * add an edge into the graph;
     * 
     * note that id of all vertex should be non-negative integer that are different from each other
     * 
     * @param v
     * @param w
     */
	public void addEdge(int v,int w) {
		if(v<0 ||v>=this.vertexNum ||w <0 ||v>this.vertexNum) {
			throw new IllegalArgumentException("id of all vertex should be from 0 to vertexNum-1, which is "+(vertexNum-1)+ "!");
		}
		
        adj[v].add(w); 
        adj[w].add(v); 
    }
    
    // A recursive function that uses visited[] and parent to detect 
    // cycle in subgraph reachable from vertex v. 
	private Boolean isCyclicUtil(int v, Boolean visited[], int parent) 
    { 
        // Mark the current node as visited 
        visited[v] = true; 
        Integer i; 
  
        // Recur for all the vertices adjacent to this vertex 
        Iterator<Integer> it = adj[v].iterator(); 
        while (it.hasNext()) 
        { 
            i = it.next(); 
            
            // If an adjacent is not visited, then recur for that 
            // adjacent 
            if (!visited[i]) 
            { 
                if (isCyclicUtil(i, visited, v)) 
                    return true; 
            } 
  
            // If an adjacent is visited and not parent of current 
            // vertex, then there is a cycle. 
            else if (i != parent) 
                return true; 
        } 
        return false; 
    } 
	
    /**
     * Returns true if the graph contains a cycle, else false.
     * 
     * 
     * @return
     */
	public Boolean isCyclic(){ 
        // Mark all the vertices as not visited and not part of 
        // recursion stack 
        Boolean visited[] = new Boolean[vertexNum];
        for (int i = 0; i < vertexNum; i++) 
            visited[i] = false; 
        
        // Call the recursive helper function to detect cycle in 
        // different DFS trees 
        for (int u = 0; u < vertexNum; u++) 
            if (!visited[u]) // Don't recur for u if already visited 
                if (isCyclicUtil(u, visited, -1)) 
                    return true; 
        
        return false; 
    }
  
  
    // Driver method to test above methods 
    public static void main(String args[]) 
    { 
        // Create a graph given in the above diagram 
    	UndirectedGraphCycleDetector g1 = new UndirectedGraphCycleDetector(5); 
        g1.addEdge(1, 0); 
        g1.addEdge(0, 2); 
        g1.addEdge(2, 1); 
        g1.addEdge(0, 3); 
        g1.addEdge(3, 4); 
        if (g1.isCyclic()) 
            System.out.println("Graph contains cycle"); 
        else
            System.out.println("Graph doesn't contains cycle"); 
  
        UndirectedGraphCycleDetector g2 = new UndirectedGraphCycleDetector(3); 
        g2.addEdge(0, 1); 
        g2.addEdge(1, 2); 
        if (g2.isCyclic())
            System.out.println("Graph contains cycle"); 
        else
            System.out.println("Graph doesn't contains cycle"); 
    } 
	
}
