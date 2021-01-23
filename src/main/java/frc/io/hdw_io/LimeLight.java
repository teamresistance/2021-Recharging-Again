package frc.io.hdw_io;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class LimeLight {
    private static NetworkTable limeTable = NetworkTableInstance.getDefault().getTable("limelight");
    private static double ledmode = 0, cammode = 0, pipeline = 0;

    public static void init() {

        limeTable = NetworkTableInstance.getDefault().getTable("limelight");
        SmartDashboard.putNumber("led mode", ledmode);
        SmartDashboard.putNumber("cam mode", cammode);
        SmartDashboard.putNumber("pipeline", pipeline);
    }

    public static boolean llHasTarget() {
        double valid = limeTable.getEntry("tv").getDouble(0);

        if (valid == 1.0) {
            return true;
        } else {
            return false;
        }
    }

    // if hastarget, -1=right, 1=left, 0=on target else 999=no target
    public static Integer llOnTarget(double db) {
        double tmpD = getLLX();
        if(llHasTarget()){
            if(Math.abs(tmpD) > db){
                return tmpD < 0.0 ? -1 : 1;
            }
            return 0;
        }
        return 999; 
    }

    public static Integer llOnTarget() {
        return llOnTarget(2);
    }

    public static double getLLX() {
        return limeTable.getEntry("tx").getDouble(999);
    }

    public static double getLLY() {
        return limeTable.getEntry("ty").getDouble(0);
    }

    public static double getLLArea() {
        return limeTable.getEntry("ta").getDouble(0);
    }

    // default of current pipeline (0), off (1), blinking? (2), on (3)
    public static void setLED() {
         limeTable.getEntry("ledMode").setNumber(ledmode);
    }

    // set vision (0) or driver mode (1)
    public static void setCamMode() {
        limeTable.getEntry("camMode").setNumber(cammode);
    }

    public static void setPipeline() {
        limeTable.getEntry("pipeline").setNumber(pipeline);
    }

    public static void sdbUpdate() {
        getLLX();
        SmartDashboard.putBoolean("ll has target", llHasTarget());
        SmartDashboard.putNumber("limelight x offset", getLLX());
        SmartDashboard.putNumber("limelight y offset", getLLY());
        SmartDashboard.putNumber("limelight percent area", getLLArea() * 100);

        ledmode = SmartDashboard.getNumber("led mode", ledmode);
        setLED();
        cammode = SmartDashboard.getNumber("cam mode", cammode);
        setCamMode();
        pipeline = SmartDashboard.getNumber("pipeline", pipeline);
        setPipeline();

    }
}
