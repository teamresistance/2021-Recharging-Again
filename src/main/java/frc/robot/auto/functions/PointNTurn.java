package frc.robot.auto.functions;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import org.opencv.core.Point;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.robot.Subsystem.drive.Steer;
import frc.util.PropMath;

public class PointNTurn extends AutoFunction {

    // Hardware
    private WPI_TalonSRX right = IO.drvMasterTSRX_R; // right motor
    private WPI_TalonSRX left = IO.drvMasterTSRX_L; // left motor
    private WPI_VictorSPX rightSlave = IO.drvFollowerVSPX_R;
    private WPI_VictorSPX leftSlave = IO.drvFollowerVSPX_L;
    private DifferentialDrive diffDrv = new DifferentialDrive(left, right);
    private double distTPF_L = IO.drvMasterTPF_L; // Left Ticks per Foot
    private double distTPF_R = -IO.drvMasterTPF_R; // Right Ticks per Foot

    private double enc_L;
    private double enc_R;
    private double dist_L;
    private double dist_R;
    private double dist_Avg;

    // General
    private int state;
    private int prvState;
    private double strCmd[] = { 0.0, 0.0 }; // Cmds returned, X, Y
    // Heading Control
    private double hdgFB = 0.0; // Gyro reading
    private double hdgOut = 0.0; // X (Hdg) output
    // Distance Control
    private double distFB = 0.0; // Dist reading
    private double distOut = 0.0; // Y (Fwd) cmd

    /* [0][]=hdg [1][]=dist SP, PB, DB, Mn, Mx, Xcl */
    private double[][] parms = { { 0.0, -130.0, 3.0, 0.4, 1.0, 0.20 },
            /*                       */ { 0.0, 5.5, 0.5, 0.10, 1.0, 0.07 } };

    private Steer steer; // Used to steer to a hdg with power for distance

    private boolean finished = false;
    private double hdg = 0.0;
    private double pwr = 0.0;
    private double dist = 0.0;
    private double traj[] = {};


    public PointNTurn(double eHdg, double ePwr, double eDist) {
        hdg = eHdg;
        pwr = ePwr;
        dist = eDist;
    }

    public void init() {
        finished = false;
        IO.navX.reset();
        left.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
        right.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
        left.set(0);
        right.set(0);
        state = -1;
        prvState = 0;
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
        switch (state) {
            case -1:
                prvState = state;
                state++;
                break;
            case 0: // Init Trajectory, turn to hdg then (1) ...
                if (prvState != state) {
                    steer.steerTo(hdg,pwr,dist);
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
                }
                state++;
                break;
            case 3:
                done();
                break;
        }
    }

    public void done() {
        finished = true;
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

    private double calcDist() {
        enc_L = left.getSelectedSensorPosition();
        enc_R = right.getSelectedSensorPosition();
        dist_L = enc_L / distTPF_L;
        dist_R = enc_R / distTPF_R;
        dist_Avg = (dist_L + dist_R) / 2.0;
        return dist_Avg;
    }

    private void resetDist() {
        left.setSelectedSensorPosition(0, 0, 0);
        right.setSelectedSensorPosition(0, 0, 0);
    }
    
    private void sdbInit() {
        SmartDashboard.putNumber("PnT Step", state);

        SmartDashboard.putNumber("Hdg Out", hdgOut);
        SmartDashboard.putNumber("Dist Out", distOut);
        SmartDashboard.putNumber("DistM L", distTPF_L);
        SmartDashboard.putNumber("DistM R", distTPF_R);
    }

    private void sdbUpdate() {
        SmartDashboard.putNumber("Auto Step", state); // Set by JS btns

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
    }
}
