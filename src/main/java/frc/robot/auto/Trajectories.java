package frc.robot.auto;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.vision.RPI;
import frc.robot.auto.functions.AutoFunction;
import frc.robot.auto.functions.CurveTurn;
import frc.robot.auto.functions.PointNTurn;
import frc.robot.auto.functions.TankTurnHdg;

//TODO: still unsure on how to take the 2D array and use it as a trajectory object
public class Trajectories {
    private static SendableChooser<String> chsr = new SendableChooser<String>();
    private static String[] chsrDesc = {
        "getEmpty", "getSlalom", "getBarrel", "getBounce", "getSquare",
        "getPathRedA", "getPathBluA", "getPathRedB", "getPathBluB", "getPAthBlue", "getPathGalaxtic",
        "getCurve1_1", "getCurve1_7", "getCurve1_5", 
        "getCurve7_1", "getCurve7_7", "getCurve7_5", 
        "getCurve5_1", "getCurve5_7", "getCurve5_5", "getCurveTry",
    };

    /**Initialize Traj chooser */
    public static void chsrInit(){
        for(int i = 0; i < chsrDesc.length; i++){
            chsr.addOption(chsrDesc[i], chsrDesc[i]);
        }
        chsr.setDefaultOption(chsrDesc[0] + " (Default)", chsrDesc[0]);   //Default MUST have a different name
        SmartDashboard.putData("Traj/Choice", chsr);
    }

    /**Show on sdb traj chooser info.  Called from robotPeriodic  */
    public static void chsrUpdate(){
        SmartDashboard.putString("Traj/String", chsr.getSelected());
        SmartDashboard.putNumber("Traj/Gxtc Num", RPI.galacticShooter());
    }

    /**
     * 
     * @param pwr - default pwr to be usedin trajectories
     * @return The active, selected, Chooser Trajectory for use by AutoSelector
     */
    public static AutoFunction[] getTraj(double pwr){
        switch(chsr.getSelected()){
            case "getEmpty":
            return getEmpty(pwr);
            case "getSlalom":
            return getSlalom(pwr);
            case "getBarrel":
            return getBarrel(pwr);
            case "getBounce":
            return getBounce(pwr);
            case "getSquare":
            return getSquare(pwr);
            case "getPathRedA":
            return getPathRedA(pwr);
            case "getPathBluA":
            return getPathBluA(pwr);
            case "getPathRedB":
            return getPathRedB(pwr);
            case "getPathBluB":
            return getPathBluB(pwr);
            case "getPathBlue":
            return getPathBlue(pwr);
            case "getPathGalaxtic":
            return getPathGalaxtic(pwr);
            case "getCurve1_1":
            return getCurve1_1(pwr);
            case "getCurve1_7":
            return getCurve1_7(pwr);
            case "getCurve1_5":
            return getCurve1_5(pwr);
            case "getCurve7_1":
            return getCurve7_1(pwr);
            case "getCurve7_7":
            return getCurve7_7(pwr);
            case "getCurve7_5":
            return getCurve7_5(pwr);
            case "getCurve5_1":
            return getCurve5_1(pwr);
            case "getCurve5_7":
            return getCurve5_7(pwr);
            case "getCurve5_5":
            return getCurve5_5(pwr);
            case "getCurveTry":
            return getCurveTry(pwr);
            default:
            System.out.println("Traj/Bad Traj Desc - " + chsr.getSelected());
            return getEmpty(0);
        }
    }

    public static String getChsrDesc(){
        return chsr.getSelected();
    }

    //------------------ Trajectories -------------------------------
    // each trajectory/path/automode is stored in each method
    // name each method by the path its doing

    public static AutoFunction[] getEmpty(double pwr) {
        AutoFunction[] traj = { new PointNTurn(0, pwr, 0) };
        return traj;
    }

    // public static AutoFunction[] getSlalom(double pwr) {
    //     AutoFunction traj[] = {
    //         new PointNTurn(0, pwr, 2.5), 
    //         new PointNTurn(-55, pwr, 5.17), 
    //         new PointNTurn(0, pwr, 9.2),
    //         new PointNTurn(55, pwr, 4.57),
    //         new PointNTurn(0, pwr, 2),
    //         new PointNTurn(90, pwr, -4.3),
    //         new PointNTurn(180, pwr, 1.5),
    //         new PointNTurn(150, pwr, 6.14),
    //         new PointNTurn(180, pwr, 9.2), 
    //         new PointNTurn(-125, pwr, 5.57),
    //         new PointNTurn(180, pwr, 2.5)
    //     };
    //     return traj;
    // }

    
    public static AutoFunction[] getSlalom(double pwr) {
        AutoFunction traj[] = {
            new PointNTurn(0, pwr, 2.5), 
            new PointNTurn(-55, pwr, 5.17), 
            new PointNTurn(0, pwr, 8.5),
            new PointNTurn(57, pwr, 3.2),
            new TankTurnHdg(135, 0, .80),
            // new PointNTurn(0, pwr, 2), // out
            // new PointNTurn(90, pwr, -4.3), // out
            // new PointNTurn(180, pwr, 1.5),
            new PointNTurn(132
            
            
            
            
            
            
            
            
            
            , pwr, 5),
            new PointNTurn(180, pwr, 8.5), 
            new PointNTurn(-125, pwr, 4.57),
            new PointNTurn(180, pwr, 2)
        };
        return traj;
    }

    // public static AutoFunction[] getBarrel(double pwr){
    //     AutoFunction[] traj = {
    //         new PointNTurn(0,100,5.9),
    //         new TankTurnHdg(-10, 0.68, 0.0),    //Turn to the right
    //         // new PointNTurn(45,100,2.5),
    //         // new PointNTurn(135,100, 2.5),
    //         // new PointNTurn(-180, 100, 2.0), //
    //         // new PointNTurn(-135, 100, 2.5), //
    //         // new PointNTurn(-90, 100, 2.0), //
    //         // new PointNTurn(-45,100, 2.5), //fix these two angles bc its going out of bounds
    //         new PointNTurn(-10,100, 5.8),
    //         new TankTurnHdg(80, 0.0, 0.78),  //Turn to the left
    //         // new PointNTurn(-45, 100, 2.5),
    //         // new PointNTurn(-135, 100, 2.5),
    //         // new PointNTurn(135, 100, 4.1),
    //         // new PointNTurn(45, pwr, 9.7),
    //         // new PointNTurn(45,100, 6.1),  //new
    //         new TankTurnHdg(10, 0.4, 1.0),
    //         new TankTurnHdg(180, 0.0, 0.8),  //Turn to the left
    //         // new PointNTurn(-45, pwr, 3.7),
    //         // new PointNTurn(-135, pwr, 3.54),
    //         // new PointNTurn(177, pwr, 20)
    //     };
    //     return traj;
    // }

    public static AutoFunction[] getBarrel(double pwr) {
        AutoFunction[] traj = {
            new PointNTurn(0,100,5.7),
            new TankTurnHdg(-10, .75, 0.1),    //Turn to the right
            new PointNTurn(0,100, 5),
            new TankTurnHdg(40, .1, .85),
            new PointNTurn(44, 100, 4.4),
            new TankTurnHdg(-165, .1, .9),
            new PointNTurn(180, 100, 18)
            // new TankTurnHdg(90, 0.1, 0.85),  //Turn to the left
            // new TankTurnHdg(25, 0.5, 1.0),
            // new TankTurnHdg(180, 0.1, 0.85),  //Turn to the left
        };
        return traj;
    }

    public static AutoFunction[] getBounce(double pwr) {
        AutoFunction[] traj = {
            new PointNTurn(0, pwr, 3), //2.8
<<<<<<< HEAD
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
=======
            new PointNTurn(270, pwr, 3.4), //3.2
            new PointNTurn(270, pwr, -2.5),
            new PointNTurn(230, pwr, -5.5), //6.3
            new PointNTurn(315, pwr, 1.8), 
            new PointNTurn(270, pwr, 7.3), //6.8
            new PointNTurn(270, pwr, -6.2), //6.8
            new PointNTurn(0, pwr, 5.7), //6
            new PointNTurn(270, pwr, 8.2),
            new PointNTurn(270, pwr, -1.8), //2.4
            new PointNTurn(-10, pwr, 4)
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
>>>>>>> origin/revolverupdate
        return traj;
    }

    //-------------- Galaxtic Search ------------------------
    //------------- Path in name open Snorfler --------------
    public static AutoFunction[] getPathRedA(double pwr) {
        AutoFunction[] traj = {
            new PointNTurn(0, pwr, 3),
            new PointNTurn(25, pwr, 5.3),
            new PointNTurn(-85, pwr, 6.2),
            new PointNTurn(0, pwr, 10.3)
         };
        return traj;
    }

    public static AutoFunction[] getPathBluA(double pwr) {
        AutoFunction[] traj = { 
            new PointNTurn(22, pwr, 9.3),
            new PointNTurn(-70, pwr, 6.5),
            new PointNTurn(27, pwr, 4),
            new PointNTurn(10, pwr, 4)
        };
        return traj;
    }

    public static AutoFunction[] getPathRedB(double pwr) {
        AutoFunction[] traj = {
            new PointNTurn(-33, 65, 3.5),
            new PointNTurn(40, pwr, 7),
            new PointNTurn(-60, pwr, 7.1),
            new PointNTurn(-10, pwr, 6.5)
         };
        return traj;
    }

    public static AutoFunction[] getPathBluB(double pwr) {
        AutoFunction[] traj = {
            new PointNTurn(11, pwr, 11),
            new PointNTurn(-60, pwr, 5),
            new PointNTurn(40, pwr, 4),
            new PointNTurn(20, pwr, 4)
         };
        return traj;
    }

    public static AutoFunction[] getPathBlue(double pwr) {
        AutoFunction[] traj = {
            new PointNTurn(21, pwr, 9.5),  //Move to BlueA ball1
            new PointNTurn(-72, pwr, 8.7),  //Move to B6 thru BlueB ball1
            new PointNTurn(48, pwr, 10.0),   //Move to BlueAB ball3
         };
        return traj;
    }

    /**
     * Establishes the Trajectory array from the Raspberry Pi
     * @param pwr - applied default power to turns and runs
     * @return the Trajectoy array for the path assigned by the Raspberry Pi
     */
    public static AutoFunction[] getPathGalaxtic(double pwr) {
        switch (RPI.galacticShooter()) {
            case 1:
                return getPathRedA(pwr);
            case 2:
            return getPathBlue(pwr);   //inside Blue
            // return getPathBluB(70);   //inside Blue
            case 3:
                return getPathRedB(pwr);
            case 4:
                return getPathBlue(pwr);   //outside Blue
                // return getPathBluA(70);   //outside Blue
            default:
                System.out.println("Bad Galaxtic path - " + RPI.galacticShooter());
                return getEmpty(0);
        }
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

}
