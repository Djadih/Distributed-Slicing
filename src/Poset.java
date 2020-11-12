import java.util.Set;


public class Poset {
    Node[] nodes;
    Boolean[][] incidenceMatrix; // initially all 0's. incidenceMatrix[i][j] = True iff nodes[i] < nodes[j]
}
