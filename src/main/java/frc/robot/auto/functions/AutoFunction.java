package frc.robot.auto.functions;

public abstract class AutoFunction {
    
    private boolean finished;


    public void init() {
        sdbInit();
        finished = false;
    }

    public void execute() {

    }

    public void done() {
        finished = true;
    }

    public boolean finished() {
        return finished;
    }

    private void sdbInit() {
        
    }

    private void sdbUpdate() {

    }
}
