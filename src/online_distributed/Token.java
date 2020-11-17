package online_distributed;

public class Token {
    Integer pid;
    Event event;
    VectorClock gCut;
    VectorClock depend;
    LocalState[] gState;
    Boolean eval;
    Event target;


    public Token(Integer pid, Event event, VectorClock gCut, VectorClock depend, LocalState[] gState, Boolean eval, Event target) {
        this.pid = pid;
        this.event = event;
        this.gCut = gCut;
        this.depend = depend;
        this.gState = gState;
        this.eval = eval;
        this.target = target;
    }
}
