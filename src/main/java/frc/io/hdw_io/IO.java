package frc.io.hdw_io;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Subsystem.Revolver;
import frc.util.Timer;

// import com.revrobotics.ColorSensorV3;

/* temp to fill with latest faults */
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;

public class IO {
    // navX
    public static NavX navX = new NavX();

    // PDP
    public static PowerDistributionPanel pdp = new PowerDistributionPanel(21);

    // Air
    public static Compressor compressor = new Compressor(22);
    public static Relay compressorRelay = new Relay(0);

    // Drive
    public static WPI_TalonSRX drvMasterTSRX_L = new WPI_TalonSRX(1); // Cmds left wheels. Includes encoders
    public static WPI_TalonSRX drvMasterTSRX_R = new WPI_TalonSRX(5); // Cmds right wheels. Includes encoders
    public static final double drvMasterTPF_L = 385.40; // 1024 t/r (0.5' * 3.14)/r 9:60 gr
    public static final double drvMasterTPF_R = -385.40; // 1024 t/r (0.5' * 3.14)/r 9:60 gr
    public static Encoder drvEnc_L = new Encoder(drvMasterTSRX_L, drvMasterTPF_L);  //Interface for feet, ticks, reset
    public static Encoder drvEnc_R = new Encoder(drvMasterTSRX_R, drvMasterTPF_R);
    public static WPI_VictorSPX drvFollowerVSPX_L = new WPI_VictorSPX(2); // Resrvd 3 & 4 maybe
    public static WPI_VictorSPX drvFollowerVSPX_R = new WPI_VictorSPX(6); // Resrvd 7 & 8 maybe

    // Shooter tbd ports
    // public static TalonSRX shooterTSRX = new TalonSRX(9); //Shooter motor.
    // Includes encoder

    public static WPI_TalonSRX shooterTSRX = new WPI_TalonSRX(9);
    public static Encoder shooter_Encoder = new Encoder(shooterTSRX, 0);
    public static ISolenoid shooterHoodUp = new InvertibleSolenoid(22, 4);

    // Turret-- LL defines itself
    public static Victor turretRot = new Victor(4); // Turret rotation motor
    public static AnalogPotentiometer turretPosition = new AnalogPotentiometer(0, -370, 190);
    public static InvertibleDigitalInput turCCWLimitSw = new InvertibleDigitalInput(4, true); // Critical, DO NOT
    public static InvertibleDigitalInput turCWLimitSw = new InvertibleDigitalInput(5, true);// EXCEED these limit
                                                                                            // switches

    // Injector, injects balls in to the shooter (AKA, Columnator)
    public static VictorSPX injector4Whl = new VictorSPX(10);
    public static Victor injectorPickup = new Victor(8);
    public static InvertibleSolenoid injectorFlipper = new InvertibleSolenoid(22, 5, false);

    // Revolver, stores up to 5 balls
    public static Victor revolverRot = new Victor(5);
    public static InvertibleDigitalInput revolerIndexer = new InvertibleDigitalInput(0, true);
    public static InvertibleDigitalInput revolNextSpaceOpen = new InvertibleDigitalInput(1, false);
    public static Timer revTimer;

    // Snorfler
    public static Victor snorfFeedMain = new Victor(9);
    public static Victor snorfFeedScdy = new Victor(6);
    public static ISolenoid snorflerExt = new InvertibleSolenoid(22, 6); // Extends both feeders
    public static InvertibleDigitalInput snorfHasBall = new InvertibleDigitalInput(2, false);

    // Climb
    public static Victor climberHoist = new Victor(3); // Extends climber
    public static ISolenoid climberExt = new InvertibleSolenoid(22, 7);

    // ---------- WoF, Color Sensor -----------------
    /**
     * Change the I2C port below to match the connection of your color sensor
     */
    private static final I2C.Port i2cPort = I2C.Port.kOnboard;

    /**
     * A Rev Color Sensor V3 object is constructed with an I2C port as a parameter.
     * The device will be automatically initialized with default parameters.
     */
    // public static ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);

    // Initialize any hardware here
    public static void init() {
        revTimer = new Timer(0);
        drvsInit();
        motorsInit();
    }

    public static void drvsInit() {
        drvMasterTSRX_L.configFactoryDefault();
        drvMasterTSRX_R.configFactoryDefault();
        drvMasterTSRX_L.setInverted(true); // Inverts motor direction and encoder if attached
        drvMasterTSRX_R.setInverted(false); // Inverts motor direction and encoder if attached
        drvMasterTSRX_L.setSensorPhase(false); // Adjust this to correct phasing with motor
        drvMasterTSRX_R.setSensorPhase(false); // Adjust this to correct phasing with motor
        drvMasterTSRX_L.setNeutralMode(NeutralMode.Brake); // change it back
        drvMasterTSRX_R.setNeutralMode(NeutralMode.Brake); // change it back

        // Tells left and right victors to follow the master
        //TODO: change the brake stuff to coast

        drvFollowerVSPX_L.configFactoryDefault();
        drvFollowerVSPX_L.setInverted(false);
        drvFollowerVSPX_L.setNeutralMode(NeutralMode.Brake); // change it back
        // drvFollowerVSPX_L[i].set(ControlMode.Follower,
        // drvMasterTSRX_L.getDeviceID());
        drvFollowerVSPX_R.configFactoryDefault();
        drvFollowerVSPX_R.setInverted(true);
        drvFollowerVSPX_R.setNeutralMode(NeutralMode.Brake); // change it back
        // drvFollowerVSPX_R[i].set(ControlMode.Follower,
        // drvMasterTSRX_R.getDeviceID());

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
    }

    public static int revolverCntr = 0; // Count revolver rotations
    public static boolean prvRevIndex = true;

    public static void update() {
        alarmUpdate();
        // drvFollowerVSPX_L[0].set(ControlMode.Follower,
        // drvMasterTSRX_L.getDeviceID());
        // drvFollowerVSPX_R[0].set(ControlMode.Follower,
        // drvMasterTSRX_R.getDeviceID());

    }

    public static double revolver_HL = 15.0; // High Amp Alarm Limit
    public static boolean revolver_HAA = false; // High Amp Alarm

    private static void alarmUpdate() {

        SmartDashboard.putNumber("revolver HAA", pdp.getCurrent(4));
        if (Revolver.getState() != 0) {
            if (revTimer.hasExpired(4, Revolver.getState()))
                revolver_HAA = true;
        }
    }

    public static void follow() {
        drvFollowerVSPX_L.follow(drvMasterTSRX_L);
        drvFollowerVSPX_R.follow(drvMasterTSRX_R);
    }
}
