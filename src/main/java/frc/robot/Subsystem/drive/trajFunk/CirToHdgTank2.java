package frc.robot.Subsystem.drive.trajFunk;

import frc.robot.Subsystem.drive.Drive;
import frc.util.PIDXController;

/**
 * This ATrajFunction uses Tank drive to circle a radius distance.
 * It calculates the radius to control the outside(JSX) wheel command from the
 * present XY position in IO to the center XY passed.  The inside(Fixed) command
 * is calculated until heading is reached.
 * NOTE: Testing control the outside and hold the inside.
 */
public class CirToHdgTank2 extends ATrajFunction {

    private double ctrX;
    private double ctrY;
    private double radiusSP;
    private double hdgSP;
    private double lCmd;
    private double rCmd;
    private boolean turnRight;

    /**
     * Constructor to make a circle using tank steering.
     * @param _ctrX
     * @param _ctrY
     * @param _radiusSP
     * @param _hdgSP
     * @param _lCmdBase
     * @param _rCmdBase
     */
    public CirToHdgTank2(double _ctrX, double _ctrY, double _radiusSP,
                         double _hdgSP, double _lCmdBase, double _rCmdBase) {
        ctrX = _ctrX;
        ctrY = _ctrY;
        radiusSP = _radiusSP;
        hdgSP = _hdgSP;
        lCmd = _lCmdBase;
        rCmd = _rCmdBase;
        turnRight = Math.abs(lCmd) > Math.abs(rCmd);

        radiusFB = radiusSP;
    }

    /*Trying something different.
    Using Steer hdg to adjust the higher speed wheel.Prop only, 
    gets closer slows down a little and check for done hdg.
    cmd[1] is applied to the outside wheel.
    Using Steer dist to adjust the lower spped wheel.  Integration only,
    to maintain radius.  Rot passed "should" be < 0.9 to 
    allow adjust to tighter curve if needed.
    cmd[0] is applied to the inside wheel.
    Both are inverted from what we normally get from steerTo.
    */
    public void execute() {
        switch (state) {
        /*Initialize objects here.  pidHdg/Dist are shared with
        other ATrajFunctions, as others.  This is executed while
        this traj is active.
        */
        case 0: // Init Trajectory
            pidHdg = new PIDXController(-1.0/45, 0.0, 0.0);
            pidHdg.enableContinuousInput(-180.0, 180.0);
            pidHdg.setSetpoint(hdgSP);
            pidHdg.setTolerance(5.0);   //Just for atSetpoint

        pidDist = new PIDXController(1.0/0.4, 0.0, 0.0);
            //Set extended values pidCtlr, SP, DB, Mn, Mx, Exp, Cmp
            setExt(pidDist, radiusSP, 0.0, 0.0, 0.5, 1.0, false);
            pidDist.setIntegratorRange(-0.5, 0.5);  //Limt integration to +/-
            pidDist.setOutFF(turnRight ? lCmd : rCmd);  //Inside whl
            //--------- maybe ------------
            // strCmd[0] = Math.abs(turnRight ? lCmd : rCmd);
            // pidDist.setOutMx(strCmd[0] + pidDist.getOutMn());
            // pidDist.setOutMn(strCmd[0] - pidDist.getOutMn());
            
            initSDB();
            state++;
            System.out.println("CHT2 - 0 \thdgSP: " + pidHdg.getSetpoint() + "\tdistSP: " + pidDist.getSetpoint());
        case 1: // Turn to heading.  Mx spd on out whl.  Hold radius on inside whl.
            strCmd[0] = turnRight ? lCmd : rCmd;                    //cmd[0]=Outside
            strCmd[0] += pidDist.calculateX(radiusFB(ctrX, ctrY));  //outside radius offset
            strCmd[1] = turnRight ? rCmd : lCmd;                    //cmd[1]=Inside
            //--------- maybe ------------
            // strCmd[0] = pidDist.calculateX(radiusFB(ctrX, ctrY));   //outside w/ radius offset
            // strCmd[1] = turnRight ? rCmd : lCmd;                    //cmd[1]=Inside

            System.out.println("rad cmd0: " + strCmd[0] + "\tcmd1: " + strCmd[1] );
            if(turnRight){      //If left is inside wheel
                Drive.cmdUpdate(strCmd[0], strCmd[1], false, 1); //Turning right, left whl is outside
            }else{              //else right is inside wheel
                Drive.cmdUpdate(strCmd[1], strCmd[0], false, 1); //Turning left, right whl is outside
            }
            if (pidHdg.atSetpoint()) state++;    // Chk hdg only
            prtShtuff("CHT2");
            break;
        case 2: // Done
            done();
            System.out.println("CHT2 - 2: Final Inside Cmd: " + strCmd[turnRight ? 1 : 0]);
            break;
        }
        updSDB();
    }
}
