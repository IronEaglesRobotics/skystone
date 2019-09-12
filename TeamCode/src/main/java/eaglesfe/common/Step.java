package eaglesfe.common;

public abstract class Step {
    private final String description;
    private final int timeout;
    private String nextStep;

    public Step (String description, int timeout) {
        this.description = description;
        this.timeout = timeout;
    }

    public Step (String description) {
        this.description = description;
        this.timeout = 10000;
    }

    public boolean isEnabled() { return true; }
    public abstract void enter();
    public abstract boolean isFinished();
    public abstract String leave();

    public String getDescription() { return this.description; }
    public int getTimeout() { return this.timeout; }

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }
}

