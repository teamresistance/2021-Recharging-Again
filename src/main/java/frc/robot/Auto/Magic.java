package frc.robot.Auto;


import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Magic implements ICommand{

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

	}
	
	/*
	
	private final static double KF = 1.47832;
	private final static double KP = .3;
	private final static double KI = 0;
	private final static double KD = 100;
	private final static double TICKS_PER_FEET = 432.9;
	private final static double TICKS_PER_INCH = 36.075;
	private final static int CRUISE = 519;
	private final static int ACCEL = 519;
	
	private double targDist;
	private double deadband = 100;
	private boolean once = true;
 	
	public Magic(int feet, int inch){
		targDist = feet * TICKS_PER_FEET + inch * TICKS_PER_INCH;
		left.setSelectedSensorPosition(0, 0, 0);
		right.setSelectedSensorPosition(0, 0, 0);
		once = true;
	}
	@Override
	public void init(){
		//magic
		left.config_kF(0, KF, 20);
		left.config_kP(0,KP, 20);
		left.config_kI(0, KI, 20);
		left.config_kD(0, KD, 20);
		left.configMotionCruiseVelocity(CRUISE, 20);
		left.configMotionAcceleration(ACCEL, 20);
		left.setSelectedSensorPosition(0, 0, 0);
		
		right.config_kF(0, KF, 20);
		right.config_kP(0,KP, 20);
		right.config_kI(0, KI, 20);
		right.config_kD(0, KD, 20);
		right.configMotionCruiseVelocity(CRUISE, 20);
		right.configMotionAcceleration(ACCEL, 20);
		right.setSelectedSensorPosition(0, 0, 0);
	}
	
	@Override
	public void execute() {
		if (once) {
			once = false;
			left.setSelectedSensorPosition(0, 0, 0);
			right.setSelectedSensorPosition(0, 0, 0);
	}
		left.set(ControlMode.MotionMagic, targDist);
		right.set(ControlMode.MotionMagic, targDist);

	}

	@Override
	public boolean done() {
			if(	left.getSelectedSensorPosition(0) > (targDist - deadband) && left.getSelectedSensorPosition(0) < (targDist + deadband)){
					return true;	
						}	
		
		
		return false;
	}
	*/
}
