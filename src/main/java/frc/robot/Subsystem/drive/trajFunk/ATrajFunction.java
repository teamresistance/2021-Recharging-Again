package frc.robot.Subsystem.drive.trajFunk;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.util.PIDXController;

/**
 * This is a collection of classes to control driving (and robot)
 * during Autonomous.
 */
public abstract class ATrajFunction {

    public static int state = 0;
    public static boolean finished = false;
    public static PIDXController pidHdg, pidDist;

    public static double hdgFB() {return IO.navX.getNormalizeTo180();}  //Only need hdg to Hold Angle 0 or 180
    public static double distFB() {return IO.drvFeet();}  //Only need hdg to Hold Angle 0 or 180
    public static double[] strCmd = new double[2];

    public static double hdgFB = 0;     //For simulator testing
    public static double distFB = 0;
    public static double radiusFB = 0;


    public static void initTraj() {
        state = 0;
        finished = false;
        System.out.println("ATraj - Init");
    }

    /**Execute this trajectory */
    public void execute() {

    }

    public static void done() {
        finished = true;
    }

    public static boolean finished() {
        return finished;
    }

    //------------------------------ Helper methods ------------------------

    /**In a PIDXController set extended values for
     * <p> SP, DB, Mn, Mx, Exp, Cmp
     */
    public static void setExt(PIDXController aPidX, double sp, double db, 
    /*                      */double mn, double mx,double exp, Boolean clmp){
        aPidX.setSetpoint(sp);
        aPidX.setInDB(db);
        aPidX.setOutMn(mn);
        aPidX.setOutMx(mx);
        aPidX.setOutExp(exp);
        aPidX.setClamp(clmp);
    }

    /**
     * Calculate the heading & distance from existing coor to coor passed.
     * @param _wpX X coor of the waypoint
     * @param _wpY Y coor of the waypoint
     * @return double[2] with the heading[0](JSX) & distance[1](JSY) from robot to waypoint.
     * */
    public static double[] wpCalcHdgDistSP(double _wpX, double _wpY){
        double deltaX = _wpX - IO.getCoorX();    //Adjacent
        double deltaY = _wpY - IO.getCoorY();    //Opposite
        double[] tmp = new double[2];   //[0] is hdgSP, [1] is distSP
        
        tmp[1] = Math.hypot(deltaX, deltaY);

        if( deltaY == 0){
            tmp[0] = deltaX < 0 ? -90 : 90;
        }else{
            tmp[0] = Math.toDegrees(Math.atan(deltaX/deltaY));
            if(deltaY < 0){
                tmp[0] += deltaX < 0 ? -180 : 180; 
            }
        }
        return tmp;
    }

    /**
     * Calculate the heading from existing coor to coor passed.
     * @param _wpX X coor of the waypoint
     * @param _wpY Y coor of the waypoint
     * @return the heading from robot to waypoint.
     * */
    public static double wpCalcHdgSP(double _wpX, double _wpY){
        double deltaX = _wpX - IO.getCoorX();    //Adjacent
        double deltaY = _wpY - IO.getCoorY();    //Opposite
        double _hdgSP;
        if( deltaY == 0){
            _hdgSP = deltaX < 0 ? -90 : 90;
        }else{
            _hdgSP = Math.toDegrees(Math.atan(deltaX/deltaY));
            if(deltaY < 0){
                _hdgSP += deltaX < 0 ? -180 : 180; 
            }
        }
        return _hdgSP;
    }

    /**
     * Calculate the distance from existing coor to coor passed.
     * @param _wpX X coor of the waypoint
     * @param _wpY Y coor of the waypoint
     * @return the distance from robot to waypoint.
     * */
    public static double wpCalcDistSP(double _wpX, double _wpY){
        return Math.hypot(_wpX - IO.getCoorX(),    //Adjacent
        /*              */_wpY - IO.getCoorY());   //Opposite
    }

    /**
     * Calculate the distance from existing coor to coor passed.
     * @param _ctrX X coor of center of the circle
     * @param _ctrY Y coor of center of the circle
     * @return the distance from robot to center.
     */
    public static double radiusFB(double _ctrX, double _ctrY){
        return Math.hypot(_ctrX - IO.getCoorX(),    //Adjacent
        /*              */_ctrY - IO.getCoorY());   //Opposite
    }

    /**Trajectory SDB initialize */
    public static void initSDB() {
        initSDBPid(pidHdg, "pidHdg");
        initSDBPid(pidDist, "pidDist");

        SmartDashboard.putNumber("Drv/pidTst/1_HdgOut", pidHdg.getOut());
        SmartDashboard.putNumber("Drv/pidTst/2_HdgAdj", pidHdg.getAdj());
        SmartDashboard.putBoolean("Drv/pidTst/3_atSP", pidHdg.atSetpoint());

        SmartDashboard.putNumber("Drv/pidTst/A_DistOut", pidDist.getOut());
        SmartDashboard.putNumber("Drv/pidTst/B_DistAdj", pidDist.getAdj());
        SmartDashboard.putBoolean("Drv/pidTst/C_atSP", pidDist.atSetpoint());
    }

    /**Trajectory SDB update */
    public static void updSDB() {
        updSDBPid(pidHdg, "pidHdg");
        updSDBPid(pidDist, "pidDist");

        SmartDashboard.putNumber("Drv/pidTst/1_HdgOut", pidHdg.getOut());
        SmartDashboard.putNumber("Drv/pidTst/2_HdgAdj", pidHdg.getAdj());
        SmartDashboard.putBoolean("Drv/pidTst/3_atSP", pidHdg.atSetpoint());

        SmartDashboard.putNumber("Drv/pidTst/A_DistOut", pidDist.getOut());
        SmartDashboard.putNumber("Drv/pidTst/B_DistAdj", pidDist.getAdj());
        SmartDashboard.putBoolean("Drv/pidTst/C_atSP", pidDist.atSetpoint());

        SmartDashboard.putNumber("Drv/pidTst/M_CoorX", IO.getCoorX());
        SmartDashboard.putNumber("Drv/pidTst/M_CoorY", IO.getCoorY());
    }

    /**Initialize SDB for a PIDXController.  Note groups limited to 10 */
    private static void initSDBPid(PIDXController pidCtlr, String pidTag) {
        SmartDashboard.putNumber("Drv/" + pidTag +"/1_P", pidCtlr.getP());
        SmartDashboard.putNumber("Drv/" + pidTag +"/2_I", pidCtlr.getI());
        SmartDashboard.putNumber("Drv/" + pidTag +"/3_D", pidCtlr.getD());
        SmartDashboard.putNumber("Drv/" + pidTag +"/4_SP",pidCtlr.getSetpoint());
        SmartDashboard.putNumber("Drv/" + pidTag +"/5_FB", pidCtlr.getInFB());
        SmartDashboard.putNumber("Drv/" + pidTag +"/6_DB", pidCtlr.getInDB());
        SmartDashboard.putNumber("Drv/" + pidTag +"/7_Mn", pidCtlr.getOutMn());
        SmartDashboard.putNumber("Drv/" + pidTag +"/8_Mx", pidCtlr.getOutMx());
        SmartDashboard.putNumber("Drv/" + pidTag +"/9_FF", pidCtlr.getOutFF());
        SmartDashboard.putNumber("Drv/" + pidTag +"/A_Exp",pidCtlr.getOutExp());
    }

    /**Update SDB for a PIDXController.  Note groups limited to 10 */
    private static void updSDBPid(PIDXController pidCtlr, String pidTag) {
        pidCtlr.setP( SmartDashboard.getNumber("Drv/" + pidTag +"/1_P", pidCtlr.getP()));
        pidCtlr.setI( SmartDashboard.getNumber("Drv/" + pidTag +"/2_I", pidCtlr.getI()));
        pidCtlr.setD( SmartDashboard.getNumber("Drv/" + pidTag +"/3_D", pidCtlr.getD()));
        pidCtlr.setSetpoint( SmartDashboard.getNumber("Drv/" + pidTag +"/4_SP", pidCtlr.getSetpoint()));
        SmartDashboard.putNumber("Drv/" + pidTag +"/5_FB", pidCtlr.getInFB());
        pidCtlr.setInDB( SmartDashboard.getNumber("Drv/" + pidTag +"/6_DB", pidCtlr.getInDB()));
        pidCtlr.setOutMn( SmartDashboard.getNumber("Drv/" + pidTag +"/7_Mn", pidCtlr.getOutMn()));
        pidCtlr.setOutMx( SmartDashboard.getNumber("Drv/" + pidTag +"/8_Mx", pidCtlr.getOutMx()));
        pidCtlr.setOutFF( SmartDashboard.getNumber("Drv/" + pidTag +"/9_FF", pidCtlr.getOutFF()));
        pidCtlr.setOutExp( SmartDashboard.getNumber("Drv/" + pidTag +"/A_Exp", pidCtlr.getOutExp()));
    }

    /**Print common stuff for pidHdg, pidDist & coors XY.  Pid SP, FB & cmd
     * @param traj name (tag) to ID the print as "tag - state:"
     */
    public static void prtShtuff(String traj){
        System.out.println(traj + " - " + state + ":\tdist   SP: " + pidDist.getSetpoint() + "\tFB: " + pidDist.getInFB() + "\tcmd: " + pidDist.getAdj());
        System.out.println("\t\thdg\tSP: " + pidHdg.getSetpoint() + "\tFB: " + pidHdg.getInFB() + "\tcmd: " + pidHdg.getAdj());
        System.out.println("Coor\tX: " + IO.getCoorX() + "\tY " + IO.getCoorY() + "\tHdg " + pidHdg.getInFB());
    }

}
