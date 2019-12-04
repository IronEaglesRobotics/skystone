package eaglesfe.skystone.opmodes;

import com.eaglesfe.birdseye.BirdseyeServer;
import com.eaglesfe.birdseye.skystone.SkystoneBirdseyeTracker;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.HashMap;
import java.util.Map;

import eaglesfe.common.Step;
import eaglesfe.common.Steps;

@Autonomous (name = "test", group = "blue competition")
public class testAuto extends LinearOpMode{


    private SkystoneBirdseyeTracker tracker;

    @Override
    public void runOpMode() {
        this.tracker = new SkystoneBirdseyeTracker();
        this.tracker.setShowCameraPreview(true);
        this.tracker.setVuforiaKey(skystoneRobot.Constants.VUFORIA_KEY);
        this.tracker.setWebcamNames(skystoneRobot.Constants.POS_CAM);
        this.tracker.setCameraForwardOffset(skystoneRobot.Constants.CAM_X_OFFSET);
        this.tracker.setCameraVerticalOffset(skystoneRobot.Constants.CAM_Z_OFFSET);
        this.tracker.cameraLeftOffsetMm(skystoneRobot.Constants.CAM_Y_OFFSET);
        this.tracker.setCameraRotationalOffset(skystoneRobot.Constants.CAM_R_OFFSET);
        this.tracker.initialize(this.hardwareMap);

        this.tracker.start();

        //wait for the auto to be started
        waitForStart();

        //telemetry
        while (opModeIsActive()) {
            telemetry.addData("intitialized", tracker.tryLocateSkystone());
            telemetry.update();
        }

        /*================================================================================================*/
        //end of autonomous
    }
}
