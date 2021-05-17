package frc.robot.Subsystem.drive3.trajFunk;

import frc.robot.Subsystem.drive3.Drive;

/**
 * This AutoFunction turns to passed heading using tank drive.
 */
public class TankTurnHdg extends ATrajFunction {

    // General
    private int state;
    private boolean finished = false;
    private double hdg = 0.0;
    private double lPwr = 0.0;
    private double rPwr = 0.0;

    // dont use negative power
    public TankTurnHdg(double _hdg, double _lPwr, double _rPwr) {
        hdg = _hdg;
        lPwr = _lPwr;
        rPwr = _rPwr;
    }

    public void init() {
        state = 0;
        finished = false;
    }

    public void execute() {
        update();
        switch (state) {
            case 0: // Init Trajectory, turn to hdg then (1) ...
                steer.steerTo(hdg, 1.0, 0.0);
                state++;
            case 1: // Turn to heading using l & r Pwr's passed as tank drive.
                Drive.cmdUpdate(-lPwr, -rPwr, true, 1);
                // Chk if trajectory is done
                steer.update();
                // if(Math.abs(Drive.hdgFB() - hdg) < 5.0 ) state++;  //This is a kludge to get things working
                System.out.println("-------- hdg -------:  " + Drive.hdgFB());
                if (steer.isHdgDone()) state++;    // Chk hdg only
                break;
            case 2:
                done();
                break;
        }
    }

    public void done() {
        Drive.cmdUpdate();
        finished = true;
    }

    public boolean finished() {
        return finished;
    }

    public void update() {
        sdbUpdate();
    }

    private void sdbInit() {
        // SmartDashboard.putNumber("AF/TTH/Step", state);

        // SmartDashboard.putNumber("AF/Hdg Out", hdgOut);
        // SmartDashboard.putNumber("AF/Dist Out", distOut);
    }

    private void sdbUpdate() {
        // SmartDashboard.putNumber("AF/Dist FB", distFB());
        // SmartDashboard.putNumber("AF/Dist Out", distOut);
        // SmartDashboard.putNumber("AF/Hdg FB", hdgFB());
        // SmartDashboard.putNumber("AF/Hdg Out", hdgOut);
        // SmartDashboard.putNumber("AF/TTH/Step", state); // Set by JS btns
    }
}
