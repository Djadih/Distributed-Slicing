import java.util.ArrayList;
import java.util.Set;
import java.util.function.Function;


public class Slice {
    Computation computation; // context 1: the original computation from which we generate this slice
    Predicate predicate; // context 2: the predicate of interest

    ConsistentCut V; // smallest consistent cut in "computation" that satisfy "predicate"
    Node[] nodes; // each node in "nodes" contains a set of Events in "computation". "nodes" form a partition of a subset of events in "computation". Textbook refers to this as F
    boolean[][] incidenceMatrix; // partial order on "nodes". incidenceMatrix[i][j] = True iff the J(e for any e in nodes[i]) \includedIn J(e for any e in nodes[j]). Textbook refers to this as ->_B


    public Slice(Computation computation, Predicate predicate, ConsistentCut V, Node[] nodes, boolean[][] incidenceMatrix) {
        this.computation = computation;
        this.predicate = predicate;
        this.V = V;
        this.nodes = nodes;
        this.incidenceMatrix = incidenceMatrix;
    }
}
