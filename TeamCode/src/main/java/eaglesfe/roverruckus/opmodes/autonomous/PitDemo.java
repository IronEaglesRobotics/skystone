package eaglesfe.roverruckus.opmodes.autonomous;

import com.eaglesfe.birdseye.BirdseyeServer;
import com.eaglesfe.birdseye.FieldPosition;
import com.eaglesfe.birdseye.roverruckus.RoverRuckusBirdseyeTracker;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.io.IOException;

import eaglesfe.roverruckus.Robot;

@Autonomous(name="Pit Demo", group ="Competition")
@Disabled
public class PitDemo extends LinearOpMode {

    BirdseyeServer server;
    RoverRuckusBirdseyeTracker tracker;

    @Override
    public void runOpMode() throws InterruptedException {
        server = new BirdseyeServer(3708, telemetry);
        this.tracker = new RoverRuckusBirdseyeTracker();
        this.tracker.setShowCameraPreview(true);
        this.tracker.setVuforiaKey(Robot.Constants.VUFORIA_KEY);
        this.tracker.setWebcamNames(Robot.Constants.POS_CAM);
        this.tracker.setCameraForwardOffset(9.0f);
        this.tracker.setCameraVerticalOffset(Robot.Constants.CAM_Z_OFFSET);
        this.tracker.cameraLeftOffsetMm(Robot.Constants.CAM_Y_OFFSET);
        this.tracker.initialize(this.hardwareMap);
        this.tracker.start();
        server.start();

        FieldPosition position = null;
        waitForStart();
         while (opModeIsActive()) {
             try {
                 position = tracker.getCurrentPosition();
                 if (position != null) {
                     server.addData("robot", position);
                 }
                 server.beginArray("gamepads");
                 server.addData("gamepad1", this.gamepad1);
                 server.addData("gamepad2", this.gamepad2);
                 server.endArray();
                 server.update();
             } catch (IOException e) {
                 e.printStackTrace();
             }

             sleep(500);
         }

         server.stop();
    }
}
