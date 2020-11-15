
public class Event {
    public String identifier; // an identifier for each Event. Should be global unique
    public int pid;


    public Event(String identifier, int pid) {
        this.identifier = identifier;
        this.pid = pid;
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

        return this.identifier.equals(rhs.identifier) && this.pid == rhs.pid;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        return "(" + pid + ", " + identifier + ")";
    }
}
