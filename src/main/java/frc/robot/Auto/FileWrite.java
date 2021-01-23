package frc.robot.Auto;

import java.io.IOException;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.joysticks.JS_IO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileWrite {
    // private ArrayList<Double> xpoints = new ArrayList<>();
    // private ArrayList<Double> ypoints = new ArrayList<>();
    // private ArrayList<Double> zpoints = new ArrayList<>();

    private ArrayList<Double> leftYpoints = new ArrayList<>();
    private ArrayList<Double> rightYpoints = new ArrayList<>();
    private boolean isRecording;

    public FileWrite() {
        isRecording = false;
        SmartDashboard.putString("state", "init");
    }

    public void fileRecord() {
        if (JS_IO.record.onButtonPressed() && !isRecording) {
            isRecording = true;
            SmartDashboard.putString("state", "start recording");
        } else if (JS_IO.record.onButtonPressed() && isRecording) {
            SmartDashboard.putString("state", "print to file");
            isRecording = false;
            try {
                fileWrite();
                SmartDashboard.putString("state", "finish print");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (isRecording) {
            SmartDashboard.putString("state", "isReccording");
            // xpoints.add(JS_IO.rightJoystick.getX());
            // ypoints.add(JS_IO.rightJoystick.getY());
            // zpoints.add(JS_IO.leftJoystick.getX());

            leftYpoints.add(JS_IO.axLeftDrive.get());
            rightYpoints.add(JS_IO.axRightDrive.get());

        }
    }
    

    public void fileWrite() throws IOException {
        BufferedWriter outputWriter = null;
        File file = new File("/home/lvuser/Data.csv");
      /*  if (file.createNewFile())
        {
            System.out.println("File is created!");
        } else {
            System.out.println("File already exists.");
        }
        */
        outputWriter = new BufferedWriter(new FileWriter(file));    
        // for (int i = 0; i < xpoints.size()-1; i++) {
        //     // Maybe:
        //     outputWriter.write(xpoints.get(i) + "," + ypoints.get(i) + "," + zpoints.get(i));
        //     outputWriter.newLine();
        // }

        for (int i = 0; i < leftYpoints.size() - 1; i++) {
            // Maybe:
            outputWriter.write(leftYpoints.get(i) + "," + rightYpoints.get(i));
            outputWriter.newLine();
        }
        outputWriter.flush();
        outputWriter.close();
    }

}
