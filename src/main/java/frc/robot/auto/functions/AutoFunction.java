package frc.robot.auto.functions;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.io.hdw_io.IO;
import frc.robot.Subsystem.drive.Steer;
import frc.util.PropMath;
import frc.io.hdw_io.Encoder;

public abstract class AutoFunction {

    // Hardware
    public static DifferentialDrive diffDrv = IO.diffDrv_M;
    public static Encoder encL = IO.drvEnc_L;
    public static Encoder encR = IO.drvEnc_R;

    /*.........[0][]=hdg [1][]=dist SP, PB, DB, Mn, Mx, Xcl */
    public double[][] parms = { { 0.0, -110.0, 6.5, 0.7, 1.0, 0.40 },
            /*                       */ { 0.0, 4.0, 0.3, 0.25, 1.0, 0.07 } };

    public Steer steer = new Steer(parms);  // Used to steer to a hdg with power for distance

    private boolean finished;


    public void init() {
        IO.drvMasterTSRX_L.set(ControlMode.Disabled, 0);
        IO.drvMasterTSRX_R.set(ControlMode.Disabled, 0);
        sdbInit();
        finished = false;
    }

    public void execute() {

    }

    public void done() {
        finished = true;
    }

    public boolean finished() {
        return finished;
    }

    private void sdbInit() {
        
    }

    private void sdbUpdate() {

    }

    public static double distFB() {
        return (encL.feet() + encR.feet()) / 2.0;
    }

    public static double hdgFB() {
        return PropMath.normalizeTo180(IO.navX.getAngle());
    }

    public static void resetDist() {
        encL.reset();
        encR.reset();
    }

}
