import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class Tests {
    public static void main(String[] args) {
        int numbOfTests = 10000;
        int numbOfTestTypes = 7;
        boolean connectedGraph = true;
        //0 - default
        //1 - incremented intensity
        //2 - incremented intensity 2 times
        //3 - incremented capacity
        //4 - incremented capacity 2 times
        //5 - changed topology to graph2
        //6 - changed topology to graph3
        DataForTests[] data = new DataForTests[numbOfTestTypes];

        ConnectivityInspector<Integer, DefaultEdge> inspectorTest;

        for(int i=0; i<numbOfTestTypes; i++){
            if(i<5)
                data[i] = new DataForTests(1);
            else if(i==5)
                data[i] = new DataForTests(2);
            else
                data[i] = new DataForTests(3);
        }

        for(int i=1; i<=2; i++){
            for(int j=1; j<=i;j++){
                data[i].incrementIntensityMatrix();
                data[i+2].incrementCMatrix();
            }
        }

        for(int i=0; i<numbOfTestTypes; i++){

            for(int j=0; j<numbOfTests; j++){

                data[i].deleteRandomEdges();
                // Test if the graph is reliable.
                inspectorTest = new ConnectivityInspector<>(data[i].graph);
                connectedGraph = inspectorTest.isConnected();

                if(connectedGraph){
                    data[i].averageTime();
                }
                else{
                    //System.err.println("Unconsistent graph: "+i);
                    data[i].numbOfFailures++;
                }
                data[i].createGraph();
                if(i==5)
                    data[i].changeTopology(2);
                else if(i==6)
                    data[i].changeTopology(3);
            }
        }
        for(int i=0; i<numbOfTestTypes;i++){
         System.out.println("Test type "+i+": Reliability: "+
                 (double)(numbOfTests-data[i].numbOfFailures)/numbOfTests+
                 " %, averageTime: "+data[i].averageTime/(double)(numbOfTests-data[i].numbOfFailures));
        }

    }
}
