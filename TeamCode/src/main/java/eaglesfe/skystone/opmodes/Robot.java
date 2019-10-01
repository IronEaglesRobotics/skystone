package eaglesfe.skystone.opmodes;

import com.eaglesfe.birdseye.FieldPosition;
import com.eaglesfe.birdseye.roverruckus.MineralSample;
import com.eaglesfe.birdseye.roverruckus.RoverRuckusBirdseyeTracker;
import com.eaglesfe.birdseye.skystone.SkystoneBirdseyeTracker;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import eaglesfe.common.MecanumDrive;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

public class Robot {

    private final   HardwareMap                     hardwareMap;
    private         MecanumDrive                    drive;
    private         SkystoneBirdseyeTracker      tracker;
    private         boolean                         isInitialized;

    public Robot(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        initializeHardware();
    }

    /**
     * Initialize all the robot hardware.
     * All devices should be properly confiugred within this method.
     */
    private void initializeHardware() {
        DcMotor frontLeft = this.hardwareMap.dcMotor.get(Constants.FRONT_LEFT);
        frontLeft.setDirection(FORWARD);
        DcMotor frontRight = this.hardwareMap.dcMotor.get(Constants.FRONT_RIGHT);
        frontRight.setDirection(REVERSE);
        DcMotor backLeft = this.hardwareMap.dcMotor.get(Constants.BACK_LEFT);
        backLeft.setDirection(FORWARD);
        DcMotor backRight = this.hardwareMap.dcMotor.get(Constants.BACK_RIGHT);
        backRight.setDirection(REVERSE);
        this.drive = new MecanumDrive(frontLeft, frontRight, backLeft, backRight);

        this.isInitialized = true;
    }

    /** Sets the given motor to isFinished using encoders and applies to it the given speed.
     * @param motor The motor on which the speed will be set.
     * @param speed The speed to which the motor will be set.
     */
    private void setMotorSpeed(DcMotor motor, double speed) {
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setPower(speed);
    }

    /**
     * Sets the given motor to isFinished to encoder position and
     * applies the given target position and speed.
     * @param motor The motor on which the speed and position will be set.
     * @param position The target position to which the motor will be set.
     * @param speed The speed to which the motor will be set.
     */
    private void setMotorPosition(DcMotor motor, int position, double speed) {
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setTargetPosition(position);
        motor.setPower(speed);
    }

    // =============================================================================================

    public void setVisionEnabled(boolean enabled) {
        if (enabled) {
            this.tracker = new SkystoneBirdseyeTracker();
            this.tracker.setShowCameraPreview(false);
            this.tracker.setVuforiaKey(Constants.VUFORIA_KEY);
            //this.tracker.setWebcamNames(Constants.MINERAL_CAM, Constants.POS_CAM); // Default to mineral cam.
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

    public FieldPosition getPosition() {
        if (this.tracker != null) {
            return this.tracker.getCurrentPosition();
        }
        return null;
    }

    private float baseGyroHeading;

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
        this.drive.setForwardTargetPositionRelative(Math.abs(inches), speed);
    }

    public void moveBackward(double inches, double speed) {
        this.drive.setForwardTargetPositionRelative(Math.abs(inches) * -1, speed);
    }

    public boolean isDriveBusy() {
        return this.drive.isBusy();
    }

    public class Constants {
        public static final String FRONT_LEFT      = "FrontLeft";
        public static final String FRONT_RIGHT     = "FrontRight";
        public static final String BACK_LEFT       = "BackLeft";
        public static final String BACK_RIGHT      = "BackRight";
        public static final String POS_CAM         = "PositionCamera";
        public static final String VUFORIA_KEY     = "AUmjH6X/////AAABmeSd/rs+aU4giLmf5DG5vUaAfHFLv0/vAnAFxt5vM6cbn1/nI2sdkRSEf6HZLA/is/+VQY5/i6u5fbJ4TugEN8HOxRwvUvkrAeIpgnMYEe3jdD+dPxhE88dB58mlPfVwIPJc2KF4RE7weuRBoZ8KlrEKbNNu20ommdG7S/HXP9Kv/xocj82rgj+iPEaitftALZ6QaGBdfSl3nzVMK8/KgQJNlSbGic/Wf3VI8zcYmMyDslQPK45hZKlHW6ezxdGgJ7VJCax+Of8u/LEwfzqDqBsuS4/moNBJ1mF6reBKe1hIE2ffVTSvKa2t95g7ht3Z4M6yQdsI0ZaJ6AGnl1wTlm8Saoal4zTbm/VCsmZI081h";
        public static final float CAM_X_OFFSET     = -8.0f;
        public static final float CAM_Y_OFFSET     = 0.0f;
        public static final float CAM_Z_OFFSET     = 5.5f;
        public static final int CAM_R_OFFSET       = 180;
    }
}