package frc.robot.auto;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.auto.functions.AutoFunction;
import frc.robot.auto.functions.CurveTurn;
import frc.robot.auto.functions.PointNTurn;

//TODO: still unsure on how to take the 2D array and use it as a trajectory object
public class Trajectories {
    private static SendableChooser<Integer> chsr = new SendableChooser<Integer>();
    private static String[] chsrDesc = {
       "Slalom", "Barrel", "Bounce",
       "Red Path A", "Blue Path A", "Red Path B", "Blue Path B",
       "Test Curve 1x1", "Test Curve 1x7", "Test Curve 1x5",
       "Test Curve 7x1", "Test Curve 7x7", "Test Curve 7x5",
       "Test Curve 5x1", "Test Curve 5x7", "Test Curve 5x5",
       "Test Curve Try", "Square", "Galaxtic"};
    private static int[] chsrNum = {
        1,2,3,
        4,5,6,7,
        8,9,10,
        11,12,13,
        14,15,16,
        17,18, 21};

    private static void chsrInit(){
        for(int i = 0; i < chsrDesc.length; i++){
            chsr.addOption(chsrDesc[i], chsrNum[i]);
        }
        chsr.setDefaultOption(chsrDesc[0] + " (Default)", chsrNum[0]);   //Default MUST have a different name
        SmartDashboard.putData("Traj/Choice", chsr);
    }

    private static AutoFunction[] getTraj(){
        switch(chsr.getSelected()){
            case 1:
            return getSlalom(70);
            case 2:
            return getBarrel(70);
            case 3:
            return getBounce(70);
            case 4:
            return getRPathA(70);
            case 5:
            return getRPathB(70);
            case 6:
            return getBPathA(70);
            case 7:
            return getBPathB(70);
            case 8:
            return getCurve1_1(70);
            case 9:
            return getCurve1_7(70);
            case 10:
            return getCurve1_5(70);
            case 11:
            return getCurve7_1(70);
            case 12:
            return getCurve7_7(70);
            case 13:
            return getCurve7_5(70);
            case 14:
            return getCurve5_1(70);
            case 15:
            return getCurve5_7(70);
            case 16:
            return getCurve5_5(70);
            case 17:
            return getCurveTry(70);
            case 18:
            return getSquare(70);
            case 21:
            return getGalaxtic(70);
            default:
            System.out.println("Bad Traj Number - " + chsr.getSelected());
            return getEmpty(0);

        }

    }

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
            new PointNTurn(0, pwr, 3), //2.8
            new PointNTurn(270, pwr, 3.2),
            new PointNTurn(270, pwr, -2.5),
            new PointNTurn(230, pwr, -6.3), //225
            new PointNTurn(315, pwr, 1.8), //2.1
            new PointNTurn(270, pwr, 6.8), //5.4
            new PointNTurn(270, pwr, -6.8),
            new PointNTurn(0, pwr, 6),
            new PointNTurn(270, pwr, 8.2),
            new PointNTurn(270, pwr, -2.4),
            new PointNTurn(0, pwr, 3)
         };
        return traj;
    }



    public static AutoFunction[] getRPathA(double pwr) {
        AutoFunction[] traj = {
            new PointNTurn(0, 40, 0),
            new PointNTurn(0, pwr, 5),
            new PointNTurn(26, 100, 0),
            new PointNTurn(26, pwr, 5.3),
            new PointNTurn(-90, pwr, 5.7),
            new PointNTurn(0, pwr, 10.3)
         };
        return traj;
    }

    public static AutoFunction[] getBPathA(double pwr) {
        AutoFunction[] traj = { 
            new PointNTurn(22, pwr, 7.5),
            new PointNTurn(22, 50, 3.5),
            new PointNTurn(-70, pwr, 5), //71
            new PointNTurn(-70, 50, 2.9),
            new PointNTurn(27, pwr, 4),
            new PointNTurn(27, 50, 7)
        };
        return traj;
    }

    public static AutoFunction[] getRPathB(double pwr) {
        AutoFunction[] traj = {
            new PointNTurn(-33, 65, 3.5),
            new PointNTurn(40, pwr, 7),
            //new PointNTurn(40, pwr, 1.6),
            new PointNTurn(-57, pwr, 7.1),
           // new PointNTurn(-57, pwr, 2),
            new PointNTurn(-14, pwr, 8.5)
         };
        return traj;
    }

    public static AutoFunction[] getBPathB(double pwr) {
        AutoFunction[] traj = {
            new PointNTurn(11, pwr, 11),
            new PointNTurn(-60, pwr, 5),
            new PointNTurn(40, pwr, 4),
            new PointNTurn(20, pwr, 4)
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

    public static AutoFunction[] getGalaxtic(double fwd) {
        switch (AutoSelector.galacticShooter()) {
            case 1:
                return getRPathA(70);
            case 2:
                return getBPathA(70);
            case 3:
                return getRPathB(70);
            case 4:
                return getRPathB(70);
            default:
                System.out.println("Bad Galaxtic path - " + AutoSelector.galacticShooter());
                return getEmpty(0);
        }
    }

}
