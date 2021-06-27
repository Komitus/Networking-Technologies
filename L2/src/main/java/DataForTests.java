import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;

public class DataForTests {

    private static final int n = 20;
    private static final int m = 8; //8 bits - 1byte
    private static final double p = 0.95;
    private static final double T_max = 0.0024;
    public double averageTime = 0.0;
    public int numbOfFailures = 0;
    private int[][] functionCMatrix;
    private int matrixSum;
    Graph<Integer, DefaultEdge> graph;
    private int[][] intensityMatrix = {
            {0, 14, 49, 56, 7, 68, 71, 81, 38, 26, 41, 54, 20, 20, 94, 82, 71, 8, 5, 56},
            {74, 0, 83, 7, 64, 21, 44, 0, 96, 88, 84, 43, 23, 67, 0, 46, 31, 99, 72, 77},
            {57, 75, 0, 17, 62, 91, 37, 87, 50, 18, 24, 65, 42, 47, 8, 45, 44, 68, 8, 39},
            {50, 3, 97, 0, 86, 2, 58, 95, 8, 33, 50, 73, 44, 89, 51, 66, 18, 98, 49, 83},
            {66, 96, 54, 25, 0, 13, 55, 35, 15, 49, 61, 56, 85, 3, 25, 81, 24, 68, 53, 31},
            {54, 66, 45, 44, 10, 0, 73, 31, 31, 73, 10, 20, 44, 48, 54, 73, 96, 90, 44, 11},
            {92, 30, 71, 25, 93, 34, 0, 96, 91, 25, 16, 52, 1, 73, 86, 80, 2, 82, 49, 65},
            {27, 73, 2, 72, 16, 63, 76, 0, 80, 77, 14, 3, 95, 35, 32, 94, 66, 57, 54, 21},
            {13, 86, 36, 33, 84, 73, 72, 16, 0, 88, 21, 64, 39, 85, 68, 46, 7, 76, 78, 51},
            {4, 26, 11, 27, 24, 9, 37, 89, 87, 0, 32, 77, 99, 32, 22, 81, 5, 21, 94, 23},
            {52, 9, 42, 13, 21, 27, 65, 16, 56, 66, 0, 71, 3, 31, 60, 85, 60, 11, 45, 47},
            {35, 31, 72, 35, 21, 88, 37, 23, 53, 18, 53, 0, 71, 93, 9, 91, 28, 90, 26, 50},
            {85, 5, 33, 77, 9, 46, 72, 95, 25, 13, 51, 98, 0, 72, 1, 32, 89, 89, 48, 41},
            {79, 29, 96, 93, 44, 15, 60, 71, 92, 21, 64, 99, 7, 0, 69, 55, 59, 27, 0, 76},
            {34, 21, 2, 72, 37, 11, 5, 13, 62, 59, 92, 3, 90, 13, 0, 8, 16, 38, 4, 26},
            {92, 11, 38, 90, 0, 27, 23, 57, 84, 52, 67, 58, 79, 48, 7, 0, 84, 5, 83, 72},
            {36, 73, 6, 82, 65, 82, 64, 13, 75, 65, 30, 25, 87, 24, 96, 27, 0, 21, 49, 43},
            {17, 88, 54, 39, 87, 71, 93, 51, 49, 95, 78, 68, 49, 68, 12, 83, 57, 0, 64, 29},
            {34, 22, 70, 33, 73, 60, 92, 10, 73, 98, 42, 51, 43, 6, 16, 43, 25, 78, 0, 22},
            {89, 83, 18, 58, 53, 23, 86, 6, 82, 2, 41, 29, 24, 31, 20, 6, 10, 31, 72, 0}
    };

    public DataForTests(int topology){
        createGraph();
        calculateIntenistySum();
        initFunctionCMatrix();
        changeTopology(topology);
        recalculateCMatrix(topology);
    }
    public void createGraph() {

        Graph<Integer, DefaultEdge> G
                = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
        //zewnętrzne koło
        G.addVertex(0);
        for (int i = 1; i < 10; i++) {
            G.addVertex(i);
            G.addEdge(i, i - 1);
        }
        //G.addEdge(9, 0);
        //wewnętrzny okrąg
        for (int i = 10; i < 20; i++)
            G.addVertex(i);
        //wewnętrzny okrąg  - połaczenia pomiędzy
        G.addEdge(10, 12);
        G.addEdge(12, 14);
        G.addEdge(14, 16);
        //G.addEdge(16, 18);
        G.addEdge(18, 10);
        //wewnętrzny okrąg - połaczenia pomiędzy
        G.addEdge(11, 13);
        G.addEdge(13, 15);
        G.addEdge(15, 17);
        G.addEdge(17, 19);
        //G.addEdge(19, 11);
        //połaczenia między okręgami
        for (int i = 0; i < 7; i++)
            G.addEdge(i, i + 10);

        graph = G;
    }
    private void initFunctionCMatrix(){
        functionCMatrix = new int[n][n];
        for(int i=0; i<n; i++){
            for(int j=0; j<n;j++){
                if(graph.containsEdge(i,j))
                    functionCMatrix[i][j]=m*(calculateAForEdge(i,j)+2000);
                else
                  functionCMatrix[i][j]=0;
            }
        }
    }

    private int calculateAForEdge(int i, int j) {

        // Get all packets that will go through that edge in this graph
        int packetsInEdge = 0;

        for (int a = 0; a < n; a++) {
            for (int b = 0; b < n; b++) {
                GraphPath<Integer, DefaultEdge> shortest_path = DijkstraShortestPath.findPathBetween(graph, a, b);
               try {
                    if (shortest_path.getEdgeList().contains(graph.getEdge(i, j))) {
                        packetsInEdge = packetsInEdge + intensityMatrix[a][b];
                    }
                } catch (NullPointerException e) {
                    System.out.println("NO PATH");
                }

            }
        }
        return packetsInEdge;
    }

    public void incrementIntensityMatrix(){

        for(int i=0; i<n; i++){
            for(int j=0; j<n;j++){
                if(i!=j){
                    intensityMatrix[i][j]+=4;
                    matrixSum+=4;
                }

            }
        }
    }

    public void incrementCMatrix(){
        for(int i=0; i<n; i++){
            for(int j=0; j<n;j++){
                if(i!=j) functionCMatrix[i][j]+=m*40; //from 1 to 3 packets
            }
        }
    }

    public void averageTime(){

        double sumEdgesEquation = 0;
        for (DefaultEdge e : graph.edgeSet()) {

            int i = graph.getEdgeSource(e);
            int j = graph.getEdgeTarget(e);

            double c = functionCMatrix[i][j];
            double a = calculateAForEdge(i, j);

            if(c/m<=a){
                //System.err.println("Edge: ("+i+", "+j+") overloaded");
                numbOfFailures++;
                return;
            }
            sumEdgesEquation += (a/(c/m-a));
        }
        double currAverageTime = (1.0/(double)matrixSum) * sumEdgesEquation;
        if(currAverageTime >=T_max){
            //System.err.println("Timed out");
            numbOfFailures++;
        }
        else{
            averageTime+=currAverageTime;
        }
    }

    private void calculateIntenistySum(){
        matrixSum = 0;
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                matrixSum += intensityMatrix[i][j];
            }
        }
    }

    private int countAverageC(){
        int sum = 0;
        int counter = 0;
        for(int i=0; i<n; i++){
            for(int j=0; j<n;j++){
                if(functionCMatrix[i][j]>0){
                    sum+=functionCMatrix[i][j];
                    counter++;
                }
            }
        }
        return sum/(counter);
    }
    public void recalculateCMatrix(int topology){

        int averageC = countAverageC();
        if(topology>1) {
            functionCMatrix[9][0] = averageC;
            functionCMatrix[16][18] = averageC;
            functionCMatrix[19][11] = averageC;
        }
        if(topology>2){
            functionCMatrix[7][17]=averageC;
            functionCMatrix[8][18]=averageC;
            functionCMatrix[9][19]=averageC;
        }
    }

    public void changeTopology(int topology){
        if(topology>1){
            graph.addEdge(9, 0);
            graph.addEdge(16, 18);
            graph.addEdge(19, 11);
        }
        if(topology>2){
            graph.addEdge(7,17);
            graph.addEdge(8,18);
            graph.addEdge(9,19); //30th edge
        }
    }

    public void deleteRandomEdges(){
        ArrayList<DefaultEdge> edges = new ArrayList<>();
        // Holds edges to be deleted further on
        // Loop through the edges of graph
        for (DefaultEdge e : graph.edgeSet()) {
            if (Math.random() > p) {
                edges.add(e);
            }
        }
        for(DefaultEdge e : edges){
            graph.removeEdge(e);
        }
        edges.removeAll(edges);
    }
    /*
    public static void createIntensityMatrix(int n){
        Random generator = new Random();
        System.out.println("{ ");
        for(int i=0; i<n; i++){
            System.out.print("     { ");
            for(int j=0; j<n-1;j++){
                if(i==j)
                    System.out.print(0+", ");
                else
                    System.out.print(generator.nextInt(100)+", ");
            }
            System.out.println(generator.nextInt(0)+" },");
        }
        System.out.println("}");
    }
    */

}
