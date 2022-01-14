package frc.robot.Subsystem;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.motorcontrol.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.InvertibleDigitalInput;
import frc.io.hdw_io.vision.LimeLight;
import frc.io.joysticks.JS_IO;
import frc.util.PropMath;

public class Turret {
    private static Victor turret = IO.turretRot;
    private static InvertibleDigitalInput leftMag = IO.turCCWLimitSw; // right rel to bot
    private static InvertibleDigitalInput rightMag = IO.turCWLimitSw;
    private static AnalogPotentiometer turretPot = IO.turretPosition;

    private static boolean atLimitLeft;
    private static boolean atLimitRight;
    private static boolean limeToggle;

    private static int state;
    //If input in range of {[0] to [1] then interpolate between [0] and [1]} etc.
    static double[][] span = { { -30.0, -2.0, -2.0, 2.0, 2.0, 30.0 },   //Input
            /*             */  { -0.45, -0.15, 0, 0, 0.15, 0.45 } };    //Output, interpolate
    static double[][] span2 = { { -30.0, -10.0, -2.0, -2.0, 2.0, 2.0, 10.0, 30.0 },
            /*              */  { -0.6, -0.3, -0.17, 0, 0, 0.17, 0.25, 0.6 } };

    public static void init() {
        state = 0;
        turret.set(0);
        atLimitLeft = false;
        atLimitRight = false;
        limeToggle = true;
    }

    private static void determ() {
        if (JS_IO.btnLimeAim.onButtonPressed()) {
            if (state == 1) {
                state = 0;
            } else {
                state = 1;
            }
        }

        if (JS_IO.btnLimeSearch.onButtonPressed()) {
            if (limeToggle) {
                state = 2;
                limeToggle = !limeToggle;
            } else {
                if (turretPot.get() < -5) {
                    state = 4;
                } else if (turretPot.get() > -5) {
                    state = 5;
                }

                limeToggle = !limeToggle;
            }
        }
    }

    public static void update() {
        sdbUpdate();
        determ();
        checkLim();
        cmdUpdate(0);
        switch (state) {
            case 0: // cojoyControl
                cmdUpdate(JS_IO.axTurretRot.get() * 0.3);
                break;
            case 1:// Lime Control\
                int limeNum = LimeLight.llOnTarget();
                if (limeNum != 999) {
                    cmdUpdate(PropMath.SegLine(LimeLight.getLLX(), span2));
                } else {
                    cmdUpdate(0);
                }
                break;
            case 2:
                cmdUpdate(0.25);
                if (LimeLight.llOnTarget() != 999) {
                    state = 1;
                } else if (turretPot.get() > 0) {
                    state = 3;
                }
                break;
            case 3:
                cmdUpdate(-.3);
                if (LimeLight.llOnTarget() != 999) {
                    state = 1;
                } else if (turretPot.get() < 0) {
                    state = 2;
                }
                break;
            case 4: // reset after lime search
                cmdUpdate(-.3);
                if (turretPot.get() < 0) {
                    if (turretPot.get() >= -10 && turretPot.get() <= 10) {
                        state = 0;
                    }
                }
            case 5: // reset after lime search
                cmdUpdate(-.3);
                if (turretPot.get() > 0) {
                    if (turretPot.get() >= -10 && turretPot.get() <= 10) {
                        state = 0;
                    }
                }
            default: // stop.
                cmdUpdate(0);
                break;
        }
    }

    public static void sdbUpdate() {
        SmartDashboard.putNumber("Turret State", state);
        SmartDashboard.putBoolean("atLeftLimit", atLimitLeft);
        SmartDashboard.putBoolean("atRightLimit", atLimitRight);
        SmartDashboard.putNumber("Potentiometer", turretPot.get());
        SmartDashboard.putNumber("turret spd", turret.get());
        SmartDashboard.putBoolean("Lime on target", isOnTarget());
        SmartDashboard.putBoolean("limeToggle", limeToggle);
    }

    private static void cmdUpdate(double val) {
        // if at limits do not run

        if (val < 0 && turretPot.get() < -120) {
            turret.set(0);
        } else if (val > 0 && turretPot.get() > 120) {
            turret.set(0);
        } else {
            if (val > 0 && atLimitRight) {
                turret.set(0);

            } else if (val < 0 && atLimitLeft) {
                turret.set(0);

            } else {
                turret.set(val);
            }
        }

    }

    public static int getState() {
        return state;
    }

    private static void checkLim() {
        if (turret.get() < -.1) { // if rotating away from limit, calling positive right
            atLimitLeft = false;
        }
        if (leftMag.get()) { // if at the limit
            atLimitLeft = true;
        }
        if (turret.get() > .1) { // if rotating away from limit
            atLimitRight = false;
        }
        if (rightMag.get()) {
            atLimitRight = true;
        }
    }

    public static boolean isOnTarget() {
        if (LimeLight.llOnTarget() == 0) {
            return true;
        }
        return false;
    }

}