package frc.robot.Subsystem;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.motorcontrol.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.InvertibleDigitalInput;
import frc.io.hdw_io.vision.LimeLight;
import frc.io.joysticks.JS_IO;
import frc.util.Timer;

public class Turret2 {
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
    private static boolean limeToggle;  //???

    private static int state;
    private static PIDController turPID = new PIDController(1.0, 0.0, 0.0); //Used by LL X fdbk
    private static double turCmdVal;    //Calc cmd signal for turret motor

    public static void init() {
        turPID.setTolerance(0.05);
        if(Math.abs(turretPot.get()) < 100.0){
            // ccwLmtSwCntr.reset();
            // cwLmtSwCntr.reset();
        }
        state = 0;
        turret.set(0);
        ccwLmtSwAlm = false;
        cwLmtSwAlm = false;
        limeToggle = true;
    }

    /**
     * Determine control.  Usually from a joystick button.
     */
    private static void determ() {
        if (JS_IO.btnLimeAim.onButtonPressed()) {
            state = state == 1 ? 0 : 1;
        }

        if (JS_IO.btnLimeSearch.onButtonPressed()) {
            if (limeToggle) {
                state = 2;
            } else {
                if (turretPot.get() < -5) {
                    state = 4;
                } else if (turretPot.get() > -5) {
                    state = 5;
                }
            }
            limeToggle = !limeToggle;
        }
    }

    /**
     * Update turret control.  Called from robot.
     * <p>0 - Joystick Control
     * <p>1 - Limeight Aim Control
     * <p>2 - Limeight Aim Control
     * <p>3 - Limeight Aim Control
     * <p>4 - Limeight Aim Control
     * <p>5 - Limeight Aim Control
     */
    public static void update() {
        sdbUpdate();
        determ();
        checkLim();
        cmdUpdate(0);
        switch (state) {
            case 0: // Joystick Control
                cmdUpdate(JS_IO.axTurretRot.get() * .2);
                break;
            case 1: // Limeight Aim Control
                int limeNum = LimeLight.llOnTarget();
                if (limeNum != 999) {
                    // cmdUpdate(PropMath.SegLine(LimeLight.getLLX(), span2));
                    turCmdVal = turPID.calculate(LimeLight.getLLX());
                } else {
                    turCmdVal = 0.0;
                }
                break;
            case 2: // 
                turCmdVal = 0.2;
                if (LimeLight.llOnTarget() != 999) {
                    state = 1;
                } else if (turretPot.get() > 0) {
                    state = 3;
                }
                break;
            case 3:
                turCmdVal = -0.2;
                if (LimeLight.llOnTarget() != 999) {
                    state = 1;
                } else if (turretPot.get() < 0) {
                    state = 2;
                }
                break;
            case 4: // reset after lime search
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
        if (val < 0 && (turretPot.get() < -120 || ccwLmtSwAlm)) {        //Check CCW limits
            val = 0;
        } else if (val > 0 && (turretPot.get() > 120 || cwLmtSwAlm)) {   //Check CW Limits
            val = 0;
        }
        val *= Math.abs(val);   //Square control value, carry sign
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
        SmartDashboard.putBoolean("Turret/limeToggle", limeToggle);
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
        if (LimeLight.llOnTarget() == 0) {
            return true;
        }
        return false;
    }

}