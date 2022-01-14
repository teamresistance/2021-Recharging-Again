/*
Desc.  Control the Injector wheels and a Flipper.  This takes the balls
from the Revolver by flipping them high enough for the pickup wheel to lift
the ball against a friction plate into the 4 wheel spinner that injects the
ball in to the shooter.

History:
3/1/2020 - Anthony - Initial release
3/11    - JCH - Chg hdw_io and cleanup
*/
package frc.robot.Subsystem.ballHandler;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.motorcontrol.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.InvertibleDigitalInput;
import frc.io.hdw_io.InvertibleSolenoid;
import frc.io.joysticks.JS_IO;
import frc.util.Timer;

public class Injector {
    private static VictorSPX injector4Whl = IO.injector4Whl; // blue most likely sparks
    private static Victor injectorPU = IO.injectorPickup; // green
    private static InvertibleSolenoid injectorFlip = IO.injectorFlipper;

    private static int state = 0;

    private static double injector4WhlPct = 1.0; // Pct for 4 wheel injector
    private static double injectorPUPct = 1.0; // Pct for the Pickup wheel

    private static Timer delayTimer = new Timer(0.25); // Timer used to delay status for motor ramp up/dn
    // private static boolean isRunning = false; // Signal others Injector is
    // running.

    public static boolean reqInjShtr = false; //assure that this is always true when shooter needs it
    // private static boolean once = true;

    // initialzer
    public static void init() {
        state = 0;
        injector4Whl.set(ControlMode.PercentOutput, 0.0);
        injectorPU.set(0.0);
        injectorFlip.set(false);
    }

    // Determinator of Injector state
    public static void determ() {

        if (reqInjShtr) {
            state = 1;
        } else {
            state = 0;
        }

        if (JS_IO.btnStop.isDown()) {
            state = 0;
        }
    }

    // State Machine for Injector, switched to a simple toggle based on request
    public static void update() {
        determ();
        sdbUpdate();
        switch (state) {
            case 0: // All Off. injector4Whl Off injectorPU Off injectorFlip Retracted
                cmdUpdate(false, false, false);
                // isRunning = false;
                break;
            case 1: // All On. Set isRunning true after delay. Time to speed up.
                if (Shooter.isNearSpeed()) {
                    cmdUpdate(true, true, true);
                } else {
                    cmdUpdate(false, false, false);
                }
                // // Starts timer and checks it
                // if (Shooter.isAtSpeed()) {
                //     state = 2;
                // }
                break;
            // case 2:
            //     // isRunning = true;
            //     cmdUpdate(true, true, true);
            //     //TO DO: Fit this in Revolver
            //     // if (Revolver.hasUnloaded() || Revolver.hasShot()) {
            //     // state = 0;
            //     // }
            //     break;

            default: // Default
                cmdUpdate(false, false, false);
                System.out.println("Invalid Injector state - " + state);
                break;
        }

    }

    // Turn off and on components
    public static void cmdUpdate(boolean injector4WhlON, boolean injectorPUON, boolean injectorFlipON) {
        injector4Whl.set(ControlMode.PercentOutput, injector4WhlON ? injector4WhlPct : 0.0);
        injectorPU.set(injectorPUON ? injectorPUPct : 0.0);
        injectorFlip.set(injectorFlipON);
    }

    // Returns true 0.25 sec. after starting motors and false 0.25 sec. before
    // shutdown
    // public static boolean isRunning() {
    // return isRunning;
    // }

    // Return the state of the injector
    public static int getState() {
        return state;
    }

    // SmartDashboard Initialize
    public void sdbInit() {

    }

    // SmartDashboard Updates
    public static void sdbUpdate() {
        SmartDashboard.putNumber("Injector/Injector State", state);
        SmartDashboard.putBoolean("Injector/Request from Shooter", reqInjShtr);

    }

}
