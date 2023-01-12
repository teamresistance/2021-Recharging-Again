package frc.io.hdw_io;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.motorcontrol.Victor;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.util.CoorSys;
import frc.io.hdw_io.util.Encoder_Tln;
import frc.io.hdw_io.util.ISolenoid;
import frc.io.hdw_io.util.InvertibleDigitalInput;
import frc.io.hdw_io.util.InvertibleSolenoid;
import frc.io.hdw_io.util.NavX;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
// import com.revrobotics.ColorSensorV3;

/* temp to fill with latest faults */
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import com.revrobotics.ColorSensorV3;

public class IO {
    // navX
    public static NavX navX = new NavX(SPI.Port.kMXP);

    // PDP
    public static PowerDistribution pdp = new PowerDistribution(21,ModuleType.kCTRE);

    // Air
    public static Compressor compressor = new Compressor(0,PneumaticsModuleType.CTREPCM);
    public static Relay compressorRelay = new Relay(0);

    //-__
    // Drive
    public static WPI_TalonSRX drvMasterTSRX_L = new WPI_TalonSRX(1); // Cmds left wheels. Includes encoders
    public static WPI_TalonSRX drvMasterTSRX_R = new WPI_TalonSRX(5); // Cmds right wheels. Includes encoders
    public static WPI_VictorSPX drvFollowerVSPX_L = new WPI_VictorSPX(2); // Resrvd 3 & 4 maybe
    public static WPI_VictorSPX drvFollowerVSPX_R = new WPI_VictorSPX(6); // Resrvd 7 & 8 maybe
    //As of 2022 DifferentialDrive no longer inverts the right motor.  Do this in the motor controller.
    public static DifferentialDrive diffDrv_M = new DifferentialDrive(IO.drvMasterTSRX_L, IO.drvMasterTSRX_R);

    public static final double drvMasterTPF_L = 368.4;  // 1024 t/r (0.5' * 3.14)/r 9:60 gr = 385.4  calibrated= 364.63
    public static final double drvMasterTPF_R = 368.4;  // 1024 t/r (0.5' * 3.14)/r 9:60 gr = 385.4  calibrated= 364.63
    public static Encoder_Tln drvEnc_L = new Encoder_Tln(drvMasterTSRX_L, drvMasterTPF_L);  //Interface for feet, ticks, reset
    public static Encoder_Tln drvEnc_R = new Encoder_Tln(drvMasterTSRX_R, drvMasterTPF_R);
    public static void drvFeetRst() { drvEnc_L.reset(); drvEnc_R.reset(); }
    public static double drvFeet() { return (drvEnc_L.feet() + drvEnc_R.feet()) / 2.0; }

    public static CoorSys coorXY = new CoorSys(navX, drvEnc_L, drvEnc_R);   //CoorXY & drvFeet

    public static WPI_TalonSRX shooterTSRX = new WPI_TalonSRX(9);
    public static Encoder_Tln shooter_Encoder = new Encoder_Tln(shooterTSRX, 0);
    public static ISolenoid shooterHoodUp = new InvertibleSolenoid(PneumaticsModuleType.CTREPCM, 4, false);

    // Turret-- LL defines itself
    public static Victor turretRot = new Victor(4); // Turret rotation motor
    public static AnalogPotentiometer turretPosition = new AnalogPotentiometer(0, -370, 190);
    public static InvertibleDigitalInput turCCWLimitSw = new InvertibleDigitalInput(4, true); // Critical, DO NOT
    public static InvertibleDigitalInput turCWLimitSw = new InvertibleDigitalInput(5, true);// EXCEED limit swithes
    // public static Counter turCCWCntr = new Counter(4);  //Hdw cntr to trap lmt sw.  Must be cleared
    // public static Counter turCWCntr = new Counter(5);   //Interupt driven.

    // Injector, injects balls in to the shooter.
    public static VictorSPX injector4Whl = new VictorSPX(10);
    public static Victor injectorPickup = new Victor(8);
    public static InvertibleSolenoid injectorFlipper = new InvertibleSolenoid(PneumaticsModuleType.CTREPCM, 5, false);

    // Revolver, stores up to 5 balls
    public static Victor revolverRot = new Victor(5);
    public static InvertibleDigitalInput revolerIndexer = new InvertibleDigitalInput(0, true);
    public static InvertibleDigitalInput revRcvSlotOpen = new InvertibleDigitalInput(1, false);
    // public static Timer revTimer;

    // Snorfler
    public static Victor snorfFeedMain = new Victor(9);
    public static Victor snorfFeedScdy = new Victor(6);
    public static ISolenoid snorflerExt = new InvertibleSolenoid(PneumaticsModuleType.CTREPCM, 6, false); // Extends both feeders
    public static InvertibleDigitalInput snorfHasBall = new InvertibleDigitalInput(2, false);

    // Climb
    public static Victor climberHoist = new Victor(3); // Extends climber
    public static ISolenoid climberExt = new InvertibleSolenoid(PneumaticsModuleType.CTREPCM, 7, false);

    // ---------- WoF, Color Sensor -----------------
    /**
     * Change the I2C port below to match the connection of your color sensor
     */
    // private static final I2C.Port i2cPort = I2C.Port.kOnboard;

    /**
     * A Rev Color Sensor V3 object is constructed with an I2C port as a parameter.
     * The device will be automatically initialized with default parameters.
     */
    // public static ColorSensorV3 ballColorSensor = new ColorSensorV3(i2cPort);

    // Initialize any hardware here
    public static void init() {
        // revTimer = new Timer(0);
        drvsInit();
        motorsInit();
        coorXY.reset();
        // turCCWCntr.setUpSourceEdge(true, true);
        // turCWCntr.setUpSourceEdge(true, true);
    }


    public static void drvsInit() {
        drvMasterTSRX_L.configFactoryDefault();
        drvMasterTSRX_R.configFactoryDefault();
        //---- Invertion set chged in 2022.  Leave these TRUE! ------
        drvMasterTSRX_L.setInverted(true); // Inverts motor direction and encoder if attached
        drvMasterTSRX_R.setInverted(true); // Inverts motor direction and encoder if attached
        drvMasterTSRX_L.setSensorPhase(false); // Adjust this to correct phasing with motor
        drvMasterTSRX_R.setSensorPhase(false); // Adjust this to correct phasing with motor
        drvMasterTSRX_L.setNeutralMode(NeutralMode.Brake); // change it back
        drvMasterTSRX_R.setNeutralMode(NeutralMode.Brake); // change it back

        // Tells left and right victors to follow the master
        //TODO: change the brake stuff to coast

        drvFollowerVSPX_L.configFactoryDefault();
        drvFollowerVSPX_L.setInverted(false);
        drvFollowerVSPX_L.setNeutralMode(NeutralMode.Brake); // change it back
        // drvFollowerVSPX_L.set(ControlMode.Follower, drvMasterTSRX_L.getDeviceID());  //Doesn't work pn Victor SPX
        drvFollowerVSPX_R.configFactoryDefault();
        drvFollowerVSPX_R.setInverted(true);
        drvFollowerVSPX_R.setNeutralMode(NeutralMode.Brake); // change it back
        // drvFollowerVSPX_R.set(ControlMode.Follower, drvMasterTSRX_R.getDeviceID());  //Doesn't work pn Victor SPX

        drvMasterTSRX_L.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
        drvMasterTSRX_R.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
    }

    // Shooter, Inject & Pikup Initialize
    public static void motorsInit() {
        shooterTSRX.configFactoryDefault();
        shooterTSRX.setInverted(false);
        shooterTSRX.setNeutralMode(NeutralMode.Brake);

        injector4Whl.configFactoryDefault();
        injector4Whl.setInverted(true);
        injector4Whl.setNeutralMode(NeutralMode.Coast);

        injectorPickup.setInverted(false);
        turretRot.setInverted(true);
        revolverRot.setInverted(true);
        snorfFeedMain.setInverted(true);
        snorfFeedScdy.setInverted(true);
        climberHoist.setInverted(false);

        SmartDashboard.putNumber("Robot/Feet Pwr2", drvAutoPwr);
    }

    public static int revolverCntr = 0; // Count revolver rotations
    public static boolean prvRevIndex = true;

    public static double drvFeetChk = 0.0;  //Testing Drv_Auto rdg.
    public static double drvAutoPwr = 0.9;  //Testing

    public static void update() {
        victorSPXfollower();
        SmartDashboard.putNumber("Robot/Feet", drvFeet());
        SmartDashboard.putNumber("Robot/Feet Chk", drvFeetChk);  //Testing
        SmartDashboard.putNumber("Robot/EncTicks L", drvEnc_L.ticks());
        SmartDashboard.putNumber("Robot/EncTicks R", drvEnc_R.ticks());
        SmartDashboard.putNumber("Robot/Mtr0 Cmd", drvMasterTSRX_R.get());
        SmartDashboard.putNumber("Robot/Mtr1 Cmd", drvFollowerVSPX_R.get());
        SmartDashboard.putNumber("Robot/Mtr12 Cmd", drvMasterTSRX_L.get());
        SmartDashboard.putNumber("Robot/Mtr11 Cmd", drvFollowerVSPX_L.get());
        drvAutoPwr = SmartDashboard.getNumber("Robot/Feet Pwr2", drvAutoPwr);  //Testing
        coorXY.update();    //Update the XY location
    }

    public static void victorSPXfollower() {
        drvFollowerVSPX_L.follow(drvMasterTSRX_L);
        drvFollowerVSPX_R.follow(drvMasterTSRX_R);
    }
}
