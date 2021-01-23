package frc.robot.Auto;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoMode implements ICommand {

	private ICommand[] commands;
	private int index;
	
	private boolean done;
	
	public AutoMode(ICommand...commands) {
		this.commands = commands;
		this.index = 0;
		this.done = false;
	}
	@Override
	public void init() {
		for (ICommand a : commands) {
			a.init();
		}
	}
	
	@Override
	public void execute() {
		SmartDashboard.putNumber("index", index);
		//SmartDashboard.putBoolean("done", commands[index].done());
		SmartDashboard.putBoolean("don1", done);
		SmartDashboard.putNumber("commands1", commands.length);
		
		if ((index < commands.length) && !commands[index].done()) {
			commands[index].execute();
		} else {
			index++;
			if (index >= commands.length) {
				this.done = true;
			}
		}
	}

	@Override
	public boolean done() {
		return done;
	}

	@Override
	public void redo() {
		// TODO Auto-generated method stub

	}
}
