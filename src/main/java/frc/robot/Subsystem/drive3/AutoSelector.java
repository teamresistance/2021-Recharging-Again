// package frc.robot.Subsystem.drive;

// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// public class AutoSelector {

//     private static int selection;
//     //TODO: change name
//     private static Drv_Auto path = new Drv_Auto(null);

 

//     public static void init(int sel) {
//         switch (sel) {
//             case 1:
//                 path = new Drv_Auto(Trajectories.getCross(70.0));
//                 break;
//             case 2:
//                 path = new Drv_Auto(Trajectories.getSquare(70.0));
//                 break;
//             case 3:
//                 path = new Drv_Auto(Trajectories.getOtherCross(70.0));
//                 break;
//             default:
//                 path = new Drv_Auto(Trajectories.getEmpty(0));
//                 break;
//         }
//         SmartDashboard.putNumber("autoselector selection",selection);
//         path.init();

//         SmartDashboard.putBoolean("path initialized", true);
//         SmartDashboard.putBoolean("path executing", false);
//         SmartDashboard.putBoolean("path done", false);
//     }

//     public static void execute() {
        
//         SmartDashboard.putBoolean("path executing", true);
//         SmartDashboard.putBoolean("path done", false);
//         path.execute();
//     }

//     public static void done() {
//         SmartDashboard.putBoolean("path executing", false);
//         SmartDashboard.putBoolean("path done", true);
//         path.done();
//     }

//     public static boolean finished() {
//         return path.finished();
//     }
    
// }