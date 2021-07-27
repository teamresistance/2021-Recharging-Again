package frc.robot.Subsystem.drive.trajFunk;

import frc.robot.Subsystem.drive.Drive;
import frc.util.PIDXController;

/**
 * This ATrajFunction calculates a heading and distance from the 
 * present XY position in IO and the Waypoint XY passed.  It then
 * steers on the calculated hdg & distance.
 */
public class Waypt extends ATrajFunction {

    private double wpX = 0.0;
    private double wpY = 0.0;
    private double pwrMx = 1.0;
    private double hdgErrLT = 10.0;

    /**
     * Constructor for turn to hdg before moving on hdg.
     * @param _wpX target X coordinate
     * @param _wpY target Y coordinate
     * @param _pwrMx max power to use
     * @param _hdgErrLT turn towards hdg until LT error then move on hdg
     */
    public Waypt(double _wpX, double _wpY, double _pwrMx, int _hdgErrLT) {
        wpX = _wpX;    wpY = _wpY;
        pwrMx = Math.abs(_pwrMx);   // dont use negative power
        hdgErrLT = _hdgErrLT;
    }

    /**Constructor for immediately move on hdg */
    public Waypt(double _wpX, double _wpY, double _pwrMx) {
        this(_wpX, _wpY, _pwrMx, 180);
    }

    /**Constructor for immediately move on hdg and use 1.0 max power */
    public Waypt(double _wpX, double _wpY) {
        this(_wpX, _wpY, 1.0, 180);
    }

    public void execute() {
        // update();
        switch (state) {
        case 0: // Init Trajectory
            pidHdg = new PIDXController(1.0/50, 0.0, 0.0);
            pidHdg.enableContinuousInput(-180.0, 180.0);
            //Set extended values pidCtlr, SP, DB, Mn, Mx, Exp, Cmp
            setExt(pidHdg, 0.0, 2.0, 0.4, pwrMx, 2.0, true);

            pidDist = new PIDXController(-1.0/8, 0.0, 0.0);
            //Set ext vals pidCtlr, SP, DB, Mn, Mx, Exp, Cmp
            setExt(pidDist, 0.0, 0.7, 0.3, pwrMx, 1.0, true);

            strCmd = wpCalcHdgDistSP(wpX, wpY); //Get present XY Loc and calc hdg & distSP's (static)
            pidHdg.setSetpoint(strCmd[0]);  //Used strCmd as tmp holder
            pidDist.setSetpoint(strCmd[1]);

            Drive.distRst();
            initSDB();
            System.out.println("WPT2 - 0 \thdgSP: " + pidHdg.getSetpoint() + "\tdistSP: " + pidDist.getSetpoint());
            state++;
        case 1: // Turn to heg error LT turnTo
            strCmd[0] = pidHdg.calculateX(hdgFB()); //cmd[0]=rotate(JSX)
            Drive.cmdUpdate(0.0, strCmd[0], false, 2); // cmdUpdate for hdg & dist.
            if (Math.abs(pidHdg.getPositionError()) < hdgErrLT ){
                strCmd = wpCalcHdgDistSP(wpX, wpY); //Get present XY Loc and calc hdg & distSP's (static)
                pidHdg.setSetpoint(strCmd[0]);      //Used strCmd as tmp holder
                pidDist.setSetpoint(strCmd[1]);
        
                state++;
            }
            prtShtuff("WPT2");
            break;
        case 2: // Move forward, steer Auto Heading and Dist
            strCmd[0] = pidHdg.calculateX(hdgFB()); //cmd[0]=rotate(JSX)
            strCmd[1] = pidDist.calculateX(distFB()); //cmd[1]=fwd(JSY)
            prtShtuff("WPT2");
            Drive.cmdUpdate(strCmd[1], strCmd[0], false, 2); // cmdUpdate for hdg & dist.
            if (pidDist.atSetpoint() && pidHdg.atSetpoint()) state++; //Chk hdg & dist done.
            break;
        case 3: //Done
            done();
            System.out.println("WPT2 - 3: ---------- Done -----------");
            break;
        }
        updSDB();
    }
}