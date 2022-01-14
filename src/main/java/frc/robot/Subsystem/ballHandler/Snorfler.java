package frc.robot.Subsystem.ballHandler;

import edu.wpi.first.wpilibj.motorcontrol.Victor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.*;
import frc.io.joysticks.JS_IO;
import frc.robot.Robot;
import frc.util.Timer;

public class Snorfler {
    private static ISolenoid snorfExtendV = IO.snorflerExt; // Extends both outter & inner arms
    private static Victor snorfFeederV = IO.snorfFeedMain; // Collector motor???
    private static Victor snorfLoaderV = IO.snorfFeedScdy; // Feed Revolver motor???
    private static InvertibleDigitalInput snorfHasBall = IO.snorfHasBall; // Banner snsr, ball at top of snorfler

    private static int state; // Snorfler state machine. 0=Off, 1-6 Snorfle, 10 Spit drvr, 90 Spit Ball jam
    private static boolean snorfArmDn;
    private static boolean snorfFeederOn;
    private static boolean snorfReverse;
    public static boolean reqsnorfDrvAuto;  //Request to enable the snorfler from Drv Auto system
    private static Timer stateTmr; // Timer used in state machine
    private static Timer safeTimer; // Safety timer, used for jammed ball hi amps
    private static Timer startUpTimer;
    private static boolean startUp;
    private static boolean ballJammed = false;          //Jammed ball, attempt to clear by reversing
    private static Timer ballJamTmr = new Timer(1.50);  //Timer if revolver current is hi flag ball jam

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
        snorfArmDn = false;     //Lower arm & extend feeder
        snorfFeederOn = false;  //Feed motors on to snorfle
        snorfReverse = false;   //Reverse motor while button down
        reqsnorfDrvAuto = false;//Request to enable the snorfler from Drv Auto system
    }

    private static void determ() {
        // toggle arms down and up
        if (JS_IO.btnTglSnorArmDn.onButtonPressed()) {
            if (!snorfArmDn) {
                state = Revolver.isFull() ? 90 : 1; // If Rev full, empty & shutdown
            } else {
                state = 0;
            }
            snorfArmDn = !snorfArmDn;
        }

        if (JS_IO.btnReverseSnorfler.onButtonPressed()) {
            state = snorfReverse ? 1 : 10;
            snorfReverse = !snorfReverse;
        }

        if(Robot.getMode() == 1)    //In robotPeriodic
            state = reqsnorfDrvAuto ? 2 : 0;

        if(ballJammed && state < 90) state = 90;

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
                if (stateTmr.hasExpired(0.03, state)) {
                    state++;
                }
                break;
            case 91:// Jammed ball, hi current or Shutdown. Reverse everything for 1.5 sec return to
                    // snorfling. (was 8)

                cmdUpdate(true, -feederSpeed, -.3);
                ballJammed = false;
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
     * @param snorfOuterSoleOut - Activates selenoid valve to lower outter &
     *                          extend inner snorfler arm.
     * @param snorfFeederSpd    - Motor speed for Feeder, collector wheels. ???
     * @param snorfLoaderSpd    - Motor speed for Loader, feed revolver. ???
     */
    public static void cmdUpdate(boolean snorfSoleOut, double snorfFeederSpd, double snorfLoaderSpd) {
        snorfExtendV.set(snorfSoleOut); // Appears we chgd to 1 output, need to chg parms to 1.
        snorfFeederV.set(snorfFeederSpd);
        snorfLoaderV.set(snorfLoaderSpd);
    }

    private static void stfUpdate(){
        // If ???? motor has hi hi amp draw, reverse motor for 1.5 sec to try to spit
        // out ball.
        if (ballJamTmr.hasExpired(1.0, IO.pdp.getCurrent(2) > 23)) {
            ballJammed = true;
        }


    }

    /** Update sdb for Snorfler */
    public static void sdbUpdate() {
        SmartDashboard.putNumber("Snorfler/1.state", state);
        SmartDashboard.putBoolean("Snorfler/2.Rev isFull", Revolver.isFull());
        SmartDashboard.putBoolean("Snorfler/3.ballBanner", snorfHasBall.get());
        SmartDashboard.putNumber("Snorfler/4.pdp snorf curr", IO.pdp.getCurrent(2));
        SmartDashboard.putBoolean("Snorfler/5.Arm is dn", snorfArmDn);
        SmartDashboard.putBoolean("Snorfler/6.Feed mtr on", snorfFeederOn);
        SmartDashboard.putBoolean("Snorfler/7.Rvlvr Rdy2Rcv", Revolver.rdy2Rcv());
        SmartDashboard.putBoolean("Snorfler/8.Req Snorf Auto", reqsnorfDrvAuto);
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
