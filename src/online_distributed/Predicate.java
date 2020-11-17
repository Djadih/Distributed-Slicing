package online_distributed;

import offline_centralized.ConsistentCut;

import java.util.function.Function;

public class Predicate {
    Function<LocalState[], Boolean> predicate;
    Function<LocalState[], Integer> findForbiddenState; // given a consistent cut G that does NOT satisfy "predicate", return the process number at which the "forbidden state" is

    public Predicate(Function<LocalState[], Boolean> predicate, Function<LocalState[], Integer> findForbiddenState) {
        this.predicate = predicate;
        this.findForbiddenState = findForbiddenState;
    }
}
