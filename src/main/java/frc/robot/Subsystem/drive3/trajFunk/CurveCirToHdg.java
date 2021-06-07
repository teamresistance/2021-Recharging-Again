package frc.robot.Subsystem.drive3.trajFunk;

import frc.io.hdw_io.IO;
import frc.robot.Subsystem.drive3.Drive;
import frc.robot.Subsystem.drive3.Steer;

public class CurveCirToHdg extends ATrajFunction {

    private double[] ctrXY = {0.0, 0.0};
    private double radiusSP = 0.0;
    private double fwdCmd = 0.0;
    private double rotCmd = 0.0;
    private double hdgSP = 0.0;
    private double[] tCmd = new double[2];

    // public static Steer steer = Drive.steer;  // Used to steer to a hdg with power for distance
    /*                [0][]=hdg [1][]=dist SP, PB, DB, Mn, Mx, Xcl */
    private static double[][] parms = { { 0.0, -45.0, 5.0, 0.55, 1.0, 1.0 },
    /*                               */ { 0.0, 1.0, 0.0, 0.8, 1.2, 1.0 } };   //Dist applied to radius

    public CurveCirToHdg(double _ctrX, double _ctrY, double _radiusSP,
                         double _hdgSP, double _fwdCmdBase, double _rotCmdBase) {
        ctrXY[0] = _ctrX;
        ctrXY[1] = _ctrY;
        radiusSP = _radiusSP;
        hdgSP = _hdgSP;
        fwdCmd = _fwdCmdBase;
        rotCmd = _rotCmdBase;
    }

    /*Trying something different.
    Using Steer hdg to adjust passed, base, curvature speed, 
    gets closer slows down a little and check for done hdg.
    This is appliedto fwd cmd[1].
    Also use Steer dist to adjust passed, base, rotation
    to maintain radius.  Rot passed "should" be < 0.9 to 
    allow adjust to tighter curve if needed.
    This is applied to rot cmd[0].
    Both are inverted from what we normally get from steerTo.
    */
    public void execute() {
        switch (state) {
            case 0:
                tSteer = new Steer(parms);
                // System.out.println("hdgPB: " + tSteer.getHdgPB() +
                //                     "\thdgMn: " +  tSteer.getHdgMn() +
                //                     "\thdgMx: " +  tSteer.getHdgMx());
                // System.out.println("distPB: " + tSteer.getDistPB()+
                //                     "\tdistMn: " +  tSteer.getDistMn() +
                //                     "\tdistMx: " +  tSteer.getDistMx());
                tSteer.steerTo(hdgSP, 1.0, radiusSP);
                state++;
            case 1:
                tCmd = tSteer.update(hdgFB(), radiusFB());
                System.out.println("rFB: " + radiusFB() + "\trSP: " + radiusSP);
                System.out.println("rot,C0: " + tCmd[0] + "\tfwd,C1: " + tCmd[1]);
                tCmd[1] *= rotCmd;
                tCmd[0] *= fwdCmd;
                System.out.println("fwdCmd,C0: " + tCmd[0] + "\trotCmd,C1: " + tCmd[1]);
                Drive.cmdUpdate(tCmd[1], tCmd[0], true, 3); //Calls steerTo & cmdUpdate for hdg & dist.
                if (tSteer.isHdgDone()) state++;    // Chk hdg only
                break;
            case 2:
                done();
                break;
        }
    }

    private double radiusFB(){
        strCmd[0] = ctrXY[0] - IO.getCoorX();
        strCmd[1] = ctrXY[1] - IO.getCoorY();
        return Math.sqrt(strCmd[0]*strCmd[0] + strCmd[1] * strCmd[1]);
    }
}
