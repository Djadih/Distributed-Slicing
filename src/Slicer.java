import java.util.function.Function;

public class Slicer {



    Poset slice(Poset computation, Function<Cut, Boolean> predicate) {
        // Precondition: predicate is a lattice-liner predicate on cuts of "computation"
        //

        
        // 1. Compute the "smallest" consistent cut V in "computation" such that predicate(V).
        // TODO: utilize the forbidden-state property of linear predicate to find this V by acting greedy. consult p140 of textbook

        // 2. Compute the "largest" consistent cut W in "computation" such that predicate(W).



        // 3.
        for (Node e : W - V) {
            // 3.1. compute the "smallest" consistent cut J(e) in "computation" such that predicate(J(e)) && e \in J(e)

        }

        // 4.
        for (Cut J_e : J_es) {
            // 4.1 de-deupliate J_es, which stores all J(e)'s
        }

        // 5.
        // Cs stores equivalent classes where each C_i is a distinct consistent cut. Let size(distinct_J_es) = m



        // 6. order Cs according to set inclusion,


        // 7.


        return null;
    }
}
