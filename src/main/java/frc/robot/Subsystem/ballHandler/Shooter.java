package frc.robot.Subsystem.ballHandler;

import com.ctre.phoenix.motorcontrol.ControlMode;
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
 * <p>
 * History:
 * <p>
 * 1/20/20 - Original Release
 * <p>
 * 2/2021 - KB - Added Chooser for RPM SPs & other cleanup
 * <p>
 * <p>
 * Desc: Handles the shooter subsystem
 * <p>
 * Note: Works with Injector and Revolver. The Injector controls a flipper,
 * pickup wheel & 4-wheel injector. The Revolver holds the balls until ready to
 * shoot then can deliever them one at a time thru the Injector to the Shooter.
 * So, the Shooter comes up to speed then requests the Injector then request the
 * Revolver. The Revolver also works with the Snofler to gather and store the
 * balls.
 * <p>
 * <p>
 * determ: When button rampUp(3) if state == 0 goto state 1 else goto state 0,
 * shut it all down, initialize.
 * <p>
 * <p>
 * Seq of Operation - Shooter
 * <p>
 * Init - System is initialized off. All requests off and retracted.
 * <p>
 * 0. Initialized. Shooter disabled, flywheel off, injector Disabled (4-wheel
 * off, pickup off, flipper down), hood down. No request to revolver, false.
 * <p>
 * 1. Starts the flywheel ramping to rpmSP (or power). At setpt goto state 2.
 * Read (1).onPressed to clear since used in next state.
 * <p>
 * 2. When button shoot(1).onPressed, go to state 3
 * <p>
 * 3. Requests injector enable (4-whl, pickup, flipper enabled & hood up). Wait
 * a period to get started then state 4
 * <p>
 * 4. When button shoot(1).onPressed 2nd time goto state 5
 * <p>
 * 5. Set revolver.start, index revolver 1 time. Goto state 6
 * <p>
 * 6. Wait for revolver to finish indexing goto state 7
 * <p>
 * 7. Wait some time goto state 8
 * <p>
 * 8. If shoot(1).isDown, stilled pressed, goto state 6, continue shooting, else
 * goto 4, single shot
 */
public class Shooter {
    private static WPI_TalonSRX shooter = IO.shooterTSRX;
    private static Encoder encSh = IO.shooter_Encoder;
    private static ISolenoid ballHood = IO.shooterHoodUp;

    private static int state; // Shooter state machine. 0=Off by pct, 1=On by velocity, RPM
    private static Timer stateTmr = new Timer(.05); // Timer for state machine

    private static boolean joelMode = false; // Control seq for Joel
    private static double joelShtrWSP = 0.45; // Percent to send shooter
    private static double joelShtrBSP = 1.00; // Percent to boost after shot
    private static double joelShtrTm = 0.15; // Time to hold boost then move on

    private static Integer rpmWSP = 3000; // Working RPM setpoint
    private static int rpmSPAdj1 = 4250; // Adjustable RPM setpoint when chosen
    private static int rpmSPAdj2 = 4650; // Adjustable RPM setpoint when chosen
    private static int rpmSPAdj3 = 5000; // Adjustable RPM setpoint when chosen

    private static int atSpeedDB = 100; // (WSP - DB) < RPM < (WSP + DB)
    private static int nearSpdDB = 400; // RPM > (WSP - DB)
    private static double rpmToTpc = .07833333; // TBD rpm to ticks per cycle (100ms) // 47 ticks per 1 rotation
    private static boolean shooterToggle = true;// ?????

    private static double kF = 2.5; // TalonSRX feedforward
    private static double kP = 100; // TalonSRX Proportional band
    private static double kI = 0; // TalonSRX Intgral term
    private static double kD = 0; // TalonSRX Differential term

    // RPM Chooser. Allows driver to select pre-select RPMs. [0]is default [last] is
    // adjustable.  Names assigned (+ "- value").
    private static SendableChooser<Integer> rpmChsr = new SendableChooser<Integer>();
    private static String[] rpmName = { "Zone1", "RPM2", "RPM3", "RPM4", "RPM_Adj1", "RPM_Adj2", "RPM_Adj3" };
    private static Integer[] rpmSP = { 1000, 4500, 4750, 4000, -1, -2, -3 }; // Values to use (return)

    /**Initializes the Chooser (drop down) for RPM selection.  
     * First 4 are fixed and the last 3 (negative) use another adjustable variable.
     * <p>Can only be called once.  Called from robotInit() in Robot.
     */
    public static void chsrInit(){
        // This initiates the RPm Chooser
        rpmChsr = new SendableChooser<Integer>();
        rpmChsr.setDefaultOption(rpmSP[0] + " " + rpmName[0], rpmSP[0]);
        for (int i = 1; i < rpmSP.length; i++) {
            rpmChsr.addOption(rpmSP[i] + " " + rpmName[i], rpmSP[i]);
        }
    }
    /**
     * Initialize Shooter stuff. Called from telopInit (maybe robotInit(?)) in
     * Robot.java
     */
    public static void init() {
        sdbInit();

        ballHood.set(false);

        shooter.config_kF(0, kF); // Send configuration parms to TalonSRX
        shooter.config_kP(0, kP);
        shooter.config_kI(0, kI);
        shooter.config_kD(0, kD);

        shooter.enableVoltageCompensation(true);
        shooter.configVoltageCompSaturation(12, 0);
        shooter.configVoltageMeasurementFilter(32, 0);
        encSh.reset();

        cmdUpdate(0.0, false, false, false); // Turn motor off(pct), injector false, revolver false
        state = 0; // Start at state 0
        rpmToTpc = .07833333; // MAke sure this hasn't chgd????
        shooterToggle = true; // ????
    }

    /**
     * Determine any state that needs to interupt the present state, usually by way
     * of a JS button but can be caused by other events.
     */
    private static void determ() {
        if (JS_IO.btnRampShooter.onButtonPressed()) { // Enable/Disable shooter, start/stop flywheel
            state = state != 0 ? 0 : joelMode ? 11 : 1; // ADDED - Joel mode, Bang/Bang
        }

        if (JS_IO.allStop.onButtonPressed())
            state = 99;
    }

    /** Update Shooter. Called from teleopPeriodic in robot.java */
    public static void update() {
        sdbUpdate();
        determ();

        switch (state) {
            // Shooter disabled, flywheel off, injector Disabled, hood down, request
            // revolver, false.
            case 0: // off - percentoutput (so that no negative power is sent to the motor)
                cmdUpdate(0.0, false, false, false);
                stateTmr.hasExpired(0.05, state); // Initialize timer for covTrgr. Do nothing.
                break;
            case 1: // Ramp flywheel to rpmSP (or power?). At setpt goto state 2. Read (1).onPressed
                    // to clear.
                cmdUpdate(rpmWSP, true, false, false);
                JS_IO.btnFireShooter.onButtonPressed(); // Clear button press, just incase. Do nothing
                if (isAtSpeed())
                    state++;
                break;
            case 2: // When button shoot(1).onPressed sets shtrRdy, go to state 3
                cmdUpdate(rpmWSP, true, false, false);
                if (JS_IO.btnFireShooter.onButtonPressed())
                    state++;
                break;
            case 3: // Requests injector enable (all 3 items). Wait for a period, then state 4
                cmdUpdate(rpmWSP, true, true, false);
                if (stateTmr.hasExpired(0.15, state))
                    state++; // Wait for Injector to get up to speed
                break;
            case 4: // When button shoot(1).onPressed 2nd time goto state 5
                cmdUpdate(rpmWSP, true, true, false);
                if (JS_IO.btnFireShooter.onButtonPressed())
                    state++;
                break;
            case 5: // Set revolver.reqRevShtr, index revolver 1 time. Goto next
                cmdUpdate(rpmWSP, true, true, true);
                state++;
                break;
            case 6: // When revolver has indexed goto next
                cmdUpdate(rpmWSP, true, true, false);
                if (!Revolver.isIndexing())
                    state++;
                break;
            case 7: // Wait some time. To release button, maybe shooter recovery on rapid fire.
                cmdUpdate(rpmWSP, true, true, false);
                if (stateTmr.hasExpired(0.15, state))
                    state++;
            case 8: // If shoot(1).isDown, still pressed, goto state 5, continue shooting, else goto
                    // 4, single shot
                cmdUpdate(rpmWSP, true, true, false);
                state = JS_IO.btnFireShooter.isDown() ? 5 : 4;
                break;
            // case 9: // TODO: add temporary injector shutdown, leave flywheel on while
            // shutting shooter down

            // break;

            // Shooter disabled, flywheel off, injector Disabled, hood down, request
            // revolver, false.
            case 10: // off - percentoutput (so that no negative power is sent to the motor)
                cmdUpdate(0.0, false, false, false);
                stateTmr.hasExpired(0.05, state); // Initialize timer for covTrgr. Do nothing.
                break;
            case 11: // Ramp flywheel to rpmSP (or power?). At setpt goto state 2. Read (1).onPressed
                     // to clear.
                cmdUpdate(rpmWSP, true, false, false);
                JS_IO.btnFireShooter.onButtonPressed(); // Clear button press, just incase. Do nothing
                if (isAtSpeed())
                    state++;
                break;
            case 12: // When button shoot(1).onPressed sets shtrRdy, go to state 3
                cmdUpdate(rpmWSP, true, false, false);
                if (JS_IO.btnFireShooter.onButtonPressed())
                    state++;
                break;
            case 13: // Requests injector enable (all 3 items). Wait for a period, then state 4
                cmdUpdate(rpmWSP, true, true, false);
                if (stateTmr.hasExpired(0.15, state))
                    state++; // Wait for Injector to get up to speed
                break;
            case 14: // When button shoot(1).onPressed 2nd time goto state 5
                cmdUpdate(rpmWSP, true, true, false);
                if (JS_IO.btnFireShooter.onButtonPressed())
                    state++;
                break;
            // -- Joel boost stuff --
            // case 15: // Set revolver.reqRevShtr, index revolver 1 time. Goto next
            // cmdUpdate(rpmWSP, true, true, true);
            // //When drop below rpmWSP by 200 (or 150 mS just incase.)
            // if(!isNearSpeed() || stateTmr.hasExpired(0.15, state)) state++;
            // break;
            case 15: // Set revolver.reqRevShtr, index revolver 1 time. Goto next
                cmdUpdate(rpmWSP, true, true, true);
                // When drop below rpmWSP by 200 (or 150 mS just incase.)
                if (isAtSpeed() || stateTmr.hasExpired(0.15, state))
                    state++;
                break;
            case 16: // When revolver has indexed goto next
                cmdUpdate(joelShtrBSP, false, true, false); // -- JOEL BOOST --
                if (!Revolver.isIndexing())
                    state++;
                break;
            case 17: // Wait some time. To release button, maybe shooter recovery on rapid fire.
                cmdUpdate(rpmWSP, true, true, true);
                if (stateTmr.hasExpired(0.15, state))
                    state++;
            case 18: // If shoot(1).isDown, still pressed, goto state 5, continue shooting, else goto
                     // 4, single shot
                cmdUpdate(rpmWSP, true, true, false);
                state = JS_IO.btnFireShooter.isDown() ? 15 : 14;
                break;
            // case 19: // TODO: add temporary injector shutdown, leave flywheel on while
            // shutting shooter down

            // break;

            default: // all off
                cmdUpdate(0, false, false, false);
                break;

        }
    }

    /**
     * Issue spd setting as rpmSP if isVelCmd true else as percent cmd.
     * 
     * @param spd      - cmd to issue to Flywheel Talon motor controller as rpm or
     *                 percentage
     * @param isVelCmd - spd should be issued as rpm setpoint else as a percenetage
     *                 output.
     * @param injCmd   - Request injector to start & stop.
     * @param revCmd   - Request revolver to index 1 time.
     */
    public static void cmdUpdate(double spd, boolean isVelCmd, boolean injCmd, boolean revCmd) { // control through
                                                                                                 // velocity or percent
        // shooter.set(ControlMode.Disabled, 0); // Don't think we need this

        Injector.reqInjShtr = injCmd; // Request injector to start & stop.
        Revolver.reqRevShtr = revCmd; // Request revolver to index 1 time.

        if (isVelCmd) { // Math.abs(spd) * rpmToTpc
            shooter.set(ControlMode.Velocity, Math.abs(spd) * rpmToTpc); // control as velocity (RPM)
        } else {
            shooter.set(ControlMode.PercentOutput, Math.abs(spd)); // control as percentage output
        }

        // if (shooter.getSelectedSensorVelocity() * 600 / 47 > 2000) { // if not
        // running, keep hood down
        if (isNearSpeed() || injCmd) { // if running or injector requested raise hood.
            ballHood.set(true);
        } else {
            ballHood.set(false);
        }

        SmartDashboard.putNumber("Shooter/cmdUpd/spd", spd);
        SmartDashboard.putNumber("Shooter/cmdUpd/vel input", rpmToTpc);
    }

    /*-------------------------  SDB Stuff --------------------------------------
    /**Initialize sdb */
    public static void sdbInit() {
        SmartDashboard.putNumber("Shooter/RPM/kP", kP); // Put kP on sdb
        SmartDashboard.putNumber("Shooter/RPM/kF", kF); // Put kF on sdb

        SmartDashboard.putData("Shooter/RPM/Selection", rpmChsr); // Put rpmChsr on sdb
        SmartDashboard.putNumber("Shooter/RPM/Adj SP1", rpmSPAdj1); // Put rpmSPAdj on sdb
        SmartDashboard.putNumber("Shooter/RPM/Adj SP2", rpmSPAdj2); // Put rpmSPAdj on sdb
        SmartDashboard.putNumber("Shooter/RPM/Adj SP3", rpmSPAdj3); // Put rpmSPAdj on sdb

        SmartDashboard.putBoolean("Shooter/Joel Mode", joelMode); // Put Joel mode on sdb
        rpmWSP = rpmSP[0];
    }

    public static void sdbUpdate() {
        kF = SmartDashboard.getNumber("Shooter/RPM/kF", kF); // Get kP from sdb
        kP = SmartDashboard.getNumber("Shooter/RPM/kP", kP); // Get kF from sdb
        shooter.config_kF(0, kF); // Send kP new value to Talon
        shooter.config_kP(0, kP); // Send kF new value to Talon

        rpmSPAdj1 = (int) SmartDashboard.getNumber("Shooter/RPM/Adj SP1", rpmSPAdj1); // Get adjustable RPM SP from sdb
        rpmSPAdj2 = (int) SmartDashboard.getNumber("Shooter/RPM/Adj SP2", rpmSPAdj2); // Get adjustable RPM SP from sdb
        rpmSPAdj3 = (int) SmartDashboard.getNumber("Shooter/RPM/Adj SP3", rpmSPAdj3); // Get adjustable RPM SP from sdb

        SmartDashboard.putNumber("Shooter/RPM/Wkg SP", rpmWSP); // Put the working RPM SP,rpmWSP
        rpmWSP = rpmChsr.getSelected() == null ? 1000 : (int) rpmChsr.getSelected();
        // rpmWSP = rpmChsr.getSelected(); // Get selected RPM SP value from rpmChsr
        if (rpmWSP == null || rpmWSP < 0) {
            switch (rpmWSP) {
                case -1:
                    rpmWSP = rpmSPAdj1; // If value is -1 (last choice) use adjustable SP
                    break;
                case -2:
                    rpmWSP = rpmSPAdj2; // If value is -2 (last choice) use adjustable SP
                    break;
                case -3:
                    rpmWSP = rpmSPAdj3; // If value is -3 (last choice) use adjustable SP
                    break;
                default:
                    rpmWSP = 4500;
                    System.out.println("Bad rpm choice: " + rpmWSP);
            }
        }

        // Put general Shooter info on sdb
        SmartDashboard.putNumber("Shooter/State", state);
        SmartDashboard.putBoolean("Shooter/On", ((state == 1) ? true : false));
        SmartDashboard.putBoolean("Shooter/isAtSpeed", isAtSpeed());
        SmartDashboard.putBoolean("Shooter/shooterToggle", shooterToggle);
        joelMode = SmartDashboard.getBoolean("Shooter/Joel Mode", joelMode);

        // Put Flywheel info on sdb
        SmartDashboard.putNumber("Shooter/Flywheel/Encoder", encSh.ticks());
        SmartDashboard.putNumber("Shooter/Flywheel/Velocity", shooter.getSelectedSensorVelocity());
        SmartDashboard.putNumber("Shooter/Flywheel/RPM", shooter.getSelectedSensorVelocity() * 600 / 47);
        SmartDashboard.putNumber("Shooter/Flywheel/SRX curr", shooter.getStatorCurrent());
        SmartDashboard.putNumber("Shooter/Flywheel/pdp curr", IO.pdp.getCurrent(13));
    }

    // ------------------------------ Shooter statuses and misc.
    // -------------------------
    /**
     * Probably shouldn't use this bc the states can change. Use statuses.
     * 
     * @return - present state of Shooter state machine.
     */
    public static int getState() {
        return state;
    }

    /**
     * 
     * @return - Is within 400 rpm of setpoint, rpmWSP
     */
    public static boolean isNearSpeed() {
        return (shooter.getSelectedSensorVelocity() * 600 / 47 >= (rpmWSP - nearSpdDB));
    }

    /**
     * 
     * @return - RPM FB is GTE setpoint & LTE SP + deadband. ---??? Now is within
     *         +/- DB of setpoint
     */
    public static boolean isAtSpeed() { // if it's within it's setpoint deadband
        return (shooter.getSelectedSensorVelocity() * 600 / 47 >= (rpmWSP - atSpeedDB)
                && shooter.getSelectedSensorVelocity() * 600 / 47 <= (rpmWSP + atSpeedDB));
    }

}
