package offline_centralized;

import java.util.Set;

public class Node {
    Set<Event> events;

    public Node(Set<Event> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return events.toString();
    }
}
