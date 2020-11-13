import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Slicer {


    Computation slice(Computation computation, Function<ConsistentCut, Boolean> predicate) {
        // Precondition: predicate is a lattice-liner predicate on cuts of "computation"
        //


        // 1. Compute the "smallest" consistent cut V in "computation" such that predicate(V).
        // TODO: utilize the forbidden-state property of linear predicate to find this V by acting greedy. consult p140 of textbook
        ConsistentCut V = smallestConsistentCut(computation, predicate);



        // 2. Compute the "largest" consistent cut W in "computation" such that predicate(W).
        ConsistentCut W = largestConsistentCut(computation, predicate);


        // 3. For each event e in W-V,
        ConsistentCut WMinusV = W.difference(V);
        Set<ConsistentCut> JOfEs = new HashSet<>();
        for (int i = 0; i < WMinusV.events.size(); ++i) {
            for (int j = 0; j < WMinusV.events.get(i).size(); ++j) {
                // 3.1. compute the "smallest" consistent cut J(e) in "computation" such that predicate(J(e)) && e \in J(e)
                ConsistentCut JOfE = smallestConsistentCut(computation, predicate, i, j);
                JOfEs.add(JOfE);
            }
        }

        // 4.
        for (ConsistentCut JofE : JOfEs) {
            // 4.1 de-deupliate JOfEs, which stores all J(e)'s
            // this step can be omitted because we rewrite equals&hashCode method for ConsistentCut
            // so JOfEs, a set, will not contain duplicates
        }

        // Debugging Step: print out each J(e) and see whether they are all there should be, and whether they're distinct
        int cntxx = 0;
        for (ConsistentCut JOfE : JOfEs) {
            System.out.println(cntxx + "th J(e) = " + JOfE);
        }

        // 5.
        // Cs stores equivalent classes where each C_i is a distinct consistent cut. Let size(distinct_J_es) = m
        // nothing to do in this step because each C_i is each J(e)


        // 6. order C_i's according to set inclusion,
        // 6.1 convert consistent cuts into node
        ArrayList<Node> nodes = new ArrayList<>();
        for (ConsistentCut c : JOfEs) {
            nodes.add(c.toNode()); // flatten events in C, which is a 2D array, into a set of events
        }

        // 6.2 create a partial order according to set inclusion
        Boolean[][] incidenceMatrix = new Boolean[nodes.size()][nodes.size()];
        for (int i = 0; i < incidenceMatrix.length; ++i) {
            for (int j = 0; j < incidenceMatrix[0].length; ++j) {
                if (nodes.get(i).isIncludedIn(nodes.get(j))) {
                    incidenceMatrix[i][j] = true;
                }
            }
        }

        // 6.3. TODO: strip Node in "nodes" so that those nodes contain disjoint events


        return null;
    }



    // Helper method 1: find the minimal consistent cut in "computation" that satisfies "predicate"
    ConsistentCut smallestConsistentCut(Computation computation, Function<ConsistentCut, Boolean> predicate) {
        int numOfProcesses = computation.getNumberOfProcesses();
        ConsistentCut G = new ConsistentCut(numOfProcesses);
        while (!predicate.apply(G)) {
            int forbiddenPID = computation.getForbiddenStateProcessNumber(G, predicate);
            // check whether all events in "computation" from this process have been included in G
            if (G.events.get(forbiddenPID).size() == computation.events.get(forbiddenPID).size()) {
                return null;
            } else {
                int numberOfEventsInGInProcessForbiddenPID = G.events.get(forbiddenPID).size();
                Event nextEvent = computation.events.get(forbiddenPID).get(numberOfEventsInGInProcessForbiddenPID);
                G.events.get(forbiddenPID).add(nextEvent);
            }
        }
        return G;
    }


    // Helper method 2: find the maximal consistent cut in "computation" that satisfies "predicate"
    ConsistentCut largestConsistentCut(Computation computation, Function<ConsistentCut, Boolean> predicate) {
        int numOfProcesses = computation.getNumberOfProcesses();
        ConsistentCut G = new ConsistentCut(computation.events);

        while (!predicate.apply(G)) {
            int forbiddenPID = computation.getForbiddenStateProcessNumber(G, predicate);
            // check whether all events in "computation" from this process have been included in G
            if (G.events.get(forbiddenPID).size() == 0) {
                return null;
            } else {
                int numberOfEventsInGForProcessForbiddenPID = G.events.get(forbiddenPID).size();
                G.events.get(forbiddenPID).remove(numberOfEventsInGForProcessForbiddenPID - 1);
            }
        }
        return G;
    }

    // Helper method 3: find the minimal consistent cut in "computation" that satisfies "predicate" and also include "e"
    ConsistentCut smallestConsistentCut(Computation computation, Function<ConsistentCut, Boolean> predicate, int pid, int eventIdxInPID) {
        int numOfProcesses = computation.getNumberOfProcesses();
        ConsistentCut G = new ConsistentCut(numOfProcesses);
        // add all events in pid until eventIdxInPID (inclusive) to G
        for (int eventId = 0; eventId <= eventIdxInPID; ++eventId) {
            Event eventToAdd = computation.events.get(pid).get(eventId);
            G.events.get(pid).add(eventToAdd);
        }

        while (!predicate.apply(G)) {
            int forbiddenPID = computation.getForbiddenStateProcessNumber(G, predicate);
            // check whether all events in "computation" from this process have been included in G
            if (G.events.get(forbiddenPID).size() == computation.events.get(forbiddenPID).size()) {
                return null;
            } else {
                int numberOfEventsInGInProcessForbiddenPID = G.events.get(forbiddenPID).size();
                Event nextEvent = computation.events.get(forbiddenPID).get(numberOfEventsInGInProcessForbiddenPID);
                G.events.get(forbiddenPID).add(nextEvent);
            }
        }
        return G;
    }
}
