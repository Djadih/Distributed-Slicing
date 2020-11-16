package online_distributed;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Scheduler {
    ArrayList<Process> processes;
    Map<VectorClock, Set<Event>> equivalentClasses;


    public void transferToken(Token t, int from, int to) {
        processes.get(from).waitingTokens.remove(t);
        processes.get(to).waitingTokens.add(t);
    }

    public void output(Event e, VectorClock gCut) {
        // gCut is the smallest consistent cut that satisfies B and include event "e"
        Set<Event> equivalentClass = equivalentClasses.getOrDefault(gCut, new HashSet<>());
        equivalentClass.add(e);
        equivalentClasses.put(gCut, equivalentClass);
    }

}
