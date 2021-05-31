package frc.robot.Subsystem.drive3.trajFunk;

import frc.io.hdw_io.IO;
import frc.robot.Subsystem.drive3.Drive;

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
                tSteer.steerTo(hdgSP, 1.0, 0.0);
                tSteer.setHdgDB(3.0);
                System.out.print("STRT TTH! ");
                // System.out.println("\thdgFB: " + hdgFB() + "\thdgSP: " + hdgSP + "\thddgErr: " + ((hdgFB() - hdgSP) % 360));
                System.out.println("\tCoorX: " + IO.getCoorX() + " \tCoorY " + IO.getCoorY() + " \tHdg " + hdgFB());
                state++;
            case 1: // Turn to heading using l & r Pwr's passed as tank drive.
                Drive.cmdUpdate(-lPwr, -rPwr, true, 1);
                // Chk if trajectory is done
                tSteer.update();
                // if((Math.abs(Drive.hdgFB() - hdgSP) % 360) < 3.0 ) state++;  //This is a kludge to get things working
                // System.out.println("hdgFB: " + hdgFB() + "\thdgSP: " + hdgSP + "\thddgErr: " + ((hdgFB() - hdgSP) % 360));
                if (tSteer.isHdgDone()) state++;    // Chk hdg only
                break;
            case 2:
                done();
                System.out.print("DONE TTH! ");
                // System.out.println("\thdgFB: " + hdgFB() + "\thdgSP: " + hdgSP + "\thddgErr: " + ((hdgFB() - hdgSP) % 360));
                System.out.println("\tCoorX: " + IO.getCoorX() + " \tCoorY " + IO.getCoorY() + " \tHdg " + hdgFB());
                break;
        }
    }
}
