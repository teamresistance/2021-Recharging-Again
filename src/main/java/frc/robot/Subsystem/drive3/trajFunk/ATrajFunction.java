package frc.robot.Subsystem.drive3.trajFunk;

import frc.robot.Subsystem.drive3.Drive;
import frc.robot.Subsystem.drive3.Drv_Auto2;
import frc.robot.Subsystem.drive3.Steer;

public abstract class ATrajFunction extends Drv_Auto2 {

    public static int state = 0;
    public static boolean finished = false;
    public static Steer steer = Drive.steer;  // Used to steer to a hdg with power for distance

    public static void initTraj() {
        state = 0;
        finished = false;
    }

    public void execute() {

    }

    public static void done() {
        finished = true;
        // Drive.cmdUpdate();
    }

    public static boolean finished() {
        return finished;
    }

    // private void sdbInit2() {
        
    // }

    // private void sdbUpdate2() {

    // }
}
