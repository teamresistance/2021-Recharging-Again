package frc.robot.Subsystem.drive.trajFunk;

import frc.robot.Subsystem.drive.Drive;
import frc.util.PIDXController;

/**
 * This AutoFunction turns to passed heading using tank drive.
 */
public class TankTurnHdg extends ATrajFunction {

    private double hdgSP = 0.0;
    private double lPwr = 0.0;
    private double rPwr = 0.0;

    // dont use negative power - why?
    public TankTurnHdg(double _hdg, double _lPwr, double _rPwr) {
        hdgSP = _hdg;
        lPwr = _lPwr;
        rPwr = _rPwr;
    }

    public void execute() {
        // System.out.println("TTH - exec: " + state);
        switch (state) {
        case 0: // Init Trajectory, turn to hdg then (1) ...
            pidHdg = new PIDXController(-1.0/70, 0.0, 0.0);
            pidHdg.enableContinuousInput(-180.0, 180.0);
            pidHdg.setSetpoint(hdgSP);
            pidHdg.setInDB(2.0);
            pidHdg.setOutMn(0.3);
            pidHdg.setOutMx(Math.abs(lPwr));
            pidHdg.setOutExp(2.0);

            pidDist = new PIDXController(1.0/70, 0.0, 0.0);
            pidDist.enableContinuousInput(-180.0, 180.0);
            pidDist.setSetpoint(hdgSP);
            pidDist.setInDB(2.0);
            pidDist.setOutMn(0.3);
            pidDist.setOutMx(Math.abs(rPwr));
            pidDist.setOutExp(2.0);

            // Drive.distRst();
            initSDB();
            state++;
            System.out.println("TTH - 0");
        case 1: // Turn to heading using l & r Pwr's passed as tank drive.
            strCmd[0] = pidHdg.calculateX(hdgFB());   //left power
            strCmd[1] = pidDist.calculateX(hdgFB());  //right power
            Drive.cmdUpdate(strCmd[0], strCmd[1], false, 1);
            // Chk if trajectory is done
            if (pidHdg.atSetpoint()) state++;    // Chk hdg only
            prtShtuff("TTH");
            break;
        case 2:
            done();
            System.out.println("TTH - 2: ---------- Done -----------");
            break;
        }
        updSDB();
    }
}