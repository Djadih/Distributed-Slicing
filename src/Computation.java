import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

public class Computation {
    ArrayList<ArrayList<Event>> events; // events[0] = the array of events in process 0, and so on

    Map<Event, Event> messages; // messages is a map from key=sendEvent to value=receiveEvent

    public int getNumberOfProcesses() {
        return events.size();
    }
}
