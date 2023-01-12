package frc.robot.Subsystem.drive.trajFunk;

import frc.util.Timer;
/*Author - Purab
 *History
 *2/24/22 - Initial release
 */

/**
 * This TrajFunction delays executionof the trajectory.
 */
public class TrajDelay extends ATrajFunction {

    private static Timer delayTimer;
    private double timeDelay;

    /**
     * Constructor to delay execution of the trajectory.
     * @param secDelay seconds to delay execution of the trajectory.
     */
    public TrajDelay(double secDelay) {
        timeDelay = secDelay;
    }

    public void execute() {
        switch (state) {
        case 0: // set Snorfler control
            delayTimer = new Timer(timeDelay);
            state++;
        // System.out.println("Snf - 0: ---------- Init -----------");
            break;
        case 1:
            if(delayTimer.hasExpired()) state++;
            break;
        case 2:
            setDone();
            // System.out.println("Snf - 1: ---------- Done -----------");
            break;
        default:
            setDone();
            System.out.println("Time Delay - Dflt: ------  Bad state  ----");
            break;
        }
    }
}
