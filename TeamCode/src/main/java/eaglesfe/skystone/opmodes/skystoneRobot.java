package eaglesfe.skystone.opmodes;

import com.eaglesfe.birdseye.roverruckus.RoverRuckusBirdseyeTracker;
import com.eaglesfe.birdseye.skystone.SkystoneBirdseyeTracker;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Const;

import eaglesfe.common.MecanumDrive;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

public class skystoneRobot {

    //hardware map
    public final HardwareMap hardwareMap;
    public MecanumDrive drive;
    private SkystoneBirdseyeTracker tracker;
    private DcMotor arm;
    private DcMotor intakeLeft;
    private Servo wrist;
    private Servo claw;
    private boolean smallLast;
    private boolean largeLast;
    public boolean isInitialized;

    //initialize hardware map
    public skystoneRobot(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        initializeHardware();
    }

    //initialize hardware
    private void initializeHardware() {
        //drive
        DcMotor frontLeft = this.hardwareMap.dcMotor.get(Constants.FRONT_LEFT);
        frontLeft.setDirection(FORWARD);
        DcMotor frontRight = this.hardwareMap.dcMotor.get(Constants.FRONT_RIGHT);
        frontRight.setDirection(REVERSE);
        DcMotor backLeft = this.hardwareMap.dcMotor.get(Constants.BACK_LEFT);
        backLeft.setDirection(FORWARD);
        DcMotor backRight = this.hardwareMap.dcMotor.get(Constants.BACK_RIGHT);
        backRight.setDirection(REVERSE);
        this.drive = new MecanumDrive(frontLeft,frontRight,backLeft,backRight);

        //stacking arm
        this.arm = this.hardwareMap.dcMotor.get(Constants.ARM);
        this.arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.arm.setDirection(DcMotorSimple.Direction.FORWARD);

        //intake motor
        this.intakeLeft = this.hardwareMap.dcMotor.get(Constants.INTAKELEFT);
        this.intakeLeft.setDirection(FORWARD);

        //stacking servo
        this.wrist = this.hardwareMap.servo.get(Constants.WRIST);
        this.wrist.scaleRange(Constants.WRISTMIN, Constants.WRISTMAX);

        this.claw = this.hardwareMap.servo.get(Constants.CLAW);
        this.claw.scaleRange(Constants.CLAWMIN, Constants.CLAWMAX);

        //is initialized
        this.isInitialized = true;
    }

    public void setVisionEnabled(boolean enabled) {
        if (enabled) {
            this.tracker = new SkystoneBirdseyeTracker();
            this.tracker.setShowCameraPreview(false);
            this.tracker.setVuforiaKey(Constants.VUFORIA_KEY);
            this.tracker.setWebcamNames(Constants.POS_CAM);
            this.tracker.setCameraForwardOffset(Constants.CAM_X_OFFSET);
            this.tracker.setCameraVerticalOffset(Constants.CAM_Z_OFFSET);
            this.tracker.cameraLeftOffsetMm(Constants.CAM_Y_OFFSET);
            this.tracker.setCameraRotationalOffset(Constants.CAM_R_OFFSET);
            this.tracker.initialize(this.hardwareMap);
        } else {
            if (this.tracker != null) {
                this.tracker.stop();
                this.tracker = null;
            }
        }
    }




    //general motor speed setter
    public void setMotorPower(DcMotor motor, double power) {
        motor.setPower(power);
    }

    //general motor to position
    private void setMotorPosition(DcMotor motor, int position, double speed) {
        motor.setPower(speed);
    }

    /* ================== DRIVE =================== */

    private double setX, setY, setZ = 0;
    public void setDriveInput(double x, double y, double z) {
        setX = x;
        setY = y;
        setZ = z;

        this.drive.setInput(setX, setY, setZ);
    }

    public void setDriveInputX(double x) {
        this.setDriveInput(x, setY, setZ);
    }

    public void setDriveInputY(double y) {
        this.setDriveInput(setX, y, setZ);
    }

    public void setDriveInputZ(double z) {
        this.setDriveInput(setX, setY, z);
    }

    public void moveForward(double inches, double speed) {
        drive.setForwardTargetPositionRelative(Math.abs(inches), speed);
    }

    public void moveBackward(double inches, double speed) {
        drive.setForwardTargetPositionRelative(Math.abs(inches) * -1, speed);
    }

    public boolean isDriveBusy() {
        return drive.isBusy();
    }

    public void stopAllMotors() {
        drive.setInput(0,0,0);
    }

    /* ================== ARM =================== */

    //input scaling
    public static double armScale(double input) {
        double armMin = 0.0;
        double armMax = 0.2;
        return ((input - armMin)/(armMax - armMin))*(armMax - armMin)+(armMin);
    }

    //specific arm speed setter
    public void setArmPower(double input) {
        setMotorPower(arm, armScale(input));
    }

    //general servo position setter
    public void setServoPostition(Servo servo, double position) {
        servo.setPosition(position);
    }

    //set wrist position
    public void setWristPosition(double position) {
        setServoPostition(wrist, position);
    }

    //set claw position
    public void setClawPosition (double position) {
        setServoPostition(claw, position);
    }

    public void armProtector () {
        double armPosition = arm.getCurrentPosition();
        if (armPosition >= 0 && armPosition <= 20) {
            setWristPosition(0);
        } else if (armPosition > 20 && armPosition < 90) {
            setWristPosition(0);
            setClawPosition(0);
        } else if (armPosition >= 90 && armPosition <= 180) {

        } else {

        }
    }

    public void clawGrab(boolean smallGrab, boolean largeGrab){
        if (smallGrab && largeGrab) {
            return;
        }
        
        boolean isClawOpen = claw.getPosition() < 0.55;

        if (smallGrab) {
            this.claw.setPosition(isClawOpen ? Constants.CLAWCLOSED : Constants.CLAWMID);
        }
        else if (largeGrab) {
            this.claw.setPosition(isClawOpen ? Constants.CLAWCLOSED : Constants.CLAWOPEN);
        }
    }

    public double wristPosition() {
        return wrist.getPosition();
    }

    public void wristTurn(boolean left, boolean right) {
        double newPosition = wristPosition();
        if (left) {
            newPosition -= Constants.WRISTRATE;
        }
        if (right) {
            newPosition += Constants.WRISTRATE;
        }

        wrist.setPosition(newPosition);
    }

    /* ================== INTAKE =================== */

    //specific intake speed setter
    public void setIntakeSpeed(double input) {
        setMotorPower(intakeLeft, input);
    }

    public class Constants {
        //hardware map things
        public static final String FRONT_LEFT      = "FrontLeft";
        public static final String FRONT_RIGHT     = "FrontRight";
        public static final String BACK_LEFT       = "BackLeft";
        public static final String BACK_RIGHT      = "BackRight";
        public static final String ARM             = "Arm";
        public static final String INTAKELEFT      = "IntakeLeft";
        public static final String WRIST           = "Wrist";
        public static final String CLAW            = "Claw";
        public static final String POS_CAM         = "PositionCamera";

        //number things
        public static final double WRISTMAX        = 1.0;
        public static final double WRISTMIN        = 0.0;
        public static final double WRISTRATE       = 0.005;
        public static final double CLAWMAX         = 1.0;
        public static final double CLAWMIN         = 0.1;
        public static final double CLAWOPEN        = CLAWMIN;
        public static final double CLAWMID         = 0.5;
        public static final double CLAWCLOSED      = CLAWMAX;

        //vuforia configuration
        public static final String VUFORIA_KEY     = "AUmjH6X/////AAABmeSd/rs+aU4giLmf5DG5vUaAfHFLv0/vAnAFxt5vM6cbn1/nI2sdkRSEf6HZLA/is/+VQY5/i6u5fbJ4TugEN8HOxRwvUvkrAeIpgnMYEe3jdD+dPxhE88dB58mlPfVwIPJc2KF4RE7weuRBoZ8KlrEKbNNu20ommdG7S/HXP9Kv/xocj82rgj+iPEaitftALZ6QaGBdfSl3nzVMK8/KgQJNlSbGic/Wf3VI8zcYmMyDslQPK45hZKlHW6ezxdGgJ7VJCax+Of8u/LEwfzqDqBsuS4/moNBJ1mF6reBKe1hIE2ffVTSvKa2t95g7ht3Z4M6yQdsI0ZaJ6AGnl1wTlm8Saoal4zTbm/VCsmZI081h";
        public static final float CAM_X_OFFSET     = -8.0f;
        public static final float CAM_Y_OFFSET     = 0.0f;
        public static final float CAM_Z_OFFSET     = 5.5f;
        public static final int CAM_R_OFFSET       = 180;
    }
}
