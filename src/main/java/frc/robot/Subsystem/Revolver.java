/*
Desc.  The revolver collects balls from the snorfler.  It holds up to 5
until requested to deliever them to the shooter.

3/1/2020 - Anthony - Original release
3/11 - JCH - updated hdw_io and cleanup
*/
package frc.robot.Subsystem;

import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.InvertibleDigitalInput;
import frc.io.joysticks.JS_IO;
import frc.util.Timer;

// CHANGES: ONE BANNER SENSOR UNDER(?) THE BALL
//          BANNER SENSOR ON FEED NOT ON SUCK 

public class Revolver {
    private static Victor revolver = IO.revolverRot;        //PWM ESC control
    private static InvertibleDigitalInput atIndexStop = IO.revolerIndexer;  //Mag switch on gear for one rotation
    private static InvertibleDigitalInput ballInSnorfler = IO.snorfHasBall;     //Ball at top of snorfler
    private static InvertibleDigitalInput nextSpaceOpen = IO.revolNextSpaceOpen;//Banner snsr, chk if a ball is in rcv'iing slot

    public static int state = 0;
    public static boolean reqRevShtr = false;
    public static boolean reqRevSnflr = false;
    private static boolean locked = false;
    private static int ballCnt = 0;             // Increment when a ball is loaded. Clear on unload.
    private static boolean isFull = false;      //Ball cnt > 4
    private static boolean jammedBall = false;  //Jammed ball, attempt to clear by reversing
    private static boolean hasUnloaded;         //Ball cnt < 1?
    private static boolean hasShot;             //??
    private static int slowFireCnt = 0;         //??

    private static double revPct = 0.25;        // Spd when Indexing

    private static Timer delayTimer = new Timer(0.50);

    public static void init() { // initialze
        hasUnloaded = false;
        hasShot = false;
        state = 3;
        isFull = false;
        revolver.set(0.0);
    }

    public static void determ() { // determinator of state


        // if (JS_IO.btnFireShooter.isDown()) state = 11; // Unload

        //single fire test
        if (JS_IO.btnSlowFire.onButtonPressed()) state = 1;
            //slowFireCnt = 0;

        // if (JS_IO.btnStop.isDown()) state = 0;

        // if (JS_IO.btnIndex.isDown()) {
        //     ballCnt = 0;
        //     state = 30;
        // }

        // if (Snorfler.hasBall() && state != 1 && !isFull) state = 1; // Load

        // if (IO.revolver_HAA && state < 90) { // jammed Ball
        //     state = 90; // Clear jammed ball
        // }
    }

    public static void update() { // cases for state of revolver
        determ();
        sdbUpdate();

        switch (state) {
            case 0: // everything off, revolverSpeed. Waiting for Load or Unload.
                cmdUpdate(0.0);
                if (delayTimer.hasExpired(0.05, state)); // Initialize timer for covTrgr
                break;
            // ------------ Start indexing --------------------
            case 1: // wait some time ... if needed
                cmdUpdate(0.0);
                if (delayTimer.hasExpired(0.05, state)) state++; // Wait time (settle time?)
                break;
            case 2: // Motor on
                cmdUpdate(revPct);
                if (!atIndexStop.get()) state++;    // Wait for sw goes false.
                break;
            case 3: // Wait for sw to go true
                cmdUpdate(revPct);
                if(atIndexStop.get()) state++;      //Wait until sw goes true again
                break;
            case 4: // Brake with small negative
                cmdUpdate(-0.15);
                if (delayTimer.hasExpired(0.07, state)) state++;    //Brake time
                break;
            case 5: //Stop
                cmdUpdate(0.0);
                if (delayTimer.hasExpired(0.15, state)) state++;    //Shutdown time
                break;
            case 6: //return to start
                state = !isFull() && !nextSpaceOpen.get() ? 0 : 1;
                break;


            // --------------- Jammed Ball Safety ----------------
            case 90: // Safety everything off.
                cmdUpdate(0.0);
                IO.revolver_HAA = false;
                state++;
                break;
            case 91: // Wait momentarily before reversing.
                if (delayTimer.hasExpired(0.2, state)) state++;
                break;
            case 92: // Reverse momentarily.
                cmdUpdate(-revPct);
                if (delayTimer.hasExpired(0.3, state)) state++;
                break;
            case 94: // Stop momentarily
                cmdUpdate(0.0);
                if (delayTimer.hasExpired(0.2, state)) state++;
                break;
            case 96: // before trying to index again.
                jammedBall = false;
                state = 1;
                break;

            default: // everything off
                cmdUpdate(0.0);
                System.out.println("Invalid Revolver state - " + state);
                break;
        }

    }

    /**Update stuff */
    private static void stfUpdate() {
        if (IO.revolver_HAA)
            jammedBall = true; // Try to clear by reversing
        // isFull = (ballCnt >= 5) || !nextSpaceOpen.get();
    }

    /**Send revolver speed cmd to motor.  Check for motor hi alarm, jam, and stop motor. */
    private static void cmdUpdate(double revolverSpeed) {
        //Jammed ball will not break anything "critical" so handle in state machine.
        revolver.set(revolverSpeed);
        // revolver.set((!IO.revolver_HAA) ? revolverSpeed : 0); // HAA now, ballJammed in state
    }

    /**@return true if revolver is at index, has an open slot and isn't indexing. */
    public static boolean rdy2Rcv() { return atIndexStop.get() && nextSpaceOpen.get() /*&& !indexing*/; }

    /**@return true if revolver thinks it has 5 balls. */
    public static boolean isFull() { return ballCnt > 5; }

    /**@return state of the Revolver.  State assignments may chg, use statuses. */
    public static int getState() { return state; }

    /**@return Has unloaded all balls rotated for 3 secs? */
    public static boolean hasUnloaded() { return ballCnt < 1; }

    /**Has shot? */
    public static boolean hasShot() { return hasShot; }     //No i deer.

    /**Initialize sdb for revolver */
    private static void sdbInit() { }

    /**SmartDashboard Updates */ 
    private static void sdbUpdate() {
        SmartDashboard.putBoolean("Rev HAA", IO.revolver_HAA);
        SmartDashboard.putNumber("Revolver State", state);
        SmartDashboard.putBoolean("Rev BallInSnorf", nextSpaceOpen.get());
        SmartDashboard.putBoolean("atIndexStop", atIndexStop.get());
        SmartDashboard.putNumber("slow count", slowFireCnt);
    }

}
