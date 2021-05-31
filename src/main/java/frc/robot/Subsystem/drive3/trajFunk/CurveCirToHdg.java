package frc.robot.Subsystem.drive3.trajFunk;

import frc.io.hdw_io.IO;
import frc.robot.Subsystem.drive3.Drive;
import frc.util.PropMath;
import frc.util.Timer;

public class CurveCirToHdg extends ATrajFunction {

    private double[] ctrXY = {0.0, 0.0};
    private double radiusSP = 0.0;
    private double fwd = 0.0;
    private double rot = 0.0;
    private double hdg = 0.0;

    private PropMath radiusProp = new PropMath(0.0, 1.0, 0.0, 0.9, 1.1);

    public CurveCirToHdg(double[] _ctrXY, double _radius, double _hdg, double _fwd, double _rot) {
        ctrXY = _ctrXY;
        radiusSP = _radius;
        hdg = _hdg;
        fwd = _fwd;
        rot = _rot;
    }

    public void execute() {
        switch (state) {
            case 0:
                state++;
                break;
            case 1:
                calcCurve();
                Drive.cmdUpdate(strCmd[1], strCmd[0], true, 3); //Calls steerTo & cmdUpdate for hdg & dist.
                break;
            case 2:
                done();
                break;
        }
    }

    private void calcCurve(){
        strCmd[0] = ctrXY[0] - IO.getCoorX();
        strCmd[1] = ctrXY[1] - IO.getCoorY();
        double radius = Math.sqrt(strCmd[0]*strCmd[0] + strCmd[1] * strCmd[1]);
        double comp = radiusProp.calcProp((radius - radiusSP), false);
        strCmd[0] = fwd * comp;
        strCmd[1] = rot;
    }
}
