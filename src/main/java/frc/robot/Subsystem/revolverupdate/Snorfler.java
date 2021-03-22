package frc.robot.Subsystem.revolverupdate;

import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.*;
import frc.io.joysticks.JS_IO;
import frc.util.Timer;

public class Snorfler {
    private static ISolenoid snorfExtendV = IO.snorflerExt;     //Extends both outter & inner arms
    private static Victor snorfFeederV = IO.snorfFeedMain;      //Collector motor???
    private static Victor snorfLoaderV = IO.snorfFeedScdy;      //Feed Revolver motor???
    private static InvertibleDigitalInput snorfHasBall = IO.snorfHasBall;   //Banner snsr, ball at top of snorfler

    private static int state;
    private static boolean lowerToggleOn;
    private static boolean feedToggleOn;
    private static Timer stateTmr;          //Timer used in state machine
    private static Timer safeTimer;         //Safety timer, used for jammed ball hi amps
    private static Timer startUpTimer;
    private static boolean startUp;

    public static double feederSpeed = 0.7;
    public static double loaderSpeed = 0.7;
    public static boolean reqRevSnflr = false;  //TO DO: Need to set in cmdUpdate

    public static void init() {

        stateTmr = new Timer(0);
        safeTimer = new Timer(0);
        startUpTimer = new Timer(0);
        snorfFeederV.set(0);
        snorfLoaderV.set(0);
        snorfExtendV.set(false);

        state = 0;
        lowerToggleOn = false;
        feedToggleOn = false;
    }

    private static void determ() {
        // toggle arms down and up
        if (JS_IO.btnLowerSnorfler.onButtonPressed()) {

            startUp = startUpTimer.hasExpired(0.2, state);

            if(!lowerToggleOn){
                state = Revolver.isFull() ? 6 : 1;
                lowerToggleOn = true;
            }else{
                state = 0;
                lowerToggleOn = false;
            }
        }

        // toggle feed motor on and off
        if (JS_IO.btnReverseSnorfler.onButtonPressed()) {
            feedToggleOn = !feedToggleOn;
            state = feedToggleOn ? 8 : 1;
        }

        //If ???? motor has hi hi amp draw, reverse motor for 1.5 sec to try to spit out ball.
        if (IO.pdp.getCurrent(2) > 21) {
            state = 8;
        }

        //If ???? motor has hi amp draw, reverse motor for 1.5 sec to try to spit out ball.
        if (IO.pdp.getCurrent(2) > 10 && safeTimer.hasExpired(0.18, state) && startUp) {
            state = 8;
        }

    }

    /**Update Snorfler.  Called from teleopPeriodic in Robot.java */
    public static void update() {
        sdbUpdate();
        determ();
        switch (state) { // cmd snorfOuterSoleOut snorfInnerSoleOut snorfFeederSpd snorfLoaderSpd
            case 0: // everything off including inner snorfler(?)
                cmdUpdate(false, false, 0, 0);
                break;
            case 1: // Extend all solenoids out, wait for entending
                cmdUpdate(true, true, 0, 0);
                if (stateTmr.hasExpired(0.2, state)) state++;  // TODO: time to be changed
                break;
            case 2: // feeder on, loader off, all solenoids out, waiting for a ball in snorfler
                cmdUpdate(true, true, feederSpeed, loaderSpeed);
                if (snorfHasBall.get()) { // TODO: assuming ball is never lost once held
                    state = Revolver.isFull() ? 10 : state++;  //If full spit out else chk for rev is ready to rcv.
                }
                break;
            case 3:// has a ball in loader, waiting for free index
                cmdUpdate(true, true, feederSpeed, 0);
                if(Revolver.rdy2Rcv()) state++;   //Chk if revolver is ready to receive a ball
                break;
            case 4: // snorfles the ball into the revolver for time period
                cmdUpdate(true, true, feederSpeed, loaderSpeed);
                if (stateTmr.hasExpired(.25, state)) state++;
                break;
            case 5: //Turn off collector and just feed the ball to the revolver
                cmdUpdate(true, true, feederSpeed, 0);
                if (stateTmr.hasExpired(.25, state)) state = 2;
                break;
                
            case 10: // Spit.  Reverse everything for 1.5 sec. Then go back to waiting for ball (was 5)
                cmdUpdate(true, true, -feederSpeed, -loaderSpeed);
                if (stateTmr.hasExpired(1.5, state)) state++;
                break;
            case 11:
                cmdUpdate(true, true, 0, 0);
                if (!Revolver.isFull()) state = 2;
                break;

            case 8: // Jammed ball, hi current.  Reverse everything for 1.5 sec return to snorfling.
                cmdUpdate(true, true, -feederSpeed, -.3);
                if (stateTmr.hasExpired(1.5, state)) state = 1;
                break;

            default:
                cmdUpdate(false, false, 0, 0);
                System.out.println("Snorfler bad state - " + state);
                break;
        }
    }

    /**
     * Send cmds to various parts of snrfling system
     * @param snorfOuterSoleOut - Activates selenoid valve to raise snorfler outter arm.
     * @param snorfInnerSoleOut - Activates selenoid valve to extend snorfler inner arm.
     * @param snorfFeederSpd - Motor speed for Feeder, collector wheels.  ???
     * @param snorfLoaderSpd - Motor speed for Loader, feed revolver.  ???
     */
    public static void cmdUpdate(boolean snorfOuterSoleOut, boolean snorfInnerSoleOut,
                                 double snorfFeederSpd, double snorfLoaderSpd) {
        snorfExtendV.set(snorfOuterSoleOut);    //Appears we chgd to 1 output, need to chg parms to 1.
        snorfFeederV.set(snorfFeederSpd);
        snorfLoaderV.set(snorfLoaderSpd);
    }

    /**Update sdb for Snorfler */
    public static void sdbUpdate() {
        SmartDashboard.putNumber("Snorfler/state", state);
        SmartDashboard.putBoolean("Snorfler/Rev isFull", Revolver.isFull());
        SmartDashboard.putBoolean("Snorfler/ballBanner", snorfHasBall.get());
        SmartDashboard.putNumber("Snorfler/pdp snorf curr", IO.pdp.getCurrent(2));
        SmartDashboard.putBoolean("Snorfler/lowerToggle", lowerToggleOn);
        SmartDashboard.putBoolean("Snorfler/feedToggle", feedToggleOn);
    }

    /**
     * Probably shouldn't use this bc the states can change.  Use statuses.
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
}
