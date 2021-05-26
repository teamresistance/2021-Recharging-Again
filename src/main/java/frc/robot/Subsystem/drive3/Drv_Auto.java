package frc.robot.Subsystem.drive3;

import frc.util.Timer;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;

public class Drv_Auto extends Drive {

    private static int state;
    private static int prvState;

    private static double path[][];
    private static int trajIdx;
    private static boolean finished;
    private static Timer brakeTmr = new Timer(0.5);
    private static boolean brake = false;

    public static void init() {
        sdbInit();          //Initialize sdb
        hdgRst();           //Reset gyro
        distRst();          //Reset encoders, distance to 0.0
        state = 0;          //Initialize state, Off
        prvState = -1;       //Used to reset dist or set timer when entering a new state
        trajIdx = 0;        //Initialize trajectory to first index
    }

    /**
     * Determine any state that needs to interupt the present state, usually by way of a JS button but
     * can be caused by other events.
     */
    public static void determ() {
        path = Traj.getActChsr(IO.drvAutoPwr);
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
                    // strCmd = steer.update(hdgFB(), IO.drvFeet);
                    strCmd = steer.update(hdgFB(), distFB());
                    hdgOut = strCmd[0]; // Get hdg output, Y
                    distOut = 0.0; // Get distance output, X
                    cmdUpdate(distOut, hdgOut, true, 2);  // Apply as a arcade joystick input

                    // Chk if trajectory is done
                    if (steer.isHdgDone()) {
                        state = 1; // Chk hdg only
                        distRst();
                    }
                }
                prvState = state;
                break;
            case 1: // steer Auto Heading and Dist
                // if(state != prvState) distRst();
                // Calc heading & dist output. rotation X, speed Y
                strCmd = steer.update(hdgFB(), distFB());
                hdgOut = strCmd[0];
                distOut = strCmd[1];
                cmdUpdate(distOut, hdgOut, true, 2);  // Apply as a arcade joystick input

                // Chk if trajectory is done
                if (steer.isDistDone()) {
                    state = brake ? 10 : 2; // Chk distance only.  Brake then go to Increment index
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
            case 10:        //Brake state
                if(brakeTmr.hasExpired(0.15, state)) state = 11;
                cmdUpdate( path[trajIdx][1] > 0.0 ? 0.5 : -0.5, 0.0, false, 2 );
                break;
                case 11:        //Brake state
                brakeTmr.hasExpired(0.5, state);    //Set trgr for nex brake
                state = 2;
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

        SmartDashboard.putNumber("Drv/Auto/Dist FB2", IO.drvFeet());
        SmartDashboard.putNumber("Drv/Auto/Dist FB", distFB());
        SmartDashboard.putNumber("Drv/Auto/Dist Out", distOut);
        SmartDashboard.putNumber("Drv/Auto/Traj Idx", trajIdx);
        SmartDashboard.putString("Drv/Auto/Traj Name", Traj.getTrajChsrName());
    }

    public static boolean finished() { return finished; }
    public static int getState() { return state; }


    private static void optUpdate(){
        brake = ((int)path[trajIdx][3] & 8) > 0;    //Opt. to apply braking
    }

}
