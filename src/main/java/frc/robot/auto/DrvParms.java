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

// //Class for taking Joystick inputs and turning them into speed values to move the robot
// public class DrvParms {

//     private static int enc_L = 0;
//     private static int enc_R = 0;
//     private static int encStart_L = 0;
//     private static int encStart_R = 0;
//     private static double distMult_L = -0.002340;
//     private static double distMult_R = 0.002340;
//     private static double dist_L = 0.0;
//     private static double dist_R = 0.0;
//     private static double dist_Avg = 0.0;
//     // Steer to heading at power for distance.
//     private static Steer steer = new Steer();
//     private static int strTrajIdx = 0;  // strCmds Index
//     //                                   {hdg, pwr, dist}
//     private static double strTraj[][] = {{0.0, 50.0, 10.0},
// /*                                  */   {90.0, 30.0, 7.0},
// /*                                  */   {90.0, 50.0, -3.0},
// /*                                  */   {0.0, 50.0, -10.0}};
// }