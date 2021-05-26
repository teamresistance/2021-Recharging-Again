package frc.robot.Subsystem.drive3;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//TODO2: still unsure on how to take the 2D array and use it as a trajectory object
public class Traj {

    // each trajectory/path/automode is stored in each method
    // name each method by the path its doing

    // the number at the end of each trajectory piece represents a function (turn and move, subsystem activation, radial turn, etc.)
    // NOTE: 0 = ..., 1 = ..., 2 = ...

    private static SendableChooser<String> trajChsr = new SendableChooser<>();

    public static String[] trajList = {"empty", "square", "twoLaps", "cross", "otherCross"};

    public static int chsrInit(){
        int i = 0;
        trajChsr.setDefaultOption(trajList[i], trajList[i]);
        for( ; i < trajList.length; i++){
            trajChsr.addOption(trajList[i], trajList[i]);
        }
        SmartDashboard.putData("Drv/Auto/Traj Choice", trajChsr);
        return trajList.length;
    }

    public static String getTrajChsrName(){return trajChsr.getSelected();}

    // private static Method rtnTraj();

    /**Power to apply to all legs in a trajectory array */
    public static double pwr = 0.4;

    /** @return the active chooser trajectory using the default power, pwr. */
    public static double[][] getActChsr() { return getActChsr(pwr); }

    /**
     * @param pwr - max power to limit all trajectories to.
     * @return the active chooser trajectory using the passed power.
     */
    public static double[][] getActChsr(double pwr){
        switch( trajChsr.getSelected() ){
            case "empty":
            return empty(pwr);
            case "square":
            return square(0.9);
            case "twoLaps":
            return twoLaps(pwr);
            case "cross":
            return cross(pwr);
            case "otherCross":
            return otherCross(pwr);
            default:
            System.out.println("Bad trajectory - " + trajChsr.getSelected());
            return empty(pwr);
        }
    }

    /** @return trajectory is off, 0 hdg, 0 pwr, 0 dist. */
    public static double[][] empty() {return empty(0.0);}
    /** @return trajectory is off, 0 hdg, 0 pwr, 0 dist. */
    public static double[][] empty(double pwr) {
        double[][] traj = {//{hdg, %pwr, dist}
                            {0, pwr, 0}
                          };
        return traj;
    }

    /** @return trajectory forms a square with default power, pwr. */
    public static double[][] square() {return square(pwr);}
    /** @return trajectory forms a square with passed power, pwr. */
    public static double[][] square(double pwr) {
        double[][] traj = {//{hdg, %pwr, dist, flags}
                            { 0.0, pwr, 7.0, 8 },
                            { 90.0, pwr, 7.0, 8 },
                            { 180.0, pwr, 7.0, 8 },
                            { 270.0, pwr, 7.0, 8 },
                            { 360.0, pwr, 0.0, 0 }
                          };
        return traj;
    }

    /** @return trajectory forms repeats 2 square with default power, pwr. */
    public static double[][] twoLaps() {return twoLaps(pwr);}
    /** @return trajectory forms repeats 2 square with passed power, pwr. */
    public static double[][] twoLaps(double pwr) {
        double traj[][] = {//{hdg, %pwr, dist}
                            { 0.0, pwr, 7.0 },
                            { 0.0, pwr, -0.4 },
                            { 90.0, pwr, 7.0 },
                            { 90.0, pwr, -0.4 },
                            { 180.0, pwr, 7.0 },
                            { 180.0, pwr, -0.4 },
                            { 270.0, pwr, 7.0 },
                            { 270.0, pwr, -0.4 },
                            { 350.0, pwr, 0.0 },
                            { 0.0, pwr, 7.0 },
                            { 0.0, pwr, -0.4 },
                            { 90.0, pwr, 7.0 },
                            { 90.0, pwr, -0.4 },
                            { 180.0, pwr, 7.0 },
                            { 180.0, pwr, -0.4 },
                            { 270.0, pwr, 7.0 },
                            { 270.0, pwr, -0.4 },
                            { 350.0, pwr, 0.0 }
                          };
        return traj;
    }

    /** @return trajectory, forms a square with X using default power, pwr.  Moving fwd. */
    public static double[][] croSS() {return cross(pwr);}
    /** @return trajectory, forms a square with X using passed power, pwr. */
    public static double[][] cross(double pwr) {
        double traj[][] = {//{hdg, %pwr, dist}
                            { 0.0, pwr, 7.0 },
                            { 0.0, pwr, -0.4 },
                            { 90.0, pwr, 7.0 },
                            { 90.0, pwr, -0.4 },
                            { 215.0, pwr, 1.4 * 7.0 },
                            { 215.0, pwr, -0.4 },
                            { 90.0, pwr, 7.0 },
                            { 90.0, pwr, -0.4 },
                            { -45.0, pwr, 1.4 * 7.0 },
                            { -45.0, pwr, -0.4 },
                            { -180.0, pwr, 7.0 },
                            { -180.0, pwr, -0.4 },
                            { 350.0, pwr, 0.0 }
                          };
        return traj;
    }
    
    /** @return trajectory, forms a square with X using default power, pwr.  Moving fwd & bkwd. */
    public static double[][] otherCross() {return otherCross(pwr);}
    /** @return trajectory, forms a square with X using passed power, pwr.  Moving fwd & bkwd. */
    public static double[][] otherCross(double pwr) {
        double traj[][] = {//{hdg, %pwr, dist}
                            { 0.0, pwr, 7.0 },
                            //{ 0.0, 70.0, -0.4 },
                            { 90.0, pwr, 7.0 },
                            //{ 90.0, 70.0, -0.4 },
                            { 45, pwr, -1.4 * 7.0 },
                            //{ 225.0, 70.0, -0.4 }, 
                            { 90.0, pwr, 7.0 },
                            //{ 90.0, 70.0, -0.4 },
                            { 135.0, pwr, -1.4 * 7.0 }, 
                            // { -45.0, 70.0, -0.4 },
                            { 0, pwr, -7.0 },
                            //{ -180.0, 70.0, -0.4 },
                            { 350.0, pwr, 0.0 }
                          };
        return traj;
    }
}
