/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.vision.LimeLight;
import frc.io.hdw_io.vision.RPI;
import frc.io.joysticks.JS_IO;

// import frc.robot.auto.AutoSelector;
// import frc.robot.auto.Trajectories;

// import frc.robot.Subsystem.drive.Drive;
// import frc.robot.Subsystem.drive3.Drv_Auto;
import frc.robot.Subsystem.drive3.Drv_Teleop;
// import frc.robot.Subsystem.drive3.Drv_Auto;
// import frc.robot.Subsystem.drive3.Traj;
import frc.robot.Subsystem.drive3.Drv_Auto2;
import frc.robot.Subsystem.drive3.Trajectories;

import frc.robot.Subsystem.Turret;
import frc.robot.Subsystem.ballHandler.Injector;
import frc.robot.Subsystem.ballHandler.Revolver;
import frc.robot.Subsystem.ballHandler.Shooter;
import frc.robot.Subsystem.ballHandler.Snorfler;

import edu.wpi.first.wpilibj.Relay;

public class Robot extends TimedRobot {
    // Used to signal Auto drv/Snorfler, need to find FMSInfo call
    private static int mode = 0; // 0=Not Init, 1=autoPeriodic, 2=teleopPeriodic

    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
        IO.init();
        JS_IO.init();
        Shooter.chsrInit();

        Drv_Teleop.chsrInit(); // Added for Drive3 testing.
        // AutoSelector.sdbInit();
        // Traj.chsrInit(); // Added for Drive3 testing.
        Trajectories.chsrInit(); // Added for Drive3 testing.
    }

    @Override
    public void robotPeriodic() {
        IO.compressorRelay.set(IO.compressor.enabled() ? Relay.Value.kForward : Relay.Value.kOff);
        IO.update();
        JS_IO.update();

        SmartDashboard.putNumber("Robot/Mode", mode);
        Trajectories.chsrUpdate();
    }

    @Override
    public void autonomousInit() {
        Revolver.init();
        Snorfler.init();

        // AutoSelector.init();
        // Drv_Auto.init();    //Test Drive3
        Drv_Auto2.init();    //Test Drive3 Get Choosen trajectory
    }

    @Override
    public void autonomousPeriodic() {
        mode = 1; // Used to signal Auto drv/Snorfler, need to find FMSInfo call
        // AutoSelector.sdbUpdate();
        Revolver.update();
        Snorfler.update();
        // AutoSelector.update();
        // Drv_Auto.update();
        Drv_Auto2.update();     //Execute choosen trajectory
    }

    @Override
    public void teleopInit() {
        // AutoSelector.disable();
        Drv_Auto2.disable();  // Added for Drive3 testing.  Disable Auto if still executing.

        Snorfler.init();
        Revolver.init();
        Shooter.init();
        Injector.init();
        Turret.init();
        LimeLight.init();
        RPI.init();

        // Drive.init();
        Drv_Teleop.init();  // Added for Drive3 testing.
    }

    @Override
    public void teleopPeriodic() {
        mode = 2; // Used to signal Auto drv/Snorfler, need to find FMSInfo call

        IO.update();
        Snorfler.update();
        Revolver.update();
        Shooter.update();
        Injector.update();
        Turret.update();
        LimeLight.update();     // Changed from sbdUpdate - AS
        RPI.sdbUpdate();

        // Drive.update();
        Drv_Teleop.update();    // Added for Drive3 testing.
    }

    @Override
    public void testPeriodic() {

    }

    @Override
    public void disabledInit() {
        mode = 0;
        // SmartDashboard.putNumber("Robot/Mode", mode);
    }

    public static int getMode() {
        return mode;
    }
}
