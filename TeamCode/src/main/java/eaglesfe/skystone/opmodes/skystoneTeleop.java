package eaglesfe.skystone.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "skystone teleop")
public class skystoneTeleop extends OpMode {

    private skystoneRobot robot;

    // Actuator Input State
    private boolean prevSmallGrab = false;
    private boolean prevLargeGrab = false;
    private boolean prevFoundationGrab = false;

    @Override
    public void init () {
        robot = new skystoneRobot(hardwareMap);
    }

    @Override
    public void loop() {
        // Gamepad 1
        int inputScale = gamepad1.left_bumper ? 1 : 2;
        double x = -gamepad1.left_stick_x / inputScale;
        double y = gamepad1.left_stick_y / inputScale;
        double z = Math.pow(gamepad1.right_stick_x, 3) / inputScale;
        double intakeInputIn = gamepad1.left_trigger;
        double intakeInputOut = gamepad1.right_trigger;

        robot.setDriveInput(x, y, z);
        robot.setIntakeSpeed(intakeInputIn - intakeInputOut);

        telemetry.addData("motor powers:", robot.drive.motorTelemetry());

        // Gamepad
        double armInputScale = gamepad2.right_trigger > 0 ? 1.5 : 3;
        double armInput = Math.pow(gamepad2.left_stick_y, 3) / armInputScale;
        boolean smallGrab = gamepad2.a && !this.prevSmallGrab;
        boolean largeGrab = gamepad2.b && !this.prevLargeGrab;
        boolean foundationGrab = gamepad2.y && !this.prevFoundationGrab;
        boolean rotateLeft = gamepad2.left_bumper;
        boolean rotateRight = gamepad2.right_bumper;
        boolean wristSave = gamepad2.x;

        robot.setArmPower(armInput);
        robot.clawGrab(smallGrab, largeGrab);
        robot.wristTurn(rotateLeft, rotateRight, wristSave);
        robot.foundationGrab(foundationGrab);

        telemetry.addData("input to arm", armInput);

        this.prevSmallGrab = gamepad2.a;
        this.prevLargeGrab = gamepad2.b;
        this.prevFoundationGrab = gamepad2.y;

        telemetry.update();
    }
}
