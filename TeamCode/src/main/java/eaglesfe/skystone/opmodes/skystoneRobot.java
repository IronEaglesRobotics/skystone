package eaglesfe.skystone.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import eaglesfe.common.MecanumDrive;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

public class skystoneRobot {

    //hardware map
    public final HardwareMap hardwareMap;
    public boolean isInitialized;
    public MecanumDrive drive;
    private DcMotor arm;
    private DcMotor intakeLeft;
    private DcMotor intakeRight;

    //initialize hardware map
    public skystoneRobot(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        initializeHardware();
    }

    //initialize hardware
    private void initializeHardware() {
        //stacking arm
        DcMotor arm = this.hardwareMap.dcMotor.get(Constants.ARM);
        arm.setDirection(DcMotorSimple.Direction.FORWARD);

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

        //intake motors
        DcMotor intakeLeft = this.hardwareMap.dcMotor.get(Constants.INTAKELEFT);
        intakeLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        DcMotor intakeRight = this.hardwareMap.dcMotor.get(Constants.INTAKERIGHT);
        intakeRight.setDirection(DcMotorSimple.Direction.REVERSE);

        //initialize
        this.arm = arm;
        this.intakeLeft = intakeLeft;
        this.intakeRight = intakeRight;

        this.isInitialized = true;
    }

    //general motor speed setter
    public void setMotorSpeed(DcMotor motor, double speed) {
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setPower(speed);
    }

    //general motor to position
    private void setMotorPosition(DcMotor motor, int position, double speed) {
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setTargetPosition(position);
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

    /* ================== ACTUATORS =================== */

    //specific arm speed setter
    public void setArmSpeed(double input) {
        setMotorSpeed(arm, input);
    }

    //specific intake speed setter
    public void setIntakeSpeed(double input) {
        setMotorSpeed(intakeLeft, input);
        setMotorSpeed(intakeRight, input);
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
        public static final String POS_CAM         = "PositionCamera";

        //vuforia configuration
        public static final String VUFORIA_KEY     = "AUmjH6X/////AAABmeSd/rs+aU4giLmf5DG5vUaAfHFLv0/vAnAFxt5vM6cbn1/nI2sdkRSEf6HZLA/is/+VQY5/i6u5fbJ4TugEN8HOxRwvUvkrAeIpgnMYEe3jdD+dPxhE88dB58mlPfVwIPJc2KF4RE7weuRBoZ8KlrEKbNNu20ommdG7S/HXP9Kv/xocj82rgj+iPEaitftALZ6QaGBdfSl3nzVMK8/KgQJNlSbGic/Wf3VI8zcYmMyDslQPK45hZKlHW6ezxdGgJ7VJCax+Of8u/LEwfzqDqBsuS4/moNBJ1mF6reBKe1hIE2ffVTSvKa2t95g7ht3Z4M6yQdsI0ZaJ6AGnl1wTlm8Saoal4zTbm/VCsmZI081h";
        public static final float CAM_X_OFFSET     = -8.0f;
        public static final float CAM_Y_OFFSET     = 0.0f;
        public static final float CAM_Z_OFFSET     = 5.5f;
        public static final int CAM_R_OFFSET       = 180;
    }
}
