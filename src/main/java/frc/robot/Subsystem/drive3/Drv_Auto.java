package frc.robot.Subsystem.drive3;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;

public class Drv_Auto extends Drive {

    private static int state;
    private static int prvState;

    /*                [0][]=hdg [1][]=dist SP, PB, DB, Mn, Mx, Xcl */
    private static double[][] parms = { { 0.0, -130.0, 3.0, 0.4, 1.0, 0.20 },
    /*                               */ { 0.0, 5.5, 0.5, 0.10, 1.0, 0.07 } };
    private static Steer steer = new Steer(parms);
    private static double[] strCmd;
    private static double hdgFB() { return IO.navX.getAngle(); }
    private static void hdgRst() { IO.navX.reset(); }
    private static double hdgOut;

    private static double distFB() { return (IO.drvEnc_L.feet() + IO.drvEnc_R.feet()) / 2; }
    private static void distRst() { IO.drvEnc_L.reset(); IO.drvEnc_R.reset(); }
    private static double distOut;

    private static double path[][];
    private static int trajIdx;
    private static boolean finished;

    public static void init() {
        sdbInit();          //Initialize sdb
        hdgRst();           //Reset gyro
        distRst();          //Reset encoders, distance to 0.0
        state = 0;          //Initialize state, Off
        prvState = 0;       //Used to reset dist or set timer when entering a new state
        trajIdx = 0;        //Initialize trajectory to first index
    }

    /**
     * Determine any state that needs to interupt the present state, usually by way of a JS button but
     * can be caused by other events.
     */
    public static void determ() {
        path = Traj.getActChsr();
    }

    /**
    * Called from Robot autoPerodic every 20mS to Update the drive sub system.
    * This rotates to the heading then resets the dist. and starts running out to
    * the new distance SP.
    */
    public static void update() {
        determ();
        sdbUpdate();
        // Drive.update();
        switch (state) {
            case -1:
                prvState = state;
                state++;
                break;
            case 0: // Init Trajectory, turn to hdg then (1) ...
                if (prvState != state) {
                    if(path[0].length > 3) optUpdate(); //Update options if called
                    steer.steerTo(path[trajIdx]);       //Initialize steer hdg & dist
                    distRst();
                } else {
                    // Calc heading & dist output. rotation X, speed Y
                    strCmd = steer.update(hdgFB(), distFB());
                    hdgOut = strCmd[0]; // Get hdg output, Y
                    distOut = 0.0; // Get distance output, X
                    // Apply as a arcade joystick input
                    // hdgOut = BotMath.SegLine(hdgOut, xOutAr); //Compensate for poor turning.
                    cmdUpdate(distOut, hdgOut, false, 2);  // Apply as a arcade joystick input

                    // Chk if trajectory is done
                    if (steer.isHdgDone()) {
                        state = 1; // Chk hdg only
                        distRst();
                    }
                }
                prvState = state;
                break;
            case 1: // steer Auto Heading and Dist
                // Calc heading & dist output. rotation X, speed Y
                strCmd = steer.update(hdgFB(), distFB());
                hdgOut = strCmd[0];
                distOut = strCmd[1];
                cmdUpdate(distOut, hdgOut, false, 2);  // Apply as a arcade joystick input

                // Chk if trajectory is done
                if (steer.isDistDone()) {
                    state = 2; // Chk distance only
                }
                prvState = state;
                break;
            case 2: // Increment Auto Index & chk for done all traj.
                cmdUpdate();            //Stop moving
                if (prvState != state) {
                    prvState = state;   // Let other states see change of state, COS
                } else {
                    trajIdx++;
                    state = trajIdx < path.length ? 0  : 3;     //Next traj else finished
                }
                break;
            case 3:
                done();
                break;
        }
    }

    public static void done() {
        finished = true;
        cmdUpdate();            //Stop moving
    }

    private static void sdbInit() {
    }

    private static void sdbUpdate() {
        SmartDashboard.putNumber("Drv/Auto/Auto Step", state); // Set by JS btns

        SmartDashboard.putNumber("Drv/Auto/Hdg FB", hdgFB());
        SmartDashboard.putNumber("Drv/Auto/Hdg Out", hdgOut);

        SmartDashboard.putNumber("Drv/Auto/Dist FB", distFB());
        SmartDashboard.putNumber("Drv/Auto/Dist Out", distOut);
        SmartDashboard.putNumber("Drv/Auto/Traj Idx", trajIdx);
    }

    public static boolean finished() { return finished; }
    public static int getState() { return state; }


    private static void optUpdate(){
        boolean tstCmd = ((int)path[trajIdx][3] & 1) > 0;
    }

}
