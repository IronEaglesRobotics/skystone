package eaglesfe.skystone.opmodes;

import com.eaglesfe.birdseye.BirdseyeServer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.HashMap;
import java.util.Map;

import eaglesfe.common.Step;
import eaglesfe.common.Steps;

@Autonomous (name = "reposition grab", group = "red competition")
public class RepositionGrab extends LinearOpMode {

    @Override
    public void runOpMode() {
        final skystoneRobot robot = new skystoneRobot(hardwareMap);
        robot.setVisionEnabled(true);
        robot.useCameraVuforia();

        Map<String, Step> steps = new HashMap<>();

        steps.put("start", new Step("deploying arm...") {
            @Override
            public void enter() {
                robot.setClawPosition(skystoneRobot.Constants.CLAWMID);
                robot.setArmPosition(1, .3);
            }

            @Override
            public boolean isFinished() {
                return !robot.isArmBusy();
            }

            @Override
            public String leave() {
                return "grab one";
            }
        });

        steps.put("grab one", new Step("grab block...", 200) {
            @Override
            public void enter() {
                robot.setClawPosition(skystoneRobot.Constants.CLAWCLOSED);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                return "move out of line";
            }
        });

        steps.put("move out of line", new Step("moving out of line...", 1500) {
            @Override
            public void enter() {
                robot.drive.setTargetPositionRelative(-8,.4);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                return "turn one";
            }
        });

        steps.put("turn one", new Step("turn...", 700) {
            @Override
            public void enter() {
                robot.setWristPosition(0);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                return "open";
            }
        });

        steps.put("open", new Step("opening...", 200) {
            @Override
            public void enter() {
                robot.setClawPosition(skystoneRobot.Constants.CLAWOPEN);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                return "turn two";
            }
        });

        steps.put("turn two", new Step("turning back...",700) {
            @Override
            public void enter() {
                robot.setWristPosition(.5);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                return "grab two";
            }
        });

        steps.put("grab two", new Step("close up",200) {
            @Override
            public void enter() {
                robot.setClawPosition(skystoneRobot.Constants.CLAWCLOSED);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                return "into robot";
            }
        });

        steps.put("into robot", new Step("moving arm into bot...") {
            @Override
            public void enter() {
                robot.setArmPosition(0, .3);
            }

            @Override
            public boolean isFinished() {
                return !robot.isArmBusy();
            }

            @Override
            public String leave() {
                return "end";
            }
        });

        steps.put("end", new Step("STOP") {
            @Override
            public void enter() {
                robot.stopAllMotors();
            }

            @Override
            public boolean isFinished() {
                return true;
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
