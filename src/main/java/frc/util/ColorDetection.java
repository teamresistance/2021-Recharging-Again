// package frc.util;

// // import edu.wpi.first.wpilibj.TimedRobot;
// // import edu.wpi.first.wpilibj.DriverStation;
// // import edu.wpi.first.wpilibj.I2C;
// // import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj.util.Color;
// import frc.io.hdw_io.IO;

// // import com.revrobotics.ColorSensorV3;
// // import com.revrobotics.ColorSensorV3.RawColor;
// import com.revrobotics.ColorMatchResult;
// //import com.fasterxml.jackson.databind.deser.DataFormatReaders.Match;
// import com.revrobotics.ColorMatch;

// public class ColorDetection {
//     /**
//      * A Rev Color Match object is used to register and detect known colors. This
//      * can be calibrated ahead of time or during operation.
//      * 
//      * This object uses a simple euclidian distance to estimate the closest match
//      * with given confidence range.
//      */
//     private final ColorMatch m_colorMatcher = new ColorMatch();

//     public ColorMatchResult match = null;

//     /**
//      * Note: Any example colors should be calibrated as the user needs, these are
//      * here as a basic example.
//      */
//     private Color lowBlue = new Color(0.134, 0.424, 0.424);
//     private Color highBlue = new Color(0.144, 0.436, 0.437);
//     // private Color lowRed

//     private Color detected = null;


//     public void init() {
//         m_colorMatcher.addColorMatch(lowBlue);
//     }

//     public void update() {
//         detected = IO.m_colorSensor.getColor();

//         match = m_colorMatcher.matchClosestColor(detected);
//     }

//     private static String rcolor = "";

//     public static void setrcolor() {
//         // if (IO.m_colorSensor.getRed() >= 0.134 && IO.m_colorSensor.getRed() <= 0.144) {
//         //     rcolor = "blue";
//         // } else if (ColorMatch.makeColor >= 0.172 && ColorMatch.makeColor <= 0.184) {
//         //     rcolor = "green";
//         // } else if (ColorMatch.makeColor >= 0.464 && ColorMatch.makeColor <= 0.475) {
//         //     rcolor = "red";
//         // } else if (ColorMatch.makeColor >= 0.306 && ColorMatch.makeColor <= 0.318) {
//         //     rcolor = "yellow";
//         // } else
//         //     rcolor = "unknown substance";
    
//     }

//     private static String gcolor;

//     // public static void setgcolor(int ColorMatch.makeColor) {
//     //     if (ColorMatch.makeColor >= 0.424 && ColorMatch.makeColor <= 0.436) {
//     //         gcolor = "blue";
//     //     } else if (ColorMatch.makeColor >= 0.567 && ColorMatch.makeColor <= 0.578) {
//     //         gcolor = "green";
//     //     } else if (ColorMatch.makeColor >= 0.347 && ColorMatch.makeColor <= 0.358) {
//     //         gcolor = "red";
//     //     } else if (ColorMatch.makeColor >= 0.551 && ColorMatch.makeColor <= 0.564) {
//     //         gcolor = "yellow";
//     //     } else
//     //         gcolor = "unknown substance";
//     // }

//     private static String bcolor;

//     // public static void setbcolor(int ColorMatch.makeColor) {
//     //     if (ColorMatch.makeColor >= 0.424 && ColorMatch.makeColor <= 0.437) {
//     //         bcolor = "blue";
//     //     } else if (ColorMatch.makeColor >= 0.250 && ColorMatch.makeColor <= 0.262) {
//     //         bcolor = "green";
//     //     } else if (ColorMatch.makeColor >= 0.146 && ColorMatch.makeColor <= 0.160) {
//     //         bcolor = "red";
//     //     } else if (ColorMatch.makeColor >= 0.122 && ColorMatch.makeColor <= 0.134) {
//     //         bcolor = "yellow";
//     //     } else
//     //         bcolor = "unknown substance";
//     // }

//   public static String getfinalColor(){
//       String colorString = "";

//       if (rcolor == "blue" && gcolor == "blue" && bcolor == "blue") {
//           colorString = "Blue";
//       } else if (rcolor == "green" && gcolor == "green" && bcolor == "green") {
//           colorString = "Green";
//       } else if (rcolor == "red" && gcolor == "red" && bcolor == "red") {
//           colorString = "Red";
//       } else if (rcolor == "yellow" && gcolor == "yellow" && bcolor == "yellow") {
//           colorString = "Yellow";
//       } else
//           colorString = "Unknown Substance";

//           return colorString;
    
//   }
// }

