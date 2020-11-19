package offline_centralized;

import java.util.ArrayList;
import java.util.Map;

public class ConsistentCut {
    private ArrayList<ArrayList<Event>> events; // nodes[0] = the events from process 0 that are included in this cut.
    Computation computation;

    // used to initialize the "smallest" consistent cut of a computation, which should always be an empty set
    // this constructor will create N processes
    public ConsistentCut(Computation computation, boolean empty) {
        if (empty) {
            this.events = new ArrayList<>();
            for (int i = 0; i < computation.getNumberOfProcesses(); ++i) {
                events.add(new ArrayList<>());
            }
        }
        else {
            // deep copy computation.events (a 2D arraylist) to this.events
            this.events = new ArrayList<>();
            for (int i = 0; i < computation.getNumberOfProcesses(); ++i) {
                this.events.add(new ArrayList<>());
                for (int j = 0; j < computation.getNumberOfEventsInProcess(i); ++j) {
                    LocalState state = computation.getEvent(i, j).localState;
                    if (state == null){
                        this.events.get(i).add(new Event(i, j, null));
                    }
                    else {
                        this.events.get(i).add(new Event(i, j, new LocalState(computation.getEvent(i, j).localState.val)));
                    }
                }
            }
        }
        this.computation = computation;
    }

    public ConsistentCut(Computation computation){
        this(computation, false);
    }

    // adds the specified event to "events", as well as any additional events necessary to keep it consistent
    public void addEvent(Event event){
        events.get(event.pid).add(event);
        this.makeConsistentForward();
    }

    // removes specified event from "events", as well as removing any events necessary to keep it consistent
    public void removeEvent(Event event){
        events.get(event.pid).remove(event);
        this.makeConsistentBackward();
    }

    // adds necessary events to make the cut consistent
    private void makeConsistentForward(){
        if (this.isConsistent()){
            return;
        }
        else{
            Event missingSender = findMissingSender();
            for (int i = this.events.get(missingSender.pid).size(); i <= missingSender.eid; i++){
                events.get(missingSender.pid).add(computation.events.get(missingSender.pid).get(i));
            }
            this.makeConsistentForward();
        }
    }

    // removes necessary events to make the cut consistent
    private void makeConsistentBackward(){
        if (this.isConsistent()) {
            return;
        }
        else{
            Event extraRecipient = findExtraRecipient();
            for (int i = this.events.get(extraRecipient.pid).size()-1; i >= extraRecipient.eid; i--){
                events.get(extraRecipient.pid).remove(i);
            }
            this.makeConsistentBackward();
        }
    }

    // checks if consistent
    private boolean isConsistent(){
        return findMissingSender() == null;
    }

    // returns the send event that is missing in order to make the cut consistent
    private Event findMissingSender(){
        for (Map.Entry<Event, Event> entry : computation.messages.entrySet()){
            if (!this.includes(entry.getKey()) && this.includes(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    // returns the excess receive event (that should be removed) in order to make the cut consistent
    private Event findExtraRecipient(){
        for (Map.Entry<Event, Event> entry : computation.messages.entrySet()){
            if (!this.includes(entry.getKey()) && this.includes(entry.getValue())) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        ConsistentCut rhs = (ConsistentCut) obj;

        if (events.size() != rhs.events.size()) {
            return false;
        }
        // check this consistent cut contains the same offline_centralized.Event at each and all processes as rhs consistent cut.
        int N = events.size();
        for (int i = 0; i < N; ++i) {
            if (events.get(i).size() != rhs.events.get(i).size()) {
                return false;
            }
            for (int j = 0; j < events.get(i).size(); ++j) {
                if (!events.get(i).get(j).equals(rhs.events.get(i).get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return getNumberOfEvents();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (int i = 0; i < events.size(); ++i) {
            for (int j = 0; j < events.get(i).size(); ++j) {
                sb.append(events.get(i).get(j) + (j==events.get(i).size() -1 ? "\n" : " -> "));
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

    public boolean isIncludedIn(ConsistentCut rhs) {
        // return whether this consistent cut is included in rhs using set inclusion

        if (rhs == this) {
            return true;
        }

        if (rhs == null || events.size() != rhs.events.size() || getNumberOfEvents() > rhs.getNumberOfEvents()) {
            return false;
        }

        int N = events.size();
        for (int i = 0; i < N; ++i) {
            if (events.get(i).size() > rhs.events.get(i).size()) {
                return false;
            }

            for (int j = 0; j < events.get(i).size(); ++j) {
                if (!events.get(i).get(j).equals(rhs.events.get(i).get(j))) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean includes(Event event){
        for (int i = 0; i < events.size(); i++){
            for (int j = 0; j < events.get(i).size(); j++){
                if (event.equals(events.get(i).get(j)))
                    return true;
            }
        }
        return false;
    }
}
