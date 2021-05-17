package frc.robot.Subsystem.drive3.trajFunk;

import frc.robot.Subsystem.drive3.Drive;
import frc.robot.Subsystem.drive3.Steer;

public abstract class ATrajFunction {

    public double hdgSP = 0.0;
    public double distSP = 0.0;
    public double pwrMx = 1.0;
    public Steer steer = Drive.steer;  // Used to steer to a hdg with power for distance

    private boolean finished;


    public void init() {

    }

    public void execute() {

    }

    public void done() {

    }

    public boolean finished() {
        return finished;
    }

    private void sdbInit() {
        
    }

    private void sdbUpdate() {

    }
}
