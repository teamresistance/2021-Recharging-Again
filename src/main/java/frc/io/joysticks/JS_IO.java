package frc.io.joysticks;
/*
Original Author: Joey & Anthony
Rewite Author: Jim Hofmann
History:
J&A - 11/6/2019 - Original Release
JCH - 11/6/2019 - Original rework
TODO: Exception for bad or unattached devices.
      Auto config based on attached devices and position?
      Add enum for jsID & BtnID?  Button(eLJS, eBtn6) or Button(eGP, eBtnA)
Desc: Reads joystick (gamePad) values.  Can be used for different stick configurations
    based on feedback from Smartdashboard.  Various feedbacks from a joystick are
    implemented in classes, Button, Axis & Pov.
    This version is using named joysticks to istantiate axis, buttons & axis
*/

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.io.joysticks.Axis;
import frc.io.joysticks.Button;
import frc.io.joysticks.Pov;

//TODO: ASSIGN BUTTON PORTS FOR EACH BUTTON INITIALIZED !!!

//Declares all joysticks, buttons, axis & pov's.
public class JS_IO {
    public static int jsConfig = 0; // 0=Joysticks, 1=gamePad only, 2=left Joystick only
                                    // 3=Mixed LJS & GP, 4=Nintendo Pad
    // Declare all possible Joysticks
     public static Joystick leftJoystick = new Joystick(0); // Left JS
     public static Joystick rightJoystick = new Joystick(1); // Right JS
    public static Joystick coJoystick = new Joystick(2); // Co-Dvr JS
    public static Joystick gamePad = new Joystick(3); // Normal mode only (not Dual Trigger mode)
    // public static Joystick neoPad = new Joystick(4); // Nintendo style gamepad
    // public static Joystick arJS[] = { leftJoystick, rightJoystick, coJoystick, gamePad };
    // Declare all stick control

    // Drive
    public static Button btnScaledDrive = new Button(); //scale the drive
    public static Button btnInvOrientation = new Button(); //invert the orientation of the robot (joystick: forwards becomes backwards for robot and same for backwards)
    public static Button btnHoldZero = new Button();
    public static Button btnHold180 = new Button();

    public static Axis axLeftDrive = new Axis(); // Left Drive
    public static Axis axRightDrive = new Axis(); // Right Drive

    //Shooter
    public static Button btnRampShooter = new Button();
    public static Button btnFireShooter = new Button();
    public static Button btnSlowFire = new Button();

    //Revolver
    public static Button btnIndex = new Button();

    //Snorfler
    public static Button btnLowerSnorfler = new Button();
    public static Button btnReverseSnorfler = new Button();

    //Turret
    public static Axis axTurretRot = new Axis(); // Rotate turret
    public static Button btnLimeAim = new Button();
    public static Button btnLimeSearch = new Button();

    //Climb
    public static Axis axClimb = new Axis();
    public static Button btnClimb = new Button();
    public static Button btnClimbOFF = new Button();

    // LimeLight - AS
    public static Button limeLightOnOff = new Button();

    //All
    public static Button allStop = new Button(); // stops all parts of the shooter sequence
    public static Button btnStop = new Button();
    //Misc
    public static Button record  = new Button();

    // // Auto
    // public static Button drive2Off = new Button();
    // public static Button drive2Tank = new Button();
    // public static Button drive2Arcade = new Button();
    // public static Button drive2AutoTest = new Button();
    // public static Button resetGyro = new Button();
    // public static Button resetDist = new Button();
    // public static Pov pov_SP = new Pov();
    // public static Axis axRightX = new Axis();

    // Constructor
    public JS_IO() {
        init();
    }

    public static void init() {
        SmartDashboard.putNumber("JS_Config", jsConfig);
        configJS();
    }

    // can put this under a button press
    public static void update() { // Chk for Joystick configuration
        if (jsConfig != SmartDashboard.getNumber("JS_Config", 0)) {
            jsConfig = (int) SmartDashboard.getNumber("JS_Config", 0);
            CaseDefault();
            configJS();
        }
    }

    public static void configJS() { // Default Joystick else as gamepad
        jsConfig = (int) SmartDashboard.getNumber("JS_Config", 0);

        switch (jsConfig) {
            case 0: // Normal 3 joystick config
                Norm3JS();
                break;

            case 1: // Gamepad only
                A_GP();
                break;

            default: // Bad assignment
                // CaseDefault();
                break;

        }
    }

    // ================ Controller actions ================

    // ----------- Normal 3 Joysticks -------------
    private static void Norm3JS() {

        // All stick axisesssss
        axLeftDrive.setAxis(leftJoystick, 1);
        axRightDrive.setAxis(rightJoystick, 1);
        axTurretRot.setAxis(coJoystick, 0);
       // axClimb.setAxis(coJoystick, 1);

        //Drive buttons
        btnScaledDrive.setButton(rightJoystick, 3);
        btnInvOrientation.setButton(rightJoystick, 1);

        //snorfler buttons
        btnReverseSnorfler.setButton(coJoystick,5);
        btnLowerSnorfler.setButton(coJoystick, 3);

        //turret buttons
        btnLimeSearch.setButton(coJoystick, 12);
        btnLimeAim.setButton(coJoystick, 10);

        //shooting buttons
        btnRampShooter.setButton(coJoystick, 4);
        btnFireShooter.setButton(coJoystick, 1);
        btnSlowFire.setButton(coJoystick, 2);
        btnIndex.setButton(coJoystick, 6);

        btnStop.setButton(coJoystick, 11);

         // Limelight Buttons - AS
         limeLightOnOff.setButton(rightJoystick, 2);

        // drive2Off.setButton(leftJoystick, 10);
        // drive2Tank.setButton(leftJoystick, 9);
        // drive2Arcade.setButton(leftJoystick, 11);
        // drive2AutoTest.setButton(leftJoystick, 12);

        // resetDist.setButton(leftJoystick, 5);
        // resetGyro.setButton(leftJoystick, 3);

        // pov_SP.setPov(coJoystick, 1);
    }

    // ----- gamePad only --------
    private static void A_GP() {
        // All stick axisesssss
        axLeftDrive.setAxis(gamePad, 1); //left stick Y
        axRightDrive.setAxis(gamePad, 5); //right stick Y
        axTurretRot.setAxis(gamePad, 4); // Neg = CW, Pos = CCW (left stick X?)

        //Drive buttons
        btnScaledDrive.setButton(gamePad, 5); //L1
        btnInvOrientation.setButton(gamePad, 10); //r-stick push

        //snorfler buttons
        btnReverseSnorfler.setButton(gamePad,9); //l-stick push
        btnLowerSnorfler.setButton(gamePad, 1); //A

        //turret buttons
        btnLimeSearch.setButton(gamePad, 4); //Y
        btnLimeAim.setButton(gamePad, 6); //R1

        //shooting buttons
        btnRampShooter.setButton(gamePad, 3); //X
        btnFireShooter.setButton(gamePad, 2); //B
        //btnSlowFire.setButton(gamePad, ???);
        btnIndex.setButton(gamePad, 7); //Back

        btnStop.setButton(gamePad, 8); //start

        // drive2Off.setButton(gamePad, 1);
        // drive2Tank.setButton(gamePad, 2);
        // drive2Arcade.setButton(gamePad, 3);
        // drive2AutoTest.setButton(gamePad, 4);
        // resetDist.setButton(gamePad, 5);
        // resetGyro.setButton(gamePad, 6);

        // pov_SP.setPov(gamePad, 0);
    }

    // ----------- Case Default -----------------
    private static void CaseDefault() {
        // All stick axisesssss
        axLeftDrive.setAxis(null, 0);
        axRightDrive.setAxis(null, 0);
        axTurretRot.setAxis(null, 0);
        
        btnScaledDrive.setButton(null, 0); //scale the drive
        btnInvOrientation.setButton(null, 0); //invert the orientation of the robot (joystick: forwards becomes backwards for robot and same for backwards)
        btnHoldZero.setButton(null, 0);
        btnHold180.setButton(null, 0);    
        btnRampShooter.setButton(null, 0);
        btnFireShooter.setButton(null, 0);
        btnSlowFire.setButton(null, 0);
        btnIndex.setButton(null, 0);
        btnLowerSnorfler.setButton(null, 0);
        btnReverseSnorfler.setButton(null, 0);
        btnLimeAim.setButton(null, 0);
        btnLimeSearch.setButton(null, 0);
        btnClimb.setButton(null, 0);
        btnClimbOFF.setButton(null, 0);    
        limeLightOnOff.setButton(null, 0);
    }
}