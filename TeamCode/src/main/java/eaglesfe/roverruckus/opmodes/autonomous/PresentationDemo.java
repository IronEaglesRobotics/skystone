package eaglesfe.roverruckus.opmodes.autonomous;

import com.eaglesfe.birdseye.BirdseyeServer;
import com.eaglesfe.birdseye.FieldPosition;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.io.IOException;

import eaglesfe.roverruckus.Robot;

@Autonomous(name="Presentation Demo", group ="Competition")
public class PresentationDemo extends LinearOpMode {

    BirdseyeServer server;
    Robot robot;
    private long nextUpdate = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        server = new BirdseyeServer(3708, telemetry);
        robot = new Robot(hardwareMap);

        robot.setVisionEnabled(true);
        robot.useRearCamera();
        server.start();

        FieldPosition position = null;
        waitForStart();
         while (opModeIsActive()) {
             try {
                 position = robot.getPosition();
                 if (position != null) {
                     server.addData("robot", position);
                 }
                 server.beginArray("gamepads");
                 server.addData("gamepad1", this.gamepad1);
                 server.addData("gamepad2", this.gamepad2);
                 server.endArray();
                 server.addData("gyro", robot.getGyroHeading180());
                 server.update();
             } catch (IOException e) {
                 e.printStackTrace();
             }

             sleep(500);
         }

         server.stop();
    }
}
