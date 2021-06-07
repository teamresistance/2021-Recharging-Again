package frc.robot.Subsystem.drive3.trajFunk;

import frc.io.hdw_io.IO;
import frc.robot.Subsystem.drive3.Steer;

public abstract class ATrajFunction {

    public static int state = 0;
    public static boolean finished = false;
    // public static Steer steer = Drive.steer;  // Used to steer to a hdg with power for distance
    /*                [0][]=hdg [1][]=dist SP, PB, DB, Mn, Mx, Xcl */
    private static double[][] parms = { { 0.0, -110.0, 1.0, 0.55, 1.0, 0.20 },
    /*                               */ { 0.0, 10.0, 0.7, 0.7, 1.0, 0.07 } };
    public static Steer tSteer = new Steer(parms);  //Create steer instance for hdg & dist, use default parms
    public static double hdgFB() {return IO.navX.getNormalizeTo180();}  //Only need hdg to Hold Angle 0 or 180
    public static double[] strCmd = new double[2];

    public static void initTraj() {
        state = 0;
        finished = false;
        tSteer = new Steer(parms);  //Init steer instance for hdg & dist, use default parms
    }

    public void execute() {

    }

    public static void done() {
        finished = true;
        // Drive.cmdUpdate();
    }

    public static boolean finished() {
        return finished;
    }
}
