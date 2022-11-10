package frc.robot.Subsystem;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.motorcontrol.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.io.hdw_io.IO;
import frc.io.hdw_io.util.InvertibleDigitalInput;
import frc.io.joysticks.JS_IO;
import frc.util.PropMath;
import frc.util.Timer;

import frc.robot.Subsystem.ballHandler.Shooter;

import java.io.Console;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Turret3 {
    private static Victor turret = IO.turretRot; // Turret motor
    private static AnalogPotentiometer turretPot = IO.turretPosition; // Ctl Safety
    private static InvertibleDigitalInput ccwLmtSw = IO.turCCWLimitSw; // Safety CCW
    private static InvertibleDigitalInput cwLmtSw = IO.turCWLimitSw; // Safety CW
    // private static Counter ccwLmtSwCntr = IO.turCCWCntr; //Latches COS using
    // interrupts.
    // private static Counter cwLmtSwCntr = IO.turCWCntr; //Cleared by reset().

    private static boolean ccwLmtSwAlm; // CCW Limit Sw Exceeded, zero neg. cmd.
    private static boolean cwLmtSwAlm; // CW Limit Sw Exceeded, zero pos. cmd.
    private static Timer ccwRstTmr = new Timer(0.1); // When moving CW for this time reset ccwLmtSwCntr
    private static boolean photonToggle; // ???

    private static int state;
    private static double turCmdVal; // Calc cmd signal for turret motor

    private static NetworkTableInstance netable;
    private static PhotonPipelineResult result;
    private static PhotonCamera camera;
    public static PhotonTrackedTarget foundTarget;

    private static double homeX;
    private static double homeY;
    private static double coorX;
    private static double coorY;
    private static double turnDegree;
    private static double heading;

    public static void init() {
        state = 0;
        turret.set(0);
        ccwLmtSwAlm = false;
        cwLmtSwAlm = false;
        photonToggle = true;
        netable = NetworkTableInstance.getDefault();
        camera = new PhotonCamera(netable, "gloworm");
        camera.setPipelineIndex(1);
        homeX = IO.coorXY.getX(); //default to init position
        homeY = IO.coorXY.getY(); //default to init position
    }

    /**
     * Determine control. Usually from a joystick button.
     */
    private static void determ() {
        // if (JS_IO.btnLimeAim.onButtonPressed()) {
        // state = state > 0 ? 0 : 1;
        // System.out.println(state);
        // }

        if (JS_IO.btnLimeSearch.onButtonPressed()) {
            System.out.println("why iss" + state);
            if (photonToggle) {
                state = 2;
            } else {
                state = 0;
            }
            photonToggle = !photonToggle;
            Shooter.limeShoot = false;
        }
    }

    public static void update() {
        result = camera.getLatestResult();
        foundTarget = result.hasTargets() ? result.getBestTarget() : null;

        sdbUpdate();
        determ();
        checkLim();
        // cmdUpdate(0);

        switch (state) {
            case 0: // Joystick Control
                double joyVal = JS_IO.axTurretRot.get();
                turCmdVal = joyVal * 0.4 * Math.abs(joyVal);
                break;
            case 1: // Limeight Aim Control(
                if (TgtInFrame() == true) { // null if not in frame of the camera
                    if (!TgtLockedOn()) { // false if the camera is not on the target
                        turCmdVal = foundTarget.getYaw() / 50;
                        // turCmdVal = (foundTarget.getYaw() > 0) ? 0.2 : -0.2;
                        // if(foundTarget.getYaw() ==0 )turCmdVal = 0;
                    } else { // on target within deadband.
                        turCmdVal = 0.0;
                        Shooter.limeShoot = true;
                    }
                } else {
                    turCmdVal = 0.0;
                    // state = 2;
                    Shooter.limeShoot = false;
                }
                break;
            case 2: // search clock wise
                turCmdVal = 0.25;
                if (TgtInFrame()) {
                    state = 1;
                } else if (turretPot.get() > 115) {
                    state = 3;
                }
                break;
            case 3: // search counter clock wise
                turCmdVal = -0.25;
                if (TgtInFrame()) {
                    state = 1;
                } else if (turretPot.get() < -115) {
                    state = 2;
                }
                break;
            case 4: // reset after lime search //? no clue
                turCmdVal = 0.2;
                if (turretPot.get() < 0) {
                    if (turretPot.get() >= -10 && turretPot.get() <= 10) {
                        state = 0;
                    }
                }
            case 5: // reset after lime search
                turCmdVal = -0.2;
                if (turretPot.get() > 0) {
                    if (turretPot.get() >= -10 && turretPot.get() <= 10) {
                        state = 0;
                    }
                }
                case 6: //coordinate system turret alignment to coordinate (homeX, homeY)
                // calculates the angle in degrees between the positive x-axis and the ray from (coorX, coorY) to the point (0,0) using the inverse of tangent
                turnDegree = Math.toDegrees(Math.atan2(coorY - homeY, coorX - homeX)); //subtract home coordinates from present ones 
                
                turnDegree = -90 - turnDegree; //translates turnDegree into a value relative to turret position
                //normalizes to 0-180 degrees
                turnDegree = turnDegree % 360.0;  //Modulo 0 to 360
                if( turnDegree < -180.0 ){    //If LT -180 add 360 for complement angle
                    turnDegree += 360.0;
                }else if(turnDegree > 180){   //If GT +180 substract 360 for complement angle
                    turnDegree -= 360;
                }

                turnDegree -= heading; //subtracts robot heading

                if (turnDegree > -120 && turnDegree < 120){ // restrict input
                    turCmdVal = turnDegree > turretPot.get() ? 0.2 : -0.2; //check which way to turn
                    if (turretPot.get() > turnDegree - 3 && turretPot.get() < turnDegree + 3) { //10 degree margin
                        turCmdVal = 0.0;
                    }
                } else {
                    turCmdVal = 0.0;
                }
                break;
            default: // stop.
                turCmdVal = 0.0;
                break;
        }
        System.out.println(turCmdVal);
        cmdUpdate(turCmdVal);
    }

    /**
     * Issues commands for turret IF within +/-120 degree of forward.
     * Should NOT exceed 120 but second safety end switches should
     * stop travel if feedback is off.
     * 
     * @param val Turret command requested.
     */
    private static void cmdUpdate(double val) {
        // ? I think this may be locking up the turret.
        if (val < 0 && (turretPot.get() < -120 || ccwLmtSwAlm)) { // Check CCW limits
            val = 0;
        } else if (val > 0 && (turretPot.get() > 120 || cwLmtSwAlm)) { // Check CW Limits
            val = 0;
        }
        turret.set(val);
    }

    /** Update itemson the Smartdashboard. */
    public static void sdbUpdate() {
        SmartDashboard.putNumber("Turret/State", state);
        SmartDashboard.putBoolean("Turret/atLeftLimit", ccwLmtSwAlm);
        SmartDashboard.putBoolean("Turret/atRightLimit", cwLmtSwAlm);
        SmartDashboard.putNumber("Turret/Potentiometer", turretPot.get());
        SmartDashboard.putNumber("Turret/speed", turret.get());
        SmartDashboard.putBoolean("Turret/Larget in frame", TgtInFrame());
        SmartDashboard.putBoolean("Turret/Lime locked on", TgtLockedOn());
        SmartDashboard.putBoolean("Turret/photonToggle", photonToggle);
        SmartDashboard.putNumber("Turret/CMDVal", turCmdVal);
        if (foundTarget != null) {
            SmartDashboard.putNumber("Turret/foundTargetX", foundTarget.getYaw());
            SmartDashboard.putNumber("Turret/foundTargetY", foundTarget.getPitch());
            // SmartDashboard.putNumber("Turret/foundTargetY", foundTarget.getY());
        } else {
            SmartDashboard.putNumber("Turret/foundTargetX", 0);
            SmartDashboard.putNumber("Turret/foundTargetY", 0);
        }

    }

    public static int getState() {
        return state;
    }

    private static void checkLim() {
        if (turret.get() > 0) { // if rotating away from limit, calling positive right
            ccwLmtSwAlm = false;
        }
        if (ccwLmtSw.get()) { // if at the limit
            ccwLmtSwAlm = true;
        }
        if (turret.get() < 0) { // if rotating away from limit
            cwLmtSwAlm = false;
        }
        if (cwLmtSw.get()) {
            cwLmtSwAlm = true;
        }

        // ---------- New -----------
        if (ccwLmtSwAlm && turret.get() < -.1) {
            if (ccwRstTmr.hasExpired())
                ;
        }
    }

    // Target Ii/isnot in frame TgtInFrame
    // Target is in limit TgtLockedOn

    public static boolean TgtInFrame() {
        return foundTarget != null;
    }

    public static boolean TgtLockedOn() {
        if (!TgtInFrame()) {
            return false;
        } else {
            return Math.abs(foundTarget.getYaw()) <= 5;
        }
    }
}