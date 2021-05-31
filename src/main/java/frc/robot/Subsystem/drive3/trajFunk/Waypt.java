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
            case 0: // Init Trajectory, turn to hdg then (1) ...
                calcHdgDistSP();    //Get present XY Loc and calc new MoveOnHdg
                tSteer.steerTo(hdgSP, pwrMx, distSP);
                Drive.distRst();
                state++;
            case 1: // Move forward, steer Auto Heading and Dist
                strCmd = tSteer.update();
                System.out.println("MWP1 \thdgSP: " + hdgSP + "\tdistSP: " + distSP);
                Drive.cmdUpdate(strCmd[1], strCmd[0], true, 2); //Calls steerTo & cmdUpdate for hdg & dist.
                // Drive.cmdUpdate(0); //Calls steerTo & cmdUpdate for hdg & dist.
                if (tSteer.isDone()) state++; //Chk hdg & dist done.
                break;
            case 2:
                done();
                System.out.print("DONE WPT: ");
                System.out.println("\tCoorX: " + IO.getCoorX() + " \tCoorY " + IO.getCoorY() + " \tHdg " + hdgFB());
                break;
        }
    }

    private void calcHdgDistSP(){
        System.out.println("wpX: " + wpX + "\twpY: " + wpY);
        System.out.println("X: " + IO.getCoorX() + "\tY: " + IO.getCoorY());
        double deltaX = wpX - IO.getCoorX();    //Adjacent
        double deltaY = wpY - IO.getCoorY();    //Opposite
        distSP = Math.sqrt(deltaX*deltaX + deltaY*deltaY);  //Hypotenuse
        System.out.println("distSP: " + distSP);
        System.out.println("hdgSP raw: " + Math.atan(deltaX/deltaY));

        if( deltaX == 0){
            hdgSP = deltaY < 0 ? -90 : 90;
        }else{
            hdgSP = Math.toDegrees(Math.atan(deltaY/deltaX));
            if(deltaY < 0){
                hdgSP += deltaX < 0 ? -180 : 180; 
            }
        }
    }
}
