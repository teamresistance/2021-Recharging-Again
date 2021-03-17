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
    // private static int shootRPM = 0;
    // private static int setpointRPM = 5500; // (original RPM)
    private static Integer rpmWSP = 3000; // (original RPM)
    private static int rpmSPAdj = 3800;
    private static int atSpeedDeadband = 200; // tbd, in rpm
    private static double rpmToTpc = .07833333; // TBD rpm to ticks per cycle (100ms)
    private static boolean shooterToggle = true;
    // 47 ticks per 1 rotation

    private static double kF = 2.5;
    private static double kP = 100;
    private static double kI = 0;
    private static double kD = 0;

    private static SendableChooser<Integer> rpmChsr = new SendableChooser<Integer>();
    private static String[] rpmName  = {"RPM1", "RPM2", "RPM3", "RPM4", "RPM_Adj"};
    private static Integer[] rpmSP = {5500, 5000, 4500, 4000, -1};

    public static void init() {
        sdbInit();

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
        shooterToggle = true;

    }

    /**
     * Determine any state that needs to interupt the present state, usually by way
     * of a JS button but can be caused by other events.
     */
    private static void determ() {
        // if (JS_IO.btnRampShooter.onButtonPressed()) {
        //     state = shooterToggle ? 1 : 0;
        //     shooterToggle = !shooterToggle;
        // }

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
                cmdUpdate(rpmWSP, true);

                break;
            default: // all off
                cmdUpdate(0, false);
                break;

        }
    }

    public static boolean isAtSpeed() { // if it's within it's setpoint deadband
        if (shooter.getSelectedSensorVelocity() * 600 / 47 >= (rpmWSP) &&
            shooter.getSelectedSensorVelocity() * 600 / 47 <= (rpmWSP + atSpeedDeadband)) {
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

        SmartDashboard.putNumber("Shooter/cmdUpd/spd", spd);
        SmartDashboard.putNumber("Shooter/cmdUpd/vel input", rpmToTpc);
    }

    public static Double[] rpmSPd = new Double[rpmSP.length];       //Testing array sdb stuff
    public static void sdbInit() {
        SmartDashboard.putNumber("Shooter/RPM/kP", kP);
        SmartDashboard.putNumber("Shooter/RPM/kF", kF);

        rpmChsr = new SendableChooser<Integer>();
        rpmSPd[0] = (double)rpmSP[0];                       //Testing array sdb stuff
        rpmChsr.setDefaultOption(rpmName[0] + "-" + rpmSP[0], rpmSP[0]);
        for(int i=1; i < rpmSP.length; i++){
            rpmChsr.addOption(rpmName[i] + "-" + rpmSP[i], rpmSP[i]);
            rpmSPd[i] = (double)rpmSP[i];                       //Testing array sdb stuff
        }
        SmartDashboard.putData("Shooter/RPM/Selection", rpmChsr);
        SmartDashboard.putNumberArray("Shooter/RPM/ArTestNum", rpmSPd); //Testing array sdb stuff
        SmartDashboard.putNumber("Shooter/RPM/Adj SP", rpmSPAdj);
    }
 
    public static void sdbUpdate() {
        kF = SmartDashboard.getNumber("Shooter/RPM/kF", kF);
        kP = SmartDashboard.getNumber("Shooter/RPM/kP", kP);
        shooter.config_kF(0, kF);
        shooter.config_kP(0, kP);

        rpmWSP = rpmChsr.getSelected();
        if(rpmWSP == null || rpmWSP < 0) rpmWSP = rpmSPAdj;

        SmartDashboard.putNumber("Shooter/RPM/Wkg SP", rpmWSP);
        rpmSPAdj = (int) SmartDashboard.getNumber("Shooter/RPM/Adj SP", rpmSPAdj);

        SmartDashboard.putNumber("Shooter/State", state);
        SmartDashboard.putBoolean("Shooter/On", ((state == 1) ? true : false));
        SmartDashboard.putBoolean("Shooter/isAtSpeed", isAtSpeed());
        SmartDashboard.putBoolean("Shooter/shooterToggle", shooterToggle);

        SmartDashboard.putNumber("Shooter/FlyWheel/Encoder", encSh.ticks());
        SmartDashboard.putNumber("Shooter/FlyWheel/Velocity", shooter.getSelectedSensorVelocity());
        SmartDashboard.putNumber("Shooter/Flywheel/RPM", shooter.getSelectedSensorVelocity() * 600 / 47);
        SmartDashboard.putNumber("Shooter/Flywheel/SRX curr", shooter.getStatorCurrent());
        SmartDashboard.putNumber("Shooter/Flywheel/pdp curr", IO.pdp.getCurrent(13));

        // rpmSP = (int) SmartDashboard.getNumberArray("Shooter/ArTestNum", rpmSPd);
        // rpmWSP = (int)rpmSP[0];
        // SmartDashboard.putNumber("Shooter/rpmWSP", rpmWSP);
    }

    public static int getState() {
        return state;
    }

    public static boolean closeToSpeed() {
        if (shooter.getSelectedSensorVelocity() * 600 / 47 >= (rpmWSP - 400)) {
            return true;
        }
        return false;
    }
}
