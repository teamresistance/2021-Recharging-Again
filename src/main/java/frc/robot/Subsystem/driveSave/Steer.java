package frc.robot.Subsystem.driveSave;
/*
Author: Team 86
History: 
jch - 2/2020 - Original Release

Desc.
Returns arcade drive commands in an array[X(hdg), Y(dist)]
to move bot along hdg at % speed for distance 
Both Hdg & dist Must be initialized with a SP, PB, DB, Mn, Mx, Xcl
A linear responce is calculated for each between SP & PB to Mn & Mx, respectfully.
Returns 0.0 for respective item when within DB.
Also limits acceleration, increase in out, to respective Xcl value.
*/

import frc.util.PropMath;

public class Steer {
    private double drvCmds[] = { 0.0, 0.0 }; // Cmds X, Y - Rot, FwdBkwd
    private int status = 0; // 0-Running, 1=On Hdg, 2=at Dist, 3=both
    private double hdgSP, hdgFB;
    private double hdgOutMn = 0.1;
    private double hdgOutMx = 1.0;
    private double hdgOut = 0.0;
    private double prvHdgOut = 0.0;
    private double hdgXclLmt = 1.0;

    private double distSP, distFB;
    private double distOutMn = 0.1;
    private double distOutMx = 1.0;
    private double pwrScalar = 100.0; // %Scale Mx
    private double distOut = 0.0;
    private double prvDistOut = 0.0;
    private double distXclLmt = 0.07;

    private PropMath hdgProp; // Calculate prop responce for hdg
    private PropMath distProp; // Calculate prop responce for dist

    // Constructor 0=hdg 1=dist / 0=SP, 1=PB, 2=DB, 3=Mn, 4=Mx, 5=Xcl
    public Steer(double[][] propParm) {
        hdgProp = new PropMath(propParm[0]);
        hdgXclLmt = propParm[0][5];
        distProp = new PropMath(propParm[1]);
        distXclLmt = propParm[1][5];
    }

    public Steer() {
        hdgProp = new PropMath(0.0, -150.0, 3.0, 0.1, 1.0);
        hdgXclLmt = 1.0;
        distProp = new PropMath(0.0, 5.0, 0.5, 0.1, 1.0);
        distXclLmt = 0.07;
    }

    // Start steering, set hdg, pwr & dist.
    public void steerTo(double _hdgSP, double _pwrSc, double _ftSP) {
        hdgSP = _hdgSP; // Heading
        pwrScalar = _pwrSc; // Scalar
        distSP = _ftSP; // Distance
        status = 0; // Status, 0=No, 1=Hdg in DB, 2Dist in DB, 3=Both in DB

        hdgProp.setSP(0.0); // Need to normalize err, so SP is 0.0 and pass norm error
        hdgProp.setOutMx((hdgOutMn + (hdgOutMx - hdgOutMn)) * pwrScalar / 100.0); // Pct of out control range
        distProp.setSP(distSP);
        distProp.setOutMx((distOutMn + (distOutMx - distOutMn)) * pwrScalar / 100.0);// Pct of out control range
    }

    // Start steering, set hdg, pwr & dist, by array.
    public void steerTo(double[] traj) {
        steerTo(traj[0], traj[1], traj[2]);
    }

    // return commands
    public double[] update(double _hdgFB, double _ftFB) {
        hdgFB = _hdgFB;
        distFB = _ftFB;
        drvCmds[0] = calcX(); // Rotation
        prvHdgOut = drvCmds[0];
        drvCmds[1] = calcY(); // Fwd/Bkwd
        prvDistOut = drvCmds[1];
        return drvCmds;
    }

    // return status. 0=Not complete, 1=hdg in DB, 2=dist in DB, 3= Both in DB
    public int getStatus() {
        return status;
    }

    // return true if hdg & dist are in DB
    public boolean isDone() {
        return (status & 3) > 0;
    }

    // return true if hdg is in DB
    public boolean isHdgDone() {
        return (status & 1) > 0;
    }

    // return true if dist is in DB
    public boolean isDistDone() {
        return (status & 2) > 0;
    }

    // Calc X, rotation
    private double calcX() {
        // Need to normalize err, so SP is 0.0 and pass norm'd error
        double err = PropMath.normalizeTo180(hdgFB - hdgSP);
        hdgOut = hdgProp.calcProp(err, false);

        if (hdgOut == 0.0) {
            status |= 1; // If in DB, set bit,
        } else {
            status &= ~1; // else clear
            hdgOut = accelLimiter(hdgOut, prvHdgOut, hdgXclLmt, hdgOutMn);
        }
        return hdgOut;
    }

    // Calc Y, distance
    private double calcY() {
        distOut = distProp.calcProp(distFB, false);
        if (distOut == 0.0) {
            status |= 2; // If in DB, set bit else clr
        } else {
            status &= ~2;
            distOut = accelLimiter(distOut, prvDistOut, distXclLmt, distOutMn);
        }
        return distOut;
    }

    // Limit acceleration
    private static double accelLimiter(double psntVal, double prvVal, double xclLmt, double outMn) {
        if (Math.abs(psntVal - prvVal) > xclLmt && psntVal != 0.0) {
            psntVal = psntVal < 0.0 ? Math.min(prvVal - xclLmt, -outMn) : // Value neg.
            /*                      */Math.max(prvVal + xclLmt, outMn); // else pos.
        }
        return psntVal;
    }
}