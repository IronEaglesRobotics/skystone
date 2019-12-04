package eaglesfe.skystone.opmodes;

import com.eaglesfe.birdseye.BirdseyeServer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.HashMap;
import java.util.Map;

import eaglesfe.common.Step;
import eaglesfe.common.Steps;

@Autonomous (name = "blue skystone", group = "blue competition")
public class BlueSkystone extends LinearOpMode {

    int moveMod = 0;
    int moveModTwo = 0;

    @Override
    public void runOpMode() {
        final skystoneRobot robot = new skystoneRobot(hardwareMap);
        robot.setVisionEnabled(true);
        robot.useCameraVuforia();

/*================================================================================================*/

        //map of the steps
        Map<String, Step> steps = new HashMap<>();

        steps.put("start", new Step("moving forward..", 2000) {
            @Override
            public void enter() {
                 robot.drive.setTargetPositionRelative(-16,.25);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "back a little";
            }
        });

        steps.put("back a little", new Step("BAAACK...",3000) {
            @Override
            public void enter() {
                robot.drive.setForwardTargetPositionRelative(4,.2);
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
                    robot.drive.setInput(0,0,0);
                    return "left";
                } else if (robot.locateSkystone() == 1.0f) {
                    robot.drive.setInput(0,0,0);
                    return "center";
                } else if (robot.locateSkystone() == 2.0f) {
                    robot.drive.setInput(0,0,0);
                    return "right";
                }
                robot.drive.setInput(0,0,0);
                return "deploy arm";
            }
        });

        steps.put("left", new Step("moving to left...",500) {
            @Override
            public void enter() {
                robot.drive.setTargetStrafePositionRelative(8,.4);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                moveMod = 4;
                return "deploy arm";
            }
        });

        steps.put("center", new Step("moving to center...",1000) {
            @Override
            public void enter() {
                robot.drive.setTargetStrafePositionRelative(-8,.4);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                moveMod = -4;
                return "deploy arm";
            }
        });

        steps.put("right", new Step("moving to right...",2200) {
            @Override
            public void enter() {
                robot.drive.setTargetStrafePositionRelative(-20,.4);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                moveMod = -12;
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

        steps.put("deploy arm", new Step("deploying arm...") {
            @Override
            public void enter() {
                robot.setClawPosition(skystoneRobot.Constants.CLAWMID);
                robot.setArmPosition(.98, .3);
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

        steps.put("grab one", new Step("grab block...", 300) {
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

        steps.put("move out of line", new Step("moving out of line...", 600) {
            @Override
            public void enter() {
                robot.drive.setInput(0,-.4,0);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "turn one";
            }
        });

        steps.put("turn one", new Step("turn...", 800) {
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

        steps.put("open", new Step("opening...", 300) {
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

        steps.put("turn two", new Step("turning back...",800) {
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

        steps.put("grab two", new Step("close up",300) {
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
                return "turn left";
            }
        });

        steps.put("turn left", new Step("turning left..", 1450) {
            @Override
            public void enter() {
                robot.drive.setInput(0,0,-0.3);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "move forward";
            }
        });

        steps.put("move forward", new Step("move forward", 4000) {
            @Override
            public void enter() {
                robot.drive.setTargetPositionRelative(-60 + moveMod,.75);
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "block out";
            }
        });

        steps.put("block out", new Step("putting block out") {
            @Override
            public void enter() {
                robot.setArmPosition(.95, .3);
            }

            @Override
            public boolean isFinished() {
                return !robot.isArmBusy();
            }

            @Override
            public String leave() {
                robot.setClawPosition(skystoneRobot.Constants.CLAWOPEN);
                return "save claw";
            }
        });

        steps.put("save claw", new Step("stop breaking robot...") {
            @Override
            public void enter() {
                robot.setArmPosition(.7, .3);
            }

            @Override
            public boolean isFinished() {
                return !robot.isArmBusy();
            }

            @Override
            public String leave() {
                robot.setClawPosition(skystoneRobot.Constants.CLAWCLOSED);
                return "arm in";
            }
        });

        steps.put("arm in", new Step("putting away our arm...") {
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
                return "park";
            }
        });

        steps.put("park", new Step("parking...", 1000) {
            @Override
            public void enter()
            {
                robot.setDriveInputY(.5);
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
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
