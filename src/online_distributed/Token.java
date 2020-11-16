package online_distributed;

public class Token {
    Integer pid;
    Event event;
    VectorClock gCut;
    VectorClock depend;
    // gstate
    Boolean eval;
    Event target;


    public Token(Integer pid, Event event, VectorClock gCut, VectorClock depend, Boolean eval, Event target) {
        this.pid = pid;
        this.event = event;
        this.gCut = gCut;
        this.depend = depend;
        this.eval = eval;
        this.target = target;
    }
}
