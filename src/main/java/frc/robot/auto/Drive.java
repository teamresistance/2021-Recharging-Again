// package frc.subsystem.drive;
// /*
// Original Author: Jim Hofmann
// History:
// JCH - 12/6/2019 - Original release

// TODO: Checkout

// Desc: Controls a tank style drive system.  Can switch between Tank & Arcade joysticks.
//     The maximum responce can be adjusted, scaled to the JSs -1 to 1 input.
//     A fixed angle can be held using the pov buttons.  Forward is still JS controlled.
//     The hold angle is calc'ed using a simple proportional calculation.
// */

// import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

// import org.opencv.core.Mat;

// import com.ctre.phoenix.motorcontrol.FeedbackDevice;
// // import com.ctre.phoenix.motorcontrol.can.TalonSRX;

// import edu.wpi.first.wpilibj.drive.DifferentialDrive;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// import frc.io.hdw_io.IO;
// import frc.io.joysticks.JS_IO;
// import frc.util.BotMath;
// import frc.util.SimpleProp;
// import frc.util.timers.OnDly;

// //Class for taking Joystick inputs and turning them into speed values to move the robot
// public class Drive {

//     private static WPI_TalonSRX right = IO.rightMtr; // right motor
//     private static WPI_TalonSRX left = IO.leftMtr;   // left motor
//     private static DifferentialDrive diffDrv = new DifferentialDrive(left, right);
//     private static int enc_L = 0;
//     private static int enc_R = 0;
//     private static double distTPF_L = -427.0; //Ticks per Ft
//     private static double distTPF_R = 427.0;
//     private static double dist_L = 0.0;
//     private static double dist_R = 0.0;
//     private static double dist_Avg = 0.0;
//     // Steer to heading at power for distance.
//     private static Steer steer = new Steer();
//     private static int trajIdx = 0;  // strCmds Index
//     private static int prvTrajIdx = 0;  // strCmds Index
//     //                                   {hdg, pwr, dist}
//     private static double traj[][] = {{0.0, 60.0, -10.0},
// /*                               */   {180.0, 60.0, -5.0}};
// // /*                               */   {90.0, 60.0, -5.0},
// // /*                               */   {-150.0, 60.0, -11.0},
// // /*                               */   {90.0, 60.0, -5.0},
// // /*                               */   {0.0, 60.0, -10.0},
// // /*                               */   {-90.0, 60.0, -5.0},
// // /*                               */   {150.0, 60.0, -11.0},
// // /*                               */   {-90.0, 60.0, -5.0},
// // /*                               */   {0.0, 60.0, 0.0}};
//     // General
//     private static int state = 0;       // 0=tank, 1=arcade
//     private static int prvState = 0;    // 0=tank, 1=arcade
//     private static double tmpD = 0.0;       //temp double
//     //Distance Control
//     private static double distSP = 0.0;     //Distance
//     private static double distFB = 0.0;     //Distance FB, Avg
//     private static double distPB = 5.5;     //Propband, +/-1.0 for this error
//     private static double distDB = 0.5;     //0.0 output if inside this 
//     private static double distOutMn = 0.0;  //Min output outside of DB
//     private static double distOutMx = 1.0;  //Max output
//     private static double distAccelLmt = 0.05;  //Y acceleration limit
//     private static double distOut = 0.0;    //Y cmd
//     private static double distPrvOut = 0.0; //Prv Y cmd
//     private static OnDly distOnDly = new OnDly(500);
//     private static SimpleProp distProp = new SimpleProp(distSP, distPB, distDB, distOutMn, distOutMx);
//     //Heading Control
//     private static double hdgSP = 0.0;      //Hold angle
//     private static double hdgFB = 0.0;      //Gyro reading
//     private static double hdgEFB = 0.0;     //Normalized err, FB - SP
//     private static double hdgPB = -150.0;    //Propband, +/-1.0 for this Angle error
//     private static double hdgDB = 3.0;      //0.0 output if inside this 
//     private static double hdgOutMn = 0.1;   //Min output outside of DB
//     private static double hdgOutMx = 1.0;   //Max output
//     private static double hdgAccelLmt = 0.1;   //Y acceleration limit
//     private static double hdgOut = 0.0;     //Hdg output
//     private static double hdgPrvOut = 0.0;  //Prv Hdg output
//     private static OnDly hdgOnDly = new OnDly(500);
//     private static SimpleProp hdgProp = new SimpleProp(hdgSP, hdgPB, hdgDB, hdgOutMn, hdgOutMx);

//     //Segmented curve to compensate for min required of 0.65 when rotating. 
//     private static double[][] inOutAr  = {{-1.0, -hdgOutMn, -hdgOutMn, hdgOutMn, hdgOutMn, 1.0},
//                                           {-1.0, -0.35,       0.0,      0.0, 0.30, 1.0}};

//     // Constructor
//     public Drive() {
//         init();
//     }

//     // Initialize
//     public static void init(){
//         // diffDrv.setMaxOutput(maxOut);
//         sdbInit();
//         IO.ahrs.reset();
//         left.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
//         right.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
//     }

//     // I am the determinator
//     private static void determ(){
//         //Set steering mode
//         if(JS_IO.offMode.get()) state = 0;      //GP 1
//         if(JS_IO.tankMode.get()) state = 1;     //GP 2
//         if(JS_IO.arcadeMode.get()) state = 2;   //GP 3
//         if(JS_IO.autoTest.onButtonPressed()){   //GP 4
//             state = 30;
//             trajIdx = 0;
//         }

//         //Set Auto Hold Angle, 0, 45, 90, ... , 315.  Press a pov direction.
//         if(!JS_IO.pov_SP.isNone()){
//             hdgSP = JS_IO.pov_SP.get();
//             // hdgProp.setSP(BotMath.normalizeTo180(JS_IO.pov_SP.get()));
//             state = 10;               //Set steering mode to hold angle
//         }
//     }

//     // Update drive commands
//     public static void update() {
//         determ();
//         sdbUpdate();
//         stfUpdate();

//         switch(state){
//             case 0:     //Off
//                 diffDrv.tankDrive(0.0, 0.0, true);
//                 prvState = state;
//                 break;
//             case 1:     //Tank Mode Drive by JS
//                 diffDrv.tankDrive(JS_IO.dvrLY.get(), JS_IO.dvrRY.get(), false);
//                 prvState = state;
//                 break;
//             case 2:     //Arcade Mode Drive by JS
//                 hdgOut = JS_IO.dvrRX.get();
//                 hdgOut = BotMath.SegLine(hdgOut, inOutAr);  //Compensate for poor turning.
//                 diffDrv.arcadeDrive(JS_IO.dvrRY.get(), hdgOut, false);
//                 prvState = state;
//                 break;
//             case 10:     //Auto Hold Heading Mode, Fwd/Bkwd by RJSY
//                 // hdgOut = BotMath.calcRotation(hdgFB, hdgSP, hdgPB, hdgDB, hdgMn, hdgMx);
//                 hdgOut = BotMath.normalizeTo180(hdgFB - hdgSP);
//                 hdgProp.setSP(0.0);
//                 hdgOut = hdgProp.calcProp(hdgOut, false);
//                 // hdgOut = accelLimiter(hdgOut, hdgPrvOut, hdgAccelLmt, hdgOutMn);
//                 hdgPrvOut = hdgOut;
//                 hdgOut = BotMath.SegLine(hdgOut, inOutAr);  //Compensate for poor turning.
//                 diffDrv.arcadeDrive(JS_IO.dvrRY.get(), hdgOut, false);
//                 prvState = state;
//                 break;
//             case 30:     //Auto Heading and Dist Mode
//                 if(prvState != state){
//                     initTraj(); //Initialize trajectory seeting from array
//                 }else{
//                     // Calc heading output, rotation, X
//                     hdgOut = hdgUpdate(hdgEFB, hdgPrvOut, hdgAccelLmt); //Err normalized, Prop SP is 0
//                     hdgPrvOut = hdgOut;
//                     hdgOut = BotMath.SegLine(hdgOut, inOutAr);  //Compensate for poor turning.
//                     // Calc distance output, Y
//                     distOut = distUpdate(distFB, distPrvOut, distAccelLmt);
//                     distPrvOut = distOut;
//                     // Apply as a arcade joystick input
//                     diffDrv.arcadeDrive(distOut, hdgOut, false);

//                     //Chk if trajectory is done
//                     if(distSP != 0.0){
//                         if(distOut == 0.0) state = 31;    //Chk distance only
//                         // if(Math.abs(distFB - distSP) < distDB) state = 31;    //Chk distance only
//                     }else{
//                         if(Math.abs(hdgFB - hdgSP) < hdgDB) state = 31;    //Chk distance only
//                     }
//                 }
//                 prvState = state;
//                 break;
//             case 31:     //Increment Auto Index & chk for done all traj.
//                 diffDrv.arcadeDrive(0.0, 0.0);
//                 if(prvState != state){
//                     prvState = state;   //Let other states see change of state, COS
//                 }else{
//                     trajIdx++;
//                     state = ((trajIdx /2) < traj.length) ? 30 : 0;
//                     System.out.println("Idx- " + trajIdx);
//                 }
//                 break;
//             default:
//                 diffDrv.tankDrive(0.0, 0.0, true);
//                 System.out.println("Bad Drive State" + state);
//         }
//     }

//     private static void initTraj(){
//         int x = trajIdx / 2;
//         hdgSP = traj[trajIdx / 2][0];  // Heading
//         // hdgProp.setOutMx(hdgOutMn + ((hdgOutMx - hdgOutMn) *
//         //                  strTraj[strTrajIdx][1] / 100.0));  // Pct of out control range
//         // hdgOutMx = strTraj[strTrajIdx][1];  // Max Power
//         hdgProp.setSP(0.0); //Real err must be norm'ed. SP is 0 err.

//         distProp.setOutMx(distOutMn + ((distOutMx - distOutMn) *
//                           traj[trajIdx / 2][1] / 100.0));  // Pct of out control range
//         distSP = (trajIdx % 2 == 0) ? 0.0 : traj[trajIdx / 2][2]; // Distance ft
//         distProp.setSP(distSP);
//         resetDist();
// }

// private static double hdgUpdate(double inFB, double prvOut, double accelLmt){
//     double outVal = 0.0;
//     outVal = hdgProp.calcProp(inFB, false);    //SP is 0.0 error
//     // outVal = accelLimiter(outVal, prvOut, accelLmt, hdgOutMn);
//     return outVal;
// }

// private static double distUpdate(double inFB, double prvOut, double accelLmt){
//     double outVal = 0.0;
//     outVal = distProp.calcProp(inFB, false);
//     outVal = accelLimiter(outVal, prvOut, accelLmt, distOutMn);
//     return outVal;
// }

// // Update Stuff
// private static double accelLimiter(double psntVal, double prvVal, double accelLmt, double outMn){
//     if(Math.abs(psntVal - prvVal) > accelLmt && psntVal != 0.0){
//         psntVal = psntVal < 0.0 ? Math.min(prvVal - accelLmt, -outMn) :    //Value neg.
//         /*                      */Math.max(prvVal + accelLmt, outMn);      //else pos.
//     }
//     return psntVal;
// }

// // Update Stuff
//     private static void stfUpdate(){
//         // // Adjust scaling of Output
//         // maxOut += JS_IO.spdShiftUp.onButtonPressed()  ?  0.2 :
//         //           JS_IO.spdShiftDn.onButtonReleased() ? -0.2 : 0.0;
//         // maxOut = BotMath.Clamp(maxOut, 0.2, 1.0);
//         // diffDrv.setMaxOutput(maxOut);

//         hdgFB = BotMath.normalizeTo180(IO.ahrs.getAngle()); //either one of these
//         hdgEFB = BotMath.normalizeTo180(hdgFB - hdgSP);
//         if(JS_IO.resetGyro.get()) IO.ahrs.reset();

//         enc_L = left.getSelectedSensorPosition();
//         enc_R = right.getSelectedSensorPosition();
//         if(JS_IO.resetDist.onButtonPressed()){
//             resetDist();
//         }
//         calcDist();
//     }

//     private static void resetDist(){
//         left.setSelectedSensorPosition(0, 0, 0);
//         right.setSelectedSensorPosition(0, 0, 0);
// }

//     private static void calcDist(){
//         dist_L = distTPF_L * enc_L;
//         dist_R = distTPF_R * enc_R;
//         dist_Avg = (dist_L + dist_R)/2.0;
//         distFB = dist_Avg;
// }

//     // Handle Smartdashbaord Initialization
//     private static void sdbInit(){
//         SmartDashboard.putNumber("Drv Mode", state);

//         SmartDashboard.putNumber("Hdg SP", hdgProp.getSP());
//         SmartDashboard.putNumber("Hdg PB", hdgProp.getPB());
//         SmartDashboard.putNumber("Hdg DB", hdgProp.getDB());
//         SmartDashboard.putNumber("Hdg Mn", hdgProp.getOutMn());
//         SmartDashboard.putNumber("Hdg Mx", hdgProp.getOutMx());
//         SmartDashboard.putNumber("Hdg Out",hdgOut);

//         SmartDashboard.putNumber("Dist SP", distProp.getSP());
//         SmartDashboard.putNumber("Dist PB", distProp.getPB());
//         SmartDashboard.putNumber("Dist DB", distProp.getDB());
//         SmartDashboard.putNumber("Dist Mn", distProp.getOutMn());
//         SmartDashboard.putNumber("Dist Mx", distProp.getOutMx());
//         SmartDashboard.putNumber("Dist Out",distOut);
//         SmartDashboard.putNumber("DistM L", distTPF_L);
//         SmartDashboard.putNumber("DistM R", distTPF_R);
//     }

//     // Handle Smartdashbaord Updates
//     private static void sdbUpdate(){
//         SmartDashboard.putNumber("Drv Mode", state);  //Set by JS btns
//         SmartDashboard.putNumber("JS Y", JS_IO.dvrRY.get());//Set by JS R Y
//         SmartDashboard.putNumber("JS X", JS_IO.dvrRX.get());//Set by JS R X

//         SmartDashboard.putNumber("Hdg FB", hdgFB);
//         SmartDashboard.putNumber("Hdg SP", hdgSP);
//         hdgProp.setPB(SmartDashboard.getNumber("Hdg PB", hdgPB));
//         hdgProp.setDB(SmartDashboard.getNumber("Hdg DB", hdgDB));
//         hdgProp.setOutMn(SmartDashboard.getNumber("Hdg Mn", hdgOutMn));
//         // hdgProp.setOutMx(SmartDashboard.getNumber("Hdg Mx", hdgOutMx));
//         SmartDashboard.putNumber("Hdg Mx",hdgOutMx);
//         SmartDashboard.putNumber("Hdg Out",hdgOut);

//         SmartDashboard.putNumber("Enc L", enc_L);
//         SmartDashboard.putNumber("Enc R", enc_R);
//         distTPF_L = SmartDashboard.getNumber("DistM L", distTPF_L);
//         distTPF_R = SmartDashboard.getNumber("DistM R", distTPF_R);
//         SmartDashboard.putNumber("Dist L", dist_L);
//         SmartDashboard.putNumber("Dist R", dist_R);
//         SmartDashboard.putNumber("Dist A", dist_Avg);
//         SmartDashboard.putNumber("Dist FB", distFB);
//         SmartDashboard.putNumber("Hdg SP", hdgSP);
//         distProp.setPB(SmartDashboard.getNumber("Dist PB", distPB));
//         distProp.setDB(SmartDashboard.getNumber("Dist DB", distDB));
//         distProp.setOutMn(SmartDashboard.getNumber("Dist Mn", distOutMn));
//         // distProp.setOutMx(SmartDashboard.getNumber("Dist Mx", distOutMx));
//         SmartDashboard.putNumber("Dist Mx",distOutMx);
//         SmartDashboard.putNumber("Dist Out",distOut);
//         SmartDashboard.putNumber("Traj Idx",trajIdx);
//     }
// }