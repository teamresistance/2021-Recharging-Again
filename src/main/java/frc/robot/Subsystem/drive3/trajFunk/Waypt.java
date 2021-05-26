package frc.robot.Subsystem.drive3.trajFunk;

import frc.io.hdw_io.IO;
import frc.robot.Subsystem.drive3.Drive;

/**
 * This ATrajFunction turns to passed heading while it moves passed distance.
 */
public class Waypt extends ATrajFunction {

    private double wpX = 0.0;
    private double wpY = 0.0;
    private double pwrMx = 1.0;

    private double hdgSP = 0.0;
    private double distSP = 0.0;

    // dont use negative power
    public Waypt(double _wpX, double _wpY, double _pwrMx) {
        wpX = _wpX;
        wpY = _wpY;
        pwrMx = _pwrMx;
    }

    public void execute() {
        // update();
        switch (state) {
            case -1: //Get present XY Loc and calc new MoveOnHdg
                calcHdgDistSP();
                break;
            case 0: // Init Trajectory, turn to hdg then (1) ...
                steer.steerTo(hdgSP, pwrMx, distSP);
                Drive.distRst();
                state++;
            case 1: // Move forward, steer Auto Heading and Dist
                Drive.cmdUpdate(0);
                // Chk if distance is done
                if (steer.isDone()) state++; // Chk distance only
                break;
            case 2:
                // Drive.distRst();
                done();
                break;
        }
    }

    private void calcHdgDistSP(){
        double x = IO.getCoorX() - wpX;
        double y = IO.getCoorY() - wpY;
        distSP = Math.sqrt(x*x + y*y);

        if( y == 0){
            hdgSP = x < 0 ? -90 : 90;
        }else{
            hdgSP = Math.atan(x/y);
            if(y < 0){
                hdgSP += x < 0 ? 180 : -180; 
            }
        }
    }
}
