import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

public class Computation {
    ArrayList<ArrayList<Event>> events; // events[0] = the array of events in process 0, and so on

    Map<Event, Event> messages; // messages is a map from key=sendEvent to value=receiveEvent

    public Computation(ArrayList<ArrayList<Event>> events, Map<Event, Event> messages){
        this.events = events;
        this.messages = messages;
    }

    public int getNumberOfProcesses() {
        return events.size();
    }

    public int getTotalNumberOfEvents(){
        int eventNum = 0;

        for (int i = 0; i < events.size(); i++){
            for (int j = 0; j < events.get(i).size(); j++){
                eventNum++;
            }
        }

        return eventNum;
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
}
