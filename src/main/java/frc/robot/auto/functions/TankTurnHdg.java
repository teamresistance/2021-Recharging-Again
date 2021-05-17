package frc.robot.auto.functions;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import org.opencv.core.Point;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.Encoder;
import frc.io.hdw_io.IO;
import frc.robot.Subsystem.drive.Steer;
import frc.util.PropMath;

public class TankTurnHdg extends AutoFunction {

    // // Hardware
    // private static DifferentialDrive diffDrv = IO.diffDrv_M;
    // private static Encoder encL = IO.drvEnc_L;
    // private static Encoder encR = IO.drvEnc_R;

    // General
    private int state;
    private int prvState;
    private double strCmd[] = { 0.0, 0.0 }; // Cmds returned, X, Y
    // Heading Control
    private double hdgOut = 0.0; // X (Hdg) output
    // Distance Control
    private double distOut = 0.0; // Y (Fwd) cmd

    // /*.........[0][]=hdg [1][]=dist SP, PB, DB, Mn, Mx, Xcl */
    // private double[][] parms = { { 0.0, -110.0, 6.5, 0.7, 1.0, 0.40 },
    //         /*                       */ { 0.0, 4.0, 0.3, 0.25, 1.0, 0.07 } };

    // private Steer steer; // Used to steer to a hdg with power for distance

    private boolean finished = false;
    private double hdg = 0.0;
    private double lPwr = 0.0;
    private double rPwr = 0.0;
    private double traj[] = {};

    // dont use negative power
    public TankTurnHdg(double _hdg, double _lPwr, double _rPwr) {
        hdg = _hdg;
        lPwr = _lPwr;
        rPwr = _rPwr;
    }

    public void init() {
        // IO.drvMasterTSRX_L.set(ControlMode.Disabled, 0);
        // IO.drvMasterTSRX_R.set(ControlMode.Disabled, 0);
        finished = false;
        resetDist();
        diffDrv.tankDrive(0, 0);
        state = -1;
        prvState = 0;
        finished = false;
        // steer = new Steer(parms);
        hdgOut = 0.0;
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
                    // steer.steerTo(hdg, pwr, dist);
                    resetDist();
                } else {
                    // Calc heading & dist output. rotation X, speed Y
                    strCmd = steer.update(hdgFB(), distFB());
                    hdgOut = strCmd[0]; // Get hdg output, Y
                    distOut = 0.0; // Get distance output, X
                    // Apply as a arcade joystick input
                    // hdgOut = BotMath.SegLine(hdgOut, xOutAr); //Compensate for poor turning.
                    diffDrv.tankDrive(lPwr, rPwr, false);

                    // Chk if trajectory is done
                    System.out.println("HdgOut0B: " + hdgOut + "  hdg: " + hdgFB() + "  hdgSP: " + hdg);
                    if(Math.abs(hdgFB() - hdg) < 5.0 ) {       //This is a kludge to get things working
                    // if (steer.isHdgDone()) {
                        state = 1; // Chk hdg only
                        resetDist();
                    }
                }
                prvState = state;
                break;
            case 1: // steer Auto Heading and Dist
                // // Calc heading & dist output. rotation X, speed Y
                // strCmd = steer.update(hdgFB(), distFB());
                // hdgOut = strCmd[0];
                // distOut = strCmd[1];
                // // Apply as a arcade joystick input
                // // hdgOut = BotMath.SegLine(hdgOut, xOutAr); //Compensate for poor turning.
                // diffDrv.arcadeDrive(-distOut, hdgOut, false);

                // // Chk if trajectory is done
                // if (steer.isDistDone()) {
                //     state = 2; // Chk distance only
                // }
                state = 2;
                prvState = state;
                break;
            case 2: // Increment Auto Index & chk for done all traj.
                diffDrv.tankDrive(0.0, 0.0);
                if (prvState != state) {
                    prvState = state; // Let other states see change of state, COS
                }
                state++;
                System.out.println("HdgOut2A: " + hdgOut);
                break;
            case 3:
                done();
                System.out.println("HdgOut3A: " + hdgOut);
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
        // IO.follow();
    }

    private void sdbInit() {
        SmartDashboard.putNumber("AF/TTH/Step", state);

        SmartDashboard.putNumber("AF/Hdg Out", hdgOut);
        SmartDashboard.putNumber("AF/Dist Out", distOut);
    }

    private void sdbUpdate() {
        SmartDashboard.putNumber("AF/Dist FB", distFB());
        SmartDashboard.putNumber("AF/Dist Out", distOut);
        SmartDashboard.putNumber("AF/Hdg FB", hdgFB());
        SmartDashboard.putNumber("AF/Hdg Out", hdgOut);
        SmartDashboard.putNumber("AF/TTH/Step", state); // Set by JS btns
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
