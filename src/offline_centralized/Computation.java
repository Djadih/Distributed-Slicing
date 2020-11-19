package offline_centralized;

import java.util.ArrayList;
import java.util.Map;

public class Computation {
    final ArrayList<ArrayList<Event>> events; // events[0] = the array of events in process 0, and so on

    final Map<Event, Event> messages; // messages is a map from key=sendEvent to value=receiveEvent

    public Computation(ArrayList<ArrayList<Event>> events, Map<Event, Event> messages){
        this.events = events;
        this.messages = messages;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < events.size(); ++i) {
            for (int j = 0; j < events.get(i).size(); ++j) {
                sb.append(events.get(i).get(j) + (j == events.get(i).size() - 1 ? "\n" : " -> "));
            }
        }
        return sb.toString();
    }

    public int getNumberOfEvents() {
        int result = 0;
        int N = events.size();
        for (int i = 0; i < N; ++i) {
            result += events.get(i).size();
        }
        return result;
    }

    public int getNumberOfEventsInProcess(int pid){
        return events.get(pid).size();
    }

    public int getNumberOfProcesses() {
        return events.size();
    }

    public Event getEvent(int pid, int eid){
        return events.get(pid).get(eid);
    }
}
