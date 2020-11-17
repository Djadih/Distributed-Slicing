package online_distributed;

public class Token {
    Integer pid;
    Event event;
    VectorClock gCut;
    VectorClock depend;
    LocalState[] gState;
    Boolean eval;
    Event target;


    public Token(Integer numberOfProcesses, Integer pid) {
        this.pid = pid;
        this.event = null;
        this.gCut = new VectorClock(numberOfProcesses);
        this.depend = new VectorClock(numberOfProcesses);
        this.gState = new LocalState[numberOfProcesses];
        for (int i = 0; i < numberOfProcesses; ++i) {
            if (i == 0) {
                this.gState[0] = new LocalState(0);
            } else if (i == 1) {
                this.gState[1] = new LocalState(-100);
            } else {
                this.gState[2] = new LocalState(4);
            }
//            this.gState[i] = new LocalState(0);
        }
        this.eval = false;
        this.target = new Event(pid, 1, null, null);
    }
}
