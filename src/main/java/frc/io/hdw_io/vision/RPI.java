package frc.io.hdw_io.vision;

import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RPI {
    private static NetworkTable rpiTable = NetworkTableInstance.getDefault().getTable("RPITable");
    private static double[] defaultValue = new double[0];

    public static void init() {
        rpiTable = NetworkTableInstance.getDefault().getTable("RPITable");
    }

    public static boolean rpiHasATarget() {
        return rpiTable.getEntry("validity").getBoolean(false);
    }

    public static double[] getRPIHeight() {
        return rpiTable.getEntry("height").getDoubleArray(defaultValue);
    }

    public static double[] getRPIWidth() {
        return rpiTable.getEntry("width").getDoubleArray(defaultValue);
    }

    public static double[] getRPIArea() {
        return rpiTable.getEntry("area").getDoubleArray(defaultValue);
    }

    public static double[] getRPIcenterX() {
        return rpiTable.getEntry("vcX").getDoubleArray(defaultValue);
    }

    public static double[] getRPIcenterY() {
        return rpiTable.getEntry("vcY").getDoubleArray(defaultValue);
    }

    public static double getNumContours() {
        return rpiTable.getEntry("nC").getDouble(0);
    }

    public static int galacticShooter() {
        //do a for each in the entire array of contours to find specific values

        
        for (double cX : getRPIcenterX()) {
            if (cX > 100110 && cX < 428131) {
                // red path a
                return 1;
            } else if ((cX > 100110 && cX < 428131)) {
                // blue path a
                return 2;
            } else if ((cX > 100110 && cX < 428131)) {
                // red path b
                return 3;
            } else {
                // blue path b
                return 4;
            }
        }

        return 999; // no paths found...?
    }

    public static void sdbUpdate() {
        SmartDashboard.putBoolean("rpi has target", rpiHasATarget());
        SmartDashboard.putNumber("num contours", getNumContours());
        // SmartDashboard.putNumber("bb height", getRPIHeight());
        // SmartDashboard.putNumber("bb width", getRPIWidth());
        // SmartDashboard.putNumber("center X", getRPIcenterX());
        // SmartDashboard.putNumber("center Y", getRPIcenterY());
        // SmartDashboard.putNumber("rpi area", getRPIArea());
    }
}
