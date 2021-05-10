package frc.robot.Subsystem.drive3;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Subsystem.drive3.trajFunk.*;

public class Drv_Auto2 extends Drive {

    private static ATrajFunction[] traj;
    private static boolean overallFin = false;
    private static int autoStep = 0;
    private static int idx = 0;
    private static int x = 0;

    //Constructor.  Called with the path array
    public Drv_Auto2() {
    }

    /**Get the active Trajectories and initialize indexs.
     * <p>Reset Heading & Distance to 0.
     */
    public static void init() {
        traj = Trajectories.getTraj(0.9);
        // disable();
        // for (AutoFunction af : traj) {
        //     af.init();
        // }
        idx = 0;
        x = 0;
        overallFin = false;
        hdgRst();
        distRst();
    } 

    //OK, WTF does this do? 
    public static void update() {
        switch (autoStep) {
            case 0:                 //Initialize the traj funk
                traj[idx].init();
                System.out.println("Made it here: Teleop Upd 0");
                autoStep++;
                break;
            case 1:                 //Run a leg of the path
                traj[idx].execute();
                System.out.println("Made it here: Teleop Upd 1");
                if (traj[idx].finished()) autoStep++;
                break;
            case 2:                 //Closeout Leg
                traj[idx].done();
                System.out.println("Made it here: Teleop Upd 2");
                autoStep++;
                idx++;
                autoStep = idx < traj.length ? 0 : autoStep++;
                break;
            case 3:                 //Done path
                setDone();          //Flag allFinished & Closeout all Legs (again?)
                System.out.println("Made it here: Teleop Upd 3");
                break;
        }
    }

    private static void runLeg(){
        switch (x) {
            case 0:
                traj[idx].execute();
                if (traj[idx].finished()) {
                    x++;
                }
                break;
            case 1:
                traj[idx].done();
                break;
        }
    }

    private static void setDone() {
        overallFin = true;
        for (ATrajFunction at : traj) {      //Why do it again?
            at.done();
        }
    }

    public static boolean finished() {
        return overallFin;
    }

    public static void disable() {
        cmdUpdate();
    }

    public static void sdbInit() {
    }

    public static void sdbUpdate() {
        SmartDashboard.putNumber("DA3/Auto Step", autoStep);
        SmartDashboard.putNumber("DA3/Current Traj Idx", idx);
    }

}
