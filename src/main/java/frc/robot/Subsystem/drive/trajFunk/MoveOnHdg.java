package frc.robot.Subsystem.drive.trajFunk;

import frc.robot.Subsystem.drive.Drive;
import frc.util.PIDXController;

/**
 * This ATrajFunction holds heading while it moves distance.
 */
public class MoveOnHdg extends ATrajFunction {

    // General
    private double hdgSP = 0.0;
    private double distSP = 0.0;
    private double pwrMx = 0.0;

    // dont use negative power - why?

    /**
     * Constructor
     * @param eHdg
     * @param eDist
     * @param ePwr
     */
    public MoveOnHdg(double eHdg, double eDist, double ePwr) {
        hdgSP = eHdg;
        distSP = eDist;
        pwrMx = Math.abs(ePwr);
    }

    /**
     * Constructor - ePwr defaults to 1.0
     * @param eHdg
     * @param eDist
     */
    public MoveOnHdg(double eHdg, double eDist) {
        this(eHdg, eDist, 1.0);
    }

    public void execute() {
        switch (state) {
        case 0: // Init Trajectory, turn to hdg then (1) ...
            pidHdg = new PIDXController(1.0/45, 0.0, 0.0);
            pidHdg.enableContinuousInput(-180.0, 180.0);
            //Set extended values SP, DB, Mn, Mx, Exp, Cmp
            setExt(pidHdg, hdgSP, 2.0, 0.35, pwrMx, 2.0, true);

            pidDist = new PIDXController(-1.0/10, 0.0, 0.0);
            //Set extended values SP, DB, Mn, Mx, Exp, Cmp
            setExt(pidDist, distSP, 0.5, 0.2, pwrMx, 1.5, true);

            Drive.distRst();
            initSDB();
            state++;
            System.out.println("MOH - 0");
            break;
        case 1: // Move forward, steer Auto Heading and Dist
            System.out.println("MOH");
            strCmd[0] = pidHdg.calculateX(hdgFB());   //cmd[0]=rotate(X), [1]=fwd(Y)
            strCmd[1] = pidDist.calculateX(distFB()); //cmd[0]=rotate(X), [1]=fwd(Y)
            Drive.cmdUpdate(strCmd[1], strCmd[0], false, 2);
            // Chk if both are done
            if (pidDist.atSetpoint() && pidHdg.atSetpoint()) state++; // Chk both
            // if (pidDist.atSetpoint()) state++; // Chk both
            prtShtuff("MOH");
            break;
        case 2: // Done
            done();
            System.out.println("MOH - 2: ---------- Done -----------");
            break;
        }
        updSDB();
    }
}
