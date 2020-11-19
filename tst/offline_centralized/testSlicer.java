package offline_centralized;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class testSlicer {

    private static boolean channelsEmptyPredicate_test1(ConsistentCut cut){
        // function will return true if all channels are empty (no messages in transit)
        // in other words, if there is no message event where the sender is included in the cut, but receiver isn't
        for (Event sender : cut.computation.messages.keySet()) {
            Event recipient = cut.computation.messages.get(sender);

            // for every message event, check that if the sender is included, then the receiver is included
            if (cut.includes(sender)) {
                if (!cut.includes(recipient)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Integer findForbiddenState_test1(ConsistentCut cut){
        // precondition: channelsEmptyPredicate is false
        // this fucntion will find the forbidden state
        // also known as the efficient advancement property

        // function will return the recipient process, as it is the forbidden process with this predicate
        for (Event sender : cut.computation.messages.keySet()) {
            Event recipient = cut.computation.messages.get(sender);

            // for every message event, check that if the sender is included, then the receiver is included
            if (cut.includes(sender)) {
                if (!cut.includes(recipient)) {
                    return recipient.pid;
                }
            }
        }
        throw new IllegalArgumentException("offline_centralized.Predicate was not false");
    }

    private static Integer findForbiddenStateInReverse_test1(ConsistentCut cut){
        // precondition: channelsEmptyPredicate is false
        // this function will find the forbidden state in reverse (used when calculating largest cut)
        // also known as the efficient advancement property

        // function will return the recipient process, as it is the forbidden process with this predicate
        for (Event sender : cut.computation.messages.keySet()) {
            Event recipient = cut.computation.messages.get(sender);

            // for every message event, check that if the sender is included, then the receiver is included
            if (cut.includes(sender)) {
                if (!cut.includes(recipient)) {
                    return sender.pid;
                }
            }
        }
        throw new IllegalArgumentException("offline_centralized.Predicate was not false");
    }

    @Test
    void test1(){
        ArrayList<ArrayList<Event>> events = new ArrayList<>();

        ArrayList<Event> p0 = new ArrayList();
        p0.add(new Event(0, 0));
        p0.add(new Event(0, 1));

        ArrayList<Event> p1 = new ArrayList<>();
        p1.add(new Event(1, 0));
        p1.add(new Event(1, 1));

        ArrayList<Event> p2 = new ArrayList<>();
        p2.add(new Event(2, 0));
        p2.add(new Event(2, 1));

        events.add(p0);
        events.add(p1);
        events.add(p2);

        Map<Event, Event> messages = new HashMap<>();

        messages.put(new Event(0, 0), new Event(1, 0));
        messages.put(new Event(1, 1), new Event(2, 1));

        // create the computation
        Computation computation = new Computation(events, messages);

        // create the predicate, need the predicate and efficientAdvancementFunction first
        Predicate predicate = new Predicate(testSlicer::channelsEmptyPredicate_test1, testSlicer::findForbiddenState_test1, testSlicer::findForbiddenStateInReverse_test1);

        Slice slice = Slicer.slice(computation, predicate);

        assertEquals("", slice.V.toString());
        assertEquals("[[(2, 0)], [(0, 0), (1, 0)], [(0, 1)], [(1, 1), (2, 1)]]", Arrays.toString(slice.nodes));
        assertEquals("[true false false true ]\n" +
                "[false true true true ]\n" +
                "[false false true false ]\n" +
                "[false false false true ]\n", Slice.incidenceMatrixString(slice.incidenceMatrix));
    }


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
        Predicate predicate = new Predicate(testSlicer::customStatePredicate_test2, testSlicer::findForbiddenState_test2, testSlicer::findForbiddenStateInReverse_test2);
        System.out.println(computation);

        Slice slice = Slicer.slice(computation, predicate);

        System.out.println(slice);

    }
}

