package frc.robot.Subsystem.drive3.trajFunk;

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
                steer.steerTo(hdgSP, 1.0, 0.0);
                state++;
            case 1: // Turn to heading using l & r Pwr's passed as tank drive.
                cmdUpdate(-lPwr, -rPwr, true, 1);
                // Chk if trajectory is done
                steer.update();
                if((Math.abs(hdgFB() - hdgSP) % 360) < 10.0 ) state++;  //This is a kludge to get things working
                System.out.println("hdgFB: " + hdgFB() + "/thdgSP: " + hdgSP + "/thddgErr: " + ((hdgFB() - hdgSP) % 360));
                // if (steer.isHdgDone()) state++;    // Chk hdg only
                break;
            case 2:
                done();
                break;
        }
    }
}
