package frc.robot.auto;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.vision.RPI;
import frc.robot.Subsystem.Revolver;
import frc.robot.Subsystem.Snorfler;

public class AutoSelector {

    private static int selection;
    // TODO: change name
    private static Auto path = new Auto(null);
    private static boolean snorflerOn = false;

    // 1 - slalom
    public static void init(int sel) {
        switch (sel) {
            case 1:
                path = new Auto(Trajectories.getSlalom(70));
                snorflerOn = false;
                break;
            case 2:
                path = new Auto(Trajectories.getBarrel(70));
                snorflerOn = false;
                break;
            case 3:
                path = new Auto(Trajectories.getBounce(70));
                snorflerOn = false;
                break;
            case 4:
                path = new Auto(Trajectories.getRPathA(70));
                snorflerOn = true;
                break;
            case 5:
                path = new Auto(Trajectories.getBPathA(70));
                snorflerOn = true;
                break;
            case 6:
                path = new Auto(Trajectories.getRPathB(70));
                snorflerOn = true;
                break;
            case 7:
                path = new Auto(Trajectories.getBPathB(70));
                snorflerOn = true;
                break;
            case 20:
                path = new Auto(Trajectories.getSquare(70.0));
                snorflerOn = false;
                break;
            case 21:
                int pathNum = galacticShooter();
                switch (pathNum) {
                    case 1:
                        path = new Auto(Trajectories.getRPathA(70));
                        break;
                    case 2:
                        path = new Auto(Trajectories.getBPathA(70));
                        break;
                    case 3:
                        path = new Auto(Trajectories.getRPathB(70));
                        break;
                    case 4:
                        path = new Auto(Trajectories.getRPathB(70));
                        break;
                    default:
                        path = new Auto(Trajectories.getEmpty(0));
                        break;
                }
                break;
            default:
                path = new Auto(Trajectories.getEmpty(0));
                break;
        }

        SmartDashboard.putNumber("autoselector selection", sel);
        // path.init();
        path.init();
        SmartDashboard.putBoolean("path initialized", true);
        SmartDashboard.putBoolean("path executing", false);
        SmartDashboard.putBoolean("path done", false);
    }

    public static void execute() {
        SmartDashboard.putNumber("navX", IO.navX.getAngle());
        SmartDashboard.putBoolean("path executing", true);
        SmartDashboard.putBoolean("path done", false);
        // path.execute();

        if (snorflerOn) {
            Snorfler.cmdUpdate(true, true, Snorfler.feederSpeed, Snorfler.loaderSpeed);
            Revolver.determ();
        }
        path.execute();
    }

    public static void done() {
        SmartDashboard.putNumber("navX", IO.navX.getAngle());
        SmartDashboard.putBoolean("path executing", false);
        SmartDashboard.putBoolean("path done", true);
        // path.done();
        path.done();

        if (snorflerOn) {
            Snorfler.cmdUpdate(false, false, 0, 0);
            Revolver.determ();
        }
    }

    public static boolean finished() {
        return path.finished();

    }

    public static void disable() {
        path.disable();
    }

    public static int galacticShooter() {
        // if (specific values) {
        //     // red path a
        //     return 1;
        // } else if (other specific) {
        //     // blue path a
        //     return 2;
        // } else if (other other) {
        //     // red path b
        //     return 3;
        // } else if (otehretete) {
        //     // blue path b
        //     return 4;
        // } else {
        //     return 999;
        // }

        return 999;
    }
}
