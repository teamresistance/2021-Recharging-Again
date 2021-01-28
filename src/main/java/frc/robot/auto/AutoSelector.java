package frc.robot.auto.holding;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoSelector {

    private static int selection;
    //TODO: change name
    private static Auto path = new Auto(null);

    public static void select() {
        switch (selection) {
            case 1:
                path = new Auto(Trajectories.getCross());
                break;
            case 2:
                path = new Auto(Trajectories.getSquare());
                break;
            default:
                path = new Auto(Trajectories.getEmpty());
        }
    }

 

    public static void init() {
        selection = 0;
        path.init();
    }

    public static void execute() {
        select();
        path.execute();
    }

    public static void done() {
        path.done();
    }

    public static boolean finished() {
        return path.finished();
    }

    public static void setSelection(int sel) {
        selection = sel;
    }

    
}
