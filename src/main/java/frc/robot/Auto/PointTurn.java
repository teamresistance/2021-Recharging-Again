package frc.robot.Auto;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PointTurn implements ICommand {
	

	// For point turn in low gear
	
	private double targetDeg;
	
	private double kP;
	private double kI;
	private double kD;
	private double maxI;
	private double Izone;
	private double integral;
	private double degreeDeadband;
	private double velocityDeadband;
	private double prevError;
	private double error;
	
	public PointTurn(double targetDegrees) {
		targetDeg = targetDegrees;
		integral = 0.0;
		degreeDeadband = 8;
		prevError = 0.0;
		velocityDeadband = 10;
		error = 100;
			
	}
/*
	@Override
	public void execute() {		
		kP = SmartDashboard.getNumber("kP", .2);
		kI = SmartDashboard.getNumber("kI", 0.001);
		kD = SmartDashboard.getNumber("kD", 1);;
		maxI = SmartDashboard.getNumber("maxI", 0);
		Izone = SmartDashboard.getNumber("Izone", 0);

		//SmartDashboard.putNumber("LF Vel",IO.leftFront.getSelectedSensorVelocity(0));
//		SmartDashboard.putNumber("RF Vel", IO.rightFront.getSelectedSensorVelocity(0));
//		kP = 0.01;
		
//		kP = .2;
//		kI = 0.001;
//		kD = 1;
//		maxI = 0;
//		Izone = 0;
//		
//		
		

		double gyroAngle = IO.navX.getNormalizedAngle();
		double targetAngle = ((targetDeg % 360) + 360) % 360;
		error = targetAngle - gyroAngle;
		SmartDashboard.putNumber("angle error", error);
		SmartDashboard.putNumber("%power", IO.left.getMotorOutputPercent());
		if (Math.abs(error) > 180) { // if going around the other way is closer
			if (error > 0) { // if positive
				error = error - 360;
			} else { // if negative
			    error =  error + 360;
			}
		}
		
//		if (kI != 0) {
//            double potentialIGain = (integral + error) * kI;
//            if (potentialIGain < maxI) {
//              if (potentialIGain > -maxI) {
//                integral += error;
//              } else {
//                integral = -maxI; // -1 / kI
//              }
//            } else {
//              integral = maxI; // 1 / kI
//            }
//        } else {
//        	integral = 0;
//        }
		
		if (kI != 0) {
			if (Math.abs(error) < Izone) {
				integral += error;
			}
			if (Math.abs(integral) > maxI) {
				integral = Math.copySign(maxI, integral);
				SmartDashboard.putNumber("Point Turn Integral", integral);
			}
		} else {
			integral = 0;
		}
		
		
		
		if (Math.abs(error) < degreeDeadband) {
			error = 0;
			integral = 0;
		}
		
		double result = (kP * error) + (kI * integral) + (kD * (error - prevError));
		
		prevError = error;
		
		 if (result > 1) {
	          result = 1;
	     } else if (result < -1) {
	          result = -1;
	     }
		 
		 IO.left.set(ControlMode.PercentOutput, result);
		 IO.right.set(ControlMode.PercentOutput, -result);
		
	}

	@Override
	public boolean done() {
		if (error < 10 && error > -10 && 
				Math.abs(IO.left.getSelectedSensorVelocity(0)) < velocityDeadband &&
				Math.abs(IO.right.getSelectedSensorVelocity(0)) < velocityDeadband) {
			return true;
		}
		return false;
	}

	@Override
	public void init() {
	
		
	}
	*/

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void redo() {
		// TODO Auto-generated method stub

	}

}
