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
package frc.robot.Subsystem.revolverupdate;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.Encoder;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.ISolenoid;
import frc.io.joysticks.JS_IO;
import frc.util.Timer;

/**
 * Original Author: Jim & Anthony
 * <p>
 * <p> History:
 * <p>  1/20/20 - Original Release
 * <p>  2/2021 - KB - Added Chooser for RPM SPs
 * <p>
 * <p> Desc: Handles the shoooter subsystem
 * <p> Note: Works with Injector and Revolver.  The Injector controls a flipper, pickup wheel & 4-wheel injector.
 * The Revolver holds the balls until ready to shoot then can deliever them one at a time thru the Injector to the Shooter.
 * So, the Shooter comes up to speed then requests the Injector then request the Revolver.  
 * The Revolver also works with the Snofler to gather and store the balls.
 * <p> 
 * <p> determ: When button rampUp(3) if state == 0 goto state 1 else goto state 0, shut it all down, initialize.
 * <p> 
 * <p> Seq of Operation - Shooter
 * <p> Init - System is initialized off.  All requests off and retracted.
 * <p> 0.  Initialized.  Shooter disabled, flywheel off, injector Disabled (4-wheel off, pickup off, flipper down),
 *  hood down. No request to revolver, false.
 * <p> 1.  Starts the flywheel ramping to rpmSP (or power).  At setpt goto state 2.  Read (1).onPressed to clear.
 * <p> 2.  When button shoot(1).onPressed sets shtrRdy, go to state 3
 * <p> 3.  Requests injector enable (4-whl, pickup & flipper enabled).  Wait for a period to get started then state 4
 * <p> 4.  When button shoot(1).onPressed 2nd time goto state 5
 * <p> 5.  Set revolver.start, index revolver 1 time.  Goto state 6
 * <p> 6.  When revolver.finished wait some time goto state 7
 * <p> 7.  If shoot(1).isDown, stilled pressed, goto state 6, continue shooting, else goto 4, single shot
 */

public class NewShooter {
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

    private static Timer trgrTimer = new Timer(0.5); // timer used for the trigger


    private static SendableChooser<Integer> rpmChoose = new SendableChooser<Integer>();

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

        cmdUpdate(0.0, false, false, false);
        state = 0;
        rpmToTpc = .07833333;
    }

    /**
     * Determine any state that needs to interupt the present state, usually by way
     * of a JS button but can be caused by other events.
     */
    private static void determ() {
        // 2nd option
        if (JS_IO.btnRampShooter.onButtonPressed()) {
            state = state != 0 ? 0 : 1;
        }

        if (JS_IO.btnFireShooter.onButtonPressed() && trgrTimer.hasExpired(.1, state)) {
            state = 2;
        }

        if (JS_IO.btnFireShooter.isDown() && trgrTimer.hasExpired(1, state)) {
            
        }

        if (JS_IO.allStop.onButtonPressed())
            state = 99;
    }

    public static void update() {
        sdbUpdate();
        determ();

        switch (state) {
            case 0: // off - percentoutput (so that no negative power is sent to the motor)
                cmdUpdate(0, false, false, false);
                break;
            case 1: // on
                shootRPM = rpmChoose.getSelected();
                cmdUpdate(shootRPM, true, false, false);
                break;
            //----------- Single Shot ------------
            case 2:

                break;
            case 5:
                break;
            default: // all off
                cmdUpdate(0, true,false, false);
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


    /**
     * Issue spd setting as rpmSP if isVelCmd true else as percent cmd.
     * @param spd - cmd to issue to Flywheel Talon motor controller as rpm or percentage
     * @param isVelCmd - spd should be issued as rpm setpoint else as a percenetage output.
     */
    public static void cmdUpdate(double spd, boolean isVelCmd, boolean injCmd, boolean revCmd) { // control through velocity or percent
        //NewInjector.reqInjShtr = injCmd;               //Injector Start request
        NewRevolver.reqRevShtr = revCmd;               //Revolver Start request

        if (isVelCmd) { // Math.abs(spd) * rpmToTpc
            shooter.set(ControlMode.Velocity, Math.abs(spd) * rpmToTpc);    // control as velocity (RPM)
        } else {
            shooter.set(ControlMode.PercentOutput, Math.abs(spd));          // control as percentage output
        }

        if (shooter.getSelectedSensorVelocity() * 600 / 47 > 2000) { // if not running, keep hood down
            ballHood.set(true);
        } else {
            ballHood.set(false);
        }

        SmartDashboard.putNumber("Shooter/cmdUpd/spd", spd);
        SmartDashboard.putNumber("Shooter/cmdUpd/vel input", rpmToTpc);
    }

    public static void sdbInit() {
        SmartDashboard.putNumber("Shooter/kP", kP);
        SmartDashboard.putNumber("Shooter/kF", kF);
        SmartDashboard.putNumber("Shooter/setpoint RPM", oldRPM);
        SmartDashboard.putNumber("Shooter/RPM 1", rpmOne);
        SmartDashboard.putNumber("Shooter/RPM 2", rpmTwo);
        SmartDashboard.putNumber("Shooter/RPM 3", rpmThree);
        SmartDashboard.putNumber("Shooter/RPM 4", rpmFour);

        
        rpmChoose = new SendableChooser<Integer>();
        rpmChoose.setDefaultOption("Original RPM (5500)", oldRPM);
        rpmChoose.addOption("RPM 1", rpmOne);
        rpmChoose.addOption("RPM 2", rpmTwo);
        rpmChoose.addOption("RPM 3", rpmThree);
        rpmChoose.addOption("RPM 4", rpmFour);
        SmartDashboard.putData("Shooter/RPM Selection", rpmChoose);
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
