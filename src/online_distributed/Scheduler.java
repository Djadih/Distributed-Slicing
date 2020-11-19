package online_distributed;

import java.util.*;

public class Scheduler {
    ArrayList<Process> processes;
    Map<VectorClock, Set<Event>> equivalentClasses;

    int messageCount;

    public Scheduler() {
        this.equivalentClasses = new HashMap<>();
        this.messageCount = 0;
    }

    public void addProcesses(ArrayList<Process> processes) {
        this.processes = processes;
    }

    void transferToken(Token t, int to) {
        processes.get(to).receiveToken(t);
        messageCount++;
    }

    void output(Event e, VectorClock gCut) {
        // gCut is the smallest consistent cut that satisfies B and include event "e"
        Set<Event> equivalentClass = equivalentClasses.getOrDefault(gCut, new HashSet<>());
        equivalentClass.add(e);
        equivalentClasses.put(gCut, equivalentClass);
    }

    // interface method 1 exposed to the user: it simulates the process of P_i enqueuing a new event to its slicer process S_i
    public void enqueueEvent(Event e) {
        processes.get(e.pid).receiveEvent(e);
    }

    // interface method 2 exposed to the user: it returns the slice of the computation so far (here computation is what has been enqueued to the system from each process P_i)
    public Slice getMeTheSlice() {
        // convert equivalentClasses into a proper Slice
        int m = equivalentClasses.size();
        VectorClock[] nodes = new VectorClock[m];

        {
            int i = 0;
            for (VectorClock vc : equivalentClasses.keySet()) {
                nodes[i] = vc;
                i++;
            }
        }

        boolean[][] incidenceMatrix = new boolean[m][m];
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < m; ++j) {
                if (nodes[i].isIncludedIn(nodes[j])) {
                    incidenceMatrix[i][j] = true;
                }
            }
        }

        return new Slice(nodes, incidenceMatrix);
    }

}
