package eaglesfe.skystone;

import com.eaglesfe.birdseye.skystone.SkystoneBirdseyeTracker;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import eaglesfe.common.MecanumDrive;
import eaglesfe.skystone.subsystems.GlobalAngle;
import eaglesfe.skystone.subsystems.PIDController;
import eaglesfe.skystone.subsystems.SensorThread;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

public class skystoneRobot {

    /** ---------- hardware ---------- **/

    public final HardwareMap hardwareMap;
    public MecanumDrive drive;

    private DcMotor arm;
    private DcMotor intakeLeft;
    private DcMotor intakeRight;

    private Servo wrist;
    private Servo claw;
    private Servo leftIntakeServo;
    private Servo rightIntakeServo;
    private Servo leftFoundation;
    private Servo rightFoundation;
    private Servo capstoneRelease;

    private BNO055IMU imu;

    private ColorSensor cLeft;
    private ColorSensor cRight;
    private DistanceSensor blockDistance;

    private DigitalChannel armSwitch;

    /** ---------- services ---------- **/

    private SkystoneBirdseyeTracker tracker;

    private Thread sensors;
    private GlobalAngle worldwideAngle;
    private double origin;
    private PIDController pidRotate;

    private final double wheelDiameter = 4.0;
    private final double wheelCircumference = Math.PI * wheelDiameter;

    double globalAngle;

    public boolean isInitialized;

    /** ---------- initialize hardware map ---------- **/

    public skystoneRobot(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        initializeHardware();
    }

    /** ---------- initialize hardware ----------**/

    private void initializeHardware() {

        /** gyro hardware **/

        this.imu = this.hardwareMap.get(BNO055IMU.class, Constants.GYRO);
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled = false;
        this.imu.initialize(parameters);

        /** gyro thread **/

        this.worldwideAngle = new GlobalAngle();
        this.sensors = new Thread(new SensorThread(imu, this.worldwideAngle));
        sensors.start();
        this.globalAngle = this.worldwideAngle.globalAngle;

        /** drive **/

        DcMotor frontLeft = this.hardwareMap.dcMotor.get(Constants.FRONT_LEFT);
        frontLeft.setDirection(FORWARD);

        DcMotor frontRight = this.hardwareMap.dcMotor.get(Constants.FRONT_RIGHT);
        frontRight.setDirection(REVERSE);

        DcMotor backLeft = this.hardwareMap.dcMotor.get(Constants.BACK_LEFT);
        backLeft.setDirection(FORWARD);

        DcMotor backRight = this.hardwareMap.dcMotor.get(Constants.BACK_RIGHT);
        backRight.setDirection(REVERSE);

        this.drive = new MecanumDrive(frontLeft, frontRight, backLeft, backRight, worldwideAngle);

        /** stacking arm **/

        this.arm = this.hardwareMap.dcMotor.get(Constants.ARM);
        this.arm.setDirection(DcMotorSimple.Direction.FORWARD);
        this.arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.armMode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
        this.arm.setMode(armMode);
        this.arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.arm.setTargetPosition(0);
        this.arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        /** intake **/

        this.intakeLeft = this.hardwareMap.dcMotor.get(Constants.INTAKELEFT);
        this.intakeLeft.setDirection(REVERSE);

        this.intakeRight = this.hardwareMap.dcMotor.get(Constants.INTAKERIGHT);
        this.intakeRight.setDirection(FORWARD);

        /** claw and wrist **/

        this.wrist = this.hardwareMap.servo.get(Constants.WRIST);
        this.wrist.scaleRange(Constants.WRISTMINHB, Constants.WRISTMAXHB);
        this.wrist.setPosition(Constants.WRISTMIDDLE);

        this.claw = this.hardwareMap.servo.get(Constants.CLAW);
        this.claw.scaleRange(Constants.CLAWMIN, Constants.CLAWMAX);

        /** intake lift **/

        this.leftIntakeServo = this.hardwareMap.servo.get(Constants.LEFTINTAKESERVO);
        this.leftIntakeServo.setDirection(Servo.Direction.FORWARD);
        this.leftIntakeServo.scaleRange(Constants.INTAKEDOWN, Constants.INTAKEUP);
        this.leftIntakeServo.setPosition(1);

        this.rightIntakeServo = this.hardwareMap.servo.get(Constants.RIGHTINTAKESERVO);
        this.rightIntakeServo.setDirection(Servo.Direction.REVERSE);
        this.rightIntakeServo.scaleRange(Constants.INTAKEDOWN, Constants.INTAKEUP);
        this.rightIntakeServo.setPosition(1);

        this.leftFoundation = this.hardwareMap.servo.get(Constants.LEFTFOUNDATION);
        this.leftFoundation.scaleRange(Constants.FOUNDATIONOPEN, Constants.FOUNDATIONCLOSED);
        this.leftFoundation.setDirection(Servo.Direction.FORWARD);
        this.leftFoundation.setPosition(1);

        this.rightFoundation = this.hardwareMap.servo.get(Constants.RIGHTFOUNDATION);
        this.rightFoundation.scaleRange(Constants.FOUNDATIONOPEN, Constants.FOUNDATIONCLOSED + 0.1);
        this.rightFoundation.setDirection(Servo.Direction.REVERSE);
        this.rightFoundation.setPosition(1);

        this.capstoneRelease = this.hardwareMap.servo.get(Constants.CAPSTONE);
        this.capstoneRelease.scaleRange(Constants.CAPSTONEATTACHED, Constants.CAPSTONERELEASED);
        this.capstoneRelease.setDirection(Servo.Direction.FORWARD);
        this.capstoneRelease.setPosition(1);

        //color sensors
        this.cLeft = this.hardwareMap.colorSensor.get("CLEFT");
        this.cRight = this.hardwareMap.colorSensor.get("CRIGHT");

        this.armSwitch = this.hardwareMap.get(DigitalChannel.class, "ARMSWITCH");
        this.armSwitch.setMode(DigitalChannel.Mode.INPUT);

        this.blockDistance = this.hardwareMap.get(DistanceSensor.class, "BLOCKDISTANCE");

        this.origin = worldwideAngle.globalAngle;

        //is initialized
        this.isInitialized = true;


    }

    public boolean haveBlock() {
        return getBlockDistanceInch() < 2.50;
    }

    public double getBlockDistanceInch() {
        return blockDistance.getDistance(DistanceUnit.INCH);
    }

    public void setVisionEnabled(boolean enabled) {
        if (enabled) {
            this.tracker = new SkystoneBirdseyeTracker();
            this.tracker.setShowCameraPreview(true);
            this.tracker.setVuforiaKey(Constants.VUFORIA_KEY);
//            this.tracker.setWebcamNames(Constants.POS_CAM);
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

    //general motor power setter
    public void setMotorPower(DcMotor motor, double power) {
        motor.setPower(power);
    }

    //general motor to position
    private void setMotorPosition(DcMotor motor, int position, double speed) {
        motor.setTargetPosition(position);
        motor.setPower(speed);
    }

    private double setX, setY, setZ = 0;

    public void setDriveInput(double x, double y, double z) {
        setX = x;
        setY = y;
        setZ = z;

        this.drive.setInput(setX, setY, setZ);
    }

    public double getOrientation() {
        return worldwideAngle.globalAngle;
    }

    public boolean corectingStrafe(long duration, double speed, LinearOpMode opmode) {
        long startingTime = System.currentTimeMillis();
        double startingPosition = worldwideAngle.globalAngle;
        while (System.currentTimeMillis() - startingTime < duration && opmode.opModeIsActive()) {
            drive.setInput(speed, 0,
                    ((worldwideAngle.globalAngle - startingPosition) / 25) * .2);
        }

        drive.setInput(0,0,0);

        return true;
    }


    public boolean suckStraight(double speed, LinearOpMode opMode) {

        double startingPosition = worldwideAngle.globalAngle;

        while (!haveBlock() && opMode.opModeIsActive()) {
            drive.setInput(0, speed,((worldwideAngle.globalAngle - startingPosition) / 25) * .2);
        }

        drive.setInput(0,0,0);

        return true;
    }

    public boolean angleTurnRelative(double degrees, double speed, LinearOpMode opmode) {
        double startingPosition = worldwideAngle.globalAngle;
        double targetPosition = startingPosition + degrees;
        double boops = opmode.getRuntime();
        while (opmode.getRuntime() < boops + 0.5 && opmode.opModeIsActive()) {
            if (Math.abs(targetPosition - worldwideAngle.globalAngle) > 1) {
                double angleDifference = targetPosition - worldwideAngle.globalAngle;
                double absoluteAngleDifference = Math.abs(angleDifference);

                drive.setInput(0, 0, Math.copySign((absoluteAngleDifference > 30) ? .7 : .1, -angleDifference));
                boops = opmode.getRuntime();
            } else {
                drive.setInput(0, 0, 0);
                opmode.sleep(1);
            }
        }
        drive.setInput(0,0,0);
        return true;
    }

    public boolean angleTurnAbsolute(double degrees, LinearOpMode opmode) {
        double targetPosition = origin + degrees;
        double boops = opmode.getRuntime();
        while (opmode.getRuntime() < boops + 0.5 && opmode.opModeIsActive()) {
            if (Math.abs(targetPosition - worldwideAngle.globalAngle) > 1) {
                double angleDifference = targetPosition - worldwideAngle.globalAngle;
                double absoluteAngleDifference = Math.abs(angleDifference);
                drive.setInput(0, 0, Math.copySign((absoluteAngleDifference > 30) ? .7 : .1, -angleDifference));
                boops = opmode.getRuntime();
            } else {
                drive.setInput(0, 0, 0);
                opmode.sleep(1);
            }
        }
        drive.setInput(0,0,0);
        return true;
    }

    public boolean roughTurnRelative(double degrees, double speed, LinearOpMode opmode) {
        double startingPosition = worldwideAngle.globalAngle;
        double targetPosition = startingPosition + degrees;
        double difference = 0;
        while (Math.abs(difference) > Math.abs(degrees) && opmode.opModeIsActive()) {
            double angleDifference = targetPosition - worldwideAngle.globalAngle;
            double absoluteAngleDifference = Math.abs(angleDifference);
            opmode.telemetry.addData("absolute angle difference", absoluteAngleDifference);
            opmode.telemetry.update();

            drive.setInput(0, 0, Math.copySign((absoluteAngleDifference > 30) ? .7 : .1, -angleDifference));

            difference = worldwideAngle.globalAngle - startingPosition;
        }
        drive.setInput(0,0,0);
        return true;
    }

    public void stopAllMotors() {
        drive.setInput(0, 0, 0);
        setIntakeSpeed(0);
    }

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
        int ticks = (int) (position * Constants.MAX_ARM_TICKS);
        setMotorPosition(this.arm, -ticks, speed);
    }

    public boolean armHomed(LinearOpMode opmode) {
        boolean armState = armSwitch.getState();
        while (armState == armSwitch.getState() && opmode.opModeIsActive()) { }
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        return true;
    }

    public boolean getArmOpen() {
        return armSwitch.getState();
    }

    public int getArmPosition() {
        return arm.getCurrentPosition();
    }

    public boolean isArmBusy() {
        return arm.isBusy();
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
    public void setClawPosition(double position) {
        setServoPostition(claw, position);
    }


    public void clawGrab(boolean smallGrab, boolean largeGrab) {
        if (smallGrab && largeGrab) {
            return;
        }

        boolean isClawOpen = claw.getPosition() < 0.55;
        boolean isClawBigOpen = !(claw.getPosition() < .55);

        if (smallGrab) {
            this.claw.setPosition(isClawOpen ? Constants.CLAWCLOSED : Constants.CLAWMID);
        } else if (largeGrab) {
            this.claw.setPosition(isClawBigOpen ? Constants.CLAWCLOSED : Constants.CLAWOPEN);
        }
    }

    public void setIntakeServos(boolean moveIntake, boolean half) {
        boolean isFoundationClamped = rightIntakeServo.getPosition() > 0.5;
        boolean isAboveHalf = rightIntakeServo.getPosition() > .15;

        if (moveIntake) {
            this.leftIntakeServo.setPosition(isFoundationClamped ? 0 : 1);
            this.rightIntakeServo.setPosition(isFoundationClamped? 0 : 1);
        }
        if (half) {
            this.leftIntakeServo.setPosition(isAboveHalf ? 0 : .35);
            this.rightIntakeServo.setPosition(isAboveHalf ? 0 : .35);
        }
    }

    public void foundationGrab(boolean foundationGrab) {
        boolean isFoundationClamped = leftFoundation.getPosition() > 0.5;

        if (foundationGrab) {
            this.leftFoundation.setPosition(isFoundationClamped ? 0 : 1);
            this.rightFoundation.setPosition(isFoundationClamped ? 0 : 1);

        }
    }

    public void capstoneRelease(double capstoneRelease) {
        if (capstoneRelease > .5) {
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

    public void displayTelemetry(LinearOpMode opMode) {
        opMode.telemetry.addData("angle", worldwideAngle.globalAngle);
        opMode.telemetry.update();
    }

    /* ================== INTAKE =================== */

    //specific intake speed setter
    public void setIntakeSpeed(double input) {
        setMotorPower(intakeLeft, input);
        setMotorPower(intakeRight, input);
    }

    public class Constants {

        /** ---------- hardware map strings ---------- **/
        public static final String FRONT_LEFT = "FrontLeft";
        public static final String FRONT_RIGHT = "FrontRight";
        public static final String BACK_LEFT = "BackLeft";
        public static final String BACK_RIGHT = "BackRight";
        public static final String ARM = "Arm";
        public static final String INTAKELEFT = "IntakeLeft";
        public static final String INTAKERIGHT = "IntakeRight";
        public static final String WRIST = "Wrist";
        public static final String CLAW = "Claw";
        public static final String LEFTINTAKESERVO = "LeftIntakeServo";
        public static final String RIGHTINTAKESERVO = "RightIntakeServo";
        public static final String LEFTFOUNDATION = "LeftFoundation";
        public static final String RIGHTFOUNDATION = "RightFoundation";
        public static final String CAPSTONE = "Capstone";
        public static final String GYRO = "IMU";
        public static final String POS_CAM = "PositionCamera";

        /** ---------- number constants ---------- **/
        public static final int MAX_ARM_TICKS = 1850;
        public static final double WRISTRATE = 0.005;
        public static final double WRISTMAXHB = 0.975;
        public static final double WRISTMIDDLE = 0.44;
        public static final double WRISTMINHB = 0.0;
        public static final double CLAWMAX = 0.9;
        public static final double CLAWMIN = 0.1;
        public static final double CLAWOPEN = CLAWMIN;
        public static final double CLAWMID = 0.5;
        public static final double CLAWCLOSED = CLAWMAX;
        public static final double INTAKEUP = 0.9;
        public static final double INTAKEDOWN = 0.1;
        public static final double FOUNDATIONOPEN = 0.02;
        public static final double FOUNDATIONCLOSED = 0.8;
        public static final double CAPSTONEATTACHED = .4;
        public static final double CAPSTONERELEASED = .8;

        /** ---------- Vuforia constants ---------- **/
        public static final String VUFORIA_KEY = "AUmjH6X/////AAABmeSd/rs+aU4giLmf5DG5vUaAfHFLv0/vAnAFxt5vM6cbn1/nI2sdkRSEf6HZLA/is/+VQY5/i6u5fbJ4TugEN8HOxRwvUvkrAeIpgnMYEe3jdD+dPxhE88dB58mlPfVwIPJc2KF4RE7weuRBoZ8KlrEKbNNu20ommdG7S/HXP9Kv/xocj82rgj+iPEaitftALZ6QaGBdfSl3nzVMK8/KgQJNlSbGic/Wf3VI8zcYmMyDslQPK45hZKlHW6ezxdGgJ7VJCax+Of8u/LEwfzqDqBsuS4/moNBJ1mF6reBKe1hIE2ffVTSvKa2t95g7ht3Z4M6yQdsI0ZaJ6AGnl1wTlm8Saoal4zTbm/VCsmZI081h";
        public static final float CAM_X_OFFSET = -8.0f;
        public static final float CAM_Y_OFFSET = 0.0f;
        public static final float CAM_Z_OFFSET = 5.5f;
        public static final int CAM_R_OFFSET = 180;
    }
}
