package offline_centralized;

import java.util.*;

public class Slicer {

    static Slice slice(Computation computation, Predicate predicate) {
        // Precondition: predicate is a lattice-liner predicate on cuts of "computation"

        // 1. Compute the "smallest" consistent cut V in "computation" such that predicate(V).
        ConsistentCut V = smallestConsistentCut(computation, predicate);


        // 2. Compute the "largest" consistent cut W in "computation" such that predicate(W).
        ConsistentCut W = largestConsistentCut(computation, predicate);

        // 3. For each event e in W-V, find the least consistent cut that satisfies B and includes e.
        // (note: since we override equals&hashCode for offline_centralized.ConsistentCut, we can use a Map whose key is of
        // type offline_centralized.ConsistentCut to quickly "aggregate" all e's that produces the same J(e)'s into the same entry
        // under their (same) J(e))
        Map<ConsistentCut, Set<Event>> equivalentClasses = new HashMap<>();
        for (int pid = 0; pid < W.getNumberOfProcesses(); ++pid) {
            for (int eid = V.getNumberOfEventsInProcess(pid); eid < W.getNumberOfEventsInProcess(pid); ++eid) {
                // 3.1. compute the "smallest" consistent cut J(e) in "computation" such that predicate(J(e)) && e \in J(e)
                ConsistentCut JOfE = smallestConsistentCutIncludingEvent(computation, predicate, pid, eid);
                Set<Event> equivalentClass = equivalentClasses.getOrDefault(JOfE, new HashSet<>());
                equivalentClass.add(W.getEvent(pid, eid));
                equivalentClasses.put(JOfE, equivalentClass);
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
        boolean[][] incidenceMatrix = new boolean[m][m];

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
                    incidenceMatrix[i][j] = true;
                }
            }
        }


        return new Slice(computation, predicate, V, nodes, incidenceMatrix);
    }



    // Helper method 1: find the minimal consistent cut in "computation" that satisfies "predicate"
    static ConsistentCut smallestConsistentCut(Computation computation, Predicate predicate) {
        ConsistentCut G = new ConsistentCut(computation, true);

        return smallestConsistentCutFromG(computation, predicate, G);
    }


    // Helper method 2: find the maximal consistent cut in "computation" that satisfies "predicate"
    static ConsistentCut largestConsistentCut(Computation computation, Predicate predicate) {
        ConsistentCut G = new ConsistentCut(computation);

        while (!predicate.predicate.apply(G)) {
            int forbiddenPID = predicate.reverseForbiddenState.apply(G);
            // check whether all events in "computation" from this process have been included in G
            if (G.getNumberOfEventsInProcess(forbiddenPID) == 0){
                return null;
            } else {
                Event eventToRemove = G.getEvent(forbiddenPID, G.getNumberOfEventsInProcess(forbiddenPID)-1);
                G.removeEvent(eventToRemove);
            }
        }
        return G;
    }

    // Helper method 3: find the minimal consistent cut in "computation" that satisfies "predicate" and also include "e"
    static ConsistentCut smallestConsistentCutIncludingEvent(Computation computation, Predicate predicate, int pid, int eventIdxInPID) {
        ConsistentCut G = new ConsistentCut(computation, true);
        // populate G with the smallest consistent cut that includes the desired event
        for (int eventId = 0; eventId <= eventIdxInPID; ++eventId) {
            Event eventToAdd = computation.events.get(pid).get(eventId);
            G.addEvent(eventToAdd);
        }

        return smallestConsistentCutFromG(computation, predicate, G);
    }

    // Helper method 4: gets the least consistent cut including at least g that satisfies a given predicate. reduces code duplication.
    private static ConsistentCut smallestConsistentCutFromG(Computation computation, Predicate predicate, ConsistentCut g) {
        while (!predicate.predicate.apply(g)) {
            int forbiddenPID = predicate.findForbiddenState.apply(g);
            // check whether all events in "computation" from this process have been included in G
            if (g.getNumberOfEventsInProcess(forbiddenPID) == computation.events.get(forbiddenPID).size()) {
                return null;
            } else {
                int numberOfEventsInGInProcessForbiddenPID = g.getNumberOfEventsInProcess(forbiddenPID);
                Event nextEvent = computation.events.get(forbiddenPID).get(numberOfEventsInGInProcessForbiddenPID);
                g.addEvent(nextEvent);
            }
        }
        return g;
    }

}
