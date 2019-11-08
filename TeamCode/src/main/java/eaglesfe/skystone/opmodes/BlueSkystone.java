package eaglesfe.skystone.opmodes;

import com.eaglesfe.birdseye.BirdseyeServer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.HashMap;
import java.util.Map;

import eaglesfe.common.Step;
import eaglesfe.common.Steps;

@Autonomous (name = "blue skystone", group = "blue competition")
public class skystoneAuto extends LinearOpMode {

    @Override
    public void runOpMode() {
        final BirdseyeServer server = new BirdseyeServer(3708, telemetry);
        final skystoneRobot robot = new skystoneRobot(hardwareMap);
        robot.setVisionEnabled(true);
        robot.useCameraVuforia();

/*================================================================================================*/

        //map of the steps
        Map<String, Step> steps = new HashMap<>();

        steps.put("start", new Step("moves forward 10 in") {
            @Override
            public void enter() {
                robot.drive.setTargetPositionRelative(-8,.5);
            }

            @Override
            public boolean isFinished() {
                 return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                return "deploy arm";
            }
        });

        steps.put("deploy arm", new Step("put out the arm to grab") {
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
                robot.setClawPosition(skystoneRobot.Constants.CLAWCLOSED);
                robot.stopAllMotors();
                return "pull back and turn";
            }
        });


        steps.put("pull back and turn", new Step("repositioning with the block") {
            @Override
            public void enter() {
                robot.resetGyroHeading();
                robot.setDriveInput(0,0, -0.30);
                robot.setArmPosition(.5,.3);

            }

            @Override
            public boolean isFinished() {
                if (robot.getGyroHeading180() > 80) {
                    robot.setDriveInput(0, 0, 0);
                    return true;
                }

                return false;
            }

            @Override
            public String leave() {
                return "strafe right";
            }
        });

        steps.put("strafe right", new Step("go right") {
            @Override
            public void enter() {
                robot.drive.setTargetStrafePositionRelative(-20, .4);
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                return "block on ground";
            }
        });

        steps.put("block on ground", new Step("block on ground") {
            @Override
            public void enter() {
                robot.setArmPosition(1, .3);
                robot.drive.setTargetPositionRelative(5,.5);
            }

            @Override
            public boolean isFinished() {
                return !robot.isArmBusy() && !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                robot.setClawPosition(skystoneRobot.Constants.CLAWMIN);
                robot.stopAllMotors();
                return "move arm back 1";
            }
        });

        steps.put("move arm back 1", new Step("arm back into robot and push block") {
            @Override
            public void enter() {
                robot.setArmPosition(.4,.5);
            }

            @Override
            public boolean isFinished() {
                return !robot.isArmBusy();
            }

            @Override
            public String leave() {
                robot.setClawPosition(skystoneRobot.Constants.CLAWCLOSED);
                return "move arm back 2";
            }
        });

        steps.put("move arm back 2", new Step("arm back into robot and push block") {
            @Override
            public void enter() {
                robot.setArmPosition(0,.3);
            }

            @Override
            public boolean isFinished() {
                return !robot.isArmBusy();
            }

            @Override
            public String leave() {
                return "move forward";
            }
        });

        steps.put("move forward", new Step("move forward") {
            @Override
            public void enter() {
                robot.drive.setTargetPositionRelative(-60,.5);
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                return "park";
            }
        });

        steps.put("park", new Step("parking...") {
            @Override
            public void enter() {
                robot.drive.setTargetPositionRelative(21,.5);
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
