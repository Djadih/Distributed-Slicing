import java.util.function.Function;

public class Predicate {
    Function<ConsistentCut, Boolean> predicate;

    Function<ConsistentCut, Integer> findForbiddenState; // given a consistent cut G that does NOT satisfy "predicate", return the process number at which the "forbidden state" is

    Function<ConsistentCut, Integer> reverseForbiddenState;


    public Predicate(Function<ConsistentCut, Boolean> predicate, Function<ConsistentCut, Integer> findForbiddenState, Function<ConsistentCut, Integer> reverseForbiddenState) {
        this.predicate = predicate;
        this.findForbiddenState = findForbiddenState;
        this.reverseForbiddenState = reverseForbiddenState;
    }
}
