package frc.robot.Old_Auto;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Auto implements ICommand{
	private AutoMode autoMode;
	public Auto(){
	//	autoMode = new AutoMode(new Magic(5,0));
	}
	
	@Override
	public void init() {
		autoMode.init();
	}

	public void selection(int select){
		switch(select){
			case 1:
				//autoMode = new AutoMode(new Magic(5,0),new PointTurn(90),new Magic(3, 0),new Magic(-3, 0), new PointTurn(180), new Magic(5,0));
				SmartDashboard.putString("machine", "case 1");
				break;
			case 2:
			//	autoMode = new AutoMode(new Magic(-3,0),new PointTurn(90), new Magic(3,0));
				SmartDashboard.putString("machine", "case 2");
				break;
			case 3:
			//	autoMode = new AutoMode(new Magic(5,0));
				SmartDashboard.putString("machine", "case 3");
				break;
			default:
			//	autoMode = new AutoMode(new Magic(2,0));
				SmartDashboard.putString("machine", "case default");
				break;
		}
	}
	
	@Override
	public void execute() {
		autoMode.execute();
	}

	@Override
	public boolean done() {
		return autoMode.done();
	}

	@Override
	public void redo() {
		// TODO Auto-generated method stub

	}
	

}
