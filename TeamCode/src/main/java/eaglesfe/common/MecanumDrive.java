package eaglesfe.common;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

import java.util.Arrays;

import eaglesfe.skystone.opmodes.GlobalAngle;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior;

public class MecanumDrive implements DriveBase {

    private final double wheelDiameter = 4.0;
    private final double wheelCircumference = Math.PI * wheelDiameter;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private GlobalAngle worldwideAngle;

    public int[] tickSnapshot = new int[4];
    public int[] targetPoint = new int[4];

    private int ticks = 0;

    private int flTicks;
    private int frTicks;
    private int blTicks;
    private int brTicks;

    public MecanumDrive(DcMotor frontLeft, DcMotor frontRight, DcMotor backLeft, DcMotor backRight, GlobalAngle worldwideAngle){

        this.worldwideAngle = worldwideAngle;

        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;

        MecanumDrive drive = this;

        this.setRunMode(RunMode.RUN_USING_ENCODER);
        this.setBrakeMode(ZeroPowerBehavior.BRAKE);

    }

    public boolean straightMove(double inches, LinearOpMode opMode) {
        int ticktolerance = 200;

        ticks = (int)((inches / wheelCircumference) * 560);

        double startingPosition = worldwideAngle.globalAngle;

        flTicks = frontLeft.getCurrentPosition() + ticks;
        frTicks = frontRight.getCurrentPosition() + ticks;
        blTicks = backLeft.getCurrentPosition() + ticks;
        brTicks = backRight.getCurrentPosition() + ticks;

        setTargetPoint(flTicks, frTicks, blTicks, brTicks);

        while (
                //ends if all motors are in range or opmode is no longer active
                (Math.abs(targetInchDifference()) > 2) && opMode.opModeIsActive())
        {

//            flPower = z + x + y;    frPower = -z - x + y;
//            blPower = z - x + y;    brPower = -z + x + y;

//            frontLeft.setPower(- ((worldwideAngle.globalAngle - startingPosition) / 25) * .2 + getPower(flTicks, frontLeft));
//            frontRight.setPower( ((worldwideAngle.globalAngle - startingPosition) / 25) * .2 + getPower(frTicks,frontRight));
//            backLeft.setPower(- ((worldwideAngle.globalAngle - startingPosition) / 25) * .2 + getPower(blTicks, backLeft));
//            backRight.setPower(((worldwideAngle.globalAngle - startingPosition) / 25) * .2 + getPower(brTicks, backRight));

            double absoluteDistance = Math.abs(targetInchDifference());
//            opMode.telemetry.addData("distance", targetInchDifference());
//            opMode.telemetry.addData("absolute distance", absoluteDistance);
//            opMode.telemetry.update();
            setInput(0, Math.copySign((absoluteDistance > 16) ? .70 : .2 , -targetInchDifference()) ,((worldwideAngle.globalAngle - startingPosition) / 25) * .2);

        }

        setInput(0,0,0);

        return true;
    }

    public static double findMedian(int a[], int n)
    {
        // First we sort the array
        Arrays.sort(a);

        // check for even case
        if (n % 2 != 0)
            return (double)a[n / 2];

        return (double)(a[(n - 1) / 2] + a[n / 2]) / 2.0;
    }

    private double getPower(int target, DcMotor motor) {
        return Math.copySign(
                //Math.max(Math.abs((motor.getCurrentPosition() - target)/ ticks), minPower)
                .2,
                (motor.getCurrentPosition() < target) ? 1 : -1);
    }

    public boolean isBusy() {
        return this.getRunMode() != RunMode.RUN_TO_POSITION
                || this.frontLeft.isBusy()
                || this.frontRight.isBusy()
                || this.backLeft.isBusy()
                || this.backRight.isBusy();
    }

    public double inchDifference() {
        tickSnapshot[0] = Math.abs(frontLeft.getCurrentPosition() - tickSnapshot[0]);
        tickSnapshot[1] = Math.abs(frontRight.getCurrentPosition() - tickSnapshot[1]);
        tickSnapshot[2] = Math.abs(backLeft.getCurrentPosition() - tickSnapshot[2]);
        tickSnapshot[3] = Math.abs(backRight.getCurrentPosition() - tickSnapshot[3]);
        return (findMedian(tickSnapshot,4)/560)*wheelCircumference;
    }

    public double targetInchDifference() {
        targetPoint[0] = frontLeft.getCurrentPosition() - flTicks;
        targetPoint[1] = frontRight.getCurrentPosition() - frTicks;
        targetPoint[2] = backLeft.getCurrentPosition() - blTicks;
        targetPoint[3] = backRight.getCurrentPosition() - brTicks;
        return (findMedian(targetPoint,4)/560)*wheelCircumference;
    }

    public void recordPoint() {
        tickSnapshot[0] = frontLeft.getCurrentPosition();
        tickSnapshot[1] = frontRight.getCurrentPosition();
        tickSnapshot[2] = backLeft.getCurrentPosition();
        tickSnapshot[3] = backRight.getCurrentPosition();
    }

    public void setTargetPoint(int fl, int fr, int bl, int br) {
        this.targetPoint[0] = fl;
        this.targetPoint[1] = fr;
        this.targetPoint[2] = bl;
        this.targetPoint[3] = br;
    }

    public void setTargetPositionRelative(double inches, double speed) {
        int ticks = (int)((inches / wheelCircumference) * 560);
        this.setRunMode(RunMode.STOP_AND_RESET_ENCODER);
        this.setRunMode(RunMode.RUN_TO_POSITION);

        this.frontLeft.setTargetPosition(ticks);
        this.frontRight.setTargetPosition(ticks);
        this.backLeft.setTargetPosition(ticks);
        this.backRight.setTargetPosition(ticks);

        this.setPower(speed);
    }

    public void setTargetStrafePositionRelative(double inches, double power) {
        int ticks = (int)((inches / wheelCircumference) * 560);
        this.setRunMode(RunMode.STOP_AND_RESET_ENCODER);
        this.setRunMode(RunMode.RUN_TO_POSITION);

        this.frontLeft.setTargetPosition(ticks);
        this.frontRight.setTargetPosition(-ticks);
        this.backLeft.setTargetPosition(-ticks);
        this.backRight.setTargetPosition(ticks);

        this.setStrafePower(power);
    }

    public void setForwardTargetPositionRelative(double inches, double power) {
        int ticks = (int)((inches / wheelCircumference) * 560);
        this.setRunMode(RunMode.STOP_AND_RESET_ENCODER);
        this.setRunMode(RunMode.RUN_TO_POSITION);

        this.frontLeft.setTargetPosition(ticks);
        this.frontRight.setTargetPosition(ticks);
        this.backLeft.setTargetPosition(ticks);
        this.backRight.setTargetPosition(ticks);

        this.setPower(power);
    }

    public void setPower(double power) {
        this.frontLeft.setPower(power);
        this.frontRight.setPower(power);
        this.backLeft.setPower(power);
        this.backRight.setPower(power);
    }

    public void setStrafePower(double power) {
        this.frontLeft.setPower(power);
        this.frontRight.setPower(-power);
        this.backLeft.setPower(-power);
        this.backRight.setPower(power);
    }

    public int getFontLeftTicks() {
        return this.frontLeft.getCurrentPosition();
    }

    public String motorTelemetry() {
        return ("fl:" + frontLeft.getPower() + "fr:" + frontRight.getPower() + "bl:" + backLeft.getPower() + "br:" + backRight.getPower());
    }

    @Override
    public void setRunMode(RunMode runMode) {
        this.frontLeft.setMode(runMode);
        this.frontRight.setMode(runMode);
        this.backLeft.setMode(runMode);
        this.backRight.setMode(runMode);
    }

    public RunMode getRunMode() {
        return this.frontLeft.getMode();
    }

    @Override
    public void setBrakeMode(ZeroPowerBehavior brakeMode) {
        this.frontLeft.setZeroPowerBehavior(brakeMode);
        this.frontRight.setZeroPowerBehavior(brakeMode);
        this.backLeft.setZeroPowerBehavior(brakeMode);
        this.backRight.setZeroPowerBehavior(brakeMode);
    }

    @Override
    public void setInputVector(VectorF vector) {
        if (vector.length() >= 3) {
            setInput(vector.get(0), vector.get(1), vector.get(2));
        } else if (vector.length() >= 2) {
            setInput(vector.get(0), vector.get(1), 0);
        } else {
            setInput(vector.get(0), 0, 0);
        }
    }

    @Override
    public void setInput(double x, double y, double z) {
        this.setRunMode(RunMode.RUN_USING_ENCODER);

        double flPower, frPower, blPower, brPower;

        flPower = z + x + y;    frPower = -z - x + y;
        blPower = z - x + y;    brPower = -z + x + y;

        double max = (Math.abs(z) + Math.abs(y) + Math.abs(x));

        if (max < 1) {
            flPower /= 1;   frPower /= 1;
            blPower /= 1;   brPower /= 1;
        } else {
            flPower /= max; frPower /= max;
            blPower /= max; brPower /= max;
        }

        frontLeft.setPower(flPower);    frontRight.setPower(frPower);
        backLeft.setPower(blPower);     backRight.setPower(brPower);

    }
}
