package online_distributed;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.function.BinaryOperator;

/* TODO: 1. two problematic observations:
            - when u is enqueued into the system, the token whose event field = (0, 1) (event "a") that has a target field of (2, 1) is not updated
            - when v is enqueued into the system, the token from P_2 is still trying to compute J(u) (and the token's target field matches v), but
              the algorithm will update the token's event field from u to v, effectively forgetting event u.

         2. I am not 100% sure that these two things are what have actually happened, so maybe the first next step to do is to
            - debug one step at a time from enqueuing the first event ("a") until the finish of enqueuing event "u". Understand
                what happened during this period of time to confirm observation 1.
            - continue the debug until the finish of enqueuing event "v". Understand what happended to confirm observation 2.
            
         3. If those two observations are confirmed, do ....
         4. Otherwise, do ....
*/
public class testScheduler {

    public static Boolean customStatePredicate(LocalState[] localStates) {

        return localStates[0].val >= 1 && localStates[1].val != -100 && localStates[2].val <= 3;
    }

    public static Integer findForbiddenProcess(LocalState[] localStates) {
        if (localStates[0].val < 1) {
            return 0;
        } else if (localStates[1].val == -100) {
            return 1;
        } else if (localStates[2].val > 3) {
            return 2;
        } else {
            throw new IllegalArgumentException("no forbidden process bc localStates satisfy the predicate");
        }
    }


        @Test
        public void testScheduler () {
        Predicate predicate = new Predicate(testScheduler::customStatePredicate, testScheduler::findForbiddenProcess);
        Scheduler scheduler = new Scheduler();

        ArrayList<Process> processes = new ArrayList<>();
        int numberOfProcesses = 3;
        for (int pid = 0; pid < numberOfProcesses; ++pid) {
            processes.add(new Process(numberOfProcesses, pid, predicate, scheduler));
        }

        scheduler.addProcesses(processes);


        // one particular order in which events can be generated, among many other orders.
        scheduler.enqueueEvent(new Event(0, 1, new VectorClock(new Integer[]{1, 0, 0}), new LocalState(1)));
        scheduler.enqueueEvent(new Event(1, 1, new VectorClock(new Integer[]{0, 1, 0}), new LocalState(0)));
        scheduler.enqueueEvent(new Event(2, 1, new VectorClock(new Integer[]{0, 0, 1}), new LocalState(4)));
        scheduler.enqueueEvent(new Event(0, 2, new VectorClock(new Integer[]{2, 0, 0}), new LocalState(2)));
        scheduler.enqueueEvent(new Event(1, 2, new VectorClock(new Integer[]{0, 2, 0}), new LocalState(2)));
        scheduler.enqueueEvent(new Event(2, 2, new VectorClock(new Integer[]{0, 2, 2}), new LocalState(1)));
        scheduler.enqueueEvent(new Event(0, 3, new VectorClock(new Integer[]{3, 0, 0}), new LocalState(-1)));
        scheduler.enqueueEvent(new Event(1, 3, new VectorClock(new Integer[]{0, 3, 3}), new LocalState(1)));
        scheduler.enqueueEvent(new Event(2, 3, new VectorClock(new Integer[]{0, 2, 3}), new LocalState(2)));
        scheduler.enqueueEvent(new Event(0, 4, new VectorClock(new Integer[]{4, 0, 0}), new LocalState(0)));
        scheduler.enqueueEvent(new Event(1, 4, new VectorClock(new Integer[]{3, 4, 3}), new LocalState(3)));
        scheduler.enqueueEvent(new Event(2, 4, new VectorClock(new Integer[]{0, 2, 4}), new LocalState(4)));

        Slice slice = scheduler.getMeTheSlice();
        System.out.println(slice);
    }
}
