package frc.io.hdw_io;
/*
Author: Shreya
History:
JCH - 11/8/2019 - added additional functions.  isActive/Deactive & onAactive/Deactive
S - 11/8/2019 - Original Release

TODO: - Check it out.

Desc:
Sets the feedback from a digital input to normally closed.
*/

import edu.wpi.first.wpilibj.DigitalInput;

public class InvertibleDigitalInput {
    private DigitalInput limitSwitch;
    private boolean isInverted;
    private boolean previousState;

    //Makes Object and sets it to either inverted or not
    public InvertibleDigitalInput(int channel, boolean invert) {
        isInverted = invert;
        limitSwitch = new DigitalInput(channel);
    }
    //Returns the state of the object
    public boolean get() {
        return (isInverted ? !limitSwitch.get() : limitSwitch.get());
    }

    public boolean isActive(boolean currentState) {
        return get();
    }	
	
	public boolean isDeactive(boolean currentState) {
		return !get();
	}	
	
	public boolean onActive() {
        if(get() != previousState){
            previousState =  get();
            return true;
        }else{
            return false;
        }
	}
	
	public boolean onDeactive() {
        if(!get() != previousState){
            previousState =  get();
            return true;
        }else{
            return false;
        }
	}
}
