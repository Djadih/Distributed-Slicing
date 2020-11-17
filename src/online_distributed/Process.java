package online_distributed;

import java.util.ArrayList;

public class Process {
    Predicate predicate; // a regular predicate
    Scheduler scheduler;

    final int N;
    final int pid;
    ArrayList<Event> localEvents; // local events in P_i so far. New events will keep coming.
    ArrayList<Token> waitingTokens; // tokens that are waiting at this process

    public Process(int N, int pid, Predicate predicate, Scheduler scheduler) {
        this.predicate = predicate;
        this.scheduler = scheduler;

        this.N = N;
        this.pid = pid;
        localEvents = new ArrayList<>();
        waitingTokens = new ArrayList<>();
        waitingTokens.add(new Token(pid,null, new VectorClock(N), new VectorClock(N), new LocalState[N],false, new Event(pid, 0, null, null)));
    }

    // handler upon receiving an new event (time-stamped) "e"
    public void receiveEvent(Event e) {
        localEvents.add(e);

        for (int i = 0; i < waitingTokens.size(); ++i) {
            Token waitingToken = waitingTokens.get(i);
            if (waitingToken.target.equals(e)) {
                addEventToToken(waitingToken, e);
                processToken(waitingToken);
            }
        }
    }

    public void addEventToToken(Token t, Event e) {
        t.gState[e.pid] = e.localState;
        t.gCut.set(e.pid,  e.eid);
        if (t.pid == pid) {
            // my token:update tokens' event pointer
            t.event = e;
        }
        t.depend = VectorClock.max(t.depend, e.vectorClock); // set causal dependency
    }


    public void processToken(Token t) {
        int k = findInconsistencyProcessNumber(t);
        if (k != -1) {
            /* find k : t.gcut[k] < t.depend[k] */
            t.target = new Event(k, t.gCut.get(k) + 1, null, null); // vectorClock is (unknown and also) unused as far as t.target is concerned
            transferToken(t, k); // send t to S_k
        } else {
            evaluateToken(t);
        }
    }

    // find the the first k such that t.gCut[k] < t.depend[k], k is in [0, #OfProcesses]
    // return -1 if t.gCut is already a consistent cut
    private int findInconsistencyProcessNumber(Token t) {
        // return True iff t.gcut[i] >= t.depende[i] for all i
        for (int i = 0; i < t.gCut.size(); ++i) {
            if (t.gCut.get(i) < t.depend.get(i)) {
                return i;
            }
        }
        return -1;
    }

    public void evaluateToken(Token t) {
        if (predicate.predicate.apply(t.gState)) {
            // B is true on cut given by t.gCut
            t.eval = true;
            transferToken(t, t.pid); // send token back to its original owner
        } else {
            t.eval = false;
            int k = predicate.findForbiddenState.apply(t.gState); // P_k is the forbidden process
            t.target = new Event(k, t.gCut.get(k) + 1, null, null);
            transferToken(t, k); // send token to S_k
        }
    }

    // handler upon receiving a token
    public void receiveToken(Token t) {
        if (t.eval && t.pid == pid) {
            // my token, B true
            scheduler.output(t.event, t.gCut);
            t.target = new Event(pid, t.gCut.get(pid) + 1, null, null);
            waitingTokens.add(t);
        } else {
            // either inconsistent cut, or predicate false
            int newID = t.target.eid; // id of event that t requires
            Event f = findlocalEventwithID(newID);
            if (f != null) {
                // required event has happened
                addEventToToken(t, f);
                // TODO: ask the author to verify this!
                processToken(t);
//                evaluateToken(t); This is what the paper said but we believe it is not correct. Instead, we think we should use processToken
            } else {
                waitingTokens.add(t); //???
            }

        }
    }

    private Event findlocalEventwithID(int eid) {
        for (Event e : localEvents) {
            if (e.eid == eid) {
                return e;
            }
        }
        return null;
    }

    private void transferToken(Token t, int to) {
        waitingTokens.remove(t);
        scheduler.transferToken(t, pid, to);
    }
}
