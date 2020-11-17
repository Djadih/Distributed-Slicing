package online_distributed;

public class Event {
    public final int pid;
    public final int eid; // an identifier for each offline_centralized.Event. Should be global unique
    public final VectorClock vectorClock;
    public final LocalState localState;

    public Event(int pid, int eid, VectorClock vectorClock, LocalState localState) {
        this.pid = pid;
        this.eid = eid;
        this.vectorClock = vectorClock;
        this.localState = localState;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Event rhs = (Event) obj;

        return this.eid == rhs.eid && this.pid == rhs.pid;
    }

    @Override
    public int hashCode() {
        return pid + eid;
    }

    @Override
    public String toString() {
        return "(" + pid + ", " + eid + ")";
    }
}
