package eaglesfe.skystone.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import eaglesfe.skystone.skystoneRobot;

@TeleOp(name = "skystone teleop")
public class skystoneTeleop extends OpMode {

    private skystoneRobot robot;

    /** input variables **/
    int inputScale;
    float capstone;
    boolean gyroReset, smallGrab, largeGrab, moveIntake, foundationGrab, rotateLeft, rotateRight, wristSave, half;
    double x, y, z, intakeInputIn, intakeInputOut, armInputScale, armInput;

    /** previous state initialization **/
    private boolean prevSmallGrab, prevLargeGrab, prevIntakeMove, prevFoundationGrab, prevIntakeHalf = false;

    @Override
    public void init () {
        robot = new skystoneRobot(hardwareMap);
    }

    @Override
    public void loop() {

        /** ---------- Gamepad 1 (Driver gamepad) --------- **/

        /** setting what will be the inputs to variables **/
        inputScale = gamepad1.left_bumper ? 1 : 2;
        gyroReset = gamepad1.a;
        x = -gamepad1.left_stick_x / inputScale;
        y = gamepad1.left_stick_y / inputScale;
        z = Math.pow(gamepad1.right_stick_x, 3) / inputScale;
        intakeInputIn = gamepad1.right_trigger;
        intakeInputOut = gamepad1.left_trigger;
        moveIntake = gamepad1.dpad_right && !this.prevIntakeMove;
        half = gamepad1.dpad_down && !this.prevIntakeHalf;

        /** method calls using those inputs **/
        robot.setDriveInput(x, y, z);
        robot.setIntakeServos(moveIntake, half);
        robot.setIntakeSpeed(intakeInputIn - intakeInputOut);

        /** ----------- Gamepad 2 (Actuators gamepad) ---------- **/

        /** setting what will be the inputs to variables **/
        armInputScale = gamepad2.right_trigger > 0 ? 1.5 : 3;
        armInput = Math.pow(gamepad2.left_stick_y, 3) / armInputScale;
        smallGrab = gamepad2.a && !this.prevSmallGrab;
        largeGrab = gamepad2.b && !this.prevLargeGrab;
        foundationGrab = gamepad2.y && !this.prevFoundationGrab;
        rotateLeft = gamepad2.left_bumper;
        rotateRight = gamepad2.right_bumper;
        wristSave = gamepad2.x;
        capstone = gamepad2.left_trigger;

        /** method calls using those inputs **/
        robot.setArmPower(armInput);
        robot.clawGrab(smallGrab, largeGrab);
        robot.wristTurn(rotateLeft, rotateRight, wristSave);
        robot.foundationGrab(foundationGrab);
        robot.capstoneRelease(capstone);

        /** setting reference variables to know previous states **/
        this.prevSmallGrab = gamepad2.a;
        this.prevLargeGrab = gamepad2.b;
        this.prevIntakeMove = gamepad1.dpad_right;
        this.prevIntakeHalf = gamepad1.dpad_down;
        this.prevFoundationGrab = gamepad2.y;

    }
}
