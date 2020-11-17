package online_distributed;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.function.BinaryOperator;

public class testScheduler {

    public static Boolean customStatePredicate(LocalState[] localStates) {
        return localStates[0].val >= 1 && localStates[2].val <= 3;
    }

    public static Integer findForbiddenProcess(LocalState[] localStates) {
        if (localStates[0].val < 1) {
            return 0;
        } else if (localStates[2].val <= 3) {
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
        scheduler.enqueueEvent(new Event(0, 0, new VectorClock(new Integer[]{1, 0, 0}), new LocalState(1)));
        scheduler.enqueueEvent(new Event(1, 0, new VectorClock(new Integer[]{0, 1, 0}), new LocalState(0)));
        scheduler.enqueueEvent(new Event(2, 0, new VectorClock(new Integer[]{0, 0, 1}), new LocalState(4)));
        scheduler.enqueueEvent(new Event(0, 1, new VectorClock(new Integer[]{2, 0, 0}), new LocalState(2)));
        scheduler.enqueueEvent(new Event(1, 1, new VectorClock(new Integer[]{0, 2, 0}), new LocalState(2)));
        scheduler.enqueueEvent(new Event(2, 1, new VectorClock(new Integer[]{0, 2, 2}), new LocalState(1)));
        scheduler.enqueueEvent(new Event(0, 2, new VectorClock(new Integer[]{3, 0, 0}), new LocalState(-1)));
        scheduler.enqueueEvent(new Event(1, 2, new VectorClock(new Integer[]{0, 3, 3}), new LocalState(1)));
        scheduler.enqueueEvent(new Event(2, 2, new VectorClock(new Integer[]{0, 2, 3}), new LocalState(2)));
        scheduler.enqueueEvent(new Event(0, 3, new VectorClock(new Integer[]{4, 0, 0}), new LocalState(0)));
        scheduler.enqueueEvent(new Event(1, 3, new VectorClock(new Integer[]{3, 4, 3}), new LocalState(3)));
        scheduler.enqueueEvent(new Event(2, 3, new VectorClock(new Integer[]{0, 2, 4}), new LocalState(4)));

        Slice slice = scheduler.getMeTheSlice();
        System.out.println(slice);
    }
}
