package eaglesfe.skystone.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "competition teleop")
public class skystoneTeleop extends OpMode {

    skystoneRobot robot;
    private double x = 0;
    private double y = 0;
    private double z = 0;
    private double armInput = 0;
    private double intakeInputIn = 0;
    private double intakeInputOut = 0;

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
        robot.setDriveInput(x,y,z);

        //stacking arm
        armInput = gamepad2.left_stick_y;
        robot.setArmSpeed(armInput);

        //intake
        intakeInputIn = gamepad2.left_trigger;
        intakeInputOut = gamepad2.right_trigger;
        robot.setIntakeSpeed(intakeInputIn - intakeInputOut);
    }
}
