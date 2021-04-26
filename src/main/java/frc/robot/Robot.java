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
import frc.io.hdw_io.IO;
import frc.io.hdw_io.vision.LimeLight;
import frc.io.hdw_io.vision.RPI;
import frc.io.hdw_io.test_io;
import frc.io.joysticks.JS_IO;
// import frc.robot.Subsystem.drive.Drive;
import frc.robot.Subsystem.drive3.Drv_Teleop;
import frc.robot.Subsystem.Turret;

import frc.robot.Subsystem.ballHandler.Injector;
import frc.robot.Subsystem.ballHandler.Revolver;
import frc.robot.Subsystem.ballHandler.Shooter;
import frc.robot.Subsystem.ballHandler.Snorfler;

import frc.robot.auto.AutoSelector;
import frc.robot.auto.Trajectories;

import com.ctre.phoenix.motorcontrol.ControlMode;

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
        Drv_Teleop.chsrInit(); // Added for Drive3 testing.
        Shooter.chsrInit();
        Trajectories.chsrInit();
        AutoSelector.sdbInit();
    }

    @Override
    public void robotPeriodic() {
        SmartDashboard.putNumber("Robot/Mode", mode);
        Trajectories.chsrUpdate();

        IO.compressorRelay.set(IO.compressor.enabled() ? Relay.Value.kForward : Relay.Value.kOff);
        IO.update();
        JS_IO.update();
    }

    @Override
    public void autonomousInit() {
        Revolver.init();
        Snorfler.init();
        AutoSelector.init();
    }

    @Override
    public void autonomousPeriodic() {
        mode = 1; // Used to signal Auto drv/Snorfler, need to find FMSInfo call
        AutoSelector.sdbUpdate();
        Revolver.update();
        Snorfler.update();
        AutoSelector.update();
    }

    @Override
    public void teleopInit() {
        AutoSelector.disable();
        Snorfler.init();
        Revolver.init();
        Shooter.init();
        Injector.init();
        // Drive.init();
        Drv_Teleop.init();  // Added for Drive3 testing.
        Turret.init();
        LimeLight.init();
        RPI.init();
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
        // Drive.update();
        Drv_Teleop.update();    // Added for Drive3 testing.
        LimeLight.update();     // Changed from sbdUpdate - AS
        RPI.sdbUpdate();
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
    // public void sbdPut(){
    // //Drive Puts:
    // SmartDashboard.putNumber("Drive Scale", .5);
    // }

}
