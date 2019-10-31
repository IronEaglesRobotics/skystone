package eaglesfe.skystone.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "skystone teleop")
public class skystoneTeleop extends OpMode {

    skystoneRobot robot;
    private double x = 0;
    private double y = 0;
    private double z = 0;
    private boolean fastFunction = false;
    private double armInput = 0;
    private double intakeInputIn = 0;
    private double intakeInputOut = 0;
    private boolean smallGrab = false;
    private boolean largeGrab = false;
    private boolean rotateLeft = false;
    private boolean rotateRight = false;

    @Override
    public void init () {
        robot = new skystoneRobot(hardwareMap);
    }

    @Override
    public void loop() {
        //drive
        x = gamepad1.left_stick_x;
        y = gamepad1.left_stick_y;
        z = gamepad1.right_stick_x;
        fastFunction = gamepad1.left_bumper;

        if (fastFunction){
            robot.setDriveInput(x, y, z);
        } else {
            robot.setDriveInput(x / 2, y / 2, z / 2);
        }
        telemetry.addData("motor powers:", robot.drive.motorTelemetry());

        //stacking arm
        armInput = gamepad2.left_stick_y;
        smallGrab = gamepad2.a;
        largeGrab = gamepad2.b;
        rotateLeft = gamepad2.left_bumper;
        rotateRight = gamepad2.right_bumper;
        robot.setArmPower(armInput);
        robot.clawGrab(smallGrab, largeGrab);
        robot.wristTurn(rotateLeft, rotateRight);

        //intake
        intakeInputIn = gamepad1.left_trigger;
        intakeInputOut = gamepad1.right_trigger;
        robot.setIntakeSpeed(intakeInputIn - intakeInputOut);

        telemetry.update();
    }
}
