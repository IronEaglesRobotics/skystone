package eaglesfe.common;

import eaglesfe.common.Step;

public class SleepStep extends Step {

    String nextState;
    public SleepStep(String description, int timeout, String nextState) {
        super(description, timeout);
        this.nextState = nextState;
    }

    public void enter() { }

    public boolean isFinished() { return false; }

    public String leave() { return nextState; }
}