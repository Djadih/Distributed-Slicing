import java.util.function.Function;

public class Predicate {
    Function<ConsistentCut, Boolean> predicate;

    Function<ConsistentCut, Integer> efficientAdvancementFunction; // given a consistent cut G that does NOT satisfy "predicate", return the process number at which the "forbidden state" is

    Function<ConsistentCut, Integer> efficientReverseAdvancementFunction;



    public Predicate(Function<ConsistentCut, Boolean> predicate, Function<ConsistentCut, Integer> efficientAdvancementFunction) {
        this.predicate = predicate;
        this.efficientAdvancementFunction = efficientAdvancementFunction;
    }
}
