package eaglesfe.skystone.opmodes;

import com.eaglesfe.birdseye.BirdseyeServer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.HashMap;
import java.util.Map;

import eaglesfe.common.Step;
import eaglesfe.common.Steps;

@Autonomous (name = "red foundation", group = "red competition")
public class RedFoundation extends LinearOpMode{

    @Override
    public void runOpMode() {
        final BirdseyeServer server = new BirdseyeServer(3708, telemetry);
        final skystoneRobot robot = new skystoneRobot(hardwareMap);
        robot.setVisionEnabled(true);
        robot.useCameraTensor();

        //map of the steps
        Map<String, Step> steps = new HashMap<>();

        steps.put("start", new Step("moving forward...") {
            @Override
            public void enter() {
                robot.drive.setTargetPositionRelative(-22,.3);
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
                robot.drive.setTargetPositionRelative(30, .5);
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
                robot.drive.setTargetStrafePositionRelative(60, .4);
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

        //telemetry
        while (!isStarted()) {
            telemetry.addData("skystone position", robot.locateSkystone());
            telemetry.addData("arm encoder ticks", robot.getArmPosition());
            telemetry.update();
        }

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
