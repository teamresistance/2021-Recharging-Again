package frc.robot.auto;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.vision.RPI;
import frc.robot.Subsystem.ballHandler.Revolver;
import frc.robot.Subsystem.ballHandler.Snorfler;

public class AutoSelector {

    private static int selection;
    private static int trajIdx = 0;
    // TODO: change name
    private static Auto path = new Auto(null);
    private static boolean snorflerOn = false;
    public static double curveTestPwr = 1.0;

    // 1 - slalom
    public static void init() {
    // public static void init(int sel) {
        curveTestPwr = SmartDashboard.getNumber("Curve Pwr", curveTestPwr);
        snorflerOn = Trajectories.getChsrAltDesc().indexOf("Path") > -1;
        path = new Auto(Trajectories.getTrajAlt(70.0));

        // SmartDashboard.putNumber("autoselector selection", sel);
        path.init();
        SmartDashboard.putBoolean("path initialized", true);
        SmartDashboard.putBoolean("path executing", false);
        SmartDashboard.putBoolean("path done", false);
    }

    public static void update(){
        SmartDashboard.putNumber("Auto/state in Robot", trajIdx);
        switch (trajIdx) {
          case 0:
            execute();
            if (finished()) {
              trajIdx++;
            }
            break;
          case 1:
            done();
            break;
        }
    }

    public static void execute() {
        SmartDashboard.putNumber("navX", IO.navX.getAngle());
        SmartDashboard.putBoolean("path executing", true);
        SmartDashboard.putBoolean("path done", false);
        // path.execute();

        if (snorflerOn) {
            Snorfler.reqsnorfDrvAuto = true;
            // Snorfler.cmdUpdate(true, Snorfler.feederSpeed, Snorfler.loaderSpeed);
            // Revolver.determ();
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
            snorflerOn = false;
            // Snorfler.cmdUpdate(false, 0, 0);
            // Revolver.determ();
        }
    }

    public static boolean finished() {
        return path.finished();

    }

    public static void disable() {
        path.disable();
    }

    public static void sdbInit(){
        SmartDashboard.putNumber("Curve Pwr", curveTestPwr);
    }

    public static void sdbUpdate(){
        curveTestPwr = SmartDashboard.getNumber("Curve Pwr", curveTestPwr);
    }
}
