package frc.robot.Subsystem.ballHandler;

import edu.wpi.first.wpilibj.motorcontrol.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.util.ISolenoid;
import frc.io.joysticks.JS_IO;
import frc.io.joysticks.util.Button;

/**
 * Author(s) - 
 * <p>
 * History:
 * 2/20/22 - Original Release
 * <p>
 * Desc: Test for 2022 bot.  Handles the snorfler subsystem
 * <p>
 * Need hdw
 */
public class Snorfler22 {
    // hdw defintions:
    private static ISolenoid snorflerExt_SV = IO.snorflerExt;
    public static Victor snorfFeed_Mtr = IO.snorfFeedMain; //Feed motor on snorfler
    public static Victor snorfElvLo_Mtr = IO.snorfFeedScdy; //Lower elevator motor
    public static Victor snorfElvHi_Mtr = IO.snorfFeedScdy; //High elevator motor

    // joystick buttons:
    private static Button btnSnorfle = JS_IO.btnTglSnorArmDn;

    // variables:
    private static int state; // Snorfler state machine. 0=Off by pct, 1=On by velocity, RPM
    public static boolean reqsnorfDrvAuto;  //Request to enable the snorfler from Drv Auto system
    
    /**
     * Initialize Shooter stuff. Called from telopInit (maybe robotInit(?)) in
     * Robot.java
     */
    public static void init() {
        sdbInit();
        cmdUpdate(false);   // select goal, left trigger, right trigger
        state = 0;          // Start at state 0
        reqsnorfDrvAuto = false;    //Request from Drv_Auto, autonomous mode.
    }

    /**
     * Update Snorfler. Called from teleopPeriodic in robot.java.
     * <p>
     * Determine any state that needs to interupt the present state, usually by way
     * of a JS button but can be caused by other events.
     */
    public static void update() {
        state = btnSnorfle.isDown() || reqsnorfDrvAuto ? 1 : 0; // for snorfler 
        smUpdate();
        sdbUpdate();
    }    

    /**State Machine Update */
    private static void smUpdate() { // State Machine Update

        switch (state) {
            case 0: // Everything is off
                cmdUpdate(false);
                break;
            case 1: // Do sumpthin and wait for action
                cmdUpdate(true);
                break;
            default: // all off
                cmdUpdate(false);
                System.out.println("bad snorfle state: " + state);
                break;

        }
    }

    /**
     * 
     * @param snorfEna    - drops the snorfler arm, turns on all motors
     * 
     */
    private static void cmdUpdate(boolean snorfEna) {
        
        snorflerExt_SV.set(snorfEna);
        snorfFeed_Mtr.set(snorfEna ? 0.7 : 0.0); // if snorf is enabled then 0.7 speed, otherwise 0.0
        snorfElvLo_Mtr.set(snorfEna ? 0.5 : 0.0);// if snorf is enabled then 0.5 speed, otherwise 0.0
        snorfElvHi_Mtr.set(snorfEna ? 0.5 : 0.0);// if snorf is enabled then 0.5 speed, otherwise 0.0

    }
    /*-------------------------  SDB Stuff --------------------------------------
    /**Initialize sdb */
    private static void sdbInit() {
        //Put stuff here on the sdb to be retrieved from the sdb later
        // SmartDashboard.putBoolean("ZZ_Template/Sumpthin", sumpthin.get());
    }

    /**Update the Smartdashboard. */
    private static void sdbUpdate() {
        //Put stuff to retrieve from sdb here.  Must have been initialized in sdbInit().
        // sumpthin = SmartDashboard.getBoolean("ZZ_Template/Sumpthin", sumpthin.get());

        //Put other stuff to be displayed here
        SmartDashboard.putNumber("Snorfler/state", state);
    }

    // ----------------- Shooter statuses and misc.-----------------
    /**
     * Probably shouldn't use this bc the states can change. Use statuses.
     * 
     * @return - present state of Shooter state machine.
     */
    public static int getState() {
        return state;
    }

    /**
     * @return If the state machine is running, not idle.
     */
    public static boolean getStatus(){
        return state != 0;      //This example says the sm is runing, not idle.
    }
    
}
