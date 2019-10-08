package eaglesfe.skystone.opmodes;

import com.eaglesfe.birdseye.BirdseyeServer;
import com.eaglesfe.birdseye.FieldPosition;
import com.eaglesfe.birdseye.skystone.SkystoneBirdseyeTracker;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

@Autonomous(name="Bing bong", group ="Competition")
public class autonomousTest extends LinearOpMode {

    BirdseyeServer server;
    SkystoneBirdseyeTracker tracker;

    private static final float mmPerInch        = 25.4f;
    private static final float mmTargetHeight   = (6) * mmPerInch;
    private static final float halfField = 72 * mmPerInch;
    private static final float quadField  = 36 * mmPerInch;

    VectorF backredWall = new VectorF(halfField, -quadField, mmTargetHeight);
    VectorF redConstructionWall = new VectorF(-quadField, -halfField, mmTargetHeight);

    @Override
    public void runOpMode() throws InterruptedException {
        server = new BirdseyeServer(3708, telemetry);
        this.tracker = new SkystoneBirdseyeTracker();
        this.tracker.setShowCameraPreview(true);
        this.tracker.setVuforiaKey(skystoneRobot.Constants.VUFORIA_KEY);
        this.tracker.setWebcamNames(skystoneRobot.Constants.POS_CAM);
        this.tracker.setCameraForwardOffset(9.0f);
        this.tracker.setCameraVerticalOffset(skystoneRobot.Constants.CAM_Z_OFFSET);
        this.tracker.cameraLeftOffsetMm(skystoneRobot.Constants.CAM_Y_OFFSET);
        this.tracker.setCameraRotationalOffset(skystoneRobot.Constants.CAM_R_OFFSET);
        this.tracker.initialize(this.hardwareMap);
        this.tracker.start();
        server.start();

        final skystoneRobot robot = new skystoneRobot(hardwareMap);

        FieldPosition position = null;
        VectorF currentTarget = backredWall;
        String currentTargetString = "backRedWall";

        waitForStart();

        while (opModeIsActive()) {
            position = tracker.getCurrentPosition();

            if (position != null) {
                telemetry.addData("X", position.getX());
                telemetry.addData("Y", position.getY());
                telemetry.addData("Z", position.getZ());
                telemetry.update();

               if (position.getDistanceToTarget(currentTarget) < 16) {
                   switch (currentTargetString) {
                       case "backRedWall" :
                           currentTarget = redConstructionWall;
                           currentTargetString = "redConstructionWall";
                           break;
                       case "redConstructionWall" :
                           currentTarget = backredWall;
                           currentTargetString = "backRedWall";
                   }
               }

               VectorF currentTargetVariable = position.getTargetVector(currentTarget);
               robot.setDriveInput(currentTargetVariable.get(0), currentTargetVariable.get(1), 0);
            }
            else {
                robot.setDriveInput(0,0,0);
            }

        }

        server.stop();
    }
}
