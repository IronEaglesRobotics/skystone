package eaglesfe.roverruckus.opmodes.autonomous;

import android.graphics.Point;

import com.eaglesfe.birdseye.BirdseyeServer;
import com.eaglesfe.birdseye.FieldPosition;
import com.eaglesfe.birdseye.roverruckus.MineralSample;
import com.eaglesfe.birdseye.util.MathHelpers;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import eaglesfe.common.SleepStep;
import eaglesfe.common.Step;
import eaglesfe.common.Steps;
import eaglesfe.roverruckus.Robot;
import eaglesfe.roverruckus.opmodes.OpModeHelpers;

@Autonomous(name="MesuredStrafeTest", group ="Competition")
@Disabled
public class mesuredStrafeTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        final BirdseyeServer server = new BirdseyeServer(3708, telemetry);
        final Robot robot = new Robot(hardwareMap);
        robot.setVisionEnabled(true);

        Map<String, Step> steps = new HashMap<>();
    }

}
