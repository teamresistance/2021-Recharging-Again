package frc.robot.Subsystem;

import frc.io.hdw_io.IO;

public class WoF {

    private static int state;

    public static void init() {
        state = 0;
    }

    private static void determ() {
        //toggle down and up


        
    }

    public static void update() {
        sdbUpdate();
        determ();
        switch (state) {
        case 0:

            break;
        case 1:

            break;
        default:
            break;
        }
    }

    public static void sdbUpdate() {

    }

    private static void cmdUpdate() { 
     
    }

    public static int getState() {
        return state;
    }

}