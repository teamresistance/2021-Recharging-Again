package frc.robot.Subsystem.drive.trajFunk;

import frc.robot.Subsystem.drive.Drive;
import frc.util.Timer;

public class CurveTurn extends ATrajFunction {

    private double pwrMx = 0;
    private double rot = 0;
    private double time = 0;
    private Timer curveTime;

    public CurveTurn(double ePWR, double eROT, double eTime) {
        pwrMx = ePWR;
        rot = eROT;
        time = eTime;
        curveTime = new Timer(eTime);
    }

    public void execute() {
        switch (state) {
            case 0:
                state++;
                break;
            case 1:
                // diffDrv.curvatureDrive(pwr, rot, false);
                Drive.cmdUpdate(pwrMx, rot, true, 3);
                if (curveTime.hasExpired(time, state)) {
                    state++;
                }
                break;
            case 2:
                done();
                break;
        }
    }
}
