package offline_centralized;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class statePredicate {
    private static boolean customStatePredicate_test2(ConsistentCut cut) {
        if (cut.getNumberOfProcesses() != 3) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < cut.getNumberOfProcesses(); ++i) {
            if (cut.getNumberOfEventsInProcess(i) == 0) {
                return false; // implicitly requiring at least one event from each process
            }
        }

        if (cut.getEvent(0, cut.getNumberOfEventsInProcess(0) - 1).localState.val < 1) {
            return false;
        }

        if (cut.getEvent(1, cut.getNumberOfEventsInProcess(1) - 1).localState.val == -100) {
            return false;
        }

        if (cut.getEvent(2, cut.getNumberOfEventsInProcess(2) - 1).localState.val > 3) {
            return false;
        }

        return true;
    }

    private static int findForbiddenState_test2(ConsistentCut cut) {
        if (cut.getNumberOfProcesses() != 3) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < cut.getNumberOfProcesses(); ++i) {
            if (cut.getNumberOfEventsInProcess(i) == 0) {
                return i; // implicitly requiring at least one event from each process
            }
        }

        if (cut.getEvent(0, cut.getNumberOfEventsInProcess(0) - 1).localState.val < 1) {
            return 0;
        }
        if (cut.getEvent(1, cut.getNumberOfEventsInProcess(1) - 1).localState.val == -100) {
            return 1;
        }
        if (cut.getEvent(2, cut.getNumberOfEventsInProcess(2) - 1).localState.val > 3) {
            return 2;
        }

        throw new IllegalStateException();
    }

    private static Integer findForbiddenStateInReverse_test2(ConsistentCut cut){
        return findForbiddenState_test2(cut); // for a conjunctive predicate, reverse forbidden process is equal to forward forbidden state (Why?)
    }

    @Test
    void test2() {
        ArrayList<ArrayList<Event>> events = new ArrayList<>();

        ArrayList<Event> p0 = new ArrayList<>();
        p0.add(new Event(0, 0, new LocalState(1)));
        p0.add(new Event(0, 1, new LocalState(2)));
        p0.add(new Event(0, 2, new LocalState(-1)));
        p0.add(new Event(0, 3, new LocalState(0)));

        ArrayList<Event> p1 = new ArrayList<>();
        p1.add(new Event(1, 0, new LocalState(0)));
        p1.add(new Event(1, 1, new LocalState(2)));
        p1.add(new Event(1, 2, new LocalState(1)));
        p1.add(new Event(1, 3, new LocalState(3)));

        ArrayList<Event> p2 = new ArrayList<>();
        p2.add(new Event(2, 0, new LocalState(4)));
        p2.add(new Event(2, 1, new LocalState(1)));
        p2.add(new Event(2, 2, new LocalState(2)));
        p2.add(new Event(2, 3, new LocalState(4)));

        events.add(p0);
        events.add(p1);
        events.add(p2);

        Map<Event, Event> messages = new HashMap<>();
        messages.put(p0.get(2), p1.get(3));
        messages.put(p1.get(1), p2.get(1));
        messages.put(p2.get(2), p1.get(2));

        Computation computation = new Computation(events, messages);
        Predicate predicate = new Predicate(statePredicate::customStatePredicate_test2, statePredicate::findForbiddenState_test2, statePredicate::findForbiddenStateInReverse_test2);

        long startTime = System.nanoTime();

        Slice slice = Slicer.slice(computation, predicate);

        long elapsedTime = System.nanoTime() - startTime;

        double secondsElapsed = (double) elapsedTime / 1_000_000_000;

        System.out.println(slice);
        System.out.println("Elapsed time: " + (secondsElapsed) + " seconds");
    }
}