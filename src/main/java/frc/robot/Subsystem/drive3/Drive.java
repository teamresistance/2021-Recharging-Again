package frc.robot.Subsystem.drive3;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.NavX;
// import frc.util.PropMath;

/**
 * This is the super class for Drv_Auto & Drv_Teleop.
 * <p>Handles common variables & methods.
 * <p>All commands to drive motors should be issue thru here.
 */
public class Drive {

    // Assignments used by DiffDrv. Slaves sent same command.  Slaves set to follow Masters in IO.
    private static DifferentialDrive diffDrv = IO.diffDrv_M;
    private static NavX gyro = IO.navX;

    public static boolean frontSwapped;    // front of robot is swapped
    public static boolean scaledOutput;    // scale the output signal
    public static double scale = 0.5;      //Scale to apply to output is active
    public static double scale() { return !scaledOutput ?  1.0 : scale; }

    /*                [0][]=hdg [1][]=dist SP, PB, DB, Mn, Mx, Xcl */
    private static double[][] parms = { { 0.0, -110.0, 1.0, 0.55, 1.0, 0.20 },
    /*                               */ { 0.0, 10.0, 0.7, 0.45, 1.0, 0.07 } };
    public static Steer steer = new Steer(parms);  //Create steer instance for hdg & dist, use default parms
    public static double strCmd[] = new double[2]; //Storage for steer return
    public static double hdgFB() {return gyro.getNormalizeTo180();}  //Only need hdg to Hold Angle 0 or 180
    public static void hdgRst() { gyro.reset(); }
    public static double hdgOut;

    public static double distFB() { return (IO.drvEnc_L.feet() + IO.drvEnc_R.feet()) / 2; }
    public static void distRst() { IO.drvEnc_L.reset(); IO.drvEnc_R.reset(); }
    public static double distOut;

    public static void init() {
        cmdUpdate(0.0, 0.0, false, 0);
    }

    /**
     * Determine any state that needs to interupt the present state, usually by way of a JS button but
     * can be caused by other events.
     */
    private static void determ() {
        diffDrv.setMaxOutput(scale());   //Testing
    }

    /**
     * Called from Robot telopPerodic every 20mS to Update the drive sub system.
     */
    public static void update() {
        determ();
        sdbUpdate();
    }

    private static void sdbUpdate() {
    }

    /**
     * Common interface for all diff drv types
     * 
     * @param lSpdY - tank(1)-left JS | arcade(2)-fwd  |  curvature(3)-fwd 
     * @param rSpdRot_XY - tank(1)-right JS | arcade(2)-rotation  |  curvature(3)-rotation
     * @param isSqOrQT - tank(1)/arcade(2)-apply sqrt  |  curvature(3)-quick turn
     * @param diffType - 0-Off  |  1=tank  |  2=arcade  |  3=curvature
     */
    public static void cmdUpdate(double lSpdY, double rSpdRot_XY, boolean isSqOrQT, int diffType) {
        switch(diffType){
            case 0:     //Off
            diffDrv.tankDrive(0.0, 0.0, false);
            break;
            case 1:     //Tank
            diffDrv.tankDrive(-lSpdY, -rSpdRot_XY, isSqOrQT);
            break;
            case 2:     //Arcade
            diffDrv.arcadeDrive(-lSpdY, rSpdRot_XY, isSqOrQT);
            break;
            case 3:     //Curvature
            diffDrv.curvatureDrive(-lSpdY, rSpdRot_XY, isSqOrQT);
            break;
            default:
            diffDrv.tankDrive(0.0, 0.0, false);
            System.out.println("Bad Diff Drive type - " + diffType);
        }
    }

    /**
     * Calls steer.update using previous steerTo values and 
     * then call the full cmdUpdate with hdgOut, distOut & square
     * for arcade control.  hdgout or distOout can be zeroed.
     * 
     * @param zero 0 - niether, 1 - hdgOut zeroed, 2 - distOut zeroed
     */
    public static void cmdUpdate(int zero){
        strCmd = steer.update();
        hdgOut = strCmd[0];     // Get hdg output, Y
        distOut = strCmd[1];    // Get dist output, X
        switch(zero) {
            case 0:
                cmdUpdate(distOut, hdgOut, true, 2);
                break;
            case 1:
                cmdUpdate(distOut, 0.0, true, 2);
                break;
            case 2:
                cmdUpdate(0.0, hdgOut, true, 2);
                break;
        }
    }

    //------------------- Legacy -------------------------
    /**
     * Stop moving  (Legacy & quick calls)
     */
    public static void cmdUpdate() { cmdUpdate(0.0, 0.0, true, 0); }

    /**
     * Tank drive
     * @param lSpdY - left tank control
     * @param rSpdRot_XY - right tank control
     */
    public static void cmdUpdate(double lSpdY, double rSpdRot_XY) { cmdUpdate(lSpdY, rSpdRot_XY, false, 1); }

    /**
     * Tank or Arcade drive  (Legacy & quick calls, Sqr true.)
     * @param lSpdY - tank-left | arcade-fwd
     * @param rSpdRot_XY - tank-right | arcade-rotation
     * @param isTank else arcade
     */
    public static void cmdUpdate(double lSpdY, double rSpdRot_XY, boolean isTank) {
        cmdUpdate(lSpdY, rSpdRot_XY, true, isTank ? 1 : 2);
    }
}
