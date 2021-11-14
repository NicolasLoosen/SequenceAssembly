/**
 * Sequence Assembler 
 * @author Jens Leiner
 * @author Nicolas Loosen
 * 07-06-2021
 */
import java.util.*;
import java.io.*;
import java.lang.*;

public class SequenzAssembler {
	private ArrayList<String> fragments = new ArrayList<String>();
	private Graph graph;
	private boolean optionTwo = false;
	private boolean optionThree = false;
	private int removeWeightEdges = 0;
	private int editSubValue = 1;
	private int errorRate = 10;
	private ArrayList<Integer> removed = new ArrayList<Integer>();
	
	public static void main(String[] args) {
		SequenzAssembler seqAss = new SequenzAssembler();
		seqAss.readFile("src/frag.txt");
		seqAss.startProcess();
	}

	private void dialog(){
		Scanner scan = new Scanner(System.in);
		int line = -1;
		int ignore =-1;
		do{	
			System.out.println("Welche Aufgabe soll ausgeführt werden?");
			System.out.println("1. Aufgabe -- Enter 1");
			System.out.println("2. Aufgabe -- Enter 2");
			System.out.println("3. Aufgabe -- Enter 3");
			while(!scan.hasNextInt()){
				System.out.println("Wrong input! Should be an Integer!");
				scan.next();
			}
			line = scan.nextInt();
			if( line == 2){
				this.optionTwo = true;
			}else if(line == 3){
				this.optionThree = true;
			}
			System.out.println("Remove Weight Edges: Default(0)");
			while(!scan.hasNextInt()){
				System.out.println("Wrong input! Should be an Integer!");
				scan.next();
			}
			this.removeWeightEdges = scan.nextInt();
			
			System.out.println("ErrorRate -- Enter Int-Number: Default(10)");
			while(!scan.hasNextInt()){
				System.out.println("Wrong input! Should be an Integer!");
				scan.next();
			}
			this.errorRate = scan.nextInt();

			System.out.println("editSubValue -- Enter Int-Number: Default(1)");
			while(!scan.hasNextInt()){
				System.out.println("Wrong input! Should be an Integer!");
				scan.next();
			}
			this.editSubValue = scan.nextInt();
		}while(line > 3 || line < 1);
	}

	private void startProcess(){
		graph = new Graph(fragments.size());

		//UI*
		dialog();
		//Aufgabe 3
		if(this.optionThree){
			checkHelix();
		}
		// build Graph
		buildOverlap();
		int i=0;
		// greedy algorithm till graph has no more edges
		while(graph.hasEdges()){
			System.out.println("----------------------------------------------------------------------");
		  	System.out.println("======================Iteration" + i++ +"=====================================");
 			System.out.println("----------------------------------------------------------------------");
		 	greedy();
			 //show steps
		 	graph.showGraph(removed);
		}
	}

	/*
	* Build the graph
	*/
	private void buildOverlap(){
		//Get Fragment
		for(int i=0; i < fragments.size(); i++){
			String fragment = fragments.get(i);
			System.out.println("----------------------------------------------------------------------");
			System.out.println("Fragment  =  "+fragment + "  Node  =  " + i);
			System.out.println("----------------------------------------------------------------------");
			List<Integer> ignoreCases = new ArrayList<Integer>();
			ignoreCases.add(i);
			//length of suffix to compare with other
			for(int j=0; j < fragment.length()-removeWeightEdges; j++){
				String suffix = fragment.substring(j);
				//Compare the Fragment with all other fragments
				for(int k=0; k < fragments.size(); k++){
					//no comparison with itself or after found case
					if(!ignoreCases.contains(k)){
						//check if suffix is larger or equal to prefix
						if(suffix.length() <= fragments.get(k).length()){
							//get prefix 
							String prefix = fragments.get(k).substring(0,suffix.length());
							//Aufgabe 1
							if(prefix.equals(suffix)){
								graph.addEdge(i,k,suffix.length(),suffix.length());
								System.out.println("Origin Node  =  "+i+"|   DestinationNode  =  "+k+"|  Weight  =  "+suffix.length() + "|  Prefix  =  "+prefix + "|  Suffix  =  "+suffix);
								ignoreCases.add(k);
								//Aufgabe2
							}else if(optionTwo || optionThree){
									// compare strings and count the errorIndex
									char[] s = suffix.toCharArray();
									char[] p = prefix.toCharArray();
								int errorIndex=0;
								for(int l=0; l <= suffix.length()-1; l++){
									if(s[l]!=p[l]){
										errorIndex++;
									}
								}
								// add Edge if errorIndex is smaller than the given errorRate with given editValue
								if(errorIndex <= (suffix.length()/errorRate) + 1){
									int weight= suffix.length() - errorIndex * editSubValue;
									graph.addEdge(i, k, suffix.length(), weight);
										System.out.println("Substitution!!!  "+"Origin Node  =  "+i+"|   DestinationNode  =  "+k+"|  Weight  =  "+weight + "|  Prefix  =  "+prefix + "|  Suffix  =  "+suffix);
										ignoreCases.add(k);
								}
							}
						}
					}			
				}
			}
		}
	}

	/*
	* Aufgabe 3
	*/
	private void checkHelix(){
		int maxN;
		int maxK;
		String komp;
		int distoneway;
		int disttheotherway;
		System.out.println("Start DoppelHelix Orientation: " + "\n");
		// base Fragment
		for(int i = 1; i < fragments.size(); i++){
			maxN = 0;
			maxK = 0;
			komp = buildComp(fragments.get(i));
			// compare with every other fragment that came before
			for(int j = 0; j < i; j++){
				distoneway = getdistance(fragments.get(i),fragments.get(j));
				disttheotherway = getdistance(fragments.get(j),fragments.get(i));
				// add same() to total
				if(distoneway > disttheotherway){
					maxN += distoneway;
				}else{
					maxN += disttheotherway;
				}
				distoneway = getdistance(komp,fragments.get(j));
				disttheotherway = getdistance(fragments.get(j),komp);
				// add opp() to total
				if(distoneway > disttheotherway){
					maxK += distoneway;
				}else{
					maxK += disttheotherway;
				}
			}
			System.out.println("Total of same: " + maxN + "  Total of opp: " + maxK);
			System.out.print("Node: "+ i + fragments.get(i));
			// check same() total and opp() total and adjust Fragment pool
			if(maxK > maxN){
				fragments.set(i,komp);
				System.out.println("  |  Changed to complement: " + fragments.get(i));
			}else{
				System.out.println("  |  Kept the original");
			}
		}
	}

	/***
	* Build the complement of a given string
	* @param fragment
	*/
	private String buildComp(String fragment){
		String newFragment = new StringBuffer(fragment).reverse().toString();
		StringBuilder builder = new StringBuilder();

		for(int i=0;i<newFragment.length();i++){
			char c = newFragment.charAt(i);
			if(c == 'T'){
				builder.append('A');
			}
			if(c == 'A'){
				builder.append('T');
			}
			if(c == 'C'){
				builder.append('G');
			}
			if(c == 'G'){
				builder.append('C');
			}
		}
		return builder.toString();
	}


	/***
	 * Returnns the distance between leftSide and rightSide
	 * @param leftSide
	 * @param rightSide
	 * @return
	 */
	private int getdistance(String leftSide, String rightSide){
		for(int i=0; i < leftSide.length(); i++){
				String suffix = leftSide.substring(i);
				//check if suffix is larger or equal to prefix
				if(suffix.length() <= rightSide.length()){
					//get prefix 
					String prefix = rightSide.substring(0,suffix.length());
					//if prefix=suffix return length of suffix
					if(prefix.equals(suffix)){
						return suffix.length();
					}
				}
			}
		return 0;
	}
	/**
	 * Get the Edge with the highest weight and
	 * Swap the Edges. Delete DestinationNodes after,
	 * set new Fragment and add Node to removed list
	 */
	private void greedy(){
		List<Edge> edges;
		Edge maxEdge = new Edge(-1,-1,-1);
		int originNode = -1;
		for(int i=0; i < fragments.size(); i++){
			edges = new LinkedList<Edge>(graph.getNode(i));
			for (Edge edge : edges) {
				if(maxEdge.edgeWeight < edge.edgeWeight){
					maxEdge = edge;
					originNode = i;
				}
			}
		}
		System.out.println( "Edge Weight  =  "+maxEdge.edgeWeight + "   Origin Node  =  "+ originNode + "   Destination Node  =  "+ maxEdge.destinationNode );
		
		graph.swapEdges(originNode, maxEdge.destinationNode);
		graph.deleteEdgesWithDestination(maxEdge.destinationNode);
		setFragment(fragments.get(originNode), fragments.get(maxEdge.destinationNode), maxEdge.edgeLength, originNode);
		removed.add(maxEdge.destinationNode);
	}

	/***
	 * Set the new Fragment String
	 * @param prefixFragment
	 * @param suffixFragment
	 * @param length
	 * @param origin
	 */
	private void setFragment(String prefixFragment, String suffixFragment, int length, int origin){
		String newFragment = prefixFragment + suffixFragment.substring(length) ;
		fragments.set(origin, newFragment);
		System.out.println("PrefixFragment= " + prefixFragment + "  SuffixFragment= " + suffixFragment+ " New Fragment= " + newFragment + " Overlap= " + suffixFragment.substring(0,length));
	}

	/***
	 * reads file
	 * @param filename
	 */
	private void readFile(String filename){
		try {
			File myObj = new File(filename);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
			  String data = myReader.nextLine();
			  fragments.add(data);
			}
			myReader.close();
			
		  } catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}