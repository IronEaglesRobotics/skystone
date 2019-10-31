package eaglesfe.skystone.opmodes;

import com.eaglesfe.birdseye.BirdseyeServer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.HashMap;
import java.util.Map;

import eaglesfe.roverruckus.opmodes.OpModeHelpers;
import eaglesfe.common.SleepStep;
import eaglesfe.common.Step;
import eaglesfe.common.Steps;

@Autonomous (name = "competition auto", group = "competition")
public class skystoneAuto extends LinearOpMode {

    @Override
    public void runOpMode() {
        final BirdseyeServer server = new BirdseyeServer(3708, telemetry);
        final skystoneRobot robot = new skystoneRobot(hardwareMap);

/*================================================================================================*/

        //map of the steps
        Map<String, Step> steps = new HashMap<>();

        //test of auto by moving in a square

        steps.put("forward", new Step("moves forward 10 in") {
            @Override
            public void enter() { robot.drive.setTargetPositionRelative(10,.5);}

            @Override
            public boolean isFinished() {
                 return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                return "";
            }
        });

        steps.put("strafe right", new Step("strafes right 10 in") {
            @Override
            public void enter() {
                robot.drive.setTargetStrafePositionRelative(10, .5);
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                return "backwards";
            }
        });

        steps.put("backwards", new Step("moves backwards 10 in") {
            @Override
            public void enter() {
                robot.drive.setTargetPositionRelative(10, -.5);
            }

            @Override
            public boolean isFinished() {
                return robot.drive.isBusy();
            }

            @Override
            public String leave() {
                return "strafe left";
            }
        });

        steps.put("strafe left", new Step("strafes left 10 in") {
            @Override
            public void enter() {
                robot.drive.setTargetStrafePositionRelative(10, -.5);
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                robot.stopAllMotors();
                return null;
            }
        });

        //wait for the auto to be started
        waitForStart();

        //once started start running through the steps
        Steps stepsRunner = new Steps(steps, this);
        stepsRunner.runStepsMap();

/*================================================================================================*/

        //end of autonomous
        robot.stopAllMotors();

    }
}
