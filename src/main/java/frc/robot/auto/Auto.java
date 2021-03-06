package frc.robot.auto;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.Encoder;
import frc.io.hdw_io.IO;
import frc.io.joysticks.JS_IO;
import frc.robot.Subsystem.drive.Steer;
import frc.util.PropMath;

public class Auto {

    // Hardware
    private static DifferentialDrive diffDrv = new DifferentialDrive(IO.drvMasterTSRX_L, IO.drvMasterTSRX_R);
    private static Encoder encL = IO.drvEnc_L;
    private static Encoder encR = IO.drvEnc_R;

    // General
    private static int state;
    private static int prvState;
    private static double strCmd[] = { 0.0, 0.0 }; // Cmds returned, X, Y
    // Heading Control
    private static double hdgOut = 0.0; // X (Hdg) output
    // Distance Control
    private static double distOut = 0.0; // Y (Fwd) cmd

    /* [0][]=hdg [1][]=dist SP, PB, DB, Mn, Mx, Xcl */
    private static double[][] parms = { { 0.0, -130.0, 3.0, 0.4, 1.0, 0.20 },
            /*                       */ { 0.0, 5.5, 0.5, 0.10, 1.0, 0.07 } };

    private static Steer steer; // Used to steer to a hdg with power for distance

    // Steer to heading at power for distance.
    private static int trajIdx; // strCmds Index

    private static double[][] path;
    private static boolean finished = false;

    public Auto(double[][] traj) {
        path = traj;
    }

    public void init() {
        sdbInit();
        IO.navX.reset();
        resetDist();
        diffDrv.tankDrive(0, 0);
        state = -1;
        prvState = 0;
        trajIdx = 0;
        finished = false;
        steer = new Steer(parms);
        hdgOut = 0.0;
        distOut = 0.0;
    }

    public void execute() {
        update();
        /*
         * This rotates to the heading then resets the dist. and starts running out to
         * the new distance SP.
         */
        switch (state) {
            case -1:
                prvState = state;
                state++;
                break;
            case 0: // Init Trajectory, turn to hdg then (1) ...
                if (prvState != state) {
                    steer.steerTo(path[trajIdx]);
                    resetDist();
                } else {
                    // Calc heading & dist output. rotation X, speed Y
                    strCmd = steer.update(hdgFB(), distFB());
                    hdgOut = strCmd[0]; // Get hdg output, Y
                    distOut = 0.0; // Get distance output, X
                    // Apply as a arcade joystick input
                    // hdgOut = BotMath.SegLine(hdgOut, xOutAr); //Compensate for poor turning.
                    diffDrv.arcadeDrive(-distOut, hdgOut, false);

                    // Chk if trajectory is done
                    if (steer.isHdgDone()) {
                        state = 1; // Chk hdg only
                        resetDist();
                    }
                }
                prvState = state;
                break;
            case 1: // steer Auto Heading and Dist
                // Calc heading & dist output. rotation X, speed Y
                strCmd = steer.update(hdgFB(), distFB());
                hdgOut = strCmd[0];
                distOut = strCmd[1];
                // Apply as a arcade joystick input
                // hdgOut = BotMath.SegLine(hdgOut, xOutAr); //Compensate for poor turning.
                diffDrv.arcadeDrive(-distOut, hdgOut, false);

                // Chk if trajectory is done
                if (steer.isDistDone()) {
                    state = 2; // Chk distance only
                }
                prvState = state;
                break;
            case 2: // Increment Auto Index & chk for done all traj.
                diffDrv.arcadeDrive(0.0, 0.0);
                if (prvState != state) {
                    prvState = state; // Let other states see change of state, COS
                } else {
                    trajIdx++;

                    if (trajIdx < path.length) {
                        state = 0;
                    } else { // Next Traj else finished
                        state = 3;
                        break;
                    }
                }
                break;
            case 3:
                done();
                break;
        }
    }

    public void done() {
        finished = true;
        diffDrv.tankDrive(0, 0);
    }

    public boolean finished() {
        return finished;
    }

    public void update() {
        sdbUpdate();
        IO.follow();
    }

    private void sdbInit() {
        SmartDashboard.putNumber("Auto Step", state);

        SmartDashboard.putNumber("Hdg Out", hdgOut);
        SmartDashboard.putNumber("Dist Out", distOut);
        SmartDashboard.putNumber("DistM L", encL.tpf());
        SmartDashboard.putNumber("DistM R", encR.tpf());
    }

    private void sdbUpdate() {
        SmartDashboard.putNumber("Auto Step", state); // Set by JS btns

        SmartDashboard.putNumber("Hdg FB", hdgFB());
        SmartDashboard.putNumber("Hdg Out", hdgOut);

        SmartDashboard.putNumber("Enc L", encL.ticks());
        SmartDashboard.putNumber("Enc R", encR.ticks());
        SmartDashboard.putNumber("Dist L", encL.feet());
        SmartDashboard.putNumber("Dist R", encR.feet());
        SmartDashboard.putNumber("Dist A", distFB());
        SmartDashboard.putNumber("Dist FB", distFB());
        SmartDashboard.putNumber("Dist Out", distOut);
        SmartDashboard.putNumber("Traj Idx", trajIdx);
    }

    private static double distFB() {
        return (encL.feet() + encR.feet()) / 2.0;
    }

    private static double hdgFB() {
        return PropMath.normalizeTo180(IO.navX.getAngle());
    }

    private static void resetDist() {
        encL.reset();
        encR.reset();
    }
}
