import java.util.*;
import java.util.function.Function;

public class Slicer {

    static Slice slicer(Computation computation, Predicate predicate) {
        // Precondition: predicate is a lattice-liner predicate on cuts of "computation"

        // 1. Compute the "smallest" consistent cut V in "computation" such that predicate(V).
        ConsistentCut V = smallestConsistentCut(computation, predicate);


        // 2. Compute the "largest" consistent cut W in "computation" such that predicate(W).
        ConsistentCut W = largestConsistentCut(computation, predicate);


        // 3. For each event e in W-V, find the least consistent cut that satisfies B and includes e.
        // (note: since we override equals&hashCode for ConsistentCut, we can use a Map whose key is of
        // type ConsistentCut to quickly "aggregate" all e's that produces the same J(e)'s into the same entry
        // under their (same) J(e))
        Map<ConsistentCut, Set<Event>> equivalentClasses = new HashMap<>();
        for (int i = 0; i < W.events.size(); ++i) {
            for (int j = V.events.get(i).size(); j < W.events.get(i).size(); ++j) {
                // 3.1. compute the "smallest" consistent cut J(e) in "computation" such that predicate(J(e)) && e \in J(e)
                ConsistentCut JOfE = smallestConsistentCutIncludingEvent(computation, predicate, i, j);
                Set<Event> equivalentClass = equivalentClasses.getOrDefault(JOfE, new HashSet<>());
                equivalentClass.add(W.events.get(i).get(j));
            }
        }

        // 4. Construct nodes and a partial oder upon those nodes according to set inclusion
        // on their corresponding J(e).

        // 4.1 Convert the Map into a (flattened) array of (J(e), equivalent events) pairs.
        ArrayList<Map.Entry<ConsistentCut, Set<Event>>> arrayOfPairs = new ArrayList<>();
        for (Map.Entry<ConsistentCut, Set<Event>> entry : equivalentClasses.entrySet()) {
            arrayOfPairs.add(entry);
        }

        // 4.1. fill in "nodes" and "incidenceMatrix"
        int m = equivalentClasses.size();
        Node[] nodes = new Node[m];
        boolean[][] incidenceMatric = new boolean[m][m];

        for (int i = 0; i < m; ++i) {
            // each node is nothing but a set of equivalent events
            nodes[i] = new Node(arrayOfPairs.get(i).getValue());
        }

        // node[i] ->_B node[j] iff the J(e) for (any) e in node[i] is set-included in J(e) for (any) e in node[j]
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < m; ++j) {
                ConsistentCut j1 = arrayOfPairs.get(i).getKey();
                ConsistentCut j2 = arrayOfPairs.get(j).getKey();
                if (j1.isIncludedIn(j2)) {
                    incidenceMatric[i][j] = true;
                }
            }
        }

        return new Slice(computation, predicate, V, nodes, incidenceMatric);
    }



    // Helper method 1: find the minimal consistent cut in "computation" that satisfies "predicate"
    static ConsistentCut smallestConsistentCut(Computation computation, Predicate predicate) {
        int numOfProcesses = computation.getNumberOfProcesses();
        ConsistentCut G = new ConsistentCut(computation);
        return smallestConsistentCutFromG(computation, predicate, G);
    }


    // Helper method 2: find the maximal consistent cut in "computation" that satisfies "predicate"
    static ConsistentCut largestConsistentCut(Computation computation, Predicate predicate) {
        int numOfProcesses = computation.getNumberOfProcesses();
        ConsistentCut G = new ConsistentCut(computation);

        while (!predicate.predicate.apply(G)) {
            int forbiddenPID = predicate.reverseForbiddenState.apply(G);
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
    static ConsistentCut smallestConsistentCutIncludingEvent(Computation computation, Predicate predicate, int pid, int eventIdxInPID) {
        int numOfProcesses = computation.getNumberOfProcesses();
        ConsistentCut G = new ConsistentCut(numOfProcesses, computation);
        // populate G with all events in pid until (and including) the desired event index
        for (int eventId = 0; eventId <= eventIdxInPID; ++eventId) {
            Event eventToAdd = computation.events.get(pid).get(eventId);
            G.events.get(pid).add(eventToAdd);
        }

        return smallestConsistentCutFromG(computation, predicate, G);
    }

    // Helper method 4: gets the least consistent cut including at least g that satisfies a given predicate. reduces code duplication.
    private static ConsistentCut smallestConsistentCutFromG(Computation computation, Predicate predicate, ConsistentCut g) {
        while (!predicate.predicate.apply(g)) {
            int forbiddenPID = predicate.findForbiddenState.apply(g);
            // check whether all events in "computation" from this process have been included in G
            if (g.events.get(forbiddenPID).size() == computation.events.get(forbiddenPID).size()) {
                return null;
            } else {
                int numberOfEventsInGInProcessForbiddenPID = g.events.get(forbiddenPID).size();
                Event nextEvent = computation.events.get(forbiddenPID).get(numberOfEventsInGInProcessForbiddenPID);
                g.events.get(forbiddenPID).add(nextEvent);
            }
        }
        return g;
    }

}
