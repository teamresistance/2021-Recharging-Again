package frc.robot.Subsystem.driveSave;
/*
Author: Team 86
History: 
jch - 2/2020 - Original Release
jch - 2/2021 - Mod to move hdg normalization to PropMath(..., k180)0
TODO2: - Need to test
Desc.
Returns arcade drive commands in an array[X(hdg), Y(dist)]
to move bot along hdg at % speed for distance 
Both Hdg & dist Must be initialized with a SP, PB, DB, Mn, Mx, Xcl
A linear responce is calculated for each between SP & PB to Mn & Mx, respectfully.
Returns 0.0 for respective item when within DB.
Also limits acceleration, increase in out, to respective Xcl value.
*/

import frc.util.PIDXController;

public class Steer2 {
    private double drvCmds[] = { 0.0, 0.0 }; // Cmds X, Y - Rot, FwdBkwd
    private int status = 0; // 0-Running, 1=On Hdg, 2=at Dist, 3=both in DB
    private double pwrScalar = 100.0; // %Scale output, apply to hdg & distance
    //Heading variables
    private PIDXController hdgPid;
    // private PropMath hdgProp;           // Instance of PropMath to calculate prop response for hdg
    private double hdgOut = 0.0;        //Calc X(rot) output
    private double prvHdgOut = 0.0;     //Previous hdgOut
    private double hdgXclLmt = 1.0;     //Limit change of hdgOut to this
    //Distance variables
    private PIDXController distPid;
    // private PropMath distProp;          // Instance of PropMath to calculate prop response for dist
    private double distOut = 0.0;       //Calc Y(dist) output
    private double prvDistOut = 0.0;    //Previous distOut
    private double distXclLmt = 0.07;   //Limit change of distOut to this

    /**
     * Constructor using a 2d array.  Creates a proportional object for hdg & dist.
     * >p>[0=hdg | 1=dist][0=PB, 1=IT, 2=DT, 3=SP, 4=DB, 5=Mn, 6=Mx, 7=FF, 8=Exp]
     * 
     * @param propParm - a 2d array of doubles[2][5]
     */
    public Steer2(double[][] propParm) {
        int pphCnt = propParm[0].length;
        int ppdCnt = propParm[1].length;
        if(pphCnt > 2 && ppdCnt > 2){
            hdgPid = new PIDXController(propParm[0][0], propParm[0][1],propParm[0][2]);
    
            if(pphCnt > 2) hdgPid.setSetpoint(propParm[0][3]);
            if(pphCnt > 3) hdgPid.setTolerance(propParm[0][4]);
            if(pphCnt > 4) hdgPid.setOutMn(propParm[0][5]);
            if(pphCnt > 5) hdgPid.setOutMx(propParm[0][6]);
            if(pphCnt > 6) hdgPid.setOutFF(propParm[0][7]);
            if(pphCnt > 7) hdgPid.setOutExp(propParm[0][8]);
    
            distPid = new PIDXController(propParm[1][0], propParm[1][1],propParm[1][2]);
            
            if(ppdCnt > 2) distPid.setSetpoint(propParm[1][3]);
            if(ppdCnt > 3) distPid.setTolerance(propParm[1][4]);
            if(ppdCnt > 4) distPid.setOutMn(propParm[1][5]);
            if(ppdCnt > 5) distPid.setOutMx(propParm[1][6]);
            if(ppdCnt > 6) distPid.setOutFF(propParm[1][7]);
            if(ppdCnt > 7) distPid.setOutFF(propParm[1][8]);
        }else{
            hdgPid = new PIDXController();
        }
        hdgPid.enableContinuousInput(-180.0, 180.0);
        hdgXclLmt = 1.0;
        distPid.disableContinuousInput();
        distXclLmt = 0.07;
        
    }

    /**
     * Constructor using defaults.  Creates a proportional object for hdg & dist:
     * <p>hdg => SP=0.0, PB=-150.0, DB=3.0, Mn=0.1, Mx=1.0, Xcl=1.0, k180=true
     * <p>dist => SP=0.0, PB=5.0, DB=0.5, Mn=0.1, Mx=1.0, Xcl=1.0, k180=false
     */
    public Steer2() {
        hdgPid = new PIDXController((1/90.0), 0.0, 0.0);                //Testing WPI PID
        hdgPid.enableContinuousInput(-180.0, 180.0);                //Testing continuous -180 to 180 degrees
        hdgXclLmt = 1.0;                                            //and Xcl

        distPid = new PIDXController((-1/5.0), 0.0, 0.0);                 //Testing WPI PID
        distXclLmt = 0.07;                                          //and Xcl
    }

    /**
     * Start steering, set hdg & dist.
     * <p>Call update after setting setpoints.
     * 
     * @param _hdgSP - heading setpoint
     * @param _distSP - distance setpoint in feet
     */
    public void steerTo(double _hdgSP, double _distSP, double _pwrMx) {
        status = 0;         // Status, 0=No, 1=Hdg in DB, 2=Dist in DB, 3=Both in DB
        setHdgSP(_hdgSP);
        setDistSP(_distSP);
        setHdgMx(_pwrMx);
        setDistMx(_pwrMx);
    }

    /**
     * Start steering, set hdg & dist.
     * <p>Call update after setting setpoints.
     * 
     * @param _hdgSP - heading setpoint
     * @param _distSP - distance setpoint in feet
     */
    public void steerTo(double _hdgSP, double _distSP) {
        status = 0;         // Status, 0=No, 1=Hdg in DB, 2=Dist in DB, 3=Both in DB
        setHdgSP(_hdgSP);
        setDistSP(_distSP);
    }

    /**
     * Start steering, set hdg, dist & pwr(opt) using double[2(3)] array.
     * <p>Call update after setting setpoints.
     * 
     * @param traj - double[2(3)] = { hdgSP, ftSP, pwr(opt) }
     */
    public void steerTo(double[] traj) {
        if(traj.length > 2){
            steerTo(traj[0], traj[1], traj[2]);
        }else{
            steerTo(traj[0], traj[1]);
        }
    }

    /**
     * Calculate arcade joystick cmds, X (rotation) & Y (fwd/bkwd move)
     * <p>Called after steerTo sets setpoints, hdgSP & distSP
     * 
     * @param _hdgFB heading feedback, gyro
     * @param _distFB distance traveled, encoders
     * @return double[] drvCmds = { X(rot), Y(fwd/bkwd) }
     */
    public double[] update(double _hdgFB, double _distFB) {
        drvCmds[0] = calcX(_hdgFB);       // Rotation
        prvHdgOut = drvCmds[0];     //Save for acceleration limiter
        drvCmds[1] = calcY(_distFB);       // Fwd/Bkwd
        prvDistOut = drvCmds[1];    //Save for acceleration limiter
        return drvCmds;
    }

    /**
     * Calculate arcade joystick cmds, X (rotation) & Y (fwd/bkwd move)
     * <p>Called after steerTo sets setpoints, hdgSP & distSP
     * 
     * @param _hdgFB heading feedback, gyro
     * @param _distFB distance traveled, encoders
     * @return double[] drvCmds = { X(rot), Y(fwd/bkwd) }
     */
    public double[] update() {
        return update(Drive.hdgFB(), Drive.distFB());
    }

    /**
     * @return  0=Not complete, 1=hdg in DB, 2=dist in DB, 3= Both in DB
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return  true if both rotation and distance are in thier respective deadbands.
     */
    public boolean isDone() {
        return (status & 3) == 3;
    }

    /**
     * @return  true if heading is within deadband
     */
    public boolean isHdgDone() {
        return (status & 1) > 0;
    }

    /**
     * @return  true if dist is in DB
     */
    public boolean isDistDone() {
        return (status & 2) > 0;
    }

    /**
     * Calculates the arcade JS X(rot) value.  Calcs err, Normalizes to +/-180 then calcs a proportional response
     * using the heading propband, hdgPB.
     * <p>Sets status bit 1 if within hdgDB, return out is 0.0 else bit is reset.
     * <p>If output is not 0.0, limit output change (acceleration). 
     * 
     * @param hdgFB heading feedback, gyro
     * @return  calc'ed arcade JS X(rot) value.
     */
    private double calcX(double hdgFB) {
        //???? Think this needsto behandled in diff drive
        // setHdgMx(Math.max(getHdgMn(), Math.min(1.0, pwrScalar)));   //Limit Mx btwn Mn & 1.0
        hdgOut = hdgPid.calculate(hdgFB);

        if (hdgPid.atSetpoint()) {
            status |= 1; // If in DB, set bit,
        } else {
            status &= ~1; // else clear
            // hdgOut = accelLimiter(hdgOut, prvHdgOut, hdgXclLmt, getHdgMn());    //Accel limiter
        }
        return hdgOut;
    }

    /**
     * Calculates the arcade JS Y(dist) value.  Passes feet FB to calc a proportional response
     * using the distance propband, distPB.
     * <p>Sets status bit 2 if within distDB, return out is 0.0 else bit is reset.
     * <p>If output is not 0.0, apply scaler & limit output change (acceleration). 
     * 
     * @param distFB distance feedback, encoders
     * @return  Calc'ed arcade JS Y(dist) value
     */
    private double calcY(double distFB) {
        //???? Think this needsto behandled in diff drive
        // setDistMx(Math.max(getDistMn(), Math.min(1.0, pwrScalar)));   //Limit Mx btwn Mn & 1.0
        distOut = distPid.calculate(distFB);

        if (distPid.atSetpoint()) {
            status |= 2; // If in DB, set bit else clr
        } else {
            status &= ~2;
            // distOut = accelLimiter(distOut, prvDistOut, distXclLmt, getDistMn());   //Accel limiter
        }
        return distOut;
    }

    /**
     * Limits the change in output to prevent slipage.
     *  
     * @param psntVal  Present value
     * @param prvVal  Previous value
     * @param xclLmt  Maximum change in present value from previous value
     * @param outMn  If present value not 0.0 must be at least outMn.
     * @return  Limited present value
     */
    private static double accelLimiter(double psntVal, double prvVal, double xclLmt, double outMn) {
        if (Math.abs(psntVal - prvVal) > xclLmt && psntVal != 0.0) {
            psntVal = psntVal < 0.0 ? Math.min(prvVal - xclLmt, -outMn) :   // Value neg.
            /*                      */Math.max(prvVal + xclLmt, outMn);     // else pos.
        }
        return psntVal;
    }

    public void setHdgSP(double hdgSP){ hdgPid.setSetpoint(hdgSP);}
    public void setHdgPB(double hdgPB){ hdgPid.setP(hdgPB);}
    public void setHdgIT(double hdgIT){ hdgPid.setP(hdgIT);}
    public void setHdgDT(double hdgDT){ hdgPid.setP(hdgDT);}
    public void setHdgDB(double hdgDB){ hdgPid.setInDB(hdgDB);}
    public void setHdgMn(double hdgMn){ hdgPid.setOutMn(hdgMn); }
    public void setHdgMx(double hdgMx){ hdgPid.setOutMx(hdgMx); }
    public void setHdgFF(double hdgFF){ hdgPid.setOutFF(hdgFF); }
    public void setHdgExp(double hdgExp){ hdgPid.setOutExp(hdgExp); }
    public void setHdgXcl(double hdgXcl){ hdgXclLmt = hdgXcl; }

    public double getHdgSP(){ return hdgPid.getSetpoint(); }
    public double getHdgPB(){ return hdgPid.getP(); }
    public double getHdgIT(){ return hdgPid.getI(); }
    public double getHdgDT(){ return hdgPid.getD(); }
    public double getHdgDB(){ return hdgPid.getInDB(); }
    public double getHdgMn(){ return hdgPid.getOutMn(); }
    public double getHdgMx(){ return hdgPid.getOutMx(); }
    public double getHdgFF(){ return hdgPid.getOutFF(); }
    public double getHdgExp(){ return hdgPid.getOutExp(); }
    public double getHdgXcl(){ return hdgXclLmt; }
    // public boolean getHdgk180(){ return hdgProp.get180(); }

    public void setDistSP(double distSP){ distPid.setSetpoint(distSP); }
    public void setDistPB(double distPB){ distPid.setP(distPB); }
    public void setDistIT(double distIT){ distPid.setI(distIT); }
    public void setDistDT(double distDT){ distPid.setD(distDT); }
    public void setDistDB(double distDB){ distPid.setInDB(distDB); }
    public void setDistMn(double distMn){ distPid.setOutMn(distMn); }
    public void setDistMx(double distMx){ distPid.setOutMx(distMx); }
    public void setDistFF(double distFF){ distPid.setOutFF(distFF); }
    public void setDistExp(double distExp){ distPid.setOutExp(distExp); }
    public void setDistXcl(double distXcl){ distXclLmt = distXcl; }

    public double getDistSP(){ return distPid.getSetpoint(); }
    public double getDistPB(){ return distPid.getP(); }
    public double getDistIT(){ return distPid.getI(); }
    public double getDistDT(){ return distPid.getD(); }
    public double getDistDB(){ return distPid.getInDB(); }
    public double getDistMn(){ return distPid.getOutMn(); }
    public double getDistMx(){ return distPid.getOutMx(); }
    public double getDistFF(){ return distPid.getOutFF(); }
    public double getDistExp(){ return distPid.getOutExp(); }
    public double getDistXcl(){ return distXclLmt; }

}