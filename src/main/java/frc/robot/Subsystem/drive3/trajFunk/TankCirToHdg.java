package frc.robot.Subsystem.drive3.trajFunk;

import frc.io.hdw_io.IO;
import frc.robot.Subsystem.drive3.Drive;
import frc.robot.Subsystem.drive3.Steer;

public class TankCirToHdg extends ATrajFunction {

    private double[] ctrXY = {0.0, 0.0};
    private double radiusSP = 0.0;
    private double lCmd = 0.0;
    private double rCmd = 0.0;
    private double hdgSP = 0.0;
    private double[] tCmd = new double[2];

    // public static Steer steer = Drive.steer;  // Used to steer to a hdg with power for distance
    /*                [0][]=hdg [1][]=dist SP, PB, DB, Mn, Mx, Xcl */
    private static double[][] parms = { { 0.0, -45.0, 5.0, 0.55, 1.0, 1.0 },
    /*                               */ { 0.0, 1.0, 0.0, -0.3, 0.3, 1.0 } };   //Dist applied to radius

    public TankCirToHdg(double _ctrX, double _ctrY, double _radiusSP,
                         double _hdgSP, double _lCmdBase, double _rCmdBase) {
        ctrXY[0] = _ctrX;
        ctrXY[1] = _ctrY;
        radiusSP = _radiusSP;
        hdgSP = _hdgSP;
        lCmd = _lCmdBase;
        rCmd = _rCmdBase;
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
                //Figure out which is the inside wheel and offset.
                if(lCmd < rCmd){        //If left is inside wheel
                    tCmd[0] += lCmd;    //add to left base cmd
                    tCmd[1] *= rCmd;    //multi right base cmd
                }else{                  //else
                    tCmd[1] += rCmd;    //add to right base cmd
                    tCmd[0] *= lCmd;    //multi left base cmd
                }
                System.out.println("leftCmd,C0: " + tCmd[0] + "\trightCmd,C1: " + tCmd[1]);
                Drive.cmdUpdate(tCmd[0], tCmd[1], true, 1); //Calls steerTo & cmdUpdate for hdg & dist.
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
