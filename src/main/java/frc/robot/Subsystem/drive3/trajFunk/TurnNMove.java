package frc.robot.Subsystem.drive3.trajFunk;

import frc.robot.Subsystem.drive3.Drive;

/**
 * This AutoFunction turns to passed heading then moves passed distance.
 */
public class TurnNMove extends ATrajFunction {

    // private boolean finished = false;
    private double hdgSP = 0.0;
    private double pwrMx = 0.0;
    private double distSP = 0.0;

    // dont use negative power
    public TurnNMove(double eHdg, double ePwr, double eDist) {
        hdgSP = eHdg;
        pwrMx = ePwr;
        distSP = eDist;
    }

    public void execute() {
        // update();
        switch (state) {
            case 0: // Init Trajectory, turn to hdg then (1) ...
                tSteer.steerTo(hdgSP, pwrMx, distSP);
                state++;
            case 1: // Turn to heading.  Do not move forward, yet.
                Drive.cmdUpdate(2);
                // Chk if trajectory is done
                if (tSteer.isHdgDone()) {
                    state++;    // Chk hdg only
                    Drive.distRst();
                }
                break;
            case 2: // Move forward, steer Auto Heading and Dist
                Drive.cmdUpdate(0);
                // Chk if distance is done
                if (tSteer.isDistDone()) state++; // Chk distance only
                break;
            case 3:
                done();
                break;
        }
    }
}
