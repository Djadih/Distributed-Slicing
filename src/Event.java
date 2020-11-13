
public class Event {
    public String identifier; // an identifier for each Event. Should be global unique


    public Event(String identifier) {
        this.identifier = identifier;
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

        return this.identifier.equals(rhs.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}
