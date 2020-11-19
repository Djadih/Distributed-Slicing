package offline_centralized;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class testSlicer {

    private static boolean channelsEmptyPredicate(ConsistentCut cut){
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

    private static Integer findForbiddenState(ConsistentCut cut){
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

    private static Integer findForbiddenStateInReverse(ConsistentCut cut){
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
        Predicate predicate = new Predicate(testSlicer::channelsEmptyPredicate, testSlicer::findForbiddenState, testSlicer::findForbiddenStateInReverse);

        long startTime = System.nanoTime();

        Slice slice = Slicer.slicer(computation, predicate);

        long elapsedTime = System.nanoTime() - startTime;

        double secondsElapsed = (double) elapsedTime / 1_000_000_000;

        assertEquals("", slice.V.toString());
        assertEquals("[[(2, 0)], [(0, 0), (1, 0)], [(0, 1)], [(1, 1), (2, 1)]]", Arrays.toString(slice.nodes));
        assertEquals("[true false false true ]\n" +
                "[false true true true ]\n" +
                "[false false true false ]\n" +
                "[false false false true ]\n", Slice.incidenceMatrixString(slice.incidenceMatrix));

        System.out.println("Elapsed time: " + (secondsElapsed) + "seconds");


    }
}

