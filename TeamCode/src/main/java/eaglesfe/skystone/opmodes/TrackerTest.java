package eaglesfe.skystone.opmodes;

import com.eaglesfe.birdseye.BirdseyeServer;
import com.eaglesfe.birdseye.FieldPosition;
import com.eaglesfe.birdseye.skystone.SkystoneBirdseyeTracker;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.lang.annotation.Target;

import eaglesfe.roverruckus.Robot;

@Autonomous(name="Tracker Test", group ="Competition")
public class TrackerTest extends LinearOpMode {

    BirdseyeServer server;
    SkystoneBirdseyeTracker tracker;

    @Override
    public void runOpMode() throws InterruptedException {
        server = new BirdseyeServer(3708, telemetry);
        this.tracker = new SkystoneBirdseyeTracker();
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
            position = tracker.getCurrentPosition();
            if (position != null) {
                telemetry.addData("X", position.getX());
                telemetry.addData("Y", position.getY());
                telemetry.addData("Z", position.getZ());
                telemetry.update();
            }

            sleep(100);
        }

        server.stop();
    }
}
