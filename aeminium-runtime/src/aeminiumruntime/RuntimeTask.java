package aeminiumruntime;



public interface RuntimeTask extends Task {
    public int getId();
    public Body getBody();
    public void execute();
    public boolean isDone();
    public boolean hasStarted();
}
