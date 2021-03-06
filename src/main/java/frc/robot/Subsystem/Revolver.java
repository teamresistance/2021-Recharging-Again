/*
Desc.  The revolver collects balls from the snorfler.  It holds up to 5
until requested to deliever them to the shooter.

3/1/2020 - Anthony - Original release
3/11 - JCH - updated hdw_io and cleanup
*/
package frc.robot.Subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.InvertibleDigitalInput;
import frc.io.hdw_io.InvertibleSolenoid;
import frc.io.joysticks.JS_IO;
import frc.util.Timer;


// CHANGES: ONE BANNER SENSOR UNDER(?) THE BALL
//          BANNER SENSOR ON FEED NOT ON SUCK 


public class Revolver {
    private static Victor revolver = IO.revolverRot;
    private static InvertibleDigitalInput atOneRevolution = IO.revolerIndexer; // mag
    private static InvertibleDigitalInput ballInSnorfler = IO.snorfHasBall;
    private static InvertibleDigitalInput nextSpaceOpen = IO.revolNextSpaceOpen;

    public static int state = 0;
    private static boolean locked = false;
    private static int ballCnt = 0; // Increment when a ball is loaded. Clear on unload.
    private static boolean isFull = false;
    private static boolean jammedBall = false; // Jammed ball, attempt to clear by reversing
    private static boolean hasUnloaded;
    private static boolean hasShot;
    private static int slowFireCnt = 0;

    private static double loadPct = 0.15; // Spd when loading
    private static double unloadPct = 0.25; // Spd when unloading

    private static Timer delayTimer = new Timer(0.5);


    public static void init() { // initialze
        hasUnloaded = false;
        hasShot = false;
        state = 3;
        isFull = false;
        revolver.set(0.0);
    }

    public static void determ() { // determinator of state
        if (!locked) {
            if (JS_IO.btnFireShooter.isDown())
                state = 11; // Unload
        }

        if (!locked) {
            if (JS_IO.btnSlowFire.isDown()) {
                state = 14;
                slowFireCnt = 0;
            }
        }

        if (JS_IO.btnStop.isDown())
            state = 0;
        if (JS_IO.btnIndex.isDown()) {
            ballCnt = 0;
            state = 30;
        }

        if (Snorfler.hasBall() && state != 1 && !isFull) {
            state = 1; // Load
        }

        if (IO.revolver_HAA && state < 90) { // jammed Ball
            state = 90; // Clear jammed ball
        }
    }

    public static void update() { // cases for state of revolver
        determ();
        sdbUpdate();

        switch (state) {
            case 0: // everything off, revolverSpeed. Waiting for Load or Unload.
                cmdUpdate(0.0);
                break;
            // ------------ Loading --------------------
            case 1: // waiting for intake, snorfling
                cmdUpdate(0.0);
                if (delayTimer.hasExpired(0.5, state))
                    state++; // Settling time
                break;
            case 2: // indexing and rotating
                cmdUpdate(loadPct);
                state++;
                break;
            case 3: // Wait for index sw to clear then start checking. When seen again stop.
                cmdUpdate(loadPct);
                if (delayTimer.hasExpired(0.25, state) && atOneRevolution.get()) {
                    cmdUpdate(0.0);
                    state = 19;
                }
                ;
                break;
            case 19:
                cmdUpdate(0);
                if (delayTimer.hasExpired(0.1, state) && !atOneRevolution.get()) {
                    state = 4;
                }
                ;
                break;
            case 4: // Done goto state 0
                cmdUpdate(0);
                if (delayTimer.hasExpired(0.25, state)) {
                    if (nextSpaceOpen.get()) {
                        state = 0;
                    } else {
                        state = 5;
                    }
                }
                break;
            case 5: // check if the next slot is empty
                cmdUpdate(loadPct);
                if (delayTimer.hasExpired(0.25, state) && atOneRevolution.get()) {
                    state = 6;
                }
                break;
            case 6:
                cmdUpdate(0);
                if (delayTimer.hasExpired(.1, state)) {
                    if (!nextSpaceOpen.get()) {
                        isFull = true;
                    }
                    state = 0;

                }

                break;

            // ------------ Unloading -------------
            case 11: // check if can unload
                cmdUpdate(0.0);
                // Turret.isOnTarget() &&
                if (Shooter.isAtSpeed() && Injector.isRunning()) { // if at speed and on target
                    state++;
                    isFull = false;
                }
                break;
            case 12: // start unloading (revolver)
                cmdUpdate(unloadPct);
                if (delayTimer.hasExpired(1.5, state)) { // waiting for all balls
                    hasUnloaded = true;
                    state++;
                }
                break;
            case 13: // move into loading position
                cmdUpdate(loadPct);
                if (atOneRevolution.get()) {
                    hasUnloaded = false;
                    state = 0;
                }
                break;

            // ------------ Slow Unloading --------------
            // case 14:
            // cmdUpdate(0.0);
            // // Turret.isOnTarget() &&
            // if (Shooter.isAtSpeed() && Injector.isRunning()) { // if at speed and on
            // target
            // state++;
            // isFull = false;
            // }
            // break;
            // case 15:
            // cmdUpdate(.6 * unloadPct);
            // if (delayTimer.hasExpired(3.5, state)) { // waiting for all balls
            // hasUnloaded = true;
            // state++;
            // }
            // break;
            // case 16:
            // cmdUpdate(loadPct);
            // if (atOneRevolution.get()) {
            // hasUnloaded = false;
            // state = 0;
            // }
            // break;

            // slow fire 2
            case 14:
                cmdUpdate(0.0);
                // Turret.isOnTarget() &&
                if (Shooter.isAtSpeed() && Injector.isRunning()) { // if at speed and on target
                    isFull = false;
                    state++;
                }
                break;
            case 15:
                cmdUpdate(0.0);
                if (slowFireCnt == 5) {
                    hasUnloaded = true;
                    state = 18;
                } else {
                    state = 16;
                }
                break;
            case 16:
                cmdUpdate(unloadPct);
                slowFireCnt++;
                state++;
                break;
            case 17:
                cmdUpdate(unloadPct);
                if (delayTimer.hasExpired(0.25, state) && atOneRevolution.get()) {
                    cmdUpdate(0.0);
                    state = 15;
                }
                break;
            case 18:
                cmdUpdate(loadPct);
                if (atOneRevolution.get()) {
                    hasUnloaded = false;
                    state = 0;
                }
                break;

            // ------------ Single Fire -------------------
            case 25:
                if (Shooter.isAtSpeed() && Injector.isRunning()) {
                    state++;
                } // end at state 4??
                break;
            case 26:
                cmdUpdate(unloadPct);
                if (delayTimer.hasExpired(1, state) && atOneRevolution.get()) {
                    hasShot = true;
                    state++;
                }
            case 27:
                cmdUpdate(loadPct);
                if (atOneRevolution.get()) {
                    hasShot = false;

                    if (isFull()) {
                        isFull = false;
                        state = 30;
                    } else {
                        state = 40;
                    }
                }
                break;
            case 40:
                cmdUpdate(0.0);
                if (nextSpaceOpen.get()) {
                    state = 41;
                }
                break;
            case 41:
                cmdUpdate(loadPct);
                if (nextSpaceOpen.get() && atOneRevolution.get()) {
                    state = 40;
                } else {
                    state = 0;
                }
                break;
            // ------------ Reindex for missed slot -------------------
            case 30: // reindex, Check for open slot
                cmdUpdate(0.0);
                if (nextSpaceOpen.get()) { // if there is NO ball in the next space
                    isFull = false;
                    state = 0;
                } else {
                    if (ballCnt == 5) {
                        isFull = true;
                        state = 0;
                    } else {
                        state = 31;
                    }
                }
                break;
            case 31: //
                cmdUpdate(loadPct);
                ballCnt++;
                state = 32;
                break;
            case 32: // Wait for index sw to clear then start checking. When seen again stop.
                cmdUpdate(loadPct);
                if (delayTimer.hasExpired(0.25, state) && atOneRevolution.get()) {
                    cmdUpdate(0.0);
                    state = 33;
                }
                break;
            case 33:
                cmdUpdate(0);
                if (delayTimer.hasExpired(0.1, state)) {
                    cmdUpdate(0.0);
                    state = 30;
                }
                break;
            // --------------- Jammed Ball Safety ----------------
            case 90: // Safety everything off. Wait momentarily before reversing.
                cmdUpdate(0.0);
                if (delayTimer.hasExpired(0.2, state)) {
                    IO.revolver_HAA = false;
                    state++;
                }
                break;
            case 91: // Reverse momentarily before trying again.

                cmdUpdate(-loadPct);
                if (delayTimer.hasExpired(0.5, state))
                    state++;
                break;
            case 92: // Stop momentarily before trying again.
                cmdUpdate(0.0);
                if (delayTimer.hasExpired(0.2, state)) {
                    jammedBall = false;
                    state = 3;
                }
                break;

            default: // everything off
                cmdUpdate(0.0);
                System.out.println("Invalid Revolver state - " + state);
                break;
        }

    }

    // Stuff Updates
    private static void stfUpdate() {
        if (IO.revolver_HAA)
            jammedBall = true; // Try to clear by reversing
        // isFull = (ballCnt >= 5) || !nextSpaceOpen.get();
    }

    // turning off and on certain functions
    private static void cmdUpdate(double revolverSpeed) {
        revolver.set((!IO.revolver_HAA) ? revolverSpeed : 0); // HAA now, ballJammed in state
    }

    public static boolean isFull() {
        return isFull;
    }

    // return the state of the Revolver
    public static int getState() {
        return state;
    }

    public static boolean hasUnloaded() {
        return hasUnloaded;
    }

    public static boolean hasShot() {
        return hasShot;
    }

    // SmartDashboard Updates
    private static void sdbInit() {

    }

    // SmartDashboard Updates
    private static void sdbUpdate() {
        SmartDashboard.putBoolean("Rev HAA", IO.revolver_HAA);
        SmartDashboard.putNumber("Revolver State", state);
        SmartDashboard.putBoolean("Rev BallInSnorf", nextSpaceOpen.get());
        SmartDashboard.putBoolean("atOneRev", atOneRevolution.get());
        SmartDashboard.putNumber("slow count", slowFireCnt);
    }

}
