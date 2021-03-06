package eaglesfe.skystone.opmodes;

import com.eaglesfe.birdseye.skystone.SkystoneBirdseyeTracker;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Random;

import eaglesfe.common.MecanumDrive;
import eaglesfe.common.SleepStep;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

public class skystoneRobot {

    //hardware map
    public final                HardwareMap             hardwareMap;
    public                      MecanumDrive            drive;
    private                     SkystoneBirdseyeTracker tracker;
    private                     DcMotor                 arm;
    private                     DcMotor                 intakeLeft;
    private                     DcMotor                 intakeRight;
    private                     Servo                   wrist;
    private                     Servo                   claw;
    private                     Servo                   leftFoundation;
    private                     Servo                   rightFoundation;
    private                     Servo                   capstoneRelease;
    private                     BNO055IMU               imu;
    private                     ColorSensor             cLeft;
    private                     ColorSensor             cRight;

    Orientation             lastAngles = new Orientation();
    double                  globalAngle;

    private LinearOpMode autoOpMode;

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
        this.arm.setDirection(DcMotorSimple.Direction.FORWARD);
        this.arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.armMode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
        this.arm.setMode(armMode);
        this.arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        this.arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.arm.setTargetPosition(0);
        this.arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        //intake motor
        this.intakeLeft = this.hardwareMap.dcMotor.get(Constants.INTAKELEFT);
        this.intakeLeft.setDirection(FORWARD);

        this.intakeRight = this.hardwareMap.dcMotor.get(Constants.INTAKERIGHT);
        this.intakeRight.setDirection(REVERSE);

        //stacking servo
        this.wrist = this.hardwareMap.servo.get(Constants.WRIST);
        this.wrist.scaleRange(Constants.WRISTMINHB, Constants.WRISTMAXHB);
        this.wrist.setPosition(Constants.WRISTMIDDLE);

        this.claw = this.hardwareMap.servo.get(Constants.CLAW);
        this.claw.scaleRange(Constants.CLAWMIN, Constants.CLAWMAX);

        this.leftFoundation = this.hardwareMap.servo.get(Constants.LEFTFOUNDATION);
        this.leftFoundation.scaleRange(Constants.FOUNDATIONOPEN, Constants.FOUNDATIONCLOSED);
        this.leftFoundation.setPosition(1);

        this.rightFoundation = this.hardwareMap.servo.get(Constants.RIGHTFOUNDATION);
        this.rightFoundation.scaleRange(Constants.FOUNDATIONOPEN, Constants.FOUNDATIONCLOSED);
        this.rightFoundation.setDirection(Servo.Direction.REVERSE);
        this.rightFoundation.setPosition(1);

        this.capstoneRelease = this.hardwareMap.servo.get(Constants.CAPSTONE);
        this.capstoneRelease.scaleRange(Constants.CAPSTONEATTACHED,Constants.CAPSTONERELEASED);
        this.capstoneRelease.setDirection(Servo.Direction.FORWARD);
        this.capstoneRelease.setPosition(1);

        //color sensors
        this.cLeft = this.hardwareMap.colorSensor.get("CLEFT");
        this.cRight = this.hardwareMap.colorSensor.get("CRIGHT");

        this.imu = this.hardwareMap.get(BNO055IMU.class, Constants.GYRO);
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode                 = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit            = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit            = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled       = false;
        this.imu.initialize(parameters);

        //is initialized
        this.isInitialized = true;
    }

    public void setVisionEnabled(boolean enabled) {
        if (enabled) {
            this.tracker = new SkystoneBirdseyeTracker();
            this.tracker.setShowCameraPreview(true);
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

    public void useCameraTensor() {
        this.tracker.stop();
    }

    public void useCameraVuforia() {
        this.tracker.start();
    }

    public float getGyroHeading180() {
        return imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
    }

    public boolean isReady() {
        return this.isInitialized
                && this.imu.isSystemCalibrated();
    }

    //general motor power setter
    public void setMotorPower(DcMotor motor, double power) {
        motor.setPower(power);
    }

    //general motor to position
    private void setMotorPosition(DcMotor motor, int position, double speed) {
        motor.setTargetPosition(position);
        motor.setPower(speed);
    }

    // =============================================================================================
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

    private Orientation angles;
    public float curHeading;

    public void settleAngle() {
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        globalAngle = angles.firstAngle;
    }

    public double checkOrientation() {
//// read the orientation of the robot
//        angles = this.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
//        this.imu.getPosition();
//// and save the heading
//        curHeading = angles.firstAngle;

        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        globalAngle += deltaAngle;

        lastAngles = angles;

        return globalAngle;
    }

    public boolean corectingStrafe(long duration, double speed, LinearOpMode opmode) {
        long startingTime = System.currentTimeMillis();
        checkOrientation();
        double startingPosition = globalAngle;
        while (System.currentTimeMillis() - startingTime < duration && opmode.opModeIsActive()) {
            checkOrientation();
            drive.setInput(speed,0,
                    //Math.copySign(Math.abs(globalAngle - startingPosition), globalAngle < startingPosition ? -1 : 1)
                    ((globalAngle - startingPosition)/25)*.2);
        }
        return true;
    }

    public boolean angleTurnRelative(double degrees, double speed, LinearOpMode opmode) {
        checkOrientation();
        double startingPosition = globalAngle;
        double targetPosition = startingPosition + degrees;
        while((Math.abs(globalAngle - startingPosition) < degrees - 5 || (Math.abs(globalAngle - startingPosition) > degrees + 5)) && opmode.opModeIsActive()) {
            drive.setInput(0,0,Math.copySign(speed, globalAngle < targetPosition ? 1 : -1));
//            drive.setInput(0,0,Math.copySign(speed, globalAngle < startingPosition ? 1 : -1));
//            (Math.abs(targetPosition - globalAngle)/degrees)
            checkOrientation();
        }
        return true;
    }

//    public boolean angleTurnRelative(double degrees, double speed) {
//        double startingPosition = globalAngle;
//        while(Math.abs(globalAngle - startingPosition) <= degrees) {
//            drive.setInput(0,0,speed);
//            checkOrientation();
//        }
//        return true;
//    }

    public boolean isDriveBusy() {
        return drive.isBusy();
    }

    public void stopAllMotors() {
        drive.setInput(0,0,0);
        setIntakeSpeed(0);
    }

    // =============================================================================================
    //specific arm speed setter

    private DcMotor.RunMode armMode;

    public void setArmPower(double speed) {
        armMode = arm.getMode();
        if (armMode != DcMotor.RunMode.RUN_USING_ENCODER) {
            armMode = DcMotor.RunMode.RUN_USING_ENCODER;
            arm.setMode(armMode);
        }
        arm.setPower(speed);
    }

    public void setArmPosition(double position, double speed) {
        int ticks = (int)(position * Constants.MAX_ARM_TICKS);
        setMotorPosition(this.arm, -ticks, speed);
    }

    public int getArmPosition() {
        return arm.getCurrentPosition();
    }

    public boolean isArmBusy() {
        return arm.isBusy();
    }

    // =============================================================================================

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


    public void clawGrab(boolean smallGrab, boolean largeGrab){
        if (smallGrab && largeGrab) {
            return;
        }
        
        boolean isClawOpen = claw.getPosition() < 0.55;
        boolean isClawBigOpen = !(claw.getPosition() < .55);

        if (smallGrab) {
            this.claw.setPosition(isClawOpen ? Constants.CLAWCLOSED : Constants.CLAWMID);
        }
        else if (largeGrab) {
            this.claw.setPosition(isClawBigOpen ? Constants.CLAWCLOSED : Constants.CLAWOPEN);
        }
    }

    public void foundationGrab(boolean foundationGrab) {
        boolean isFoundationClamped = leftFoundation.getPosition() > 0.5;

        if(foundationGrab) {
            this.leftFoundation.setPosition(isFoundationClamped ? Constants.FOUNDATIONOPEN : Constants.FOUNDATIONCLOSED);
            this.rightFoundation.setPosition(isFoundationClamped ? Constants.FOUNDATIONOPEN : Constants.FOUNDATIONCLOSED);

        }
    }

    public void capstoneRelease(double capstoneRelease) {
        if(capstoneRelease > .5) {
            this.capstoneRelease.setPosition(0);
        }
    }


    public double wristPosition() {
        return wrist.getPosition();
    }

    public void wristTurn(boolean left, boolean right, boolean safe) {
        double newPosition = wristPosition();
        if (left) {
            newPosition -= Constants.WRISTRATE;
        } else if (right) {
            newPosition += Constants.WRISTRATE;
        } else if (safe) {
            newPosition = .44;
        }

        wrist.setPosition(newPosition);
    }

    public String skystoneTelemetry() {
        return "Left: " + cLeft.alpha() + " Right: " + cRight.alpha();
    }

    public float locateSkystone() {
        if (Math.abs(cLeft.alpha() - cRight.alpha()) < 8) {
          return 2.0f;
        } else {
            return cLeft.alpha() > cRight.alpha() ? 1.0f : 0.0f;
        }
    }

    /* ================== INTAKE =================== */

    //specific intake speed setter
    public void setIntakeSpeed(double input) {
        setMotorPower(intakeLeft, input);
        setMotorPower(intakeRight, input);
    }

    public class Constants {
        //hardware map things
        public static final String FRONT_LEFT      = "FrontLeft";
        public static final String FRONT_RIGHT     = "FrontRight";
        public static final String BACK_LEFT       = "BackLeft";
        public static final String BACK_RIGHT      = "BackRight";
        public static final String ARM             = "Arm";
        public static final String INTAKELEFT      = "IntakeLeft";
        public static final String INTAKERIGHT     = "IntakeRight";
        public static final String WRIST           = "Wrist";
        public static final String CLAW            = "Claw";
        public static final String LEFTFOUNDATION  = "LeftFoundation";
        public static final String RIGHTFOUNDATION = "RightFoundation";
        public static final String CAPSTONE        = "Capstone";
        public static final String GYRO            = "IMU";
        public static final String POS_CAM         = "PositionCamera";

        //number things
        public static final int    MAX_ARM_TICKS   = 1850;
        public static final double WRISTMAX        = 1.0;
        public static final double WRISTMIN        = 0.0;
        public static final double WRISTRATE       = 0.005;
        public static final double WRISTMAXHB      = 0.975;
        public static final double WRISTMIDDLE     = 0.44;
        public static final double WRISTMINHB      = 0.0;
        public static final double CLAWMAX         = 0.9;
        public static final double CLAWMIN         = 0.1;
        public static final double CLAWOPEN        = CLAWMIN;
        public static final double CLAWMID         = 0.5;
        public static final double CLAWCLOSED      = CLAWMAX;
        public static final double FOUNDATIONOPEN  = 0.0;
        public static final double FOUNDATIONCLOSED = .75;
        public static final double CAPSTONEATTACHED = .4;
        public static final double CAPSTONERELEASED = .8;

        //vuforia configuration
        public static final String VUFORIA_KEY     = "AUmjH6X/////AAABmeSd/rs+aU4giLmf5DG5vUaAfHFLv0/vAnAFxt5vM6cbn1/nI2sdkRSEf6HZLA/is/+VQY5/i6u5fbJ4TugEN8HOxRwvUvkrAeIpgnMYEe3jdD+dPxhE88dB58mlPfVwIPJc2KF4RE7weuRBoZ8KlrEKbNNu20ommdG7S/HXP9Kv/xocj82rgj+iPEaitftALZ6QaGBdfSl3nzVMK8/KgQJNlSbGic/Wf3VI8zcYmMyDslQPK45hZKlHW6ezxdGgJ7VJCax+Of8u/LEwfzqDqBsuS4/moNBJ1mF6reBKe1hIE2ffVTSvKa2t95g7ht3Z4M6yQdsI0ZaJ6AGnl1wTlm8Saoal4zTbm/VCsmZI081h";
        public static final float CAM_X_OFFSET     = -8.0f;
        public static final float CAM_Y_OFFSET     = 0.0f;
        public static final float CAM_Z_OFFSET     = 5.5f;
        public static final int CAM_R_OFFSET       = 180;
    }
}
