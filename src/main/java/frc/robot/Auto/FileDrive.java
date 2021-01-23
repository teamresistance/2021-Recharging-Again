package frc.robot.Auto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.PrivateKey;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import frc.robot.DriveMath.DriveType;
import frc.robot.Subsystem.drive.Drive;

public class FileDrive implements ICommand {

    // private ArrayList<Double> xpoints = new ArrayList<>();
    // private ArrayList<Double> ypoints = new ArrayList<>();
    // private ArrayList<Double> zpoints = new ArrayList<>();

    private ArrayList<Double> leftYpoints = new ArrayList<>();
    private ArrayList<Double> rightYpoints = new ArrayList<>();

    private File file;
    private String[] values;
    int i;

    private boolean isFinished;
    @Override
    public void init() {
        redo();
        file = new File("/home/lvuser/Data.csv");
        try {
            fileRead();
            SmartDashboard.putString("Auto Joy Drive State", "init");
        } catch (Exception e) {
            e.printStackTrace();
            SmartDashboard.putString("Auto Joy Drive State", "failure to init");
        }
    }

    @Override
    public void redo(){
        i = 0;
        isFinished = false;
    }

    @Override
    public void execute() {

     //   for (i = 0; i < xpoints.size()-1; i++) {
            //Drive.drive(DriveType.STICK_FIELD, (0.7*xpoints.get(i)), (-0.7*ypoints.get(i)), (0.7*zpoints.get(i)), IO.navX.getNormalizedAngle());
            SmartDashboard.putString("Auto Joy Drive State", "driving...");
      //  }
      //  if(i >= (xpoints.size()-1)){
     //  }
        Drive.cmdUpdate(0.7*leftYpoints.get(i), 0.7*rightYpoints.get(i));
        
      if(i < (leftYpoints.size() - 1)){
          i++;
      }else{
        isFinished = true;
        Drive.cmdUpdate(0,0);
        SmartDashboard.putString("Auto Joy Drive State", "done");
      }
    }

    @Override
    public boolean done() {
        return isFinished;
    }

    private void fileRead() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null) {
            values = st.split(",");
            leftYpoints.add(new Double(values[0]));
            rightYpoints.add(new Double(values[1]));            
        }
    }
}
