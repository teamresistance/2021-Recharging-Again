package frc.robot.auto.functions;

import com.ctre.phoenix.motorcontrol.ControlMode;

import frc.io.hdw_io.IO;

public abstract class AutoFunction {
    
    private boolean finished;


    public void init() {
        IO.drvMasterTSRX_L.set(ControlMode.Disabled, 0);
        IO.drvMasterTSRX_R.set(ControlMode.Disabled, 0);
        sdbInit();
        finished = false;
    }

    public void execute() {

    }

    public void done() {
        finished = true;
    }

    public boolean finished() {
        return finished;
    }

    private void sdbInit() {
        
    }

    private void sdbUpdate() {

    }
}
