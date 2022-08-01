/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.joysticks.JS_IO;
import frc.io.vision.LimeLight;
import frc.io.vision.RPI;
import frc.io.hdw_io.IO;
import frc.robot.Subsystem.drive.Drive;
import frc.robot.Subsystem.drive.Drv_Auto;
import frc.robot.Subsystem.drive.Drv_Teleop;
import frc.robot.Subsystem.drive.Trajectories;

import frc.robot.Subsystem.Turret;
import frc.robot.Subsystem.Turret3;
import frc.robot.Subsystem.ballHandler.Injector;
import frc.robot.Subsystem.ballHandler.Revolver;
import frc.robot.Subsystem.ballHandler.Shooter;
import frc.robot.Subsystem.ballHandler.Snorfler;
import frc.robot.Subsystem.ballHandler.Snorfler22;
import edu.wpi.first.wpilibj.Relay;

public class Robot extends TimedRobot {
    // Used to signal Auto drv/Snorfler, need to find FMSInfo call
    private static int mode = 0; // 0=Not Init, 1=autoPeriodic, 2=teleopPeriodic
    private static boolean cmprEna = true;  //Don't need cmpr when testing drive.

    //---------- Team Color Chooser -----------------
    public static SendableChooser<String> teamColorchsr = new SendableChooser<String>();
    private static String[] chsrDesc = {
        "Blue", "Red"
    };

    /**Initialize Traj chooser for Driver to select team's alliance */
    public static void teamColorchsrInit(){
        for(int i = 0; i < chsrDesc.length; i++){
            teamColorchsr.addOption(chsrDesc[i], chsrDesc[i]);
        }
        teamColorchsr.setDefaultOption(chsrDesc[0] + " (Default)", chsrDesc[0]);   //Default MUST have a different name
        SmartDashboard.putData("Robot/TeamColor", teamColorchsr);
    }

    /**Show on sdb traj chooser info.  Called from robotPeriodic  */
    public static void teamColorchsrUpdate(){
        SmartDashboard.putString("Robot/TeamColorChoosen", teamColorchsr.getSelected());
    }
    //-----------------------------------------------

    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
        IO.init();
        JS_IO.init();
        Drv_Teleop.chsrInit();      //Drv_Teleop init Drv type Chooser.

        Shooter.chsrInit();         //Shooter preset RPMs Chooser
        Trajectories.chsrInit();    //Drv_Auto init Traj Chooser.
        teamColorchsrInit();        //Allows Driver to select team's alliance
        //Allows Driver to disable compressor during testing due to leak.
        SmartDashboard.putBoolean("Robot/Cmpr Enabled", cmprEna);
    }

    @Override
    public void robotPeriodic() {
        //Pneu. system has leak.  Dont need it when testing drive.
        cmprEna = SmartDashboard.getBoolean("Robot/Cmpr Enabled", cmprEna);
        IO.compressorRelay.set(IO.compressor.enabled() && cmprEna ? Relay.Value.kForward : Relay.Value.kOff);
        IO.update();
        JS_IO.update();
        Drv_Teleop.chsrUpdate();
        teamColorchsrUpdate();

        SmartDashboard.putNumber("Robot/Mode", mode);
        Trajectories.chsrUpdate();
    }

    @Override
    public void autonomousInit() {
        Revolver.init();
        // Snorfler.init();
        Snorfler22.init();
        Drv_Auto.init();    //Drv_Auto Get Choosen trajectory
    }

    @Override
    public void autonomousPeriodic() {
        mode = 1; // Used to signal Auto drv/Snorfler, need to find FMSInfo call
        IO.update();
        Revolver.update();
        // Snorfler.update();
        Snorfler22.update();
        Drv_Auto.update();     //Execute choosen trajectory
    }

    @Override
    public void teleopInit() {
        Drv_Auto.disable();  //Disable Auto if still executing.

        // Snorfler.init();
        Snorfler22.init();
        Revolver.init();
        Shooter.init();
        Injector.init();
        //Turret.init();
        Turret3.init();
        LimeLight.init();
        RPI.init();

        Drv_Teleop.init();  //Drv_Teleop initialize.
    }

    @Override
    public void teleopPeriodic() {
        mode = 2; // Used to signal Auto drv/Snorfler, need to find FMSInfo call

        IO.update();
        // Snorfler.update();
        Snorfler22.update();
        Revolver.update();
        Shooter.update();
        Injector.update();
        //Turret.update();
        Turret3.update();
        LimeLight.update();     // Changed from sbdUpdate - AS
        RPI.sdbUpdate();

        Drv_Teleop.update();
    }

    @Override
    public void testPeriodic() {
        SmartDashboard.putNumber("JS Axis/LX 0", JS_IO.leftJoystick.getRawAxis(0)); //Arc Rot
        SmartDashboard.putNumber("JS Axis/LY 1", JS_IO.leftJoystick.getRawAxis(1)); //Arc Spd, Tank Left
        SmartDashboard.putNumber("JS Axis/RX 0", JS_IO.rightJoystick.getRawAxis(0));//na
        SmartDashboard.putNumber("JS Axis/RY 1", JS_IO.rightJoystick.getRawAxis(1));//Tank Right
        //Enable only 1 at a time!  Comment out the other
        // IO.diffDrv_M.tankDrive(-JS_IO.leftJoystick.getRawAxis(1), -JS_IO.rightJoystick.getRawAxis(1));
        IO.diffDrv_M.arcadeDrive(-JS_IO.leftJoystick.getRawAxis(1), JS_IO.leftJoystick.getRawAxis(0));
    }

    @Override
    public void disabledInit() {
        mode = 0;
    }

    public static int getMode() {
        return mode;
    }
}
