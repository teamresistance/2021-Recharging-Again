package frc.robot.Subsystem;

import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.*;
import frc.io.joysticks.JS_IO;
import frc.util.Timer;

public class Snorfler {
    private static ISolenoid snorfExtendV = IO.snorflerExt; // Extend both
    private static Victor snorfFeederV = IO.snorfFeedMain;
    private static Victor snorfLoaderV = IO.snorfFeedScdy;
    private static InvertibleDigitalInput snorfHasBall = IO.snorfHasBall;

    private static int state;
    private static boolean lowerToggleOn;
    private static boolean feedToggleOn;
    private static Timer timer;
    private static Timer safeTimer;
    private static Timer startUpTimer;
    private static boolean startUp;

    private static double feederSpeed = .7;
    private static double loaderSpeed = .7;

    public static void init() {

        timer = new Timer(0);
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

            startUp = startUpTimer.hasExpired(.2, state);
            if (!lowerToggleOn) {
                if (Revolver.isFull()) {
                    state = 6;
                } else {
                    state = 1;
                }
                lowerToggleOn = true;
            } else {
                state = 0;
                lowerToggleOn = false;
            }
        }

        // toggle feed motor on and off
        if (JS_IO.btnReverseSnorfler.onButtonPressed()) {

            if (feedToggleOn) {
                state = 8;
                feedToggleOn = false;
            } else {
                state = 1;
                feedToggleOn = true;
            }
        }

            if (IO.pdp.getCurrent(2) > 21) {
                state = 8;
            }

            if (IO.pdp.getCurrent(2) > 10 && safeTimer.hasExpired(.18, state) && startUp) {
                state = 8;
            }

    }

    public static void update() {
        sdbUpdate();
        determ();
        switch (state) { // cmd snorfOuterSoleOut snorfInnerSoleOut snorfFeederSpd snorfLoaderSpd
            case 0: // everything off including inner snorfler(?)
                cmdUpdate(false, false, 0, 0);
                break;
            case 1: // all solenoids out
                cmdUpdate(true, true, 0, 0);
                if (timer.hasExpired(0.2, state)) { // TODO: time to be changed
                    state = 2;
                }
                break;
            case 2: // feeder on, loader off, all solenoids out
                cmdUpdate(true, true, feederSpeed, loaderSpeed);
                if (snorfHasBall.get()) { // TODO: assuming ball is never lost once held
                    if (Revolver.isFull()) {
                        state = 5;
                    } else {
                        state = 3;
                    }
                }
                break;
            case 3:// has a ball in loader, waiting for free index
                cmdUpdate(true, true, feederSpeed, 0);
                if (Revolver.getState() == 1 || Revolver.getState() == 0) { // checks if the revolver has an empty slot
                    state = 4;
                }
                break;
            case 4: // snorfles the ball into the revolver for time period
                cmdUpdate(true, true, feederSpeed, loaderSpeed);

                if (timer.hasExpired(.25, state)) {
                    state = 7;
                }
                break;
            case 7:
                cmdUpdate(true, true, feederSpeed, 0);

                if (timer.hasExpired(.25, state)) {
                    state = 2;
                }
                break;
            case 5: // reverse everything, go back to waiting for ball
                cmdUpdate(true, true, -feederSpeed, -loaderSpeed);
                if (timer.hasExpired(1.5, state)) {
                    state = 6;
                }
                break;
            case 6:
                cmdUpdate(true, true, 0, 0);
                if (!Revolver.isFull()) {
                    state = 2;
                }
                break;
            case 8:
                cmdUpdate(true, true, -feederSpeed, -.3);
                if (timer.hasExpired(1.5, state)) {
                    state = 1;
                }
                break;
            default:
                cmdUpdate(false, false, 0, 0);
                break;
        }
    }

    public static void sdbUpdate() {
        SmartDashboard.putNumber("Snorfler State", state);
        SmartDashboard.putBoolean("isFull", Revolver.isFull());
        SmartDashboard.putBoolean("ballBanner", snorfHasBall.get());
        SmartDashboard.putNumber("pdp snorf curr", IO.pdp.getCurrent(2));
        SmartDashboard.putBoolean("lowerToggle", lowerToggleOn);
        SmartDashboard.putBoolean("feedToggle", feedToggleOn);

    }

    private static void cmdUpdate(boolean snorfOuterSoleOut, boolean snorfInnerSoleOut, double snorfFeederSpd,
            double snorfLoaderSpd) {
        snorfExtendV.set(snorfOuterSoleOut);

        snorfFeederV.set(snorfFeederSpd);
        snorfLoaderV.set(snorfLoaderSpd);
    }

    public static int getState() {
        return state;
    }

    public static boolean hasBall() {
        return snorfHasBall.get();
    }
}
