package frc.robot.auto;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;

public class AutoSelector {

    private static int selection;
    //TODO: change name
    private static Auto path = new Auto(null);
    private static Auto2 path2 = new Auto2(null);

 

    public static void init(int sel) {
        switch (sel) {
            case 1:
                path = new Auto(Trajectories.getCross(70.0));
                break;
            case 2:
                path = new Auto(Trajectories.getSquare(70.0));
                break;
            case 3:
                path = new Auto(Trajectories.getOtherCross(70.0));
                break;
            case 4:
                path2 = new Auto2(Trajectories.getSlalom(70.0));
                break;
            default:
                path = new Auto(Trajectories.getEmpty(0));
                break;
        }
        SmartDashboard.putNumber("autoselector selection",selection);
        //path.init();
        path2.init();
        SmartDashboard.putNumber("navX", IO.navX.getAngle());
        SmartDashboard.putBoolean("path initialized", true);
        SmartDashboard.putBoolean("path executing", false);
        SmartDashboard.putBoolean("path done", false);
    }

    public static void execute() {
        SmartDashboard.putNumber("navX", IO.navX.getAngle());
        SmartDashboard.putBoolean("path executing", true);
        SmartDashboard.putBoolean("path done", false);
        //path.execute();
        path2.execute();
    }

    public static void done() {
        SmartDashboard.putNumber("navX", IO.navX.getAngle());
        SmartDashboard.putBoolean("path executing", false);
        SmartDashboard.putBoolean("path done", true);
        //path.done();
        path2.done();
    }

    public static boolean finished(boolean lol) {
            return path2.finished();
        
    }
    
}
