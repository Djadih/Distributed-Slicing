import java.util.ArrayList;
import java.util.Comparator;

public class ConsistentCut {
    ArrayList<ArrayList<Event>> events; // nodes[0] = the events from process 0 that are included in this cut.

    // used to initialize the "smallest" consistent cut of a computation, which should always be an empty set
    public ConsistentCut(int N) {
        events = new ArrayList<>(N);
        for (int i = 0; i < N; ++i) {
            events.set(i, new ArrayList<>());
        }
    }

    // Used to initialize the "largest" consistent cut of a computation.
    public ConsistentCut(ArrayList<ArrayList<Event>> events) {
        this.events = events;
    }


    public ConsistentCut difference(ConsistentCut V) {
        // return a cut that consists of events from this.events - rhsCut.events

        // precondition: number Of processes in events == number of processes in rhs.events
        if (events.size() != V.events.size()) {
            throw new IllegalArgumentException();
        }

        // for P_i, add the events that are in Cut 'this' but not in cut V.
        int N = events.size();
        ConsistentCut result = new ConsistentCut(N);
        for (int i = 0; i < N; ++i) {
            int sizeDifference = events.get(i).size() - V.events.get(i).size();
            int startIndex = V.events.get(i).size();
            for (int j = startIndex; j < events.get(i).size(); ++j) {
                result.events.get(i).add(events.get(i).get(j));
            }
        }
        return result;
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
            for (int j = 0; j < events.get(i).size(); ++j) {
                result += 1;
            }
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

    /*Comparator for sorting the ConsistentCut by consistent cut size (i.e. number of events)*/
    public static Comparator<ConsistentCut> ConsistentCutSizeComparator = new Comparator<ConsistentCut>() {

        public int compare(ConsistentCut c1, ConsistentCut c2) {
            int c1Size = c1.getNumberOfEvents();
            int c2Size = c2.getNumberOfEvents();

            // ascending order
            return c1Size - c2Size;
        }};

}
