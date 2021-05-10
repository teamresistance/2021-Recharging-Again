package frc.robot.Subsystem.drive3.trajFunk;

import frc.robot.Subsystem.drive3.Drive;

/**
 * This AutoFunction turns to passed heading then moves passed distance.
 */
public class TurnNMove extends ATrajFunction {

    // General
    private int state;
    private int prvState;

    private boolean finished = false;
    private double hdg = 0.0;
    private double pwr = 0.0;
    private double dist = 0.0;

    // dont use negative power
    public TurnNMove(double eHdg, double ePwr, double eDist) {
        hdg = eHdg;
        pwr = ePwr;
        dist = eDist;
    }

    public void init() {
        finished = false;
        state = 0;
        prvState = -1;      //Initalize different from state for firstpass
        finished = false;
    }

    public void execute() {
        update();
        switch (state) {
            case 0: // Init Trajectory, turn to hdg then (1) ...
            System.out.println("Made it here: TNM Exec 0");
                steer.steerTo(hdg, pwr, dist);
                state++;
            case 1: // Turn to heading.  Do not move forward, yet.
            System.out.println("Made it here: TNM Exec 1");
                Drive.cmdUpdate(2);
                // Chk if trajectory is done
                if (steer.isHdgDone()) {
                    state++;    // Chk hdg only
                    Drive.distRst();
                }
                break;
            case 2: // Move forward, steer Auto Heading and Dist
            System.out.println("Made it here: TNM Exec 2");
                Drive.cmdUpdate(0);
                // Chk if trajectory is done
                if (steer.isDistDone()) state++; // Chk distance only
                break;
            case 3:
                done();
                break;
        }
    }

    public void done() {
        finished = true;
        Drive.cmdUpdate();
    }

    public boolean finished() {
        return finished;
    }

    public void update() {
        sdbUpdate();
    }

    private void sdbInit() {
        // SmartDashboard.putNumber("PnT Step", state);

        // SmartDashboard.putNumber("Hdg Out", hdgOut);
        // SmartDashboard.putNumber("Dist Out", distOut);
        // SmartDashboard.putNumber("DistM L", encL.tpf());
        // SmartDashboard.putNumber("DistM R", encR.tpf());
    }

    private void sdbUpdate() {
        // SmartDashboard.putNumber("Auto Step", state); // Set by JS btns

        // SmartDashboard.putNumber("Hdg FB", hdgFB());
        // SmartDashboard.putNumber("Hdg Out", hdgOut);

        // SmartDashboard.putNumber("Enc L", encL.ticks());
        // SmartDashboard.putNumber("Enc R", encR.ticks());
        // SmartDashboard.putNumber("Dist L", encL.feet());
        // SmartDashboard.putNumber("Dist R", encR.feet());
        // SmartDashboard.putNumber("Dist A", distFB());
        // SmartDashboard.putNumber("Dist FB", distFB());
        // SmartDashboard.putNumber("Dist Out", distOut);
    }

    // private static double distFB() {
    //     return (encL.feet() + encR.feet()) / 2.0;
    // }

    // private static double hdgFB() {
    //     return PropMath.normalizeTo180(IO.navX.getAngle());
    // }

    // private static void resetDist() {
    //     encL.reset();
    //     encR.reset();
    // }
}
