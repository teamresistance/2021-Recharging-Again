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
        path = new Auto(Trajectories.getTraj(70.0));
        trajIdx = 0;        //Opps

        path.init();
        SmartDashboard.putBoolean("AS/path initialized", true);
        SmartDashboard.putBoolean("AS/path executing", false);
        SmartDashboard.putBoolean("AS/path done", false);
    }

    public static void update(){
        SmartDashboard.putNumber("AS/Auto idx", trajIdx);
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
        SmartDashboard.putBoolean("AS/snorf On", snorflerOn);
        snorflerOn = (trajIdx == 0 && (Trajectories.getChsrDesc().indexOf("Path") > -1));
        Snorfler.reqsnorfDrvAuto = snorflerOn;
    }

    public static void execute() {
        SmartDashboard.putNumber("AS/navX", IO.navX.getAngle());
        SmartDashboard.putBoolean("AS/path executing", true);
        SmartDashboard.putBoolean("AS/path done", false);

        path.execute();
    }

    public static void done() {
        SmartDashboard.putNumber("AS/navX", IO.navX.getAngle());
        SmartDashboard.putBoolean("AS/path executing", false);
        SmartDashboard.putBoolean("AS/path done", true);

        path.done();
    }

    public static boolean finished() {
        return path.finished();

    }

    public static void disable() {
        path.disable();
    }

    public static void sdbInit(){
        SmartDashboard.putNumber("AS/Curve Pwr", curveTestPwr);
    }

    public static void sdbUpdate(){
        curveTestPwr = SmartDashboard.getNumber("AS/Curve Pwr", curveTestPwr);
    }
}
