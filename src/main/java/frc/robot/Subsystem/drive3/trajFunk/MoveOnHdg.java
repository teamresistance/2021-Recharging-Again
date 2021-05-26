package frc.robot.Subsystem.drive3.trajFunk;

import frc.robot.Subsystem.drive3.Drive;

/**
 * This ATrajFunction turns to passed heading while it moves passed distance.
 */
public class MoveOnHdg extends ATrajFunction {

    // General
    private double hdgSP = 0.0;
    private double pwrMx = 0.0;
    private double distSP = 0.0;

    // dont use negative power - why?
    public MoveOnHdg(double eHdg, double ePwr, double eDist) {
        hdgSP = eHdg;
        pwrMx = ePwr;
        distSP = eDist;
    }

    public void execute() {
        switch (state) {
            case 0: // Init Trajectory, turn to hdg then (1) ...
                steer.steerTo(hdgSP, pwrMx, distSP);
                Drive.distRst();
                state++;
            case 1: // Move forward, steer Auto Heading and Dist
                Drive.cmdUpdate(0);
                // Chk if distance is done
                if (steer.isDone()) state++; // Chk distance only
                break;
            case 2:
                done();
                break;
        }
    }
}
