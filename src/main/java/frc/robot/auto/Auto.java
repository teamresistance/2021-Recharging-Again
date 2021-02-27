package frc.robot.auto;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.io.joysticks.JS_IO;
import frc.robot.Subsystem.drive.Steer;
import frc.util.PropMath;

public class Auto {

    // Hardware
    private static WPI_TalonSRX right = IO.drvMasterTSRX_R; // right motor
    private static WPI_TalonSRX left = IO.drvMasterTSRX_L; // left motor
    private static WPI_VictorSPX rightSlave = IO.drvFollowerVSPX_R;
    private static WPI_VictorSPX leftSlave = IO.drvFollowerVSPX_L;
    private static DifferentialDrive diffDrv = new DifferentialDrive(left, right);
    private static double distTPF_L = IO.drvMasterTPF_L; // Left Ticks per Foot
    private static double distTPF_R = -IO.drvMasterTPF_R; // Right Ticks per Foot

    private static double enc_L;
    private static double enc_R;
    private static double dist_L;
    private static double dist_R;
    private static double dist_Avg;

    // General
    private static int state;
    private static int prvState;
    private static double strCmd[] = { 0.0, 0.0 }; // Cmds returned, X, Y
    // Heading Control
    private static double hdgFB = 0.0; // Gyro reading
    private static double hdgOut = 0.0; // X (Hdg) output
    // Distance Control
    private static double distFB = 0.0; // Dist reading
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
        left.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
        right.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
        left.set(0);
        right.set(0);
        state = -1;
        prvState = 0;
        trajIdx = 0;
        enc_L = 0;
        enc_R = 0;
        dist_L = 0.0;
        dist_R = 0.0;
        dist_Avg = 0.0;
        finished = false;
        steer = new Steer(parms);
        hdgFB = 0.0;
        hdgOut = 0.0;
        distFB = 0.0;
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
                    strCmd = steer.update(hdgFB, dist_Avg);
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
                strCmd = steer.update(hdgFB, dist_Avg);
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
        left.set(ControlMode.PercentOutput, 0);
        right.set(ControlMode.PercentOutput, 0);
    }

    public boolean finished() {
        return finished;
    }

    public void update() {
        sdbUpdate();
        leftSlave.follow(left);
        rightSlave.follow(right);
        hdgFB = PropMath.normalizeTo180(IO.navX.getAngle());
        distFB = calcDist();
    }

    private void sdbInit() {
        SmartDashboard.putNumber("Auto Step", state);

        SmartDashboard.putNumber("Hdg Out", hdgOut);
        SmartDashboard.putNumber("Dist Out", distOut);
        SmartDashboard.putNumber("DistM L", distTPF_L);
        SmartDashboard.putNumber("DistM R", distTPF_R);
    }

    private void sdbUpdate() {
        SmartDashboard.putNumber("Auto Step", state); // Set by JS btns
        SmartDashboard.putNumber("JS Y", JS_IO.axRightDrive.get());// Set by JS R Y
        SmartDashboard.putNumber("JS X", JS_IO.axRightX.get());// Set by JS R X

        SmartDashboard.putNumber("Hdg FB", hdgFB);
        SmartDashboard.putNumber("Hdg Out", hdgOut);

        SmartDashboard.putNumber("Enc L", enc_L);
        SmartDashboard.putNumber("Enc R", enc_R);
        distTPF_L = SmartDashboard.getNumber("DistM L", distTPF_L);
        distTPF_R = SmartDashboard.getNumber("DistM R", distTPF_R);
        SmartDashboard.putNumber("Dist L", dist_L);
        SmartDashboard.putNumber("Dist R", dist_R);
        SmartDashboard.putNumber("Dist A", dist_Avg);
        SmartDashboard.putNumber("Dist FB", distFB);
        SmartDashboard.putNumber("Dist Out", distOut);
        SmartDashboard.putNumber("Traj Idx", trajIdx);
    }

    private static double calcDist() {
        enc_L = left.getSelectedSensorPosition();
        enc_R = right.getSelectedSensorPosition();
        dist_L = enc_L / distTPF_L;
        dist_R = enc_R / distTPF_R;
        dist_Avg = (dist_L + dist_R) / 2.0;
        return dist_Avg;
    }

    private static void resetDist() {
        left.setSelectedSensorPosition(0, 0, 0);
        right.setSelectedSensorPosition(0, 0, 0);
    }
}
