package frc.io.hdw_io.vision;

import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RPI {
    private static NetworkTable rpiTable = NetworkTableInstance.getDefault().getTable("RPITable");

    public static void init() {

        rpiTable = NetworkTableInstance.getDefault().getTable("RPITable");
    }

    public static boolean rpiHasTarget() {
        return rpiTable.getEntry("validity").getBoolean(false);
    }

    public static double getRPIHeight() {
        return rpiTable.getEntry("height").getDouble(999);
    }

    public static double getRPIWidth() {
        return rpiTable.getEntry("width").getDouble(0);
    }

    public static double getRPIArea() {
        return rpiTable.getEntry("area").getDouble(0);
    }

    public static double getRPIcenterX() {
        return rpiTable.getEntry("vcX").getDouble(0);
    }

    public static double getRPIcenterY() {
        return rpiTable.getEntry("vcY").getDouble(0);
    }

    public static double getNumContours() {
        return rpiTable.getEntry("nC").getDouble(0);
    }

    public static void sdbUpdate() {
        SmartDashboard.putBoolean("rpi has target", rpiHasTarget());
        SmartDashboard.putNumber("bb height", getRPIHeight());
        SmartDashboard.putNumber("bb width", getRPIWidth());
        SmartDashboard.putNumber("num contours", getNumContours());
        SmartDashboard.putNumber("center X", getRPIcenterX());
        SmartDashboard.putNumber("center Y", getRPIcenterY());
        SmartDashboard.putNumber("rpi area", getRPIArea());
    }
}
