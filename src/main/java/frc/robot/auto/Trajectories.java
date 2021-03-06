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

    // public static double[][] getSlalom(double pwr) {
    //     double traj[][] = {
    //         {-30.0, pwr, 4.9}, //Go to pt 1 (8' x 5')
    //         {-63.0, pwr, 2.7}, //Go to pt 2 (9' x 7')
    //         {-9.0, pwr, 6.1}, //Go to pt 3 (15' x 7'11")
    //         {186.0,pwr, 4.0}, //Go to pt 4 (19' x 7'6")
    //         {216.0,pwr, 4.3} //Go to pt 5 (22'6" x 5')
    //     };
    //  return traj;
    // }

    public static double[][] getSlalom(double pwr) {
        double traj[][] = {
            {-30.0, pwr, 4.3}, //Go to pt 1 (8' x 5')
            {0, pwr, 9.6}, //Go to pt 2 (9' x 7')
            {30, pwr, 6.2}, //Go to pt 3 (15' x 7'11")
            {-30, pwr, 5},
            {-135, pwr, 5}, //Go to pt 4 (19' x 7'6")
            {135, pwr, 6.2}, //Go to pt 5 (22'6" x 5')
            {180, pwr, 9.6},
            {-135, pwr, 4.3}
        };
     return traj;
    }
}
