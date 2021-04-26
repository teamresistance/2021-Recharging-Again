package frc.robot.Subsystem.drive3;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.io.hdw_io.IO;

/**
 * Add your docs here.
 */
public class Drive {

    // Assignments used by DiffDrv. Slaves sent same command.  Slaves set to follow Masters in IO.
    private static DifferentialDrive diffDrv = IO.diffDrv_M;

    public static void init() {
        cmdUpdate(0.0, 0.0, false, 0);
    }

    /**
     * Determine any state that needs to interupt the present state, usually by way of a JS button but
     * can be caused by other events.
     */
    private static void determ() {
    }

    /**
     * Called from Robot telopPerodic every 20mS to Update the drive sub system.
     */
    public static void update() {
    }

    private static void sdbUpdate() {
    }

    /**
     * Common interface for all diff drv types
     * @param lSpdY - tank(1)-left JS | arcade(2)-fwd  |  curvature(3)-fwd 
     * @param rSpdRot_XY - tank(1)-right JS | arcade(2)-rotation  |  curvature(3)-rotation
     * @param isSqrtOrQT - tank(1)/arcade(2)-apply sqrt  |  curvature(3)-quick turn
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
