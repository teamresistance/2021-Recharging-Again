package frc.robot.auto;

import frc.robot.auto.functions.AutoFunction;
import frc.robot.auto.functions.CurveTurn;
import frc.robot.auto.functions.PointNTurn;

//TODO: still unsure on how to take the 2D array and use it as a trajectory object
public class Trajectories {

    // each trajectory/path/automode is stored in each method
    // name each method by the path its doing

    public static AutoFunction[] getEmpty(double pwr) {
        AutoFunction[] traj = { new PointNTurn(0, pwr, 0) };
        return traj;
    }

    // Dianette
    // public static double[][] getSlalom(double pwr) {
    // double traj[][] = {
    // {-30.0, pwr, 4.3}, //Go to pt 1 (8' x 5')
    // {0, pwr, 9.6}, //Go to pt 2 (9' x 7')
    // {30, pwr, 6.2}, //Go to pt 3 (15' x 7'11")
    // {-30, pwr, 5},
    // {-135, pwr, 5}, //Go to pt 4 (19' x 7'6")
    // {135, pwr, 6.2}, //Go to pt 5 (22'6" x 5')
    // {180, pwr, 9.6},
    // {-135, pwr, 4.3}
    // };
    // return traj;
    // }

    // public static AutoFunction[] getSlalom(double pwr) {
    //     AutoFunction traj[] = {
    //         new PointNTurn(0, pwr, 2.4), 
    //         new PointNTurn(-47, pwr, 6), 
    //         new PointNTurn(0, pwr, 9.2),
    //         new PointNTurn(53, pwr, 6), 
    //         new PointNTurn(-50, pwr, 3.8), 
    //         new PointNTurn(-135, pwr, 3.8),
    //         new PointNTurn(135, pwr, 7.5), 
    //         new PointNTurn(180, pwr, 9.2), 
    //         new PointNTurn(-135, pwr, 4.3) 
    //     };
    //     return traj;
    // }

    // public static AutoFunction[] getSlalom(double pwr) {
    //     AutoFunction traj[] = {
    //         new PointNTurn(0, pwr, 2.5), 
    //         new PointNTurn(-55, pwr, 5.17), 
    //         new PointNTurn(0, pwr, 8.7),
    //         new PointNTurn(55, pwr, 6.37), 
    //         new PointNTurn(-45, pwr, 4.64), 
    //         new PointNTurn(-135, pwr, 3.54),
    //         new PointNTurn(130, pwr, 6.57), 
    //         new PointNTurn(180, pwr, 8.7), 
    //         new PointNTurn(-125, pwr, 5.57),
    //         new PointNTurn(180, pwr, 2.5)
    //     };
    //     return traj;
    // }

    public static AutoFunction[] getSlalom(double pwr) {
        AutoFunction traj[] = {
            new PointNTurn(0, pwr, 2.5), 
            new PointNTurn(-55, pwr, 5.17), 
            new PointNTurn(0, pwr, 9.2),
            new PointNTurn(55, pwr, 4.57),
            new PointNTurn(0, pwr, 2),
            new PointNTurn(90, pwr, -4.3),
            new PointNTurn(180, pwr, 1.5),
            new PointNTurn(150, pwr, 6.14),
            new PointNTurn(180, pwr, 9.2), 
            new PointNTurn(-125, pwr, 5.57),
            new PointNTurn(180, pwr, 2.5)
        };
        return traj;
    }

    // Ariel
    // public static double [][] getBarrelCross()
    // {
    //     double traj [][] = {
    //         {0, 70, 8.75},
    //         {90, 70, 2.5},
    //         {180, 70, 2.5},
    //         {-90, 70, 3.75},
    //         {0, 70, 10.5},
    //         {-90, 70, 3.75},
    //         {-180, 70, 4.25},
    //         {45, 70, 10.6},
    //         {-56.3, 70, 4.5},
    //         {180, 70, 22.5}
    //       };
    //      return traj;

    // }

    // public static AutoFunction[] getBarrel(double pwr) {
    //     AutoFunction[] traj = {
    //         new PointNTurn(0, pwr, 8.75),
    //         new PointNTurn(90, pwr, 2.5),
    //         new PointNTurn(180, pwr, 2.5),
    //         new PointNTurn(-90, pwr, 3.75),
    //         new PointNTurn(0, pwr, 10.5),
    //         new PointNTurn(-90, pwr, 3.75),
    //         new PointNTurn(-180, pwr, 4.25),
    //         new PointNTurn(45, pwr, 10.6),
    //         new PointNTurn(-56.3, pwr, 4.5),
    //         new PointNTurn(180, pwr, 22.5),
    //      };
    //     return traj;
    // }


    public static AutoFunction[] getBarrel(double pwr){
        AutoFunction[] traj = {
            new PointNTurn(0,pwr,8.4),
            new PointNTurn(45,pwr,3.2),
            new PointNTurn(135,pwr, 3.6),
            new PointNTurn(-135, pwr, 3.54), //
            new PointNTurn(-45,pwr, 3.54), //fix these two angles bc its going out of bounds
            new PointNTurn(0,pwr, 5.8),
            new PointNTurn(-45, pwr, 3.54),
            new PointNTurn(-135, pwr, 3.54),
            new PointNTurn(135, pwr, 4.1),
            new PointNTurn(45, pwr, 9.7),
            new PointNTurn(-45, pwr, 3.7),
            new PointNTurn(-135, pwr, 3.54),
            new PointNTurn(177, pwr, 20)
        };
        return traj;
    }

    // Jennifer and Vireli
    // exiting start zone (20") 0 0 50 1.66'
    // going to navpoint 1 (47") 297 -63 50 3.91'
    // backwards (100") 255 -105 50 -8.33'
    // move forward (50") 0 0 50 4.17'
    // reflecting step 3 forward (100") 285 -75 50 8.33'
    // backwards from navpoint 2 (100") 265 -95 50 -8.33'
    // move forward (60") 0 0 50 5'
    // going to navpoint 3 (100") 285 -75 50 8.33'
    // entering finish zone (50") 235 -125 50 -4.17'

    public static AutoFunction[] getBounce(double pwr) {
        AutoFunction[] traj = {
            new PointNTurn(0, pwr, 1.66),
            new PointNTurn(297, pwr, 3.91),
            new PointNTurn(255, pwr, -8.33),
            new PointNTurn(0, pwr, 4.17),
            new PointNTurn(285, pwr, 8.33),
            new PointNTurn(265, pwr, -8.33),
            new PointNTurn(0, pwr, 5),
            new PointNTurn(285, pwr, 8.33),
            new PointNTurn(235, pwr, -4.17)
         };
        return traj;
    }

    public static AutoFunction[] getBounce2(double pwr) {
        AutoFunction[] traj = {
            new PointNTurn(0, pwr, 1.66),
            new PointNTurn(-63, pwr, 3.91),
            new PointNTurn(-105, pwr, -8.33),
            new PointNTurn(0, pwr, 4.17),
            new PointNTurn(-75, pwr, 8.33),
            new PointNTurn(-95, pwr, -8.33),
            new PointNTurn(0, pwr, 5),
            new PointNTurn(-75, pwr, 8.33),
            new PointNTurn(-125, pwr, -4.17)
         };
        return traj;
    }

    public static AutoFunction[] getRPathA(double pwr) {
        AutoFunction[] traj = {
            new PointNTurn(0, pwr, 5),
            new PointNTurn(26, pwr, 5.6),
            new PointNTurn(-71, pwr, 7.9),
            new PointNTurn(0, pwr, 12.5)
         };
        return traj;
    }

    public static AutoFunction[] getBPathA(double pwr) {
        AutoFunction[] traj = { 
            new PointNTurn(22, pwr, 13.5),
            new PointNTurn(-71, pwr, 7.9),
            new PointNTurn(27, pwr, 11.1)
        };
        return traj;
    }

    public static AutoFunction[] getRPathB(double pwr) {
        AutoFunction[] traj = {
            new PointNTurn(-26, pwr, 5.6),
            new PointNTurn(45, pwr, 7.1),
            new PointNTurn(-45, pwr, 7.1),
            new PointNTurn(-14, pwr, 10.3)
         };
        return traj;
    }

    public static AutoFunction[] getBPathB(double pwr) {
        AutoFunction[] traj = {
            new PointNTurn(11, pwr, 12.7),
            new PointNTurn(-45, pwr, 7.1),
            new PointNTurn(45, pwr, 10.6)
         };
        return traj;
    }

    public static AutoFunction[] getSquare(double pwr) {
        AutoFunction traj[] = {
        new PointNTurn(0, pwr, 7),
        new PointNTurn(90, pwr, 7),
        new PointNTurn(180, pwr, 7),
        new PointNTurn(270, pwr, 7),
        new PointNTurn(350, pwr, 0)
        };
        return traj;
    }

    public static AutoFunction[] getCurve1_1(double fwd) {
        AutoFunction traj[] = {
            new CurveTurn(1.0, 1.0, 3.0)
        };
        return traj;
    }

    public static AutoFunction[] getCurve1_7(double fwd) {
        AutoFunction traj[] = {
            new CurveTurn(1.0, 0.75, 3.0)
        };
        return traj;
    }

    public static AutoFunction[] getCurve1_5(double fwd) {
        AutoFunction traj[] = {
            new CurveTurn(1.0, 0.5, 3.0)
        };
        return traj;
    }

    public static AutoFunction[] getCurve7_1(double fwd) {
        AutoFunction traj[] = {
            new CurveTurn(0.75, 1.0, 3.0)
        };
        return traj;
    }

    public static AutoFunction[] getCurve7_7(double fwd) {
        AutoFunction traj[] = {
            new CurveTurn(0.75, 0.75, 3.0)
        };
        return traj;
    }

    public static AutoFunction[] getCurve7_5(double fwd) {
        AutoFunction traj[] = {
            new CurveTurn(0.75, 0.5, 3.0)
        };
        return traj;
    }

    public static AutoFunction[] getCurve5_1(double fwd) {
        AutoFunction traj[] = {
            new CurveTurn(0.5, 1.0, 3.0)
        };
        return traj;
    }

    public static AutoFunction[] getCurve5_7(double fwd) {
        AutoFunction traj[] = {
            new CurveTurn(0.5, 0.75, 3.0)
        };
        return traj;
    }

    public static AutoFunction[] getCurve5_5(double fwd) {
        AutoFunction traj[] = {
            new CurveTurn(0.5, 0.5, 3.0)
        };
        return traj;
    }

    public static AutoFunction[] getCurveTry(double fwd) {
        AutoFunction traj[] = {
            new CurveTurn(1.0, -0.9, 1.0),
            new CurveTurn(1.0, 0.9, 1.0),
            new PointNTurn(0.0, 1.0, 4.0),
            new CurveTurn(1.0, 1.0, 1.0),
        };
        return traj;
    }

}
