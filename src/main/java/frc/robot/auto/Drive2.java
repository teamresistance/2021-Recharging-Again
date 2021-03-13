// package frc.robot.auto;
// /*
// Original Author: Team 86
// History:
// JCH - 12/6/2019 - Original release

// TODO: Checkout

// Desc: 
// Controls a differential style drive system.  Can switch between Tank & Arcade joysticks.
// The maximum responce can be adjusted, scaled to the JSs -1 to 1 input.
// A fixed angle can be held using the pov buttons.  Forward is still JS controlled.
// The hold angle is calc'ed using a simple proportional calculation.

// Sequence:
// (0)Off
// (1)When button pressed, use JS's to drive in Tank Drive mode.
// (2)When button pressed, use JS's to drive in Arcade Drive mode.
// (10)When pov is pressed hold angle as long as pressed else use R JS X
//     Fwd is still controlled by the R JS Y.
//     When not pressed same as (2).
// (30)Auto drive per an array of "hdg, pwr & dist".
//     Init steer method. Turn to hdg then go to 31.
// (31)Steer to heading and distance.  When distance done go to 32.
// (32)Increment Trajectory index.  Check for last one and go to 0, Off.
//     Else go to 30, run next trajectory.
// */

// import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
// import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
// import com.ctre.phoenix.motorcontrol.ControlMode;
// import com.ctre.phoenix.motorcontrol.FeedbackDevice;
// // import com.ctre.phoenix.motorcontrol.can.TalonSRX;

// import edu.wpi.first.wpilibj.drive.DifferentialDrive;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// import frc.io.hdw_io.IO;
// import frc.io.joysticks.JS_IO;
// import frc.util.PropMath;
// import frc.robot.Subsystem.drive.*;

// //Class for taking Joystick inputs and turning them into speed values to move the robot
// public class Drive2 {

//     // Hardware
//     private static WPI_TalonSRX right = IO.drvMasterTSRX_R; // right motor
//     private static WPI_TalonSRX left = IO.drvMasterTSRX_L; // left motor
//     private static WPI_VictorSPX rightSlave = IO.drvFollowerVSPX_R;
//     private static WPI_VictorSPX leftSlave = IO.drvFollowerVSPX_L;
//     private static DifferentialDrive diffDrv = new DifferentialDrive(left, right);
//     private static double enc_L = 0;
//     private static double enc_R = 0;
//     private static double distTPF_L = IO.drvMasterTPF_L; // Left Ticks per Foot
//     private static double distTPF_R = -IO.drvMasterTPF_R; // Right Ticks per Foot
//     private static double dist_L = 0.0;
//     private static double dist_R = 0.0;
//     private static double dist_Avg = 0.0;

//     // General
//     private static int state = 0; // 0=tank, 1=arcade
//     private static int prvState = 0; // 0=tank, 1=arcade
//     private static double strCmd[] = { 0.0, 0.0 }; // Cmds returned, X, Y
//     // Heading Control
//     private static double hdgFB = 0.0; // Gyro reading
//     private static double hdgOut = 0.0; // X (Hdg) output
//     private static double hdgFixedTurn = 0.65; // Fixed value for turning
//     // Distance Control
//     private static double distFB = 0.0; // Dist reading
//     private static double distOut = 0.0; // Y (Fwd) cmd
//     private static double distFixedTurn = -0.50; // Fixed value for turning

//                  /* [0][]=hdg [1][]=dist SP, PB, DB, Mn, Mx, Xcl */
//     private static double[][] parms = { { 0.0, -130.0, 3.0, 0.4, 1.0, 0.20 },
//             /*                       */ { 0.0, 5.5, 0.5, 0.10, 1.0, 0.07 } };
//     private static Steer steer = new Steer(parms); // Used to steer to a hdg with power for distance

//     // Steer to heading at power for distance.
//     private static int trajIdx = 0; // strCmds Index
            
//     //dont use negative power
//     private static double traj[][] = {//{hdg, %pwr, dist}
//                                         { 0.0, 50.0, 7.0 },
//                                         //{ 0.0, 70.0, -0.4 },
//                                         { 90.0, 50.0, 7.0 },
//                                         //{ 90.0, 70.0, -0.4 },
//                                         { 45, -50.0, 1.4 * 7.0 },
//                                         //{ 225.0, 70.0, -0.4 }, 
//                                         { 90.0, 50.0, 7.0 },
//                                         //{ 90.0, 70.0, -0.4 },
//                                         { 135.0, -50.0, 1.4 * 7.0 }, 
//                                        // { -45.0, 70.0, -0.4 },
//                                         { 0, -50.0, 7.0 },
//                                         //{ -180.0, 70.0, -0.4 },
//                                         { 350.0, 50.0, 0.0 }
//                                      };
//     // /* */ {90.0, 70.0, 5.0},
//     // /* */ {-135.0, 70.0, 7.1},
//     // /* */ {90.0, 70.0, 5.0},
//     // /* */ {0.0, 70.0, 5.0},
//     // /* */ {-90.0, 70.0, 5.0},
//     // /* */ {135.0, 70.0, 7.1},
//     // /* */ {-90.0, 70.0, 5.0},
//     // /* */ {0.0, 70.0, -0.5}};

//     // Segmented curve to compensate for min required of 0.65 when rotating.
//     // parm[1][3] is hdgOutMn.
//     // private static double[][] xOutAr = {{-1.0, -parms[0][3], -parms[0][3],
//     // parms[0][3], parms[0][3], 1.0},
//     // /* */ {-1.0, -0.35, 0.0, 0.0, 0.30, 1.0}};

//     // Constructor
//     public Drive2() {
//         init();
//     }

//     // Initialize
//     public static void init() {
//         sdbInit();
//         IO.navX.reset();
//         left.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
//         right.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
//     }

//     // I am the determinator
//     private static void determ() {
//         // Set steering mode
//         if (JS_IO.drive2Off.onButtonPressed())
//             state = 0; // GP 1, A
//         if (JS_IO.drive2Tank.onButtonPressed())
//             state = 1; // GP 2, X
//         if (JS_IO.drive2Arcade.onButtonPressed())
//             state = 2; // GP 3, Y
//         if (JS_IO.drive2AutoTest.onButtonPressed()) { // GP 4, B
//             state = 30; //TODO: should this still be 40?
//             trajIdx = 0;
//         }

//         // Set Auto Hold Angle, 0, 45, 90, ... , 315. Press a pov direction.
//         if (!JS_IO.pov_SP.isNone()) {
//             state = 10; // Set steering mode to hold angle
//         }
//     }

//     // Update drive commands
//     public static void update() {
//         determ();
//         sdbUpdate();
//         stfUpdate();

//         switch (state) {
//             case 0: // Off
//                 diffDrv.tankDrive(0.0, 0.0, true);
//                 prvState = state;
//                 break;
//             case 1: // Tank Mode Drive by JS
//                 diffDrv.tankDrive(-JS_IO.axLeftDrive.get(), -JS_IO.axRightDrive.get(), false);
//                 prvState = state;
//                 break;
//             case 2: // Arcade Mode Drive by JS
//                 hdgOut = JS_IO.axRightX.get();
//                 // hdgOut = BotMath.SegLine(hdgOut, xOutAr); //Compensate for poor turning.
//                 diffDrv.arcadeDrive(-JS_IO.axRightDrive.get(), hdgOut, false);
//                 prvState = state;
//                 break;
//             case 10: // Auto Hold Heading Mode, Fwd/Bkwd by RJSY
//                 // hdgOut steerTo(pov, pwr, dist) else by JS;
//                 // Set Auto Hold Angle, 0, 45, 90, ... , 315. Press a pov direction.
//                 if (!JS_IO.pov_SP.isNone()) {
//                     steer.steerTo(JS_IO.pov_SP.get(), 100.0, 0.0);
//                     strCmd = steer.update(hdgFB, dist_Avg);
//                     hdgOut = strCmd[0];
//                 } else {
//                     hdgOut = JS_IO.axRightX.get();
//                 }
//                 // hdgOut = BotMath.SegLine(hdgOut, xOutAr); //Compensate for poor turning.
//                 diffDrv.arcadeDrive(JS_IO.axRightDrive.get(), hdgOut, false);
//                 prvState = state;
//                 break;
//             /*
//              * This rotates to the heading then resets the dist. and starts running out to
//              * the new distance SP.
//              */
//             case 30: // Init Trajectory, turn to hdg then (31) ...
//                 if (prvState != state) {
//                     steer.steerTo(traj[trajIdx]);
//                     resetDist();
//                 } else {
//                     // Calc heading & dist output. rotation X, speed Y
//                     strCmd = steer.update(hdgFB, dist_Avg);
//                     hdgOut = strCmd[0]; // Get hdg output, Y
//                     distOut = 0.0; // Get distance output, X
//                     // Apply as a arcade joystick input
//                     // hdgOut = BotMath.SegLine(hdgOut, xOutAr); //Compensate for poor turning.
//                     diffDrv.arcadeDrive(-distOut, hdgOut, false);

//                     // Chk if trajectory is done
//                     if (steer.isHdgDone()) {
//                         state = 31; // Chk hdg only
//                         resetDist();
//                     }
//                 }
//                 prvState = state;
//                 break;
//             case 31: // steer Auto Heading and Dist
//                 // Calc heading & dist output. rotation X, speed Y
//                 strCmd = steer.update(hdgFB, dist_Avg);
//                 hdgOut = strCmd[0];
//                 distOut = strCmd[1];
//                 // Apply as a arcade joystick input
//                 // hdgOut = BotMath.SegLine(hdgOut, xOutAr); //Compensate for poor turning.
//                 diffDrv.arcadeDrive(-distOut, hdgOut, false);

//                 // Chk if trajectory is done
//                 if (steer.isDistDone()) {
//                     state = 32; // Chk distance only
//                 }
//                 prvState = state;
//                 break;
//             case 32: // Increment Auto Index & chk for done all traj.
//                 diffDrv.arcadeDrive(0.0, 0.0);
//                 if (prvState != state) {
//                     prvState = state; // Let other states see change of state, COS
//                 } else {
//                     trajIdx++;
//                     state = ((trajIdx) < traj.length) ? 30 : 0; // Next Traj else Off
//                 }
//                 break;
//             /*
//              * This turns on a fixed radius of ~2 at a fixed speed. Then when on heading
//              * resets the dist. and then starts running out the distance.
//              */
//             case 40: // Init Trajectory, turn to hdg then (41) ...
//                 if (prvState != state) {
//                     steer.steerTo(traj[trajIdx]);
//                     resetDist();
//                 } else {
//                     // Calc heading & dist output. rotation X, speed Y
//                     strCmd = steer.update(hdgFB, dist_Avg);
//                     hdgOut = strCmd[0]; // Get hdg output, Y
//                     distOut = 0.0; // Get distance output, X
//                     // Apply as a arcade joystick input
//                     // hdgOut = BotMath.SegLine(hdgOut, xOutAr); //Compensate for poor turning.
//                     System.out.println("Here 1");
//                     if (trajIdx >= 1 && trajIdx <= 10) {
//                         hdgOut = hdgFixedTurn * (hdgOut < 0.0 ? -1.0 : 1.0);
//                         distOut = distFixedTurn;
//                         System.out.println("Here 2");
//                     }
//                     diffDrv.arcadeDrive(distOut, hdgOut, false);

//                     // Chk if trajectory is done
//                     if (steer.isHdgDone()) {
//                         state++; // Chk hdg only
//                         resetDist();
//                     }
//                 }
//                 prvState = state;
//                 break;
//             case 41: // steer Auto Heading and Dist
//                 // Calc heading & dist output. rotation X, speed Y
//                 strCmd = steer.update(hdgFB, dist_Avg);
//                 hdgOut = strCmd[0];
//                 distOut = strCmd[1];
//                 // Apply as a arcade joystick input
//                 // hdgOut = BotMath.SegLine(hdgOut, xOutAr); //Compensate for poor turning.
//                 diffDrv.arcadeDrive(distOut, hdgOut, false);

//                 // Chk if trajectory is done
//                 if (steer.isDistDone()) {
//                     state++; // Chk distance only
//                 }
//                 prvState = state;
//                 break;
//             case 42: // Increment Auto Index & chk for done all traj.
//                 diffDrv.arcadeDrive(0.0, 0.0);
//                 if (prvState != state) {
//                     prvState = state; // Let other states see change of state, COS
//                 } else {
//                     trajIdx++;
//                     state = ((trajIdx) < traj.length) ? state / 10 * 10 : 0; // Next Traj else Off
//                 }
//                 break;
//             default:
//                 diffDrv.tankDrive(0.0, 0.0, true);
//                 System.out.println("Bad Drive State" + state);
//         }
//     }

//     // Update Stuff
//     private static void stfUpdate() {
//         // // Adjust scaling of Output
//         // maxOut += JS_IO.spdShiftUp.onButtonPressed() ? 0.2 :
//         // JS_IO.spdShiftDn.onButtonReleased() ? -0.2 : 0.0;
//         // maxOut = BotMath.Clamp(maxOut, 0.2, 1.0);
//         // diffDrv.setMaxOutput(maxOut);
//         rightSlave.follow(right);
//         leftSlave.follow(left);

//         hdgFB = PropMath.normalizeTo180(IO.navX.getAngle()); // either one of these
//         if (JS_IO.resetGyro.onButtonPressed())
//             IO.navX.reset();

//         if (JS_IO.resetDist.onButtonPressed()) {
//             resetDist();
//         }
//         distFB = calcDist();
//     }

//     private static void resetDist() {
//         left.setSelectedSensorPosition(0, 0, 0);
//         right.setSelectedSensorPosition(0, 0, 0);
//     }

//     private static double calcDist() {
//         enc_L = left.getSelectedSensorPosition();
//         enc_R = right.getSelectedSensorPosition();
//         dist_L = enc_L / distTPF_L;
//         dist_R = enc_R / distTPF_R;
//         dist_Avg = (dist_L + dist_R) / 2.0;
//         return dist_Avg;
//     }

//     // Handle Smartdashbaord Initialization
//     private static void sdbInit() {
//         SmartDashboard.putNumber("Drv Mode", state);

//         SmartDashboard.putNumber("Hdg Out", hdgOut);
//         SmartDashboard.putNumber("Dist Out", distOut);
//         SmartDashboard.putNumber("DistM L", distTPF_L);
//         SmartDashboard.putNumber("DistM R", distTPF_R);

//         SmartDashboard.putNumber("Hdg Fixed", hdgFixedTurn);
//         SmartDashboard.putNumber("Dist Fixed", distFixedTurn);
//     }

//     // Handle Smartdashbaord Updates
//     private static void sdbUpdate() {
//         SmartDashboard.putNumber("Drv Mode", state); // Set by JS btns
//         SmartDashboard.putNumber("JS Y", JS_IO.axRightDrive.get());// Set by JS R Y
//         SmartDashboard.putNumber("JS X", JS_IO.axRightX.get());// Set by JS R X

//         SmartDashboard.putNumber("Hdg FB", hdgFB);
//         SmartDashboard.putNumber("Hdg Out", hdgOut);

//         SmartDashboard.putNumber("Enc L", enc_L);
//         SmartDashboard.putNumber("Enc R", enc_R);
//         distTPF_L = SmartDashboard.getNumber("DistM L", distTPF_L);
//         distTPF_R = SmartDashboard.getNumber("DistM R", distTPF_R);
//         SmartDashboard.putNumber("Dist L", dist_L);
//         SmartDashboard.putNumber("Dist R", dist_R);
//         SmartDashboard.putNumber("Dist A", dist_Avg);
//         SmartDashboard.putNumber("Dist FB", distFB);
//         SmartDashboard.putNumber("Dist Out", distOut);
//         SmartDashboard.putNumber("Traj Idx", trajIdx);
//         SmartDashboard.putNumber("Hdg Fixed", hdgFixedTurn);
//         SmartDashboard.putNumber("Dist Fixed", distFixedTurn);
//     }
// }
