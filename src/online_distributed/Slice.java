package online_distributed;

// a different representation of the output slice than the Slice.java in offline_centralized package
// but they're essentially the same thing.
// it follows the representation of the paper: Slice = (J_B, set-inclusion)
// so here, each node in the Slice is a consistent cut J_B(e) (represented using the vector clock shortened representation)
// for some e and they're ordered by set-inclusion.

import java.util.Arrays;

public class Slice {
    VectorClock[] nodes;
    boolean[][] incidenceMatrix;

    public Slice(VectorClock[] nodes, boolean[][] incidenceMatrix) {
        this.nodes = nodes;
        this.incidenceMatrix = incidenceMatrix;
    }

    public static String incidenceMatrixString(boolean[][] incidenceMatrix){
        StringBuilder sb = new StringBuilder();
        for (boolean[] row : incidenceMatrix){
            sb.append("[");
            for (boolean b : row){
                sb.append(b + " ");
            }
            sb.append("]\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "online_distributed.Slice{\n" +
                "\ncuts=" + Arrays.toString(nodes) +
                "\nincidenceMatrix=\n" + incidenceMatrixString(incidenceMatrix) +
                '}';
    }
}
