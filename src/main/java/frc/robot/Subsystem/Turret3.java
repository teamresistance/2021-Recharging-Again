package frc.robot.Subsystem;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.motorcontrol.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.util.InvertibleDigitalInput;
import frc.io.joysticks.JS_IO;
import frc.util.PropMath;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Turret3 {
    private static Victor turret = IO.turretRot;
    private static InvertibleDigitalInput leftMag = IO.turCCWLimitSw; // right rel to bot
    private static InvertibleDigitalInput rightMag = IO.turCWLimitSw;
    private static AnalogPotentiometer turretPot = IO.turretPosition;
    
    private static boolean atLimitLeft;
    private static boolean atLimitRight;
    private static NetworkTable neTable;

    public static void init() {
        atLimitLeft = false;
        atLimitRight = false;
        turret.set(0);
        neTable = NetworkTableInstance.getDefault().getTable("photonvision");
    }

    public static void update() {
        //System.out.println(SmartDashboard.getNumber("photonvision/gloworm/targetPixelsX", 10));
        System.out.println(neTable.getEntry("gloworm/targetPixelsX").getDouble(10));
    }

    private static void cmdUpdate(double val) {

    }
}
