package frc.robot.Subsystem.drive3;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.io.hdw_io.IO;
import frc.io.joysticks.JS_IO;

/**
 * Add your docs here.
 */
public class Drive {

    // Assignments used by DiffDrv. Slaves sent same command.  Slaves set to follow Masters in IO.
    private static DifferentialDrive diffDrv = IO.diffDrv_M;


    // public static double tnkLeft() {return JS_IO.axLeftY.get();}       //Tank Left
    // public static double tnkRight() {return JS_IO.axRightY.get();}     //Tank Right
    // public static double arcMove() {return JS_IO.axLeftY.get();}       //Arcade move, fwd/bkwd
    // public static double arcRot() {return JS_IO.axLeftX.get();}        //Arcade Rotation
    // public static double curMove() {return JS_IO.axLeftY.get();}       //Curvature move, pwr applied
    // public static double curRot() {return JS_IO.axRightX.get();}       //Curvature direction, left.right

    // public static boolean tglFrontBtn() {return JS_IO.btnInvOrientation.onButtonPressed();}//Toggle orientation
    // public static boolean tglScaleBtn() {return JS_IO.btnScaledDrive.onButtonPressed();}   //Toggle appling scaling
    // public static boolean holdZeroBtn() {return JS_IO.btnHoldZero.isDown();}               //Hold zero hdg when help down
    // public static boolean hold180Btn() {return JS_IO.btnHold180.isDown();}                 //Hold 180 hdg when held down

    public static boolean frontSwapped;    // front of robot is swapped
    public static boolean scaledOutput;    // scale the output signal
    public static double scale = 0.5;      //Scale to apply to output is active
    public static double scale() { return !scaledOutput ?  1.0 : scale; }

    /*                [0][]=hdg [1][]=dist SP, PB, DB, Mn, Mx, Xcl */
    private static double[][] parms = { { 0.0, -110.0, 1.0, 0.5, 1.0, 0.20 },
    /*                               */ { 0.0, 10.0, 0.7, 0.45, 1.0, 0.07 } };
    public static Steer steer = new Steer(parms);  //Create steer instance for hdg & dist, use default parms
    public static double strCmd[] = new double[2]; //Storage for steer return
    public static double hdgFB() {return IO.navX.getAngle();}  //Only need hdg to Hold Angle 0 or 180
    public static void hdgRst() { IO.navX.reset(); }
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
