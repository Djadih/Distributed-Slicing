import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.module.FindException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class testSlicer {

//    Computation computation;
//    Predicate predicate;

//    public testSlicer(Computation computation, Predicate predicate){
//        this.computation = computation;
//        this.predicate = predicate;
//    }

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
        throw new IllegalArgumentException("Predicate was not false");
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
        throw new IllegalArgumentException("Predicate was not false");
    }

    @Test
    void test1(){
        ArrayList<ArrayList<Event>> events = new ArrayList<>();

        ArrayList<Event> p0 = new ArrayList();
        p0.add(new Event("e1", 0));
        p0.add(new Event("e2", 0));

        ArrayList<Event> p1 = new ArrayList<>();
        p1.add(new Event("f1", 1));
        p1.add(new Event("f2", 1));

        ArrayList<Event> p2 = new ArrayList<>();
        p2.add(new Event("g1", 2));
        p2.add(new Event("g2", 2));

        events.add(p0);
        events.add(p1);
        events.add(p2);

        Map<Event, Event> messages = new HashMap<>();

        messages.put(new Event("e1", 0), new Event("f1", 1));
        messages.put(new Event("f2", 1), new Event("g2", 2));

        // create the computation
        Computation computation = new Computation(events, messages);

        // create the predicate, need the predicate and efficientAdvancementFunction first
        Predicate predicate = new Predicate(testSlicer::channelsEmptyPredicate, testSlicer::findForbiddenState, testSlicer::findForbiddenStateInReverse);

        Slice slice = Slicer.slicer(computation, predicate);

        System.out.println(slice);
    }
}

