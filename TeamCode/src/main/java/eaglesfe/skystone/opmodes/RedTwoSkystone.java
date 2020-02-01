package eaglesfe.skystone.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.HashMap;
import java.util.Map;

import eaglesfe.common.Step;
import eaglesfe.common.Steps;

@Autonomous(name = "red two skystone", group = "red competition")
public class RedTwoSkystone extends LinearOpMode {

    int moveMod = 0;
    int moveModTwo = 0;
    int position = 1;

    @Override
    public void runOpMode() {
        final skystoneRobot robot = new skystoneRobot(hardwareMap);

        //map of the steps
        Map<String, Step> steps = new HashMap<>();

        steps.put("start", new Step("moving forward..", 3500) {
            @Override
            public void enter() {
                robot.setArmPosition(.3, .3);
            }

            @Override
            public boolean isFinished() {
                return robot.drive.straightMove(24, .5,RedTwoSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "arm down";
            }
        });

        steps.put("arm down", new Step("homing arm...") {
            @Override
            public void enter() {
                robot.setArmPosition(-.1, .1);
            }

            @Override
            public boolean isFinished() {
                return robot.armHomed(RedTwoSkystone.this);
            }

            @Override
            public String leave() {
                return "back a little";
            }
        });

        steps.put("back a little", new Step("BAAACK...",400) {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                sleep(500);
                return true;
            }

            @Override
            public String leave() {
                int locate = (int) robot.locateSkystone();
                switch (locate) {
                    case 0 :
                        robot.drive.setInput(0,0,0);
                        position = 0;
                        return "center";
                    case 1 :
                        robot.drive.setInput(0,0,0);
                        position = 4;
                        return "right";
                    case 2 :
                        robot.drive.setInput(0,0,0);
                        position = 8;
                        return "left";
                    default:
                        robot.drive.setInput(0,0,0);
                        return "center";
                }
            }

        });

        steps.put("left", new Step("moving to left...",1900) {
            @Override
            public void enter() {
                moveMod = - 12;
                moveModTwo = -4;
            }

            @Override
            public boolean isFinished() {
                return robot.corectingStrafe(2900, -.2, RedTwoSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "suck";
            }
        });

        steps.put("center", new Step("moving to center...",2500) {
            @Override
            public void enter() {
                moveMod = - 16;
                moveModTwo = -16;
            }

            @Override
            public boolean isFinished() {
                return robot.corectingStrafe(1600, -.2, RedTwoSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "suck";
            }
        });

        steps.put("right", new Step("moving to right...",3000) {
            @Override
            public void enter() {
                moveMod = - 20;
                moveModTwo = -16;
            }

            @Override
            public boolean isFinished() {
                return robot.corectingStrafe(1650, .2, RedTwoSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "suck";
            }
        });

        steps.put("suck", new Step("nice...", 2000) {
            @Override
            public void enter() {
                robot.drive.recordPoint();
                robot.setArmPosition(-.04,.1);
                robot.setIntakeSpeed(1);
            }

            @Override
            public boolean isFinished() {
                return robot.suckStraight(.35,RedTwoSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                robot.setIntakeSpeed(0);
                return "reposition";
            }
        });

        steps.put("reposition", new Step("repostion block...", 1400) {
            @Override
            public void enter() {
            }

            @Override
            public boolean isFinished() {
                return robot.drive.straightMove(-robot.drive.inchDifference(), RedTwoSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "turn left";
            }
        });

        steps.put("turn left", new Step("turning left..", 2300) {
            @Override
            public void enter() {
                robot.setArmPosition(-.04,.1);
            }

            @Override
            public boolean isFinished() {
                return robot.angleTurnAbsolute(-90,RedTwoSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "move forward";
            }
        });

        steps.put("move forward", new Step("move forward", 5000) {
            @Override
            public void enter() {
            }

            @Override
            public boolean isFinished() {
                return robot.drive.straightMove(65 + moveMod, RedTwoSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "spit";
            }
        });

        steps.put("spit", new Step("spitting...", 500) {
            @Override
            public void enter() {
                robot.setIntakeSpeed(-1);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                robot.setIntakeSpeed(0);
                return "turn around";
            }
        });

        steps.put("turn around", new Step("turning...") {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                return robot.angleTurnAbsolute(-270,RedTwoSkystone.this);
            }

            @Override
            public String leave() {
                return "second skystone";
            }
        });

        steps.put("second skystone", new Step("move forward", 5000) {
            @Override
            public void enter() {
            }

            @Override
            public boolean isFinished() {
                return robot.drive.straightMove(65 + moveModTwo, RedTwoSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "second strafe";
            }
        });

        steps.put("second strafe", new Step("strafing...") {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                return robot.corectingStrafe(2900, .2, RedTwoSkystone.this);
            }

            @Override
            public String leave() {
                return "second suck";
            }
        });

        steps.put("second suck", new Step("nice...", 5000) {
            @Override
            public void enter() {
                robot.drive.recordPoint();
                robot.setArmPosition(-.04,.1);
                robot.setIntakeSpeed(1);
            }

            @Override
            public boolean isFinished() {
                return robot.suckStraight(.2,RedTwoSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                robot.setIntakeSpeed(0);
                return "strafe out";
            }
        });

        steps.put("strafe out", new Step("strafing...") {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                return robot.corectingStrafe(2900, -.2, RedTwoSkystone.this);
            }

            @Override
            public String leave() {
                return "turn around two";
            }
        });

        steps.put("turn around two", new Step("turning...") {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                return robot.angleTurnAbsolute(-90,RedTwoSkystone.this);
            }

            @Override
            public String leave() {
                return "deliver second skystone";
            }
        });

        steps.put("deliver second skystone", new Step("move forward", 5000) {
            @Override
            public void enter() {
            }

            @Override
            public boolean isFinished() {
                return robot.drive.straightMove(65, RedTwoSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "spit two";
            }
        });

        steps.put("spit two", new Step("spitting...", 500) {
            @Override
            public void enter() {
                robot.setIntakeSpeed(-1);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                robot.setIntakeSpeed(0);
                return "park";
            }
        });

        steps.put("park", new Step("parking...") {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                return robot.drive.straightMove(-10, RedTwoSkystone.this);
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
