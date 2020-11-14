import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Slicer {


    Computation slice(Computation computation, Function<ConsistentCut, Boolean> predicate) {
        // Precondition: predicate is a lattice-liner predicate on cuts of "computation"

        // 1. Compute the "smallest" consistent cut V in "computation" such that predicate(V).
        // TODO: utilize the forbidden-state property of linear predicate to find this V by acting greedy. consult p140 of textbook
        ConsistentCut V = smallestConsistentCut(computation, predicate);


        // 2. Compute the "largest" consistent cut W in "computation" such that predicate(W).
        ConsistentCut W = largestConsistentCut(computation, predicate);


        // 3. For each event e in W-V, find the least consistent cut that satisfies B and includes e.
        ConsistentCut WMinusV = W.difference(V);
        Set<ConsistentCut> JOfEs = new HashSet<>();
        for (int i = 0; i < WMinusV.events.size(); ++i) {
            for (int j = 0; j < WMinusV.events.get(i).size(); ++j) {
                // 3.1. compute the "smallest" consistent cut J(e) in "computation" such that predicate(J(e)) && e \in J(e)
                ConsistentCut JOfE = smallestConsistentCut(computation, predicate, i, j);
                JOfEs.add(JOfE);
            }
        }


        // Debugging Step: print out each J(e) and see whether they are all there should be, and whether they're distinct
        int cntxx = 0;
        for (ConsistentCut JOfE : JOfEs) {
            System.out.println("J(e) #" + cntxx + " : " + JOfE);
        }

        // 4. Form equivalence classes based on all the least consistent cuts for each event.

        // Cs stores equivalent classes where each C_i is a distinct consistent cut. Let size(distinct_J_es) = m


        // need to order C_i's according to set inclusion,
        ArrayList<Node> events = new ArrayList<>();
        for (ConsistentCut c : JOfEs) {
            events.add(c.toNode()); // flatten events in C, which is a 2D array of process:events, into just a set of events
        }

        // 4.1 create a partial order of events according to J(e) inclusion
//        Boolean[][] incidenceMatrix = new Boolean[events.size()][events.size()];

        Graph graph = new Graph(events.size());

        for (int i = 0; i < events.size(); ++i) {
            for (int j = 0; j < events.size(); ++j) {
                if (events.get(i).isIncludedIn(events.get(j))) {
                    graph.addEdge(i, j);
//                    incidenceMatrix[i][j] = true;
                }
            }
        }

        // 4.2 equivalence classes are composed of sets of strongly connected components
        // find all the strongly connected components, and return them as equivalence classes
        // but the order of the equivalence classes matters
        ArrayList<ArrayList<Integer>> SCCs = graph.retrieveSCCs();

        return null;
    }



    // Helper method 1: find the minimal consistent cut in "computation" that satisfies "predicate"
    ConsistentCut smallestConsistentCut(Computation computation, Function<ConsistentCut, Boolean> predicate) {
        int numOfProcesses = computation.getNumberOfProcesses();
        ConsistentCut G = new ConsistentCut(numOfProcesses);
        return getConsistentCut(computation, predicate, G);
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
        // populate G with all events in pid until (and including) the desired event index
        for (int eventId = 0; eventId <= eventIdxInPID; ++eventId) {
            Event eventToAdd = computation.events.get(pid).get(eventId);
            G.events.get(pid).add(eventToAdd);
        }

        return getConsistentCut(computation, predicate, G);
    }

    // Helper method 4: gets the least consistent cut including at least g that satisfies a given predicate. reduces code duplication.
    private ConsistentCut getConsistentCut(Computation computation, Function<ConsistentCut, Boolean> predicate, ConsistentCut g) {
        while (!predicate.apply(g)) {
            int forbiddenPID = computation.getForbiddenStateProcessNumber(g, predicate);
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
