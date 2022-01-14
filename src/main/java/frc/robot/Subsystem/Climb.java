/*
Desc.  The climber extends arms that have hooks with paracord attached.
Once "hooked up" the robot is then hoisted up.

History
3/1/2020 - Anthony - Original release
3/11 - JCH - hdw_io update
*/
package frc.robot.Subsystem;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.motorcontrol.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.ISolenoid;
import frc.io.joysticks.JS_IO;
import frc.util.Timer;

public class Climb {

    // button for cancel, button to start (press twice: start solenoid, start motor)
    // if its up dont try to come down
    //

    private static Victor climbV = IO.climberHoist;
    private static ISolenoid climbSole = IO.climberExt;
    private static PowerDistribution pdp = IO.pdp;

    private static int state;
    private static Timer timer;

    private static boolean isUp;
    private static boolean rdyForCtl;

    private static double hoistSpeed = 0.6;
    private static int pushCount = 0;

    public static void init() {
        state = -1;
        climbV.set(0);
        pushCount = 0;
        isUp = false;
        rdyForCtl = false;
    }

    private static void determ() {
        if (JS_IO.btnClimbOFF.onButtonPressed() && isUp) {
            state = 0;
            pushCount = 0;
        } else if (JS_IO.btnClimbOFF.onButtonPressed()) {
            state = -1;
            pushCount = 0;
        }

        if (JS_IO.btnClimb.onButtonPressed() && pushCount == 0) {
            state = 1;
            pushCount = 1;
        } else if (JS_IO.btnClimb.onButtonPressed() && pushCount == 1) {
            state = 2;
            pushCount = 2;
        }

        if (rdyForCtl) {
            state  = 5;
        }
    }

    public static void update() {
        sdbUpdate();
        determ();
        switch (state) {
        case -1: // climb motor off, sole down
            cmdUpdate(false, 0);
            break;
        case 0:
            cmdUpdate(true, 0);
        case 1: // sole out
            cmdUpdate(true, 0);
            isUp = true;
            break;
        case 2: // climb motor on
            cmdUpdate(true, hoistSpeed);
            // fill in index and current
            if (timer.hasExpired(0.2, state)) {
                if (pdp.getCurrent(0) >= /* number */ .4) { //wait until the time passed and the pdp current gets stally
                    state = 5;
                }
            }
            isUp = true;
            rdyForCtl = true;
            break;
        case 5: //JS control of winch going up and down
            cmdUpdate(false, JS_IO.axClimb.get() * 0.5);
            break;
        default:
            cmdUpdate(false, 0);
            isUp = false;
            System.out.println("Invalid Climber state - " + state);
            break;
        }
    }

    public static void sdbUpdate() {
        SmartDashboard.putNumber("Climb State", state);
    }

    private static void cmdUpdate(boolean soleON, double winchSpeed) {
        climbSole.set(soleON);
        climbV.set(winchSpeed);
    }

    public static int getState() {
        return state;
    }
}