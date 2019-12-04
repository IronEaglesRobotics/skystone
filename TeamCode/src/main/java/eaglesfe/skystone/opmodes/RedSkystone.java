package eaglesfe.skystone.opmodes;

import com.eaglesfe.birdseye.BirdseyeServer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.HashMap;
import java.util.Map;

import eaglesfe.common.Step;
import eaglesfe.common.Steps;

@Autonomous (name = "red skystone", group = "red competition")
public class RedSkystone extends LinearOpMode {

    int moveMod = 0;
    int moveModTwo = 0;

    @Override
    public void runOpMode() {
        final BirdseyeServer server = new BirdseyeServer(3708, telemetry);
        final skystoneRobot robot = new skystoneRobot(hardwareMap);
        robot.setVisionEnabled(true);
        robot.useCameraVuforia();

        /*================================================================================================*/

        //map of the steps
        Map<String, Step> steps = new HashMap<>();

        steps.put("start", new Step("moving forward..") {
            @Override
            public void enter() {
                robot.drive.setTargetPositionRelative(-16,.3);
            }

            @Override
            public boolean isFinished() {
                if(!robot.drive.isBusy()) {
                    sleep(1000);
                    return true;
                }
                return false;
            }

            @Override
            public String leave() {

                return "back a little";
            }
        });

        steps.put("back a little", new Step("BAAACK...") {
            @Override
            public void enter() {
                robot.drive.setForwardTargetPositionRelative(4,.3);
            }

            @Override
            public boolean isFinished() {
                if (!robot.drive.isBusy()) {
                    sleep(1000);
                    return true;
                }
                return false;
            }

            @Override
            public String leave() {
                if (robot.locateSkystone() == 0.0f) {
                    return "left";
                } else if (robot.locateSkystone() == 1.0f) {
                    return "deploy arm";
                } else if (robot.locateSkystone() == 2.0f) {
                    return "right";
                }
                return "deploy arm";
            }
        });

        steps.put("left", new Step("moving to left...") {
            @Override
            public void enter() {
                robot.drive.setTargetStrafePositionRelative(9, .4);
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                moveMod = 8;
                moveModTwo = -8;
                return "deploy arm";
            }
        });

        steps.put("right", new Step("moving to right...") {
            @Override
            public void enter() {
                robot.drive.setTargetStrafePositionRelative(-12, .4);
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                moveMod = -8;
                moveModTwo = -8;
                return "right reposition";
            }
        });

        steps.put("right reposition", new Step("re boop", 100) {
            @Override
            public void enter() {
                robot.setDriveInput(0,0, 0.30);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                robot.stopAllMotors();
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


        steps.put("pull back and turn", new Step("repositioning with the block", 1500) {
            @Override
            public void enter() {
                robot.resetGyroHeading();
                robot.setDriveInput(0,0, 0.30);
                robot.setArmPosition(.5,.3);

            }

            @Override
            public boolean isFinished() {
                float heading = robot.getGyroHeading180();
                if (heading < -80) {
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
                robot.drive.setTargetStrafePositionRelative(15, .4);
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
                robot.drive.setTargetPositionRelative(5 + moveMod,.5);
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
                robot.drive.setTargetPositionRelative(-60 + moveMod + moveModTwo,.5);
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
