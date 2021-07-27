/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.Subsystem.driveSave;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.Encoder;
import frc.io.hdw_io.IO;
import frc.io.joysticks.JS_IO;

/**
 * Add your docs here.
 */
public class Drive {

    private static int state;

    private static boolean invToggle; // toggles between inverted and not
    private static boolean scaleToggle;
    private static boolean inverted;
    private static boolean scaled;

    private static double scale = -0.5;

    // TODO: Need to see if this split modes will work when switching between them.
    // Don't think will work. Don't think we can do this. May not be able to
    // send cmds to slaves by DiffDrv AND define Followers.
    // Assignments used by ControlMode. Slaves "follow" masters.
    // private static TalonSRX left = IO.drvMasterTSRX_L;
    // private static TalonSRX right = IO.drvMasterTSRX_R;
    // private static VictorSPX leftSlave = IO.drvFollowerVSPX_L;
    // private static VictorSPX rightSlave = IO.drvFollowerVSPX_R;
    private static Encoder encL = IO.drvEnc_L;
    private static Encoder encR = IO.drvEnc_R;

    // Assignments used by DiffDrv. Slaves sent same command.
    private static DifferentialDrive diffDrv_M = IO.diffDrv_M;
    // private static DifferentialDrive diffDrv_S = new DifferentialDrive(IO.drvFollowerVSPX_L, IO.drvFollowerVSPX_R);

    private static Steer steer = new Steer();
    private static double[] strCmd;
    private static double hdgFB;

    public static double distFB() { return (IO.drvEnc_L.feet() + IO.drvEnc_R.feet()) / 2; }
    public static void distRst() { IO.drvEnc_L.reset(); IO.drvEnc_R.reset(); }
    private static double dist_Avg;

    public static double hdgFB() {return IO.navX.getNormalizeTo180();}  //Only need hdg to Hold Angle 0 or 180
    private static double hdgOut;

    public static void init() {
        SmartDashboard.putNumber("Drive Scale", -0.5);
        cmdUpdate(0, 0);
        state = 0;
        invToggle = true;
        scaleToggle = true;
        inverted = false;
        scaled = false;
        // IO.drvMasterTSRX_L.set(ControlMode.Disabled, 0);
        // IO.drvMasterTSRX_R.set(ControlMode.Disabled, 0);
    }

    // Determine the drive mode.
    // TODO: How does the driver return to unscaled, normal mode?
    public static void determ() {
        // if (JS_IO.btnInvOrientation.onButtonPressed()) {
        //     if (invToggle) {
        //         state = scaled ? 2 : 5;
        //     } else {
        //         state = scaled ? 1 : 0;
        //     }
        //     invToggle = !invToggle;
        // }

        // if (JS_IO.btnScaledDrive.onButtonPressed()) {
        //     if (scaleToggle) {
        //         state = inverted ? 2 : 1;
        //     } else {
        //         state = inverted ? 5 : 0;
        //     }
        //     scaleToggle = !scaleToggle;
        // }

        if (JS_IO.btnInvOrientation.onButtonPressed()) {
            if (invToggle) {
                if (scaled) {
                    state = 2;
                } else {
                    state = 5;
                }
                invToggle = !invToggle;
            } else {
                if (scaled) {
                    state = 1;
                } else {
                    state = 0;
                }
                invToggle = !invToggle;
            }
        }

        if (JS_IO.btnScaledDrive.onButtonPressed()) {
            if (scaleToggle) {
                if (inverted) {
                    state = 2;
                } else {
                    state = 1;
                }
                scaleToggle = !scaleToggle;
            } else {
                if (inverted) {
                    state = 5;
                } else {
                    state = 0;
                }
                scaleToggle = !scaleToggle;
            }
        }
    }

    // Update Drive mode. Called from Robot.
    public static void update() {
        if(JS_IO.btnRstGyro.onButtonPressed()){     //Testing location
            IO.navX.reset();
            IO.resetCoor();
        } 
        determ();
        sdbUpdate();
        switch (state) {
            case 0: // Tank mode, no scaling. JSs to wheels.
                cmdUpdate(-JS_IO.axLeftDrive.get(), -JS_IO.axRightDrive.get());
                scaled = false;
                inverted = false;
                // diffDrv_M.tankDrive(-JS_IO.axLeftDrive.get(), -JS_IO.axRightDrive.get());
                // diffDrv_S.tankDrive(-JS_IO.axLeftDrive.get(), -JS_IO.axRightDrive.get());
                break;
            case 1: // Tank mode, w/ scaling. JSs * scale to wheels.
                cmdUpdate(scale * JS_IO.axLeftDrive.get(), scale * JS_IO.axRightDrive.get());
                scaled = true;
                inverted = false;
                break;
            case 2: // Tank mode, w/ scaling. Reversre direction, front & back. swaps axes
                cmdUpdate(-scale * JS_IO.axRightDrive.get(), -scale * JS_IO.axLeftDrive.get());
                scaled = true;
                inverted = true;
                break;
            case 5: // reverse no scaled, swaps axes
                cmdUpdate(JS_IO.axRightDrive.get(), JS_IO.axLeftDrive.get());
                scaled = false;
                inverted = true;
                break;
            // TODO: This doesn't work. Talons followers don't work in Diff Drive.
            case 3: // hold 0
                steer.steerTo(0, 100.0, 0.0);
                strCmd = steer.update(hdgFB, distFB());
                hdgOut = strCmd[0];
                // diffDrv_M.arcadeDrive(JS_IO.axLeftDrive.get(), hdgOut, false);
                // diffDrv_S.arcadeDrive(JS_IO.axLeftDrive.get(), hdgOut, false);
                break;
            case 4: // hold 180
                steer.steerTo(180, 100.0, 0.0);
                strCmd = steer.update(hdgFB, distFB());
                hdgOut = strCmd[0];
                // diffDrv_M.arcadeDrive(JS_IO.axLeftDrive.get(), hdgOut, false);
                // diffDrv_S.arcadeDrive(JS_IO.axLeftDrive.get(), hdgOut, false);
                break;
            default:
                cmdUpdate(0, 0);
                System.out.println("Invaid Drive State - " + state);
                break;
        }
    }

    public static void sdbUpdate() {
        SmartDashboard.putNumber("Driver State", state);
        scale = SmartDashboard.getNumber("Drive Scale", -0.5);
        SmartDashboard.putNumber("Right Drive Enc", encL.ticks());
        SmartDashboard.putNumber("Left Drive Enc", encR.ticks());
        SmartDashboard.putNumber("dist L", encL.feet());
        SmartDashboard.putNumber("dist R", encR.feet());
        SmartDashboard.putBoolean("scaled", scaled);
        SmartDashboard.putBoolean("inverted", inverted);

        // Location test
        SmartDashboard.putNumber("ALoc/Angle", IO.navX.getAngle());
        SmartDashboard.putNumber("ALoc/Distance", IO.getDeltaD());
        SmartDashboard.putNumber("ALoc/X", IO.getCoorX());
        SmartDashboard.putNumber("ALoc/Y", IO.getCoorY());
        SmartDashboard.putNumberArray("ALoc/arXY", IO.getCoor());
        SmartDashboard.putNumber("ALoc/arX", IO.getCoor()[0]);
        SmartDashboard.putNumber("ALoc/arY", IO.getCoor()[1]);
    }

    public static int getState() {
        return state;
    }

    public static void cmdUpdate(double lSpeed, double rSpeed) {
        // left.set(ControlMode.PercentOutput, lSpeed);
        // right.set(ControlMode.PercentOutput, -rSpeed);
        diffDrv_M.tankDrive(lSpeed, rSpeed);
        // IO.follow();
        // leftSlave.set(ControlMode.PercentOutput, lSpeed);
        // rightSlave.set(ControlMode.PercentOutput, rSpeed);
    }
}
