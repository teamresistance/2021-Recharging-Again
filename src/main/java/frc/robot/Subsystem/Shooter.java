/*
Original Author: Jim & Anthony

History:
A - 1/20/20 - Original Release

Desc: Handles the shoooter subsystem
    0- everything off
    1- shooter up to speed, hood up
    default- everything off
    
    Buttons:

*/
package frc.robot.Subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.ISolenoid;
import frc.io.joysticks.JS_IO;

public class Shooter {
    private static TalonSRX shooter = IO.shooterTSRX;
    private static ISolenoid ballHood = IO.shooterHoodUp;

    private static int state;
    private static int setpointRPM = 4000; // tbd
    private static int atSpeedDeadband = 200; // tbd, in rpm
    private static double rpmToTpc = .07833333; // TBD rpm to ticks per cycle (100ms)
    private static boolean shooterToggle = false;
    // 47 ticks per 1 rotation

    private static double kF = 2.5;
    private static double kP = 100;
    private static double kI = 0;
    private static double kD = 0;

    public static void init() {
        SmartDashboard.putNumber("kP", kP);
        SmartDashboard.putNumber("kF", kF);
        SmartDashboard.putNumber("setpoint RPM", setpointRPM);
        ballHood.set(false);
        shooter.config_kF(0, kF);
        shooter.config_kP(0, kP);
        shooter.config_kI(0, kI);
        shooter.config_kD(0, kD);

        shooter.enableVoltageCompensation(true);
        shooter.configVoltageCompSaturation(12, 0);
        shooter.configVoltageMeasurementFilter(32, 0);
        shooter.setSelectedSensorPosition(0, 0, 0);

        cmdUpdate(0.0, false);
        state = 0;
        rpmToTpc = .07833333;
        shooterToggle = true;
    }

    private static void determ() {
        if (JS_IO.btnRampShooter.onButtonPressed()) {
            if (shooterToggle) {
                state = 1;
                shooterToggle = !shooterToggle;
            } else {
                state = 0;
                shooterToggle = !shooterToggle;
            }
        }

        if (JS_IO.allStop.onButtonPressed())
            state = 99;
    }

    public static void update() {
        sdbUpdate();
        determ();

        switch (state) {
            case 0: // off
                cmdUpdate(0, false);
                break;
            case 1: // on
                    // shooter.set(ControlMode.Velocity, 300);
                cmdUpdate(setpointRPM, true);

                break;
            default: // all off
                cmdUpdate(0, true);
                break;

        }
    }

    public static boolean isAtSpeed() { // if it's within it's setpoint deadband
        if (shooter.getSelectedSensorVelocity() * 600 / 47 >= (setpointRPM)
                && shooter.getSelectedSensorVelocity() * 600 / 47 <= (setpointRPM + atSpeedDeadband)) {
            return true;
        }
        return false;
    }

    public static void cmdUpdate(double spd, boolean cmdVel) { // control through velocity or percent
        if (cmdVel) { // Math.abs(spd) * rpmToTpc
            shooter.set(ControlMode.Velocity, Math.abs(spd) * rpmToTpc);
        } else {
            shooter.set(ControlMode.PercentOutput, Math.abs(spd));
        }
        if (shooter.getSelectedSensorVelocity() * 600 / 47 > 100) { // if not running, keep hood down
            ballHood.set(true);
        } else {
            ballHood.set(false);
        }

        SmartDashboard.putNumber("spd", spd);
        SmartDashboard.putNumber("vel input", rpmToTpc);
    }

    public static void sdbUpdate() {
        kF = SmartDashboard.getNumber("kF", kF);
        kP = SmartDashboard.getNumber("kP", kP);
        shooter.config_kF(0, kF);
        shooter.config_kP(0, kP);
        setpointRPM = (int) SmartDashboard.getNumber("setpoint RPM", 4000);
        SmartDashboard.putNumber("Shooter State", state);
        SmartDashboard.putNumber("FlyWheel Encoder", shooter.getSelectedSensorPosition());
        SmartDashboard.putNumber("FlyWheel Velocity", shooter.getSelectedSensorVelocity());
        SmartDashboard.putNumber("Flywheel RPM", shooter.getSelectedSensorVelocity() * 600 / 47);
        SmartDashboard.putBoolean("Shooter On", ((state == 1) ? true : false));
        SmartDashboard.putBoolean("isAtSpeed", isAtSpeed());
        SmartDashboard.putNumber("flywheel current", shooter.getStatorCurrent());
        SmartDashboard.putNumber("flywheel curr pdp", IO.pdp.getCurrent(13));
        SmartDashboard.putBoolean("shooterToggle", shooterToggle);
    }

    public static int getState() {
        return state;
    }

    public static boolean closeToSpeed() {
        if (shooter.getSelectedSensorVelocity() * 600 / 47 >= (setpointRPM - 400)) {
            return true;
        }
        return false;
    }
}
