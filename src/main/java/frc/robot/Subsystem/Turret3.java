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
    private static PIDController turPID = new PIDController(1.0, 0.0, 0.0); //!Used by LL X fdbk, Look at docs later
    private static double turCmdVal;    //Calc cmd signal for turret motor

    private static NetworkTableInstance netable;
    private static PhotonPipelineResult result;
    private static PhotonCamera camera;
    private static PhotonTrackedTarget foundTarget;


    public static void init() {
        turPID.setTolerance(0.05);
        state = 0;
        turret.set(0);
        ccwLmtSwAlm = false;
        cwLmtSwAlm = false;
        photonToggle = true;
        netable = NetworkTableInstance.getDefault();
        camera = new PhotonCamera(netable,"gloworm");
        camera.setPipelineIndex(1);
    }

    /**
     * Determine control.  Usually from a joystick button.
     */
    private static void determ() {
        if (JS_IO.btnLimeAim.onButtonPressed()) {
            state = state > 0 ? 0 : 1;
            System.out.println(state);
        }

        // if (JS_IO.btnLimeSearch.onButtonPressed()) {
        //     if (photonToggle) {
        //         state = 2;
        //     } else {
        //         if (turretPot.get() < -5) {
        //             state = 4;
        //         } else if (turretPot.get() > -5) {
        //             state = 5;
        //         }
        //     }
        //     photonToggle = !photonToggle;
        // }
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
                turCmdVal =  joyVal * 0.4 * Math.abs(joyVal);
                break;
            case 1: // Limeight Aim Control(
                if (isOnTarget() != null) { // null if not in frame of the camera
                    if (!isOnTarget()) { // false if the camera is not on the target 
                        turCmdVal = turPID.calculate( Math.min(2.0, -foundTarget.getYaw()));
                        // turCmdVal = (foundTarget.getYaw() > 0) ? 0.2 : -0.2;
                        // if(foundTarget.getYaw() ==0 )turCmdVal  = 0;
                    } else { // on target within deadband.
                        turCmdVal = 0.0;
                    }
                } else {
                    state = 2;
                }
                break;
            case 2: // search clock wise
                turCmdVal = 0.1;
                if (isOnTarget() != null) {
                    state = 1;
                } else if (turretPot.get() > 115) {
                    state = 3;
                }
                break;
            case 3: // search counter clock wise
                turCmdVal = -0.1;
                if (isOnTarget() != null) {
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
        SmartDashboard.putBoolean("Turret/Lime on target", isOnTarget() != null ? isOnTarget() : false );
        SmartDashboard.putBoolean("Turret/photonToggle", photonToggle);
        if (foundTarget != null) {
            SmartDashboard.putNumber("Turret/foundTargetX", foundTarget.getYaw());
            //SmartDashboard.putNumber("Turret/foundTargetY", foundTarget.getY());
        } else {
            SmartDashboard.putNumber("Turret/foundTargetX", -999);
            SmartDashboard.putNumber("Turret/foundTargetY", -999);
        }

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

    public static Boolean isOnTarget() {
        if (foundTarget != null) {
            return Math.abs(foundTarget.getYaw()) <= 1;
        } else {
            return null;
        }
    }
}