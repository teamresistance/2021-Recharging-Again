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

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Turret3 {
    private static Victor turret = IO.turretRot;    //Turret motor
    private static AnalogPotentiometer turretPot = IO.turretPosition;   //Ctl Safety
    private static InvertibleDigitalInput ccwLmtSw = IO.turCCWLimitSw;   //Safety CCW
    private static InvertibleDigitalInput cwLmtSw = IO.turCWLimitSw;   //Safety CW
    // private static Counter ccwLmtSwCntr = IO.turCCWCntr;    //Latches COS using interrupts.
    // private static Counter cwLmtSwCntr = IO.turCWCntr;      //Cleared by reset().

    private static boolean ccwLmtSwAlm;  //CCW Limit Sw Exceeded, zero neg. cmd.
    private static boolean cwLmtSwAlm;   //CW Limit Sw Exceeded, zero pos. cmd.
    private static Timer ccwRstTmr = new Timer(0.1);    //When moving CW for this time reset ccwLmtSwCntr
    private static Timer cwRstTmr = new Timer(0.1);    //When moving CW for this time reset ccwLmtSwCntr
    private static boolean photonToggle;  //???

    private static int state;
    private static PIDController turPID = new PIDController(1.0, 0.0, 0.0); //Used by LL X fdbk
    private static double turCmdVal;    //Calc cmd signal for turret motor

    private static NetworkTableInstance netable;
    private static PhotonPipelineResult result;
    private static PhotonCamera camera;
    private static Transform2d targetOffset;


    public static void init() {
        turPID.setTolerance(0.05);
        state = 0;
        turret.set(0);
        ccwLmtSwAlm = false;
        cwLmtSwAlm = false;
        photonToggle = true;
    }

    /**
     * Determine control.  Usually from a joystick button.
     */
    private static void determ() {
        if (JS_IO.btnLimeAim.onButtonPressed()) {
            state = state == 1 ? 0 : 1;
        }

        if (JS_IO.btnLimeSearch.onButtonPressed()) {
            if (photonToggle) {
                state = 2;
            } else {
                if (turretPot.get() < -5) {
                    state = 4;
                } else if (turretPot.get() > -5) {
                    state = 5;
                }
            }
            photonToggle = !photonToggle;
        }
    }

    public static void update() {
        sdbUpdate();
        determ();
        checkLim();
        // cmdUpdate(0);
        result = camera.getLatestResult();
        targetOffset = result.getBestTarget().getCameraToTarget();

        switch (state) {
            case 0: // Joystick Control
                cmdUpdate(JS_IO.axTurretRot.get() * .4 * Math.abs(JS_IO.axTurretRot.get()));
                break;
            case 1: // Limeight Aim Control
                if (isOnTarget()) {
                    // cmdUpdate(PropMath.SegLine(LimeLight.getLLX(), span2));
                    turCmdVal = turPID.calculate(targetOffset.getX());
                } else {
                    turCmdVal = 0.0;
                }
                break;
            case 2: // 
                turCmdVal = 0.1;
                if (!isOnTarget()) {
                    state = 1;
                } else if (turretPot.get() > 0) {
                    state = 3;
                }
                break;
            case 3:
                turCmdVal = -0.1;
                if (!isOnTarget()) {
                    state = 1;
                } else if (turretPot.get() < 0) {
                    state = 2;
                }
                break;
            case 4: // reset after lime search //? no clue
                turCmdVal = -0.2;
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
            default: // stop.
                turCmdVal = 0.0;
                break;
        }
        cmdUpdate(turCmdVal);
    }

    /**Issues commands for turret IF within +/-120 degree of forward.
     * Should NOT exceed 120 but second safety end switches should
     * stop travel if feedback is off.
     * 
     * @param val Turret command requested.
     */
    private static void cmdUpdate(double val) {
        //? I think this may be locking up the turret. 
        if (val < 0 && (turretPot.get() < -120 || ccwLmtSwAlm)) {        //Check CCW limits
            val = 0;
        } else if (val > 0 && (turretPot.get() > 120 || cwLmtSwAlm)) {   //Check CW Limits
            val = 0;
        }
        turret.set(val);
    }

    /**Update itemson the Smartdashboard. */
    public static void sdbUpdate() {
        SmartDashboard.putNumber("Turret/State", state);
        SmartDashboard.putBoolean("Turret/atLeftLimit", ccwLmtSwAlm);
        SmartDashboard.putBoolean("Turret/atRightLimit", cwLmtSwAlm);
        SmartDashboard.putNumber("Turret/Potentiometer", turretPot.get());
        SmartDashboard.putNumber("Turret/speed", turret.get());
        SmartDashboard.putBoolean("Turret/Lime on target", isOnTarget());
        SmartDashboard.putBoolean("Turret/photonToggle", photonToggle);
    }

    public static int getState() {
        return state;
    }

    private static void checkLim() {
        if (turret.get() < -.1) { // if rotating away from limit, calling positive right
            ccwLmtSwAlm = false;
        }
        if (ccwLmtSw.get()) { // if at the limit
            ccwLmtSwAlm = true;
        }
        if (turret.get() > .1) { // if rotating away from limit
            cwLmtSwAlm = false;
        }
        if (cwLmtSw.get()) {
            cwLmtSwAlm = true;
        }

        //---------- New -----------
        if(ccwLmtSwAlm && turret.get() < -.1){
            if(ccwRstTmr.hasExpired());
        }
    }

    public static boolean isOnTarget() {
        return Math.abs(result.getBestTarget().getCameraToTarget().getTranslation().getX()) <= 1; //TODO: change this to be more accurate
        // if (LimeLight.llOnTarget() == 0) {
        //     return true;
        // }
        // return false;
    }
}
//     private static Victor turret = IO.turretRot;
//     private static InvertibleDigitalInput leftMag = IO.turCCWLimitSw; // right rel to bot
//     private static InvertibleDigitalInput rightMag = IO.turCWLimitSw;
//     private static AnalogPotentiometer turretPot = IO.turretPosition;

//     private static boolean atLimitLeft;
//     private static boolean atLimitRight;
//     private static boolean ccwLmtSwAlm;  //CCW Limit Sw Exceeded, zero neg. cmd.
//     private static boolean cwLmtSwAlm;   //CW Limit Sw Exceeded, zero pos. cmd.
//     private static Timer ccwRstTmr = new Timer(0.1);    //When moving CW for this time reset ccwLmtSwCntr
//     private static Timer cwRstTmr = new Time(0.1);    //When moving CW for this time reset ccwLmtSwCntr
//     private static NetworkTableInstance netable;
//     private static PhotonPipelineResult result;
//     private static PhotonCamera camera;


//     public static void init() {
//         atLimitLeft = false;
//         atLimitRight = false;
//         turret.set(0);
//         netable = NetworkTableInstance.getDefault();
//         camera = new PhotonCamera(netable,"gloworm");
//         camera.setPipelineIndex(1);

//     }

//     public static void update() {
//         result = camera.getLatestResult();
//         Transform2d pose = result.getBestTarget().getCameraToTarget();
//         SmartDashboard.putNumber("X", pose.getTranslation().getX());
//         // System.out.println("Target PX: " + netable.getEntry("gloworm/targetPixelsX").getDouble(0)); // must use instance of netable to get entry
//         // System.out.println("Target PY: " + netable.getEntry("gloworm/targetPixelsY").getDouble(0));
        
//     }

//     private static void cmdUpdate(double val) {
//         if (val < 0 && (turretPot.get() < -120 || ccwLmtSwAlm)) {        //Check CCW limits
//             val = 0;
//         } else if (val > 0 && (turretPot.get() > 120 || cwLmtSwAlm)) {   //Check CW Limits
//             val = 0;
//         }
//         val *= Math.abs(val);   //Square control value, carry sign
//         turret.set(val);
//     }
    
//     private static void checkLim() {
//         if (turret.get() < -.1) { // if rotating away from limit, calling positive right
//             ccwLmtSwAlm = false;
//         }
//         if (ccwLmtSw.get()) { // if at the limit
//             ccwLmtSwAlm = true;
//         }
//         if (turret.get() > .1) { // if rotating away from limit
//             cwLmtSwAlm = false;
//         }
//         if (cwLmtSw.get()) {
//             cwLmtSwAlm = true;
//         }

//         //---------- New -----------
//         if(ccwLmtSwAlm && turret.get() < -.1){
//             if(ccwRstTmr.hasExpired());
//         }
//     }

//     public static void sdbUpdate() {
//         SmartDashboard.putNumber("Turret/State", state);
//         SmartDashboard.putBoolean("Turret/atLeftLimit", ccwLmtSwAlm);
//         SmartDashboard.putBoolean("Turret/atRightLimit", cwLmtSwAlm);
//         SmartDashboard.putNumber("Turret/Potentiometer", turretPot.get());
//         SmartDashboard.putNumber("Turret/speed", turret.get());
//         // SmartDashboard.putBoolean("Turret/Lime on target", isOnTarget());
//         // SmartDashboard.putBoolean("Turret/photonToggle", photonToggle);
//     }

// }