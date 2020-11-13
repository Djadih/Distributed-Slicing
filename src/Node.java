import java.util.ArrayList;
import java.util.Set;

public class Node {
    Set<Event> events;

    public Node(Set<Event> events) {
        this.events = events;
    }

    public boolean isIncludedIn(Node rhs) {
        // check whether this node is included in rhs, meaning checking whether events in this node, as a set,
        // is included in events in rhs
        return rhs.events.containsAll(events);
    }
}
