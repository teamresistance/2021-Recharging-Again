/*
Desc. The timer is set, started, when the a change of variable, cov, occurs
for an int or boolean.  The same call is made to set the time and check it.

History -
1/1/20 - Anthony released
3/12/20 - JCH added simple startTimer and renamed vars.
*/

package frc.util;

public class Timer {
    //sets timer in seconds
    private double delay; //in seconds
    private double initTime;
    private int covTrgr = -1;
    private boolean trgr = false;

    //Constructor
    public Timer(double delay){
        initTime = System.currentTimeMillis();
        this.delay = delay * 1000;
    }

    // If chg of var, cov, set delay time once, then continue to call for expired time.
    public boolean hasExpired(double delay, int covTrgr){
        if(this.covTrgr != covTrgr){
            initTime = System.currentTimeMillis();
            this.delay = delay * 1000;
        }
        this.covTrgr = covTrgr;
        return hasExpired();
    }

    // If chg of var, cov, set delay time once, then continue to call for expired time.
    public boolean hasExpired(double delay, boolean trgr){
        if(this.trgr != trgr){
            initTime = System.currentTimeMillis();
            this.delay = delay * 1000;
        } 
        this.trgr = trgr;
        return hasExpired();
    }

    // Has present time exceed delay time.
    public boolean hasExpired(){
        return System.currentTimeMillis() >= (initTime + delay);
    }

    // Resets the timer and sets a new delay
    public void startTimer(double sec){
        delay = sec;
        initTime = System.currentTimeMillis();
    }

    // Resets the timer and uses existing delay
    public void startTimer(){
        initTime = System.currentTimeMillis();
    }

}
