package frc.robot.auto;

//TODO: still unsure on how to take the 2D array and use it as a trajectory object
public class Trajectories {

    // each trajectory/path/automode is stored in each method
    // name each method by the path its doing

    // the number at the end of each trajectory piece represents a function (turn and move, subsystem activation, radial turn, etc.)
    // NOTE: 0 = ..., 1 = ..., 2 = ...


    public static double[][] getEmpty(double pwr) {
        double[][] traj = {{0, pwr, 0}};
        return traj;
    }

    public static double[][] getSquare(double pwr) {
        double[][] traj = { /*                      */ { 0.0, pwr, 7.0 },
                /*                              */ { 0.0, pwr, -0.4 },
                /*                              */ { 90.0, pwr, 7.0 },
                /*                              */ { 90.0, pwr, -0.4 },
                /*                              */ { 180.0, pwr, 7.0 },
                /*                              */ { 180.0, pwr, -0.4 },
                /*                              */ { 270.0, pwr, 7.0 },
                /*                              */ { 270.0, pwr, -0.4 },
                /*                              */ { 350.0, pwr, 0.0 } };
        return traj;
    }

    public static double[][] getTwoLaps(double pwr) {
        double traj[][] = { /*                  */ { 0.0, pwr, 7.0 },
                /*                              */ { 0.0, pwr, -0.4 },
                /*                              */ { 90.0, pwr, 7.0 },
                /*                              */ { 90.0, pwr, -0.4 },
                /*                              */ { 180.0, pwr, 7.0 },
                /*                              */ { 180.0, pwr, -0.4 },
                /*                              */ { 270.0, pwr, 7.0 },
                /*                              */ { 270.0, pwr, -0.4 },
                /*                              */ { 350.0, pwr, 0.0 },
                /*                              */ { 0.0, pwr, 7.0 },
                /*                              */ { 0.0, pwr, -0.4 },
                /*                              */ { 90.0, pwr, 7.0 },
                /*                              */ { 90.0, pwr, -0.4 },
                /*                              */ { 180.0, pwr, 7.0 },
                /*                              */ { 180.0, pwr, -0.4 },
                /*                              */ { 270.0, pwr, 7.0 },
                /*                              */ { 270.0, pwr, -0.4 },
                /*                              */ { 350.0, pwr, 0.0 } };
        return traj;
    }

    public static double[][] getCross(double pwr) {
        double traj[][] = { /*                  */ { 0.0, pwr, 7.0 },
                /*                              */ { 0.0, pwr, -0.4 },
                /*                              */ { 90.0, pwr, 7.0 },
                /*                              */ { 90.0, pwr, -0.4 },
                /*                              */ { 215.0, pwr, 1.4 * 7.0 },
                /*                              */ { 215.0, pwr, -0.4 },
                /*                              */ { 90.0, pwr, 7.0 },
                /*                              */ { 90.0, pwr, -0.4 },
                /*                              */ { -45.0, pwr, 1.4 * 7.0 },
                /*                              */ { -45.0, pwr, -0.4 },
                /*                              */ { -180.0, pwr, 7.0 },
                /*                              */ { -180.0, pwr, -0.4 },
                /*                              */ { 350.0, pwr, 0.0 } };
        return traj;
    }
    
    public static double[][] getOtherCross(double pwr) {
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
