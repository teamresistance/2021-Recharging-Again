/*
Desc.  The revolver collects balls from the snorfler.  It holds up to 5
until requested to deliever them to the shooter.
3/1/2020 - Anthony - Original release
3/11 - JCH - updated hdw_io and cleanup
*/
package frc.robot.Subsystem.ballHandler;

import edu.wpi.first.wpilibj.motorcontrol.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.InvertibleDigitalInput;
import frc.io.joysticks.JS_IO;
import frc.util.Timer;

// CHANGES: ONE BANNER SENSOR UNDER(?) THE BALL
//          BANNER SENSOR ON FEED NOT ON SUCK 

public class Revolver {
    private static Victor revolver = IO.revolverRot; // PWM ESC control
    private static InvertibleDigitalInput atIndexStop = IO.revolerIndexer; // Mag switch on gear for one rotation
    private static InvertibleDigitalInput rcvSlotOpen = IO.revRcvSlotOpen;// Banner snsr, chk if a ball is in
                                                                                // rcv'iing slot

    public static int state = 0;
    public static boolean reqRevShtr = false;

    private static int ballCnt = 0;             // Increment when a ball is loaded. Clear on unload.
    private static boolean isFull = false;      // Ball cnt > 4

    private static double revAmp = 0.0;         //Revolver amps
    private static double revAmp_HL = 17.0;     //Revolver amp High Alarm Limit
    private static boolean ballJammed = false;  //Jammed ball, attempt to clear by reversing

    private static double revPct = 0.15;        // Spd when Indexing

    private static Timer stateTimer = new Timer(0.50);  //Timer used in state machine
    private static Timer ballJamTmr = new Timer(1.50);  //Timer if revolver current is hi flag ball jam

    private static boolean isIndexing = true;

    /**Initilize Revolver called from teleopInit() in Robot.java */
    public static void init() { // initialze
        revolver.set(0.0);
        ballCnt = 0;            //Clear ball count then count balls in revolver
        state = 1;              //Default to state 1 to align indexer
        update();               //Call updateto align indexer
    }

    /*
    Revolver indexes if not isFull && not rcvSlotOpen, not (isFull or rcvSlotOpen).
    Snorfler can push ball if rdy2Rcv, not isFull && rcvSlotOpen
    If slot is open max ball cnt "should be" 4.
    stfUpdate updates conditions statuses.  determ reacts to those statuses.

    Hand shake.  Shooter requests indexing, Revolver goes to 1 sets indexing.
    Shooter req goes false and waits while indexing is true.
    Revolver finishes indexing (state 7?), not isIndexing.
    Shooter then can decide to stop or request again.
    */

    /**Determine if the Revolver indexing should start or be interupted */
    public static void determ() {

        if (reqRevShtr && !isIndexing) {    //Shooter is requesting indexing
            state = 1;
        }

        //Slot has a ball but cnt is not full and it's not indexing, call for indexing
        if (!rcvSlotOpen.get() && !isFull() && !isIndexing) {
            state = 1;
        }
        
        //Dvr req to reindex, chk for open slot or all slots filled
        if (JS_IO.btnIndex.onButtonPressed()) {
            ballCnt = 0;
            state = 1;
        }

        if (ballJammed && state < 90) { // jammed Ball
            state = 90; //Attempt to clear jammed ball
        }
    }

    //--------------------------  Updating  -------------------------------------------------
    /**Update Revolver state machine.  Called from teleopPeriodic in Robot.java */
    public static void update() { // cases for state of revolver
        stfUpdate();        //Updated associated stuff, statuses
        determ();           //Determine if we need to start or interupt the indexing
        sdbUpdate();        //Update Revolver Smartdashboard items

        switch (state) {
            case 0: // everything off, revolverSpeed. Waiting for Load or Unload.
                cmdUpdate(0.0);
                if (stateTimer.hasExpired(0.05, state));    // Initialize timer for covTrgr
                isIndexing = false;
                break;
            // ------------ Start indexing --------------------
            case 1: // wait some time ... if needed.  Flag isIndexing true.
                cmdUpdate(0.0);
                if (stateTimer.hasExpired(0.05, state))
                    state++; // Wait time (settle time?)
                isIndexing = true;
                break;
            case 2: // Motor On boost mode for short time to get off stall issue
                cmdUpdate(0.25);
                if (stateTimer.hasExpired(0.10, state)) 
                    state++;
                break;
            case 3: // Motor on at normal spd until index sw.
                cmdUpdate(revPct);
                if (atIndexStop.get())
                    state++; // Wait for sw goes false.
                break;
            case 4: // Brake with small negative for short time(stop overshoot, maybe)
                cmdUpdate(-0.20);   //0.25 moves it back to switch @ 0.07mS
                if (stateTimer.hasExpired(0.05, state))
                    state++; // Brake time
                break;
            case 5: // Stop the revolver but wait a short period before signaling done indexing
                cmdUpdate(0.0);
                if (stateTimer.hasExpired(0.15, state)){
                    // reqRevShtr = false;
                    state++; // Shutdown time
                }
                break;
            case 6: // If full or slot is not open stop indexing else index again and incr ball cnt.

                //Stop indexing if isFull && not rcvSlotOpen  else index again and incr ball cnt.
                //Revolver indexes if not isFull && not rcvSlotOpen, not (isFull or rcvSlotOpen).
                //Stop indexing if isFull && not rcvSlotOpen
                ballCnt++;
                if( isFull() && rcvSlotOpen.get()) ballCnt = 4;     //Slot is open, can't be full, reduce.
                state = isFull() || rcvSlotOpen.get() ? 0 : 1;      //If full or slot open stop index else one more time
                break;
            // case 8: // btnIndex pressed
            //     Snorfler.ballCnt = 0;
            //     state = nextSpaceOpen.get() ? 0 : 1;
            //     break;

            // --------------- Jammed Ball Safety ----------------
            case 90: // Safety everything off.
                cmdUpdate(0.0);
                // IO.revolver_HAA = false;
                state++;
                break;
            case 91: // Wait momentarily before reversing.
                cmdUpdate(0.0);
                if (stateTimer.hasExpired(0.2, state))
                    state++;
                break;
            case 92: // Reverse momentarily.
                cmdUpdate(-revPct);
                if (stateTimer.hasExpired(0.3, state))
                    state++;
                break;
            case 93: // Stop momentarily
                cmdUpdate(0.0);
                if (stateTimer.hasExpired(0.2, state))
                    state++;
                break;
            case 94: // before trying to index again.
                ballJammed = false;
                state = isIndexing ? 3 : 1;     //Maybe an issue with boost since it's already indexing
                break;

            default: // everything off
                cmdUpdate(0.0);
                System.out.println("Invalid Revolver state - " + state);
                break;
        }
    }

    /** Update stuff */
    private static void stfUpdate() {
        revAmp = IO.pdp.getCurrent(4);  //Revolver current
        // if(ballJamTmr.hasExpired(1.0, revAmp > 21.0) && !jammedBall) jammedBall = true;   //Reset after action, 90
        //Alternative - Entire indexing should take les than 1 sec.
        if (ballJamTmr.hasExpired(2.0, state) && isIndexing)   //SHOULD be a OnDelay, good for now.  Need to mod covTmr
            ballJammed = true;
    }

    /**
     * Send revolver speed cmd to motor. Check for motor hi alarm, jam, and stop
     * motor.
     */
    private static void cmdUpdate(double revolverSpeed) {
        // Jammed ball will not break anything "critical" so handle in state machine (90).
        revolver.set(revolverSpeed);
    }

    /** Initialize sdb for revolver */
    private static void sdbInit() {
    }

    /** SmartDashboard Updates */
    private static void sdbUpdate() {
        SmartDashboard.putNumber("Revolver/1.Revolver State", state);
        SmartDashboard.putBoolean("Revolver/2.Rev slot open", rcvSlotOpen.get());
        SmartDashboard.putBoolean("Revolver/3.atIndexStop", atIndexStop.get());
        SmartDashboard.putNumber("Revolver/4.Ball Count", ballCnt);
        SmartDashboard.putBoolean("Revolver/5.Is full", isFull());
        SmartDashboard.putBoolean("Revolver/6.Ball Jam", ballJammed);
        SmartDashboard.putBoolean("Revolver/7.Req from Shtr", reqRevShtr);
    }

    //-------------------  Statuses and conditioning -----------------------------------------
    /**
     * @return true if revolver is at index, has an open slot and isn't indexing.
     */
    public static boolean rdy2Rcv() {
        return /*atIndexStop.get()  && */ rcvSlotOpen.get() && state == 0; /* && !indexing */
    }

    /** @return true if revolver had/has hi amp and is attempting to clear. */
    public static boolean isJammedBall() {
        return ballJammed;
    }

    /** @return true if revolver thinks it has 5 balls. */
    public static boolean isFull() {
        return ballCnt >= 5;
    }

    /** @return true if revolver is still indexing since last request */
    public static boolean isIndexing() {
        return isIndexing;
    }

    /** @return state of the Revolver. State assignments may chg, use statuses. */
    public static int getState() {
        return state;
    }

}
