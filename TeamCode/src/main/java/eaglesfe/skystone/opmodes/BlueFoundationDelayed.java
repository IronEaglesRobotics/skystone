package eaglesfe.skystone.opmodes;

import com.eaglesfe.birdseye.BirdseyeServer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.HashMap;
import java.util.Map;

import eaglesfe.common.Step;
import eaglesfe.common.Steps;

@Autonomous (name = "blue foundation delayed", group = "blue competition")
public class BlueFoundationDelayed extends LinearOpMode{

    @Override
    public void runOpMode() {
        final BirdseyeServer server = new BirdseyeServer(3708, telemetry);
        final skystoneRobot robot = new skystoneRobot(hardwareMap);
        robot.setVisionEnabled(true);
        robot.useCameraTensor();

        //map of the steps
        final Map<String, Step> steps = new HashMap<>();

        steps.put("start", new Step("strafe to the left...") {
            @Override
            public void enter() {
                robot.drive.setTargetStrafePositionRelative(20, .4);
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                return "initial forward";
            }
        });

        steps.put("initial forward", new Step("15 second delay...") {
            double startingTime;

            @Override
            public void enter() {
                startingTime = getRuntime();
            }

            @Override
            public boolean isFinished() {
                return (getRuntime() - startingTime) > 15000;
            }

            @Override
            public String leave() {
                return "move forward";
            }
        });

        steps.put("move forward", new Step("moving forward...") {
            @Override
            public void enter() {
                robot.drive.setTargetPositionRelative(-32,.3);
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                robot.foundationGrab(true);
                return "grab foundation";
            }
        });

        steps.put("grab foundation", new Step("repositioning foundation...") {
            @Override
            public void enter() {
                robot.drive.setTargetPositionRelative(-6, .3);
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                return "back to wall";
            }
        });

        steps.put("back to wall", new Step("moving back...") {
            @Override
            public void enter() {
                robot.drive.setTargetPositionRelative(44, .5);
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                robot.foundationGrab(true);
                return "loud strafe";
            }
        });

        steps.put("loud strafe", new Step("Shhhhh...") {
            @Override
            public void enter() {
                robot.drive.setTargetStrafePositionRelative(-60, .4);
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                return "end";
            }
        });

        steps.put("end", new Step("end") {
            @Override
            public void enter() {
                robot.stopAllMotors();
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                return null;
            }
        });

        //wait for the auto to be started
        waitForStart();

        //telemetry
        telemetry.addData("intitialized", "");
        telemetry.update();

        //once started start running through the steps
        Steps stepsRunner = new Steps(steps, this);
        stepsRunner.runStepsMap();

        /*================================================================================================*/

        //end of autonomous
        robot.stopAllMotors();
    }
}
