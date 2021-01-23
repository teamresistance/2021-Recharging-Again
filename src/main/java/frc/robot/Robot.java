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
import frc.io.hdw_io.LimeLight;
import frc.io.hdw_io.test_io;
import frc.io.joysticks.JS_IO;
import frc.robot.Subsystem.Injector;
import frc.robot.Subsystem.Revolver;
import frc.robot.Subsystem.Shooter;
import frc.robot.Subsystem.Snorfler;
import frc.robot.Subsystem.Turret;
import frc.robot.Subsystem.drive.Drive;
import frc.robot.auto.Drive2;
import frc.robot.auto.holding.AutoSelector;

import javax.naming.LimitExceededException;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Relay;

public class Robot extends TimedRobot {
  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    IO.init();
    JS_IO.init();
    // sbdPut();
  }

  @Override
  public void robotPeriodic() {
    IO.compressorRelay.set(IO.compressor.enabled() ? Relay.Value.kForward : Relay.Value.kOff);
    IO.update();
    JS_IO.update();
  }

  @Override
  public void autonomousInit() {
    AutoSelector.init();

    SmartDashboard.putNumber("Path Selection", 0);
  }

  @Override
  public void autonomousPeriodic() {
    int choice = (int) SmartDashboard.getNumber("Path Selection", 0);

    AutoSelector.select(choice);

    if (!AutoSelector.done()) {
      AutoSelector.execute();
    }
  }

  @Override
  public void teleopInit() {
    // test_io.init();
    // Snorfler.init();
    // Revolver.init();
    // Shooter.init();
    // Injector.init();
    // Drive.init();
    // Turret.init();
    Drive2.init();
    LimeLight.init();
  }

  @Override
  public void teleopPeriodic() {
    IO.update();
    // test_io.update();
    // Snorfler.update();
    // Revolver.update();
    // Shooter.update();
    // IO.shooterTSRX.set(ControlMode.Velocity, 500);
    // Injector.update();
    // Turret.update();
    // Drive.update();
    LimeLight.sdbUpdate();
    Drive2.update();
  }

  @Override
  public void testPeriodic() {

  }

  // public void sbdPut(){
  // //Drive Puts:
  // SmartDashboard.putNumber("Drive Scale", .5);
  // }

}
