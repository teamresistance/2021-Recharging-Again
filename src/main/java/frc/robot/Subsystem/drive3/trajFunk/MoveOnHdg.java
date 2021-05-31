package frc.robot.Subsystem.drive3.trajFunk;

import frc.io.hdw_io.IO;
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
                tSteer.steerTo(hdgSP, pwrMx, distSP);
                Drive.distRst();
                state++;
                System.out.println("---- DONE MOH: 0");
            case 1: // Move forward, steer Auto Heading and Dist
                strCmd = tSteer.update();
                Drive.cmdUpdate(strCmd[1], strCmd[0], true, 2); //Calls steerTo & cmdUpdate for hdg & dist.
                if (tSteer.isDone()) state++; //Chk hdg & dist done.
                System.out.println("---- DONE MOH: 1");
                break;
            case 2:
                done();
                System.out.print("DONE MOH: ");
                System.out.println("\tCoorX: " + IO.getCoorX() + " \tCoorY " + IO.getCoorY() + " \tHdg " + hdgFB());
                break;
        }
    }
}
