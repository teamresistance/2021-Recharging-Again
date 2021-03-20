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
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.Encoder;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.ISolenoid;
import frc.io.joysticks.JS_IO;

public class Shooter {
    private static WPI_TalonSRX shooter = IO.shooterTSRX;
    private static Encoder encSh = IO.shooter_Encoder;
    private static ISolenoid ballHood = IO.shooterHoodUp;

    private static int state;
    private static int shootRPM = 0;
    private static int oldRPM = 5500; // (original RPM)
    private static int rpmOne = 0;
    private static int rpmTwo = 0;
    private static int rpmThree = 0;
    private static int rpmFour = 0;
    private static int atSpeedDeadband = 200; // tbd, in rpm
    private static double rpmToTpc = .07833333; // TBD rpm to ticks per cycle (100ms)
    // 47 ticks per 1 rotation

    private static double kF = 2.5;
    private static double kP = 100;
    private static double kI = 0;
    private static double kD = 0;

    private static SendableChooser<Integer> rpmChoose = new SendableChooser<Integer>();

    public static void init() {
        SmartDashboard.putNumber("kP", kP);
        SmartDashboard.putNumber("kF", kF);
        SmartDashboard.putNumber("Shooter/setpoint RPM", oldRPM);
        SmartDashboard.putNumber("Shooter/RPM 1", rpmOne);
        SmartDashboard.putNumber("Shooter/RPM 2", rpmTwo);
        SmartDashboard.putNumber("Shooter/RPM 3", rpmThree);
        SmartDashboard.putNumber("Shooter/RPM 4", rpmFour);
        ballHood.set(false);
        shooter.config_kF(0, kF);
        shooter.config_kP(0, kP);
        shooter.config_kI(0, kI);
        shooter.config_kD(0, kD);

        shooter.enableVoltageCompensation(true);
        shooter.configVoltageCompSaturation(12, 0);
        shooter.configVoltageMeasurementFilter(32, 0);
        encSh.reset();

        cmdUpdate(0.0, false);
        state = 0;
        rpmToTpc = .07833333;

        shootRPM = 0;
        rpmChoose = new SendableChooser<Integer>();
        rpmChoose.setDefaultOption("Original RPM (5500)", oldRPM);
        rpmChoose.addOption("RPM 1", rpmOne);
        rpmChoose.addOption("RPM 2", rpmTwo);
        rpmChoose.addOption("RPM 3", rpmThree);
        rpmChoose.addOption("RPM 4", rpmFour);
        SmartDashboard.putData("Shooter/RPM Selection", rpmChoose);
    }

    /**
     * Determine any state that needs to interupt the present state, usually by way
     * of a JS button but can be caused by other events.
     */
    private static void determ() {
        // 2nd option
        if (JS_IO.btnRampShooter.onButtonPressed()) {
            state = state != 1 ? 1 : 0;
        }

        if (JS_IO.allStop.onButtonPressed())
            state = 99;
    }

    public static void update() {
        sdbUpdate();
        determ();

        switch (state) {
            case 0: // off - percentoutput (so that no negative power is sent to the motor)
                cmdUpdate(0, false);
                break;
            case 1: // on
                shootRPM = rpmChoose.getSelected();
                cmdUpdate(shootRPM, true);

                break;
            default: // all off
                cmdUpdate(0, true);
                break;

        }
    }

    public static boolean isAtSpeed() { // if it's within it's setpoint deadband
        if (shooter.getSelectedSensorVelocity() * 600 / 47 >= (shootRPM)
                && shooter.getSelectedSensorVelocity() * 600 / 47 <= (shootRPM + atSpeedDeadband)) {
            return true;
        }
        return false;
    }

    public static void cmdUpdate(double spd, boolean cmdVel) { // control through velocity or percent
        shooter.set(ControlMode.Disabled, 0);
        if (cmdVel) { // Math.abs(spd) * rpmToTpc
            shooter.set(ControlMode.Velocity, Math.abs(spd) * rpmToTpc);
        } else {
            shooter.set(ControlMode.PercentOutput, Math.abs(spd));
        }

        if (shooter.getSelectedSensorVelocity() * 600 / 47 > 2000) { // if not running, keep hood down
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
        oldRPM = (int) SmartDashboard.getNumber("Shooter/setpoint RPM", 4000);
        rpmOne = (int) SmartDashboard.getNumber("Shooter/RPM 1", rpmOne);
        rpmTwo = (int) SmartDashboard.getNumber("Shooter/RPM 2", rpmTwo);
        rpmThree = (int) SmartDashboard.getNumber("Shooter/RPM 3", rpmThree);
        rpmFour = (int) SmartDashboard.getNumber("Shooter/RPM 4", rpmFour);
        rpmChoose.addOption("RPM 1", rpmOne);
        rpmChoose.addOption("RPM 2", rpmTwo);
        rpmChoose.addOption("RPM 3", rpmThree);
        rpmChoose.addOption("RPM 4", rpmFour);
        SmartDashboard.putNumber("Shooter State", state);
        SmartDashboard.putNumber("FlyWheel Encoder", encSh.ticks());
        SmartDashboard.putNumber("FlyWheel Velocity", shooter.getSelectedSensorVelocity());
        SmartDashboard.putNumber("Flywheel RPM", shooter.getSelectedSensorVelocity() * 600 / 47);
        SmartDashboard.putBoolean("Shooter On", ((state == 1) ? true : false));
        SmartDashboard.putBoolean("isAtSpeed", isAtSpeed());
        SmartDashboard.putNumber("flywheel current", shooter.getStatorCurrent());
        SmartDashboard.putNumber("flywheel curr pdp", IO.pdp.getCurrent(13));
    }

    public static int getState() {
        return state;
    }

    public static boolean closeToSpeed() {
        if (shooter.getSelectedSensorVelocity() * 600 / 47 >= (oldRPM - 400)) {
            return true;
        }
        return false;
    }

    public static void bangbang(double rpmInTpc) {
        shooter.set(ControlMode.Velocity, rpmInTpc);

        if (shooter.getSelectedSensorVelocity() < rpmInTpc) {
            shooter.set(ControlMode.PercentOutput, 100);
        } else {
            shooter.set(ControlMode.Velocity, rpmInTpc);
        }
    }
}
