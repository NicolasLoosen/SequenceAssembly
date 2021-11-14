import java.util.*;
public class Graph {

	List<Edge> G[];
	public Graph(int n){
		G = new LinkedList[n];
		for(int i=0; i < G.length; i++)
			G[i]=new LinkedList<Edge>();
	}

	public List<Edge> getNode(int i){
		return G[i];
	}

	public void showGraph(ArrayList<Integer> ignore){
		int i = 0;
		System.out.println("----------------------------------------------------------------------");
		System.out.println("========================ShowGraph=====================================");
	    System.out.println("----------------------------------------------------------------------");

		for (List<Edge> edge : G) {
			if(!ignore.contains(i)){
				System.out.print("Node = " + i +  "  Edges: {Destination Node; Edge Weight; Overlap Length} ");
				for(Edge ed : edge){
					//System.out.println( "Edge Weight  =  "+ed.edgeWeight + "   Origin Node  =  "+ i + "   Destination Node  =  "+ ed.destinationNode );
					System.out.print( "{ " + ed.destinationNode + "; " + ed.edgeWeight + "; " + ed.edgeLength + " } " );
				}
				System.out.println();
			}
			
		i++;
		}	
	}

	public void swapEdges(int origin, int destination){
		G[origin] = G[destination];
		G[destination] = new LinkedList<Edge>();
		G[origin].removeIf(e -> e.destinationNode == origin);
    }

	public Boolean hasEdges(){
		boolean hasEdges = false;
		for (List<Edge> edge : G) {
			if(!edge.isEmpty()){
				hasEdges = true;
			}
		}
		return hasEdges;
	}

    public void deleteEdge(int origin, Edge edge){
		G[origin].remove(edge);
    }

    public void deleteEdgesWithDestination(int destination){
		for(List<Edge> liste : G){	
			liste.removeIf(e -> e.destinationNode == destination);		
		}
    }

	boolean isConnected(int originNode,int destinationNode){
		for(Edge i: G[originNode])
			if(i.destinationNode==destinationNode) return true;
		return false;
	}
	void addEdge(int originNode,int destinationNode, int edgeLength, int edgeWeight){
		G[originNode].add(0,new Edge(destinationNode,edgeLength,edgeWeight)); 
	}
}


