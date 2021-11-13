/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.joysticks.JS_IO;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.vision.LimeLight;
import frc.io.hdw_io.vision.RPI;

import frc.robot.Subsystem.drive.Drive;
import frc.robot.Subsystem.drive.Drv_Auto;
import frc.robot.Subsystem.drive.Drv_Teleop;
import frc.robot.Subsystem.drive.Trajectories;

import frc.robot.Subsystem.Turret;
import frc.robot.Subsystem.ballHandler.Injector;
import frc.robot.Subsystem.ballHandler.Revolver;
import frc.robot.Subsystem.ballHandler.Shooter;
import frc.robot.Subsystem.ballHandler.Snorfler;

import edu.wpi.first.wpilibj.Relay;

public class Robot extends TimedRobot {
    // Used to signal Auto drv/Snorfler, need to find FMSInfo call
    private static int mode = 0; // 0=Not Init, 1=autoPeriodic, 2=teleopPeriodic
    private static boolean cmprEna = true;  //Don't need cmpr when testing drive.

    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
        IO.init();
        JS_IO.init();

        Shooter.chsrInit();         //Shooter preset RPMs Chooser
        Drv_Teleop.chsrInit();      //Drv_Teleop init JS Chooser.
        Trajectories.chsrInit();    //Drv_Auto init Traj Chooser.

        SmartDashboard.putBoolean("Robot/Cmpr Enabled", cmprEna);
    }

    @Override
    public void robotPeriodic() {
        //Pneu. system has leak.  Dont need it when testing drive.
        cmprEna = SmartDashboard.getBoolean("Robot/Cmpr Enabled", cmprEna);
        IO.compressorRelay.set(IO.compressor.enabled() && cmprEna ? Relay.Value.kForward : Relay.Value.kOff);
        IO.update();
        JS_IO.update();

        SmartDashboard.putNumber("Robot/Mode", mode);
        Trajectories.chsrUpdate();
    }

    @Override
    public void autonomousInit() {
        Revolver.init();
        Snorfler.init();
        Drv_Auto.init();    //Drv_Auto Get Choosen trajectory
    }

    @Override
    public void autonomousPeriodic() {
        mode = 1; // Used to signal Auto drv/Snorfler, need to find FMSInfo call
        IO.update();
        Revolver.update();
        Snorfler.update();
        Drv_Auto.update();     //Execute choosen trajectory
    }

    @Override
    public void teleopInit() {
        Drv_Auto.disable();  //Disable Auto if still executing.

        Snorfler.init();
        Revolver.init();
        Shooter.init();
        Injector.init();
        Turret.init();
        LimeLight.init();
        RPI.init();

        Drv_Teleop.init();  //Drv_Teleop initialize.
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

        Drv_Teleop.update();
    }

    @Override
    public void testPeriodic() {

    }

    @Override
    public void disabledInit() {
        mode = 0;
    }

    public static int getMode() {
        return mode;
    }
}
