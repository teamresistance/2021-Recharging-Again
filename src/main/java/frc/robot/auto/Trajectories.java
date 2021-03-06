package frc.robot.auto;

import frc.robot.auto.functions.AutoFunction;
import frc.robot.auto.functions.CurveTurn;
import frc.robot.auto.functions.PointNTurn;

//TODO: still unsure on how to take the 2D array and use it as a trajectory object
public class Trajectories {

    // each trajectory/path/automode is stored in each method
    // name each method by the path its doing

    public static double[][] getEmpty(double pwr) {
        double[][] traj = { { 0, pwr, 0 } };
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
        double traj[][] = { // {hdg, %pwr, dist}
                { 0.0, pwr, 7.0 },
                // { 0.0, 70.0, -0.4 },
                { 90.0, pwr, 7.0 },
                // { 90.0, 70.0, -0.4 },
                { 45, pwr, -1.4 * 7.0 },
                // { 225.0, 70.0, -0.4 },
                { 90.0, pwr, 7.0 },
                // { 90.0, 70.0, -0.4 },
                { 135.0, pwr, -1.4 * 7.0 },
                // { -45.0, 70.0, -0.4 },
                { 0, pwr, -7.0 },
                // { -180.0, 70.0, -0.4 },
                { 350.0, pwr, 0.0 } };
        return traj;
    }

    public static AutoFunction[] getTest(double pwr) {
        // AutoFunction traj[] = {
        // new PointNTurn(0, pwr, 7),
        // new PointNTurn(90, pwr, 7),
        // new PointNTurn(180, pwr, 7),
        // new PointNTurn(270, pwr, 7),
        // new PointNTurn(350, pwr, 0)
        // };
        // return traj;

        AutoFunction traj[] = { new CurveTurn(.7, .8, 10) };
        return traj;
    }

    // public static double[][] getSlalom(double pwr) {
    //     double traj[][] = {
    //         {-30.0, pwr, 4.3}, //Go to pt 1 (8' x 5')
    //         {0, pwr, 9.6}, //Go to pt 2 (9' x 7')
    //         {30, pwr, 6.2}, //Go to pt 3 (15' x 7'11")
    //         {-30, pwr, 5},
    //         {-135, pwr, 5}, //Go to pt 4 (19' x 7'6")
    //         {135, pwr, 6.2}, //Go to pt 5 (22'6" x 5')
    //         {180, pwr, 9.6},
    //         {-135, pwr, 4.3}
    //     };
    //  return traj;
    // }

    public static AutoFunction[] getSlalom(double pwr) {
        AutoFunction traj[] = {
            new PointNTurn(-30, pwr, 4.3),
            new PointNTurn(0,pwr, 9.6),
            new PointNTurn(30, pwr, 6.2),
            new PointNTurn(-30, pwr, 5),
            new PointNTurn(-135, pwr, 5),
            new PointNTurn(180, pwr, 9.6),
            new PointNTurn(-135, pwr, 4.3)
        };
        return traj;
    }
    
}
