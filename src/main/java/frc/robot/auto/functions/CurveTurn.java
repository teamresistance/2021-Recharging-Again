package frc.robot.auto.functions;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import frc.util.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.io.hdw_io.IO;

public class CurveTurn extends AutoFunction {

    private WPI_TalonSRX right = IO.drvMasterTSRX_R; // right motor
    private WPI_TalonSRX left = IO.drvMasterTSRX_L; // left motor
    private WPI_VictorSPX rightSlave = IO.drvFollowerVSPX_R;
    private WPI_VictorSPX leftSlave = IO.drvFollowerVSPX_L;
    private DifferentialDrive diffDrv = new DifferentialDrive(left, right);

    private int state;
    private int prvState;

    private boolean finished = false;
    private double pwr = 0;
    private double rot = 0;
    private double time = 0;
    private Timer curveTime;

    public CurveTurn(double ePWR, double eROT, double eTime) {
        pwr = ePWR;
        rot = eROT;
        time = eTime;
        curveTime = new Timer(eTime);
    }

    public void init() {
        finished = false;
        left.set(0);
        right.set(0);
        state = -1;
        prvState = 0;
    }

    public void execute() {
        switch (state) {
            case -1:
                prvState = state;
                state++;
                break;
            case 0:
                diffDrv.curvatureDrive(pwr, rot, false);
                if (curveTime.hasExpired(time, state)) {
                    state++;
                }
                break;
            case 1:
                done();
                break;
        }
    }

    public void done() {
        finished = true;
        diffDrv.tankDrive(0, 0);
        left.set(ControlMode.Disabled, 0);
        right.set(ControlMode.Disabled, 0;);
    }

    public void update() {
        //sdbUpdate();
        leftSlave.follow(left);
        rightSlave.follow(right);
    }
    
}
