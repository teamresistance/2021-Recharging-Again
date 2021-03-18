package frc.robot.auto;

import javax.xml.namespace.QName;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.robot.auto.functions.*;

public class Auto {

    private AutoFunction[] traj;
    private boolean overallFin = false;
    private int autoStep = 0;
    private int idx = 0;
    private int x = 0;

    public Auto(AutoFunction[] path) {
        traj = path;
    }

    public void init() {
        disable();
        for (AutoFunction af : traj) {
            af.init();
        }
        idx = 0;
        x = 0;
        overallFin = false;
        IO.navX.reset();
    } 

    public void execute() {
        switch (autoStep) {
            case 0:
                traj[idx].init();
                x = 0;
                autoStep++;
                break;
            case 1:
                traj[idx].execute();
                switch (x) {
                    case 0:
                        traj[idx].execute();
                        if (traj[idx].finished()) {
                            x++;
                        }
                        break;
                    case 1:
                        traj[idx].done();
                        autoStep++;
                        break;
                }
                break;
            case 2:
                idx++;
                if (idx < traj.length) {
                    autoStep = 0;
                } else { // Next Traj else finished
                    autoStep = 3;
                    break;
                }
                break;
            case 3:
                done();
                break;
        }
    }

    public void done() {
        overallFin = true;
        for (AutoFunction af : traj) {
            af.done();
        }
    }

    public boolean finished() {
        return overallFin;
    }

    public void disable() {
        IO.diffDrv_M.tankDrive(0.0, 0.0);
        IO.follow();  
    }

    public void sdbInit() {
    }

    public void sdbUpdate() {
        SmartDashboard.putNumber("Auto Step", autoStep);
        SmartDashboard.putNumber("Current Traj Idx", idx);
    }

}
