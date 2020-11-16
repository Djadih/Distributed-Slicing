package offline_centralized;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

/*
    Credit due to and adapted from: https://www.geeksforgeeks.org/strongly-connected-components/
    Following Kosaraju's algorithm for finding strongly connected components.
 */

public class Graph {
    private int numNodes;

    private LinkedList<Integer> adj[];

    Graph(int numNodes){
        this.numNodes = numNodes;

        adj = new LinkedList[numNodes];
        for (int i = 0; i < numNodes; i++){
            adj[i] = new LinkedList<>();
        }
    }

    void addEdge(int v, int w){
        adj[v].add(w);
    }

    private boolean[] DFSUtil(int v, boolean visited[]){
        visited[v] = true;

        Iterator<Integer> i = adj[v].iterator();

        int n;
        while (i.hasNext()){
            n = i.next();

            if (!visited[n])
                DFSUtil(n, visited);
        }

        return visited;
    }

    Graph getTranspose(){
        Graph g = new Graph(numNodes);

        for (int node = 0; node < numNodes; node++){
            Iterator<Integer> i = adj[node].listIterator();

            while (i.hasNext()){
                g.adj[i.next()].add(node);
            }
        }
        return g;
    }

    void fillOrder(int node, boolean visited[], Stack stack){
        visited[node] = true;

        // recur for all adjacent events
        Iterator<Integer> i = adj[node].iterator();

        while (i.hasNext()){
            int n = i.next();
            if (!visited[n])
                fillOrder(n, visited, stack);
        }

        // processed all the vertices reachable from node
        // push node to the stack (finished)
        stack.push(node);
    }

    ArrayList<ArrayList<Integer>> retrieveSCCs(){

        // SCCs holds lists of events for each strongly connected component
        ArrayList<ArrayList<Integer>> SCCs = new ArrayList<>();

        Stack stack = new Stack();

        // Mark all the vertices as not visited (For first DFS)
        boolean visited[] = new boolean[numNodes];

        // fill vertices in stack according to their finishing times
        for (int i = 0; i < numNodes; i++){
            if (!visited[i]) {
                fillOrder(i, visited, stack);
            }
        }

        // create reversed graph
        Graph graph = getTranspose();

        for (int i = 0; i < numNodes; i++){
            //reset all values to false for second DFS
            visited[i] = false;
        }

        // process all the vertices in order defined by the stack
        while (!stack.empty()){
            int v = (int)stack.pop();

            // find and add the SCC to the SCCs list
            if (!visited[v]){
                boolean[] nodesInSCC = graph.DFSUtil(v, visited);
                SCCs.add(findVisitedNodes(nodesInSCC));
            }
        }

        return SCCs;
    }

    // adds all the nodes visited during the SCC to a list
    private ArrayList<Integer> findVisitedNodes(boolean[] visited){
        ArrayList<Integer> SCC = new ArrayList<>();

        for (int i = 0; i < visited.length; i++){
            if (visited[i]){
                SCC.add(i);
            }
        }

        return SCC;
    }
}
