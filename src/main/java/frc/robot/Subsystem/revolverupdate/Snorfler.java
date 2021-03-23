package frc.robot.Subsystem.revolverupdate;

import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.*;
import frc.io.joysticks.JS_IO;
import frc.util.Timer;

public class Snorfler {
    private static ISolenoid snorfExtendV = IO.snorflerExt; // Extends both outter & inner arms
    private static Victor snorfFeederV = IO.snorfFeedMain; // Collector motor???
    private static Victor snorfLoaderV = IO.snorfFeedScdy; // Feed Revolver motor???
    private static InvertibleDigitalInput snorfHasBall = IO.snorfHasBall; // Banner snsr, ball at top of snorfler

    private static int state; // Snorfler state machine. 0=Off, 1-6 Snorfle, 10 Spit drvr, 90 Spit Ball jam
    private static boolean lowerToggleOn;
    private static boolean feedToggleOn;
    private static boolean reverseSnrfToggle;
    private static Timer stateTmr; // Timer used in state machine
    private static Timer safeTimer; // Safety timer, used for jammed ball hi amps
    private static Timer startUpTimer;
    private static boolean startUp;

    public static double feederSpeed = 0.7;
    public static double loaderSpeed = 0.7;
    // public static boolean reqRevSnflr = false; // TO DO: Need to set in cmdUpdate

    public static void init() {

        stateTmr = new Timer(0); // Timer for state machine
        safeTimer = new Timer(0); // Timer for jammed ball safety
        startUpTimer = new Timer(0); // Timer to delay startup & shutdown of snorfler. Why???
        snorfFeederV.set(0);
        snorfLoaderV.set(0);
        snorfExtendV.set(false);

        state = 0;
        lowerToggleOn = false; // True = Disable snorfler. This seems backward???
        feedToggleOn = false; // True = Reverse direction of feed & loader motors. Spit balls.
        reverseSnrfToggle = false; // True = snorfler reverse until button hit again
    }

    private static void determ() {
        // toggle arms down and up
        if (JS_IO.btnLowerSnorfler.onButtonPressed()) {

            // startUp = startUpTimer.hasExpired(0.2, state); //??? startup(AND shutdown)
            // delay should be in SM?

            if (!lowerToggleOn) {
                state = Revolver.isFull() ? 90 : 1; // If Rev full, empty & shutdown
            } else {
                state = 0;
            }
            lowerToggleOn = !lowerToggleOn;
        }

        // if (JS_IO.btnReverseSnorfler.onButtonPressed()) { // Toggle feed motor
        // Reverse and Forward
        // feedToggleOn = !feedToggleOn;
        // state = feedToggleOn ? 9 : 1;
        // }

        if (JS_IO.btnReverseSnorfler.onButtonPressed()) {
            state = reverseSnrfToggle ? 1 : 10;
            reverseSnrfToggle = !reverseSnrfToggle;
        }

        // If ???? motor has hi hi amp draw, reverse motor for 1.5 sec to try to spit
        // out ball.
        if (IO.pdp.getCurrent(2) > 21) {
            state = 90;
        }

        // If ???? motor has hi amp draw, reverse motor for 1.5 sec to try to spit out
        // ball.
        // if (safeTimer.hasExpired(2, IO.pdp.getCurrent(2) > 15) /* && startUp */) {
        // state = 90;
        // }

    }

    /** Update Snorfler. Called from teleopPeriodic in Robot.java */
    public static void update() {
        sdbUpdate();
        determ();
        switch (state) { // cmd snorfOuterSoleOut snorfInnerSoleOut snorfFeederSpd snorfLoaderSpd
            case 0: // everything off including inner snorfler(?)
                cmdUpdate(false, 0, 0);
                stateTmr.hasExpired(0.05, state);
                break;
            // ------------ Snorfle, suck, balls -----------------------------
            case 1: // Extend all solenoids out, wait for entending
                cmdUpdate(true, 0, 0);
                if (stateTmr.hasExpired(0.2, state))
                    state++; // TODO: time to be changed
                break;
            case 2: // feeder on, loader on, all solenoids out, waiting for a ball in snorfler
                cmdUpdate(true, feederSpeed, loaderSpeed);
                if (snorfHasBall.get()) { // TODO: assuming ball is never lost once held
                    state = Revolver.isFull() ? 90 : state++; // If full spit out else chk for rev is ready to rcv.
                }
                break;
            case 3:// has a ball in loader, waiting for free index
                cmdUpdate(true, feederSpeed, 0);
                if (Revolver.rdy2Rcv())
                    state++; // Chk if revolver is ready to receive a ball
                break;
            case 4: // snorfles the ball into the revolver for time period
                cmdUpdate(true, feederSpeed, loaderSpeed);
                if (stateTmr.hasExpired(.25, state))
                    state++;
                break;
            case 5: // Turn off collector and just feed the ball to the revolver (was 7)
                cmdUpdate(true, feederSpeed, 0);
                if (stateTmr.hasExpired(.25, state))
                    state = 2;
                break;
            // -------------- Spit balls if rev full or driver request, return to Snorfling
            // ------------------------
            case 10: // Spit. Reverse everything for 1.5 sec. Then go back to waiting for ball (was
                // 5)
                //TODO:FIXXXX
                cmdUpdate(true, 0, 0);
                if (stateTmr.hasExpired(.02, state))
                    state++;
                break;
            case 11:
                cmdUpdate(true, -feederSpeed, -loaderSpeed);
                break;
            // case 11: // If Rev is not full return to snorfling else continue to spit
            // balls (was 6)

            // cmdUpdate(true, 0, 0);
            // if (!Revolver.isFull())
            // state = 2;
            // else {
            // lowerToggleOn = false;
            // state = 0;
            // }
            // break;
            // -------------------- Spit balls if jammed (hi amps) or toggle req. return to
            // waiting ----------------
            case 90:
                //TODO: FIXXXXXX
                cmdUpdate(true, 0, 0);
                if (stateTmr.hasExpired(0.01, state)) {
                    state++;
                }
                break;
            case 91:// Jammed ball, hi current or Shutdown. Reverse everything for 1.5 sec return to
                    // snorfling. (was 8)

                cmdUpdate(true, -feederSpeed, -.3);
                if (stateTmr.hasExpired(1.5, state))
                    state = 1;
                break;

            default:
                cmdUpdate(false, 0, 0);
                System.out.println("Snorfler bad state - " + state);
                break;
        }
    }

    /**
     * Send cmds to various parts of snrfling system
     * 
     * @param snorfOuterSoleOut - Activates selenoid valve to raise snorfler outter
     *                          arm.
     * @param snorfInnerSoleOut - Activates selenoid valve to extend snorfler inner
     *                          arm.
     * @param snorfFeederSpd    - Motor speed for Feeder, collector wheels. ???
     * @param snorfLoaderSpd    - Motor speed for Loader, feed revolver. ???
     */
    public static void cmdUpdate(boolean snorfSoleOut, double snorfFeederSpd, double snorfLoaderSpd) {
        snorfExtendV.set(snorfSoleOut); // Appears we chgd to 1 output, need to chg parms to 1.
        snorfFeederV.set(snorfFeederSpd);
        snorfLoaderV.set(snorfLoaderSpd);
    }

    /** Update sdb for Snorfler */
    public static void sdbUpdate() {
        SmartDashboard.putNumber("Snorfler/state", state);
        SmartDashboard.putBoolean("Snorfler/Rev isFull", Revolver.isFull());
        SmartDashboard.putBoolean("Snorfler/ballBanner", snorfHasBall.get());
        SmartDashboard.putNumber("Snorfler/pdp snorf curr", IO.pdp.getCurrent(2));
        SmartDashboard.putBoolean("Snorfler/lowerToggle", lowerToggleOn);
        SmartDashboard.putBoolean("Snorfler/feedToggle", feedToggleOn);
        SmartDashboard.putBoolean("Snorfler/Rev Rdy2Rcv", Revolver.rdy2Rcv());
    }

    /**
     * Probably shouldn't use this bc the states can change. Use statuses.
     * 
     * @return - present state of Shooter state machine.
     */
    public static int getState() {
        return state;
    }

    /**
     * @return - status of banner sensor at top of snofler, has a ball.
     */
    public static boolean hasBall() {
        return snorfHasBall.get();
    }

    /**
     * Returns if the snorfler is currently in an unready state. Change state number
     * if state changes.
     * 
     * @return - returns if the snorfler is in the cmdUpdate(true, 0, 0) state.
     */
    public static boolean snorfNotRdy() {
        return snorfExtendV.get();
    }
}
