import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class ConsistentCut {
    // TODO: this should be standardized to use the new computation class, rather than events
    ArrayList<ArrayList<Event>> events; // nodes[0] = the events from process 0 that are included in this cut.
    Computation computation;

    // used to initialize the "smallest" consistent cut of a computation, which should always be an empty set
    public ConsistentCut(int N, Computation computation) {
        this.events = new ArrayList<>();
        for (int i = 0; i < N; ++i) {
            events.add(new ArrayList<>());
        }
        this.computation = computation;
    }

    public ConsistentCut(Computation computation){
        this.computation = computation;
        this.events = computation.events;
    }

    // Used to initialize the "largest" consistent cut of a computation.
//    public ConsistentCut(ArrayList<ArrayList<Event>> events) {
//        this.events = events;
//    }


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
        // check this consistent cut contains the same Event at each and all processes as rhs consistent cut.
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
        // TODO: need to investigate this, there will probably be a lot of collisions if we use this hash method
        return getNumberOfEvents();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
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
                if (event.identifier.equals(events.get(i).get(j).identifier))
                    return true;
            }
        }
        return false;
    }

    /*Comparator for sorting the ConsistentCut by consistent cut size (i.e. number of events)*/
    public static Comparator<ConsistentCut> ConsistentCutSizeComparator = new Comparator<ConsistentCut>() {

        public int compare(ConsistentCut c1, ConsistentCut c2) {
            int c1Size = c1.getNumberOfEvents();
            int c2Size = c2.getNumberOfEvents();

            // ascending order
            return c1Size - c2Size;
        }};



}
