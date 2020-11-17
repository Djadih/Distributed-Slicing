package online_distributed;

import java.util.Arrays;

public class VectorClock {
    Integer[] vectorClock;

    public VectorClock(Integer[] vectorClock) {
        this.vectorClock = vectorClock;
    }

    public VectorClock(int numOfProcesses) {
        vectorClock = new Integer[numOfProcesses];
        for (int i = 0; i < numOfProcesses; ++i) {
            vectorClock[i] = 0;
        }
    }

    public VectorClock(VectorClock rhs) {
        this(rhs.vectorClock.length);
        for (int i = 0; i < this.vectorClock.length; ++i) {
            this.vectorClock[i] = rhs.vectorClock[i];
        }

    }

    public Integer get(int index) {
        return vectorClock[index];
    }

    public void set(int index, Integer val) {
        vectorClock[index] = val;
    }

    public int size() {
        return vectorClock.length;
    }

    public static VectorClock max(VectorClock vc1, VectorClock vc2) {
        // precondition: vc1.size() == vc2.size()
        if (vc1.size() != vc2.size()) {
            throw new IllegalArgumentException();
        }

        Integer[] result = new Integer[vc1.size()];
        for (int i = 0; i < vc1.size(); ++i) {
            result[i] = Math.max(vc1.get(i), vc2.get(i));
        }
        return new VectorClock(result);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        VectorClock rhs = (VectorClock) obj;
        if (vectorClock.length != rhs.vectorClock.length) {
            return false;
        }

        for (int i = 0; i < vectorClock.length; ++i) {
            if (vectorClock[i].equals(rhs.vectorClock[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (int val : vectorClock) {
            result += val;
        }
        return result;
    }

    public boolean isIncludedIn(VectorClock rhs) {
        // this is included in rhs iff this.vectorClock[k] <= rhs.vectorClock[k], for all k
        // Or, equivalently, the component-wise max of this.vectorClock and rhs.vectorClock is equal to rhs.vectorClock
        return VectorClock.max(this, rhs).equals(rhs);
    }

    @Override
    public String toString() {
        return Arrays.toString(vectorClock);
    }
}
